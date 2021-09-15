package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class DLSSampleOptions {
   int unitynote;
   short finetune;
   int attenuation;
   long options;
   List<DLSSampleLoop> loops = new ArrayList();

   public int getAttenuation() {
      return this.attenuation;
   }

   public void setAttenuation(int var1) {
      this.attenuation = var1;
   }

   public short getFinetune() {
      return this.finetune;
   }

   public void setFinetune(short var1) {
      this.finetune = var1;
   }

   public List<DLSSampleLoop> getLoops() {
      return this.loops;
   }

   public long getOptions() {
      return this.options;
   }

   public void setOptions(long var1) {
      this.options = var1;
   }

   public int getUnitynote() {
      return this.unitynote;
   }

   public void setUnitynote(int var1) {
      this.unitynote = var1;
   }
}
