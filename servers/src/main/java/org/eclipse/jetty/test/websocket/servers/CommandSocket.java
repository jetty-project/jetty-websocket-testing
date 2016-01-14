package org.eclipse.jetty.test.websocket.servers;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.test.websocket.servers.browser.BrowserTrackingConfigurator;
import org.eclipse.jetty.test.websocket.servers.commands.BrowserInfoCommand;
import org.eclipse.jetty.test.websocket.servers.commands.CategoriesCommand;
import org.eclipse.jetty.test.websocket.servers.commands.CategoryConfigsCommand;
import org.eclipse.jetty.test.websocket.servers.commands.EchoCommand;
import org.eclipse.jetty.test.websocket.servers.commands.ManyRandomTextMessagesCommand;
import org.eclipse.jetty.test.websocket.servers.commands.ThreadedManyRandomTextMessagesCommand;
import org.eclipse.jetty.test.websocket.servers.commands.TimeCommand;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@ServerEndpoint(value = "/command", configurator = BrowserTrackingConfigurator.class, decoders = { CommandDecoder.class })
public class CommandSocket
{
    private static final Logger LOG = Log.getLogger(CommandSocket.class);
    private Commands commands = new Commands();
    private Session session;
    private Async remote;

    public Commands getCommands()
    {
        return this.commands;
    }

    @OnClose
    public void onClose(CloseReason close)
    {
        this.session = null;
        this.remote = null;

        // Clear Commands
        commands.close(close);
    }

    @OnMessage
    public void onCommand(CommandLine commandLine)
    {
        LOG.info("onCommand({})",commandLine);

        Command cmd = this.commands.get(commandLine.name);
        if (cmd == null)
        {
            writeTextMessage("ERROR:Unable to find command named [%s]",commandLine.name);
        }
        else
        {
            cmd.handle(remote,commandLine.args);
        }
    }

    @OnOpen
    public void onOpen(Session session)
    {
        this.session = session;
        this.remote = session.getAsyncRemote();

        // Establish Commands
        commands.add(new CategoriesCommand(commands));
        commands.add(new CategoryConfigsCommand(commands));

        commands.add(new BrowserInfoCommand());
        commands.add(new TimeCommand());
        commands.add(new ManyRandomTextMessagesCommand());
        commands.add(new ThreadedManyRandomTextMessagesCommand());
        commands.add(new EchoCommand());

        // Init Commands
        commands.open(this.session,this.remote);
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
