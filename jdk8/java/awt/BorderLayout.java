package java.awt;

import java.io.Serializable;

public class BorderLayout implements LayoutManager2, Serializable {
   int hgap;
   int vgap;
   Component north;
   Component west;
   Component east;
   Component south;
   Component center;
   Component firstLine;
   Component lastLine;
   Component firstItem;
   Component lastItem;
   public static final String NORTH = "North";
   public static final String SOUTH = "South";
   public static final String EAST = "East";
   public static final String WEST = "West";
   public static final String CENTER = "Center";
   public static final String BEFORE_FIRST_LINE = "First";
   public static final String AFTER_LAST_LINE = "Last";
   public static final String BEFORE_LINE_BEGINS = "Before";
   public static final String AFTER_LINE_ENDS = "After";
   public static final String PAGE_START = "First";
   public static final String PAGE_END = "Last";
   public static final String LINE_START = "Before";
   public static final String LINE_END = "After";
   private static final long serialVersionUID = -8658291919501921765L;

   public BorderLayout() {
      this(0, 0);
   }

   public BorderLayout(int var1, int var2) {
      this.hgap = var1;
      this.vgap = var2;
   }

   public int getHgap() {
      return this.hgap;
   }

   public void setHgap(int var1) {
      this.hgap = var1;
   }

   public int getVgap() {
      return this.vgap;
   }

   public void setVgap(int var1) {
      this.vgap = var1;
   }

   public void addLayoutComponent(Component var1, Object var2) {
      synchronized(var1.getTreeLock()) {
         if (var2 != null && !(var2 instanceof String)) {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
         } else {
            this.addLayoutComponent((String)var2, var1);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void addLayoutComponent(String var1, Component var2) {
      synchronized(var2.getTreeLock()) {
         if (var1 == null) {
            var1 = "Center";
         }

         if ("Center".equals(var1)) {
            this.center = var2;
         } else if ("North".equals(var1)) {
            this.north = var2;
         } else if ("South".equals(var1)) {
            this.south = var2;
         } else if ("East".equals(var1)) {
            this.east = var2;
         } else if ("West".equals(var1)) {
            this.west = var2;
         } else if ("First".equals(var1)) {
            this.firstLine = var2;
         } else if ("Last".equals(var1)) {
            this.lastLine = var2;
         } else if ("Before".equals(var1)) {
            this.firstItem = var2;
         } else {
            if (!"After".equals(var1)) {
               throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + var1);
            }

            this.lastItem = var2;
         }

      }
   }

   public void removeLayoutComponent(Component var1) {
      synchronized(var1.getTreeLock()) {
         if (var1 == this.center) {
            this.center = null;
         } else if (var1 == this.north) {
            this.north = null;
         } else if (var1 == this.south) {
            this.south = null;
         } else if (var1 == this.east) {
            this.east = null;
         } else if (var1 == this.west) {
            this.west = null;
         }

         if (var1 == this.firstLine) {
            this.firstLine = null;
         } else if (var1 == this.lastLine) {
            this.lastLine = null;
         } else if (var1 == this.firstItem) {
            this.firstItem = null;
         } else if (var1 == this.lastItem) {
            this.lastItem = null;
         }

      }
   }

   public Component getLayoutComponent(Object var1) {
      if ("Center".equals(var1)) {
         return this.center;
      } else if ("North".equals(var1)) {
         return this.north;
      } else if ("South".equals(var1)) {
         return this.south;
      } else if ("West".equals(var1)) {
         return this.west;
      } else if ("East".equals(var1)) {
         return this.east;
      } else if ("First".equals(var1)) {
         return this.firstLine;
      } else if ("Last".equals(var1)) {
         return this.lastLine;
      } else if ("Before".equals(var1)) {
         return this.firstItem;
      } else if ("After".equals(var1)) {
         return this.lastItem;
      } else {
         throw new IllegalArgumentException("cannot get component: unknown constraint: " + var1);
      }
   }

   public Component getLayoutComponent(Container var1, Object var2) {
      boolean var3 = var1.getComponentOrientation().isLeftToRight();
      Component var4 = null;
      if ("North".equals(var2)) {
         var4 = this.firstLine != null ? this.firstLine : this.north;
      } else if ("South".equals(var2)) {
         var4 = this.lastLine != null ? this.lastLine : this.south;
      } else if ("West".equals(var2)) {
         var4 = var3 ? this.firstItem : this.lastItem;
         if (var4 == null) {
            var4 = this.west;
         }
      } else if ("East".equals(var2)) {
         var4 = var3 ? this.lastItem : this.firstItem;
         if (var4 == null) {
            var4 = this.east;
         }
      } else {
         if (!"Center".equals(var2)) {
            throw new IllegalArgumentException("cannot get component: invalid constraint: " + var2);
         }

         var4 = this.center;
      }

      return var4;
   }

   public Object getConstraints(Component var1) {
      if (var1 == null) {
         return null;
      } else if (var1 == this.center) {
         return "Center";
      } else if (var1 == this.north) {
         return "North";
      } else if (var1 == this.south) {
         return "South";
      } else if (var1 == this.west) {
         return "West";
      } else if (var1 == this.east) {
         return "East";
      } else if (var1 == this.firstLine) {
         return "First";
      } else if (var1 == this.lastLine) {
         return "Last";
      } else if (var1 == this.firstItem) {
         return "Before";
      } else {
         return var1 == this.lastItem ? "After" : null;
      }
   }

   public Dimension minimumLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Dimension var3 = new Dimension(0, 0);
         boolean var4 = var1.getComponentOrientation().isLeftToRight();
         Component var5 = null;
         Dimension var6;
         if ((var5 = this.getChild("East", var4)) != null) {
            var6 = var5.getMinimumSize();
            var3.width += var6.width + this.hgap;
            var3.height = Math.max(var6.height, var3.height);
         }

         if ((var5 = this.getChild("West", var4)) != null) {
            var6 = var5.getMinimumSize();
            var3.width += var6.width + this.hgap;
            var3.height = Math.max(var6.height, var3.height);
         }

         if ((var5 = this.getChild("Center", var4)) != null) {
            var6 = var5.getMinimumSize();
            var3.width += var6.width;
            var3.height = Math.max(var6.height, var3.height);
         }

         if ((var5 = this.getChild("North", var4)) != null) {
            var6 = var5.getMinimumSize();
            var3.width = Math.max(var6.width, var3.width);
            var3.height += var6.height + this.vgap;
         }

         if ((var5 = this.getChild("South", var4)) != null) {
            var6 = var5.getMinimumSize();
            var3.width = Math.max(var6.width, var3.width);
            var3.height += var6.height + this.vgap;
         }

         Insets var9 = var1.getInsets();
         var3.width += var9.left + var9.right;
         var3.height += var9.top + var9.bottom;
         return var3;
      }
   }

