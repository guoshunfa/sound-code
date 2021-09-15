package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class SchemaDVFactoryImpl extends BaseSchemaDVFactory {
   static final SymbolHash fBuiltInTypes = new SymbolHash();

   static void createBuiltInTypes() {
      createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType);
   }

   public XSSimpleType getBuiltInType(String name) {
      return (XSSimpleType)fBuiltInTypes.get(name);
   }

   public SymbolHash getBuiltInTypes() {
      return fBuiltInTypes.makeClone();
   }

   static {
      createBuiltInTypes();
   }
}
