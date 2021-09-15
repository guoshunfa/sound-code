package java.awt;

public class PointerInfo {
   private final GraphicsDevice device;
   private final Point location;

   PointerInfo(GraphicsDevice var1, Point var2) {
      this.device = var1;
      this.location = var2;
   }

   public GraphicsDevice getDevice() {
      return this.device;
   }

   public Point getLocation() {
      return this.location;
   }
}
