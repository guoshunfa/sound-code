package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent extends DummyEvent implements EntityReference {
   private EntityDeclaration fEntityDeclaration;
   private String fEntityName;

   public EntityReferenceEvent() {
      this.init();
   }

   public EntityReferenceEvent(String entityName, EntityDeclaration entityDeclaration) {
      this.init();
      this.fEntityName = entityName;
      this.fEntityDeclaration = entityDeclaration;
   }

   public String getName() {
      return this.fEntityName;
   }

   public String toString() {
      String text = this.fEntityDeclaration.getReplacementText();
      if (text == null) {
         text = "";
      }

      return "&" + this.getName() + ";='" + text + "'";
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write(38);
      writer.write(this.getName());
      writer.write(59);
   }

   public EntityDeclaration getDeclaration() {
      return this.fEntityDeclaration;
   }

   protected void init() {
      this.setEventType(9);
   }
}
