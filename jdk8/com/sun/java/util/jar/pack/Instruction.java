package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.util.Arrays;

class Instruction {
   protected byte[] bytes;
   protected int pc;
   protected int bc;
   protected int w;
   protected int length;
   protected boolean special;
   private static final byte[][] BC_LENGTH = new byte[2][256];
   private static final byte[][] BC_INDEX = new byte[2][256];
   private static final byte[][] BC_TAG = new byte[2][256];
   private static final byte[][] BC_BRANCH = new byte[2][256];
   private static final byte[][] BC_SLOT = new byte[2][256];
   private static final byte[][] BC_CON = new byte[2][256];
   private static final String[] BC_NAME = new String[256];
   private static final String[][] BC_FORMAT = new String[2][202];
   private static int BW;

   protected Instruction(byte[] var1, int var2, int var3, int var4, int var5) {
      this.reset(var1, var2, var3, var4, var5);
   }

   private void reset(byte[] var1, int var2, int var3, int var4, int var5) {
      this.bytes = var1;
      this.pc = var2;
      this.bc = var3;
      this.w = var4;
      this.length = var5;
   }

   public int getBC() {
      return this.bc;
   }

   public boolean isWide() {
      return this.w != 0;
   }

   public byte[] getBytes() {
      return this.bytes;
   }

   public int getPC() {
      return this.pc;
   }

   public int getLength() {
      return this.length;
   }

   public int getNextPC() {
      return this.pc + this.length;
   }

   public Instruction next() {
      int var1 = this.pc + this.length;
      return var1 == this.bytes.length ? null : at(this.bytes, var1, this);
   }

   public boolean isNonstandard() {
      return isNonstandard(this.bc);
   }

   public void setNonstandardLength(int var1) {
      assert this.isNonstandard();

      this.length = var1;
   }

   public Instruction forceNextPC(int var1) {
      int var2 = var1 - this.pc;
      return new Instruction(this.bytes, this.pc, -1, -1, var2);
   }

   public static Instruction at(byte[] var0, int var1) {
      return at(var0, var1, (Instruction)null);
   }

   public static Instruction at(byte[] var0, int var1, Instruction var2) {
      int var3 = getByte(var0, var1);
      boolean var4 = true;
      byte var5 = 0;
      byte var6 = BC_LENGTH[var5][var3];
      if (var6 == 0) {
         switch(var3) {
         case 170:
            return new Instruction.TableSwitch(var0, var1);
         case 171:
            return new Instruction.LookupSwitch(var0, var1);
         case 196:
            var3 = getByte(var0, var1 + 1);
            var5 = 1;
            var6 = BC_LENGTH[var5][var3];
            if (var6 == 0) {
               var6 = 1;
            }
            break;
         default:
            var6 = 1;
         }
      }

      assert var6 > 0;

      assert var1 + var6 <= var0.length;

      if (var2 != null && !var2.special) {
         var2.reset(var0, var1, var3, var5, var6);
         return var2;
      } else {
         return new Instruction(var0, var1, var3, var5, var6);
      }
   }

   public byte getCPTag() {
      return BC_TAG[this.w][this.bc];
   }

   public int getCPIndex() {
      byte var1 = BC_INDEX[this.w][this.bc];
      if (var1 == 0) {
         return -1;
      } else {
         assert this.w == 0;

         return this.length == 2 ? getByte(this.bytes, this.pc + var1) : getShort(this.bytes, this.pc + var1);
      }
   }

   public void setCPIndex(int var1) {
      byte var2 = BC_INDEX[this.w][this.bc];

      assert var2 != 0;

      if (this.length == 2) {
         setByte(this.bytes, this.pc + var2, var1);
      } else {
         setShort(this.bytes, this.pc + var2, var1);
      }

      assert this.getCPIndex() == var1;

   }

   public ConstantPool.Entry getCPRef(ConstantPool.Entry[] var1) {
      int var2 = this.getCPIndex();
      return var2 < 0 ? null : var1[var2];
   }

