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

package org.eclipse.jetty.test.websocket.servers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

public class RemoteEndpointExamples
{
    private Session session;

    public void exampleAsyncSendBytesFireAndForget()
    {
        RemoteEndpoint remote = session.getRemote();

        // Async Send of a BINARY message to remote endpoint
        ByteBuffer buf = ByteBuffer.wrap(new byte[]
        { 0x11, 0x22, 0x33, 0x44 });
        remote.sendBytesByFuture(buf);
    }

    public void exampleAsyncSendBytesTimeout()
    {
        RemoteEndpoint remote = session.getRemote();

        // Async Send of a BINARY message to remote endpoint
        ByteBuffer buf = ByteBuffer.wrap(new byte[]
        { 0x11, 0x22, 0x33, 0x44 });
        Future<Void> fut = null;
        try
        {
            fut = remote.sendBytesByFuture(buf);
            // wait for completion (timeout)
            fut.get(2,TimeUnit.SECONDS);
        }
        catch (ExecutionException | InterruptedException e)
        {
            // Send failed
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            // timeout
            e.printStackTrace();
            if (fut != null)
            {
                // cancel the message
                fut.cancel(true);
            }
        }
    }

    public void exampleAsyncSendBytesWaitForCompletion()
    {
        RemoteEndpoint remote = session.getRemote();

        // Async Send of a BINARY message to remote endpoint
        ByteBuffer buf = ByteBuffer.wrap(new byte[]
        { 0x11, 0x22, 0x33, 0x44 });
        try
        {
            Future<Void> fut = remote.sendBytesByFuture(buf);
            // wait for completion (forever)
            fut.get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            // Send failed
            e.printStackTrace();
        }
    }

    public void exampleAsyncSendStringFireAndForget()
    {
        RemoteEndpoint remote = session.getRemote();

        // Async Send of a TEXT message to remote endpoint
        remote.sendStringByFuture("Hello World");
    }

    public void exampleAsyncSendStringTimeout()
    {
        RemoteEndpoint remote = session.getRemote();

        // Async Send of a TEXT message to remote endpoint
        Future<Void> fut = null;
        try
        {
            fut = remote.sendStringByFuture("Hello World");
            // wait for completion (timeout)
            fut.get(2,TimeUnit.SECONDS);
        }
        catch (ExecutionException | InterruptedException e)
        {
            // Send failed
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            // timeout
            e.printStackTrace();
            if (fut != null)
            {
                // cancel the message
                fut.cancel(true);
            }
        }
    }

    public void exampleAsyncSendStringWaitForCompletion()
    {
        RemoteEndpoint remote = session.getRemote();

        // Async Send of a TEXT message to remote endpoint
        try
        {
            Future<Void> fut = remote.sendStringByFuture("Hello World");
            // wait for completion (forever)
            fut.get();
        }
        catch (ExecutionException | InterruptedException e)
        {
            // Send failed
            e.printStackTrace();
        }
    }

    public void exampleBlockingSendBytes()
    {
        RemoteEndpoint remote = session.getRemote();

        // Blocking Send of a BINARY message to remote endpoint
        byte array[] = new byte[]
        { 0x11, 0x22, 0x33, 0x44 };
        ByteBuffer buf = ByteBuffer.wrap(array);
        try
        {
            remote.sendBytes(buf);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    public void exampleBlockingSendPartialBytes()
    {
        RemoteEndpoint remote = session.getRemote();

        // Blocking Send of a BINARY message to remote endpoint
        // Part 1
        ByteBuffer buf1 = ByteBuffer.wrap(new byte[]
        { 0x11, 0x22 });
        // Part 2
        ByteBuffer buf2 = ByteBuffer.wrap(new byte[]
        { 0x33, 0x44 });
        try
        {
            remote.sendPartialBytes(buf1,false);
            remote.sendPartialBytes(buf2,true); // last part
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    public void exampleBlockingSendPartialString()
    {
        RemoteEndpoint remote = session.getRemote();

        // Blocking Send of a TEXT message to remote endpoint
        String part1 = "Hello";
        String part2 = " World";
        try
        {
            remote.sendPartialString(part1,false);
            remote.sendPartialString(part2,true); // last part
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    public void exampleBlockingSendPing()
    {
        RemoteEndpoint remote = session.getRemote();

        // Blocking Send of a PING to remote endpoint
        String data = "You There?";
        ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
        try
        {
            remote.sendPing(payload);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    public void exampleBlockingSendPong()
    {
        RemoteEndpoint remote = session.getRemote();

        // Blocking Send of a PONG to remote endpoint
        String data = "Yup, I'm here";
        ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
        try
        {
            remote.sendPong(payload);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    public void exampleBlockingSendString()
    {
        RemoteEndpoint remote = session.getRemote();

        // Blocking Send of a TEXT message to remote endpoint
        try
        {
            remote.sendString("Hello World");
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }
}
