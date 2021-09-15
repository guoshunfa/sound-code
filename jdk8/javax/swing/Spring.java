package javax.swing;

import java.awt.Component;

public abstract class Spring {
   public static final int UNSET = Integer.MIN_VALUE;

   protected Spring() {
   }

   public abstract int getMinimumValue();

   public abstract int getPreferredValue();

   public abstract int getMaximumValue();

   public abstract int getValue();

   public abstract void setValue(int var1);

   private double range(boolean var1) {
      return var1 ? (double)(this.getPreferredValue() - this.getMinimumValue()) : (double)(this.getMaximumValue() - this.getPreferredValue());
   }

   double getStrain() {
      double var1 = (double)(this.getValue() - this.getPreferredValue());
      return var1 / this.range(this.getValue() < this.getPreferredValue());
   }

   void setStrain(double var1) {
      this.setValue(this.getPreferredValue() + (int)(var1 * this.range(var1 < 0.0D)));
   }

   boolean isCyclic(SpringLayout var1) {
      return false;
   }

   public static Spring constant(int var0) {
      return constant(var0, var0, var0);
   }

   public static Spring constant(int var0, int var1, int var2) {
      return new Spring.StaticSpring(var0, var1, var2);
   }

   public static Spring minus(Spring var0) {
      return new Spring.NegativeSpring(var0);
   }

   public static Spring sum(Spring var0, Spring var1) {
      return new Spring.SumSpring(var0, var1);
   }

   public static Spring max(Spring var0, Spring var1) {
      return new Spring.MaxSpring(var0, var1);
   }

   static Spring difference(Spring var0, Spring var1) {
      return sum(var0, minus(var1));
   }

   public static Spring scale(Spring var0, float var1) {
      checkArg(var0);
      return new Spring.ScaleSpring(var0, var1);
   }

   public static Spring width(Component var0) {
      checkArg(var0);
      return new Spring.WidthSpring(var0);
   }

   public static Spring height(Component var0) {
      checkArg(var0);
      return new Spring.HeightSpring(var0);
   }

   private static void checkArg(Object var0) {
      if (var0 == null) {
         throw new NullPointerException("Argument must not be null");
      }
   }

   private static class MaxSpring extends Spring.CompoundSpring {
      public MaxSpring(Spring var1, Spring var2) {
         super(var1, var2);
      }

      protected int op(int var1, int var2) {
         return Math.max(var1, var2);
      }

      protected void setNonClearValue(int var1) {
         super.setNonClearValue(var1);
         this.s1.setValue(var1);
         this.s2.setValue(var1);
      }
   }

   private static class SumSpring extends Spring.CompoundSpring {
      public SumSpring(Spring var1, Spring var2) {
         super(var1, var2);
      }

      protected int op(int var1, int var2) {
         return var1 + var2;
      }

      protected void setNonClearValue(int var1) {
         super.setNonClearValue(var1);
         this.s1.setStrain(this.getStrain());
         this.s2.setValue(var1 - this.s1.getValue());
      }
   }

   abstract static class CompoundSpring extends Spring.StaticSpring {
      protected Spring s1;
      protected Spring s2;

      public CompoundSpring(Spring var1, Spring var2) {
         super(Integer.MIN_VALUE);
         this.s1 = var1;
         this.s2 = var2;
      }

      public String toString() {
         return "CompoundSpring of " + this.s1 + " and " + this.s2;
      }

      protected void clear() {
         super.clear();
         this.min = this.pref = this.max = Integer.MIN_VALUE;
         this.s1.setValue(Integer.MIN_VALUE);
         this.s2.setValue(Integer.MIN_VALUE);
      }

      protected abstract int op(int var1, int var2);

      public int getMinimumValue() {
         if (this.min == Integer.MIN_VALUE) {
            this.min = this.op(this.s1.getMinimumValue(), this.s2.getMinimumValue());
         }

         return this.min;
      }

      public int getPreferredValue() {
         if (this.pref == Integer.MIN_VALUE) {
            this.pref = this.op(this.s1.getPreferredValue(), this.s2.getPreferredValue());
         }

         return this.pref;
      }

      public int getMaximumValue() {
         if (this.max == Integer.MIN_VALUE) {
            this.max = this.op(this.s1.getMaximumValue(), this.s2.getMaximumValue());
         }

         return this.max;
      }

      public int getValue() {
         if (this.size == Integer.MIN_VALUE) {
            this.size = this.op(this.s1.getValue(), this.s2.getValue());
         }

         return this.size;
      }

      boolean isCyclic(SpringLayout var1) {
         return var1.isCyclic(this.s1) || var1.isCyclic(this.s2);
      }
   }

   abstract static class SpringMap extends Spring {
      private Spring s;