   public int getLocalSlot() {
      byte var1 = BC_SLOT[this.w][this.bc];
      if (var1 == 0) {
         return -1;
      } else {
         return this.w == 0 ? getByte(this.bytes, this.pc + var1) : getShort(this.bytes, this.pc + var1);
      }
   }

   public int getBranchLabel() {
      byte var1 = BC_BRANCH[this.w][this.bc];
      if (var1 == 0) {
         return -1;
      } else {
         assert this.w == 0;

         assert this.length == 3 || this.length == 5;

         int var2;
         if (this.length == 3) {
            var2 = (short)getShort(this.bytes, this.pc + var1);
         } else {
            var2 = getInt(this.bytes, this.pc + var1);
         }

         assert var2 + this.pc >= 0;

         assert var2 + this.pc <= this.bytes.length;

         return var2 + this.pc;
      }
   }

   public void setBranchLabel(int var1) {
      byte var2 = BC_BRANCH[this.w][this.bc];

      assert var2 != 0;

      if (this.length == 3) {
         setShort(this.bytes, this.pc + var2, var1 - this.pc);
      } else {
         setInt(this.bytes, this.pc + var2, var1 - this.pc);
      }

      assert var1 == this.getBranchLabel();

   }

   public int getConstant() {
      byte var1 = BC_CON[this.w][this.bc];
      if (var1 == 0) {
         return 0;
      } else {
         switch(this.length - var1) {
         case 1:
            return (byte)getByte(this.bytes, this.pc + var1);
         case 2:
            return (short)getShort(this.bytes, this.pc + var1);
         default:
            assert false;

            return 0;
         }
      }
   }

   public void setConstant(int var1) {
      byte var2 = BC_CON[this.w][this.bc];

      assert var2 != 0;

      switch(this.length - var2) {
      case 1:
         setByte(this.bytes, this.pc + var2, var1);
         break;
      case 2:
         setShort(this.bytes, this.pc + var2, var1);
      }

      assert var1 == this.getConstant();

   }

   public boolean equals(Object var1) {
      return var1 != null && var1.getClass() == Instruction.class && this.equals((Instruction)var1);
   }

   public int hashCode() {
      byte var1 = 3;
      int var2 = 11 * var1 + Arrays.hashCode(this.bytes);
      var2 = 11 * var2 + this.pc;
      var2 = 11 * var2 + this.bc;
      var2 = 11 * var2 + this.w;
      var2 = 11 * var2 + this.length;
      return var2;
   }

   public boolean equals(Instruction var1) {
      if (this.pc != var1.pc) {
         return false;
      } else if (this.bc != var1.bc) {
         return false;
      } else if (this.w != var1.w) {
         return false;
      } else if (this.length != var1.length) {
         return false;
      } else {
         for(int var2 = 1; var2 < this.length; ++var2) {
            if (this.bytes[this.pc + var2] != var1.bytes[var1.pc + var2]) {
               return false;
            }
         }

         return true;
      }
   }

   static String labstr(int var0) {
      return var0 >= 0 && var0 < 100000 ? (100000 + var0 + "").substring(1) : var0 + "";
   }

   public String toString() {
      return this.toString((ConstantPool.Entry[])null);
   }

   public String toString(ConstantPool.Entry[] var1) {
      String var2 = labstr(this.pc) + ": ";
      if (this.bc >= 202) {
         var2 = var2 + Integer.toHexString(this.bc);
         return var2;
      } else {
         if (this.w == 1) {
            var2 = var2 + "wide ";
         }

         String var3 = this.bc < BC_NAME.length ? BC_NAME[this.bc] : null;
         if (var3 == null) {
            return var2 + "opcode#" + this.bc;
         } else {
            var2 = var2 + var3;
            byte var4 = this.getCPTag();
            if (var4 != 0) {
               var2 = var2 + " " + ConstantPool.tagName(var4) + ":";
            }

            int var5 = this.getCPIndex();
            if (var5 >= 0) {
               var2 = var2 + (var1 == null ? "" + var5 : "=" + var1[var5].stringValue());
            }

            int var6 = this.getLocalSlot();
            if (var6 >= 0) {
               var2 = var2 + " Local:" + var6;
            }

            int var7 = this.getBranchLabel();
            if (var7 >= 0) {
               var2 = var2 + " To:" + labstr(var7);
            }

            int var8 = this.getConstant();
            if (var8 != 0) {
               var2 = var2 + " Con:" + var8;
            }

            return var2;
         }
      }
   }

