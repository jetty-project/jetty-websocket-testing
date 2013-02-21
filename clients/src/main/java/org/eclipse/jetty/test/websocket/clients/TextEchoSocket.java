package org.eclipse.jetty.test.websocket.clients;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class TextEchoSocket extends WebSocketAdapter
{
    @Override
    public void onWebSocketText(String message)
    {
        // echo string back
        if (isConnected())
        {
            getRemote().sendStringByFuture(message);
        }
    }
}
