package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;

public class XSAttributeGroupDecl implements XSAttributeGroupDefinition {
   public String fName = null;
   public String fTargetNamespace = null;
   int fAttrUseNum = 0;
   private static final int INITIAL_SIZE = 5;
   XSAttributeUseImpl[] fAttributeUses = new XSAttributeUseImpl[5];
   public XSWildcardDecl fAttributeWC = null;
   public String fIDAttrName = null;
   public XSObjectList fAnnotations;
   protected XSObjectListImpl fAttrUses = null;
   private XSNamespaceItem fNamespaceItem = null;

   public String addAttributeUse(XSAttributeUseImpl attrUse) {
      if (attrUse.fUse != 2 && attrUse.fAttrDecl.fType.isIDType()) {
         if (this.fIDAttrName != null) {
            return this.fIDAttrName;
         }

         this.fIDAttrName = attrUse.fAttrDecl.fName;
      }

      if (this.fAttrUseNum == this.fAttributeUses.length) {
         this.fAttributeUses = resize(this.fAttributeUses, this.fAttrUseNum * 2);
      }

      this.fAttributeUses[this.fAttrUseNum++] = attrUse;
      return null;
   }

   public void replaceAttributeUse(XSAttributeUse oldUse, XSAttributeUseImpl newUse) {
      for(int i = 0; i < this.fAttrUseNum; ++i) {
         if (this.fAttributeUses[i] == oldUse) {
            this.fAttributeUses[i] = newUse;
         }
      }

   }

   public XSAttributeUse getAttributeUse(String namespace, String name) {
      for(int i = 0; i < this.fAttrUseNum; ++i) {
         if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace == namespace && this.fAttributeUses[i].fAttrDecl.fName == name) {
            return this.fAttributeUses[i];
         }
      }

