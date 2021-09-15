package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextExAdaper implements NamespaceContextEx {
   private final NamespaceContext nsContext;

   public NamespaceContextExAdaper(NamespaceContext nsContext) {
      this.nsContext = nsContext;
   }

   public Iterator<NamespaceContextEx.Binding> iterator() {
      throw new UnsupportedOperationException();
   }

   public String getNamespaceURI(String prefix) {
      return this.nsContext.getNamespaceURI(prefix);
   }

   public String getPrefix(String namespaceURI) {
      return this.nsContext.getPrefix(namespaceURI);
   }

   public Iterator getPrefixes(String namespaceURI) {
      return this.nsContext.getPrefixes(namespaceURI);
   }
}
