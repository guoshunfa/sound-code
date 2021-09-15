package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSNotationDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

class XSDNotationTraverser extends XSDAbstractTraverser {
   XSDNotationTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   XSNotationDecl traverse(Element elmNode, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, true, schemaDoc);
      String nameAttr = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
      String publicAttr = (String)attrValues[XSAttributeChecker.ATTIDX_PUBLIC];
      String systemAttr = (String)attrValues[XSAttributeChecker.ATTIDX_SYSTEM];
      if (nameAttr == null) {
         this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_NOTATION, SchemaSymbols.ATT_NAME}, elmNode);
         this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
         return null;
      } else {
         if (systemAttr == null && publicAttr == null) {
            this.reportSchemaError("PublicSystemOnNotation", (Object[])null, elmNode);
            publicAttr = "missing";
         }

         XSNotationDecl notation = new XSNotationDecl();
         notation.fName = nameAttr;
         notation.fTargetNamespace = schemaDoc.fTargetNamespace;
         notation.fPublicId = publicAttr;
         notation.fSystemId = systemAttr;
         Element content = DOMUtil.getFirstChildElement(elmNode);
         XSAnnotationImpl annotation = null;
         if (content != null && DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = this.traverseAnnotationDecl(content, attrValues, false, schemaDoc);
            content = DOMUtil.getNextSiblingElement(content);
         } else {
            String text = DOMUtil.getSyntheticAnnotation(elmNode);
            if (text != null) {
               annotation = this.traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
            }
         }

         XSObjectListImpl annotations;
         if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
         } else {
            annotations = XSObjectListImpl.EMPTY_LIST;
         }

         notation.fAnnotations = annotations;
         if (content != null) {
            Object[] args = new Object[]{SchemaSymbols.ELT_NOTATION, "(annotation?)", DOMUtil.getLocalName(content)};
            this.reportSchemaError("s4s-elt-must-match.1", args, content);
         }

         if (grammar.getGlobalNotationDecl(notation.fName) == null) {
            grammar.addGlobalNotationDecl(notation);
         }

         String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
         XSNotationDecl notation2 = grammar.getGlobalNotationDecl(notation.fName, loc);
         if (notation2 == null) {
            grammar.addGlobalNotationDecl(notation, loc);
         }

         if (this.fSchemaHandler.fTolerateDuplicates) {
            if (notation2 != null) {
               notation = notation2;
            }

            this.fSchemaHandler.addGlobalNotationDecl(notation);
         }

         this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
         return notation;
      }
   }
}
