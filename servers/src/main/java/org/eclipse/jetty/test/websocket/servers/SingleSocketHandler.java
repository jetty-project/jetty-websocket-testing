package org.eclipse.jetty.test.websocket.servers;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class SingleSocketHandler extends WebSocketHandler
{
    private Class<?> websocketClass;

    public SingleSocketHandler(Class<?> websocketClass)
    {
        this.websocketClass = websocketClass;
    }

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.register(websocketClass);
    }
}
