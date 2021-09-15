package com.sun.media.sound;

import java.util.Comparator;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;

public final class ModelInstrumentComparator implements Comparator<Instrument> {
   public int compare(Instrument var1, Instrument var2) {
      Patch var3 = var1.getPatch();
      Patch var4 = var2.getPatch();
      int var5 = var3.getBank() * 128 + var3.getProgram();
      int var6 = var4.getBank() * 128 + var4.getProgram();
      if (var3 instanceof ModelPatch) {
         var5 += ((ModelPatch)var3).isPercussion() ? 2097152 : 0;
      }

      if (var4 instanceof ModelPatch) {
         var6 += ((ModelPatch)var4).isPercussion() ? 2097152 : 0;
      }

      return var5 - var6;
   }
}
