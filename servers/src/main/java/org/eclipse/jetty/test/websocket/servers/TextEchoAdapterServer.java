//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
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

package org.eclipse.jetty.test.websocket.servers;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class TextEchoAdapterServer
{
    public static void main(String[] args)
    {
        try
        {
            int port = 28282;
            if (args != null && args.length >= 1)
            {
                port = Integer.parseInt(args[0]);
            }

            new TextEchoAdapterServer().start(port);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void start(int port) throws Exception
    {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        server.setHandler(new SingleSocketHandler(TextEchoSocket.class));

        server.start();

        String host = connector.getHost();
        if (host == null)
        {
            host = "localhost";
        }
        System.out.printf("WebSocket TextEcho Server started on ws://%s:%d/%n",host,connector.getLocalPort());
        System.out.printf("Use CTRL-C to exit%n");
        server.join();
    }
}
