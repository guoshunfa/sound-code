package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;

final class SCAnnotations {
   final ArrayList<QName> portQNames = new ArrayList();
   final ArrayList<Class> classes = new ArrayList();

   SCAnnotations(final Class<?> sc) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            WebServiceClient wsc = (WebServiceClient)sc.getAnnotation(WebServiceClient.class);
            if (wsc == null) {
               throw new WebServiceException("Service Interface Annotations required, exiting...");
            } else {
               String tns = wsc.targetNamespace();

               try {
                  JAXWSUtils.getFileOrURL(wsc.wsdlLocation());
               } catch (IOException var10) {
                  throw new WebServiceException(var10);
               }

               Method[] var3 = sc.getDeclaredMethods();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Method method = var3[var5];
                  WebEndpoint webEndpoint = (WebEndpoint)method.getAnnotation(WebEndpoint.class);
                  if (webEndpoint != null) {
                     String endpointName = webEndpoint.name();
                     QName portQName = new QName(tns, endpointName);
                     SCAnnotations.this.portQNames.add(portQName);
                  }

                  Class<?> seiClazz = method.getReturnType();
                  if (seiClazz != Void.TYPE) {
                     SCAnnotations.this.classes.add(seiClazz);
                  }
               }

               return null;
            }
         }
      });
   }
}
