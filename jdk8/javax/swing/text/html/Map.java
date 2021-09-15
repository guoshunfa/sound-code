package javax.swing.text.html;

import java.awt.Polygon;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.text.AttributeSet;

class Map implements Serializable {
   private String name;
   private Vector<AttributeSet> areaAttributes;
   private Vector<Map.RegionContainment> areas;

   public Map() {
   }

   public Map(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public void addArea(AttributeSet var1) {
      if (var1 != null) {
         if (this.areaAttributes == null) {
            this.areaAttributes = new Vector(2);
         }

         this.areaAttributes.addElement(var1.copyAttributes());
      }
   }

   public void removeArea(AttributeSet var1) {
      if (var1 != null && this.areaAttributes != null) {
         int var2 = this.areas != null ? this.areas.size() : 0;

         for(int var3 = this.areaAttributes.size() - 1; var3 >= 0; --var3) {
            if (((AttributeSet)this.areaAttributes.elementAt(var3)).isEqual(var1)) {
               this.areaAttributes.removeElementAt(var3);
               if (var3 < var2) {
                  this.areas.removeElementAt(var3);
               }
            }
         }
      }

   }

   public AttributeSet[] getAreas() {
      int var1 = this.areaAttributes != null ? this.areaAttributes.size() : 0;
      if (var1 != 0) {
         AttributeSet[] var2 = new AttributeSet[var1];
         this.areaAttributes.copyInto(var2);
         return var2;
      } else {
         return null;
      }
   }

   public AttributeSet getArea(int var1, int var2, int var3, int var4) {
      int var5 = this.areaAttributes != null ? this.areaAttributes.size() : 0;
      if (var5 > 0) {
         int var6 = this.areas != null ? this.areas.size() : 0;
         if (this.areas == null) {
            this.areas = new Vector(var5);
         }

         for(int var7 = 0; var7 < var5; ++var7) {
            if (var7 >= var6) {
               this.areas.addElement(this.createRegionContainment((AttributeSet)this.areaAttributes.elementAt(var7)));
            }

            Map.RegionContainment var8 = (Map.RegionContainment)this.areas.elementAt(var7);
            if (var8 != null && var8.contains(var1, var2, var3, var4)) {
               return (AttributeSet)this.areaAttributes.elementAt(var7);
            }
         }
      }

      return null;
   }

   protected Map.RegionContainment createRegionContainment(AttributeSet var1) {
      Object var2 = var1.getAttribute(HTML.Attribute.SHAPE);
      if (var2 == null) {
         var2 = "rect";
      }

      if (var2 instanceof String) {
         String var3 = ((String)var2).toLowerCase();
         Object var4 = null;

         try {
            if (var3.equals("rect")) {
               var4 = new Map.RectangleRegionContainment(var1);
            } else if (var3.equals("circle")) {
               var4 = new Map.CircleRegionContainment(var1);
            } else if (var3.equals("poly")) {
               var4 = new Map.PolygonRegionContainment(var1);
            } else if (var3.equals("default")) {
               var4 = Map.DefaultRegionContainment.sharedInstance();
            }
         } catch (RuntimeException var6) {
            var4 = null;
         }

         return (Map.RegionContainment)var4;
      } else {
         return null;
      }
   }

   protected static int[] extractCoords(Object var0) {
      if (var0 != null && var0 instanceof String) {
         StringTokenizer var1 = new StringTokenizer((String)var0, ", \t\n\r");
         int[] var2 = null;
         int var3 = 0;

         while(var1.hasMoreElements()) {
            String var4 = var1.nextToken();
            byte var5;
            if (var4.endsWith("%")) {
               var5 = -1;
               var4 = var4.substring(0, var4.length() - 1);
            } else {
               var5 = 1;
            }

            try {
               int var6 = Integer.parseInt(var4);
               if (var2 == null) {
                  var2 = new int[4];
               } else if (var3 == var2.length) {
                  int[] var7 = new int[var2.length * 2];
                  System.arraycopy(var2, 0, var7, 0, var2.length);
                  var2 = var7;
               }

               var2[var3++] = var6 * var5;
            } catch (NumberFormatException var8) {
               return null;
            }
         }

         if (var3 > 0 && var3 != var2.length) {
            int[] var9 = new int[var3];
            System.arraycopy(var2, 0, var9, 0, var3);
            var2 = var9;
         }

         return var2;
      } else {
         return null;
      }
   }

   static class DefaultRegionContainment implements Map.RegionContainment {
      static Map.DefaultRegionContainment si = null;

      public static Map.DefaultRegionContainment sharedInstance() {
         if (si == null) {
            si = new Map.DefaultRegionContainment();
         }

         return si;
      }

      public boolean contains(int var1, int var2, int var3, int var4) {
         return var1 <= var3 && var1 >= 0 && var2 >= 0 && var2 <= var3;
      }
   }

   static class CircleRegionContainment implements Map.RegionContainment {
      int x;
      int y;
      int radiusSquared;
      float[] percentValues;
      int lastWidth;
      int lastHeight;

      public CircleRegionContainment(AttributeSet var1) {
         int[] var2 = Map.extractCoords(var1.getAttribute(HTML.Attribute.COORDS));
         if (var2 != null && var2.length == 3) {
            this.x = var2[0];
            this.y = var2[1];
            this.radiusSquared = var2[2] * var2[2];
            if (var2[0] >= 0 && var2[1] >= 0 && var2[2] >= 0) {
               this.percentValues = null;
            } else {
               this.lastWidth = this.lastHeight = -1;
               this.percentValues = new float[3];

               for(int var3 = 0; var3 < 3; ++var3) {
                  if (var2[var3] < 0) {
                     this.percentValues[var3] = (float)var2[var3] / -100.0F;
                  } else {
                     this.percentValues[var3] = -1.0F;
                  }
               }
            }

         } else {
            throw new RuntimeException("Unable to parse circular area");
         }
      }

      public boolean contains(int var1, int var2, int var3, int var4) {
         if (this.percentValues != null && (this.lastWidth != var3 || this.lastHeight != var4)) {
            int var5 = Math.min(var3, var4) / 2;
            this.lastWidth = var3;
            this.lastHeight = var4;
            if (this.percentValues[0] != -1.0F) {
               this.x = (int)(this.percentValues[0] * (float)var3);
            }

            if (this.percentValues[1] != -1.0F) {
               this.y = (int)(this.percentValues[1] * (float)var4);
            }

            if (this.percentValues[2] != -1.0F) {
               this.radiusSquared = (int)(this.percentValues[2] * (float)Math.min(var3, var4));
               this.radiusSquared *= this.radiusSquared;
            }
         }

         return (var1 - this.x) * (var1 - this.x) + (var2 - this.y) * (var2 - this.y) <= this.radiusSquared;
      }
   }

   static class PolygonRegionContainment extends Polygon implements Map.RegionContainment {
      float[] percentValues;
      int[] percentIndexs;
      int lastWidth;
      int lastHeight;

      public PolygonRegionContainment(AttributeSet var1) {
         int[] var2 = Map.extractCoords(var1.getAttribute(HTML.Attribute.COORDS));
         if (var2 != null && var2.length != 0 && var2.length % 2 == 0) {
            int var3 = 0;
            this.lastWidth = this.lastHeight = -1;

            int var4;
            for(var4 = var2.length - 1; var4 >= 0; --var4) {
               if (var2[var4] < 0) {
                  ++var3;
               }
            }

            if (var3 > 0) {
               this.percentIndexs = new int[var3];
               this.percentValues = new float[var3];
               var4 = var2.length - 1;

               for(int var5 = 0; var4 >= 0; --var4) {
                  if (var2[var4] < 0) {
                     this.percentValues[var5] = (float)var2[var4] / -100.0F;
                     this.percentIndexs[var5] = var4;
                     ++var5;
                  }
               }
            } else {
               this.percentIndexs = null;
               this.percentValues = null;
            }

            this.npoints = var2.length / 2;
            this.xpoints = new int[this.npoints];
            this.ypoints = new int[this.npoints];

            for(var4 = 0; var4 < this.npoints; ++var4) {
               this.xpoints[var4] = var2[var4 + var4];
               this.ypoints[var4] = var2[var4 + var4 + 1];
            }

         } else {
            throw new RuntimeException("Unable to parse polygon area");
         }
      }

      public boolean contains(int var1, int var2, int var3, int var4) {
         if (this.percentValues != null && (this.lastWidth != var3 || this.lastHeight != var4)) {
            this.bounds = null;
            this.lastWidth = var3;
            this.lastHeight = var4;
            float var5 = (float)var3;
            float var6 = (float)var4;

            for(int var7 = this.percentValues.length - 1; var7 >= 0; --var7) {
               if (this.percentIndexs[var7] % 2 == 0) {
                  this.xpoints[this.percentIndexs[var7] / 2] = (int)(this.percentValues[var7] * var5);
               } else {
                  this.ypoints[this.percentIndexs[var7] / 2] = (int)(this.percentValues[var7] * var6);
               }
            }

            return this.contains(var1, var2);
         } else {
            return this.contains(var1, var2);
         }
      }
   }

   static class RectangleRegionContainment implements Map.RegionContainment {
      float[] percents;
      int lastWidth;
      int lastHeight;
      int x0;
      int y0;
      int x1;
      int y1;

      public RectangleRegionContainment(AttributeSet var1) {
         int[] var2 = Map.extractCoords(var1.getAttribute(HTML.Attribute.COORDS));
         this.percents = null;
         if (var2 != null && var2.length == 4) {
            this.x0 = var2[0];
            this.y0 = var2[1];
            this.x1 = var2[2];
            this.y1 = var2[3];
            if (this.x0 < 0 || this.y0 < 0 || this.x1 < 0 || this.y1 < 0) {
               this.percents = new float[4];
               this.lastWidth = this.lastHeight = -1;

               for(int var3 = 0; var3 < 4; ++var3) {
                  if (var2[var3] < 0) {
                     this.percents[var3] = (float)Math.abs(var2[var3]) / 100.0F;
                  } else {
                     this.percents[var3] = -1.0F;
                  }
               }
            }

         } else {
            throw new RuntimeException("Unable to parse rectangular area");
         }
      }

      public boolean contains(int var1, int var2, int var3, int var4) {
         if (this.percents == null) {
            return this.contains(var1, var2);
         } else {
            if (this.lastWidth != var3 || this.lastHeight != var4) {
               this.lastWidth = var3;
               this.lastHeight = var4;
               if (this.percents[0] != -1.0F) {
                  this.x0 = (int)(this.percents[0] * (float)var3);
               }

               if (this.percents[1] != -1.0F) {
                  this.y0 = (int)(this.percents[1] * (float)var4);
               }

               if (this.percents[2] != -1.0F) {
                  this.x1 = (int)(this.percents[2] * (float)var3);
               }

               if (this.percents[3] != -1.0F) {
                  this.y1 = (int)(this.percents[3] * (float)var4);
               }
            }

            return this.contains(var1, var2);
         }
      }

      public boolean contains(int var1, int var2) {
         return var1 >= this.x0 && var1 <= this.x1 && var2 >= this.y0 && var2 <= this.y1;
      }
   }

   interface RegionContainment {
      boolean contains(int var1, int var2, int var3, int var4);
   }
}
