package sun.audio;

import com.sun.media.sound.DataPusher;
import com.sun.media.sound.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioDevice {
   private boolean DEBUG = false;
   private Hashtable clipStreams = new Hashtable();
   private Vector infos = new Vector();
   private boolean playing = false;
   private Mixer mixer = null;
   public static final AudioDevice device = new AudioDevice();

   private AudioDevice() {
   }

   private synchronized void startSampled(AudioInputStream var1, InputStream var2) throws UnsupportedAudioFileException, LineUnavailableException {
      AudioDevice.Info var3 = null;
      DataPusher var4 = null;
      DataLine.Info var5 = null;
      SourceDataLine var6 = null;
      var1 = Toolkit.getPCMConvertedAudioInputStream(var1);
      if (var1 != null) {
         var5 = new DataLine.Info(SourceDataLine.class, var1.getFormat());
         if (AudioSystem.isLineSupported(var5)) {
            var6 = (SourceDataLine)AudioSystem.getLine(var5);
            var4 = new DataPusher(var6, var1);
            var3 = new AudioDevice.Info((Sequencer)null, var2, var4);
            this.infos.addElement(var3);
            var4.start();
         }
      }
   }

   private synchronized void startMidi(InputStream var1, InputStream var2) throws InvalidMidiDataException, MidiUnavailableException {
      Sequencer var3 = null;
      AudioDevice.Info var4 = null;
      var3 = MidiSystem.getSequencer();
      var3.open();

      try {
         var3.setSequence(var1);
      } catch (IOException var6) {
         throw new InvalidMidiDataException(var6.getMessage());
      }

      var4 = new AudioDevice.Info(var3, var2, (DataPusher)null);
      this.infos.addElement(var4);
      var3.addMetaEventListener(var4);
      var3.start();
   }

   public synchronized void openChannel(InputStream var1) {
      if (this.DEBUG) {
         System.out.println("AudioDevice: openChannel");
         System.out.println("input stream =" + var1);
      }

      AudioDevice.Info var2 = null;

      for(int var3 = 0; var3 < this.infos.size(); ++var3) {
         var2 = (AudioDevice.Info)this.infos.elementAt(var3);
         if (var2.in == var1) {
            return;
         }
      }

      AudioInputStream var21 = null;
      if (var1 instanceof AudioStream) {
         if (((AudioStream)var1).midiformat != null) {
            try {
               this.startMidi(((AudioStream)var1).stream, var1);
            } catch (Exception var20) {
               return;
            }
         } else if (((AudioStream)var1).ais != null) {
            try {
               this.startSampled(((AudioStream)var1).ais, var1);
            } catch (Exception var19) {
               return;
            }
         }
      } else if (var1 instanceof AudioDataStream) {
         AudioInputStream var4;
         if (var1 instanceof ContinuousAudioDataStream) {
            try {
               var4 = new AudioInputStream(var1, ((AudioDataStream)var1).getAudioData().format, -1L);
               this.startSampled(var4, var1);
            } catch (Exception var18) {
               return;
            }
         } else {
            try {
               var4 = new AudioInputStream(var1, ((AudioDataStream)var1).getAudioData().format, (long)((AudioDataStream)var1).getAudioData().buffer.length);
               this.startSampled(var4, var1);
            } catch (Exception var17) {
               return;
            }
         }
      } else {
         BufferedInputStream var22 = new BufferedInputStream(var1, 1024);

         try {
            try {
               var21 = AudioSystem.getAudioInputStream((InputStream)var22);
            } catch (IOException var14) {
               return;
            }

            this.startSampled(var21, var1);
         } catch (UnsupportedAudioFileException var15) {
            try {
               try {
                  MidiFileFormat var6 = MidiSystem.getMidiFileFormat((InputStream)var22);
               } catch (IOException var11) {
                  return;
               }

               this.startMidi(var22, var1);
            } catch (InvalidMidiDataException var12) {
               AudioFormat var7 = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);

               try {
                  AudioInputStream var8 = new AudioInputStream(var22, var7, -1L);
                  this.startSampled(var8, var1);
               } catch (UnsupportedAudioFileException var9) {
                  return;
               } catch (LineUnavailableException var10) {
                  return;
               }
            } catch (MidiUnavailableException var13) {
               return;
            }
         } catch (LineUnavailableException var16) {
            return;
         }
      }

      this.notify();
   }

   public synchronized void closeChannel(InputStream var1) {
      if (this.DEBUG) {
         System.out.println("AudioDevice.closeChannel");
      }

      if (var1 != null) {
         for(int var3 = 0; var3 < this.infos.size(); ++var3) {
            AudioDevice.Info var2 = (AudioDevice.Info)this.infos.elementAt(var3);
            if (var2.in == var1) {
               if (var2.sequencer != null) {
                  var2.sequencer.stop();
                  this.infos.removeElement(var2);
               } else if (var2.datapusher != null) {
                  var2.datapusher.stop();
                  this.infos.removeElement(var2);
               }
            }
         }

         this.notify();
      }
   }

   public synchronized void open() {
   }

   public synchronized void close() {
   }

   public void play() {
      if (this.DEBUG) {
         System.out.println("exiting play()");
      }

   }

   public synchronized void closeStreams() {
      for(int var2 = 0; var2 < this.infos.size(); ++var2) {
         AudioDevice.Info var1 = (AudioDevice.Info)this.infos.elementAt(var2);
         if (var1.sequencer != null) {
            var1.sequencer.stop();
            var1.sequencer.close();
            this.infos.removeElement(var1);
         } else if (var1.datapusher != null) {
            var1.datapusher.stop();
            this.infos.removeElement(var1);
         }
      }

      if (this.DEBUG) {
         System.err.println("Audio Device: Streams all closed.");
      }

      this.clipStreams = new Hashtable();
      this.infos = new Vector();
   }

   public int openChannels() {
      return this.infos.size();
   }

   void setVerbose(boolean var1) {
      this.DEBUG = var1;
   }

   final class Info implements MetaEventListener {
      final Sequencer sequencer;
      final InputStream in;
      final DataPusher datapusher;

      Info(Sequencer var2, InputStream var3, DataPusher var4) {
         this.sequencer = var2;
         this.in = var3;
         this.datapusher = var4;
      }

      public void meta(MetaMessage var1) {
         if (var1.getType() == 47 && this.sequencer != null) {
            this.sequencer.close();
         }

      }
   }
}
