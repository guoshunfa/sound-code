package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;

public class NamedEvent extends DummyEvent {
   private QName name;

   public NamedEvent() {
   }

   public NamedEvent(QName qname) {
      this.name = qname;
   }

   public NamedEvent(String prefix, String uri, String localpart) {
      this.name = new QName(uri, localpart, prefix);
   }

   public String getPrefix() {
      return this.name.getPrefix();
   }

   public QName getName() {
      return this.name;
   }

   public void setName(QName qname) {
      this.name = qname;
   }

   public String nameAsString() {
      if ("".equals(this.name.getNamespaceURI())) {
         return this.name.getLocalPart();
      } else {
         return this.name.getPrefix() != null ? "['" + this.name.getNamespaceURI() + "']:" + this.getPrefix() + ":" + this.name.getLocalPart() : "['" + this.name.getNamespaceURI() + "']:" + this.name.getLocalPart();
      }
   }

   public String getNamespace() {
      return this.name.getNamespaceURI();
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write(this.nameAsString());
   }
}
