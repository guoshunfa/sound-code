package com.sun.xml.internal.ws.api.model.wsdl;

public enum WSDLDescriptorKind {
   ELEMENT(0),
   TYPE(1);

   private final int value;

   private WSDLDescriptorKind(int value) {
      this.value = value;
   }
}
