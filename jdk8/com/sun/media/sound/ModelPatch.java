package com.sun.media.sound;

import javax.sound.midi.Patch;

public final class ModelPatch extends Patch {
   private boolean percussion = false;

   public ModelPatch(int var1, int var2) {
      super(var1, var2);
   }

   public ModelPatch(int var1, int var2, boolean var3) {
      super(var1, var2);
      this.percussion = var3;
   }

   public boolean isPercussion() {
      return this.percussion;
   }
}
