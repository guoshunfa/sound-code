package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.util.HashMap;

final class UnparsedEntityHandler implements XMLDTDFilter, EntityState {
   private XMLDTDSource fDTDSource;
   private XMLDTDHandler fDTDHandler;
   private final ValidationManager fValidationManager;
   private HashMap fUnparsedEntities = null;

   UnparsedEntityHandler(ValidationManager manager) {
      this.fValidationManager = manager;
   }

   public void startDTD(XMLLocator locator, Augmentations augmentations) throws XNIException {
      this.fValidationManager.setEntityState(this);
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startDTD(locator, augmentations);
      }

   }

   public void startParameterEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startParameterEntity(name, identifier, encoding, augmentations);
      }

   }

   public void textDecl(String version, String encoding, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.textDecl(version, encoding, augmentations);
      }

   }

   public void endParameterEntity(String name, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endParameterEntity(name, augmentations);
      }

   }

   public void startExternalSubset(XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startExternalSubset(identifier, augmentations);
      }

   }

   public void endExternalSubset(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endExternalSubset(augmentations);
      }

   }

   public void comment(XMLString text, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.comment(text, augmentations);
      }

   }

   public void processingInstruction(String target, XMLString data, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.processingInstruction(target, data, augmentations);
      }

   }

   public void elementDecl(String name, String contentModel, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.elementDecl(name, contentModel, augmentations);
      }

   }

   public void startAttlist(String elementName, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startAttlist(elementName, augmentations);
      }

   }

   public void attributeDecl(String elementName, String attributeName, String type, String[] enumeration, String defaultType, XMLString defaultValue, XMLString nonNormalizedDefaultValue, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augmentations);
      }

   }

   public void endAttlist(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endAttlist(augmentations);
      }

   }

   public void internalEntityDecl(String name, XMLString text, XMLString nonNormalizedText, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augmentations);
      }

   }

   public void externalEntityDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.externalEntityDecl(name, identifier, augmentations);
      }

   }

   public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier, String notation, Augmentations augmentations) throws XNIException {
      if (this.fUnparsedEntities == null) {
         this.fUnparsedEntities = new HashMap();
      }

      this.fUnparsedEntities.put(name, name);
      if (this.fDTDHandler != null) {
         this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augmentations);
      }

   }

   public void notationDecl(String name, XMLResourceIdentifier identifier, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.notationDecl(name, identifier, augmentations);
      }

   }

   public void startConditional(short type, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.startConditional(type, augmentations);
      }

   }

   public void ignoredCharacters(XMLString text, Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.ignoredCharacters(text, augmentations);
      }

   }

   public void endConditional(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endConditional(augmentations);
      }

   }

   public void endDTD(Augmentations augmentations) throws XNIException {
      if (this.fDTDHandler != null) {
         this.fDTDHandler.endDTD(augmentations);
      }

   }

   public void setDTDSource(XMLDTDSource source) {
      this.fDTDSource = source;
   }

   public XMLDTDSource getDTDSource() {
      return this.fDTDSource;
   }

   public void setDTDHandler(XMLDTDHandler handler) {
      this.fDTDHandler = handler;
   }

   public XMLDTDHandler getDTDHandler() {
      return this.fDTDHandler;
   }

   public boolean isEntityDeclared(String name) {
      return false;
   }

   public boolean isEntityUnparsed(String name) {
      return this.fUnparsedEntities != null ? this.fUnparsedEntities.containsKey(name) : false;
   }

   public void reset() {
      if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
         this.fUnparsedEntities.clear();
      }

   }
}
