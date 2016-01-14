//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.test.websocket.servers.browser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.test.websocket.servers.CommandSocket;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

/**
 * Tool to help debug websocket circumstances reported around browsers.
 * <p>
 * Provides a server, with a few simple websocket's that can be twiddled from a browser.
 * <p> 
 * This helps with setting up breakpoints and whatnot to help debug our
 * websocket implementation from the context of a browser client.
 */
public class BrowserBasedDebugTool
{
    private static final Logger LOG = Log.getLogger(BrowserBasedDebugTool.class);

    public static void main(String[] args)
    {
        int port = 8080;

        for (int i = 0; i < args.length; i++)
        {
            String a = args[i];
            if ("-p".equals(a) || "--port".equals(a))
            {
                port = Integer.parseInt(args[++i]);
            }
        }

        try
        {
            BrowserBasedDebugTool tool = new BrowserBasedDebugTool();
            tool.setupServer(port);
            tool.runForever();
        }
        catch (Throwable t)
        {
            LOG.warn(t);
        }
    }

    private Server server;

    private void runForever() throws Exception
    {
        server.start();
        // server.dumpStdErr();
        LOG.info("Server available.");
        server.join();
    }

    private void setupServer(int port) throws DeploymentException, ServletException, URISyntaxException, MalformedURLException
    {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
        
        // Figure out what path to serve content from
        ClassLoader cl = BrowserBasedDebugTool.class.getClassLoader();
        // We look for a file, as ClassLoader.getResource() is not
        // designed to look for directories (we resolve the directory later)
        URL f = cl.getResource("browser-base/index.html");
        if (f == null)
        {
            throw new RuntimeException("Unable to find resource directory");
        }

        // Resolve file to directory
        URI webRootUri = f.toURI().resolve("./").normalize();

        // Setup context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setBaseResource(Resource.newResource(webRootUri));
        context.setContextPath("/");
        context.addServlet(DefaultServlet.class,"/");
        server.setHandler(context);

        // Add endpoints
        ServerContainer container = WebSocketServerContainerInitializer.configureContext(context);
        container.addEndpoint(CommandSocket.class);

        LOG.info("{} setup on port {}",this.getClass().getName(),port);
    }
}
