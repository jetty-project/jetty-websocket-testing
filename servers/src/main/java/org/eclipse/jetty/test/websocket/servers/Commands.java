package org.eclipse.jetty.test.websocket.servers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

public class Commands
{
    private Map<String, Command> commands = new HashMap<>();

    public void add(Command command)
    {
        this.commands.put(command.getName(),command);
    }

    private Stream<Command> getCommandStream()
    {
        return commands.values().stream();
    }

    public void open(Session session, Async remote)
    {
        getCommandStream().forEach(cmd -> cmd.open(session,remote));
    }

    public void close(CloseReason close)
    {
        getCommandStream().forEach(cmd -> cmd.close(close));
        commands.clear();
    }

    public Set<String> getCategories()
    {
        Set<String> categories = new TreeSet<>();
        getCommandStream().forEach(cmd -> categories.add(cmd.getCategory()));
        return categories;
    }

    public List<Command> getCategory(String categoryId)
    {
        return getCommandStream().filter(cmd -> cmd.getCategory().equals(categoryId)).collect(Collectors.toList());
    }

    public Command get(String name)
    {
        return this.commands.get(name);
    }
}
