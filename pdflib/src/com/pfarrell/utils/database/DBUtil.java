/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 */

package  com.pfarrell.utils.database;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.pfarrell.busobj.AbstractPersistentBusinessObject;
import com.pfarrell.exceptions.PibException;
import com.pfarrell.utils.misc.TimeUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.apache.log4j.Logger;

/**
 * The <tt><DBUtil/tt> class contains handy utilty functions for
 * use with Sql
 */
public abstract class DBUtil {
    /** our log4j logger */
protected static final Logger dbLog = Logger.getLogger(DBUtil.class);
    /**
     * do a generic query that returns an array of primary keys, return in
     * an ArrayList of <Integers> so we can auto-unbox and use it.
     * 
     * @return ArrayList<Long> of longs returned.
     * @param query SQL string that will yield one column of integers
     * @throws java.sql.SQLException pass up any SQL problems
     */
    public static ArrayList<Long> getPrimaryKeysFromQuery(String query) throws SQLException {
        ArrayList<Long> rval = new ArrayList<Long>();
        Connection myCon = DBConnectionPool.getConnection();
        Statement  s = myCon.createStatement();
        if (dbLog != null) {
            dbLog.info(query);
        }

        ResultSet rs= s.executeQuery(query);
        boolean next = rs.next();
        while (next) {
            long id = rs.getLong(1);
            rval.add(id);
            next = rs.next();
        }
        s.close();

        DBConnectionPool.returnCon(myCon);
        return rval;
    }
    /**
     * gets the count of records in the table for the argument class
     * @param boc class of a AbstractPersistentBusinessObject
     * @return  the count of records in the table for the argument class
     * @throws SQLException pass up any SQL problems
     * @throws InstantiationException pass up any Instantiation problems.
     * @throws IllegalAccessException pass up any access problems.
     */
    public static int getCountFromClass(Class<? extends AbstractPersistentBusinessObject> boc) throws SQLException, InstantiationException,  IllegalAccessException  {
        return getCountFromClass(boc, "");
    }
    /**
     * gets the count of records in the table for the argument class
     * @param boc class of a AbstractPersistentBusinessObject
     * @param where a SQL where-clause such as "where age > 30"
     * @return  the count of records in the table for the argument class
     * @throws SQLException pass up any SQL problems
     * @throws InstantiationException pass up any Instantiation problems.
     * @throws IllegalAccessException pass up any access problems.
     */
    public static int getCountFromClass(Class<? extends AbstractPersistentBusinessObject> boc, String where) throws SQLException, InstantiationException, IllegalAccessException  {
        int rval = 0;
        try {
            AbstractPersistentBusinessObject asCast = (AbstractPersistentBusinessObject) boc.cast(boc.newInstance());
            String query = "select count(*) from " +  asCast.getTable() + (where != null && ! where.isEmpty() ? where : "");
            Connection myCon = DBConnectionPool.getConnection();
            Statement  s = myCon.createStatement();
            Logger.getLogger(DBUtil.class).info(query);

            ResultSet rs= s.executeQuery(query);
            boolean next = rs.next();
            while (next) {
                int val = rs.getInt(1);
                rval = val;
                next = rs.next();
            }
            s.close();

            DBConnectionPool.returnCon(myCon);

        } catch (ClassCastException cce) {
            dbLog.error("DBUtils.getCountFromClass only works for PersistentBusinessObject", cce);
        }

        return rval;
    }
    
