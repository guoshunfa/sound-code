package javax.sound.sampled;

import com.sun.media.sound.JDK13Services;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.MixerProvider;

public class AudioSystem {
   public static final int NOT_SPECIFIED = -1;

   private AudioSystem() {
   }

   public static Mixer.Info[] getMixerInfo() {
      List var0 = getMixerInfoList();
      Mixer.Info[] var1 = (Mixer.Info[])((Mixer.Info[])var0.toArray(new Mixer.Info[var0.size()]));
      return var1;
   }

   public static Mixer getMixer(Mixer.Info var0) {
      Object var1 = null;
      List var2 = getMixerProviders();

      int var3;
      for(var3 = 0; var3 < var2.size(); ++var3) {
         try {
            return ((MixerProvider)var2.get(var3)).getMixer(var0);
         } catch (IllegalArgumentException var8) {
         } catch (NullPointerException var9) {
         }
      }

      if (var0 == null) {
         for(var3 = 0; var3 < var2.size(); ++var3) {
            try {
               MixerProvider var4 = (MixerProvider)var2.get(var3);
               Mixer.Info[] var5 = var4.getMixerInfo();
               int var6 = 0;

               while(var6 < var5.length) {
                  try {
                     return var4.getMixer(var5[var6]);
                  } catch (IllegalArgumentException var10) {
                     ++var6;
                  }
               }
            } catch (IllegalArgumentException var11) {
            } catch (NullPointerException var12) {
            }
         }
      }

      throw new IllegalArgumentException("Mixer not supported: " + (var0 != null ? var0.toString() : "null"));
   }

   public static Line.Info[] getSourceLineInfo(Line.Info var0) {
      Vector var1 = new Vector();
      Object var4 = null;
      Mixer.Info[] var5 = getMixerInfo();

      int var7;
      for(int var6 = 0; var6 < var5.length; ++var6) {
         Mixer var3 = getMixer(var5[var6]);
         Line.Info[] var2 = var3.getSourceLineInfo(var0);

         for(var7 = 0; var7 < var2.length; ++var7) {
            var1.addElement(var2[var7]);
         }
      }

      Line.Info[] var8 = new Line.Info[var1.size()];

      for(var7 = 0; var7 < var8.length; ++var7) {
         var8[var7] = (Line.Info)var1.get(var7);
      }

      return var8;
   }

   public static Line.Info[] getTargetLineInfo(Line.Info var0) {
      Vector var1 = new Vector();
      Object var4 = null;
      Mixer.Info[] var5 = getMixerInfo();

      int var7;
      for(int var6 = 0; var6 < var5.length; ++var6) {
         Mixer var3 = getMixer(var5[var6]);
         Line.Info[] var2 = var3.getTargetLineInfo(var0);

         for(var7 = 0; var7 < var2.length; ++var7) {
            var1.addElement(var2[var7]);
         }
      }

      Line.Info[] var8 = new Line.Info[var1.size()];

      for(var7 = 0; var7 < var8.length; ++var7) {
         var8[var7] = (Line.Info)var1.get(var7);
      }

      return var8;
   }

