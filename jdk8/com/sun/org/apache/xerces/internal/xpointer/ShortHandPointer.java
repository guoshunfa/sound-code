package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

class ShortHandPointer implements XPointerPart {
   private String fShortHandPointer;
   private boolean fIsFragmentResolved = false;
   private SymbolTable fSymbolTable;
   int fMatchingChildCount = 0;

   public ShortHandPointer() {
   }

   public ShortHandPointer(SymbolTable symbolTable) {
      this.fSymbolTable = symbolTable;
   }

   public void parseXPointer(String part) throws XNIException {
      this.fShortHandPointer = part;
      this.fIsFragmentResolved = false;
   }

   public boolean resolveXPointer(QName element, XMLAttributes attributes, Augmentations augs, int event) throws XNIException {
      if (this.fMatchingChildCount == 0) {
         this.fIsFragmentResolved = false;
      }

      if (event == 0) {
         if (this.fMatchingChildCount == 0) {
            this.fIsFragmentResolved = this.hasMatchingIdentifier(element, attributes, augs, event);
         }

         if (this.fIsFragmentResolved) {
            ++this.fMatchingChildCount;
         }
      } else if (event == 2) {
         if (this.fMatchingChildCount == 0) {
            this.fIsFragmentResolved = this.hasMatchingIdentifier(element, attributes, augs, event);
         }
      } else if (this.fIsFragmentResolved) {
         --this.fMatchingChildCount;
      }

      return this.fIsFragmentResolved;
   }

   private boolean hasMatchingIdentifier(QName element, XMLAttributes attributes, Augmentations augs, int event) throws XNIException {
      String normalizedValue = null;
      if (attributes != null) {
         for(int i = 0; i < attributes.getLength(); ++i) {
            normalizedValue = this.getSchemaDeterminedID(attributes, i);
            if (normalizedValue != null) {
               break;
            }

            normalizedValue = this.getChildrenSchemaDeterminedID(attributes, i);
            if (normalizedValue != null) {
               break;
            }

            normalizedValue = this.getDTDDeterminedID(attributes, i);
            if (normalizedValue != null) {
               break;
            }
         }
      }

      return normalizedValue != null && normalizedValue.equals(this.fShortHandPointer);
   }

   public String getDTDDeterminedID(XMLAttributes attributes, int index) throws XNIException {
      return attributes.getType(index).equals("ID") ? attributes.getValue(index) : null;
   }

   public String getSchemaDeterminedID(XMLAttributes attributes, int index) throws XNIException {
      Augmentations augs = attributes.getAugmentations(index);
      AttributePSVI attrPSVI = (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
      if (attrPSVI != null) {
         XSTypeDefinition typeDef = attrPSVI.getMemberTypeDefinition();
         if (typeDef != null) {
            typeDef = attrPSVI.getTypeDefinition();
         }

         if (typeDef != null && ((XSSimpleType)typeDef).isIDType()) {
            return attrPSVI.getSchemaNormalizedValue();
         }
      }

      return null;
   }

   public String getChildrenSchemaDeterminedID(XMLAttributes attributes, int index) throws XNIException {
      return null;
   }

   public boolean isFragmentResolved() {
      return this.fIsFragmentResolved;
   }

   public boolean isChildFragmentResolved() {
      return this.fIsFragmentResolved & this.fMatchingChildCount > 0;
   }

   public String getSchemeName() {
      return this.fShortHandPointer;
   }

   public String getSchemeData() {
      return null;
   }

   public void setSchemeName(String schemeName) {
      this.fShortHandPointer = schemeName;
   }

   public void setSchemeData(String schemeData) {
   }
}
