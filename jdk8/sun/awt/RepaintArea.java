package sun.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public class RepaintArea {
   private static final int MAX_BENEFIT_RATIO = 4;
   private static final int HORIZONTAL = 0;
   private static final int VERTICAL = 1;
   private static final int UPDATE = 2;
   private static final int RECT_COUNT = 3;
   private Rectangle[] paintRects = new Rectangle[3];

   public RepaintArea() {
   }

   private RepaintArea(RepaintArea var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         this.paintRects[var2] = var1.paintRects[var2];
      }

   }

   public synchronized void add(Rectangle var1, int var2) {
      if (!var1.isEmpty()) {
         int var3 = 2;
         if (var2 == 800) {
            var3 = var1.width > var1.height ? 0 : 1;
         }

         if (this.paintRects[var3] != null) {
            this.paintRects[var3].add(var1);
         } else {
            this.paintRects[var3] = new Rectangle(var1);
         }

      }
   }

   private synchronized RepaintArea cloneAndReset() {
      RepaintArea var1 = new RepaintArea(this);

      for(int var2 = 0; var2 < 3; ++var2) {
         this.paintRects[var2] = null;
      }

      return var1;
   }

   public boolean isEmpty() {
      for(int var1 = 0; var1 < 3; ++var1) {
         if (this.paintRects[var1] != null) {
            return false;
         }
      }

      return true;
   }

   public synchronized void constrain(int var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < 3; ++var5) {
         Rectangle var6 = this.paintRects[var5];
         if (var6 != null) {
            if (var6.x < var1) {
               var6.width -= var1 - var6.x;
               var6.x = var1;
            }

            if (var6.y < var2) {
               var6.height -= var2 - var6.y;
               var6.y = var2;
            }

            int var7 = var6.x + var6.width - var1 - var3;
            if (var7 > 0) {
               var6.width -= var7;
            }

            int var8 = var6.y + var6.height - var2 - var4;
            if (var8 > 0) {
               var6.height -= var8;
            }

            if (var6.width <= 0 || var6.height <= 0) {
               this.paintRects[var5] = null;
            }
         }
      }

   }

   public synchronized void subtract(int var1, int var2, int var3, int var4) {
      Rectangle var5 = new Rectangle(var1, var2, var3, var4);

      for(int var6 = 0; var6 < 3; ++var6) {
         if (subtract(this.paintRects[var6], var5) && this.paintRects[var6] != null && this.paintRects[var6].isEmpty()) {
            this.paintRects[var6] = null;
         }
      }

   }

   public void paint(Object var1, boolean var2) {
      Component var3 = (Component)var1;
      if (!this.isEmpty()) {
         if (var3.isVisible()) {
            RepaintArea var4 = this.cloneAndReset();
            if (!subtract(var4.paintRects[1], var4.paintRects[0])) {
               subtract(var4.paintRects[0], var4.paintRects[1]);
            }

            if (var4.paintRects[0] != null && var4.paintRects[1] != null) {
               Rectangle var5 = var4.paintRects[0].union(var4.paintRects[1]);
               int var6 = var5.width * var5.height;
               int var7 = var6 - var4.paintRects[0].width * var4.paintRects[0].height - var4.paintRects[1].width * var4.paintRects[1].height;
               if (4 * var7 < var6) {
                  var4.paintRects[0] = var5;
                  var4.paintRects[1] = null;
               }
            }

            for(int var11 = 0; var11 < this.paintRects.length; ++var11) {
               if (var4.paintRects[var11] != null && !var4.paintRects[var11].isEmpty()) {
                  Graphics var12 = var3.getGraphics();
                  if (var12 != null) {
                     try {
                        var12.setClip(var4.paintRects[var11]);
                        if (var11 == 2) {
                           this.updateComponent(var3, var12);
                        } else {
                           if (var2) {
                              var12.clearRect(var4.paintRects[var11].x, var4.paintRects[var11].y, var4.paintRects[var11].width, var4.paintRects[var11].height);
                           }

                           this.paintComponent(var3, var12);
                        }
                     } finally {
                        var12.dispose();
                     }
                  }
               }
            }

         }
      }
   }

   protected void updateComponent(Component var1, Graphics var2) {
      if (var1 != null) {
         var1.update(var2);
      }

   }

   protected void paintComponent(Component var1, Graphics var2) {
      if (var1 != null) {
         var1.paint(var2);
      }

   }

   static boolean subtract(Rectangle var0, Rectangle var1) {
      if (var0 != null && var1 != null) {
         Rectangle var2 = var0.intersection(var1);
         if (var2.isEmpty()) {
            return true;
         } else {
            if (var0.x == var2.x && var0.y == var2.y) {
               if (var0.width == var2.width) {
                  var0.y += var2.height;
                  var0.height -= var2.height;
                  return true;
               }

               if (var0.height == var2.height) {
                  var0.x += var2.width;
                  var0.width -= var2.width;
                  return true;
               }
            } else if (var0.x + var0.width == var2.x + var2.width && var0.y + var0.height == var2.y + var2.height) {
               if (var0.width == var2.width) {
                  var0.height -= var2.height;
                  return true;
               }

               if (var0.height == var2.height) {
                  var0.width -= var2.width;
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public String toString() {
      return super.toString() + "[ horizontal=" + this.paintRects[0] + " vertical=" + this.paintRects[1] + " update=" + this.paintRects[2] + "]";
   }
}
