package javax.swing.plaf;

import java.awt.Color;
import java.beans.ConstructorProperties;

public class ColorUIResource extends Color implements UIResource {
   @ConstructorProperties({"red", "green", "blue"})
   public ColorUIResource(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public ColorUIResource(int var1) {
      super(var1);
   }

   public ColorUIResource(float var1, float var2, float var3) {
      super(var1, var2, var3);
   }

   public ColorUIResource(Color var1) {
      super(var1.getRGB(), (var1.getRGB() & -16777216) != -16777216);
   }
}
