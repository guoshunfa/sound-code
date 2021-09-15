package sun.management;

import com.sun.management.GcInfo;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class GcInfoBuilder {
   private final GarbageCollectorMXBean gc;
   private final String[] poolNames;
   private String[] allItemNames;
   private CompositeType gcInfoCompositeType;
   private final int gcExtItemCount;
   private final String[] gcExtItemNames;
   private final String[] gcExtItemDescs;
   private final char[] gcExtItemTypes;

   GcInfoBuilder(GarbageCollectorMXBean var1, String[] var2) {
      this.gc = var1;
      this.poolNames = var2;
      this.gcExtItemCount = this.getNumGcExtAttributes(var1);
      this.gcExtItemNames = new String[this.gcExtItemCount];
      this.gcExtItemDescs = new String[this.gcExtItemCount];
      this.gcExtItemTypes = new char[this.gcExtItemCount];
      this.fillGcAttributeInfo(var1, this.gcExtItemCount, this.gcExtItemNames, this.gcExtItemTypes, this.gcExtItemDescs);
      this.gcInfoCompositeType = null;
   }

   GcInfo getLastGcInfo() {
      MemoryUsage[] var1 = new MemoryUsage[this.poolNames.length];
      MemoryUsage[] var2 = new MemoryUsage[this.poolNames.length];
      Object[] var3 = new Object[this.gcExtItemCount];
      return this.getLastGcInfo0(this.gc, this.gcExtItemCount, var3, this.gcExtItemTypes, var1, var2);
   }

   public String[] getPoolNames() {
      return this.poolNames;
   }

   int getGcExtItemCount() {
      return this.gcExtItemCount;
   }

   synchronized CompositeType getGcInfoCompositeType() {
      if (this.gcInfoCompositeType != null) {
         return this.gcInfoCompositeType;
      } else {
         String[] var1 = GcInfoCompositeData.getBaseGcInfoItemNames();
         OpenType[] var2 = GcInfoCompositeData.getBaseGcInfoItemTypes();
         int var3 = var1.length;
         int var4 = var3 + this.gcExtItemCount;
         this.allItemNames = new String[var4];
         String[] var5 = new String[var4];
         OpenType[] var6 = new OpenType[var4];
         System.arraycopy(var1, 0, this.allItemNames, 0, var3);
         System.arraycopy(var1, 0, var5, 0, var3);
         System.arraycopy(var2, 0, var6, 0, var3);
         if (this.gcExtItemCount > 0) {
            this.fillGcAttributeInfo(this.gc, this.gcExtItemCount, this.gcExtItemNames, this.gcExtItemTypes, this.gcExtItemDescs);
            System.arraycopy(this.gcExtItemNames, 0, this.allItemNames, var3, this.gcExtItemCount);
            System.arraycopy(this.gcExtItemDescs, 0, var5, var3, this.gcExtItemCount);
            int var7 = var3;

            for(int var8 = 0; var8 < this.gcExtItemCount; ++var8) {
               switch(this.gcExtItemTypes[var8]) {
               case 'B':
                  var6[var7] = SimpleType.BYTE;
                  break;
               case 'C':
                  var6[var7] = SimpleType.CHARACTER;
                  break;
               case 'D':
                  var6[var7] = SimpleType.DOUBLE;
                  break;
               case 'E':
               case 'G':
               case 'H':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               default:
                  throw new AssertionError("Unsupported type [" + this.gcExtItemTypes[var7] + "]");
               case 'F':
                  var6[var7] = SimpleType.FLOAT;
                  break;
               case 'I':
                  var6[var7] = SimpleType.INTEGER;
                  break;
               case 'J':
                  var6[var7] = SimpleType.LONG;
                  break;
               case 'S':
                  var6[var7] = SimpleType.SHORT;
                  break;
               case 'Z':
                  var6[var7] = SimpleType.BOOLEAN;
               }

               ++var7;
            }
         }

         CompositeType var10 = null;

         try {
            String var11 = "sun.management." + this.gc.getName() + ".GcInfoCompositeType";
            var10 = new CompositeType(var11, "CompositeType for GC info for " + this.gc.getName(), this.allItemNames, var5, var6);
         } catch (OpenDataException var9) {
            throw Util.newException(var9);
         }

         this.gcInfoCompositeType = var10;
         return this.gcInfoCompositeType;
      }
   }

   synchronized String[] getItemNames() {
      if (this.allItemNames == null) {
         this.getGcInfoCompositeType();
      }

      return this.allItemNames;
   }

   private native int getNumGcExtAttributes(GarbageCollectorMXBean var1);

   private native void fillGcAttributeInfo(GarbageCollectorMXBean var1, int var2, String[] var3, char[] var4, String[] var5);

   private native GcInfo getLastGcInfo0(GarbageCollectorMXBean var1, int var2, Object[] var3, char[] var4, MemoryUsage[] var5, MemoryUsage[] var6);
}
