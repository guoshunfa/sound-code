package javax.sound.midi;

import com.sun.media.sound.AutoConnectSequencer;
import com.sun.media.sound.JDK13Services;
import com.sun.media.sound.MidiDeviceReceiverEnvelope;
import com.sun.media.sound.MidiDeviceTransmitterEnvelope;
import com.sun.media.sound.ReferenceCountingDevice;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.spi.MidiDeviceProvider;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.SoundbankReader;

public class MidiSystem {
   private MidiSystem() {
   }

   public static MidiDevice.Info[] getMidiDeviceInfo() {
      ArrayList var0 = new ArrayList();
      List var1 = getMidiDeviceProviders();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MidiDeviceProvider var3 = (MidiDeviceProvider)var1.get(var2);
         MidiDevice.Info[] var4 = var3.getDeviceInfo();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var0.add(var4[var5]);
         }
      }

      MidiDevice.Info[] var6 = (MidiDevice.Info[])((MidiDevice.Info[])var0.toArray(new MidiDevice.Info[0]));
      return var6;
   }

   public static MidiDevice getMidiDevice(MidiDevice.Info var0) throws MidiUnavailableException {
      List var1 = getMidiDeviceProviders();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MidiDeviceProvider var3 = (MidiDeviceProvider)var1.get(var2);
         if (var3.isDeviceSupported(var0)) {
            MidiDevice var4 = var3.getDevice(var0);
            return var4;
         }
      }

      throw new IllegalArgumentException("Requested device not installed: " + var0);
   }

   public static Receiver getReceiver() throws MidiUnavailableException {
      MidiDevice var0 = getDefaultDeviceWrapper(Receiver.class);
      Object var1;
      if (var0 instanceof ReferenceCountingDevice) {
         var1 = ((ReferenceCountingDevice)var0).getReceiverReferenceCounting();
      } else {
         var1 = var0.getReceiver();
      }

      if (!(var1 instanceof MidiDeviceReceiver)) {
         var1 = new MidiDeviceReceiverEnvelope(var0, (Receiver)var1);
      }

      return (Receiver)var1;
   }

   public static Transmitter getTransmitter() throws MidiUnavailableException {
      MidiDevice var0 = getDefaultDeviceWrapper(Transmitter.class);
      Object var1;
      if (var0 instanceof ReferenceCountingDevice) {
         var1 = ((ReferenceCountingDevice)var0).getTransmitterReferenceCounting();
      } else {
         var1 = var0.getTransmitter();
      }

      if (!(var1 instanceof MidiDeviceTransmitter)) {
         var1 = new MidiDeviceTransmitterEnvelope(var0, (Transmitter)var1);
      }

      return (Transmitter)var1;
   }

   public static Synthesizer getSynthesizer() throws MidiUnavailableException {
      return (Synthesizer)getDefaultDeviceWrapper(Synthesizer.class);
   }

   public static Sequencer getSequencer() throws MidiUnavailableException {
      return getSequencer(true);
   }

   public static Sequencer getSequencer(boolean var0) throws MidiUnavailableException {
      Sequencer var1 = (Sequencer)getDefaultDeviceWrapper(Sequencer.class);
      if (var0) {
         Receiver var2 = null;
         MidiUnavailableException var3 = null;

         try {
            Synthesizer var4 = getSynthesizer();
            if (var4 instanceof ReferenceCountingDevice) {
               var2 = ((ReferenceCountingDevice)var4).getReceiverReferenceCounting();
            } else {
               var4.open();

               try {
                  var2 = var4.getReceiver();
               } finally {
                  if (var2 == null) {
                     var4.close();
                  }

               }
            }
         } catch (MidiUnavailableException var11) {
            if (var11 instanceof MidiUnavailableException) {
               var3 = var11;
            }
         }

         if (var2 == null) {
            try {
               var2 = getReceiver();
            } catch (Exception var9) {
               if (var9 instanceof MidiUnavailableException) {
                  var3 = (MidiUnavailableException)var9;
               }
            }
         }

         if (var2 == null) {
            if (var3 != null) {
               throw var3;
            }

            throw new MidiUnavailableException("no receiver available");
         }

         var1.getTransmitter().setReceiver(var2);
         if (var1 instanceof AutoConnectSequencer) {
            ((AutoConnectSequencer)var1).setAutoConnect(var2);
         }
      }

      return var1;
   }

   public static Soundbank getSoundbank(InputStream var0) throws InvalidMidiDataException, IOException {
      SoundbankReader var1 = null;
      Soundbank var2 = null;
      List var3 = getSoundbankReaders();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         var1 = (SoundbankReader)var3.get(var4);
         var2 = var1.getSoundbank(var0);
         if (var2 != null) {
            return var2;
         }
      }

      throw new InvalidMidiDataException("cannot get soundbank from stream");
   }

   public static Soundbank getSoundbank(URL var0) throws InvalidMidiDataException, IOException {
      SoundbankReader var1 = null;
      Soundbank var2 = null;
      List var3 = getSoundbankReaders();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         var1 = (SoundbankReader)var3.get(var4);
         var2 = var1.getSoundbank(var0);
         if (var2 != null) {
            return var2;
         }
      }

      throw new InvalidMidiDataException("cannot get soundbank from stream");
   }

   public static Soundbank getSoundbank(File var0) throws InvalidMidiDataException, IOException {
      SoundbankReader var1 = null;
      Soundbank var2 = null;
      List var3 = getSoundbankReaders();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         var1 = (SoundbankReader)var3.get(var4);
         var2 = var1.getSoundbank(var0);
         if (var2 != null) {
            return var2;
         }
      }

      throw new InvalidMidiDataException("cannot get soundbank from stream");
   }

   public static MidiFileFormat getMidiFileFormat(InputStream var0) throws InvalidMidiDataException, IOException {
      List var1 = getMidiFileReaders();
      MidiFileFormat var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         MidiFileReader var4 = (MidiFileReader)var1.get(var3);

         try {
            var2 = var4.getMidiFileFormat(var0);
            break;
         } catch (InvalidMidiDataException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new InvalidMidiDataException("input stream is not a supported file type");
      } else {
         return var2;
      }
   }

   public static MidiFileFormat getMidiFileFormat(URL var0) throws InvalidMidiDataException, IOException {
      List var1 = getMidiFileReaders();
      MidiFileFormat var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         MidiFileReader var4 = (MidiFileReader)var1.get(var3);

         try {
            var2 = var4.getMidiFileFormat(var0);
            break;
         } catch (InvalidMidiDataException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new InvalidMidiDataException("url is not a supported file type");
      } else {
         return var2;
      }
   }

   public static MidiFileFormat getMidiFileFormat(File var0) throws InvalidMidiDataException, IOException {
      List var1 = getMidiFileReaders();
      MidiFileFormat var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         MidiFileReader var4 = (MidiFileReader)var1.get(var3);

         try {
            var2 = var4.getMidiFileFormat(var0);
            break;
         } catch (InvalidMidiDataException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new InvalidMidiDataException("file is not a supported file type");
      } else {
         return var2;
      }
   }

   public static Sequence getSequence(InputStream var0) throws InvalidMidiDataException, IOException {
      List var1 = getMidiFileReaders();
      Sequence var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         MidiFileReader var4 = (MidiFileReader)var1.get(var3);

         try {
            var2 = var4.getSequence(var0);
            break;
         } catch (InvalidMidiDataException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new InvalidMidiDataException("could not get sequence from input stream");
      } else {
         return var2;
      }
   }

   public static Sequence getSequence(URL var0) throws InvalidMidiDataException, IOException {
      List var1 = getMidiFileReaders();
      Sequence var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         MidiFileReader var4 = (MidiFileReader)var1.get(var3);

         try {
            var2 = var4.getSequence(var0);
            break;
         } catch (InvalidMidiDataException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new InvalidMidiDataException("could not get sequence from URL");
      } else {
         return var2;
      }
   }

   public static Sequence getSequence(File var0) throws InvalidMidiDataException, IOException {
      List var1 = getMidiFileReaders();
      Sequence var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         MidiFileReader var4 = (MidiFileReader)var1.get(var3);

         try {
            var2 = var4.getSequence(var0);
            break;
         } catch (InvalidMidiDataException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new InvalidMidiDataException("could not get sequence from file");
      } else {
         return var2;
      }
   }

   public static int[] getMidiFileTypes() {
      List var0 = getMidiFileWriters();
      HashSet var1 = new HashSet();

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         MidiFileWriter var3 = (MidiFileWriter)var0.get(var2);
         int[] var4 = var3.getMidiFileTypes();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var1.add(new Integer(var4[var5]));
         }
      }

      int[] var6 = new int[var1.size()];
      int var7 = 0;

      Integer var9;
      for(Iterator var8 = var1.iterator(); var8.hasNext(); var6[var7++] = var9) {
         var9 = (Integer)var8.next();
      }

      return var6;
   }

   public static boolean isFileTypeSupported(int var0) {
      List var1 = getMidiFileWriters();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MidiFileWriter var3 = (MidiFileWriter)var1.get(var2);
         if (var3.isFileTypeSupported(var0)) {
            return true;
         }
      }

      return false;
   }

   public static int[] getMidiFileTypes(Sequence var0) {
      List var1 = getMidiFileWriters();
      HashSet var2 = new HashSet();

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         MidiFileWriter var4 = (MidiFileWriter)var1.get(var3);
         int[] var5 = var4.getMidiFileTypes(var0);

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var2.add(new Integer(var5[var6]));
         }
      }

      int[] var7 = new int[var2.size()];
      int var8 = 0;

      Integer var10;
      for(Iterator var9 = var2.iterator(); var9.hasNext(); var7[var8++] = var10) {
         var10 = (Integer)var9.next();
      }

      return var7;
   }

   public static boolean isFileTypeSupported(int var0, Sequence var1) {
      List var2 = getMidiFileWriters();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         MidiFileWriter var4 = (MidiFileWriter)var2.get(var3);
         if (var4.isFileTypeSupported(var0, var1)) {
            return true;
         }
      }

      return false;
   }

   public static int write(Sequence var0, int var1, OutputStream var2) throws IOException {
      List var3 = getMidiFileWriters();
      int var4 = -2;

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         MidiFileWriter var6 = (MidiFileWriter)var3.get(var5);
         if (var6.isFileTypeSupported(var1, var0)) {
            var4 = var6.write(var0, var1, var2);
            break;
         }
      }

      if (var4 == -2) {
         throw new IllegalArgumentException("MIDI file type is not supported");
      } else {
         return var4;
      }
   }

   public static int write(Sequence var0, int var1, File var2) throws IOException {
      List var3 = getMidiFileWriters();
      int var4 = -2;

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         MidiFileWriter var6 = (MidiFileWriter)var3.get(var5);
         if (var6.isFileTypeSupported(var1, var0)) {
            var4 = var6.write(var0, var1, var2);
            break;
         }
      }

      if (var4 == -2) {
         throw new IllegalArgumentException("MIDI file type is not supported");
      } else {
         return var4;
      }
   }

   private static List getMidiDeviceProviders() {
      return getProviders(MidiDeviceProvider.class);
   }

   private static List getSoundbankReaders() {
      return getProviders(SoundbankReader.class);
   }

   private static List getMidiFileWriters() {
      return getProviders(MidiFileWriter.class);
   }

   private static List getMidiFileReaders() {
      return getProviders(MidiFileReader.class);
   }

   private static MidiDevice getDefaultDeviceWrapper(Class var0) throws MidiUnavailableException {
      try {
         return getDefaultDevice(var0);
      } catch (IllegalArgumentException var3) {
         MidiUnavailableException var2 = new MidiUnavailableException();
         var2.initCause(var3);
         throw var2;
      }
   }

   private static MidiDevice getDefaultDevice(Class var0) {
      List var1 = getMidiDeviceProviders();
      String var2 = JDK13Services.getDefaultProviderClassName(var0);
      String var3 = JDK13Services.getDefaultInstanceName(var0);
      MidiDevice var4;
      if (var2 != null) {
         MidiDeviceProvider var5 = getNamedProvider(var2, var1);
         if (var5 != null) {
            if (var3 != null) {
               var4 = getNamedDevice(var3, var5, var0);
               if (var4 != null) {
                  return var4;
               }
            }

            var4 = getFirstDevice(var5, var0);
            if (var4 != null) {
               return var4;
            }
         }
      }

      if (var3 != null) {
         var4 = getNamedDevice(var3, var1, var0);
         if (var4 != null) {
            return var4;
         }
      }

      var4 = getFirstDevice(var1, var0);
      if (var4 != null) {
         return var4;
      } else {
         throw new IllegalArgumentException("Requested device not installed");
      }
   }

   private static MidiDeviceProvider getNamedProvider(String var0, List var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MidiDeviceProvider var3 = (MidiDeviceProvider)var1.get(var2);
         if (var3.getClass().getName().equals(var0)) {
            return var3;
         }
      }

      return null;
   }

   private static MidiDevice getNamedDevice(String var0, MidiDeviceProvider var1, Class var2) {
      MidiDevice var3 = getNamedDevice(var0, var1, var2, false, false);
      if (var3 != null) {
         return var3;
      } else {
         if (var2 == Receiver.class) {
            var3 = getNamedDevice(var0, var1, var2, true, false);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      }
   }

   private static MidiDevice getNamedDevice(String var0, MidiDeviceProvider var1, Class var2, boolean var3, boolean var4) {
      MidiDevice.Info[] var5 = var1.getDeviceInfo();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         if (var5[var6].getName().equals(var0)) {
            MidiDevice var7 = var1.getDevice(var5[var6]);
            if (isAppropriateDevice(var7, var2, var3, var4)) {
               return var7;
            }
         }
      }

      return null;
   }

   private static MidiDevice getNamedDevice(String var0, List var1, Class var2) {
      MidiDevice var3 = getNamedDevice(var0, var1, var2, false, false);
      if (var3 != null) {
         return var3;
      } else {
         if (var2 == Receiver.class) {
            var3 = getNamedDevice(var0, var1, var2, true, false);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      }
   }

   private static MidiDevice getNamedDevice(String var0, List var1, Class var2, boolean var3, boolean var4) {
      for(int var5 = 0; var5 < var1.size(); ++var5) {
         MidiDeviceProvider var6 = (MidiDeviceProvider)var1.get(var5);
         MidiDevice var7 = getNamedDevice(var0, var6, var2, var3, var4);
         if (var7 != null) {
            return var7;
         }
      }

      return null;
   }

   private static MidiDevice getFirstDevice(MidiDeviceProvider var0, Class var1) {
      MidiDevice var2 = getFirstDevice(var0, var1, false, false);
      if (var2 != null) {
         return var2;
      } else {
         if (var1 == Receiver.class) {
            var2 = getFirstDevice(var0, var1, true, false);
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      }
   }

   private static MidiDevice getFirstDevice(MidiDeviceProvider var0, Class var1, boolean var2, boolean var3) {
      MidiDevice.Info[] var4 = var0.getDeviceInfo();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         MidiDevice var6 = var0.getDevice(var4[var5]);
         if (isAppropriateDevice(var6, var1, var2, var3)) {
            return var6;
         }
      }

      return null;
   }

   private static MidiDevice getFirstDevice(List var0, Class var1) {
      MidiDevice var2 = getFirstDevice(var0, var1, false, false);
      if (var2 != null) {
         return var2;
      } else {
         if (var1 == Receiver.class) {
            var2 = getFirstDevice(var0, var1, true, false);
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      }
   }

   private static MidiDevice getFirstDevice(List var0, Class var1, boolean var2, boolean var3) {
      for(int var4 = 0; var4 < var0.size(); ++var4) {
         MidiDeviceProvider var5 = (MidiDeviceProvider)var0.get(var4);
         MidiDevice var6 = getFirstDevice(var5, var1, var2, var3);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }

   private static boolean isAppropriateDevice(MidiDevice var0, Class var1, boolean var2, boolean var3) {
      if (var1.isInstance(var0)) {
         return true;
      } else {
         return (!(var0 instanceof Sequencer) && !(var0 instanceof Synthesizer) || var0 instanceof Sequencer && var3 || var0 instanceof Synthesizer && var2) && (var1 == Receiver.class && var0.getMaxReceivers() != 0 || var1 == Transmitter.class && var0.getMaxTransmitters() != 0);
      }
   }

   private static List getProviders(Class var0) {
      return JDK13Services.getProviders(var0);
   }
}