   public int getIntAt(int var1) {
      return getInt(this.bytes, this.pc + var1);
   }

   public int getShortAt(int var1) {
      return getShort(this.bytes, this.pc + var1);
   }

   public int getByteAt(int var1) {
      return getByte(this.bytes, this.pc + var1);
   }

   public static int getInt(byte[] var0, int var1) {
      return (getShort(var0, var1 + 0) << 16) + (getShort(var0, var1 + 2) << 0);
   }

   public static int getShort(byte[] var0, int var1) {
      return (getByte(var0, var1 + 0) << 8) + (getByte(var0, var1 + 1) << 0);
   }

   public static int getByte(byte[] var0, int var1) {
      return var0[var1] & 255;
   }

   public static void setInt(byte[] var0, int var1, int var2) {
      setShort(var0, var1 + 0, var2 >> 16);
      setShort(var0, var1 + 2, var2 >> 0);
   }

   public static void setShort(byte[] var0, int var1, int var2) {
      setByte(var0, var1 + 0, var2 >> 8);
      setByte(var0, var1 + 1, var2 >> 0);
   }

   public static void setByte(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)var2;
   }

   public static boolean isNonstandard(int var0) {
      return BC_LENGTH[0][var0] < 0;
   }

   public static int opLength(int var0) {
      byte var1 = BC_LENGTH[0][var0];

      assert var1 > 0;

      return var1;
   }

   public static int opWideLength(int var0) {
      byte var1 = BC_LENGTH[1][var0];

      assert var1 > 0;

      return var1;
   }

   public static boolean isLocalSlotOp(int var0) {
      return var0 < BC_SLOT[0].length && BC_SLOT[0][var0] > 0;
   }

   public static boolean isBranchOp(int var0) {
      return var0 < BC_BRANCH[0].length && BC_BRANCH[0][var0] > 0;
   }

   public static boolean isCPRefOp(int var0) {
      if (var0 < BC_INDEX[0].length && BC_INDEX[0][var0] > 0) {
         return true;
      } else if (var0 >= 233 && var0 < 242) {
         return true;
      } else {
         return var0 == 242 || var0 == 243;
      }
   }

   public static byte getCPRefOpTag(int var0) {
      if (var0 < BC_INDEX[0].length && BC_INDEX[0][var0] > 0) {
         return BC_TAG[0][var0];
      } else if (var0 >= 233 && var0 < 242) {
         return 51;
      } else {
         return (byte)(var0 != 243 && var0 != 242 ? 0 : 11);
      }
   }

   public static boolean isFieldOp(int var0) {
      return var0 >= 178 && var0 <= 181;
   }

   public static boolean isInvokeInitOp(int var0) {
      return var0 >= 230 && var0 < 233;
   }

   public static boolean isSelfLinkerOp(int var0) {
      return var0 >= 202 && var0 < 230;
   }

   public static String byteName(int var0) {
      String var1;
      if (var0 < BC_NAME.length && BC_NAME[var0] != null) {
         var1 = BC_NAME[var0];
      } else {
         int var2;
         if (isSelfLinkerOp(var0)) {
            var2 = var0 - 202;
            boolean var3 = var2 >= 14;
            if (var3) {
               var2 -= 14;
            }

            boolean var4 = var2 >= 7;
            if (var4) {
               var2 -= 7;
            }

            int var5 = 178 + var2;

            assert var5 >= 178 && var5 <= 184;

            var1 = BC_NAME[var5];
            var1 = var1 + (var3 ? "_super" : "_this");
            if (var4) {
               var1 = "aload_0&" + var1;
            }

            var1 = "*" + var1;
         } else if (isInvokeInitOp(var0)) {
            var2 = var0 - 230;
            switch(var2) {
            case 0:
               var1 = "*invokespecial_init_this";
               break;
            case 1:
               var1 = "*invokespecial_init_super";
               break;
            default:
               assert var2 == 2;

               var1 = "*invokespecial_init_new";
            }
         } else {
            switch(var0) {
            case 233:
               var1 = "*cldc";
               break;
            case 234:
               var1 = "*ildc";
               break;
            case 235:
               var1 = "*fldc";
               break;
            case 236:
               var1 = "*cldc_w";
               break;
            case 237:
               var1 = "*ildc_w";
               break;
            case 238:
               var1 = "*fldc_w";
               break;
            case 239:
               var1 = "*dldc2_w";
               break;
            case 240:
               var1 = "*qldc";
               break;
            case 241:
               var1 = "*qldc_w";
               break;
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
            case 249:
            case 250:
            case 251:
            case 252:
            default:
               var1 = "*bc#" + var0;
               break;
            case 253:
               var1 = "*ref_escape";
               break;
            case 254:
               var1 = "*byte_escape";
               break;
            case 255:
               var1 = "*end";
            }
         }
      }

      return var1;
   }

   private static void def(String var0, int var1) {
      def(var0, var1, var1);
   }

   private static void def(String var0, int var1, int var2) {
      String[] var3 = new String[]{var0, null};
      if (var0.indexOf(119) > 0) {
         var3[1] = var0.substring(var0.indexOf(119));
         var3[0] = var0.substring(0, var0.indexOf(119));
      }

      for(int var4 = 0; var4 <= 1; ++var4) {
         var0 = var3[var4];
         if (var0 != null) {
            int var5 = var0.length();
            int var6 = Math.max(0, var0.indexOf(107));
            byte var7 = 0;
            int var8 = Math.max(0, var0.indexOf(111));
            int var9 = Math.max(0, var0.indexOf(108));
            int var10 = Math.max(0, var0.indexOf(120));
            if (var6 > 0 && var6 + 1 < var5) {
               switch(var0.charAt(var6 + 1)) {
               case 'c':
                  var7 = 7;
                  break;
               case 'f':
                  var7 = 9;
                  break;
               case 'i':
                  var7 = 11;
                  break;
               case 'k':
                  var7 = 51;
                  break;
               case 'm':
                  var7 = 10;
                  break;
               case 'y':
                  var7 = 18;
               }

               assert var7 != 0;
            } else if (var6 > 0 && var5 == 2) {
               assert var1 == 18;

               var7 = 51;
            }

            for(int var11 = var1; var11 <= var2; ++var11) {
               BC_FORMAT[var4][var11] = var0;

               assert BC_LENGTH[var4][var11] == -1;

               BC_LENGTH[var4][var11] = (byte)var5;
               BC_INDEX[var4][var11] = (byte)var6;
               BC_TAG[var4][var11] = (byte)var7;

               assert var6 != 0 || var7 == 0;

               BC_BRANCH[var4][var11] = (byte)var8;
               BC_SLOT[var4][var11] = (byte)var9;

               assert var8 == 0 || var9 == 0;

               assert var8 == 0 || var6 == 0;

               assert var9 == 0 || var6 == 0;

               BC_CON[var4][var11] = (byte)var10;
            }
         }
      }

   }

   public static void opcodeChecker(byte[] var0, ConstantPool.Entry[] var1, Package.Version var2) throws Instruction.FormatException {
      for(Instruction var3 = at(var0, 0); var3 != null; var3 = var3.next()) {
         int var4 = var3.getBC();
         if (var4 < 0 || var4 > 201) {
            String var9 = "illegal opcode: " + var4 + " " + var3;
            throw new Instruction.FormatException(var9);
         }

         ConstantPool.Entry var5 = var3.getCPRef(var1);
         if (var5 != null) {
            byte var6 = var3.getCPTag();
            boolean var7 = var5.tagMatches(var6);
            if (!var7 && (var3.bc == 183 || var3.bc == 184) && var5.tagMatches(11) && var2.greaterThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
               var7 = true;
            }

            if (!var7) {
               String var8 = "illegal reference, expected type=" + ConstantPool.tagName(var6) + ": " + var3.toString(var1);
               throw new Instruction.FormatException(var8);
            }
         }
      }

   }

   static {
      int var0;
      for(var0 = 0; var0 < 202; ++var0) {
         BC_LENGTH[0][var0] = -1;
         BC_LENGTH[1][var0] = -1;
      }

      def("b", 0, 15);
      def("bx", 16);
      def("bxx", 17);
      def("bk", 18);
      def("bkk", 19, 20);
      def("blwbll", 21, 25);
      def("b", 26, 53);
      def("blwbll", 54, 58);
      def("b", 59, 131);
      def("blxwbllxx", 132);
      def("b", 133, 152);
      def("boo", 153, 168);
      def("blwbll", 169);
      def("", 170, 171);
      def("b", 172, 177);
      def("bkf", 178, 181);
      def("bkm", 182, 184);
      def("bkixx", 185);
      def("bkyxx", 186);
      def("bkc", 187);
      def("bx", 188);
      def("bkc", 189);
      def("b", 190, 191);
      def("bkc", 192, 193);
      def("b", 194, 195);
      def("", 196);
      def("bkcx", 197);
      def("boo", 198, 199);
      def("boooo", 200, 201);

      for(var0 = 0; var0 < 202; ++var0) {
         if (BC_LENGTH[0][var0] != -1 && BC_LENGTH[1][var0] == -1) {
            BC_LENGTH[1][var0] = (byte)(1 + BC_LENGTH[0][var0]);
         }
      }

      String var3 = "nop aconst_null iconst_m1 iconst_0 iconst_1 iconst_2 iconst_3 iconst_4 iconst_5 lconst_0 lconst_1 fconst_0 fconst_1 fconst_2 dconst_0 dconst_1 bipush sipush ldc ldc_w ldc2_w iload lload fload dload aload iload_0 iload_1 iload_2 iload_3 lload_0 lload_1 lload_2 lload_3 fload_0 fload_1 fload_2 fload_3 dload_0 dload_1 dload_2 dload_3 aload_0 aload_1 aload_2 aload_3 iaload laload faload daload aaload baload caload saload istore lstore fstore dstore astore istore_0 istore_1 istore_2 istore_3 lstore_0 lstore_1 lstore_2 lstore_3 fstore_0 fstore_1 fstore_2 fstore_3 dstore_0 dstore_1 dstore_2 dstore_3 astore_0 astore_1 astore_2 astore_3 iastore lastore fastore dastore aastore bastore castore sastore pop pop2 dup dup_x1 dup_x2 dup2 dup2_x1 dup2_x2 swap iadd ladd fadd dadd isub lsub fsub dsub imul lmul fmul dmul idiv ldiv fdiv ddiv irem lrem frem drem ineg lneg fneg dneg ishl lshl ishr lshr iushr lushr iand land ior lor ixor lxor iinc i2l i2f i2d l2i l2f l2d f2i f2l f2d d2i d2l d2f i2b i2c i2s lcmp fcmpl fcmpg dcmpl dcmpg ifeq ifne iflt ifge ifgt ifle if_icmpeq if_icmpne if_icmplt if_icmpge if_icmpgt if_icmple if_acmpeq if_acmpne goto jsr ret tableswitch lookupswitch ireturn lreturn freturn dreturn areturn return getstatic putstatic getfield putfield invokevirtual invokespecial invokestatic invokeinterface invokedynamic new newarray anewarray arraylength athrow checkcast instanceof monitorenter monitorexit wide multianewarray ifnull ifnonnull goto_w jsr_w ";

      for(int var1 = 0; var3.length() > 0; ++var1) {
         int var2 = var3.indexOf(32);
         BC_NAME[var1] = var3.substring(0, var2);
         var3 = var3.substring(var2 + 1);
      }

      BW = 4;
   }

   static class FormatException extends IOException {
      private static final long serialVersionUID = 3175572275651367015L;

      FormatException(String var1) {
         super(var1);
      }
   }

   public static class LookupSwitch extends Instruction.Switch {
      public int getCaseCount() {
         return this.intAt(1);
      }

      public int getCaseValue(int var1) {
         return this.intAt(2 + var1 * 2 + 0);
      }

      public int getCaseLabel(int var1) {
         return this.intAt(2 + var1 * 2 + 1) + this.pc;
      }

      public void setCaseCount(int var1) {
         this.setIntAt(1, var1);
         this.length = this.getLength(var1);
      }

      public void setCaseValue(int var1, int var2) {
         this.setIntAt(2 + var1 * 2 + 0, var2);
      }

      public void setCaseLabel(int var1, int var2) {
         this.setIntAt(2 + var1 * 2 + 1, var2 - this.pc);
      }

      LookupSwitch(byte[] var1, int var2) {
         super(var1, var2, 171);
      }

      protected int getLength(int var1) {
         return this.apc - this.pc + (2 + var1 * 2) * 4;
      }
   }

   public static class TableSwitch extends Instruction.Switch {
      public int getLowCase() {
         return this.intAt(1);
      }

      public int getHighCase() {
         return this.intAt(2);
      }

      public int getCaseCount() {
         return this.intAt(2) - this.intAt(1) + 1;
      }

      public int getCaseValue(int var1) {
         return this.getLowCase() + var1;
      }

      public int getCaseLabel(int var1) {
         return this.intAt(3 + var1) + this.pc;
      }

      public void setLowCase(int var1) {
         this.setIntAt(1, var1);
      }

      public void setHighCase(int var1) {
         this.setIntAt(2, var1);
      }

      public void setCaseLabel(int var1, int var2) {
         this.setIntAt(3 + var1, var2 - this.pc);
      }

      public void setCaseCount(int var1) {
         this.setHighCase(this.getLowCase() + var1 - 1);
         this.length = this.getLength(var1);
      }

      public void setCaseValue(int var1, int var2) {
         if (var1 != 0) {
            throw new UnsupportedOperationException();
         } else {
            int var3 = this.getCaseCount();
            this.setLowCase(var2);
            this.setCaseCount(var3);
         }
      }

      TableSwitch(byte[] var1, int var2) {
         super(var1, var2, 170);
      }

      protected int getLength(int var1) {
         return this.apc - this.pc + (3 + var1) * 4;
      }
   }

   public abstract static class Switch extends Instruction {
      protected int apc;

      public abstract int getCaseCount();

      public abstract int getCaseValue(int var1);

      public abstract int getCaseLabel(int var1);

      public abstract void setCaseCount(int var1);

      public abstract void setCaseValue(int var1, int var2);

      public abstract void setCaseLabel(int var1, int var2);

      protected abstract int getLength(int var1);

      public int getDefaultLabel() {
         return this.intAt(0) + this.pc;
      }

      public void setDefaultLabel(int var1) {
         this.setIntAt(0, var1 - this.pc);
      }

      protected int intAt(int var1) {
         return getInt(this.bytes, this.apc + var1 * 4);
      }

      protected void setIntAt(int var1, int var2) {
         setInt(this.bytes, this.apc + var1 * 4, var2);
      }

      protected Switch(byte[] var1, int var2, int var3) {
         super(var1, var2, var3, 0, 0);
         this.apc = alignPC(var2 + 1);
         this.special = true;
         this.length = this.getLength(this.getCaseCount());
      }

      public int getAlignedPC() {
         return this.apc;
      }

      public String toString() {
         String var1 = super.toString();
         var1 = var1 + " Default:" + labstr(this.getDefaultLabel());
         int var2 = this.getCaseCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            var1 = var1 + "\n\tCase " + this.getCaseValue(var3) + ":" + labstr(this.getCaseLabel(var3));
         }

         return var1;
      }

      public static int alignPC(int var0) {
         while(var0 % 4 != 0) {
            ++var0;
         }

         return var0;
      }
   }
}
