package sun.reflect;

class ClassFileAssembler implements ClassFileConstants {
   private ByteVector vec;
   private short cpIdx;
   private int stack;
   private int maxStack;
   private int maxLocals;

   public ClassFileAssembler() {
      this(ByteVectorFactory.create());
   }

   public ClassFileAssembler(ByteVector var1) {
      this.cpIdx = 0;
      this.stack = 0;
      this.maxStack = 0;
      this.maxLocals = 0;
      this.vec = var1;
   }

   public ByteVector getData() {
      return this.vec;
   }

   public short getLength() {
      return (short)this.vec.getLength();
   }

   public void emitMagicAndVersion() {
      this.emitInt(-889275714);
      this.emitShort((short)0);
      this.emitShort((short)49);
   }

   public void emitInt(int var1) {
      this.emitByte((byte)(var1 >> 24));
      this.emitByte((byte)(var1 >> 16 & 255));
      this.emitByte((byte)(var1 >> 8 & 255));
      this.emitByte((byte)(var1 & 255));
   }

   public void emitShort(short var1) {
      this.emitByte((byte)(var1 >> 8 & 255));
      this.emitByte((byte)(var1 & 255));
   }

   void emitShort(short var1, short var2) {
      this.vec.put(var1, (byte)(var2 >> 8 & 255));
      this.vec.put(var1 + 1, (byte)(var2 & 255));
   }

   public void emitByte(byte var1) {
      this.vec.add(var1);
   }

   public void append(ClassFileAssembler var1) {
      this.append(var1.vec);
   }

   public void append(ByteVector var1) {
      for(int var2 = 0; var2 < var1.getLength(); ++var2) {
         this.emitByte(var1.get(var2));
      }

   }

   public short cpi() {
      if (this.cpIdx == 0) {
         throw new RuntimeException("Illegal use of ClassFileAssembler");
      } else {
         return this.cpIdx;
      }
   }

   public void emitConstantPoolUTF8(String var1) {
      byte[] var2 = UTF8.encode(var1);
      this.emitByte((byte)1);
      this.emitShort((short)var2.length);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.emitByte(var2[var3]);
      }

