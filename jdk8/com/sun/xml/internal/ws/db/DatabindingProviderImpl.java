package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import java.io.File;
import java.util.Map;

public class DatabindingProviderImpl implements DatabindingProvider {
   private static final String CachedDatabinding = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
   Map<String, Object> properties;

   public void init(Map<String, Object> p) {
      this.properties = p;
   }

   DatabindingImpl getCachedDatabindingImpl(DatabindingConfig config) {
      Object object = config.properties().get("com.sun.xml.internal.ws.db.DatabindingProviderImpl");
      return object != null && object instanceof DatabindingImpl ? (DatabindingImpl)object : null;
   }

   public Databinding create(DatabindingConfig config) {
      DatabindingImpl impl = this.getCachedDatabindingImpl(config);
      if (impl == null) {
         impl = new DatabindingImpl(this, config);
         config.properties().put("com.sun.xml.internal.ws.db.DatabindingProviderImpl", impl);
      }

      return impl;
   }

   public WSDLGenerator wsdlGen(DatabindingConfig config) {
      DatabindingImpl impl = (DatabindingImpl)this.create(config);
      return new DatabindingProviderImpl.JaxwsWsdlGen(impl);
   }

   public boolean isFor(String databindingMode) {
      return true;
   }

   public static class JaxwsWsdlGen implements WSDLGenerator {
      DatabindingImpl databinding;
      WSDLGenInfo wsdlGenInfo;

      JaxwsWsdlGen(DatabindingImpl impl) {
         this.databinding = impl;
         this.wsdlGenInfo = new WSDLGenInfo();
      }

      public WSDLGenerator inlineSchema(boolean inline) {
         this.wsdlGenInfo.setInlineSchemas(inline);
         return this;
      }

      public WSDLGenerator property(String name, Object value) {
         return this;
      }

      public void generate(WSDLResolver wsdlResolver) {
         this.wsdlGenInfo.setWsdlResolver(wsdlResolver);
         this.databinding.generateWSDL(this.wsdlGenInfo);
      }

      public void generate(File outputDir, String name) {
         this.databinding.generateWSDL(this.wsdlGenInfo);
      }
   }
}
