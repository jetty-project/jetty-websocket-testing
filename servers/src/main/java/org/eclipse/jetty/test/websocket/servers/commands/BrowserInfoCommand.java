package org.eclipse.jetty.test.websocket.servers.commands;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;
import org.eclipse.jetty.util.StringUtil;

public class BrowserInfoCommand extends Command
{
    private String userAgent;
    private String requestedExtensions;

    @Override
    public String getName()
    {
        return "browser-info";
    }
    
    @Override
    public String getCategory()
    {
        return Categories.INFO;
    }
    
    @Override
    public void open(Session session, Async remote)
    {
        super.open(session,remote);
        this.userAgent = (String)session.getUserProperties().get("userAgent");
        this.requestedExtensions = (String)session.getUserProperties().get("requestedExtensions");
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        writeTextMessage("Using javax.websocket");
        if (StringUtil.isBlank(userAgent))
        {
            writeTextMessage("Client has no User-Agent");
        }
        else
        {
            writeTextMessage("Client User-Agent: " + this.userAgent);
        }

        if (StringUtil.isBlank(requestedExtensions))
        {
            writeTextMessage("Client requested no Sec-WebSocket-Extensions");
        }
        else
        {
            writeTextMessage("Client Sec-WebSocket-Extensions: " + this.requestedExtensions);
        }
    }
}
