package com.oracle.webservices.internal.api.message;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;

public abstract class MessageContextFactory {
   private static final MessageContextFactory DEFAULT = new com.sun.xml.internal.ws.api.message.MessageContextFactory(new WebServiceFeature[0]);

   protected abstract MessageContextFactory newFactory(WebServiceFeature... var1);

   public abstract MessageContext createContext();

   public abstract MessageContext createContext(SOAPMessage var1);

   public abstract MessageContext createContext(Source var1);

   public abstract MessageContext createContext(Source var1, EnvelopeStyle.Style var2);

   public abstract MessageContext createContext(InputStream var1, String var2) throws IOException;

   /** @deprecated */
   @Deprecated
   public abstract MessageContext createContext(InputStream var1, MimeHeaders var2) throws IOException;

   public static MessageContextFactory createFactory(WebServiceFeature... f) {
      return createFactory((ClassLoader)null, f);
   }

   public static MessageContextFactory createFactory(ClassLoader cl, WebServiceFeature... f) {
      Iterator var2 = ServiceFinder.find(MessageContextFactory.class, cl).iterator();

      MessageContextFactory newfac;
      do {
         if (!var2.hasNext()) {
            return new com.sun.xml.internal.ws.api.message.MessageContextFactory(f);
         }

         MessageContextFactory factory = (MessageContextFactory)var2.next();
         newfac = factory.newFactory(f);
      } while(newfac == null);

      return newfac;
   }

   /** @deprecated */
   @Deprecated
   public abstract MessageContext doCreate();

   /** @deprecated */
   @Deprecated
   public abstract MessageContext doCreate(SOAPMessage var1);

   /** @deprecated */
   @Deprecated
   public abstract MessageContext doCreate(Source var1, SOAPVersion var2);

   /** @deprecated */
   @Deprecated
   public static MessageContext create(ClassLoader... classLoader) {
      return serviceFinder(classLoader, new MessageContextFactory.Creator() {
         public MessageContext create(MessageContextFactory f) {
            return f.doCreate();
         }
      });
   }

   /** @deprecated */
   @Deprecated
   public static MessageContext create(final SOAPMessage m, ClassLoader... classLoader) {
      return serviceFinder(classLoader, new MessageContextFactory.Creator() {
         public MessageContext create(MessageContextFactory f) {
            return f.doCreate(m);
         }
      });
   }

   /** @deprecated */
   @Deprecated
   public static MessageContext create(final Source m, final SOAPVersion v, ClassLoader... classLoader) {
      return serviceFinder(classLoader, new MessageContextFactory.Creator() {
         public MessageContext create(MessageContextFactory f) {
            return f.doCreate(m, v);
         }
      });
   }

   /** @deprecated */
   @Deprecated
   private static MessageContext serviceFinder(ClassLoader[] classLoader, MessageContextFactory.Creator creator) {
      ClassLoader cl = classLoader.length == 0 ? null : classLoader[0];
      Iterator var3 = ServiceFinder.find(MessageContextFactory.class, cl).iterator();

      MessageContext messageContext;
      do {
         if (!var3.hasNext()) {
            return creator.create(DEFAULT);
         }

         MessageContextFactory factory = (MessageContextFactory)var3.next();
         messageContext = creator.create(factory);
      } while(messageContext == null);

      return messageContext;
   }

   /** @deprecated */
   @Deprecated
   private interface Creator {
      MessageContext create(MessageContextFactory var1);
   }
}
