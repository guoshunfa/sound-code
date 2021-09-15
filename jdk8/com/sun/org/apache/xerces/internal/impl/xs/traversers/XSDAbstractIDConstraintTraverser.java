package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.Element;

class XSDAbstractIDConstraintTraverser extends XSDAbstractTraverser {
   public XSDAbstractIDConstraintTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   boolean traverseIdentityConstraint(IdentityConstraint ic, Element icElem, XSDocumentInfo schemaDoc, Object[] icElemAttrs) {
      Element sElem = DOMUtil.getFirstChildElement(icElem);
      if (sElem == null) {
         this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, icElem);
         return false;
      } else {
         if (DOMUtil.getLocalName(sElem).equals(SchemaSymbols.ELT_ANNOTATION)) {
            ic.addAnnotation(this.traverseAnnotationDecl(sElem, icElemAttrs, false, schemaDoc));
            sElem = DOMUtil.getNextSiblingElement(sElem);
            if (sElem == null) {
               this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, icElem);
               return false;
            }
         } else {
            String text = DOMUtil.getSyntheticAnnotation(icElem);
            if (text != null) {
               ic.addAnnotation(this.traverseSyntheticAnnotation(icElem, text, icElemAttrs, false, schemaDoc));
            }
         }

         if (!DOMUtil.getLocalName(sElem).equals(SchemaSymbols.ELT_SELECTOR)) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_SELECTOR}, sElem);
            return false;
         } else {
            Object[] attrValues = this.fAttrChecker.checkAttributes(sElem, false, schemaDoc);
            Element selChild = DOMUtil.getFirstChildElement(sElem);
            String sText;
            if (selChild != null) {
               if (DOMUtil.getLocalName(selChild).equals(SchemaSymbols.ELT_ANNOTATION)) {
                  ic.addAnnotation(this.traverseAnnotationDecl(selChild, attrValues, false, schemaDoc));
                  selChild = DOMUtil.getNextSiblingElement(selChild);
               } else {
                  this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(selChild)}, selChild);
               }

               if (selChild != null) {
                  this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(selChild)}, selChild);
               }
            } else {
               sText = DOMUtil.getSyntheticAnnotation(sElem);
               if (sText != null) {
                  ic.addAnnotation(this.traverseSyntheticAnnotation(icElem, sText, attrValues, false, schemaDoc));
               }
            }

            sText = (String)attrValues[XSAttributeChecker.ATTIDX_XPATH];
            if (sText == null) {
               this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_SELECTOR, SchemaSymbols.ATT_XPATH}, sElem);
               return false;
            } else {
               sText = XMLChar.trim(sText);
               Selector.XPath sXpath = null;

               try {
                  sXpath = new Selector.XPath(sText, this.fSymbolTable, schemaDoc.fNamespaceSupport);
                  Selector selector = new Selector(sXpath, ic);
                  ic.setSelector(selector);
               } catch (XPathException var16) {
                  this.reportSchemaError(var16.getKey(), new Object[]{sText}, sElem);
                  this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                  return false;
               }

               this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
               Element fElem = DOMUtil.getNextSiblingElement(sElem);
               if (fElem == null) {
                  this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, sElem);
                  return false;
               } else {
                  while(fElem != null) {
                     if (!DOMUtil.getLocalName(fElem).equals(SchemaSymbols.ELT_FIELD)) {
                        this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_FIELD}, fElem);
                        fElem = DOMUtil.getNextSiblingElement(fElem);
                     } else {
                        attrValues = this.fAttrChecker.checkAttributes(fElem, false, schemaDoc);
                        Element fieldChild = DOMUtil.getFirstChildElement(fElem);
                        if (fieldChild != null && DOMUtil.getLocalName(fieldChild).equals(SchemaSymbols.ELT_ANNOTATION)) {
                           ic.addAnnotation(this.traverseAnnotationDecl(fieldChild, attrValues, false, schemaDoc));
                           fieldChild = DOMUtil.getNextSiblingElement(fieldChild);
                        }

                        String fText;
                        if (fieldChild != null) {
                           this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_FIELD, "(annotation?)", DOMUtil.getLocalName(fieldChild)}, fieldChild);
                        } else {
                           fText = DOMUtil.getSyntheticAnnotation(fElem);
                           if (fText != null) {
                              ic.addAnnotation(this.traverseSyntheticAnnotation(icElem, fText, attrValues, false, schemaDoc));
                           }
                        }

                        fText = (String)attrValues[XSAttributeChecker.ATTIDX_XPATH];
                        if (fText == null) {
                           this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_FIELD, SchemaSymbols.ATT_XPATH}, fElem);
                           this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                           return false;
                        }

                        fText = XMLChar.trim(fText);

                        try {
                           Field.XPath fXpath = new Field.XPath(fText, this.fSymbolTable, schemaDoc.fNamespaceSupport);
                           Field field = new Field(fXpath, ic);
                           ic.addField(field);
                        } catch (XPathException var15) {
                           this.reportSchemaError(var15.getKey(), new Object[]{fText}, fElem);
                           this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                           return false;
                        }

                        fElem = DOMUtil.getNextSiblingElement(fElem);
                        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                     }
                  }

                  return ic.getFieldCount() > 0;
               }
            }
         }
      }
   }
}
