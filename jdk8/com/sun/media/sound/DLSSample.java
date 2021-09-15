package com.sun.media.sound;

import java.io.InputStream;
import java.util.Arrays;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class DLSSample extends SoundbankResource {
   byte[] guid = null;
   DLSInfo info = new DLSInfo();
   DLSSampleOptions sampleoptions;
   ModelByteBuffer data;
   AudioFormat format;

   public DLSSample(Soundbank var1) {
      super(var1, (String)null, AudioInputStream.class);
   }

   public DLSSample() {
      super((Soundbank)null, (String)null, AudioInputStream.class);
   }

   public DLSInfo getInfo() {
      return this.info;
   }

   public Object getData() {
      AudioFormat var1 = this.getFormat();
      InputStream var2 = this.data.getInputStream();
      return var2 == null ? null : new AudioInputStream(var2, var1, this.data.capacity());
   }

   public ModelByteBuffer getDataBuffer() {
      return this.data;
   }

   public AudioFormat getFormat() {
      return this.format;
   }

   public void setFormat(AudioFormat var1) {
      this.format = var1;
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

   public String getName() {
      return this.info.name;
   }

   public void setName(String var1) {
      this.info.name = var1;
   }

   public DLSSampleOptions getSampleoptions() {
      return this.sampleoptions;
   }

   public void setSampleoptions(DLSSampleOptions var1) {
      this.sampleoptions = var1;
   }

   public String toString() {
      return "Sample: " + this.info.name;
   }

   public byte[] getGuid() {
      return this.guid == null ? null : Arrays.copyOf(this.guid, this.guid.length);
   }

   public void setGuid(byte[] var1) {
      this.guid = var1 == null ? null : Arrays.copyOf(var1, var1.length);
   }
}
