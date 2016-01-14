package org.eclipse.jetty.test.websocket.servers.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.websocket.RemoteEndpoint.Async;

import org.eclipse.jetty.test.websocket.servers.Categories;
import org.eclipse.jetty.test.websocket.servers.Command;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.scopes.WebSocketContainerScope;

public class ContainerCommand extends Command
{
    private WebSocketContainerScope wsContainerScope;
    private javax.websocket.Session jsrSession;
    private org.eclipse.jetty.websocket.api.Session apiSession;
    private org.eclipse.jetty.websocket.common.WebSocketSession wsSession;
    private org.eclipse.jetty.util.component.Container jettyContainer;

    public ContainerCommand(javax.websocket.Session session)
    {
        this.jsrSession = session;
        if (session instanceof org.eclipse.jetty.websocket.api.Session)
        {
            this.apiSession = (org.eclipse.jetty.websocket.api.Session)session;
        }
        if (session instanceof org.eclipse.jetty.websocket.common.WebSocketSession)
        {
            this.wsSession = (WebSocketSession)session;
            this.wsContainerScope = wsSession.getContainerScope();
            if (this.wsContainerScope instanceof org.eclipse.jetty.util.component.Container)
            {
                this.jettyContainer = (org.eclipse.jetty.util.component.Container)this.wsContainerScope;
            }
        }
    }

    @Override
    public String getCategory()
    {
        return Categories.INFO;
    }

    @Override
    public String getName()
    {
        return "container";
    }
    
    @Override
    public List<String[]> getConfigs()
    {
        List<String[]> configs = new ArrayList<>();
        configs.add(new String[] { getName(), "jsr-session" });
        configs.add(new String[] { getName(), "api-session" });
        configs.add(new String[] { getName(), "ws-session" });
        configs.add(new String[] { getName(), "ws-container-component" });
        configs.add(new String[] { getName(), "container.beans" });
        configs.add(new String[] { getName(), "buffer-pool-impl" });
        return configs;
    }

    @Override
    public void handle(Async remote, String[] args)
    {
        String query = "";
        if (args.length >= 1)
        {
            query = args[0];
        }
        switch (query)
        {
            case "jsr-session":
                writeTextMessage("%s:jsr-session:%s",getName(),jsrSession);
                return;
            case "api-session":
                writeTextMessage("%s:api-session:%s",getName(),apiSession);
                return;
            case "ws-session":
                writeTextMessage("%s:ws-session:%s",getName(),wsSession);
                return;
            case "ws-container-component":
                writeTextMessage("%s:ws-container-component:%s",getName(),jettyContainer);
                return;
            case "container.beans":
                queryContainerBeans();
                return;
            case "buffer-pool-impl":
                writeTextMessage("%s:buffer-pool-impl:%s",getName(),wsContainerScope.getBufferPool().getClass().getName());
                return;
            default:
                writeTextMessage("%s:unrecognized-query:%s",getName(),query);
                return;
        }
    }
    
    private void queryContainerBeans()
    {
        if (jettyContainer == null)
        {
            writeTextMessage("%s:container.beans=container is <null>",getName());
        }
        
        Collection<Object> beans = jettyContainer.getBeans();
        
        Map<Class<?>,Long> typeCounts = beans.stream()
                .collect(
                    Collectors.groupingBy(Object::getClass,
                       Collectors.counting()));
        
        writeTextMessage("%s:container.beans = %d entries", getName(), beans.size());
        typeCounts.forEach((c,l) -> {
            writeTextMessage("  %s: %d",c,l);
        });
    }
}
