package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.Comment;

public class CommentEvent extends DummyEvent implements Comment {
   private String fText;

   public CommentEvent() {
      this.init();
   }

   public CommentEvent(String text) {
      this.init();
      this.fText = text;
   }

   protected void init() {
      this.setEventType(5);
   }

   public String toString() {
      return "<!--" + this.getText() + "-->";
   }

   public String getText() {
      return this.fText;
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write("<!--" + this.getText() + "-->");
   }
}
