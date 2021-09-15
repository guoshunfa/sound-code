package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class XSAttributeUseImpl implements XSAttributeUse {
   public XSAttributeDecl fAttrDecl = null;
   public short fUse = 0;
   public short fConstraintType = 0;
   public ValidatedInfo fDefault = null;
   public XSObjectList fAnnotations = null;

   public void reset() {
      this.fDefault = null;
      this.fAttrDecl = null;
      this.fUse = 0;
      this.fConstraintType = 0;
      this.fAnnotations = null;
   }

   public short getType() {
      return 4;
   }

   public String getName() {
      return null;
   }

   public String getNamespace() {
      return null;
   }

   public boolean getRequired() {
      return this.fUse == 1;
   }

   public XSAttributeDeclaration getAttrDeclaration() {
      return this.fAttrDecl;
   }

   public short getConstraintType() {
      return this.fConstraintType;
   }

   public String getConstraintValue() {
      return this.getConstraintType() == 0 ? null : this.fDefault.stringValue();
   }

   public XSNamespaceItem getNamespaceItem() {
      return null;
   }

   public Object getActualVC() {
      return this.getConstraintType() == 0 ? null : this.fDefault.actualValue;
   }

   public short getActualVCType() {
      return this.getConstraintType() == 0 ? 45 : this.fDefault.actualValueType;
   }

   public ShortList getItemValueTypes() {
      return this.getConstraintType() == 0 ? null : this.fDefault.itemValueTypes;
   }

   public XSObjectList getAnnotations() {
      return (XSObjectList)(this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST);
   }
}
