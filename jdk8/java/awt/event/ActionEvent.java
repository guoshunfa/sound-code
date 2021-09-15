package java.awt.event;

import java.awt.AWTEvent;

public class ActionEvent extends AWTEvent {
   public static final int SHIFT_MASK = 1;
   public static final int CTRL_MASK = 2;
   public static final int META_MASK = 4;
   public static final int ALT_MASK = 8;
   public static final int ACTION_FIRST = 1001;
   public static final int ACTION_LAST = 1001;
   public static final int ACTION_PERFORMED = 1001;
   String actionCommand;
   long when;
   int modifiers;
   private static final long serialVersionUID = -7671078796273832149L;

   public ActionEvent(Object var1, int var2, String var3) {
      this(var1, var2, var3, 0);
   }

   public ActionEvent(Object var1, int var2, String var3, int var4) {
      this(var1, var2, var3, 0L, var4);
   }

   public ActionEvent(Object var1, int var2, String var3, long var4, int var6) {
      super(var1, var2);
      this.actionCommand = var3;
      this.when = var4;
      this.modifiers = var6;
   }

   public String getActionCommand() {
      return this.actionCommand;
   }

   public long getWhen() {
      return this.when;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 1001:
         var1 = "ACTION_PERFORMED";
         break;
      default:
         var1 = "unknown type";
      }

      return var1 + ",cmd=" + this.actionCommand + ",when=" + this.when + ",modifiers=" + KeyEvent.getKeyModifiersText(this.modifiers);
   }
}
