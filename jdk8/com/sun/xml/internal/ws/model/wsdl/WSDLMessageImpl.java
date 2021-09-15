package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLMessageImpl extends AbstractExtensibleImpl implements EditableWSDLMessage {
   private final QName name;
   private final ArrayList<EditableWSDLPart> parts;

   public WSDLMessageImpl(XMLStreamReader xsr, QName name) {
      super(xsr);
      this.name = name;
      this.parts = new ArrayList();
   }

   public QName getName() {
      return this.name;
   }

   public void add(EditableWSDLPart part) {
      this.parts.add(part);
   }

   public Iterable<EditableWSDLPart> parts() {
      return this.parts;
   }
}
