package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import java.util.Vector;
import org.w3c.dom.Element;

abstract class XSDAbstractTraverser {
   protected static final String NO_NAME = "(no name)";
   protected static final int NOT_ALL_CONTEXT = 0;
   protected static final int PROCESSING_ALL_EL = 1;
   protected static final int GROUP_REF_WITH_ALL = 2;
   protected static final int CHILD_OF_GROUP = 4;
   protected static final int PROCESSING_ALL_GP = 8;
   protected XSDHandler fSchemaHandler = null;
   protected SymbolTable fSymbolTable = null;
   protected XSAttributeChecker fAttrChecker = null;
   protected boolean fValidateAnnotations = false;
   ValidationState fValidationState = new ValidationState();
   private static final XSSimpleType fQNameDV;
   private StringBuffer fPattern = new StringBuffer();
   private final XSFacets xsFacets = new XSFacets();

   XSDAbstractTraverser(XSDHandler handler, XSAttributeChecker attrChecker) {
      this.fSchemaHandler = handler;
      this.fAttrChecker = attrChecker;
   }

   void reset(SymbolTable symbolTable, boolean validateAnnotations, Locale locale) {
      this.fSymbolTable = symbolTable;
      this.fValidateAnnotations = validateAnnotations;
      this.fValidationState.setExtraChecking(false);
      this.fValidationState.setSymbolTable(symbolTable);
      this.fValidationState.setLocale(locale);
   }

   XSAnnotationImpl traverseAnnotationDecl(Element annotationDecl, Object[] parentAttrs, boolean isGlobal, XSDocumentInfo schemaDoc) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(annotationDecl, isGlobal, schemaDoc);
      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      String contents = DOMUtil.getAnnotation(annotationDecl);
      Element child = DOMUtil.getFirstChildElement(annotationDecl);
      if (child != null) {
         do {
            String name = DOMUtil.getLocalName(child);
            if (!name.equals(SchemaSymbols.ELT_APPINFO) && !name.equals(SchemaSymbols.ELT_DOCUMENTATION)) {
               this.reportSchemaError("src-annotation", new Object[]{name}, child);
            } else {
               attrValues = this.fAttrChecker.checkAttributes(child, true, schemaDoc);
               this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            }

            child = DOMUtil.getNextSiblingElement(child);
         } while(child != null);
      }

