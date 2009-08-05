/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.modcluster.mcmp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;

/**
 * Handles communication via MCMP with the httpd side.
 * 
 * @author Brian Stansberry
 * @version $Revision$
 */
public interface MCMPHandler
{  
   /** Initialize the handler with the given list of proxies */
   void init(List<AddressPort> initialProxies);
   
   /** Perform any shut down work. */
   void shutdown();
   
   /** 
    * Send a request to all healthy proxies.
    * 
    * @param request the request. Cannot be <code>null</code>
    **/
   void sendRequest(MCMPRequest request);
   
   /** 
    * Send a list of requests to all healthy proxies, with all requests
    * in the list sent to each proxy before moving on to the next.
    * 
    * @param requests the requests. Cannot be <code>null</code>
    */
   void sendRequests(List<MCMPRequest> requests);
   
   /**
    * Add a proxy to the list of those with which this handler communicates.
    * Communication does not begin until the next call to {@link #status()}.
    * 
    * @param address a string in the form hostname:port, where the hostname
    *                portion is suitable for passing to <code>InetAddress.getByHost(...)</code>
    */
   void addProxy(String address);
   
   /**
    * Add a proxy to the list of those with which this handler communicates.
    * Communication does not begin until the next call to {@link #status()}.
    * 
    * @param host the hostname of the proxy; a string suitable for passing to 
    *             <code>InetAddress.getByHost(...)</code> 
    * @param port the port on which the proxy listens for MCMP requests
    */
   void addProxy(String host, int port);
   
   /**
    * Add a proxy to the list of those with which this handler communicates.
    * Communication does not begin until the next call to {@link #status()}.
    * <p>
    * Same as {@link #addProxy(InetAddress, int, boolean) addProxy(address, port, false}.
    * </p>
    * 
    * @param address InetAddress on which the proxy listens for MCMP requests
    * @param port  the port on which the proxy listens for MCMP requests
    */
   void addProxy(InetAddress address, int port);
   
   /**
    * Add a proxy to the list of those with which this handler communicates.
    * Communication does not begin until the next call to {@link #status()}.
    * 
    * @param address InetAddress on which the proxy listens for MCMP requests
    * @param port  the port on which the proxy listens for MCMP requests
    * @param established <code>true</code> if the proxy should be considered 
    *                    {@link MCMPServer#isEstablished() established},
    *                    <code>false</code> otherwise.
    */
   void addProxy(InetAddress address, int port, boolean established);
   
   /**
    * Remove a proxy from the list of those with which this handler communicates.
    * Communication does not end until the next call to {@link #status()}.
    * 
    * @param host the hostname of the proxy; a string suitable for passing to 
    *             <code>InetAddress.getByHost(...)</code> 
    * @param port the port on which the proxy listens for MCMP requests
    */
   void removeProxy(String host, int port);
   
   /**
    * Remove a proxy from the list of those with which this handler communicates.
    * Communication does not begin until the next call to {@link #status()}.
    * 
    * @param address InetAddress on which the proxy listens for MCMP requests
    * @param port  the port on which the proxy listens for MCMP requests
    */
   void removeProxy(InetAddress address, int port);
   
   /**
    * Get the state of all proxies
    * 
    * @return a set of status objects indicating the status of this handler's
    *         communication with all proxies.
    */
   Set<MCMPServerState> getProxyStates();
   
   /**
    * Reset any proxies whose status is {@link MCMPServerState#DOWN DOWN} up to 
    * {@link MCMPServerState#ERROR ERROR}, where the configuration will
    * be refreshed.
    */
   void reset();
   
   /** 
    * Reset any proxies whose status is {@link MCMPServerState#OK OK} down to 
    * {@link MCMPServerState#ERROR ERROR}, which will trigger a refresh of 
    * their configuration.
    */
   void markProxiesInError();   
   
   /**
    * Convenience method that checks whether the status of all proxies is
    * {@link MCMPServerState#OK OK}.
    * 
    * @return <code>true</code> if all proxies are {@link MCMPServerState#OK OK},
    *         <code>false</code> otherwise
    */
   boolean isProxyHealthOK();
   
   /**
    * Attempts to determine the address via which this node communicates
    * with the proxies.
    * 
    * @return the address, or <code>null</code> if it cannot be determined
    * 
    * @throws IOException
    */
   InetAddress getLocalAddress() throws IOException;
   
   /**
    * Sends a {@link MCMPRequestType#DUMP DUMP} request to all proxies,
    * concatentating their responses into a single string.
    * 
    * TODO wouldn't a List<String> be better? Let the caller concatenate if
    * so desired.
    * 
    * @return the configuration information from all the accessible proxies.
    */
   String getProxyConfiguration();
   
   /**
    * Sends a {@link MCMPRequestType#INFO INFO} request to all proxies,
    * concatentating their responses into a single string.
    * 
    * @return the configuration information from all the accessible proxies.
    */
   String getProxyInfo();
   
   /**
    * Perform periodic processing. Update the list of proxies to reflect any
    * calls to <code>addProxy(...)</code> or <code>removeProxy(...)</code>.
    * Attempt to establish communication with any proxies whose state is
    * {@link MCMPServerState#ERROR ERROR}. If successful and a 
    * {@link ResetRequestSource} has been provided, update the proxy with the 
    * list of requests provided by the source.
    */
   void status();
   
}
