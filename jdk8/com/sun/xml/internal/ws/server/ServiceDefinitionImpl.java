package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServiceDefinitionImpl implements ServiceDefinition, SDDocumentResolver {
   private final List<SDDocumentImpl> docs;
   private final Map<String, SDDocumentImpl> bySystemId;
   @NotNull
   private final SDDocumentImpl primaryWsdl;
   WSEndpointImpl<?> owner;
   final List<SDDocumentFilter> filters = new ArrayList();

   public ServiceDefinitionImpl(List<SDDocumentImpl> docs, @NotNull SDDocumentImpl primaryWsdl) {
      assert docs.contains(primaryWsdl);

      this.docs = docs;
      this.primaryWsdl = primaryWsdl;
      this.bySystemId = new HashMap(docs.size());
      Iterator var3 = docs.iterator();

      while(var3.hasNext()) {
         SDDocumentImpl doc = (SDDocumentImpl)var3.next();
         this.bySystemId.put(doc.getURL().toExternalForm(), doc);
         doc.setFilters(this.filters);
         doc.setResolver(this);
      }

   }

   void setOwner(WSEndpointImpl<?> owner) {
      assert owner != null && this.owner == null;

      this.owner = owner;
   }

   @NotNull
   public SDDocument getPrimary() {
      return this.primaryWsdl;
   }

   public void addFilter(SDDocumentFilter filter) {
      this.filters.add(filter);
   }

   public Iterator<SDDocument> iterator() {
      return this.docs.iterator();
   }

   public SDDocument resolve(String systemId) {
      return (SDDocument)this.bySystemId.get(systemId);
   }
}
