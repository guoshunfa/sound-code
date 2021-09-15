package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.util.Map;

public abstract class DTDDVFactory {
   private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl";
   private static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";

   public static final DTDDVFactory getInstance() throws DVFactoryException {
      return getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl");
   }

   public static final DTDDVFactory getInstance(String factoryClass) throws DVFactoryException {
      try {
         if ("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl".equals(factoryClass)) {
            return new DTDDVFactoryImpl();
         } else {
            return (DTDDVFactory)("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl".equals(factoryClass) ? new XML11DTDDVFactoryImpl() : (DTDDVFactory)((DTDDVFactory)ObjectFactory.newInstance(factoryClass, true)));
         }
      } catch (ClassCastException var2) {
         throw new DVFactoryException("DTD factory class " + factoryClass + " does not extend from DTDDVFactory.");
      }
   }

   protected DTDDVFactory() {
   }

   public abstract DatatypeValidator getBuiltInDV(String var1);

   public abstract Map<String, DatatypeValidator> getBuiltInTypes();
}
