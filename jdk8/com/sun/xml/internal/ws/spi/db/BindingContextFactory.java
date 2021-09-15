package com.sun.xml.internal.ws.spi.db;

import com.sun.xml.internal.ws.db.glassfish.JAXBRIContextFactory;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public abstract class BindingContextFactory {
   public static final String DefaultDatabindingMode = "glassfish.jaxb";
   public static final String JAXB_CONTEXT_FACTORY_PROPERTY = BindingContextFactory.class.getName();
   public static final Logger LOGGER = Logger.getLogger(BindingContextFactory.class.getName());

   public static Iterator<BindingContextFactory> serviceIterator() {
      ServiceFinder<BindingContextFactory> sf = ServiceFinder.find(BindingContextFactory.class);
      final Iterator<BindingContextFactory> ibcf = sf.iterator();
      return new Iterator<BindingContextFactory>() {
         private BindingContextFactory bcf;

         public boolean hasNext() {
            while(true) {
               try {
                  if (ibcf.hasNext()) {
                     this.bcf = (BindingContextFactory)ibcf.next();
                     return true;
                  }

                  return false;
               } catch (ServiceConfigurationError var2) {
                  BindingContextFactory.LOGGER.warning("skipping factory: ServiceConfigurationError: " + var2.getMessage());
               } catch (NoClassDefFoundError var3) {
                  BindingContextFactory.LOGGER.fine("skipping factory: NoClassDefFoundError: " + var3.getMessage());
               }
            }
         }

         public BindingContextFactory next() {
            if (BindingContextFactory.LOGGER.isLoggable(Level.FINER)) {
               BindingContextFactory.LOGGER.finer("SPI found provider: " + this.bcf.getClass().getName());
            }

            return this.bcf;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static List<BindingContextFactory> factories() {
      List<BindingContextFactory> factories = new ArrayList();
      Iterator ibcf = serviceIterator();

      while(ibcf.hasNext()) {
         factories.add(ibcf.next());
      }

      if (factories.isEmpty()) {
         if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.log(Level.FINER, "No SPI providers for BindingContextFactory found, adding: " + JAXBRIContextFactory.class.getName());
         }

         factories.add(new JAXBRIContextFactory());
      }

      return factories;
   }

   protected abstract BindingContext newContext(JAXBContext var1);

   protected abstract BindingContext newContext(BindingInfo var1);

   protected abstract boolean isFor(String var1);

   /** @deprecated */
   protected abstract BindingContext getContext(Marshaller var1);

   private static BindingContextFactory getFactory(String mode) {
      Iterator var1 = factories().iterator();

      BindingContextFactory f;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         f = (BindingContextFactory)var1.next();
      } while(!f.isFor(mode));

      return f;
   }

   public static BindingContext create(JAXBContext context) throws DatabindingException {
      return getJAXBFactory(context).newContext(context);
   }

   public static BindingContext create(BindingInfo bi) {
      String mode = bi.getDatabindingMode();
      if (mode != null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Using SEI-configured databindng mode: " + mode);
         }
      } else if ((mode = System.getProperty("BindingContextFactory")) != null) {
         bi.setDatabindingMode(mode);
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Using databindng: " + mode + " based on 'BindingContextFactory' System property");
         }
      } else {
         if ((mode = System.getProperty(JAXB_CONTEXT_FACTORY_PROPERTY)) == null) {
            Iterator var4 = factories().iterator();
            if (var4.hasNext()) {
               BindingContextFactory factory = (BindingContextFactory)var4.next();
               if (LOGGER.isLoggable(Level.FINE)) {
                  LOGGER.log(Level.FINE, "Using SPI-determined databindng mode: " + factory.getClass().getName());
               }

               return factory.newContext(bi);
            }

            LOGGER.log(Level.SEVERE, "No Binding Context Factories found.");
            throw new DatabindingException("No Binding Context Factories found.");
         }

         bi.setDatabindingMode(mode);
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Using databindng: " + mode + " based on '" + JAXB_CONTEXT_FACTORY_PROPERTY + "' System property");
         }
      }

      BindingContextFactory f = getFactory(mode);
      if (f != null) {
         return f.newContext(bi);
      } else {
         LOGGER.severe("Unknown Databinding mode: " + mode);
         throw new DatabindingException("Unknown Databinding mode: " + mode);
      }
   }

   public static boolean isContextSupported(Object o) {
      if (o == null) {
         return false;
      } else {
         String pkgName = o.getClass().getPackage().getName();
         Iterator var2 = factories().iterator();

         BindingContextFactory f;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            f = (BindingContextFactory)var2.next();
         } while(!f.isFor(pkgName));

         return true;
      }
   }

   static BindingContextFactory getJAXBFactory(Object o) {
      String pkgName = o.getClass().getPackage().getName();
      BindingContextFactory f = getFactory(pkgName);
      if (f != null) {
         return f;
      } else {
         throw new DatabindingException("Unknown JAXBContext implementation: " + o.getClass());
      }
   }

   /** @deprecated */
   public static BindingContext getBindingContext(Marshaller m) {
      return getJAXBFactory(m).getContext(m);
   }
}
