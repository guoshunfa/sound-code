package com.sun.media.sound;

import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class JavaSoundAudioClip implements AudioClip, MetaEventListener, LineListener {
   private static final boolean DEBUG = false;
   private static final int BUFFER_SIZE = 16384;
   private long lastPlayCall = 0L;
   private static final int MINIMUM_PLAY_DELAY = 30;
   private byte[] loadedAudio = null;
   private int loadedAudioByteLength = 0;
   private AudioFormat loadedAudioFormat = null;
   private AutoClosingClip clip = null;
   private boolean clipLooping = false;
   private DataPusher datapusher = null;
   private Sequencer sequencer = null;
   private Sequence sequence = null;
   private boolean sequencerloop = false;
   private static final long CLIP_THRESHOLD = 1048576L;
   private static final int STREAM_BUFFER_SIZE = 1024;

   public JavaSoundAudioClip(InputStream var1) throws IOException {
      BufferedInputStream var2 = new BufferedInputStream(var1, 1024);
      var2.mark(1024);
      boolean var3 = false;

      try {
         AudioInputStream var4 = AudioSystem.getAudioInputStream((InputStream)var2);
         var3 = this.loadAudioData(var4);
         if (var3) {
            var3 = false;
            if ((long)this.loadedAudioByteLength < 1048576L) {
               var3 = this.createClip();
            }

            if (!var3) {
               var3 = this.createSourceDataLine();
            }
         }
      } catch (UnsupportedAudioFileException var7) {
         try {
            MidiFileFormat var5 = MidiSystem.getMidiFileFormat((InputStream)var2);
            var3 = this.createSequencer(var2);
         } catch (InvalidMidiDataException var6) {
            var3 = false;
         }
      }

      if (!var3) {
         throw new IOException("Unable to create AudioClip from input stream");
      }
   }

   public synchronized void play() {
      this.startImpl(false);
   }

   public synchronized void loop() {
      this.startImpl(true);
   }

   private synchronized void startImpl(boolean var1) {
      long var2 = System.currentTimeMillis();
      long var4 = var2 - this.lastPlayCall;
      if (var4 >= 30L) {
         this.lastPlayCall = var2;

         try {
            if (this.clip != null) {
               if (!this.clip.isOpen()) {
                  this.clip.open(this.loadedAudioFormat, this.loadedAudio, 0, this.loadedAudioByteLength);
               } else {
                  this.clip.flush();
                  if (var1 != this.clipLooping) {
                     this.clip.stop();
                  }
               }

               this.clip.setFramePosition(0);
               if (var1) {
                  this.clip.loop(-1);
               } else {
                  this.clip.start();
               }

               this.clipLooping = var1;
            } else if (this.datapusher != null) {
               this.datapusher.start(var1);
            } else if (this.sequencer != null) {
               this.sequencerloop = var1;
               if (this.sequencer.isRunning()) {
                  this.sequencer.setMicrosecondPosition(0L);
               }

               if (!this.sequencer.isOpen()) {
                  try {
                     this.sequencer.open();
                     this.sequencer.setSequence(this.sequence);
                  } catch (InvalidMidiDataException var8) {
                  } catch (MidiUnavailableException var9) {
                  }
               }

               this.sequencer.addMetaEventListener(this);

               try {
                  this.sequencer.start();
               } catch (Exception var7) {
               }
            }
         } catch (Exception var10) {
         }

      }
   }

   public synchronized void stop() {
      this.lastPlayCall = 0L;
      if (this.clip != null) {
         try {
            this.clip.flush();
         } catch (Exception var5) {
         }

         try {
            this.clip.stop();
         } catch (Exception var4) {
         }
      } else if (this.datapusher != null) {
         this.datapusher.stop();
      } else if (this.sequencer != null) {
         try {
            this.sequencerloop = false;
            this.sequencer.addMetaEventListener(this);
            this.sequencer.stop();
         } catch (Exception var3) {
         }

         try {
            this.sequencer.close();
         } catch (Exception var2) {
         }
      }

   }

   public synchronized void update(LineEvent var1) {
   }

   public synchronized void meta(MetaMessage var1) {
      if (var1.getType() == 47) {
         if (this.sequencerloop) {
            this.sequencer.setMicrosecondPosition(0L);
            this.loop();
         } else {
            this.stop();
         }
      }

   }

   public String toString() {
      return this.getClass().toString();
   }

   protected void finalize() {
      if (this.clip != null) {
         this.clip.close();
      }

      if (this.datapusher != null) {
         this.datapusher.close();
      }

      if (this.sequencer != null) {
         this.sequencer.close();
      }

   }

   private boolean loadAudioData(AudioInputStream var1) throws IOException, UnsupportedAudioFileException {
      var1 = Toolkit.getPCMConvertedAudioInputStream(var1);
      if (var1 == null) {
         return false;
      } else {
         this.loadedAudioFormat = var1.getFormat();
         long var2 = var1.getFrameLength();
         int var4 = this.loadedAudioFormat.getFrameSize();
         long var5 = -1L;
         if (var2 != -1L && var2 > 0L && var4 != -1 && var4 > 0) {
            var5 = var2 * (long)var4;
         }

         if (var5 != -1L) {
            this.readStream(var1, var5);
         } else {
            this.readStream(var1);
         }

         return true;
      }
   }

   private void readStream(AudioInputStream var1, long var2) throws IOException {
      int var4;
      if (var2 > 2147483647L) {
         var4 = Integer.MAX_VALUE;
      } else {
         var4 = (int)var2;
      }

      this.loadedAudio = new byte[var4];
      this.loadedAudioByteLength = 0;

      while(true) {
         int var5 = var1.read(this.loadedAudio, this.loadedAudioByteLength, var4 - this.loadedAudioByteLength);
         if (var5 <= 0) {
            var1.close();
            return;
         }

         this.loadedAudioByteLength += var5;
      }
   }

   private void readStream(AudioInputStream var1) throws IOException {
      JavaSoundAudioClip.DirectBAOS var2 = new JavaSoundAudioClip.DirectBAOS();
      byte[] var3 = new byte[16384];
      boolean var4 = false;
      int var5 = 0;

      while(true) {
         int var6 = var1.read(var3, 0, var3.length);
         if (var6 <= 0) {
            var1.close();
            this.loadedAudio = var2.getInternalBuffer();
            this.loadedAudioByteLength = var5;
            return;
         }

         var5 += var6;
         var2.write(var3, 0, var6);
      }
   }

   private boolean createClip() {
      try {
         DataLine.Info var1 = new DataLine.Info(Clip.class, this.loadedAudioFormat);
         if (!AudioSystem.isLineSupported(var1)) {
            return false;
         }

         Line var2 = AudioSystem.getLine(var1);
         if (!(var2 instanceof AutoClosingClip)) {
            return false;
         }

         this.clip = (AutoClosingClip)var2;
         this.clip.setAutoClosing(true);
      } catch (Exception var3) {
         return false;
      }

      return this.clip != null;
   }

   private boolean createSourceDataLine() {
      try {
         DataLine.Info var1 = new DataLine.Info(SourceDataLine.class, this.loadedAudioFormat);
         if (!AudioSystem.isLineSupported(var1)) {
            return false;
         }

         SourceDataLine var2 = (SourceDataLine)AudioSystem.getLine(var1);
         this.datapusher = new DataPusher(var2, this.loadedAudioFormat, this.loadedAudio, this.loadedAudioByteLength);
      } catch (Exception var3) {
         return false;
      }

      return this.datapusher != null;
   }

   private boolean createSequencer(BufferedInputStream var1) throws IOException {
      try {
         this.sequencer = MidiSystem.getSequencer();
      } catch (MidiUnavailableException var4) {
         return false;
      }

      if (this.sequencer == null) {
         return false;
      } else {
         try {
            this.sequence = MidiSystem.getSequence((InputStream)var1);
            return this.sequence != null;
         } catch (InvalidMidiDataException var3) {
            return false;
         }
      }
   }

   private static class DirectBAOS extends ByteArrayOutputStream {
      DirectBAOS() {
      }

      public byte[] getInternalBuffer() {
         return this.buf;
      }
   }
}
