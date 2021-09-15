package javax.sound.sampled;

public abstract class CompoundControl extends Control {
   private Control[] controls;

   protected CompoundControl(CompoundControl.Type var1, Control[] var2) {
      super(var1);
      this.controls = var2;
   }

   public Control[] getMemberControls() {
      Control[] var1 = new Control[this.controls.length];

      for(int var2 = 0; var2 < this.controls.length; ++var2) {
         var1[var2] = this.controls[var2];
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < this.controls.length; ++var2) {
         if (var2 != 0) {
            var1.append(", ");
            if (var2 + 1 == this.controls.length) {
               var1.append("and ");
            }
         }

         var1.append((Object)this.controls[var2].getType());
      }

      return new String(this.getType() + " Control containing " + var1 + " Controls.");
   }

   public static class Type extends Control.Type {
      protected Type(String var1) {
         super(var1);
      }
   }
}
