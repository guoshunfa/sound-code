package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class UsesJAXBContextFeature extends WebServiceFeature {
   public static final String ID = "http://jax-ws.dev.java.net/features/uses-jaxb-context";
   private final JAXBContextFactory factory;

   @FeatureConstructor({"value"})
   public UsesJAXBContextFeature(@NotNull Class<? extends JAXBContextFactory> factoryClass) {
      InstantiationError x;
      try {
         this.factory = (JAXBContextFactory)factoryClass.getConstructor().newInstance();
      } catch (InstantiationException var4) {
         x = new InstantiationError(var4.getMessage());
         x.initCause(var4);
         throw x;
      } catch (IllegalAccessException var5) {
         Error x = new IllegalAccessError(var5.getMessage());
         x.initCause(var5);
         throw x;
      } catch (InvocationTargetException var6) {
         x = new InstantiationError(var6.getMessage());
         x.initCause(var6);
         throw x;
      } catch (NoSuchMethodException var7) {
         Error x = new NoSuchMethodError(var7.getMessage());
         x.initCause(var7);
         throw x;
      }
   }

   public UsesJAXBContextFeature(@Nullable JAXBContextFactory factory) {
      this.factory = factory;
   }

   public UsesJAXBContextFeature(@Nullable final JAXBRIContext context) {
      this.factory = new JAXBContextFactory() {
         @NotNull
         public JAXBRIContext createJAXBContext(@NotNull SEIModel sei, @NotNull List<Class> classesToBind, @NotNull List<TypeReference> typeReferences) throws JAXBException {
            return context;
         }
      };
   }

   @ManagedAttribute
   @Nullable
   public JAXBContextFactory getFactory() {
      return this.factory;
   }

   @ManagedAttribute
   public String getID() {
      return "http://jax-ws.dev.java.net/features/uses-jaxb-context";
   }
}
