package javax.sound.sampled;

public abstract class FloatControl extends Control {
   private float minimum;
   private float maximum;
   private float precision;
   private int updatePeriod;
   private final String units;
   private final String minLabel;
   private final String maxLabel;
   private final String midLabel;
   private float value;

   protected FloatControl(FloatControl.Type var1, float var2, float var3, float var4, int var5, float var6, String var7, String var8, String var9, String var10) {
      super(var1);
      if (var2 > var3) {
         throw new IllegalArgumentException("Minimum value " + var2 + " exceeds maximum value " + var3 + ".");
      } else if (var6 < var2) {
         throw new IllegalArgumentException("Initial value " + var6 + " smaller than allowable minimum value " + var2 + ".");
      } else if (var6 > var3) {
         throw new IllegalArgumentException("Initial value " + var6 + " exceeds allowable maximum value " + var3 + ".");
      } else {
         this.minimum = var2;
         this.maximum = var3;
         this.precision = var4;
         this.updatePeriod = var5;
         this.value = var6;
         this.units = var7;
         this.minLabel = var8 == null ? "" : var8;
         this.midLabel = var9 == null ? "" : var9;
         this.maxLabel = var10 == null ? "" : var10;
      }
   }

   protected FloatControl(FloatControl.Type var1, float var2, float var3, float var4, int var5, float var6, String var7) {
      this(var1, var2, var3, var4, var5, var6, var7, "", "", "");
   }

   public void setValue(float var1) {
      if (var1 > this.maximum) {
         throw new IllegalArgumentException("Requested value " + var1 + " exceeds allowable maximum value " + this.maximum + ".");
      } else if (var1 < this.minimum) {
         throw new IllegalArgumentException("Requested value " + var1 + " smaller than allowable minimum value " + this.minimum + ".");
      } else {
         this.value = var1;
      }
   }

   public float getValue() {
      return this.value;
   }

   public float getMaximum() {
      return this.maximum;
   }

   public float getMinimum() {
      return this.minimum;
   }

   public String getUnits() {
      return this.units;
   }

   public String getMinLabel() {
      return this.minLabel;
   }

   public String getMidLabel() {
      return this.midLabel;
   }

   public String getMaxLabel() {
      return this.maxLabel;
   }

   public float getPrecision() {
      return this.precision;
   }

   public int getUpdatePeriod() {
      return this.updatePeriod;
   }

   public void shift(float var1, float var2, int var3) {
      if (var1 < this.minimum) {
         throw new IllegalArgumentException("Requested value " + var1 + " smaller than allowable minimum value " + this.minimum + ".");
      } else if (var1 > this.maximum) {
         throw new IllegalArgumentException("Requested value " + var1 + " exceeds allowable maximum value " + this.maximum + ".");
      } else {
         this.setValue(var2);
      }
   }

   public String toString() {
      return new String(this.getType() + " with current value: " + this.getValue() + " " + this.units + " (range: " + this.minimum + " - " + this.maximum + ")");
   }

   public static class Type extends Control.Type {
      public static final FloatControl.Type MASTER_GAIN = new FloatControl.Type("Master Gain");
      public static final FloatControl.Type AUX_SEND = new FloatControl.Type("AUX Send");
      public static final FloatControl.Type AUX_RETURN = new FloatControl.Type("AUX Return");
      public static final FloatControl.Type REVERB_SEND = new FloatControl.Type("Reverb Send");
      public static final FloatControl.Type REVERB_RETURN = new FloatControl.Type("Reverb Return");
      public static final FloatControl.Type VOLUME = new FloatControl.Type("Volume");
      public static final FloatControl.Type PAN = new FloatControl.Type("Pan");
      public static final FloatControl.Type BALANCE = new FloatControl.Type("Balance");
      public static final FloatControl.Type SAMPLE_RATE = new FloatControl.Type("Sample Rate");

      protected Type(String var1) {
         super(var1);
      }
   }
}
