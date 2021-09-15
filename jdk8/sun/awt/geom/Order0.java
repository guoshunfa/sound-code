package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order0 extends Curve {
   private double x;
   private double y;

   public Order0(double var1, double var3) {
      super(1);
      this.x = var1;
      this.y = var3;
   }

   public int getOrder() {
      return 0;
   }

   public double getXTop() {
      return this.x;
   }

   public double getYTop() {
      return this.y;
   }

   public double getXBot() {
      return this.x;
   }

   public double getYBot() {
      return this.y;
   }

   public double getXMin() {
      return this.x;
   }

   public double getXMax() {
      return this.x;
   }

   public double getX0() {
      return this.x;
   }

   public double getY0() {
      return this.y;
   }

   public double getX1() {
      return this.x;
   }

   public double getY1() {
      return this.y;
   }

   public double XforY(double var1) {
      return var1;
   }

   public double TforY(double var1) {
      return 0.0D;
   }

   public double XforT(double var1) {
      return this.x;
   }

   public double YforT(double var1) {
      return this.y;
   }

   public double dXforT(double var1, int var3) {
      return 0.0D;
   }

   public double dYforT(double var1, int var3) {
      return 0.0D;
   }

   public double nextVertical(double var1, double var3) {
      return var3;
   }

   public int crossingsFor(double var1, double var3) {
      return 0;
   }

   public boolean accumulateCrossings(Crossings var1) {
      return this.x > var1.getXLo() && this.x < var1.getXHi() && this.y > var1.getYLo() && this.y < var1.getYHi();
   }

   public void enlarge(Rectangle2D var1) {
      var1.add(this.x, this.y);
   }

   public Curve getSubCurve(double var1, double var3, int var5) {
      return this;
   }

   public Curve getReversedCurve() {
      return this;
   }

   public int getSegment(double[] var1) {
      var1[0] = this.x;
      var1[1] = this.y;
      return 0;
   }
}
