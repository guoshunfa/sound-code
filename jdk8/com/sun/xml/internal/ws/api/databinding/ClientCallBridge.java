package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.lang.reflect.Method;

public interface ClientCallBridge {
   Packet createRequestPacket(com.oracle.webservices.internal.api.databinding.JavaCallInfo var1);

   com.oracle.webservices.internal.api.databinding.JavaCallInfo readResponse(Packet var1, com.oracle.webservices.internal.api.databinding.JavaCallInfo var2) throws Throwable;

   Method getMethod();

   JavaMethod getOperationModel();
}
