package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.handler.HandlerChainsModel;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.soap.SOAPMessageHandlers;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;

public class HandlerAnnotationProcessor {
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.util");

   public static HandlerAnnotationInfo buildHandlerInfo(@NotNull Class<?> clazz, QName serviceName, QName portName, WSBinding binding) {
      MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(clazz, binding);
      if (metadataReader == null) {
         metadataReader = new ReflectAnnotationReader();
      }

      HandlerChain handlerChain = (HandlerChain)((MetadataReader)metadataReader).getAnnotation(HandlerChain.class, clazz);
      if (handlerChain == null) {
         clazz = getSEI(clazz, (MetadataReader)metadataReader);
         if (clazz != null) {
            handlerChain = (HandlerChain)((MetadataReader)metadataReader).getAnnotation(HandlerChain.class, clazz);
         }

         if (handlerChain == null) {
            return null;
         }
      }

      if (clazz.getAnnotation(SOAPMessageHandlers.class) != null) {
         throw new UtilException("util.handler.cannot.combine.soapmessagehandlers", new Object[0]);
      } else {
         InputStream iStream = getFileAsStream(clazz, handlerChain);
         XMLStreamReader reader = XMLStreamReaderFactory.create((String)null, (InputStream)iStream, true);
         XMLStreamReaderUtil.nextElementContent(reader);
         HandlerAnnotationInfo handlerAnnInfo = HandlerChainsModel.parseHandlerFile(reader, clazz.getClassLoader(), serviceName, portName, binding);

         try {
            reader.close();
            iStream.close();
            return handlerAnnInfo;
         } catch (XMLStreamException var10) {
            var10.printStackTrace();
            throw new UtilException(var10.getMessage(), new Object[0]);
         } catch (IOException var11) {
            var11.printStackTrace();
            throw new UtilException(var11.getMessage(), new Object[0]);
         }
      }
   }

   public static HandlerChainsModel buildHandlerChainsModel(Class<?> clazz) {
      if (clazz == null) {
         return null;
      } else {
         HandlerChain handlerChain = (HandlerChain)clazz.getAnnotation(HandlerChain.class);
         if (handlerChain == null) {
            return null;
         } else {
            InputStream iStream = getFileAsStream(clazz, handlerChain);
            XMLStreamReader reader = XMLStreamReaderFactory.create((String)null, (InputStream)iStream, true);
            XMLStreamReaderUtil.nextElementContent(reader);
            HandlerChainsModel handlerChainsModel = HandlerChainsModel.parseHandlerConfigFile(clazz, reader);

            try {
               reader.close();
               iStream.close();
               return handlerChainsModel;
            } catch (XMLStreamException var6) {
               var6.printStackTrace();
               throw new UtilException(var6.getMessage(), new Object[0]);
            } catch (IOException var7) {
               var7.printStackTrace();
               throw new UtilException(var7.getMessage(), new Object[0]);
            }
         }
      }
   }

   static Class getClass(String className) {
      try {
         return Thread.currentThread().getContextClassLoader().loadClass(className);
      } catch (ClassNotFoundException var2) {
         throw new UtilException("util.handler.class.not.found", new Object[]{className});
      }
   }

   static Class getSEI(Class<?> clazz, MetadataReader metadataReader) {
      if (metadataReader == null) {
         metadataReader = new ReflectAnnotationReader();
      }

      if (!Provider.class.isAssignableFrom(clazz) && !AsyncProvider.class.isAssignableFrom(clazz)) {
         if (Service.class.isAssignableFrom(clazz)) {
            return null;
         } else {
            WebService webService = (WebService)((MetadataReader)metadataReader).getAnnotation(WebService.class, clazz);
            if (webService == null) {
               throw new UtilException("util.handler.no.webservice.annotation", new Object[]{clazz.getCanonicalName()});
            } else {
               String ei = webService.endpointInterface();
               if (ei.length() > 0) {
                  clazz = getClass(webService.endpointInterface());
                  WebService ws = (WebService)((MetadataReader)metadataReader).getAnnotation(WebService.class, clazz);
                  if (ws == null) {
                     throw new UtilException("util.handler.endpoint.interface.no.webservice", new Object[]{webService.endpointInterface()});
                  } else {
                     return clazz;
                  }
               } else {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   static InputStream getFileAsStream(Class clazz, HandlerChain chain) {
      URL url = clazz.getResource(chain.file());
      if (url == null) {
         url = Thread.currentThread().getContextClassLoader().getResource(chain.file());
      }

      if (url == null) {
         String tmp = clazz.getPackage().getName();
         tmp = tmp.replace('.', '/');
         tmp = tmp + "/" + chain.file();
         url = Thread.currentThread().getContextClassLoader().getResource(tmp);
      }

      if (url == null) {
         throw new UtilException("util.failed.to.find.handlerchain.file", new Object[]{clazz.getName(), chain.file()});
      } else {
         try {
            return url.openStream();
         } catch (IOException var4) {
            throw new UtilException("util.failed.to.parse.handlerchain.file", new Object[]{clazz.getName(), chain.file()});
         }
      }
   }
}
