package sun.management.counter.perf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class PerfDataEntry {
   private String name;
   private int entryStart;
   private int entryLength;
   private int vectorLength;
   private PerfDataType dataType;
   private int flags;
   private Units unit;
   private Variability variability;
   private int dataOffset;
   private int dataSize;
   private ByteBuffer data;

   PerfDataEntry(ByteBuffer var1) {
      this.entryStart = var1.position();
      this.entryLength = var1.getInt();
      if (this.entryLength > 0 && this.entryLength <= var1.limit()) {
         if (this.entryStart + this.entryLength > var1.limit()) {
            throw new InstrumentationException("Entry extends beyond end of buffer:  entryStart = " + this.entryStart + " entryLength = " + this.entryLength + " buffer limit = " + var1.limit());
         } else {
            var1.position(this.entryStart + 4);
            int var2 = var1.getInt();
            if (this.entryStart + var2 > var1.limit()) {
               throw new InstrumentationException("Invalid name offset:  entryStart = " + this.entryStart + " nameOffset = " + var2 + " buffer limit = " + var1.limit());
            } else {
               var1.position(this.entryStart + 8);
               this.vectorLength = var1.getInt();
               var1.position(this.entryStart + 12);
               this.dataType = PerfDataType.toPerfDataType(var1.get());
               var1.position(this.entryStart + 13);
               this.flags = var1.get();
               var1.position(this.entryStart + 14);
               this.unit = Units.toUnits(var1.get());
               var1.position(this.entryStart + 15);
               this.variability = Variability.toVariability(var1.get());
               var1.position(this.entryStart + 16);
               this.dataOffset = var1.getInt();
               var1.position(this.entryStart + var2);

               int var3;
               for(var3 = 0; var1.get() != 0; ++var3) {
               }

               byte[] var5 = new byte[var3];
               var1.position(this.entryStart + var2);

               for(int var6 = 0; var6 < var3; ++var6) {
                  var5[var6] = var1.get();
               }

               try {
                  this.name = new String(var5, "UTF-8");
               } catch (UnsupportedEncodingException var7) {
                  throw new InternalError(var7.getMessage(), var7);
               }

               if (this.variability == Variability.INVALID) {
                  throw new InstrumentationException("Invalid variability attribute: name = " + this.name);
               } else if (this.unit == Units.INVALID) {
                  throw new InstrumentationException("Invalid units attribute:  name = " + this.name);
               } else {
                  if (this.vectorLength > 0) {
                     this.dataSize = this.vectorLength * this.dataType.size();
                  } else {
                     this.dataSize = this.dataType.size();
                  }

                  if (this.entryStart + this.dataOffset + this.dataSize > var1.limit()) {
                     throw new InstrumentationException("Data extends beyond end of buffer:  entryStart = " + this.entryStart + " dataOffset = " + this.dataOffset + " dataSize = " + this.dataSize + " buffer limit = " + var1.limit());
                  } else {
                     var1.position(this.entryStart + this.dataOffset);
                     this.data = var1.slice();
                     this.data.order(var1.order());
                     this.data.limit(this.dataSize);
                  }
               }
            }
         }
      } else {
         throw new InstrumentationException("Invalid entry length:  entryLength = " + this.entryLength);
      }
   }

   public int size() {
      return this.entryLength;
   }

   public String name() {
      return this.name;
   }

   public PerfDataType type() {
      return this.dataType;
   }

   public Units units() {
      return this.unit;
   }

   public int flags() {
      return this.flags;
   }

   public int vectorLength() {
      return this.vectorLength;
   }

   public Variability variability() {
      return this.variability;
   }

   public ByteBuffer byteData() {
      this.data.position(0);

      assert this.data.remaining() == this.vectorLength();

      return this.data.duplicate();
   }

   public LongBuffer longData() {
      LongBuffer var1 = this.data.asLongBuffer();
      return var1;
   }

   private class EntryFieldOffset {
      private static final int SIZEOF_BYTE = 1;
      private static final int SIZEOF_INT = 4;
      private static final int SIZEOF_LONG = 8;
      private static final int ENTRY_LENGTH_SIZE = 4;
      private static final int NAME_OFFSET_SIZE = 4;
      private static final int VECTOR_LENGTH_SIZE = 4;
      private static final int DATA_TYPE_SIZE = 1;
      private static final int FLAGS_SIZE = 1;
      private static final int DATA_UNIT_SIZE = 1;
      private static final int DATA_VAR_SIZE = 1;
      private static final int DATA_OFFSET_SIZE = 4;
      static final int ENTRY_LENGTH = 0;
      static final int NAME_OFFSET = 4;
      static final int VECTOR_LENGTH = 8;
      static final int DATA_TYPE = 12;
      static final int FLAGS = 13;
      static final int DATA_UNIT = 14;
      static final int DATA_VAR = 15;
      static final int DATA_OFFSET = 16;
   }
}
