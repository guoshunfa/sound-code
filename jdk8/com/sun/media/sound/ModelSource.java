package com.sun.media.sound;

public final class ModelSource {
   public static final ModelIdentifier SOURCE_NONE = null;
   public static final ModelIdentifier SOURCE_NOTEON_KEYNUMBER = new ModelIdentifier("noteon", "keynumber");
   public static final ModelIdentifier SOURCE_NOTEON_VELOCITY = new ModelIdentifier("noteon", "velocity");
   public static final ModelIdentifier SOURCE_EG1 = new ModelIdentifier("eg", (String)null, 0);
   public static final ModelIdentifier SOURCE_EG2 = new ModelIdentifier("eg", (String)null, 1);
   public static final ModelIdentifier SOURCE_LFO1 = new ModelIdentifier("lfo", (String)null, 0);
   public static final ModelIdentifier SOURCE_LFO2 = new ModelIdentifier("lfo", (String)null, 1);
   public static final ModelIdentifier SOURCE_MIDI_PITCH = new ModelIdentifier("midi", "pitch", 0);
   public static final ModelIdentifier SOURCE_MIDI_CHANNEL_PRESSURE = new ModelIdentifier("midi", "channel_pressure", 0);
   public static final ModelIdentifier SOURCE_MIDI_POLY_PRESSURE = new ModelIdentifier("midi", "poly_pressure", 0);
   public static final ModelIdentifier SOURCE_MIDI_CC_0 = new ModelIdentifier("midi_cc", "0", 0);
   public static final ModelIdentifier SOURCE_MIDI_RPN_0 = new ModelIdentifier("midi_rpn", "0", 0);
   private ModelIdentifier source;
   private ModelTransform transform;

   public ModelSource() {
      this.source = SOURCE_NONE;
      this.transform = new ModelStandardTransform();
   }

   public ModelSource(ModelIdentifier var1) {
      this.source = SOURCE_NONE;
      this.source = var1;
      this.transform = new ModelStandardTransform();
   }

   public ModelSource(ModelIdentifier var1, boolean var2) {
      this.source = SOURCE_NONE;
      this.source = var1;
      this.transform = new ModelStandardTransform(var2);
   }

   public ModelSource(ModelIdentifier var1, boolean var2, boolean var3) {
      this.source = SOURCE_NONE;
      this.source = var1;
      this.transform = new ModelStandardTransform(var2, var3);
   }

   public ModelSource(ModelIdentifier var1, boolean var2, boolean var3, int var4) {
      this.source = SOURCE_NONE;
      this.source = var1;
      this.transform = new ModelStandardTransform(var2, var3, var4);
   }

   public ModelSource(ModelIdentifier var1, ModelTransform var2) {
      this.source = SOURCE_NONE;
      this.source = var1;
      this.transform = var2;
   }

   public ModelIdentifier getIdentifier() {
      return this.source;
   }

   public void setIdentifier(ModelIdentifier var1) {
      this.source = var1;
   }

   public ModelTransform getTransform() {
      return this.transform;
   }

   public void setTransform(ModelTransform var1) {
      this.transform = var1;
   }
}
