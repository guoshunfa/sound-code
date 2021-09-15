package com.sun.xml.internal.ws.model;

import com.oracle.xmlns.internal.webservices.jaxws_databinding.ExistingAnnotationsType;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaMethod;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaWsdlMappingType;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.ObjectFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExternalMetadataReader extends ReflectAnnotationReader {
   private static final String NAMESPACE_WEBLOGIC_WSEE_DATABINDING = "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding";
   private static final String NAMESPACE_JAXWS_RI_EXTERNAL_METADATA = "http://xmlns.oracle.com/webservices/jaxws-databinding";
   private Map<String, JavaWsdlMappingType> readers = new HashMap();

   public ExternalMetadataReader(Collection<File> files, Collection<String> resourcePaths, ClassLoader classLoader, boolean xsdValidation, boolean disableXmlSecurity) {
      Iterator var6;
      String namespace;
      JavaWsdlMappingType externalMapping;
      if (files != null) {
         var6 = files.iterator();

         while(var6.hasNext()) {
            File file = (File)var6.next();

            try {
               namespace = ExternalMetadataReader.Util.documentRootNamespace(this.newSource(file), disableXmlSecurity);
               externalMapping = this.parseMetadata(xsdValidation, this.newSource(file), namespace, disableXmlSecurity);
               this.readers.put(externalMapping.getJavaTypeName(), externalMapping);
            } catch (Exception var11) {
               throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[]{file.getAbsolutePath()});
            }
         }
      }

      if (resourcePaths != null) {
         var6 = resourcePaths.iterator();

         while(var6.hasNext()) {
            String resourcePath = (String)var6.next();

            try {
               namespace = ExternalMetadataReader.Util.documentRootNamespace(this.newSource(resourcePath, classLoader), disableXmlSecurity);
               externalMapping = this.parseMetadata(xsdValidation, this.newSource(resourcePath, classLoader), namespace, disableXmlSecurity);
               this.readers.put(externalMapping.getJavaTypeName(), externalMapping);
            } catch (Exception var10) {
               throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[]{resourcePath});
            }
         }
      }

   }

   private StreamSource newSource(String resourcePath, ClassLoader classLoader) {
      InputStream is = classLoader.getResourceAsStream(resourcePath);
      return new StreamSource(is);
   }

   private JavaWsdlMappingType parseMetadata(boolean xsdValidation, StreamSource source, String namespace, boolean disableXmlSecurity) throws JAXBException, IOException, TransformerException {
      if ("http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding".equals(namespace)) {
         return ExternalMetadataReader.Util.transformAndRead(source, disableXmlSecurity);
      } else if ("http://xmlns.oracle.com/webservices/jaxws-databinding".equals(namespace)) {
         return ExternalMetadataReader.Util.read(source, xsdValidation, disableXmlSecurity);
      } else {
         throw new RuntimeModelerException("runtime.modeler.external.metadata.unsupported.schema", new Object[]{namespace, Arrays.asList("http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding", "http://xmlns.oracle.com/webservices/jaxws-databinding").toString()});
      }
   }

   private StreamSource newSource(File file) {
      try {
         return new StreamSource(new FileInputStream(file));
      } catch (FileNotFoundException var3) {
         throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[]{file.getAbsolutePath()});
      }
   }

   public <A extends Annotation> A getAnnotation(Class<A> annType, Class<?> cls) {
      JavaWsdlMappingType r = this.reader(cls);
      return r == null ? super.getAnnotation(annType, cls) : (Annotation)ExternalMetadataReader.Util.annotation(r, annType);
   }

   private JavaWsdlMappingType reader(Class<?> cls) {
      return (JavaWsdlMappingType)this.readers.get(cls.getName());
   }

   Annotation[] getAnnotations(List<Object> objects) {
      ArrayList<Annotation> list = new ArrayList();
      Iterator var3 = objects.iterator();

      while(var3.hasNext()) {
         Object a = var3.next();
         if (Annotation.class.isInstance(a)) {
            list.add(Annotation.class.cast(a));
         }
      }

      return (Annotation[])list.toArray(new Annotation[list.size()]);
   }

   public Annotation[] getAnnotations(final Class<?> c) {
      ExternalMetadataReader.Merger<Annotation[]> merger = new ExternalMetadataReader.Merger<Annotation[]>(this.reader(c)) {
         Annotation[] reflection() {
            return ExternalMetadataReader.super.getAnnotations(c);
         }

         Annotation[] external() {
            return ExternalMetadataReader.this.getAnnotations(this.reader.getClassAnnotation());
         }
      };
      return (Annotation[])merger.merge();
   }

   public Annotation[] getAnnotations(final Method m) {
      ExternalMetadataReader.Merger<Annotation[]> merger = new ExternalMetadataReader.Merger<Annotation[]>(this.reader(m.getDeclaringClass())) {
         Annotation[] reflection() {
            return ExternalMetadataReader.super.getAnnotations(m);
         }

         Annotation[] external() {
            JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
            return jm == null ? new Annotation[0] : ExternalMetadataReader.this.getAnnotations(jm.getMethodAnnotation());
         }
      };
      return (Annotation[])merger.merge();
   }

   public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
      ExternalMetadataReader.Merger<Annotation> merger = new ExternalMetadataReader.Merger<Annotation>(this.reader(m.getDeclaringClass())) {
         Annotation reflection() {
            return ExternalMetadataReader.super.getAnnotation(annType, m);
         }

         Annotation external() {
            JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
            return (Annotation)ExternalMetadataReader.Util.annotation(jm, annType);
         }
      };
      return (Annotation)merger.merge();
   }

   public Annotation[][] getParameterAnnotations(final Method m) {
      ExternalMetadataReader.Merger<Annotation[][]> merger = new ExternalMetadataReader.Merger<Annotation[][]>(this.reader(m.getDeclaringClass())) {
         Annotation[][] reflection() {
            return ExternalMetadataReader.super.getParameterAnnotations(m);
         }

         Annotation[][] external() {
            JavaMethod jm = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
            Annotation[][] a = m.getParameterAnnotations();

            for(int i = 0; i < m.getParameterTypes().length; ++i) {
               if (jm != null) {
                  JavaParam jp = (JavaParam)jm.getJavaParams().getJavaParam().get(i);
                  a[i] = ExternalMetadataReader.this.getAnnotations(jp.getParamAnnotation());
               }
            }

            return a;
         }
      };
      return (Annotation[][])merger.merge();
   }

   public void getProperties(Map<String, Object> prop, Class<?> cls) {
      JavaWsdlMappingType r = this.reader(cls);
      if (r == null || ExistingAnnotationsType.MERGE.equals(r.getExistingAnnotations())) {
         super.getProperties(prop, cls);
      }

   }

   public void getProperties(Map<String, Object> prop, Method m) {
      JavaWsdlMappingType r = this.reader(m.getDeclaringClass());
      if (r == null || ExistingAnnotationsType.MERGE.equals(r.getExistingAnnotations())) {
         super.getProperties(prop, m);
      }

      if (r != null) {
         JavaMethod jm = this.getJavaMethod(m, r);
         Element[] e = ExternalMetadataReader.Util.annotation(jm);
         prop.put("eclipselink-oxm-xml.xml-element", this.findXmlElement(e));
      }

   }

   public void getProperties(Map<String, Object> prop, Method m, int pos) {
      JavaWsdlMappingType r = this.reader(m.getDeclaringClass());
      if (r == null || ExistingAnnotationsType.MERGE.equals(r.getExistingAnnotations())) {
         super.getProperties(prop, m, pos);
      }

      if (r != null) {
         JavaMethod jm = this.getJavaMethod(m, r);
         if (jm == null) {
            return;
         }

         JavaParam jp = (JavaParam)jm.getJavaParams().getJavaParam().get(pos);
         Element[] e = ExternalMetadataReader.Util.annotation(jp);
         prop.put("eclipselink-oxm-xml.xml-element", this.findXmlElement(e));
      }

   }

   JavaMethod getJavaMethod(Method method, JavaWsdlMappingType r) {
      JavaWsdlMappingType.JavaMethods javaMethods = r.getJavaMethods();
      if (javaMethods == null) {
         return null;
      } else {
         List<JavaMethod> sameName = new ArrayList();
         Iterator var5 = javaMethods.getJavaMethod().iterator();

         while(var5.hasNext()) {
            JavaMethod jm = (JavaMethod)var5.next();
            if (method.getName().equals(jm.getName())) {
               sameName.add(jm);
            }
         }

         if (sameName.isEmpty()) {
            return null;
         } else if (sameName.size() == 1) {
            return (JavaMethod)sameName.get(0);
         } else {
            Class<?>[] argCls = method.getParameterTypes();
            Iterator var13 = sameName.iterator();

            JavaMethod jm;
            int count;
            do {
               JavaMethod.JavaParams params;
               do {
                  do {
                     do {
                        if (!var13.hasNext()) {
                           return null;
                        }

                        jm = (JavaMethod)var13.next();
                        params = jm.getJavaParams();
                     } while(params == null);
                  } while(params.getJavaParam() == null);
               } while(params.getJavaParam().size() != argCls.length);

               count = 0;

               for(int i = 0; i < argCls.length; ++i) {
                  JavaParam jp = (JavaParam)params.getJavaParam().get(i);
                  if (argCls[i].getName().equals(jp.getJavaType())) {
                     ++count;
                  }
               }
            } while(count != argCls.length);

            return jm;
         }
      }
   }

   Element findXmlElement(Element[] xa) {
      if (xa == null) {
         return null;
      } else {
         Element[] var2 = xa;
         int var3 = xa.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Element e = var2[var4];
            if (e.getLocalName().equals("java-type")) {
               return e;
            }

            if (e.getLocalName().equals("xml-element")) {
               return e;
            }
         }

         return null;
      }
   }

   static class Util {
      private static final String DATABINDING_XSD = "jaxws-databinding.xsd";
      private static final String TRANSLATE_NAMESPACES_XSL = "jaxws-databinding-translate-namespaces.xml";
      static Schema schema;
      static JAXBContext jaxbContext;

      private static URL getResource() {
         ClassLoader classLoader = ExternalMetadataReader.Util.class.getClassLoader();
         return classLoader != null ? classLoader.getResource("jaxws-databinding.xsd") : ClassLoader.getSystemResource("jaxws-databinding.xsd");
      }

      private static JAXBContext createJaxbContext(boolean disableXmlSecurity) {
         Class[] cls = new Class[]{ObjectFactory.class};

         try {
            if (disableXmlSecurity) {
               Map<String, Object> properties = new HashMap();
               properties.put("com.sun.xml.internal.bind.disableXmlSecurity", disableXmlSecurity);
               return JAXBContext.newInstance((Class[])cls, (Map)properties);
            } else {
               return JAXBContext.newInstance(cls);
            }
         } catch (JAXBException var3) {
            var3.printStackTrace();
            return null;
         }
      }

      public static JavaWsdlMappingType read(Source src, boolean xsdValidation, boolean disableXmlSecurity) throws IOException, JAXBException {
         JAXBContext ctx = jaxbContext(disableXmlSecurity);

         try {
            Unmarshaller um = ctx.createUnmarshaller();
            if (xsdValidation) {
               if (schema == null) {
               }

               um.setSchema(schema);
            }

            Object o = um.unmarshal(src);
            return getJavaWsdlMapping(o);
         } catch (JAXBException var9) {
            URL url = new URL(src.getSystemId());
            Source s = new StreamSource(url.openStream());
            Unmarshaller um = ctx.createUnmarshaller();
            if (xsdValidation) {
               if (schema == null) {
               }

               um.setSchema(schema);
            }

            Object o = um.unmarshal((Source)s);
            return getJavaWsdlMapping(o);
         }
      }

      private static JAXBContext jaxbContext(boolean disableXmlSecurity) {
         return disableXmlSecurity ? createJaxbContext(true) : jaxbContext;
      }

      public static JavaWsdlMappingType transformAndRead(Source src, boolean disableXmlSecurity) throws TransformerException, JAXBException {
         Source xsl = new StreamSource(ExternalMetadataReader.Util.class.getResourceAsStream("jaxws-databinding-translate-namespaces.xml"));
         JAXBResult result = new JAXBResult(jaxbContext(disableXmlSecurity));
         TransformerFactory tf = XmlUtil.newTransformerFactory(!disableXmlSecurity);
         Transformer transformer = tf.newTemplates(xsl).newTransformer();
         transformer.transform(src, result);
         return getJavaWsdlMapping(result.getResult());
      }

      static JavaWsdlMappingType getJavaWsdlMapping(Object o) {
         Object val = o instanceof JAXBElement ? ((JAXBElement)o).getValue() : o;
         return val instanceof JavaWsdlMappingType ? (JavaWsdlMappingType)val : null;
      }

      static <T> T findInstanceOf(Class<T> type, List<Object> objects) {
         Iterator var2 = objects.iterator();

         Object o;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            o = var2.next();
         } while(!type.isInstance(o));

         return type.cast(o);
      }

      public static <T> T annotation(JavaWsdlMappingType jwse, Class<T> anntype) {
         return jwse != null && jwse.getClassAnnotation() != null ? findInstanceOf(anntype, jwse.getClassAnnotation()) : null;
      }

      public static <T> T annotation(JavaMethod jm, Class<T> anntype) {
         return jm != null && jm.getMethodAnnotation() != null ? findInstanceOf(anntype, jm.getMethodAnnotation()) : null;
      }

      public static <T> T annotation(JavaParam jp, Class<T> anntype) {
         return jp != null && jp.getParamAnnotation() != null ? findInstanceOf(anntype, jp.getParamAnnotation()) : null;
      }

      public static Element[] annotation(JavaMethod jm) {
         return jm != null && jm.getMethodAnnotation() != null ? findElements(jm.getMethodAnnotation()) : null;
      }

      public static Element[] annotation(JavaParam jp) {
         return jp != null && jp.getParamAnnotation() != null ? findElements(jp.getParamAnnotation()) : null;
      }

      private static Element[] findElements(List<Object> objects) {
         List<Element> elems = new ArrayList();
         Iterator var2 = objects.iterator();

         while(var2.hasNext()) {
            Object o = var2.next();
            if (o instanceof Element) {
               elems.add((Element)o);
            }
         }

         return (Element[])elems.toArray(new Element[elems.size()]);
      }

      static String documentRootNamespace(Source src, boolean disableXmlSecurity) throws XMLStreamException {
         XMLInputFactory factory = XmlUtil.newXMLInputFactory(!disableXmlSecurity);
         XMLStreamReader streamReader = factory.createXMLStreamReader(src);
         XMLStreamReaderUtil.nextElementContent(streamReader);
         String namespaceURI = streamReader.getName().getNamespaceURI();
         XMLStreamReaderUtil.close(streamReader);
         return namespaceURI;
      }

      static {
         SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

         try {
            URL xsdUrl = getResource();
            if (xsdUrl != null) {
               schema = sf.newSchema(xsdUrl);
            }
         } catch (SAXException var2) {
         }

         jaxbContext = createJaxbContext(false);
      }
   }

   abstract static class Merger<T> {
      JavaWsdlMappingType reader;

      Merger(JavaWsdlMappingType r) {
         this.reader = r;
      }

      abstract T reflection();

      abstract T external();

      T merge() {
         T reflection = this.reflection();
         if (this.reader == null) {
            return reflection;
         } else {
            T external = this.external();
            if (!ExistingAnnotationsType.MERGE.equals(this.reader.getExistingAnnotations())) {
               return external;
            } else if (reflection instanceof Annotation) {
               return this.doMerge((Annotation)reflection, (Annotation)external);
            } else {
               return reflection instanceof Annotation[][] ? this.doMerge((Annotation[][])((Annotation[][])reflection), (Annotation[][])((Annotation[][])external)) : this.doMerge((Annotation[])((Annotation[])reflection), (Annotation[])((Annotation[])external));
            }
         }
      }

      private Annotation doMerge(Annotation reflection, Annotation external) {
         return external != null ? external : reflection;
      }

      private Annotation[][] doMerge(Annotation[][] reflection, Annotation[][] external) {
         for(int i = 0; i < reflection.length; ++i) {
            reflection[i] = this.doMerge(reflection[i], external.length > i ? external[i] : null);
         }

         return reflection;
      }

      private Annotation[] doMerge(Annotation[] annotations, Annotation[] externalAnnotations) {
         HashMap<String, Annotation> mergeMap = new HashMap();
         Annotation[] var4;
         int size;
         int var6;
         Annotation externalAnnotation;
         if (annotations != null) {
            var4 = annotations;
            size = annotations.length;

            for(var6 = 0; var6 < size; ++var6) {
               externalAnnotation = var4[var6];
               mergeMap.put(externalAnnotation.annotationType().getName(), externalAnnotation);
            }
         }

         if (externalAnnotations != null) {
            var4 = externalAnnotations;
            size = externalAnnotations.length;

            for(var6 = 0; var6 < size; ++var6) {
               externalAnnotation = var4[var6];
               mergeMap.put(externalAnnotation.annotationType().getName(), externalAnnotation);
            }
         }

         Collection<Annotation> values = mergeMap.values();
         size = values.size();
         return size == 0 ? null : (Annotation[])values.toArray(new Annotation[size]);
      }
   }
}
