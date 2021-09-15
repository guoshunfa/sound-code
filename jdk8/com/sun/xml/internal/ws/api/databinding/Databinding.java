package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public interface Databinding extends com.oracle.webservices.internal.api.databinding.Databinding {
   EndpointCallBridge getEndpointBridge(Packet var1) throws DispatchException;

   ClientCallBridge getClientBridge(Method var1);

   void generateWSDL(WSDLGenInfo var1);

   /** @deprecated */
   ContentType encode(Packet var1, OutputStream var2) throws IOException;

   /** @deprecated */
   void decode(InputStream var1, String var2, Packet var3) throws IOException;

   MessageContextFactory getMessageContextFactory();
}
