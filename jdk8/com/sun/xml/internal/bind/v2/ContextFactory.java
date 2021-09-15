package com.sun.xml.internal.bind.v2;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.util.TypeCast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class ContextFactory {
   public static final String USE_JAXB_PROPERTIES = "_useJAXBProperties";

   public static JAXBContext createContext(Class[] classes, Map<String, Object> properties) throws JAXBException {
      Object properties;
      if (properties == null) {
         properties = Collections.emptyMap();
      } else {
         properties = new HashMap(properties);
      }

      String defaultNsUri = (String)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.defaultNamespaceRemap", String.class);
      Boolean c14nSupport = (Boolean)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.c14n", Boolean.class);
      if (c14nSupport == null) {
         c14nSupport = false;
      }

      Boolean disablesecurityProcessing = (Boolean)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.disableXmlSecurity", Boolean.class);
      if (disablesecurityProcessing == null) {
         disablesecurityProcessing = false;
      }

      Boolean allNillable = (Boolean)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.treatEverythingNillable", Boolean.class);
      if (allNillable == null) {
         allNillable = false;
      }

      Boolean retainPropertyInfo = (Boolean)getPropertyValue((Map)properties, "retainReferenceToInfo", Boolean.class);
      if (retainPropertyInfo == null) {
         retainPropertyInfo = false;
      }

      Boolean supressAccessorWarnings = (Boolean)getPropertyValue((Map)properties, "supressAccessorWarnings", Boolean.class);
      if (supressAccessorWarnings == null) {
         supressAccessorWarnings = false;
      }

      Boolean improvedXsiTypeHandling = (Boolean)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.improvedXsiTypeHandling", Boolean.class);
      if (improvedXsiTypeHandling == null) {
         String improvedXsiSystemProperty = Util.getSystemProperty("com.sun.xml.internal.bind.improvedXsiTypeHandling");
         if (improvedXsiSystemProperty == null) {
            improvedXsiTypeHandling = true;
         } else {
            improvedXsiTypeHandling = Boolean.valueOf(improvedXsiSystemProperty);
         }
      }

      Boolean xmlAccessorFactorySupport = (Boolean)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.XmlAccessorFactory", Boolean.class);
      if (xmlAccessorFactorySupport == null) {
         xmlAccessorFactorySupport = false;
         Util.getClassLogger().log(Level.FINE, "Property com.sun.xml.internal.bind.XmlAccessorFactoryis not active.  Using JAXB's implementation");
      }

      RuntimeAnnotationReader ar = (RuntimeAnnotationReader)getPropertyValue((Map)properties, JAXBRIContext.ANNOTATION_READER, RuntimeAnnotationReader.class);
      Collection<TypeReference> tr = (Collection)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.typeReferences", Collection.class);
      if (tr == null) {
         tr = Collections.emptyList();
      }

      Map subclassReplacements;
      try {
         subclassReplacements = TypeCast.checkedCast((Map)getPropertyValue((Map)properties, "com.sun.xml.internal.bind.subclassReplacements", Map.class), Class.class, Class.class);
      } catch (ClassCastException var14) {
         throw new JAXBException(Messages.INVALID_TYPE_IN_MAP.format(), var14);
      }

      if (!((Map)properties).isEmpty()) {
         throw new JAXBException(Messages.UNSUPPORTED_PROPERTY.format(((Map)properties).keySet().iterator().next()));
      } else {
         JAXBContextImpl.JAXBContextBuilder builder = new JAXBContextImpl.JAXBContextBuilder();
         builder.setClasses(classes);
         builder.setTypeRefs((Collection)tr);
         builder.setSubclassReplacements(subclassReplacements);
         builder.setDefaultNsUri(defaultNsUri);
         builder.setC14NSupport(c14nSupport);
         builder.setAnnotationReader(ar);
         builder.setXmlAccessorFactorySupport(xmlAccessorFactorySupport);
         builder.setAllNillable(allNillable);
         builder.setRetainPropertyInfo(retainPropertyInfo);
         builder.setSupressAccessorWarnings(supressAccessorWarnings);
         builder.setImprovedXsiTypeHandling(improvedXsiTypeHandling);
         builder.setDisableSecurityProcessing(disablesecurityProcessing);
         return builder.build();
      }
   }

   private static <T> T getPropertyValue(Map<String, Object> properties, String keyName, Class<T> type) throws JAXBException {
      Object o = properties.get(keyName);
      if (o == null) {
         return null;
      } else {
         properties.remove(keyName);
         if (!type.isInstance(o)) {
            throw new JAXBException(Messages.INVALID_PROPERTY_VALUE.format(keyName, o));
         } else {
            return type.cast(o);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static JAXBRIContext createContext(Class[] classes, Collection<TypeReference> typeRefs, Map<Class, Class> subclassReplacements, String defaultNsUri, boolean c14nSupport, RuntimeAnnotationReader ar, boolean xmlAccessorFactorySupport, boolean allNillable, boolean retainPropertyInfo) throws JAXBException {
      return createContext(classes, typeRefs, subclassReplacements, defaultNsUri, c14nSupport, ar, xmlAccessorFactorySupport, allNillable, retainPropertyInfo, false);
   }

   /** @deprecated */
   @Deprecated
   public static JAXBRIContext createContext(Class[] classes, Collection<TypeReference> typeRefs, Map<Class, Class> subclassReplacements, String defaultNsUri, boolean c14nSupport, RuntimeAnnotationReader ar, boolean xmlAccessorFactorySupport, boolean allNillable, boolean retainPropertyInfo, boolean improvedXsiTypeHandling) throws JAXBException {
      JAXBContextImpl.JAXBContextBuilder builder = new JAXBContextImpl.JAXBContextBuilder();
      builder.setClasses(classes);
      builder.setTypeRefs(typeRefs);
      builder.setSubclassReplacements(subclassReplacements);
      builder.setDefaultNsUri(defaultNsUri);
      builder.setC14NSupport(c14nSupport);
      builder.setAnnotationReader(ar);
      builder.setXmlAccessorFactorySupport(xmlAccessorFactorySupport);
      builder.setAllNillable(allNillable);
      builder.setRetainPropertyInfo(retainPropertyInfo);
      builder.setImprovedXsiTypeHandling(improvedXsiTypeHandling);
      return builder.build();
   }

   public static JAXBContext createContext(String contextPath, ClassLoader classLoader, Map<String, Object> properties) throws JAXBException {
      FinalArrayList<Class> classes = new FinalArrayList();
      StringTokenizer tokens = new StringTokenizer(contextPath, ":");

      while(tokens.hasMoreTokens()) {
         boolean foundJaxbIndex = false;
         boolean foundObjectFactory = false;
         String pkg = tokens.nextToken();

         try {
            Class<?> o = classLoader.loadClass(pkg + ".ObjectFactory");
            classes.add(o);
            foundObjectFactory = true;
         } catch (ClassNotFoundException var12) {
         }

         List indexedClasses;
         try {
            indexedClasses = loadIndexedClasses(pkg, classLoader);
         } catch (IOException var11) {
            throw new JAXBException(var11);
         }

         if (indexedClasses != null) {
            classes.addAll(indexedClasses);
            foundJaxbIndex = true;
         }

         if (!foundObjectFactory && !foundJaxbIndex) {
            throw new JAXBException(Messages.BROKEN_CONTEXTPATH.format(pkg));
         }
      }

      return createContext((Class[])classes.toArray(new Class[classes.size()]), properties);
   }

   private static List<Class> loadIndexedClasses(String pkg, ClassLoader classLoader) throws IOException, JAXBException {
      String resource = pkg.replace('.', '/') + "/jaxb.index";
      InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
      if (resourceAsStream == null) {
         return null;
      } else {
         BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"));

         try {
            FinalArrayList<Class> classes = new FinalArrayList();
            String className = in.readLine();

            while(className != null) {
               className = className.trim();
               if (!className.startsWith("#") && className.length() != 0) {
                  if (className.endsWith(".class")) {
                     throw new JAXBException(Messages.ILLEGAL_ENTRY.format(className));
                  }

                  try {
                     classes.add(classLoader.loadClass(pkg + '.' + className));
                  } catch (ClassNotFoundException var11) {
                     throw new JAXBException(Messages.ERROR_LOADING_CLASS.format(className, resource), var11);
                  }

                  className = in.readLine();
               } else {
                  className = in.readLine();
               }
            }

            FinalArrayList var7 = classes;
            return var7;
         } finally {
            in.close();
         }
      }
   }
}
