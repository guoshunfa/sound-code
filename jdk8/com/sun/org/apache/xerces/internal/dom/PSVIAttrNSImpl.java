package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PSVIAttrNSImpl extends AttrNSImpl implements AttributePSVI {
   static final long serialVersionUID = -3241738699421018889L;
   protected XSAttributeDeclaration fDeclaration = null;
   protected XSTypeDefinition fTypeDecl = null;
   protected boolean fSpecified = true;
   protected String fNormalizedValue = null;
   protected Object fActualValue = null;
   protected short fActualValueType = 45;
   protected ShortList fItemValueTypes = null;
   protected XSSimpleTypeDefinition fMemberType = null;
   protected short fValidationAttempted = 0;
   protected short fValidity = 0;
   protected StringList fErrorCodes = null;
   protected String fValidationContext = null;

   public PSVIAttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
      super(ownerDocument, namespaceURI, qualifiedName, localName);
   }

   public PSVIAttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) {
      super(ownerDocument, namespaceURI, qualifiedName);
   }

   public String getSchemaDefault() {
      return this.fDeclaration == null ? null : this.fDeclaration.getConstraintValue();
   }

   public String getSchemaNormalizedValue() {
      return this.fNormalizedValue;
   }

   public boolean getIsSchemaSpecified() {
      return this.fSpecified;
   }

   public short getValidationAttempted() {
      return this.fValidationAttempted;
   }

   public short getValidity() {
      return this.fValidity;
   }

   public StringList getErrorCodes() {
      return this.fErrorCodes;
   }

   public String getValidationContext() {
      return this.fValidationContext;
   }

   public XSTypeDefinition getTypeDefinition() {
      return this.fTypeDecl;
   }

   public XSSimpleTypeDefinition getMemberTypeDefinition() {
      return this.fMemberType;
   }

   public XSAttributeDeclaration getAttributeDeclaration() {
      return this.fDeclaration;
   }

   public void setPSVI(AttributePSVI attr) {
      this.fDeclaration = attr.getAttributeDeclaration();
      this.fValidationContext = attr.getValidationContext();
      this.fValidity = attr.getValidity();
      this.fValidationAttempted = attr.getValidationAttempted();
      this.fErrorCodes = attr.getErrorCodes();
      this.fNormalizedValue = attr.getSchemaNormalizedValue();
      this.fActualValue = attr.getActualNormalizedValue();
      this.fActualValueType = attr.getActualNormalizedValueType();
      this.fItemValueTypes = attr.getItemValueTypes();
      this.fTypeDecl = attr.getTypeDefinition();
      this.fMemberType = attr.getMemberTypeDefinition();
      this.fSpecified = attr.getIsSchemaSpecified();
   }

   public Object getActualNormalizedValue() {
      return this.fActualValue;
   }

   public short getActualNormalizedValueType() {
      return this.fActualValueType;
   }

   public ShortList getItemValueTypes() {
      return this.fItemValueTypes;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      throw new NotSerializableException(this.getClass().getName());
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      throw new NotSerializableException(this.getClass().getName());
   }
}
