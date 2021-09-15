package java.time.zone;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

final class TzdbZoneRulesProvider extends ZoneRulesProvider {
   private List<String> regionIds;
   private String versionId;
   private final Map<String, Object> regionToRules = new ConcurrentHashMap();

   public TzdbZoneRulesProvider() {
      try {
         String var1 = System.getProperty("java.home") + File.separator + "lib";
         DataInputStream var2 = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(var1, "tzdb.dat"))));
         Throwable var3 = null;

         try {
            this.load(var2);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }

      } catch (Exception var15) {
         throw new ZoneRulesException("Unable to load TZDB time-zone rules", var15);
      }
   }

   protected Set<String> provideZoneIds() {
      return new HashSet(this.regionIds);
   }

   protected ZoneRules provideRules(String var1, boolean var2) {
      Object var3 = this.regionToRules.get(var1);
      if (var3 == null) {
         throw new ZoneRulesException("Unknown time-zone ID: " + var1);
      } else {
         try {
            if (var3 instanceof byte[]) {
               byte[] var4 = (byte[])((byte[])var3);
               DataInputStream var5 = new DataInputStream(new ByteArrayInputStream(var4));
               var3 = Ser.read(var5);
               this.regionToRules.put(var1, var3);
            }

            return (ZoneRules)var3;
         } catch (Exception var6) {
            throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + var1 + ", version: " + this.versionId, var6);
         }
      }
   }

   protected NavigableMap<String, ZoneRules> provideVersions(String var1) {
      TreeMap var2 = new TreeMap();
      ZoneRules var3 = getRules(var1, false);
      if (var3 != null) {
         var2.put(this.versionId, var3);
      }

      return var2;
   }

   private void load(DataInputStream var1) throws Exception {
      if (var1.readByte() != 1) {
         throw new StreamCorruptedException("File format not recognised");
      } else {
         String var2 = var1.readUTF();
         if (!"TZDB".equals(var2)) {
            throw new StreamCorruptedException("File format not recognised");
         } else {
            short var3 = var1.readShort();

            for(int var4 = 0; var4 < var3; ++var4) {
               this.versionId = var1.readUTF();
            }

            short var13 = var1.readShort();
            String[] var5 = new String[var13];

            for(int var6 = 0; var6 < var13; ++var6) {
               var5[var6] = var1.readUTF();
            }

            this.regionIds = Arrays.asList(var5);
            short var14 = var1.readShort();
            Object[] var7 = new Object[var14];

            int var8;
            for(var8 = 0; var8 < var14; ++var8) {
               byte[] var9 = new byte[var1.readShort()];
               var1.readFully(var9);
               var7[var8] = var9;
            }

            for(var8 = 0; var8 < var3; ++var8) {
               short var15 = var1.readShort();
               this.regionToRules.clear();

               for(int var10 = 0; var10 < var15; ++var10) {
                  String var11 = var5[var1.readShort()];
                  Object var12 = var7[var1.readShort() & '\uffff'];
                  this.regionToRules.put(var11, var12);
               }
            }

         }
      }
   }

   public String toString() {
      return "TZDB[" + this.versionId + "]";
   }
}