    /**
     * do a generic query that returns an array of primary keys, return in
     * an ArrayList of <Integers> so we can auto-unbox and use it.
     * @param boc a class that is a subclass of a AbstractPersistentBusinessObject
     * @return List of integers returned.
     * @throws java.sql.SQLException pass up any SQL problems
     * @throws java.lang.InstantiationException pass up exceptions
     * @throws java.lang.IllegalAccessException pass up exceptions
     */
    public static List<Integer> getPrimaryKeysFromClass(Class<? extends AbstractPersistentBusinessObject> boc) throws SQLException, InstantiationException,  IllegalAccessException  {
        List<Integer> rval = new ImmutableList.Builder<Integer>().build();
        AbstractPersistentBusinessObject asCast = null;
        try {
            asCast =  (AbstractPersistentBusinessObject) boc.cast(boc.newInstance());
        } catch (ClassCastException cce) {
            dbLog.error("DBUtils.getPrimaryKeysFromQuery only works for PersistentBusinessObject", cce);
            return rval;
        }
        if ( asCast == null) return null;
        rval = new ArrayList<Integer>();
        String primeKey = asCast.getIdFieldName();
        String query = "select " + primeKey + " from " + asCast.getTable();
        Connection myCon = DBConnectionPool.getConnection();
        Statement  s = myCon.createStatement();
        dbLog.info(query);

        ResultSet rs= s.executeQuery(query);
        boolean next = rs.next();
        while (next) {
            int id = rs.getInt(1);
            rval.add(id);
            next = rs.next();
        }
        s.close();

        DBConnectionPool.returnCon(myCon);
        return rval;
    }    
    /**
     * delete all records in specified table
     * @return true if we think it worked
     * @param table table name to zap
     * @throws java.sql.SQLException pass up exceptions
     */
public static boolean deleteFromTable(String table)  throws SQLException {
    Connection myCon = null;
    Statement stmt = null;
    int numRows = 0;
    myCon = DBConnectionPool.getConnection();
    String command = " delete from " + table ;
    stmt = myCon.createStatement();
    Logger.getLogger(DBUtil.class).info(command);
    numRows = stmt.executeUpdate(command);
    DBConnectionPool.returnCon(myCon);
    stmt.close();
    return numRows == 1;
}

