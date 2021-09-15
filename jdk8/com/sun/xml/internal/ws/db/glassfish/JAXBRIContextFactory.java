package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.CompositeStructure;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.ContextFactory;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class JAXBRIContextFactory extends BindingContextFactory {
   public BindingContext newContext(JAXBContext context) {
      return new JAXBRIContextWrapper((JAXBRIContext)context, (Map)null);
   }

   public BindingContext newContext(BindingInfo bi) {
      Class[] classes = (Class[])bi.contentClasses().toArray(new Class[bi.contentClasses().size()]);

      for(int i = 0; i < classes.length; ++i) {
         if (WrapperComposite.class.equals(classes[i])) {
            classes[i] = CompositeStructure.class;
         }
      }

      Map<TypeInfo, TypeReference> typeInfoMappings = this.typeInfoMappings(bi.typeInfos());
      Map<Class, Class> subclassReplacements = bi.subclassReplacements();
      String defaultNamespaceRemap = bi.getDefaultNamespace();
      Boolean c14nSupport = (Boolean)bi.properties().get("c14nSupport");
      RuntimeAnnotationReader ar = (RuntimeAnnotationReader)bi.properties().get("com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader");
      JAXBContextFactory jaxbContextFactory = (JAXBContextFactory)bi.properties().get(JAXBContextFactory.class.getName());

      try {
         JAXBRIContext context = jaxbContextFactory != null ? jaxbContextFactory.createJAXBContext(bi.getSEIModel(), this.toList((Object[])classes), this.toList(typeInfoMappings.values())) : ContextFactory.createContext(classes, typeInfoMappings.values(), subclassReplacements, defaultNamespaceRemap, c14nSupport != null ? c14nSupport : false, ar, false, false, false);
         return new JAXBRIContextWrapper(context, typeInfoMappings);
      } catch (Exception var10) {
         throw new DatabindingException(var10);
      }
   }

   private <T> List<T> toList(T[] a) {
      List<T> l = new ArrayList();
      l.addAll(Arrays.asList(a));
      return l;
   }

   private <T> List<T> toList(Collection<T> col) {
      if (col instanceof List) {
         return (List)col;
      } else {
         List<T> l = new ArrayList();
         l.addAll(col);
         return l;
      }
   }

   private Map<TypeInfo, TypeReference> typeInfoMappings(Collection<TypeInfo> typeInfos) {
      Map<TypeInfo, TypeReference> map = new HashMap();
      Iterator var3 = typeInfos.iterator();

      while(var3.hasNext()) {
         TypeInfo ti = (TypeInfo)var3.next();
         Type type = WrapperComposite.class.equals(ti.type) ? CompositeStructure.class : ti.type;
         TypeReference tr = new TypeReference(ti.tagName, (Type)type, ti.annotations);
         map.put(ti, tr);
      }

      return map;
   }

   protected BindingContext getContext(Marshaller m) {
      return this.newContext((JAXBContext)((MarshallerImpl)m).getContext());
   }

   protected boolean isFor(String str) {
      return str.equals("glassfish.jaxb") || str.equals(this.getClass().getName()) || str.equals("com.sun.xml.internal.bind.v2.runtime");
   }
}
