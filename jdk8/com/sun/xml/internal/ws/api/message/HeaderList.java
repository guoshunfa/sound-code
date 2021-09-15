package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public class HeaderList extends ArrayList<Header> implements MessageHeaders {
   private static final long serialVersionUID = -6358045781349627237L;
   private int understoodBits;
   private BitSet moreUnderstoodBits = null;
   private SOAPVersion soapVersion;

   /** @deprecated */
   @Deprecated
   public HeaderList() {
   }

   public HeaderList(SOAPVersion soapVersion) {
      this.soapVersion = soapVersion;
   }

   public HeaderList(HeaderList that) {
      super(that);
      this.understoodBits = that.understoodBits;
      if (that.moreUnderstoodBits != null) {
         this.moreUnderstoodBits = (BitSet)that.moreUnderstoodBits.clone();
      }

   }

   public HeaderList(MessageHeaders that) {
      super(that.asList());
      if (that instanceof HeaderList) {
         HeaderList hThat = (HeaderList)that;
         this.understoodBits = hThat.understoodBits;
         if (hThat.moreUnderstoodBits != null) {
            this.moreUnderstoodBits = (BitSet)hThat.moreUnderstoodBits.clone();
         }
      } else {
         Set<QName> understood = that.getUnderstoodHeaders();
         if (understood != null) {
            Iterator var3 = understood.iterator();

            while(var3.hasNext()) {
               QName qname = (QName)var3.next();
               this.understood(qname);
            }
         }
      }

   }

   public int size() {
      return super.size();
   }

   public boolean hasHeaders() {
      return !this.isEmpty();
   }

   /** @deprecated */
   @Deprecated
   public void addAll(Header... headers) {
      this.addAll(Arrays.asList(headers));
   }

   public Header get(int index) {
      return (Header)super.get(index);
   }

   public void understood(int index) {
      if (index >= this.size()) {
         throw new ArrayIndexOutOfBoundsException(index);
      } else {
         if (index < 32) {
            this.understoodBits |= 1 << index;
         } else {
            if (this.moreUnderstoodBits == null) {
               this.moreUnderstoodBits = new BitSet();
            }

            this.moreUnderstoodBits.set(index - 32);
         }

      }
   }

   public boolean isUnderstood(int index) {
      if (index >= this.size()) {
         throw new ArrayIndexOutOfBoundsException(index);
      } else if (index < 32) {
         return this.understoodBits == (this.understoodBits | 1 << index);
      } else {
         return this.moreUnderstoodBits == null ? false : this.moreUnderstoodBits.get(index - 32);
      }
   }

   /** @deprecated */
   public void understood(@NotNull Header header) {
      int sz = this.size();

      for(int i = 0; i < sz; ++i) {
         if (this.get(i) == header) {
            this.understood(i);
            return;
         }
      }

      throw new IllegalArgumentException();
   }

   @Nullable
   public Header get(@NotNull String nsUri, @NotNull String localName, boolean markAsUnderstood) {
      int len = this.size();

      for(int i = 0; i < len; ++i) {
         Header h = this.get(i);
         if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
            if (markAsUnderstood) {
               this.understood(i);
            }

            return h;
         }
      }

      return null;
   }

   /** @deprecated */
   public Header get(String nsUri, String localName) {
      return this.get(nsUri, localName, true);
   }

   @Nullable
   public Header get(@NotNull QName name, boolean markAsUnderstood) {
      return this.get(name.getNamespaceURI(), name.getLocalPart(), markAsUnderstood);
   }

   /** @deprecated */
   @Nullable
   public Header get(@NotNull QName name) {
      return this.get(name, true);
   }

   /** @deprecated */
   public Iterator<Header> getHeaders(String nsUri, String localName) {
      return this.getHeaders(nsUri, localName, true);
   }

   @NotNull
   public Iterator<Header> getHeaders(@NotNull final String nsUri, @NotNull final String localName, final boolean markAsUnderstood) {
      return new Iterator<Header>() {
         int idx = 0;
         Header next;

         public boolean hasNext() {
            if (this.next == null) {
               this.fetch();
            }

            return this.next != null;
         }

         public Header next() {
            if (this.next == null) {
               this.fetch();
               if (this.next == null) {
                  throw new NoSuchElementException();
               }
            }

            if (markAsUnderstood) {
               assert HeaderList.this.get(this.idx - 1) == this.next;

               HeaderList.this.understood(this.idx - 1);
            }

            Header r = this.next;
            this.next = null;
            return r;
         }

         private void fetch() {
            while(true) {
               if (this.idx < HeaderList.this.size()) {
                  Header h = HeaderList.this.get(this.idx++);
                  if (!h.getLocalPart().equals(localName) || !h.getNamespaceURI().equals(nsUri)) {
                     continue;
                  }

                  this.next = h;
               }

               return;
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @NotNull
   public Iterator<Header> getHeaders(@NotNull QName headerName, boolean markAsUnderstood) {
      return this.getHeaders(headerName.getNamespaceURI(), headerName.getLocalPart(), markAsUnderstood);
   }

   /** @deprecated */
   @NotNull
   public Iterator<Header> getHeaders(@NotNull String nsUri) {
      return this.getHeaders(nsUri, true);
   }

   @NotNull
   public Iterator<Header> getHeaders(@NotNull final String nsUri, final boolean markAsUnderstood) {
      return new Iterator<Header>() {
         int idx = 0;
         Header next;

         public boolean hasNext() {
            if (this.next == null) {
               this.fetch();
            }

            return this.next != null;
         }

         public Header next() {
            if (this.next == null) {
               this.fetch();
               if (this.next == null) {
                  throw new NoSuchElementException();
               }
            }

            if (markAsUnderstood) {
               assert HeaderList.this.get(this.idx - 1) == this.next;

               HeaderList.this.understood(this.idx - 1);
            }

            Header r = this.next;
            this.next = null;
            return r;
         }

         private void fetch() {
            while(true) {
               if (this.idx < HeaderList.this.size()) {
                  Header h = HeaderList.this.get(this.idx++);
                  if (!h.getNamespaceURI().equals(nsUri)) {
                     continue;
                  }

                  this.next = h;
               }

               return;
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public String getTo(AddressingVersion av, SOAPVersion sv) {
      return AddressingUtils.getTo(this, av, sv);
   }

   public String getAction(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      return AddressingUtils.getAction(this, av, sv);
   }

   public WSEndpointReference getReplyTo(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      return AddressingUtils.getReplyTo(this, av, sv);
   }

   public WSEndpointReference getFaultTo(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      return AddressingUtils.getFaultTo(this, av, sv);
   }

   public String getMessageID(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      return AddressingUtils.getMessageID(this, av, sv);
   }

   public String getRelatesTo(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
      return AddressingUtils.getRelatesTo(this, av, sv);
   }

   public void fillRequestAddressingHeaders(Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action, boolean mustUnderstand) {
      AddressingUtils.fillRequestAddressingHeaders(this, packet, av, sv, oneway, action, mustUnderstand);
   }

   public void fillRequestAddressingHeaders(Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action) {
      AddressingUtils.fillRequestAddressingHeaders(this, packet, av, sv, oneway, action);
   }

   public void fillRequestAddressingHeaders(WSDLPort wsdlPort, @NotNull WSBinding binding, Packet packet) {
      AddressingUtils.fillRequestAddressingHeaders(this, wsdlPort, binding, packet);
   }

   public boolean add(Header header) {
      return super.add(header);
   }

   @Nullable
   public Header remove(@NotNull String nsUri, @NotNull String localName) {
      int len = this.size();

      for(int i = 0; i < len; ++i) {
         Header h = this.get(i);
         if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
            return this.remove(i);
         }
      }

      return null;
   }

   public boolean addOrReplace(Header header) {
      for(int i = 0; i < this.size(); ++i) {
         Header hdr = this.get(i);
         if (hdr.getNamespaceURI().equals(header.getNamespaceURI()) && hdr.getLocalPart().equals(header.getLocalPart())) {
            this.removeInternal(i);
            this.addInternal(i, header);
            return true;
         }
      }

      return this.add(header);
   }

   public void replace(Header old, Header header) {
      for(int i = 0; i < this.size(); ++i) {
         Header hdr = this.get(i);
         if (hdr.getNamespaceURI().equals(header.getNamespaceURI()) && hdr.getLocalPart().equals(header.getLocalPart())) {
            this.removeInternal(i);
            this.addInternal(i, header);
            return;
         }
      }

      throw new IllegalArgumentException();
   }

   protected void addInternal(int index, Header header) {
      super.add(index, header);
   }

   protected Header removeInternal(int index) {
      return (Header)super.remove(index);
   }

   @Nullable
   public Header remove(@NotNull QName name) {
      return this.remove(name.getNamespaceURI(), name.getLocalPart());
   }

   public Header remove(int index) {
      this.removeUnderstoodBit(index);
      return (Header)super.remove(index);
   }

   private void removeUnderstoodBit(int index) {
      assert index < this.size();

      int i;
      if (index < 32) {
         i = this.understoodBits >>> -31 + index << index;
         int lowerBits = this.understoodBits << -index >>> 31 - index >>> 1;
         this.understoodBits = i | lowerBits;
         if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
            if (this.moreUnderstoodBits.get(0)) {
               this.understoodBits |= Integer.MIN_VALUE;
            }

            this.moreUnderstoodBits.clear(0);

            for(int i = this.moreUnderstoodBits.nextSetBit(1); i > 0; i = this.moreUnderstoodBits.nextSetBit(i + 1)) {
               this.moreUnderstoodBits.set(i - 1);
               this.moreUnderstoodBits.clear(i);
            }
         }
      } else if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
         index -= 32;
         this.moreUnderstoodBits.clear(index);

         for(i = this.moreUnderstoodBits.nextSetBit(index); i >= 1; i = this.moreUnderstoodBits.nextSetBit(i + 1)) {
            this.moreUnderstoodBits.set(i - 1);
            this.moreUnderstoodBits.clear(i);
         }
      }

      if (this.size() - 1 <= 33 && this.moreUnderstoodBits != null) {
         this.moreUnderstoodBits = null;
      }

   }

   public boolean remove(Object o) {
      if (o != null) {
         for(int index = 0; index < this.size(); ++index) {
            if (o.equals(this.get(index))) {
               this.remove(index);
               return true;
            }
         }
      }

      return false;
   }

   public Header remove(Header h) {
      return this.remove((Object)h) ? h : null;
   }

   public static HeaderList copy(MessageHeaders original) {
      return original == null ? null : new HeaderList(original);
   }

   public static HeaderList copy(HeaderList original) {
      return copy((MessageHeaders)original);
   }

   public void readResponseAddressingHeaders(WSDLPort wsdlPort, WSBinding binding) {
   }

   public void understood(QName name) {
      this.get(name, true);
   }

   public void understood(String nsUri, String localName) {
      this.get(nsUri, localName, true);
   }

   public Set<QName> getUnderstoodHeaders() {
      Set<QName> understoodHdrs = new HashSet();

      for(int i = 0; i < this.size(); ++i) {
         if (this.isUnderstood(i)) {
            Header header = this.get(i);
            understoodHdrs.add(new QName(header.getNamespaceURI(), header.getLocalPart()));
         }
      }

      return understoodHdrs;
   }

   public boolean isUnderstood(Header header) {
      return this.isUnderstood(header.getNamespaceURI(), header.getLocalPart());
   }

   public boolean isUnderstood(String nsUri, String localName) {
      for(int i = 0; i < this.size(); ++i) {
         Header h = this.get(i);
         if (h.getLocalPart().equals(localName) && h.getNamespaceURI().equals(nsUri)) {
            return this.isUnderstood(i);
         }
      }

      return false;
   }

   public boolean isUnderstood(QName name) {
      return this.isUnderstood(name.getNamespaceURI(), name.getLocalPart());
   }

   public Set<QName> getNotUnderstoodHeaders(Set<String> roles, Set<QName> knownHeaders, WSBinding binding) {
      Set<QName> notUnderstoodHeaders = null;
      if (roles == null) {
         roles = new HashSet();
      }

      SOAPVersion effectiveSoapVersion = this.getEffectiveSOAPVersion(binding);
      ((Set)roles).add(effectiveSoapVersion.implicitRole);

      for(int i = 0; i < this.size(); ++i) {
         if (!this.isUnderstood(i)) {
            Header header = this.get(i);
            if (!header.isIgnorable(effectiveSoapVersion, (Set)roles)) {
               QName qName = new QName(header.getNamespaceURI(), header.getLocalPart());
               if (binding == null) {
                  if (notUnderstoodHeaders == null) {
                     notUnderstoodHeaders = new HashSet();
                  }

                  notUnderstoodHeaders.add(qName);
               } else if (binding instanceof SOAPBindingImpl && !((SOAPBindingImpl)binding).understandsHeader(qName) && !knownHeaders.contains(qName)) {
                  if (notUnderstoodHeaders == null) {
                     notUnderstoodHeaders = new HashSet();
                  }

                  notUnderstoodHeaders.add(qName);
               }
            }
         }
      }

      return notUnderstoodHeaders;
   }

   private SOAPVersion getEffectiveSOAPVersion(WSBinding binding) {
      SOAPVersion mySOAPVersion = this.soapVersion != null ? this.soapVersion : binding.getSOAPVersion();
      if (mySOAPVersion == null) {
         mySOAPVersion = SOAPVersion.SOAP_11;
      }

      return mySOAPVersion;
   }

   public void setSoapVersion(SOAPVersion soapVersion) {
      this.soapVersion = soapVersion;
   }

   public Iterator<Header> getHeaders() {
      return this.iterator();
   }

   public List<Header> asList() {
      return this;
   }
}
