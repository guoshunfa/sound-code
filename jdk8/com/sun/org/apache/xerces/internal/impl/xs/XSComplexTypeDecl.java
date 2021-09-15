package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import org.w3c.dom.TypeInfo;

public class XSComplexTypeDecl implements XSComplexTypeDefinition, TypeInfo {
   String fName = null;
   String fTargetNamespace = null;
   XSTypeDefinition fBaseType = null;
   short fDerivedBy = 2;
   short fFinal = 0;
   short fBlock = 0;
   short fMiscFlags = 0;
   XSAttributeGroupDecl fAttrGrp = null;
   short fContentType = 0;
   XSSimpleType fXSSimpleType = null;
   XSParticleDecl fParticle = null;
   volatile XSCMValidator fCMValidator = null;
   XSCMValidator fUPACMValidator = null;
   XSObjectListImpl fAnnotations = null;
   private XSNamespaceItem fNamespaceItem = null;
   static final int DERIVATION_ANY = 0;
   static final int DERIVATION_RESTRICTION = 1;
   static final int DERIVATION_EXTENSION = 2;
   static final int DERIVATION_UNION = 4;
   static final int DERIVATION_LIST = 8;
   private static final short CT_IS_ABSTRACT = 1;
   private static final short CT_HAS_TYPE_ID = 2;
   private static final short CT_IS_ANONYMOUS = 4;

   public void setValues(String name, String targetNamespace, XSTypeDefinition baseType, short derivedBy, short schemaFinal, short block, short contentType, boolean isAbstract, XSAttributeGroupDecl attrGrp, XSSimpleType simpleType, XSParticleDecl particle, XSObjectListImpl annotations) {
      this.fTargetNamespace = targetNamespace;
      this.fBaseType = baseType;
      this.fDerivedBy = derivedBy;
      this.fFinal = schemaFinal;
      this.fBlock = block;
      this.fContentType = contentType;
      if (isAbstract) {
         this.fMiscFlags = (short)(this.fMiscFlags | 1);
      }

      this.fAttrGrp = attrGrp;
      this.fXSSimpleType = simpleType;
      this.fParticle = particle;
      this.fAnnotations = annotations;
   }

   public void setName(String name) {
      this.fName = name;
   }

   public short getTypeCategory() {
      return 15;
   }

   public String getTypeName() {
      return this.fName;
   }

   public short getFinalSet() {
      return this.fFinal;
   }

   public String getTargetNamespace() {
      return this.fTargetNamespace;
   }

   public boolean containsTypeID() {
      return (this.fMiscFlags & 2) != 0;
   }

   public void setIsAbstractType() {
      this.fMiscFlags = (short)(this.fMiscFlags | 1);
   }

   public void setContainsTypeID() {
      this.fMiscFlags = (short)(this.fMiscFlags | 2);
   }

   public void setIsAnonymous() {
      this.fMiscFlags = (short)(this.fMiscFlags | 4);
   }

   public XSCMValidator getContentModel(CMBuilder cmBuilder) {
      if (this.fContentType != 1 && this.fContentType != 0) {
         if (this.fCMValidator == null) {
            synchronized(this) {
               if (this.fCMValidator == null) {
                  this.fCMValidator = cmBuilder.getContentModel(this);
               }
            }
         }

         return this.fCMValidator;
      } else {
         return null;
      }
   }

   public XSAttributeGroupDecl getAttrGrp() {
      return this.fAttrGrp;
   }

   public String toString() {
      StringBuilder str = new StringBuilder(192);
      this.appendTypeInfo(str);
      return str.toString();
   }

   void appendTypeInfo(StringBuilder str) {
      String[] contentType = new String[]{"EMPTY", "SIMPLE", "ELEMENT", "MIXED"};
      String[] derivedBy = new String[]{"EMPTY", "EXTENSION", "RESTRICTION"};
      str.append("Complex type name='").append(this.fTargetNamespace).append(',').append(this.getTypeName()).append("', ");
      if (this.fBaseType != null) {
         str.append(" base type name='").append(this.fBaseType.getName()).append("', ");
      }

      str.append(" content type='").append(contentType[this.fContentType]).append("', ");
      str.append(" isAbstract='").append(this.getAbstract()).append("', ");
      str.append(" hasTypeId='").append(this.containsTypeID()).append("', ");
      str.append(" final='").append((int)this.fFinal).append("', ");
      str.append(" block='").append((int)this.fBlock).append("', ");
      if (this.fParticle != null) {
         str.append(" particle='").append(this.fParticle.toString()).append("', ");
      }

      str.append(" derivedBy='").append(derivedBy[this.fDerivedBy]).append("'. ");
   }

