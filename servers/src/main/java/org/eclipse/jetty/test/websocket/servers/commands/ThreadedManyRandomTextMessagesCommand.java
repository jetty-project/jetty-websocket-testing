package org.eclipse.jetty.test.websocket.servers.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;

public class ThreadedManyRandomTextMessagesCommand extends Command
{
    private static class WriteMany implements Callable<Void>
    {
        private Async remote;
        private int size;
        private int count;

        public WriteMany(Async remote, int size, int count)
        {
            this.remote = remote;
            this.size = size;
            this.count = count;
        }

        @Override
        public Void call() throws Exception
        {
            char letters[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-|{}[]():".toCharArray();
            int lettersLen = letters.length;
            char randomText[] = new char[size];
            Random rand = new Random(42);
            String msg;

            for (int n = 0; n < count; n++)
            {
                // create random text
                for (int i = 0; i < size; i++)
                {
                    randomText[i] = letters[rand.nextInt(lettersLen)];
                }
                msg = String.format("Many-Text Threads [%s]",String.valueOf(randomText));
                remote.sendText(msg);
            }
            return null;
        }
    }

    private final int THREAD_POOL_SIZE = 20;
    private ThreadPoolExecutor executor;

    public ThreadedManyRandomTextMessagesCommand()
    {
        this.executor = new ThreadPoolExecutor(THREAD_POOL_SIZE,THREAD_POOL_SIZE,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public String getCategory()
    {
        return Categories.TEXT_LOAD;
    }

    @Override
    public String getName()
    {
        return "many-text-threaded";
    }
    
    @Override
    public List<String[]> getConfigs()
    {
        List<String[]> configs = new ArrayList<>();
        configs.add(new String[] { getName(), "20", "25", "60" });
        configs.add(new String[] { getName(), "100", "200", "500" });
        return configs;
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        int threadCount = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        int count = Integer.parseInt(args[2]);

        // Setup threads
        for (int n = 0; n < threadCount; n++)
        {
            executor.submit(new WriteMany(remote,size,count));
        }
    }
}
