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
