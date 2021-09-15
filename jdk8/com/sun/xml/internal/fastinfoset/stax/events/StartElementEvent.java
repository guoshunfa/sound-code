package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class StartElementEvent extends EventBase implements StartElement {
   private Map _attributes;
   private List _namespaces;
   private NamespaceContext _context;
   private QName _qname;

   public void reset() {
      if (this._attributes != null) {
         this._attributes.clear();
      }

      if (this._namespaces != null) {
         this._namespaces.clear();
      }

      if (this._context != null) {
         this._context = null;
      }

   }

   public StartElementEvent() {
      this._context = null;
      this.init();
   }

   public StartElementEvent(String prefix, String uri, String localpart) {
      this._context = null;
      this.init();
      if (uri == null) {
         uri = "";
      }

      if (prefix == null) {
         prefix = "";
      }

      this._qname = new QName(uri, localpart, prefix);
      this.setEventType(1);
   }

   public StartElementEvent(QName qname) {
      this._context = null;
      this.init();
      this._qname = qname;
   }

   public StartElementEvent(StartElement startelement) {
      this(startelement.getName());
      this.addAttributes(startelement.getAttributes());
      this.addNamespaces(startelement.getNamespaces());
   }

   protected void init() {
      this.setEventType(1);
      this._attributes = new HashMap();
      this._namespaces = new ArrayList();
   }

   public QName getName() {
      return this._qname;
   }

   public Iterator getAttributes() {
      if (this._attributes != null) {
         Collection coll = this._attributes.values();
         return new ReadIterator(coll.iterator());
      } else {
         return EmptyIterator.getInstance();
      }
   }

   public Iterator getNamespaces() {
      return (Iterator)(this._namespaces != null ? new ReadIterator(this._namespaces.iterator()) : EmptyIterator.getInstance());
   }

   public Attribute getAttributeByName(QName qname) {
      return qname == null ? null : (Attribute)this._attributes.get(qname);
   }

   public NamespaceContext getNamespaceContext() {
      return this._context;
   }

   public void setName(QName qname) {
      this._qname = qname;
   }

   public String getNamespace() {
      return this._qname.getNamespaceURI();
   }

   public String getNamespaceURI(String prefix) {
      if (this.getNamespace() != null) {
         return this.getNamespace();
      } else {
         return this._context != null ? this._context.getNamespaceURI(prefix) : null;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(64);
      sb.append('<').append(this.nameAsString());
      Iterator it;
      Attribute attr;
      if (this._attributes != null) {
         it = this.getAttributes();
         attr = null;

         while(it.hasNext()) {
            attr = (Attribute)it.next();
            sb.append(' ').append(attr.toString());
         }
      }

      if (this._namespaces != null) {
         it = this._namespaces.iterator();
         attr = null;

         while(it.hasNext()) {
            Namespace attr = (Namespace)it.next();
            sb.append(' ').append(attr.toString());
         }
      }

      sb.append('>');
      return sb.toString();
   }

   public String nameAsString() {
      if ("".equals(this._qname.getNamespaceURI())) {
         return this._qname.getLocalPart();
      } else {
         return this._qname.getPrefix() != null ? "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart() : "['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart();
      }
   }

   public void setNamespaceContext(NamespaceContext context) {
      this._context = context;
   }

   public void addAttribute(Attribute attr) {
      this._attributes.put(attr.getName(), attr);
   }

   public void addAttributes(Iterator attrs) {
      if (attrs != null) {
         while(attrs.hasNext()) {
            Attribute attr = (Attribute)attrs.next();
            this._attributes.put(attr.getName(), attr);
         }
      }

   }

   public void addNamespace(Namespace namespace) {
      if (namespace != null) {
         this._namespaces.add(namespace);
      }

   }

   public void addNamespaces(Iterator namespaces) {
      if (namespaces != null) {
         while(namespaces.hasNext()) {
            Namespace namespace = (Namespace)namespaces.next();
            this._namespaces.add(namespace);
         }
      }

   }
}
