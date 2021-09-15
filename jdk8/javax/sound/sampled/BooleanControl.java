package javax.sound.sampled;

public abstract class BooleanControl extends Control {
   private final String trueStateLabel;
   private final String falseStateLabel;
   private boolean value;

   protected BooleanControl(BooleanControl.Type var1, boolean var2, String var3, String var4) {
      super(var1);
      this.value = var2;
      this.trueStateLabel = var3;
      this.falseStateLabel = var4;
   }

   protected BooleanControl(BooleanControl.Type var1, boolean var2) {
      this(var1, var2, "true", "false");
   }

   public void setValue(boolean var1) {
      this.value = var1;
   }

   public boolean getValue() {
      return this.value;
   }

   public String getStateLabel(boolean var1) {
      return var1 ? this.trueStateLabel : this.falseStateLabel;
   }

   public String toString() {
      return new String(super.toString() + " with current value: " + this.getStateLabel(this.getValue()));
   }

   public static class Type extends Control.Type {
      public static final BooleanControl.Type MUTE = new BooleanControl.Type("Mute");
      public static final BooleanControl.Type APPLY_REVERB = new BooleanControl.Type("Apply Reverb");

      protected Type(String var1) {
         super(var1);
      }
   }
}
