package com.sun.xml.internal.ws.api.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

public interface MessageHandlerContext extends MessageContext {
   Message getMessage();

   void setMessage(Message var1);

   Set<String> getRoles();

   WSBinding getWSBinding();

   @Nullable
   SEIModel getSEIModel();

   @Nullable
   WSDLPort getPort();
}
