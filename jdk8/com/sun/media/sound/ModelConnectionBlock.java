package com.sun.media.sound;

import java.util.Arrays;

public final class ModelConnectionBlock {
   private static final ModelSource[] no_sources = new ModelSource[0];
   private ModelSource[] sources;
   private double scale;
   private ModelDestination destination;

   public ModelConnectionBlock() {
      this.sources = no_sources;
      this.scale = 1.0D;
   }

   public ModelConnectionBlock(double var1, ModelDestination var3) {
      this.sources = no_sources;
      this.scale = 1.0D;
      this.scale = var1;
      this.destination = var3;
   }

   public ModelConnectionBlock(ModelSource var1, ModelDestination var2) {
      this.sources = no_sources;
      this.scale = 1.0D;
      if (var1 != null) {
         this.sources = new ModelSource[1];
         this.sources[0] = var1;
      }

      this.destination = var2;
   }

   public ModelConnectionBlock(ModelSource var1, double var2, ModelDestination var4) {
      this.sources = no_sources;
      this.scale = 1.0D;
      if (var1 != null) {
         this.sources = new ModelSource[1];
         this.sources[0] = var1;
      }

      this.scale = var2;
      this.destination = var4;
   }

   public ModelConnectionBlock(ModelSource var1, ModelSource var2, ModelDestination var3) {
      this.sources = no_sources;
      this.scale = 1.0D;
      if (var1 != null) {
         if (var2 == null) {
            this.sources = new ModelSource[1];
            this.sources[0] = var1;
         } else {
            this.sources = new ModelSource[2];
            this.sources[0] = var1;
            this.sources[1] = var2;
         }
      }

      this.destination = var3;
   }

   public ModelConnectionBlock(ModelSource var1, ModelSource var2, double var3, ModelDestination var5) {
      this.sources = no_sources;
      this.scale = 1.0D;
      if (var1 != null) {
         if (var2 == null) {
            this.sources = new ModelSource[1];
            this.sources[0] = var1;
         } else {
            this.sources = new ModelSource[2];
            this.sources[0] = var1;
            this.sources[1] = var2;
         }
      }

      this.scale = var3;
      this.destination = var5;
   }

   public ModelDestination getDestination() {
      return this.destination;
   }

   public void setDestination(ModelDestination var1) {
      this.destination = var1;
   }

   public double getScale() {
      return this.scale;
   }

   public void setScale(double var1) {
      this.scale = var1;
   }

   public ModelSource[] getSources() {
      return (ModelSource[])Arrays.copyOf((Object[])this.sources, this.sources.length);
   }

   public void setSources(ModelSource[] var1) {
      this.sources = var1 == null ? no_sources : (ModelSource[])Arrays.copyOf((Object[])var1, var1.length);
   }

   public void addSource(ModelSource var1) {
      ModelSource[] var2 = this.sources;
      this.sources = new ModelSource[var2.length + 1];
      System.arraycopy(var2, 0, this.sources, 0, var2.length);
      this.sources[this.sources.length - 1] = var1;
   }
}
