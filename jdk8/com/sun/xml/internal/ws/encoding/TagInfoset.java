package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class TagInfoset {
   @NotNull
   public final String[] ns;
   @NotNull
   public final AttributesImpl atts;
   @Nullable
   public final String prefix;
   @Nullable
   public final String nsUri;
   @NotNull
   public final String localName;
   @Nullable
   private String qname;
   private static final String[] EMPTY_ARRAY = new String[0];
   private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();

   public TagInfoset(String nsUri, String localName, String prefix, AttributesImpl atts, String... ns) {
      this.nsUri = nsUri;
      this.prefix = prefix;
      this.localName = localName;
      this.atts = atts;
      this.ns = ns;
   }

   public TagInfoset(XMLStreamReader reader) {
      this.prefix = reader.getPrefix();
      this.nsUri = reader.getNamespaceURI();
      this.localName = reader.getLocalName();
      int nsc = reader.getNamespaceCount();
      int ac;
      if (nsc > 0) {
         this.ns = new String[nsc * 2];

         for(ac = 0; ac < nsc; ++ac) {
            this.ns[ac * 2] = fixNull(reader.getNamespacePrefix(ac));
            this.ns[ac * 2 + 1] = fixNull(reader.getNamespaceURI(ac));
         }
      } else {
         this.ns = EMPTY_ARRAY;
      }

      ac = reader.getAttributeCount();
      if (ac > 0) {
         this.atts = new AttributesImpl();
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < ac; ++i) {
            sb.setLength(0);
            String prefix = reader.getAttributePrefix(i);
            String localName = reader.getAttributeLocalName(i);
            String qname;
            if (prefix != null && prefix.length() != 0) {
               sb.append(prefix);
               sb.append(":");
               sb.append(localName);
               qname = sb.toString();
            } else {
               qname = localName;
            }

            this.atts.addAttribute(fixNull(reader.getAttributeNamespace(i)), localName, qname, reader.getAttributeType(i), reader.getAttributeValue(i));
         }
      } else {
         this.atts = EMPTY_ATTRIBUTES;
      }

   }

   public void writeStart(ContentHandler contentHandler) throws SAXException {
      for(int i = 0; i < this.ns.length; i += 2) {
         contentHandler.startPrefixMapping(fixNull(this.ns[i]), fixNull(this.ns[i + 1]));
      }

      contentHandler.startElement(fixNull(this.nsUri), this.localName, this.getQName(), this.atts);
   }

   public void writeEnd(ContentHandler contentHandler) throws SAXException {
      contentHandler.endElement(fixNull(this.nsUri), this.localName, this.getQName());

      for(int i = this.ns.length - 2; i >= 0; i -= 2) {
         contentHandler.endPrefixMapping(fixNull(this.ns[i]));
      }

   }

   public void writeStart(XMLStreamWriter w) throws XMLStreamException {
      if (this.prefix == null) {
         if (this.nsUri == null) {
            w.writeStartElement(this.localName);
         } else {
            w.writeStartElement("", this.localName, this.nsUri);
         }
      } else {
         w.writeStartElement(this.prefix, this.localName, this.nsUri);
      }

      int i;
      for(i = 0; i < this.ns.length; i += 2) {
         w.writeNamespace(this.ns[i], this.ns[i + 1]);
      }

      for(i = 0; i < this.atts.getLength(); ++i) {
         String nsUri = this.atts.getURI(i);
         if (nsUri != null && nsUri.length() != 0) {
            String rawName = this.atts.getQName(i);
            String prefix = rawName.substring(0, rawName.indexOf(58));
            w.writeAttribute(prefix, nsUri, this.atts.getLocalName(i), this.atts.getValue(i));
         } else {
            w.writeAttribute(this.atts.getLocalName(i), this.atts.getValue(i));
         }
      }

   }

   private String getQName() {
      if (this.qname != null) {
         return this.qname;
      } else {
         StringBuilder sb = new StringBuilder();
         if (this.prefix != null) {
            sb.append(this.prefix);
            sb.append(':');
            sb.append(this.localName);
            this.qname = sb.toString();
         } else {
            this.qname = this.localName;
         }

         return this.qname;
      }
   }

   private static String fixNull(String s) {
      return s == null ? "" : s;
   }

   public String getNamespaceURI(String prefix) {
      int size = this.ns.length / 2;

      for(int i = 0; i < size; ++i) {
         String p = this.ns[i * 2];
         String n = this.ns[i * 2 + 1];
         if (prefix.equals(p)) {
            return n;
         }
      }

      return null;
   }

   public String getPrefix(String namespaceURI) {
      int size = this.ns.length / 2;

      for(int i = 0; i < size; ++i) {
         String p = this.ns[i * 2];
         String n = this.ns[i * 2 + 1];
         if (namespaceURI.equals(n)) {
            return p;
         }
      }

      return null;
   }

   public List<String> allPrefixes(String namespaceURI) {
      int size = this.ns.length / 2;
      List<String> l = new ArrayList();

      for(int i = 0; i < size; ++i) {
         String p = this.ns[i * 2];
         String n = this.ns[i * 2 + 1];
         if (namespaceURI.equals(n)) {
            l.add(p);
         }
      }

      return l;
   }
}
