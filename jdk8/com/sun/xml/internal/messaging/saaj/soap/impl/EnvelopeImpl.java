package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

public abstract class EnvelopeImpl extends ElementImpl implements Envelope {
   protected HeaderImpl header;
   protected BodyImpl body;
   String omitXmlDecl;
   String charset;
   String xmlDecl;

   protected EnvelopeImpl(SOAPDocumentImpl ownerDoc, Name name) {
      super(ownerDoc, name);
      this.omitXmlDecl = "yes";
      this.charset = "utf-8";
      this.xmlDecl = null;
   }

   protected EnvelopeImpl(SOAPDocumentImpl ownerDoc, QName name) {
      super(ownerDoc, name);
      this.omitXmlDecl = "yes";
      this.charset = "utf-8";
      this.xmlDecl = null;
   }

   protected EnvelopeImpl(SOAPDocumentImpl ownerDoc, NameImpl name, boolean createHeader, boolean createBody) throws SOAPException {
      this(ownerDoc, (Name)name);
      this.ensureNamespaceIsDeclared(this.getElementQName().getPrefix(), this.getElementQName().getNamespaceURI());
      if (createHeader) {
         this.addHeader();
      }

      if (createBody) {
         this.addBody();
      }

   }

   protected abstract NameImpl getHeaderName(String var1);

   protected abstract NameImpl getBodyName(String var1);

   public SOAPHeader addHeader() throws SOAPException {
      return this.addHeader((String)null);
   }

   public SOAPHeader addHeader(String prefix) throws SOAPException {
      if (prefix == null || prefix.equals("")) {
         prefix = this.getPrefix();
      }

      NameImpl headerName = this.getHeaderName(prefix);
      NameImpl bodyName = this.getBodyName(prefix);
      HeaderImpl header = null;
      SOAPElement firstChild = null;
      Iterator eachChild = this.getChildElementNodes();
      if (eachChild.hasNext()) {
         firstChild = (SOAPElement)eachChild.next();
         if (firstChild.getElementName().equals(headerName)) {
            log.severe("SAAJ0120.impl.header.already.exists");
            throw new SOAPExceptionImpl("Can't add a header when one is already present.");
         }

         if (!firstChild.getElementName().equals(bodyName)) {
            log.severe("SAAJ0121.impl.invalid.first.child.of.envelope");
            throw new SOAPExceptionImpl("First child of Envelope must be either a Header or Body");
         }
      }

      header = (HeaderImpl)this.createElement(headerName);
      this.insertBefore(header, firstChild);
      header.ensureNamespaceIsDeclared(headerName.getPrefix(), headerName.getURI());
      return header;
   }

   protected void lookForHeader() throws SOAPException {
      NameImpl headerName = this.getHeaderName((String)null);
      HeaderImpl hdr = (HeaderImpl)this.findChild(headerName);
      this.header = hdr;
   }

   public SOAPHeader getHeader() throws SOAPException {
      this.lookForHeader();
      return this.header;
   }

   protected void lookForBody() throws SOAPException {
      NameImpl bodyName = this.getBodyName((String)null);
      BodyImpl bodyChildElement = (BodyImpl)this.findChild(bodyName);
      this.body = bodyChildElement;
   }

   public SOAPBody addBody() throws SOAPException {
      return this.addBody((String)null);
   }

