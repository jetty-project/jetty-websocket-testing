package org.eclipse.jetty.test.websocket.servers.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;

public class ManyRandomTextMessagesCommand extends Command
{
    @Override
    public String getCategory()
    {
        return Categories.TEXT_LOAD;
    }

    @Override
    public String getName()
    {
        return "many-text";
    }
    
    @Override
    public List<String[]> getConfigs()
    {
        List<String[]> configs = new ArrayList<>();
        configs.add(new String[] { getName(), "15", "300" });
        configs.add(new String[] { getName(), "300", "1000" });
        return configs;
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        int size = Integer.parseInt(args[0]);
        int count = Integer.parseInt(args[1]);

        writeManyAsync(size,count);
    }

    private void writeManyAsync(int size, int count)
    {
        char letters[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-|{}[]():".toCharArray();
        int lettersLen = letters.length;
        char randomText[] = new char[size];
        Random rand = new Random(42);

        for (int n = 0; n < count; n++)
        {
            // create random text
            for (int i = 0; i < size; i++)
            {
                randomText[i] = letters[rand.nextInt(lettersLen)];
            }
            writeTextMessage("Many Text [%s]",String.valueOf(randomText));
        }
    }
}
