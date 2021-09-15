package com.sun.xml.internal.ws.api.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;

public abstract class XMLStreamWriterFactory {
   private static final Logger LOGGER = Logger.getLogger(XMLStreamWriterFactory.class.getName());
   private static volatile ContextClassloaderLocal<XMLStreamWriterFactory> writerFactory = new ContextClassloaderLocal<XMLStreamWriterFactory>() {
      protected XMLStreamWriterFactory initialValue() {
         XMLOutputFactory xof = null;
         if (Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".woodstox")) {
            try {
               xof = (XMLOutputFactory)Class.forName("com.ctc.wstx.stax.WstxOutputFactory").newInstance();
            } catch (Exception var6) {
            }
         }

         if (xof == null) {
            xof = XMLOutputFactory.newInstance();
         }

         XMLStreamWriterFactory f = null;
         if (!Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".noPool")) {
            try {
               Class<?> clazz = xof.createXMLStreamWriter((Writer)(new StringWriter())).getClass();
               if (clazz.getName().startsWith("com.sun.xml.internal.stream.")) {
                  f = new XMLStreamWriterFactory.Zephyr(xof, clazz);
               }
            } catch (XMLStreamException var4) {
               Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, (String)null, (Throwable)var4);
            } catch (NoSuchMethodException var5) {
               Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, (String)null, (Throwable)var5);
            }
         }

         if (f == null && xof.getClass().getName().equals("com.ctc.wstx.stax.WstxOutputFactory")) {
            f = new XMLStreamWriterFactory.NoLock(xof);
         }

         if (f == null) {
            f = new XMLStreamWriterFactory.Default(xof);
         }

         if (XMLStreamWriterFactory.LOGGER.isLoggable(Level.FINE)) {
            XMLStreamWriterFactory.LOGGER.log(Level.FINE, "XMLStreamWriterFactory instance is = {0}", f);
         }

         return (XMLStreamWriterFactory)f;
      }
   };

   public abstract XMLStreamWriter doCreate(OutputStream var1);

   public abstract XMLStreamWriter doCreate(OutputStream var1, String var2);

   public abstract void doRecycle(XMLStreamWriter var1);

   public static void recycle(XMLStreamWriter r) {
      get().doRecycle(r);
   }

   @NotNull
   public static XMLStreamWriterFactory get() {
      return (XMLStreamWriterFactory)writerFactory.get();
   }

   public static void set(@NotNull XMLStreamWriterFactory f) {
      if (f == null) {
         throw new IllegalArgumentException();
      } else {
         writerFactory.set(f);
      }
   }

   public static XMLStreamWriter create(OutputStream out) {
      return get().doCreate(out);
   }

   public static XMLStreamWriter create(OutputStream out, String encoding) {
      return get().doCreate(out, encoding);
   }

   /** @deprecated */
   public static XMLStreamWriter createXMLStreamWriter(OutputStream out) {
      return create(out);
   }

   /** @deprecated */
   public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) {
      return create(out, encoding);
   }

   /** @deprecated */
   public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding, boolean declare) {
      return create(out, encoding);
   }

   public static class HasEncodingWriter extends XMLStreamWriterFilter implements HasEncoding {
      private final String encoding;

      HasEncodingWriter(XMLStreamWriter writer, String encoding) {
         super(writer);
         this.encoding = encoding;
      }

      public String getEncoding() {
         return this.encoding;
      }

      public XMLStreamWriter getWriter() {
         return this.writer;
      }
   }

   public static final class NoLock extends XMLStreamWriterFactory {
      private final XMLOutputFactory xof;

      public NoLock(XMLOutputFactory xof) {
         this.xof = xof;
      }

      public XMLStreamWriter doCreate(OutputStream out) {
         return this.doCreate(out, "utf-8");
      }

      public XMLStreamWriter doCreate(OutputStream out, String encoding) {
         try {
            XMLStreamWriter writer = this.xof.createXMLStreamWriter(out, encoding);
            return new XMLStreamWriterFactory.HasEncodingWriter(writer, encoding);
         } catch (XMLStreamException var4) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var4});
         }
      }

      public void doRecycle(XMLStreamWriter r) {
      }
   }

   public static final class Zephyr extends XMLStreamWriterFactory {
      private final XMLOutputFactory xof;
      private final ThreadLocal<XMLStreamWriter> pool;
      private final Method resetMethod;
      private final Method setOutputMethod;
      private final Class zephyrClass;

      public static XMLStreamWriterFactory newInstance(XMLOutputFactory xof) {
         try {
            Class<?> clazz = xof.createXMLStreamWriter((Writer)(new StringWriter())).getClass();
            return !clazz.getName().startsWith("com.sun.xml.internal.stream.") ? null : new XMLStreamWriterFactory.Zephyr(xof, clazz);
         } catch (XMLStreamException var2) {
            return null;
         } catch (NoSuchMethodException var3) {
            return null;
         }
      }

      private Zephyr(XMLOutputFactory xof, Class clazz) throws NoSuchMethodException {
         this.pool = new ThreadLocal();
         this.xof = xof;
         this.zephyrClass = clazz;
         this.setOutputMethod = clazz.getMethod("setOutput", StreamResult.class, String.class);
         this.resetMethod = clazz.getMethod("reset");
      }

      @Nullable
      private XMLStreamWriter fetch() {
         XMLStreamWriter sr = (XMLStreamWriter)this.pool.get();
         if (sr == null) {
            return null;
         } else {
            this.pool.set((Object)null);
            return sr;
         }
      }

      public XMLStreamWriter doCreate(OutputStream out) {
         return this.doCreate(out, "UTF-8");
      }

      public XMLStreamWriter doCreate(OutputStream out, String encoding) {
         XMLStreamWriter xsw = this.fetch();
         if (xsw != null) {
            try {
               this.resetMethod.invoke(xsw);
               this.setOutputMethod.invoke(xsw, new StreamResult(out), encoding);
            } catch (IllegalAccessException var6) {
               throw new XMLReaderException("stax.cantCreate", new Object[]{var6});
            } catch (InvocationTargetException var7) {
               throw new XMLReaderException("stax.cantCreate", new Object[]{var7});
            }
         } else {
            try {
               xsw = this.xof.createXMLStreamWriter(out, encoding);
            } catch (XMLStreamException var5) {
               throw new XMLReaderException("stax.cantCreate", new Object[]{var5});
            }
         }

         return new XMLStreamWriterFactory.HasEncodingWriter(xsw, encoding);
      }

      public void doRecycle(XMLStreamWriter r) {
         if (r instanceof XMLStreamWriterFactory.HasEncodingWriter) {
            r = ((XMLStreamWriterFactory.HasEncodingWriter)r).getWriter();
         }

         if (this.zephyrClass.isInstance(r)) {
            try {
               r.close();
            } catch (XMLStreamException var3) {
               throw new WebServiceException(var3);
            }

            this.pool.set(r);
         }

         if (r instanceof XMLStreamWriterFactory.RecycleAware) {
            ((XMLStreamWriterFactory.RecycleAware)r).onRecycled();
         }

      }

      // $FF: synthetic method
      Zephyr(XMLOutputFactory x0, Class x1, Object x2) throws NoSuchMethodException {
         this(x0, x1);
      }
   }

   public static final class Default extends XMLStreamWriterFactory {
      private final XMLOutputFactory xof;

      public Default(XMLOutputFactory xof) {
         this.xof = xof;
      }

      public XMLStreamWriter doCreate(OutputStream out) {
         return this.doCreate(out, "UTF-8");
      }

      public synchronized XMLStreamWriter doCreate(OutputStream out, String encoding) {
         try {
            XMLStreamWriter writer = this.xof.createXMLStreamWriter(out, encoding);
            return new XMLStreamWriterFactory.HasEncodingWriter(writer, encoding);
         } catch (XMLStreamException var4) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var4});
         }
      }

      public void doRecycle(XMLStreamWriter r) {
      }
   }

   public interface RecycleAware {
      void onRecycled();
   }
}
