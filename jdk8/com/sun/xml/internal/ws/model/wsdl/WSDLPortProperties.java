package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.namespace.QName;

public final class WSDLPortProperties extends WSDLProperties {
   @NotNull
   private final WSDLPort port;

   public WSDLPortProperties(@NotNull WSDLPort port) {
      this(port, (SEIModel)null);
   }

   public WSDLPortProperties(@NotNull WSDLPort port, @Nullable SEIModel seiModel) {
      super(seiModel);
      this.port = port;
   }

   public QName getWSDLService() {
      return this.port.getOwner().getName();
   }

   public QName getWSDLPort() {
      return this.port.getName();
   }

   public QName getWSDLPortType() {
      return this.port.getBinding().getPortTypeName();
   }
}
