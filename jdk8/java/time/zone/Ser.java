package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.time.ZoneOffset;

final class Ser implements Externalizable {
   private static final long serialVersionUID = -8885321777449118786L;
   static final byte ZRULES = 1;
   static final byte ZOT = 2;
   static final byte ZOTRULE = 3;
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

   static void write(Object var0, DataOutput var1) throws IOException {
      writeInternal((byte)1, var0, var1);
   }

   private static void writeInternal(byte var0, Object var1, DataOutput var2) throws IOException {
      var2.writeByte(var0);
      switch(var0) {
      case 1:
         ((ZoneRules)var1).writeExternal(var2);
         break;
      case 2:
         ((ZoneOffsetTransition)var1).writeExternal(var2);
         break;
      case 3:
         ((ZoneOffsetTransitionRule)var1).writeExternal(var2);
         break;
      default:
         throw new InvalidClassException("Unknown serialized type");
      }

   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.type = var1.readByte();
      this.object = readInternal(this.type, var1);
   }

   static Object read(DataInput var0) throws IOException, ClassNotFoundException {
      byte var1 = var0.readByte();
      return readInternal(var1, var0);
   }

   private static Object readInternal(byte var0, DataInput var1) throws IOException, ClassNotFoundException {
      switch(var0) {
      case 1:
         return ZoneRules.readExternal(var1);
      case 2:
         return ZoneOffsetTransition.readExternal(var1);
      case 3:
         return ZoneOffsetTransitionRule.readExternal(var1);
      default:
         throw new StreamCorruptedException("Unknown serialized type");
      }
   }

   private Object readResolve() {
      return this.object;
   }

   static void writeOffset(ZoneOffset var0, DataOutput var1) throws IOException {
      int var2 = var0.getTotalSeconds();
      int var3 = var2 % 900 == 0 ? var2 / 900 : 127;
      var1.writeByte(var3);
      if (var3 == 127) {
         var1.writeInt(var2);
      }

   }

   static ZoneOffset readOffset(DataInput var0) throws IOException {
      byte var1 = var0.readByte();
      return var1 == 127 ? ZoneOffset.ofTotalSeconds(var0.readInt()) : ZoneOffset.ofTotalSeconds(var1 * 900);
   }

   static void writeEpochSec(long var0, DataOutput var2) throws IOException {
      if (var0 >= -4575744000L && var0 < 10413792000L && var0 % 900L == 0L) {
         int var3 = (int)((var0 + 4575744000L) / 900L);
         var2.writeByte(var3 >>> 16 & 255);
         var2.writeByte(var3 >>> 8 & 255);
         var2.writeByte(var3 & 255);
      } else {
         var2.writeByte(255);
         var2.writeLong(var0);
      }

   }

   static long readEpochSec(DataInput var0) throws IOException {
      int var1 = var0.readByte() & 255;
      if (var1 == 255) {
         return var0.readLong();
      } else {
         int var2 = var0.readByte() & 255;
         int var3 = var0.readByte() & 255;
         long var4 = (long)((var1 << 16) + (var2 << 8) + var3);
         return var4 * 900L - 4575744000L;
      }
   }
}