      if (contents == null) {
         return null;
      } else {
         SchemaGrammar grammar = this.fSchemaHandler.getGrammar(schemaDoc.fTargetNamespace);
         Vector annotationLocalAttrs = (Vector)parentAttrs[XSAttributeChecker.ATTIDX_NONSCHEMA];
         if (annotationLocalAttrs != null && !annotationLocalAttrs.isEmpty()) {
            StringBuffer localStrBuffer = new StringBuffer(64);
            localStrBuffer.append(" ");
            int i = 0;

            int annotationTokenEnd;
            String annotation;
            while(i < annotationLocalAttrs.size()) {
               String rawname = (String)annotationLocalAttrs.elementAt(i++);
               annotationTokenEnd = rawname.indexOf(58);
               String localpart;
               if (annotationTokenEnd == -1) {
                  annotation = "";
                  localpart = rawname;
               } else {
                  annotation = rawname.substring(0, annotationTokenEnd);
                  localpart = rawname.substring(annotationTokenEnd + 1);
               }

               String uri = schemaDoc.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(annotation));
               if (annotationDecl.getAttributeNS(uri, localpart).length() != 0) {
                  ++i;
               } else {
                  localStrBuffer.append(rawname).append("=\"");
                  String value = (String)annotationLocalAttrs.elementAt(i++);
                  value = processAttValue(value);
                  localStrBuffer.append(value).append("\" ");
               }
            }

            StringBuffer contentBuffer = new StringBuffer(contents.length() + localStrBuffer.length());
            annotationTokenEnd = contents.indexOf(SchemaSymbols.ELT_ANNOTATION);
            if (annotationTokenEnd == -1) {
               return null;
            } else {
               annotationTokenEnd += SchemaSymbols.ELT_ANNOTATION.length();
               contentBuffer.append(contents.substring(0, annotationTokenEnd));
               contentBuffer.append(localStrBuffer.toString());
               contentBuffer.append(contents.substring(annotationTokenEnd, contents.length()));
               annotation = contentBuffer.toString();
               if (this.fValidateAnnotations) {
                  schemaDoc.addAnnotation(new XSAnnotationInfo(annotation, annotationDecl));
               }

               return new XSAnnotationImpl(annotation, grammar);
            }
         } else {
            if (this.fValidateAnnotations) {
               schemaDoc.addAnnotation(new XSAnnotationInfo(contents, annotationDecl));
            }

            return new XSAnnotationImpl(contents, grammar);
         }
      }
   }

   XSAnnotationImpl traverseSyntheticAnnotation(Element annotationParent, String initialContent, Object[] parentAttrs, boolean isGlobal, XSDocumentInfo schemaDoc) {
      SchemaGrammar grammar = this.fSchemaHandler.getGrammar(schemaDoc.fTargetNamespace);
      Vector annotationLocalAttrs = (Vector)parentAttrs[XSAttributeChecker.ATTIDX_NONSCHEMA];
      if (annotationLocalAttrs != null && !annotationLocalAttrs.isEmpty()) {
         StringBuffer localStrBuffer = new StringBuffer(64);
         localStrBuffer.append(" ");
         int i = 0;

         int annotationTokenEnd;
         String annotation;
         while(i < annotationLocalAttrs.size()) {
            String rawname = (String)annotationLocalAttrs.elementAt(i++);
            annotationTokenEnd = rawname.indexOf(58);
            if (annotationTokenEnd == -1) {
               annotation = "";
            } else {
               annotation = rawname.substring(0, annotationTokenEnd);
               rawname.substring(annotationTokenEnd + 1);
            }

            String uri = schemaDoc.fNamespaceSupport.getURI(this.fSymbolTable.addSymbol(annotation));
            localStrBuffer.append(rawname).append("=\"");
            String value = (String)annotationLocalAttrs.elementAt(i++);
            value = processAttValue(value);
            localStrBuffer.append(value).append("\" ");
         }

         StringBuffer contentBuffer = new StringBuffer(initialContent.length() + localStrBuffer.length());
         annotationTokenEnd = initialContent.indexOf(SchemaSymbols.ELT_ANNOTATION);
         if (annotationTokenEnd == -1) {
            return null;
         } else {
            annotationTokenEnd += SchemaSymbols.ELT_ANNOTATION.length();
            contentBuffer.append(initialContent.substring(0, annotationTokenEnd));
            contentBuffer.append(localStrBuffer.toString());
            contentBuffer.append(initialContent.substring(annotationTokenEnd, initialContent.length()));
            annotation = contentBuffer.toString();
            if (this.fValidateAnnotations) {
               schemaDoc.addAnnotation(new XSAnnotationInfo(annotation, annotationParent));
            }

            return new XSAnnotationImpl(annotation, grammar);
         }
      } else {
         if (this.fValidateAnnotations) {
            schemaDoc.addAnnotation(new XSAnnotationInfo(initialContent, annotationParent));
         }

         return new XSAnnotationImpl(initialContent, grammar);
      }
   }

   XSDAbstractTraverser.FacetInfo traverseFacets(Element content, XSSimpleType baseValidator, XSDocumentInfo schemaDoc) {
      short facetsPresent = 0;
      short facetsFixed = 0;
      boolean hasQName = this.containsQName(baseValidator);
      Vector enumData = null;
      XSObjectListImpl enumAnnotations = null;
      XSObjectListImpl patternAnnotations = null;
      Vector enumNSDecls = hasQName ? new Vector() : null;
      int currentFacet = false;
      this.xsFacets.reset();

      while(content != null) {
         Object[] attrs = null;
         String facet = DOMUtil.getLocalName(content);
         String enumVal;
         if (facet.equals(SchemaSymbols.ELT_ENUMERATION)) {
            attrs = this.fAttrChecker.checkAttributes(content, false, schemaDoc, hasQName);
            enumVal = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
            if (enumVal == null) {
               this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_ENUMERATION, SchemaSymbols.ATT_VALUE}, content);
               this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
               content = DOMUtil.getNextSiblingElement(content);
               continue;
            }

            NamespaceSupport nsDecls = (NamespaceSupport)attrs[XSAttributeChecker.ATTIDX_ENUMNSDECLS];
            if (baseValidator.getVariety() == 1 && baseValidator.getPrimitiveKind() == 20) {
               schemaDoc.fValidationContext.setNamespaceSupport(nsDecls);
               Object notation = null;

               try {
                  QName temp = (QName)fQNameDV.validate((String)enumVal, schemaDoc.fValidationContext, (ValidatedInfo)null);
                  notation = this.fSchemaHandler.getGlobalDecl(schemaDoc, 6, temp, content);
               } catch (InvalidDatatypeValueException var18) {
                  this.reportSchemaError(var18.getKey(), var18.getArgs(), content);
               }

               if (notation == null) {
                  this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                  content = DOMUtil.getNextSiblingElement(content);
                  continue;
               }

               schemaDoc.fValidationContext.setNamespaceSupport(schemaDoc.fNamespaceSupport);
            }

            if (enumData == null) {
               enumData = new Vector();
               enumAnnotations = new XSObjectListImpl();
            }

            enumData.addElement(enumVal);
            enumAnnotations.addXSObject((XSObject)null);
            if (hasQName) {
               enumNSDecls.addElement(nsDecls);
            }

            Element child = DOMUtil.getFirstChildElement(content);
            if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
               enumAnnotations.addXSObject(enumAnnotations.getLength() - 1, this.traverseAnnotationDecl(child, attrs, false, schemaDoc));
               child = DOMUtil.getNextSiblingElement(child);
            } else {
               String text = DOMUtil.getSyntheticAnnotation(content);
               if (text != null) {
                  enumAnnotations.addXSObject(enumAnnotations.getLength() - 1, this.traverseSyntheticAnnotation(content, text, attrs, false, schemaDoc));
               }
            }

            if (child != null) {
               this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"enumeration", "(annotation?)", DOMUtil.getLocalName(child)}, child);
            }
         } else {
            String text;
            if (facet.equals(SchemaSymbols.ELT_PATTERN)) {
               facetsPresent = (short)(facetsPresent | 8);
               attrs = this.fAttrChecker.checkAttributes(content, false, schemaDoc);
               enumVal = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
               if (enumVal == null) {
                  this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_PATTERN, SchemaSymbols.ATT_VALUE}, content);
                  this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                  content = DOMUtil.getNextSiblingElement(content);
                  continue;
               }

               if (this.fPattern.length() == 0) {
                  this.fPattern.append(enumVal);
               } else {
                  this.fPattern.append("|");
                  this.fPattern.append(enumVal);
               }

               Element child = DOMUtil.getFirstChildElement(content);
               if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                  if (patternAnnotations == null) {
                     patternAnnotations = new XSObjectListImpl();
                  }

                  patternAnnotations.addXSObject(this.traverseAnnotationDecl(child, attrs, false, schemaDoc));
                  child = DOMUtil.getNextSiblingElement(child);
               } else {
                  text = DOMUtil.getSyntheticAnnotation(content);
                  if (text != null) {
                     if (patternAnnotations == null) {
                        patternAnnotations = new XSObjectListImpl();
                     }

                     patternAnnotations.addXSObject(this.traverseSyntheticAnnotation(content, text, attrs, false, schemaDoc));
                  }
               }

               if (child != null) {
                  this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"pattern", "(annotation?)", DOMUtil.getLocalName(child)}, child);
               }
            } else {
               short currentFacet;
               if (facet.equals(SchemaSymbols.ELT_MINLENGTH)) {
                  currentFacet = 2;
               } else if (facet.equals(SchemaSymbols.ELT_MAXLENGTH)) {
                  currentFacet = 4;
               } else if (facet.equals(SchemaSymbols.ELT_MAXEXCLUSIVE)) {
                  currentFacet = 64;
               } else if (facet.equals(SchemaSymbols.ELT_MAXINCLUSIVE)) {
                  currentFacet = 32;
               } else if (facet.equals(SchemaSymbols.ELT_MINEXCLUSIVE)) {
                  currentFacet = 128;
               } else if (facet.equals(SchemaSymbols.ELT_MININCLUSIVE)) {
                  currentFacet = 256;
               } else if (facet.equals(SchemaSymbols.ELT_TOTALDIGITS)) {
                  currentFacet = 512;
               } else if (facet.equals(SchemaSymbols.ELT_FRACTIONDIGITS)) {
                  currentFacet = 1024;
               } else if (facet.equals(SchemaSymbols.ELT_WHITESPACE)) {
                  currentFacet = 16;
               } else {
                  if (!facet.equals(SchemaSymbols.ELT_LENGTH)) {
                     break;
                  }

                  currentFacet = 1;
               }

               attrs = this.fAttrChecker.checkAttributes(content, false, schemaDoc);
               if ((facetsPresent & currentFacet) != 0) {
                  this.reportSchemaError("src-single-facet-value", new Object[]{facet}, content);
                  this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                  content = DOMUtil.getNextSiblingElement(content);
                  continue;
               }

               if (attrs[XSAttributeChecker.ATTIDX_VALUE] == null) {
                  if (content.getAttributeNodeNS((String)null, "value") == null) {
                     this.reportSchemaError("s4s-att-must-appear", new Object[]{content.getLocalName(), SchemaSymbols.ATT_VALUE}, content);
                  }

                  this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
                  content = DOMUtil.getNextSiblingElement(content);
                  continue;
               }

               facetsPresent |= currentFacet;
               if ((Boolean)attrs[XSAttributeChecker.ATTIDX_FIXED]) {
                  facetsFixed |= currentFacet;
               }

               switch(currentFacet) {
               case 1:
                  this.xsFacets.length = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                  break;
               case 2:
                  this.xsFacets.minLength = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                  break;
               case 4:
                  this.xsFacets.maxLength = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                  break;
               case 16:
                  this.xsFacets.whiteSpace = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).shortValue();
                  break;
               case 32:
                  this.xsFacets.maxInclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                  break;
               case 64:
                  this.xsFacets.maxExclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                  break;
               case 128:
                  this.xsFacets.minExclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                  break;
               case 256:
                  this.xsFacets.minInclusive = (String)attrs[XSAttributeChecker.ATTIDX_VALUE];
                  break;
               case 512:
                  this.xsFacets.totalDigits = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
                  break;
               case 1024:
                  this.xsFacets.fractionDigits = ((XInt)attrs[XSAttributeChecker.ATTIDX_VALUE]).intValue();
               }

               Element child = DOMUtil.getFirstChildElement(content);
               XSAnnotationImpl annotation = null;
               if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                  annotation = this.traverseAnnotationDecl(child, attrs, false, schemaDoc);
                  child = DOMUtil.getNextSiblingElement(child);
               } else {
                  text = DOMUtil.getSyntheticAnnotation(content);
                  if (text != null) {
                     annotation = this.traverseSyntheticAnnotation(content, text, attrs, false, schemaDoc);
                  }
               }

               switch(currentFacet) {
               case 1:
                  this.xsFacets.lengthAnnotation = annotation;
                  break;
               case 2:
                  this.xsFacets.minLengthAnnotation = annotation;
                  break;
               case 4:
                  this.xsFacets.maxLengthAnnotation = annotation;
                  break;
               case 16:
                  this.xsFacets.whiteSpaceAnnotation = annotation;
                  break;
               case 32:
                  this.xsFacets.maxInclusiveAnnotation = annotation;
                  break;
               case 64:
                  this.xsFacets.maxExclusiveAnnotation = annotation;
                  break;
               case 128:
                  this.xsFacets.minExclusiveAnnotation = annotation;
                  break;
               case 256:
                  this.xsFacets.minInclusiveAnnotation = annotation;
                  break;
               case 512:
                  this.xsFacets.totalDigitsAnnotation = annotation;
                  break;
               case 1024:
                  this.xsFacets.fractionDigitsAnnotation = annotation;
               }

               if (child != null) {
                  this.reportSchemaError("s4s-elt-must-match.1", new Object[]{facet, "(annotation?)", DOMUtil.getLocalName(child)}, child);
               }
            }
         }

         this.fAttrChecker.returnAttrArray(attrs, schemaDoc);
         content = DOMUtil.getNextSiblingElement(content);
      }

      if (enumData != null) {
         facetsPresent = (short)(facetsPresent | 2048);
         this.xsFacets.enumeration = enumData;
         this.xsFacets.enumNSDecls = enumNSDecls;
         this.xsFacets.enumAnnotations = enumAnnotations;
      }

      if ((facetsPresent & 8) != 0) {
         this.xsFacets.pattern = this.fPattern.toString();
         this.xsFacets.patternAnnotations = patternAnnotations;
      }

      this.fPattern.setLength(0);
      return new XSDAbstractTraverser.FacetInfo(this.xsFacets, content, facetsPresent, facetsFixed);
   }

   private boolean containsQName(XSSimpleType type) {
      if (type.getVariety() == 1) {
         short primitive = type.getPrimitiveKind();
         return primitive == 18 || primitive == 20;
      } else if (type.getVariety() == 2) {
         return this.containsQName((XSSimpleType)type.getItemType());
      } else {
         if (type.getVariety() == 3) {
            XSObjectList members = type.getMemberTypes();

            for(int i = 0; i < members.getLength(); ++i) {
               if (this.containsQName((XSSimpleType)members.item(i))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   Element traverseAttrsAndAttrGrps(Element firstAttr, XSAttributeGroupDecl attrGrp, XSDocumentInfo schemaDoc, SchemaGrammar grammar, XSComplexTypeDecl enclosingCT) {
      Element child = null;
      XSAttributeGroupDecl tempAttrGrp = null;
      XSAttributeUseImpl tempAttrUse = null;
      XSAttributeUse otherUse = null;

      String childName;
      String code;
      String name;
      for(child = firstAttr; child != null; child = DOMUtil.getNextSiblingElement(child)) {
         childName = DOMUtil.getLocalName(child);
         if (childName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
            tempAttrUse = this.fSchemaHandler.fAttributeTraverser.traverseLocal(child, schemaDoc, grammar, enclosingCT);
            if (tempAttrUse != null) {
               if (tempAttrUse.fUse == 2) {
                  attrGrp.addAttributeUse(tempAttrUse);
               } else {
                  otherUse = attrGrp.getAttributeUseNoProhibited(tempAttrUse.fAttrDecl.getNamespace(), tempAttrUse.fAttrDecl.getName());
                  String idName;
                  if (otherUse == null) {
                     idName = attrGrp.addAttributeUse(tempAttrUse);
                     if (idName != null) {
                        code = enclosingCT == null ? "ag-props-correct.3" : "ct-props-correct.5";
                        name = enclosingCT == null ? attrGrp.fName : enclosingCT.getName();
                        this.reportSchemaError(code, new Object[]{name, tempAttrUse.fAttrDecl.getName(), idName}, child);
                     }
                  } else if (otherUse != tempAttrUse) {
                     idName = enclosingCT == null ? "ag-props-correct.2" : "ct-props-correct.4";
                     code = enclosingCT == null ? attrGrp.fName : enclosingCT.getName();
                     this.reportSchemaError(idName, new Object[]{code, tempAttrUse.fAttrDecl.getName()}, child);
                  }
               }
            }
         } else {
            if (!childName.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
               break;
            }

            tempAttrGrp = this.fSchemaHandler.fAttributeGroupTraverser.traverseLocal(child, schemaDoc, grammar);
            if (tempAttrGrp != null) {
               XSObjectList attrUseS = tempAttrGrp.getAttributeUses();
               int attrCount = attrUseS.getLength();

               String name;
               for(int i = 0; i < attrCount; ++i) {
                  XSAttributeUseImpl oneAttrUse = (XSAttributeUseImpl)attrUseS.item(i);
                  if (oneAttrUse.fUse == 2) {
                     attrGrp.addAttributeUse(oneAttrUse);
                  } else {
                     otherUse = attrGrp.getAttributeUseNoProhibited(oneAttrUse.fAttrDecl.getNamespace(), oneAttrUse.fAttrDecl.getName());
                     String code;
                     if (otherUse == null) {
                        name = attrGrp.addAttributeUse(oneAttrUse);
                        if (name != null) {
                           code = enclosingCT == null ? "ag-props-correct.3" : "ct-props-correct.5";
                           String name = enclosingCT == null ? attrGrp.fName : enclosingCT.getName();
                           this.reportSchemaError(code, new Object[]{name, oneAttrUse.fAttrDecl.getName(), name}, child);
                        }
                     } else if (oneAttrUse != otherUse) {
                        name = enclosingCT == null ? "ag-props-correct.2" : "ct-props-correct.4";
                        code = enclosingCT == null ? attrGrp.fName : enclosingCT.getName();
                        this.reportSchemaError(name, new Object[]{code, oneAttrUse.fAttrDecl.getName()}, child);
                     }
                  }
               }

               if (tempAttrGrp.fAttributeWC != null) {
                  if (attrGrp.fAttributeWC == null) {
                     attrGrp.fAttributeWC = tempAttrGrp.fAttributeWC;
                  } else {
                     attrGrp.fAttributeWC = attrGrp.fAttributeWC.performIntersectionWith(tempAttrGrp.fAttributeWC, attrGrp.fAttributeWC.fProcessContents);
                     if (attrGrp.fAttributeWC == null) {
                        String code = enclosingCT == null ? "src-attribute_group.2" : "src-ct.4";
                        name = enclosingCT == null ? attrGrp.fName : enclosingCT.getName();
                        this.reportSchemaError(code, new Object[]{name}, child);
                     }
                  }
               }
            }
         }
      }

      if (child != null) {
         childName = DOMUtil.getLocalName(child);
         if (childName.equals(SchemaSymbols.ELT_ANYATTRIBUTE)) {
            XSWildcardDecl tempAttrWC = this.fSchemaHandler.fWildCardTraverser.traverseAnyAttribute(child, schemaDoc, grammar);
            if (attrGrp.fAttributeWC == null) {
               attrGrp.fAttributeWC = tempAttrWC;
            } else {
               attrGrp.fAttributeWC = tempAttrWC.performIntersectionWith(attrGrp.fAttributeWC, tempAttrWC.fProcessContents);
               if (attrGrp.fAttributeWC == null) {
                  code = enclosingCT == null ? "src-attribute_group.2" : "src-ct.4";
                  name = enclosingCT == null ? attrGrp.fName : enclosingCT.getName();
                  this.reportSchemaError(code, new Object[]{name}, child);
               }
            }

            child = DOMUtil.getNextSiblingElement(child);
         }
      }

      return child;
   }

   void reportSchemaError(String key, Object[] args, Element ele) {
      this.fSchemaHandler.reportSchemaError(key, args, ele);
   }

   void checkNotationType(String refName, XSTypeDefinition typeDecl, Element elem) {
      if (typeDecl.getTypeCategory() == 16 && ((XSSimpleType)typeDecl).getVariety() == 1 && ((XSSimpleType)typeDecl).getPrimitiveKind() == 20 && (((XSSimpleType)typeDecl).getDefinedFacets() & 2048) == 0) {
         this.reportSchemaError("enumeration-required-notation", new Object[]{typeDecl.getName(), refName, DOMUtil.getLocalName(elem)}, elem);
      }

   }

   protected XSParticleDecl checkOccurrences(XSParticleDecl particle, String particleName, Element parent, int allContextFlags, long defaultVals) {
      int min = particle.fMinOccurs;
      int max = particle.fMaxOccurs;
      boolean defaultMin = (defaultVals & (long)(1 << XSAttributeChecker.ATTIDX_MINOCCURS)) != 0L;
      boolean defaultMax = (defaultVals & (long)(1 << XSAttributeChecker.ATTIDX_MAXOCCURS)) != 0L;
      boolean processingAllEl = (allContextFlags & 1) != 0;
      boolean processingAllGP = (allContextFlags & 8) != 0;
      boolean groupRefWithAll = (allContextFlags & 2) != 0;
      boolean isGroupChild = (allContextFlags & 4) != 0;
      if (isGroupChild) {
         Object[] args;
         if (!defaultMin) {
            args = new Object[]{particleName, "minOccurs"};
            this.reportSchemaError("s4s-att-not-allowed", args, parent);
            min = 1;
         }

         if (!defaultMax) {
            args = new Object[]{particleName, "maxOccurs"};
            this.reportSchemaError("s4s-att-not-allowed", args, parent);
            max = 1;
         }
      }

      if (min == 0 && max == 0) {
         particle.fType = 0;
         return null;
      } else {
         if (processingAllEl) {
            if (max != 1) {
               this.reportSchemaError("cos-all-limited.2", new Object[]{max == -1 ? "unbounded" : Integer.toString(max), ((XSElementDecl)particle.fValue).getName()}, parent);
               max = 1;
               if (min > 1) {
                  min = 1;
               }
            }
         } else if ((processingAllGP || groupRefWithAll) && max != 1) {
            this.reportSchemaError("cos-all-limited.1.2", (Object[])null, parent);
            if (min > 1) {
               min = 1;
            }

            max = 1;
         }

         particle.fMinOccurs = min;
         particle.fMaxOccurs = max;
         return particle;
      }
   }

   private static String processAttValue(String original) {
      int length = original.length();

      for(int i = 0; i < length; ++i) {
         char currChar = original.charAt(i);
         if (currChar == '"' || currChar == '<' || currChar == '&' || currChar == '\t' || currChar == '\n' || currChar == '\r') {
            return escapeAttValue(original, i);
         }
      }

      return original;
   }

   private static String escapeAttValue(String original, int from) {
      int length = original.length();
      StringBuffer newVal = new StringBuffer(length);
      newVal.append(original.substring(0, from));

      for(int i = from; i < length; ++i) {
         char currChar = original.charAt(i);
         if (currChar == '"') {
            newVal.append("&quot;");
         } else if (currChar == '<') {
            newVal.append("&lt;");
         } else if (currChar == '&') {
            newVal.append("&amp;");
         } else if (currChar == '\t') {
            newVal.append("&#x9;");
         } else if (currChar == '\n') {
            newVal.append("&#xA;");
         } else if (currChar == '\r') {
            newVal.append("&#xD;");
         } else {
            newVal.append(currChar);
         }
      }

      return newVal.toString();
   }

   static {
      fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
   }

   static final class FacetInfo {
      final XSFacets facetdata;
      final Element nodeAfterFacets;
      final short fPresentFacets;
      final short fFixedFacets;

      FacetInfo(XSFacets facets, Element nodeAfterFacets, short presentFacets, short fixedFacets) {
         this.facetdata = facets;
         this.nodeAfterFacets = nodeAfterFacets;
         this.fPresentFacets = presentFacets;
         this.fFixedFacets = fixedFacets;
      }
   }
}