      ++this.cpIdx;
   }

   public void emitConstantPoolClass(short var1) {
      this.emitByte((byte)7);
      this.emitShort(var1);
      ++this.cpIdx;
   }

   public void emitConstantPoolNameAndType(short var1, short var2) {
      this.emitByte((byte)12);
      this.emitShort(var1);
      this.emitShort(var2);
      ++this.cpIdx;
   }

   public void emitConstantPoolFieldref(short var1, short var2) {
      this.emitByte((byte)9);
      this.emitShort(var1);
      this.emitShort(var2);
      ++this.cpIdx;
   }

   public void emitConstantPoolMethodref(short var1, short var2) {
      this.emitByte((byte)10);
      this.emitShort(var1);
      this.emitShort(var2);
      ++this.cpIdx;
   }

   public void emitConstantPoolInterfaceMethodref(short var1, short var2) {
      this.emitByte((byte)11);
      this.emitShort(var1);
      this.emitShort(var2);
      ++this.cpIdx;
   }

   public void emitConstantPoolString(short var1) {
      this.emitByte((byte)8);
      this.emitShort(var1);
      ++this.cpIdx;
   }

   private void incStack() {
      this.setStack(this.stack + 1);
   }

   private void decStack() {
      --this.stack;
   }

   public short getMaxStack() {
      return (short)this.maxStack;
   }

   public short getMaxLocals() {
      return (short)this.maxLocals;
   }

   public void setMaxLocals(int var1) {
      this.maxLocals = var1;
   }

   public int getStack() {
      return this.stack;
   }

   public void setStack(int var1) {
      this.stack = var1;
      if (this.stack > this.maxStack) {
         this.maxStack = this.stack;
      }

   }

   public void opc_aconst_null() {
      this.emitByte((byte)1);
      this.incStack();
   }

   public void opc_sipush(short var1) {
      this.emitByte((byte)17);
      this.emitShort(var1);
      this.incStack();
   }

   public void opc_ldc(byte var1) {
      this.emitByte((byte)18);
      this.emitByte(var1);
      this.incStack();
   }

   public void opc_iload_0() {
      this.emitByte((byte)26);
      if (this.maxLocals < 1) {
         this.maxLocals = 1;
      }

      this.incStack();
   }

   public void opc_iload_1() {
      this.emitByte((byte)27);
      if (this.maxLocals < 2) {
         this.maxLocals = 2;
      }

      this.incStack();
   }

   public void opc_iload_2() {
      this.emitByte((byte)28);
      if (this.maxLocals < 3) {
         this.maxLocals = 3;
      }

      this.incStack();
   }

   public void opc_iload_3() {
      this.emitByte((byte)29);
      if (this.maxLocals < 4) {
         this.maxLocals = 4;
      }

      this.incStack();
   }

   public void opc_lload_0() {
      this.emitByte((byte)30);
      if (this.maxLocals < 2) {
         this.maxLocals = 2;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_lload_1() {
      this.emitByte((byte)31);
      if (this.maxLocals < 3) {
         this.maxLocals = 3;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_lload_2() {
      this.emitByte((byte)32);
      if (this.maxLocals < 4) {
         this.maxLocals = 4;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_lload_3() {
      this.emitByte((byte)33);
      if (this.maxLocals < 5) {
         this.maxLocals = 5;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_fload_0() {
      this.emitByte((byte)34);
      if (this.maxLocals < 1) {
         this.maxLocals = 1;
      }

      this.incStack();
   }

   public void opc_fload_1() {
      this.emitByte((byte)35);
      if (this.maxLocals < 2) {
         this.maxLocals = 2;
      }

      this.incStack();
   }

   public void opc_fload_2() {
      this.emitByte((byte)36);
      if (this.maxLocals < 3) {
         this.maxLocals = 3;
      }

      this.incStack();
   }

   public void opc_fload_3() {
      this.emitByte((byte)37);
      if (this.maxLocals < 4) {
         this.maxLocals = 4;
      }

      this.incStack();
   }

   public void opc_dload_0() {
      this.emitByte((byte)38);
      if (this.maxLocals < 2) {
         this.maxLocals = 2;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_dload_1() {
      this.emitByte((byte)39);
      if (this.maxLocals < 3) {
         this.maxLocals = 3;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_dload_2() {
      this.emitByte((byte)40);
      if (this.maxLocals < 4) {
         this.maxLocals = 4;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_dload_3() {
      this.emitByte((byte)41);
      if (this.maxLocals < 5) {
         this.maxLocals = 5;
      }

      this.incStack();
      this.incStack();
   }

   public void opc_aload_0() {
      this.emitByte((byte)42);
      if (this.maxLocals < 1) {
         this.maxLocals = 1;
      }

      this.incStack();
   }

   public void opc_aload_1() {
      this.emitByte((byte)43);
      if (this.maxLocals < 2) {
         this.maxLocals = 2;
      }

      this.incStack();
   }

   public void opc_aload_2() {
      this.emitByte((byte)44);
      if (this.maxLocals < 3) {
         this.maxLocals = 3;
      }

      this.incStack();
   }

   public void opc_aload_3() {
      this.emitByte((byte)45);
      if (this.maxLocals < 4) {
         this.maxLocals = 4;
      }

      this.incStack();
   }

   public void opc_aaload() {
      this.emitByte((byte)50);
      this.decStack();
   }

   public void opc_astore_0() {
      this.emitByte((byte)75);
      if (this.maxLocals < 1) {
         this.maxLocals = 1;
      }

      this.decStack();
   }

   public void opc_astore_1() {
      this.emitByte((byte)76);
      if (this.maxLocals < 2) {
         this.maxLocals = 2;
      }

      this.decStack();
   }

   public void opc_astore_2() {
      this.emitByte((byte)77);
      if (this.maxLocals < 3) {
         this.maxLocals = 3;
      }

      this.decStack();
   }

   public void opc_astore_3() {
      this.emitByte((byte)78);
      if (this.maxLocals < 4) {
         this.maxLocals = 4;
      }

      this.decStack();
   }

   public void opc_pop() {
      this.emitByte((byte)87);
      this.decStack();
   }

   public void opc_dup() {
      this.emitByte((byte)89);
      this.incStack();
   }

   public void opc_dup_x1() {
      this.emitByte((byte)90);
      this.incStack();
   }

   public void opc_swap() {
      this.emitByte((byte)95);
   }

   public void opc_i2l() {
      this.emitByte((byte)-123);
   }

   public void opc_i2f() {
      this.emitByte((byte)-122);
   }

   public void opc_i2d() {
      this.emitByte((byte)-121);
   }

   public void opc_l2f() {
      this.emitByte((byte)-119);
   }

   public void opc_l2d() {
      this.emitByte((byte)-118);
   }

   public void opc_f2d() {
      this.emitByte((byte)-115);
   }

   public void opc_ifeq(short var1) {
      this.emitByte((byte)-103);
      this.emitShort(var1);
      this.decStack();
   }

   public void opc_ifeq(Label var1) {
      short var2 = this.getLength();
      this.emitByte((byte)-103);
      var1.add(this, var2, this.getLength(), this.getStack() - 1);
      this.emitShort((short)-1);
   }

   public void opc_if_icmpeq(short var1) {
      this.emitByte((byte)-97);
      this.emitShort(var1);
      this.setStack(this.getStack() - 2);
   }

   public void opc_if_icmpeq(Label var1) {
      short var2 = this.getLength();
      this.emitByte((byte)-97);
      var1.add(this, var2, this.getLength(), this.getStack() - 2);
      this.emitShort((short)-1);
   }

   public void opc_goto(short var1) {
      this.emitByte((byte)-89);
      this.emitShort(var1);
   }

   public void opc_goto(Label var1) {
      short var2 = this.getLength();
      this.emitByte((byte)-89);
      var1.add(this, var2, this.getLength(), this.getStack());
      this.emitShort((short)-1);
   }

   public void opc_ifnull(short var1) {
      this.emitByte((byte)-58);
      this.emitShort(var1);
      this.decStack();
   }

   public void opc_ifnull(Label var1) {
      short var2 = this.getLength();
      this.emitByte((byte)-58);
      var1.add(this, var2, this.getLength(), this.getStack() - 1);
      this.emitShort((short)-1);
      this.decStack();
   }

   public void opc_ifnonnull(short var1) {
      this.emitByte((byte)-57);
      this.emitShort(var1);
      this.decStack();
   }

   public void opc_ifnonnull(Label var1) {
      short var2 = this.getLength();
      this.emitByte((byte)-57);
      var1.add(this, var2, this.getLength(), this.getStack() - 1);
      this.emitShort((short)-1);
      this.decStack();
   }

   public void opc_ireturn() {
      this.emitByte((byte)-84);
      this.setStack(0);
   }

   public void opc_lreturn() {
      this.emitByte((byte)-83);
      this.setStack(0);
   }

   public void opc_freturn() {
      this.emitByte((byte)-82);
      this.setStack(0);
   }

   public void opc_dreturn() {
      this.emitByte((byte)-81);
      this.setStack(0);
   }

   public void opc_areturn() {
      this.emitByte((byte)-80);
      this.setStack(0);
   }

   public void opc_return() {
      this.emitByte((byte)-79);
      this.setStack(0);
   }

   public void opc_getstatic(short var1, int var2) {
      this.emitByte((byte)-78);
      this.emitShort(var1);
      this.setStack(this.getStack() + var2);
   }

   public void opc_putstatic(short var1, int var2) {
      this.emitByte((byte)-77);
      this.emitShort(var1);
      this.setStack(this.getStack() - var2);
   }

   public void opc_getfield(short var1, int var2) {
      this.emitByte((byte)-76);
      this.emitShort(var1);
      this.setStack(this.getStack() + var2 - 1);
   }

   public void opc_putfield(short var1, int var2) {
      this.emitByte((byte)-75);
      this.emitShort(var1);
      this.setStack(this.getStack() - var2 - 1);
   }

   public void opc_invokevirtual(short var1, int var2, int var3) {
      this.emitByte((byte)-74);
      this.emitShort(var1);
      this.setStack(this.getStack() - var2 - 1 + var3);
   }

   public void opc_invokespecial(short var1, int var2, int var3) {
      this.emitByte((byte)-73);
      this.emitShort(var1);
      this.setStack(this.getStack() - var2 - 1 + var3);
   }

   public void opc_invokestatic(short var1, int var2, int var3) {
      this.emitByte((byte)-72);
      this.emitShort(var1);
      this.setStack(this.getStack() - var2 + var3);
   }

   public void opc_invokeinterface(short var1, int var2, byte var3, int var4) {
      this.emitByte((byte)-71);
      this.emitShort(var1);
      this.emitByte(var3);
      this.emitByte((byte)0);
      this.setStack(this.getStack() - var2 - 1 + var4);
   }

   public void opc_arraylength() {
      this.emitByte((byte)-66);
   }

   public void opc_new(short var1) {
      this.emitByte((byte)-69);
      this.emitShort(var1);
      this.incStack();
   }

   public void opc_athrow() {
      this.emitByte((byte)-65);
      this.setStack(1);
   }

   public void opc_checkcast(short var1) {
      this.emitByte((byte)-64);
      this.emitShort(var1);
   }

   public void opc_instanceof(short var1) {
      this.emitByte((byte)-63);
      this.emitShort(var1);
   }
}
