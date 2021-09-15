package com.sun.media.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public abstract class SoftMixingDataLine implements DataLine {
   public static final FloatControl.Type CHORUS_SEND = new FloatControl.Type("Chorus Send") {
   };
   private final SoftMixingDataLine.Gain gain_control = new SoftMixingDataLine.Gain();
   private final SoftMixingDataLine.Mute mute_control = new SoftMixingDataLine.Mute();
   private final SoftMixingDataLine.Balance balance_control = new SoftMixingDataLine.Balance();
   private final SoftMixingDataLine.Pan pan_control = new SoftMixingDataLine.Pan();
   private final SoftMixingDataLine.ReverbSend reverbsend_control = new SoftMixingDataLine.ReverbSend();
   private final SoftMixingDataLine.ChorusSend chorussend_control = new SoftMixingDataLine.ChorusSend();
   private final SoftMixingDataLine.ApplyReverb apply_reverb = new SoftMixingDataLine.ApplyReverb();
   private final Control[] controls;
   float leftgain = 1.0F;
   float rightgain = 1.0F;
   float eff1gain = 0.0F;
   float eff2gain = 0.0F;
   List<LineListener> listeners = new ArrayList();
   final Object control_mutex;
   SoftMixingMixer mixer;
   DataLine.Info info;

   protected abstract void processControlLogic();

   protected abstract void processAudioLogic(SoftAudioBuffer[] var1);

   SoftMixingDataLine(SoftMixingMixer var1, DataLine.Info var2) {
      this.mixer = var1;
      this.info = var2;
      this.control_mutex = var1.control_mutex;
      this.controls = new Control[]{this.gain_control, this.mute_control, this.balance_control, this.pan_control, this.reverbsend_control, this.chorussend_control, this.apply_reverb};
      this.calcVolume();
   }

   final void calcVolume() {
      synchronized(this.control_mutex) {
         double var2 = Math.pow(10.0D, (double)this.gain_control.getValue() / 20.0D);
         if (this.mute_control.getValue()) {
            var2 = 0.0D;
         }

         this.leftgain = (float)var2;
         this.rightgain = (float)var2;
         if (this.mixer.getFormat().getChannels() > 1) {
            double var4 = (double)this.balance_control.getValue();
            if (var4 > 0.0D) {
               this.leftgain = (float)((double)this.leftgain * (1.0D - var4));
            } else {
               this.rightgain = (float)((double)this.rightgain * (1.0D + var4));
            }
         }
      }

      this.eff1gain = (float)Math.pow(10.0D, (double)this.reverbsend_control.getValue() / 20.0D);
      this.eff2gain = (float)Math.pow(10.0D, (double)this.chorussend_control.getValue() / 20.0D);
      if (!this.apply_reverb.getValue()) {
         this.eff1gain = 0.0F;
      }

   }

   final void sendEvent(LineEvent var1) {
      if (this.listeners.size() != 0) {
         LineListener[] var2 = (LineListener[])this.listeners.toArray(new LineListener[this.listeners.size()]);
         LineListener[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            LineListener var6 = var3[var5];
            var6.update(var1);
         }

      }
   }

   public final void addLineListener(LineListener var1) {
      synchronized(this.control_mutex) {
         this.listeners.add(var1);
      }
   }

   public final void removeLineListener(LineListener var1) {
      synchronized(this.control_mutex) {
         this.listeners.add(var1);
      }
   }

   public final Line.Info getLineInfo() {
      return this.info;
   }

   public final Control getControl(Control.Type var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < this.controls.length; ++var2) {
            if (this.controls[var2].getType() == var1) {
               return this.controls[var2];
            }
         }
      }

      throw new IllegalArgumentException("Unsupported control type : " + var1);
   }

   public final Control[] getControls() {
      return (Control[])Arrays.copyOf((Object[])this.controls, this.controls.length);
   }

   public final boolean isControlSupported(Control.Type var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < this.controls.length; ++var2) {
            if (this.controls[var2].getType() == var1) {
               return true;
            }
         }
      }

      return false;
   }

   private final class ChorusSend extends FloatControl {
      private ChorusSend() {
         super(SoftMixingDataLine.CHORUS_SEND, -80.0F, 6.0206F, 0.625F, -1, -80.0F, "dB", "Minimum", "", "Maximum");
      }

      public void setValue(float var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.balance_control.setValue(var1);
      }

      // $FF: synthetic method
      ChorusSend(Object var2) {
         this();
      }
   }

   private final class ReverbSend extends FloatControl {
      private ReverbSend() {
         super(FloatControl.Type.REVERB_SEND, -80.0F, 6.0206F, 0.625F, -1, -80.0F, "dB", "Minimum", "", "Maximum");
      }

      public void setValue(float var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.balance_control.setValue(var1);
      }

      // $FF: synthetic method
      ReverbSend(Object var2) {
         this();
      }
   }

   private final class Pan extends FloatControl {
      private Pan() {
         super(FloatControl.Type.PAN, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right");
      }

      public void setValue(float var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.balance_control.setValue(var1);
      }

      public float getValue() {
         return SoftMixingDataLine.this.balance_control.getValue();
      }

      // $FF: synthetic method
      Pan(Object var2) {
         this();
      }
   }

   private final class Balance extends FloatControl {
      private Balance() {
         super(FloatControl.Type.BALANCE, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right");
      }

      public void setValue(float var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.calcVolume();
      }

      // $FF: synthetic method
      Balance(Object var2) {
         this();
      }
   }

   private final class ApplyReverb extends BooleanControl {
      private ApplyReverb() {
         super(BooleanControl.Type.APPLY_REVERB, false, "True", "False");
      }

      public void setValue(boolean var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.calcVolume();
      }

      // $FF: synthetic method
      ApplyReverb(Object var2) {
         this();
      }
   }

   private final class Mute extends BooleanControl {
      private Mute() {
         super(BooleanControl.Type.MUTE, false, "True", "False");
      }

      public void setValue(boolean var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.calcVolume();
      }

      // $FF: synthetic method
      Mute(Object var2) {
         this();
      }
   }

   private final class Gain extends FloatControl {
      private Gain() {
         super(FloatControl.Type.MASTER_GAIN, -80.0F, 6.0206F, 0.625F, -1, 0.0F, "dB", "Minimum", "", "Maximum");
      }

      public void setValue(float var1) {
         super.setValue(var1);
         SoftMixingDataLine.this.calcVolume();
      }

      // $FF: synthetic method
      Gain(Object var2) {
         this();
      }
   }

   protected static final class AudioFloatInputStreamResampler extends AudioFloatInputStream {
      private final AudioFloatInputStream ais;
      private final AudioFormat targetFormat;
      private float[] skipbuffer;
      private SoftAbstractResampler resampler;
      private final float[] pitch = new float[1];
      private final float[] ibuffer2;
      private final float[][] ibuffer;
      private float ibuffer_index = 0.0F;
      private int ibuffer_len = 0;
      private int nrofchannels = 0;
      private float[][] cbuffer;
      private final int buffer_len = 512;
      private final int pad;
      private final int pad2;
      private final float[] ix = new float[1];
      private final int[] ox = new int[1];
      private float[][] mark_ibuffer = (float[][])null;
      private float mark_ibuffer_index = 0.0F;
      private int mark_ibuffer_len = 0;

      public AudioFloatInputStreamResampler(AudioFloatInputStream var1, AudioFormat var2) {
         this.ais = var1;
         AudioFormat var3 = var1.getFormat();
         this.targetFormat = new AudioFormat(var3.getEncoding(), var2.getSampleRate(), var3.getSampleSizeInBits(), var3.getChannels(), var3.getFrameSize(), var2.getSampleRate(), var3.isBigEndian());
         this.nrofchannels = this.targetFormat.getChannels();
         Object var4 = var2.getProperty("interpolation");
         if (var4 != null && var4 instanceof String) {
            String var5 = (String)var4;
            if (var5.equalsIgnoreCase("point")) {
               this.resampler = new SoftPointResampler();
            }

            if (var5.equalsIgnoreCase("linear")) {
               this.resampler = new SoftLinearResampler2();
            }

            if (var5.equalsIgnoreCase("linear1")) {
               this.resampler = new SoftLinearResampler();
            }

            if (var5.equalsIgnoreCase("linear2")) {
               this.resampler = new SoftLinearResampler2();
            }

            if (var5.equalsIgnoreCase("cubic")) {
               this.resampler = new SoftCubicResampler();
            }

            if (var5.equalsIgnoreCase("lanczos")) {
               this.resampler = new SoftLanczosResampler();
            }

            if (var5.equalsIgnoreCase("sinc")) {
               this.resampler = new SoftSincResampler();
            }
         }

         if (this.resampler == null) {
            this.resampler = new SoftLinearResampler2();
         }

         this.pitch[0] = var3.getSampleRate() / var2.getSampleRate();
         this.pad = this.resampler.getPadding();
         this.pad2 = this.pad * 2;
         this.ibuffer = new float[this.nrofchannels][512 + this.pad2];
         this.ibuffer2 = new float[this.nrofchannels * 512];
         this.ibuffer_index = (float)(512 + this.pad);
         this.ibuffer_len = 512;
      }

      public int available() throws IOException {
         return 0;
      }

      public void close() throws IOException {
         this.ais.close();
      }

      public AudioFormat getFormat() {
         return this.targetFormat;
      }

      public long getFrameLength() {
         return -1L;
      }

      public void mark(int var1) {
         this.ais.mark((int)((float)var1 * this.pitch[0]));
         this.mark_ibuffer_index = this.ibuffer_index;
         this.mark_ibuffer_len = this.ibuffer_len;
         if (this.mark_ibuffer == null) {
            this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length];
         }

         for(int var2 = 0; var2 < this.ibuffer.length; ++var2) {
            float[] var3 = this.ibuffer[var2];
            float[] var4 = this.mark_ibuffer[var2];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = var3[var5];
            }
         }

      }

      public boolean markSupported() {
         return this.ais.markSupported();
      }

      private void readNextBuffer() throws IOException {
         if (this.ibuffer_len != -1) {
            int var1;
            int var4;
            int var5;
            for(var1 = 0; var1 < this.nrofchannels; ++var1) {
               float[] var2 = this.ibuffer[var1];
               int var3 = this.ibuffer_len + this.pad2;
               var4 = this.ibuffer_len;

               for(var5 = 0; var4 < var3; ++var5) {
                  var2[var5] = var2[var4];
                  ++var4;
               }
            }

            this.ibuffer_index -= (float)this.ibuffer_len;
            this.ibuffer_len = this.ais.read(this.ibuffer2);
            if (this.ibuffer_len < 0) {
               Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0F);
            } else {
               while(this.ibuffer_len < this.ibuffer2.length) {
                  var1 = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
                  if (var1 == -1) {
                     break;
                  }

                  this.ibuffer_len += var1;
               }

               Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0F);
               this.ibuffer_len /= this.nrofchannels;
            }

            var1 = this.ibuffer2.length;

            for(int var6 = 0; var6 < this.nrofchannels; ++var6) {
               float[] var7 = this.ibuffer[var6];
               var4 = var6;

               for(var5 = this.pad2; var4 < var1; ++var5) {
                  var7[var5] = this.ibuffer2[var4];
                  var4 += this.nrofchannels;
               }
            }

         }
      }

      public int read(float[] var1, int var2, int var3) throws IOException {
         if (this.cbuffer == null || this.cbuffer[0].length < var3 / this.nrofchannels) {
            this.cbuffer = new float[this.nrofchannels][var3 / this.nrofchannels];
         }

         if (this.ibuffer_len == -1) {
            return -1;
         } else if (var3 < 0) {
            return 0;
         } else {
            int var4 = var3 / this.nrofchannels;
            int var5 = 0;

            int var7;
            int var8;
            float[] var9;
            for(int var6 = this.ibuffer_len; var4 > 0; var4 -= var5 - var7) {
               if (this.ibuffer_len >= 0) {
                  if (this.ibuffer_index >= (float)(this.ibuffer_len + this.pad)) {
                     this.readNextBuffer();
                  }

                  var6 = this.ibuffer_len + this.pad;
               }

               if (this.ibuffer_len < 0) {
                  var6 = this.pad2;
                  if (this.ibuffer_index >= (float)var6) {
                     break;
                  }
               }

               if (this.ibuffer_index < 0.0F) {
                  break;
               }

               var7 = var5;

               for(var8 = 0; var8 < this.nrofchannels; ++var8) {
                  this.ix[0] = this.ibuffer_index;
                  this.ox[0] = var5;
                  var9 = this.ibuffer[var8];
                  this.resampler.interpolate(var9, this.ix, (float)var6, this.pitch, 0.0F, this.cbuffer[var8], this.ox, var3 / this.nrofchannels);
               }

               this.ibuffer_index = this.ix[0];
               var5 = this.ox[0];
            }

            for(var7 = 0; var7 < this.nrofchannels; ++var7) {
               var8 = 0;
               var9 = this.cbuffer[var7];

               for(int var10 = var7; var10 < var1.length; var10 += this.nrofchannels) {
                  var1[var10] = var9[var8++];
               }
            }

            return var3 - var4 * this.nrofchannels;
         }
      }

      public void reset() throws IOException {
         this.ais.reset();
         if (this.mark_ibuffer != null) {
            this.ibuffer_index = this.mark_ibuffer_index;
            this.ibuffer_len = this.mark_ibuffer_len;

            for(int var1 = 0; var1 < this.ibuffer.length; ++var1) {
               float[] var2 = this.mark_ibuffer[var1];
               float[] var3 = this.ibuffer[var1];

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  var3[var4] = var2[var4];
               }
            }

         }
      }

      public long skip(long var1) throws IOException {
         if (var1 > 0L) {
            return 0L;
         } else {
            if (this.skipbuffer == null) {
               this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()];
            }

            float[] var3 = this.skipbuffer;

            long var4;
            int var6;
            for(var4 = var1; var4 > 0L; var4 -= (long)var6) {
               var6 = this.read(var3, 0, (int)Math.min(var4, (long)this.skipbuffer.length));
               if (var6 < 0) {
                  if (var4 == var1) {
                     return (long)var6;
                  }
                  break;
               }
            }

            return var1 - var4;
         }
      }
   }
}
