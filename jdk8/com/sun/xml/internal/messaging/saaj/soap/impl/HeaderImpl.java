package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class HeaderImpl extends ElementImpl implements SOAPHeader {
   protected static final boolean MUST_UNDERSTAND_ONLY = false;

   protected HeaderImpl(SOAPDocumentImpl ownerDoc, NameImpl name) {
      super(ownerDoc, (Name)name);
   }

   protected abstract SOAPHeaderElement createHeaderElement(Name var1) throws SOAPException;

   protected abstract SOAPHeaderElement createHeaderElement(QName var1) throws SOAPException;

   protected abstract NameImpl getNotUnderstoodName();

   protected abstract NameImpl getUpgradeName();

   protected abstract NameImpl getSupportedEnvelopeName();

   public SOAPHeaderElement addHeaderElement(Name name) throws SOAPException {
      SOAPElement newHeaderElement = ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), name.getLocalName(), name.getPrefix(), name.getURI());
      if (newHeaderElement == null || !(newHeaderElement instanceof SOAPHeaderElement)) {
         newHeaderElement = this.createHeaderElement(name);
      }

      String uri = ((SOAPElement)newHeaderElement).getElementQName().getNamespaceURI();
      if (uri != null && !"".equals(uri)) {
         this.addNode((Node)newHeaderElement);
         return (SOAPHeaderElement)newHeaderElement;
      } else {
         log.severe("SAAJ0131.impl.header.elems.ns.qualified");
         throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
      }
   }

   public SOAPHeaderElement addHeaderElement(QName name) throws SOAPException {
      SOAPElement newHeaderElement = ElementFactory.createNamedElement(((SOAPDocument)this.getOwnerDocument()).getDocument(), name.getLocalPart(), name.getPrefix(), name.getNamespaceURI());
      if (newHeaderElement == null || !(newHeaderElement instanceof SOAPHeaderElement)) {
         newHeaderElement = this.createHeaderElement(name);
      }

      String uri = ((SOAPElement)newHeaderElement).getElementQName().getNamespaceURI();
      if (uri != null && !"".equals(uri)) {
         this.addNode((Node)newHeaderElement);
         return (SOAPHeaderElement)newHeaderElement;
      } else {
         log.severe("SAAJ0131.impl.header.elems.ns.qualified");
         throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
      }
   }

   protected SOAPElement addElement(Name name) throws SOAPException {
      return this.addHeaderElement(name);
   }

   protected SOAPElement addElement(QName name) throws SOAPException {
      return this.addHeaderElement(name);
   }

   public Iterator examineHeaderElements(String actor) {
      return this.getHeaderElementsForActor(actor, false, false);
   }

   public Iterator extractHeaderElements(String actor) {
      return this.getHeaderElementsForActor(actor, true, false);
   }

   protected Iterator getHeaderElementsForActor(String actor, boolean detach, boolean mustUnderstand) {
      if (actor != null && !actor.equals("")) {
         return this.getHeaderElements(actor, detach, mustUnderstand);
      } else {
         log.severe("SAAJ0132.impl.invalid.value.for.actor.or.role");
         throw new IllegalArgumentException("Invalid value for actor or role");
      }
   }

   protected Iterator getHeaderElements(String actor, boolean detach, boolean mustUnderstand) {
      List elementList = new ArrayList();
      Iterator eachChild = this.getChildElements();
      Object currentChild = this.iterate(eachChild);

      while(true) {
         while(currentChild != null) {
            if (!(currentChild instanceof SOAPHeaderElement)) {
               currentChild = this.iterate(eachChild);
            } else {
               HeaderElementImpl currentElement = (HeaderElementImpl)currentChild;
               currentChild = this.iterate(eachChild);
               boolean isMustUnderstandMatching = !mustUnderstand || currentElement.getMustUnderstand();
               boolean doAdd = false;
               if (actor == null && isMustUnderstandMatching) {
                  doAdd = true;
               } else {
                  String currentActor = currentElement.getActorOrRole();
                  if (currentActor == null) {
                     currentActor = "";
                  }

                  if (currentActor.equalsIgnoreCase(actor) && isMustUnderstandMatching) {
                     doAdd = true;
                  }
               }

               if (doAdd) {
                  elementList.add(currentElement);
                  if (detach) {
                     currentElement.detachNode();
                  }
               }
            }
         }

         return elementList.listIterator();
      }
   }

   private Object iterate(Iterator each) {
      return each.hasNext() ? each.next() : null;
   }

   public void setParentElement(SOAPElement element) throws SOAPException {
      if (!(element instanceof SOAPEnvelope)) {
         log.severe("SAAJ0133.impl.header.parent.mustbe.envelope");
         throw new SOAPException("Parent of SOAPHeader has to be a SOAPEnvelope");
      } else {
         super.setParentElement(element);
      }
   }

   public SOAPElement addChildElement(String localName) throws SOAPException {
      SOAPElement element = super.addChildElement(localName);
      String uri = element.getElementName().getURI();
      if (uri != null && !"".equals(uri)) {
         return element;
      } else {
         log.severe("SAAJ0134.impl.header.elems.ns.qualified");
         throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
      }
   }

   public Iterator examineAllHeaderElements() {
      return this.getHeaderElements((String)null, false, false);
   }

   public Iterator examineMustUnderstandHeaderElements(String actor) {
      return this.getHeaderElements(actor, false, true);
   }

   public Iterator extractAllHeaderElements() {
      return this.getHeaderElements((String)null, true, false);
   }

   public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSoapUris) throws SOAPException {
      if (supportedSoapUris == null) {
         log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
         throw new SOAPException("Argument cannot be null; iterator of supportedURIs cannot be null");
      } else if (!supportedSoapUris.hasNext()) {
         log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
         throw new SOAPException("List of supported URIs cannot be empty");
      } else {
         Name upgradeName = this.getUpgradeName();
         SOAPHeaderElement upgradeHeaderElement = (SOAPHeaderElement)this.addChildElement(upgradeName);
         Name supportedEnvelopeName = this.getSupportedEnvelopeName();

         for(int i = 0; supportedSoapUris.hasNext(); ++i) {
            SOAPElement subElement = upgradeHeaderElement.addChildElement(supportedEnvelopeName);
            String ns = "ns" + Integer.toString(i);
            subElement.addAttribute((Name)NameImpl.createFromUnqualifiedName("qname"), ns + ":Envelope");
            subElement.addNamespaceDeclaration(ns, (String)supportedSoapUris.next());
         }

         return upgradeHeaderElement;
      }
   }

   public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri) throws SOAPException {
      return this.addUpgradeHeaderElement(new String[]{supportedSoapUri});
   }

   public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) throws SOAPException {
      if (supportedSoapUris == null) {
         log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
         throw new SOAPException("Argument cannot be null; array of supportedURIs cannot be null");
      } else if (supportedSoapUris.length == 0) {
         log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
         throw new SOAPException("List of supported URIs cannot be empty");
      } else {
         Name upgradeName = this.getUpgradeName();
         SOAPHeaderElement upgradeHeaderElement = (SOAPHeaderElement)this.addChildElement(upgradeName);
         Name supportedEnvelopeName = this.getSupportedEnvelopeName();

         for(int i = 0; i < supportedSoapUris.length; ++i) {
            SOAPElement subElement = upgradeHeaderElement.addChildElement(supportedEnvelopeName);
            String ns = "ns" + Integer.toString(i);
            subElement.addAttribute((Name)NameImpl.createFromUnqualifiedName("qname"), ns + ":Envelope");
            subElement.addNamespaceDeclaration(ns, supportedSoapUris[i]);
         }

         return upgradeHeaderElement;
      }
   }

   protected SOAPElement convertToSoapElement(Element element) {
      if (element instanceof SOAPHeaderElement) {
         return (SOAPElement)element;
      } else {
         SOAPHeaderElement headerElement;
         try {
            headerElement = this.createHeaderElement(NameImpl.copyElementName(element));
         } catch (SOAPException var4) {
            throw new ClassCastException("Could not convert Element to SOAPHeaderElement: " + var4.getMessage());
         }

         return replaceElementWithSOAPElement(element, (ElementImpl)headerElement);
      }
   }

   public SOAPElement setElementQName(QName newName) throws SOAPException {
      log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[]{this.elementQName.getLocalPart(), newName.getLocalPart()});
      throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
   }
}
