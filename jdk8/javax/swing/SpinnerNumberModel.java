package javax.swing;

import java.io.Serializable;

public class SpinnerNumberModel extends AbstractSpinnerModel implements Serializable {
   private Number stepSize;
   private Number value;
   private Comparable minimum;
   private Comparable maximum;

   public SpinnerNumberModel(Number var1, Comparable var2, Comparable var3, Number var4) {
      if (var1 != null && var4 != null) {
         if ((var2 == null || var2.compareTo(var1) <= 0) && (var3 == null || var3.compareTo(var1) >= 0)) {
            this.value = var1;
            this.minimum = var2;
            this.maximum = var3;
            this.stepSize = var4;
         } else {
            throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
         }
      } else {
         throw new IllegalArgumentException("value and stepSize must be non-null");
      }
   }

   public SpinnerNumberModel(int var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4);
   }

   public SpinnerNumberModel(double var1, double var3, double var5, double var7) {
      this(new Double(var1), new Double(var3), new Double(var5), new Double(var7));
   }

   public SpinnerNumberModel() {
      this(0, (Comparable)null, (Comparable)null, 1);
   }

   public void setMinimum(Comparable var1) {
      if (var1 == null) {
         if (this.minimum == null) {
            return;
         }
      } else if (var1.equals(this.minimum)) {
         return;
      }

      this.minimum = var1;
      this.fireStateChanged();
   }

   public Comparable getMinimum() {
      return this.minimum;
   }

   public void setMaximum(Comparable var1) {
      if (var1 == null) {
         if (this.maximum == null) {
            return;
         }
      } else if (var1.equals(this.maximum)) {
         return;
      }

      this.maximum = var1;
      this.fireStateChanged();
   }

   public Comparable getMaximum() {
      return this.maximum;
   }

   public void setStepSize(Number var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null stepSize");
      } else {
         if (!var1.equals(this.stepSize)) {
            this.stepSize = var1;
            this.fireStateChanged();
         }

      }
   }

   public Number getStepSize() {
      return this.stepSize;
   }

   private Number incrValue(int var1) {
      Object var2;
      if (!(this.value instanceof Float) && !(this.value instanceof Double)) {
         long var5 = this.value.longValue() + this.stepSize.longValue() * (long)var1;
         if (this.value instanceof Long) {
            var2 = var5;
         } else if (this.value instanceof Integer) {
            var2 = (int)var5;
         } else if (this.value instanceof Short) {
            var2 = (short)((int)var5);
         } else {
            var2 = (byte)((int)var5);
         }
      } else {
         double var3 = this.value.doubleValue() + this.stepSize.doubleValue() * (double)var1;
         if (this.value instanceof Double) {
            var2 = new Double(var3);
         } else {
            var2 = new Float(var3);
         }
      }

      if (this.maximum != null && this.maximum.compareTo(var2) < 0) {
         return null;
      } else {
         return (Number)(this.minimum != null && this.minimum.compareTo(var2) > 0 ? null : var2);
      }
   }

   public Object getNextValue() {
      return this.incrValue(1);
   }

   public Object getPreviousValue() {
      return this.incrValue(-1);
   }

   public Number getNumber() {
      return this.value;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object var1) {
      if (var1 != null && var1 instanceof Number) {
         if (!var1.equals(this.value)) {
            this.value = (Number)var1;
            this.fireStateChanged();
         }

      } else {
         throw new IllegalArgumentException("illegal value");
      }
   }
}
