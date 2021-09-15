package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class XSElementDecl implements XSElementDeclaration {
   public static final short SCOPE_ABSENT = 0;
   public static final short SCOPE_GLOBAL = 1;
   public static final short SCOPE_LOCAL = 2;
   public String fName = null;
   public String fTargetNamespace = null;
   public XSTypeDefinition fType = null;
   public QName fUnresolvedTypeName = null;
   short fMiscFlags = 0;
   public short fScope = 0;
   XSComplexTypeDecl fEnclosingCT = null;
   public short fBlock = 0;
   public short fFinal = 0;
   public XSObjectList fAnnotations = null;
   public ValidatedInfo fDefault = null;
   public XSElementDecl fSubGroup = null;
   static final int INITIAL_SIZE = 2;
   int fIDCPos = 0;
   IdentityConstraint[] fIDConstraints = new IdentityConstraint[2];
   private XSNamespaceItem fNamespaceItem = null;
   private static final short CONSTRAINT_MASK = 3;
   private static final short NILLABLE = 4;
   private static final short ABSTRACT = 8;
   private String fDescription = null;

   public void setConstraintType(short constraintType) {
      this.fMiscFlags = (short)(this.fMiscFlags ^ this.fMiscFlags & 3);
      this.fMiscFlags = (short)(this.fMiscFlags | constraintType & 3);
   }

   public void setIsNillable() {
      this.fMiscFlags = (short)(this.fMiscFlags | 4);
   }

   public void setIsAbstract() {
      this.fMiscFlags = (short)(this.fMiscFlags | 8);
   }

   public void setIsGlobal() {
      this.fScope = 1;
   }

   public void setIsLocal(XSComplexTypeDecl enclosingCT) {
      this.fScope = 2;
      this.fEnclosingCT = enclosingCT;
   }

   public void addIDConstraint(IdentityConstraint idc) {
      if (this.fIDCPos == this.fIDConstraints.length) {
         this.fIDConstraints = resize(this.fIDConstraints, this.fIDCPos * 2);
      }

      this.fIDConstraints[this.fIDCPos++] = idc;
   }

   public IdentityConstraint[] getIDConstraints() {
      if (this.fIDCPos == 0) {
         return null;
      } else {
         if (this.fIDCPos < this.fIDConstraints.length) {
            this.fIDConstraints = resize(this.fIDConstraints, this.fIDCPos);
         }

         return this.fIDConstraints;
      }
   }

   static final IdentityConstraint[] resize(IdentityConstraint[] oldArray, int newSize) {
      IdentityConstraint[] newArray = new IdentityConstraint[newSize];
      System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
      return newArray;
   }

   public String toString() {
      if (this.fDescription == null) {
         if (this.fTargetNamespace != null) {
            StringBuffer buffer = new StringBuffer(this.fTargetNamespace.length() + (this.fName != null ? this.fName.length() : 4) + 3);
            buffer.append('"');
            buffer.append(this.fTargetNamespace);
            buffer.append('"');
            buffer.append(':');
            buffer.append(this.fName);
            this.fDescription = buffer.toString();
         } else {
            this.fDescription = this.fName;
         }
      }

      return this.fDescription;
   }

   public int hashCode() {
      int code = this.fName.hashCode();
      if (this.fTargetNamespace != null) {
         code = (code << 16) + this.fTargetNamespace.hashCode();
      }

      return code;
   }

   public boolean equals(Object o) {
      return o == this;
   }

   public void reset() {
      this.fScope = 0;
      this.fName = null;
      this.fTargetNamespace = null;
      this.fType = null;
      this.fUnresolvedTypeName = null;
      this.fMiscFlags = 0;
      this.fBlock = 0;
      this.fFinal = 0;
      this.fDefault = null;
      this.fAnnotations = null;
      this.fSubGroup = null;

      for(int i = 0; i < this.fIDCPos; ++i) {
         this.fIDConstraints[i] = null;
      }

      this.fIDCPos = 0;
   }

   public short getType() {
      return 2;
   }

   public String getName() {
      return this.fName;
   }

   public String getNamespace() {
      return this.fTargetNamespace;
   }

   public XSTypeDefinition getTypeDefinition() {
      return this.fType;
   }

   public short getScope() {
      return this.fScope;
   }

   public XSComplexTypeDefinition getEnclosingCTDefinition() {
      return this.fEnclosingCT;
   }

   public short getConstraintType() {
      return (short)(this.fMiscFlags & 3);
   }

   public String getConstraintValue() {
      return this.getConstraintType() == 0 ? null : this.fDefault.stringValue();
   }

   public boolean getNillable() {
      return (this.fMiscFlags & 4) != 0;
   }

   public XSNamedMap getIdentityConstraints() {
      return new XSNamedMapImpl(this.fIDConstraints, this.fIDCPos);
   }

   public XSElementDeclaration getSubstitutionGroupAffiliation() {
      return this.fSubGroup;
   }

   public boolean isSubstitutionGroupExclusion(short exclusion) {
      return (this.fFinal & exclusion) != 0;
   }

   public short getSubstitutionGroupExclusions() {
      return this.fFinal;
   }

   public boolean isDisallowedSubstitution(short disallowed) {
      return (this.fBlock & disallowed) != 0;
   }

   public short getDisallowedSubstitutions() {
      return this.fBlock;
   }

   public boolean getAbstract() {
      return (this.fMiscFlags & 8) != 0;
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
