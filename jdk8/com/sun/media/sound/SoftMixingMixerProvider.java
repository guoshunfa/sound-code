package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public final class SoftMixingMixerProvider extends MixerProvider {
   static SoftMixingMixer globalmixer = null;
   static Thread lockthread = null;
   static final Object mutex = new Object();

   public Mixer getMixer(Mixer.Info var1) {
      if (var1 != null && var1 != SoftMixingMixer.info) {
         throw new IllegalArgumentException("Mixer " + var1.toString() + " not supported by this provider.");
      } else {
         synchronized(mutex) {
            if (lockthread != null && Thread.currentThread() == lockthread) {
               throw new IllegalArgumentException("Mixer " + var1.toString() + " not supported by this provider.");
            } else {
               if (globalmixer == null) {
                  globalmixer = new SoftMixingMixer();
               }

               return globalmixer;
            }
         }
      }
   }

   public Mixer.Info[] getMixerInfo() {
      return new Mixer.Info[]{SoftMixingMixer.info};
   }
}
