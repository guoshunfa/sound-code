package javax.xml.ws.handler;

import java.util.List;

public interface HandlerResolver {
   List<Handler> getHandlerChain(PortInfo var1);
}
