package org.eclipse.jetty.test.websocket.servers.commands;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;
import org.eclipse.jetty.test.websocket.servers.Commands;

public class CategoriesCommand extends Command
{
    private final Commands commands;

    public CategoriesCommand(Commands commands)
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
        return "categories";
    }
    
    @Override
    public void handle(Async remote, String[] args)
    {
        this.commands.getCategories().stream()
            .filter(c -> !c.equals(Categories.COMMAND_INTERNAL))
            .forEach(category -> writeTextMessage("category:%s",category));
    }
}
