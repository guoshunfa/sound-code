package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PSVIElementNSImpl extends ElementNSImpl implements ElementPSVI {
   static final long serialVersionUID = 6815489624636016068L;
   protected XSElementDeclaration fDeclaration = null;
   protected XSTypeDefinition fTypeDecl = null;
   protected boolean fNil = false;
   protected boolean fSpecified = true;
   protected String fNormalizedValue = null;
   protected Object fActualValue = null;
   protected short fActualValueType = 45;
   protected ShortList fItemValueTypes = null;
   protected XSNotationDeclaration fNotation = null;
   protected XSSimpleTypeDefinition fMemberType = null;
   protected short fValidationAttempted = 0;
   protected short fValidity = 0;
   protected StringList fErrorCodes = null;
   protected String fValidationContext = null;
   protected XSModel fSchemaInformation = null;

   public PSVIElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
      super(ownerDocument, namespaceURI, qualifiedName, localName);
   }

   public PSVIElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) {
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

   public boolean getNil() {
      return this.fNil;
   }

   public XSNotationDeclaration getNotation() {
      return this.fNotation;
   }

   public XSTypeDefinition getTypeDefinition() {
      return this.fTypeDecl;
   }

   public XSSimpleTypeDefinition getMemberTypeDefinition() {
      return this.fMemberType;
   }

   public XSElementDeclaration getElementDeclaration() {
      return this.fDeclaration;
   }

   public XSModel getSchemaInformation() {
      return this.fSchemaInformation;
   }

   public void setPSVI(ElementPSVI elem) {
      this.fDeclaration = elem.getElementDeclaration();
      this.fNotation = elem.getNotation();
      this.fValidationContext = elem.getValidationContext();
      this.fTypeDecl = elem.getTypeDefinition();
      this.fSchemaInformation = elem.getSchemaInformation();
      this.fValidity = elem.getValidity();
      this.fValidationAttempted = elem.getValidationAttempted();
      this.fErrorCodes = elem.getErrorCodes();
      this.fNormalizedValue = elem.getSchemaNormalizedValue();
      this.fActualValue = elem.getActualNormalizedValue();
      this.fActualValueType = elem.getActualNormalizedValueType();
      this.fItemValueTypes = elem.getItemValueTypes();
      this.fMemberType = elem.getMemberTypeDefinition();
      this.fSpecified = elem.getIsSchemaSpecified();
      this.fNil = elem.getNil();
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
