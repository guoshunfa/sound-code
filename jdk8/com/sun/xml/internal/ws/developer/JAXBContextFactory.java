package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;

public interface JAXBContextFactory {
   JAXBContextFactory DEFAULT = new JAXBContextFactory() {
      @NotNull
      public JAXBRIContext createJAXBContext(@NotNull SEIModel sei, @NotNull List<Class> classesToBind, @NotNull List<TypeReference> typeReferences) throws JAXBException {
         return JAXBRIContext.newInstance((Class[])classesToBind.toArray(new Class[classesToBind.size()]), typeReferences, (Map)null, sei.getTargetNamespace(), false, (RuntimeAnnotationReader)null);
      }
   };

   @NotNull
   JAXBRIContext createJAXBContext(@NotNull SEIModel var1, @NotNull List<Class> var2, @NotNull List<TypeReference> var3) throws JAXBException;
}
