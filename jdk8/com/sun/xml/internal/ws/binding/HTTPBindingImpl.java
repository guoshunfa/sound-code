package com.sun.xml.internal.ws.binding;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.http.HTTPBinding;

public class HTTPBindingImpl extends BindingImpl implements HTTPBinding {
   HTTPBindingImpl() {
      this(EMPTY_FEATURES);
   }

   HTTPBindingImpl(WebServiceFeature... features) {
      super(BindingID.XML_HTTP, features);
   }

   public void setHandlerChain(List<Handler> chain) {
      Iterator var2 = chain.iterator();

      Handler handler;
      do {
         if (!var2.hasNext()) {
            this.setHandlerConfig(new HandlerConfiguration(Collections.emptySet(), chain));
            return;
         }

         handler = (Handler)var2.next();
      } while(handler instanceof LogicalHandler);

      throw new WebServiceException(ClientMessages.NON_LOGICAL_HANDLER_SET(handler.getClass()));
   }
}
