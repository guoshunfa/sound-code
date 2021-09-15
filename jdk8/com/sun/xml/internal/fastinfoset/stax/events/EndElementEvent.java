package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

public class EndElementEvent extends EventBase implements EndElement {
   List _namespaces = null;
   QName _qname;

   public void reset() {
      if (this._namespaces != null) {
         this._namespaces.clear();
      }

   }

   public EndElementEvent() {
      this.setEventType(2);
   }

   public EndElementEvent(String prefix, String namespaceURI, String localpart) {
      this._qname = this.getQName(namespaceURI, localpart, prefix);
      this.setEventType(2);
   }

   public EndElementEvent(QName qname) {
      this._qname = qname;
      this.setEventType(2);
   }

   public QName getName() {
      return this._qname;
   }

   public void setName(QName qname) {
      this._qname = qname;
   }

   public Iterator getNamespaces() {
      return (Iterator)(this._namespaces != null ? this._namespaces.iterator() : EmptyIterator.getInstance());
   }

   public void addNamespace(Namespace namespace) {
      if (this._namespaces == null) {
         this._namespaces = new ArrayList();
      }

      this._namespaces.add(namespace);
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("</").append(this.nameAsString());
      Iterator namespaces = this.getNamespaces();

      while(namespaces.hasNext()) {
         sb.append(" ").append(namespaces.next().toString());
      }

      sb.append(">");
      return sb.toString();
   }

   private String nameAsString() {
      if ("".equals(this._qname.getNamespaceURI())) {
         return this._qname.getLocalPart();
      } else {
         return this._qname.getPrefix() != null ? "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart() : "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart();
      }
   }

   private QName getQName(String uri, String localPart, String prefix) {
      QName qn = null;
      if (prefix != null && uri != null) {
         qn = new QName(uri, localPart, prefix);
      } else if (prefix == null && uri != null) {
         qn = new QName(uri, localPart);
      } else if (prefix == null && uri == null) {
         qn = new QName(localPart);
      }

      return qn;
   }
}
