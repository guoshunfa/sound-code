package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;

public class SimpleInstrument extends ModelInstrument {
   protected int preset = 0;
   protected int bank = 0;
   protected boolean percussion = false;
   protected String name = "";
   protected List<SimpleInstrument.SimpleInstrumentPart> parts = new ArrayList();

   public SimpleInstrument() {
      super((Soundbank)null, (Patch)null, (String)null, (Class)null);
   }

   public void clear() {
      this.parts.clear();
   }

   public void add(ModelPerformer[] var1, int var2, int var3, int var4, int var5, int var6) {
      SimpleInstrument.SimpleInstrumentPart var7 = new SimpleInstrument.SimpleInstrumentPart();
      var7.performers = var1;
      var7.keyFrom = var2;
      var7.keyTo = var3;
      var7.velFrom = var4;
      var7.velTo = var5;
      var7.exclusiveClass = var6;
      this.parts.add(var7);
   }

   public void add(ModelPerformer[] var1, int var2, int var3, int var4, int var5) {
      this.add((ModelPerformer[])var1, var2, var3, var4, var5, -1);
   }

   public void add(ModelPerformer[] var1, int var2, int var3) {
      this.add((ModelPerformer[])var1, var2, var3, 0, 127, -1);
   }

   public void add(ModelPerformer[] var1) {
      this.add((ModelPerformer[])var1, 0, 127, 0, 127, -1);
   }

   public void add(ModelPerformer var1, int var2, int var3, int var4, int var5, int var6) {
      this.add(new ModelPerformer[]{var1}, var2, var3, var4, var5, var6);
   }

   public void add(ModelPerformer var1, int var2, int var3, int var4, int var5) {
      this.add(new ModelPerformer[]{var1}, var2, var3, var4, var5);
   }

   public void add(ModelPerformer var1, int var2, int var3) {
      this.add(new ModelPerformer[]{var1}, var2, var3);
   }

   public void add(ModelPerformer var1) {
      this.add(new ModelPerformer[]{var1});
   }

   public void add(ModelInstrument var1, int var2, int var3, int var4, int var5, int var6) {
      this.add(var1.getPerformers(), var2, var3, var4, var5, var6);
   }

   public void add(ModelInstrument var1, int var2, int var3, int var4, int var5) {
      this.add(var1.getPerformers(), var2, var3, var4, var5);
   }

   public void add(ModelInstrument var1, int var2, int var3) {
      this.add(var1.getPerformers(), var2, var3);
   }

   public void add(ModelInstrument var1) {
      this.add(var1.getPerformers());
   }

   public ModelPerformer[] getPerformers() {
      int var1 = 0;
      Iterator var2 = this.parts.iterator();

      while(var2.hasNext()) {
         SimpleInstrument.SimpleInstrumentPart var3 = (SimpleInstrument.SimpleInstrumentPart)var2.next();
         if (var3.performers != null) {
            var1 += var3.performers.length;
         }
      }

      ModelPerformer[] var11 = new ModelPerformer[var1];
      int var12 = 0;
      Iterator var4 = this.parts.iterator();

      while(true) {
         SimpleInstrument.SimpleInstrumentPart var5;
         do {
            if (!var4.hasNext()) {
               return var11;
            }

            var5 = (SimpleInstrument.SimpleInstrumentPart)var4.next();
         } while(var5.performers == null);

         ModelPerformer[] var6 = var5.performers;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ModelPerformer var9 = var6[var8];
            ModelPerformer var10 = new ModelPerformer();
            var10.setName(this.getName());
            var11[var12++] = var10;
            var10.setDefaultConnectionsEnabled(var9.isDefaultConnectionsEnabled());
            var10.setKeyFrom(var9.getKeyFrom());
            var10.setKeyTo(var9.getKeyTo());
            var10.setVelFrom(var9.getVelFrom());
            var10.setVelTo(var9.getVelTo());
            var10.setExclusiveClass(var9.getExclusiveClass());
            var10.setSelfNonExclusive(var9.isSelfNonExclusive());
            var10.setReleaseTriggered(var9.isReleaseTriggered());
            if (var5.exclusiveClass != -1) {
               var10.setExclusiveClass(var5.exclusiveClass);
            }

            if (var5.keyFrom > var10.getKeyFrom()) {
               var10.setKeyFrom(var5.keyFrom);
            }

            if (var5.keyTo < var10.getKeyTo()) {
               var10.setKeyTo(var5.keyTo);
            }

            if (var5.velFrom > var10.getVelFrom()) {
               var10.setVelFrom(var5.velFrom);
            }

            if (var5.velTo < var10.getVelTo()) {
               var10.setVelTo(var5.velTo);
            }

            var10.getOscillators().addAll(var9.getOscillators());
            var10.getConnectionBlocks().addAll(var9.getConnectionBlocks());
         }
      }
   }

   public Object getData() {
      return null;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public ModelPatch getPatch() {
      return new ModelPatch(this.bank, this.preset, this.percussion);
   }

   public void setPatch(Patch var1) {
      if (var1 instanceof ModelPatch && ((ModelPatch)var1).isPercussion()) {
         this.percussion = true;
         this.bank = var1.getBank();
         this.preset = var1.getProgram();
      } else {
         this.percussion = false;
         this.bank = var1.getBank();
         this.preset = var1.getProgram();
      }

   }

   private static class SimpleInstrumentPart {
      ModelPerformer[] performers;
      int keyFrom;
      int keyTo;
      int velFrom;
      int velTo;
      int exclusiveClass;

      private SimpleInstrumentPart() {
      }

      // $FF: synthetic method
      SimpleInstrumentPart(Object var1) {
         this();
      }
   }
}
