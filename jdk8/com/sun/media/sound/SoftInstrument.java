package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;

public final class SoftInstrument extends Instrument {
   private SoftPerformer[] performers;
   private ModelPerformer[] modelperformers;
   private final Object data;
   private final ModelInstrument ins;

   public SoftInstrument(ModelInstrument var1) {
      super(var1.getSoundbank(), var1.getPatch(), var1.getName(), var1.getDataClass());
      this.data = var1.getData();
      this.ins = var1;
      this.initPerformers(var1.getPerformers());
   }

   public SoftInstrument(ModelInstrument var1, ModelPerformer[] var2) {
      super(var1.getSoundbank(), var1.getPatch(), var1.getName(), var1.getDataClass());
      this.data = var1.getData();
      this.ins = var1;
      this.initPerformers(var2);
   }

   private void initPerformers(ModelPerformer[] var1) {
      this.modelperformers = var1;
      this.performers = new SoftPerformer[var1.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.performers[var2] = new SoftPerformer(var1[var2]);
      }

   }

   public ModelDirector getDirector(MidiChannel var1, ModelDirectedPlayer var2) {
      return this.ins.getDirector(this.modelperformers, var1, var2);
   }

   public ModelInstrument getSourceInstrument() {
      return this.ins;
   }

   public Object getData() {
      return this.data;
   }

   public SoftPerformer getPerformer(int var1) {
      return this.performers[var1];
   }
}
