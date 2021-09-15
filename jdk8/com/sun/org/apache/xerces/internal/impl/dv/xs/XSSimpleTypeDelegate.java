package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class XSSimpleTypeDelegate implements XSSimpleType {
   protected final XSSimpleType type;

   public XSSimpleTypeDelegate(XSSimpleType type) {
      if (type == null) {
         throw new NullPointerException();
      } else {
         this.type = type;
      }
   }

   public XSSimpleType getWrappedXSSimpleType() {
      return this.type;
   }

   public XSObjectList getAnnotations() {
      return this.type.getAnnotations();
   }

   public boolean getBounded() {
      return this.type.getBounded();
   }

   public short getBuiltInKind() {
      return this.type.getBuiltInKind();
   }

   public short getDefinedFacets() {
      return this.type.getDefinedFacets();
   }

   public XSObjectList getFacets() {
      return this.type.getFacets();
   }

   public boolean getFinite() {
      return this.type.getFinite();
   }

   public short getFixedFacets() {
      return this.type.getFixedFacets();
   }

   public XSSimpleTypeDefinition getItemType() {
      return this.type.getItemType();
   }

   public StringList getLexicalEnumeration() {
      return this.type.getLexicalEnumeration();
   }

   public String getLexicalFacetValue(short facetName) {
      return this.type.getLexicalFacetValue(facetName);
   }

   public StringList getLexicalPattern() {
      return this.type.getLexicalPattern();
   }

   public XSObjectList getMemberTypes() {
      return this.type.getMemberTypes();
   }

   public XSObjectList getMultiValueFacets() {
      return this.type.getMultiValueFacets();
   }

   public boolean getNumeric() {
      return this.type.getNumeric();
   }

   public short getOrdered() {
      return this.type.getOrdered();
   }

   public XSSimpleTypeDefinition getPrimitiveType() {
      return this.type.getPrimitiveType();
   }

   public short getVariety() {
      return this.type.getVariety();
   }

   public boolean isDefinedFacet(short facetName) {
      return this.type.isDefinedFacet(facetName);
   }

   public boolean isFixedFacet(short facetName) {
      return this.type.isFixedFacet(facetName);
   }

   public boolean derivedFrom(String namespace, String name, short derivationMethod) {
      return this.type.derivedFrom(namespace, name, derivationMethod);
   }

   public boolean derivedFromType(XSTypeDefinition ancestorType, short derivationMethod) {
      return this.type.derivedFromType(ancestorType, derivationMethod);
   }

   public boolean getAnonymous() {
      return this.type.getAnonymous();
   }

   public XSTypeDefinition getBaseType() {
      return this.type.getBaseType();
   }

   public short getFinal() {
      return this.type.getFinal();
   }

   public short getTypeCategory() {
      return this.type.getTypeCategory();
   }

   public boolean isFinal(short restriction) {
      return this.type.isFinal(restriction);
   }

   public String getName() {
      return this.type.getName();
   }

   public String getNamespace() {
      return this.type.getNamespace();
   }

   public XSNamespaceItem getNamespaceItem() {
      return this.type.getNamespaceItem();
   }

   public short getType() {
      return this.type.getType();
   }

   public void applyFacets(XSFacets facets, short presentFacet, short fixedFacet, ValidationContext context) throws InvalidDatatypeFacetException {
      this.type.applyFacets(facets, presentFacet, fixedFacet, context);
   }

   public short getPrimitiveKind() {
      return this.type.getPrimitiveKind();
   }

   public short getWhitespace() throws DatatypeException {
      return this.type.getWhitespace();
   }

   public boolean isEqual(Object value1, Object value2) {
      return this.type.isEqual(value1, value2);
   }

   public boolean isIDType() {
      return this.type.isIDType();
   }

   public void validate(ValidationContext context, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
      this.type.validate(context, validatedInfo);
   }

   public Object validate(String content, ValidationContext context, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
      return this.type.validate(content, context, validatedInfo);
   }

   public Object validate(Object content, ValidationContext context, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
      return this.type.validate(content, context, validatedInfo);
   }

   public String toString() {
      return this.type.toString();
   }
}