   public Dimension preferredLayoutSize(Container var1) {
      synchronized(var1.getTreeLock()) {
         Dimension var3 = new Dimension(0, 0);
         boolean var4 = var1.getComponentOrientation().isLeftToRight();
         Component var5 = null;
         Dimension var6;
         if ((var5 = this.getChild("East", var4)) != null) {
            var6 = var5.getPreferredSize();
            var3.width += var6.width + this.hgap;
            var3.height = Math.max(var6.height, var3.height);
         }

         if ((var5 = this.getChild("West", var4)) != null) {
            var6 = var5.getPreferredSize();
            var3.width += var6.width + this.hgap;
            var3.height = Math.max(var6.height, var3.height);
         }

         if ((var5 = this.getChild("Center", var4)) != null) {
            var6 = var5.getPreferredSize();
            var3.width += var6.width;
            var3.height = Math.max(var6.height, var3.height);
         }

         if ((var5 = this.getChild("North", var4)) != null) {
            var6 = var5.getPreferredSize();
            var3.width = Math.max(var6.width, var3.width);
            var3.height += var6.height + this.vgap;
         }

         if ((var5 = this.getChild("South", var4)) != null) {
            var6 = var5.getPreferredSize();
            var3.width = Math.max(var6.width, var3.width);
            var3.height += var6.height + this.vgap;
         }

         Insets var9 = var1.getInsets();
         var3.width += var9.left + var9.right;
         var3.height += var9.top + var9.bottom;
         return var3;
      }
   }

   public Dimension maximumLayoutSize(Container var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public float getLayoutAlignmentX(Container var1) {
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container var1) {
      return 0.5F;
   }

   public void invalidateLayout(Container var1) {
   }

   public void layoutContainer(Container var1) {
      synchronized(var1.getTreeLock()) {
         Insets var3 = var1.getInsets();
         int var4 = var3.top;
         int var5 = var1.height - var3.bottom;
         int var6 = var3.left;
         int var7 = var1.width - var3.right;
         boolean var8 = var1.getComponentOrientation().isLeftToRight();
         Component var9 = null;
         Dimension var10;
         if ((var9 = this.getChild("North", var8)) != null) {
            var9.setSize(var7 - var6, var9.height);
            var10 = var9.getPreferredSize();
            var9.setBounds(var6, var4, var7 - var6, var10.height);
            var4 += var10.height + this.vgap;
         }

         if ((var9 = this.getChild("South", var8)) != null) {
            var9.setSize(var7 - var6, var9.height);
            var10 = var9.getPreferredSize();
            var9.setBounds(var6, var5 - var10.height, var7 - var6, var10.height);
            var5 -= var10.height + this.vgap;
         }

         if ((var9 = this.getChild("East", var8)) != null) {
            var9.setSize(var9.width, var5 - var4);
            var10 = var9.getPreferredSize();
            var9.setBounds(var7 - var10.width, var4, var10.width, var5 - var4);
            var7 -= var10.width + this.hgap;
         }

         if ((var9 = this.getChild("West", var8)) != null) {
            var9.setSize(var9.width, var5 - var4);
            var10 = var9.getPreferredSize();
            var9.setBounds(var6, var4, var10.width, var5 - var4);
            var6 += var10.width + this.hgap;
         }

         if ((var9 = this.getChild("Center", var8)) != null) {
            var9.setBounds(var6, var4, var7 - var6, var5 - var4);
         }

      }
   }

   private Component getChild(String var1, boolean var2) {
      Component var3 = null;
      if (var1 == "North") {
         var3 = this.firstLine != null ? this.firstLine : this.north;
      } else if (var1 == "South") {
         var3 = this.lastLine != null ? this.lastLine : this.south;
      } else if (var1 == "West") {
         var3 = var2 ? this.firstItem : this.lastItem;
         if (var3 == null) {
            var3 = this.west;
         }
      } else if (var1 == "East") {
         var3 = var2 ? this.lastItem : this.firstItem;
         if (var3 == null) {
            var3 = this.east;
         }
      } else if (var1 == "Center") {
         var3 = this.center;
      }

      if (var3 != null && !var3.visible) {
         var3 = null;
      }

      return var3;
   }

   public String toString() {
      return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]";
   }
}
