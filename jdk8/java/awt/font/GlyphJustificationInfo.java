package java.awt.font;

public final class GlyphJustificationInfo {
   public static final int PRIORITY_KASHIDA = 0;
   public static final int PRIORITY_WHITESPACE = 1;
   public static final int PRIORITY_INTERCHAR = 2;
   public static final int PRIORITY_NONE = 3;
   public final float weight;
   public final int growPriority;
   public final boolean growAbsorb;
   public final float growLeftLimit;
   public final float growRightLimit;
   public final int shrinkPriority;
   public final boolean shrinkAbsorb;
   public final float shrinkLeftLimit;
   public final float shrinkRightLimit;

   public GlyphJustificationInfo(float var1, boolean var2, int var3, float var4, float var5, boolean var6, int var7, float var8, float var9) {
      if (var1 < 0.0F) {
         throw new IllegalArgumentException("weight is negative");
      } else if (!priorityIsValid(var3)) {
         throw new IllegalArgumentException("Invalid grow priority");
      } else if (var4 < 0.0F) {
         throw new IllegalArgumentException("growLeftLimit is negative");
      } else if (var5 < 0.0F) {
         throw new IllegalArgumentException("growRightLimit is negative");
      } else if (!priorityIsValid(var7)) {
         throw new IllegalArgumentException("Invalid shrink priority");
      } else if (var8 < 0.0F) {
         throw new IllegalArgumentException("shrinkLeftLimit is negative");
      } else if (var9 < 0.0F) {
         throw new IllegalArgumentException("shrinkRightLimit is negative");
      } else {
         this.weight = var1;
         this.growAbsorb = var2;
         this.growPriority = var3;
         this.growLeftLimit = var4;
         this.growRightLimit = var5;
         this.shrinkAbsorb = var6;
         this.shrinkPriority = var7;
         this.shrinkLeftLimit = var8;
         this.shrinkRightLimit = var9;
      }
   }

   private static boolean priorityIsValid(int var0) {
      return var0 >= 0 && var0 <= 3;
   }
}
