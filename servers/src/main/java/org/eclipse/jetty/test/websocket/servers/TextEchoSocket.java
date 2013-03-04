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

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class TextEchoSocket extends WebSocketAdapter
{
    private static AtomicInteger socketNum = new AtomicInteger(0);

    private int socketId;
    private RemoteEndpoint remote;

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);

        this.socketId = socketNum.incrementAndGet();
        this.remote = getRemote();
        respond("Thanks for connecting");
    }

    @Override
    public void onWebSocketText(String message)
    {
        // echo string back
        respond(message);
    }

    private void respond(String string)
    {
        if (!isConnected())
        {
            System.err.printf("Not connected!%n");
            return;
        }

        remote.sendStringByFuture(String.format("[%d] %s",socketId,string));
    }
}