    /**
     * performs an DB execute on the argument string
     * @param command the command to execute
     * @throws SQLException pass up any Sql problems
     */
public static void execute(String command) throws SQLException {
    Connection myCon = DBConnectionPool.getConnection();
    Statement stmt = myCon.createStatement();
    dbLog.debug(command);
    stmt.execute(command);
    stmt.close();
    DBConnectionPool.returnCon(myCon);
    stmt.close();
}
   /**
    * gets an array of distinct String values and the count of occurances from the argument class
    * filtered by the where clause.
    * @param fieldname table field, must be String valued
    * @param where optional where clause for additional filtering
    * @param boc the class of the BusinessObject
    * @return array<Pair<String, int>> from the table
    * @throws SQLException  pass up any Sql problems
    * @throws InstantiationException  pass up any instantiation problems
    * @throws IllegalAccessException  pass up any access problems
    */
public static List<Pair<String, Integer>> getDistinctStringsAndCountFromClass(String fieldname, String where, Class<? extends AbstractPersistentBusinessObject> boc) throws SQLException, InstantiationException,  IllegalAccessException  {
    Preconditions.checkNotNull(fieldname);
    Preconditions.checkNotNull(where);
    Preconditions.checkNotNull(boc);
    ArrayList<Pair<String, Integer>> rval = new ArrayList<Pair<String, Integer>>();
    AbstractPersistentBusinessObject asCast = null;
    try {
        asCast =  (AbstractPersistentBusinessObject) boc.cast(boc.newInstance());
    } catch (ClassCastException cce) {
        dbLog.error("DBUtils.getDistinctStringFromQuery only works for PersistentBusinessObject", cce);
        return null;
    }
    if ( asCast == null) return rval;
    //select distinct(tags), count(*) v from blogentry group by tags order by v desc
    String wherePart = where != null && where.length() > 0 ? where : "";
    String query = "select distinct(" + fieldname + "), count(*) v from "
            + asCast.getTable() + " " + wherePart
            + " group by " + fieldname + "  order by v desc";
    Connection myCon = DBConnectionPool.getConnection();
    Statement  s = myCon.createStatement();
    dbLog.debug(query);

    ResultSet rs= s.executeQuery(query);
    boolean next = rs.next();
    while (next) {
        String val = rs.getString(1);
        int count = rs.getInt(2);
        Pair<String, Integer> aPair = new Pair<String, Integer>(val, count);
        rval.add(aPair);
        next = rs.next();
    }
    s.close();

    DBConnectionPool.returnCon(myCon);
    return rval;
}
    /**
     * do a generic query that returns an array of distinct foreign keys, return in
     * an ArrayList of <Integers> so we can auto-unbox and use it.
     * @param foreignKey name of field in table to execute distinct() on
     * @param boc a class that is a subclass of a AbstractPersistentBusinessObject
     * @return ArrayList of integers returned.
     * @throws java.sql.SQLException pass up any SQL problems
     * @throws java.lang.InstantiationException pass up exceptions
     * @throws java.lang.IllegalAccessException pass up exceptions
     */
public static List<Pair<Long, Integer>>
            getDistinctKeysAndCountFromClass(String foreignKey, Class<? extends AbstractPersistentBusinessObject> boc)
                        throws SQLException, InstantiationException,  IllegalAccessException  {
    return getDistinctKeysAndCountFromClass( foreignKey, boc, -1);
}
    /**
     * do a generic query that returns an array of distinct foreign keys, return in
     * an ArrayList of <Integers> so we can auto-unbox and use it.
     * @param foreignKey name of field in table to execute distinct() on
     * @param boc a class that is a subclass of a AbstractPersistentBusinessObject
     * @param limit limit to the number of records returned, -1 for no limit, any positive interger will be passed
     * to the SQL query
     * @return ArrayList of integers returned.
     * @throws java.sql.SQLException pass up any SQL problems
     * @throws java.lang.InstantiationException pass up exceptions
     * @throws java.lang.IllegalAccessException pass up exceptions
     */
public static List<Pair<Long, Integer>> 
        getDistinctKeysAndCountFromClass(String foreignKey, Class<? extends AbstractPersistentBusinessObject> boc, int limit)
                    throws SQLException, InstantiationException,  IllegalAccessException  {
    Preconditions.checkNotNull(foreignKey);
    Preconditions.checkNotNull(boc);
    AbstractPersistentBusinessObject asCast = null;
    try {
        asCast =  (AbstractPersistentBusinessObject) boc.cast(boc.newInstance());
    } catch (ClassCastException cce) {
        dbLog.error("DBUtils.getDistinctKeysAndCountFromClass only works for PersistentBusinessObject", cce);
        return  new ImmutableList.Builder<Pair<Long, Integer>>().build();
    }
    if ( asCast == null) {
        return  new ImmutableList.Builder<Pair<Long, Integer>>().build();
    }
    String query = "select distinct(" + foreignKey + "), count(*) c from "
            + asCast.getTable() + " group by " + foreignKey 
            + " order by c desc, " + foreignKey
            + ((limit > 0) ? " limit " + limit : "");

    return doKeysAndCountFromQuery(query, boc);
}
   /**
    * gets the Pair of numbers, Long and Integer from query string
    * @param query Sql query ready to go, must have two fields: first is some bigint (usually primary key or foreign
    * key of a table) and second an int/Integer that is usually the count.
    * @param boc a class that is a subclass of a AbstractPersistentBusinessObject
    * @return ArrayList of integers returned.
    * @throws java.sql.SQLException pass up any SQL problems
    * @throws java.lang.InstantiationException pass up exceptions
    * @throws java.lang.IllegalAccessException pass up exceptions
    */
public static List<Pair<Long, Integer>>
                    doKeysAndCountFromQuery(String query, Class<? extends AbstractPersistentBusinessObject> boc)
                                        throws SQLException, InstantiationException,  IllegalAccessException  {
    Preconditions.checkNotNull(query);
    Preconditions.checkNotNull(boc);
    ArrayList<Pair<Long, Integer>> rval = new ArrayList<Pair<Long, Integer>>();
    Connection myCon = DBConnectionPool.getConnection();
    Statement  s = myCon.createStatement();
    dbLog.debug(query);

    ResultSet rs= s.executeQuery(query);
    boolean next = rs.next();
    while (next) {
        long id = rs.getLong(1);
        int count = rs.getInt(2);
        Pair<Long, Integer> aPair = new Pair<Long, Integer>(id, count);
        rval.add(aPair);
        next = rs.next();
    }
    s.close();

    DBConnectionPool.returnCon(myCon);
    return rval;
}        
    
