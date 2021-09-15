package com.sun.java.util.jar.pack;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

class ClassReader {
   int verbose;
   Package pkg;
   Package.Class cls;
   long inPos;
   long constantPoolLimit = -1L;
   DataInputStream in;
   Map<Attribute.Layout, Attribute> attrDefs;
   Map<Attribute.Layout, String> attrCommands;
   String unknownAttrCommand = "error";
   boolean haveUnresolvedEntry;

   ClassReader(Package.Class var1, InputStream var2) throws IOException {
      this.pkg = var1.getPackage();
      this.cls = var1;
      this.verbose = this.pkg.verbose;
      this.in = new DataInputStream(new FilterInputStream(var2) {
         public int read(byte[] var1, int var2, int var3) throws IOException {
            int var4 = super.read(var1, var2, var3);
            if (var4 >= 0) {
               ClassReader var10000 = ClassReader.this;
               var10000.inPos += (long)var4;
            }

            return var4;
         }

         public int read() throws IOException {
            int var1 = super.read();
            if (var1 >= 0) {
               ++ClassReader.this.inPos;
            }

            return var1;
         }

         public long skip(long var1) throws IOException {
            long var3 = super.skip(var1);
            if (var3 >= 0L) {
               ClassReader var10000 = ClassReader.this;
               var10000.inPos += var3;
            }

            return var3;
         }
      });
   }

   public void setAttrDefs(Map<Attribute.Layout, Attribute> var1) {
      this.attrDefs = var1;
   }

   public void setAttrCommands(Map<Attribute.Layout, String> var1) {
      this.attrCommands = var1;
   }

   private void skip(int var1, String var2) throws IOException {
      Utils.log.warning("skipping " + var1 + " bytes of " + var2);

      long var3;
      long var5;
      for(var3 = 0L; var3 < (long)var1; var3 += var5) {
         var5 = this.in.skip((long)var1 - var3);

         assert var5 > 0L;
      }

      assert var3 == (long)var1;

   }

   private int readUnsignedShort() throws IOException {
      return this.in.readUnsignedShort();
   }

   private int readInt() throws IOException {
      return this.in.readInt();
   }

   private ConstantPool.Entry readRef() throws IOException {
      int var1 = this.in.readUnsignedShort();
      return var1 == 0 ? null : this.cls.cpMap[var1];
   }

   private ConstantPool.Entry readRef(byte var1) throws IOException {
      ConstantPool.Entry var2 = this.readRef();

      assert !(var2 instanceof ClassReader.UnresolvedEntry);

      this.checkTag(var2, var1);
      return var2;
   }

   private ConstantPool.Entry checkTag(ConstantPool.Entry var1, byte var2) throws ClassReader.ClassFormatException {
      if (var1 != null && var1.tagMatches(var2)) {
         return var1;
      } else {
         String var3 = this.inPos == this.constantPoolLimit ? " in constant pool" : " at pos: " + this.inPos;
         String var4 = var1 == null ? "null CP index" : "type=" + ConstantPool.tagName(var1.tag);
         throw new ClassReader.ClassFormatException("Bad constant, expected type=" + ConstantPool.tagName(var2) + " got " + var4 + ", in File: " + this.cls.file.nameString + var3);
      }
   }

   private ConstantPool.Entry checkTag(ConstantPool.Entry var1, byte var2, boolean var3) throws ClassReader.ClassFormatException {
      return var3 && var1 == null ? null : this.checkTag(var1, var2);
   }

   private ConstantPool.Entry readRefOrNull(byte var1) throws IOException {
      ConstantPool.Entry var2 = this.readRef();
      this.checkTag(var2, var1, true);
      return var2;
   }

   private ConstantPool.Utf8Entry readUtf8Ref() throws IOException {
      return (ConstantPool.Utf8Entry)this.readRef((byte)1);
   }

   private ConstantPool.ClassEntry readClassRef() throws IOException {
      return (ConstantPool.ClassEntry)this.readRef((byte)7);
   }

   private ConstantPool.ClassEntry readClassRefOrNull() throws IOException {
      return (ConstantPool.ClassEntry)this.readRefOrNull((byte)7);
   }

