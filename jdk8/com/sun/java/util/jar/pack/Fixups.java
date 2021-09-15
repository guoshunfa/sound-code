package com.sun.java.util.jar.pack;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

final class Fixups extends AbstractCollection<Fixups.Fixup> {
   byte[] bytes;
   int head;
   int tail;
   int size;
   ConstantPool.Entry[] entries;
   int[] bigDescs;
   private static final int MINBIGSIZE = 1;
   private static final int[] noBigDescs = new int[]{1};
   private static final int LOC_SHIFT = 1;
   private static final int FMT_MASK = 1;
   private static final byte UNUSED_BYTE = 0;
   private static final byte OVERFLOW_BYTE = -1;
   private static final int BIGSIZE = 0;
   private static final int U2_FORMAT = 0;
   private static final int U1_FORMAT = 1;
   private static final int SPECIAL_LOC = 0;
   private static final int SPECIAL_FMT = 0;

   Fixups(byte[] var1) {
      this.bytes = var1;
      this.entries = new ConstantPool.Entry[3];
      this.bigDescs = noBigDescs;
   }

   Fixups() {
      this((byte[])null);
   }

   Fixups(byte[] var1, Collection<Fixups.Fixup> var2) {
      this(var1);
      this.addAll(var2);
   }

   Fixups(Collection<Fixups.Fixup> var1) {
      this((byte[])null);
      this.addAll(var1);
   }

   public int size() {
      return this.size;
   }

   public void trimToSize() {
      if (this.size != this.entries.length) {
         ConstantPool.Entry[] var1 = this.entries;
         this.entries = new ConstantPool.Entry[this.size];
         System.arraycopy(var1, 0, this.entries, 0, this.size);
      }

      int var3 = this.bigDescs[0];
      if (var3 == 1) {
         this.bigDescs = noBigDescs;
      } else if (var3 != this.bigDescs.length) {
         int[] var2 = this.bigDescs;
         this.bigDescs = new int[var3];
         System.arraycopy(var2, 0, this.bigDescs, 0, var3);
      }

   }

