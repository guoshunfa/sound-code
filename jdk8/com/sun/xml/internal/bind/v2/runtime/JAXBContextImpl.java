package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Pool;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.api.ErrorListener;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.txw2.output.ResultFactory;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBContextImpl extends JAXBRIContext {
   private final Map<TypeReference, Bridge> bridges;
   private static DocumentBuilder db;
   private final QNameMap<JaxBeanInfo> rootMap;
   private final HashMap<QName, JaxBeanInfo> typeMap;
   private final Map<Class, JaxBeanInfo> beanInfoMap;
   protected Map<RuntimeTypeInfo, JaxBeanInfo> beanInfos;
   private final Map<Class, Map<QName, ElementBeanInfoImpl>> elements;
   public final Pool<Marshaller> marshallerPool;
   public final Pool<Unmarshaller> unmarshallerPool;
   public NameBuilder nameBuilder;
   public final NameList nameList;
   private final String defaultNsUri;
   private final Class[] classes;
   protected final boolean c14nSupport;
   public final boolean xmlAccessorFactorySupport;
   public final boolean allNillable;
   public final boolean retainPropertyInfo;
   public final boolean supressAccessorWarnings;
   public final boolean improvedXsiTypeHandling;
   public final boolean disableSecurityProcessing;
   private WeakReference<RuntimeTypeInfoSet> typeInfoSetCache;
   @NotNull
   private RuntimeAnnotationReader annotationReader;
   private boolean hasSwaRef;
   @NotNull
   private final Map<Class, Class> subclassReplacements;
   public final boolean fastBoot;
   private Set<XmlNs> xmlNsSet;
   private Encoded[] utf8nameTable;
   private static final Comparator<QName> QNAME_COMPARATOR = new Comparator<QName>() {
      public int compare(QName lhs, QName rhs) {
         int r = lhs.getLocalPart().compareTo(rhs.getLocalPart());
         return r != 0 ? r : lhs.getNamespaceURI().compareTo(rhs.getNamespaceURI());
      }
   };

   public Set<XmlNs> getXmlNsSet() {
      return this.xmlNsSet;
   }

   private JAXBContextImpl(JAXBContextImpl.JAXBContextBuilder builder) throws JAXBException {
      this.bridges = new LinkedHashMap();
      this.rootMap = new QNameMap();
      this.typeMap = new HashMap();
      this.beanInfoMap = new LinkedHashMap();
      this.beanInfos = new LinkedHashMap();
      this.elements = new LinkedHashMap();
      this.marshallerPool = new Pool.Impl<Marshaller>() {
         @NotNull
         protected Marshaller create() {
            return JAXBContextImpl.this.createMarshaller();
         }
      };
      this.unmarshallerPool = new Pool.Impl<Unmarshaller>() {
         @NotNull
         protected Unmarshaller create() {
            return JAXBContextImpl.this.createUnmarshaller();
         }
      };
      this.nameBuilder = new NameBuilder();
      this.xmlNsSet = null;
      this.defaultNsUri = builder.defaultNsUri;
      this.retainPropertyInfo = builder.retainPropertyInfo;
      this.annotationReader = builder.annotationReader;
      this.subclassReplacements = builder.subclassReplacements;
      this.c14nSupport = builder.c14nSupport;
      this.classes = builder.classes;
      this.xmlAccessorFactorySupport = builder.xmlAccessorFactorySupport;
      this.allNillable = builder.allNillable;
      this.supressAccessorWarnings = builder.supressAccessorWarnings;
      this.improvedXsiTypeHandling = builder.improvedXsiTypeHandling;
      this.disableSecurityProcessing = builder.disableSecurityProcessing;
      Collection typeRefs = builder.typeRefs;

      boolean fastB;
      try {
         fastB = Boolean.getBoolean(JAXBContextImpl.class.getName() + ".fastBoot");
      } catch (SecurityException var14) {
         fastB = false;
      }

      this.fastBoot = fastB;
      RuntimeTypeInfoSet typeSet = this.getTypeInfoSet();
      this.elements.put((Object)null, new LinkedHashMap());
      Iterator var5 = RuntimeBuiltinLeafInfoImpl.builtinBeanInfos.iterator();

      Iterator var8;
      QName qn;
      while(var5.hasNext()) {
         RuntimeBuiltinLeafInfo leaf = (RuntimeBuiltinLeafInfo)var5.next();
         LeafBeanInfoImpl<?> bi = new LeafBeanInfoImpl(this, leaf);
         this.beanInfoMap.put(leaf.getClazz(), bi);
         var8 = bi.getTypeNames().iterator();

         while(var8.hasNext()) {
            qn = (QName)var8.next();
            this.typeMap.put(qn, bi);
         }
      }

      var5 = typeSet.enums().values().iterator();

      JaxBeanInfo bi;
      while(var5.hasNext()) {
         RuntimeEnumLeafInfo e = (RuntimeEnumLeafInfo)var5.next();
         bi = this.getOrCreate(e);
         var8 = bi.getTypeNames().iterator();

         while(var8.hasNext()) {
            qn = (QName)var8.next();
            this.typeMap.put(qn, bi);
         }

         if (e.isElement()) {
            this.rootMap.put((QName)e.getElementName(), bi);
         }
      }

      var5 = typeSet.arrays().values().iterator();

      while(var5.hasNext()) {
         RuntimeArrayInfo a = (RuntimeArrayInfo)var5.next();
         bi = this.getOrCreate(a);
         var8 = bi.getTypeNames().iterator();

         while(var8.hasNext()) {
            qn = (QName)var8.next();
            this.typeMap.put(qn, bi);
         }
      }

      var5 = typeSet.beans().entrySet().iterator();

      Map.Entry e;
      while(var5.hasNext()) {
         e = (Map.Entry)var5.next();
         ClassBeanInfoImpl<?> bi = this.getOrCreate((RuntimeClassInfo)e.getValue());
         XmlSchema xs = (XmlSchema)this.annotationReader.getPackageAnnotation(XmlSchema.class, e.getKey(), (Locatable)null);
         if (xs != null && xs.xmlns() != null && xs.xmlns().length > 0) {
            if (this.xmlNsSet == null) {
               this.xmlNsSet = new HashSet();
            }

            this.xmlNsSet.addAll(Arrays.asList(xs.xmlns()));
         }

         if (bi.isElement()) {
            this.rootMap.put((QName)((RuntimeClassInfo)e.getValue()).getElementName(), bi);
         }

         Iterator var29 = bi.getTypeNames().iterator();

         while(var29.hasNext()) {
            QName qn = (QName)var29.next();
            this.typeMap.put(qn, bi);
         }
      }

      RuntimeElementInfo n;
      ElementBeanInfoImpl bi;
      Object m;
      for(var5 = typeSet.getAllElements().iterator(); var5.hasNext(); ((Map)m).put(n.getElementName(), bi)) {
         n = (RuntimeElementInfo)var5.next();
         bi = this.getOrCreate(n);
         if (n.getScope() == null) {
            this.rootMap.put((QName)n.getElementName(), bi);
         }

         RuntimeClassInfo scope = n.getScope();
         Class scopeClazz = scope == null ? null : (Class)scope.getClazz();
         m = (Map)this.elements.get(scopeClazz);
         if (m == null) {
            m = new LinkedHashMap();
            this.elements.put(scopeClazz, m);
         }
      }

      this.beanInfoMap.put(JAXBElement.class, new ElementBeanInfoImpl(this));
      this.beanInfoMap.put(CompositeStructure.class, new CompositeStructureBeanInfo(this));
      this.getOrCreate((RuntimeTypeInfo)typeSet.getAnyTypeInfo());
      var5 = this.beanInfos.values().iterator();

      while(var5.hasNext()) {
         JaxBeanInfo bi = (JaxBeanInfo)var5.next();
         bi.link(this);
      }

      var5 = RuntimeUtil.primitiveToBox.entrySet().iterator();

      while(var5.hasNext()) {
         e = (Map.Entry)var5.next();
         this.beanInfoMap.put(e.getKey(), this.beanInfoMap.get(e.getValue()));
      }

      Navigator<Type, Class, Field, Method> nav = typeSet.getNavigator();

      Object bridge;
      Iterator var24;
      TypeReference tr;
      for(var24 = typeRefs.iterator(); var24.hasNext(); this.bridges.put(tr, bridge)) {
         tr = (TypeReference)var24.next();
         XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)tr.get(XmlJavaTypeAdapter.class);
         Adapter<Type, Class> a = null;
         XmlList xl = (XmlList)tr.get(XmlList.class);
         Class erasedType = (Class)nav.erasure(tr.type);
         if (xjta != null) {
            a = new Adapter(xjta.value(), nav);
         }

         if (tr.get(XmlAttachmentRef.class) != null) {
            a = new Adapter(SwaRefAdapter.class, nav);
            this.hasSwaRef = true;
         }

         if (a != null) {
            erasedType = (Class)nav.erasure(a.defaultType);
         }

         Name name = this.nameBuilder.createElementName(tr.tagName);
         if (xl == null) {
            bridge = new BridgeImpl(this, name, this.getBeanInfo(erasedType, true), tr);
         } else {
            bridge = new BridgeImpl(this, name, new ValueListBeanInfoImpl(this, erasedType), tr);
         }

         if (a != null) {
            bridge = new BridgeAdapter((InternalBridge)bridge, (Class)a.adapterType);
         }
      }

      this.nameList = this.nameBuilder.conclude();
      var24 = this.beanInfos.values().iterator();

      while(var24.hasNext()) {
         bi = (JaxBeanInfo)var24.next();
         bi.wrapUp();
      }

      this.nameBuilder = null;
      this.beanInfos = null;
   }

   public boolean hasSwaRef() {
      return this.hasSwaRef;
   }

   public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
      try {
         return this.getTypeInfoSet();
      } catch (IllegalAnnotationsException var2) {
         throw new AssertionError(var2);
      }
   }

   public RuntimeTypeInfoSet getTypeInfoSet() throws IllegalAnnotationsException {
      if (this.typeInfoSetCache != null) {
         RuntimeTypeInfoSet r = (RuntimeTypeInfoSet)this.typeInfoSetCache.get();
         if (r != null) {
            return r;
         }
      }

      RuntimeModelBuilder builder = new RuntimeModelBuilder(this, this.annotationReader, this.subclassReplacements, this.defaultNsUri);
      IllegalAnnotationsException.Builder errorHandler = new IllegalAnnotationsException.Builder();
      builder.setErrorHandler(errorHandler);
      Class[] var3 = this.classes;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Class c = var3[var5];
         if (c != CompositeStructure.class) {
            builder.getTypeInfo(new Ref(c));
         }
      }

      this.hasSwaRef |= builder.hasSwaRef;
      RuntimeTypeInfoSet r = builder.link();
      errorHandler.check();

      assert r != null : "if no error was reported, the link must be a success";

      this.typeInfoSetCache = new WeakReference(r);
      return r;
   }

   public ElementBeanInfoImpl getElement(Class scope, QName name) {
      Map<QName, ElementBeanInfoImpl> m = (Map)this.elements.get(scope);
      if (m != null) {
         ElementBeanInfoImpl bi = (ElementBeanInfoImpl)m.get(name);
         if (bi != null) {
            return bi;
         }
      }

      m = (Map)this.elements.get((Object)null);
      return (ElementBeanInfoImpl)m.get(name);
   }

   private ElementBeanInfoImpl getOrCreate(RuntimeElementInfo rei) {
      JaxBeanInfo bi = (JaxBeanInfo)this.beanInfos.get(rei);
      return bi != null ? (ElementBeanInfoImpl)bi : new ElementBeanInfoImpl(this, rei);
   }

   protected JaxBeanInfo getOrCreate(RuntimeEnumLeafInfo eli) {
      JaxBeanInfo bi = (JaxBeanInfo)this.beanInfos.get(eli);
      if (bi != null) {
         return bi;
      } else {
         JaxBeanInfo bi = new LeafBeanInfoImpl(this, eli);
         this.beanInfoMap.put(bi.jaxbType, bi);
         return bi;
      }
   }

   protected ClassBeanInfoImpl getOrCreate(RuntimeClassInfo ci) {
      ClassBeanInfoImpl bi = (ClassBeanInfoImpl)this.beanInfos.get(ci);
      if (bi != null) {
         return bi;
      } else {
         bi = new ClassBeanInfoImpl(this, ci);
         this.beanInfoMap.put(bi.jaxbType, bi);
         return bi;
      }
   }

   protected JaxBeanInfo getOrCreate(RuntimeArrayInfo ai) {
      JaxBeanInfo abi = (JaxBeanInfo)this.beanInfos.get(ai);
      if (abi != null) {
         return abi;
      } else {
         JaxBeanInfo abi = new ArrayBeanInfoImpl(this, ai);
         this.beanInfoMap.put(ai.getType(), abi);
         return abi;
      }
   }

   public JaxBeanInfo getOrCreate(RuntimeTypeInfo e) {
      if (e instanceof RuntimeElementInfo) {
         return this.getOrCreate((RuntimeElementInfo)e);
      } else if (e instanceof RuntimeClassInfo) {
         return this.getOrCreate((RuntimeClassInfo)e);
      } else if (e instanceof RuntimeLeafInfo) {
         JaxBeanInfo bi = (JaxBeanInfo)this.beanInfos.get(e);

         assert bi != null;

         return bi;
      } else if (e instanceof RuntimeArrayInfo) {
         return this.getOrCreate((RuntimeArrayInfo)e);
      } else if (e.getType() == Object.class) {
         JaxBeanInfo bi = (JaxBeanInfo)this.beanInfoMap.get(Object.class);
         if (bi == null) {
            bi = new AnyTypeBeanInfo(this, e);
            this.beanInfoMap.put(Object.class, bi);
         }

         return (JaxBeanInfo)bi;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final JaxBeanInfo getBeanInfo(Object o) {
      for(Class c = o.getClass(); c != Object.class; c = c.getSuperclass()) {
         JaxBeanInfo bi = (JaxBeanInfo)this.beanInfoMap.get(c);
         if (bi != null) {
            return bi;
         }
      }

      if (o instanceof Element) {
         return (JaxBeanInfo)this.beanInfoMap.get(Object.class);
      } else {
         Class[] var7 = o.getClass().getInterfaces();
         int var8 = var7.length;

         for(int var4 = 0; var4 < var8; ++var4) {
            Class c = var7[var4];
            JaxBeanInfo bi = (JaxBeanInfo)this.beanInfoMap.get(c);
            if (bi != null) {
               return bi;
            }
         }

         return null;
      }
   }

   public final JaxBeanInfo getBeanInfo(Object o, boolean fatal) throws JAXBException {
      JaxBeanInfo bi = this.getBeanInfo(o);
      if (bi != null) {
         return bi;
      } else if (fatal) {
         if (o instanceof Document) {
            throw new JAXBException(Messages.ELEMENT_NEEDED_BUT_FOUND_DOCUMENT.format(o.getClass()));
         } else {
            throw new JAXBException(Messages.UNKNOWN_CLASS.format(o.getClass()));
         }
      } else {
         return null;
      }
   }

   public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz) {
      return (JaxBeanInfo)this.beanInfoMap.get(clazz);
   }

   public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz, boolean fatal) throws JAXBException {
      JaxBeanInfo<T> bi = this.getBeanInfo(clazz);
      if (bi != null) {
         return bi;
      } else if (fatal) {
         throw new JAXBException(clazz.getName() + " is not known to this context");
      } else {
         return null;
      }
   }

   public final Loader selectRootLoader(UnmarshallingContext.State state, TagName tag) {
      JaxBeanInfo beanInfo = (JaxBeanInfo)this.rootMap.get(tag.uri, tag.local);
      return beanInfo == null ? null : beanInfo.getLoader(this, true);
   }

   public JaxBeanInfo getGlobalType(QName name) {
      return (JaxBeanInfo)this.typeMap.get(name);
   }

   public String getNearestTypeName(QName name) {
      String[] all = new String[this.typeMap.size()];
      int i = 0;

      QName qn;
      for(Iterator var4 = this.typeMap.keySet().iterator(); var4.hasNext(); all[i++] = qn.toString()) {
         qn = (QName)var4.next();
         if (qn.getLocalPart().equals(name.getLocalPart())) {
            return qn.toString();
         }
      }

      String nearest = EditDistance.findNearest(name.toString(), all);
      if (EditDistance.editDistance(nearest, name.toString()) > 10) {
         return null;
      } else {
         return nearest;
      }
   }

   public Set<QName> getValidRootNames() {
      Set<QName> r = new TreeSet(QNAME_COMPARATOR);
      Iterator var2 = this.rootMap.entrySet().iterator();

      while(var2.hasNext()) {
         QNameMap.Entry e = (QNameMap.Entry)var2.next();
         r.add(e.createQName());
      }

      return r;
   }

   public synchronized Encoded[] getUTF8NameTable() {
      if (this.utf8nameTable == null) {
         Encoded[] x = new Encoded[this.nameList.localNames.length];

         for(int i = 0; i < x.length; ++i) {
            Encoded e = new Encoded(this.nameList.localNames[i]);
            e.compact();
            x[i] = e;
         }

         this.utf8nameTable = x;
      }

      return this.utf8nameTable;
   }

   public int getNumberOfLocalNames() {
      return this.nameList.localNames.length;
   }

   public int getNumberOfElementNames() {
      return this.nameList.numberOfElementNames;
   }

   public int getNumberOfAttributeNames() {
      return this.nameList.numberOfAttributeNames;
   }

   static Transformer createTransformer(boolean disableSecureProcessing) {
      try {
         SAXTransformerFactory tf = (SAXTransformerFactory)XmlFactory.createTransformerFactory(disableSecureProcessing);
         return tf.newTransformer();
      } catch (TransformerConfigurationException var2) {
         throw new Error(var2);
      }
   }

   public static TransformerHandler createTransformerHandler(boolean disableSecureProcessing) {
      try {
         SAXTransformerFactory tf = (SAXTransformerFactory)XmlFactory.createTransformerFactory(disableSecureProcessing);
         return tf.newTransformerHandler();
      } catch (TransformerConfigurationException var2) {
         throw new Error(var2);
      }
   }

   static Document createDom(boolean disableSecurityProcessing) {
      Class var1 = JAXBContextImpl.class;
      synchronized(JAXBContextImpl.class) {
         if (db == null) {
            try {
               DocumentBuilderFactory dbf = XmlFactory.createDocumentBuilderFactory(disableSecurityProcessing);
               db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException var4) {
               throw new FactoryConfigurationError(var4);
            }
         }

         return db.newDocument();
      }
   }

   public MarshallerImpl createMarshaller() {
      return new MarshallerImpl(this, (AssociationMap)null);
   }

   public UnmarshallerImpl createUnmarshaller() {
      return new UnmarshallerImpl(this, (AssociationMap)null);
   }

   public Validator createValidator() {
      throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED_IN_2_0.format());
   }

   public JAXBIntrospector createJAXBIntrospector() {
      return new JAXBIntrospector() {
         public boolean isElement(Object object) {
            return this.getElementName(object) != null;
         }

         public QName getElementName(Object jaxbElement) {
            try {
               return JAXBContextImpl.this.getElementName(jaxbElement);
            } catch (JAXBException var3) {
               return null;
            }
         }
      };
   }

   private NonElement<Type, Class> getXmlType(RuntimeTypeInfoSet tis, TypeReference tr) {
      if (tr == null) {
         throw new IllegalArgumentException();
      } else {
         XmlJavaTypeAdapter xjta = (XmlJavaTypeAdapter)tr.get(XmlJavaTypeAdapter.class);
         XmlList xl = (XmlList)tr.get(XmlList.class);
         Ref<Type, Class> ref = new Ref(this.annotationReader, tis.getNavigator(), tr.type, xjta, xl);
         return tis.getTypeInfo((Ref)ref);
      }
   }

   public void generateEpisode(Result output) {
      if (output == null) {
         throw new IllegalArgumentException();
      } else {
         this.createSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(output));
      }
   }

   public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
      if (outputResolver == null) {
         throw new IOException(Messages.NULL_OUTPUT_RESOLVER.format());
      } else {
         final SAXParseException[] e = new SAXParseException[1];
         final SAXParseException[] w = new SAXParseException[1];
         this.createSchemaGenerator().write(outputResolver, new ErrorListener() {
            public void error(SAXParseException exception) {
               e[0] = exception;
            }

            public void fatalError(SAXParseException exception) {
               e[0] = exception;
            }

            public void warning(SAXParseException exception) {
               w[0] = exception;
            }

            public void info(SAXParseException exception) {
            }
         });
         IOException x;
         if (e[0] != null) {
            x = new IOException(Messages.FAILED_TO_GENERATE_SCHEMA.format());
            x.initCause(e[0]);
            throw x;
         } else if (w[0] != null) {
            x = new IOException(Messages.ERROR_PROCESSING_SCHEMA.format());
            x.initCause(w[0]);
            throw x;
         }
      }
   }

   private XmlSchemaGenerator<Type, Class, Field, Method> createSchemaGenerator() {
      RuntimeTypeInfoSet tis;
      try {
         tis = this.getTypeInfoSet();
      } catch (IllegalAnnotationsException var7) {
         throw new AssertionError(var7);
      }

      XmlSchemaGenerator<Type, Class, Field, Method> xsdgen = new XmlSchemaGenerator(tis.getNavigator(), tis);
      Set<QName> rootTagNames = new HashSet();
      Iterator var4 = tis.getAllElements().iterator();

      while(var4.hasNext()) {
         RuntimeElementInfo ei = (RuntimeElementInfo)var4.next();
         rootTagNames.add(ei.getElementName());
      }

      var4 = tis.beans().values().iterator();

      while(var4.hasNext()) {
         RuntimeClassInfo ci = (RuntimeClassInfo)var4.next();
         if (ci.isElement()) {
            rootTagNames.add(ci.asElement().getElementName());
         }
      }

      var4 = this.bridges.keySet().iterator();

      while(true) {
         while(true) {
            TypeReference tr;
            do {
               if (!var4.hasNext()) {
                  return xsdgen;
               }

               tr = (TypeReference)var4.next();
            } while(rootTagNames.contains(tr.tagName));

            if (tr.type != Void.TYPE && tr.type != Void.class) {
               if (tr.type != CompositeStructure.class) {
                  NonElement<Type, Class> typeInfo = this.getXmlType(tis, tr);
                  xsdgen.add(tr.tagName, !tis.getNavigator().isPrimitive(tr.type), typeInfo);
               }
            } else {
               xsdgen.add(tr.tagName, false, (NonElement)null);
            }
         }
      }
   }

   public QName getTypeName(TypeReference tr) {
      try {
         NonElement<Type, Class> xt = this.getXmlType(this.getTypeInfoSet(), tr);
         if (xt == null) {
            throw new IllegalArgumentException();
         } else {
            return xt.getTypeName();
         }
      } catch (IllegalAnnotationsException var3) {
         throw new AssertionError(var3);
      }
   }

   public <T> Binder<T> createBinder(Class<T> domType) {
      return domType == Node.class ? this.createBinder() : super.createBinder(domType);
   }

   public Binder<Node> createBinder() {
      return new BinderImpl(this, new DOMScanner());
   }

   public QName getElementName(Object o) throws JAXBException {
      JaxBeanInfo bi = this.getBeanInfo(o, true);
      return !bi.isElement() ? null : new QName(bi.getElementNamespaceURI(o), bi.getElementLocalName(o));
   }

   public QName getElementName(Class o) throws JAXBException {
      JaxBeanInfo bi = this.getBeanInfo(o, true);
      return !bi.isElement() ? null : new QName(bi.getElementNamespaceURI(o), bi.getElementLocalName(o));
   }

   public Bridge createBridge(TypeReference ref) {
      return (Bridge)this.bridges.get(ref);
   }

   @NotNull
   public BridgeContext createBridgeContext() {
      return new BridgeContextImpl(this);
   }

   public RawAccessor getElementPropertyAccessor(Class wrapperBean, String nsUri, String localName) throws JAXBException {
      JaxBeanInfo bi = this.getBeanInfo(wrapperBean, true);
      if (!(bi instanceof ClassBeanInfoImpl)) {
         throw new JAXBException(wrapperBean + " is not a bean");
      } else {
         for(ClassBeanInfoImpl cb = (ClassBeanInfoImpl)bi; cb != null; cb = cb.superClazz) {
            Property[] var6 = cb.properties;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Property p = var6[var8];
               final Accessor acc = p.getElementPropertyAccessor(nsUri, localName);
               if (acc != null) {
                  return new RawAccessor() {
                     public Object get(Object bean) throws AccessorException {
                        return acc.getUnadapted(bean);
                     }

                     public void set(Object bean, Object value) throws AccessorException {
                        acc.setUnadapted(bean, value);
                     }
                  };
               }
            }
         }

         throw new JAXBException(new QName(nsUri, localName) + " is not a valid property on " + wrapperBean);
      }
   }

   public List<String> getKnownNamespaceURIs() {
      return Arrays.asList(this.nameList.namespaceURIs);
   }

   public String getBuildId() {
      Package pkg = this.getClass().getPackage();
      return pkg == null ? null : pkg.getImplementationVersion();
   }

   public String toString() {
      StringBuilder buf = new StringBuilder(Which.which(this.getClass()) + " Build-Id: " + this.getBuildId());
      buf.append("\nClasses known to this context:\n");
      Set<String> names = new TreeSet();
      Iterator var3 = this.beanInfoMap.keySet().iterator();

      while(var3.hasNext()) {
         Class key = (Class)var3.next();
         names.add(key.getName());
      }

      var3 = names.iterator();

      while(var3.hasNext()) {
         String name = (String)var3.next();
         buf.append("  ").append(name).append('\n');
      }

      return buf.toString();
   }

   public String getXMIMEContentType(Object o) {
      JaxBeanInfo bi = this.getBeanInfo(o);
      if (!(bi instanceof ClassBeanInfoImpl)) {
         return null;
      } else {
         ClassBeanInfoImpl cb = (ClassBeanInfoImpl)bi;
         Property[] var4 = cb.properties;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Property p = var4[var6];
            if (p instanceof AttributeProperty) {
               AttributeProperty ap = (AttributeProperty)p;
               if (ap.attName.equals("http://www.w3.org/2005/05/xmlmime", "contentType")) {
                  try {
                     return (String)ap.xacc.print(o);
                  } catch (AccessorException var10) {
                     return null;
                  } catch (SAXException var11) {
                     return null;
                  } catch (ClassCastException var12) {
                     return null;
                  }
               }
            }
         }

         return null;
      }
   }

   public JAXBContextImpl createAugmented(Class<?> clazz) throws JAXBException {
      Class[] newList = new Class[this.classes.length + 1];
      System.arraycopy(this.classes, 0, newList, 0, this.classes.length);
      newList[this.classes.length] = clazz;
      JAXBContextImpl.JAXBContextBuilder builder = new JAXBContextImpl.JAXBContextBuilder(this);
      builder.setClasses(newList);
      return builder.build();
   }

   // $FF: synthetic method
   JAXBContextImpl(JAXBContextImpl.JAXBContextBuilder x0, Object x1) throws JAXBException {
      this(x0);
   }

   public static class JAXBContextBuilder {
      private boolean retainPropertyInfo = false;
      private boolean supressAccessorWarnings = false;
      private String defaultNsUri = "";
      @NotNull
      private RuntimeAnnotationReader annotationReader = new RuntimeInlineAnnotationReader();
      @NotNull
      private Map<Class, Class> subclassReplacements = Collections.emptyMap();
      private boolean c14nSupport = false;
      private Class[] classes;
      private Collection<TypeReference> typeRefs;
      private boolean xmlAccessorFactorySupport = false;
      private boolean allNillable;
      private boolean improvedXsiTypeHandling = true;
      private boolean disableSecurityProcessing = true;

      public JAXBContextBuilder() {
      }

      public JAXBContextBuilder(JAXBContextImpl baseImpl) {
         this.supressAccessorWarnings = baseImpl.supressAccessorWarnings;
         this.retainPropertyInfo = baseImpl.retainPropertyInfo;
         this.defaultNsUri = baseImpl.defaultNsUri;
         this.annotationReader = baseImpl.annotationReader;
         this.subclassReplacements = baseImpl.subclassReplacements;
         this.c14nSupport = baseImpl.c14nSupport;
         this.classes = baseImpl.classes;
         this.typeRefs = baseImpl.bridges.keySet();
         this.xmlAccessorFactorySupport = baseImpl.xmlAccessorFactorySupport;
         this.allNillable = baseImpl.allNillable;
         this.disableSecurityProcessing = baseImpl.disableSecurityProcessing;
      }

      public JAXBContextImpl.JAXBContextBuilder setRetainPropertyInfo(boolean val) {
         this.retainPropertyInfo = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setSupressAccessorWarnings(boolean val) {
         this.supressAccessorWarnings = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setC14NSupport(boolean val) {
         this.c14nSupport = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setXmlAccessorFactorySupport(boolean val) {
         this.xmlAccessorFactorySupport = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setDefaultNsUri(String val) {
         this.defaultNsUri = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setAllNillable(boolean val) {
         this.allNillable = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setClasses(Class[] val) {
         this.classes = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setAnnotationReader(RuntimeAnnotationReader val) {
         this.annotationReader = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setSubclassReplacements(Map<Class, Class> val) {
         this.subclassReplacements = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setTypeRefs(Collection<TypeReference> val) {
         this.typeRefs = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setImprovedXsiTypeHandling(boolean val) {
         this.improvedXsiTypeHandling = val;
         return this;
      }

      public JAXBContextImpl.JAXBContextBuilder setDisableSecurityProcessing(boolean val) {
         this.disableSecurityProcessing = val;
         return this;
      }

      public JAXBContextImpl build() throws JAXBException {
         if (this.defaultNsUri == null) {
            this.defaultNsUri = "";
         }

         if (this.subclassReplacements == null) {
            this.subclassReplacements = Collections.emptyMap();
         }

         if (this.annotationReader == null) {
            this.annotationReader = new RuntimeInlineAnnotationReader();
         }

         if (this.typeRefs == null) {
            this.typeRefs = Collections.emptyList();
         }

         return new JAXBContextImpl(this);
      }
   }
}
