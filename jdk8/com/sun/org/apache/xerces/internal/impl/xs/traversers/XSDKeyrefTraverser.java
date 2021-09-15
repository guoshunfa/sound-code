package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.w3c.dom.Element;

class XSDKeyrefTraverser extends XSDAbstractIDConstraintTraverser {
   public XSDKeyrefTraverser(XSDHandler handler, XSAttributeChecker gAttrCheck) {
      super(handler, gAttrCheck);
   }

   void traverse(Element krElem, XSElementDecl element, XSDocumentInfo schemaDoc, SchemaGrammar grammar) {
      Object[] attrValues = this.fAttrChecker.checkAttributes(krElem, false, schemaDoc);
      String krName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
      if (krName == null) {
         this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_NAME}, krElem);
         this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
      } else {
         QName kName = (QName)attrValues[XSAttributeChecker.ATTIDX_REFER];
         if (kName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_REFER}, krElem);
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
         } else {
            UniqueOrKey key = null;
            IdentityConstraint ret = (IdentityConstraint)this.fSchemaHandler.getGlobalDecl(schemaDoc, 5, kName, krElem);
            if (ret != null) {
               if (ret.getCategory() != 1 && ret.getCategory() != 3) {
                  this.reportSchemaError("src-resolve", new Object[]{kName.rawname, "identity constraint key/unique"}, krElem);
               } else {
                  key = (UniqueOrKey)ret;
               }
            }

            if (key == null) {
               this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            } else {
               KeyRef keyRef = new KeyRef(schemaDoc.fTargetNamespace, krName, element.fName, key);
               if (this.traverseIdentityConstraint(keyRef, krElem, schemaDoc, attrValues)) {
                  if (key.getFieldCount() != keyRef.getFieldCount()) {
                     this.reportSchemaError("c-props-correct.2", new Object[]{krName, key.getIdentityConstraintName()}, krElem);
                  } else {
                     if (grammar.getIDConstraintDecl(keyRef.getIdentityConstraintName()) == null) {
                        grammar.addIDConstraintDecl(element, keyRef);
                     }

                     String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
                     IdentityConstraint idc = grammar.getIDConstraintDecl(keyRef.getIdentityConstraintName(), loc);
                     if (idc == null) {
                        grammar.addIDConstraintDecl(element, keyRef, loc);
                     }

                     if (this.fSchemaHandler.fTolerateDuplicates) {
                        if (idc != null && idc instanceof KeyRef) {
                           keyRef = (KeyRef)idc;
                        }

                        this.fSchemaHandler.addIDConstraintDecl(keyRef);
                     }
                  }
               }

               this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            }
         }
      }
   }
}
