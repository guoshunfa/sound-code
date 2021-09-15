package javax.sound.sampled;

public abstract class EnumControl extends Control {
   private Object[] values;
   private Object value;

   protected EnumControl(EnumControl.Type var1, Object[] var2, Object var3) {
      super(var1);
      this.values = var2;
      this.value = var3;
   }

   public void setValue(Object var1) {
      if (!this.isValueSupported(var1)) {
         throw new IllegalArgumentException("Requested value " + var1 + " is not supported.");
      } else {
         this.value = var1;
      }
   }

   public Object getValue() {
      return this.value;
   }

   public Object[] getValues() {
      Object[] var1 = new Object[this.values.length];

      for(int var2 = 0; var2 < this.values.length; ++var2) {
         var1[var2] = this.values[var2];
      }

      return var1;
   }

   private boolean isValueSupported(Object var1) {
      for(int var2 = 0; var2 < this.values.length; ++var2) {
         if (var1.equals(this.values[var2])) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      return new String(this.getType() + " with current value: " + this.getValue());
   }

   public static class Type extends Control.Type {
      public static final EnumControl.Type REVERB = new EnumControl.Type("Reverb");

      protected Type(String var1) {
         super(var1);
      }
   }
}
