package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import sun.security.action.GetPropertyAction;

public final class CompositeFont extends Font2D {
   private boolean[] deferredInitialisation;
   String[] componentFileNames;
   String[] componentNames;
   private PhysicalFont[] components;
   int numSlots;
   int numMetricsSlots;
   int[] exclusionRanges;
   int[] maxIndices;
   int numGlyphs = 0;
   int localeSlot = -1;
   boolean isStdComposite = true;

   public CompositeFont(String var1, String[] var2, String[] var3, int var4, int[] var5, int[] var6, boolean var7, SunFontManager var8) {
      this.handle = new Font2DHandle(this);
      this.fullName = var1;
      this.componentFileNames = var2;
      this.componentNames = var3;
      if (var3 == null) {
         this.numSlots = this.componentFileNames.length;
      } else {
         this.numSlots = this.componentNames.length;
      }

      this.numSlots = this.numSlots <= 254 ? this.numSlots : 254;
      this.numMetricsSlots = var4;
      this.exclusionRanges = var5;
      this.maxIndices = var6;
      int var9;
      if (var8.getEUDCFont() != null) {
         var9 = this.numMetricsSlots;
         int var10 = this.numSlots - var9;
         ++this.numSlots;
         if (this.componentNames != null) {
            this.componentNames = new String[this.numSlots];
            System.arraycopy(var3, 0, this.componentNames, 0, var9);
            this.componentNames[var9] = var8.getEUDCFont().getFontName((Locale)null);
            System.arraycopy(var3, var9, this.componentNames, var9 + 1, var10);
         }

         if (this.componentFileNames != null) {
            this.componentFileNames = new String[this.numSlots];
            System.arraycopy(var2, 0, this.componentFileNames, 0, var9);
            System.arraycopy(var2, var9, this.componentFileNames, var9 + 1, var10);
         }

         this.components = new PhysicalFont[this.numSlots];
         this.components[var9] = var8.getEUDCFont();
         this.deferredInitialisation = new boolean[this.numSlots];
         if (var7) {
            for(int var11 = 0; var11 < this.numSlots - 1; ++var11) {
               this.deferredInitialisation[var11] = true;
            }
         }
      } else {
         this.components = new PhysicalFont[this.numSlots];
         this.deferredInitialisation = new boolean[this.numSlots];
         if (var7) {
            for(var9 = 0; var9 < this.numSlots; ++var9) {
               this.deferredInitialisation[var9] = true;
            }
         }
      }

      this.fontRank = 2;
      var9 = this.fullName.indexOf(46);
      if (var9 > 0) {
         this.familyName = this.fullName.substring(0, var9);
         if (var9 + 1 < this.fullName.length()) {
            String var12 = this.fullName.substring(var9 + 1);
            if ("plain".equals(var12)) {
               this.style = 0;
            } else if ("bold".equals(var12)) {
               this.style = 1;
            } else if ("italic".equals(var12)) {
               this.style = 2;
            } else if ("bolditalic".equals(var12)) {
               this.style = 3;
            }
         }
      } else {
         this.familyName = this.fullName;
      }

   }

   CompositeFont(PhysicalFont[] var1) {
      this.isStdComposite = false;
      this.handle = new Font2DHandle(this);
      this.fullName = var1[0].fullName;
      this.familyName = var1[0].familyName;
      this.style = var1[0].style;
      this.numMetricsSlots = 1;
      this.numSlots = var1.length;
      this.components = new PhysicalFont[this.numSlots];
      System.arraycopy(var1, 0, this.components, 0, this.numSlots);
      this.deferredInitialisation = new boolean[this.numSlots];
   }

   CompositeFont(PhysicalFont var1, CompositeFont var2) {
      this.isStdComposite = false;
      this.handle = new Font2DHandle(this);
      this.fullName = var1.fullName;
      this.familyName = var1.familyName;
      this.style = var1.style;
      this.numMetricsSlots = 1;
      this.numSlots = var2.numSlots + 1;
      synchronized(FontManagerFactory.getInstance()) {
         this.components = new PhysicalFont[this.numSlots];
         this.components[0] = var1;
         System.arraycopy(var2.components, 0, this.components, 1, var2.numSlots);
         if (var2.componentNames != null) {
            this.componentNames = new String[this.numSlots];
            this.componentNames[0] = var1.fullName;
            System.arraycopy(var2.componentNames, 0, this.componentNames, 1, var2.numSlots);
         }

         if (var2.componentFileNames != null) {
            this.componentFileNames = new String[this.numSlots];
            this.componentFileNames[0] = null;
            System.arraycopy(var2.componentFileNames, 0, this.componentFileNames, 1, var2.numSlots);
         }

         this.deferredInitialisation = new boolean[this.numSlots];
         this.deferredInitialisation[0] = false;
         System.arraycopy(var2.deferredInitialisation, 0, this.deferredInitialisation, 1, var2.numSlots);
      }
   }

