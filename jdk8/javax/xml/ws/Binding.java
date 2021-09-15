package javax.xml.ws;

import java.util.List;
import javax.xml.ws.handler.Handler;

public interface Binding {
   List<Handler> getHandlerChain();

   void setHandlerChain(List<Handler> var1);

   String getBindingID();
}
