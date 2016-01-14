package org.eclipse.jetty.test.websocket.servers.commands;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;
import org.eclipse.jetty.test.websocket.servers.RandomContent;

public class ManyRandomJsonMessagesCommand extends Command
{
    @Override
    public String getCategory()
    {
        return Categories.TEXT_LOAD;
    }

    @Override
    public String getName()
    {
        return "many-json";
    }
    
    @Override
    public List<String[]> getConfigs()
    {
        List<String[]> configs = new ArrayList<>();
        configs.add(new String[] { getName(), "4", "123" });
        configs.add(new String[] { getName(), "40", "456" });
        configs.add(new String[] { getName(), "400", "789" });
        return configs;
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        int count = Integer.parseInt(args[0]);
        
        RandomContent rand = new RandomContent();
        if(args.length >= 2) {
            long seed = Long.parseLong(args[1]);
            rand.setSeed(seed);
        }
        
        for (int i = 0; i < count; i++)
        {
            writeTextMessage(genRandomJson(rand).toString());
        }
    }

    private CharSequence genRandomJson(RandomContent rand)
    {
        // Example: {"msgType":"add","id":193,"url":"/video/193.mp4","width":1920,"height":1080,"color":"#ff0080"}
        StringBuilder json = new StringBuilder();
        json.append('{');
        
        json.append("\"msgType\":\"");
        rand.appendRandomText(json,3,16);
        json.append("\"");
        
        json.append(",\"id\":");
        rand.appendRandomNumber(json,1,1000000);
        
        json.append(",\"url\":\"");
        rand.appendRandomPath(json,2,10,".mp4");
        json.append("\"");
        
        json.append(",\"width\":");
        rand.appendRandomNumber(json,200,11000);
        json.append(",\"height\":");
        rand.appendRandomNumber(json,200,1600);
        
        json.append(",\"color\":\"#");
        rand.appendRandomHexBytes(json,3);
        json.append("\"");
        json.append('}');
        return json;
    }
}
