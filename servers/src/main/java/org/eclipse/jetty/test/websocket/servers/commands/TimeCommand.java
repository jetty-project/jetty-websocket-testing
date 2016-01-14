package org.eclipse.jetty.test.websocket.servers.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;

public class TimeCommand extends Command
{
    @Override
    public String getName()
    {
        return "time";
    }

    @Override
    public String getCategory()
    {
        return Categories.INFO;
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        Calendar now = Calendar.getInstance();
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL,SimpleDateFormat.FULL);
        writeTextMessage("Server time: %s",sdf.format(now.getTime()));
    }
}
