package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSRegion {
   public static final int OPTION_SELFNONEXCLUSIVE = 1;
   List<DLSModulator> modulators = new ArrayList();
   int keyfrom;
   int keyto;
   int velfrom;
   int velto;
   int options;
   int exclusiveClass;
   int fusoptions;
   int phasegroup;
   long channel;
   DLSSample sample = null;
   DLSSampleOptions sampleoptions;

   public List<DLSModulator> getModulators() {
      return this.modulators;
   }

   public long getChannel() {
      return this.channel;
   }

   public void setChannel(long var1) {
      this.channel = var1;
   }

   public int getExclusiveClass() {
      return this.exclusiveClass;
   }

   public void setExclusiveClass(int var1) {
      this.exclusiveClass = var1;
   }

   public int getFusoptions() {
      return this.fusoptions;
   }

   public void setFusoptions(int var1) {
      this.fusoptions = var1;
   }

   public int getKeyfrom() {
      return this.keyfrom;
   }

   public void setKeyfrom(int var1) {
      this.keyfrom = var1;
   }

   public int getKeyto() {
      return this.keyto;
   }

   public void setKeyto(int var1) {
      this.keyto = var1;
   }

   public int getOptions() {
      return this.options;
   }

   public void setOptions(int var1) {
      this.options = var1;
   }

   public int getPhasegroup() {
      return this.phasegroup;
   }

   public void setPhasegroup(int var1) {
      this.phasegroup = var1;
   }

   public DLSSample getSample() {
      return this.sample;
   }

   public void setSample(DLSSample var1) {
      this.sample = var1;
   }

   public int getVelfrom() {
      return this.velfrom;
   }

   public void setVelfrom(int var1) {
      this.velfrom = var1;
   }

   public int getVelto() {
      return this.velto;
   }

   public void setVelto(int var1) {
      this.velto = var1;
   }

   public void setModulators(List<DLSModulator> var1) {
      this.modulators = var1;
   }

   public DLSSampleOptions getSampleoptions() {
      return this.sampleoptions;
   }

   public void setSampleoptions(DLSSampleOptions var1) {
      this.sampleoptions = var1;
   }
}
