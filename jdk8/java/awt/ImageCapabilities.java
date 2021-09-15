package java.awt;

public class ImageCapabilities implements Cloneable {
   private boolean accelerated = false;

   public ImageCapabilities(boolean var1) {
      this.accelerated = var1;
   }

   public boolean isAccelerated() {
      return this.accelerated;
   }

   public boolean isTrueVolatile() {
      return false;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
