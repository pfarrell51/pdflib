/*
 * TimedSocket.java
 *
 * Created on August 28, 2006, 5:57 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.utils.io;
import java.net.*;
import java.io.*;
import java.nio.channels.SocketChannel;

/**
 * The  <code>TimedSocket</code> class offers a timeout feature on socket connections.
 * A maximum length of time allowed for a connection can be
 * specified, along with a host and port. Converted to use java.nio so it does not
 * hang the thread by Pat Farrell
 *
 * @author David Reilly, for JavaWorld
 * @see <a href="http://www.javaworld.com/jw-09-1999/jw-09-timeout.html">http://www.javaworld.com/jw-09-1999/jw-09-timeout.html</a>
 */
public class TimedSocket {
    
    /** Creates a new instance of TimedSocket */
    public TimedSocket() {
    }
    
    /**
      * Attempts to connect to a service at the specified address
      * and port, for a specified maximum amount of time.
      *
      *	@param	addr	Address of host
      *	@param	port	Port of service
      * @param	delay	Delay in milliseconds
      */
    public static Socket getSocket ( InetAddress addr, int port, int delay) throws InterruptedIOException, IOException {
        // Create a new socket thread, and start it running
        SocketThread st = new SocketThread( addr, port );
        st.start();

        int timer = 0;
        Socket sock = null;

        for (;;)      {
            // Check to see if a connection is established
            if (st.isConnected()) {
                // Yes ...  assign to sock variable, and break out of loop
                sock = st.getSocket();
                break;
            }
            else {
                // Check to see if an error occurred
                if (st.isError())  {
                        // No connection could be established
                        throw (st.getException());
                }

                try  {
                    // Sleep for a short period of time
                    Thread.sleep ( POLL_DELAY );
                } catch (InterruptedException ie) {}

                // Increment timer
                timer += POLL_DELAY;

                // Check to see if time limit exceeded
                if (timer > delay)   {
                    // Can't connect to server
                    throw new InterruptedIOException("Could not connect for " + delay + " milliseconds");
                }
            }
        }
        return sock;
}

    /**
      * Attempts to connect to a service at the specified address
      * and port, for a specified maximum amount of time.
      *
      *	@param	host	Hostname of machine
      *	@param	port	Port of service
      * @param	delay	Delay in milliseconds
      */
    public static Socket getSocket ( String host, int port, int delay) throws InterruptedIOException, IOException   {
        // Convert host into an InetAddress, and call getSocket method
        InetAddress inetAddr = InetAddress.getByName (host);
        return getSocket ( inetAddr, port, delay );
    }

    public static void main(String args[]) throws Exception   {
        try   {
            //InetAddress addr = InetAddress.getByName("192.168.0.3");
            //Socket s = TimedSocket.getSocket (addr, 80, 5000);
            Socket s = TimedSocket.getSocket ("172.16.4.5", 80, 5000);
            s.close();
            System.out.println ("connected");
        }
        catch (IOException ioe)      {
            System.out.println ("time out");
            System.err.println(ioe);
        }
        threadInspector();
    }
/**
 * snoop arround at our threads and see why we are not exiting
 */
public static void threadInspector() {
    Thread current = Thread.currentThread();
    ThreadGroup thrGrp = current.getThreadGroup();
    Thread[] trdList = new Thread[thrGrp.activeCount()*2]; 
    int lth = thrGrp.enumerate(trdList, true );
    if ( lth >= trdList.length) {
        System.err.println("probably ignored some threads");
    }

    for ( int i = 0; i < trdList.length; i++) {
        if ( trdList[i] != null) {
            Thread aT = trdList[i];
            System.out.println(aT);
            aT.interrupt();
        }
    }
}
// Inner class for establishing a socket thread
// within another thread, to prevent blocking.
static class SocketThread extends Thread     {
    /** Socket connection to remote host */
    volatile private Socket connection = null;
    /** Internet Address to connect to */
    private InetAddress inet  = null;
    /** Port number to connect to */
    private int    port       = 0;
    /** Exception in the event a connection error occurs */
    private IOException exception = null;

    // Connect to the specified host IP and port number
    public SocketThread ( InetAddress inetAddr, int port )   {
        // Assign to member variables
        this.inet = inetAddr;
        this.port = port;
        setName("TimedSocketopenerByInetAddr");
    }

    public void run()  {
        // Socket used for establishing a connection
        Socket sock = null;
        InetSocketAddress isn  = new InetSocketAddress(inet, port);

        try   {
            // Connect to a remote host - BLOCKING I/O
            SocketChannel aSC = SocketChannel.open(isn);
            sock = aSC.socket();
        }
        catch (IOException ioe)    {
            // Assign to our exception member variable
            exception = ioe;
            return;
        }

        // If socket constructor returned without error,
        // then connection finished
        connection = sock;
    }

    // Are we connected?
    public boolean isConnected()   {
        if (connection == null)
            return false;
        else
            return true;
    }

    // Did an error occur?
    public boolean isError()      {
        if (exception == null)
            return false;
        else
            return true;
    }

    // Get socket
    public Socket getSocket() {
        return connection;
    }

    // Get exception
    public IOException getException()     {
        return exception;
    }
}

    // Polling delay for socket checks (in milliseconds)
    private static final int POLL_DELAY = 100;
}
