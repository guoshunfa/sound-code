package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingFactoryImpl extends DatabindingFactory {
   static final String WsRuntimeFactoryDefaultImpl = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
   protected Map<String, Object> properties = new HashMap();
   protected DatabindingProvider defaultRuntimeFactory;
   protected List<DatabindingProvider> providers;

   private static List<DatabindingProvider> providers() {
      List<DatabindingProvider> factories = new ArrayList();
      Iterator var1 = ServiceFinder.find(DatabindingProvider.class).iterator();

      while(var1.hasNext()) {
         DatabindingProvider p = (DatabindingProvider)var1.next();
         factories.add(p);
      }

      return factories;
   }

   public Map<String, Object> properties() {
      return this.properties;
   }

   <T> T property(Class<T> propType, String propName) {
      if (propName == null) {
         propName = propType.getName();
      }

      return propType.cast(this.properties.get(propName));
   }

   public DatabindingProvider provider(DatabindingConfig config) {
      String mode = this.databindingMode(config);
      if (this.providers == null) {
         this.providers = providers();
      }

      DatabindingProvider provider = null;
      if (this.providers != null) {
         Iterator var4 = this.providers.iterator();

         while(var4.hasNext()) {
            DatabindingProvider p = (DatabindingProvider)var4.next();
            if (p.isFor(mode)) {
               provider = p;
            }
         }
      }

      if (provider == null) {
         provider = new DatabindingProviderImpl();
      }

      return (DatabindingProvider)provider;
   }

   public Databinding createRuntime(DatabindingConfig config) {
      DatabindingProvider provider = this.provider(config);
      return provider.create(config);
   }

   public WSDLGenerator createWsdlGen(DatabindingConfig config) {
      DatabindingProvider provider = this.provider(config);
      return provider.wsdlGen(config);
   }

   String databindingMode(DatabindingConfig config) {
      if (config.getMappingInfo() != null && config.getMappingInfo().getDatabindingMode() != null) {
         return config.getMappingInfo().getDatabindingMode();
      } else {
         if (config.getFeatures() != null) {
            Iterator var2 = config.getFeatures().iterator();

            while(var2.hasNext()) {
               WebServiceFeature f = (WebServiceFeature)var2.next();
               if (f instanceof DatabindingModeFeature) {
                  DatabindingModeFeature dmf = (DatabindingModeFeature)f;
                  config.properties().putAll(dmf.getProperties());
                  return dmf.getMode();
               }
            }
         }

         return null;
      }
   }

   ClassLoader classLoader() {
      ClassLoader classLoader = (ClassLoader)this.property(ClassLoader.class, (String)null);
      if (classLoader == null) {
         classLoader = Thread.currentThread().getContextClassLoader();
      }

      return classLoader;
   }

   Properties loadPropertiesFile(String fileName) {
      ClassLoader classLoader = this.classLoader();
      Properties p = new Properties();

      try {
         InputStream is = null;
         if (classLoader == null) {
            is = ClassLoader.getSystemResourceAsStream(fileName);
         } else {
            is = classLoader.getResourceAsStream(fileName);
         }

         if (is != null) {
            p.load(is);
         }

         return p;
      } catch (Exception var5) {
         throw new WebServiceException(var5);
      }
   }

   public Databinding.Builder createBuilder(Class<?> contractClass, Class<?> endpointClass) {
      return new DatabindingFactoryImpl.ConfigBuilder(this, contractClass, endpointClass);
   }

   static class ConfigBuilder implements Databinding.Builder {
      DatabindingConfig config;
      DatabindingFactoryImpl factory;

      ConfigBuilder(DatabindingFactoryImpl f, Class<?> contractClass, Class<?> implBeanClass) {
         this.factory = f;
         this.config = new DatabindingConfig();
         this.config.setContractClass(contractClass);
         this.config.setEndpointClass(implBeanClass);
      }

      public Databinding.Builder targetNamespace(String targetNamespace) {
         this.config.getMappingInfo().setTargetNamespace(targetNamespace);
         return this;
      }

      public Databinding.Builder serviceName(QName serviceName) {
         this.config.getMappingInfo().setServiceName(serviceName);
         return this;
      }

      public Databinding.Builder portName(QName portName) {
         this.config.getMappingInfo().setPortName(portName);
         return this;
      }

      public Databinding.Builder wsdlURL(URL wsdlURL) {
         this.config.setWsdlURL(wsdlURL);
         return this;
      }

      public Databinding.Builder wsdlSource(Source wsdlSource) {
         this.config.setWsdlSource(wsdlSource);
         return this;
      }

      public Databinding.Builder entityResolver(EntityResolver entityResolver) {
         this.config.setEntityResolver(entityResolver);
         return this;
      }

      public Databinding.Builder classLoader(ClassLoader classLoader) {
         this.config.setClassLoader(classLoader);
         return this;
      }

      public Databinding.Builder feature(WebServiceFeature... f) {
         this.config.setFeatures(f);
         return this;
      }

      public Databinding.Builder property(String name, Object value) {
         this.config.properties().put(name, value);
         if (this.isfor(BindingID.class, name, value)) {
            this.config.getMappingInfo().setBindingID((BindingID)value);
         }

         if (this.isfor(WSBinding.class, name, value)) {
            this.config.setWSBinding((WSBinding)value);
         }

         if (this.isfor(WSDLPort.class, name, value)) {
            this.config.setWsdlPort((WSDLPort)value);
         }

         if (this.isfor(MetadataReader.class, name, value)) {
            this.config.setMetadataReader((MetadataReader)value);
         }

         return this;
      }

      boolean isfor(Class<?> type, String name, Object value) {
         return type.getName().equals(name) && type.isInstance(value);
      }

      public Databinding build() {
         return this.factory.createRuntime(this.config);
      }

      public WSDLGenerator createWSDLGenerator() {
         return this.factory.createWsdlGen(this.config);
      }
   }
}
