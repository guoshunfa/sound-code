package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public abstract class DetailImpl extends FaultElementImpl implements Detail {
   public DetailImpl(SOAPDocumentImpl ownerDoc, NameImpl detailName) {
      super(ownerDoc, detailName);
   }

   protected abstract DetailEntry createDetailEntry(Name var1);

   protected abstract DetailEntry createDetailEntry(QName var1);

   public DetailEntry addDetailEntry(Name name) throws SOAPException {
      DetailEntry entry = this.createDetailEntry(name);
      this.addNode(entry);
      return entry;
   }

   public DetailEntry addDetailEntry(QName qname) throws SOAPException {
      DetailEntry entry = this.createDetailEntry(qname);
      this.addNode(entry);
      return entry;
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      return this.addDetailEntry(name);
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      return this.addDetailEntry(name);
   }

   protected SOAPElement convertToSoapElement(Element element) {
      if (element instanceof DetailEntry) {
         return (SOAPElement)element;
      } else {
         DetailEntry detailEntry = this.createDetailEntry(NameImpl.copyElementName(element));
         return replaceElementWithSOAPElement(element, (ElementImpl)detailEntry);
      }
   }

   public Iterator getDetailEntries() {
      return new Iterator() {
         Iterator eachNode = DetailImpl.this.getChildElementNodes();
         SOAPElement next = null;
         SOAPElement last = null;

         public boolean hasNext() {
            if (this.next == null) {
               while(this.eachNode.hasNext()) {
                  this.next = (SOAPElement)this.eachNode.next();
                  if (this.next instanceof DetailEntry) {
                     break;
                  }

                  this.next = null;
               }
            }

            return this.next != null;
         }

         public Object next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.last = this.next;
               this.next = null;
               return this.last;
            }
         }

         public void remove() {
            if (this.last == null) {
               throw new IllegalStateException();
            } else {
               SOAPElement target = this.last;
               DetailImpl.this.removeChild(target);
               this.last = null;
            }
         }
      };
   }

   protected boolean isStandardFaultElement() {
      return true;
   }
}
