/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.pfarrell.busobj.AbstractPersistentBusinessObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * The <code>CacheFactory</code> class implements a factory to return instances of
 * a DenningCache. The factory defaults reasonably, but can be controlled while testing by calling the
 * set*Class() functions
 * 
 * @author pfarrell
 * Created on Nov 20, 2009, 3:53:21 PM
 */
public abstract class CacheFactory {
     /** logger instance */
private static final Logger cfLog = Logger.getLogger(CacheFactory.class);
/** the base cache */
private static Class wscClass;
private static Class dbcClass;
private static long sleepTime = TimeUnit.MINUTES.toMillis(6);
private static CacheTracker theTracker = new CacheTracker();

private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

/**
 * set the sleep time for subsequent instances of a cache
 * @param arg milliseconds to sleep
 */
public static void setCommonSleepTime(long arg) {
    Preconditions.checkArgument(arg > AbstractWSCache.MINIMUM_SLEEPTIME);
    sleepTime = arg;
}
/**
 * gets the sleep time for subsequent instances of a cache
 * @return sleep time of caches we make.
 */
public static long getCommonSleepTime() {
    return sleepTime;
}
public static void setCommonSleepToMinimum() {
    sleepTime = AbstractWSCache.MINIMUM_SLEEPTIME;
}
/**
 * tells this factory what DB class to use
 * @param aClass a DatabaseCache or mock to use to generate DatabaseCaches
 */
public static void setDbcClass(Class aClass) {
    lock.writeLock().lock();
    try {
        if (dbcClass == null) {
            if (  aClass == null)
                return;
            else if ( aClass != null)
                dbcClass =  aClass;
        } else {
            if ( aClass == null) {
                dbcClass = null;
            } else {
                cfLog.fatal("very bad, trying to change out live factory");
                  throw new IllegalStateException(" live CacheFactory can not be re-initialized. "
                              + "Did you forget to call CacheFactory.setInstance(null) ?");
            }
        }
    } finally {
        lock.writeLock().unlock();
    }
}
  /**
   * sets up the factory to return a nice instance of an DenningCache -- note: once the instance is initialized,
   * it can not directly be changed, you must change it to null first, and then you can initialize it again. Will expliticly
   * call shutdown on instance when clearing it out.
   * @param aClass a Cache or mock to use to generate WorkingSetCaches
   * @throws IllegalStateException if instance was setup and an attempt is made to set it to something else.
   */
public static void setWscClass(Class aClass) {
    lock.writeLock().lock();
    try {
        if (wscClass == null) {
            if (  aClass == null)
                return;
            else if ( aClass != null)
                wscClass =  aClass;
        } else {
            if ( aClass == null) {
                wscClass = null;
            } else {
                cfLog.fatal("very bad, trying to change out live factory");
                  throw new IllegalStateException(" live CacheFactory can not be re-initialized. "
                              + "Did you forget to call CacheFactory.setInstance(null) ?");
            }
        }
    } finally {
        lock.writeLock().unlock();
    }
}
/**
 * create and return a {@link WorkingSetCache} object
 * @param arg optional instance of the class we will cache, handy for setting name
 * @return a running WorkingSetCache
 */
public static WorkingSetCache makeWSCinstance(Object arg) {
    if (wscClass == null) {
        cfLog.fatal("null result in getInstance in CacheFactory");
        throw new IllegalStateException(" CacheFactory not initialized. "
                              + "Did you forget to call CacheFactory.setInstance() ?");
    }
    WorkingSetCache rval = null;
    int numCtorArgs = arg == null ? 1 : 2;
    cfLog.debug(String.format("makingWSC, num Args: %d", numCtorArgs));
    try {
        Constructor ctorToUse = null;
        Constructor[] ctors = wscClass.getDeclaredConstructors();
        for ( int i = 0; i < ctors.length && ctorToUse == null; i++) {
            Constructor ctor = ctors[i];
            Type[] types = ctor.getGenericParameterTypes();
            switch (numCtorArgs) {
                case 1:
                    if ( types.length == numCtorArgs && types[0].toString().equals("long")) {
                        ctorToUse = ctor;
                    }
                    break;
                case 2:
                    if (types.length == numCtorArgs && types[1].toString().equals("V")) {
                        ctorToUse = ctor;
                    }
                    break;
                default:
                    String msg = "illegal number of arguments for constructor in CacheFactory";
                    cfLog.fatal(msg);
                    throw new IllegalStateException(msg);
            }
        }
        rval = (WorkingSetCache) (numCtorArgs == 1 ? ctorToUse.newInstance(sleepTime) : ctorToUse.newInstance(sleepTime, arg) );
        if (arg != null) {
            rval.setThreadName(arg.getClass());
        }
        rval.ensureRunning();
        theTracker.add(rval);
    } catch (Exception ex) {
        cfLog.fatal("most serious", ex);
    }
    return rval;
}
/**
   * makes and gets an instance of the WorkingSetCache
   * @return the WorkingSetCache instance
   */
public static WorkingSetCache makeWSCinstance() {
    return makeWSCinstance(null);
}
/**
 * makes and gets an instance of the DatabaseCache
 * @param pbo a Cachable PBO to use to set name of thread.
 * @return the DatabaseCache instance
 */
public static DatabaseCache makeDBCinstance(AbstractPersistentBusinessObject pbo) {
    if (dbcClass == null) {
        cfLog.fatal("null result in getInstance in CacheFactory");
        throw new IllegalStateException(" CacheFactory not initialized. "
                              + "Did you forget to call CacheFactory.setInstance() ?");
    }
    DatabaseCache rval = null;
    int numCtorArgs = pbo == null ? 1 : 2;
    cfLog.debug(String.format("makingDBC, num Args: %d", numCtorArgs));
    try {
        Constructor ctorToUse = null;
        Constructor[] ctors = dbcClass.getDeclaredConstructors();
        for ( int i = 0; i < ctors.length && ctorToUse == null; i++) {
            Constructor ctor = ctors[i];
            Type[] types = ctor.getGenericParameterTypes();
            switch (numCtorArgs) {
                case 1:
                    if ( types.length == numCtorArgs && types[0].toString().equals("long")) {
                        ctorToUse = ctor;
                    }
                    break;
                case 2:
                    if (types.length == numCtorArgs && types[1].toString().equals("V")) {
                        ctorToUse = ctor;
                    }
                    break;
                default:
                    String msg = "illegal number of arguments for constructor in CacheFactory";
                    cfLog.fatal(msg);
                    throw new IllegalStateException(msg);
            }
        }
        rval = (DatabaseCache) (numCtorArgs == 1 ? ctorToUse.newInstance(sleepTime) : ctorToUse.newInstance(sleepTime, pbo) );
        if (pbo != null) {
            rval.setThreadName(pbo.getClass());
        }
        rval.ensureRunning();
        theTracker.add(rval);
    } catch (Exception ex) {
        cfLog.fatal("CF::unexpexted exception", ex);
    }
    return rval;
}
/**
 * makes and gets an instance of the DatabaseCache
 * @return the DatabaseCache instance
 */
public static DatabaseCache makeDBCinstance() {
    return  makeDBCinstance(null);
}

/**
 * @return list of known caches.
 */
@SuppressWarnings("unchecked")
public static ImmutableList<DenningCache> getKnownCaches() {
    ImmutableList rval = ImmutableList.of();
    lock.readLock().lock();
    try {
        rval = ImmutableList.copyOf(theTracker.getKnownCaches());
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
/**
 * @return list of names of known caches
 */
public static ImmutableList<String> getKnownCacheNames() {
    ArrayList<String> names = new ArrayList<String>();
    lock.readLock().lock();
    try {
        for (AbstractWSCache<?,?> wsc : theTracker.getKnownCaches()) {
            names.add(wsc.myThread.getName());
        }
    } finally {
        lock.readLock().unlock();
    }
    return ImmutableList.copyOf(names);
}
  /**
   * public utility to close all cache threads
   */
public static void closeAllThreads() {
    boolean gotLock = lock.writeLock().tryLock();
    if ( ! gotLock) {
        cfLog.fatal("can't get lock in CloseAllThreads");
    }
    try {
        if ( theTracker.isEmpty()) return;
        for (AbstractWSCache<?,?> w : theTracker.getKnownCaches() ) {
            w.myThread.interrupt();
        }
        theTracker.clear();
    } finally {
        lock.writeLock().unlock();
    }
}
public static void addToTracker(AbstractWSCache<?,?> arg) {
    Preconditions.checkNotNull(arg);
    lock.writeLock().lock();
    try {
        theTracker.add(arg);
    } finally {
        lock.writeLock().unlock();
    }
}

  /**
   * creates and instantiates the usual default class implementing a WorkingSetCache.
   */
public static void doDefaultSetup() {
    lock.writeLock().lock();
    try {
        if (wscClass == null) {
            cfLog.debug("starting a WorkingSetCache");
            setWscClass(WorkingSetCache.class);
        }
        if (dbcClass == null) {
            cfLog.debug("starting a DatabaseCache");
            setDbcClass(DatabaseCache.class);
        }
    } finally {
        lock.writeLock().unlock();
    }
}
}
