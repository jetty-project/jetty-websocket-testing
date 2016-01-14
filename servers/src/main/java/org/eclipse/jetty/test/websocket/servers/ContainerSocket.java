package org.eclipse.jetty.test.websocket.servers;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.common.scopes.WebSocketContainerScope;

@ServerEndpoint("/container")
public class ContainerSocket
{
    private WebSocketContainerScope wsContainerScope;
    private Session jsrSession;
    private org.eclipse.jetty.websocket.api.Session apiSession;
    private org.eclipse.jetty.websocket.common.WebSocketSession wsSession;
    private org.eclipse.jetty.util.component.Container jettyContainer;

    @OnOpen
    public void onOpen(Session session)
    {
        this.jsrSession = session;
        if(session instanceof org.eclipse.jetty.websocket.api.Session)
        {
            this.apiSession = (org.eclipse.jetty.websocket.api.Session)session;
        }
        if(session instanceof org.eclipse.jetty.websocket.common.WebSocketSession)
        {
            this.wsSession = (WebSocketSession)session;
            this.wsContainerScope = wsSession.getContainerScope();
            if(this.wsContainerScope instanceof org.eclipse.jetty.util.component.Container)
            {
                this.jettyContainer = (org.eclipse.jetty.util.component.Container) this.wsContainerScope;
            }
        }
    }

    @OnMessage
    public String onQuery(String query)
    {
        switch (query)
        {
            case "jsr-session":
                return String.format("jsr-session:%s",jsrSession);
            case "api-session":
                return String.format("api-session:%s",apiSession);
            case "ws-session":
                return String.format("ws-session:%s",wsSession);
            case "ws-container-component":
                return String.format("ws-container-component:%s",jettyContainer);
            case "container.beans":
                return getContainerBeans();
            case "buffer-pool-impl":
                return String.format("buffer-pool-impl:%s",wsContainerScope.getBufferPool().getClass().getName());
            default:
                return String.format("unrecognized-query:%s",query);
        }
    }

    private String getContainerBeans()
    {
        if (jettyContainer == null)
        {
            return "container.beans=container is <null>";
        }
        
        StringBuilder resp = new StringBuilder();
        resp.append("container.beans:");

        Collection<Object> beans = jettyContainer.getBeans();
        resp.append("count=").append(beans.size());
        
        Map<Class<?>,Long> typeCounts = beans.stream()
                .collect(
                    Collectors.groupingBy(Object::getClass,
                       Collectors.counting())); 
                
        typeCounts.forEach((c,l) -> resp.append(',').append(c.getName()).append('=').append(l));

        return resp.toString();
    }
}