   private ConstantPool.SignatureEntry readSignatureRef() throws IOException {
      ConstantPool.Entry var1 = this.readRef((byte)13);
      return var1 != null && var1.getTag() == 1 ? ConstantPool.getSignatureEntry(var1.stringValue()) : (ConstantPool.SignatureEntry)var1;
   }

   void read() throws IOException {
      boolean var1 = false;

      try {
         this.readMagicNumbers();
         this.readConstantPool();
         this.readHeader();
         this.readMembers(false);
         this.readMembers(true);
         this.readAttributes(0, this.cls);
         this.fixUnresolvedEntries();
         this.cls.finishReading();

         assert 0 >= this.in.read(new byte[1]);

         var1 = true;
      } finally {
         if (!var1 && this.verbose > 0) {
            Utils.log.warning("Erroneous data at input offset " + this.inPos + " of " + this.cls.file);
         }

      }

   }

   void readMagicNumbers() throws IOException {
      this.cls.magic = this.in.readInt();
      if (this.cls.magic != -889275714) {
         throw new Attribute.FormatException("Bad magic number in class file " + Integer.toHexString(this.cls.magic), 0, "magic-number", "pass");
      } else {
         short var1 = (short)this.readUnsignedShort();
         short var2 = (short)this.readUnsignedShort();
         this.cls.version = Package.Version.of(var2, var1);
         String var3 = this.checkVersion(this.cls.version);
         if (var3 != null) {
            throw new Attribute.FormatException("classfile version too " + var3 + ": " + this.cls.version + " in " + this.cls.file, 0, "version", "pass");
         }
      }
   }

   private String checkVersion(Package.Version var1) {
      short var2 = var1.major;
      short var3 = var1.minor;
      if (var2 >= this.pkg.minClassVersion.major && (var2 != this.pkg.minClassVersion.major || var3 >= this.pkg.minClassVersion.minor)) {
         return var2 <= this.pkg.maxClassVersion.major && (var2 != this.pkg.maxClassVersion.major || var3 <= this.pkg.maxClassVersion.minor) ? null : "large";
      } else {
         return "small";
      }
   }

