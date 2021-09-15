package java.time.chrono;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

final class Ser implements Externalizable {
   private static final long serialVersionUID = -6103370247208168577L;
   static final byte CHRONO_TYPE = 1;
   static final byte CHRONO_LOCAL_DATE_TIME_TYPE = 2;
   static final byte CHRONO_ZONE_DATE_TIME_TYPE = 3;
   static final byte JAPANESE_DATE_TYPE = 4;
   static final byte JAPANESE_ERA_TYPE = 5;
   static final byte HIJRAH_DATE_TYPE = 6;
   static final byte MINGUO_DATE_TYPE = 7;
   static final byte THAIBUDDHIST_DATE_TYPE = 8;
   static final byte CHRONO_PERIOD_TYPE = 9;
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

   private static void writeInternal(byte var0, Object var1, ObjectOutput var2) throws IOException {
      var2.writeByte(var0);
      switch(var0) {
      case 1:
         ((AbstractChronology)var1).writeExternal(var2);
         break;
      case 2:
         ((ChronoLocalDateTimeImpl)var1).writeExternal(var2);
         break;
      case 3:
         ((ChronoZonedDateTimeImpl)var1).writeExternal(var2);
         break;
      case 4:
         ((JapaneseDate)var1).writeExternal(var2);
         break;
      case 5:
         ((JapaneseEra)var1).writeExternal(var2);
         break;
      case 6:
         ((HijrahDate)var1).writeExternal(var2);
         break;
      case 7:
         ((MinguoDate)var1).writeExternal(var2);
         break;
      case 8:
         ((ThaiBuddhistDate)var1).writeExternal(var2);
         break;
      case 9:
         ((ChronoPeriodImpl)var1).writeExternal(var2);
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
         return AbstractChronology.readExternal(var1);
      case 2:
         return ChronoLocalDateTimeImpl.readExternal(var1);
      case 3:
         return ChronoZonedDateTimeImpl.readExternal(var1);
      case 4:
         return JapaneseDate.readExternal(var1);
      case 5:
         return JapaneseEra.readExternal(var1);
      case 6:
         return HijrahDate.readExternal(var1);
      case 7:
         return MinguoDate.readExternal(var1);
      case 8:
         return ThaiBuddhistDate.readExternal(var1);
      case 9:
         return ChronoPeriodImpl.readExternal(var1);
      default:
         throw new StreamCorruptedException("Unknown serialized type");
      }
   }

   private Object readResolve() {
      return this.object;
   }
}
