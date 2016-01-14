package org.eclipse.jetty.test.websocket.servers.commands;

import java.util.List;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;
import org.eclipse.jetty.test.websocket.servers.CommandLine;
import org.eclipse.jetty.test.websocket.servers.Commands;

public class CategoryConfigsCommand extends Command
{
    private final Commands commands;

    public CategoryConfigsCommand(Commands commands)
    {
        this.commands = commands;
    }

    @Override
    public String getCategory()
    {
        return Categories.COMMAND_INTERNAL;
    }

    @Override
    public String getName()
    {
        return "category-config";
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        String categoryId = args[0];
        List<Command> cmds = this.commands.getCategory(categoryId);
        cmds.stream().forEach(cmd -> writeCommandConfigs(categoryId,cmd));
    }

    private void writeCommandConfigs(String categoryId, Command cmd)
    {
        cmd.getConfigs().stream().forEach(config -> writeTextMessage("%s:%s:%s",getName(),categoryId,new CommandLine(config)));
    }
}