   private void doDeferredInitialisation(int var1) {
      if (this.deferredInitialisation[var1]) {
         SunFontManager var2 = SunFontManager.getInstance();
         synchronized(var2) {
            if (this.componentNames == null) {
               this.componentNames = new String[this.numSlots];
            }

            if (this.components[var1] == null) {
               if (this.componentFileNames != null && this.componentFileNames[var1] != null) {
                  this.components[var1] = var2.initialiseDeferredFont(this.componentFileNames[var1]);
               }

               if (this.components[var1] == null) {
                  this.components[var1] = var2.getDefaultPhysicalFont();
               }

               String var4 = this.components[var1].getFontName((Locale)null);
               if (this.componentNames[var1] == null) {
                  this.componentNames[var1] = var4;
               } else if (!this.componentNames[var1].equalsIgnoreCase(var4)) {
                  try {
                     this.components[var1] = (PhysicalFont)var2.findFont2D(this.componentNames[var1], this.style, 1);
                  } catch (ClassCastException var7) {
                     this.components[var1] = var2.getDefaultPhysicalFont();
                  }
               }
            }

            this.deferredInitialisation[var1] = false;
         }
      }
   }

   void replaceComponentFont(PhysicalFont var1, PhysicalFont var2) {
      if (this.components != null) {
         for(int var3 = 0; var3 < this.numSlots; ++var3) {
            if (this.components[var3] == var1) {
               this.components[var3] = var2;
               if (this.componentNames != null) {
                  this.componentNames[var3] = var2.getFontName((Locale)null);
               }
            }
         }

      }
   }

   public boolean isExcludedChar(int var1, int var2) {
      if (this.exclusionRanges != null && this.maxIndices != null && var1 < this.numMetricsSlots) {
         int var3 = 0;
         int var4 = this.maxIndices[var1];
         if (var1 > 0) {
            var3 = this.maxIndices[var1 - 1];
         }

         for(int var5 = var3; var4 > var5; var5 += 2) {
            if (var2 >= this.exclusionRanges[var5] && var2 <= this.exclusionRanges[var5 + 1]) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void getStyleMetrics(float var1, float[] var2, int var3) {
      PhysicalFont var4 = this.getSlotFont(0);
      if (var4 == null) {
         super.getStyleMetrics(var1, var2, var3);
      } else {
         var4.getStyleMetrics(var1, var2, var3);
      }

   }

   public int getNumSlots() {
      return this.numSlots;
   }

   public PhysicalFont getSlotFont(int var1) {
      if (this.deferredInitialisation[var1]) {
         this.doDeferredInitialisation(var1);
      }

      SunFontManager var2 = SunFontManager.getInstance();

      try {
         PhysicalFont var3 = this.components[var1];
         if (var3 == null) {
            try {
               var3 = (PhysicalFont)var2.findFont2D(this.componentNames[var1], this.style, 1);
               this.components[var1] = var3;
            } catch (ClassCastException var5) {
               var3 = var2.getDefaultPhysicalFont();
            }
         }

         return var3;
      } catch (Exception var6) {
         return var2.getDefaultPhysicalFont();
      }
   }

   FontStrike createStrike(FontStrikeDesc var1) {
      return new CompositeStrike(this, var1);
   }

   public boolean isStdComposite() {
      return this.isStdComposite;
   }

   protected int getValidatedGlyphCode(int var1) {
      int var2 = var1 >>> 24;
      if (var2 >= this.numSlots) {
         return this.getMapper().getMissingGlyphCode();
      } else {
         int var3 = var1 & 16777215;
         PhysicalFont var4 = this.getSlotFont(var2);
         return var4.getValidatedGlyphCode(var3) == var4.getMissingGlyphCode() ? this.getMapper().getMissingGlyphCode() : var1;
      }
   }

   public CharToGlyphMapper getMapper() {
      if (this.mapper == null) {
         this.mapper = new CompositeGlyphMapper(this);
      }

      return this.mapper;
   }

   public boolean hasSupplementaryChars() {
      for(int var1 = 0; var1 < this.numSlots; ++var1) {
         if (this.getSlotFont(var1).hasSupplementaryChars()) {
            return true;
         }
      }

      return false;
   }

   public int getNumGlyphs() {
      if (this.numGlyphs == 0) {
         this.numGlyphs = this.getMapper().getNumGlyphs();
      }

      return this.numGlyphs;
   }

   public int getMissingGlyphCode() {
      return this.getMapper().getMissingGlyphCode();
   }

   public boolean canDisplay(char var1) {
      return this.getMapper().canDisplay(var1);
   }

   public boolean useAAForPtSize(int var1) {
      if (this.localeSlot == -1) {
         int var2 = this.numMetricsSlots;
         if (var2 == 1 && !this.isStdComposite()) {
            var2 = this.numSlots;
         }

         for(int var3 = 0; var3 < var2; ++var3) {
            if (this.getSlotFont(var3).supportsEncoding((String)null)) {
               this.localeSlot = var3;
               break;
            }
         }

         if (this.localeSlot == -1) {
            this.localeSlot = 0;
         }
      }

      return this.getSlotFont(this.localeSlot).useAAForPtSize(var1);
   }

   public String toString() {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));
      String var2 = "";

      for(int var3 = 0; var3 < this.numSlots; ++var3) {
         var2 = var2 + "    Slot[" + var3 + "]=" + this.getSlotFont(var3) + var1;
      }

      return "** Composite Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + var1 + var2;
   }
}
