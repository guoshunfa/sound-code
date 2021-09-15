package com.sun.xml.internal.stream.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceImpl extends AttributeImpl implements Namespace {
   public NamespaceImpl() {
      this.init();
   }

   public NamespaceImpl(String namespaceURI) {
      super("xmlns", "http://www.w3.org/2000/xmlns/", "", namespaceURI, (String)null);
      this.init();
   }

   public NamespaceImpl(String prefix, String namespaceURI) {
      super("xmlns", "http://www.w3.org/2000/xmlns/", prefix, namespaceURI, (String)null);
      this.init();
   }

   public boolean isDefaultNamespaceDeclaration() {
      QName name = this.getName();
      return name != null && name.getLocalPart().equals("");
   }

   void setPrefix(String prefix) {
      if (prefix == null) {
         this.setName(new QName("http://www.w3.org/2000/xmlns/", "", "xmlns"));
      } else {
         this.setName(new QName("http://www.w3.org/2000/xmlns/", prefix, "xmlns"));
      }

   }

   public String getPrefix() {
      QName name = this.getName();
      return name != null ? name.getLocalPart() : null;
   }

   public String getNamespaceURI() {
      return this.getValue();
   }

   void setNamespaceURI(String uri) {
      this.setValue(uri);
   }

   protected void init() {
      this.setEventType(13);
   }

   public int getEventType() {
      return 13;
   }

   public boolean isNamespace() {
      return true;
   }
}
