package com.sun.java.util.jar.pack;

import java.util.Arrays;
import java.util.Collection;

class Code extends Attribute.Holder {
   Package.Class.Method m;
   private static final ConstantPool.Entry[] noRefs;
   int max_stack;
   int max_locals;
   ConstantPool.Entry[] handler_class;
   int[] handler_start;
   int[] handler_end;
   int[] handler_catch;
   byte[] bytes;
   Fixups fixups;
   Object insnMap;
   static final boolean shrinkMaps = true;

   public Code(Package.Class.Method var1) {
      this.handler_class = noRefs;
      this.handler_start = Constants.noInts;
      this.handler_end = Constants.noInts;
      this.handler_catch = Constants.noInts;
      this.m = var1;
   }

   public Package.Class.Method getMethod() {
      return this.m;
   }

   public Package.Class thisClass() {
      return this.m.thisClass();
   }

   public Package getPackage() {
      return this.m.thisClass().getPackage();
   }

   public ConstantPool.Entry[] getCPMap() {
      return this.m.getCPMap();
   }

   int getLength() {
      return this.bytes.length;
   }

   int getMaxStack() {
      return this.max_stack;
   }

   void setMaxStack(int var1) {
      this.max_stack = var1;
   }

   int getMaxNALocals() {
      int var1 = this.m.getArgumentSize();
      return this.max_locals - var1;
   }

   void setMaxNALocals(int var1) {
      int var2 = this.m.getArgumentSize();
      this.max_locals = var2 + var1;
   }

   int getHandlerCount() {
      assert this.handler_class.length == this.handler_start.length;

      assert this.handler_class.length == this.handler_end.length;

      assert this.handler_class.length == this.handler_catch.length;

      return this.handler_class.length;
   }

   void setHandlerCount(int var1) {
      if (var1 > 0) {
         this.handler_class = new ConstantPool.Entry[var1];
         this.handler_start = new int[var1];
         this.handler_end = new int[var1];
         this.handler_catch = new int[var1];
      }

   }

   void setBytes(byte[] var1) {
      this.bytes = var1;
      if (this.fixups != null) {
         this.fixups.setBytes(var1);
      }

   }

   void setInstructionMap(int[] var1, int var2) {
      this.insnMap = this.allocateInstructionMap(var1, var2);
   }

   void setInstructionMap(int[] var1) {
      this.setInstructionMap(var1, var1.length);
   }

   int[] getInstructionMap() {
      return this.expandInstructionMap(this.getInsnMap());
   }

   void addFixups(Collection<Fixups.Fixup> var1) {
      if (this.fixups == null) {
         this.fixups = new Fixups(this.bytes);
      }

      assert this.fixups.getBytes() == this.bytes;

      this.fixups.addAll(var1);
   }

   public void trimToSize() {
      if (this.fixups != null) {
         this.fixups.trimToSize();
         if (this.fixups.size() == 0) {
            this.fixups = null;
         }
      }

      super.trimToSize();
   }

   protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
      int var3 = this.getPackage().verbose;
      if (var3 > 2) {
         System.out.println("Reference scan " + this);
      }

      var2.addAll(Arrays.asList(this.handler_class));
      if (this.fixups != null) {
         this.fixups.visitRefs(var2);
      } else {
         ConstantPool.Entry[] var4 = this.getCPMap();

         for(Instruction var5 = this.instructionAt(0); var5 != null; var5 = var5.next()) {
            if (var3 > 4) {
               System.out.println((Object)var5);
            }

            int var6 = var5.getCPIndex();
            if (var6 >= 0) {
               var2.add(var4[var6]);
            }
         }
      }

