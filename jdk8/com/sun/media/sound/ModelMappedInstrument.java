package com.sun.media.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioFormat;

public final class ModelMappedInstrument extends ModelInstrument {
   private final ModelInstrument ins;

   public ModelMappedInstrument(ModelInstrument var1, Patch var2) {
      super(var1.getSoundbank(), var2, var1.getName(), var1.getDataClass());
      this.ins = var1;
   }

   public Object getData() {
      return this.ins.getData();
   }

   public ModelPerformer[] getPerformers() {
      return this.ins.getPerformers();
   }

   public ModelDirector getDirector(ModelPerformer[] var1, MidiChannel var2, ModelDirectedPlayer var3) {
      return this.ins.getDirector(var1, var2, var3);
   }

   public ModelChannelMixer getChannelMixer(MidiChannel var1, AudioFormat var2) {
      return this.ins.getChannelMixer(var1, var2);
   }
}
