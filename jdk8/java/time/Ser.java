package java.time;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

final class Ser implements Externalizable {
   private static final long serialVersionUID = -7683839454370182990L;
   static final byte DURATION_TYPE = 1;
   static final byte INSTANT_TYPE = 2;
   static final byte LOCAL_DATE_TYPE = 3;
   static final byte LOCAL_TIME_TYPE = 4;
   static final byte LOCAL_DATE_TIME_TYPE = 5;
   static final byte ZONE_DATE_TIME_TYPE = 6;
   static final byte ZONE_REGION_TYPE = 7;
   static final byte ZONE_OFFSET_TYPE = 8;
   static final byte OFFSET_TIME_TYPE = 9;
   static final byte OFFSET_DATE_TIME_TYPE = 10;
   static final byte YEAR_TYPE = 11;
   static final byte YEAR_MONTH_TYPE = 12;
   static final byte MONTH_DAY_TYPE = 13;
   static final byte PERIOD_TYPE = 14;
   private byte type;
   private Object object;

   public Ser() {
   }

   Ser(byte var1, Object var2) {
      this.type = var1;
      this.object = var2;
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      writeInternal(this.type, this.object, var1);
   }

   static void writeInternal(byte var0, Object var1, ObjectOutput var2) throws IOException {
      var2.writeByte(var0);
      switch(var0) {
      case 1:
         ((Duration)var1).writeExternal(var2);
         break;
      case 2:
         ((Instant)var1).writeExternal(var2);
         break;
      case 3:
         ((LocalDate)var1).writeExternal(var2);
         break;
      case 4:
         ((LocalTime)var1).writeExternal(var2);
         break;
      case 5:
         ((LocalDateTime)var1).writeExternal(var2);
         break;
      case 6:
         ((ZonedDateTime)var1).writeExternal(var2);
         break;
      case 7:
         ((ZoneRegion)var1).writeExternal(var2);
         break;
      case 8:
         ((ZoneOffset)var1).writeExternal(var2);
         break;
      case 9:
         ((OffsetTime)var1).writeExternal(var2);
         break;
      case 10:
         ((OffsetDateTime)var1).writeExternal(var2);
         break;
      case 11:
         ((Year)var1).writeExternal(var2);
         break;
      case 12:
         ((YearMonth)var1).writeExternal(var2);
         break;
      case 13:
         ((MonthDay)var1).writeExternal(var2);
         break;
      case 14:
         ((Period)var1).writeExternal(var2);
         break;
      default:
         throw new InvalidClassException("Unknown serialized type");
      }

   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.type = var1.readByte();
      this.object = readInternal(this.type, var1);
   }

   static Object read(ObjectInput var0) throws IOException, ClassNotFoundException {
      byte var1 = var0.readByte();
      return readInternal(var1, var0);
   }

   private static Object readInternal(byte var0, ObjectInput var1) throws IOException, ClassNotFoundException {
      switch(var0) {
      case 1:
         return Duration.readExternal(var1);
      case 2:
         return Instant.readExternal(var1);
      case 3:
         return LocalDate.readExternal(var1);
      case 4:
         return LocalTime.readExternal(var1);
      case 5:
         return LocalDateTime.readExternal(var1);
      case 6:
         return ZonedDateTime.readExternal(var1);
      case 7:
         return ZoneRegion.readExternal(var1);
      case 8:
         return ZoneOffset.readExternal(var1);
      case 9:
         return OffsetTime.readExternal(var1);
      case 10:
         return OffsetDateTime.readExternal(var1);
      case 11:
         return Year.readExternal(var1);
      case 12:
         return YearMonth.readExternal(var1);
      case 13:
         return MonthDay.readExternal(var1);
      case 14:
         return Period.readExternal(var1);
      default:
         throw new StreamCorruptedException("Unknown serialized type");
      }
   }

   private Object readResolve() {
      return this.object;
   }
}
