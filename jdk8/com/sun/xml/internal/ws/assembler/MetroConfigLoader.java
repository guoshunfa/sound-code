package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.MetroConfig;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import com.sun.xml.internal.ws.runtime.config.TubelineDefinition;
import com.sun.xml.internal.ws.runtime.config.TubelineMapping;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.ws.WebServiceException;

class MetroConfigLoader {
   private static final Logger LOGGER = Logger.getLogger(MetroConfigLoader.class);
   private MetroConfigName defaultTubesConfigNames;
   private static final MetroConfigLoader.TubeFactoryListResolver ENDPOINT_SIDE_RESOLVER = new MetroConfigLoader.TubeFactoryListResolver() {
      public TubeFactoryList getFactories(TubelineDefinition td) {
         return td != null ? td.getEndpointSide() : null;
      }
   };
   private static final MetroConfigLoader.TubeFactoryListResolver CLIENT_SIDE_RESOLVER = new MetroConfigLoader.TubeFactoryListResolver() {
      public TubeFactoryList getFactories(TubelineDefinition td) {
         return td != null ? td.getClientSide() : null;
      }
   };
   private MetroConfig defaultConfig;
   private URL defaultConfigUrl;
   private MetroConfig appConfig;
   private URL appConfigUrl;

   MetroConfigLoader(Container container, MetroConfigName defaultTubesConfigNames) {
      this.defaultTubesConfigNames = defaultTubesConfigNames;
      ResourceLoader spiResourceLoader = null;
      if (container != null) {
         spiResourceLoader = (ResourceLoader)container.getSPI(ResourceLoader.class);
      }

      this.init(container, spiResourceLoader, new MetroConfigLoader.MetroConfigUrlLoader(container));
   }

