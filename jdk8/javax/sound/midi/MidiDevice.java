package javax.sound.midi;

import java.util.List;

public interface MidiDevice extends AutoCloseable {
   MidiDevice.Info getDeviceInfo();

   void open() throws MidiUnavailableException;

   void close();

   boolean isOpen();

   long getMicrosecondPosition();

   int getMaxReceivers();

   int getMaxTransmitters();

   Receiver getReceiver() throws MidiUnavailableException;

   List<Receiver> getReceivers();

   Transmitter getTransmitter() throws MidiUnavailableException;

   List<Transmitter> getTransmitters();

   public static class Info {
      private String name;
      private String vendor;
      private String description;
      private String version;

      protected Info(String var1, String var2, String var3, String var4) {
         this.name = var1;
         this.vendor = var2;
         this.description = var3;
         this.version = var4;
      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final String getName() {
         return this.name;
      }

      public final String getVendor() {
         return this.vendor;
      }

      public final String getDescription() {
         return this.description;
      }

      public final String getVersion() {
         return this.version;
      }

      public final String toString() {
         return this.name;
      }
   }
}
