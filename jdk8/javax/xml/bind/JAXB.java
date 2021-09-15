package javax.xml.bind;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class JAXB {
   private static volatile WeakReference<JAXB.Cache> cache;

   private JAXB() {
   }

   private static <T> JAXBContext getContext(Class<T> type) throws JAXBException {
      WeakReference<JAXB.Cache> c = cache;
      JAXB.Cache d;
      if (c != null) {
         d = (JAXB.Cache)c.get();
         if (d != null && d.type == type) {
            return d.context;
         }
      }

      d = new JAXB.Cache(type);
      cache = new WeakReference(d);
      return d.context;
   }

   public static <T> T unmarshal(File xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal((Source)(new StreamSource(xml)), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      }
   }

   public static <T> T unmarshal(URL xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      } catch (IOException var4) {
         throw new DataBindingException(var4);
      }
   }

   public static <T> T unmarshal(URI xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      } catch (IOException var4) {
         throw new DataBindingException(var4);
      }
   }

   public static <T> T unmarshal(String xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      } catch (IOException var4) {
         throw new DataBindingException(var4);
      }
   }

   public static <T> T unmarshal(InputStream xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      } catch (IOException var4) {
         throw new DataBindingException(var4);
      }
   }

   public static <T> T unmarshal(Reader xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      } catch (IOException var4) {
         throw new DataBindingException(var4);
      }
   }

   public static <T> T unmarshal(Source xml, Class<T> type) {
      try {
         JAXBElement<T> item = getContext(type).createUnmarshaller().unmarshal(toSource(xml), type);
         return item.getValue();
      } catch (JAXBException var3) {
         throw new DataBindingException(var3);
      } catch (IOException var4) {
         throw new DataBindingException(var4);
      }
   }

   private static Source toSource(Object xml) throws IOException {
      if (xml == null) {
         throw new IllegalArgumentException("no XML is given");
      } else {
         if (xml instanceof String) {
            try {
               xml = new URI((String)xml);
            } catch (URISyntaxException var2) {
               xml = new File((String)xml);
            }
         }

         if (xml instanceof File) {
            File file = (File)xml;
            return new StreamSource(file);
         } else {
            if (xml instanceof URI) {
               URI uri = (URI)xml;
               xml = uri.toURL();
            }

            if (xml instanceof URL) {
               URL url = (URL)xml;
               return new StreamSource(url.toExternalForm());
            } else if (xml instanceof InputStream) {
               InputStream in = (InputStream)xml;
               return new StreamSource(in);
            } else if (xml instanceof Reader) {
               Reader r = (Reader)xml;
               return new StreamSource(r);
            } else if (xml instanceof Source) {
               return (Source)xml;
            } else {
               throw new IllegalArgumentException("I don't understand how to handle " + xml.getClass());
            }
         }
      }
   }

   public static void marshal(Object jaxbObject, File xml) {
      _marshal(jaxbObject, xml);
   }

   public static void marshal(Object jaxbObject, URL xml) {
      _marshal(jaxbObject, xml);
   }

   public static void marshal(Object jaxbObject, URI xml) {
      _marshal(jaxbObject, xml);
   }

   public static void marshal(Object jaxbObject, String xml) {
      _marshal(jaxbObject, xml);
   }

   public static void marshal(Object jaxbObject, OutputStream xml) {
      _marshal(jaxbObject, xml);
   }

   public static void marshal(Object jaxbObject, Writer xml) {
      _marshal(jaxbObject, xml);
   }

   public static void marshal(Object jaxbObject, Result xml) {
      _marshal(jaxbObject, xml);
   }

   private static void _marshal(Object jaxbObject, Object xml) {
      try {
         JAXBContext context;
         if (jaxbObject instanceof JAXBElement) {
            context = getContext(((JAXBElement)jaxbObject).getDeclaredType());
         } else {
            Class<?> clazz = jaxbObject.getClass();
            XmlRootElement r = (XmlRootElement)clazz.getAnnotation(XmlRootElement.class);
            context = getContext(clazz);
            if (r == null) {
               jaxbObject = new JAXBElement(new QName(inferName(clazz)), clazz, jaxbObject);
            }
         }

         Marshaller m = context.createMarshaller();
         m.setProperty("jaxb.formatted.output", true);
         m.marshal(jaxbObject, toResult(xml));
      } catch (JAXBException var5) {
         throw new DataBindingException(var5);
      } catch (IOException var6) {
         throw new DataBindingException(var6);
      }
   }

   private static String inferName(Class clazz) {
      return Introspector.decapitalize(clazz.getSimpleName());
   }

   private static Result toResult(Object xml) throws IOException {
      if (xml == null) {
         throw new IllegalArgumentException("no XML is given");
      } else {
         if (xml instanceof String) {
            try {
               xml = new URI((String)xml);
            } catch (URISyntaxException var3) {
               xml = new File((String)xml);
            }
         }

         if (xml instanceof File) {
            File file = (File)xml;
            return new StreamResult(file);
         } else {
            if (xml instanceof URI) {
               URI uri = (URI)xml;
               xml = uri.toURL();
            }

            if (xml instanceof URL) {
               URL url = (URL)xml;
               URLConnection con = url.openConnection();
               con.setDoOutput(true);
               con.setDoInput(false);
               con.connect();
               return new StreamResult(con.getOutputStream());
            } else if (xml instanceof OutputStream) {
               OutputStream os = (OutputStream)xml;
               return new StreamResult(os);
            } else if (xml instanceof Writer) {
               Writer w = (Writer)xml;
               return new StreamResult(w);
            } else if (xml instanceof Result) {
               return (Result)xml;
            } else {
               throw new IllegalArgumentException("I don't understand how to handle " + xml.getClass());
            }
         }
      }
   }

   private static final class Cache {
      final Class type;
      final JAXBContext context;

      public Cache(Class type) throws JAXBException {
         this.type = type;
         this.context = JAXBContext.newInstance(type);
      }
   }
}
