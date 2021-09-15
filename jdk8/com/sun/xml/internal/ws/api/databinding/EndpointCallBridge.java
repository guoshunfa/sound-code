package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;

public interface EndpointCallBridge {
   com.oracle.webservices.internal.api.databinding.JavaCallInfo deserializeRequest(Packet var1);

   Packet serializeResponse(com.oracle.webservices.internal.api.databinding.JavaCallInfo var1);

   JavaMethod getOperationModel();
}
