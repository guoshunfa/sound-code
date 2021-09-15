package com.sun.media.sound;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.sound.midi.spi.MidiDeviceProvider;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;

public final class JDK13Services {
   private static final String PROPERTIES_FILENAME = "sound.properties";
   private static Properties properties;

   private JDK13Services() {
   }

   public static List<?> getProviders(Class<?> var0) {
      Object var1;
      if (!MixerProvider.class.equals(var0) && !FormatConversionProvider.class.equals(var0) && !AudioFileReader.class.equals(var0) && !AudioFileWriter.class.equals(var0) && !MidiDeviceProvider.class.equals(var0) && !SoundbankReader.class.equals(var0) && !MidiFileWriter.class.equals(var0) && !MidiFileReader.class.equals(var0)) {
         var1 = new ArrayList(0);
      } else {
         var1 = JSSecurityManager.getProviders(var0);
      }

      return Collections.unmodifiableList((List)var1);
   }

   public static synchronized String getDefaultProviderClassName(Class var0) {
      String var1 = null;
      String var2 = getDefaultProvider(var0);
      if (var2 != null) {
         int var3 = var2.indexOf(35);
         if (var3 != 0) {
            if (var3 > 0) {
               var1 = var2.substring(0, var3);
            } else {
               var1 = var2;
            }
         }
      }

      return var1;
   }

   public static synchronized String getDefaultInstanceName(Class var0) {
      String var1 = null;
      String var2 = getDefaultProvider(var0);
      if (var2 != null) {
         int var3 = var2.indexOf(35);
         if (var3 >= 0 && var3 < var2.length() - 1) {
            var1 = var2.substring(var3 + 1);
         }
      }

      return var1;
   }

   private static synchronized String getDefaultProvider(Class var0) {
      if (!SourceDataLine.class.equals(var0) && !TargetDataLine.class.equals(var0) && !Clip.class.equals(var0) && !Port.class.equals(var0) && !Receiver.class.equals(var0) && !Transmitter.class.equals(var0) && !Synthesizer.class.equals(var0) && !Sequencer.class.equals(var0)) {
         return null;
      } else {
         String var1 = var0.getName();
         String var2 = (String)AccessController.doPrivileged(() -> {
            return System.getProperty(var1);
         });
         if (var2 == null) {
            var2 = getProperties().getProperty(var1);
         }

         if ("".equals(var2)) {
            var2 = null;
         }

         return var2;
      }
   }

   private static synchronized Properties getProperties() {
      if (properties == null) {
         properties = new Properties();
         JSSecurityManager.loadProperties(properties, "sound.properties");
      }

      return properties;
   }
}
