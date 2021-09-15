package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

class JAXBRIContextWrapper implements BindingContext {
   private Map<TypeInfo, TypeReference> typeRefs;
   private Map<TypeReference, TypeInfo> typeInfos;
   private JAXBRIContext context;

   JAXBRIContextWrapper(JAXBRIContext cxt, Map<TypeInfo, TypeReference> refs) {
      this.context = cxt;
      this.typeRefs = refs;
      if (refs != null) {
         this.typeInfos = new HashMap();
         Iterator var3 = refs.keySet().iterator();

         while(var3.hasNext()) {
            TypeInfo ti = (TypeInfo)var3.next();
            this.typeInfos.put(this.typeRefs.get(ti), ti);
         }
      }

   }

   TypeReference typeReference(TypeInfo ti) {
      return this.typeRefs != null ? (TypeReference)this.typeRefs.get(ti) : null;
   }

   TypeInfo typeInfo(TypeReference tr) {
      return this.typeInfos != null ? (TypeInfo)this.typeInfos.get(tr) : null;
   }

   public Marshaller createMarshaller() throws JAXBException {
      return this.context.createMarshaller();
   }

   public Unmarshaller createUnmarshaller() throws JAXBException {
      return this.context.createUnmarshaller();
   }

   public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
      this.context.generateSchema(outputResolver);
   }

   public String getBuildId() {
      return this.context.getBuildId();
   }

   public QName getElementName(Class o) throws JAXBException {
      return this.context.getElementName(o);
   }

   public QName getElementName(Object o) throws JAXBException {
      return this.context.getElementName(o);
   }

   public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> wrapperBean, String nsUri, String localName) throws JAXBException {
      return new RawAccessorWrapper(this.context.getElementPropertyAccessor(wrapperBean, nsUri, localName));
   }

   public List<String> getKnownNamespaceURIs() {
      return this.context.getKnownNamespaceURIs();
   }

   public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
      return this.context.getRuntimeTypeInfoSet();
   }

   public QName getTypeName(TypeReference tr) {
      return this.context.getTypeName(tr);
   }

   public int hashCode() {
      return this.context.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         JAXBRIContextWrapper other = (JAXBRIContextWrapper)obj;
         return this.context == other.context || this.context != null && this.context.equals(other.context);
      }
   }

   public boolean hasSwaRef() {
      return this.context.hasSwaRef();
   }

   public String toString() {
      return JAXBRIContextWrapper.class.getName() + " : " + this.context.toString();
   }

   public XMLBridge createBridge(TypeInfo ti) {
      TypeReference tr = (TypeReference)this.typeRefs.get(ti);
      Bridge b = this.context.createBridge(tr);
      return (XMLBridge)(WrapperComposite.class.equals(ti.type) ? new WrapperBridge(this, b) : new BridgeWrapper(this, b));
   }

   public JAXBContext getJAXBContext() {
      return this.context;
   }

   public QName getTypeName(TypeInfo ti) {
      TypeReference tr = (TypeReference)this.typeRefs.get(ti);
      return this.context.getTypeName(tr);
   }

   public XMLBridge createFragmentBridge() {
      return new MarshallerBridge((JAXBContextImpl)this.context);
   }

   public Object newWrapperInstace(Class<?> wrapperType) throws InstantiationException, IllegalAccessException {
      return wrapperType.newInstance();
   }
}
