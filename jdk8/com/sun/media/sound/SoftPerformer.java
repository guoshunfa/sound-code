package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SoftPerformer {
   static ModelConnectionBlock[] defaultconnections = new ModelConnectionBlock[42];
   public int keyFrom = 0;
   public int keyTo = 127;
   public int velFrom = 0;
   public int velTo = 127;
   public int exclusiveClass = 0;
   public boolean selfNonExclusive = false;
   public boolean forcedVelocity = false;
   public boolean forcedKeynumber = false;
   public ModelPerformer performer;
   public ModelConnectionBlock[] connections;
   public ModelOscillator[] oscillators;
   public Map<Integer, int[]> midi_rpn_connections = new HashMap();
   public Map<Integer, int[]> midi_nrpn_connections = new HashMap();
   public int[][] midi_ctrl_connections;
   public int[][] midi_connections;
   public int[] ctrl_connections;
   private List<Integer> ctrl_connections_list = new ArrayList();
   private static SoftPerformer.KeySortComparator keySortComparator;

   private String extractKeys(ModelConnectionBlock var1) {
      StringBuffer var2 = new StringBuffer();
      if (var1.getSources() != null) {
         var2.append("[");
         ModelSource[] var3 = var1.getSources();
         ModelSource[] var4 = new ModelSource[var3.length];

         int var5;
         for(var5 = 0; var5 < var3.length; ++var5) {
            var4[var5] = var3[var5];
         }

         Arrays.sort(var4, keySortComparator);

         for(var5 = 0; var5 < var3.length; ++var5) {
            var2.append((Object)var3[var5].getIdentifier());
            var2.append(";");
         }

         var2.append("]");
      }

      var2.append(";");
      if (var1.getDestination() != null) {
         var2.append((Object)var1.getDestination().getIdentifier());
      }

      var2.append(";");
      return var2.toString();
   }

   private void processSource(ModelSource var1, int var2) {
      ModelIdentifier var3 = var1.getIdentifier();
      String var4 = var3.getObject();
      if (var4.equals("midi_cc")) {
         this.processMidiControlSource(var1, var2);
      } else if (var4.equals("midi_rpn")) {
         this.processMidiRpnSource(var1, var2);
      } else if (var4.equals("midi_nrpn")) {
         this.processMidiNrpnSource(var1, var2);
      } else if (var4.equals("midi")) {
         this.processMidiSource(var1, var2);
      } else if (var4.equals("noteon")) {
         this.processNoteOnSource(var1, var2);
      } else {
         if (var4.equals("osc")) {
            return;
         }

         if (var4.equals("mixer")) {
            return;
         }

         this.ctrl_connections_list.add(var2);
      }

   }

   private void processMidiControlSource(ModelSource var1, int var2) {
      String var3 = var1.getIdentifier().getVariable();
      if (var3 != null) {
         int var4 = Integer.parseInt(var3);
         if (this.midi_ctrl_connections[var4] == null) {
            this.midi_ctrl_connections[var4] = new int[]{var2};
         } else {
            int[] var5 = this.midi_ctrl_connections[var4];
            int[] var6 = new int[var5.length + 1];

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6[var7] = var5[var7];
            }

            var6[var6.length - 1] = var2;
            this.midi_ctrl_connections[var4] = var6;
         }

      }
   }

   private void processNoteOnSource(ModelSource var1, int var2) {
      String var3 = var1.getIdentifier().getVariable();
      byte var4 = -1;
      if (var3.equals("on")) {
         var4 = 3;
      }

      if (var3.equals("keynumber")) {
         var4 = 4;
      }

      if (var4 != -1) {
         if (this.midi_connections[var4] == null) {
            this.midi_connections[var4] = new int[]{var2};
         } else {
            int[] var5 = this.midi_connections[var4];
            int[] var6 = new int[var5.length + 1];

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6[var7] = var5[var7];
            }

            var6[var6.length - 1] = var2;
            this.midi_connections[var4] = var6;
         }

      }
   }

   private void processMidiSource(ModelSource var1, int var2) {
      String var3 = var1.getIdentifier().getVariable();
      byte var4 = -1;
      if (var3.equals("pitch")) {
         var4 = 0;
      }

      if (var3.equals("channel_pressure")) {
         var4 = 1;
      }

      if (var3.equals("poly_pressure")) {
         var4 = 2;
      }

      if (var4 != -1) {
         if (this.midi_connections[var4] == null) {
            this.midi_connections[var4] = new int[]{var2};
         } else {
            int[] var5 = this.midi_connections[var4];
            int[] var6 = new int[var5.length + 1];

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6[var7] = var5[var7];
            }

            var6[var6.length - 1] = var2;
            this.midi_connections[var4] = var6;
         }

      }
   }

   private void processMidiRpnSource(ModelSource var1, int var2) {
      String var3 = var1.getIdentifier().getVariable();
      if (var3 != null) {
         int var4 = Integer.parseInt(var3);
         if (this.midi_rpn_connections.get(var4) == null) {
            this.midi_rpn_connections.put(var4, new int[]{var2});
         } else {
            int[] var5 = (int[])this.midi_rpn_connections.get(var4);
            int[] var6 = new int[var5.length + 1];

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6[var7] = var5[var7];
            }

            var6[var6.length - 1] = var2;
            this.midi_rpn_connections.put(var4, var6);
         }

      }
   }

   private void processMidiNrpnSource(ModelSource var1, int var2) {
      String var3 = var1.getIdentifier().getVariable();
      if (var3 != null) {
         int var4 = Integer.parseInt(var3);
         if (this.midi_nrpn_connections.get(var4) == null) {
            this.midi_nrpn_connections.put(var4, new int[]{var2});
         } else {
            int[] var5 = (int[])this.midi_nrpn_connections.get(var4);
            int[] var6 = new int[var5.length + 1];

            for(int var7 = 0; var7 < var5.length; ++var7) {
               var6[var7] = var5[var7];
            }

            var6[var6.length - 1] = var2;
            this.midi_nrpn_connections.put(var4, var6);
         }

      }
   }

   public SoftPerformer(ModelPerformer var1) {
      this.performer = var1;
      this.keyFrom = var1.getKeyFrom();
      this.keyTo = var1.getKeyTo();
      this.velFrom = var1.getVelFrom();
      this.velTo = var1.getVelTo();
      this.exclusiveClass = var1.getExclusiveClass();
      this.selfNonExclusive = var1.isSelfNonExclusive();
      HashMap var2 = new HashMap();
      ArrayList var3 = new ArrayList();
      var3.addAll(var1.getConnectionBlocks());
      int var5;
      int var10;
      ModelConnectionBlock var18;
      boolean var20;
      ModelConnectionBlock var22;
      if (var1.isDefaultConnectionsEnabled()) {
         boolean var4 = false;

         ModelConnectionBlock var29;
         for(var5 = 0; var5 < var3.size(); ++var5) {
            ModelConnectionBlock var6 = (ModelConnectionBlock)var3.get(var5);
            ModelSource[] var7 = var6.getSources();
            ModelDestination var8 = var6.getDestination();
            boolean var9 = false;
            if (var8 != null && var7 != null && var7.length > 1) {
               for(var10 = 0; var10 < var7.length; ++var10) {
                  if (var7[var10].getIdentifier().getObject().equals("midi_cc") && var7[var10].getIdentifier().getVariable().equals("1")) {
                     var9 = true;
                     var4 = true;
                     break;
                  }
               }
            }

            if (var9) {
               var29 = new ModelConnectionBlock();
               var29.setSources(var6.getSources());
               var29.setDestination(var6.getDestination());
               var29.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
               var29.setScale(var6.getScale() * 256.0D);
               var3.set(var5, var29);
            }
         }

         if (!var4) {
            var18 = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0D, new ModelDestination(ModelDestination.DESTINATION_PITCH));
            var18.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
            var18.setScale(var18.getScale() * 256.0D);
            var3.add(var18);
         }

         boolean var19 = false;
         var20 = false;
         var22 = null;
         int var23 = 0;
         Iterator var27 = var3.iterator();

         label254:
         while(true) {
            ModelSource[] var11;
            ModelDestination var12;
            do {
               do {
                  if (!var27.hasNext()) {
                     ModelConnectionBlock var28;
                     if (var22 != null) {
                        ModelSource[] var30;
                        int var33;
                        if (!var19) {
                           var28 = new ModelConnectionBlock();
                           var28.setDestination(var22.getDestination());
                           var28.setScale(var22.getScale());
                           var30 = var22.getSources();
                           var11 = new ModelSource[var30.length];

                           for(var33 = 0; var33 < var11.length; ++var33) {
                              var11[var33] = var30[var33];
                           }

                           var11[var23] = new ModelSource(new ModelIdentifier("midi", "channel_pressure"));
                           var28.setSources(var11);
                           var2.put(this.extractKeys(var28), var28);
                        }

                        if (!var20) {
                           var28 = new ModelConnectionBlock();
                           var28.setDestination(var22.getDestination());
                           var28.setScale(var22.getScale());
                           var30 = var22.getSources();
                           var11 = new ModelSource[var30.length];

                           for(var33 = 0; var33 < var11.length; ++var33) {
                              var11[var33] = var30[var33];
                           }

                           var11[var23] = new ModelSource(new ModelIdentifier("midi", "poly_pressure"));
                           var28.setSources(var11);
                           var2.put(this.extractKeys(var28), var28);
                        }
                     }

                     var28 = null;
                     Iterator var31 = var3.iterator();

                     ModelConnectionBlock var32;
                     while(var31.hasNext()) {
                        var32 = (ModelConnectionBlock)var31.next();
                        ModelSource[] var38 = var32.getSources();
                        if (var38.length != 0 && var38[0].getIdentifier().getObject().equals("lfo") && var32.getDestination().getIdentifier().equals(ModelDestination.DESTINATION_PITCH)) {
                           if (var28 == null) {
                              var28 = var32;
                           } else if (var28.getSources().length > var38.length) {
                              var28 = var32;
                           } else if (var28.getSources()[0].getIdentifier().getInstance() < 1 && var28.getSources()[0].getIdentifier().getInstance() > var38[0].getIdentifier().getInstance()) {
                              var28 = var32;
                           }
                        }
                     }

                     var10 = 1;
                     if (var28 != null) {
                        var10 = var28.getSources()[0].getIdentifier().getInstance();
                     }

                     var32 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "78"), false, true, 0), 2000.0D, new ModelDestination(new ModelIdentifier("lfo", "delay2", var10)));
                     var2.put(this.extractKeys(var32), var32);
                     final double var39 = var28 == null ? 0.0D : var28.getScale();
                     var32 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("lfo", var10)), new ModelSource(new ModelIdentifier("midi_cc", "77"), new ModelTransform() {
                        double s = var39;

                        public double transform(double var1) {
                           var1 = var1 * 2.0D - 1.0D;
                           var1 *= 600.0D;
                           if (this.s == 0.0D) {
                              return var1;
                           } else if (this.s > 0.0D) {
                              if (var1 < -this.s) {
                                 var1 = -this.s;
                              }

                              return var1;
                           } else {
                              if (var1 < this.s) {
                                 var1 = -this.s;
                              }

                              return -var1;
                           }
                        }
                     }), new ModelDestination(ModelDestination.DESTINATION_PITCH));
                     var2.put(this.extractKeys(var32), var32);
                     var32 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "76"), false, true, 0), 2400.0D, new ModelDestination(new ModelIdentifier("lfo", "freq", var10)));
                     var2.put(this.extractKeys(var32), var32);
                     break label254;
                  }

                  var29 = (ModelConnectionBlock)var27.next();
                  var11 = var29.getSources();
                  var12 = var29.getDestination();
               } while(var12 == null);
            } while(var11 == null);

            for(int var13 = 0; var13 < var11.length; ++var13) {
               ModelIdentifier var14 = var11[var13].getIdentifier();
               if (var14.getObject().equals("midi_cc") && var14.getVariable().equals("1")) {
                  var22 = var29;
                  var23 = var13;
               }

               if (var14.getObject().equals("midi")) {
                  if (var14.getVariable().equals("channel_pressure")) {
                     var19 = true;
                  }

                  if (var14.getVariable().equals("poly_pressure")) {
                     var20 = true;
                  }
               }
            }
         }
      }

      if (var1.isDefaultConnectionsEnabled()) {
         ModelConnectionBlock[] var15 = defaultconnections;
         var5 = var15.length;

         for(int var21 = 0; var21 < var5; ++var21) {
            var22 = var15[var21];
            var2.put(this.extractKeys(var22), var22);
         }
      }

      Iterator var16 = var3.iterator();

      while(var16.hasNext()) {
         var18 = (ModelConnectionBlock)var16.next();
         var2.put(this.extractKeys(var18), var18);
      }

      ArrayList var17 = new ArrayList();
      this.midi_ctrl_connections = new int[128][];

      for(var5 = 0; var5 < this.midi_ctrl_connections.length; ++var5) {
         this.midi_ctrl_connections[var5] = null;
      }

      this.midi_connections = new int[5][];

      for(var5 = 0; var5 < this.midi_connections.length; ++var5) {
         this.midi_connections[var5] = null;
      }

      var5 = 0;
      var20 = false;
      Iterator var24 = var2.values().iterator();

      ModelConnectionBlock var25;
      while(var24.hasNext()) {
         var25 = (ModelConnectionBlock)var24.next();
         if (var25.getDestination() != null) {
            ModelDestination var34 = var25.getDestination();
            ModelIdentifier var36 = var34.getIdentifier();
            if (var36.getObject().equals("noteon")) {
               var20 = true;
               if (var36.getVariable().equals("keynumber")) {
                  this.forcedKeynumber = true;
               }

               if (var36.getVariable().equals("velocity")) {
                  this.forcedVelocity = true;
               }
            }
         }

         if (var20) {
            var17.add(0, var25);
            var20 = false;
         } else {
            var17.add(var25);
         }
      }

      ModelSource[] var35;
      for(var24 = var17.iterator(); var24.hasNext(); ++var5) {
         var25 = (ModelConnectionBlock)var24.next();
         if (var25.getSources() != null) {
            var35 = var25.getSources();

            for(var10 = 0; var10 < var35.length; ++var10) {
               this.processSource(var35[var10], var5);
            }
         }
      }

      this.connections = new ModelConnectionBlock[var17.size()];
      var17.toArray(this.connections);
      this.ctrl_connections = new int[this.ctrl_connections_list.size()];

      for(int var26 = 0; var26 < this.ctrl_connections.length; ++var26) {
         this.ctrl_connections[var26] = (Integer)this.ctrl_connections_list.get(var26);
      }

      this.oscillators = new ModelOscillator[var1.getOscillators().size()];
      var1.getOscillators().toArray(this.oscillators);
      var24 = var17.iterator();

      while(true) {
         do {
            if (!var24.hasNext()) {
               return;
            }

            var25 = (ModelConnectionBlock)var24.next();
            if (var25.getDestination() != null && isUnnecessaryTransform(var25.getDestination().getTransform())) {
               var25.getDestination().setTransform((ModelTransform)null);
            }
         } while(var25.getSources() == null);

         var35 = var25.getSources();
         var10 = var35.length;

         for(int var37 = 0; var37 < var10; ++var37) {
            ModelSource var40 = var35[var37];
            if (isUnnecessaryTransform(var40.getTransform())) {
               var40.setTransform((ModelTransform)null);
            }
         }
      }
   }

   private static boolean isUnnecessaryTransform(ModelTransform var0) {
      if (var0 == null) {
         return false;
      } else if (!(var0 instanceof ModelStandardTransform)) {
         return false;
      } else {
         ModelStandardTransform var1 = (ModelStandardTransform)var0;
         if (var1.getDirection()) {
            return false;
         } else if (var1.getPolarity()) {
            return false;
         } else {
            return var1.getTransform() != 0 ? false : false;
         }
      }
   }

   static {
      byte var0 = 0;
      int var1 = var0 + 1;
      defaultconnections[var0] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("eg", "on", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("eg", "on", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", "active", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("mixer", "active", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", 0), true, false, 0), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "velocity"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi", "pitch"), false, true, 0), new ModelSource(new ModelIdentifier("midi_rpn", "0"), new ModelTransform() {
         public double transform(double var1) {
            int var3 = (int)(var1 * 16384.0D);
            int var4 = var3 >> 7;
            int var5 = var3 & 127;
            return (double)(var4 * 100 + var5);
         }
      }), new ModelDestination(new ModelIdentifier("osc", "pitch")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "keynumber"), false, false, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "7"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "8"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "balance")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "10"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "pan")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "11"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "91"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "reverb")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "93"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "chorus")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "71"), false, true, 0), 200.0D, new ModelDestination(new ModelIdentifier("filter", "q")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "74"), false, true, 0), 9600.0D, new ModelDestination(new ModelIdentifier("filter", "freq")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "72"), false, true, 0), 6000.0D, new ModelDestination(new ModelIdentifier("eg", "release2")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "73"), false, true, 0), 2000.0D, new ModelDestination(new ModelIdentifier("eg", "attack2")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "75"), false, true, 0), 6000.0D, new ModelDestination(new ModelIdentifier("eg", "decay2")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -50.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -2400.0D, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "1"), false, true, 0), 100.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "2"), false, true, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "fine_tuning"), false, true, 0), 100.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
      defaultconnections[var1++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "coarse_tuning"), false, true, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
      defaultconnections[var1++] = new ModelConnectionBlock(13500.0D, new ModelDestination(new ModelIdentifier("filter", "freq", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(1000.0D, new ModelDestination(new ModelIdentifier("eg", "sustain", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(1200.0D * Math.log(0.015D) / Math.log(2.0D), new ModelDestination(new ModelIdentifier("eg", "shutdown", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(1000.0D, new ModelDestination(new ModelIdentifier("eg", "sustain", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(-8.51318D, new ModelDestination(new ModelIdentifier("lfo", "freq", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 0)));
      defaultconnections[var1++] = new ModelConnectionBlock(-8.51318D, new ModelDestination(new ModelIdentifier("lfo", "freq", 1)));
      defaultconnections[var1++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 1)));
      keySortComparator = new SoftPerformer.KeySortComparator();
   }

   private static class KeySortComparator implements Comparator<ModelSource> {
      private KeySortComparator() {
      }

      public int compare(ModelSource var1, ModelSource var2) {
         return var1.getIdentifier().toString().compareTo(var2.getIdentifier().toString());
      }

      // $FF: synthetic method
      KeySortComparator(Object var1) {
         this();
      }
   }
}
