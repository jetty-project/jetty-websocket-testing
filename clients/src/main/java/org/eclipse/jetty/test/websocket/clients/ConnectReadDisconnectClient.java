package org.eclipse.jetty.test.websocket.clients;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * Connect, read a single Text message, Disconnect
 */
public class ConnectReadDisconnectClient
{
    public static void main(String[] args)
    {
        try
        {
            URI uri = new URI("ws://localhost:28282/");
            if (args != null && args.length >= 1)
            {
                uri = new URI(args[0]);
            }
            new ConnectReadDisconnectClient().start(uri);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private void start(URI uri) throws Exception
    {
        WebSocketClient client = new WebSocketClient();
        try
        {
            // start client lifecycle
            client.start();

            System.out.printf("Attempting to connect to %s%n",uri);
            ReadSingleTextSocket socket = new ReadSingleTextSocket();
            client.connect(socket,uri);
            String message = socket.readMessage(10,TimeUnit.SECONDS);
            System.out.printf("Got message [%s]%n",message);
        }
        finally
        {
            client.stop();
            System.out.printf("Client stopped%n",uri);
        }
    }
}