   void readConstantPool() throws IOException {
      int var1 = this.in.readUnsignedShort();
      int[] var2 = new int[var1 * 4];
      int var3 = 0;
      ConstantPool.Entry[] var4 = new ConstantPool.Entry[var1];
      var4[0] = null;

      int var5;
      for(var5 = 1; var5 < var1; ++var5) {
         byte var6 = this.in.readByte();
         switch(var6) {
         case 1:
            var4[var5] = ConstantPool.getUtf8Entry(this.in.readUTF());
            break;
         case 2:
         case 13:
         case 14:
         case 17:
         default:
            throw new ClassReader.ClassFormatException("Bad constant pool tag " + var6 + " in File: " + this.cls.file.nameString + " at pos: " + this.inPos);
         case 3:
            var4[var5] = ConstantPool.getLiteralEntry(this.in.readInt());
            break;
         case 4:
            var4[var5] = ConstantPool.getLiteralEntry(this.in.readFloat());
            break;
         case 5:
            var4[var5] = ConstantPool.getLiteralEntry(this.in.readLong());
            ++var5;
            var4[var5] = null;
            break;
         case 6:
            var4[var5] = ConstantPool.getLiteralEntry(this.in.readDouble());
            ++var5;
            var4[var5] = null;
            break;
         case 7:
         case 8:
         case 16:
            var2[var3++] = var5;
            var2[var3++] = var6;
            var2[var3++] = this.in.readUnsignedShort();
            var2[var3++] = -1;
            break;
         case 9:
         case 10:
         case 11:
         case 12:
            var2[var3++] = var5;
            var2[var3++] = var6;
            var2[var3++] = this.in.readUnsignedShort();
            var2[var3++] = this.in.readUnsignedShort();
            break;
         case 15:
            var2[var3++] = var5;
            var2[var3++] = var6;
            var2[var3++] = ~this.in.readUnsignedByte();
            var2[var3++] = this.in.readUnsignedShort();
            break;
         case 18:
            var2[var3++] = var5;
            var2[var3++] = var6;
            var2[var3++] = ~this.in.readUnsignedShort();
            var2[var3++] = this.in.readUnsignedShort();
         }
      }

      this.constantPoolLimit = this.inPos;

      label74:
      do {
         if (var3 <= 0) {
            this.cls.cpMap = var4;
            return;
         }

         if (this.verbose > 3) {
            Utils.log.fine("CP fixups [" + var3 / 4 + "]");
         }

         var5 = var3;
         var3 = 0;
         int var18 = 0;

         while(true) {
            while(true) {
               if (var18 >= var5) {
                  continue label74;
               }

               int var7 = var2[var18++];
               int var8 = var2[var18++];
               int var9 = var2[var18++];
               int var10 = var2[var18++];
               if (this.verbose > 3) {
                  Utils.log.fine("  cp[" + var7 + "] = " + ConstantPool.tagName(var8) + "{" + var9 + "," + var10 + "}");
               }

               if (var9 >= 0 && var4[var9] == null || var10 >= 0 && var4[var10] == null) {
                  var2[var3++] = var7;
                  var2[var3++] = var8;
                  var2[var3++] = var9;
                  var2[var3++] = var10;
               } else {
                  switch(var8) {
                  case 7:
                     var4[var7] = ConstantPool.getClassEntry(var4[var9].stringValue());
                     break;
                  case 8:
                     var4[var7] = ConstantPool.getStringEntry(var4[var9].stringValue());
                     break;
                  case 9:
                  case 10:
                  case 11:
                     ConstantPool.ClassEntry var11 = (ConstantPool.ClassEntry)this.checkTag(var4[var9], (byte)7);
                     ConstantPool.DescriptorEntry var12 = (ConstantPool.DescriptorEntry)this.checkTag(var4[var10], (byte)12);
                     var4[var7] = ConstantPool.getMemberEntry((byte)var8, var11, var12);
                     break;
                  case 12:
                     ConstantPool.Utf8Entry var13 = (ConstantPool.Utf8Entry)this.checkTag(var4[var9], (byte)1);
                     ConstantPool.Utf8Entry var14 = (ConstantPool.Utf8Entry)this.checkTag(var4[var10], (byte)13);
                     var4[var7] = ConstantPool.getDescriptorEntry(var13, var14);
                     break;
                  case 13:
                  case 14:
                  case 17:
                  default:
                     assert false;
                     break;
                  case 15:
                     byte var15 = (byte)(~var9);
                     ConstantPool.MemberEntry var16 = (ConstantPool.MemberEntry)this.checkTag(var4[var10], (byte)52);
                     var4[var7] = ConstantPool.getMethodHandleEntry(var15, var16);
                     break;
                  case 16:
                     var4[var7] = ConstantPool.getMethodTypeEntry((ConstantPool.Utf8Entry)this.checkTag(var4[var9], (byte)13));
                     break;
                  case 18:
                     ConstantPool.DescriptorEntry var17 = (ConstantPool.DescriptorEntry)this.checkTag(var4[var10], (byte)12);
                     var4[var7] = new ClassReader.UnresolvedEntry((byte)var8, new Object[]{~var9, var17});
                  }
               }
            }
         }
      } while($assertionsDisabled || var3 < var5);

      throw new AssertionError();
   }

