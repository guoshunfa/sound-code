package com.sun.xml.internal.ws.api.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.InputSource;

public abstract class XMLStreamReaderFactory {
   private static final Logger LOGGER = Logger.getLogger(XMLStreamReaderFactory.class.getName());
   private static final String CLASS_NAME_OF_WSTXINPUTFACTORY = "com.ctc.wstx.stax.WstxInputFactory";
   private static volatile ContextClassloaderLocal<XMLStreamReaderFactory> streamReader = new ContextClassloaderLocal<XMLStreamReaderFactory>() {
      protected XMLStreamReaderFactory initialValue() {
         XMLInputFactory xif = XMLStreamReaderFactory.getXMLInputFactory();
         XMLStreamReaderFactory f = null;
         if (!XMLStreamReaderFactory.getProperty(XMLStreamReaderFactory.class.getName() + ".noPool")) {
            f = XMLStreamReaderFactory.Zephyr.newInstance(xif);
         }

         if (f == null && xif.getClass().getName().equals("com.ctc.wstx.stax.WstxInputFactory")) {
            f = new XMLStreamReaderFactory.Woodstox(xif);
         }

         if (f == null) {
            f = new XMLStreamReaderFactory.Default();
         }

         if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
            XMLStreamReaderFactory.LOGGER.log(Level.FINE, "XMLStreamReaderFactory instance is = {0}", f);
         }

         return (XMLStreamReaderFactory)f;
      }
   };

   private static XMLInputFactory getXMLInputFactory() {
      XMLInputFactory xif = null;
      if (getProperty(XMLStreamReaderFactory.class.getName() + ".woodstox")) {
         try {
            xif = (XMLInputFactory)Class.forName("com.ctc.wstx.stax.WstxInputFactory").newInstance();
         } catch (Exception var2) {
            if (LOGGER.isLoggable(Level.WARNING)) {
               LOGGER.log(Level.WARNING, (String)StreamingMessages.WOODSTOX_CANT_LOAD("com.ctc.wstx.stax.WstxInputFactory"), (Throwable)var2);
            }
         }
      }

      if (xif == null) {
         xif = XmlUtil.newXMLInputFactory(true);
      }

      xif.setProperty("javax.xml.stream.isNamespaceAware", true);
      xif.setProperty("javax.xml.stream.supportDTD", false);
      xif.setProperty("javax.xml.stream.isCoalescing", true);
      return xif;
   }

   public static void set(XMLStreamReaderFactory f) {
      if (f == null) {
         throw new IllegalArgumentException();
      } else {
         streamReader.set(f);
      }
   }

   public static XMLStreamReaderFactory get() {
      return (XMLStreamReaderFactory)streamReader.get();
   }

   public static XMLStreamReader create(InputSource source, boolean rejectDTDs) {
      try {
         if (source.getCharacterStream() != null) {
            return get().doCreate(source.getSystemId(), source.getCharacterStream(), rejectDTDs);
         } else {
            return source.getByteStream() != null ? get().doCreate(source.getSystemId(), source.getByteStream(), rejectDTDs) : get().doCreate(source.getSystemId(), (new URL(source.getSystemId())).openStream(), rejectDTDs);
         }
      } catch (IOException var3) {
         throw new XMLReaderException("stax.cantCreate", new Object[]{var3});
      }
   }

   public static XMLStreamReader create(@Nullable String systemId, InputStream in, boolean rejectDTDs) {
      return get().doCreate(systemId, in, rejectDTDs);
   }

   public static XMLStreamReader create(@Nullable String systemId, InputStream in, @Nullable String encoding, boolean rejectDTDs) {
      return encoding == null ? create(systemId, in, rejectDTDs) : get().doCreate(systemId, in, encoding, rejectDTDs);
   }

   public static XMLStreamReader create(@Nullable String systemId, Reader reader, boolean rejectDTDs) {
      return get().doCreate(systemId, reader, rejectDTDs);
   }

   public static void recycle(XMLStreamReader r) {
      get().doRecycle(r);
      if (r instanceof XMLStreamReaderFactory.RecycleAware) {
         ((XMLStreamReaderFactory.RecycleAware)r).onRecycled();
      }

   }

   public abstract XMLStreamReader doCreate(String var1, InputStream var2, boolean var3);

   private XMLStreamReader doCreate(String systemId, InputStream in, @NotNull String encoding, boolean rejectDTDs) {
      InputStreamReader reader;
      try {
         reader = new InputStreamReader(in, encoding);
      } catch (UnsupportedEncodingException var7) {
         throw new XMLReaderException("stax.cantCreate", new Object[]{var7});
      }

      return this.doCreate(systemId, (Reader)reader, rejectDTDs);
   }

   public abstract XMLStreamReader doCreate(String var1, Reader var2, boolean var3);

   public abstract void doRecycle(XMLStreamReader var1);

   private static int buildIntegerValue(String propertyName, int defaultValue) {
      String propVal = System.getProperty(propertyName);
      if (propVal != null && propVal.length() > 0) {
         try {
            Integer value = Integer.parseInt(propVal);
            if (value > 0) {
               return value;
            }
         } catch (NumberFormatException var4) {
            if (LOGGER.isLoggable(Level.WARNING)) {
               LOGGER.log(Level.WARNING, (String)StreamingMessages.INVALID_PROPERTY_VALUE_INTEGER(propertyName, propVal, Integer.toString(defaultValue)), (Throwable)var4);
            }
         }
      }

      return defaultValue;
   }

   private static long buildLongValue(String propertyName, long defaultValue) {
      String propVal = System.getProperty(propertyName);
      if (propVal != null && propVal.length() > 0) {
         try {
            long value = Long.parseLong(propVal);
            if (value > 0L) {
               return value;
            }
         } catch (NumberFormatException var6) {
            if (LOGGER.isLoggable(Level.WARNING)) {
               LOGGER.log(Level.WARNING, (String)StreamingMessages.INVALID_PROPERTY_VALUE_LONG(propertyName, propVal, Long.toString(defaultValue)), (Throwable)var6);
            }
         }
      }

      return defaultValue;
   }

   private static Boolean getProperty(final String prop) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String value = System.getProperty(prop);
            return value != null ? Boolean.valueOf(value) : Boolean.FALSE;
         }
      });
   }

   public static final class Woodstox extends XMLStreamReaderFactory.NoLock {
      public static final String PROPERTY_MAX_ATTRIBUTES_PER_ELEMENT = "xml.ws.maximum.AttributesPerElement";
      public static final String PROPERTY_MAX_ATTRIBUTE_SIZE = "xml.ws.maximum.AttributeSize";
      public static final String PROPERTY_MAX_CHILDREN_PER_ELEMENT = "xml.ws.maximum.ChildrenPerElement";
      public static final String PROPERTY_MAX_ELEMENT_COUNT = "xml.ws.maximum.ElementCount";
      public static final String PROPERTY_MAX_ELEMENT_DEPTH = "xml.ws.maximum.ElementDepth";
      public static final String PROPERTY_MAX_CHARACTERS = "xml.ws.maximum.Characters";
      private static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 500;
      private static final int DEFAULT_MAX_ATTRIBUTE_SIZE = 524288;
      private static final int DEFAULT_MAX_CHILDREN_PER_ELEMENT = Integer.MAX_VALUE;
      private static final int DEFAULT_MAX_ELEMENT_DEPTH = 500;
      private static final long DEFAULT_MAX_ELEMENT_COUNT = 2147483647L;
      private static final long DEFAULT_MAX_CHARACTERS = Long.MAX_VALUE;
      private int maxAttributesPerElement = 500;
      private int maxAttributeSize = 524288;
      private int maxChildrenPerElement = Integer.MAX_VALUE;
      private int maxElementDepth = 500;
      private long maxElementCount = 2147483647L;
      private long maxCharacters = Long.MAX_VALUE;
      private static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
      private static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
      private static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
      private static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
      private static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
      private static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
      private static final String P_INTERN_NSURIS = "org.codehaus.stax2.internNsUris";

      public Woodstox(XMLInputFactory xif) {
         super(xif);
         if (xif.isPropertySupported("org.codehaus.stax2.internNsUris")) {
            xif.setProperty("org.codehaus.stax2.internNsUris", true);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"org.codehaus.stax2.internNsUris is {0}", (Object)true);
            }
         }

         if (xif.isPropertySupported("com.ctc.wstx.maxAttributesPerElement")) {
            this.maxAttributesPerElement = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.AttributesPerElement", 500));
            xif.setProperty("com.ctc.wstx.maxAttributesPerElement", this.maxAttributesPerElement);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"com.ctc.wstx.maxAttributesPerElement is {0}", (Object)this.maxAttributesPerElement);
            }
         }

         if (xif.isPropertySupported("com.ctc.wstx.maxAttributeSize")) {
            this.maxAttributeSize = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.AttributeSize", 524288));
            xif.setProperty("com.ctc.wstx.maxAttributeSize", this.maxAttributeSize);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"com.ctc.wstx.maxAttributeSize is {0}", (Object)this.maxAttributeSize);
            }
         }

         if (xif.isPropertySupported("com.ctc.wstx.maxChildrenPerElement")) {
            this.maxChildrenPerElement = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.ChildrenPerElement", Integer.MAX_VALUE));
            xif.setProperty("com.ctc.wstx.maxChildrenPerElement", this.maxChildrenPerElement);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"com.ctc.wstx.maxChildrenPerElement is {0}", (Object)this.maxChildrenPerElement);
            }
         }

         if (xif.isPropertySupported("com.ctc.wstx.maxElementDepth")) {
            this.maxElementDepth = Integer.valueOf(XMLStreamReaderFactory.buildIntegerValue("xml.ws.maximum.ElementDepth", 500));
            xif.setProperty("com.ctc.wstx.maxElementDepth", this.maxElementDepth);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"com.ctc.wstx.maxElementDepth is {0}", (Object)this.maxElementDepth);
            }
         }

         if (xif.isPropertySupported("com.ctc.wstx.maxElementCount")) {
            this.maxElementCount = Long.valueOf(XMLStreamReaderFactory.buildLongValue("xml.ws.maximum.ElementCount", 2147483647L));
            xif.setProperty("com.ctc.wstx.maxElementCount", this.maxElementCount);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"com.ctc.wstx.maxElementCount is {0}", (Object)this.maxElementCount);
            }
         }

         if (xif.isPropertySupported("com.ctc.wstx.maxCharacters")) {
            this.maxCharacters = Long.valueOf(XMLStreamReaderFactory.buildLongValue("xml.ws.maximum.Characters", Long.MAX_VALUE));
            xif.setProperty("com.ctc.wstx.maxCharacters", this.maxCharacters);
            if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
               XMLStreamReaderFactory.LOGGER.log(Level.FINE, (String)"com.ctc.wstx.maxCharacters is {0}", (Object)this.maxCharacters);
            }
         }

      }

      public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
         return super.doCreate(systemId, in, rejectDTDs);
      }

      public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
         return super.doCreate(systemId, in, rejectDTDs);
      }
   }

   public static class NoLock extends XMLStreamReaderFactory {
      private final XMLInputFactory xif;

      public NoLock(XMLInputFactory xif) {
         this.xif = xif;
      }

      public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
         try {
            return this.xif.createXMLStreamReader(systemId, in);
         } catch (XMLStreamException var5) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var5});
         }
      }

      public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
         try {
            return this.xif.createXMLStreamReader(systemId, in);
         } catch (XMLStreamException var5) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var5});
         }
      }

      public void doRecycle(XMLStreamReader r) {
      }
   }

   public static final class Default extends XMLStreamReaderFactory {
      private final ThreadLocal<XMLInputFactory> xif = new ThreadLocal<XMLInputFactory>() {
         public XMLInputFactory initialValue() {
            return XMLStreamReaderFactory.getXMLInputFactory();
         }
      };

      public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
         try {
            return ((XMLInputFactory)this.xif.get()).createXMLStreamReader(systemId, in);
         } catch (XMLStreamException var5) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var5});
         }
      }

      public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
         try {
            return ((XMLInputFactory)this.xif.get()).createXMLStreamReader(systemId, in);
         } catch (XMLStreamException var5) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var5});
         }
      }

      public void doRecycle(XMLStreamReader r) {
      }
   }

   private static final class Zephyr extends XMLStreamReaderFactory {
      private final XMLInputFactory xif;
      private final ThreadLocal<XMLStreamReader> pool = new ThreadLocal();
      private final Method setInputSourceMethod;
      private final Method resetMethod;
      private final Class zephyrClass;

      @Nullable
      public static XMLStreamReaderFactory newInstance(XMLInputFactory xif) {
         try {
            Class<?> clazz = xif.createXMLStreamReader((Reader)(new StringReader("<foo/>"))).getClass();
            return !clazz.getName().startsWith("com.sun.xml.internal.stream.") ? null : new XMLStreamReaderFactory.Zephyr(xif, clazz);
         } catch (NoSuchMethodException var2) {
            return null;
         } catch (XMLStreamException var3) {
            return null;
         }
      }

      public Zephyr(XMLInputFactory xif, Class clazz) throws NoSuchMethodException {
         this.zephyrClass = clazz;
         this.setInputSourceMethod = clazz.getMethod("setInputSource", InputSource.class);
         this.resetMethod = clazz.getMethod("reset");

         try {
            xif.setProperty("reuse-instance", false);
         } catch (IllegalArgumentException var4) {
         }

         this.xif = xif;
      }

      @Nullable
      private XMLStreamReader fetch() {
         XMLStreamReader sr = (XMLStreamReader)this.pool.get();
         if (sr == null) {
            return null;
         } else {
            this.pool.set((Object)null);
            return sr;
         }
      }

      public void doRecycle(XMLStreamReader r) {
         if (this.zephyrClass.isInstance(r)) {
            this.pool.set(r);
         }

      }

      public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
         try {
            XMLStreamReader xsr = this.fetch();
            if (xsr == null) {
               return this.xif.createXMLStreamReader(systemId, in);
            } else {
               InputSource is = new InputSource(systemId);
               is.setByteStream(in);
               this.reuse(xsr, is);
               return xsr;
            }
         } catch (IllegalAccessException var6) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var6});
         } catch (InvocationTargetException var7) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var7});
         } catch (XMLStreamException var8) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var8});
         }
      }

      public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
         try {
            XMLStreamReader xsr = this.fetch();
            if (xsr == null) {
               return this.xif.createXMLStreamReader(systemId, in);
            } else {
               InputSource is = new InputSource(systemId);
               is.setCharacterStream(in);
               this.reuse(xsr, is);
               return xsr;
            }
         } catch (IllegalAccessException var6) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var6});
         } catch (InvocationTargetException var7) {
            Throwable cause = var7.getCause();
            if (cause == null) {
               cause = var7;
            }

            throw new XMLReaderException("stax.cantCreate", new Object[]{cause});
         } catch (XMLStreamException var8) {
            throw new XMLReaderException("stax.cantCreate", new Object[]{var8});
         }
      }

      private void reuse(XMLStreamReader xsr, InputSource in) throws IllegalAccessException, InvocationTargetException {
         this.resetMethod.invoke(xsr);
         this.setInputSourceMethod.invoke(xsr, in);
      }
   }

   public interface RecycleAware {
      void onRecycled();
   }
}
