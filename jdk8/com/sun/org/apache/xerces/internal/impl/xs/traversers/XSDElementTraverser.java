package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

class XSDElementTraverser extends XSDAbstractTraverser {
   protected final XSElementDecl fTempElementDecl = new XSElementDecl();
   boolean fDeferTraversingLocalElements;

   XSDElementTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   XSParticleDecl traverseLocal(Element elmDecl, XSDocumentInfo schemaDoc, SchemaGrammar grammar, int allContextFlags, XSObject parent) {
      XSParticleDecl particle = null;
      if (this.fSchemaHandler.fDeclPool != null) {
         particle = this.fSchemaHandler.fDeclPool.getParticleDecl();
      } else {
         particle = new XSParticleDecl();
      }

      if (this.fDeferTraversingLocalElements) {
         particle.fType = 1;
         Attr attr = elmDecl.getAttributeNode(SchemaSymbols.ATT_MINOCCURS);
         if (attr != null) {
            String min = attr.getValue();

            try {
               int m = Integer.parseInt(XMLChar.trim(min));
               if (m >= 0) {
                  particle.fMinOccurs = m;
               }
            } catch (NumberFormatException var10) {
            }
         }

         this.fSchemaHandler.fillInLocalElemInfo(elmDecl, schemaDoc, allContextFlags, parent, particle);
      } else {
         this.traverseLocal(particle, elmDecl, schemaDoc, grammar, allContextFlags, parent, (String[])null);
         if (particle.fType == 0) {
            particle = null;
         }
      }

      return particle;
   }

