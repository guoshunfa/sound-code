package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class SerializerFactory {
   public static final String FactoriesProperty = "com.sun.org.apache.xml.internal.serialize.factories";
   private static final Map<String, SerializerFactory> _factories = Collections.synchronizedMap(new HashMap());

   public static void registerSerializerFactory(SerializerFactory factory) {
      synchronized(_factories) {
         String method = factory.getSupportedMethod();
         _factories.put(method, factory);
      }
   }

   public static SerializerFactory getSerializerFactory(String method) {
      return (SerializerFactory)_factories.get(method);
   }

   protected abstract String getSupportedMethod();

   public abstract Serializer makeSerializer(OutputFormat var1);

   public abstract Serializer makeSerializer(Writer var1, OutputFormat var2);

   public abstract Serializer makeSerializer(OutputStream var1, OutputFormat var2) throws UnsupportedEncodingException;

   static {
      SerializerFactory factory = new SerializerFactoryImpl("xml");
      registerSerializerFactory(factory);
      factory = new SerializerFactoryImpl("html");
      registerSerializerFactory(factory);
      factory = new SerializerFactoryImpl("xhtml");
      registerSerializerFactory(factory);
      factory = new SerializerFactoryImpl("text");
      registerSerializerFactory(factory);
      String list = com.sun.org.apache.xerces.internal.utils.SecuritySupport.getSystemProperty("com.sun.org.apache.xml.internal.serialize.factories");
      if (list != null) {
         StringTokenizer token = new StringTokenizer(list, " ;,:");

         while(token.hasMoreTokens()) {
            String className = token.nextToken();

            try {
               SerializerFactory factory = (SerializerFactory)ObjectFactory.newInstance(className, true);
               if (_factories.containsKey(factory.getSupportedMethod())) {
                  _factories.put(factory.getSupportedMethod(), factory);
               }
            } catch (Exception var5) {
            }
         }
      }

   }
}