      return null;
   }

   public XSAttributeUse getAttributeUseNoProhibited(String namespace, String name) {
      for(int i = 0; i < this.fAttrUseNum; ++i) {
         if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace == namespace && this.fAttributeUses[i].fAttrDecl.fName == name && this.fAttributeUses[i].fUse != 2) {
            return this.fAttributeUses[i];
         }
      }

      return null;
   }

   public void removeProhibitedAttrs() {
      if (this.fAttrUseNum != 0) {
         int count = 0;
         XSAttributeUseImpl[] uses = new XSAttributeUseImpl[this.fAttrUseNum];

         for(int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fUse != 2) {
               uses[count++] = this.fAttributeUses[i];
            }
         }

         this.fAttributeUses = uses;
         this.fAttrUseNum = count;
      }
   }

   public Object[] validRestrictionOf(String typeName, XSAttributeGroupDecl baseGroup) {
      Object[] errorArgs = null;
      XSAttributeUseImpl attrUse = null;
      XSAttributeDecl attrDecl = null;
      XSAttributeUseImpl baseAttrUse = null;
      XSAttributeDecl baseAttrDecl = null;

      int i;
      for(i = 0; i < this.fAttrUseNum; ++i) {
         attrUse = this.fAttributeUses[i];
         attrDecl = attrUse.fAttrDecl;
         baseAttrUse = (XSAttributeUseImpl)baseGroup.getAttributeUse(attrDecl.fTargetNamespace, attrDecl.fName);
         if (baseAttrUse != null) {
            if (baseAttrUse.getRequired() && !attrUse.getRequired()) {
               errorArgs = new Object[]{typeName, attrDecl.fName, attrUse.fUse == 0 ? "optional" : "prohibited", "derivation-ok-restriction.2.1.1"};
               return errorArgs;
            }

            if (attrUse.fUse != 2) {
               baseAttrDecl = baseAttrUse.fAttrDecl;
               if (!XSConstraints.checkSimpleDerivationOk(attrDecl.fType, baseAttrDecl.fType, baseAttrDecl.fType.getFinal())) {
                  errorArgs = new Object[]{typeName, attrDecl.fName, attrDecl.fType.getName(), baseAttrDecl.fType.getName(), "derivation-ok-restriction.2.1.2"};
                  return errorArgs;
               }

               int baseConsType = baseAttrUse.fConstraintType != 0 ? baseAttrUse.fConstraintType : baseAttrDecl.getConstraintType();
               int thisConstType = attrUse.fConstraintType != 0 ? attrUse.fConstraintType : attrDecl.getConstraintType();
               if (baseConsType == 2) {
                  if (thisConstType != 2) {
                     errorArgs = new Object[]{typeName, attrDecl.fName, "derivation-ok-restriction.2.1.3.a"};
                     return errorArgs;
                  }

                  ValidatedInfo baseFixedValue = baseAttrUse.fDefault != null ? baseAttrUse.fDefault : baseAttrDecl.fDefault;
                  ValidatedInfo thisFixedValue = attrUse.fDefault != null ? attrUse.fDefault : attrDecl.fDefault;
                  if (!baseFixedValue.actualValue.equals(thisFixedValue.actualValue)) {
                     errorArgs = new Object[]{typeName, attrDecl.fName, thisFixedValue.stringValue(), baseFixedValue.stringValue(), "derivation-ok-restriction.2.1.3.b"};
                     return errorArgs;
                  }
               }
            }
         } else {
            if (baseGroup.fAttributeWC == null) {
               errorArgs = new Object[]{typeName, attrDecl.fName, "derivation-ok-restriction.2.2.a"};
               return errorArgs;
            }

            if (!baseGroup.fAttributeWC.allowNamespace(attrDecl.fTargetNamespace)) {
               errorArgs = new Object[]{typeName, attrDecl.fName, attrDecl.fTargetNamespace == null ? "" : attrDecl.fTargetNamespace, "derivation-ok-restriction.2.2.b"};
               return errorArgs;
            }
         }
      }

      for(i = 0; i < baseGroup.fAttrUseNum; ++i) {
         baseAttrUse = baseGroup.fAttributeUses[i];
         if (baseAttrUse.fUse == 1) {
            baseAttrDecl = baseAttrUse.fAttrDecl;
            if (this.getAttributeUse(baseAttrDecl.fTargetNamespace, baseAttrDecl.fName) == null) {
               errorArgs = new Object[]{typeName, baseAttrUse.fAttrDecl.fName, "derivation-ok-restriction.3"};
               return errorArgs;
            }
         }
      }

      if (this.fAttributeWC != null) {
         if (baseGroup.fAttributeWC == null) {
            errorArgs = new Object[]{typeName, "derivation-ok-restriction.4.1"};
            return errorArgs;
         }

         if (!this.fAttributeWC.isSubsetOf(baseGroup.fAttributeWC)) {
            errorArgs = new Object[]{typeName, "derivation-ok-restriction.4.2"};
            return errorArgs;
         }

         if (this.fAttributeWC.weakerProcessContents(baseGroup.fAttributeWC)) {
            errorArgs = new Object[]{typeName, this.fAttributeWC.getProcessContentsAsString(), baseGroup.fAttributeWC.getProcessContentsAsString(), "derivation-ok-restriction.4.3"};
            return errorArgs;
         }
      }

      return null;
   }

   static final XSAttributeUseImpl[] resize(XSAttributeUseImpl[] oldArray, int newSize) {
      XSAttributeUseImpl[] newArray = new XSAttributeUseImpl[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
      return newArray;
   }

   public void reset() {
      this.fName = null;
      this.fTargetNamespace = null;

      for(int i = 0; i < this.fAttrUseNum; ++i) {
         this.fAttributeUses[i] = null;
      }

      this.fAttrUseNum = 0;
      this.fAttributeWC = null;
      this.fAnnotations = null;
      this.fIDAttrName = null;
   }

   public short getType() {
      return 5;
   }

   public String getName() {
      return this.fName;
   }

   public String getNamespace() {
      return this.fTargetNamespace;
   }

   public XSObjectList getAttributeUses() {
      if (this.fAttrUses == null) {
         this.fAttrUses = new XSObjectListImpl(this.fAttributeUses, this.fAttrUseNum);
      }

      return this.fAttrUses;
   }

   public XSWildcard getAttributeWildcard() {
      return this.fAttributeWC;
   }

   public XSAnnotation getAnnotation() {
      return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
   }

   public XSObjectList getAnnotations() {
      return (XSObjectList)(this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST);
   }

   public XSNamespaceItem getNamespaceItem() {
      return this.fNamespaceItem;
   }

   void setNamespaceItem(XSNamespaceItem namespaceItem) {
      this.fNamespaceItem = namespaceItem;
   }
}
