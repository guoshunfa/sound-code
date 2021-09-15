package java.awt;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class MenuShortcut implements Serializable {
   int key;
   boolean usesShift;
   private static final long serialVersionUID = 143448358473180225L;

   public MenuShortcut(int var1) {
      this(var1, false);
   }

   public MenuShortcut(int var1, boolean var2) {
      this.key = var1;
      this.usesShift = var2;
   }

   public int getKey() {
      return this.key;
   }

   public boolean usesShiftModifier() {
      return this.usesShift;
   }

   public boolean equals(MenuShortcut var1) {
      return var1 != null && var1.getKey() == this.key && var1.usesShiftModifier() == this.usesShift;
   }

   public boolean equals(Object var1) {
      return var1 instanceof MenuShortcut ? this.equals((MenuShortcut)var1) : false;
   }

   public int hashCode() {
      return this.usesShift ? ~this.key : this.key;
   }

   public String toString() {
      int var1 = 0;
      if (!GraphicsEnvironment.isHeadless()) {
         var1 = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      }

      if (this.usesShiftModifier()) {
         var1 |= 1;
      }

      return KeyEvent.getKeyModifiersText(var1) + "+" + KeyEvent.getKeyText(this.key);
   }

   protected String paramString() {
      String var1 = "key=" + this.key;
      if (this.usesShiftModifier()) {
         var1 = var1 + ",usesShiftModifier";
      }

      return var1;
   }
}
