package sun.java2d.jules;

public class TrapezoidList {
   public static final int TRAP_START_INDEX = 5;
   public static final int TRAP_SIZE = 10;
   int[] trapArray;

   public TrapezoidList(int[] var1) {
      this.trapArray = var1;
   }

   public final int[] getTrapArray() {
      return this.trapArray;
   }

   public final int getSize() {
      return this.trapArray[0];
   }

   public final void setSize(int var1) {
      this.trapArray[0] = 0;
   }

   public final int getLeft() {
      return this.trapArray[1];
   }

   public final int getTop() {
      return this.trapArray[2];
   }

   public final int getRight() {
      return this.trapArray[3];
   }

   public final int getBottom() {
      return this.trapArray[4];
   }

   private final int getTrapStartAddresse(int var1) {
      return 5 + 10 * var1;
   }

   public final int getTop(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 0];
   }

   public final int getBottom(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 1];
   }

   public final int getP1XLeft(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 2];
   }

   public final int getP1YLeft(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 3];
   }

   public final int getP2XLeft(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 4];
   }

   public final int getP2YLeft(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 5];
   }

   public final int getP1XRight(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 6];
   }

   public final int getP1YRight(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 7];
   }

   public final int getP2XRight(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 8];
   }

   public final int getP2YRight(int var1) {
      return this.trapArray[this.getTrapStartAddresse(var1) + 9];
   }
}