    /**
     * do a generic query that returns an array of strings, return in
     * an ArrayList<String> so we can auto-unbox and use it.
     * 
     * @return ArrayList of Strings returned.
     * @param query SQL string that will yield one column of integers
     * @throws java.sql.SQLException pass up any SQL problems
     */
public static List<String> getStringsFromQuery(String query) throws SQLException {
    ArrayList<String> rval = new ArrayList<String>();
    Connection myCon = DBConnectionPool.getConnection();
    Statement  s = myCon.createStatement();
    Logger.getLogger(DBUtil.class).info(query);

    ResultSet rs= s.executeQuery(query);
    boolean next = rs.next();
    while (next) {
        String val = rs.getString(1);
        rval.add(val);
        next = rs.next();
    }
    s.close();

    DBConnectionPool.returnCon(myCon);
    return rval;
}
   /**
     * performs a generic query that returns two string values per row of the result set, returning the values in a 
     * Pair<String,String>  
     * @param query sql query ready to go
     * @return list of Pair returned
     * @throws SQLException pass up any Sql issues
     */ 
public static List<Pair<String,String>> getStringPairsFromQuery(String query) throws SQLException {
    List<Pair<String,String>> rval = new ArrayList<Pair<String,String>>();
    Connection myCon = DBConnectionPool.getConnection();
    Statement  s = myCon.createStatement();
    Logger.getLogger(DBUtil.class).info(query);

    ResultSet rs= s.executeQuery(query);
    boolean next = rs.next();
    while (next) {
        String v1 = rs.getString(1);
        String v2 = rs.getString(2);
        Pair<String,String> aP = new Pair<String,String>(v1, v2);
        rval.add(aP);
        next = rs.next();
    }
    s.close();

    DBConnectionPool.returnCon(myCon);
    return rval;
}
    /**
     * Place single quotes around input boolean
     * @param arg incoming enum to quote
     * @return quoted string
     */
public static String escapeInsert(boolean arg) {
    return arg ? "'true'" : "'false'";
}
    /**
     * Place single quotes around input enum
     * Escape single quotes that will cause problems.
     * @param arg incoming enum to quote
     * @return quoted string
     */
    public static String escapeInsert(Enum arg) {
        StringBuilder sb = new StringBuilder();
        if ( arg == null)
            sb.append("NULL");
        else
            sb.append("'").append(arg.toString()).append("'");
        return sb.toString();
    }
    /**
     * Place single quotes around input string,
     * Escape single quotes that will cause problems.
     * 
     * @param s incomming string to quote
     * @param max maximum allowable length
     * @return quoted string
     */
    public static String escapeInsert(String s, int max) {
        if (s == null) return "NULL";
        if (s.isEmpty()) return "\'\'";
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        int maxsize = Math.min(s.length(), max);
        for (int i=0; i < maxsize; i++) {
            char ch = s.charAt(i);
            if (ch == '\'')  sb.append("\\'");      //ugly code too many doubled backslashes
            else if (ch == '\\') sb.append("\\\\");
            else if (ch == '\n') sb.append("\\n");
            else if (ch == '\r') sb.append("");
            else sb.append(ch);
        }
        sb.append("'");
        return sb.toString();
    }
    /**
     * Place single quotes around input string,
     * Escape single quotes that will cause problems.
     * 
     * @param s incomming string to quote
     * @return quoted string
     */
    public static String escapeInsert(String s) {
        if (s == null) return "NULL";
        if (s.isEmpty()) return "\'\'";
        return escapeInsert(s, s.length());
    }
    /**
     * Place double quotes arround input string,
     * Escape single quotes that will cause problems.
     * remove any CR, CRLF, newlines, etc.
     * 
     * @param s incomming string to quote
     * @return quoted string
     */
    public static String doubleQuote(String s) {
        if (s == null) return "NULL";
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\'')  sb.append("\\'");      //ugly code too many doubled backslashes
            else if (ch == '\\') sb.append("\\\\");
            else if (ch == '\n')  sb.append(" ");
            else if (ch == '\r')  sb.append(" ");
            else sb.append(ch);
        }
        sb.append('"');
        return sb.toString();
    }
    /**
     * remove any commans in  input string,
     * Escape single quotes that will cause problems.
     * remove any CR, CRLF, newlines, etc.
     * 
     * @param s incomming string to quote
     * @return quoted string
     */
    public static String commaFree(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ',')  sb.append(" ");
            else if (ch == '\'')  sb.append("\\'");      //ugly code too many doubled backslashes
            else if (ch == '\\') sb.append("\\\\");
            else if (ch == '\n')  sb.append(" ");
            else if (ch == '\r')  sb.append(" ");
            else sb.append(ch);
        }
        return sb.toString();
    }
    /**
     * generate a nice string representation of this floating point number suitable for Sql
     * @param f input floating point number
     * @return nice string that sql will like
     */
    public static String floatForSql(Float f) {
        if (f == null) return "NULL";
        Formatter fmt = new Formatter();
        fmt.format("%.2f", f);
        return fmt.toString();
    }
    /**
     * Place single quotes around input string,
     * convert input string to suitable string escaped for a Query
     * @param s input string
     * @return escaped string
     */
    public static String escapeQuery(String s) {
        if (s == null) return "NULL";
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        for (int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\'')  sb.append("\\'");      //ugly code too many doubled backslashes
            else if (ch == '\\') sb.append("\\\\");
            else if (ch == '%')  sb.append("\\%");
            else sb.append(ch);
        }
        sb.append("'");
        return sb.toString();
    }
    /**
     * Place single quotes around input string, leaving % character alone,
     * convert input string to suitable string escaped for a Query
     * @param s input string
     * @return escaped string
     */
    public static String escapeLikeQuery(String s) {
        if (s == null) return "NULL";
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        for (int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\'')  sb.append("\\'");      //ugly code too many doubled backslashes
            else if (ch == '\\') sb.append("\\\\");
            else sb.append(ch);
        }
        sb.append("'");
        return sb.toString();
    }
    /**
     * wrap argument string with single quotes, remove internal quotes
     * @param s input string
     * @return wrapped string
     */
    public static String safeQuery(String s) {
        if (s == null) return "NULL";
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        sb.append(noSqlInjection(s));
        sb.append("'");
        return sb.toString();
    }
   /**
    * remove single quotes and double quotes from string to make it safer against sql injection
    * @param s input string
    * @return cleaned up string
    */
    public static String noSqlInjection(String s) {
        if (s == null) return "NULL";
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\'':
                case '\"':
                case '\\':
                case '%':
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
    /**
     * create "in" input array from set of AbstractPersistentBusinessObject using the getId()
     * result is suitable for use in a Sql select "in ()" clause
     * @param <T> generic type of the AbstractPersistentBusinessObject
     * @param s AbstractPersistentBusinessObject collection or Iterable
     * @return ( ... list ...) ready to use in a SQL select 
     */    
    public static <T extends AbstractPersistentBusinessObject> String makeInClauseFromPBO(Iterable<T> s) {
        ArrayList<Long> idList = new ArrayList<Long>();
        for ( T anElement : s) {
            idList.add(anElement.getId());
        }
        return makeInClause(idList);
    }

    /**
     * convert input array into set of numbers or values suitable
     * for use in a Sql select "in ()" clause
     * @param s source array
     * @return ( ... list ...) ready to use in a SQL select 
     */    
    public static String makeInClause(Iterable s) {
        return "(" + makeCommaSeparatedString(s) + ")";
    }
    /**
     * convert input array into set of numbers or values suitable
     * @param s  source array
     * @return comma separated list of numbers
     */
    public static String makeCommaSeparatedString(Iterable s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Iterator it = s.iterator(); it.hasNext(); ) {
            if (first) first = false;
            else sb.append(",");
            Object o = it.next();
            if (o == null) continue;        // nulls are not valid in in clause
            if ( o instanceof Number)
                sb.append(o.toString());
            else 
                sb.append(escapeQuery(o.toString()));
        }
        return sb.toString();
    }
   /**
    * SET GLOBAL time_zone = 'UCT';
    * SET time_zone = 'UCT';
    * @return the SQL server's session time zone.
    */
