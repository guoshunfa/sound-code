package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLServiceImpl extends AbstractExtensibleImpl implements EditableWSDLService {
   private final QName name;
   private final Map<QName, EditableWSDLPort> ports;
   private final EditableWSDLModel parent;

   public WSDLServiceImpl(XMLStreamReader xsr, EditableWSDLModel parent, QName name) {
      super(xsr);
      this.parent = parent;
      this.name = name;
      this.ports = new LinkedHashMap();
   }

   @NotNull
   public EditableWSDLModel getParent() {
      return this.parent;
   }

   public QName getName() {
      return this.name;
   }

   public EditableWSDLPort get(QName portName) {
      return (EditableWSDLPort)this.ports.get(portName);
   }

   public EditableWSDLPort getFirstPort() {
      return this.ports.isEmpty() ? null : (EditableWSDLPort)this.ports.values().iterator().next();
   }

   public Iterable<EditableWSDLPort> getPorts() {
      return this.ports.values();
   }

   @Nullable
   public EditableWSDLPort getMatchingPort(QName portTypeName) {
      Iterator var2 = this.getPorts().iterator();

      EditableWSDLPort port;
      QName ptName;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         port = (EditableWSDLPort)var2.next();
         ptName = port.getBinding().getPortTypeName();

         assert ptName != null;
      } while(!ptName.equals(portTypeName));

      return port;
   }

   public void put(QName portName, EditableWSDLPort port) {
      if (portName != null && port != null) {
         this.ports.put(portName, port);
      } else {
         throw new NullPointerException();
      }
   }

   public void freeze(EditableWSDLModel root) {
      Iterator var2 = this.ports.values().iterator();

      while(var2.hasNext()) {
         EditableWSDLPort port = (EditableWSDLPort)var2.next();
         port.freeze(root);
      }

   }
}
