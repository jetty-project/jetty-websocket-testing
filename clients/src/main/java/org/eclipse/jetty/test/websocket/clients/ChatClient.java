package org.eclipse.jetty.test.websocket.clients;

import java.net.URI;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class ChatClient
{
    public static void main(String[] args) throws Exception
    {
        int id = ThreadLocalRandom.current().nextInt(1000);
        WebSocketClient client = new WebSocketClient();
        client.start();
        final AtomicReference<Session> r = new AtomicReference<>();
        client.connect(new WebSocketAdapter()
        {
            public void onWebSocketConnect(Session sess)
            {
                r.set(sess);
            }
        },new URI("ws://localhost:61616")).get();
        for (;;)
        {
            r.get().getRemote().sendString("Hi from " + id);
            Thread.sleep(1000);
        }
    }
}
