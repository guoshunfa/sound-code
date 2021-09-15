package sun.management.counter.perf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.management.counter.Counter;
import sun.management.counter.Units;

public class PerfInstrumentation {
   private ByteBuffer buffer;
   private Prologue prologue;
   private long lastModificationTime;
   private long lastUsed;
   private int nextEntry;
   private SortedMap<String, Counter> map;

   public PerfInstrumentation(ByteBuffer var1) {
      this.prologue = new Prologue(var1);
      this.buffer = var1;
      this.buffer.order(this.prologue.getByteOrder());
      int var2 = this.getMajorVersion();
      int var3 = this.getMinorVersion();
      if (var2 < 2) {
         throw new InstrumentationException("Unsupported version: " + var2 + "." + var3);
      } else {
         this.rewind();
      }
   }

   public int getMajorVersion() {
      return this.prologue.getMajorVersion();
   }

   public int getMinorVersion() {
      return this.prologue.getMinorVersion();
   }

   public long getModificationTimeStamp() {
      return this.prologue.getModificationTimeStamp();
   }

   void rewind() {
      this.buffer.rewind();
      this.buffer.position(this.prologue.getEntryOffset());
      this.nextEntry = this.buffer.position();
      this.map = new TreeMap();
   }

   boolean hasNext() {
      return this.nextEntry < this.prologue.getUsed();
   }

   Counter getNextCounter() {
      if (!this.hasNext()) {
         return null;
      } else if (this.nextEntry % 4 != 0) {
         throw new InstrumentationException("Entry index not properly aligned: " + this.nextEntry);
      } else if (this.nextEntry >= 0 && this.nextEntry <= this.buffer.limit()) {
         this.buffer.position(this.nextEntry);
         PerfDataEntry var1 = new PerfDataEntry(this.buffer);
         this.nextEntry += var1.size();
         Object var2 = null;
         PerfDataType var3 = var1.type();
         if (var3 == PerfDataType.BYTE) {
            if (var1.units() == Units.STRING && var1.vectorLength() > 0) {
               var2 = new PerfStringCounter(var1.name(), var1.variability(), var1.flags(), var1.vectorLength(), var1.byteData());
            } else if (var1.vectorLength() > 0) {
               var2 = new PerfByteArrayCounter(var1.name(), var1.units(), var1.variability(), var1.flags(), var1.vectorLength(), var1.byteData());
            } else {
               assert false;
            }
         } else if (var3 == PerfDataType.LONG) {
            if (var1.vectorLength() == 0) {
               var2 = new PerfLongCounter(var1.name(), var1.units(), var1.variability(), var1.flags(), var1.longData());
            } else {
               var2 = new PerfLongArrayCounter(var1.name(), var1.units(), var1.variability(), var1.flags(), var1.vectorLength(), var1.longData());
            }
         } else {
            assert false;
         }

         return (Counter)var2;
      } else {
         throw new InstrumentationException("Entry index out of bounds: nextEntry = " + this.nextEntry + ", limit = " + this.buffer.limit());
      }
   }

   public synchronized List<Counter> getAllCounters() {
      while(this.hasNext()) {
         Counter var1 = this.getNextCounter();
         if (var1 != null) {
            this.map.put(var1.getName(), var1);
         }
      }

      return new ArrayList(this.map.values());
   }

   public synchronized List<Counter> findByPattern(String var1) {
      while(this.hasNext()) {
         Counter var2 = this.getNextCounter();
         if (var2 != null) {
            this.map.put(var2.getName(), var2);
         }
      }

      Pattern var8 = Pattern.compile(var1);
      Matcher var3 = var8.matcher("");
      ArrayList var4 = new ArrayList();
      Iterator var5 = this.map.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         String var7 = (String)var6.getKey();
         var3.reset(var7);
         if (var3.lookingAt()) {
            var4.add(var6.getValue());
         }
      }

      return var4;
   }
}
