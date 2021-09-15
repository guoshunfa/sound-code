package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.PortInfo;

public class PortInfoImpl implements PortInfo {
   private BindingID bindingId;
   private QName portName;
   private QName serviceName;

   public PortInfoImpl(BindingID bindingId, QName portName, QName serviceName) {
      if (bindingId == null) {
         throw new RuntimeException("bindingId cannot be null");
      } else if (portName == null) {
         throw new RuntimeException("portName cannot be null");
      } else if (serviceName == null) {
         throw new RuntimeException("serviceName cannot be null");
      } else {
         this.bindingId = bindingId;
         this.portName = portName;
         this.serviceName = serviceName;
      }
   }

   public String getBindingID() {
      return this.bindingId.toString();
   }

   public QName getPortName() {
      return this.portName;
   }

   public QName getServiceName() {
      return this.serviceName;
   }

   public boolean equals(Object obj) {
      if (obj instanceof PortInfo) {
         PortInfo info = (PortInfo)obj;
         if (this.bindingId.toString().equals(info.getBindingID()) && this.portName.equals(info.getPortName()) && this.serviceName.equals(info.getServiceName())) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return this.bindingId.hashCode();
   }
}
