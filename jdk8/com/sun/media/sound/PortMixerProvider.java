package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class PortMixerProvider extends MixerProvider {
   private static PortMixerProvider.PortMixerInfo[] infos;
   private static PortMixer[] devices;

   public PortMixerProvider() {
      Class var1 = PortMixerProvider.class;
      synchronized(PortMixerProvider.class) {
         if (Platform.isPortsEnabled()) {
            init();
         } else {
            infos = new PortMixerProvider.PortMixerInfo[0];
            devices = new PortMixer[0];
         }

      }
   }

   private static void init() {
      int var0 = nGetNumDevices();
      if (infos == null || infos.length != var0) {
         infos = new PortMixerProvider.PortMixerInfo[var0];
         devices = new PortMixer[var0];

         for(int var1 = 0; var1 < infos.length; ++var1) {
            infos[var1] = nNewPortMixerInfo(var1);
         }
      }

   }

   public Mixer.Info[] getMixerInfo() {
      Class var1 = PortMixerProvider.class;
      synchronized(PortMixerProvider.class) {
         Mixer.Info[] var2 = new Mixer.Info[infos.length];
         System.arraycopy(infos, 0, var2, 0, infos.length);
         return var2;
      }
   }

   public Mixer getMixer(Mixer.Info var1) {
      Class var2 = PortMixerProvider.class;
      synchronized(PortMixerProvider.class) {
         for(int var3 = 0; var3 < infos.length; ++var3) {
            if (infos[var3].equals(var1)) {
               return getDevice(infos[var3]);
            }
         }

         throw new IllegalArgumentException("Mixer " + var1.toString() + " not supported by this provider.");
      }
   }

   private static Mixer getDevice(PortMixerProvider.PortMixerInfo var0) {
      int var1 = var0.getIndex();
      if (devices[var1] == null) {
         devices[var1] = new PortMixer(var0);
      }

      return devices[var1];
   }

   private static native int nGetNumDevices();

   private static native PortMixerProvider.PortMixerInfo nNewPortMixerInfo(int var0);

   static {
      Platform.initialize();
   }

   static final class PortMixerInfo extends Mixer.Info {
      private final int index;

      private PortMixerInfo(int var1, String var2, String var3, String var4, String var5) {
         super("Port " + var2, var3, var4, var5);
         this.index = var1;
      }

      int getIndex() {
         return this.index;
      }
   }
}
