package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class FullDVFactory extends BaseDVFactory {
   static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
   static SymbolHash fFullTypes = new SymbolHash(89);

   public XSSimpleType getBuiltInType(String name) {
      return (XSSimpleType)fFullTypes.get(name);
   }

   public SymbolHash getBuiltInTypes() {
      return fFullTypes.makeClone();
   }

   static void createBuiltInTypes(SymbolHash types) {
      BaseDVFactory.createBuiltInTypes(types);
      String DOUBLE = "double";
      String DURATION = "duration";
      String ENTITY = "ENTITY";
      String ENTITIES = "ENTITIES";
      String FLOAT = "float";
      String HEXBINARY = "hexBinary";
      String ID = "ID";
      String IDREF = "IDREF";
      String IDREFS = "IDREFS";
      String NAME = "Name";
      String NCNAME = "NCName";
      String NMTOKEN = "NMTOKEN";
      String NMTOKENS = "NMTOKENS";
      String LANGUAGE = "language";
      String NORMALIZEDSTRING = "normalizedString";
      String NOTATION = "NOTATION";
      String QNAME = "QName";
      String STRING = "string";
      String TOKEN = "token";
      XSFacets facets = new XSFacets();
      XSSimpleTypeDecl anySimpleType = XSSimpleTypeDecl.fAnySimpleType;
      XSSimpleTypeDecl stringDV = (XSSimpleTypeDecl)types.get("string");
      types.put("float", new XSSimpleTypeDecl(anySimpleType, "float", (short)4, (short)1, true, true, true, true, (short)5));
      types.put("double", new XSSimpleTypeDecl(anySimpleType, "double", (short)5, (short)1, true, true, true, true, (short)6));
      types.put("duration", new XSSimpleTypeDecl(anySimpleType, "duration", (short)6, (short)1, false, false, false, true, (short)7));
      types.put("hexBinary", new XSSimpleTypeDecl(anySimpleType, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
      types.put("QName", new XSSimpleTypeDecl(anySimpleType, "QName", (short)18, (short)0, false, false, false, true, (short)19));
      types.put("NOTATION", new XSSimpleTypeDecl(anySimpleType, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
      facets.whiteSpace = 1;
      XSSimpleTypeDecl normalizedDV = new XSSimpleTypeDecl(stringDV, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)21);
      normalizedDV.applyFacets1(facets, (short)16, (short)0);
      types.put("normalizedString", normalizedDV);
      facets.whiteSpace = 2;
      XSSimpleTypeDecl tokenDV = new XSSimpleTypeDecl(normalizedDV, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)22);
      tokenDV.applyFacets1(facets, (short)16, (short)0);
      types.put("token", tokenDV);
      facets.whiteSpace = 2;
      facets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
      XSSimpleTypeDecl languageDV = new XSSimpleTypeDecl(tokenDV, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)23);
      languageDV.applyFacets1(facets, (short)24, (short)0);
      types.put("language", languageDV);
      facets.whiteSpace = 2;
      XSSimpleTypeDecl nameDV = new XSSimpleTypeDecl(tokenDV, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)25);
      nameDV.applyFacets1(facets, (short)16, (short)0, (short)2);
      types.put("Name", nameDV);
      facets.whiteSpace = 2;
      XSSimpleTypeDecl ncnameDV = new XSSimpleTypeDecl(nameDV, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)26);
      ncnameDV.applyFacets1(facets, (short)16, (short)0, (short)3);
      types.put("NCName", ncnameDV);
      types.put("ID", new XSSimpleTypeDecl(ncnameDV, "ID", (short)21, (short)0, false, false, false, true, (short)27));
      XSSimpleTypeDecl idrefDV = new XSSimpleTypeDecl(ncnameDV, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
      types.put("IDREF", idrefDV);
      facets.minLength = 1;
      XSSimpleTypeDecl tempDV = new XSSimpleTypeDecl((String)null, "http://www.w3.org/2001/XMLSchema", (short)0, idrefDV, true, (XSObjectList)null);
      XSSimpleTypeDecl idrefsDV = new XSSimpleTypeDecl(tempDV, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null);
      idrefsDV.applyFacets1(facets, (short)2, (short)0);
      types.put("IDREFS", idrefsDV);
      XSSimpleTypeDecl entityDV = new XSSimpleTypeDecl(ncnameDV, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
      types.put("ENTITY", entityDV);
      facets.minLength = 1;
      tempDV = new XSSimpleTypeDecl((String)null, "http://www.w3.org/2001/XMLSchema", (short)0, entityDV, true, (XSObjectList)null);
      XSSimpleTypeDecl entitiesDV = new XSSimpleTypeDecl(tempDV, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null);
      entitiesDV.applyFacets1(facets, (short)2, (short)0);
      types.put("ENTITIES", entitiesDV);
      facets.whiteSpace = 2;
      XSSimpleTypeDecl nmtokenDV = new XSSimpleTypeDecl(tokenDV, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)24);
      nmtokenDV.applyFacets1(facets, (short)16, (short)0, (short)1);
      types.put("NMTOKEN", nmtokenDV);
      facets.minLength = 1;
      tempDV = new XSSimpleTypeDecl((String)null, "http://www.w3.org/2001/XMLSchema", (short)0, nmtokenDV, true, (XSObjectList)null);
      XSSimpleTypeDecl nmtokensDV = new XSSimpleTypeDecl(tempDV, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null);
      nmtokensDV.applyFacets1(facets, (short)2, (short)0);
      types.put("NMTOKENS", nmtokensDV);
   }

   static {
      createBuiltInTypes(fFullTypes);
   }
}
