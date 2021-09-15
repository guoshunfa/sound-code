package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PSVIDocumentImpl extends DocumentImpl {
   static final long serialVersionUID = -8822220250676434522L;

   public PSVIDocumentImpl() {
   }

   public PSVIDocumentImpl(DocumentType doctype) {
      super(doctype);
   }

   public Node cloneNode(boolean deep) {
      PSVIDocumentImpl newdoc = new PSVIDocumentImpl();
      this.callUserDataHandlers(this, newdoc, (short)1);
      this.cloneNode(newdoc, deep);
      newdoc.mutationEvents = this.mutationEvents;
      return newdoc;
   }

   public DOMImplementation getImplementation() {
      return PSVIDOMImplementationImpl.getDOMImplementation();
   }

   public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
      return new PSVIElementNSImpl(this, namespaceURI, qualifiedName);
   }

   public Element createElementNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
      return new PSVIElementNSImpl(this, namespaceURI, qualifiedName, localpart);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
      return new PSVIAttrNSImpl(this, namespaceURI, qualifiedName);
   }

   public Attr createAttributeNS(String namespaceURI, String qualifiedName, String localName) throws DOMException {
      return new PSVIAttrNSImpl(this, namespaceURI, qualifiedName, localName);
   }

   public DOMConfiguration getDomConfig() {
      super.getDomConfig();
      return this.fConfiguration;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      throw new NotSerializableException(this.getClass().getName());
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      throw new NotSerializableException(this.getClass().getName());
   }
}
