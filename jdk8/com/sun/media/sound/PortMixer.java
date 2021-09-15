package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;

final class PortMixer extends AbstractMixer {
   private static final int SRC_UNKNOWN = 1;
   private static final int SRC_MICROPHONE = 2;
   private static final int SRC_LINE_IN = 3;
   private static final int SRC_COMPACT_DISC = 4;
   private static final int SRC_MASK = 255;
   private static final int DST_UNKNOWN = 256;
   private static final int DST_SPEAKER = 512;
   private static final int DST_HEADPHONE = 768;
   private static final int DST_LINE_OUT = 1024;
   private static final int DST_MASK = 65280;
   private Port.Info[] portInfos;
   private PortMixer.PortMixerPort[] ports;
   private long id = 0L;

   PortMixer(PortMixerProvider.PortMixerInfo var1) {
      super(var1, (Control[])null, (Line.Info[])null, (Line.Info[])null);
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      int var5;
      try {
         try {
            this.id = nOpen(this.getMixerIndex());
            if (this.id != 0L) {
               var2 = nGetPortCount(this.id);
               if (var2 < 0) {
                  var2 = 0;
               }
            }
         } catch (Exception var10) {
         }

         this.portInfos = new Port.Info[var2];

         for(var5 = 0; var5 < var2; ++var5) {
            int var6 = nGetPortType(this.id, var5);
            var3 += (var6 & 255) != 0 ? 1 : 0;
            var4 += (var6 & '\uff00') != 0 ? 1 : 0;
            this.portInfos[var5] = this.getPortInfo(var5, var6);
         }
      } finally {
         if (this.id != 0L) {
            nClose(this.id);
         }

         this.id = 0L;
      }

      this.sourceLineInfo = new Port.Info[var3];
      this.targetLineInfo = new Port.Info[var4];
      var3 = 0;
      var4 = 0;

      for(var5 = 0; var5 < var2; ++var5) {
         if (this.portInfos[var5].isSource()) {
            this.sourceLineInfo[var3++] = this.portInfos[var5];
         } else {
            this.targetLineInfo[var4++] = this.portInfos[var5];
         }
      }

   }

   public Line getLine(Line.Info var1) throws LineUnavailableException {
      Line.Info var2 = this.getLineInfo(var1);
      if (var2 != null && var2 instanceof Port.Info) {
         for(int var3 = 0; var3 < this.portInfos.length; ++var3) {
            if (var2.equals(this.portInfos[var3])) {
               return this.getPort(var3);
            }
         }
      }

      throw new IllegalArgumentException("Line unsupported: " + var1);
   }

   public int getMaxLines(Line.Info var1) {
      Line.Info var2 = this.getLineInfo(var1);
      if (var2 == null) {
         return 0;
      } else {
         return var2 instanceof Port.Info ? 1 : 0;
      }
   }

   protected void implOpen() throws LineUnavailableException {
      this.id = nOpen(this.getMixerIndex());
   }

   protected void implClose() {
      long var1 = this.id;
      this.id = 0L;
      nClose(var1);
      if (this.ports != null) {
         for(int var3 = 0; var3 < this.ports.length; ++var3) {
            if (this.ports[var3] != null) {
               this.ports[var3].disposeControls();
            }
         }
      }

   }

   protected void implStart() {
   }

   protected void implStop() {
   }

   private Port.Info getPortInfo(int var1, int var2) {
      switch(var2) {
      case 1:
         return new PortMixer.PortInfo(nGetPortName(this.getID(), var1), true);
      case 2:
         return Port.Info.MICROPHONE;
      case 3:
         return Port.Info.LINE_IN;
      case 4:
         return Port.Info.COMPACT_DISC;
      case 256:
         return new PortMixer.PortInfo(nGetPortName(this.getID(), var1), false);
      case 512:
         return Port.Info.SPEAKER;
      case 768:
         return Port.Info.HEADPHONE;
      case 1024:
         return Port.Info.LINE_OUT;
      default:
         return null;
      }
   }

   int getMixerIndex() {
      return ((PortMixerProvider.PortMixerInfo)this.getMixerInfo()).getIndex();
   }

   Port getPort(int var1) {
      if (this.ports == null) {
         this.ports = new PortMixer.PortMixerPort[this.portInfos.length];
      }

      if (this.ports[var1] == null) {
         this.ports[var1] = new PortMixer.PortMixerPort(this.portInfos[var1], this, var1);
         return this.ports[var1];
      } else {
         return this.ports[var1];
      }
   }

   long getID() {
      return this.id;
   }

   private static native long nOpen(int var0) throws LineUnavailableException;

   private static native void nClose(long var0);

   private static native int nGetPortCount(long var0);

   private static native int nGetPortType(long var0, int var2);

   private static native String nGetPortName(long var0, int var2);

   private static native void nGetControls(long var0, int var2, Vector var3);

   private static native void nControlSetIntValue(long var0, int var2);

   private static native int nControlGetIntValue(long var0);

   private static native void nControlSetFloatValue(long var0, float var2);

   private static native float nControlGetFloatValue(long var0);

   private static final class PortInfo extends Port.Info {
      private PortInfo(String var1, boolean var2) {
         super(Port.class, var1, var2);
      }

      // $FF: synthetic method
      PortInfo(String var1, boolean var2, Object var3) {
         this(var1, var2);
      }
   }

