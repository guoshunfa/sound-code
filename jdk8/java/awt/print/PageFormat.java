package java.awt.print;

public class PageFormat implements Cloneable {
   public static final int LANDSCAPE = 0;
   public static final int PORTRAIT = 1;
   public static final int REVERSE_LANDSCAPE = 2;
   private Paper mPaper = new Paper();
   private int mOrientation = 1;

   public Object clone() {
      PageFormat var1;
      try {
         var1 = (PageFormat)super.clone();
         var1.mPaper = (Paper)this.mPaper.clone();
      } catch (CloneNotSupportedException var3) {
         var3.printStackTrace();
         var1 = null;
      }

      return var1;
   }

   public double getWidth() {
      int var3 = this.getOrientation();
      double var1;
      if (var3 == 1) {
         var1 = this.mPaper.getWidth();
      } else {
         var1 = this.mPaper.getHeight();
      }

      return var1;
   }

   public double getHeight() {
      int var3 = this.getOrientation();
      double var1;
      if (var3 == 1) {
         var1 = this.mPaper.getHeight();
      } else {
         var1 = this.mPaper.getWidth();
      }

      return var1;
   }

   public double getImageableX() {
      double var1;
      switch(this.getOrientation()) {
      case 0:
         var1 = this.mPaper.getHeight() - (this.mPaper.getImageableY() + this.mPaper.getImageableHeight());
         break;
      case 1:
         var1 = this.mPaper.getImageableX();
         break;
      case 2:
         var1 = this.mPaper.getImageableY();
         break;
      default:
         throw new InternalError("unrecognized orientation");
      }

      return var1;
   }

   public double getImageableY() {
      double var1;
      switch(this.getOrientation()) {
      case 0:
         var1 = this.mPaper.getImageableX();
         break;
      case 1:
         var1 = this.mPaper.getImageableY();
         break;
      case 2:
         var1 = this.mPaper.getWidth() - (this.mPaper.getImageableX() + this.mPaper.getImageableWidth());
         break;
      default:
         throw new InternalError("unrecognized orientation");
      }

      return var1;
   }

   public double getImageableWidth() {
      double var1;
      if (this.getOrientation() == 1) {
         var1 = this.mPaper.getImageableWidth();
      } else {
         var1 = this.mPaper.getImageableHeight();
      }

      return var1;
   }

   public double getImageableHeight() {
      double var1;
      if (this.getOrientation() == 1) {
         var1 = this.mPaper.getImageableHeight();
      } else {
         var1 = this.mPaper.getImageableWidth();
      }

      return var1;
   }

   public Paper getPaper() {
      return (Paper)this.mPaper.clone();
   }

   public void setPaper(Paper var1) {
      this.mPaper = (Paper)var1.clone();
   }

   public void setOrientation(int var1) throws IllegalArgumentException {
      if (0 <= var1 && var1 <= 2) {
         this.mOrientation = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getOrientation() {
      return this.mOrientation;
   }

   public double[] getMatrix() {
      double[] var1 = new double[6];
      switch(this.mOrientation) {
      case 0:
         var1[0] = 0.0D;
         var1[1] = -1.0D;
         var1[2] = 1.0D;
         var1[3] = 0.0D;
         var1[4] = 0.0D;
         var1[5] = this.mPaper.getHeight();
         break;
      case 1:
         var1[0] = 1.0D;
         var1[1] = 0.0D;
         var1[2] = 0.0D;
         var1[3] = 1.0D;
         var1[4] = 0.0D;
         var1[5] = 0.0D;
         break;
      case 2:
         var1[0] = 0.0D;
         var1[1] = 1.0D;
         var1[2] = -1.0D;
         var1[3] = 0.0D;
         var1[4] = this.mPaper.getWidth();
         var1[5] = 0.0D;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var1;
   }
}
