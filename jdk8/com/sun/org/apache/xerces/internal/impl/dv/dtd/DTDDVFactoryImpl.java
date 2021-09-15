package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DTDDVFactoryImpl extends DTDDVFactory {
   static final Map<String, DatatypeValidator> fBuiltInTypes;

   public DatatypeValidator getBuiltInDV(String name) {
      return (DatatypeValidator)fBuiltInTypes.get(name);
   }

   public Map<String, DatatypeValidator> getBuiltInTypes() {
      return new HashMap(fBuiltInTypes);
   }

   static {
      Map<String, DatatypeValidator> builtInTypes = new HashMap();
      builtInTypes.put("string", new StringDatatypeValidator());
      builtInTypes.put("ID", new IDDatatypeValidator());
      DatatypeValidator dvTemp = new IDREFDatatypeValidator();
      builtInTypes.put("IDREF", dvTemp);
      builtInTypes.put("IDREFS", new ListDatatypeValidator(dvTemp));
      DatatypeValidator dvTemp = new ENTITYDatatypeValidator();
      builtInTypes.put("ENTITY", new ENTITYDatatypeValidator());
      builtInTypes.put("ENTITIES", new ListDatatypeValidator(dvTemp));
      builtInTypes.put("NOTATION", new NOTATIONDatatypeValidator());
      DatatypeValidator dvTemp = new NMTOKENDatatypeValidator();
      builtInTypes.put("NMTOKEN", dvTemp);
      builtInTypes.put("NMTOKENS", new ListDatatypeValidator(dvTemp));
      fBuiltInTypes = Collections.unmodifiableMap(builtInTypes);
   }
}
