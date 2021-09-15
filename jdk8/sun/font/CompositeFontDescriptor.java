package sun.font;

public class CompositeFontDescriptor {
   private String faceName;
   private int coreComponentCount;
   private String[] componentFaceNames;
   private String[] componentFileNames;
   private int[] exclusionRanges;
   private int[] exclusionRangeLimits;

   public CompositeFontDescriptor(String var1, int var2, String[] var3, String[] var4, int[] var5, int[] var6) {
      this.faceName = var1;
      this.coreComponentCount = var2;
      this.componentFaceNames = var3;
      this.componentFileNames = var4;
      this.exclusionRanges = var5;
      this.exclusionRangeLimits = var6;
   }

   public String getFaceName() {
      return this.faceName;
   }

   public int getCoreComponentCount() {
      return this.coreComponentCount;
   }

   public String[] getComponentFaceNames() {
      return this.componentFaceNames;
   }

   public String[] getComponentFileNames() {
      return this.componentFileNames;
   }

   public int[] getExclusionRanges() {
      return this.exclusionRanges;
   }

   public int[] getExclusionRangeLimits() {
      return this.exclusionRangeLimits;
   }
}
