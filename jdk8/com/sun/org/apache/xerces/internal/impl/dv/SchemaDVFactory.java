package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class SchemaDVFactory {
   private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl";

   public static final synchronized SchemaDVFactory getInstance() throws DVFactoryException {
      return getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl");
   }

   public static final synchronized SchemaDVFactory getInstance(String factoryClass) throws DVFactoryException {
      try {
         return (SchemaDVFactory)((SchemaDVFactory)ObjectFactory.newInstance(factoryClass, true));
      } catch (ClassCastException var2) {
         throw new DVFactoryException("Schema factory class " + factoryClass + " does not extend from SchemaDVFactory.");
      }
   }

   protected SchemaDVFactory() {
   }

   public abstract XSSimpleType getBuiltInType(String var1);

   public abstract SymbolHash getBuiltInTypes();

   public abstract XSSimpleType createTypeRestriction(String var1, String var2, short var3, XSSimpleType var4, XSObjectList var5);

   public abstract XSSimpleType createTypeList(String var1, String var2, short var3, XSSimpleType var4, XSObjectList var5);

   public abstract XSSimpleType createTypeUnion(String var1, String var2, short var3, XSSimpleType[] var4, XSObjectList var5);
}