   protected void traverseLocal(XSParticleDecl particle, Element elmDecl, XSDocumentInfo schemaDoc, SchemaGrammar grammar, int allContextFlags, XSObject parent, String[] localNSDecls) {
      if (localNSDecls != null) {
         schemaDoc.fNamespaceSupport.setEffectiveContext(localNSDecls);
      }

      Object[] attrValues = this.fAttrChecker.checkAttributes(elmDecl, false, schemaDoc);
      QName refAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_REF];
      XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
      XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
      XSElementDecl element = null;
      XSAnnotationImpl annotation = null;
      if (elmDecl.getAttributeNode(SchemaSymbols.ATT_REF) != null) {
         if (refAtt != null) {
            element = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(schemaDoc, 3, refAtt, elmDecl);
            Element child = DOMUtil.getFirstChildElement(elmDecl);
            if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
               annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
               child = DOMUtil.getNextSiblingElement(child);
            } else {
               String text = DOMUtil.getSyntheticAnnotation(elmDecl);
               if (text != null) {
                  annotation = this.traverseSyntheticAnnotation(elmDecl, text, attrValues, false, schemaDoc);
               }
            }

            if (child != null) {
               this.reportSchemaError("src-element.2.2", new Object[]{refAtt.rawname, DOMUtil.getLocalName(child)}, child);
            }
         } else {
            element = null;
         }
      } else {
         element = this.traverseNamedElement(elmDecl, attrValues, schemaDoc, grammar, false, parent);
      }

      particle.fMinOccurs = minAtt.intValue();
      particle.fMaxOccurs = maxAtt.intValue();
      if (element != null) {
         particle.fType = 1;
         particle.fValue = element;
      } else {
         particle.fType = 0;
      }

      if (refAtt != null) {
         XSObjectListImpl annotations;
         if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
         } else {
            annotations = XSObjectListImpl.EMPTY_LIST;
         }

         particle.fAnnotations = annotations;
      } else {
         particle.fAnnotations = (XSObjectList)(element != null ? element.fAnnotations : XSObjectListImpl.EMPTY_LIST);
      }

      Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
      this.checkOccurrences(particle, SchemaSymbols.ELT_ELEMENT, (Element)elmDecl.getParentNode(), allContextFlags, defaultVals);
      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
   }

   XSElementDecl traverseGlobal(Element elmDecl, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(elmDecl, true, schemaDoc);
      XSElementDecl element = this.traverseNamedElement(elmDecl, attrValues, schemaDoc, grammar, true, (XSObject)null);
      this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      return element;
   }

   XSElementDecl traverseNamedElement(Element elmDecl, Object[] attrValues, XSDocumentInfo schemaDoc, SchemaGrammar grammar, boolean isGlobal, XSObject parent) {
      Boolean abstractAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_ABSTRACT];
      XInt blockAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_BLOCK];
      String defaultAtt = (String)attrValues[XSAttributeChecker.ATTIDX_DEFAULT];
      XInt finalAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FINAL];
      String fixedAtt = (String)attrValues[XSAttributeChecker.ATTIDX_FIXED];
      XInt formAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_FORM];
      String nameAtt = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
      Boolean nillableAtt = (Boolean)attrValues[XSAttributeChecker.ATTIDX_NILLABLE];
      QName subGroupAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_SUBSGROUP];
      QName typeAtt = (QName)attrValues[XSAttributeChecker.ATTIDX_TYPE];
      XSElementDecl element = null;
      if (this.fSchemaHandler.fDeclPool != null) {
         element = this.fSchemaHandler.fDeclPool.getElementDecl();
      } else {
         element = new XSElementDecl();
      }

      if (nameAtt != null) {
         element.fName = this.fSymbolTable.addSymbol(nameAtt);
      }

      if (isGlobal) {
         element.fTargetNamespace = schemaDoc.fTargetNamespace;
         element.setIsGlobal();
      } else {
         if (parent instanceof XSComplexTypeDecl) {
            element.setIsLocal((XSComplexTypeDecl)parent);
         }

         if (formAtt != null) {
            if (formAtt.intValue() == 1) {
               element.fTargetNamespace = schemaDoc.fTargetNamespace;
            } else {
               element.fTargetNamespace = null;
            }
         } else if (schemaDoc.fAreLocalElementsQualified) {
            element.fTargetNamespace = schemaDoc.fTargetNamespace;
         } else {
            element.fTargetNamespace = null;
         }
      }

      if (blockAtt == null) {
         element.fBlock = schemaDoc.fBlockDefault;
         if (element.fBlock != 31) {
            element.fBlock = (short)(element.fBlock & 7);
         }
      } else {
         element.fBlock = blockAtt.shortValue();
         if (element.fBlock != 31 && (element.fBlock | 7) != 7) {
            this.reportSchemaError("s4s-att-invalid-value", new Object[]{element.fName, "block", "must be (#all | List of (extension | restriction | substitution))"}, elmDecl);
         }
      }

      element.fFinal = finalAtt == null ? schemaDoc.fFinalDefault : finalAtt.shortValue();
      element.fFinal = (short)(element.fFinal & 3);
      if (nillableAtt) {
         element.setIsNillable();
      }

      if (abstractAtt != null && abstractAtt) {
         element.setIsAbstract();
      }

      if (fixedAtt != null) {
         element.fDefault = new ValidatedInfo();
         element.fDefault.normalizedValue = fixedAtt;
         element.setConstraintType((short)2);
      } else if (defaultAtt != null) {
         element.fDefault = new ValidatedInfo();
         element.fDefault.normalizedValue = defaultAtt;
         element.setConstraintType((short)1);
      } else {
         element.setConstraintType((short)0);
      }

      if (subGroupAtt != null) {
         element.fSubGroup = (XSElementDecl)this.fSchemaHandler.getGlobalDecl(schemaDoc, 3, subGroupAtt, elmDecl);
      }

      Element child = DOMUtil.getFirstChildElement(elmDecl);
      XSAnnotationImpl annotation = null;
      if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
         annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
         child = DOMUtil.getNextSiblingElement(child);
      } else {
         String text = DOMUtil.getSyntheticAnnotation(elmDecl);
         if (text != null) {
            annotation = this.traverseSyntheticAnnotation(elmDecl, text, attrValues, false, schemaDoc);
         }
      }

      XSObjectListImpl annotations;
      if (annotation != null) {
         annotations = new XSObjectListImpl();
         ((XSObjectListImpl)annotations).addXSObject(annotation);
      } else {
         annotations = XSObjectListImpl.EMPTY_LIST;
      }

      element.fAnnotations = annotations;
      XSTypeDefinition elementType = null;
      boolean haveAnonType = false;
      String loc;
      if (child != null) {
         loc = DOMUtil.getLocalName(child);
         if (loc.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
            elementType = this.fSchemaHandler.fComplexTypeTraverser.traverseLocal(child, schemaDoc, grammar);
            haveAnonType = true;
            child = DOMUtil.getNextSiblingElement(child);
         } else if (loc.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            elementType = this.fSchemaHandler.fSimpleTypeTraverser.traverseLocal(child, schemaDoc, grammar);
            haveAnonType = true;
            child = DOMUtil.getNextSiblingElement(child);
         }
      }

      if (elementType == null && typeAtt != null) {
         elementType = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(schemaDoc, 7, typeAtt, elmDecl);
         if (elementType == null) {
            element.fUnresolvedTypeName = typeAtt;
         }
      }

      if (elementType == null && element.fSubGroup != null) {
         elementType = element.fSubGroup.fType;
      }

      if (elementType == null) {
         elementType = SchemaGrammar.fAnyType;
      }

      element.fType = (XSTypeDefinition)elementType;
      if (child != null) {
         loc = DOMUtil.getLocalName(child);

         while(child != null && (loc.equals(SchemaSymbols.ELT_KEY) || loc.equals(SchemaSymbols.ELT_KEYREF) || loc.equals(SchemaSymbols.ELT_UNIQUE))) {
            if (!loc.equals(SchemaSymbols.ELT_KEY) && !loc.equals(SchemaSymbols.ELT_UNIQUE)) {
               if (loc.equals(SchemaSymbols.ELT_KEYREF)) {
                  this.fSchemaHandler.storeKeyRef(child, schemaDoc, element);
               }
            } else {
               DOMUtil.setHidden(child, this.fSchemaHandler.fHiddenNodes);
               this.fSchemaHandler.fUniqueOrKeyTraverser.traverse(child, element, schemaDoc, grammar);
               if (DOMUtil.getAttrValue(child, SchemaSymbols.ATT_NAME).length() != 0) {
                  XSDHandler var10000 = this.fSchemaHandler;
                  String var10001 = schemaDoc.fTargetNamespace == null ? "," + DOMUtil.getAttrValue(child, SchemaSymbols.ATT_NAME) : schemaDoc.fTargetNamespace + "," + DOMUtil.getAttrValue(child, SchemaSymbols.ATT_NAME);
                  XSDHandler var10002 = this.fSchemaHandler;
                  var10000.checkForDuplicateNames(var10001, 1, this.fSchemaHandler.getIDRegistry(), this.fSchemaHandler.getIDRegistry_sub(), child, schemaDoc);
               }
            }

            child = DOMUtil.getNextSiblingElement(child);
            if (child != null) {
               loc = DOMUtil.getLocalName(child);
            }
         }
      }

      if (nameAtt == null) {
         if (isGlobal) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_ELEMENT, SchemaSymbols.ATT_NAME}, elmDecl);
         } else {
            this.reportSchemaError("src-element.2.1", (Object[])null, elmDecl);
         }

         nameAtt = "(no name)";
      }

      if (child != null) {
         this.reportSchemaError("s4s-elt-must-match.1", new Object[]{nameAtt, "(annotation?, (simpleType | complexType)?, (unique | key | keyref)*))", DOMUtil.getLocalName(child)}, child);
      }

      if (defaultAtt != null && fixedAtt != null) {
         this.reportSchemaError("src-element.1", new Object[]{nameAtt}, elmDecl);
      }

      if (haveAnonType && typeAtt != null) {
         this.reportSchemaError("src-element.3", new Object[]{nameAtt}, elmDecl);
      }

      this.checkNotationType(nameAtt, (XSTypeDefinition)elementType, elmDecl);
      if (element.fDefault != null) {
         this.fValidationState.setNamespaceSupport(schemaDoc.fNamespaceSupport);
         if (XSConstraints.ElementDefaultValidImmediate(element.fType, element.fDefault.normalizedValue, this.fValidationState, element.fDefault) == null) {
            this.reportSchemaError("e-props-correct.2", new Object[]{nameAtt, element.fDefault.normalizedValue}, elmDecl);
            element.fDefault = null;
            element.setConstraintType((short)0);
         }
      }

      if (element.fSubGroup != null && !XSConstraints.checkTypeDerivationOk(element.fType, element.fSubGroup.fType, element.fSubGroup.fFinal)) {
         this.reportSchemaError("e-props-correct.4", new Object[]{nameAtt, subGroupAtt.prefix + ":" + subGroupAtt.localpart}, elmDecl);
         element.fSubGroup = null;
      }

      if (element.fDefault != null && (((XSTypeDefinition)elementType).getTypeCategory() == 16 && ((XSSimpleType)elementType).isIDType() || ((XSTypeDefinition)elementType).getTypeCategory() == 15 && ((XSComplexTypeDecl)elementType).containsTypeID())) {
         this.reportSchemaError("e-props-correct.5", new Object[]{element.fName}, elmDecl);
         element.fDefault = null;
         element.setConstraintType((short)0);
      }

      if (element.fName == null) {
         return null;
      } else {
         if (isGlobal) {
            grammar.addGlobalElementDeclAll(element);
            if (grammar.getGlobalElementDecl(element.fName) == null) {
               grammar.addGlobalElementDecl(element);
            }

            loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            XSElementDecl element2 = grammar.getGlobalElementDecl(element.fName, loc);
            if (element2 == null) {
               grammar.addGlobalElementDecl(element, loc);
            }

            if (this.fSchemaHandler.fTolerateDuplicates) {
               if (element2 != null) {
                  element = element2;
               }

               this.fSchemaHandler.addGlobalElementDecl(element);
            }
         }

         return element;
      }
   }

   void reset(SymbolTable symbolTable, boolean validateAnnotations, Locale locale) {
      super.reset(symbolTable, validateAnnotations, locale);
      this.fDeferTraversingLocalElements = true;
   }
}