   private void fixUnresolvedEntries() {
      if (this.haveUnresolvedEntry) {
         ConstantPool.Entry[] var1 = this.cls.getCPMap();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            ConstantPool.Entry var3 = var1[var2];
            if (var3 instanceof ClassReader.UnresolvedEntry) {
               var1[var2] = var3 = ((ClassReader.UnresolvedEntry)var3).resolve();

               assert !(var3 instanceof ClassReader.UnresolvedEntry);
            }
         }

         this.haveUnresolvedEntry = false;
      }
   }

   void readHeader() throws IOException {
      this.cls.flags = this.readUnsignedShort();
      this.cls.thisClass = this.readClassRef();
      this.cls.superClass = this.readClassRefOrNull();
      int var1 = this.readUnsignedShort();
      this.cls.interfaces = new ConstantPool.ClassEntry[var1];

      for(int var2 = 0; var2 < var1; ++var2) {
         this.cls.interfaces[var2] = this.readClassRef();
      }

   }

   void readMembers(boolean var1) throws IOException {
      int var2 = this.readUnsignedShort();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.readMember(var1);
      }

   }

   void readMember(boolean var1) throws IOException {
      int var2 = this.readUnsignedShort();
      ConstantPool.Utf8Entry var3 = this.readUtf8Ref();
      ConstantPool.SignatureEntry var4 = this.readSignatureRef();
      ConstantPool.DescriptorEntry var5 = ConstantPool.getDescriptorEntry(var3, var4);
      Object var6;
      if (!var1) {
         var6 = this.cls.new Field(var2, var5);
      } else {
         var6 = this.cls.new Method(var2, var5);
      }

      this.readAttributes(!var1 ? 1 : 2, (Attribute.Holder)var6);
   }

   void readAttributes(int var1, Attribute.Holder var2) throws IOException {
      int var3 = this.readUnsignedShort();
      if (var3 != 0) {
         if (this.verbose > 3) {
            Utils.log.fine("readAttributes " + var2 + " [" + var3 + "]");
         }

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = this.readUtf8Ref().stringValue();
            int var6 = this.readInt();
            if (this.attrCommands != null) {
               Attribute.Layout var7 = Attribute.keyForLookup(var1, var5);
               String var8 = (String)this.attrCommands.get(var7);
               if (var8 != null) {
                  byte var10 = -1;
                  switch(var8.hashCode()) {
                  case 3433489:
                     if (var8.equals("pass")) {
                        var10 = 0;
                     }
                     break;
                  case 96784904:
                     if (var8.equals("error")) {
                        var10 = 1;
                     }
                     break;
                  case 109773592:
                     if (var8.equals("strip")) {
                        var10 = 2;
                     }
                  }

                  switch(var10) {
                  case 0:
                     String var11 = "passing attribute bitwise in " + var2;
                     throw new Attribute.FormatException(var11, var1, var5, var8);
                  case 1:
                     String var12 = "attribute not allowed in " + var2;
                     throw new Attribute.FormatException(var12, var1, var5, var8);
                  case 2:
                     this.skip(var6, var5 + " attribute in " + var2);
                     continue;
                  }
               }
            }

            Attribute var15 = Attribute.lookup(Package.attrDefs, var1, var5);
            if (this.verbose > 4 && var15 != null) {
               Utils.log.fine("pkg_attribute_lookup " + var5 + " = " + var15);
            }

            if (var15 == null) {
               var15 = Attribute.lookup(this.attrDefs, var1, var5);
               if (this.verbose > 4 && var15 != null) {
                  Utils.log.fine("this " + var5 + " = " + var15);
               }
            }

            if (var15 == null) {
               var15 = Attribute.lookup((Map)null, var1, var5);
               if (this.verbose > 4 && var15 != null) {
                  Utils.log.fine("null_attribute_lookup " + var5 + " = " + var15);
               }
            }

            if (var15 == null && var6 == 0) {
               var15 = Attribute.find(var1, var5, "");
            }

            boolean var16 = var1 == 3 && (var5.equals("StackMap") || var5.equals("StackMapX"));
            if (var16) {
               Code var9 = (Code)var2;
               if (var9.max_stack >= 65536 || var9.max_locals >= 65536 || var9.getLength() >= 65536 || var5.endsWith("X")) {
                  var15 = null;
               }
            }

            if (var15 == null) {
               String var17;
               if (var16) {
                  var17 = "unsupported StackMap variant in " + var2;
                  throw new Attribute.FormatException(var17, var1, var5, "pass");
               }

               if (!"strip".equals(this.unknownAttrCommand)) {
                  var17 = " is unknown attribute in class " + var2;
                  throw new Attribute.FormatException(var17, var1, var5, this.unknownAttrCommand);
               }

               this.skip(var6, "unknown " + var5 + " attribute in " + var2);
            } else {
               long var18 = this.inPos;
               if (var15.layout() == Package.attrCodeEmpty) {
                  Package.Class.Method var19 = (Package.Class.Method)var2;
                  var19.code = new Code(var19);

                  try {
                     this.readCode(var19.code);
                  } catch (Instruction.FormatException var14) {
                     String var13 = var14.getMessage() + " in " + var2;
                     throw new ClassReader.ClassFormatException(var13, var14);
                  }

                  assert (long)var6 == this.inPos - var18;
               } else {
                  if (var15.layout() == Package.attrBootstrapMethodsEmpty) {
                     assert var2 == this.cls;

                     this.readBootstrapMethods(this.cls);

                     assert (long)var6 == this.inPos - var18;
                     continue;
                  }

                  if (var15.layout() == Package.attrInnerClassesEmpty) {
                     assert var2 == this.cls;

                     this.readInnerClasses(this.cls);

                     assert (long)var6 == this.inPos - var18;
                  } else if (var6 > 0) {
                     byte[] var20 = new byte[var6];
                     this.in.readFully(var20);
                     var15 = var15.addContent(var20);
                  }
               }

               if (var15.size() == 0 && !var15.layout().isEmpty()) {
                  throw new ClassReader.ClassFormatException(var5 + ": attribute length cannot be zero, in " + var2);
               }

               var2.addAttribute(var15);
               if (this.verbose > 2) {
                  Utils.log.fine("read " + var15);
               }
            }
         }

      }
   }

   void readCode(Code var1) throws IOException {
      var1.max_stack = this.readUnsignedShort();
      var1.max_locals = this.readUnsignedShort();
      var1.bytes = new byte[this.readInt()];
      this.in.readFully(var1.bytes);
      ConstantPool.Entry[] var2 = this.cls.getCPMap();
      Instruction.opcodeChecker(var1.bytes, var2, this.cls.version);
      int var3 = this.readUnsignedShort();
      var1.setHandlerCount(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         var1.handler_start[var4] = this.readUnsignedShort();
         var1.handler_end[var4] = this.readUnsignedShort();
         var1.handler_catch[var4] = this.readUnsignedShort();
         var1.handler_class[var4] = this.readClassRefOrNull();
      }

      this.readAttributes(3, var1);
   }

   void readBootstrapMethods(Package.Class var1) throws IOException {
      ConstantPool.BootstrapMethodEntry[] var2 = new ConstantPool.BootstrapMethodEntry[this.readUnsignedShort()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ConstantPool.MethodHandleEntry var4 = (ConstantPool.MethodHandleEntry)this.readRef((byte)15);
         ConstantPool.Entry[] var5 = new ConstantPool.Entry[this.readUnsignedShort()];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = this.readRef((byte)51);
         }

         var2[var3] = ConstantPool.getBootstrapMethodEntry(var4, var5);
      }

      var1.setBootstrapMethods(Arrays.asList(var2));
   }

   void readInnerClasses(Package.Class var1) throws IOException {
      int var2 = this.readUnsignedShort();
      ArrayList var3 = new ArrayList(var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         Package.InnerClass var5 = new Package.InnerClass(this.readClassRef(), this.readClassRefOrNull(), (ConstantPool.Utf8Entry)this.readRefOrNull((byte)1), this.readUnsignedShort());
         var3.add(var5);
      }

      var1.innerClasses = var3;
   }

   static class ClassFormatException extends IOException {
      private static final long serialVersionUID = -3564121733989501833L;

      public ClassFormatException(String var1) {
         super(var1);
      }

      public ClassFormatException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   private class UnresolvedEntry extends ConstantPool.Entry {
      final Object[] refsOrIndexes;

      UnresolvedEntry(byte var2, Object... var3) {
         super(var2);
         this.refsOrIndexes = var3;
         ClassReader.this.haveUnresolvedEntry = true;
      }

      ConstantPool.Entry resolve() {
         Package.Class var1 = ClassReader.this.cls;
         switch(this.tag) {
         case 18:
            ConstantPool.BootstrapMethodEntry var3 = (ConstantPool.BootstrapMethodEntry)var1.bootstrapMethods.get((Integer)this.refsOrIndexes[0]);
            ConstantPool.DescriptorEntry var4 = (ConstantPool.DescriptorEntry)this.refsOrIndexes[1];
            ConstantPool.InvokeDynamicEntry var2 = ConstantPool.getInvokeDynamicEntry(var3, var4);
            return var2;
         default:
            throw new AssertionError();
         }
      }

      private void unresolved() {
         throw new RuntimeException("unresolved entry has no string");
      }

      public int compareTo(Object var1) {
         this.unresolved();
         return 0;
      }

      public boolean equals(Object var1) {
         this.unresolved();
         return false;
      }

      protected int computeValueHash() {
         this.unresolved();
         return 0;
      }

      public String stringValue() {
         this.unresolved();
         return this.toString();
      }

      public String toString() {
         return "(unresolved " + ConstantPool.tagName(this.tag) + ")";
      }
   }
}
