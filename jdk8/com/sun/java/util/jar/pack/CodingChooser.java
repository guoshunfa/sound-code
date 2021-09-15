package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class CodingChooser {
   int verbose;
   int effort;
   boolean optUseHistogram = true;
   boolean optUsePopulationCoding = true;
   boolean optUseAdaptiveCoding = true;
   boolean disablePopCoding;
   boolean disableRunCoding;
   boolean topLevel = true;
   double fuzz;
   Coding[] allCodingChoices;
   CodingChooser.Choice[] choices;
   ByteArrayOutputStream context;
   CodingChooser popHelper;
   CodingChooser runHelper;
   Random stress;
   private int[] values;
   private int start;
   private int end;
   private int[] deltas;
   private int min;
   private int max;
   private Histogram vHist;
   private Histogram dHist;
   private int searchOrder;
   private CodingChooser.Choice regularChoice;
   private CodingChooser.Choice bestChoice;
   private CodingMethod bestMethod;
   private int bestByteSize;
   private int bestZipSize;
   private int targetSize;
   public static final int MIN_EFFORT = 1;
   public static final int MID_EFFORT = 5;
   public static final int MAX_EFFORT = 9;
   public static final int POP_EFFORT = 4;
   public static final int RUN_EFFORT = 3;
   public static final int BYTE_SIZE = 0;
   public static final int ZIP_SIZE = 1;
   private CodingChooser.Sizer zipSizer = new CodingChooser.Sizer();
   private Deflater zipDef = new Deflater();
   private DeflaterOutputStream zipOut;
   private CodingChooser.Sizer byteSizer;
   private CodingChooser.Sizer byteOnlySizer;

   CodingChooser(int var1, Coding[] var2) {
      this.zipOut = new DeflaterOutputStream(this.zipSizer, this.zipDef);
      this.byteSizer = new CodingChooser.Sizer(this.zipOut);
      this.byteOnlySizer = new CodingChooser.Sizer();
      PropMap var3 = Utils.currentPropMap();
      int var4;
      if (var3 != null) {
         this.verbose = Math.max(var3.getInteger("com.sun.java.util.jar.pack.verbose"), var3.getInteger("com.sun.java.util.jar.pack.verbose.coding"));
         this.optUseHistogram = !var3.getBoolean("com.sun.java.util.jar.pack.no.histogram");
         this.optUsePopulationCoding = !var3.getBoolean("com.sun.java.util.jar.pack.no.population.coding");
         this.optUseAdaptiveCoding = !var3.getBoolean("com.sun.java.util.jar.pack.no.adaptive.coding");
         var4 = var3.getInteger("com.sun.java.util.jar.pack.stress.coding");
         if (var4 != 0) {
            this.stress = new Random((long)var4);
         }
      }

      this.effort = var1;
      this.allCodingChoices = var2;
      this.fuzz = 1.0D + 0.0025D * (double)(var1 - 5);
      var4 = 0;

      int var5;
      for(var5 = 0; var5 < var2.length; ++var5) {
         if (var2[var5] != null) {
            ++var4;
         }
      }

      this.choices = new CodingChooser.Choice[var4];
      var4 = 0;

      for(var5 = 0; var5 < var2.length; ++var5) {
         if (var2[var5] != null) {
            int[] var6 = new int[this.choices.length];
            this.choices[var4++] = new CodingChooser.Choice(var2[var5], var5, var6);
         }
      }

      for(var5 = 0; var5 < this.choices.length; ++var5) {
         Coding var10 = this.choices[var5].coding;

         assert var10.distanceFrom(var10) == 0;

         for(int var7 = 0; var7 < var5; ++var7) {
            Coding var8 = this.choices[var7].coding;
            int var9 = var10.distanceFrom(var8);

            assert var9 > 0;

            assert var9 == var8.distanceFrom(var10);

            this.choices[var5].distance[var7] = var9;
            this.choices[var7].distance[var5] = var9;
         }
      }

   }

   CodingChooser.Choice makeExtraChoice(Coding var1) {
      int[] var2 = new int[this.choices.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Coding var4 = this.choices[var3].coding;
         int var5 = var1.distanceFrom(var4);

         assert var5 > 0;

         assert var5 == var4.distanceFrom(var1);

         var2[var3] = var5;
      }

      CodingChooser.Choice var6 = new CodingChooser.Choice(var1, -1, var2);
      var6.reset();
      return var6;
   }

   ByteArrayOutputStream getContext() {
      if (this.context == null) {
         this.context = new ByteArrayOutputStream(65536);
      }

      return this.context;
   }

   private void reset(int[] var1, int var2, int var3) {
      this.values = var1;
      this.start = var2;
      this.end = var3;
      this.deltas = null;
      this.min = Integer.MAX_VALUE;
      this.max = Integer.MIN_VALUE;
      this.vHist = null;
      this.dHist = null;
      this.searchOrder = 0;
      this.regularChoice = null;
      this.bestChoice = null;
      this.bestMethod = null;
      this.bestZipSize = Integer.MAX_VALUE;
      this.bestByteSize = Integer.MAX_VALUE;
      this.targetSize = Integer.MAX_VALUE;
   }

   CodingMethod choose(int[] var1, int var2, int var3, Coding var4, int[] var5) {
      this.reset(var1, var2, var3);
      if (this.effort > 1 && var2 < var3) {
         if (this.optUseHistogram) {
            this.getValueHistogram();
            this.getDeltaHistogram();
         }

         int var7;
         int var18;
         for(var18 = var2; var18 < var3; ++var18) {
            var7 = var1[var18];
            if (this.min > var7) {
               this.min = var7;
            }

            if (this.max < var7) {
               this.max = var7;
            }
         }

         var18 = this.markUsableChoices(var4);
         int var9;
         if (this.stress != null) {
            var7 = this.stress.nextInt(var18 * 2 + 4);
            Object var8 = null;

            for(var9 = 0; var9 < this.choices.length; ++var9) {
               CodingChooser.Choice var20 = this.choices[var9];
               if (var20.searchOrder >= 0 && var7-- == 0) {
                  var8 = var20.coding;
                  break;
               }
            }

            if (var8 == null) {
               if ((var7 & 7) != 0) {
                  var8 = var4;
               } else {
                  var8 = this.stressCoding(this.min, this.max);
               }
            }

            if (!this.disablePopCoding && this.optUsePopulationCoding && this.effort >= 4) {
               var8 = this.stressPopCoding((CodingMethod)var8);
            }

            if (!this.disableRunCoding && this.optUseAdaptiveCoding && this.effort >= 3) {
               var8 = this.stressAdaptiveCoding((CodingMethod)var8);
            }

            return (CodingMethod)var8;
         } else {
            double var19 = 1.0D;

            for(var9 = this.effort; var9 < 9; ++var9) {
               var19 /= 1.414D;
            }

            var9 = (int)Math.ceil((double)var18 * var19);
            this.bestChoice = this.regularChoice;
            this.evaluate(this.regularChoice);
            int var10 = this.updateDistances(this.regularChoice);
            int var11 = this.bestZipSize;
            int var12 = this.bestByteSize;
            int var13;
            if (this.regularChoice.coding == var4 && this.topLevel) {
               var13 = BandStructure.encodeEscapeValue(115, var4);
               if (var4.canRepresentSigned(var13)) {
                  int var14 = var4.getLength(var13);
                  CodingChooser.Choice var10000 = this.regularChoice;
                  var10000.zipSize -= var14;
                  this.bestByteSize = this.regularChoice.byteSize;
                  this.bestZipSize = this.regularChoice.zipSize;
               }
            }

            var13 = 1;

            while(this.searchOrder < var9) {
               if (var13 > var10) {
                  var13 = 1;
               }

               int var15 = var10 / var13;
               int var16 = var10 / (var13 *= 2) + 1;
               CodingChooser.Choice var21 = this.findChoiceNear(this.bestChoice, var15, var16);
               if (var21 != null) {
                  assert var21.coding.canRepresent(this.min, this.max);

                  this.evaluate(var21);
                  int var17 = this.updateDistances(var21);
                  if (var21 == this.bestChoice) {
                     var10 = var17;
                     if (this.verbose > 5) {
                        Utils.log.info("maxd = " + var17);
                     }
                  }
               }
            }

            Coding var22 = this.bestChoice.coding;

            assert var22 == this.bestMethod;

            if (this.verbose > 2) {
               Utils.log.info("chooser: plain result=" + this.bestChoice + " after " + this.bestChoice.searchOrder + " rounds, " + (this.regularChoice.zipSize - this.bestZipSize) + " fewer bytes than regular " + var4);
            }

            this.bestChoice = null;
            if (!this.disablePopCoding && this.optUsePopulationCoding && this.effort >= 4 && this.bestMethod instanceof Coding) {
               this.tryPopulationCoding(var22);
            }

            if (!this.disableRunCoding && this.optUseAdaptiveCoding && this.effort >= 3 && this.bestMethod instanceof Coding) {
               this.tryAdaptiveCoding(var22);
            }

            if (var5 != null) {
               var5[0] = this.bestByteSize;
               var5[1] = this.bestZipSize;
            }

            if (this.verbose > 1) {
               Utils.log.info("chooser: result=" + this.bestMethod + " " + (var11 - this.bestZipSize) + " fewer bytes than regular " + var4 + "; win=" + pct((double)(var11 - this.bestZipSize), (double)var11));
            }

            CodingMethod var23 = this.bestMethod;
            this.reset((int[])null, 0, 0);
            return var23;
         }
      } else {
         if (var5 != null) {
            int[] var6 = this.computeSizePrivate(var4);
            var5[0] = var6[0];
            var5[1] = var6[1];
         }

         return var4;
      }
   }

   CodingMethod choose(int[] var1, int var2, int var3, Coding var4) {
      return this.choose(var1, var2, var3, var4, (int[])null);
   }

   CodingMethod choose(int[] var1, Coding var2, int[] var3) {
      return this.choose(var1, 0, var1.length, var2, var3);
   }

   CodingMethod choose(int[] var1, Coding var2) {
      return this.choose(var1, 0, var1.length, var2, (int[])null);
   }

   private int markUsableChoices(Coding var1) {
      int var2 = 0;

      int var3;
      CodingChooser.Choice var4;
      for(var3 = 0; var3 < this.choices.length; ++var3) {
         var4 = this.choices[var3];
         var4.reset();
         if (!var4.coding.canRepresent(this.min, this.max)) {
            var4.searchOrder = -1;
            if (this.verbose > 1 && var4.coding == var1) {
               Utils.log.info("regular coding cannot represent [" + this.min + ".." + this.max + "]: " + var1);
            }
         } else {
            if (var4.coding == var1) {
               this.regularChoice = var4;
            }

            ++var2;
         }
      }

      if (this.regularChoice == null && var1.canRepresent(this.min, this.max)) {
         this.regularChoice = this.makeExtraChoice(var1);
         if (this.verbose > 1) {
            Utils.log.info("*** regular choice is extra: " + this.regularChoice.coding);
         }
      }

      if (this.regularChoice == null) {
         for(var3 = 0; var3 < this.choices.length; ++var3) {
            var4 = this.choices[var3];
            if (var4.searchOrder != -1) {
               this.regularChoice = var4;
               break;
            }
         }

         if (this.verbose > 1) {
            Utils.log.info("*** regular choice does not apply " + var1);
            Utils.log.info("    using instead " + this.regularChoice.coding);
         }
      }

      if (this.verbose > 2) {
         Utils.log.info("chooser: #choices=" + var2 + " [" + this.min + ".." + this.max + "]");
         if (this.verbose > 4) {
            for(var3 = 0; var3 < this.choices.length; ++var3) {
               var4 = this.choices[var3];
               if (var4.searchOrder >= 0) {
                  Utils.log.info("  " + var4);
               }
            }
         }
      }

      return var2;
   }

   private CodingChooser.Choice findChoiceNear(CodingChooser.Choice var1, int var2, int var3) {
      if (this.verbose > 5) {
         Utils.log.info("findChoice " + var2 + ".." + var3 + " near: " + var1);
      }

      int[] var4 = var1.distance;
      CodingChooser.Choice var5 = null;

      for(int var6 = 0; var6 < this.choices.length; ++var6) {
         CodingChooser.Choice var7 = this.choices[var6];
         if (var7.searchOrder >= this.searchOrder && var4[var6] >= var3 && var4[var6] <= var2) {
            if (var7.minDistance >= var3 && var7.minDistance <= var2) {
               if (this.verbose > 5) {
                  Utils.log.info("findChoice => good " + var7);
               }

               return var7;
            }

            var5 = var7;
         }
      }

      if (this.verbose > 5) {
         Utils.log.info("findChoice => found " + var5);
      }

      return var5;
   }

   private void evaluate(CodingChooser.Choice var1) {
      assert var1.searchOrder == Integer.MAX_VALUE;

      var1.searchOrder = this.searchOrder++;
      boolean var2;
      if (var1 != this.bestChoice && !var1.isExtra()) {
         if (this.optUseHistogram) {
            Histogram var3 = this.getHistogram(var1.coding.isDelta());
            var1.histSize = (int)Math.ceil(var3.getBitLength(var1.coding) / 8.0D);
            var1.byteSize = var1.histSize;
            var2 = var1.byteSize <= this.targetSize;
         } else {
            var2 = true;
         }
      } else {
         var2 = true;
      }

      if (var2) {
         int[] var4 = this.computeSizePrivate(var1.coding);
         var1.byteSize = var4[0];
         var1.zipSize = var4[1];
         if (this.noteSizes(var1.coding, var1.byteSize, var1.zipSize)) {
            this.bestChoice = var1;
         }
      }

      assert var1.histSize < 0 || var1.byteSize == var1.histSize;

      if (this.verbose > 4) {
         Utils.log.info("evaluated " + var1);
      }

   }

   private boolean noteSizes(CodingMethod var1, int var2, int var3) {
      assert var3 > 0 && var2 > 0;

      boolean var4 = var3 < this.bestZipSize;
      if (this.verbose > 3) {
         Utils.log.info("computed size " + var1 + " " + var2 + "/zs=" + var3 + (var4 && this.bestMethod != null ? " better by " + pct((double)(this.bestZipSize - var3), (double)var3) : ""));
      }

      if (var4) {
         this.bestMethod = var1;
         this.bestZipSize = var3;
         this.bestByteSize = var2;
         this.targetSize = (int)((double)var2 * this.fuzz);
         return true;
      } else {
         return false;
      }
   }

   private int updateDistances(CodingChooser.Choice var1) {
      int[] var2 = var1.distance;
      int var3 = 0;

      for(int var4 = 0; var4 < this.choices.length; ++var4) {
         CodingChooser.Choice var5 = this.choices[var4];
         if (var5.searchOrder >= this.searchOrder) {
            int var6 = var2[var4];
            if (this.verbose > 5) {
               Utils.log.info("evaluate dist " + var6 + " to " + var5);
            }

            int var7 = var5.minDistance;
            if (var7 > var6) {
               var5.minDistance = var6;
            }

            if (var3 < var6) {
               var3 = var6;
            }
         }
      }

      if (this.verbose > 5) {
         Utils.log.info("evaluate maxd => " + var3);
      }

      return var3;
   }

   public void computeSize(CodingMethod var1, int[] var2, int var3, int var4, int[] var5) {
      if (var4 <= var3) {
         var5[0] = var5[1] = 0;
      } else {
         try {
            this.resetData();
            var1.writeArrayTo(this.byteSizer, var2, var3, var4);
            var5[0] = this.getByteSize();
            var5[1] = this.getZipSize();
         } catch (IOException var7) {
            throw new RuntimeException(var7);
         }
      }
   }

   public void computeSize(CodingMethod var1, int[] var2, int[] var3) {
      this.computeSize(var1, var2, 0, var2.length, var3);
   }

   public int[] computeSize(CodingMethod var1, int[] var2, int var3, int var4) {
      int[] var5 = new int[]{0, 0};
      this.computeSize(var1, var2, var3, var4, var5);
      return var5;
   }

   public int[] computeSize(CodingMethod var1, int[] var2) {
      return this.computeSize(var1, var2, 0, var2.length);
   }

   private int[] computeSizePrivate(CodingMethod var1) {
      int[] var2 = new int[]{0, 0};
      this.computeSize(var1, this.values, this.start, this.end, var2);
      return var2;
   }

   public int computeByteSize(CodingMethod var1, int[] var2, int var3, int var4) {
      int var5 = var4 - var3;
      if (var5 < 0) {
         return 0;
      } else if (var1 instanceof Coding) {
         Coding var6 = (Coding)var1;
         int var7 = var6.getLength(var2, var3, var4);
         int var8;

         assert var7 == (var8 = this.countBytesToSizer(var1, var2, var3, var4)) : var1 + " : " + var7 + " != " + var8;

         return var7;
      } else {
         return this.countBytesToSizer(var1, var2, var3, var4);
      }
   }

   private int countBytesToSizer(CodingMethod var1, int[] var2, int var3, int var4) {
      try {
         this.byteOnlySizer.reset();
         var1.writeArrayTo(this.byteOnlySizer, var2, var3, var4);
         return this.byteOnlySizer.getSize();
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      }
   }

   int[] getDeltas(int var1, int var2) {
      if ((var1 | var2) != 0) {
         return Coding.makeDeltas(this.values, this.start, this.end, var1, var2);
      } else {
         if (this.deltas == null) {
            this.deltas = Coding.makeDeltas(this.values, this.start, this.end, 0, 0);
         }

         return this.deltas;
      }
   }

   Histogram getValueHistogram() {
      if (this.vHist == null) {
         this.vHist = new Histogram(this.values, this.start, this.end);
         if (this.verbose > 3) {
            this.vHist.print("vHist", System.out);
         } else if (this.verbose > 1) {
            this.vHist.print("vHist", (String[])null, System.out);
         }
      }

      return this.vHist;
   }

   Histogram getDeltaHistogram() {
      if (this.dHist == null) {
         this.dHist = new Histogram(this.getDeltas(0, 0));
         if (this.verbose > 3) {
            this.dHist.print("dHist", System.out);
         } else if (this.verbose > 1) {
            this.dHist.print("dHist", (String[])null, System.out);
         }
      }

      return this.dHist;
   }

   Histogram getHistogram(boolean var1) {
      return var1 ? this.getDeltaHistogram() : this.getValueHistogram();
   }

   private void tryPopulationCoding(Coding var1) {
      Histogram var2 = this.getValueHistogram();
      Coding var4 = var1.getValueCoding();
      Coding var5 = BandStructure.UNSIGNED5.setL(64);
      Coding var6 = var1.getValueCoding();
      int var8 = 4 + Math.max(var4.getLength(this.min), var4.getLength(this.max));
      int var11 = var5.getLength(0);
      int var9 = var11 * (this.end - this.start);
      int var10 = (int)Math.ceil(var2.getBitLength(var6) / 8.0D);
      int var12 = var8 + var9 + var10;
      int var13 = 0;
      int[] var14 = new int[1 + var2.getTotalLength()];
      int var15 = -1;
      int var16 = -1;
      int[][] var17 = var2.getMatrix();
      int var18 = -1;
      int var19 = 1;
      int var20 = 0;

      int var21;
      for(var21 = 1; var21 <= var2.getTotalLength(); ++var21) {
         if (var19 == 1) {
            ++var18;
            var20 = var17[var18][0];
            var19 = var17[var18].length;
         }

         int[] var10000 = var17[var18];
         --var19;
         int var22 = var10000[var19];
         var14[var21] = var22;
         int var23 = var4.getLength(var22);
         var8 += var23;
         var9 += (var5.getLength(var21) - var11) * var20;
         var10 -= var23 * var20;
         int var26 = var8 + var9 + var10;
         if (var12 > var26) {
            if (var26 <= this.targetSize) {
               var16 = var21;
               if (var15 < 0) {
                  var15 = var21;
               }

               if (this.verbose > 4) {
                  Utils.log.info("better pop-size at fvc=" + var21 + " by " + pct((double)(var12 - var26), (double)var12));
               }
            }

            var12 = var26;
            var13 = var21;
         }
      }

      if (var15 < 0) {
         if (this.verbose > 1 && this.verbose > 1) {
            Utils.log.info("no good pop-size; best was " + var12 + " at " + var13 + " worse by " + pct((double)(var12 - this.bestByteSize), (double)this.bestByteSize));
         }

      } else {
         if (this.verbose > 1) {
            Utils.log.info("initial best pop-size at fvc=" + var13 + " in [" + var15 + ".." + var16 + "] by " + pct((double)(this.bestByteSize - var12), (double)this.bestByteSize));
         }

         var21 = this.bestZipSize;
         int[] var36 = PopulationCoding.LValuesCoded;
         ArrayList var37 = new ArrayList();
         ArrayList var24 = new ArrayList();
         ArrayList var25 = new ArrayList();
         Iterator var41;
         Coding var42;
         if (var13 <= 255) {
            var37.add(BandStructure.BYTE1);
         } else {
            int var27 = 5;
            boolean var28 = this.effort > 4;
            if (var28) {
               var24.add(BandStructure.BYTE1.setS(1));
            }

            for(int var29 = var36.length - 1; var29 >= 1; --var29) {
               int var30 = var36[var29];
               Coding var31 = PopulationCoding.fitTokenCoding(var15, var30);
               Coding var32 = PopulationCoding.fitTokenCoding(var13, var30);
               Coding var33 = PopulationCoding.fitTokenCoding(var16, var30);
               if (var32 != null) {
                  if (!var37.contains(var32)) {
                     var37.add(var32);
                  }

                  if (var27 > var32.B()) {
                     var27 = var32.B();
                  }
               }

               if (var28) {
                  if (var33 == null) {
                     var33 = var32;
                  }

                  for(int var34 = var31.B(); var34 <= var33.B(); ++var34) {
                     if (var34 != var32.B() && var34 != 1) {
                        Coding var35 = var33.setB(var34).setS(1);
                        if (!var24.contains(var35)) {
                           var24.add(var35);
                        }
                     }
                  }
               }
            }

            var41 = var37.iterator();

            while(var41.hasNext()) {
               var42 = (Coding)var41.next();
               if (var42.B() > var27) {
                  var41.remove();
                  var25.add(0, var42);
               }
            }
         }

         ArrayList var38 = new ArrayList();
         Iterator var39 = var37.iterator();
         var41 = var24.iterator();
         Iterator var43 = var25.iterator();

         while(var39.hasNext() || var41.hasNext() || var43.hasNext()) {
            if (var39.hasNext()) {
               var38.add(var39.next());
            }

            if (var41.hasNext()) {
               var38.add(var41.next());
            }

            if (var43.hasNext()) {
               var38.add(var43.next());
            }
         }

         var37.clear();
         var24.clear();
         var25.clear();
         int var40 = var38.size();
         if (this.effort == 4) {
            var40 = 2;
         } else if (var40 > 4) {
            var40 -= 4;
            var40 = var40 * (this.effort - 4) / 5;
            var40 += 4;
         }

         if (var38.size() > var40) {
            if (this.verbose > 4) {
               Utils.log.info("allFits before clip: " + var38);
            }

            var38.subList(var40, var38.size()).clear();
         }

         if (this.verbose > 3) {
            Utils.log.info("allFits: " + var38);
         }

         var41 = var38.iterator();

         while(true) {
            int var45;
            do {
               if (!var41.hasNext()) {
                  if (this.verbose > 3) {
                     Utils.log.info("measured best pop, size=" + this.bestByteSize + "/zs=" + this.bestZipSize + " better by " + pct((double)(var21 - this.bestZipSize), (double)var21));
                     if (this.bestZipSize < var21) {
                        Utils.log.info(">>> POP WINS BY " + (var21 - this.bestZipSize));
                     }
                  }

                  return;
               }

               var42 = (Coding)var41.next();
               boolean var44 = false;
               if (var42.S() == 1) {
                  var44 = true;
                  var42 = var42.setS(0);
               }

               if (!var44) {
                  var45 = var13;

                  assert var42.umax() >= var13;

                  assert var42.B() == 1 || var42.setB(var42.B() - 1).umax() < var13;
                  break;
               }

               var45 = Math.min(var42.umax(), var16);
            } while(var45 < var15 || var45 == var13);

            PopulationCoding var46 = new PopulationCoding();
            var46.setHistogram(var2);
            var46.setL(var42.L());
            var46.setFavoredValues(var14, var45);

            assert var46.tokenCoding == var42;

            var46.resortFavoredValues();
            int[] var47 = this.computePopSizePrivate(var46, var4, var6);
            this.noteSizes(var46, var47[0], 4 + var47[1]);
         }
      }
   }

   private int[] computePopSizePrivate(PopulationCoding var1, Coding var2, Coding var3) {
      if (this.popHelper == null) {
         this.popHelper = new CodingChooser(this.effort, this.allCodingChoices);
         if (this.stress != null) {
            this.popHelper.addStressSeed(this.stress.nextInt());
         }

         this.popHelper.topLevel = false;
         --this.popHelper.verbose;
         this.popHelper.disablePopCoding = true;
         this.popHelper.disableRunCoding = this.disableRunCoding;
         if (this.effort < 5) {
            this.popHelper.disableRunCoding = true;
         }
      }

      int var4 = var1.fVlen;
      if (this.verbose > 2) {
         Utils.log.info("computePopSizePrivate fvlen=" + var4 + " tc=" + var1.tokenCoding);
         Utils.log.info("{ //BEGIN");
      }

      int[] var5 = var1.fValues;
      int[][] var6 = var1.encodeValues(this.values, this.start, this.end);
      int[] var7 = var6[0];
      int[] var8 = var6[1];
      if (this.verbose > 2) {
         Utils.log.info("-- refine on fv[" + var4 + "] fc=" + var2);
      }

      var1.setFavoredCoding(this.popHelper.choose(var5, 1, 1 + var4, var2));
      if (var1.tokenCoding instanceof Coding && (this.stress == null || this.stress.nextBoolean())) {
         if (this.verbose > 2) {
            Utils.log.info("-- refine on tv[" + var7.length + "] tc=" + var1.tokenCoding);
         }

         CodingMethod var9 = this.popHelper.choose(var7, (Coding)var1.tokenCoding);
         if (var9 != var1.tokenCoding) {
            if (this.verbose > 2) {
               Utils.log.info(">>> refined tc=" + var9);
            }

            var1.setTokenCoding(var9);
         }
      }

      if (var8.length == 0) {
         var1.setUnfavoredCoding((CodingMethod)null);
      } else {
         if (this.verbose > 2) {
            Utils.log.info("-- refine on uv[" + var8.length + "] uc=" + var1.unfavoredCoding);
         }

         var1.setUnfavoredCoding(this.popHelper.choose(var8, var3));
      }

      if (this.verbose > 3) {
         Utils.log.info("finish computePopSizePrivate fvlen=" + var4 + " fc=" + var1.favoredCoding + " tc=" + var1.tokenCoding + " uc=" + var1.unfavoredCoding);
         StringBuilder var12 = new StringBuilder();
         var12.append("fv = {");

         for(int var10 = 1; var10 <= var4; ++var10) {
            if (var10 % 10 == 0) {
               var12.append('\n');
            }

            var12.append(" ").append(var5[var10]);
         }

         var12.append('\n');
         var12.append("}");
         Utils.log.info(var12.toString());
      }

      if (this.verbose > 2) {
         Utils.log.info("} //END");
      }

      if (this.stress != null) {
         return null;
      } else {
         int[] var13;
         try {
            this.resetData();
            var1.writeSequencesTo(this.byteSizer, var7, var8);
            var13 = new int[]{this.getByteSize(), this.getZipSize()};
         } catch (IOException var11) {
            throw new RuntimeException(var11);
         }

         int[] var14 = null;

         assert (var14 = this.computeSizePrivate(var1)) != null;

         assert var14[0] == var13[0] : var14[0] + " != " + var13[0];

         return var13;
      }
   }

   private void tryAdaptiveCoding(Coding var1) {
      int var2 = this.bestZipSize;
      int var3 = this.start;
      int var4 = this.end;
      int[] var5 = this.values;
      int var6 = var4 - var3;
      if (var1.isDelta()) {
         var5 = this.getDeltas(0, 0);
         var3 = 0;
         var4 = var5.length;
      }

      int[] var7 = new int[var6 + 1];
      int var8 = 0;
      int var9 = 0;

      for(int var10 = var3; var10 < var4; ++var10) {
         int var11 = var5[var10];
         var7[var8++] = var9;
         int var12 = var1.getLength(var11);

         assert var12 < Integer.MAX_VALUE;

         var9 += var12;
      }

      var7[var8++] = var9;

      assert var8 == var7.length;

      double var42 = (double)var9 / (double)var6;
      double var43;
      if (this.effort >= 5) {
         if (this.effort > 6) {
            var43 = 1.001D;
         } else {
            var43 = 1.003D;
         }
      } else if (this.effort > 3) {
         var43 = 1.01D;
      } else {
         var43 = 1.03D;
      }

      var43 *= var43;
      double var14 = var43 * var43;
      double var16 = var43 * var43 * var43;
      double[] var18 = new double[1 + (this.effort - 3)];
      double var19 = Math.log((double)var6);

      for(int var21 = 0; var21 < var18.length; ++var21) {
         var18[var21] = Math.exp(var19 * (double)(var21 + 1) / (double)(var18.length + 1));
      }

      int[] var44 = new int[var18.length];
      int var22 = 0;

      for(int var23 = 0; var23 < var18.length; ++var23) {
         int var24 = (int)Math.round(var18[var23]);
         var24 = AdaptiveCoding.getNextK(var24 - 1);
         if (var24 > 0 && var24 < var6 && (var22 <= 0 || var24 != var44[var22 - 1])) {
            var44[var22++] = var24;
         }
      }

      var44 = BandStructure.realloc(var44, var22);
      int[] var45 = new int[var44.length];
      double[] var25 = new double[var44.length];

      int var26;
      int var27;
      for(var26 = 0; var26 < var44.length; ++var26) {
         var27 = var44[var26];
         double var28;
         if (var27 < 10) {
            var28 = var16;
         } else if (var27 < 100) {
            var28 = var14;
         } else {
            var28 = var43;
         }

         var25[var26] = var28;
         var45[var26] = 4 + (int)Math.ceil((double)var27 * var42 * var28);
      }

      if (this.verbose > 1) {
         System.out.print("tryAdaptiveCoding [" + var6 + "] avgS=" + var42 + " fuzz=" + var43 + " meshes: {");

         for(var26 = 0; var26 < var44.length; ++var26) {
            System.out.print(" " + var44[var26] + "(" + var45[var26] + ")");
         }

         Utils.log.info(" }");
      }

      if (this.runHelper == null) {
         this.runHelper = new CodingChooser(this.effort, this.allCodingChoices);
         if (this.stress != null) {
            this.runHelper.addStressSeed(this.stress.nextInt());
         }

         this.runHelper.topLevel = false;
         --this.runHelper.verbose;
         this.runHelper.disableRunCoding = true;
         this.runHelper.disablePopCoding = this.disablePopCoding;
         if (this.effort < 5) {
            this.runHelper.disablePopCoding = true;
         }
      }

      for(var26 = 0; var26 < var6; ++var26) {
         var26 = AdaptiveCoding.getNextK(var26 - 1);
         if (var26 > var6) {
            var26 = var6;
         }

         for(var27 = var44.length - 1; var27 >= 0; --var27) {
            int var46 = var44[var27];
            int var29 = var45[var27];
            if (var26 + var46 <= var6) {
               int var30 = var7[var26 + var46] - var7[var26];
               if (var30 >= var29) {
                  int var31 = var26 + var46;
                  int var32 = var30;
                  double var33 = var42 * var25[var27];

                  while(var31 < var6 && var31 - var26 <= var6 / 2) {
                     int var35 = var31;
                     int var36 = var32;
                     var31 += var46;
                     var31 = var26 + AdaptiveCoding.getNextK(var31 - var26 - 1);
                     if (var31 < 0 || var31 > var6) {
                        var31 = var6;
                     }

                     var32 = var7[var31] - var7[var26];
                     if ((double)var32 < 4.0D + (double)(var31 - var26) * var33) {
                        var32 = var36;
                        var31 = var35;
                        break;
                     }
                  }

                  if (this.verbose > 2) {
                     Utils.log.info("bulge at " + var26 + "[" + (var31 - var26) + "] of " + pct((double)var32 - var42 * (double)(var31 - var26), var42 * (double)(var31 - var26)));
                     Utils.log.info("{ //BEGIN");
                  }

                  CodingMethod var37 = this.runHelper.choose(this.values, this.start + var26, this.start + var31, var1);
                  Object var38;
                  Object var47;
                  if (var37 == var1) {
                     var47 = var1;
                     var38 = var1;
                  } else {
                     var47 = this.runHelper.choose(this.values, this.start, this.start + var26, var1);
                     var38 = this.runHelper.choose(this.values, this.start + var31, this.start + var6, var1);
                  }

                  if (this.verbose > 2) {
                     Utils.log.info("} //END");
                  }

                  if (var47 == var37 && var26 > 0 && AdaptiveCoding.isCodableLength(var31)) {
                     var26 = 0;
                  }

                  if (var37 == var38 && var31 < var6) {
                     var31 = var6;
                  }

                  if (var47 != var1 || var37 != var1 || var38 != var1) {
                     int var40 = 0;
                     Object var39;
                     if (var31 == var6) {
                        var39 = var37;
                     } else {
                        var39 = new AdaptiveCoding(var31 - var26, var37, (CodingMethod)var38);
                        var40 += 4;
                     }

                     if (var26 > 0) {
                        var39 = new AdaptiveCoding(var26, (CodingMethod)var47, (CodingMethod)var39);
                        var40 += 4;
                     }

                     int[] var41 = this.computeSizePrivate((CodingMethod)var39);
                     this.noteSizes((CodingMethod)var39, var41[0], var41[1] + var40);
                  }

                  var26 = var31;
                  break;
               }
            }
         }
      }

      if (this.verbose > 3 && this.bestZipSize < var2) {
         Utils.log.info(">>> RUN WINS BY " + (var2 - this.bestZipSize));
      }

   }

   private static String pct(double var0, double var2) {
      return (double)Math.round(var0 / var2 * 10000.0D) / 100.0D + "%";
   }

   private void resetData() {
      this.flushData();
      this.zipDef.reset();
      if (this.context != null) {
         try {
            this.context.writeTo(this.byteSizer);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      }

      this.zipSizer.reset();
      this.byteSizer.reset();
   }

   private void flushData() {
      try {
         this.zipOut.finish();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   private int getByteSize() {
      return this.byteSizer.getSize();
   }

   private int getZipSize() {
      this.flushData();
      return this.zipSizer.getSize();
   }

   void addStressSeed(int var1) {
      if (this.stress != null) {
         this.stress.setSeed((long)var1 + ((long)this.stress.nextInt() << 32));
      }
   }

   private CodingMethod stressPopCoding(CodingMethod var1) {
      assert this.stress != null;

      if (!(var1 instanceof Coding)) {
         return var1;
      } else {
         Coding var2 = ((Coding)var1).getValueCoding();
         Histogram var3 = this.getValueHistogram();
         int var4 = this.stressLen(var3.getTotalLength());
         if (var4 == 0) {
            return var1;
         } else {
            ArrayList var5 = new ArrayList();
            int var7;
            if (this.stress.nextBoolean()) {
               HashSet var6 = new HashSet();

               for(var7 = this.start; var7 < this.end; ++var7) {
                  if (var6.add(this.values[var7])) {
                     var5.add(this.values[var7]);
                  }
               }
            } else {
               int[][] var14 = var3.getMatrix();

               for(var7 = 0; var7 < var14.length; ++var7) {
                  int[] var8 = var14[var7];

                  for(int var9 = 1; var9 < var8.length; ++var9) {
                     var5.add(var8[var9]);
                  }
               }
            }

            int var15 = this.stress.nextInt();
            if ((var15 & 7) <= 2) {
               Collections.shuffle(var5, this.stress);
            } else {
               if (((var15 >>>= 3) & 7) <= 2) {
                  Collections.sort(var5);
               }

               if (((var15 >>>= 3) & 7) <= 2) {
                  Collections.reverse(var5);
               }

               if (((var15 >>>= 3) & 7) <= 2) {
                  Collections.rotate(var5, this.stressLen(var5.size()));
               }
            }

            if (var5.size() > var4) {
               if (((var15 >>>= 3) & 7) <= 2) {
                  var5.subList(var4, var5.size()).clear();
               } else {
                  var5.subList(0, var5.size() - var4).clear();
               }
            }

            var4 = var5.size();
            int[] var16 = new int[1 + var4];

            for(int var17 = 0; var17 < var4; ++var17) {
               var16[1 + var17] = (Integer)var5.get(var17);
            }

            PopulationCoding var18 = new PopulationCoding();
            var18.setFavoredValues(var16, var4);
            int[] var19 = PopulationCoding.LValuesCoded;

            int var10;
            int var11;
            for(var10 = 0; var10 < var19.length / 2; ++var10) {
               var11 = var19[this.stress.nextInt(var19.length)];
               if (var11 >= 0 && PopulationCoding.fitTokenCoding(var4, var11) != null) {
                  var18.setL(var11);
                  break;
               }
            }

            if (var18.tokenCoding == null) {
               var10 = var16[1];
               var11 = var10;

               for(int var12 = 2; var12 <= var4; ++var12) {
                  int var13 = var16[var12];
                  if (var10 > var13) {
                     var10 = var13;
                  }

                  if (var11 < var13) {
                     var11 = var13;
                  }
               }

               var18.tokenCoding = this.stressCoding(var10, var11);
            }

            this.computePopSizePrivate(var18, var2, var2);
            return var18;
         }
      }
   }

   private CodingMethod stressAdaptiveCoding(CodingMethod var1) {
      assert this.stress != null;

      if (!(var1 instanceof Coding)) {
         return var1;
      } else {
         Coding var2 = (Coding)var1;
         int var3 = this.end - this.start;
         if (var3 < 2) {
            return var1;
         } else {
            int var4 = this.stressLen(var3 - 1) + 1;
            if (var4 == var3) {
               return var1;
            } else {
               try {
                  assert !this.disableRunCoding;

                  this.disableRunCoding = true;
                  int[] var5 = (int[])this.values.clone();
                  Object var6 = null;
                  int var7 = this.end;

                  int var9;
                  for(int var8 = this.start; var7 > var8; var7 = var9) {
                     int var11 = var7 - var8 < 100 ? -1 : this.stress.nextInt();
                     int var10;
                     if ((var11 & 7) != 0) {
                        var10 = var4 == 1 ? var4 : this.stressLen(var4 - 1) + 1;
                     } else {
                        int var12 = (var11 >>>= 3) & 3;
                        int var13 = (var11 >>>= 3) & 255;

                        while(true) {
                           var10 = AdaptiveCoding.decodeK(var12, var13);
                           if (var10 <= var7 - var8) {
                              assert AdaptiveCoding.isCodableLength(var10);
                              break;
                           }

                           if (var13 != 3) {
                              var13 = 3;
                           } else {
                              --var12;
                           }
                        }
                     }

                     if (var10 > var7 - var8) {
                        var10 = var7 - var8;
                     }

                     while(!AdaptiveCoding.isCodableLength(var10)) {
                        --var10;
                     }

                     var9 = var7 - var10;

                     assert var9 < var7;

                     assert var9 >= var8;

                     CodingMethod var18 = this.choose(var5, var9, var7, var2);
                     if (var6 == null) {
                        var6 = var18;
                     } else {
                        var6 = new AdaptiveCoding(var7 - var9, var18, (CodingMethod)var6);
                     }
                  }

                  Object var17 = var6;
                  return (CodingMethod)var17;
               } finally {
                  this.disableRunCoding = false;
               }
            }
         }
      }
   }

   private Coding stressCoding(int var1, int var2) {
      assert this.stress != null;

      for(int var3 = 0; var3 < 100; ++var3) {
         Coding var4 = Coding.of(this.stress.nextInt(5) + 1, this.stress.nextInt(256) + 1, this.stress.nextInt(3));
         if (var4.B() == 1) {
            var4 = var4.setH(256);
         }

         if (var4.H() == 256 && var4.B() >= 5) {
            var4 = var4.setB(4);
         }

         if (this.stress.nextBoolean()) {
            Coding var5 = var4.setD(1);
            if (var5.canRepresent(var1, var2)) {
               return var5;
            }
         }

         if (var4.canRepresent(var1, var2)) {
            return var4;
         }
      }

      return BandStructure.UNSIGNED5;
   }

   private int stressLen(int var1) {
      assert this.stress != null;

      assert var1 >= 0;

      int var2 = this.stress.nextInt(100);
      if (var2 < 20) {
         return Math.min(var1 / 5, var2);
      } else {
         return var2 < 40 ? var1 : this.stress.nextInt(var1);
      }
   }

   static class Sizer extends OutputStream {
      final OutputStream out;
      private int count;

      Sizer(OutputStream var1) {
         this.out = var1;
      }

      Sizer() {
         this((OutputStream)null);
      }

      public void write(int var1) throws IOException {
         ++this.count;
         if (this.out != null) {
            this.out.write(var1);
         }

      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.count += var3;
         if (this.out != null) {
            this.out.write(var1, var2, var3);
         }

      }

      public void reset() {
         this.count = 0;
      }

      public int getSize() {
         return this.count;
      }

      public String toString() {
         String var1 = super.toString();

         assert (var1 = this.stringForDebug()) != null;

         return var1;
      }

      String stringForDebug() {
         return "<Sizer " + this.getSize() + ">";
      }
   }

   static class Choice {
      final Coding coding;
      final int index;
      final int[] distance;
      int searchOrder;
      int minDistance;
      int zipSize;
      int byteSize;
      int histSize;

      Choice(Coding var1, int var2, int[] var3) {
         this.coding = var1;
         this.index = var2;
         this.distance = var3;
      }

      void reset() {
         this.searchOrder = Integer.MAX_VALUE;
         this.minDistance = Integer.MAX_VALUE;
         this.zipSize = this.byteSize = this.histSize = -1;
      }

      boolean isExtra() {
         return this.index < 0;
      }

      public String toString() {
         return this.stringForDebug();
      }

      private String stringForDebug() {
         String var1 = "";
         if (this.searchOrder < Integer.MAX_VALUE) {
            var1 = var1 + " so: " + this.searchOrder;
         }

         if (this.minDistance < Integer.MAX_VALUE) {
            var1 = var1 + " md: " + this.minDistance;
         }

         if (this.zipSize > 0) {
            var1 = var1 + " zs: " + this.zipSize;
         }

         if (this.byteSize > 0) {
            var1 = var1 + " bs: " + this.byteSize;
         }

         if (this.histSize > 0) {
            var1 = var1 + " hs: " + this.histSize;
         }

         return "Choice[" + this.index + "] " + var1 + " " + this.coding;
      }
   }
}
