package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class BaseDVFactory extends SchemaDVFactory {
   static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
   static SymbolHash fBaseTypes = new SymbolHash(53);

   public XSSimpleType getBuiltInType(String name) {
      return (XSSimpleType)fBaseTypes.get(name);
   }

   public SymbolHash getBuiltInTypes() {
      return fBaseTypes.makeClone();
   }

   public XSSimpleType createTypeRestriction(String name, String targetNamespace, short finalSet, XSSimpleType base, XSObjectList annotations) {
      return new XSSimpleTypeDecl((XSSimpleTypeDecl)base, name, targetNamespace, finalSet, false, annotations);
   }

   public XSSimpleType createTypeList(String name, String targetNamespace, short finalSet, XSSimpleType itemType, XSObjectList annotations) {
      return new XSSimpleTypeDecl(name, targetNamespace, finalSet, (XSSimpleTypeDecl)itemType, false, annotations);
   }

   public XSSimpleType createTypeUnion(String name, String targetNamespace, short finalSet, XSSimpleType[] memberTypes, XSObjectList annotations) {
      int typeNum = memberTypes.length;
      XSSimpleTypeDecl[] mtypes = new XSSimpleTypeDecl[typeNum];
      System.arraycopy(memberTypes, 0, mtypes, 0, typeNum);
      return new XSSimpleTypeDecl(name, targetNamespace, finalSet, mtypes, annotations);
   }

   static void createBuiltInTypes(SymbolHash types) {
      String ANYSIMPLETYPE = "anySimpleType";
      String ANYURI = "anyURI";
      String BASE64BINARY = "base64Binary";
      String BOOLEAN = "boolean";
      String BYTE = "byte";
      String DATE = "date";
      String DATETIME = "dateTime";
      String DAY = "gDay";
      String DECIMAL = "decimal";
      String INT = "int";
      String INTEGER = "integer";
      String LONG = "long";
      String NEGATIVEINTEGER = "negativeInteger";
      String MONTH = "gMonth";
      String MONTHDAY = "gMonthDay";
      String NONNEGATIVEINTEGER = "nonNegativeInteger";
      String NONPOSITIVEINTEGER = "nonPositiveInteger";
      String POSITIVEINTEGER = "positiveInteger";
      String SHORT = "short";
      String STRING = "string";
      String TIME = "time";
      String UNSIGNEDBYTE = "unsignedByte";
      String UNSIGNEDINT = "unsignedInt";
      String UNSIGNEDLONG = "unsignedLong";
      String UNSIGNEDSHORT = "unsignedShort";
      String YEAR = "gYear";
      String YEARMONTH = "gYearMonth";
      XSFacets facets = new XSFacets();
      XSSimpleTypeDecl anySimpleType = XSSimpleTypeDecl.fAnySimpleType;
      types.put("anySimpleType", anySimpleType);
      XSSimpleTypeDecl stringDV = new XSSimpleTypeDecl(anySimpleType, "string", (short)1, (short)0, false, false, false, true, (short)2);
      types.put("string", stringDV);
      types.put("boolean", new XSSimpleTypeDecl(anySimpleType, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
      XSSimpleTypeDecl decimalDV = new XSSimpleTypeDecl(anySimpleType, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
      types.put("decimal", decimalDV);
      types.put("anyURI", new XSSimpleTypeDecl(anySimpleType, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
      types.put("base64Binary", new XSSimpleTypeDecl(anySimpleType, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
      types.put("dateTime", new XSSimpleTypeDecl(anySimpleType, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
      types.put("time", new XSSimpleTypeDecl(anySimpleType, "time", (short)8, (short)1, false, false, false, true, (short)9));
      types.put("date", new XSSimpleTypeDecl(anySimpleType, "date", (short)9, (short)1, false, false, false, true, (short)10));
      types.put("gYearMonth", new XSSimpleTypeDecl(anySimpleType, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
      types.put("gYear", new XSSimpleTypeDecl(anySimpleType, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
      types.put("gMonthDay", new XSSimpleTypeDecl(anySimpleType, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
      types.put("gDay", new XSSimpleTypeDecl(anySimpleType, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
      types.put("gMonth", new XSSimpleTypeDecl(anySimpleType, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
      XSSimpleTypeDecl integerDV = new XSSimpleTypeDecl(decimalDV, "integer", (short)24, (short)2, false, false, true, true, (short)30);
      types.put("integer", integerDV);
      facets.maxInclusive = "0";
      XSSimpleTypeDecl nonPositiveDV = new XSSimpleTypeDecl(integerDV, "nonPositiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)31);
      nonPositiveDV.applyFacets1(facets, (short)32, (short)0);
      types.put("nonPositiveInteger", nonPositiveDV);
      facets.maxInclusive = "-1";
      XSSimpleTypeDecl negativeDV = new XSSimpleTypeDecl(nonPositiveDV, "negativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)32);
      negativeDV.applyFacets1(facets, (short)32, (short)0);
      types.put("negativeInteger", negativeDV);
      facets.maxInclusive = "9223372036854775807";
      facets.minInclusive = "-9223372036854775808";
      XSSimpleTypeDecl longDV = new XSSimpleTypeDecl(integerDV, "long", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)33);
      longDV.applyFacets1(facets, (short)288, (short)0);
      types.put("long", longDV);
      facets.maxInclusive = "2147483647";
      facets.minInclusive = "-2147483648";
      XSSimpleTypeDecl intDV = new XSSimpleTypeDecl(longDV, "int", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)34);
      intDV.applyFacets1(facets, (short)288, (short)0);
      types.put("int", intDV);
      facets.maxInclusive = "32767";
      facets.minInclusive = "-32768";
      XSSimpleTypeDecl shortDV = new XSSimpleTypeDecl(intDV, "short", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)35);
      shortDV.applyFacets1(facets, (short)288, (short)0);
      types.put("short", shortDV);
      facets.maxInclusive = "127";
      facets.minInclusive = "-128";
      XSSimpleTypeDecl byteDV = new XSSimpleTypeDecl(shortDV, "byte", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)36);
      byteDV.applyFacets1(facets, (short)288, (short)0);
      types.put("byte", byteDV);
      facets.minInclusive = "0";
      XSSimpleTypeDecl nonNegativeDV = new XSSimpleTypeDecl(integerDV, "nonNegativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)37);
      nonNegativeDV.applyFacets1(facets, (short)256, (short)0);
      types.put("nonNegativeInteger", nonNegativeDV);
      facets.maxInclusive = "18446744073709551615";
      XSSimpleTypeDecl unsignedLongDV = new XSSimpleTypeDecl(nonNegativeDV, "unsignedLong", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)38);
      unsignedLongDV.applyFacets1(facets, (short)32, (short)0);
      types.put("unsignedLong", unsignedLongDV);
      facets.maxInclusive = "4294967295";
      XSSimpleTypeDecl unsignedIntDV = new XSSimpleTypeDecl(unsignedLongDV, "unsignedInt", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)39);
      unsignedIntDV.applyFacets1(facets, (short)32, (short)0);
      types.put("unsignedInt", unsignedIntDV);
      facets.maxInclusive = "65535";
      XSSimpleTypeDecl unsignedShortDV = new XSSimpleTypeDecl(unsignedIntDV, "unsignedShort", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)40);
      unsignedShortDV.applyFacets1(facets, (short)32, (short)0);
      types.put("unsignedShort", unsignedShortDV);
      facets.maxInclusive = "255";
      XSSimpleTypeDecl unsignedByteDV = new XSSimpleTypeDecl(unsignedShortDV, "unsignedByte", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)41);
      unsignedByteDV.applyFacets1(facets, (short)32, (short)0);
      types.put("unsignedByte", unsignedByteDV);
      facets.minInclusive = "1";
      XSSimpleTypeDecl positiveIntegerDV = new XSSimpleTypeDecl(nonNegativeDV, "positiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, (XSObjectList)null, (short)42);
      positiveIntegerDV.applyFacets1(facets, (short)256, (short)0);
      types.put("positiveInteger", positiveIntegerDV);
   }

   static {
      createBuiltInTypes(fBaseTypes);
   }
}
