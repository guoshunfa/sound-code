package java.awt.font;

public final class TextHitInfo {
   private int charIndex;
   private boolean isLeadingEdge;

   private TextHitInfo(int var1, boolean var2) {
      this.charIndex = var1;
      this.isLeadingEdge = var2;
   }

   public int getCharIndex() {
      return this.charIndex;
   }

   public boolean isLeadingEdge() {
      return this.isLeadingEdge;
   }

   public int getInsertionIndex() {
      return this.isLeadingEdge ? this.charIndex : this.charIndex + 1;
   }

   public int hashCode() {
      return this.charIndex;
   }

   public boolean equals(Object var1) {
      return var1 instanceof TextHitInfo && this.equals((TextHitInfo)var1);
   }

   public boolean equals(TextHitInfo var1) {
      return var1 != null && this.charIndex == var1.charIndex && this.isLeadingEdge == var1.isLeadingEdge;
   }

   public String toString() {
      return "TextHitInfo[" + this.charIndex + (this.isLeadingEdge ? "L" : "T") + "]";
   }

   public static TextHitInfo leading(int var0) {
      return new TextHitInfo(var0, true);
   }

   public static TextHitInfo trailing(int var0) {
      return new TextHitInfo(var0, false);
   }

   public static TextHitInfo beforeOffset(int var0) {
      return new TextHitInfo(var0 - 1, false);
   }

   public static TextHitInfo afterOffset(int var0) {
      return new TextHitInfo(var0, true);
   }

   public TextHitInfo getOtherHit() {
      return this.isLeadingEdge ? trailing(this.charIndex - 1) : leading(this.charIndex + 1);
   }

   public TextHitInfo getOffsetHit(int var1) {
      return new TextHitInfo(this.charIndex + var1, this.isLeadingEdge);
   }
}
