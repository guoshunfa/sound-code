package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.policy.PolicyMap;
import javax.xml.ws.handler.PortInfo;

public interface WSPortInfo extends PortInfo {
   @NotNull
   WSService getOwner();

   @NotNull
   BindingID getBindingId();

   @NotNull
   EndpointAddress getEndpointAddress();

   @Nullable
   WSDLPort getPort();

   /** @deprecated */
   PolicyMap getPolicyMap();
}
