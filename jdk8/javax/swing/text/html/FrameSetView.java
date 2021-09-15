package javax.swing.text.html;

import java.util.StringTokenizer;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

class FrameSetView extends BoxView {
   String[] children = null;
   int[] percentChildren;
   int[] absoluteChildren;
   int[] relativeChildren;
   int percentTotals;
   int absoluteTotals;
   int relativeTotals;

   public FrameSetView(Element var1, int var2) {
      super(var1, var2);
   }

   private String[] parseRowColSpec(HTML.Attribute var1) {
      AttributeSet var2 = this.getElement().getAttributes();
      String var3 = "*";
      if (var2 != null && var2.getAttribute(var1) != null) {
         var3 = (String)var2.getAttribute(var1);
      }

      StringTokenizer var4 = new StringTokenizer(var3, ",");
      int var5 = var4.countTokens();
      int var6 = this.getViewCount();
      String[] var7 = new String[Math.max(var5, var6)];

      int var8;
      for(var8 = 0; var8 < var5; ++var8) {
         var7[var8] = var4.nextToken().trim();
         if (var7[var8].equals("100%")) {
            var7[var8] = "*";
         }
      }

      while(var8 < var7.length) {
         var7[var8] = "*";
         ++var8;
      }

      return var7;
   }

   private void init() {
      if (this.getAxis() == 1) {
         this.children = this.parseRowColSpec(HTML.Attribute.ROWS);
      } else {
         this.children = this.parseRowColSpec(HTML.Attribute.COLS);
      }

      this.percentChildren = new int[this.children.length];
      this.relativeChildren = new int[this.children.length];
      this.absoluteChildren = new int[this.children.length];

      int var1;
      for(var1 = 0; var1 < this.children.length; ++var1) {
         this.percentChildren[var1] = -1;
         this.relativeChildren[var1] = -1;
         this.absoluteChildren[var1] = -1;
         if (this.children[var1].endsWith("*")) {
            if (this.children[var1].length() > 1) {
               this.relativeChildren[var1] = Integer.parseInt(this.children[var1].substring(0, this.children[var1].length() - 1));
               this.relativeTotals += this.relativeChildren[var1];
            } else {
               this.relativeChildren[var1] = 1;
               ++this.relativeTotals;
            }
         } else if (this.children[var1].indexOf(37) != -1) {
            this.percentChildren[var1] = this.parseDigits(this.children[var1]);
            this.percentTotals += this.percentChildren[var1];
         } else {
            this.absoluteChildren[var1] = Integer.parseInt(this.children[var1]);
         }
      }

      if (this.percentTotals > 100) {
         for(var1 = 0; var1 < this.percentChildren.length; ++var1) {
            if (this.percentChildren[var1] > 0) {
               this.percentChildren[var1] = this.percentChildren[var1] * 100 / this.percentTotals;
            }
         }

         this.percentTotals = 100;
      }

   }

   protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
      if (this.children == null) {
         this.init();
      }

      SizeRequirements.calculateTiledPositions(var1, (SizeRequirements)null, this.getChildRequests(var1, var2), var3, var4);
   }

   protected SizeRequirements[] getChildRequests(int var1, int var2) {
      int[] var3 = new int[this.children.length];
      this.spread(var1, var3);
      int var4 = this.getViewCount();
      SizeRequirements[] var5 = new SizeRequirements[var4];
      int var6 = 0;

      for(int var7 = 0; var6 < var4; ++var6) {
         View var8 = this.getView(var6);
         if (!(var8 instanceof FrameView) && !(var8 instanceof FrameSetView)) {
            int var9 = (int)var8.getMinimumSpan(var2);
            int var10 = (int)var8.getPreferredSpan(var2);
            int var11 = (int)var8.getMaximumSpan(var2);
            float var12 = var8.getAlignment(var2);
            var5[var6] = new SizeRequirements(var9, var10, var11, var12);
         } else {
            var5[var6] = new SizeRequirements((int)var8.getMinimumSpan(var2), var3[var7], (int)var8.getMaximumSpan(var2), 0.5F);
            ++var7;
         }
      }

      return var5;
   }

   private void spread(int var1, int[] var2) {
      if (var1 != 0) {
         boolean var3 = false;
         int var4 = var1;

         int var5;
         for(var5 = 0; var5 < var2.length; ++var5) {
            if (this.absoluteChildren[var5] > 0) {
               var2[var5] = this.absoluteChildren[var5];
               var4 -= var2[var5];
            }
         }

         int var8 = var4;

         for(var5 = 0; var5 < var2.length; ++var5) {
            if (this.percentChildren[var5] > 0 && var8 > 0) {
               var2[var5] = this.percentChildren[var5] * var8 / 100;
               var4 -= var2[var5];
            } else if (this.percentChildren[var5] > 0 && var8 <= 0) {
               var2[var5] = var1 / var2.length;
               var4 -= var2[var5];
            }
         }

         if (var4 > 0 && this.relativeTotals > 0) {
            for(var5 = 0; var5 < var2.length; ++var5) {
               if (this.relativeChildren[var5] > 0) {
                  var2[var5] = var4 * this.relativeChildren[var5] / this.relativeTotals;
               }
            }
         } else if (var4 > 0) {
            float var9 = (float)(var1 - var4);
            float[] var6 = new float[var2.length];
            var4 = var1;

            int var7;
            for(var7 = 0; var7 < var2.length; ++var7) {
               var6[var7] = (float)var2[var7] / var9 * 100.0F;
               var2[var7] = (int)((float)var1 * var6[var7] / 100.0F);
               var4 -= var2[var7];
            }

            var7 = 0;

            while(var4 != 0) {
               int var10002;
               if (var4 < 0) {
                  var10002 = var2[var7++]--;
                  ++var4;
               } else {
                  var10002 = var2[var7++]++;
                  --var4;
               }

               if (var7 == var2.length) {
                  var7 = 0;
               }
            }
         }

      }
   }

   private int parseDigits(String var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         char var4 = var1.charAt(var3);
         if (Character.isDigit(var4)) {
            var2 = var2 * 10 + Character.digit((char)var4, 10);
         }
      }

      return var2;
   }
}
