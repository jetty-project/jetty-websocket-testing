package org.eclipse.jetty.test.websocket.servers;

import java.util.Locale;

public class CommandLine
{
    public final String name;
    public final String args[];

    public CommandLine(String raw)
    {
        int idx = raw.indexOf(':');
        if (idx > 0)
        {
            this.name = raw.substring(0,idx).toLowerCase(Locale.ENGLISH);
            this.args = raw.substring(idx + 1).split(":");
        }
        else
        {
            this.name = raw.toLowerCase(Locale.ENGLISH);
            this.args = new String[0];
        }
    }

    public CommandLine(String[] arr)
    {
        if (arr == null || arr.length < 1)
        {
            throw new IllegalArgumentException("Cannot have empty CommandLine");
        }

        this.name = arr[0];
        if (arr.length > 1)
        {
            this.args = new String[arr.length - 1];
            System.arraycopy(arr,1,this.args,0,args.length);
        }
        else
        {
            this.args = new String[0];
        }
    }

    @Override
    public String toString()
    {
        StringBuilder line = new StringBuilder();
        line.append(this.name);
        for (String arg : args)
        {
            line.append(':').append(arg);
        }
        return line.toString();
    }
}
