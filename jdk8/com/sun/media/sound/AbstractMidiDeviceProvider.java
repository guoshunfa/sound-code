package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

public abstract class AbstractMidiDeviceProvider extends MidiDeviceProvider {
   private static final boolean enabled;

   final synchronized void readDeviceInfos() {
      AbstractMidiDeviceProvider.Info[] var1 = this.getInfoCache();
      MidiDevice[] var2 = this.getDeviceCache();
      if (!enabled) {
         if (var1 == null || var1.length != 0) {
            this.setInfoCache(new AbstractMidiDeviceProvider.Info[0]);
         }

         if (var2 == null || var2.length != 0) {
            this.setDeviceCache(new MidiDevice[0]);
         }

      } else {
         int var3 = var1 == null ? -1 : var1.length;
         int var4 = this.getNumDevices();
         if (var3 != var4) {
            AbstractMidiDeviceProvider.Info[] var5 = new AbstractMidiDeviceProvider.Info[var4];
            MidiDevice[] var6 = new MidiDevice[var4];

            int var7;
            for(var7 = 0; var7 < var4; ++var7) {
               AbstractMidiDeviceProvider.Info var8 = this.createInfo(var7);
               if (var1 != null) {
                  for(int var9 = 0; var9 < var1.length; ++var9) {
                     AbstractMidiDeviceProvider.Info var10 = var1[var9];
                     if (var10 != null && var10.equalStrings(var8)) {
                        var5[var7] = var10;
                        var10.setIndex(var7);
                        var1[var9] = null;
                        var6[var7] = var2[var9];
                        var2[var9] = null;
                        break;
                     }
                  }
               }

               if (var5[var7] == null) {
                  var5[var7] = var8;
               }
            }

            if (var1 != null) {
               for(var7 = 0; var7 < var1.length; ++var7) {
                  if (var1[var7] != null) {
                     var1[var7].setIndex(-1);
                  }
               }
            }

            this.setInfoCache(var5);
            this.setDeviceCache(var6);
         }

      }
   }

   public final MidiDevice.Info[] getDeviceInfo() {
      this.readDeviceInfos();
      AbstractMidiDeviceProvider.Info[] var1 = this.getInfoCache();
      MidiDevice.Info[] var2 = new MidiDevice.Info[var1.length];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      return var2;
   }

   public final MidiDevice getDevice(MidiDevice.Info var1) {
      if (var1 instanceof AbstractMidiDeviceProvider.Info) {
         this.readDeviceInfos();
         MidiDevice[] var2 = this.getDeviceCache();
         AbstractMidiDeviceProvider.Info[] var3 = this.getInfoCache();
         AbstractMidiDeviceProvider.Info var4 = (AbstractMidiDeviceProvider.Info)var1;
         int var5 = var4.getIndex();
         if (var5 >= 0 && var5 < var2.length && var3[var5] == var1) {
            if (var2[var5] == null) {
               var2[var5] = this.createDevice(var4);
            }

            if (var2[var5] != null) {
               return var2[var5];
            }
         }
      }

      throw new IllegalArgumentException("MidiDevice " + var1.toString() + " not supported by this provider.");
   }

   abstract int getNumDevices();

   abstract MidiDevice[] getDeviceCache();

   abstract void setDeviceCache(MidiDevice[] var1);

   abstract AbstractMidiDeviceProvider.Info[] getInfoCache();

   abstract void setInfoCache(AbstractMidiDeviceProvider.Info[] var1);

   abstract AbstractMidiDeviceProvider.Info createInfo(int var1);

   abstract MidiDevice createDevice(AbstractMidiDeviceProvider.Info var1);

   static {
      Platform.initialize();
      enabled = Platform.isMidiIOEnabled();
   }

   static class Info extends MidiDevice.Info {
      private int index;

      Info(String var1, String var2, String var3, String var4, int var5) {
         super(var1, var2, var3, var4);
         this.index = var5;
      }

      final boolean equalStrings(AbstractMidiDeviceProvider.Info var1) {
         return var1 != null && this.getName().equals(var1.getName()) && this.getVendor().equals(var1.getVendor()) && this.getDescription().equals(var1.getDescription()) && this.getVersion().equals(var1.getVersion());
      }

      final int getIndex() {
         return this.index;
      }

      final void setIndex(int var1) {
         this.index = var1;
      }
   }
}