   public boolean derivedFromType(XSTypeDefinition ancestor, short derivationMethod) {
      if (ancestor == null) {
         return false;
      } else if (ancestor == SchemaGrammar.fAnyType) {
         return true;
      } else {
         Object type;
         for(type = this; type != ancestor && type != SchemaGrammar.fAnySimpleType && type != SchemaGrammar.fAnyType; type = ((XSTypeDefinition)type).getBaseType()) {
         }

         return type == ancestor;
      }
   }

   public boolean derivedFrom(String ancestorNS, String ancestorName, short derivationMethod) {
      if (ancestorName == null) {
         return false;
      } else if (ancestorNS != null && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
         return true;
      } else {
         Object type;
         for(type = this; (!ancestorName.equals(((XSTypeDefinition)type).getName()) || (ancestorNS != null || ((XSTypeDefinition)type).getNamespace() != null) && (ancestorNS == null || !ancestorNS.equals(((XSTypeDefinition)type).getNamespace()))) && type != SchemaGrammar.fAnySimpleType && type != SchemaGrammar.fAnyType; type = ((XSTypeDefinition)type).getBaseType()) {
         }

         return type != SchemaGrammar.fAnySimpleType && type != SchemaGrammar.fAnyType;
      }
   }

   public boolean isDOMDerivedFrom(String ancestorNS, String ancestorName, int derivationMethod) {
      if (ancestorName == null) {
         return false;
      } else if (ancestorNS != null && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType") && derivationMethod == 1 && derivationMethod == 2) {
         return true;
      } else if ((derivationMethod & 1) != 0 && this.isDerivedByRestriction(ancestorNS, ancestorName, derivationMethod, this)) {
         return true;
      } else if ((derivationMethod & 2) != 0 && this.isDerivedByExtension(ancestorNS, ancestorName, derivationMethod, this)) {
         return true;
      } else {
         if (((derivationMethod & 8) != 0 || (derivationMethod & 4) != 0) && (derivationMethod & 1) == 0 && (derivationMethod & 2) == 0) {
            if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
               ancestorName = "anySimpleType";
            }

            if (!this.fName.equals("anyType") || !this.fTargetNamespace.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
               if (this.fBaseType != null && this.fBaseType instanceof XSSimpleTypeDecl) {
                  return ((XSSimpleTypeDecl)this.fBaseType).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
               }

               if (this.fBaseType != null && this.fBaseType instanceof XSComplexTypeDecl) {
                  return ((XSComplexTypeDecl)this.fBaseType).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
               }
            }
         }

         return (derivationMethod & 2) == 0 && (derivationMethod & 1) == 0 && (derivationMethod & 8) == 0 && (derivationMethod & 4) == 0 ? this.isDerivedByAny(ancestorNS, ancestorName, derivationMethod, this) : false;
      }
   }

   private boolean isDerivedByAny(String ancestorNS, String ancestorName, int derivationMethod, XSTypeDefinition type) {
      XSTypeDefinition oldType = null;

      boolean derivedFrom;
      for(derivedFrom = false; type != null && type != oldType; type = type.getBaseType()) {
         if (ancestorName.equals(type.getName()) && (ancestorNS == null && type.getNamespace() == null || ancestorNS != null && ancestorNS.equals(type.getNamespace()))) {
            derivedFrom = true;
            break;
         }

         if (this.isDerivedByRestriction(ancestorNS, ancestorName, derivationMethod, type)) {
            return true;
         }

         if (!this.isDerivedByExtension(ancestorNS, ancestorName, derivationMethod, type)) {
            return true;
         }

         oldType = type;
      }

      return derivedFrom;
   }

   private boolean isDerivedByRestriction(String ancestorNS, String ancestorName, int derivationMethod, XSTypeDefinition type) {
      for(XSTypeDefinition oldType = null; type != null && type != oldType; type = type.getBaseType()) {
         if (ancestorNS != null && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anySimpleType")) {
            return false;
         }

         if (ancestorName.equals(type.getName()) && ancestorNS != null && ancestorNS.equals(type.getNamespace()) || type.getNamespace() == null && ancestorNS == null) {
            return true;
         }

         if (type instanceof XSSimpleTypeDecl) {
            if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
               ancestorName = "anySimpleType";
            }

            return ((XSSimpleTypeDecl)type).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
         }

         if (((XSComplexTypeDecl)type).getDerivationMethod() != 2) {
            return false;
         }

         oldType = type;
      }

      return false;
   }

   private boolean isDerivedByExtension(String ancestorNS, String ancestorName, int derivationMethod, XSTypeDefinition type) {
      boolean extension = false;

      for(XSTypeDefinition oldType = null; type != null && type != oldType && (ancestorNS == null || !ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) || !ancestorName.equals("anySimpleType") || !SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(type.getNamespace()) || !"anyType".equals(type.getName())); type = type.getBaseType()) {
         if (ancestorName.equals(type.getName()) && (ancestorNS == null && type.getNamespace() == null || ancestorNS != null && ancestorNS.equals(type.getNamespace()))) {
            return extension;
         }

         if (type instanceof XSSimpleTypeDecl) {
            if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && ancestorName.equals("anyType")) {
               ancestorName = "anySimpleType";
            }

            if ((derivationMethod & 2) != 0) {
               return extension & ((XSSimpleTypeDecl)type).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod & 1);
            }

            return extension & ((XSSimpleTypeDecl)type).isDOMDerivedFrom(ancestorNS, ancestorName, derivationMethod);
         }

         if (((XSComplexTypeDecl)type).getDerivationMethod() == 1) {
            extension |= true;
         }

         oldType = type;
      }

      return false;
   }

   public void reset() {
      this.fName = null;
      this.fTargetNamespace = null;
      this.fBaseType = null;
      this.fDerivedBy = 2;
      this.fFinal = 0;
      this.fBlock = 0;
      this.fMiscFlags = 0;
      this.fAttrGrp.reset();
      this.fContentType = 0;
      this.fXSSimpleType = null;
      this.fParticle = null;
      this.fCMValidator = null;
      this.fUPACMValidator = null;
      if (this.fAnnotations != null) {
         this.fAnnotations.clearXSObjectList();
      }

      this.fAnnotations = null;
   }

   public short getType() {
      return 3;
   }

   public String getName() {
      return this.getAnonymous() ? null : this.fName;
   }

   public boolean getAnonymous() {
      return (this.fMiscFlags & 4) != 0;
   }

   public String getNamespace() {
      return this.fTargetNamespace;
   }

   public XSTypeDefinition getBaseType() {
      return this.fBaseType;
   }

   public short getDerivationMethod() {
      return this.fDerivedBy;
   }

   public boolean isFinal(short derivation) {
      return (this.fFinal & derivation) != 0;
   }

   public short getFinal() {
      return this.fFinal;
   }

   public boolean getAbstract() {
      return (this.fMiscFlags & 1) != 0;
   }

   public XSObjectList getAttributeUses() {
      return this.fAttrGrp.getAttributeUses();
   }

   public XSWildcard getAttributeWildcard() {
      return this.fAttrGrp.getAttributeWildcard();
   }

   public short getContentType() {
      return this.fContentType;
   }

   public XSSimpleTypeDefinition getSimpleType() {
      return this.fXSSimpleType;
   }

   public XSParticle getParticle() {
      return this.fParticle;
   }

   public boolean isProhibitedSubstitution(short prohibited) {
      return (this.fBlock & prohibited) != 0;
   }

   public short getProhibitedSubstitutions() {
      return this.fBlock;
   }

   public XSObjectList getAnnotations() {
      return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
   }

   public XSNamespaceItem getNamespaceItem() {
      return this.fNamespaceItem;
   }

   void setNamespaceItem(XSNamespaceItem namespaceItem) {
      this.fNamespaceItem = namespaceItem;
   }

   public XSAttributeUse getAttributeUse(String namespace, String name) {
      return this.fAttrGrp.getAttributeUse(namespace, name);
   }

   public String getTypeNamespace() {
      return this.getNamespace();
   }

   public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
      return this.isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
   }
}
