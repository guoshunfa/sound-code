package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.IOException;
import java.io.Writer;
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

public class StartElementEvent extends DummyEvent implements StartElement {
   private Map fAttributes;
   private List fNamespaces;
   private NamespaceContext fNamespaceContext;
   private QName fQName;

   public StartElementEvent(String prefix, String uri, String localpart) {
      this(new QName(uri, localpart, prefix));
   }

   public StartElementEvent(QName qname) {
      this.fNamespaceContext = null;
      this.fQName = qname;
      this.init();
   }

   public StartElementEvent(StartElement startelement) {
      this(startelement.getName());
      this.addAttributes(startelement.getAttributes());
      this.addNamespaceAttributes(startelement.getNamespaces());
   }

   protected void init() {
      this.setEventType(1);
      this.fAttributes = new HashMap();
      this.fNamespaces = new ArrayList();
   }

   public QName getName() {
      return this.fQName;
   }

   public void setName(QName qname) {
      this.fQName = qname;
   }

   public Iterator getAttributes() {
      if (this.fAttributes != null) {
         Collection coll = this.fAttributes.values();
         return new ReadOnlyIterator(coll.iterator());
      } else {
         return new ReadOnlyIterator();
      }
   }

   public Iterator getNamespaces() {
      return this.fNamespaces != null ? new ReadOnlyIterator(this.fNamespaces.iterator()) : new ReadOnlyIterator();
   }

   public Attribute getAttributeByName(QName qname) {
      return qname == null ? null : (Attribute)this.fAttributes.get(qname);
   }

   public String getNamespace() {
      return this.fQName.getNamespaceURI();
   }

   public String getNamespaceURI(String prefix) {
      if (this.getNamespace() != null && this.fQName.getPrefix().equals(prefix)) {
         return this.getNamespace();
      } else {
         return this.fNamespaceContext != null ? this.fNamespaceContext.getNamespaceURI(prefix) : null;
      }
   }

   public String toString() {
      StringBuffer startElement = new StringBuffer();
      startElement.append("<");
      startElement.append(this.nameAsString());
      Iterator it;
      Attribute attr;
      if (this.fAttributes != null) {
         it = this.getAttributes();
         attr = null;

         while(it.hasNext()) {
            attr = (Attribute)it.next();
            startElement.append(" ");
            startElement.append(attr.toString());
         }
      }

      if (this.fNamespaces != null) {
         it = this.fNamespaces.iterator();
         attr = null;

         while(it.hasNext()) {
            Namespace attr = (Namespace)it.next();
            startElement.append(" ");
            startElement.append(attr.toString());
         }
      }

      startElement.append(">");
      return startElement.toString();
   }

   public String nameAsString() {
      if ("".equals(this.fQName.getNamespaceURI())) {
         return this.fQName.getLocalPart();
      } else {
         return this.fQName.getPrefix() != null ? "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getPrefix() + ":" + this.fQName.getLocalPart() : "['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getLocalPart();
      }
   }

   public NamespaceContext getNamespaceContext() {
      return this.fNamespaceContext;
   }

   public void setNamespaceContext(NamespaceContext nc) {
      this.fNamespaceContext = nc;
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write(this.toString());
   }

   void addAttribute(Attribute attr) {
      if (attr.isNamespace()) {
         this.fNamespaces.add(attr);
      } else {
         this.fAttributes.put(attr.getName(), attr);
      }

   }

   void addAttributes(Iterator attrs) {
      if (attrs != null) {
         while(attrs.hasNext()) {
            Attribute attr = (Attribute)attrs.next();
            this.fAttributes.put(attr.getName(), attr);
         }

      }
   }

   void addNamespaceAttribute(Namespace attr) {
      if (attr != null) {
         this.fNamespaces.add(attr);
      }
   }

   void addNamespaceAttributes(Iterator attrs) {
      if (attrs != null) {
         while(attrs.hasNext()) {
            Namespace attr = (Namespace)attrs.next();
            this.fNamespaces.add(attr);
         }

      }
   }
}
