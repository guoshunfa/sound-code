package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Document {
   private final XmlSerializer out;
   private boolean started = false;
   private Content current = null;
   private final Map<Class, DatatypeWriter> datatypeWriters = new HashMap();
   private int iota = 1;
   private final NamespaceSupport inscopeNamespace = new NamespaceSupport();
   private NamespaceDecl activeNamespaces;
   private final ContentVisitor visitor = new ContentVisitor() {
      public void onStartDocument() {
         throw new IllegalStateException();
      }

      public void onEndDocument() {
         Document.this.out.endDocument();
      }

      public void onEndTag() {
         Document.this.out.endTag();
         Document.this.inscopeNamespace.popContext();
         Document.this.activeNamespaces = null;
      }

      public void onPcdata(StringBuilder buffer) {
         if (Document.this.activeNamespaces != null) {
            buffer = Document.this.fixPrefix(buffer);
         }

         Document.this.out.text(buffer);
      }

      public void onCdata(StringBuilder buffer) {
         if (Document.this.activeNamespaces != null) {
            buffer = Document.this.fixPrefix(buffer);
         }

         Document.this.out.cdata(buffer);
      }

      public void onComment(StringBuilder buffer) {
         if (Document.this.activeNamespaces != null) {
            buffer = Document.this.fixPrefix(buffer);
         }

         Document.this.out.comment(buffer);
      }

      public void onStartTag(String nsUri, String localName, Attribute attributes, NamespaceDecl namespaces) {
         assert nsUri != null;

         assert localName != null;

         Document.this.activeNamespaces = namespaces;
         if (!Document.this.started) {
            Document.this.started = true;
            Document.this.out.startDocument();
         }

         Document.this.inscopeNamespace.pushContext();

         NamespaceDecl ns;
         String prefix;
         for(ns = namespaces; ns != null; ns = ns.next) {
            ns.declared = false;
            if (ns.prefix != null) {
               prefix = Document.this.inscopeNamespace.getURI(ns.prefix);
               if (prefix == null || !prefix.equals(ns.uri)) {
                  Document.this.inscopeNamespace.declarePrefix(ns.prefix, ns.uri);
                  ns.declared = true;
               }
            }
         }

         for(ns = namespaces; ns != null; ns = ns.next) {
            if (ns.prefix == null) {
               if (Document.this.inscopeNamespace.getURI("").equals(ns.uri)) {
                  ns.prefix = "";
               } else {
                  prefix = Document.this.inscopeNamespace.getPrefix(ns.uri);
                  if (prefix == null) {
                     while(true) {
                        if (Document.this.inscopeNamespace.getURI(prefix = Document.this.newPrefix()) == null) {
                           ns.declared = true;
                           Document.this.inscopeNamespace.declarePrefix(prefix, ns.uri);
                           break;
                        }
                     }
                  }

                  ns.prefix = prefix;
               }
            }
         }

         assert namespaces.uri.equals(nsUri);

         assert namespaces.prefix != null : "a prefix must have been all allocated";

         Document.this.out.beginStartTag(nsUri, localName, namespaces.prefix);

         for(ns = namespaces; ns != null; ns = ns.next) {
            if (ns.declared) {
               Document.this.out.writeXmlns(ns.prefix, ns.uri);
            }
         }

         for(Attribute a = attributes; a != null; a = a.next) {
            if (a.nsUri.length() == 0) {
               prefix = "";
            } else {
               prefix = Document.this.inscopeNamespace.getPrefix(a.nsUri);
            }

            Document.this.out.writeAttribute(a.nsUri, a.localName, prefix, Document.this.fixPrefix(a.value));
         }

         Document.this.out.endStartTag(nsUri, localName, namespaces.prefix);
      }
   };
   private final StringBuilder prefixSeed = new StringBuilder("ns");
   private int prefixIota = 0;
   static final char MAGIC = '\u0000';

   Document(XmlSerializer out) {
      this.out = out;
      Iterator var2 = DatatypeWriter.BUILTIN.iterator();

      while(var2.hasNext()) {
         DatatypeWriter dw = (DatatypeWriter)var2.next();
         this.datatypeWriters.put(dw.getType(), dw);
      }

   }

   void flush() {
      this.out.flush();
   }

   void setFirstContent(Content c) {
      assert this.current == null;

      this.current = new StartDocument();
      this.current.setNext(this, c);
   }

   public void addDatatypeWriter(DatatypeWriter<?> dw) {
      this.datatypeWriters.put(dw.getType(), dw);
   }

   void run() {
      while(true) {
         Content next = this.current.getNext();
         if (next == null || !next.isReadyToCommit()) {
            return;
         }

         next.accept(this.visitor);
         next.written();
         this.current = next;
      }
   }

   void writeValue(Object obj, NamespaceResolver nsResolver, StringBuilder buf) {
      if (obj == null) {
         throw new IllegalArgumentException("argument contains null");
      } else if (obj instanceof Object[]) {
         Object[] var9 = (Object[])((Object[])obj);
         int var11 = var9.length;

         for(int var6 = 0; var6 < var11; ++var6) {
            Object o = var9[var6];
            this.writeValue(o, nsResolver, buf);
         }

      } else if (!(obj instanceof Iterable)) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         for(Class c = obj.getClass(); c != null; c = c.getSuperclass()) {
            DatatypeWriter dw = (DatatypeWriter)this.datatypeWriters.get(c);
            if (dw != null) {
               dw.print(obj, nsResolver, buf);
               return;
            }
         }

         buf.append(obj);
      } else {
         Iterator var4 = ((Iterable)obj).iterator();

         while(var4.hasNext()) {
            Object o = var4.next();
            this.writeValue(o, nsResolver, buf);
         }

      }
   }

   private String newPrefix() {
      this.prefixSeed.setLength(2);
      this.prefixSeed.append(++this.prefixIota);
      return this.prefixSeed.toString();
   }

   private StringBuilder fixPrefix(StringBuilder buf) {
      assert this.activeNamespaces != null;

      int len = buf.length();

      int i;
      for(i = 0; i < len && buf.charAt(i) != 0; ++i) {
      }

      if (i == len) {
         return buf;
      } else {
         while(i < len) {
            char uriIdx = buf.charAt(i + 1);

            NamespaceDecl ns;
            for(ns = this.activeNamespaces; ns != null && ns.uniqueId != uriIdx; ns = ns.next) {
            }

            if (ns == null) {
               throw new IllegalStateException("Unexpected use of prefixes " + buf);
            }

            int length = 2;
            String prefix = ns.prefix;
            if (prefix.length() == 0) {
               if (buf.length() <= i + 2 || buf.charAt(i + 2) != ':') {
                  throw new IllegalStateException("Unexpected use of prefixes " + buf);
               }

               length = 3;
            }

            buf.replace(i, i + length, prefix);

            for(len += prefix.length() - length; i < len && buf.charAt(i) != 0; ++i) {
            }
         }

         return buf;
      }
   }

   char assignNewId() {
      return (char)(this.iota++);
   }
}
