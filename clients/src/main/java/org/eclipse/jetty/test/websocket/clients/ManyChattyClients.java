//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.test.websocket.clients;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Many Chatty Clients.
 */
public class ManyChattyClients
{
    public class ChattyClient extends WebSocketAdapter implements Runnable
    {
        private static final boolean DEBUG = false;
        private final int clientNum;
        private final WebSocketClient client;
        private final CountDownLatch receiveLatch;
        private final long expectedTimeout;

        public ChattyClient(int socketNum, WebSocketClient client)
        {
            this.clientNum = socketNum;
            this.client = client;
            this.receiveLatch = new CountDownLatch(opts.numMessages + 1); // plus 1 for welcome message
            this.expectedTimeout = opts.numMessages * 1000 + opts.numMessages * opts.msgDelay;
        }

        private void debug(String format, Object... args)
        {
            if (DEBUG)
            {
                System.err.printf("[Client " + clientNum + "] " + format + "%n",args);
            }
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason)
        {
            super.onWebSocketClose(statusCode,reason);
            debug("Closed (%d - %s)",statusCode,reason);
        }

        @Override
        public void onWebSocketConnect(Session sess)
        {
            super.onWebSocketConnect(sess);
            debug("Connected");

            // Write Messages
            RemoteEndpoint remote = getRemote();

            for (int i = 0; i < opts.numMessages; i++)
            {
                remote.sendStringByFuture(String.format("Message [%05d]",i));
            }
        }

        @Override
        public void onWebSocketError(Throwable cause)
        {
            cause.printStackTrace(System.err);
        }

        @Override
        public void onWebSocketText(String message)
        {
            receiveLatch.countDown();
            long left = receiveLatch.getCount();
            if (left > 0)
            {
                debug("Waiting on %d messages",left);
            }
            else
            {
                debug("Got all Messages!");
            }
        }

        @Override
        public void run()
        {
            // Connect
            try
            {
                debug("Connecting");
                Future<Session> session = client.connect(this,opts.destUri);
                session.get(5,TimeUnit.SECONDS); // wait for connect
            }
            catch (IOException | InterruptedException | ExecutionException | TimeoutException e)
            {
                debug("Unable to connect: %s",e.getMessage());
                e.printStackTrace(System.err);
                return;
            }

            // Read Responses
            try
            {
                if (!receiveLatch.await(expectedTimeout,TimeUnit.MILLISECONDS))
                {
                    debug("Timed out waiting for response messages");
                    getSession().close(StatusCode.NORMAL,"Timeout");
                }
                else
                {
                    getSession().close(StatusCode.NORMAL,"Finished successfully");
                }
            }
            catch (Throwable t)
            {
                Session session = getSession();
                int port = session.getLocalAddress().getPort();
                System.err.printf("[Client %d] Failed to read message from server: %s (client port %d)%n",this.clientNum,t.getMessage(),port);
            }
        }
    }

    private static class CliOptions
    {
        @Option(name = "--clients", usage = "Total number of clients to run")
        public int numClients = 100;

        @Option(name = "--active", usage = "Total number of active clients at any one moment (threadpool size)")
        public int numActive = 10;

        @Option(name = "--messages", usage = "Total number of messages per client to send")
        public int numMessages = 10;

        @Option(name = "--delay", usage = "Delay in milliseconds between each message")
        public long msgDelay = 0;

        @Argument(metaVar = "Server URI")
        public URI destUri = URI.create("ws://localhost:28282/");
    }

    public static void main(String[] args)
    {
        try
        {
            ManyChattyClients load = new ManyChattyClients(args);
            load.start();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private CliOptions opts;

    public ManyChattyClients(String[] args) throws CmdLineException
    {
        this.opts = new CliOptions();
        CmdLineParser parser = new CmdLineParser(opts);
        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            System.err.printf("java -cp jetty-websocket-testing-clients.jar %s [options...] [Server URI]%n",ManyChattyClients.class);
            parser.printUsage(System.err);
            throw e;
        }
    }

    private void start() throws Exception
    {
        ExecutorService executor = Executors.newFixedThreadPool(opts.numActive);

        WebSocketClient client = new WebSocketClient();
        try
        {
            // start client lifecycle
            client.start();
            System.err.printf("Number of Clients: %,d%n",opts.numClients);
            System.err.printf("Number of Active Clients/Threads: %,d%n",opts.numActive);
            System.err.printf("Number of Messages per Client: %,d%n",opts.numMessages);
            System.err.printf("Milliseconds of Delay between each message: %,d ms%n",opts.msgDelay);
            long totalWriteMessages = opts.numMessages * opts.numClients;
            System.err.printf("Expected Number of Messages Sent: %,d%n",totalWriteMessages);
            long totalReadMessages = opts.numMessages * opts.numClients + opts.numClients;
            System.err.printf("Expected Number of Messages Received: %,d%n",totalReadMessages);

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < opts.numClients; i++)
            {
                executor.execute(new ChattyClient(i,client));
            }

            // request orderly shutdown
            executor.shutdown();

            // Wait for termination
            long totalArtificialDelayMs = totalWriteMessages * opts.msgDelay;
            long perMessagesMs = 10;
            long expectedTime = totalWriteMessages * perMessagesMs + totalArtificialDelayMs;
            System.err.printf("Waiting %,d ms for all clients to finish%n",expectedTime);
            if (!executor.awaitTermination(expectedTime,TimeUnit.MILLISECONDS))
            {
                List<Runnable> unrun = executor.shutdownNow();
                System.err.printf("Took too long to execute, timed out waiting for completion. %d tasks not run.%n",unrun.size());
                return;
            }
            else
            {
                long endTime = System.currentTimeMillis();
                System.err.printf("Successfully ran all [%d] clients sending [%d] messages (each)%n",opts.numClients,opts.numMessages);
                long duration = endTime - startTime;
                System.err.printf("Duration: %,dms%n",duration);
                double readsPerSec = totalReadMessages / ((double)duration / (double)1000);
                double writesPerSec = totalWriteMessages / ((double)duration / (double)1000);

                System.err.printf("Message Reads Per Second: %,.2f%n",readsPerSec);
                System.err.printf("Message Writes Per Second: %,.2f%n",writesPerSec);
            }
        }
        finally
        {
            client.stop();
            System.out.printf("%s stopped%n",this.getClass().getSimpleName());
        }
    }
}
