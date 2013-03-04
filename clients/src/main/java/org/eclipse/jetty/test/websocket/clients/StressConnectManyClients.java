package org.eclipse.jetty.test.websocket.clients;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class StressConnectManyClients
{
    public static void main(String[] args)
    {
        try
        {
            URI uri = new URI("ws://localhost:28282/");
            int count = 1000;
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
        int success = 0;
        int failure = 0;

        WebSocketClient client = new WebSocketClient();
        try
        {
            // start client lifecycle
            client.start();

            System.err.printf("Attempting to connect %d times to %s%n",count,uri);
            for (int i = 0; i < count; i++)
            {
                ReadSingleTextSocket socket = new ReadSingleTextSocket();
                client.connect(socket,uri);
                try
                {
                    String message = socket.readMessage(2,TimeUnit.SECONDS);
                    success++;
                    System.err.printf("[%d] Server Message -> \"%s\"%n",i,message);
                }
                catch (RuntimeException e)
                {
                    failure++;
                    Session session = socket.getSession();
                    int port = session.getLocalAddress().getPort();
                    System.err.printf("[%d] Failed to read message from server: %s (client port %d)%n",i,e.getMessage(),port);
                }
            }
        }
        finally
        {
            client.stop();
            System.out.printf("Client stopped%n",uri);
            System.out.printf("Run Results:%n");
            System.out.printf("    Success: %d%n",success);
            System.out.printf("    Failure: %d%n",failure);
        }
    }
}
