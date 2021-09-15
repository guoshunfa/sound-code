package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSGrammarBucket;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XIntPool;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class XSAttributeChecker {
   private static final String ELEMENT_N = "element_n";
   private static final String ELEMENT_R = "element_r";
   private static final String ATTRIBUTE_N = "attribute_n";
   private static final String ATTRIBUTE_R = "attribute_r";
   private static int ATTIDX_COUNT = 0;
   public static final int ATTIDX_ABSTRACT;
   public static final int ATTIDX_AFORMDEFAULT;
   public static final int ATTIDX_BASE;
   public static final int ATTIDX_BLOCK;
   public static final int ATTIDX_BLOCKDEFAULT;
   public static final int ATTIDX_DEFAULT;
   public static final int ATTIDX_EFORMDEFAULT;
   public static final int ATTIDX_FINAL;
   public static final int ATTIDX_FINALDEFAULT;
   public static final int ATTIDX_FIXED;
   public static final int ATTIDX_FORM;
   public static final int ATTIDX_ID;
   public static final int ATTIDX_ITEMTYPE;
   public static final int ATTIDX_MAXOCCURS;
   public static final int ATTIDX_MEMBERTYPES;
   public static final int ATTIDX_MINOCCURS;
   public static final int ATTIDX_MIXED;
   public static final int ATTIDX_NAME;
   public static final int ATTIDX_NAMESPACE;
   public static final int ATTIDX_NAMESPACE_LIST;
   public static final int ATTIDX_NILLABLE;
   public static final int ATTIDX_NONSCHEMA;
   public static final int ATTIDX_PROCESSCONTENTS;
   public static final int ATTIDX_PUBLIC;
   public static final int ATTIDX_REF;
   public static final int ATTIDX_REFER;
   public static final int ATTIDX_SCHEMALOCATION;
   public static final int ATTIDX_SOURCE;
   public static final int ATTIDX_SUBSGROUP;
   public static final int ATTIDX_SYSTEM;
   public static final int ATTIDX_TARGETNAMESPACE;
   public static final int ATTIDX_TYPE;
   public static final int ATTIDX_USE;
   public static final int ATTIDX_VALUE;
   public static final int ATTIDX_ENUMNSDECLS;
   public static final int ATTIDX_VERSION;
   public static final int ATTIDX_XML_LANG;
   public static final int ATTIDX_XPATH;
   public static final int ATTIDX_FROMDEFAULT;
   public static final int ATTIDX_ISRETURNED;
   private static final XIntPool fXIntPool;
   private static final XInt INT_QUALIFIED;
   private static final XInt INT_UNQUALIFIED;
   private static final XInt INT_EMPTY_SET;
   private static final XInt INT_ANY_STRICT;
   private static final XInt INT_ANY_LAX;
   private static final XInt INT_ANY_SKIP;
   private static final XInt INT_ANY_ANY;
   private static final XInt INT_ANY_LIST;
   private static final XInt INT_ANY_NOT;
   private static final XInt INT_USE_OPTIONAL;
   private static final XInt INT_USE_REQUIRED;
   private static final XInt INT_USE_PROHIBITED;
   private static final XInt INT_WS_PRESERVE;
   private static final XInt INT_WS_REPLACE;
   private static final XInt INT_WS_COLLAPSE;
   private static final XInt INT_UNBOUNDED;
   private static final Map fEleAttrsMapG;
   private static final Map fEleAttrsMapL;
   protected static final int DT_ANYURI = 0;
   protected static final int DT_ID = 1;
   protected static final int DT_QNAME = 2;
   protected static final int DT_STRING = 3;
   protected static final int DT_TOKEN = 4;
   protected static final int DT_NCNAME = 5;
   protected static final int DT_XPATH = 6;
   protected static final int DT_XPATH1 = 7;
   protected static final int DT_LANGUAGE = 8;
   protected static final int DT_COUNT = 9;
   private static final XSSimpleType[] fExtraDVs;
   protected static final int DT_BLOCK = -1;
   protected static final int DT_BLOCK1 = -2;
   protected static final int DT_FINAL = -3;
   protected static final int DT_FINAL1 = -4;
   protected static final int DT_FINAL2 = -5;
   protected static final int DT_FORM = -6;
   protected static final int DT_MAXOCCURS = -7;
   protected static final int DT_MAXOCCURS1 = -8;
   protected static final int DT_MEMBERTYPES = -9;
   protected static final int DT_MINOCCURS1 = -10;
   protected static final int DT_NAMESPACE = -11;
   protected static final int DT_PROCESSCONTENTS = -12;
   protected static final int DT_USE = -13;
   protected static final int DT_WHITESPACE = -14;
   protected static final int DT_BOOLEAN = -15;
   protected static final int DT_NONNEGINT = -16;
   protected static final int DT_POSINT = -17;
   protected XSDHandler fSchemaHandler = null;
   protected SymbolTable fSymbolTable = null;
   protected Map fNonSchemaAttrs = new HashMap();
   protected Vector fNamespaceList = new Vector();
   protected boolean[] fSeen;
   private static boolean[] fSeenTemp;
   static final int INIT_POOL_SIZE = 10;
   static final int INC_POOL_SIZE = 10;
   Object[][] fArrayPool;
   private static Object[] fTempArray;
   int fPoolPos;

   public XSAttributeChecker(XSDHandler schemaHandler) {
      this.fSeen = new boolean[ATTIDX_COUNT];
      this.fArrayPool = new Object[10][ATTIDX_COUNT];
      this.fPoolPos = 0;
      this.fSchemaHandler = schemaHandler;
   }

   public void reset(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
      this.fNonSchemaAttrs.clear();
   }

   public Object[] checkAttributes(Element element, boolean isGlobal, XSDocumentInfo schemaDoc) {
      return this.checkAttributes(element, isGlobal, schemaDoc, false);
   }

   public Object[] checkAttributes(Element element, boolean isGlobal, XSDocumentInfo schemaDoc, boolean enumAsQName) {
      if (element == null) {
         return null;
      } else {
         Attr[] attrs = DOMUtil.getAttrs(element);
         this.resolveNamespace(element, attrs, schemaDoc.fNamespaceSupport);
         String uri = DOMUtil.getNamespaceURI(element);
         String elName = DOMUtil.getLocalName(element);
         if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(uri)) {
            this.reportSchemaError("s4s-elt-schema-ns", new Object[]{elName}, element);
         }

         Map eleAttrsMap = fEleAttrsMapG;
         String lookupName = elName;
         if (!isGlobal) {
            eleAttrsMap = fEleAttrsMapL;
            if (elName.equals(SchemaSymbols.ELT_ELEMENT)) {
               if (DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null) {
                  lookupName = "element_r";
               } else {
                  lookupName = "element_n";
               }
            } else if (elName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
               if (DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null) {
                  lookupName = "attribute_r";
               } else {
                  lookupName = "attribute_n";
               }
            }
         }

         Container attrList = (Container)eleAttrsMap.get(lookupName);
         if (attrList == null) {
            this.reportSchemaError("s4s-elt-invalid", new Object[]{elName}, element);
            return null;
         } else {
            Object[] attrValues = this.getAvailableArray();
            long fromDefault = 0L;
            System.arraycopy(fSeenTemp, 0, this.fSeen, 0, ATTIDX_COUNT);
            int length = attrs.length;
            Attr sattr = null;

            String attrVal;
            for(int i = 0; i < length; ++i) {
               sattr = attrs[i];
               String attrName = sattr.getName();
               String attrURI = DOMUtil.getNamespaceURI(sattr);
               attrVal = DOMUtil.getValue(sattr);
               if (attrName.startsWith("xml")) {
                  String attrPrefix = DOMUtil.getPrefix(sattr);
                  if ("xmlns".equals(attrPrefix) || "xmlns".equals(attrName)) {
                     continue;
                  }

                  if (SchemaSymbols.ATT_XML_LANG.equals(attrName) && (SchemaSymbols.ELT_SCHEMA.equals(elName) || SchemaSymbols.ELT_DOCUMENTATION.equals(elName))) {
                     attrURI = null;
                  }
               }

               if (attrURI != null && attrURI.length() != 0) {
                  if (attrURI.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
                     this.reportSchemaError("s4s-att-not-allowed", new Object[]{elName, attrName}, element);
                  } else {
                     if (attrValues[ATTIDX_NONSCHEMA] == null) {
                        attrValues[ATTIDX_NONSCHEMA] = new Vector(4, 2);
                     }

                     ((Vector)attrValues[ATTIDX_NONSCHEMA]).addElement(attrName);
                     ((Vector)attrValues[ATTIDX_NONSCHEMA]).addElement(attrVal);
                  }
               } else {
                  OneAttr oneAttr = attrList.get(attrName);
                  if (oneAttr == null) {
                     this.reportSchemaError("s4s-att-not-allowed", new Object[]{elName, attrName}, element);
                  } else {
                     this.fSeen[oneAttr.valueIndex] = true;

                     try {
                        if (oneAttr.dvIndex >= 0) {
                           if (oneAttr.dvIndex != 3 && oneAttr.dvIndex != 6 && oneAttr.dvIndex != 7) {
                              XSSimpleType dv = fExtraDVs[oneAttr.dvIndex];
                              Object avalue = dv.validate((String)attrVal, schemaDoc.fValidationContext, (ValidatedInfo)null);
                              if (oneAttr.dvIndex == 2) {
                                 QName qname = (QName)avalue;
                                 if (qname.prefix == XMLSymbols.EMPTY_STRING && qname.uri == null && schemaDoc.fIsChameleonSchema) {
                                    qname.uri = schemaDoc.fTargetNamespace;
                                 }
                              }

                              attrValues[oneAttr.valueIndex] = avalue;
                           } else {
                              attrValues[oneAttr.valueIndex] = attrVal;
                           }
                        } else {
                           attrValues[oneAttr.valueIndex] = this.validate(attrValues, attrName, attrVal, oneAttr.dvIndex, schemaDoc);
                        }
                     } catch (InvalidDatatypeValueException var24) {
                        this.reportSchemaError("s4s-att-invalid-value", new Object[]{elName, attrName, var24.getMessage()}, element);
                        if (oneAttr.dfltValue != null) {
                           attrValues[oneAttr.valueIndex] = oneAttr.dfltValue;
                        }
                     }

                     if (elName.equals(SchemaSymbols.ELT_ENUMERATION) && enumAsQName) {
                        attrValues[ATTIDX_ENUMNSDECLS] = new SchemaNamespaceSupport(schemaDoc.fNamespaceSupport);
                     }
                  }
               }
            }

            OneAttr[] reqAttrs = attrList.values;

            int min;
            for(min = 0; min < reqAttrs.length; ++min) {
               OneAttr oneAttr = reqAttrs[min];
               if (oneAttr.dfltValue != null && !this.fSeen[oneAttr.valueIndex]) {
                  attrValues[oneAttr.valueIndex] = oneAttr.dfltValue;
                  fromDefault |= (long)(1 << oneAttr.valueIndex);
               }
            }

            attrValues[ATTIDX_FROMDEFAULT] = new Long(fromDefault);
            if (attrValues[ATTIDX_MAXOCCURS] != null) {
               min = ((XInt)attrValues[ATTIDX_MINOCCURS]).intValue();
               int max = ((XInt)attrValues[ATTIDX_MAXOCCURS]).intValue();
               if (max != -1) {
                  if (this.fSchemaHandler.fSecurityManager != null) {
                     attrVal = element.getLocalName();
                     boolean optimize = (attrVal.equals("element") || attrVal.equals("any")) && element.getNextSibling() == null && element.getPreviousSibling() == null && element.getParentNode().getLocalName().equals("sequence");
                     if (!optimize) {
                        int maxOccurNodeLimit = this.fSchemaHandler.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT);
                        if (max > maxOccurNodeLimit && !this.fSchemaHandler.fSecurityManager.isNoLimit(maxOccurNodeLimit)) {
                           this.reportSchemaFatalError("MaxOccurLimit", new Object[]{new Integer(maxOccurNodeLimit)}, element);
                           attrValues[ATTIDX_MAXOCCURS] = fXIntPool.getXInt(maxOccurNodeLimit);
                           max = maxOccurNodeLimit;
                        }
                     }
                  }

                  if (min > max) {
                     this.reportSchemaError("p-props-correct.2.1", new Object[]{elName, attrValues[ATTIDX_MINOCCURS], attrValues[ATTIDX_MAXOCCURS]}, element);
                     attrValues[ATTIDX_MINOCCURS] = attrValues[ATTIDX_MAXOCCURS];
                  }
               }
            }

            return attrValues;
         }
      }
   }

   private Object validate(Object[] attrValues, String attr, String ivalue, int dvIndex, XSDocumentInfo schemaDoc) throws InvalidDatatypeValueException {
      if (ivalue == null) {
         return null;
      } else {
         String value = XMLChar.trim(ivalue);
         Object retValue = null;
         int choice;
         StringTokenizer tokens;
         String token;
         switch(dvIndex) {
         case -17:
            try {
               if (value.length() > 0 && value.charAt(0) == '+') {
                  value = value.substring(1);
               }

               retValue = fXIntPool.getXInt(Integer.parseInt(value));
            } catch (NumberFormatException var17) {
               throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{value, "positiveInteger"});
            }

            if (((XInt)retValue).intValue() <= 0) {
               throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{value, "positiveInteger"});
            }
            break;
         case -16:
            try {
               if (value.length() > 0 && value.charAt(0) == '+') {
                  value = value.substring(1);
               }

               retValue = fXIntPool.getXInt(Integer.parseInt(value));
            } catch (NumberFormatException var16) {
               throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{value, "nonNegativeInteger"});
            }

            if (((XInt)retValue).intValue() < 0) {
               throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{value, "nonNegativeInteger"});
            }
            break;
         case -15:
            if (!value.equals("false") && !value.equals("0")) {
               if (!value.equals("true") && !value.equals("1")) {
                  throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{value, "boolean"});
               }

               retValue = Boolean.TRUE;
            } else {
               retValue = Boolean.FALSE;
            }
            break;
         case -14:
            if (value.equals("preserve")) {
               retValue = INT_WS_PRESERVE;
            } else if (value.equals("replace")) {
               retValue = INT_WS_REPLACE;
            } else {
               if (!value.equals("collapse")) {
                  throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{value, "(preserve | replace | collapse)"});
               }

               retValue = INT_WS_COLLAPSE;
            }
            break;
         case -13:
            if (value.equals("optional")) {
               retValue = INT_USE_OPTIONAL;
            } else if (value.equals("required")) {
               retValue = INT_USE_REQUIRED;
            } else {
               if (!value.equals("prohibited")) {
                  throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{value, "(optional | prohibited | required)"});
               }

               retValue = INT_USE_PROHIBITED;
            }
            break;
         case -12:
            if (value.equals("strict")) {
               retValue = INT_ANY_STRICT;
            } else if (value.equals("lax")) {
               retValue = INT_ANY_LAX;
            } else {
               if (!value.equals("skip")) {
                  throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{value, "(lax | skip | strict)"});
               }

               retValue = INT_ANY_SKIP;
            }
            break;
         case -11:
            if (value.equals("##any")) {
               retValue = INT_ANY_ANY;
            } else if (value.equals("##other")) {
               retValue = INT_ANY_NOT;
               String[] list = new String[]{schemaDoc.fTargetNamespace, null};
               attrValues[ATTIDX_NAMESPACE_LIST] = list;
            } else {
               retValue = INT_ANY_LIST;
               this.fNamespaceList.removeAllElements();
               tokens = new StringTokenizer(value, " \n\t\r");

               try {
                  while(tokens.hasMoreTokens()) {
                     token = tokens.nextToken();
                     String tempNamespace;
                     if (token.equals("##local")) {
                        tempNamespace = null;
                     } else if (token.equals("##targetNamespace")) {
                        tempNamespace = schemaDoc.fTargetNamespace;
                     } else {
                        fExtraDVs[0].validate((String)token, schemaDoc.fValidationContext, (ValidatedInfo)null);
                        tempNamespace = this.fSymbolTable.addSymbol(token);
                     }

                     if (!this.fNamespaceList.contains(tempNamespace)) {
                        this.fNamespaceList.addElement(tempNamespace);
                     }
                  }
               } catch (InvalidDatatypeValueException var18) {
                  throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{value, "((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )"});
               }

               int num = this.fNamespaceList.size();
               String[] list = new String[num];
               this.fNamespaceList.copyInto(list);
               attrValues[ATTIDX_NAMESPACE_LIST] = list;
            }
            break;
         case -10:
            if (value.equals("0")) {
               retValue = fXIntPool.getXInt(0);
            } else {
               if (!value.equals("1")) {
                  throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{value, "(0 | 1)"});
               }

               retValue = fXIntPool.getXInt(1);
            }
            break;
         case -9:
            Vector memberType = new Vector();

            try {
               QName qname;
               for(tokens = new StringTokenizer(value, " \n\t\r"); tokens.hasMoreTokens(); memberType.addElement(qname)) {
                  token = tokens.nextToken();
                  qname = (QName)fExtraDVs[2].validate((String)token, schemaDoc.fValidationContext, (ValidatedInfo)null);
                  if (qname.prefix == XMLSymbols.EMPTY_STRING && qname.uri == null && schemaDoc.fIsChameleonSchema) {
                     qname.uri = schemaDoc.fTargetNamespace;
                  }
               }

               retValue = memberType;
               break;
            } catch (InvalidDatatypeValueException var19) {
               throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.2", new Object[]{value, "(List of QName)"});
            }
         case -8:
            if (!value.equals("1")) {
               throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{value, "(1)"});
            }

            retValue = fXIntPool.getXInt(1);
            break;
         case -7:
            if (value.equals("unbounded")) {
               retValue = INT_UNBOUNDED;
            } else {
               try {
                  retValue = this.validate(attrValues, attr, value, -16, schemaDoc);
               } catch (NumberFormatException var15) {
                  throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{value, "(nonNegativeInteger | unbounded)"});
               }
            }
            break;
         case -6:
            if (value.equals("qualified")) {
               retValue = INT_QUALIFIED;
            } else {
               if (!value.equals("unqualified")) {
                  throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{value, "(qualified | unqualified)"});
               }

               retValue = INT_UNQUALIFIED;
            }
            break;
         case -5:
            choice = 0;
            if (value.equals("#all")) {
               choice = 31;
            } else {
               tokens = new StringTokenizer(value, " \n\t\r");

               while(tokens.hasMoreTokens()) {
                  token = tokens.nextToken();
                  if (token.equals("extension")) {
                     choice |= 1;
                  } else if (token.equals("restriction")) {
                     choice |= 2;
                  } else if (token.equals("list")) {
                     choice |= 16;
                  } else {
                     if (!token.equals("union")) {
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{value, "(#all | List of (extension | restriction | list | union))"});
                     }

                     choice |= 8;
                  }
               }
            }

            retValue = fXIntPool.getXInt(choice);
            break;
         case -4:
            choice = 0;
            if (value.equals("#all")) {
               choice = 31;
            } else {
               tokens = new StringTokenizer(value, " \n\t\r");

               while(tokens.hasMoreTokens()) {
                  token = tokens.nextToken();
                  if (token.equals("list")) {
                     choice |= 16;
                  } else if (token.equals("union")) {
                     choice |= 8;
                  } else {
                     if (!token.equals("restriction")) {
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{value, "(#all | List of (list | union | restriction))"});
                     }

                     choice |= 2;
                  }
               }
            }

            retValue = fXIntPool.getXInt(choice);
            break;
         case -3:
         case -2:
            choice = 0;
            if (value.equals("#all")) {
               choice = 31;
            } else {
               tokens = new StringTokenizer(value, " \n\t\r");

               while(tokens.hasMoreTokens()) {
                  token = tokens.nextToken();
                  if (token.equals("extension")) {
                     choice |= 1;
                  } else {
                     if (!token.equals("restriction")) {
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{value, "(#all | List of (extension | restriction))"});
                     }

                     choice |= 2;
                  }
               }
            }

            retValue = fXIntPool.getXInt(choice);
            break;
         case -1:
            choice = 0;
            if (value.equals("#all")) {
               choice = 7;
            } else {
               tokens = new StringTokenizer(value, " \n\t\r");

               while(tokens.hasMoreTokens()) {
                  token = tokens.nextToken();
                  if (token.equals("extension")) {
                     choice |= 1;
                  } else if (token.equals("restriction")) {
                     choice |= 2;
                  } else {
                     if (!token.equals("substitution")) {
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{value, "(#all | List of (extension | restriction | substitution))"});
                     }

                     choice |= 4;
                  }
               }
            }

            retValue = fXIntPool.getXInt(choice);
         }

         return retValue;
      }
   }

   void reportSchemaFatalError(String key, Object[] args, Element ele) {
      this.fSchemaHandler.reportSchemaFatalError(key, args, ele);
   }

   void reportSchemaError(String key, Object[] args, Element ele) {
      this.fSchemaHandler.reportSchemaError(key, args, ele);
   }

   public void checkNonSchemaAttributes(XSGrammarBucket grammarBucket) {
      Iterator entries = this.fNonSchemaAttrs.entrySet().iterator();

      while(true) {
         Map.Entry entry;
         XSSimpleType dv;
         do {
            XSAttributeDecl attrDecl;
            do {
               String attrLocal;
               SchemaGrammar sGrammar;
               do {
                  if (!entries.hasNext()) {
                     return;
                  }

                  entry = (Map.Entry)entries.next();
                  String attrRName = (String)entry.getKey();
                  String attrURI = attrRName.substring(0, attrRName.indexOf(44));
                  attrLocal = attrRName.substring(attrRName.indexOf(44) + 1);
                  sGrammar = grammarBucket.getGrammar(attrURI);
               } while(sGrammar == null);

               attrDecl = sGrammar.getGlobalAttributeDecl(attrLocal);
            } while(attrDecl == null);

            dv = (XSSimpleType)attrDecl.getTypeDefinition();
         } while(dv == null);

         Vector values = (Vector)entry.getValue();
         String attrName = (String)values.elementAt(0);
         int count = values.size();

         for(int i = 1; i < count; i += 2) {
            String elName = (String)values.elementAt(i);

            try {
               dv.validate((String)((String)values.elementAt(i + 1)), (ValidationContext)null, (ValidatedInfo)null);
            } catch (InvalidDatatypeValueException var16) {
               this.reportSchemaError("s4s-att-invalid-value", new Object[]{elName, attrName, var16.getMessage()}, (Element)null);
            }
         }
      }
   }

   public static String normalize(String content, short ws) {
      int len = content == null ? 0 : content.length();
      if (len != 0 && ws != 0) {
         StringBuffer sb = new StringBuffer();
         char ch;
         int i;
         if (ws == 1) {
            for(i = 0; i < len; ++i) {
               ch = content.charAt(i);
               if (ch != '\t' && ch != '\n' && ch != '\r') {
                  sb.append(ch);
               } else {
                  sb.append(' ');
               }
            }
         } else {
            boolean isLeading = true;

            for(i = 0; i < len; ++i) {
               ch = content.charAt(i);
               if (ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                  sb.append(ch);
                  isLeading = false;
               } else {
                  while(i < len - 1) {
                     ch = content.charAt(i + 1);
                     if (ch != '\t' && ch != '\n' && ch != '\r' && ch != ' ') {
                        break;
                     }

                     ++i;
                  }

                  if (i < len - 1 && !isLeading) {
                     sb.append(' ');
                  }
               }
            }
         }

         return sb.toString();
      } else {
         return content;
      }
   }

   protected Object[] getAvailableArray() {
      if (this.fArrayPool.length == this.fPoolPos) {
         this.fArrayPool = new Object[this.fPoolPos + 10][];

         for(int i = this.fPoolPos; i < this.fArrayPool.length; ++i) {
            this.fArrayPool[i] = new Object[ATTIDX_COUNT];
         }
      }

      Object[] retArray = this.fArrayPool[this.fPoolPos];
      this.fArrayPool[this.fPoolPos++] = null;
      System.arraycopy(fTempArray, 0, retArray, 0, ATTIDX_COUNT - 1);
      retArray[ATTIDX_ISRETURNED] = Boolean.FALSE;
      return retArray;
   }

   public void returnAttrArray(Object[] attrArray, XSDocumentInfo schemaDoc) {
      if (schemaDoc != null) {
         schemaDoc.fNamespaceSupport.popContext();
      }

      if (this.fPoolPos != 0 && attrArray != null && attrArray.length == ATTIDX_COUNT && !(Boolean)attrArray[ATTIDX_ISRETURNED]) {
         attrArray[ATTIDX_ISRETURNED] = Boolean.TRUE;
         if (attrArray[ATTIDX_NONSCHEMA] != null) {
            ((Vector)attrArray[ATTIDX_NONSCHEMA]).clear();
         }

         this.fArrayPool[--this.fPoolPos] = attrArray;
      }
   }

   public void resolveNamespace(Element element, Attr[] attrs, SchemaNamespaceSupport nsSupport) {
      nsSupport.pushContext();
      int length = attrs.length;
      Attr sattr = null;

      for(int i = 0; i < length; ++i) {
         sattr = attrs[i];
         String rawname = DOMUtil.getName(sattr);
         String prefix = null;
         if (rawname.equals(XMLSymbols.PREFIX_XMLNS)) {
            prefix = XMLSymbols.EMPTY_STRING;
         } else if (rawname.startsWith("xmlns:")) {
            prefix = this.fSymbolTable.addSymbol(DOMUtil.getLocalName(sattr));
         }

         if (prefix != null) {
            String uri = this.fSymbolTable.addSymbol(DOMUtil.getValue(sattr));
            nsSupport.declarePrefix(prefix, uri.length() != 0 ? uri : null);
         }
      }

   }

   static {
      ATTIDX_ABSTRACT = ATTIDX_COUNT++;
      ATTIDX_AFORMDEFAULT = ATTIDX_COUNT++;
      ATTIDX_BASE = ATTIDX_COUNT++;
      ATTIDX_BLOCK = ATTIDX_COUNT++;
      ATTIDX_BLOCKDEFAULT = ATTIDX_COUNT++;
      ATTIDX_DEFAULT = ATTIDX_COUNT++;
      ATTIDX_EFORMDEFAULT = ATTIDX_COUNT++;
      ATTIDX_FINAL = ATTIDX_COUNT++;
      ATTIDX_FINALDEFAULT = ATTIDX_COUNT++;
      ATTIDX_FIXED = ATTIDX_COUNT++;
      ATTIDX_FORM = ATTIDX_COUNT++;
      ATTIDX_ID = ATTIDX_COUNT++;
      ATTIDX_ITEMTYPE = ATTIDX_COUNT++;
      ATTIDX_MAXOCCURS = ATTIDX_COUNT++;
      ATTIDX_MEMBERTYPES = ATTIDX_COUNT++;
      ATTIDX_MINOCCURS = ATTIDX_COUNT++;
      ATTIDX_MIXED = ATTIDX_COUNT++;
      ATTIDX_NAME = ATTIDX_COUNT++;
      ATTIDX_NAMESPACE = ATTIDX_COUNT++;
      ATTIDX_NAMESPACE_LIST = ATTIDX_COUNT++;
      ATTIDX_NILLABLE = ATTIDX_COUNT++;
      ATTIDX_NONSCHEMA = ATTIDX_COUNT++;
      ATTIDX_PROCESSCONTENTS = ATTIDX_COUNT++;
      ATTIDX_PUBLIC = ATTIDX_COUNT++;
      ATTIDX_REF = ATTIDX_COUNT++;
      ATTIDX_REFER = ATTIDX_COUNT++;
      ATTIDX_SCHEMALOCATION = ATTIDX_COUNT++;
      ATTIDX_SOURCE = ATTIDX_COUNT++;
      ATTIDX_SUBSGROUP = ATTIDX_COUNT++;
      ATTIDX_SYSTEM = ATTIDX_COUNT++;
      ATTIDX_TARGETNAMESPACE = ATTIDX_COUNT++;
      ATTIDX_TYPE = ATTIDX_COUNT++;
      ATTIDX_USE = ATTIDX_COUNT++;
      ATTIDX_VALUE = ATTIDX_COUNT++;
      ATTIDX_ENUMNSDECLS = ATTIDX_COUNT++;
      ATTIDX_VERSION = ATTIDX_COUNT++;
      ATTIDX_XML_LANG = ATTIDX_COUNT++;
      ATTIDX_XPATH = ATTIDX_COUNT++;
      ATTIDX_FROMDEFAULT = ATTIDX_COUNT++;
      ATTIDX_ISRETURNED = ATTIDX_COUNT++;
      fXIntPool = new XIntPool();
      INT_QUALIFIED = fXIntPool.getXInt(1);
      INT_UNQUALIFIED = fXIntPool.getXInt(0);
      INT_EMPTY_SET = fXIntPool.getXInt(0);
      INT_ANY_STRICT = fXIntPool.getXInt(1);
      INT_ANY_LAX = fXIntPool.getXInt(3);
      INT_ANY_SKIP = fXIntPool.getXInt(2);
      INT_ANY_ANY = fXIntPool.getXInt(1);
      INT_ANY_LIST = fXIntPool.getXInt(3);
      INT_ANY_NOT = fXIntPool.getXInt(2);
      INT_USE_OPTIONAL = fXIntPool.getXInt(0);
      INT_USE_REQUIRED = fXIntPool.getXInt(1);
      INT_USE_PROHIBITED = fXIntPool.getXInt(2);
      INT_WS_PRESERVE = fXIntPool.getXInt(0);
      INT_WS_REPLACE = fXIntPool.getXInt(1);
      INT_WS_COLLAPSE = fXIntPool.getXInt(2);
      INT_UNBOUNDED = fXIntPool.getXInt(-1);
      fEleAttrsMapG = new HashMap(29);
      fEleAttrsMapL = new HashMap(79);
      fExtraDVs = new XSSimpleType[9];
      SchemaGrammar grammar = SchemaGrammar.SG_SchemaNS;
      fExtraDVs[0] = (XSSimpleType)grammar.getGlobalTypeDecl("anyURI");
      fExtraDVs[1] = (XSSimpleType)grammar.getGlobalTypeDecl("ID");
      fExtraDVs[2] = (XSSimpleType)grammar.getGlobalTypeDecl("QName");
      fExtraDVs[3] = (XSSimpleType)grammar.getGlobalTypeDecl("string");
      fExtraDVs[4] = (XSSimpleType)grammar.getGlobalTypeDecl("token");
      fExtraDVs[5] = (XSSimpleType)grammar.getGlobalTypeDecl("NCName");
      fExtraDVs[6] = fExtraDVs[3];
      fExtraDVs[6] = fExtraDVs[3];
      fExtraDVs[8] = (XSSimpleType)grammar.getGlobalTypeDecl("language");
      int attCount = 0;
      int attCount = attCount + 1;
      int ATT_ATTRIBUTE_FD_D = attCount++;
      int ATT_BASE_R = attCount++;
      int ATT_BASE_N = attCount++;
      int ATT_BLOCK_N = attCount++;
      int ATT_BLOCK1_N = attCount++;
      int ATT_BLOCK_D_D = attCount++;
      int ATT_DEFAULT_N = attCount++;
      int ATT_ELEMENT_FD_D = attCount++;
      int ATT_FINAL_N = attCount++;
      int ATT_FINAL1_N = attCount++;
      int ATT_FINAL_D_D = attCount++;
      int ATT_FIXED_N = attCount++;
      int ATT_FIXED_D = attCount++;
      int ATT_FORM_N = attCount++;
      int ATT_ID_N = attCount++;
      int ATT_ITEMTYPE_N = attCount++;
      int ATT_MAXOCCURS_D = attCount++;
      int ATT_MAXOCCURS1_D = attCount++;
      int ATT_MEMBER_T_N = attCount++;
      int ATT_MINOCCURS_D = attCount++;
      int ATT_MINOCCURS1_D = attCount++;
      int ATT_MIXED_D = attCount++;
      int ATT_MIXED_N = attCount++;
      int ATT_NAME_R = attCount++;
      int ATT_NAMESPACE_D = attCount++;
      int ATT_NAMESPACE_N = attCount++;
      int ATT_NILLABLE_D = attCount++;
      int ATT_PROCESS_C_D = attCount++;
      int ATT_PUBLIC_R = attCount++;
      int ATT_REF_R = attCount++;
      int ATT_REFER_R = attCount++;
      int ATT_SCHEMA_L_R = attCount++;
      int ATT_SCHEMA_L_N = attCount++;
      int ATT_SOURCE_N = attCount++;
      int ATT_SUBSTITUTION_G_N = attCount++;
      int ATT_SYSTEM_N = attCount++;
      int ATT_TARGET_N_N = attCount++;
      int ATT_TYPE_N = attCount++;
      int ATT_USE_D = attCount++;
      int ATT_VALUE_NNI_N = attCount++;
      int ATT_VALUE_PI_N = attCount++;
      int ATT_VALUE_STR_N = attCount++;
      int ATT_VALUE_WS_N = attCount++;
      int ATT_VERSION_N = attCount++;
      int ATT_XML_LANG = attCount++;
      int ATT_XPATH_R = attCount++;
      int ATT_XPATH1_R = attCount++;
      OneAttr[] allAttrs = new OneAttr[attCount];
      allAttrs[attCount] = new OneAttr(SchemaSymbols.ATT_ABSTRACT, -15, ATTIDX_ABSTRACT, Boolean.FALSE);
      allAttrs[ATT_ATTRIBUTE_FD_D] = new OneAttr(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, -6, ATTIDX_AFORMDEFAULT, INT_UNQUALIFIED);
      allAttrs[ATT_BASE_R] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, (Object)null);
      allAttrs[ATT_BASE_N] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, (Object)null);
      allAttrs[ATT_BLOCK_N] = new OneAttr(SchemaSymbols.ATT_BLOCK, -1, ATTIDX_BLOCK, (Object)null);
      allAttrs[ATT_BLOCK1_N] = new OneAttr(SchemaSymbols.ATT_BLOCK, -2, ATTIDX_BLOCK, (Object)null);
      allAttrs[ATT_BLOCK_D_D] = new OneAttr(SchemaSymbols.ATT_BLOCKDEFAULT, -1, ATTIDX_BLOCKDEFAULT, INT_EMPTY_SET);
      allAttrs[ATT_DEFAULT_N] = new OneAttr(SchemaSymbols.ATT_DEFAULT, 3, ATTIDX_DEFAULT, (Object)null);
      allAttrs[ATT_ELEMENT_FD_D] = new OneAttr(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, -6, ATTIDX_EFORMDEFAULT, INT_UNQUALIFIED);
      allAttrs[ATT_FINAL_N] = new OneAttr(SchemaSymbols.ATT_FINAL, -3, ATTIDX_FINAL, (Object)null);
      allAttrs[ATT_FINAL1_N] = new OneAttr(SchemaSymbols.ATT_FINAL, -4, ATTIDX_FINAL, (Object)null);
      allAttrs[ATT_FINAL_D_D] = new OneAttr(SchemaSymbols.ATT_FINALDEFAULT, -5, ATTIDX_FINALDEFAULT, INT_EMPTY_SET);
      allAttrs[ATT_FIXED_N] = new OneAttr(SchemaSymbols.ATT_FIXED, 3, ATTIDX_FIXED, (Object)null);
      allAttrs[ATT_FIXED_D] = new OneAttr(SchemaSymbols.ATT_FIXED, -15, ATTIDX_FIXED, Boolean.FALSE);
      allAttrs[ATT_FORM_N] = new OneAttr(SchemaSymbols.ATT_FORM, -6, ATTIDX_FORM, (Object)null);
      allAttrs[ATT_ID_N] = new OneAttr(SchemaSymbols.ATT_ID, 1, ATTIDX_ID, (Object)null);
      allAttrs[ATT_ITEMTYPE_N] = new OneAttr(SchemaSymbols.ATT_ITEMTYPE, 2, ATTIDX_ITEMTYPE, (Object)null);
      allAttrs[ATT_MAXOCCURS_D] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -7, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
      allAttrs[ATT_MAXOCCURS1_D] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -8, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
      allAttrs[ATT_MEMBER_T_N] = new OneAttr(SchemaSymbols.ATT_MEMBERTYPES, -9, ATTIDX_MEMBERTYPES, (Object)null);
      allAttrs[ATT_MINOCCURS_D] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -16, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
      allAttrs[ATT_MINOCCURS1_D] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -10, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
      allAttrs[ATT_MIXED_D] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, Boolean.FALSE);
      allAttrs[ATT_MIXED_N] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, (Object)null);
      allAttrs[ATT_NAME_R] = new OneAttr(SchemaSymbols.ATT_NAME, 5, ATTIDX_NAME, (Object)null);
      allAttrs[ATT_NAMESPACE_D] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -11, ATTIDX_NAMESPACE, INT_ANY_ANY);
      allAttrs[ATT_NAMESPACE_N] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, 0, ATTIDX_NAMESPACE, (Object)null);
      allAttrs[ATT_NILLABLE_D] = new OneAttr(SchemaSymbols.ATT_NILLABLE, -15, ATTIDX_NILLABLE, Boolean.FALSE);
      allAttrs[ATT_PROCESS_C_D] = new OneAttr(SchemaSymbols.ATT_PROCESSCONTENTS, -12, ATTIDX_PROCESSCONTENTS, INT_ANY_STRICT);
      allAttrs[ATT_PUBLIC_R] = new OneAttr(SchemaSymbols.ATT_PUBLIC, 4, ATTIDX_PUBLIC, (Object)null);
      allAttrs[ATT_REF_R] = new OneAttr(SchemaSymbols.ATT_REF, 2, ATTIDX_REF, (Object)null);
      allAttrs[ATT_REFER_R] = new OneAttr(SchemaSymbols.ATT_REFER, 2, ATTIDX_REFER, (Object)null);
      allAttrs[ATT_SCHEMA_L_R] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, (Object)null);
      allAttrs[ATT_SCHEMA_L_N] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, (Object)null);
      allAttrs[ATT_SOURCE_N] = new OneAttr(SchemaSymbols.ATT_SOURCE, 0, ATTIDX_SOURCE, (Object)null);
      allAttrs[ATT_SUBSTITUTION_G_N] = new OneAttr(SchemaSymbols.ATT_SUBSTITUTIONGROUP, 2, ATTIDX_SUBSGROUP, (Object)null);
      allAttrs[ATT_SYSTEM_N] = new OneAttr(SchemaSymbols.ATT_SYSTEM, 0, ATTIDX_SYSTEM, (Object)null);
      allAttrs[ATT_TARGET_N_N] = new OneAttr(SchemaSymbols.ATT_TARGETNAMESPACE, 0, ATTIDX_TARGETNAMESPACE, (Object)null);
      allAttrs[ATT_TYPE_N] = new OneAttr(SchemaSymbols.ATT_TYPE, 2, ATTIDX_TYPE, (Object)null);
      allAttrs[ATT_USE_D] = new OneAttr(SchemaSymbols.ATT_USE, -13, ATTIDX_USE, INT_USE_OPTIONAL);
      allAttrs[ATT_VALUE_NNI_N] = new OneAttr(SchemaSymbols.ATT_VALUE, -16, ATTIDX_VALUE, (Object)null);
      allAttrs[ATT_VALUE_PI_N] = new OneAttr(SchemaSymbols.ATT_VALUE, -17, ATTIDX_VALUE, (Object)null);
      allAttrs[ATT_VALUE_STR_N] = new OneAttr(SchemaSymbols.ATT_VALUE, 3, ATTIDX_VALUE, (Object)null);
      allAttrs[ATT_VALUE_WS_N] = new OneAttr(SchemaSymbols.ATT_VALUE, -14, ATTIDX_VALUE, (Object)null);
      allAttrs[ATT_VERSION_N] = new OneAttr(SchemaSymbols.ATT_VERSION, 4, ATTIDX_VERSION, (Object)null);
      allAttrs[ATT_XML_LANG] = new OneAttr(SchemaSymbols.ATT_XML_LANG, 8, ATTIDX_XML_LANG, (Object)null);
      allAttrs[ATT_XPATH_R] = new OneAttr(SchemaSymbols.ATT_XPATH, 6, ATTIDX_XPATH, (Object)null);
      allAttrs[ATT_XPATH1_R] = new OneAttr(SchemaSymbols.ATT_XPATH, 7, ATTIDX_XPATH, (Object)null);
      Container attrList = Container.getContainer(5);
      attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTE, attrList);
      attrList = Container.getContainer(7);
      attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
      attrList.put(SchemaSymbols.ATT_FORM, allAttrs[ATT_FORM_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
      attrList.put(SchemaSymbols.ATT_USE, allAttrs[ATT_USE_D]);
      fEleAttrsMapL.put("attribute_n", attrList);
      attrList = Container.getContainer(5);
      attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
      attrList.put(SchemaSymbols.ATT_USE, allAttrs[ATT_USE_D]);
      fEleAttrsMapL.put("attribute_r", attrList);
      attrList = Container.getContainer(10);
      attrList.put(SchemaSymbols.ATT_ABSTRACT, allAttrs[attCount]);
      attrList.put(SchemaSymbols.ATT_BLOCK, allAttrs[ATT_BLOCK_N]);
      attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
      attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      attrList.put(SchemaSymbols.ATT_NILLABLE, allAttrs[ATT_NILLABLE_D]);
      attrList.put(SchemaSymbols.ATT_SUBSTITUTIONGROUP, allAttrs[ATT_SUBSTITUTION_G_N]);
      attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_ELEMENT, attrList);
      attrList = Container.getContainer(10);
      attrList.put(SchemaSymbols.ATT_BLOCK, allAttrs[ATT_BLOCK_N]);
      attrList.put(SchemaSymbols.ATT_DEFAULT, allAttrs[ATT_DEFAULT_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_N]);
      attrList.put(SchemaSymbols.ATT_FORM, allAttrs[ATT_FORM_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      attrList.put(SchemaSymbols.ATT_NILLABLE, allAttrs[ATT_NILLABLE_D]);
      attrList.put(SchemaSymbols.ATT_TYPE, allAttrs[ATT_TYPE_N]);
      fEleAttrsMapL.put("element_n", attrList);
      attrList = Container.getContainer(4);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
      fEleAttrsMapL.put("element_r", attrList);
      attrList = Container.getContainer(6);
      attrList.put(SchemaSymbols.ATT_ABSTRACT, allAttrs[attCount]);
      attrList.put(SchemaSymbols.ATT_BLOCK, allAttrs[ATT_BLOCK1_N]);
      attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MIXED, allAttrs[ATT_MIXED_D]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_COMPLEXTYPE, attrList);
      attrList = Container.getContainer(4);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      attrList.put(SchemaSymbols.ATT_PUBLIC, allAttrs[ATT_PUBLIC_R]);
      attrList.put(SchemaSymbols.ATT_SYSTEM, allAttrs[ATT_SYSTEM_N]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_NOTATION, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MIXED, allAttrs[ATT_MIXED_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXTYPE, attrList);
      attrList = Container.getContainer(1);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLECONTENT, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_BASE, allAttrs[ATT_BASE_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_RESTRICTION, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_BASE, allAttrs[ATT_BASE_R]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_EXTENSION, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAMESPACE, allAttrs[ATT_NAMESPACE_D]);
      attrList.put(SchemaSymbols.ATT_PROCESSCONTENTS, allAttrs[ATT_PROCESS_C_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_ANYATTRIBUTE, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MIXED, allAttrs[ATT_MIXED_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXCONTENT, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_GROUP, attrList);
      attrList = Container.getContainer(4);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_REF, allAttrs[ATT_REF_R]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_GROUP, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS1_D]);
      attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS1_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_ALL, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_CHOICE, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_SEQUENCE, attrList);
      attrList = Container.getContainer(5);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MAXOCCURS, allAttrs[ATT_MAXOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_MINOCCURS, allAttrs[ATT_MINOCCURS_D]);
      attrList.put(SchemaSymbols.ATT_NAMESPACE, allAttrs[ATT_NAMESPACE_D]);
      attrList.put(SchemaSymbols.ATT_PROCESSCONTENTS, allAttrs[ATT_PROCESS_C_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_ANY, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_UNIQUE, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_KEY, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      attrList.put(SchemaSymbols.ATT_REFER, allAttrs[ATT_REFER_R]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_KEYREF, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_XPATH, allAttrs[ATT_XPATH_R]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_SELECTOR, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_XPATH, allAttrs[ATT_XPATH1_R]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_FIELD, attrList);
      attrList = Container.getContainer(1);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_ANNOTATION, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_ANNOTATION, attrList);
      attrList = Container.getContainer(1);
      attrList.put(SchemaSymbols.ATT_SOURCE, allAttrs[ATT_SOURCE_N]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_APPINFO, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_APPINFO, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_SOURCE, allAttrs[ATT_SOURCE_N]);
      attrList.put(SchemaSymbols.ATT_XML_LANG, allAttrs[ATT_XML_LANG]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_DOCUMENTATION, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_DOCUMENTATION, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL1_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAME, allAttrs[ATT_NAME_R]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_SIMPLETYPE, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_FINAL, allAttrs[ATT_FINAL1_N]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLETYPE, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_ITEMTYPE, allAttrs[ATT_ITEMTYPE_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_LIST, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_MEMBERTYPES, allAttrs[ATT_MEMBER_T_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_UNION, attrList);
      attrList = Container.getContainer(8);
      attrList.put(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, allAttrs[ATT_ATTRIBUTE_FD_D]);
      attrList.put(SchemaSymbols.ATT_BLOCKDEFAULT, allAttrs[ATT_BLOCK_D_D]);
      attrList.put(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, allAttrs[ATT_ELEMENT_FD_D]);
      attrList.put(SchemaSymbols.ATT_FINALDEFAULT, allAttrs[ATT_FINAL_D_D]);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_TARGETNAMESPACE, allAttrs[ATT_TARGET_N_N]);
      attrList.put(SchemaSymbols.ATT_VERSION, allAttrs[ATT_VERSION_N]);
      attrList.put(SchemaSymbols.ATT_XML_LANG, allAttrs[ATT_XML_LANG]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_SCHEMA, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_SCHEMALOCATION, allAttrs[ATT_SCHEMA_L_R]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_INCLUDE, attrList);
      fEleAttrsMapG.put(SchemaSymbols.ELT_REDEFINE, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_NAMESPACE, allAttrs[ATT_NAMESPACE_N]);
      attrList.put(SchemaSymbols.ATT_SCHEMALOCATION, allAttrs[ATT_SCHEMA_L_N]);
      fEleAttrsMapG.put(SchemaSymbols.ELT_IMPORT, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_NNI_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_LENGTH, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_MINLENGTH, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_MAXLENGTH, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_FRACTIONDIGITS, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_PI_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_TOTALDIGITS, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_STR_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_PATTERN, attrList);
      attrList = Container.getContainer(2);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_STR_N]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_ENUMERATION, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_WS_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_WHITESPACE, attrList);
      attrList = Container.getContainer(3);
      attrList.put(SchemaSymbols.ATT_ID, allAttrs[ATT_ID_N]);
      attrList.put(SchemaSymbols.ATT_VALUE, allAttrs[ATT_VALUE_STR_N]);
      attrList.put(SchemaSymbols.ATT_FIXED, allAttrs[ATT_FIXED_D]);
      fEleAttrsMapL.put(SchemaSymbols.ELT_MAXINCLUSIVE, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_MAXEXCLUSIVE, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_MININCLUSIVE, attrList);
      fEleAttrsMapL.put(SchemaSymbols.ELT_MINEXCLUSIVE, attrList);
      fSeenTemp = new boolean[ATTIDX_COUNT];
      fTempArray = new Object[ATTIDX_COUNT];
   }
}
