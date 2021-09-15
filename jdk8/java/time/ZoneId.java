package java.time;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

public abstract class ZoneId implements Serializable {
   public static final Map<String, String> SHORT_IDS;
   private static final long serialVersionUID = 8352817235686L;

   public static ZoneId systemDefault() {
      return TimeZone.getDefault().toZoneId();
   }

   public static Set<String> getAvailableZoneIds() {
      return ZoneRulesProvider.getAvailableZoneIds();
   }

   public static ZoneId of(String var0, Map<String, String> var1) {
      Objects.requireNonNull(var0, (String)"zoneId");
      Objects.requireNonNull(var1, (String)"aliasMap");
      String var2 = (String)var1.get(var0);
      var2 = var2 != null ? var2 : var0;
      return of(var2);
   }

   public static ZoneId of(String var0) {
      return of(var0, true);
   }

   public static ZoneId ofOffset(String var0, ZoneOffset var1) {
      Objects.requireNonNull(var0, (String)"prefix");
      Objects.requireNonNull(var1, (String)"offset");
      if (var0.length() == 0) {
         return var1;
      } else if (!var0.equals("GMT") && !var0.equals("UTC") && !var0.equals("UT")) {
         throw new IllegalArgumentException("prefix should be GMT, UTC or UT, is: " + var0);
      } else {
         if (var1.getTotalSeconds() != 0) {
            var0 = var0.concat(var1.getId());
         }

         return new ZoneRegion(var0, var1.getRules());
      }
   }

   static ZoneId of(String var0, boolean var1) {
      Objects.requireNonNull(var0, (String)"zoneId");
      if (var0.length() > 1 && !var0.startsWith("+") && !var0.startsWith("-")) {
         if (!var0.startsWith("UTC") && !var0.startsWith("GMT")) {
            return (ZoneId)(var0.startsWith("UT") ? ofWithPrefix(var0, 2, var1) : ZoneRegion.ofId(var0, var1));
         } else {
            return ofWithPrefix(var0, 3, var1);
         }
      } else {
         return ZoneOffset.of(var0);
      }
   }

   private static ZoneId ofWithPrefix(String var0, int var1, boolean var2) {
      String var3 = var0.substring(0, var1);
      if (var0.length() == var1) {
         return ofOffset(var3, ZoneOffset.UTC);
      } else if (var0.charAt(var1) != '+' && var0.charAt(var1) != '-') {
         return ZoneRegion.ofId(var0, var2);
      } else {
         try {
            ZoneOffset var4 = ZoneOffset.of(var0.substring(var1));
            return var4 == ZoneOffset.UTC ? ofOffset(var3, var4) : ofOffset(var3, var4);
         } catch (DateTimeException var5) {
            throw new DateTimeException("Invalid ID for offset-based ZoneId: " + var0, var5);
         }
      }
   }

   public static ZoneId from(TemporalAccessor var0) {
      ZoneId var1 = (ZoneId)var0.query(TemporalQueries.zone());
      if (var1 == null) {
         throw new DateTimeException("Unable to obtain ZoneId from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName());
      } else {
         return var1;
      }
   }

   ZoneId() {
      if (this.getClass() != ZoneOffset.class && this.getClass() != ZoneRegion.class) {
         throw new AssertionError("Invalid subclass");
      }
   }

   public abstract String getId();

   public String getDisplayName(TextStyle var1, Locale var2) {
      return (new DateTimeFormatterBuilder()).appendZoneText(var1).toFormatter(var2).format(this.toTemporal());
   }

   private TemporalAccessor toTemporal() {
      return new TemporalAccessor() {
         public boolean isSupported(TemporalField var1) {
            return false;
         }

         public long getLong(TemporalField var1) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }

         public <R> R query(TemporalQuery<R> var1) {
            return var1 == TemporalQueries.zoneId() ? ZoneId.this : TemporalAccessor.super.query(var1);
         }
      };
   }

   public abstract ZoneRules getRules();

   public ZoneId normalized() {
      try {
         ZoneRules var1 = this.getRules();
         if (var1.isFixedOffset()) {
            return var1.getOffset(Instant.EPOCH);
         }
      } catch (ZoneRulesException var2) {
      }

      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ZoneId) {
         ZoneId var2 = (ZoneId)var1;
         return this.getId().equals(var2.getId());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getId().hashCode();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   public String toString() {
      return this.getId();
   }

   private Object writeReplace() {
      return new Ser((byte)7, this);
   }

   abstract void write(DataOutput var1) throws IOException;

   static {
      HashMap var0 = new HashMap(64);
      var0.put("ACT", "Australia/Darwin");
      var0.put("AET", "Australia/Sydney");
      var0.put("AGT", "America/Argentina/Buenos_Aires");
      var0.put("ART", "Africa/Cairo");
      var0.put("AST", "America/Anchorage");
      var0.put("BET", "America/Sao_Paulo");
      var0.put("BST", "Asia/Dhaka");
      var0.put("CAT", "Africa/Harare");
      var0.put("CNT", "America/St_Johns");
      var0.put("CST", "America/Chicago");
      var0.put("CTT", "Asia/Shanghai");
      var0.put("EAT", "Africa/Addis_Ababa");
      var0.put("ECT", "Europe/Paris");
      var0.put("IET", "America/Indiana/Indianapolis");
      var0.put("IST", "Asia/Kolkata");
      var0.put("JST", "Asia/Tokyo");
      var0.put("MIT", "Pacific/Apia");
      var0.put("NET", "Asia/Yerevan");
      var0.put("NST", "Pacific/Auckland");
      var0.put("PLT", "Asia/Karachi");
      var0.put("PNT", "America/Phoenix");
      var0.put("PRT", "America/Puerto_Rico");
      var0.put("PST", "America/Los_Angeles");
      var0.put("SST", "Pacific/Guadalcanal");
      var0.put("VST", "Asia/Ho_Chi_Minh");
      var0.put("EST", "-05:00");
      var0.put("MST", "-07:00");
      var0.put("HST", "-10:00");
      SHORT_IDS = Collections.unmodifiableMap(var0);
   }
}
