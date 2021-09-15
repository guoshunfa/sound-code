package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

public interface BindingContext {
   String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.internal.bind.defaultNamespaceRemap";
   String TYPE_REFERENCES = "com.sun.xml.internal.bind.typeReferences";
   String CANONICALIZATION_SUPPORT = "com.sun.xml.internal.bind.c14n";
   String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.internal.bind.treatEverythingNillable";
   String ENABLE_XOP = "com.sun.xml.internal.bind.XOP";
   String SUBCLASS_REPLACEMENTS = "com.sun.xml.internal.bind.subclassReplacements";
   String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.internal.bind.XmlAccessorFactory";
   String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";

   Marshaller createMarshaller() throws JAXBException;

   Unmarshaller createUnmarshaller() throws JAXBException;

   JAXBContext getJAXBContext();

   Object newWrapperInstace(Class<?> var1) throws InstantiationException, IllegalAccessException;

   boolean hasSwaRef();

   @Nullable
   QName getElementName(@NotNull Object var1) throws JAXBException;

   @Nullable
   QName getElementName(@NotNull Class var1) throws JAXBException;

   XMLBridge createBridge(@NotNull TypeInfo var1);

   XMLBridge createFragmentBridge();

   <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> var1, String var2, String var3) throws JAXBException;

   @NotNull
   List<String> getKnownNamespaceURIs();

   void generateSchema(@NotNull SchemaOutputResolver var1) throws IOException;

   QName getTypeName(@NotNull TypeInfo var1);

   @NotNull
   String getBuildId();
}
