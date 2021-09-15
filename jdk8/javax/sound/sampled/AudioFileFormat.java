package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioFileFormat {
   private AudioFileFormat.Type type;
   private int byteLength;
   private AudioFormat format;
   private int frameLength;
   private HashMap<String, Object> properties;

   protected AudioFileFormat(AudioFileFormat.Type var1, int var2, AudioFormat var3, int var4) {
      this.type = var1;
      this.byteLength = var2;
      this.format = var3;
      this.frameLength = var4;
      this.properties = null;
   }

   public AudioFileFormat(AudioFileFormat.Type var1, AudioFormat var2, int var3) {
      this(var1, -1, var2, var3);
   }

   public AudioFileFormat(AudioFileFormat.Type var1, AudioFormat var2, int var3, Map<String, Object> var4) {
      this(var1, -1, var2, var3);
      this.properties = new HashMap(var4);
   }

   public AudioFileFormat.Type getType() {
      return this.type;
   }

   public int getByteLength() {
      return this.byteLength;
   }

   public AudioFormat getFormat() {
      return this.format;
   }

   public int getFrameLength() {
      return this.frameLength;
   }

   public Map<String, Object> properties() {
      Object var1;
      if (this.properties == null) {
         var1 = new HashMap(0);
      } else {
         var1 = (Map)((Map)this.properties.clone());
      }

      return Collections.unmodifiableMap((Map)var1);
   }

   public Object getProperty(String var1) {
      return this.properties == null ? null : this.properties.get(var1);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      if (this.type != null) {
         var1.append(this.type.toString() + " (." + this.type.getExtension() + ") file");
      } else {
         var1.append("unknown file format");
      }

      if (this.byteLength != -1) {
         var1.append(", byte length: " + this.byteLength);
      }

      var1.append(", data format: " + this.format);
      if (this.frameLength != -1) {
         var1.append(", frame length: " + this.frameLength);
      }

      return new String(var1);
   }

   public static class Type {
      public static final AudioFileFormat.Type WAVE = new AudioFileFormat.Type("WAVE", "wav");
      public static final AudioFileFormat.Type AU = new AudioFileFormat.Type("AU", "au");
      public static final AudioFileFormat.Type AIFF = new AudioFileFormat.Type("AIFF", "aif");
      public static final AudioFileFormat.Type AIFC = new AudioFileFormat.Type("AIFF-C", "aifc");
      public static final AudioFileFormat.Type SND = new AudioFileFormat.Type("SND", "snd");
      private final String name;
      private final String extension;

      public Type(String var1, String var2) {
         this.name = var1;
         this.extension = var2;
      }

      public final boolean equals(Object var1) {
         if (this.toString() != null) {
            return var1 instanceof AudioFileFormat.Type ? this.toString().equals(var1.toString()) : false;
         } else {
            return var1 != null && var1.toString() == null;
         }
      }

      public final int hashCode() {
         return this.toString() == null ? 0 : this.toString().hashCode();
      }

      public final String toString() {
         return this.name;
      }

      public String getExtension() {
         return this.extension;
      }
   }
}
