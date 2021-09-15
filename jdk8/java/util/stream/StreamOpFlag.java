package java.util.stream;

import java.util.EnumMap;
import java.util.Map;
import java.util.Spliterator;

enum StreamOpFlag {
   DISTINCT(0, set(StreamOpFlag.Type.SPLITERATOR).set(StreamOpFlag.Type.STREAM).setAndClear(StreamOpFlag.Type.OP)),
   SORTED(1, set(StreamOpFlag.Type.SPLITERATOR).set(StreamOpFlag.Type.STREAM).setAndClear(StreamOpFlag.Type.OP)),
   ORDERED(2, set(StreamOpFlag.Type.SPLITERATOR).set(StreamOpFlag.Type.STREAM).setAndClear(StreamOpFlag.Type.OP).clear(StreamOpFlag.Type.TERMINAL_OP).clear(StreamOpFlag.Type.UPSTREAM_TERMINAL_OP)),
   SIZED(3, set(StreamOpFlag.Type.SPLITERATOR).set(StreamOpFlag.Type.STREAM).clear(StreamOpFlag.Type.OP)),
   SHORT_CIRCUIT(12, set(StreamOpFlag.Type.OP).set(StreamOpFlag.Type.TERMINAL_OP));

   private static final int SET_BITS = 1;
   private static final int CLEAR_BITS = 2;
   private static final int PRESERVE_BITS = 3;
   private final Map<StreamOpFlag.Type, Integer> maskTable;
   private final int bitPosition;
   private final int set;
   private final int clear;
   private final int preserve;
   static final int SPLITERATOR_CHARACTERISTICS_MASK = createMask(StreamOpFlag.Type.SPLITERATOR);
   static final int STREAM_MASK = createMask(StreamOpFlag.Type.STREAM);
   static final int OP_MASK = createMask(StreamOpFlag.Type.OP);
   static final int TERMINAL_OP_MASK = createMask(StreamOpFlag.Type.TERMINAL_OP);
   static final int UPSTREAM_TERMINAL_OP_MASK = createMask(StreamOpFlag.Type.UPSTREAM_TERMINAL_OP);
   private static final int FLAG_MASK = createFlagMask();
   private static final int FLAG_MASK_IS = STREAM_MASK;
   private static final int FLAG_MASK_NOT = STREAM_MASK << 1;
   static final int INITIAL_OPS_VALUE = FLAG_MASK_IS | FLAG_MASK_NOT;
   static final int IS_DISTINCT = DISTINCT.set;
   static final int NOT_DISTINCT = DISTINCT.clear;
   static final int IS_SORTED = SORTED.set;
   static final int NOT_SORTED = SORTED.clear;
   static final int IS_ORDERED = ORDERED.set;
   static final int NOT_ORDERED = ORDERED.clear;
   static final int IS_SIZED = SIZED.set;
   static final int NOT_SIZED = SIZED.clear;
   static final int IS_SHORT_CIRCUIT = SHORT_CIRCUIT.set;

   private static StreamOpFlag.MaskBuilder set(StreamOpFlag.Type var0) {
      return (new StreamOpFlag.MaskBuilder(new EnumMap(StreamOpFlag.Type.class))).set(var0);
   }

   private StreamOpFlag(int var3, StreamOpFlag.MaskBuilder var4) {
      this.maskTable = var4.build();
      var3 *= 2;
      this.bitPosition = var3;
      this.set = 1 << var3;
      this.clear = 2 << var3;
      this.preserve = 3 << var3;
   }

   int set() {
      return this.set;
   }

   int clear() {
      return this.clear;
   }

   boolean isStreamFlag() {
      return (Integer)this.maskTable.get(StreamOpFlag.Type.STREAM) > 0;
   }

   boolean isKnown(int var1) {
      return (var1 & this.preserve) == this.set;
   }

   boolean isCleared(int var1) {
      return (var1 & this.preserve) == this.clear;
   }

   boolean isPreserved(int var1) {
      return (var1 & this.preserve) == this.preserve;
   }

   boolean canSet(StreamOpFlag.Type var1) {
      return ((Integer)this.maskTable.get(var1) & 1) > 0;
   }

   private static int createMask(StreamOpFlag.Type var0) {
      int var1 = 0;
      StreamOpFlag[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         StreamOpFlag var5 = var2[var4];
         var1 |= (Integer)var5.maskTable.get(var0) << var5.bitPosition;
      }

      return var1;
   }

   private static int createFlagMask() {
      int var0 = 0;
      StreamOpFlag[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         StreamOpFlag var4 = var1[var3];
         var0 |= var4.preserve;
      }

      return var0;
   }

   private static int getMask(int var0) {
      return var0 == 0 ? FLAG_MASK : ~(var0 | (FLAG_MASK_IS & var0) << 1 | (FLAG_MASK_NOT & var0) >> 1);
   }

   static int combineOpFlags(int var0, int var1) {
      return var1 & getMask(var0) | var0;
   }

   static int toStreamFlags(int var0) {
      return ~var0 >> 1 & FLAG_MASK_IS & var0;
   }

   static int toCharacteristics(int var0) {
      return var0 & SPLITERATOR_CHARACTERISTICS_MASK;
   }

   static int fromCharacteristics(Spliterator<?> var0) {
      int var1 = var0.characteristics();
      return (var1 & 4) != 0 && var0.getComparator() != null ? var1 & SPLITERATOR_CHARACTERISTICS_MASK & -5 : var1 & SPLITERATOR_CHARACTERISTICS_MASK;
   }

   static int fromCharacteristics(int var0) {
      return var0 & SPLITERATOR_CHARACTERISTICS_MASK;
   }

   private static class MaskBuilder {
      final Map<StreamOpFlag.Type, Integer> map;

      MaskBuilder(Map<StreamOpFlag.Type, Integer> var1) {
         this.map = var1;
      }

      StreamOpFlag.MaskBuilder mask(StreamOpFlag.Type var1, Integer var2) {
         this.map.put(var1, var2);
         return this;
      }

      StreamOpFlag.MaskBuilder set(StreamOpFlag.Type var1) {
         return this.mask(var1, 1);
      }

      StreamOpFlag.MaskBuilder clear(StreamOpFlag.Type var1) {
         return this.mask(var1, 2);
      }

      StreamOpFlag.MaskBuilder setAndClear(StreamOpFlag.Type var1) {
         return this.mask(var1, 3);
      }

      Map<StreamOpFlag.Type, Integer> build() {
         StreamOpFlag.Type[] var1 = StreamOpFlag.Type.values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            StreamOpFlag.Type var4 = var1[var3];
            this.map.putIfAbsent(var4, 0);
         }

         return this.map;
      }
   }

   static enum Type {
      SPLITERATOR,
      STREAM,
      OP,
      TERMINAL_OP,
      UPSTREAM_TERMINAL_OP;
   }
}
