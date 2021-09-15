package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl extends DummyEvent implements EntityDeclaration {
   private XMLResourceIdentifier fXMLResourceIdentifier;
   private String fEntityName;
   private String fReplacementText;
   private String fNotationName;

   public EntityDeclarationImpl() {
      this.init();
   }

   public EntityDeclarationImpl(String entityName, String replacementText) {
      this(entityName, replacementText, (XMLResourceIdentifier)null);
   }

   public EntityDeclarationImpl(String entityName, String replacementText, XMLResourceIdentifier resourceIdentifier) {
      this.init();
      this.fEntityName = entityName;
      this.fReplacementText = replacementText;
      this.fXMLResourceIdentifier = resourceIdentifier;
   }

   public void setEntityName(String entityName) {
      this.fEntityName = entityName;
   }

   public String getEntityName() {
      return this.fEntityName;
   }

   public void setEntityReplacementText(String replacementText) {
      this.fReplacementText = replacementText;
   }

   public void setXMLResourceIdentifier(XMLResourceIdentifier resourceIdentifier) {
      this.fXMLResourceIdentifier = resourceIdentifier;
   }

   public XMLResourceIdentifier getXMLResourceIdentifier() {
      return this.fXMLResourceIdentifier;
   }

   public String getSystemId() {
      return this.fXMLResourceIdentifier != null ? this.fXMLResourceIdentifier.getLiteralSystemId() : null;
   }

   public String getPublicId() {
      return this.fXMLResourceIdentifier != null ? this.fXMLResourceIdentifier.getPublicId() : null;
   }

   public String getBaseURI() {
      return this.fXMLResourceIdentifier != null ? this.fXMLResourceIdentifier.getBaseSystemId() : null;
   }

   public String getName() {
      return this.fEntityName;
   }

   public String getNotationName() {
      return this.fNotationName;
   }

   public void setNotationName(String notationName) {
      this.fNotationName = notationName;
   }

   public String getReplacementText() {
      return this.fReplacementText;
   }

   protected void init() {
      this.setEventType(15);
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write("<!ENTITY ");
      writer.write(this.fEntityName);
      if (this.fReplacementText != null) {
         writer.write(" \"");
         this.charEncode(writer, this.fReplacementText);
      } else {
         String pubId = this.getPublicId();
         if (pubId != null) {
            writer.write(" PUBLIC \"");
            writer.write(pubId);
         } else {
            writer.write(" SYSTEM \"");
            writer.write(this.getSystemId());
         }
      }

      writer.write("\"");
      if (this.fNotationName != null) {
         writer.write(" NDATA ");
         writer.write(this.fNotationName);
      }

      writer.write(">");
   }
}
