package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

class XSDUniqueOrKeyTraverser extends XSDAbstractIDConstraintTraverser {
   public XSDUniqueOrKeyTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   void traverse(Element uElem, XSElementDecl element, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(uElem, false, schemaDoc);
      String uName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
      if (uName == null) {
         this.reportSchemaError("s4s-att-must-appear", new Object[]{DOMUtil.getLocalName(uElem), SchemaSymbols.ATT_NAME}, uElem);
         this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      } else {
         UniqueOrKey uniqueOrKey = null;
         if (DOMUtil.getLocalName(uElem).equals(SchemaSymbols.ELT_UNIQUE)) {
            uniqueOrKey = new UniqueOrKey(schemaDoc.fTargetNamespace, uName, element.fName, (short)3);
         } else {
            uniqueOrKey = new UniqueOrKey(schemaDoc.fTargetNamespace, uName, element.fName, (short)1);
         }

         if (this.traverseIdentityConstraint(uniqueOrKey, uElem, schemaDoc, attrValues)) {
            if (grammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName()) == null) {
               grammar.addIDConstraintDecl(element, uniqueOrKey);
            }

            String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
            IdentityConstraint idc = grammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName(), loc);
            if (idc == null) {
               grammar.addIDConstraintDecl(element, uniqueOrKey, loc);
            }

            if (this.fSchemaHandler.fTolerateDuplicates) {
               if (idc != null && idc instanceof UniqueOrKey) {
                  uniqueOrKey = uniqueOrKey;
               }

               this.fSchemaHandler.addIDConstraintDecl(uniqueOrKey);
            }
         }

         this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      }
   }
}
