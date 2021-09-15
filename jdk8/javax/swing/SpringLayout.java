package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpringLayout implements LayoutManager2 {
   private Map<Component, SpringLayout.Constraints> componentConstraints = new HashMap();
   private Spring cyclicReference = Spring.constant(Integer.MIN_VALUE);
   private Set<Spring> cyclicSprings;
   private Set<Spring> acyclicSprings;
   public static final String NORTH = "North";
   public static final String SOUTH = "South";
   public static final String EAST = "East";
   public static final String WEST = "West";
   public static final String HORIZONTAL_CENTER = "HorizontalCenter";
   public static final String VERTICAL_CENTER = "VerticalCenter";
   public static final String BASELINE = "Baseline";
   public static final String WIDTH = "Width";
   public static final String HEIGHT = "Height";
   private static String[] ALL_HORIZONTAL = new String[]{"West", "Width", "East", "HorizontalCenter"};
   private static String[] ALL_VERTICAL = new String[]{"North", "Height", "South", "VerticalCenter", "Baseline"};

   private void resetCyclicStatuses() {
      this.cyclicSprings = new HashSet();
      this.acyclicSprings = new HashSet();
   }

   private void setParent(Container var1) {
      this.resetCyclicStatuses();
      SpringLayout.Constraints var2 = this.getConstraints(var1);
      var2.setX(Spring.constant(0));
      var2.setY(Spring.constant(0));
      Spring var3 = var2.getWidth();
      if (var3 instanceof Spring.WidthSpring && ((Spring.WidthSpring)var3).c == var1) {
         var2.setWidth(Spring.constant(0, 0, Integer.MAX_VALUE));
      }

      Spring var4 = var2.getHeight();
      if (var4 instanceof Spring.HeightSpring && ((Spring.HeightSpring)var4).c == var1) {
         var2.setHeight(Spring.constant(0, 0, Integer.MAX_VALUE));
      }

   }

   boolean isCyclic(Spring var1) {
      if (var1 == null) {
         return false;
      } else if (this.cyclicSprings.contains(var1)) {
         return true;
      } else if (this.acyclicSprings.contains(var1)) {
         return false;
      } else {
         this.cyclicSprings.add(var1);
         boolean var2 = var1.isCyclic(this);
         if (!var2) {
            this.acyclicSprings.add(var1);
            this.cyclicSprings.remove(var1);
         } else {
            System.err.println(var1 + " is cyclic. ");
         }

         return var2;
      }
   }

   private Spring abandonCycles(Spring var1) {
      return this.isCyclic(var1) ? this.cyclicReference : var1;
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
      this.componentConstraints.remove(var1);
   }

   private static Dimension addInsets(int var0, int var1, Container var2) {
      Insets var3 = var2.getInsets();
      return new Dimension(var0 + var3.left + var3.right, var1 + var3.top + var3.bottom);
   }

   public Dimension minimumLayoutSize(Container var1) {
      this.setParent(var1);
      SpringLayout.Constraints var2 = this.getConstraints(var1);
      return addInsets(this.abandonCycles(var2.getWidth()).getMinimumValue(), this.abandonCycles(var2.getHeight()).getMinimumValue(), var1);
   }

   public Dimension preferredLayoutSize(Container var1) {
      this.setParent(var1);
      SpringLayout.Constraints var2 = this.getConstraints(var1);
      return addInsets(this.abandonCycles(var2.getWidth()).getPreferredValue(), this.abandonCycles(var2.getHeight()).getPreferredValue(), var1);
   }

   public Dimension maximumLayoutSize(Container var1) {
      this.setParent(var1);
      SpringLayout.Constraints var2 = this.getConstraints(var1);
      return addInsets(this.abandonCycles(var2.getWidth()).getMaximumValue(), this.abandonCycles(var2.getHeight()).getMaximumValue(), var1);
   }

   public void addLayoutComponent(Component var1, Object var2) {
      if (var2 instanceof SpringLayout.Constraints) {
         this.putConstraints(var1, (SpringLayout.Constraints)var2);
      }

   }

   public float getLayoutAlignmentX(Container var1) {
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container var1) {
      return 0.5F;
   }

   public void invalidateLayout(Container var1) {
   }

   public void putConstraint(String var1, Component var2, int var3, String var4, Component var5) {
      this.putConstraint(var1, var2, Spring.constant(var3), var4, var5);
   }

   public void putConstraint(String var1, Component var2, Spring var3, String var4, Component var5) {
      this.putConstraint(var1, var2, Spring.sum(var3, this.getConstraint(var4, var5)));
   }

   private void putConstraint(String var1, Component var2, Spring var3) {
      if (var3 != null) {
         this.getConstraints(var2).setConstraint(var1, var3);
      }

   }

   private SpringLayout.Constraints applyDefaults(Component var1, SpringLayout.Constraints var2) {
      if (var2 == null) {
         var2 = new SpringLayout.Constraints();
      }

      if (var2.c == null) {
         var2.c = var1;
      }

      if (var2.horizontalHistory.size() < 2) {
         this.applyDefaults(var2, "West", Spring.constant(0), "Width", Spring.width(var1), var2.horizontalHistory);
      }

      if (var2.verticalHistory.size() < 2) {
         this.applyDefaults(var2, "North", Spring.constant(0), "Height", Spring.height(var1), var2.verticalHistory);
      }

      return var2;
   }

   private void applyDefaults(SpringLayout.Constraints var1, String var2, Spring var3, String var4, Spring var5, List<String> var6) {
      if (var6.size() == 0) {
         var1.setConstraint(var2, var3);
         var1.setConstraint(var4, var5);
      } else {
         if (var1.getConstraint(var4) == null) {
            var1.setConstraint(var4, var5);
         } else {
            var1.setConstraint(var2, var3);
         }

         Collections.rotate(var6, 1);
      }

   }

   private void putConstraints(Component var1, SpringLayout.Constraints var2) {
      this.componentConstraints.put(var1, this.applyDefaults(var1, var2));
   }

   public SpringLayout.Constraints getConstraints(Component var1) {
      SpringLayout.Constraints var2 = (SpringLayout.Constraints)this.componentConstraints.get(var1);
      if (var2 == null) {
         if (var1 instanceof JComponent) {
            Object var3 = ((JComponent)var1).getClientProperty(SpringLayout.class);
            if (var3 instanceof SpringLayout.Constraints) {
               return this.applyDefaults(var1, (SpringLayout.Constraints)var3);
            }
         }

         var2 = new SpringLayout.Constraints();
         this.putConstraints(var1, var2);
      }

      return var2;
   }

   public Spring getConstraint(String var1, Component var2) {
      var1 = var1.intern();
      return new SpringLayout.SpringProxy(var1, var2, this);
   }

   public void layoutContainer(Container var1) {
      this.setParent(var1);
      int var2 = var1.getComponentCount();
      this.getConstraints(var1).reset();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.getConstraints(var1.getComponent(var3)).reset();
      }

      Insets var12 = var1.getInsets();
      SpringLayout.Constraints var4 = this.getConstraints(var1);
      this.abandonCycles(var4.getX()).setValue(0);
      this.abandonCycles(var4.getY()).setValue(0);
      this.abandonCycles(var4.getWidth()).setValue(var1.getWidth() - var12.left - var12.right);
      this.abandonCycles(var4.getHeight()).setValue(var1.getHeight() - var12.top - var12.bottom);

      for(int var5 = 0; var5 < var2; ++var5) {
         Component var6 = var1.getComponent(var5);
         SpringLayout.Constraints var7 = this.getConstraints(var6);
         int var8 = this.abandonCycles(var7.getX()).getValue();
         int var9 = this.abandonCycles(var7.getY()).getValue();
         int var10 = this.abandonCycles(var7.getWidth()).getValue();
         int var11 = this.abandonCycles(var7.getHeight()).getValue();
         var6.setBounds(var12.left + var8, var12.top + var9, var10, var11);
      }

   }

   private static class SpringProxy extends Spring {
      private String edgeName;
      private Component c;
      private SpringLayout l;

      public SpringProxy(String var1, Component var2, SpringLayout var3) {
         this.edgeName = var1;
         this.c = var2;
         this.l = var3;
      }

      private Spring getConstraint() {
         return this.l.getConstraints(this.c).getConstraint(this.edgeName);
      }

      public int getMinimumValue() {
         return this.getConstraint().getMinimumValue();
      }

      public int getPreferredValue() {
         return this.getConstraint().getPreferredValue();
      }

      public int getMaximumValue() {
         return this.getConstraint().getMaximumValue();
      }

      public int getValue() {
         return this.getConstraint().getValue();
      }

      public void setValue(int var1) {
         this.getConstraint().setValue(var1);
      }

      boolean isCyclic(SpringLayout var1) {
         return var1.isCyclic(this.getConstraint());
      }

      public String toString() {
         return "SpringProxy for " + this.edgeName + " edge of " + this.c.getName() + ".";
      }
   }

   public static class Constraints {
      private Spring x;
      private Spring y;
      private Spring width;
      private Spring height;
      private Spring east;
      private Spring south;
      private Spring horizontalCenter;
      private Spring verticalCenter;
      private Spring baseline;
      private List<String> horizontalHistory = new ArrayList(2);
      private List<String> verticalHistory = new ArrayList(2);
      private Component c;

      public Constraints() {
      }

      public Constraints(Spring var1, Spring var2) {
         this.setX(var1);
         this.setY(var2);
      }

      public Constraints(Spring var1, Spring var2, Spring var3, Spring var4) {
         this.setX(var1);
         this.setY(var2);
         this.setWidth(var3);
         this.setHeight(var4);
      }

      public Constraints(Component var1) {
         this.c = var1;
         this.setX(Spring.constant(var1.getX()));
         this.setY(Spring.constant(var1.getY()));
         this.setWidth(Spring.width(var1));
         this.setHeight(Spring.height(var1));
      }

      private void pushConstraint(String var1, Spring var2, boolean var3) {
         boolean var4 = true;
         List var5 = var3 ? this.horizontalHistory : this.verticalHistory;
         if (var5.contains(var1)) {
            var5.remove(var1);
            var4 = false;
         } else if (var5.size() == 2 && var2 != null) {
            var5.remove(0);
            var4 = false;
         }

         if (var2 != null) {
            var5.add(var1);
         }

         if (!var4) {
            String[] var6 = var3 ? SpringLayout.ALL_HORIZONTAL : SpringLayout.ALL_VERTICAL;
            String[] var7 = var6;
            int var8 = var6.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String var10 = var7[var9];
               if (!var5.contains(var10)) {
                  this.setConstraint(var10, (Spring)null);
               }
            }
         }

      }

      private Spring sum(Spring var1, Spring var2) {
         return var1 != null && var2 != null ? Spring.sum(var1, var2) : null;
      }

      private Spring difference(Spring var1, Spring var2) {
         return var1 != null && var2 != null ? Spring.difference(var1, var2) : null;
      }

      private Spring scale(Spring var1, float var2) {
         return var1 == null ? null : Spring.scale(var1, var2);
      }

      private int getBaselineFromHeight(int var1) {
         return var1 < 0 ? -this.c.getBaseline(this.c.getPreferredSize().width, -var1) : this.c.getBaseline(this.c.getPreferredSize().width, var1);
      }

      private int getHeightFromBaseLine(int var1) {
         Dimension var2 = this.c.getPreferredSize();
         int var3 = var2.height;
         int var4 = this.c.getBaseline(var2.width, var3);
         if (var4 == var1) {
            return var3;
         } else {
            switch(this.c.getBaselineResizeBehavior()) {
            case CONSTANT_DESCENT:
               return var3 + (var1 - var4);
            case CENTER_OFFSET:
               return var3 + 2 * (var1 - var4);
            case CONSTANT_ASCENT:
            default:
               return Integer.MIN_VALUE;
            }
         }
      }

      private Spring heightToRelativeBaseline(Spring var1) {
         return new Spring.SpringMap(var1) {
            protected int map(int var1) {
               return Constraints.this.getBaselineFromHeight(var1);
            }

            protected int inv(int var1) {
               return Constraints.this.getHeightFromBaseLine(var1);
            }
         };
      }

      private Spring relativeBaselineToHeight(Spring var1) {
         return new Spring.SpringMap(var1) {
            protected int map(int var1) {
               return Constraints.this.getHeightFromBaseLine(var1);
            }

            protected int inv(int var1) {
               return Constraints.this.getBaselineFromHeight(var1);
            }
         };
      }

      private boolean defined(List var1, String var2, String var3) {
         return var1.contains(var2) && var1.contains(var3);
      }

      public void setX(Spring var1) {
         this.x = var1;
         this.pushConstraint("West", var1, true);
      }

      public Spring getX() {
         if (this.x == null) {
            if (this.defined(this.horizontalHistory, "East", "Width")) {
               this.x = this.difference(this.east, this.width);
            } else if (this.defined(this.horizontalHistory, "HorizontalCenter", "Width")) {
               this.x = this.difference(this.horizontalCenter, this.scale(this.width, 0.5F));
            } else if (this.defined(this.horizontalHistory, "HorizontalCenter", "East")) {
               this.x = this.difference(this.scale(this.horizontalCenter, 2.0F), this.east);
            }
         }

         return this.x;
      }

      public void setY(Spring var1) {
         this.y = var1;
         this.pushConstraint("North", var1, false);
      }

      public Spring getY() {
         if (this.y == null) {
            if (this.defined(this.verticalHistory, "South", "Height")) {
               this.y = this.difference(this.south, this.height);
            } else if (this.defined(this.verticalHistory, "VerticalCenter", "Height")) {
               this.y = this.difference(this.verticalCenter, this.scale(this.height, 0.5F));
            } else if (this.defined(this.verticalHistory, "VerticalCenter", "South")) {
               this.y = this.difference(this.scale(this.verticalCenter, 2.0F), this.south);
            } else if (this.defined(this.verticalHistory, "Baseline", "Height")) {
               this.y = this.difference(this.baseline, this.heightToRelativeBaseline(this.height));
            } else if (this.defined(this.verticalHistory, "Baseline", "South")) {
               this.y = this.scale(this.difference(this.baseline, this.heightToRelativeBaseline(this.south)), 2.0F);
            }
         }

         return this.y;
      }

      public void setWidth(Spring var1) {
         this.width = var1;
         this.pushConstraint("Width", var1, true);
      }

      public Spring getWidth() {
         if (this.width == null) {
            if (this.horizontalHistory.contains("East")) {
               this.width = this.difference(this.east, this.getX());
            } else if (this.horizontalHistory.contains("HorizontalCenter")) {
               this.width = this.scale(this.difference(this.horizontalCenter, this.getX()), 2.0F);
            }
         }

         return this.width;
      }

      public void setHeight(Spring var1) {
         this.height = var1;
         this.pushConstraint("Height", var1, false);
      }

      public Spring getHeight() {
         if (this.height == null) {
            if (this.verticalHistory.contains("South")) {
               this.height = this.difference(this.south, this.getY());
            } else if (this.verticalHistory.contains("VerticalCenter")) {
               this.height = this.scale(this.difference(this.verticalCenter, this.getY()), 2.0F);
            } else if (this.verticalHistory.contains("Baseline")) {
               this.height = this.relativeBaselineToHeight(this.difference(this.baseline, this.getY()));
            }
         }

         return this.height;
      }

      private void setEast(Spring var1) {
         this.east = var1;
         this.pushConstraint("East", var1, true);
      }

      private Spring getEast() {
         if (this.east == null) {
            this.east = this.sum(this.getX(), this.getWidth());
         }

         return this.east;
      }

      private void setSouth(Spring var1) {
         this.south = var1;
         this.pushConstraint("South", var1, false);
      }

      private Spring getSouth() {
         if (this.south == null) {
            this.south = this.sum(this.getY(), this.getHeight());
         }

         return this.south;
      }

      private Spring getHorizontalCenter() {
         if (this.horizontalCenter == null) {
            this.horizontalCenter = this.sum(this.getX(), this.scale(this.getWidth(), 0.5F));
         }

         return this.horizontalCenter;
      }

      private void setHorizontalCenter(Spring var1) {
         this.horizontalCenter = var1;
         this.pushConstraint("HorizontalCenter", var1, true);
      }

      private Spring getVerticalCenter() {
         if (this.verticalCenter == null) {
            this.verticalCenter = this.sum(this.getY(), this.scale(this.getHeight(), 0.5F));
         }

         return this.verticalCenter;
      }

      private void setVerticalCenter(Spring var1) {
         this.verticalCenter = var1;
         this.pushConstraint("VerticalCenter", var1, false);
      }

      private Spring getBaseline() {
         if (this.baseline == null) {
            this.baseline = this.sum(this.getY(), this.heightToRelativeBaseline(this.getHeight()));
         }

         return this.baseline;
      }

      private void setBaseline(Spring var1) {
         this.baseline = var1;
         this.pushConstraint("Baseline", var1, false);
      }

      public void setConstraint(String var1, Spring var2) {
         var1 = var1.intern();
         if (var1 == "West") {
            this.setX(var2);
         } else if (var1 == "North") {
            this.setY(var2);
         } else if (var1 == "East") {
            this.setEast(var2);
         } else if (var1 == "South") {
            this.setSouth(var2);
         } else if (var1 == "HorizontalCenter") {
            this.setHorizontalCenter(var2);
         } else if (var1 == "Width") {
            this.setWidth(var2);
         } else if (var1 == "Height") {
            this.setHeight(var2);
         } else if (var1 == "VerticalCenter") {
            this.setVerticalCenter(var2);
         } else if (var1 == "Baseline") {
            this.setBaseline(var2);
         }

      }

      public Spring getConstraint(String var1) {
         var1 = var1.intern();
         return var1 == "West" ? this.getX() : (var1 == "North" ? this.getY() : (var1 == "East" ? this.getEast() : (var1 == "South" ? this.getSouth() : (var1 == "Width" ? this.getWidth() : (var1 == "Height" ? this.getHeight() : (var1 == "HorizontalCenter" ? this.getHorizontalCenter() : (var1 == "VerticalCenter" ? this.getVerticalCenter() : (var1 == "Baseline" ? this.getBaseline() : null))))))));
      }

      void reset() {
         Spring[] var1 = new Spring[]{this.x, this.y, this.width, this.height, this.east, this.south, this.horizontalCenter, this.verticalCenter, this.baseline};
         Spring[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Spring var5 = var2[var4];
            if (var5 != null) {
               var5.setValue(Integer.MIN_VALUE);
            }
         }

      }
   }
}
