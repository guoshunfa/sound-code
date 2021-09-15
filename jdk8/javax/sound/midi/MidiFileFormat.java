package javax.sound.midi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MidiFileFormat {
   public static final int UNKNOWN_LENGTH = -1;
   protected int type;
   protected float divisionType;
   protected int resolution;
   protected int byteLength;
   protected long microsecondLength;
   private HashMap<String, Object> properties;

   public MidiFileFormat(int var1, float var2, int var3, int var4, long var5) {
      this.type = var1;
      this.divisionType = var2;
      this.resolution = var3;
      this.byteLength = var4;
      this.microsecondLength = var5;
      this.properties = null;
   }

   public MidiFileFormat(int var1, float var2, int var3, int var4, long var5, Map<String, Object> var7) {
      this(var1, var2, var3, var4, var5);
      this.properties = new HashMap(var7);
   }

   public int getType() {
      return this.type;
   }

   public float getDivisionType() {
      return this.divisionType;
   }

   public int getResolution() {
      return this.resolution;
   }

   public int getByteLength() {
      return this.byteLength;
   }

   public long getMicrosecondLength() {
      return this.microsecondLength;
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
}
