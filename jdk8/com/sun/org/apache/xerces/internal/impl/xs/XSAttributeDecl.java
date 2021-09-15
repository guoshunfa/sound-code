package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;

public class XSAttributeDecl implements XSAttributeDeclaration {
   public static final short SCOPE_ABSENT = 0;
   public static final short SCOPE_GLOBAL = 1;
   public static final short SCOPE_LOCAL = 2;
   String fName = null;
   String fTargetNamespace = null;
   XSSimpleType fType = null;
   public QName fUnresolvedTypeName = null;
   short fConstraintType = 0;
   short fScope = 0;
   XSComplexTypeDecl fEnclosingCT = null;
   XSObjectList fAnnotations = null;
   ValidatedInfo fDefault = null;
   private XSNamespaceItem fNamespaceItem = null;

   public void setValues(String name, String targetNamespace, XSSimpleType simpleType, short constraintType, short scope, ValidatedInfo valInfo, XSComplexTypeDecl enclosingCT, XSObjectList annotations) {
      this.fName = name;
      this.fTargetNamespace = targetNamespace;
      this.fType = simpleType;
      this.fConstraintType = constraintType;
      this.fScope = scope;
      this.fDefault = valInfo;
      this.fEnclosingCT = enclosingCT;
      this.fAnnotations = annotations;
   }

   public void reset() {
      this.fName = null;
      this.fTargetNamespace = null;
      this.fType = null;
      this.fUnresolvedTypeName = null;
      this.fConstraintType = 0;
      this.fScope = 0;
      this.fDefault = null;
      this.fAnnotations = null;
   }

   public short getType() {
      return 1;
   }

   public String getName() {
      return this.fName;
   }

   public String getNamespace() {
      return this.fTargetNamespace;
   }

   public XSSimpleTypeDefinition getTypeDefinition() {
      return this.fType;
   }

   public short getScope() {
      return this.fScope;
   }

   public XSComplexTypeDefinition getEnclosingCTDefinition() {
      return this.fEnclosingCT;
   }

   public short getConstraintType() {
      return this.fConstraintType;
   }

   public String getConstraintValue() {
      return this.getConstraintType() == 0 ? null : this.fDefault.stringValue();
   }

   public XSAnnotation getAnnotation() {
      return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
   }

   public XSObjectList getAnnotations() {
      return (XSObjectList)(this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST);
   }

   public ValidatedInfo getValInfo() {
      return this.fDefault;
   }

   public XSNamespaceItem getNamespaceItem() {
      return this.fNamespaceItem;
   }

   void setNamespaceItem(XSNamespaceItem namespaceItem) {
      this.fNamespaceItem = namespaceItem;
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
}
