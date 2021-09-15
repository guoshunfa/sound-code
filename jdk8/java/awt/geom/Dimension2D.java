package java.awt.geom;

public abstract class Dimension2D implements Cloneable {
   protected Dimension2D() {
   }

   public abstract double getWidth();

   public abstract double getHeight();

   public abstract void setSize(double var1, double var3);

   public void setSize(Dimension2D var1) {
      this.setSize(var1.getWidth(), var1.getHeight());
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
