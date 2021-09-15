package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.namespace.QName;

public final class WSDLDirectProperties extends WSDLProperties {
   private final QName serviceName;
   private final QName portName;

   public WSDLDirectProperties(QName serviceName, QName portName) {
      this(serviceName, portName, (SEIModel)null);
   }

   public WSDLDirectProperties(QName serviceName, QName portName, SEIModel seiModel) {
      super(seiModel);
      this.serviceName = serviceName;
      this.portName = portName;
   }

   public QName getWSDLService() {
      return this.serviceName;
   }

   public QName getWSDLPort() {
      return this.portName;
   }

   public QName getWSDLPortType() {
      return null;
   }
}
