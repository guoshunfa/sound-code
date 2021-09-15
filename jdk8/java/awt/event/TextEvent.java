package java.awt.event;

import java.awt.AWTEvent;

public class TextEvent extends AWTEvent {
   public static final int TEXT_FIRST = 900;
   public static final int TEXT_LAST = 900;
   public static final int TEXT_VALUE_CHANGED = 900;
   private static final long serialVersionUID = 6269902291250941179L;

   public TextEvent(Object var1, int var2) {
      super(var1, var2);
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 900:
         var1 = "TEXT_VALUE_CHANGED";
         break;
      default:
         var1 = "unknown type";
      }

      return var1;
   }
}
