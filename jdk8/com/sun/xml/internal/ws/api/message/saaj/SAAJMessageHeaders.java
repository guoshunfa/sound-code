package com.sun.xml.internal.ws.api.message.saaj;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

public class SAAJMessageHeaders implements MessageHeaders {
   SOAPMessage sm;
   Map<SOAPHeaderElement, Header> nonSAAJHeaders;
   Map<QName, Integer> notUnderstoodCount;
   SOAPVersion soapVersion;
   private Set<QName> understoodHeaders;

   public SAAJMessageHeaders(SOAPMessage sm, SOAPVersion version) {
      this.sm = sm;
      this.soapVersion = version;
      this.initHeaderUnderstanding();
   }

   private void initHeaderUnderstanding() {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader != null) {
         Iterator allHeaders = soapHeader.examineAllHeaderElements();

         while(allHeaders.hasNext()) {
            SOAPHeaderElement nextHdrElem = (SOAPHeaderElement)allHeaders.next();
            if (nextHdrElem != null && nextHdrElem.getMustUnderstand()) {
               this.notUnderstood(nextHdrElem.getElementQName());
            }
         }

      }
   }

   public void understood(Header header) {
      this.understood(header.getNamespaceURI(), header.getLocalPart());
   }

   public void understood(String nsUri, String localName) {
      this.understood(new QName(nsUri, localName));
   }

   public void understood(QName qName) {
      if (this.notUnderstoodCount == null) {
         this.notUnderstoodCount = new HashMap();
      }

      Integer count = (Integer)this.notUnderstoodCount.get(qName);
      if (count != null && count > 0) {
         count = count - 1;
         if (count <= 0) {
            this.notUnderstoodCount.remove(qName);
         } else {
            this.notUnderstoodCount.put(qName, count);
         }
      }

      if (this.understoodHeaders == null) {
         this.understoodHeaders = new HashSet();
      }

      this.understoodHeaders.add(qName);
   }

   public boolean isUnderstood(Header header) {
      return this.isUnderstood(header.getNamespaceURI(), header.getLocalPart());
   }

   public boolean isUnderstood(String nsUri, String localName) {
      return this.isUnderstood(new QName(nsUri, localName));
   }

   public boolean isUnderstood(QName name) {
      return this.understoodHeaders == null ? false : this.understoodHeaders.contains(name);
   }

   public boolean isUnderstood(int index) {
      return false;
   }

   public Header get(String nsUri, String localName, boolean markAsUnderstood) {
      SOAPHeaderElement h = this.find(nsUri, localName);
      if (h != null) {
         if (markAsUnderstood) {
            this.understood(nsUri, localName);
         }

         return new SAAJHeader(h);
      } else {
         return null;
      }
   }

   public Header get(QName name, boolean markAsUnderstood) {
      return this.get(name.getNamespaceURI(), name.getLocalPart(), markAsUnderstood);
   }

   public Iterator<Header> getHeaders(QName headerName, boolean markAsUnderstood) {
      return this.getHeaders(headerName.getNamespaceURI(), headerName.getLocalPart(), markAsUnderstood);
   }

   public Iterator<Header> getHeaders(String nsUri, String localName, boolean markAsUnderstood) {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader == null) {
         return null;
      } else {
         Iterator allHeaders = soapHeader.examineAllHeaderElements();
         if (!markAsUnderstood) {
            return new SAAJMessageHeaders.HeaderReadIterator(allHeaders, nsUri, localName);
         } else {
            ArrayList headers = new ArrayList();

            while(true) {
               SOAPHeaderElement nextHdr;
               do {
                  do {
                     do {
                        if (!allHeaders.hasNext()) {
                           return headers.iterator();
                        }

                        nextHdr = (SOAPHeaderElement)allHeaders.next();
                     } while(nextHdr == null);
                  } while(!nextHdr.getNamespaceURI().equals(nsUri));
               } while(localName != null && !nextHdr.getLocalName().equals(localName));

               this.understood(nextHdr.getNamespaceURI(), nextHdr.getLocalName());
               headers.add(new SAAJHeader(nextHdr));
            }
         }
      }
   }

   public Iterator<Header> getHeaders(String nsUri, boolean markAsUnderstood) {
      return this.getHeaders(nsUri, (String)null, markAsUnderstood);
   }

   public boolean add(Header header) {
      try {
         header.writeTo(this.sm);
      } catch (SOAPException var3) {
         return false;
      }

      this.notUnderstood(new QName(header.getNamespaceURI(), header.getLocalPart()));
      if (this.isNonSAAJHeader(header)) {
         this.addNonSAAJHeader(this.find(header.getNamespaceURI(), header.getLocalPart()), header);
      }

      return true;
   }

   public Header remove(QName name) {
      return this.remove(name.getNamespaceURI(), name.getLocalPart());
   }

   public Header remove(String nsUri, String localName) {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader == null) {
         return null;
      } else {
         SOAPHeaderElement headerElem = this.find(nsUri, localName);
         if (headerElem == null) {
            return null;
         } else {
            headerElem = (SOAPHeaderElement)soapHeader.removeChild(headerElem);
            this.removeNonSAAJHeader(headerElem);
            QName hdrName = nsUri == null ? new QName(localName) : new QName(nsUri, localName);
            if (this.understoodHeaders != null) {
               this.understoodHeaders.remove(hdrName);
            }

            this.removeNotUnderstood(hdrName);
            return new SAAJHeader(headerElem);
         }
      }
   }

   private void removeNotUnderstood(QName hdrName) {
      if (this.notUnderstoodCount != null) {
         Integer notUnderstood = (Integer)this.notUnderstoodCount.get(hdrName);
         if (notUnderstood != null) {
            int intNotUnderstood = notUnderstood;
            --intNotUnderstood;
            if (intNotUnderstood <= 0) {
               this.notUnderstoodCount.remove(hdrName);
            }
         }

      }
   }

   private SOAPHeaderElement find(QName qName) {
      return this.find(qName.getNamespaceURI(), qName.getLocalPart());
   }

   private SOAPHeaderElement find(String nsUri, String localName) {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader == null) {
         return null;
      } else {
         Iterator allHeaders = soapHeader.examineAllHeaderElements();

         SOAPHeaderElement nextHdrElem;
         do {
            if (!allHeaders.hasNext()) {
               return null;
            }

            nextHdrElem = (SOAPHeaderElement)allHeaders.next();
         } while(!nextHdrElem.getNamespaceURI().equals(nsUri) || !nextHdrElem.getLocalName().equals(localName));

         return nextHdrElem;
      }
   }

   private void notUnderstood(QName qName) {
      if (this.notUnderstoodCount == null) {
         this.notUnderstoodCount = new HashMap();
      }

      Integer count = (Integer)this.notUnderstoodCount.get(qName);
      if (count == null) {
         this.notUnderstoodCount.put(qName, 1);
      } else {
         this.notUnderstoodCount.put(qName, count + 1);
      }

      if (this.understoodHeaders != null) {
         this.understoodHeaders.remove(qName);
      }

   }

   private SOAPHeader ensureSOAPHeader() {
      try {
         SOAPHeader header = this.sm.getSOAPPart().getEnvelope().getHeader();
         return header != null ? header : this.sm.getSOAPPart().getEnvelope().addHeader();
      } catch (Exception var3) {
         return null;
      }
   }

   private boolean isNonSAAJHeader(Header header) {
      return !(header instanceof SAAJHeader);
   }

   private void addNonSAAJHeader(SOAPHeaderElement headerElem, Header header) {
      if (this.nonSAAJHeaders == null) {
         this.nonSAAJHeaders = new HashMap();
      }

      this.nonSAAJHeaders.put(headerElem, header);
   }

   private void removeNonSAAJHeader(SOAPHeaderElement headerElem) {
      if (this.nonSAAJHeaders != null) {
         this.nonSAAJHeaders.remove(headerElem);
      }

   }

   public boolean addOrReplace(Header header) {
      this.remove(header.getNamespaceURI(), header.getLocalPart());
      return this.add(header);
   }

   public void replace(Header old, Header header) {
      if (this.remove(old.getNamespaceURI(), old.getLocalPart()) == null) {
         throw new IllegalArgumentException();
      } else {
         this.add(header);
      }
   }

   public Set<QName> getUnderstoodHeaders() {
      return this.understoodHeaders;
   }

   public Set<QName> getNotUnderstoodHeaders(Set<String> roles, Set<QName> knownHeaders, WSBinding binding) {
      Set<QName> notUnderstoodHeaderNames = new HashSet();
      if (this.notUnderstoodCount == null) {
         return notUnderstoodHeaderNames;
      } else {
         Iterator var5 = this.notUnderstoodCount.keySet().iterator();

         while(var5.hasNext()) {
            QName headerName = (QName)var5.next();
            int count = (Integer)this.notUnderstoodCount.get(headerName);
            if (count > 0) {
               SOAPHeaderElement hdrElem = this.find(headerName);
               if (hdrElem.getMustUnderstand()) {
                  SAAJHeader hdr = new SAAJHeader(hdrElem);
                  boolean understood = false;
                  if (roles != null) {
                     understood = !roles.contains(hdr.getRole(this.soapVersion));
                  }

                  if (!understood) {
                     if (binding != null && binding instanceof SOAPBindingImpl) {
                        understood = ((SOAPBindingImpl)binding).understandsHeader(headerName);
                        if (!understood && knownHeaders != null && knownHeaders.contains(headerName)) {
                           understood = true;
                        }
                     }

                     if (!understood) {
                        notUnderstoodHeaderNames.add(headerName);
                     }
                  }
               }
            }
         }

         return notUnderstoodHeaderNames;
      }
   }

   public Iterator<Header> getHeaders() {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader == null) {
         return null;
      } else {
         Iterator allHeaders = soapHeader.examineAllHeaderElements();
         return new SAAJMessageHeaders.HeaderReadIterator(allHeaders, (String)null, (String)null);
      }
   }

   public boolean hasHeaders() {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader == null) {
         return false;
      } else {
         Iterator allHeaders = soapHeader.examineAllHeaderElements();
         return allHeaders.hasNext();
      }
   }

   public List<Header> asList() {
      SOAPHeader soapHeader = this.ensureSOAPHeader();
      if (soapHeader == null) {
         return Collections.emptyList();
      } else {
         Iterator allHeaders = soapHeader.examineAllHeaderElements();
         ArrayList headers = new ArrayList();

         while(allHeaders.hasNext()) {
            SOAPHeaderElement nextHdr = (SOAPHeaderElement)allHeaders.next();
            headers.add(new SAAJHeader(nextHdr));
         }

         return headers;
      }
   }

   private static class HeaderReadIterator implements Iterator<Header> {
      SOAPHeaderElement current;
      Iterator soapHeaders;
      String myNsUri;
      String myLocalName;

      public HeaderReadIterator(Iterator allHeaders, String nsUri, String localName) {
         this.soapHeaders = allHeaders;
         this.myNsUri = nsUri;
         this.myLocalName = localName;
      }

      public boolean hasNext() {
         if (this.current == null) {
            this.advance();
         }

         return this.current != null;
      }

      public Header next() {
         if (!this.hasNext()) {
            return null;
         } else if (this.current == null) {
            return null;
         } else {
            SAAJHeader ret = new SAAJHeader(this.current);
            this.current = null;
            return ret;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      private void advance() {
         while(true) {
            if (this.soapHeaders.hasNext()) {
               SOAPHeaderElement nextHdr = (SOAPHeaderElement)this.soapHeaders.next();
               if (nextHdr == null || this.myNsUri != null && !nextHdr.getNamespaceURI().equals(this.myNsUri) || this.myLocalName != null && !nextHdr.getLocalName().equals(this.myLocalName)) {
                  continue;
               }

               this.current = nextHdr;
               return;
            }

            this.current = null;
            return;
         }
      }
   }
}
