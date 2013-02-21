package org.eclipse.jetty.test.websocket.clients;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class ReadSingleTextSocket extends WebSocketAdapter
{
    private CountDownLatch latch = new CountDownLatch(1);
    private String message;

    @Override
    public void onWebSocketText(String message)
    {
        this.message = message;
        latch.countDown();
    }

    public String readMessage(int timeout, TimeUnit unit)
    {
        try
        {
            if (latch.await(timeout,unit))
            {
                return this.message;
            }
            else
            {
                throw new RuntimeException("Read timed out");
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
