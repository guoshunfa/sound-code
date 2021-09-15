package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Transmitter;
import javax.sound.midi.VoiceStatus;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoftSynthesizer implements AudioSynthesizer, ReferenceCountingDevice {
   static final String INFO_NAME = "Gervill";
   static final String INFO_VENDOR = "OpenJDK";
   static final String INFO_DESCRIPTION = "Software MIDI Synthesizer";
   static final String INFO_VERSION = "1.0";
   static final MidiDevice.Info info = new SoftSynthesizer.Info();
   private static SourceDataLine testline = null;
   private static Soundbank defaultSoundBank = null;
   SoftSynthesizer.WeakAudioStream weakstream = null;
   final Object control_mutex = this;
   int voiceIDCounter = 0;
   int voice_allocation_mode = 0;
   boolean load_default_soundbank = false;
   boolean reverb_light = true;
   boolean reverb_on = true;
   boolean chorus_on = true;
   boolean agc_on = true;
   SoftChannel[] channels;
   SoftChannelProxy[] external_channels = null;
   private boolean largemode = false;
   private int gmmode = 0;
   private int deviceid = 0;
   private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
   private SourceDataLine sourceDataLine = null;
   private SoftAudioPusher pusher = null;
   private AudioInputStream pusher_stream = null;
   private float controlrate = 147.0F;
   private boolean open = false;
   private boolean implicitOpen = false;
   private String resamplerType = "linear";
   private SoftResampler resampler = new SoftLinearResampler();
   private int number_of_midi_channels = 16;
   private int maxpoly = 64;
   private long latency = 200000L;
   private boolean jitter_correction = false;
   private SoftMainMixer mainmixer;
   private SoftVoice[] voices;
   private Map<String, SoftTuning> tunings = new HashMap();
   private Map<String, SoftInstrument> inslist = new HashMap();
   private Map<String, ModelInstrument> loadedlist = new HashMap();
   private ArrayList<Receiver> recvslist = new ArrayList();

   private void getBuffers(ModelInstrument var1, List<ModelByteBuffer> var2) {
      ModelPerformer[] var3 = var1.getPerformers();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModelPerformer var6 = var3[var5];
         if (var6.getOscillators() != null) {
            Iterator var7 = var6.getOscillators().iterator();

            while(var7.hasNext()) {
               ModelOscillator var8 = (ModelOscillator)var7.next();
               if (var8 instanceof ModelByteBufferWavetable) {
                  ModelByteBufferWavetable var9 = (ModelByteBufferWavetable)var8;
                  ModelByteBuffer var10 = var9.getBuffer();
                  if (var10 != null) {
                     var2.add(var10);
                  }

                  var10 = var9.get8BitExtensionBuffer();
                  if (var10 != null) {
                     var2.add(var10);
                  }
               }
            }
         }
      }

   }

   private boolean loadSamples(List<ModelInstrument> var1) {
      if (this.largemode) {
         return true;
      } else {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            ModelInstrument var4 = (ModelInstrument)var3.next();
            this.getBuffers(var4, var2);
         }

         try {
            ModelByteBuffer.loadAll(var2);
            return true;
         } catch (IOException var5) {
            return false;
         }
      }
   }

   private boolean loadInstruments(List<ModelInstrument> var1) {
      if (!this.isOpen()) {
         return false;
      } else if (!this.loadSamples(var1)) {
         return false;
      } else {
         synchronized(this.control_mutex) {
            if (this.channels != null) {
               SoftChannel[] var3 = this.channels;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  SoftChannel var6 = var3[var5];
                  var6.current_instrument = null;
                  var6.current_director = null;
               }
            }

            Iterator var9 = var1.iterator();

            while(var9.hasNext()) {
               Instrument var10 = (Instrument)var9.next();
               String var11 = this.patchToString(var10.getPatch());
               SoftInstrument var12 = new SoftInstrument((ModelInstrument)var10);
               this.inslist.put(var11, var12);
               this.loadedlist.put(var11, (ModelInstrument)var10);
            }

            return true;
         }
      }
   }

   private void processPropertyInfo(Map<String, Object> var1) {
      AudioSynthesizerPropertyInfo[] var2 = this.getPropertyInfo(var1);
      String var3 = (String)var2[0].value;
      if (var3.equalsIgnoreCase("point")) {
         this.resampler = new SoftPointResampler();
         this.resamplerType = "point";
      } else if (var3.equalsIgnoreCase("linear")) {
         this.resampler = new SoftLinearResampler2();
         this.resamplerType = "linear";
      } else if (var3.equalsIgnoreCase("linear1")) {
         this.resampler = new SoftLinearResampler();
         this.resamplerType = "linear1";
      } else if (var3.equalsIgnoreCase("linear2")) {
         this.resampler = new SoftLinearResampler2();
         this.resamplerType = "linear2";
      } else if (var3.equalsIgnoreCase("cubic")) {
         this.resampler = new SoftCubicResampler();
         this.resamplerType = "cubic";
      } else if (var3.equalsIgnoreCase("lanczos")) {
         this.resampler = new SoftLanczosResampler();
         this.resamplerType = "lanczos";
      } else if (var3.equalsIgnoreCase("sinc")) {
         this.resampler = new SoftSincResampler();
         this.resamplerType = "sinc";
      }

      this.setFormat((AudioFormat)var2[2].value);
      this.controlrate = (Float)var2[1].value;
      this.latency = (Long)var2[3].value;
      this.deviceid = (Integer)var2[4].value;
      this.maxpoly = (Integer)var2[5].value;
      this.reverb_on = (Boolean)var2[6].value;
      this.chorus_on = (Boolean)var2[7].value;
      this.agc_on = (Boolean)var2[8].value;
      this.largemode = (Boolean)var2[9].value;
      this.number_of_midi_channels = (Integer)var2[10].value;
      this.jitter_correction = (Boolean)var2[11].value;
      this.reverb_light = (Boolean)var2[12].value;
      this.load_default_soundbank = (Boolean)var2[13].value;
   }

   private String patchToString(Patch var1) {
      return var1 instanceof ModelPatch && ((ModelPatch)var1).isPercussion() ? "p." + var1.getProgram() + "." + var1.getBank() : var1.getProgram() + "." + var1.getBank();
   }

   private void setFormat(AudioFormat var1) {
      if (var1.getChannels() > 2) {
         throw new IllegalArgumentException("Only mono and stereo audio supported.");
      } else if (AudioFloatConverter.getConverter(var1) == null) {
         throw new IllegalArgumentException("Audio format not supported.");
      } else {
         this.format = var1;
      }
   }

   void removeReceiver(Receiver var1) {
      boolean var2 = false;
      synchronized(this.control_mutex) {
         if (this.recvslist.remove(var1) && this.implicitOpen && this.recvslist.isEmpty()) {
            var2 = true;
         }
      }

      if (var2) {
         this.close();
      }

   }

   SoftMainMixer getMainMixer() {
      return !this.isOpen() ? null : this.mainmixer;
   }

   SoftInstrument findInstrument(int var1, int var2, int var3) {
      if (var2 >> 7 != 120 && var2 >> 7 != 121) {
         String var6;
         if (var3 == 9) {
            var6 = "p.";
         } else {
            var6 = "";
         }

         SoftInstrument var7 = (SoftInstrument)this.inslist.get(var6 + var1 + "." + var2);
         if (var7 != null) {
            return var7;
         } else {
            var7 = (SoftInstrument)this.inslist.get(var6 + var1 + ".0");
            if (var7 != null) {
               return var7;
            } else {
               var7 = (SoftInstrument)this.inslist.get(var6 + "0.0");
               return var7 != null ? var7 : null;
            }
         }
      } else {
         SoftInstrument var4 = (SoftInstrument)this.inslist.get(var1 + "." + var2);
         if (var4 != null) {
            return var4;
         } else {
            String var5;
            if (var2 >> 7 == 120) {
               var5 = "p.";
            } else {
               var5 = "";
            }

            var4 = (SoftInstrument)this.inslist.get(var5 + var1 + "." + ((var2 & 128) << 7));
            if (var4 != null) {
               return var4;
            } else {
               var4 = (SoftInstrument)this.inslist.get(var5 + var1 + "." + (var2 & 128));
               if (var4 != null) {
                  return var4;
               } else {
                  var4 = (SoftInstrument)this.inslist.get(var5 + var1 + ".0");
                  if (var4 != null) {
                     return var4;
                  } else {
                     var4 = (SoftInstrument)this.inslist.get(var5 + var1 + "0.0");
                     return var4 != null ? var4 : null;
                  }
               }
            }
         }
      }
   }

   int getVoiceAllocationMode() {
      return this.voice_allocation_mode;
   }

   int getGeneralMidiMode() {
      return this.gmmode;
   }

   void setGeneralMidiMode(int var1) {
      this.gmmode = var1;
   }

   int getDeviceID() {
      return this.deviceid;
   }

   float getControlRate() {
      return this.controlrate;
   }

   SoftVoice[] getVoices() {
      return this.voices;
   }

   SoftTuning getTuning(Patch var1) {
      String var2 = this.patchToString(var1);
      SoftTuning var3 = (SoftTuning)this.tunings.get(var2);
      if (var3 == null) {
         var3 = new SoftTuning(var1);
         this.tunings.put(var2, var3);
      }

      return var3;
   }

   public long getLatency() {
      synchronized(this.control_mutex) {
         return this.latency;
      }
   }

   public AudioFormat getFormat() {
      synchronized(this.control_mutex) {
         return this.format;
      }
   }

   public int getMaxPolyphony() {
      synchronized(this.control_mutex) {
         return this.maxpoly;
      }
   }

   public MidiChannel[] getChannels() {
      synchronized(this.control_mutex) {
         if (this.external_channels == null) {
            this.external_channels = new SoftChannelProxy[16];

            for(int var2 = 0; var2 < this.external_channels.length; ++var2) {
               this.external_channels[var2] = new SoftChannelProxy();
            }
         }

         MidiChannel[] var6;
         if (this.isOpen()) {
            var6 = new MidiChannel[this.channels.length];
         } else {
            var6 = new MidiChannel[16];
         }

         for(int var3 = 0; var3 < var6.length; ++var3) {
            var6[var3] = this.external_channels[var3];
         }

         return var6;
      }
   }

   public VoiceStatus[] getVoiceStatus() {
      if (!this.isOpen()) {
         VoiceStatus[] var1 = new VoiceStatus[this.getMaxPolyphony()];

         for(int var8 = 0; var8 < var1.length; ++var8) {
            VoiceStatus var9 = new VoiceStatus();
            var9.active = false;
            var9.bank = 0;
            var9.channel = 0;
            var9.note = 0;
            var9.program = 0;
            var9.volume = 0;
            var1[var8] = var9;
         }

         return var1;
      } else {
         synchronized(this.control_mutex) {
            VoiceStatus[] var2 = new VoiceStatus[this.voices.length];

            for(int var3 = 0; var3 < this.voices.length; ++var3) {
               SoftVoice var4 = this.voices[var3];
               VoiceStatus var5 = new VoiceStatus();
               var5.active = var4.active;
               var5.bank = var4.bank;
               var5.channel = var4.channel;
               var5.note = var4.note;
               var5.program = var4.program;
               var5.volume = var4.volume;
               var2[var3] = var5;
            }

            return var2;
         }
      }
   }

   public boolean isSoundbankSupported(Soundbank var1) {
      Instrument[] var2 = var1.getInstruments();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Instrument var5 = var2[var4];
         if (!(var5 instanceof ModelInstrument)) {
            return false;
         }
      }

      return true;
   }

   public boolean loadInstrument(Instrument var1) {
      if (var1 != null && var1 instanceof ModelInstrument) {
         ArrayList var2 = new ArrayList();
         var2.add((ModelInstrument)var1);
         return this.loadInstruments(var2);
      } else {
         throw new IllegalArgumentException("Unsupported instrument: " + var1);
      }
   }

   public void unloadInstrument(Instrument var1) {
      if (var1 != null && var1 instanceof ModelInstrument) {
         if (this.isOpen()) {
            String var2 = this.patchToString(var1.getPatch());
            synchronized(this.control_mutex) {
               SoftChannel[] var4 = this.channels;
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  SoftChannel var7 = var4[var6];
                  var7.current_instrument = null;
               }

               this.inslist.remove(var2);
               this.loadedlist.remove(var2);

               for(int var10 = 0; var10 < this.channels.length; ++var10) {
                  this.channels[var10].allSoundOff();
               }

            }
         }
      } else {
         throw new IllegalArgumentException("Unsupported instrument: " + var1);
      }
   }

   public boolean remapInstrument(Instrument var1, Instrument var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof ModelInstrument)) {
         throw new IllegalArgumentException("Unsupported instrument: " + var1.toString());
      } else if (!(var2 instanceof ModelInstrument)) {
         throw new IllegalArgumentException("Unsupported instrument: " + var2.toString());
      } else if (!this.isOpen()) {
         return false;
      } else {
         synchronized(this.control_mutex) {
            if (!this.loadedlist.containsValue(var2)) {
               throw new IllegalArgumentException("Instrument to is not loaded.");
            } else {
               this.unloadInstrument(var1);
               ModelMappedInstrument var4 = new ModelMappedInstrument((ModelInstrument)var2, var1.getPatch());
               return this.loadInstrument(var4);
            }
         }
      }
   }

   public Soundbank getDefaultSoundbank() {
      Class var1 = SoftSynthesizer.class;
      synchronized(SoftSynthesizer.class) {
         if (defaultSoundBank != null) {
            return defaultSoundBank;
         }

         ArrayList var2 = new ArrayList();
         var2.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               File var1 = new File(System.getProperties().getProperty("java.home"));
               File var2 = new File(new File(var1, "lib"), "audio");
               if (var2.exists()) {
                  File var3 = null;
                  File[] var4 = var2.listFiles();
                  if (var4 != null) {
                     for(int var5 = 0; var5 < var4.length; ++var5) {
                        File var6 = var4[var5];
                        if (var6.isFile()) {
                           String var7 = var6.getName().toLowerCase();
                           if ((var7.endsWith(".sf2") || var7.endsWith(".dls")) && (var3 == null || var6.length() > var3.length())) {
                              var3 = var6;
                           }
                        }
                     }
                  }

                  if (var3 != null) {
                     try {
                        return new FileInputStream(var3);
                     } catch (IOException var8) {
                     }
                  }
               }

               return null;
            }
         });
         var2.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               if (System.getProperties().getProperty("os.name").startsWith("Linux")) {
                  File[] var1 = new File[]{new File("/usr/share/soundfonts/"), new File("/usr/local/share/soundfonts/"), new File("/usr/share/sounds/sf2/"), new File("/usr/local/share/sounds/sf2/")};
                  File[] var2 = var1;
                  int var3 = var1.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     File var5 = var2[var4];
                     if (var5.exists()) {
                        File var6 = new File(var5, "default.sf2");
                        if (var6.exists()) {
                           try {
                              return new FileInputStream(var6);
                           } catch (IOException var8) {
                           }
                        }
                     }
                  }
               }

               return null;
            }
         });
         var2.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               if (System.getProperties().getProperty("os.name").startsWith("Windows")) {
                  File var1 = new File(System.getenv("SystemRoot") + "\\system32\\drivers\\gm.dls");
                  if (var1.exists()) {
                     try {
                        return new FileInputStream(var1);
                     } catch (IOException var3) {
                     }
                  }
               }

               return null;
            }
         });
         var2.add(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               File var1 = new File(System.getProperty("user.home"), ".gervill");
               File var2 = new File(var1, "soundbank-emg.sf2");
               if (var2.exists()) {
                  try {
                     return new FileInputStream(var2);
                  } catch (IOException var4) {
                  }
               }

               return null;
            }
         });
         Iterator var3 = var2.iterator();

         while(true) {
            if (var3.hasNext()) {
               PrivilegedAction var4 = (PrivilegedAction)var3.next();

               Soundbank var10000;
               try {
                  InputStream var5 = (InputStream)AccessController.doPrivileged(var4);
                  if (var5 == null) {
                     continue;
                  }

                  Soundbank var6;
                  try {
                     var6 = MidiSystem.getSoundbank((InputStream)(new BufferedInputStream(var5)));
                  } finally {
                     var5.close();
                  }

                  if (var6 == null) {
                     continue;
                  }

                  defaultSoundBank = var6;
                  var10000 = defaultSoundBank;
               } catch (Exception var17) {
                  continue;
               }

               return var10000;
            }

            try {
               defaultSoundBank = EmergencySoundbank.createSoundbank();
            } catch (Exception var15) {
            }

            if (defaultSoundBank != null) {
               OutputStream var19 = (OutputStream)AccessController.doPrivileged(() -> {
                  try {
                     File var0 = new File(System.getProperty("user.home"), ".gervill");
                     if (!var0.exists()) {
                        var0.mkdirs();
                     }

                     File var1 = new File(var0, "soundbank-emg.sf2");
                     return var1.exists() ? null : new FileOutputStream(var1);
                  } catch (FileNotFoundException var2) {
                     return null;
                  }
               });
               if (var19 != null) {
                  try {
                     ((SF2Soundbank)defaultSoundBank).save(var19);
                     var19.close();
                  } catch (IOException var14) {
                  }
               }
            }
            break;
         }
      }

      return defaultSoundBank;
   }

   public Instrument[] getAvailableInstruments() {
      Soundbank var1 = this.getDefaultSoundbank();
      if (var1 == null) {
         return new Instrument[0];
      } else {
         Instrument[] var2 = var1.getInstruments();
         Arrays.sort(var2, new ModelInstrumentComparator());
         return var2;
      }
   }

   public Instrument[] getLoadedInstruments() {
      if (!this.isOpen()) {
         return new Instrument[0];
      } else {
         synchronized(this.control_mutex) {
            ModelInstrument[] var2 = new ModelInstrument[this.loadedlist.values().size()];
            this.loadedlist.values().toArray(var2);
            Arrays.sort(var2, new ModelInstrumentComparator());
            return var2;
         }
      }
   }

   public boolean loadAllInstruments(Soundbank var1) {
      ArrayList var2 = new ArrayList();
      Instrument[] var3 = var1.getInstruments();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Instrument var6 = var3[var5];
         if (var6 == null || !(var6 instanceof ModelInstrument)) {
            throw new IllegalArgumentException("Unsupported instrument: " + var6);
         }

         var2.add((ModelInstrument)var6);
      }

      return this.loadInstruments(var2);
   }

   public void unloadAllInstruments(Soundbank var1) {
      if (var1 != null && this.isSoundbankSupported(var1)) {
         if (this.isOpen()) {
            Instrument[] var2 = var1.getInstruments();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Instrument var5 = var2[var4];
               if (var5 instanceof ModelInstrument) {
                  this.unloadInstrument(var5);
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Unsupported soundbank: " + var1);
      }
   }

   public boolean loadInstruments(Soundbank var1, Patch[] var2) {
      ArrayList var3 = new ArrayList();
      Patch[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Patch var7 = var4[var6];
         Instrument var8 = var1.getInstrument(var7);
         if (var8 == null || !(var8 instanceof ModelInstrument)) {
            throw new IllegalArgumentException("Unsupported instrument: " + var8);
         }

         var3.add((ModelInstrument)var8);
      }

      return this.loadInstruments(var3);
   }

   public void unloadInstruments(Soundbank var1, Patch[] var2) {
      if (var1 != null && this.isSoundbankSupported(var1)) {
         if (this.isOpen()) {
            Patch[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Patch var6 = var3[var5];
               Instrument var7 = var1.getInstrument(var6);
               if (var7 instanceof ModelInstrument) {
                  this.unloadInstrument(var7);
               }
            }

         }
      } else {
         throw new IllegalArgumentException("Unsupported soundbank: " + var1);
      }
   }

   public MidiDevice.Info getDeviceInfo() {
      return info;
   }

   private Properties getStoredProperties() {
      return (Properties)AccessController.doPrivileged(() -> {
         Properties var0 = new Properties();
         String var1 = "/com/sun/media/sound/softsynthesizer";

         try {
            Preferences var2 = Preferences.userRoot();
            if (var2.nodeExists(var1)) {
               Preferences var3 = var2.node(var1);
               String[] var4 = var3.keys();
               String[] var5 = var4;
               int var6 = var4.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  String var8 = var5[var7];
                  String var9 = var3.get(var8, (String)null);
                  if (var9 != null) {
                     var0.setProperty(var8, var9);
                  }
               }
            }
         } catch (BackingStoreException var10) {
         }

         return var0;
      });
   }

   public AudioSynthesizerPropertyInfo[] getPropertyInfo(Map<String, Object> var1) {
      ArrayList var2 = new ArrayList();
      boolean var4 = var1 == null && this.open;
      AudioSynthesizerPropertyInfo var3 = new AudioSynthesizerPropertyInfo("interpolation", var4 ? this.resamplerType : "linear");
      var3.choices = new String[]{"linear", "linear1", "linear2", "cubic", "lanczos", "sinc", "point"};
      var3.description = "Interpolation method";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("control rate", var4 ? this.controlrate : 147.0F);
      var3.description = "Control rate";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("format", var4 ? this.format : new AudioFormat(44100.0F, 16, 2, true, false));
      var3.description = "Default audio format";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("latency", var4 ? this.latency : 120000L);
      var3.description = "Default latency";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("device id", var4 ? this.deviceid : 0);
      var3.description = "Device ID for SysEx Messages";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("max polyphony", var4 ? this.maxpoly : 64);
      var3.description = "Maximum polyphony";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("reverb", var4 ? this.reverb_on : true);
      var3.description = "Turn reverb effect on or off";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("chorus", var4 ? this.chorus_on : true);
      var3.description = "Turn chorus effect on or off";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("auto gain control", var4 ? this.agc_on : true);
      var3.description = "Turn auto gain control on or off";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("large mode", var4 ? this.largemode : false);
      var3.description = "Turn large mode on or off.";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("midi channels", var4 ? this.channels.length : 16);
      var3.description = "Number of midi channels.";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("jitter correction", var4 ? this.jitter_correction : true);
      var3.description = "Turn jitter correction on or off.";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("light reverb", var4 ? this.reverb_light : true);
      var3.description = "Turn light reverb mode on or off";
      var2.add(var3);
      var3 = new AudioSynthesizerPropertyInfo("load default soundbank", var4 ? this.load_default_soundbank : true);
      var3.description = "Enabled/disable loading default soundbank";
      var2.add(var3);
      AudioSynthesizerPropertyInfo[] var5 = (AudioSynthesizerPropertyInfo[])var2.toArray(new AudioSynthesizerPropertyInfo[var2.size()]);
      Properties var6 = this.getStoredProperties();
      AudioSynthesizerPropertyInfo[] var7 = var5;
      int var8 = var5.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         AudioSynthesizerPropertyInfo var10 = var7[var9];
         Object var11 = var1 == null ? null : var1.get(var10.name);
         var11 = var11 != null ? var11 : var6.getProperty(var10.name);
         if (var11 != null) {
            Class var12 = var10.valueClass;
            if (var12.isInstance(var11)) {
               var10.value = var11;
            } else if (var11 instanceof String) {
               String var24 = (String)var11;
               if (var12 == Boolean.class) {
                  if (var24.equalsIgnoreCase("true")) {
                     var10.value = Boolean.TRUE;
                  }

                  if (var24.equalsIgnoreCase("false")) {
                     var10.value = Boolean.FALSE;
                  }
               } else if (var12 == AudioFormat.class) {
                  int var14 = 2;
                  boolean var15 = true;
                  boolean var16 = false;
                  int var17 = 16;
                  float var18 = 44100.0F;

                  try {
                     StringTokenizer var19 = new StringTokenizer(var24, ", ");

                     String var21;
                     for(String var20 = ""; var19.hasMoreTokens(); var20 = var21) {
                        var21 = var19.nextToken().toLowerCase();
                        if (var21.equals("mono")) {
                           var14 = 1;
                        }

                        if (var21.startsWith("channel")) {
                           var14 = Integer.parseInt(var20);
                        }

                        if (var21.contains("unsigned")) {
                           var15 = false;
                        }

                        if (var21.equals("big-endian")) {
                           var16 = true;
                        }

                        if (var21.equals("bit")) {
                           var17 = Integer.parseInt(var20);
                        }

                        if (var21.equals("hz")) {
                           var18 = Float.parseFloat(var20);
                        }
                     }

                     var10.value = new AudioFormat(var18, var17, var14, var15, var16);
                  } catch (NumberFormatException var23) {
                  }
               } else {
                  try {
                     if (var12 == Byte.class) {
                        var10.value = Byte.valueOf(var24);
                     } else if (var12 == Short.class) {
                        var10.value = Short.valueOf(var24);
                     } else if (var12 == Integer.class) {
                        var10.value = Integer.valueOf(var24);
                     } else if (var12 == Long.class) {
                        var10.value = Long.valueOf(var24);
                     } else if (var12 == Float.class) {
                        var10.value = Float.valueOf(var24);
                     } else if (var12 == Double.class) {
                        var10.value = Double.valueOf(var24);
                     }
                  } catch (NumberFormatException var22) {
                  }
               }
            } else if (var11 instanceof Number) {
               Number var13 = (Number)var11;
               if (var12 == Byte.class) {
                  var10.value = var13.byteValue();
               }

               if (var12 == Short.class) {
                  var10.value = var13.shortValue();
               }

               if (var12 == Integer.class) {
                  var10.value = var13.intValue();
               }

               if (var12 == Long.class) {
                  var10.value = var13.longValue();
               }

               if (var12 == Float.class) {
                  var10.value = var13.floatValue();
               }

               if (var12 == Double.class) {
                  var10.value = var13.doubleValue();
               }
            }
         }
      }

      return var5;
   }

   public void open() throws MidiUnavailableException {
      if (this.isOpen()) {
         synchronized(this.control_mutex) {
            this.implicitOpen = false;
         }
      } else {
         this.open((SourceDataLine)null, (Map)null);
      }
   }

   public void open(SourceDataLine var1, Map<String, Object> var2) throws MidiUnavailableException {
      if (this.isOpen()) {
         synchronized(this.control_mutex) {
            this.implicitOpen = false;
         }
      } else {
         synchronized(this.control_mutex) {
            try {
               if (var1 != null) {
                  this.setFormat(var1.getFormat());
               }

               AudioInputStream var4 = this.openStream(this.getFormat(), var2);
               this.weakstream = new SoftSynthesizer.WeakAudioStream(var4);
               Object var14 = this.weakstream.getAudioInputStream();
               if (var1 == null) {
                  if (testline != null) {
                     var1 = testline;
                  } else {
                     var1 = AudioSystem.getSourceDataLine(this.getFormat());
                  }
               }

               double var15 = (double)this.latency;
               int var7;
               if (!var1.isOpen()) {
                  var7 = this.getFormat().getFrameSize() * (int)((double)this.getFormat().getFrameRate() * (var15 / 1000000.0D));
                  var1.open(this.getFormat(), var7);
                  this.sourceDataLine = var1;
               }

               if (!var1.isActive()) {
                  var1.start();
               }

               var7 = 512;

               try {
                  var7 = ((AudioInputStream)var14).available();
               } catch (IOException var11) {
               }

               int var8 = var1.getBufferSize();
               var8 -= var8 % var7;
               if (var8 < 3 * var7) {
                  var8 = 3 * var7;
               }

               if (this.jitter_correction) {
                  var14 = new SoftJitterCorrector((AudioInputStream)var14, var8, var7);
                  if (this.weakstream != null) {
                     this.weakstream.jitter_stream = (AudioInputStream)var14;
                  }
               }

               this.pusher = new SoftAudioPusher(var1, (AudioInputStream)var14, var7);
               this.pusher_stream = (AudioInputStream)var14;
               this.pusher.start();
               if (this.weakstream != null) {
                  this.weakstream.pusher = this.pusher;
                  this.weakstream.sourceDataLine = this.sourceDataLine;
               }
            } catch (SecurityException | IllegalArgumentException | LineUnavailableException var12) {
               if (this.isOpen()) {
                  this.close();
               }

               MidiUnavailableException var5 = new MidiUnavailableException("Can not open line");
               var5.initCause(var12);
               throw var5;
            }

         }
      }
   }

   public AudioInputStream openStream(AudioFormat var1, Map<String, Object> var2) throws MidiUnavailableException {
      if (this.isOpen()) {
         throw new MidiUnavailableException("Synthesizer is already open");
      } else {
         synchronized(this.control_mutex) {
            this.gmmode = 0;
            this.voice_allocation_mode = 0;
            this.processPropertyInfo(var2);
            this.open = true;
            this.implicitOpen = false;
            if (var1 != null) {
               this.setFormat(var1);
            }

            if (this.load_default_soundbank) {
               Soundbank var4 = this.getDefaultSoundbank();
               if (var4 != null) {
                  this.loadAllInstruments(var4);
               }
            }

            this.voices = new SoftVoice[this.maxpoly];

            int var10;
            for(var10 = 0; var10 < this.maxpoly; ++var10) {
               this.voices[var10] = new SoftVoice(this);
            }

            this.mainmixer = new SoftMainMixer(this);
            this.channels = new SoftChannel[this.number_of_midi_channels];

            for(var10 = 0; var10 < this.channels.length; ++var10) {
               this.channels[var10] = new SoftChannel(this, var10);
            }

            int var5;
            if (this.external_channels == null) {
               if (this.channels.length < 16) {
                  this.external_channels = new SoftChannelProxy[16];
               } else {
                  this.external_channels = new SoftChannelProxy[this.channels.length];
               }

               for(var10 = 0; var10 < this.external_channels.length; ++var10) {
                  this.external_channels[var10] = new SoftChannelProxy();
               }
            } else if (this.channels.length > this.external_channels.length) {
               SoftChannelProxy[] var11 = new SoftChannelProxy[this.channels.length];

               for(var5 = 0; var5 < this.external_channels.length; ++var5) {
                  var11[var5] = this.external_channels[var5];
               }

               for(var5 = this.external_channels.length; var5 < var11.length; ++var5) {
                  var11[var5] = new SoftChannelProxy();
               }
            }

            for(var10 = 0; var10 < this.channels.length; ++var10) {
               this.external_channels[var10].setChannel(this.channels[var10]);
            }

            SoftVoice[] var14 = this.getVoices();
            var5 = var14.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               SoftVoice var7 = var14[var6];
               var7.resampler = this.resampler.openStreamer();
            }

            SoftReceiver var13;
            for(Iterator var15 = this.getReceivers().iterator(); var15.hasNext(); var13.midimessages = this.mainmixer.midimessages) {
               Receiver var12 = (Receiver)var15.next();
               var13 = (SoftReceiver)var12;
               var13.open = this.open;
               var13.mainmixer = this.mainmixer;
            }

            return this.mainmixer.getInputStream();
         }
      }
   }

   public void close() {
      if (this.isOpen()) {
         SoftAudioPusher var1 = null;
         AudioInputStream var2 = null;
         synchronized(this.control_mutex) {
            if (this.pusher != null) {
               var1 = this.pusher;
               var2 = this.pusher_stream;
               this.pusher = null;
               this.pusher_stream = null;
            }
         }

         if (var1 != null) {
            var1.stop();

            try {
               var2.close();
            } catch (IOException var6) {
            }
         }

         synchronized(this.control_mutex) {
            if (this.mainmixer != null) {
               this.mainmixer.close();
            }

            this.open = false;
            this.implicitOpen = false;
            this.mainmixer = null;
            this.voices = null;
            this.channels = null;
            if (this.external_channels != null) {
               for(int var4 = 0; var4 < this.external_channels.length; ++var4) {
                  this.external_channels[var4].setChannel((MidiChannel)null);
               }
            }

            if (this.sourceDataLine != null) {
               this.sourceDataLine.close();
               this.sourceDataLine = null;
            }

            this.inslist.clear();
            this.loadedlist.clear();
            this.tunings.clear();

            while(this.recvslist.size() != 0) {
               ((Receiver)this.recvslist.get(this.recvslist.size() - 1)).close();
            }

         }
      }
   }

   public boolean isOpen() {
      synchronized(this.control_mutex) {
         return this.open;
      }
   }

   public long getMicrosecondPosition() {
      if (!this.isOpen()) {
         return 0L;
      } else {
         synchronized(this.control_mutex) {
            return this.mainmixer.getMicrosecondPosition();
         }
      }
   }

   public int getMaxReceivers() {
      return -1;
   }

   public int getMaxTransmitters() {
      return 0;
   }

   public Receiver getReceiver() throws MidiUnavailableException {
      synchronized(this.control_mutex) {
         SoftReceiver var2 = new SoftReceiver(this);
         var2.open = this.open;
         this.recvslist.add(var2);
         return var2;
      }
   }

   public List<Receiver> getReceivers() {
      synchronized(this.control_mutex) {
         ArrayList var2 = new ArrayList();
         var2.addAll(this.recvslist);
         return var2;
      }
   }

   public Transmitter getTransmitter() throws MidiUnavailableException {
      throw new MidiUnavailableException("No transmitter available");
   }

   public List<Transmitter> getTransmitters() {
      return new ArrayList();
   }

   public Receiver getReceiverReferenceCounting() throws MidiUnavailableException {
      if (!this.isOpen()) {
         this.open();
         synchronized(this.control_mutex) {
            this.implicitOpen = true;
         }
      }

      return this.getReceiver();
   }

   public Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException {
      throw new MidiUnavailableException("No transmitter available");
   }

   private static class Info extends MidiDevice.Info {
      Info() {
         super("Gervill", "OpenJDK", "Software MIDI Synthesizer", "1.0");
      }
   }

   protected static final class WeakAudioStream extends InputStream {
      private volatile AudioInputStream stream;
      public SoftAudioPusher pusher = null;
      public AudioInputStream jitter_stream = null;
      public SourceDataLine sourceDataLine = null;
      public volatile long silent_samples = 0L;
      private int framesize = 0;
      private WeakReference<AudioInputStream> weak_stream_link;
      private AudioFloatConverter converter;
      private float[] silentbuffer = null;
      private int samplesize;

      public void setInputStream(AudioInputStream var1) {
         this.stream = var1;
      }

      public int available() throws IOException {
         AudioInputStream var1 = this.stream;
         return var1 != null ? var1.available() : 0;
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         return this.read(var1) == -1 ? -1 : var1[0] & 255;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         AudioInputStream var4 = this.stream;
         if (var4 != null) {
            return var4.read(var1, var2, var3);
         } else {
            int var5 = var3 / this.samplesize;
            if (this.silentbuffer == null || this.silentbuffer.length < var5) {
               this.silentbuffer = new float[var5];
            }

            this.converter.toByteArray(this.silentbuffer, var5, var1, var2);
            this.silent_samples += (long)(var3 / this.framesize);
            if (this.pusher != null && this.weak_stream_link.get() == null) {
               Runnable var6 = new Runnable() {
                  SoftAudioPusher _pusher;
                  AudioInputStream _jitter_stream;
                  SourceDataLine _sourceDataLine;

                  {
                     this._pusher = WeakAudioStream.this.pusher;
                     this._jitter_stream = WeakAudioStream.this.jitter_stream;
                     this._sourceDataLine = WeakAudioStream.this.sourceDataLine;
                  }

                  public void run() {
                     this._pusher.stop();
                     if (this._jitter_stream != null) {
                        try {
                           this._jitter_stream.close();
                        } catch (IOException var2) {
                           var2.printStackTrace();
                        }
                     }

                     if (this._sourceDataLine != null) {
                        this._sourceDataLine.close();
                     }

                  }
               };
               this.pusher = null;
               this.jitter_stream = null;
               this.sourceDataLine = null;
               (new Thread(var6)).start();
            }

            return var3;
         }
      }

      public WeakAudioStream(AudioInputStream var1) {
         this.stream = var1;
         this.weak_stream_link = new WeakReference(var1);
         this.converter = AudioFloatConverter.getConverter(var1.getFormat());
         this.samplesize = var1.getFormat().getFrameSize() / var1.getFormat().getChannels();
         this.framesize = var1.getFormat().getFrameSize();
      }

      public AudioInputStream getAudioInputStream() {
         return new AudioInputStream(this, this.stream.getFormat(), -1L);
      }

      public void close() throws IOException {
         AudioInputStream var1 = (AudioInputStream)this.weak_stream_link.get();
         if (var1 != null) {
            var1.close();
         }

      }
   }
}
