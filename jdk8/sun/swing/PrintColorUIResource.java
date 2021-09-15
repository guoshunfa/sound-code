package sun.swing;

import java.awt.Color;
import javax.swing.plaf.ColorUIResource;

public class PrintColorUIResource extends ColorUIResource {
   private Color printColor;

   public PrintColorUIResource(int var1, Color var2) {
      super(var1);
      this.printColor = var2;
   }

   public Color getPrintColor() {
      return (Color)(this.printColor != null ? this.printColor : this);
   }

   private Object writeReplace() {
      return new ColorUIResource(this);
   }
}
