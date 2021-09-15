package java.util;

import java.io.Serializable;

public class EventObject implements Serializable {
   private static final long serialVersionUID = 5516075349620653480L;
   protected transient Object source;

   public EventObject(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null source");
      } else {
         this.source = var1;
      }
   }

   public Object getSource() {
      return this.source;
   }

   public String toString() {
      return this.getClass().getName() + "[source=" + this.source + "]";
   }
}