      super.visitRefs(var1, var2);
   }

   private Object allocateInstructionMap(int[] var1, int var2) {
      int var3 = this.getLength();
      int var5;
      if (var3 <= 255) {
         byte[] var7 = new byte[var2 + 1];

         for(var5 = 0; var5 < var2; ++var5) {
            var7[var5] = (byte)(var1[var5] + -128);
         }

         var7[var2] = (byte)(var3 + -128);
         return var7;
      } else if (var3 >= 65535) {
         int[] var6 = Arrays.copyOf(var1, var2 + 1);
         var6[var2] = var3;
         return var6;
      } else {
         short[] var4 = new short[var2 + 1];

         for(var5 = 0; var5 < var2; ++var5) {
            var4[var5] = (short)(var1[var5] + -32768);
         }

         var4[var2] = (short)(var3 + -32768);
         return var4;
      }
   }

   private int[] expandInstructionMap(Object var1) {
      int[] var2;
      int var4;
      if (var1 instanceof byte[]) {
         byte[] var3 = (byte[])((byte[])var1);
         var2 = new int[var3.length - 1];

         for(var4 = 0; var4 < var2.length; ++var4) {
            var2[var4] = var3[var4] - -128;
         }
      } else if (var1 instanceof short[]) {
         short[] var5 = (short[])((short[])var1);
         var2 = new int[var5.length - 1];

         for(var4 = 0; var4 < var2.length; ++var4) {
            var2[var4] = var5[var4] - -128;
         }
      } else {
         int[] var6 = (int[])((int[])var1);
         var2 = Arrays.copyOfRange((int[])var6, 0, var6.length - 1);
      }

      return var2;
   }

   Object getInsnMap() {
      if (this.insnMap != null) {
         return this.insnMap;
      } else {
         int[] var1 = new int[this.getLength()];
         int var2 = 0;

         for(Instruction var3 = this.instructionAt(0); var3 != null; var3 = var3.next()) {
            var1[var2++] = var3.getPC();
         }

         this.insnMap = this.allocateInstructionMap(var1, var2);
         return this.insnMap;
      }
   }

   public int encodeBCI(int var1) {
      if (var1 > 0 && var1 <= this.getLength()) {
         Object var2 = this.getInsnMap();
         int var3;
         int var4;
         if (var2 instanceof byte[]) {
            byte[] var5 = (byte[])((byte[])var2);
            var4 = var5.length;
            var3 = Arrays.binarySearch(var5, (byte)(var1 + -128));
         } else if (var2 instanceof short[]) {
            short[] var6 = (short[])((short[])var2);
            var4 = var6.length;
            var3 = Arrays.binarySearch(var6, (short)(var1 + -32768));
         } else {
            int[] var7 = (int[])((int[])var2);
            var4 = var7.length;
            var3 = Arrays.binarySearch(var7, var1);
         }

         assert var3 != -1;

         assert var3 != 0;

         assert var3 != var4;

         assert var3 != -var4 - 1;

         return var3 >= 0 ? var3 : var4 + var1 - (-var3 - 1);
      } else {
         return var1;
      }
   }

   public int decodeBCI(int var1) {
      if (var1 > 0 && var1 <= this.getLength()) {
         Object var2 = this.getInsnMap();
         int var3;
         int var4;
         int var6;
         if (var2 instanceof byte[]) {
            byte[] var5 = (byte[])((byte[])var2);
            var4 = var5.length;
            if (var1 < var4) {
               return var5[var1] - -128;
            }

            var3 = Arrays.binarySearch(var5, (byte)(var1 + -128));
            if (var3 < 0) {
               var3 = -var3 - 1;
            }

            for(var6 = var1 - var4 + -128; var5[var3 - 1] - (var3 - 1) > var6; --var3) {
            }
         } else if (var2 instanceof short[]) {
            short[] var7 = (short[])((short[])var2);
            var4 = var7.length;
            if (var1 < var4) {
               return var7[var1] - -32768;
            }

            var3 = Arrays.binarySearch(var7, (short)(var1 + -32768));
            if (var3 < 0) {
               var3 = -var3 - 1;
            }

            for(var6 = var1 - var4 + -32768; var7[var3 - 1] - (var3 - 1) > var6; --var3) {
            }
         } else {
            int[] var8 = (int[])((int[])var2);
            var4 = var8.length;
            if (var1 < var4) {
               return var8[var1];
            }

            var3 = Arrays.binarySearch(var8, var1);
            if (var3 < 0) {
               var3 = -var3 - 1;
            }

            for(var6 = var1 - var4; var8[var3 - 1] - (var3 - 1) > var6; --var3) {
            }
         }

         return var1 - var4 + var3;
      } else {
         return var1;
      }
   }

   public void finishRefs(ConstantPool.Index var1) {
      if (this.fixups != null) {
         this.fixups.finishRefs(var1);
         this.fixups = null;
      }

   }

   Instruction instructionAt(int var1) {
      return Instruction.at(this.bytes, var1);
   }

   static boolean flagsRequireCode(int var0) {
      return (var0 & 1280) == 0;
   }

   public String toString() {
      return this.m + ".Code";
   }

   public int getInt(int var1) {
      return Instruction.getInt(this.bytes, var1);
   }

   public int getShort(int var1) {
      return Instruction.getShort(this.bytes, var1);
   }

   public int getByte(int var1) {
      return Instruction.getByte(this.bytes, var1);
   }

   void setInt(int var1, int var2) {
      Instruction.setInt(this.bytes, var1, var2);
   }

   void setShort(int var1, int var2) {
      Instruction.setShort(this.bytes, var1, var2);
   }

   void setByte(int var1, int var2) {
      Instruction.setByte(this.bytes, var1, var2);
   }

   static {
      noRefs = ConstantPool.noRefs;
   }
}
