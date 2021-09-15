package com.sun.media.sound;

import java.io.IOException;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.VoiceStatus;

public abstract class ModelAbstractOscillator implements ModelOscillator, ModelOscillatorStream, Soundbank {
   protected float pitch = 6000.0F;
   protected float samplerate;
   protected MidiChannel channel;
   protected VoiceStatus voice;
   protected int noteNumber;
   protected int velocity;
   protected boolean on = false;

   public void init() {
   }

   public void close() throws IOException {
   }

   public void noteOff(int var1) {
      this.on = false;
   }

   public void noteOn(MidiChannel var1, VoiceStatus var2, int var3, int var4) {
      this.channel = var1;
      this.voice = var2;
      this.noteNumber = var3;
      this.velocity = var4;
      this.on = true;
   }

   public int read(float[][] var1, int var2, int var3) throws IOException {
      return -1;
   }

   public MidiChannel getChannel() {
      return this.channel;
   }

   public VoiceStatus getVoice() {
      return this.voice;
   }

   public int getNoteNumber() {
      return this.noteNumber;
   }

   public int getVelocity() {
      return this.velocity;
   }

   public boolean isOn() {
      return this.on;
   }

   public void setPitch(float var1) {
      this.pitch = var1;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setSampleRate(float var1) {
      this.samplerate = var1;
   }

   public float getSampleRate() {
      return this.samplerate;
   }

   public float getAttenuation() {
      return 0.0F;
   }

   public int getChannels() {
      return 1;
   }

   public String getName() {
      return this.getClass().getName();
   }

   public Patch getPatch() {
      return new Patch(0, 0);
   }

   public ModelOscillatorStream open(float var1) {
      ModelAbstractOscillator var2;
      try {
         var2 = (ModelAbstractOscillator)this.getClass().newInstance();
      } catch (InstantiationException var4) {
         throw new IllegalArgumentException(var4);
      } catch (IllegalAccessException var5) {
         throw new IllegalArgumentException(var5);
      }

      var2.setSampleRate(var1);
      var2.init();
      return var2;
   }

   public ModelPerformer getPerformer() {
      ModelPerformer var1 = new ModelPerformer();
      var1.getOscillators().add(this);
      return var1;
   }

   public ModelInstrument getInstrument() {
      SimpleInstrument var1 = new SimpleInstrument();
      var1.setName(this.getName());
      var1.add(this.getPerformer());
      var1.setPatch(this.getPatch());
      return var1;
   }

   public Soundbank getSoundBank() {
      SimpleSoundbank var1 = new SimpleSoundbank();
      var1.addInstrument(this.getInstrument());
      return var1;
   }

   public String getDescription() {
      return this.getName();
   }

   public Instrument getInstrument(Patch var1) {
      ModelInstrument var2 = this.getInstrument();
      Patch var3 = var2.getPatch();
      if (var3.getBank() != var1.getBank()) {
         return null;
      } else if (var3.getProgram() != var1.getProgram()) {
         return null;
      } else {
         return var3 instanceof ModelPatch && var1 instanceof ModelPatch && ((ModelPatch)var3).isPercussion() != ((ModelPatch)var1).isPercussion() ? null : var2;
      }
   }

   public Instrument[] getInstruments() {
      return new Instrument[]{this.getInstrument()};
   }

   public SoundbankResource[] getResources() {
      return new SoundbankResource[0];
   }

   public String getVendor() {
      return null;
   }

   public String getVersion() {
      return null;
   }
}