   public SOAPBody addBody(String prefix) throws SOAPException {
      this.lookForBody();
      if (prefix == null || prefix.equals("")) {
         prefix = this.getPrefix();
      }

      if (this.body == null) {
         NameImpl bodyName = this.getBodyName(prefix);
         this.body = (BodyImpl)this.createElement(bodyName);
         this.insertBefore(this.body, (Node)null);
         this.body.ensureNamespaceIsDeclared(bodyName.getPrefix(), bodyName.getURI());
         return this.body;
      } else {
         log.severe("SAAJ0122.impl.body.already.exists");
         throw new SOAPExceptionImpl("Can't add a body when one is already present.");
      }
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      if (this.getBodyName((String)null).equals(name)) {
         return this.addBody(name.getPrefix());
      } else {
         return (SOAPElement)(this.getHeaderName((String)null).equals(name) ? this.addHeader(name.getPrefix()) : super.addElement(name));
      }
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      if (this.getBodyName((String)null).equals(NameImpl.convertToName(name))) {
         return this.addBody(name.getPrefix());
      } else {
         return (SOAPElement)(this.getHeaderName((String)null).equals(NameImpl.convertToName(name)) ? this.addHeader(name.getPrefix()) : super.addElement(name));
      }
   }

   public SOAPBody getBody() throws SOAPException {
      this.lookForBody();
      return this.body;
   }

   public Source getContent() {
      return new DOMSource(this.getOwnerDocument());
   }

   public Name createName(String localName, String prefix, String uri) throws SOAPException {
      if ("xmlns".equals(prefix)) {
         log.severe("SAAJ0123.impl.no.reserved.xmlns");
         throw new SOAPExceptionImpl("Cannot declare reserved xmlns prefix");
      } else if (prefix == null && "xmlns".equals(localName)) {
         log.severe("SAAJ0124.impl.qualified.name.cannot.be.xmlns");
         throw new SOAPExceptionImpl("Qualified name cannot be xmlns");
      } else {
         return NameImpl.create(localName, prefix, uri);
      }
   }

   public Name createName(String localName, String prefix) throws SOAPException {
      String namespace = this.getNamespaceURI(prefix);
      if (namespace == null) {
         log.log(Level.SEVERE, (String)"SAAJ0126.impl.cannot.locate.ns", (Object[])(new String[]{prefix}));
         throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + prefix);
      } else {
         return NameImpl.create(localName, prefix, namespace);
      }
   }

   public Name createName(String localName) throws SOAPException {
      return NameImpl.createFromUnqualifiedName(localName);
   }

   public void setOmitXmlDecl(String value) {
      this.omitXmlDecl = value;
   }

   public void setXmlDecl(String value) {
      this.xmlDecl = value;
   }

   private String getOmitXmlDecl() {
      return this.omitXmlDecl;
   }

   public void setCharsetEncoding(String value) {
      this.charset = value;
   }

   public void output(OutputStream out) throws IOException {
      try {
         Transformer transformer = EfficientStreamingTransformer.newTransformer();
         transformer.setOutputProperty("omit-xml-declaration", "yes");
         transformer.setOutputProperty("encoding", this.charset);
         if (this.omitXmlDecl.equals("no") && this.xmlDecl == null) {
            this.xmlDecl = "<?xml version=\"" + this.getOwnerDocument().getXmlVersion() + "\" encoding=\"" + this.charset + "\" ?>";
         }

         StreamResult result = new StreamResult(out);
         if (this.xmlDecl != null) {
            OutputStreamWriter writer = new OutputStreamWriter(out, this.charset);
            writer.write(this.xmlDecl);
            writer.flush();
            result = new StreamResult(writer);
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, (String)"SAAJ0190.impl.set.xml.declaration", (Object[])(new String[]{this.omitXmlDecl}));
            log.log(Level.FINE, (String)"SAAJ0191.impl.set.encoding", (Object[])(new String[]{this.charset}));
         }

         transformer.transform(this.getContent(), result);
      } catch (Exception var5) {
         throw new IOException(var5.getMessage());
      }
   }

   public void output(OutputStream out, boolean isFastInfoset) throws IOException {
      if (!isFastInfoset) {
         this.output(out);
      } else {
         try {
            Source source = this.getContent();
            Transformer transformer = EfficientStreamingTransformer.newTransformer();
            transformer.transform(this.getContent(), FastInfosetReflection.FastInfosetResult_new(out));
         } catch (Exception var5) {
            throw new IOException(var5.getMessage());
         }
      }

   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
      throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
   }
}
