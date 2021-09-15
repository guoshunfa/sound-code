package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Objects;

final class ZoneRegion extends ZoneId implements Serializable {
   private static final long serialVersionUID = 8386373296231747096L;
   private final String id;
   private final transient ZoneRules rules;

   static ZoneRegion ofId(String var0, boolean var1) {
      Objects.requireNonNull(var0, (String)"zoneId");
      checkName(var0);
      ZoneRules var2 = null;

      try {
         var2 = ZoneRulesProvider.getRules(var0, true);
      } catch (ZoneRulesException var4) {
         if (var1) {
            throw var4;
         }
      }

      return new ZoneRegion(var0, var2);
   }

   private static void checkName(String var0) {
      int var1 = var0.length();
      if (var1 < 2) {
         throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + var0);
      } else {
         for(int var2 = 0; var2 < var1; ++var2) {
            char var3 = var0.charAt(var2);
            if ((var3 < 'a' || var3 > 'z') && (var3 < 'A' || var3 > 'Z') && (var3 != '/' || var2 == 0) && (var3 < '0' || var3 > '9' || var2 == 0) && (var3 != '~' || var2 == 0) && (var3 != '.' || var2 == 0) && (var3 != '_' || var2 == 0) && (var3 != '+' || var2 == 0) && (var3 != '-' || var2 == 0)) {
               throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + var0);
            }
         }

      }
   }

   ZoneRegion(String var1, ZoneRules var2) {
      this.id = var1;
      this.rules = var2;
   }

   public String getId() {
      return this.id;
   }

   public ZoneRules getRules() {
      return this.rules != null ? this.rules : ZoneRulesProvider.getRules(this.id, false);
   }

   private Object writeReplace() {
      return new Ser((byte)7, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void write(DataOutput var1) throws IOException {
      var1.writeByte(7);
      this.writeExternal(var1);
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeUTF(this.id);
   }

   static ZoneId readExternal(DataInput var0) throws IOException {
      String var1 = var0.readUTF();
      return ZoneId.of(var1, false);
   }
}
