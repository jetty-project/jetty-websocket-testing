package org.eclipse.jetty.test.websocket.servers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public abstract class Command implements Comparator<Command>
{
    private static final Logger LOG = Log.getLogger(Command.class);

    protected javax.websocket.Session session;
    protected javax.websocket.RemoteEndpoint.Async remote;
    protected CloseReason close;

    public void close(CloseReason close)
    {
        this.close = close;
        this.session = null;
        this.remote = null;
    }

    @Override
    public int compare(Command o1, Command o2)
    {
        return o1.getName().compareTo(o2.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Command other = (Command)obj;
        if (getName() == null)
        {
            if (other.getName() != null)
            {
                return false;
            }
        }
        else if (!getName().equals(other.getName()))
        {
            return false;
        }
        return true;
    }

    public abstract String getCategory();

    public abstract String getName();

    public abstract void handle(RemoteEndpoint.Async remote, String args[]);
    
    public List<String[]> getConfigs()
    {
        List<String[]> configs = new ArrayList<>();
        configs.add(new String[] { getName() });
        return configs;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    public void open(javax.websocket.Session session, javax.websocket.RemoteEndpoint.Async remote)
    {
        this.session = session;
        this.remote = remote;
    }

    protected void writeTextMessage(String message)
    {
        if (this.session == null)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Not connected");
            }
            return;
        }

        if (session.isOpen() == false)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("Not open");
            }
            return;
        }

        if (this.remote == null)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug("remote closed");
            }
            return;
        }

        // Async write
        remote.sendText(message);
    }

    protected void writeTextMessage(String format, Object... args)
    {
        writeTextMessage(String.format(format,args));
    }
}
