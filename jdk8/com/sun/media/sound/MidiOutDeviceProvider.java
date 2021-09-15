package com.sun.media.sound;

import javax.sound.midi.MidiDevice;

public final class MidiOutDeviceProvider extends AbstractMidiDeviceProvider {
   private static AbstractMidiDeviceProvider.Info[] infos = null;
   private static MidiDevice[] devices = null;
   private static final boolean enabled;

   AbstractMidiDeviceProvider.Info createInfo(int var1) {
      return !enabled ? null : new MidiOutDeviceProvider.MidiOutDeviceInfo(var1, MidiOutDeviceProvider.class);
   }

   MidiDevice createDevice(AbstractMidiDeviceProvider.Info var1) {
      return enabled && var1 instanceof MidiOutDeviceProvider.MidiOutDeviceInfo ? new MidiOutDevice(var1) : null;
   }

   int getNumDevices() {
      return !enabled ? 0 : nGetNumDevices();
   }

   MidiDevice[] getDeviceCache() {
      return devices;
   }

   void setDeviceCache(MidiDevice[] var1) {
      devices = var1;
   }

   AbstractMidiDeviceProvider.Info[] getInfoCache() {
      return infos;
   }

   void setInfoCache(AbstractMidiDeviceProvider.Info[] var1) {
      infos = var1;
   }

   private static native int nGetNumDevices();

   private static native String nGetName(int var0);

   private static native String nGetVendor(int var0);

   private static native String nGetDescription(int var0);

   private static native String nGetVersion(int var0);

   static {
      Platform.initialize();
      enabled = Platform.isMidiIOEnabled();
   }

   static final class MidiOutDeviceInfo extends AbstractMidiDeviceProvider.Info {
      private final Class providerClass;

      private MidiOutDeviceInfo(int var1, Class var2) {
         super(MidiOutDeviceProvider.nGetName(var1), MidiOutDeviceProvider.nGetVendor(var1), MidiOutDeviceProvider.nGetDescription(var1), MidiOutDeviceProvider.nGetVersion(var1), var1);
         this.providerClass = var2;
      }

      // $FF: synthetic method
      MidiOutDeviceInfo(int var1, Class var2, Object var3) {
         this(var1, var2);
      }
   }
}