public static TimeZone ensureUtcTimeZone() {
    String command = "SET time_zone = 'UTC'";
    try {
        execute(command);
    } catch (SQLException ex) {
       dbLog.error( ex);
    }
    return getSqlTimeZone();
}
   /**
    * returns the "session" TimeZone from the sql server 
    * @return the "session" TimeZone from the sql server
    */
public static TimeZone getSqlTimeZone() {
    return getSqlTimeZone(TimeZoneScope.session);
}
   /**
    * returns the TimeZone from the sql server for the argument flavor
    * @param arg which value to return, MySql has only two:  @@global.time_zone, @@session.time_zone
    * @return the TimeZone from the sql server for the argument
    */
public static TimeZone getSqlTimeZone(TimeZoneScope arg) {
    String working = getSqlTimeZoneString(TimeZoneScope.session);
    TimeZone rval = TimeZone.getTimeZone(working);
    return rval;
}
   /**
    * gets the string 'id" TimeZone from the sql server for the @@session.time_zone
    * @return the string ID TimeZone from the sql server for the @@session.time_zone
    */
public static String getSqlTimeZoneString() {
    return getSqlTimeZoneString(TimeZoneScope.session);
}
   /**
    * gets the string 'id" TimeZone from the sql server for the argument flavor
    * @param arg which value to return, MySql has only two:  @@global.time_zone, @@session.time_zone
    * @return the string ID TimeZone from the sql server for the argument
    */
