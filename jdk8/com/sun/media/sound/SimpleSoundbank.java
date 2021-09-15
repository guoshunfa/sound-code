package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public class SimpleSoundbank implements Soundbank {
   String name = "";
   String version = "";
   String vendor = "";
   String description = "";
   List<SoundbankResource> resources = new ArrayList();
   List<Instrument> instruments = new ArrayList();

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public String getVendor() {
      return this.vendor;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setVendor(String var1) {
      this.vendor = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public SoundbankResource[] getResources() {
      return (SoundbankResource[])this.resources.toArray(new SoundbankResource[this.resources.size()]);
   }

   public Instrument[] getInstruments() {
      Instrument[] var1 = (Instrument[])this.instruments.toArray(new Instrument[this.resources.size()]);
      Arrays.sort(var1, new ModelInstrumentComparator());
      return var1;
   }

   public Instrument getInstrument(Patch var1) {
      int var2 = var1.getProgram();
      int var3 = var1.getBank();
      boolean var4 = false;
      if (var1 instanceof ModelPatch) {
         var4 = ((ModelPatch)var1).isPercussion();
      }

      Iterator var5 = this.instruments.iterator();

      while(var5.hasNext()) {
         Instrument var6 = (Instrument)var5.next();
         Patch var7 = var6.getPatch();
         int var8 = var7.getProgram();
         int var9 = var7.getBank();
         if (var2 == var8 && var3 == var9) {
            boolean var10 = false;
            if (var7 instanceof ModelPatch) {
               var10 = ((ModelPatch)var7).isPercussion();
            }

            if (var4 == var10) {
               return var6;
            }
         }
      }

      return null;
   }

   public void addResource(SoundbankResource var1) {
      if (var1 instanceof Instrument) {
         this.instruments.add((Instrument)var1);
      } else {
         this.resources.add(var1);
      }

   }

   public void removeResource(SoundbankResource var1) {
      if (var1 instanceof Instrument) {
         this.instruments.remove((Instrument)var1);
      } else {
         this.resources.remove(var1);
      }

   }

   public void addInstrument(Instrument var1) {
      this.instruments.add(var1);
   }

   public void removeInstrument(Instrument var1) {
      this.instruments.remove(var1);
   }

   public void addAllInstruments(Soundbank var1) {
      Instrument[] var2 = var1.getInstruments();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Instrument var5 = var2[var4];
         this.addInstrument(var5);
      }

   }

   public void removeAllInstruments(Soundbank var1) {
      Instrument[] var2 = var1.getInstruments();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Instrument var5 = var2[var4];
         this.removeInstrument(var5);
      }

   }
}
