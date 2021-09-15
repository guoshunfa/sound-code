package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class DirectAudioDeviceProvider extends MixerProvider {
   private static DirectAudioDeviceProvider.DirectAudioDeviceInfo[] infos;
   private static DirectAudioDevice[] devices;

   public DirectAudioDeviceProvider() {
      Class var1 = DirectAudioDeviceProvider.class;
      synchronized(DirectAudioDeviceProvider.class) {
         if (Platform.isDirectAudioEnabled()) {
            init();
         } else {
            infos = new DirectAudioDeviceProvider.DirectAudioDeviceInfo[0];
            devices = new DirectAudioDevice[0];
         }

      }
   }

   private static void init() {
      int var0 = nGetNumDevices();
      if (infos == null || infos.length != var0) {
         infos = new DirectAudioDeviceProvider.DirectAudioDeviceInfo[var0];
         devices = new DirectAudioDevice[var0];

         for(int var1 = 0; var1 < infos.length; ++var1) {
            infos[var1] = nNewDirectAudioDeviceInfo(var1);
         }
      }

   }

   public Mixer.Info[] getMixerInfo() {
      Class var1 = DirectAudioDeviceProvider.class;
      synchronized(DirectAudioDeviceProvider.class) {
         Mixer.Info[] var2 = new Mixer.Info[infos.length];
         System.arraycopy(infos, 0, var2, 0, infos.length);
         return var2;
      }
   }

   public Mixer getMixer(Mixer.Info var1) {
      Class var2 = DirectAudioDeviceProvider.class;
      synchronized(DirectAudioDeviceProvider.class) {
         int var3;
         if (var1 == null) {
            for(var3 = 0; var3 < infos.length; ++var3) {
               Mixer var4 = getDevice(infos[var3]);
               if (var4.getSourceLineInfo().length > 0) {
                  return var4;
               }
            }
         }

         for(var3 = 0; var3 < infos.length; ++var3) {
            if (infos[var3].equals(var1)) {
               return getDevice(infos[var3]);
            }
         }

         throw new IllegalArgumentException("Mixer " + var1.toString() + " not supported by this provider.");
      }
   }

   private static Mixer getDevice(DirectAudioDeviceProvider.DirectAudioDeviceInfo var0) {
      int var1 = var0.getIndex();
      if (devices[var1] == null) {
         devices[var1] = new DirectAudioDevice(var0);
      }

      return devices[var1];
   }

   private static native int nGetNumDevices();

   private static native DirectAudioDeviceProvider.DirectAudioDeviceInfo nNewDirectAudioDeviceInfo(int var0);

   static {
      Platform.initialize();
   }

   static final class DirectAudioDeviceInfo extends Mixer.Info {
      private final int index;
      private final int maxSimulLines;
      private final int deviceID;

      private DirectAudioDeviceInfo(int var1, int var2, int var3, String var4, String var5, String var6, String var7) {
         super(var4, var5, "Direct Audio Device: " + var6, var7);
         this.index = var1;
         this.maxSimulLines = var3;
         this.deviceID = var2;
      }

      int getIndex() {
         return this.index;
      }

      int getMaxSimulLines() {
         return this.maxSimulLines;
      }

      int getDeviceID() {
         return this.deviceID;
      }
   }
}