      public SpringMap(Spring var1) {
         this.s = var1;
      }

      protected abstract int map(int var1);

      protected abstract int inv(int var1);

      public int getMinimumValue() {
         return this.map(this.s.getMinimumValue());
      }

      public int getPreferredValue() {
         return this.map(this.s.getPreferredValue());
      }

      public int getMaximumValue() {
         return Math.min(32767, this.map(this.s.getMaximumValue()));
      }

      public int getValue() {
         return this.map(this.s.getValue());
      }

      public void setValue(int var1) {
         if (var1 == Integer.MIN_VALUE) {
            this.s.setValue(Integer.MIN_VALUE);
         } else {
            this.s.setValue(this.inv(var1));
         }

      }

      boolean isCyclic(SpringLayout var1) {
         return this.s.isCyclic(var1);
      }
   }

   static class HeightSpring extends Spring.AbstractSpring {
      Component c;

      public HeightSpring(Component var1) {
         this.c = var1;
      }

      public int getMinimumValue() {
         return this.c.getMinimumSize().height;
      }

      public int getPreferredValue() {
         return this.c.getPreferredSize().height;
      }

      public int getMaximumValue() {
         return Math.min(32767, this.c.getMaximumSize().height);
      }
   }

   static class WidthSpring extends Spring.AbstractSpring {
      Component c;

      public WidthSpring(Component var1) {
         this.c = var1;
      }

      public int getMinimumValue() {
         return this.c.getMinimumSize().width;
      }

      public int getPreferredValue() {
         return this.c.getPreferredSize().width;
      }

      public int getMaximumValue() {
         return Math.min(32767, this.c.getMaximumSize().width);
      }
   }

   private static class ScaleSpring extends Spring {
      private Spring s;
      private float factor;

      private ScaleSpring(Spring var1, float var2) {
         this.s = var1;
         this.factor = var2;
      }

      public int getMinimumValue() {
         return Math.round((float)(this.factor < 0.0F ? this.s.getMaximumValue() : this.s.getMinimumValue()) * this.factor);
      }

      public int getPreferredValue() {
         return Math.round((float)this.s.getPreferredValue() * this.factor);
      }

      public int getMaximumValue() {
         return Math.round((float)(this.factor < 0.0F ? this.s.getMinimumValue() : this.s.getMaximumValue()) * this.factor);
      }

      public int getValue() {
         return Math.round((float)this.s.getValue() * this.factor);
      }

      public void setValue(int var1) {
         if (var1 == Integer.MIN_VALUE) {
            this.s.setValue(Integer.MIN_VALUE);
         } else {
            this.s.setValue(Math.round((float)var1 / this.factor));
         }

      }

      boolean isCyclic(SpringLayout var1) {
         return this.s.isCyclic(var1);
      }

      // $FF: synthetic method
      ScaleSpring(Spring var1, float var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class NegativeSpring extends Spring {
      private Spring s;

      public NegativeSpring(Spring var1) {
         this.s = var1;
      }

      public int getMinimumValue() {
         return -this.s.getMaximumValue();
      }

      public int getPreferredValue() {
         return -this.s.getPreferredValue();
      }

      public int getMaximumValue() {
         return -this.s.getMinimumValue();
      }

      public int getValue() {
         return -this.s.getValue();
      }

      public void setValue(int var1) {
         this.s.setValue(-var1);
      }

      boolean isCyclic(SpringLayout var1) {
         return this.s.isCyclic(var1);
      }
   }

   private static class StaticSpring extends Spring.AbstractSpring {
      protected int min;
      protected int pref;
      protected int max;

      public StaticSpring(int var1) {
         this(var1, var1, var1);
      }

      public StaticSpring(int var1, int var2, int var3) {
         this.min = var1;
         this.pref = var2;
         this.max = var3;
      }

      public String toString() {
         return "StaticSpring [" + this.min + ", " + this.pref + ", " + this.max + "]";
      }

      public int getMinimumValue() {
         return this.min;
      }

      public int getPreferredValue() {
         return this.pref;
      }

      public int getMaximumValue() {
         return this.max;
      }
   }

   abstract static class AbstractSpring extends Spring {
      protected int size = Integer.MIN_VALUE;

      public int getValue() {
         return this.size != Integer.MIN_VALUE ? this.size : this.getPreferredValue();
      }

      public final void setValue(int var1) {
         if (this.size != var1) {
            if (var1 == Integer.MIN_VALUE) {
               this.clear();
            } else {
               this.setNonClearValue(var1);
            }

         }
      }

      protected void clear() {
         this.size = Integer.MIN_VALUE;
      }

      protected void setNonClearValue(int var1) {
         this.size = var1;
      }
   }
}
