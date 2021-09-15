package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.ContextFactory;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

public abstract class JAXBRIContext extends JAXBContext {
   public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.internal.bind.defaultNamespaceRemap";
   public static final String TYPE_REFERENCES = "com.sun.xml.internal.bind.typeReferences";
   public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.internal.bind.c14n";
   public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.internal.bind.treatEverythingNillable";
   public static final String ANNOTATION_READER = RuntimeAnnotationReader.class.getName();
   public static final String ENABLE_XOP = "com.sun.xml.internal.bind.XOP";
   public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.internal.bind.subclassReplacements";
   public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.internal.bind.XmlAccessorFactory";
   public static final String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";
   public static final String SUPRESS_ACCESSOR_WARNINGS = "supressAccessorWarnings";
   public static final String IMPROVED_XSI_TYPE_HANDLING = "com.sun.xml.internal.bind.improvedXsiTypeHandling";
   public static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";

   protected JAXBRIContext() {
   }

   public static JAXBRIContext newInstance(@NotNull Class[] classes, @Nullable Collection<TypeReference> typeRefs, @Nullable Map<Class, Class> subclassReplacements, @Nullable String defaultNamespaceRemap, boolean c14nSupport, @Nullable RuntimeAnnotationReader ar) throws JAXBException {
      return newInstance(classes, typeRefs, subclassReplacements, defaultNamespaceRemap, c14nSupport, ar, false, false, false, false);
   }

   public static JAXBRIContext newInstance(@NotNull Class[] classes, @Nullable Collection<TypeReference> typeRefs, @Nullable Map<Class, Class> subclassReplacements, @Nullable String defaultNamespaceRemap, boolean c14nSupport, @Nullable RuntimeAnnotationReader ar, boolean xmlAccessorFactorySupport, boolean allNillable, boolean retainPropertyInfo, boolean supressAccessorWarnings) throws JAXBException {
      Map<String, Object> properties = new HashMap();
      if (typeRefs != null) {
         properties.put("com.sun.xml.internal.bind.typeReferences", typeRefs);
      }

      if (subclassReplacements != null) {
         properties.put("com.sun.xml.internal.bind.subclassReplacements", subclassReplacements);
      }

      if (defaultNamespaceRemap != null) {
         properties.put("com.sun.xml.internal.bind.defaultNamespaceRemap", defaultNamespaceRemap);
      }

      if (ar != null) {
         properties.put(ANNOTATION_READER, ar);
      }

      properties.put("com.sun.xml.internal.bind.c14n", c14nSupport);
      properties.put("com.sun.xml.internal.bind.XmlAccessorFactory", xmlAccessorFactorySupport);
      properties.put("com.sun.xml.internal.bind.treatEverythingNillable", allNillable);
      properties.put("retainReferenceToInfo", retainPropertyInfo);
      properties.put("supressAccessorWarnings", supressAccessorWarnings);
      return (JAXBRIContext)ContextFactory.createContext(classes, properties);
   }

   /** @deprecated */
   public static JAXBRIContext newInstance(@NotNull Class[] classes, @Nullable Collection<TypeReference> typeRefs, @Nullable String defaultNamespaceRemap, boolean c14nSupport) throws JAXBException {
      return newInstance(classes, typeRefs, Collections.emptyMap(), defaultNamespaceRemap, c14nSupport, (RuntimeAnnotationReader)null);
   }

   public abstract boolean hasSwaRef();

   @Nullable
   public abstract QName getElementName(@NotNull Object var1) throws JAXBException;

   @Nullable
   public abstract QName getElementName(@NotNull Class var1) throws JAXBException;

   public abstract Bridge createBridge(@NotNull TypeReference var1);

   @NotNull
   public abstract BridgeContext createBridgeContext();

   public abstract <B, V> RawAccessor<B, V> getElementPropertyAccessor(Class<B> var1, String var2, String var3) throws JAXBException;

   @NotNull
   public abstract List<String> getKnownNamespaceURIs();

   public abstract void generateSchema(@NotNull SchemaOutputResolver var1) throws IOException;

   public abstract QName getTypeName(@NotNull TypeReference var1);

   @NotNull
   public abstract String getBuildId();

   public abstract void generateEpisode(Result var1);

   public abstract RuntimeTypeInfoSet getRuntimeTypeInfoSet();

   @NotNull
   public static String mangleNameToVariableName(@NotNull String localName) {
      return NameConverter.standard.toVariableName(localName);
   }

   @NotNull
   public static String mangleNameToClassName(@NotNull String localName) {
      return NameConverter.standard.toClassName(localName);
   }

   @NotNull
   public static String mangleNameToPropertyName(@NotNull String localName) {
      return NameConverter.standard.toPropertyName(localName);
   }

   @Nullable
   public static Type getBaseType(@NotNull Type type, @NotNull Class baseType) {
      return (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(type, baseType);
   }
}
