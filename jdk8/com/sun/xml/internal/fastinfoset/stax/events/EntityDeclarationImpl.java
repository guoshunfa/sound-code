package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl extends EventBase implements EntityDeclaration {
   private String _publicId;
   private String _systemId;
   private String _baseURI;
   private String _entityName;
   private String _replacement;
   private String _notationName;

   public EntityDeclarationImpl() {
      this.init();
   }

   public EntityDeclarationImpl(String entityName, String replacement) {
      this.init();
      this._entityName = entityName;
      this._replacement = replacement;
   }

   public String getPublicId() {
      return this._publicId;
   }

   public String getSystemId() {
      return this._systemId;
   }

   public String getName() {
      return this._entityName;
   }

   public String getNotationName() {
      return this._notationName;
   }

   public String getReplacementText() {
      return this._replacement;
   }

   public String getBaseURI() {
      return this._baseURI;
   }

   public void setPublicId(String publicId) {
      this._publicId = publicId;
   }

   public void setSystemId(String systemId) {
      this._systemId = systemId;
   }

   public void setBaseURI(String baseURI) {
      this._baseURI = baseURI;
   }

   public void setName(String entityName) {
      this._entityName = entityName;
   }

   public void setReplacementText(String replacement) {
      this._replacement = replacement;
   }

   public void setNotationName(String notationName) {
      this._notationName = notationName;
   }

   protected void init() {
      this.setEventType(15);
   }
}