   private static final class FloatCtrl extends FloatControl {
      private final long controlID;
      private boolean closed;
      private static final FloatControl.Type[] FLOAT_CONTROL_TYPES;

      private FloatCtrl(long var1, String var3, float var4, float var5, float var6, String var7) {
         this(var1, (FloatControl.Type)(new PortMixer.FloatCtrl.FCT(var3)), var4, var5, var6, var7);
      }

      private FloatCtrl(long var1, int var3, float var4, float var5, float var6, String var7) {
         this(var1, FLOAT_CONTROL_TYPES[var3], var4, var5, var6, var7);
      }

      private FloatCtrl(long var1, FloatControl.Type var3, float var4, float var5, float var6, String var7) {
         super(var3, var4, var5, var6, 1000, var4, var7);
         this.closed = false;
         this.controlID = var1;
      }

      public void setValue(float var1) {
         if (!this.closed) {
            PortMixer.nControlSetFloatValue(this.controlID, var1);
         }

      }

      public float getValue() {
         return !this.closed ? PortMixer.nControlGetFloatValue(this.controlID) : this.getMinimum();
      }

      static {
         FLOAT_CONTROL_TYPES = new FloatControl.Type[]{null, FloatControl.Type.BALANCE, FloatControl.Type.MASTER_GAIN, FloatControl.Type.PAN, FloatControl.Type.VOLUME};
      }

      private static final class FCT extends FloatControl.Type {
         private FCT(String var1) {
            super(var1);
         }

         // $FF: synthetic method
         FCT(String var1, Object var2) {
            this(var1);
         }
      }
   }

   private static final class CompCtrl extends CompoundControl {
      private CompCtrl(String var1, Control[] var2) {
         super(new PortMixer.CompCtrl.CCT(var1), var2);
      }

      private static final class CCT extends CompoundControl.Type {
         private CCT(String var1) {
            super(var1);
         }

         // $FF: synthetic method
         CCT(String var1, Object var2) {
            this(var1);
         }
      }
   }

   private static final class BoolCtrl extends BooleanControl {
      private final long controlID;
      private boolean closed;

      private static BooleanControl.Type createType(String var0) {
         if (var0.equals("Mute")) {
            return BooleanControl.Type.MUTE;
         } else {
            if (var0.equals("Select")) {
            }

            return new PortMixer.BoolCtrl.BCT(var0);
         }
      }

      private BoolCtrl(long var1, String var3) {
         this(var1, createType(var3));
      }

      private BoolCtrl(long var1, BooleanControl.Type var3) {
         super(var3, false);
         this.closed = false;
         this.controlID = var1;
      }

      public void setValue(boolean var1) {
         if (!this.closed) {
            PortMixer.nControlSetIntValue(this.controlID, var1 ? 1 : 0);
         }

      }

      public boolean getValue() {
         if (!this.closed) {
            return PortMixer.nControlGetIntValue(this.controlID) != 0;
         } else {
            return false;
         }
      }

      private static final class BCT extends BooleanControl.Type {
         private BCT(String var1) {
            super(var1);
         }

         // $FF: synthetic method
         BCT(String var1, Object var2) {
            this(var1);
         }
      }
   }

   private static final class PortMixerPort extends AbstractLine implements Port {
      private final int portIndex;
      private long id;

      private PortMixerPort(Port.Info var1, PortMixer var2, int var3) {
         super(var1, var2, (Control[])null);
         this.portIndex = var3;
      }

      void implOpen() throws LineUnavailableException {
         long var1 = ((PortMixer)this.mixer).getID();
         if (this.id != 0L && var1 == this.id && this.controls.length != 0) {
            this.enableControls(this.controls, true);
         } else {
            this.id = var1;
            Vector var3 = new Vector();
            synchronized(var3) {
               PortMixer.nGetControls(this.id, this.portIndex, var3);
               this.controls = new Control[var3.size()];

               for(int var5 = 0; var5 < this.controls.length; ++var5) {
                  this.controls[var5] = (Control)var3.elementAt(var5);
               }
            }
         }

      }

      private void enableControls(Control[] var1, boolean var2) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] instanceof PortMixer.BoolCtrl) {
               ((PortMixer.BoolCtrl)var1[var3]).closed = !var2;
            } else if (var1[var3] instanceof PortMixer.FloatCtrl) {
               ((PortMixer.FloatCtrl)var1[var3]).closed = !var2;
            } else if (var1[var3] instanceof CompoundControl) {
               this.enableControls(((CompoundControl)var1[var3]).getMemberControls(), var2);
            }
         }

      }

      private void disposeControls() {
         this.enableControls(this.controls, false);
         this.controls = new Control[0];
      }

      void implClose() {
         this.enableControls(this.controls, false);
      }

      public void open() throws LineUnavailableException {
         synchronized(this.mixer) {
            if (!this.isOpen()) {
               this.mixer.open(this);

               try {
                  this.implOpen();
                  this.setOpen(true);
               } catch (LineUnavailableException var4) {
                  this.mixer.close(this);
                  throw var4;
               }
            }

         }
      }

      public void close() {
         synchronized(this.mixer) {
            if (this.isOpen()) {
               this.setOpen(false);
               this.implClose();
               this.mixer.close(this);
            }

         }
      }

      // $FF: synthetic method
      PortMixerPort(Port.Info var1, PortMixer var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