public static String getSqlTimeZoneString(TimeZoneScope arg) {
    String rval = "UTC";
    String query =  arg == TimeZoneScope.session ? "SELECT @@session.time_zone" : "SELECT @@global.time_zone";
    try {
        List<String> values = getStringsFromQuery(query);
        if (values != null) {
            if (values.size() == 1) {
                String working  = values.get(0);
                dbLog.debug(working);
                if ( working.equals(TimeUtils.utcTZ.getID())) {
                    rval = TimeUtils.utcTZ.getID();
                } else if (working.equalsIgnoreCase("system")) {
                    rval = TimeZone.getDefault().getID();
                } else {
                    rval = working;
                    dbLog.debug("unexpected Sql time zone " + working);
                }
            } else {
                String msg ="impossible number of time zone values from SQL";
                dbLog.error(msg);
                throw new PibException(msg);
            }
        } else {
            String msg ="impossible null time zone values from SQL";
            dbLog.error(msg);
            throw new PibException(msg);
        }
    } catch (SQLException ex) {
        dbLog.error("session zone", ex);
    }
    return rval;
}
   /** 
    * simply enum of major scope within MySql for time zones. MySql has only two:  @@global.time_zone, @@session.time_zone
    */
public static enum TimeZoneScope {
    /** everyone on this server */
    global,
    /** current session */
    session;
}
/** simple struct, define a nice pair of values
 * @param <X> generic type of first in pair
 * @param <Y> generic type of second in pair
 */
static  public class Pair<X extends Comparable,Y extends Comparable> implements Comparable<Pair<X,Y>> { 
      private X first;
      private Y second;

      /**
       * construct a pair
       * @param a1 first item
       * @param a2 second in tuple
       */
      public Pair(X a1, Y a2) {
        first  = a1;
        second = a2;
      }
      /**
       * gets first of tuple
       * @return first element
       */
      public X getFirst()  { return first; }
      /**
       * gets second of tuple
       * @return the second value
       */
      public Y getSecond() { return second; }
      /**
       * sets first in pair
       * @param arg the value to put first
       */
      public void setFirst(X arg)  { first = arg; }
      /**
       * sets the second in the pair
       * @param arg the value to place in spot two
       */
      public void setSecond(Y arg) { second = arg; }

      /**
       * usual compareTo based on contents of second value in pair
       * @param o other object to check
       * @return usual -1, 0, or 1
       */
      @SuppressWarnings("unchecked") 
        public int compareTo(DBUtil.Pair<X, Y> o) {
            return this.second.compareTo(o.second);
        }
    } 
    
}
