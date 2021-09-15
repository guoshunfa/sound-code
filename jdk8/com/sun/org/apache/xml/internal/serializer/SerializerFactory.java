package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.util.Properties;
import org.xml.sax.ContentHandler;

public final class SerializerFactory {
   private SerializerFactory() {
   }

   public static Serializer getSerializer(Properties format) {
      try {
         String method = format.getProperty("method");
         String className;
         if (method == null) {
            className = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[]{"method"});
            throw new IllegalArgumentException(className);
         } else {
            className = format.getProperty("{http://xml.apache.org/xalan}content-handler");
            if (null == className) {
               Properties methodDefaults = OutputPropertiesFactory.getDefaultMethodProperties(method);
               className = methodDefaults.getProperty("{http://xml.apache.org/xalan}content-handler");
               if (null == className) {
                  String msg = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[]{"{http://xml.apache.org/xalan}content-handler"});
                  throw new IllegalArgumentException(msg);
               }
            }

            Class cls = ObjectFactory.findProviderClass(className, true);
            Object obj = cls.newInstance();
            Object ser;
            if (obj instanceof SerializationHandler) {
               ser = (Serializer)cls.newInstance();
               ((Serializer)ser).setOutputFormat(format);
            } else {
               if (!(obj instanceof ContentHandler)) {
                  throw new Exception(Utils.messages.createMessage("ER_SERIALIZER_NOT_CONTENTHANDLER", new Object[]{className}));
               }

               className = "com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler";
               cls = ObjectFactory.findProviderClass(className, true);
               SerializationHandler sh = (SerializationHandler)cls.newInstance();
               sh.setContentHandler((ContentHandler)obj);
               sh.setOutputFormat(format);
               ser = sh;
            }

            return (Serializer)ser;
         }
      } catch (Exception var7) {
         throw new WrappedRuntimeException(var7);
      }
   }
}
