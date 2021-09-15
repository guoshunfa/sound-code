package java.time.format;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class DateTimeParseContext {
   private DateTimeFormatter formatter;
   private boolean caseSensitive = true;
   private boolean strict = true;
   private final ArrayList<Parsed> parsed = new ArrayList();
   private ArrayList<Consumer<Chronology>> chronoListeners = null;

   DateTimeParseContext(DateTimeFormatter var1) {
      this.formatter = var1;
      this.parsed.add(new Parsed());
   }

   DateTimeParseContext copy() {
      DateTimeParseContext var1 = new DateTimeParseContext(this.formatter);
      var1.caseSensitive = this.caseSensitive;
      var1.strict = this.strict;
      return var1;
   }

   Locale getLocale() {
      return this.formatter.getLocale();
   }

   DecimalStyle getDecimalStyle() {
      return this.formatter.getDecimalStyle();
   }

   Chronology getEffectiveChronology() {
      Object var1 = this.currentParsed().chrono;
      if (var1 == null) {
         var1 = this.formatter.getChronology();
         if (var1 == null) {
            var1 = IsoChronology.INSTANCE;
         }
      }

      return (Chronology)var1;
   }

   boolean isCaseSensitive() {
      return this.caseSensitive;
   }

   void setCaseSensitive(boolean var1) {
      this.caseSensitive = var1;
   }

   boolean subSequenceEquals(CharSequence var1, int var2, CharSequence var3, int var4, int var5) {
      if (var2 + var5 <= var1.length() && var4 + var5 <= var3.length()) {
         int var6;
         char var7;
         char var8;
         if (this.isCaseSensitive()) {
            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var1.charAt(var2 + var6);
               var8 = var3.charAt(var4 + var6);
               if (var7 != var8) {
                  return false;
               }
            }
         } else {
            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var1.charAt(var2 + var6);
               var8 = var3.charAt(var4 + var6);
               if (var7 != var8 && Character.toUpperCase(var7) != Character.toUpperCase(var8) && Character.toLowerCase(var7) != Character.toLowerCase(var8)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   boolean charEquals(char var1, char var2) {
      if (this.isCaseSensitive()) {
         return var1 == var2;
      } else {
         return charEqualsIgnoreCase(var1, var2);
      }
   }

   static boolean charEqualsIgnoreCase(char var0, char var1) {
      return var0 == var1 || Character.toUpperCase(var0) == Character.toUpperCase(var1) || Character.toLowerCase(var0) == Character.toLowerCase(var1);
   }

   boolean isStrict() {
      return this.strict;
   }

   void setStrict(boolean var1) {
      this.strict = var1;
   }

   void startOptional() {
      this.parsed.add(this.currentParsed().copy());
   }

   void endOptional(boolean var1) {
      if (var1) {
         this.parsed.remove(this.parsed.size() - 2);
      } else {
         this.parsed.remove(this.parsed.size() - 1);
      }

   }

   private Parsed currentParsed() {
      return (Parsed)this.parsed.get(this.parsed.size() - 1);
   }

   Parsed toUnresolved() {
      return this.currentParsed();
   }

   TemporalAccessor toResolved(ResolverStyle var1, Set<TemporalField> var2) {
      Parsed var3 = this.currentParsed();
      var3.chrono = this.getEffectiveChronology();
      var3.zone = var3.zone != null ? var3.zone : this.formatter.getZone();
      return var3.resolve(var1, var2);
   }

   Long getParsed(TemporalField var1) {
      return (Long)this.currentParsed().fieldValues.get(var1);
   }

   int setParsedField(TemporalField var1, long var2, int var4, int var5) {
      Objects.requireNonNull(var1, (String)"field");
      Long var6 = (Long)this.currentParsed().fieldValues.put(var1, var2);
      return var6 != null && var6 != var2 ? ~var4 : var5;
   }

   void setParsed(Chronology var1) {
      Objects.requireNonNull(var1, (String)"chrono");
      this.currentParsed().chrono = var1;
      if (this.chronoListeners != null && !this.chronoListeners.isEmpty()) {
         Consumer[] var2 = new Consumer[1];
         Consumer[] var3 = (Consumer[])this.chronoListeners.toArray(var2);
         this.chronoListeners.clear();
         Consumer[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Consumer var7 = var4[var6];
            var7.accept(var1);
         }
      }

   }

   void addChronoChangedListener(Consumer<Chronology> var1) {
      if (this.chronoListeners == null) {
         this.chronoListeners = new ArrayList();
      }

      this.chronoListeners.add(var1);
   }

   void setParsed(ZoneId var1) {
      Objects.requireNonNull(var1, (String)"zone");
      this.currentParsed().zone = var1;
   }

   void setParsedLeapSecond() {
      this.currentParsed().leapSecond = true;
   }

   public String toString() {
      return this.currentParsed().toString();
   }
}