   public void visitRefs(Collection<ConstantPool.Entry> var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.add(this.entries[var2]);
      }

   }

   public void clear() {
      if (this.bytes != null) {
         Iterator var1 = this.iterator();

         while(var1.hasNext()) {
            Fixups.Fixup var2 = (Fixups.Fixup)var1.next();
            this.storeIndex(var2.location(), var2.format(), 0);
         }
      }

      this.size = 0;
      if (this.bigDescs != noBigDescs) {
         this.bigDescs[0] = 1;
      }

   }

   public byte[] getBytes() {
      return this.bytes;
   }

   public void setBytes(byte[] var1) {
      if (this.bytes != var1) {
         ArrayList var2 = null;

         assert (var2 = new ArrayList(this)) != null;

         if (this.bytes != null && var1 != null) {
            this.bytes = var1;
         } else {
            ArrayList var3 = new ArrayList(this);
            this.clear();
            this.bytes = var1;
            this.addAll(var3);
         }

         assert var2.equals(new ArrayList(this));

      }
   }

   static int fmtLen(int var0) {
      return 1 + (var0 - 1) / -1;
   }

   static int descLoc(int var0) {
      return var0 >>> 1;
   }

   static int descFmt(int var0) {
      return var0 & 1;
   }

   static int descEnd(int var0) {
      return descLoc(var0) + fmtLen(descFmt(var0));
   }

   static int makeDesc(int var0, int var1) {
      int var2 = var0 << 1 | var1;

      assert descLoc(var2) == var0;

      assert descFmt(var2) == var1;

      return var2;
   }

   int fetchDesc(int var1, int var2) {
      byte var3 = this.bytes[var1];

      assert var3 != -1;

      int var4;
      if (var2 == 0) {
         byte var5 = this.bytes[var1 + 1];
         var4 = ((var3 & 255) << 8) + (var5 & 255);
      } else {
         var4 = var3 & 255;
      }

      return var4 + (var1 << 1);
   }

   boolean storeDesc(int var1, int var2, int var3) {
      if (this.bytes == null) {
         return false;
      } else {
         int var4 = var3 - (var1 << 1);
         byte var5;
         switch(var2) {
         case 0:
            assert this.bytes[var1 + 0] == 0;

            assert this.bytes[var1 + 1] == 0;

            var5 = (byte)(var4 >> 8);
            byte var6 = (byte)(var4 >> 0);
            if (var4 == (var4 & '\uffff') && var5 != -1) {
               this.bytes[var1 + 0] = var5;
               this.bytes[var1 + 1] = var6;

               assert this.fetchDesc(var1, var2) == var3;

               return true;
            }
            break;
         case 1:
            assert this.bytes[var1] == 0;

            var5 = (byte)var4;
            if (var4 == (var4 & 255) && var5 != -1) {
               this.bytes[var1] = var5;

               assert this.fetchDesc(var1, var2) == var3;

               return true;
            }
            break;
         default:
            assert false;
         }

         this.bytes[var1] = -1;

         assert var2 == 1 || (this.bytes[var1 + 1] = (byte)this.bigDescs[0]) != 999;

         return false;
      }
   }

   void storeIndex(int var1, int var2, int var3) {
      storeIndex(this.bytes, var1, var2, var3);
   }

   static void storeIndex(byte[] var0, int var1, int var2, int var3) {
      switch(var2) {
      case 0:
         assert var3 == (var3 & '\uffff') : var3;

         var0[var1 + 0] = (byte)(var3 >> 8);
         var0[var1 + 1] = (byte)(var3 >> 0);
         break;
      case 1:
         assert var3 == (var3 & 255) : var3;

         var0[var1] = (byte)var3;
         break;
      default:
         assert false;
      }

   }

   void addU1(int var1, ConstantPool.Entry var2) {
      this.add(var1, 1, var2);
   }

   void addU2(int var1, ConstantPool.Entry var2) {
      this.add(var1, 0, var2);
   }

   public Iterator<Fixups.Fixup> iterator() {
      return new Fixups.Itr();
   }

   public void add(int var1, int var2, ConstantPool.Entry var3) {
      this.addDesc(makeDesc(var1, var2), var3);
   }

   public boolean add(Fixups.Fixup var1) {
      this.addDesc(var1.desc, var1.entry);
      return true;
   }

   public boolean addAll(Collection<? extends Fixups.Fixup> var1) {
      if (!(var1 instanceof Fixups)) {
         return super.addAll(var1);
      } else {
         Fixups var2 = (Fixups)var1;
         if (var2.size == 0) {
            return false;
         } else {
            if (this.size == 0 && this.entries.length < var2.size) {
               this.growEntries(var2.size);
            }

            ConstantPool.Entry[] var3 = var2.entries;
            Fixups.Itr var4 = var2.new Itr();

            while(var4.hasNext()) {
               int var5 = var4.index;
               this.addDesc(var4.nextDesc(), var3[var5]);
            }

            return true;
         }
      }
   }

   private void addDesc(int var1, ConstantPool.Entry var2) {
      if (this.entries.length == this.size) {
         this.growEntries(this.size * 2);
      }

      this.entries[this.size] = var2;
      if (this.size == 0) {
         this.head = this.tail = var1;
      } else {
         int var3 = this.tail;
         int var4 = descLoc(var3);
         int var5 = descFmt(var3);
         int var6 = fmtLen(var5);
         int var7 = descLoc(var1);
         if (var7 < var4 + var6) {
            this.badOverlap(var7);
         }

         this.tail = var1;
         if (!this.storeDesc(var4, var5, var1)) {
            int var8 = this.bigDescs[0];
            if (this.bigDescs.length == var8) {
               this.growBigDescs();
            }

            this.bigDescs[var8++] = var1;
            this.bigDescs[0] = var8;
         }
      }

      ++this.size;
   }

   private void badOverlap(int var1) {
      throw new IllegalArgumentException("locs must be ascending and must not overlap:  " + var1 + " >> " + this);
   }

   private void growEntries(int var1) {
      ConstantPool.Entry[] var2 = this.entries;
      this.entries = new ConstantPool.Entry[Math.max(3, var1)];
      System.arraycopy(var2, 0, this.entries, 0, var2.length);
   }

   private void growBigDescs() {
      int[] var1 = this.bigDescs;
      this.bigDescs = new int[var1.length * 2];
      System.arraycopy(var1, 0, this.bigDescs, 0, var1.length);
   }

   static Object addRefWithBytes(Object var0, byte[] var1, ConstantPool.Entry var2) {
      return add(var0, var1, 0, 0, var2);
   }

   static Object addRefWithLoc(Object var0, int var1, ConstantPool.Entry var2) {
      return add(var0, (byte[])null, var1, 0, var2);
   }

   private static Object add(Object var0, byte[] var1, int var2, int var3, ConstantPool.Entry var4) {
      Fixups var5;
      if (var0 == null) {
         if (var2 == 0 && var3 == 0) {
            return var4;
         }

         var5 = new Fixups(var1);
      } else if (!(var0 instanceof Fixups)) {
         ConstantPool.Entry var6 = (ConstantPool.Entry)var0;
         var5 = new Fixups(var1);
         var5.add(0, 0, var6);
      } else {
         var5 = (Fixups)var0;

         assert var5.bytes == var1;
      }

      var5.add(var2, var3, var4);
      return var5;
   }

   public static void setBytes(Object var0, byte[] var1) {
      if (var0 instanceof Fixups) {
         Fixups var2 = (Fixups)var0;
         var2.setBytes(var1);
      }

   }

   public static Object trimToSize(Object var0) {
      if (var0 instanceof Fixups) {
         Fixups var1 = (Fixups)var0;
         var1.trimToSize();
         if (var1.size() == 0) {
            var0 = null;
         }
      }

      return var0;
   }

   public static void visitRefs(Object var0, Collection<ConstantPool.Entry> var1) {
      if (var0 != null) {
         if (!(var0 instanceof Fixups)) {
            var1.add((ConstantPool.Entry)var0);
         } else {
            Fixups var2 = (Fixups)var0;
            var2.visitRefs(var1);
         }
      }

   }

   public static void finishRefs(Object var0, byte[] var1, ConstantPool.Index var2) {
      if (var0 != null) {
         if (!(var0 instanceof Fixups)) {
            int var4 = var2.indexOf((ConstantPool.Entry)var0);
            storeIndex(var1, 0, 0, var4);
         } else {
            Fixups var3 = (Fixups)var0;

            assert var3.bytes == var1;

            var3.finishRefs(var2);
         }
      }
   }

   void finishRefs(ConstantPool.Index var1) {
      if (!this.isEmpty()) {
         Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            Fixups.Fixup var3 = (Fixups.Fixup)var2.next();
            int var4 = var1.indexOf(var3.entry);
            this.storeIndex(var3.location(), var3.format(), var4);
         }

         this.bytes = null;
         this.clear();
      }
   }

   private class Itr implements Iterator<Fixups.Fixup> {
      int index;
      int bigIndex;
      int next;

      private Itr() {
         this.index = 0;
         this.bigIndex = 1;
         this.next = Fixups.this.head;
      }

      public boolean hasNext() {
         return this.index < Fixups.this.size;
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public Fixups.Fixup next() {
         int var1 = this.index;
         return new Fixups.Fixup(this.nextDesc(), Fixups.this.entries[var1]);
      }

      int nextDesc() {
         ++this.index;
         int var1 = this.next;
         if (this.index < Fixups.this.size) {
            int var2 = Fixups.descLoc(var1);
            int var3 = Fixups.descFmt(var1);
            if (Fixups.this.bytes != null && Fixups.this.bytes[var2] != -1) {
               this.next = Fixups.this.fetchDesc(var2, var3);
            } else {
               assert var3 == 1 || Fixups.this.bytes == null || Fixups.this.bytes[var2 + 1] == (byte)this.bigIndex;

               this.next = Fixups.this.bigDescs[this.bigIndex++];
            }
         }

         return var1;
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }

   public static class Fixup implements Comparable<Fixups.Fixup> {
      int desc;
      ConstantPool.Entry entry;

      Fixup(int var1, ConstantPool.Entry var2) {
         this.desc = var1;
         this.entry = var2;
      }

      public Fixup(int var1, int var2, ConstantPool.Entry var3) {
         this.desc = Fixups.makeDesc(var1, var2);
         this.entry = var3;
      }

      public int location() {
         return Fixups.descLoc(this.desc);
      }

      public int format() {
         return Fixups.descFmt(this.desc);
      }

      public ConstantPool.Entry entry() {
         return this.entry;
      }

      public int compareTo(Fixups.Fixup var1) {
         return this.location() - var1.location();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Fixups.Fixup)) {
            return false;
         } else {
            Fixups.Fixup var2 = (Fixups.Fixup)var1;
            return this.desc == var2.desc && this.entry == var2.entry;
         }
      }

      public int hashCode() {
         byte var1 = 7;
         int var2 = 59 * var1 + this.desc;
         var2 = 59 * var2 + Objects.hashCode(this.entry);
         return var2;
      }

      public String toString() {
         return "@" + this.location() + (this.format() == 1 ? ".1" : "") + "=" + this.entry;
      }
   }
}