   private void init(Container container, ResourceLoader... loaders) {
      String appFileName = null;
      String defaultFileName = null;
      if (container != null) {
         MetroConfigName mcn = (MetroConfigName)container.getSPI(MetroConfigName.class);
         if (mcn != null) {
            appFileName = mcn.getAppFileName();
            defaultFileName = mcn.getDefaultFileName();
         }
      }

      if (appFileName == null) {
         appFileName = this.defaultTubesConfigNames.getAppFileName();
      }

      if (defaultFileName == null) {
         defaultFileName = this.defaultTubesConfigNames.getDefaultFileName();
      }

      this.defaultConfigUrl = locateResource(defaultFileName, loaders);
      if (this.defaultConfigUrl == null) {
         throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0001_DEFAULT_CFG_FILE_NOT_FOUND(defaultFileName)));
      } else {
         LOGGER.config(TubelineassemblyMessages.MASM_0002_DEFAULT_CFG_FILE_LOCATED(defaultFileName, this.defaultConfigUrl));
         this.defaultConfig = loadMetroConfig(this.defaultConfigUrl);
         if (this.defaultConfig == null) {
            throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0003_DEFAULT_CFG_FILE_NOT_LOADED(defaultFileName)));
         } else if (this.defaultConfig.getTubelines() == null) {
            throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0004_NO_TUBELINES_SECTION_IN_DEFAULT_CFG_FILE(defaultFileName)));
         } else if (this.defaultConfig.getTubelines().getDefault() == null) {
            throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0005_NO_DEFAULT_TUBELINE_IN_DEFAULT_CFG_FILE(defaultFileName)));
         } else {
            this.appConfigUrl = locateResource(appFileName, loaders);
            if (this.appConfigUrl != null) {
               LOGGER.config(TubelineassemblyMessages.MASM_0006_APP_CFG_FILE_LOCATED(this.appConfigUrl));
               this.appConfig = loadMetroConfig(this.appConfigUrl);
            } else {
               LOGGER.config(TubelineassemblyMessages.MASM_0007_APP_CFG_FILE_NOT_FOUND());
               this.appConfig = null;
            }

         }
      }
   }

   TubeFactoryList getEndpointSideTubeFactories(URI endpointReference) {
      return this.getTubeFactories(endpointReference, ENDPOINT_SIDE_RESOLVER);
   }

   TubeFactoryList getClientSideTubeFactories(URI endpointReference) {
      return this.getTubeFactories(endpointReference, CLIENT_SIDE_RESOLVER);
   }

   private TubeFactoryList getTubeFactories(URI endpointReference, MetroConfigLoader.TubeFactoryListResolver resolver) {
      Iterator var3;
      TubelineMapping mapping;
      TubeFactoryList list;
      if (this.appConfig != null && this.appConfig.getTubelines() != null) {
         var3 = this.appConfig.getTubelines().getTubelineMappings().iterator();

         while(var3.hasNext()) {
            mapping = (TubelineMapping)var3.next();
            if (mapping.getEndpointRef().equals(endpointReference.toString())) {
               list = resolver.getFactories(this.getTubeline(this.appConfig, resolveReference(mapping.getTubelineRef())));
               if (list != null) {
                  return list;
               }
               break;
            }
         }

         if (this.appConfig.getTubelines().getDefault() != null) {
            TubeFactoryList list = resolver.getFactories(this.getTubeline(this.appConfig, resolveReference(this.appConfig.getTubelines().getDefault())));
            if (list != null) {
               return list;
            }
         }
      }

      var3 = this.defaultConfig.getTubelines().getTubelineMappings().iterator();

      while(var3.hasNext()) {
         mapping = (TubelineMapping)var3.next();
         if (mapping.getEndpointRef().equals(endpointReference.toString())) {
            list = resolver.getFactories(this.getTubeline(this.defaultConfig, resolveReference(mapping.getTubelineRef())));
            if (list != null) {
               return list;
            }
            break;
         }
      }

      return resolver.getFactories(this.getTubeline(this.defaultConfig, resolveReference(this.defaultConfig.getTubelines().getDefault())));
   }

   TubelineDefinition getTubeline(MetroConfig config, URI tubelineDefinitionUri) {
      if (config != null && config.getTubelines() != null) {
         Iterator var3 = config.getTubelines().getTubelineDefinitions().iterator();

         while(var3.hasNext()) {
            TubelineDefinition td = (TubelineDefinition)var3.next();
            if (td.getName().equals(tubelineDefinitionUri.getFragment())) {
               return td;
            }
         }
      }

      return null;
   }

   private static URI resolveReference(String reference) {
      try {
         return new URI(reference);
      } catch (URISyntaxException var2) {
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(TubelineassemblyMessages.MASM_0008_INVALID_URI_REFERENCE(reference), var2));
      }
   }

   private static URL locateResource(String resource, ResourceLoader loader) {
      if (loader == null) {
         return null;
      } else {
         try {
            return loader.getResource(resource);
         } catch (MalformedURLException var3) {
            LOGGER.severe(TubelineassemblyMessages.MASM_0009_CANNOT_FORM_VALID_URL(resource), (Throwable)var3);
            return null;
         }
      }
   }

   private static URL locateResource(String resource, ResourceLoader[] loaders) {
      ResourceLoader[] var2 = loaders;
      int var3 = loaders.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ResourceLoader loader = var2[var4];
         URL url = locateResource(resource, loader);
         if (url != null) {
            return url;
         }
      }

      return null;
   }

   private static MetroConfig loadMetroConfig(@NotNull URL resourceUrl) {
      MetroConfig result = null;

      try {
         JAXBContext jaxbContext = createJAXBContext();
         Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
         XMLInputFactory factory = XmlUtil.newXMLInputFactory(true);
         JAXBElement<MetroConfig> configElement = unmarshaller.unmarshal(factory.createXMLStreamReader(resourceUrl.openStream()), MetroConfig.class);
         result = (MetroConfig)configElement.getValue();
      } catch (Exception var6) {
         LOGGER.warning(TubelineassemblyMessages.MASM_0010_ERROR_READING_CFG_FILE_FROM_LOCATION(resourceUrl.toString()), (Throwable)var6);
      }

      return result;
   }

   private static JAXBContext createJAXBContext() throws Exception {
      return isJDKInternal() ? (JAXBContext)AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>() {
         public JAXBContext run() throws Exception {
            return JAXBContext.newInstance(MetroConfig.class.getPackage().getName());
         }
      }, createSecurityContext()) : JAXBContext.newInstance(MetroConfig.class.getPackage().getName());
   }

   private static AccessControlContext createSecurityContext() {
      PermissionCollection perms = new Permissions();
      perms.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.runtime.config"));
      perms.add(new ReflectPermission("suppressAccessChecks"));
      return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, perms)});
   }

   private static boolean isJDKInternal() {
      return MetroConfigLoader.class.getName().startsWith("com.sun.xml.internal.ws");
   }

   private static class MetroConfigUrlLoader extends ResourceLoader {
      Container container;
      ResourceLoader parentLoader;

      MetroConfigUrlLoader(ResourceLoader parentLoader) {
         this.parentLoader = parentLoader;
      }

      MetroConfigUrlLoader(Container container) {
         this(container != null ? (ResourceLoader)container.getSPI(ResourceLoader.class) : null);
         this.container = container;
      }

      public URL getResource(String resource) throws MalformedURLException {
         MetroConfigLoader.LOGGER.entering(resource);
         URL resourceUrl = null;

         URL var3;
         try {
            if (this.parentLoader != null) {
               if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
                  MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0011_LOADING_RESOURCE(resource, this.parentLoader));
               }

               resourceUrl = this.parentLoader.getResource(resource);
            }

            if (resourceUrl == null) {
               resourceUrl = loadViaClassLoaders("com/sun/xml/internal/ws/assembler/" + resource);
            }

            if (resourceUrl == null && this.container != null) {
               resourceUrl = this.loadFromServletContext(resource);
            }

            var3 = resourceUrl;
         } finally {
            MetroConfigLoader.LOGGER.exiting(resourceUrl);
         }

         return var3;
      }

      private static URL loadViaClassLoaders(String resource) {
         URL resourceUrl = tryLoadFromClassLoader(resource, Thread.currentThread().getContextClassLoader());
         if (resourceUrl == null) {
            resourceUrl = tryLoadFromClassLoader(resource, MetroConfigLoader.class.getClassLoader());
            if (resourceUrl == null) {
               return ClassLoader.getSystemResource(resource);
            }
         }

         return resourceUrl;
      }

      private static URL tryLoadFromClassLoader(String resource, ClassLoader loader) {
         return loader != null ? loader.getResource(resource) : null;
      }

      private URL loadFromServletContext(String resource) throws RuntimeException {
         Object context = null;

         try {
            Class<?> contextClass = Class.forName("javax.servlet.ServletContext");
            context = this.container.getSPI(contextClass);
            if (context != null) {
               if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
                  MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0012_LOADING_VIA_SERVLET_CONTEXT(resource, context));
               }

               try {
                  Method method = context.getClass().getMethod("getResource", String.class);
                  Object result = method.invoke(context, "/WEB-INF/" + resource);
                  return (URL)URL.class.cast(result);
               } catch (Exception var6) {
                  throw (RuntimeException)MetroConfigLoader.LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0013_ERROR_INVOKING_SERVLET_CONTEXT_METHOD("getResource()")), var6);
               }
            }
         } catch (ClassNotFoundException var7) {
            if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
               MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0014_UNABLE_TO_LOAD_CLASS("javax.servlet.ServletContext"));
            }
         }

         return null;
      }
   }

   private interface TubeFactoryListResolver {
      TubeFactoryList getFactories(TubelineDefinition var1);
   }
}
