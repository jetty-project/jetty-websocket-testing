package org.eclipse.jetty.test.websocket.servers;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class CommandDecoder implements Decoder.Text<CommandLine>
{
    @Override
    public void init(EndpointConfig config)
    {
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public CommandLine decode(String s) throws DecodeException
    {
        return new CommandLine(s);
    }

    @Override
    public boolean willDecode(String s)
    {
        return true;
    }
}
