package com.sun.media.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class DLSSoundbank implements Soundbank {
   private static final int DLS_CDL_AND = 1;
   private static final int DLS_CDL_OR = 2;
   private static final int DLS_CDL_XOR = 3;
   private static final int DLS_CDL_ADD = 4;
   private static final int DLS_CDL_SUBTRACT = 5;
   private static final int DLS_CDL_MULTIPLY = 6;
   private static final int DLS_CDL_DIVIDE = 7;
   private static final int DLS_CDL_LOGICAL_AND = 8;
   private static final int DLS_CDL_LOGICAL_OR = 9;
   private static final int DLS_CDL_LT = 10;
   private static final int DLS_CDL_LE = 11;
   private static final int DLS_CDL_GT = 12;
   private static final int DLS_CDL_GE = 13;
   private static final int DLS_CDL_EQ = 14;
   private static final int DLS_CDL_NOT = 15;
   private static final int DLS_CDL_CONST = 16;
   private static final int DLS_CDL_QUERY = 17;
   private static final int DLS_CDL_QUERYSUPPORTED = 18;
   private static final DLSSoundbank.DLSID DLSID_GMInHardware = new DLSSoundbank.DLSID(395259684L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
   private static final DLSSoundbank.DLSID DLSID_GSInHardware = new DLSSoundbank.DLSID(395259685L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
   private static final DLSSoundbank.DLSID DLSID_XGInHardware = new DLSSoundbank.DLSID(395259686L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
   private static final DLSSoundbank.DLSID DLSID_SupportsDLS1 = new DLSSoundbank.DLSID(395259687L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
   private static final DLSSoundbank.DLSID DLSID_SupportsDLS2 = new DLSSoundbank.DLSID(-247096859L, 18057, 4562, 175, 166, 0, 170, 0, 36, 216, 182);
   private static final DLSSoundbank.DLSID DLSID_SampleMemorySize = new DLSSoundbank.DLSID(395259688L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
   private static final DLSSoundbank.DLSID DLSID_ManufacturersID = new DLSSoundbank.DLSID(-1338109567L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
   private static final DLSSoundbank.DLSID DLSID_ProductID = new DLSSoundbank.DLSID(-1338109566L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
   private static final DLSSoundbank.DLSID DLSID_SamplePlaybackRate = new DLSSoundbank.DLSID(714209043L, 42175, 4562, 187, 223, 0, 96, 8, 51, 219, 216);
   private long major = -1L;
   private long minor = -1L;
   private final DLSInfo info = new DLSInfo();
   private final List<DLSInstrument> instruments = new ArrayList();
   private final List<DLSSample> samples = new ArrayList();
   private boolean largeFormat = false;
   private File sampleFile;
   private Map<DLSRegion, Long> temp_rgnassign = new HashMap();

   public DLSSoundbank() {
   }

   public DLSSoundbank(URL var1) throws IOException {
      InputStream var2 = var1.openStream();

      try {
         this.readSoundbank(var2);
      } finally {
         var2.close();
      }

   }

   public DLSSoundbank(File var1) throws IOException {
      this.largeFormat = true;
      this.sampleFile = var1;
      FileInputStream var2 = new FileInputStream(var1);

      try {
         this.readSoundbank(var2);
      } finally {
         var2.close();
      }

   }

   public DLSSoundbank(InputStream var1) throws IOException {
      this.readSoundbank(var1);
   }

   private void readSoundbank(InputStream var1) throws IOException {
      RIFFReader var2 = new RIFFReader(var1);
      if (!var2.getFormat().equals("RIFF")) {
         throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
      } else if (!var2.getType().equals("DLS ")) {
         throw new RIFFInvalidFormatException("Input stream is not a valid DLS soundbank!");
      } else {
         while(var2.hasNextChunk()) {
            RIFFReader var3 = var2.nextChunk();
            if (var3.getFormat().equals("LIST")) {
               if (var3.getType().equals("INFO")) {
                  this.readInfoChunk(var3);
               }

               if (var3.getType().equals("lins")) {
                  this.readLinsChunk(var3);
               }

               if (var3.getType().equals("wvpl")) {
                  this.readWvplChunk(var3);
               }
            } else {
               if (var3.getFormat().equals("cdl ") && !this.readCdlChunk(var3)) {
                  throw new RIFFInvalidFormatException("DLS file isn't supported!");
               }

               if (var3.getFormat().equals("colh")) {
               }

               if (var3.getFormat().equals("ptbl")) {
               }

               if (var3.getFormat().equals("vers")) {
                  this.major = var3.readUnsignedInt();
                  this.minor = var3.readUnsignedInt();
               }
            }
         }

         Map.Entry var4;
         for(Iterator var5 = this.temp_rgnassign.entrySet().iterator(); var5.hasNext(); ((DLSRegion)var4.getKey()).sample = (DLSSample)this.samples.get((int)(Long)var4.getValue())) {
            var4 = (Map.Entry)var5.next();
         }

         this.temp_rgnassign = null;
      }
   }

   private boolean cdlIsQuerySupported(DLSSoundbank.DLSID var1) {
      return var1.equals(DLSID_GMInHardware) || var1.equals(DLSID_GSInHardware) || var1.equals(DLSID_XGInHardware) || var1.equals(DLSID_SupportsDLS1) || var1.equals(DLSID_SupportsDLS2) || var1.equals(DLSID_SampleMemorySize) || var1.equals(DLSID_ManufacturersID) || var1.equals(DLSID_ProductID) || var1.equals(DLSID_SamplePlaybackRate);
   }

   private long cdlQuery(DLSSoundbank.DLSID var1) {
      if (var1.equals(DLSID_GMInHardware)) {
         return 1L;
      } else if (var1.equals(DLSID_GSInHardware)) {
         return 0L;
      } else if (var1.equals(DLSID_XGInHardware)) {
         return 0L;
      } else if (var1.equals(DLSID_SupportsDLS1)) {
         return 1L;
      } else if (var1.equals(DLSID_SupportsDLS2)) {
         return 1L;
      } else if (var1.equals(DLSID_SampleMemorySize)) {
         return Runtime.getRuntime().totalMemory();
      } else if (var1.equals(DLSID_ManufacturersID)) {
         return 0L;
      } else if (var1.equals(DLSID_ProductID)) {
         return 0L;
      } else {
         return var1.equals(DLSID_SamplePlaybackRate) ? 44100L : 0L;
      }
   }

   private boolean readCdlChunk(RIFFReader var1) throws IOException {
      Stack var7 = new Stack();

      while(true) {
         while(var1.available() != 0) {
            int var8 = var1.readUnsignedShort();
            DLSSoundbank.DLSID var2;
            long var3;
            long var5;
            switch(var8) {
            case 1:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 != 0L && var5 != 0L ? 1L : 0L);
               break;
            case 2:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 == 0L && var5 == 0L ? 0L : 1L);
               break;
            case 3:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 != 0L ^ var5 != 0L ? 1L : 0L);
               break;
            case 4:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 + var5);
               break;
            case 5:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 - var5);
               break;
            case 6:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 * var5);
               break;
            case 7:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 / var5);
               break;
            case 8:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 != 0L && var5 != 0L ? 1L : 0L);
               break;
            case 9:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 == 0L && var5 == 0L ? 0L : 1L);
               break;
            case 10:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 < var5 ? 1L : 0L);
               break;
            case 11:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 <= var5 ? 1L : 0L);
               break;
            case 12:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 > var5 ? 1L : 0L);
               break;
            case 13:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 >= var5 ? 1L : 0L);
               break;
            case 14:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 == var5 ? 1L : 0L);
               break;
            case 15:
               var3 = (Long)var7.pop();
               var5 = (Long)var7.pop();
               var7.push(var3 == 0L ? 1L : 0L);
               break;
            case 16:
               var7.push(var1.readUnsignedInt());
               break;
            case 17:
               var2 = DLSSoundbank.DLSID.read(var1);
               var7.push(this.cdlQuery(var2));
               break;
            case 18:
               var2 = DLSSoundbank.DLSID.read(var1);
               var7.push(this.cdlIsQuerySupported(var2) ? 1L : 0L);
            }
         }

         if (var7.isEmpty()) {
            return false;
         }

         return (Long)var7.pop() == 1L;
      }
   }

   private void readInfoChunk(RIFFReader var1) throws IOException {
      this.info.name = null;

      while(var1.hasNextChunk()) {
         RIFFReader var2 = var1.nextChunk();
         String var3 = var2.getFormat();
         if (var3.equals("INAM")) {
            this.info.name = var2.readString(var2.available());
         } else if (var3.equals("ICRD")) {
            this.info.creationDate = var2.readString(var2.available());
         } else if (var3.equals("IENG")) {
            this.info.engineers = var2.readString(var2.available());
         } else if (var3.equals("IPRD")) {
            this.info.product = var2.readString(var2.available());
         } else if (var3.equals("ICOP")) {
            this.info.copyright = var2.readString(var2.available());
         } else if (var3.equals("ICMT")) {
            this.info.comments = var2.readString(var2.available());
         } else if (var3.equals("ISFT")) {
            this.info.tools = var2.readString(var2.available());
         } else if (var3.equals("IARL")) {
            this.info.archival_location = var2.readString(var2.available());
         } else if (var3.equals("IART")) {
            this.info.artist = var2.readString(var2.available());
         } else if (var3.equals("ICMS")) {
            this.info.commissioned = var2.readString(var2.available());
         } else if (var3.equals("IGNR")) {
            this.info.genre = var2.readString(var2.available());
         } else if (var3.equals("IKEY")) {
            this.info.keywords = var2.readString(var2.available());
         } else if (var3.equals("IMED")) {
            this.info.medium = var2.readString(var2.available());
         } else if (var3.equals("ISBJ")) {
            this.info.subject = var2.readString(var2.available());
         } else if (var3.equals("ISRC")) {
            this.info.source = var2.readString(var2.available());
         } else if (var3.equals("ISRF")) {
            this.info.source_form = var2.readString(var2.available());
         } else if (var3.equals("ITCH")) {
            this.info.technician = var2.readString(var2.available());
         }
      }

   }

   private void readLinsChunk(RIFFReader var1) throws IOException {
      while(var1.hasNextChunk()) {
         RIFFReader var2 = var1.nextChunk();
         if (var2.getFormat().equals("LIST") && var2.getType().equals("ins ")) {
            this.readInsChunk(var2);
         }
      }

   }

   private void readInsChunk(RIFFReader var1) throws IOException {
      DLSInstrument var2 = new DLSInstrument(this);

      while(true) {
         RIFFReader var3;
         ArrayList var9;
         RIFFReader var11;
         label92:
         do {
            while(var1.hasNextChunk()) {
               var3 = var1.nextChunk();
               String var4 = var3.getFormat();
               if (var4.equals("LIST")) {
                  if (var3.getType().equals("INFO")) {
                     this.readInsInfoChunk(var2, var3);
                  }

                  if (var3.getType().equals("lrgn")) {
                     while(var3.hasNextChunk()) {
                        RIFFReader var8 = var3.nextChunk();
                        if (var8.getFormat().equals("LIST")) {
                           DLSRegion var10;
                           if (var8.getType().equals("rgn ")) {
                              var10 = new DLSRegion();
                              if (this.readRgnChunk(var10, var8)) {
                                 var2.getRegions().add(var10);
                              }
                           }

                           if (var8.getType().equals("rgn2")) {
                              var10 = new DLSRegion();
                              if (this.readRgnChunk(var10, var8)) {
                                 var2.getRegions().add(var10);
                              }
                           }
                        }
                     }
                  }

                  if (var3.getType().equals("lart")) {
                     var9 = new ArrayList();

                     while(var3.hasNextChunk()) {
                        var11 = var3.nextChunk();
                        if (var3.getFormat().equals("cdl ") && !this.readCdlChunk(var3)) {
                           var9.clear();
                           break;
                        }

                        if (var11.getFormat().equals("art1")) {
                           this.readArt1Chunk(var9, var11);
                        }
                     }

                     var2.getModulators().addAll(var9);
                  }
                  continue label92;
               }

               if (var4.equals("dlid")) {
                  var2.guid = new byte[16];
                  var3.readFully(var2.guid);
               }

               if (var4.equals("insh")) {
                  var3.readUnsignedInt();
                  int var5 = var3.read();
                  var5 += (var3.read() & 127) << 7;
                  var3.read();
                  int var6 = var3.read();
                  int var7 = var3.read() & 127;
                  var3.read();
                  var3.read();
                  var3.read();
                  var2.bank = var5;
                  var2.preset = var7;
                  var2.druminstrument = (var6 & 128) > 0;
               }
            }

            this.instruments.add(var2);
            return;
         } while(!var3.getType().equals("lar2"));

         var9 = new ArrayList();

         while(var3.hasNextChunk()) {
            var11 = var3.nextChunk();
            if (var3.getFormat().equals("cdl ") && !this.readCdlChunk(var3)) {
               var9.clear();
               break;
            }

            if (var11.getFormat().equals("art2")) {
               this.readArt2Chunk(var9, var11);
            }
         }

         var2.getModulators().addAll(var9);
      }
   }

   private void readArt1Chunk(List<DLSModulator> var1, RIFFReader var2) throws IOException {
      long var3 = var2.readUnsignedInt();
      long var5 = var2.readUnsignedInt();
      if (var3 - 8L != 0L) {
         var2.skipBytes(var3 - 8L);
      }

      for(int var7 = 0; (long)var7 < var5; ++var7) {
         DLSModulator var8 = new DLSModulator();
         var8.version = 1;
         var8.source = var2.readUnsignedShort();
         var8.control = var2.readUnsignedShort();
         var8.destination = var2.readUnsignedShort();
         var8.transform = var2.readUnsignedShort();
         var8.scale = var2.readInt();
         var1.add(var8);
      }

   }

   private void readArt2Chunk(List<DLSModulator> var1, RIFFReader var2) throws IOException {
      long var3 = var2.readUnsignedInt();
      long var5 = var2.readUnsignedInt();
      if (var3 - 8L != 0L) {
         var2.skipBytes(var3 - 8L);
      }

      for(int var7 = 0; (long)var7 < var5; ++var7) {
         DLSModulator var8 = new DLSModulator();
         var8.version = 2;
         var8.source = var2.readUnsignedShort();
         var8.control = var2.readUnsignedShort();
         var8.destination = var2.readUnsignedShort();
         var8.transform = var2.readUnsignedShort();
         var8.scale = var2.readInt();
         var1.add(var8);
      }

   }

   private boolean readRgnChunk(DLSRegion var1, RIFFReader var2) throws IOException {
      while(var2.hasNextChunk()) {
         RIFFReader var3 = var2.nextChunk();
         String var4 = var3.getFormat();
         if (!var4.equals("LIST")) {
            if (var4.equals("cdl ") && !this.readCdlChunk(var3)) {
               return false;
            }

            if (var4.equals("rgnh")) {
               var1.keyfrom = var3.readUnsignedShort();
               var1.keyto = var3.readUnsignedShort();
               var1.velfrom = var3.readUnsignedShort();
               var1.velto = var3.readUnsignedShort();
               var1.options = var3.readUnsignedShort();
               var1.exclusiveClass = var3.readUnsignedShort();
            }

            if (var4.equals("wlnk")) {
               var1.fusoptions = var3.readUnsignedShort();
               var1.phasegroup = var3.readUnsignedShort();
               var1.channel = var3.readUnsignedInt();
               long var7 = var3.readUnsignedInt();
               this.temp_rgnassign.put(var1, var7);
            }

            if (var4.equals("wsmp")) {
               var1.sampleoptions = new DLSSampleOptions();
               this.readWsmpChunk(var1.sampleoptions, var3);
            }
         } else {
            ArrayList var5;
            RIFFReader var6;
            if (var3.getType().equals("lart")) {
               var5 = new ArrayList();

               while(var3.hasNextChunk()) {
                  var6 = var3.nextChunk();
                  if (var3.getFormat().equals("cdl ") && !this.readCdlChunk(var3)) {
                     var5.clear();
                     break;
                  }

                  if (var6.getFormat().equals("art1")) {
                     this.readArt1Chunk(var5, var6);
                  }
               }

               var1.getModulators().addAll(var5);
            }

            if (var3.getType().equals("lar2")) {
               var5 = new ArrayList();

               while(var3.hasNextChunk()) {
                  var6 = var3.nextChunk();
                  if (var3.getFormat().equals("cdl ") && !this.readCdlChunk(var3)) {
                     var5.clear();
                     break;
                  }

                  if (var6.getFormat().equals("art2")) {
                     this.readArt2Chunk(var5, var6);
                  }
               }

               var1.getModulators().addAll(var5);
            }
         }
      }

      return true;
   }

   private void readWsmpChunk(DLSSampleOptions var1, RIFFReader var2) throws IOException {
      long var3 = var2.readUnsignedInt();
      var1.unitynote = var2.readUnsignedShort();
      var1.finetune = var2.readShort();
      var1.attenuation = var2.readInt();
      var1.options = var2.readUnsignedInt();
      long var5 = (long)var2.readInt();
      if (var3 > 20L) {
         var2.skipBytes(var3 - 20L);
      }

      for(int var7 = 0; (long)var7 < var5; ++var7) {
         DLSSampleLoop var8 = new DLSSampleLoop();
         long var9 = var2.readUnsignedInt();
         var8.type = var2.readUnsignedInt();
         var8.start = var2.readUnsignedInt();
         var8.length = var2.readUnsignedInt();
         var1.loops.add(var8);
         if (var9 > 16L) {
            var2.skipBytes(var9 - 16L);
         }
      }

   }

   private void readInsInfoChunk(DLSInstrument var1, RIFFReader var2) throws IOException {
      var1.info.name = null;

      while(var2.hasNextChunk()) {
         RIFFReader var3 = var2.nextChunk();
         String var4 = var3.getFormat();
         if (var4.equals("INAM")) {
            var1.info.name = var3.readString(var3.available());
         } else if (var4.equals("ICRD")) {
            var1.info.creationDate = var3.readString(var3.available());
         } else if (var4.equals("IENG")) {
            var1.info.engineers = var3.readString(var3.available());
         } else if (var4.equals("IPRD")) {
            var1.info.product = var3.readString(var3.available());
         } else if (var4.equals("ICOP")) {
            var1.info.copyright = var3.readString(var3.available());
         } else if (var4.equals("ICMT")) {
            var1.info.comments = var3.readString(var3.available());
         } else if (var4.equals("ISFT")) {
            var1.info.tools = var3.readString(var3.available());
         } else if (var4.equals("IARL")) {
            var1.info.archival_location = var3.readString(var3.available());
         } else if (var4.equals("IART")) {
            var1.info.artist = var3.readString(var3.available());
         } else if (var4.equals("ICMS")) {
            var1.info.commissioned = var3.readString(var3.available());
         } else if (var4.equals("IGNR")) {
            var1.info.genre = var3.readString(var3.available());
         } else if (var4.equals("IKEY")) {
            var1.info.keywords = var3.readString(var3.available());
         } else if (var4.equals("IMED")) {
            var1.info.medium = var3.readString(var3.available());
         } else if (var4.equals("ISBJ")) {
            var1.info.subject = var3.readString(var3.available());
         } else if (var4.equals("ISRC")) {
            var1.info.source = var3.readString(var3.available());
         } else if (var4.equals("ISRF")) {
            var1.info.source_form = var3.readString(var3.available());
         } else if (var4.equals("ITCH")) {
            var1.info.technician = var3.readString(var3.available());
         }
      }

   }

   private void readWvplChunk(RIFFReader var1) throws IOException {
      while(var1.hasNextChunk()) {
         RIFFReader var2 = var1.nextChunk();
         if (var2.getFormat().equals("LIST") && var2.getType().equals("wave")) {
            this.readWaveChunk(var2);
         }
      }

   }

   private void readWaveChunk(RIFFReader var1) throws IOException {
      DLSSample var2 = new DLSSample(this);

      while(true) {
         while(var1.hasNextChunk()) {
            RIFFReader var3 = var1.nextChunk();
            String var4 = var3.getFormat();
            if (var4.equals("LIST")) {
               if (var3.getType().equals("INFO")) {
                  this.readWaveInfoChunk(var2, var3);
               }
            } else {
               if (var4.equals("dlid")) {
                  var2.guid = new byte[16];
                  var3.readFully(var2.guid);
               }

               int var6;
               if (var4.equals("fmt ")) {
                  int var5 = var3.readUnsignedShort();
                  if (var5 != 1 && var5 != 3) {
                     throw new RIFFInvalidDataException("Only PCM samples are supported!");
                  }

                  var6 = var3.readUnsignedShort();
                  long var7 = var3.readUnsignedInt();
                  var3.readUnsignedInt();
                  int var9 = var3.readUnsignedShort();
                  int var10 = var3.readUnsignedShort();
                  AudioFormat var11 = null;
                  if (var5 == 1) {
                     if (var10 == 8) {
                        var11 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)var7, var10, var6, var9, (float)var7, false);
                     } else {
                        var11 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)var7, var10, var6, var9, (float)var7, false);
                     }
                  }

                  if (var5 == 3) {
                     var11 = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)var7, var10, var6, var9, (float)var7, false);
                  }

                  var2.format = var11;
               }

               if (var4.equals("data")) {
                  if (this.largeFormat) {
                     var2.setData(new ModelByteBuffer(this.sampleFile, var3.getFilePointer(), (long)var3.available()));
                  } else {
                     byte[] var12 = new byte[var3.available()];
                     var2.setData(var12);
                     var6 = 0;
                     int var13 = var3.available();

                     while(var6 != var13) {
                        if (var13 - var6 > 65536) {
                           var3.readFully(var12, var6, 65536);
                           var6 += 65536;
                        } else {
                           var3.readFully(var12, var6, var13 - var6);
                           var6 = var13;
                        }
                     }
                  }
               }

               if (var4.equals("wsmp")) {
                  var2.sampleoptions = new DLSSampleOptions();
                  this.readWsmpChunk(var2.sampleoptions, var3);
               }
            }
         }

         this.samples.add(var2);
         return;
      }
   }

   private void readWaveInfoChunk(DLSSample var1, RIFFReader var2) throws IOException {
      var1.info.name = null;

      while(var2.hasNextChunk()) {
         RIFFReader var3 = var2.nextChunk();
         String var4 = var3.getFormat();
         if (var4.equals("INAM")) {
            var1.info.name = var3.readString(var3.available());
         } else if (var4.equals("ICRD")) {
            var1.info.creationDate = var3.readString(var3.available());
         } else if (var4.equals("IENG")) {
            var1.info.engineers = var3.readString(var3.available());
         } else if (var4.equals("IPRD")) {
            var1.info.product = var3.readString(var3.available());
         } else if (var4.equals("ICOP")) {
            var1.info.copyright = var3.readString(var3.available());
         } else if (var4.equals("ICMT")) {
            var1.info.comments = var3.readString(var3.available());
         } else if (var4.equals("ISFT")) {
            var1.info.tools = var3.readString(var3.available());
         } else if (var4.equals("IARL")) {
            var1.info.archival_location = var3.readString(var3.available());
         } else if (var4.equals("IART")) {
            var1.info.artist = var3.readString(var3.available());
         } else if (var4.equals("ICMS")) {
            var1.info.commissioned = var3.readString(var3.available());
         } else if (var4.equals("IGNR")) {
            var1.info.genre = var3.readString(var3.available());
         } else if (var4.equals("IKEY")) {
            var1.info.keywords = var3.readString(var3.available());
         } else if (var4.equals("IMED")) {
            var1.info.medium = var3.readString(var3.available());
         } else if (var4.equals("ISBJ")) {
            var1.info.subject = var3.readString(var3.available());
         } else if (var4.equals("ISRC")) {
            var1.info.source = var3.readString(var3.available());
         } else if (var4.equals("ISRF")) {
            var1.info.source_form = var3.readString(var3.available());
         } else if (var4.equals("ITCH")) {
            var1.info.technician = var3.readString(var3.available());
         }
      }

   }

   public void save(String var1) throws IOException {
      this.writeSoundbank(new RIFFWriter(var1, "DLS "));
   }

   public void save(File var1) throws IOException {
      this.writeSoundbank(new RIFFWriter(var1, "DLS "));
   }

   public void save(OutputStream var1) throws IOException {
      this.writeSoundbank(new RIFFWriter(var1, "DLS "));
   }

   private void writeSoundbank(RIFFWriter var1) throws IOException {
      RIFFWriter var2 = var1.writeChunk("colh");
      var2.writeUnsignedInt((long)this.instruments.size());
      RIFFWriter var3;
      if (this.major != -1L && this.minor != -1L) {
         var3 = var1.writeChunk("vers");
         var3.writeUnsignedInt(this.major);
         var3.writeUnsignedInt(this.minor);
      }

      this.writeInstruments(var1.writeList("lins"));
      var3 = var1.writeChunk("ptbl");
      var3.writeUnsignedInt(8L);
      var3.writeUnsignedInt((long)this.samples.size());
      long var4 = var1.getFilePointer();

      for(int var6 = 0; var6 < this.samples.size(); ++var6) {
         var3.writeUnsignedInt(0L);
      }

      RIFFWriter var15 = var1.writeList("wvpl");
      long var7 = var15.getFilePointer();
      ArrayList var9 = new ArrayList();
      Iterator var10 = this.samples.iterator();

      while(var10.hasNext()) {
         DLSSample var11 = (DLSSample)var10.next();
         var9.add(var15.getFilePointer() - var7);
         this.writeSample(var15.writeList("wave"), var11);
      }

      long var14 = var1.getFilePointer();
      var1.seek(var4);
      var1.setWriteOverride(true);
      Iterator var12 = var9.iterator();

      while(var12.hasNext()) {
         Long var13 = (Long)var12.next();
         var1.writeUnsignedInt(var13);
      }

      var1.setWriteOverride(false);
      var1.seek(var14);
      this.writeInfo(var1.writeList("INFO"), this.info);
      var1.close();
   }

   private void writeSample(RIFFWriter var1, DLSSample var2) throws IOException {
      AudioFormat var3 = var2.getFormat();
      AudioFormat.Encoding var4 = var3.getEncoding();
      float var5 = var3.getSampleRate();
      int var6 = var3.getSampleSizeInBits();
      int var7 = var3.getChannels();
      int var8 = var3.getFrameSize();
      float var9 = var3.getFrameRate();
      boolean var10 = var3.isBigEndian();
      boolean var11 = false;
      if (var3.getSampleSizeInBits() == 8) {
         if (!var4.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            var4 = AudioFormat.Encoding.PCM_UNSIGNED;
            var11 = true;
         }
      } else {
         if (!var4.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            var4 = AudioFormat.Encoding.PCM_SIGNED;
            var11 = true;
         }

         if (var10) {
            var10 = false;
            var11 = true;
         }
      }

      if (var11) {
         var3 = new AudioFormat(var4, var5, var6, var7, var8, var9, var10);
      }

      RIFFWriter var12 = var1.writeChunk("fmt ");
      byte var13 = 0;
      if (var3.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
         var13 = 1;
      } else if (var3.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
         var13 = 1;
      } else if (var3.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
         var13 = 3;
      }

      var12.writeUnsignedShort(var13);
      var12.writeUnsignedShort(var3.getChannels());
      var12.writeUnsignedInt((long)var3.getSampleRate());
      long var14 = (long)var3.getFrameRate() * (long)var3.getFrameSize();
      var12.writeUnsignedInt(var14);
      var12.writeUnsignedShort(var3.getFrameSize());
      var12.writeUnsignedShort(var3.getSampleSizeInBits());
      var12.write(0);
      var12.write(0);
      this.writeSampleOptions(var1.writeChunk("wsmp"), var2.sampleoptions);
      RIFFWriter var16;
      if (var11) {
         var16 = var1.writeChunk("data");
         AudioInputStream var17 = AudioSystem.getAudioInputStream(var3, (AudioInputStream)var2.getData());
         byte[] var18 = new byte[1024];

         int var19;
         while((var19 = var17.read(var18)) != -1) {
            var16.write(var18, 0, var19);
         }
      } else {
         var16 = var1.writeChunk("data");
         ModelByteBuffer var20 = var2.getDataBuffer();
         var20.writeTo(var16);
      }

      this.writeInfo(var1.writeList("INFO"), var2.info);
   }

   private void writeInstruments(RIFFWriter var1) throws IOException {
      Iterator var2 = this.instruments.iterator();

      while(var2.hasNext()) {
         DLSInstrument var3 = (DLSInstrument)var2.next();
         this.writeInstrument(var1.writeList("ins "), var3);
      }

   }

   private void writeInstrument(RIFFWriter var1, DLSInstrument var2) throws IOException {
      int var3 = 0;
      int var4 = 0;
      Iterator var5 = var2.getModulators().iterator();

      while(var5.hasNext()) {
         DLSModulator var6 = (DLSModulator)var5.next();
         if (var6.version == 1) {
            ++var3;
         }

         if (var6.version == 2) {
            ++var4;
         }
      }

      var5 = var2.regions.iterator();

      while(var5.hasNext()) {
         DLSRegion var11 = (DLSRegion)var5.next();
         Iterator var7 = var11.getModulators().iterator();

         while(var7.hasNext()) {
            DLSModulator var8 = (DLSModulator)var7.next();
            if (var8.version == 1) {
               ++var3;
            }

            if (var8.version == 2) {
               ++var4;
            }
         }
      }

      byte var10 = 1;
      if (var4 > 0) {
         var10 = 2;
      }

      RIFFWriter var12 = var1.writeChunk("insh");
      var12.writeUnsignedInt((long)var2.getRegions().size());
      var12.writeUnsignedInt((long)var2.bank + (var2.druminstrument ? 2147483648L : 0L));
      var12.writeUnsignedInt((long)var2.preset);
      RIFFWriter var13 = var1.writeList("lrgn");
      Iterator var14 = var2.regions.iterator();

      while(var14.hasNext()) {
         DLSRegion var9 = (DLSRegion)var14.next();
         this.writeRegion(var13, var9, var10);
      }

      this.writeArticulators(var1, var2.getModulators());
      this.writeInfo(var1.writeList("INFO"), var2.info);
   }

   private void writeArticulators(RIFFWriter var1, List<DLSModulator> var2) throws IOException {
      int var3 = 0;
      int var4 = 0;
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         DLSModulator var6 = (DLSModulator)var5.next();
         if (var6.version == 1) {
            ++var3;
         }

         if (var6.version == 2) {
            ++var4;
         }
      }

      Iterator var7;
      DLSModulator var8;
      RIFFWriter var9;
      RIFFWriter var10;
      if (var3 > 0) {
         var9 = var1.writeList("lart");
         var10 = var9.writeChunk("art1");
         var10.writeUnsignedInt(8L);
         var10.writeUnsignedInt((long)var3);
         var7 = var2.iterator();

         while(var7.hasNext()) {
            var8 = (DLSModulator)var7.next();
            if (var8.version == 1) {
               var10.writeUnsignedShort(var8.source);
               var10.writeUnsignedShort(var8.control);
               var10.writeUnsignedShort(var8.destination);
               var10.writeUnsignedShort(var8.transform);
               var10.writeInt(var8.scale);
            }
         }
      }

      if (var4 > 0) {
         var9 = var1.writeList("lar2");
         var10 = var9.writeChunk("art2");
         var10.writeUnsignedInt(8L);
         var10.writeUnsignedInt((long)var4);
         var7 = var2.iterator();

         while(var7.hasNext()) {
            var8 = (DLSModulator)var7.next();
            if (var8.version == 2) {
               var10.writeUnsignedShort(var8.source);
               var10.writeUnsignedShort(var8.control);
               var10.writeUnsignedShort(var8.destination);
               var10.writeUnsignedShort(var8.transform);
               var10.writeInt(var8.scale);
            }
         }
      }

   }

   private void writeRegion(RIFFWriter var1, DLSRegion var2, int var3) throws IOException {
      RIFFWriter var4 = null;
      if (var3 == 1) {
         var4 = var1.writeList("rgn ");
      }

      if (var3 == 2) {
         var4 = var1.writeList("rgn2");
      }

      if (var4 != null) {
         RIFFWriter var5 = var4.writeChunk("rgnh");
         var5.writeUnsignedShort(var2.keyfrom);
         var5.writeUnsignedShort(var2.keyto);
         var5.writeUnsignedShort(var2.velfrom);
         var5.writeUnsignedShort(var2.velto);
         var5.writeUnsignedShort(var2.options);
         var5.writeUnsignedShort(var2.exclusiveClass);
         if (var2.sampleoptions != null) {
            this.writeSampleOptions(var4.writeChunk("wsmp"), var2.sampleoptions);
         }

         if (var2.sample != null && this.samples.indexOf(var2.sample) != -1) {
            RIFFWriter var6 = var4.writeChunk("wlnk");
            var6.writeUnsignedShort(var2.fusoptions);
            var6.writeUnsignedShort(var2.phasegroup);
            var6.writeUnsignedInt(var2.channel);
            var6.writeUnsignedInt((long)this.samples.indexOf(var2.sample));
         }

         this.writeArticulators(var4, var2.getModulators());
         var4.close();
      }
   }

   private void writeSampleOptions(RIFFWriter var1, DLSSampleOptions var2) throws IOException {
      var1.writeUnsignedInt(20L);
      var1.writeUnsignedShort(var2.unitynote);
      var1.writeShort(var2.finetune);
      var1.writeInt(var2.attenuation);
      var1.writeUnsignedInt(var2.options);
      var1.writeInt(var2.loops.size());
      Iterator var3 = var2.loops.iterator();

      while(var3.hasNext()) {
         DLSSampleLoop var4 = (DLSSampleLoop)var3.next();
         var1.writeUnsignedInt(16L);
         var1.writeUnsignedInt(var4.type);
         var1.writeUnsignedInt(var4.start);
         var1.writeUnsignedInt(var4.length);
      }

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

   private void writeInfo(RIFFWriter var1, DLSInfo var2) throws IOException {
      this.writeInfoStringChunk(var1, "INAM", var2.name);
      this.writeInfoStringChunk(var1, "ICRD", var2.creationDate);
      this.writeInfoStringChunk(var1, "IENG", var2.engineers);
      this.writeInfoStringChunk(var1, "IPRD", var2.product);
      this.writeInfoStringChunk(var1, "ICOP", var2.copyright);
      this.writeInfoStringChunk(var1, "ICMT", var2.comments);
      this.writeInfoStringChunk(var1, "ISFT", var2.tools);
      this.writeInfoStringChunk(var1, "IARL", var2.archival_location);
      this.writeInfoStringChunk(var1, "IART", var2.artist);
      this.writeInfoStringChunk(var1, "ICMS", var2.commissioned);
      this.writeInfoStringChunk(var1, "IGNR", var2.genre);
      this.writeInfoStringChunk(var1, "IKEY", var2.keywords);
      this.writeInfoStringChunk(var1, "IMED", var2.medium);
      this.writeInfoStringChunk(var1, "ISBJ", var2.subject);
      this.writeInfoStringChunk(var1, "ISRC", var2.source);
      this.writeInfoStringChunk(var1, "ISRF", var2.source_form);
      this.writeInfoStringChunk(var1, "ITCH", var2.technician);
   }

   public DLSInfo getInfo() {
      return this.info;
   }

   public String getName() {
      return this.info.name;
   }

   public String getVersion() {
      return this.major + "." + this.minor;
   }

   public String getVendor() {
      return this.info.engineers;
   }

   public String getDescription() {
      return this.info.comments;
   }

   public void setName(String var1) {
      this.info.name = var1;
   }

   public void setVendor(String var1) {
      this.info.engineers = var1;
   }

   public void setDescription(String var1) {
      this.info.comments = var1;
   }

   public SoundbankResource[] getResources() {
      SoundbankResource[] var1 = new SoundbankResource[this.samples.size()];
      int var2 = 0;

      for(int var3 = 0; var3 < this.samples.size(); ++var3) {
         var1[var2++] = (SoundbankResource)this.samples.get(var3);
      }

      return var1;
   }

   public DLSInstrument[] getInstruments() {
      DLSInstrument[] var1 = (DLSInstrument[])this.instruments.toArray(new DLSInstrument[this.instruments.size()]);
      Arrays.sort(var1, new ModelInstrumentComparator());
      return var1;
   }

   public DLSSample[] getSamples() {
      return (DLSSample[])this.samples.toArray(new DLSSample[this.samples.size()]);
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
      if (var1 instanceof DLSInstrument) {
         this.instruments.add((DLSInstrument)var1);
      }

      if (var1 instanceof DLSSample) {
         this.samples.add((DLSSample)var1);
      }

   }

   public void removeResource(SoundbankResource var1) {
      if (var1 instanceof DLSInstrument) {
         this.instruments.remove((DLSInstrument)var1);
      }

      if (var1 instanceof DLSSample) {
         this.samples.remove((DLSSample)var1);
      }

   }

   public void addInstrument(DLSInstrument var1) {
      this.instruments.add(var1);
   }

   public void removeInstrument(DLSInstrument var1) {
      this.instruments.remove(var1);
   }

   public long getMajor() {
      return this.major;
   }

   public void setMajor(long var1) {
      this.major = var1;
   }

   public long getMinor() {
      return this.minor;
   }

   public void setMinor(long var1) {
      this.minor = var1;
   }

   private static class DLSID {
      long i1;
      int s1;
      int s2;
      int x1;
      int x2;
      int x3;
      int x4;
      int x5;
      int x6;
      int x7;
      int x8;

      private DLSID() {
      }

      DLSID(long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
         this.i1 = var1;
         this.s1 = var3;
         this.s2 = var4;
         this.x1 = var5;
         this.x2 = var6;
         this.x3 = var7;
         this.x4 = var8;
         this.x5 = var9;
         this.x6 = var10;
         this.x7 = var11;
         this.x8 = var12;
      }

      public static DLSSoundbank.DLSID read(RIFFReader var0) throws IOException {
         DLSSoundbank.DLSID var1 = new DLSSoundbank.DLSID();
         var1.i1 = var0.readUnsignedInt();
         var1.s1 = var0.readUnsignedShort();
         var1.s2 = var0.readUnsignedShort();
         var1.x1 = var0.readUnsignedByte();
         var1.x2 = var0.readUnsignedByte();
         var1.x3 = var0.readUnsignedByte();
         var1.x4 = var0.readUnsignedByte();
         var1.x5 = var0.readUnsignedByte();
         var1.x6 = var0.readUnsignedByte();
         var1.x7 = var0.readUnsignedByte();
         var1.x8 = var0.readUnsignedByte();
         return var1;
      }

      public int hashCode() {
         return (int)this.i1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof DLSSoundbank.DLSID)) {
            return false;
         } else {
            DLSSoundbank.DLSID var2 = (DLSSoundbank.DLSID)var1;
            return this.i1 == var2.i1 && this.s1 == var2.s1 && this.s2 == var2.s2 && this.x1 == var2.x1 && this.x2 == var2.x2 && this.x3 == var2.x3 && this.x4 == var2.x4 && this.x5 == var2.x5 && this.x6 == var2.x6 && this.x7 == var2.x7 && this.x8 == var2.x8;
         }
      }
   }
}
