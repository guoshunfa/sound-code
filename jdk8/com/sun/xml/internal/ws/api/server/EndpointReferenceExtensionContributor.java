package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;

public abstract class EndpointReferenceExtensionContributor {
   public abstract WSEndpointReference.EPRExtension getEPRExtension(WSEndpoint var1, @Nullable WSEndpointReference.EPRExtension var2);

   public abstract QName getQName();
}
