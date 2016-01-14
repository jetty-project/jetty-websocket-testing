package org.eclipse.jetty.test.websocket.servers;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.websocket.Session;

public abstract class AbstractInfoSocket
{
    private static final Logger LOG = Log.getLogger(AbstractInfoSocket.class);
    protected Session session;
    protected Async remote;
    
    @OnOpen
    public void onOpen(Session session)
    {
        this.session = session;
        this.remote = session.getAsyncRemote();
    }

    @OnClose
    public void onClose(CloseReason close)
    {
        this.session = null;
    }
    
    protected void writeMessage(String message)
    {
        if (this.session == null)
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Not connected");
            return;
        }

        if (session.isOpen() == false)
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Not open");
            return;
        }

        // Async write
        remote.sendText(message);
    }

    protected void writeMessage(String format, Object... args)
    {
        writeMessage(String.format(format,args));
    }
}