   public static boolean isLineSupported(Line.Info var0) {
      Mixer.Info[] var2 = getMixerInfo();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] != null) {
            Mixer var1 = getMixer(var2[var3]);
            if (var1.isLineSupported(var0)) {
               return true;
            }
         }
      }

      return false;
   }

   public static Line getLine(Line.Info var0) throws LineUnavailableException {
      LineUnavailableException var1 = null;
      List var2 = getMixerProviders();

      try {
         Mixer var3 = getDefaultMixer(var2, var0);
         if (var3 != null && var3.isLineSupported(var0)) {
            return var3.getLine(var0);
         }
      } catch (LineUnavailableException var12) {
         var1 = var12;
      } catch (IllegalArgumentException var13) {
      }

      MixerProvider var4;
      Mixer.Info[] var5;
      int var6;
      Mixer var7;
      int var14;
      for(var14 = 0; var14 < var2.size(); ++var14) {
         var4 = (MixerProvider)var2.get(var14);
         var5 = var4.getMixerInfo();

         for(var6 = 0; var6 < var5.length; ++var6) {
            try {
               var7 = var4.getMixer(var5[var6]);
               if (isAppropriateMixer(var7, var0, true)) {
                  return var7.getLine(var0);
               }
            } catch (LineUnavailableException var10) {
               var1 = var10;
            } catch (IllegalArgumentException var11) {
            }
         }
      }

      for(var14 = 0; var14 < var2.size(); ++var14) {
         var4 = (MixerProvider)var2.get(var14);
         var5 = var4.getMixerInfo();

         for(var6 = 0; var6 < var5.length; ++var6) {
            try {
               var7 = var4.getMixer(var5[var6]);
               if (isAppropriateMixer(var7, var0, false)) {
                  return var7.getLine(var0);
               }
            } catch (LineUnavailableException var8) {
               var1 = var8;
            } catch (IllegalArgumentException var9) {
            }
         }
      }

      if (var1 != null) {
         throw var1;
      } else {
         throw new IllegalArgumentException("No line matching " + var0.toString() + " is supported.");
      }
   }

   public static Clip getClip() throws LineUnavailableException {
      AudioFormat var0 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true);
      DataLine.Info var1 = new DataLine.Info(Clip.class, var0);
      return (Clip)getLine(var1);
   }

   public static Clip getClip(Mixer.Info var0) throws LineUnavailableException {
      AudioFormat var1 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 16, 2, 4, -1.0F, true);
      DataLine.Info var2 = new DataLine.Info(Clip.class, var1);
      Mixer var3 = getMixer(var0);
      return (Clip)var3.getLine(var2);
   }

   public static SourceDataLine getSourceDataLine(AudioFormat var0) throws LineUnavailableException {
      DataLine.Info var1 = new DataLine.Info(SourceDataLine.class, var0);
      return (SourceDataLine)getLine(var1);
   }

   public static SourceDataLine getSourceDataLine(AudioFormat var0, Mixer.Info var1) throws LineUnavailableException {
      DataLine.Info var2 = new DataLine.Info(SourceDataLine.class, var0);
      Mixer var3 = getMixer(var1);
      return (SourceDataLine)var3.getLine(var2);
   }

   public static TargetDataLine getTargetDataLine(AudioFormat var0) throws LineUnavailableException {
      DataLine.Info var1 = new DataLine.Info(TargetDataLine.class, var0);
      return (TargetDataLine)getLine(var1);
   }

   public static TargetDataLine getTargetDataLine(AudioFormat var0, Mixer.Info var1) throws LineUnavailableException {
      DataLine.Info var2 = new DataLine.Info(TargetDataLine.class, var0);
      Mixer var3 = getMixer(var1);
      return (TargetDataLine)var3.getLine(var2);
   }

   public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat.Encoding var0) {
      List var1 = getFormatConversionProviders();
      Vector var2 = new Vector();
      AudioFormat.Encoding[] var3 = null;

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         FormatConversionProvider var5 = (FormatConversionProvider)var1.get(var4);
         if (var5.isSourceEncodingSupported(var0)) {
            var3 = var5.getTargetEncodings();

            for(int var6 = 0; var6 < var3.length; ++var6) {
               var2.addElement(var3[var6]);
            }
         }
      }

      AudioFormat.Encoding[] var7 = (AudioFormat.Encoding[])((AudioFormat.Encoding[])var2.toArray(new AudioFormat.Encoding[0]));
      return var7;
   }

   public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat var0) {
      List var1 = getFormatConversionProviders();
      Vector var2 = new Vector();
      int var3 = 0;
      int var4 = 0;
      AudioFormat.Encoding[] var5 = null;

      for(int var6 = 0; var6 < var1.size(); ++var6) {
         var5 = ((FormatConversionProvider)var1.get(var6)).getTargetEncodings(var0);
         var3 += var5.length;
         var2.addElement(var5);
      }

      AudioFormat.Encoding[] var9 = new AudioFormat.Encoding[var3];

      for(int var7 = 0; var7 < var2.size(); ++var7) {
         var5 = (AudioFormat.Encoding[])((AudioFormat.Encoding[])var2.get(var7));

         for(int var8 = 0; var8 < var5.length; ++var8) {
            var9[var4++] = var5[var8];
         }
      }

      return var9;
   }

   public static boolean isConversionSupported(AudioFormat.Encoding var0, AudioFormat var1) {
      List var2 = getFormatConversionProviders();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         FormatConversionProvider var4 = (FormatConversionProvider)var2.get(var3);
         if (var4.isConversionSupported(var0, var1)) {
            return true;
         }
      }

      return false;
   }

   public static AudioInputStream getAudioInputStream(AudioFormat.Encoding var0, AudioInputStream var1) {
      List var2 = getFormatConversionProviders();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         FormatConversionProvider var4 = (FormatConversionProvider)var2.get(var3);
         if (var4.isConversionSupported(var0, var1.getFormat())) {
            return var4.getAudioInputStream(var0, var1);
         }
      }

      throw new IllegalArgumentException("Unsupported conversion: " + var0 + " from " + var1.getFormat());
   }

   public static AudioFormat[] getTargetFormats(AudioFormat.Encoding var0, AudioFormat var1) {
      List var2 = getFormatConversionProviders();
      Vector var3 = new Vector();
      int var4 = 0;
      int var5 = 0;
      AudioFormat[] var6 = null;

      for(int var7 = 0; var7 < var2.size(); ++var7) {
         FormatConversionProvider var8 = (FormatConversionProvider)var2.get(var7);
         var6 = var8.getTargetFormats(var0, var1);
         var4 += var6.length;
         var3.addElement(var6);
      }

      AudioFormat[] var10 = new AudioFormat[var4];

      for(int var11 = 0; var11 < var3.size(); ++var11) {
         var6 = (AudioFormat[])((AudioFormat[])var3.get(var11));

         for(int var9 = 0; var9 < var6.length; ++var9) {
            var10[var5++] = var6[var9];
         }
      }

      return var10;
   }

   public static boolean isConversionSupported(AudioFormat var0, AudioFormat var1) {
      List var2 = getFormatConversionProviders();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         FormatConversionProvider var4 = (FormatConversionProvider)var2.get(var3);
         if (var4.isConversionSupported(var0, var1)) {
            return true;
         }
      }

      return false;
   }

   public static AudioInputStream getAudioInputStream(AudioFormat var0, AudioInputStream var1) {
      if (var1.getFormat().matches(var0)) {
         return var1;
      } else {
         List var2 = getFormatConversionProviders();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            FormatConversionProvider var4 = (FormatConversionProvider)var2.get(var3);
            if (var4.isConversionSupported(var0, var1.getFormat())) {
               return var4.getAudioInputStream(var0, var1);
            }
         }

         throw new IllegalArgumentException("Unsupported conversion: " + var0 + " from " + var1.getFormat());
      }
   }

   public static AudioFileFormat getAudioFileFormat(InputStream var0) throws UnsupportedAudioFileException, IOException {
      List var1 = getAudioFileReaders();
      AudioFileFormat var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         AudioFileReader var4 = (AudioFileReader)var1.get(var3);

         try {
            var2 = var4.getAudioFileFormat(var0);
            break;
         } catch (UnsupportedAudioFileException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new UnsupportedAudioFileException("file is not a supported file type");
      } else {
         return var2;
      }
   }

   public static AudioFileFormat getAudioFileFormat(URL var0) throws UnsupportedAudioFileException, IOException {
      List var1 = getAudioFileReaders();
      AudioFileFormat var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         AudioFileReader var4 = (AudioFileReader)var1.get(var3);

         try {
            var2 = var4.getAudioFileFormat(var0);
            break;
         } catch (UnsupportedAudioFileException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new UnsupportedAudioFileException("file is not a supported file type");
      } else {
         return var2;
      }
   }

   public static AudioFileFormat getAudioFileFormat(File var0) throws UnsupportedAudioFileException, IOException {
      List var1 = getAudioFileReaders();
      AudioFileFormat var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         AudioFileReader var4 = (AudioFileReader)var1.get(var3);

         try {
            var2 = var4.getAudioFileFormat(var0);
            break;
         } catch (UnsupportedAudioFileException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new UnsupportedAudioFileException("file is not a supported file type");
      } else {
         return var2;
      }
   }

   public static AudioInputStream getAudioInputStream(InputStream var0) throws UnsupportedAudioFileException, IOException {
      List var1 = getAudioFileReaders();
      AudioInputStream var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         AudioFileReader var4 = (AudioFileReader)var1.get(var3);

         try {
            var2 = var4.getAudioInputStream(var0);
            break;
         } catch (UnsupportedAudioFileException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new UnsupportedAudioFileException("could not get audio input stream from input stream");
      } else {
         return var2;
      }
   }

   public static AudioInputStream getAudioInputStream(URL var0) throws UnsupportedAudioFileException, IOException {
      List var1 = getAudioFileReaders();
      AudioInputStream var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         AudioFileReader var4 = (AudioFileReader)var1.get(var3);

         try {
            var2 = var4.getAudioInputStream(var0);
            break;
         } catch (UnsupportedAudioFileException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new UnsupportedAudioFileException("could not get audio input stream from input URL");
      } else {
         return var2;
      }
   }

   public static AudioInputStream getAudioInputStream(File var0) throws UnsupportedAudioFileException, IOException {
      List var1 = getAudioFileReaders();
      AudioInputStream var2 = null;
      int var3 = 0;

      while(var3 < var1.size()) {
         AudioFileReader var4 = (AudioFileReader)var1.get(var3);

         try {
            var2 = var4.getAudioInputStream(var0);
            break;
         } catch (UnsupportedAudioFileException var6) {
            ++var3;
         }
      }

      if (var2 == null) {
         throw new UnsupportedAudioFileException("could not get audio input stream from input file");
      } else {
         return var2;
      }
   }

   public static AudioFileFormat.Type[] getAudioFileTypes() {
      List var0 = getAudioFileWriters();
      HashSet var1 = new HashSet();

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         AudioFileWriter var3 = (AudioFileWriter)var0.get(var2);
         AudioFileFormat.Type[] var4 = var3.getAudioFileTypes();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var1.add(var4[var5]);
         }
      }

      AudioFileFormat.Type[] var6 = (AudioFileFormat.Type[])((AudioFileFormat.Type[])var1.toArray(new AudioFileFormat.Type[0]));
      return var6;
   }

   public static boolean isFileTypeSupported(AudioFileFormat.Type var0) {
      List var1 = getAudioFileWriters();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         AudioFileWriter var3 = (AudioFileWriter)var1.get(var2);
         if (var3.isFileTypeSupported(var0)) {
            return true;
         }
      }

      return false;
   }

   public static AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream var0) {
      List var1 = getAudioFileWriters();
      HashSet var2 = new HashSet();

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         AudioFileWriter var4 = (AudioFileWriter)var1.get(var3);
         AudioFileFormat.Type[] var5 = var4.getAudioFileTypes(var0);

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var2.add(var5[var6]);
         }
      }

      AudioFileFormat.Type[] var7 = (AudioFileFormat.Type[])((AudioFileFormat.Type[])var2.toArray(new AudioFileFormat.Type[0]));
      return var7;
   }

   public static boolean isFileTypeSupported(AudioFileFormat.Type var0, AudioInputStream var1) {
      List var2 = getAudioFileWriters();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         AudioFileWriter var4 = (AudioFileWriter)var2.get(var3);
         if (var4.isFileTypeSupported(var0, var1)) {
            return true;
         }
      }

      return false;
   }

   public static int write(AudioInputStream var0, AudioFileFormat.Type var1, OutputStream var2) throws IOException {
      List var3 = getAudioFileWriters();
      int var4 = 0;
      boolean var5 = false;
      int var6 = 0;

      while(var6 < var3.size()) {
         AudioFileWriter var7 = (AudioFileWriter)var3.get(var6);

         try {
            var4 = var7.write(var0, var1, var2);
            var5 = true;
            break;
         } catch (IllegalArgumentException var9) {
            ++var6;
         }
      }

      if (!var5) {
         throw new IllegalArgumentException("could not write audio file: file type not supported: " + var1);
      } else {
         return var4;
      }
   }

   public static int write(AudioInputStream var0, AudioFileFormat.Type var1, File var2) throws IOException {
      List var3 = getAudioFileWriters();
      int var4 = 0;
      boolean var5 = false;
      int var6 = 0;

      while(var6 < var3.size()) {
         AudioFileWriter var7 = (AudioFileWriter)var3.get(var6);

         try {
            var4 = var7.write(var0, var1, var2);
            var5 = true;
            break;
         } catch (IllegalArgumentException var9) {
            ++var6;
         }
      }

      if (!var5) {
         throw new IllegalArgumentException("could not write audio file: file type not supported: " + var1);
      } else {
         return var4;
      }
   }

   private static List getMixerProviders() {
      return getProviders(MixerProvider.class);
   }

   private static List getFormatConversionProviders() {
      return getProviders(FormatConversionProvider.class);
   }

   private static List getAudioFileReaders() {
      return getProviders(AudioFileReader.class);
   }

   private static List getAudioFileWriters() {
      return getProviders(AudioFileWriter.class);
   }

   private static Mixer getDefaultMixer(List var0, Line.Info var1) {
      Class var2 = var1.getLineClass();
      String var3 = JDK13Services.getDefaultProviderClassName(var2);
      String var4 = JDK13Services.getDefaultInstanceName(var2);
      Mixer var5;
      if (var3 != null) {
         MixerProvider var6 = getNamedProvider(var3, var0);
         if (var6 != null) {
            if (var4 != null) {
               var5 = getNamedMixer(var4, var6, var1);
               if (var5 != null) {
                  return var5;
               }
            } else {
               var5 = getFirstMixer(var6, var1, false);
               if (var5 != null) {
                  return var5;
               }
            }
         }
      }

      if (var4 != null) {
         var5 = getNamedMixer(var4, var0, var1);
         if (var5 != null) {
            return var5;
         }
      }

      return null;
   }

   private static MixerProvider getNamedProvider(String var0, List var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         MixerProvider var3 = (MixerProvider)var1.get(var2);
         if (var3.getClass().getName().equals(var0)) {
            return var3;
         }
      }

      return null;
   }

   private static Mixer getNamedMixer(String var0, MixerProvider var1, Line.Info var2) {
      Mixer.Info[] var3 = var1.getMixerInfo();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4].getName().equals(var0)) {
            Mixer var5 = var1.getMixer(var3[var4]);
            if (isAppropriateMixer(var5, var2, false)) {
               return var5;
            }
         }
      }

      return null;
   }

   private static Mixer getNamedMixer(String var0, List var1, Line.Info var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         MixerProvider var4 = (MixerProvider)var1.get(var3);
         Mixer var5 = getNamedMixer(var0, var4, var2);
         if (var5 != null) {
            return var5;
         }
      }

      return null;
   }

   private static Mixer getFirstMixer(MixerProvider var0, Line.Info var1, boolean var2) {
      Mixer.Info[] var3 = var0.getMixerInfo();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Mixer var5 = var0.getMixer(var3[var4]);
         if (isAppropriateMixer(var5, var1, var2)) {
            return var5;
         }
      }

      return null;
   }

   private static boolean isAppropriateMixer(Mixer var0, Line.Info var1, boolean var2) {
      if (!var0.isLineSupported(var1)) {
         return false;
      } else {
         Class var3 = var1.getLineClass();
         if (!var2 || !SourceDataLine.class.isAssignableFrom(var3) && !Clip.class.isAssignableFrom(var3)) {
            return true;
         } else {
            int var4 = var0.getMaxLines(var1);
            return var4 == -1 || var4 > 1;
         }
      }
   }

   private static List getMixerInfoList() {
      List var0 = getMixerProviders();
      return getMixerInfoList(var0);
   }

   private static List getMixerInfoList(List var0) {
      ArrayList var1 = new ArrayList();

      for(int var4 = 0; var4 < var0.size(); ++var4) {
         Mixer.Info[] var2 = (Mixer.Info[])((MixerProvider)var0.get(var4)).getMixerInfo();

         for(int var5 = 0; var5 < var2.length; ++var5) {
            var1.add(var2[var5]);
         }
      }

      return var1;
   }

   private static List getProviders(Class var0) {
      return JDK13Services.getProviders(var0);
   }
}
