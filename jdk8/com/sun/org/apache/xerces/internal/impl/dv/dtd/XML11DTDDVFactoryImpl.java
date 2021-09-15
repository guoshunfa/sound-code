package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XML11DTDDVFactoryImpl extends DTDDVFactoryImpl {
   static Map<String, DatatypeValidator> XML11BUILTINTYPES;

   public DatatypeValidator getBuiltInDV(String name) {
      return XML11BUILTINTYPES.get(name) != null ? (DatatypeValidator)XML11BUILTINTYPES.get(name) : (DatatypeValidator)fBuiltInTypes.get(name);
   }

   public Map<String, DatatypeValidator> getBuiltInTypes() {
      HashMap<String, DatatypeValidator> toReturn = new HashMap(fBuiltInTypes);
      toReturn.putAll(XML11BUILTINTYPES);
      return toReturn;
   }

   static {
      Map<String, DatatypeValidator> xml11BuiltInTypes = new HashMap();
      xml11BuiltInTypes.put("XML11ID", new XML11IDDatatypeValidator());
      DatatypeValidator dvTemp = new XML11IDREFDatatypeValidator();
      xml11BuiltInTypes.put("XML11IDREF", dvTemp);
      xml11BuiltInTypes.put("XML11IDREFS", new ListDatatypeValidator(dvTemp));
      DatatypeValidator dvTemp = new XML11NMTOKENDatatypeValidator();
      xml11BuiltInTypes.put("XML11NMTOKEN", dvTemp);
      xml11BuiltInTypes.put("XML11NMTOKENS", new ListDatatypeValidator(dvTemp));
      XML11BUILTINTYPES = Collections.unmodifiableMap(xml11BuiltInTypes);
   }
}
