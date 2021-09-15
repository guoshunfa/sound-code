package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.util.Vector;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextWrapper implements NamespaceContext {
   private com.sun.org.apache.xerces.internal.xni.NamespaceContext fNamespaceContext;

   public NamespaceContextWrapper(NamespaceSupport namespaceContext) {
      this.fNamespaceContext = namespaceContext;
   }

   public String getNamespaceURI(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException("Prefix can't be null");
      } else {
         return this.fNamespaceContext.getURI(prefix.intern());
      }
   }

   public String getPrefix(String namespaceURI) {
      if (namespaceURI == null) {
         throw new IllegalArgumentException("URI can't be null.");
      } else {
         return this.fNamespaceContext.getPrefix(namespaceURI.intern());
      }
   }

   public Iterator getPrefixes(String namespaceURI) {
      if (namespaceURI == null) {
         throw new IllegalArgumentException("URI can't be null.");
      } else {
         Vector vector = ((NamespaceSupport)this.fNamespaceContext).getPrefixes(namespaceURI.intern());
         return vector.iterator();
      }
   }

   public com.sun.org.apache.xerces.internal.xni.NamespaceContext getNamespaceContext() {
      return this.fNamespaceContext;
   }
}
