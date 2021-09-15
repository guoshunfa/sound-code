package sun.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Map;
import sun.text.CodePointIterator;

public final class FontResolver {
   private Font[] allFonts;
   private Font[] supplementaryFonts;
   private int[] supplementaryIndices;
   private static final int DEFAULT_SIZE = 12;
   private Font defaultFont = new Font("Dialog", 0, 12);
   private static final int SHIFT = 9;
   private static final int BLOCKSIZE = 128;
   private static final int MASK = 127;
   private int[][] blocks = new int[512][];
   private static FontResolver INSTANCE;

   private FontResolver() {
   }

   private Font[] getAllFonts() {
      if (this.allFonts == null) {
         this.allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

         for(int var1 = 0; var1 < this.allFonts.length; ++var1) {
            this.allFonts[var1] = this.allFonts[var1].deriveFont(12.0F);
         }
      }

      return this.allFonts;
   }

   private int getIndexFor(char var1) {
      if (this.defaultFont.canDisplay(var1)) {
         return 1;
      } else {
         for(int var2 = 0; var2 < this.getAllFonts().length; ++var2) {
            if (this.allFonts[var2].canDisplay(var1)) {
               return var2 + 2;
            }
         }

         return 1;
      }
   }

   private Font[] getAllSCFonts() {
      if (this.supplementaryFonts == null) {
         ArrayList var1 = new ArrayList();
         ArrayList var2 = new ArrayList();

         int var3;
         for(var3 = 0; var3 < this.getAllFonts().length; ++var3) {
            Font var4 = this.allFonts[var3];
            Font2D var5 = FontUtilities.getFont2D(var4);
            if (var5.hasSupplementaryChars()) {
               var1.add(var4);
               var2.add(var3);
            }
         }

         var3 = var1.size();
         this.supplementaryIndices = new int[var3];

         for(int var6 = 0; var6 < var3; ++var6) {
            this.supplementaryIndices[var6] = (Integer)var2.get(var6);
         }

         this.supplementaryFonts = (Font[])var1.toArray(new Font[var3]);
      }

      return this.supplementaryFonts;
   }

   private int getIndexFor(int var1) {
      if (this.defaultFont.canDisplay(var1)) {
         return 1;
      } else {
         for(int var2 = 0; var2 < this.getAllSCFonts().length; ++var2) {
            if (this.supplementaryFonts[var2].canDisplay(var1)) {
               return this.supplementaryIndices[var2] + 2;
            }
         }

         return 1;
      }
   }

   public int getFontIndex(char var1) {
      int var2 = var1 >> 9;
      int[] var3 = this.blocks[var2];
      if (var3 == null) {
         var3 = new int[128];
         this.blocks[var2] = var3;
      }

      int var4 = var1 & 127;
      if (var3[var4] == 0) {
         var3[var4] = this.getIndexFor(var1);
      }

      return var3[var4];
   }

   public int getFontIndex(int var1) {
      return var1 < 65536 ? this.getFontIndex((char)var1) : this.getIndexFor(var1);
   }

   public int nextFontRunIndex(CodePointIterator var1) {
      int var2 = var1.next();
      int var3 = 1;
      if (var2 != -1) {
         var3 = this.getFontIndex(var2);

         while((var2 = var1.next()) != -1) {
            if (this.getFontIndex(var2) != var3) {
               var1.prev();
               break;
            }
         }
      }

      return var3;
   }

   public Font getFont(int var1, Map var2) {
      Font var3 = this.defaultFont;
      if (var1 >= 2) {
         var3 = this.allFonts[var1 - 2];
      }

      return var3.deriveFont(var2);
   }

   public static FontResolver getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new FontResolver();
      }

      return INSTANCE;
   }
}
