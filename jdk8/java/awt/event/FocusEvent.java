package java.awt.event;

import java.awt.Component;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class FocusEvent extends ComponentEvent {
   public static final int FOCUS_FIRST = 1004;
   public static final int FOCUS_LAST = 1005;
   public static final int FOCUS_GAINED = 1004;
   public static final int FOCUS_LOST = 1005;
   boolean temporary;
   transient Component opposite;
   private static final long serialVersionUID = 523753786457416396L;

   public FocusEvent(Component var1, int var2, boolean var3, Component var4) {
      super(var1, var2);
      this.temporary = var3;
      this.opposite = var4;
   }

   public FocusEvent(Component var1, int var2, boolean var3) {
      this(var1, var2, var3, (Component)null);
   }

   public FocusEvent(Component var1, int var2) {
      this(var1, var2, false);
   }

   public boolean isTemporary() {
      return this.temporary;
   }

   public Component getOppositeComponent() {
      if (this.opposite == null) {
         return null;
      } else {
         return SunToolkit.targetToAppContext(this.opposite) == AppContext.getAppContext() ? this.opposite : null;
      }
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 1004:
         var1 = "FOCUS_GAINED";
         break;
      case 1005:
         var1 = "FOCUS_LOST";
         break;
      default:
         var1 = "unknown type";
      }

      return var1 + (this.temporary ? ",temporary" : ",permanent") + ",opposite=" + this.getOppositeComponent();
   }
}
