package com.sun.media.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public final class SF2Soundbank implements Soundbank {
   int major = 2;
   int minor = 1;
   String targetEngine = "EMU8000";
   String name = "untitled";
   String romName = null;
   int romVersionMajor = -1;
   int romVersionMinor = -1;
   String creationDate = null;
   String engineers = null;
   String product = null;
   String copyright = null;
   String comments = null;
   String tools = null;
   private ModelByteBuffer sampleData = null;
   private ModelByteBuffer sampleData24 = null;
   private File sampleFile = null;
   private boolean largeFormat = false;
   private final List<SF2Instrument> instruments = new ArrayList();
   private final List<SF2Layer> layers = new ArrayList();
   private final List<SF2Sample> samples = new ArrayList();

   public SF2Soundbank() {
   }

   public SF2Soundbank(URL var1) throws IOException {
      InputStream var2 = var1.openStream();

      try {
         this.readSoundbank(var2);
      } finally {
         var2.close();
      }

   }

   public SF2Soundbank(File var1) throws IOException {
      this.largeFormat = true;
      this.sampleFile = var1;
      FileInputStream var2 = new FileInputStream(var1);

      try {
         this.readSoundbank(var2);
      } finally {
         var2.close();
      }

   }

   public SF2Soundbank(InputStream var1) throws IOException {
      this.readSoundbank(var1);
   }

   private void readSoundbank(InputStream var1) throws IOException {
      RIFFReader var2 = new RIFFReader(var1);
      if (!var2.getFormat().equals("RIFF")) {
         throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
      } else if (!var2.getType().equals("sfbk")) {
         throw new RIFFInvalidFormatException("Input stream is not a valid SoundFont!");
      } else {
         while(var2.hasNextChunk()) {
            RIFFReader var3 = var2.nextChunk();
            if (var3.getFormat().equals("LIST")) {
               if (var3.getType().equals("INFO")) {
                  this.readInfoChunk(var3);
               }

               if (var3.getType().equals("sdta")) {
                  this.readSdtaChunk(var3);
               }

               if (var3.getType().equals("pdta")) {
                  this.readPdtaChunk(var3);
               }
            }
         }

      }
   }

   private void readInfoChunk(RIFFReader var1) throws IOException {
      while(var1.hasNextChunk()) {
         RIFFReader var2 = var1.nextChunk();
         String var3 = var2.getFormat();
         if (var3.equals("ifil")) {
            this.major = var2.readUnsignedShort();
            this.minor = var2.readUnsignedShort();
         } else if (var3.equals("isng")) {
            this.targetEngine = var2.readString(var2.available());
         } else if (var3.equals("INAM")) {
            this.name = var2.readString(var2.available());
         } else if (var3.equals("irom")) {
            this.romName = var2.readString(var2.available());
         } else if (var3.equals("iver")) {
            this.romVersionMajor = var2.readUnsignedShort();
            this.romVersionMinor = var2.readUnsignedShort();
         } else if (var3.equals("ICRD")) {
            this.creationDate = var2.readString(var2.available());
         } else if (var3.equals("IENG")) {
            this.engineers = var2.readString(var2.available());
         } else if (var3.equals("IPRD")) {
            this.product = var2.readString(var2.available());
         } else if (var3.equals("ICOP")) {
            this.copyright = var2.readString(var2.available());
         } else if (var3.equals("ICMT")) {
            this.comments = var2.readString(var2.available());
         } else if (var3.equals("ISFT")) {
            this.tools = var2.readString(var2.available());
         }
      }

   }

   private void readSdtaChunk(RIFFReader var1) throws IOException {
      while(var1.hasNextChunk()) {
         RIFFReader var2 = var1.nextChunk();
         byte[] var3;
         int var4;
         int var5;
         if (var2.getFormat().equals("smpl")) {
            if (!this.largeFormat) {
               var3 = new byte[var2.available()];
               var4 = 0;
               var5 = var2.available();

               while(var4 != var5) {
                  if (var5 - var4 > 65536) {
                     var2.readFully(var3, var4, 65536);
                     var4 += 65536;
                  } else {
                     var2.readFully(var3, var4, var5 - var4);
                     var4 = var5;
                  }
               }

               this.sampleData = new ModelByteBuffer(var3);
            } else {
               this.sampleData = new ModelByteBuffer(this.sampleFile, var2.getFilePointer(), (long)var2.available());
            }
         }

         if (var2.getFormat().equals("sm24")) {
            if (!this.largeFormat) {
               var3 = new byte[var2.available()];
               var4 = 0;
               var5 = var2.available();

               while(var4 != var5) {
                  if (var5 - var4 > 65536) {
                     var2.readFully(var3, var4, 65536);
                     var4 += 65536;
                  } else {
                     var2.readFully(var3, var4, var5 - var4);
                     var4 = var5;
                  }
               }

               this.sampleData24 = new ModelByteBuffer(var3);
            } else {
               this.sampleData24 = new ModelByteBuffer(this.sampleFile, var2.getFilePointer(), (long)var2.available());
            }
         }
      }

   }

   private void readPdtaChunk(RIFFReader var1) throws IOException {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();
      ArrayList var9 = new ArrayList();

      while(true) {
         while(true) {
            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        while(true) {
                           SF2InstrumentRegion var26;
                           SF2InstrumentRegion var27;
                           SF2LayerRegion var43;
                           while(var1.hasNextChunk()) {
                              RIFFReader var10 = var1.nextChunk();
                              String var11 = var10.getFormat();
                              int var12;
                              int var13;
                              if (!var11.equals("phdr")) {
                                 int var15;
                                 int var16;
                                 int var17;
                                 int var18;
                                 int var19;
                                 int var24;
                                 if (!var11.equals("pbag")) {
                                    SF2Modulator var25;
                                    if (!var11.equals("pmod")) {
                                       short var28;
                                       if (!var11.equals("pgen")) {
                                          if (!var11.equals("inst")) {
                                             if (!var11.equals("ibag")) {
                                                if (var11.equals("imod")) {
                                                   for(var12 = 0; var12 < var9.size(); ++var12) {
                                                      var25 = new SF2Modulator();
                                                      var25.sourceOperator = var10.readUnsignedShort();
                                                      var25.destinationOperator = var10.readUnsignedShort();
                                                      var25.amount = var10.readShort();
                                                      var25.amountSourceOperator = var10.readUnsignedShort();
                                                      var25.transportOperator = var10.readUnsignedShort();
                                                      if (var12 < 0 || var12 >= var8.size()) {
                                                         throw new RIFFInvalidDataException();
                                                      }

                                                      var43 = (SF2LayerRegion)var8.get(var12);
                                                      if (var43 != null) {
                                                         var43.modulators.add(var25);
                                                      }
                                                   }
                                                } else if (var11.equals("igen")) {
                                                   for(var12 = 0; var12 < var8.size(); ++var12) {
                                                      var13 = var10.readUnsignedShort();
                                                      var28 = var10.readShort();
                                                      SF2LayerRegion var32 = (SF2LayerRegion)var8.get(var12);
                                                      if (var32 != null) {
                                                         var32.generators.put(var13, var28);
                                                      }
                                                   }
                                                } else if (var11.equals("shdr")) {
                                                   if (var10.available() % 46 != 0) {
                                                      throw new RIFFInvalidDataException();
                                                   }

                                                   var12 = var10.available() / 46;

                                                   for(var13 = 0; var13 < var12; ++var13) {
                                                      SF2Sample var42 = new SF2Sample(this);
                                                      var42.name = var10.readString(20);
                                                      long var31 = var10.readUnsignedInt();
                                                      long var40 = var10.readUnsignedInt();
                                                      if (this.sampleData != null) {
                                                         var42.data = this.sampleData.subbuffer(var31 * 2L, var40 * 2L, true);
                                                      }

                                                      if (this.sampleData24 != null) {
                                                         var42.data24 = this.sampleData24.subbuffer(var31, var40, true);
                                                      }

                                                      var42.startLoop = var10.readUnsignedInt() - var31;
                                                      var42.endLoop = var10.readUnsignedInt() - var31;
                                                      if (var42.startLoop < 0L) {
                                                         var42.startLoop = -1L;
                                                      }

                                                      if (var42.endLoop < 0L) {
                                                         var42.endLoop = -1L;
                                                      }

                                                      var42.sampleRate = var10.readUnsignedInt();
                                                      var42.originalPitch = var10.readUnsignedByte();
                                                      var42.pitchCorrection = var10.readByte();
                                                      var42.sampleLink = var10.readUnsignedShort();
                                                      var42.sampleType = var10.readUnsignedShort();
                                                      if (var13 != var12 - 1) {
                                                         this.samples.add(var42);
                                                      }
                                                   }
                                                }
                                             } else {
                                                if (var10.available() % 4 != 0) {
                                                   throw new RIFFInvalidDataException();
                                                }

                                                var12 = var10.available() / 4;
                                                var13 = var10.readUnsignedShort();
                                                var24 = var10.readUnsignedShort();

                                                while(var8.size() < var13) {
                                                   var8.add((Object)null);
                                                }

                                                while(var9.size() < var24) {
                                                   var9.add((Object)null);
                                                }

                                                --var12;
                                                if (var7.isEmpty()) {
                                                   throw new RIFFInvalidDataException();
                                                }

                                                var13 = (Integer)var7.get(0);

                                                for(var24 = 0; var24 < var13; ++var24) {
                                                   if (var12 == 0) {
                                                      throw new RIFFInvalidDataException();
                                                   }

                                                   var15 = var10.readUnsignedShort();
                                                   var16 = var10.readUnsignedShort();

                                                   while(var8.size() < var15) {
                                                      var8.add((Object)null);
                                                   }

                                                   while(var9.size() < var16) {
                                                      var9.add((Object)null);
                                                   }

                                                   --var12;
                                                }

                                                for(var24 = 0; var24 < var7.size() - 1; ++var24) {
                                                   var15 = (Integer)var7.get(var24 + 1) - (Integer)var7.get(var24);
                                                   SF2Layer var33 = (SF2Layer)this.layers.get(var24);

                                                   for(var17 = 0; var17 < var15; ++var17) {
                                                      if (var12 == 0) {
                                                         throw new RIFFInvalidDataException();
                                                      }

                                                      var18 = var10.readUnsignedShort();
                                                      var19 = var10.readUnsignedShort();
                                                      SF2LayerRegion var45 = new SF2LayerRegion();
                                                      var33.regions.add(var45);

                                                      while(var8.size() < var18) {
                                                         var8.add(var45);
                                                      }

                                                      while(var9.size() < var19) {
                                                         var9.add(var45);
                                                      }

                                                      --var12;
                                                   }
                                                }
                                             }
                                          } else {
                                             if (var10.available() % 22 != 0) {
                                                throw new RIFFInvalidDataException();
                                             }

                                             var12 = var10.available() / 22;

                                             for(var13 = 0; var13 < var12; ++var13) {
                                                SF2Layer var30 = new SF2Layer(this);
                                                var30.name = var10.readString(20);
                                                var7.add(var10.readUnsignedShort());
                                                var6.add(var30);
                                                if (var13 != var12 - 1) {
                                                   this.layers.add(var30);
                                                }
                                             }
                                          }
                                       } else {
                                          for(var12 = 0; var12 < var4.size(); ++var12) {
                                             var13 = var10.readUnsignedShort();
                                             var28 = var10.readShort();
                                             var26 = (SF2InstrumentRegion)var4.get(var12);
                                             if (var26 != null) {
                                                var26.generators.put(var13, var28);
                                             }
                                          }
                                       }
                                    } else {
                                       for(var12 = 0; var12 < var5.size(); ++var12) {
                                          var25 = new SF2Modulator();
                                          var25.sourceOperator = var10.readUnsignedShort();
                                          var25.destinationOperator = var10.readUnsignedShort();
                                          var25.amount = var10.readShort();
                                          var25.amountSourceOperator = var10.readUnsignedShort();
                                          var25.transportOperator = var10.readUnsignedShort();
                                          var27 = (SF2InstrumentRegion)var5.get(var12);
                                          if (var27 != null) {
                                             var27.modulators.add(var25);
                                          }
                                       }
                                    }
                                 } else {
                                    if (var10.available() % 4 != 0) {
                                       throw new RIFFInvalidDataException();
                                    }

                                    var12 = var10.available() / 4;
                                    var13 = var10.readUnsignedShort();
                                    var24 = var10.readUnsignedShort();

                                    while(var4.size() < var13) {
                                       var4.add((Object)null);
                                    }

                                    while(var5.size() < var24) {
                                       var5.add((Object)null);
                                    }

                                    --var12;
                                    if (var3.isEmpty()) {
                                       throw new RIFFInvalidDataException();
                                    }

                                    var13 = (Integer)var3.get(0);

                                    for(var24 = 0; var24 < var13; ++var24) {
                                       if (var12 == 0) {
                                          throw new RIFFInvalidDataException();
                                       }

                                       var15 = var10.readUnsignedShort();
                                       var16 = var10.readUnsignedShort();

                                       while(var4.size() < var15) {
                                          var4.add((Object)null);
                                       }

                                       while(var5.size() < var16) {
                                          var5.add((Object)null);
                                       }

                                       --var12;
                                    }

                                    for(var24 = 0; var24 < var3.size() - 1; ++var24) {
                                       var15 = (Integer)var3.get(var24 + 1) - (Integer)var3.get(var24);
                                       SF2Instrument var29 = (SF2Instrument)var2.get(var24);

                                       for(var17 = 0; var17 < var15; ++var17) {
                                          if (var12 == 0) {
                                             throw new RIFFInvalidDataException();
                                          }

                                          var18 = var10.readUnsignedShort();
                                          var19 = var10.readUnsignedShort();
                                          SF2InstrumentRegion var20 = new SF2InstrumentRegion();
                                          var29.regions.add(var20);

                                          while(var4.size() < var18) {
                                             var4.add(var20);
                                          }

                                          while(var5.size() < var19) {
                                             var5.add(var20);
                                          }

                                          --var12;
                                       }
                                    }
                                 }
                              } else {
                                 if (var10.available() % 38 != 0) {
                                    throw new RIFFInvalidDataException();
                                 }

                                 var12 = var10.available() / 38;

                                 for(var13 = 0; var13 < var12; ++var13) {
                                    SF2Instrument var14 = new SF2Instrument(this);
                                    var14.name = var10.readString(20);
                                    var14.preset = var10.readUnsignedShort();
                                    var14.bank = var10.readUnsignedShort();
                                    var3.add(var10.readUnsignedShort());
                                    var14.library = var10.readUnsignedInt();
                                    var14.genre = var10.readUnsignedInt();
                                    var14.morphology = var10.readUnsignedInt();
                                    var2.add(var14);
                                    if (var13 != var12 - 1) {
                                       this.instruments.add(var14);
                                    }
                                 }
                              }
                           }

                           Iterator var21 = this.layers.iterator();

                           while(var21.hasNext()) {
                              SF2Layer var22 = (SF2Layer)var21.next();
                              Iterator var38 = var22.regions.iterator();
                              SF2LayerRegion var36 = null;

                              while(var38.hasNext()) {
                                 var43 = (SF2LayerRegion)var38.next();
                                 if (var43.generators.get(53) != null) {
                                    short var34 = (Short)var43.generators.get(53);
                                    var43.generators.remove(53);
                                    if (var34 < 0 || var34 >= this.samples.size()) {
                                       throw new RIFFInvalidDataException();
                                    }

                                    var43.sample = (SF2Sample)this.samples.get(var34);
                                 } else {
                                    var36 = var43;
                                 }
                              }

                              if (var36 != null) {
                                 var22.getRegions().remove(var36);
                                 SF2GlobalRegion var44 = new SF2GlobalRegion();
                                 var44.generators = var36.generators;
                                 var44.modulators = var36.modulators;
                                 var22.setGlobalZone(var44);
                              }
                           }

                           Iterator var23 = this.instruments.iterator();

                           while(var23.hasNext()) {
                              SF2Instrument var41 = (SF2Instrument)var23.next();
                              Iterator var39 = var41.regions.iterator();
                              var27 = null;

                              while(var39.hasNext()) {
                                 var26 = (SF2InstrumentRegion)var39.next();
                                 if (var26.generators.get(41) != null) {
                                    short var35 = (Short)var26.generators.get(41);
                                    var26.generators.remove(41);
                                    if (var35 < 0 || var35 >= this.layers.size()) {
                                       throw new RIFFInvalidDataException();
                                    }

                                    var26.layer = (SF2Layer)this.layers.get(var35);
                                 } else {
                                    var27 = var26;
                                 }
                              }

                              if (var27 != null) {
                                 var41.getRegions().remove(var27);
                                 SF2GlobalRegion var37 = new SF2GlobalRegion();
                                 var37.generators = var27.generators;
                                 var37.modulators = var27.modulators;
                                 var41.setGlobalZone(var37);
                              }
                           }

                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void save(String var1) throws IOException {
      this.writeSoundbank(new RIFFWriter(var1, "sfbk"));
   }

   public void save(File var1) throws IOException {
      this.writeSoundbank(new RIFFWriter(var1, "sfbk"));
   }

   public void save(OutputStream var1) throws IOException {
      this.writeSoundbank(new RIFFWriter(var1, "sfbk"));
   }

   private void writeSoundbank(RIFFWriter var1) throws IOException {
      this.writeInfo(var1.writeList("INFO"));
      this.writeSdtaChunk(var1.writeList("sdta"));
      this.writePdtaChunk(var1.writeList("pdta"));
      var1.close();
   }

   private void writeInfoStringChunk(RIFFWriter var1, String var2, String var3) throws IOException {
      if (var3 != null) {
         RIFFWriter var4 = var1.writeChunk(var2);
         var4.writeString(var3);
         int var5 = var3.getBytes("ascii").length;
         var4.write(0);
         ++var5;
         if (var5 % 2 != 0) {
            var4.write(0);
         }

      }
   }

   private void writeInfo(RIFFWriter var1) throws IOException {
      if (this.targetEngine == null) {
         this.targetEngine = "EMU8000";
      }

      if (this.name == null) {
         this.name = "";
      }

      RIFFWriter var2 = var1.writeChunk("ifil");
      var2.writeUnsignedShort(this.major);
      var2.writeUnsignedShort(this.minor);
      this.writeInfoStringChunk(var1, "isng", this.targetEngine);
      this.writeInfoStringChunk(var1, "INAM", this.name);
      this.writeInfoStringChunk(var1, "irom", this.romName);
      if (this.romVersionMajor != -1) {
         RIFFWriter var3 = var1.writeChunk("iver");
         var3.writeUnsignedShort(this.romVersionMajor);
         var3.writeUnsignedShort(this.romVersionMinor);
      }

      this.writeInfoStringChunk(var1, "ICRD", this.creationDate);
      this.writeInfoStringChunk(var1, "IENG", this.engineers);
      this.writeInfoStringChunk(var1, "IPRD", this.product);
      this.writeInfoStringChunk(var1, "ICOP", this.copyright);
      this.writeInfoStringChunk(var1, "ICMT", this.comments);
      this.writeInfoStringChunk(var1, "ISFT", this.tools);
      var1.close();
   }

   private void writeSdtaChunk(RIFFWriter var1) throws IOException {
      byte[] var2 = new byte[32];
      RIFFWriter var3 = var1.writeChunk("smpl");
      Iterator var4 = this.samples.iterator();

      SF2Sample var5;
      ModelByteBuffer var6;
      while(var4.hasNext()) {
         var5 = (SF2Sample)var4.next();
         var6 = var5.getDataBuffer();
         var6.writeTo(var3);
         var3.write(var2);
         var3.write(var2);
      }

      if (this.major >= 2) {
         if (this.major != 2 || this.minor >= 4) {
            var4 = this.samples.iterator();

            while(var4.hasNext()) {
               var5 = (SF2Sample)var4.next();
               var6 = var5.getData24Buffer();
               if (var6 == null) {
                  return;
               }
            }

            RIFFWriter var8 = var1.writeChunk("sm24");
            Iterator var9 = this.samples.iterator();

            while(var9.hasNext()) {
               SF2Sample var10 = (SF2Sample)var9.next();
               ModelByteBuffer var7 = var10.getData24Buffer();
               var7.writeTo(var8);
               var3.write(var2);
            }

         }
      }
   }

   private void writeModulators(RIFFWriter var1, List<SF2Modulator> var2) throws IOException {
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         SF2Modulator var4 = (SF2Modulator)var3.next();
         var1.writeUnsignedShort(var4.sourceOperator);
         var1.writeUnsignedShort(var4.destinationOperator);
         var1.writeShort(var4.amount);
         var1.writeUnsignedShort(var4.amountSourceOperator);
         var1.writeUnsignedShort(var4.transportOperator);
      }

   }

   private void writeGenerators(RIFFWriter var1, Map<Integer, Short> var2) throws IOException {
      Short var3 = (Short)var2.get(43);
      Short var4 = (Short)var2.get(44);
      if (var3 != null) {
         var1.writeUnsignedShort(43);
         var1.writeShort(var3);
      }

      if (var4 != null) {
         var1.writeUnsignedShort(44);
         var1.writeShort(var4);
      }

      Iterator var5 = var2.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         if ((Integer)var6.getKey() != 43 && (Integer)var6.getKey() != 44) {
            var1.writeUnsignedShort((Integer)var6.getKey());
            var1.writeShort((Short)var6.getValue());
         }
      }

   }

   private void writePdtaChunk(RIFFWriter var1) throws IOException {
      RIFFWriter var2 = var1.writeChunk("phdr");
      int var3 = 0;
      Iterator var4 = this.instruments.iterator();

      while(var4.hasNext()) {
         SF2Instrument var5 = (SF2Instrument)var4.next();
         var2.writeString(var5.name, 20);
         var2.writeUnsignedShort(var5.preset);
         var2.writeUnsignedShort(var5.bank);
         var2.writeUnsignedShort(var3);
         if (var5.getGlobalRegion() != null) {
            ++var3;
         }

         var3 += var5.getRegions().size();
         var2.writeUnsignedInt(var5.library);
         var2.writeUnsignedInt(var5.genre);
         var2.writeUnsignedInt(var5.morphology);
      }

      var2.writeString("EOP", 20);
      var2.writeUnsignedShort(0);
      var2.writeUnsignedShort(0);
      var2.writeUnsignedShort(var3);
      var2.writeUnsignedInt(0L);
      var2.writeUnsignedInt(0L);
      var2.writeUnsignedInt(0L);
      RIFFWriter var29 = var1.writeChunk("pbag");
      int var30 = 0;
      int var6 = 0;
      Iterator var7 = this.instruments.iterator();

      Iterator var9;
      while(var7.hasNext()) {
         SF2Instrument var8 = (SF2Instrument)var7.next();
         if (var8.getGlobalRegion() != null) {
            var29.writeUnsignedShort(var30);
            var29.writeUnsignedShort(var6);
            var30 += var8.getGlobalRegion().getGenerators().size();
            var6 += var8.getGlobalRegion().getModulators().size();
         }

         SF2InstrumentRegion var10;
         for(var9 = var8.getRegions().iterator(); var9.hasNext(); var6 += var10.getModulators().size()) {
            var10 = (SF2InstrumentRegion)var9.next();
            var29.writeUnsignedShort(var30);
            var29.writeUnsignedShort(var6);
            if (this.layers.indexOf(var10.layer) != -1) {
               ++var30;
            }

            var30 += var10.getGenerators().size();
         }
      }

      var29.writeUnsignedShort(var30);
      var29.writeUnsignedShort(var6);
      RIFFWriter var31 = var1.writeChunk("pmod");
      Iterator var32 = this.instruments.iterator();

      while(var32.hasNext()) {
         SF2Instrument var34 = (SF2Instrument)var32.next();
         if (var34.getGlobalRegion() != null) {
            this.writeModulators(var31, var34.getGlobalRegion().getModulators());
         }

         Iterator var36 = var34.getRegions().iterator();

         while(var36.hasNext()) {
            SF2InstrumentRegion var11 = (SF2InstrumentRegion)var36.next();
            this.writeModulators(var31, var11.getModulators());
         }
      }

      var31.write(new byte[10]);
      RIFFWriter var33 = var1.writeChunk("pgen");
      var9 = this.instruments.iterator();

      int var13;
      Iterator var39;
      while(var9.hasNext()) {
         SF2Instrument var37 = (SF2Instrument)var9.next();
         if (var37.getGlobalRegion() != null) {
            this.writeGenerators(var33, var37.getGlobalRegion().getGenerators());
         }

         var39 = var37.getRegions().iterator();

         while(var39.hasNext()) {
            SF2InstrumentRegion var12 = (SF2InstrumentRegion)var39.next();
            this.writeGenerators(var33, var12.getGenerators());
            var13 = this.layers.indexOf(var12.layer);
            if (var13 != -1) {
               var33.writeUnsignedShort(41);
               var33.writeShort((short)var13);
            }
         }
      }

      var33.write(new byte[4]);
      RIFFWriter var35 = var1.writeChunk("inst");
      int var38 = 0;

      SF2Layer var41;
      for(var39 = this.layers.iterator(); var39.hasNext(); var38 += var41.getRegions().size()) {
         var41 = (SF2Layer)var39.next();
         var35.writeString(var41.name, 20);
         var35.writeUnsignedShort(var38);
         if (var41.getGlobalRegion() != null) {
            ++var38;
         }
      }

      var35.writeString("EOI", 20);
      var35.writeUnsignedShort(var38);
      RIFFWriter var40 = var1.writeChunk("ibag");
      int var42 = 0;
      var13 = 0;
      Iterator var14 = this.layers.iterator();

      Iterator var16;
      while(var14.hasNext()) {
         SF2Layer var15 = (SF2Layer)var14.next();
         if (var15.getGlobalRegion() != null) {
            var40.writeUnsignedShort(var42);
            var40.writeUnsignedShort(var13);
            var42 += var15.getGlobalRegion().getGenerators().size();
            var13 += var15.getGlobalRegion().getModulators().size();
         }

         SF2LayerRegion var17;
         for(var16 = var15.getRegions().iterator(); var16.hasNext(); var13 += var17.getModulators().size()) {
            var17 = (SF2LayerRegion)var16.next();
            var40.writeUnsignedShort(var42);
            var40.writeUnsignedShort(var13);
            if (this.samples.indexOf(var17.sample) != -1) {
               ++var42;
            }

            var42 += var17.getGenerators().size();
         }
      }

      var40.writeUnsignedShort(var42);
      var40.writeUnsignedShort(var13);
      RIFFWriter var43 = var1.writeChunk("imod");
      Iterator var44 = this.layers.iterator();

      while(var44.hasNext()) {
         SF2Layer var46 = (SF2Layer)var44.next();
         if (var46.getGlobalRegion() != null) {
            this.writeModulators(var43, var46.getGlobalRegion().getModulators());
         }

         Iterator var48 = var46.getRegions().iterator();

         while(var48.hasNext()) {
            SF2LayerRegion var18 = (SF2LayerRegion)var48.next();
            this.writeModulators(var43, var18.getModulators());
         }
      }

      var43.write(new byte[10]);
      RIFFWriter var45 = var1.writeChunk("igen");
      var16 = this.layers.iterator();

      while(var16.hasNext()) {
         SF2Layer var49 = (SF2Layer)var16.next();
         if (var49.getGlobalRegion() != null) {
            this.writeGenerators(var45, var49.getGlobalRegion().getGenerators());
         }

         Iterator var51 = var49.getRegions().iterator();

         while(var51.hasNext()) {
            SF2LayerRegion var19 = (SF2LayerRegion)var51.next();
            this.writeGenerators(var45, var19.getGenerators());
            int var20 = this.samples.indexOf(var19.sample);
            if (var20 != -1) {
               var45.writeUnsignedShort(53);
               var45.writeShort((short)var20);
            }
         }
      }

      var45.write(new byte[4]);
      RIFFWriter var47 = var1.writeChunk("shdr");
      long var50 = 0L;

      for(Iterator var52 = this.samples.iterator(); var52.hasNext(); var50 += 32L) {
         SF2Sample var53 = (SF2Sample)var52.next();
         var47.writeString(var53.name, 20);
         long var21 = var50;
         var50 += var53.data.capacity() / 2L;
         long var25 = var53.startLoop + var21;
         long var27 = var53.endLoop + var21;
         if (var25 < var21) {
            var25 = var21;
         }

         if (var27 > var50) {
            var27 = var50;
         }

         var47.writeUnsignedInt(var21);
         var47.writeUnsignedInt(var50);
         var47.writeUnsignedInt(var25);
         var47.writeUnsignedInt(var27);
         var47.writeUnsignedInt(var53.sampleRate);
         var47.writeUnsignedByte(var53.originalPitch);
         var47.writeByte(var53.pitchCorrection);
         var47.writeUnsignedShort(var53.sampleLink);
         var47.writeUnsignedShort(var53.sampleType);
      }

      var47.writeString("EOS", 20);
      var47.write(new byte[26]);
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.major + "." + this.minor;
   }

   public String getVendor() {
      return this.engineers;
   }

   public String getDescription() {
      return this.comments;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setVendor(String var1) {
      this.engineers = var1;
   }

   public void setDescription(String var1) {
      this.comments = var1;
   }

   public SoundbankResource[] getResources() {
      SoundbankResource[] var1 = new SoundbankResource[this.layers.size() + this.samples.size()];
      int var2 = 0;

      int var3;
      for(var3 = 0; var3 < this.layers.size(); ++var3) {
         var1[var2++] = (SoundbankResource)this.layers.get(var3);
      }

      for(var3 = 0; var3 < this.samples.size(); ++var3) {
         var1[var2++] = (SoundbankResource)this.samples.get(var3);
      }

      return var1;
   }

   public SF2Instrument[] getInstruments() {
      SF2Instrument[] var1 = (SF2Instrument[])this.instruments.toArray(new SF2Instrument[this.instruments.size()]);
      Arrays.sort(var1, new ModelInstrumentComparator());
      return var1;
   }

   public SF2Layer[] getLayers() {
      return (SF2Layer[])this.layers.toArray(new SF2Layer[this.layers.size()]);
   }

   public SF2Sample[] getSamples() {
      return (SF2Sample[])this.samples.toArray(new SF2Sample[this.samples.size()]);
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

   public String getCreationDate() {
      return this.creationDate;
   }

   public void setCreationDate(String var1) {
      this.creationDate = var1;
   }

   public String getProduct() {
      return this.product;
   }

   public void setProduct(String var1) {
      this.product = var1;
   }

   public String getRomName() {
      return this.romName;
   }

   public void setRomName(String var1) {
      this.romName = var1;
   }

   public int getRomVersionMajor() {
      return this.romVersionMajor;
   }

   public void setRomVersionMajor(int var1) {
      this.romVersionMajor = var1;
   }

   public int getRomVersionMinor() {
      return this.romVersionMinor;
   }

   public void setRomVersionMinor(int var1) {
      this.romVersionMinor = var1;
   }

   public String getTargetEngine() {
      return this.targetEngine;
   }

   public void setTargetEngine(String var1) {
      this.targetEngine = var1;
   }

   public String getTools() {
      return this.tools;
   }

   public void setTools(String var1) {
      this.tools = var1;
   }

   public void addResource(SoundbankResource var1) {
      if (var1 instanceof SF2Instrument) {
         this.instruments.add((SF2Instrument)var1);
      }

      if (var1 instanceof SF2Layer) {
         this.layers.add((SF2Layer)var1);
      }

      if (var1 instanceof SF2Sample) {
         this.samples.add((SF2Sample)var1);
      }

   }

   public void removeResource(SoundbankResource var1) {
      if (var1 instanceof SF2Instrument) {
         this.instruments.remove((SF2Instrument)var1);
      }

      if (var1 instanceof SF2Layer) {
         this.layers.remove((SF2Layer)var1);
      }

      if (var1 instanceof SF2Sample) {
         this.samples.remove((SF2Sample)var1);
      }

   }

   public void addInstrument(SF2Instrument var1) {
      this.instruments.add(var1);
   }

   public void removeInstrument(SF2Instrument var1) {
      this.instruments.remove(var1);
   }
}
