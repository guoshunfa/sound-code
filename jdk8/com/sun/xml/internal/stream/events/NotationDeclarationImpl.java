package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.NotationDeclaration;

public class NotationDeclarationImpl extends DummyEvent implements NotationDeclaration {
   String fName = null;
   String fPublicId = null;
   String fSystemId = null;

   public NotationDeclarationImpl() {
      this.setEventType(14);
   }

   public NotationDeclarationImpl(String name, String publicId, String systemId) {
      this.fName = name;
      this.fPublicId = publicId;
      this.fSystemId = systemId;
      this.setEventType(14);
   }

   public NotationDeclarationImpl(XMLNotationDecl notation) {
      this.fName = notation.name;
      this.fPublicId = notation.publicId;
      this.fSystemId = notation.systemId;
      this.setEventType(14);
   }

   public String getName() {
      return this.fName;
   }

   public String getPublicId() {
      return this.fPublicId;
   }

   public String getSystemId() {
      return this.fSystemId;
   }

   void setPublicId(String publicId) {
      this.fPublicId = publicId;
   }

   void setSystemId(String systemId) {
      this.fSystemId = systemId;
   }

   void setName(String name) {
      this.fName = name;
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write("<!NOTATION ");
      writer.write(this.getName());
      if (this.fPublicId != null) {
         writer.write(" PUBLIC \"");
         writer.write(this.fPublicId);
         writer.write("\"");
      } else if (this.fSystemId != null) {
         writer.write(" SYSTEM");
         writer.write(" \"");
         writer.write(this.fSystemId);
         writer.write("\"");
      }

      writer.write(62);
   }
}
