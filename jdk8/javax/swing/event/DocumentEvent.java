package javax.swing.event;

import javax.swing.text.Document;
import javax.swing.text.Element;

public interface DocumentEvent {
   int getOffset();

   int getLength();

   Document getDocument();

   DocumentEvent.EventType getType();

   DocumentEvent.ElementChange getChange(Element var1);

   public interface ElementChange {
      Element getElement();

      int getIndex();

      Element[] getChildrenRemoved();

      Element[] getChildrenAdded();
   }

   public static final class EventType {
      public static final DocumentEvent.EventType INSERT = new DocumentEvent.EventType("INSERT");
      public static final DocumentEvent.EventType REMOVE = new DocumentEvent.EventType("REMOVE");
      public static final DocumentEvent.EventType CHANGE = new DocumentEvent.EventType("CHANGE");
      private String typeString;

      private EventType(String var1) {
         this.typeString = var1;
      }

      public String toString() {
         return this.typeString;
      }
   }
}
