package org.eclipse.jetty.test.websocket.clients;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.WebSocketClient;

public class StressConnectManyClients
{
    public static void main(String[] args)
    {
        try
        {
            URI uri = new URI("ws://localhost:28282/");
            int count = 50;
            if (args != null)
            {
                if (args.length >= 1)
                {
                    uri = new URI(args[0]);
                }
                if (args.length >= 2)
                {
                    count = Integer.parseInt(args[1]);
                }
            }
            new StressConnectManyClients().start(uri,count);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private void start(URI uri, int count) throws Exception
    {
        WebSocketClient client = new WebSocketClient();
        try
        {
            // start client lifecycle
            client.start();

            System.out.printf("Attempting to connect %d times to %s%n",count,uri);
            for (int i = 0; i < count; i++)
            {
                ReadSingleTextSocket socket = new ReadSingleTextSocket();
                client.connect(socket,uri);
                try
                {
                    String message = socket.readMessage(2,TimeUnit.SECONDS);
                    System.out.printf("[%d] Server Message -> \"%s\"%n",i,message);
                }
                catch (RuntimeException e)
                {
                    System.out.printf("[%d] Failed to read message from server: %s%n",i,e.getMessage());
                }
            }
        }
        finally
        {
            client.stop();
            System.out.printf("Client stopped%n",uri);
        }
    }
}
