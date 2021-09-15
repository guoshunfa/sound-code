package com.sun.media.sound;

import java.io.InputStream;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SF2Sample extends SoundbankResource {
   String name = "";
   long startLoop = 0L;
   long endLoop = 0L;
   long sampleRate = 44100L;
   int originalPitch = 60;
   byte pitchCorrection = 0;
   int sampleLink = 0;
   int sampleType = 0;
   ModelByteBuffer data;
   ModelByteBuffer data24;

   public SF2Sample(Soundbank var1) {
      super(var1, (String)null, AudioInputStream.class);
   }

   public SF2Sample() {
      super((Soundbank)null, (String)null, AudioInputStream.class);
   }

   public Object getData() {
      AudioFormat var1 = this.getFormat();
      InputStream var2 = this.data.getInputStream();
      return var2 == null ? null : new AudioInputStream(var2, var1, this.data.capacity());
   }

   public ModelByteBuffer getDataBuffer() {
      return this.data;
   }

   public ModelByteBuffer getData24Buffer() {
      return this.data24;
   }

   public AudioFormat getFormat() {
      return new AudioFormat((float)this.sampleRate, 16, 1, true, false);
   }

   public void setData(ModelByteBuffer var1) {
      this.data = var1;
   }

   public void setData(byte[] var1) {
      this.data = new ModelByteBuffer(var1);
   }

   public void setData(byte[] var1, int var2, int var3) {
      this.data = new ModelByteBuffer(var1, var2, var3);
   }

   public void setData24(ModelByteBuffer var1) {
      this.data24 = var1;
   }

   public void setData24(byte[] var1) {
      this.data24 = new ModelByteBuffer(var1);
   }

   public void setData24(byte[] var1, int var2, int var3) {
      this.data24 = new ModelByteBuffer(var1, var2, var3);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public long getEndLoop() {
      return this.endLoop;
   }

   public void setEndLoop(long var1) {
      this.endLoop = var1;
   }

   public int getOriginalPitch() {
      return this.originalPitch;
   }

   public void setOriginalPitch(int var1) {
      this.originalPitch = var1;
   }

   public byte getPitchCorrection() {
      return this.pitchCorrection;
   }

   public void setPitchCorrection(byte var1) {
      this.pitchCorrection = var1;
   }

   public int getSampleLink() {
      return this.sampleLink;
   }

   public void setSampleLink(int var1) {
      this.sampleLink = var1;
   }

   public long getSampleRate() {
      return this.sampleRate;
   }

   public void setSampleRate(long var1) {
      this.sampleRate = var1;
   }

   public int getSampleType() {
      return this.sampleType;
   }

   public void setSampleType(int var1) {
      this.sampleType = var1;
   }

   public long getStartLoop() {
      return this.startLoop;
   }

   public void setStartLoop(long var1) {
      this.startLoop = var1;
   }

   public String toString() {
      return "Sample: " + this.name;
   }
}
