package org.eclipse.jetty.test.websocket.servers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ChatServer extends WebSocketAdapter
{
    public static final boolean IS_ASYNC = true; // change this flag
    static CopyOnWriteArraySet<Session> s = new CopyOnWriteArraySet<>();

    public static void main(String[] args) throws Exception
    {
        try
        {
            WebSocketHandler wsHandler = new WebSocketHandler()
            {
                @Override
                public void configure(WebSocketServletFactory factory)
                {
                    factory.setCreator(new WebSocketCreator()
                    {
                        @Override
                        public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp)
                        {
                            return new ChatServer();
                        }
                    });
                }
            };

            Server server = new Server(new InetSocketAddress(61616));
            server.setHandler(wsHandler);
            server.start();
            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

    @Override
    public void onWebSocketConnect(Session sess)
    {
        s.add(sess);
    }

    @Override
    public void onWebSocketText(final String message)
    {
        System.out.println(message);
        for (final Session ses : s)
        {
            if (IS_ASYNC)
            {
                ses.getRemote().sendStringByFuture(message);
            }
            else
            {
                try
                {
                    ses.getRemote().sendString(message);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
