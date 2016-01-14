package org.eclipse.jetty.test.websocket.servers.commands;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;

public class EchoCommand extends Command
{
    @Override
    public String getCategory()
    {
        return Categories.ECHO;
    }

    @Override
    public String getName()
    {
        return "echo";
    }

    @Override
    public List<String[]> getConfigs()
    {
        List<String[]> configs = new ArrayList<>();
        configs.add(new String[] { getName(), "Hello World" });
        configs.add(new String[] { getName(), "Hello" });
        configs.add(new String[] { getName(), "World" });
        return configs;
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        String message = args[0];
        writeTextMessage("echo:%s",message);
    }
}
