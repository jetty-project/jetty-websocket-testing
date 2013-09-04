package org.eclipse.jetty.test.websocket.servers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ChatServer extends WebSocketAdapter
{
    public static final boolean IS_ASYNC = false; // change this flag
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
                    factory.getPolicy().setIdleTimeout(1500);
                    factory.setCreator(new WebSocketCreator()
                    {
                        @Override
                        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
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

    private String remoteId = "<unconnected>";
    private Session session;

    private void debug(String format, Object... args)
    {
        System.out.printf("[" + remoteId + "] " + format + "%n",args);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        debug(" ## Close: [%d,%s]",statusCode,reason);
        s.remove(this.session);
        remoteId = "<unconnected>";
    }

    @Override
    public void onWebSocketConnect(Session sess)
    {
        this.session = sess;
        this.remoteId = session.getRemoteAddress().toString();
        debug(" ## Connect: %s",sess);
        s.add(sess);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        debug(" ## Error: ");
        cause.printStackTrace();
        s.remove(this.session);
    }

    @Override
    public void onWebSocketText(final String message)
    {
        debug(message);
        for (final Session ses : s)
        {
            if (!ses.isOpen())
            {
                debug("Skipping non-open session: %s",ses.getRemoteAddress());
                continue; // skip non-open sessions (let onClose/onError take care of removing it)
            }

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
