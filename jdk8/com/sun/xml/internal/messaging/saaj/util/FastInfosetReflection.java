package com.sun.xml.internal.messaging.saaj.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class FastInfosetReflection {
   static Constructor fiDOMDocumentParser_new;
   static Method fiDOMDocumentParser_parse;
   static Constructor fiDOMDocumentSerializer_new;
   static Method fiDOMDocumentSerializer_serialize;
   static Method fiDOMDocumentSerializer_setOutputStream;
   static Class fiFastInfosetSource_class;
   static Constructor fiFastInfosetSource_new;
   static Method fiFastInfosetSource_getInputStream;
   static Method fiFastInfosetSource_setInputStream;
   static Constructor fiFastInfosetResult_new;
   static Method fiFastInfosetResult_getOutputStream;

   public static Object DOMDocumentParser_new() throws Exception {
      if (fiDOMDocumentParser_new == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return fiDOMDocumentParser_new.newInstance((Object[])null);
      }
   }

   public static void DOMDocumentParser_parse(Object parser, Document d, InputStream s) throws Exception {
      if (fiDOMDocumentParser_parse == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         fiDOMDocumentParser_parse.invoke(parser, d, s);
      }
   }

   public static Object DOMDocumentSerializer_new() throws Exception {
      if (fiDOMDocumentSerializer_new == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return fiDOMDocumentSerializer_new.newInstance((Object[])null);
      }
   }

   public static void DOMDocumentSerializer_serialize(Object serializer, Node node) throws Exception {
      if (fiDOMDocumentSerializer_serialize == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         fiDOMDocumentSerializer_serialize.invoke(serializer, node);
      }
   }

   public static void DOMDocumentSerializer_setOutputStream(Object serializer, OutputStream os) throws Exception {
      if (fiDOMDocumentSerializer_setOutputStream == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         fiDOMDocumentSerializer_setOutputStream.invoke(serializer, os);
      }
   }

   public static boolean isFastInfosetSource(Source source) {
      return source.getClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
   }

   public static Class getFastInfosetSource_class() {
      if (fiFastInfosetSource_class == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return fiFastInfosetSource_class;
      }
   }

   public static Source FastInfosetSource_new(InputStream is) throws Exception {
      if (fiFastInfosetSource_new == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return (Source)fiFastInfosetSource_new.newInstance(is);
      }
   }

   public static InputStream FastInfosetSource_getInputStream(Source source) throws Exception {
      if (fiFastInfosetSource_getInputStream == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return (InputStream)fiFastInfosetSource_getInputStream.invoke(source, (Object[])null);
      }
   }

   public static void FastInfosetSource_setInputStream(Source source, InputStream is) throws Exception {
      if (fiFastInfosetSource_setInputStream == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         fiFastInfosetSource_setInputStream.invoke(source, is);
      }
   }

   public static boolean isFastInfosetResult(Result result) {
      return result.getClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult");
   }

   public static Result FastInfosetResult_new(OutputStream os) throws Exception {
      if (fiFastInfosetResult_new == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return (Result)fiFastInfosetResult_new.newInstance(os);
      }
   }

   public static OutputStream FastInfosetResult_getOutputStream(Result result) throws Exception {
      if (fiFastInfosetResult_getOutputStream == null) {
         throw new RuntimeException("Unable to locate Fast Infoset implementation");
      } else {
         return (OutputStream)fiFastInfosetResult_getOutputStream.invoke(result, (Object[])null);
      }
   }

   static {
      try {
         Class clazz = Class.forName("com.sun.xml.internal.fastinfoset.dom.DOMDocumentParser");
         fiDOMDocumentParser_new = clazz.getConstructor((Class[])null);
         fiDOMDocumentParser_parse = clazz.getMethod("parse", Document.class, InputStream.class);
         clazz = Class.forName("com.sun.xml.internal.fastinfoset.dom.DOMDocumentSerializer");
         fiDOMDocumentSerializer_new = clazz.getConstructor((Class[])null);
         fiDOMDocumentSerializer_serialize = clazz.getMethod("serialize", Node.class);
         fiDOMDocumentSerializer_setOutputStream = clazz.getMethod("setOutputStream", OutputStream.class);
         fiFastInfosetSource_class = clazz = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
         fiFastInfosetSource_new = clazz.getConstructor(InputStream.class);
         fiFastInfosetSource_getInputStream = clazz.getMethod("getInputStream", (Class[])null);
         fiFastInfosetSource_setInputStream = clazz.getMethod("setInputStream", InputStream.class);
         clazz = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult");
         fiFastInfosetResult_new = clazz.getConstructor(OutputStream.class);
         fiFastInfosetResult_getOutputStream = clazz.getMethod("getOutputStream", (Class[])null);
      } catch (Exception var1) {
      }

   }
}
