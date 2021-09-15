package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

class ClassWriter {
   int verbose;
   Package pkg;
   Package.Class cls;
   DataOutputStream out;
   ConstantPool.Index cpIndex;
   ConstantPool.Index bsmIndex;
   ByteArrayOutputStream buf = new ByteArrayOutputStream();
   DataOutputStream bufOut;

   ClassWriter(Package.Class var1, OutputStream var2) throws IOException {
      this.bufOut = new DataOutputStream(this.buf);
      this.pkg = var1.getPackage();
      this.cls = var1;
      this.verbose = this.pkg.verbose;
      this.out = new DataOutputStream(new BufferedOutputStream(var2));
      this.cpIndex = ConstantPool.makeIndex(var1.toString(), var1.getCPMap());
      this.cpIndex.flattenSigs = true;
      if (var1.hasBootstrapMethods()) {
         this.bsmIndex = ConstantPool.makeIndex(this.cpIndex.debugName + ".BootstrapMethods", (ConstantPool.Entry[])var1.getBootstrapMethodMap());
      }

      if (this.verbose > 1) {
         Utils.log.fine("local CP=" + (this.verbose > 2 ? this.cpIndex.dumpString() : this.cpIndex.toString()));
      }

   }

   private void writeShort(int var1) throws IOException {
      this.out.writeShort(var1);
   }

   private void writeInt(int var1) throws IOException {
      this.out.writeInt(var1);
   }

   private void writeRef(ConstantPool.Entry var1) throws IOException {
      this.writeRef(var1, this.cpIndex);
   }

   private void writeRef(ConstantPool.Entry var1, ConstantPool.Index var2) throws IOException {
      int var3 = var1 == null ? 0 : var2.indexOf(var1);
      this.writeShort(var3);
   }

   void write() throws IOException {
      boolean var1 = false;

      try {
         if (this.verbose > 1) {
            Utils.log.fine("...writing " + this.cls);
         }

         this.writeMagicNumbers();
         this.writeConstantPool();
         this.writeHeader();
         this.writeMembers(false);
         this.writeMembers(true);
         this.writeAttributes(0, this.cls);
         this.out.flush();
         var1 = true;
      } finally {
         if (!var1) {
            Utils.log.warning("Error on output of " + this.cls);
         }

      }

   }

   void writeMagicNumbers() throws IOException {
      this.writeInt(this.cls.magic);
      this.writeShort(this.cls.version.minor);
      this.writeShort(this.cls.version.major);
   }

   void writeConstantPool() throws IOException {
      ConstantPool.Entry[] var1 = this.cls.cpMap;
      this.writeShort(var1.length);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         ConstantPool.Entry var3 = var1[var2];

         assert var3 == null == (var2 == 0 || var1[var2 - 1] != null && var1[var2 - 1].isDoubleWord());

         if (var3 != null) {
            byte var4 = var3.getTag();
            if (this.verbose > 2) {
               Utils.log.fine("   CP[" + var2 + "] = " + var3);
            }

            this.out.write(var4);
            switch(var4) {
            case 1:
               this.out.writeUTF(var3.stringValue());
               break;
            case 2:
            case 14:
            default:
               throw new IOException("Bad constant pool tag " + var4);
            case 3:
               this.out.writeInt(((ConstantPool.NumberEntry)var3).numberValue().intValue());
               break;
            case 4:
               float var5 = ((ConstantPool.NumberEntry)var3).numberValue().floatValue();
               this.out.writeInt(Float.floatToRawIntBits(var5));
               break;
            case 5:
               this.out.writeLong(((ConstantPool.NumberEntry)var3).numberValue().longValue());
               break;
            case 6:
               double var6 = ((ConstantPool.NumberEntry)var3).numberValue().doubleValue();
               this.out.writeLong(Double.doubleToRawLongBits(var6));
               break;
            case 7:
            case 8:
            case 16:
               this.writeRef(var3.getRef(0));
               break;
            case 9:
            case 10:
            case 11:
            case 12:
               this.writeRef(var3.getRef(0));
               this.writeRef(var3.getRef(1));
               break;
            case 13:
               throw new AssertionError("CP should have Signatures remapped to Utf8");
            case 15:
               ConstantPool.MethodHandleEntry var8 = (ConstantPool.MethodHandleEntry)var3;
               this.out.writeByte(var8.refKind);
               this.writeRef(var8.getRef(0));
               break;
            case 17:
               throw new AssertionError("CP should have BootstrapMethods moved to side-table");
            case 18:
               this.writeRef(var3.getRef(0), this.bsmIndex);
               this.writeRef(var3.getRef(1));
            }
         }
      }

   }

   void writeHeader() throws IOException {
      this.writeShort(this.cls.flags);
      this.writeRef(this.cls.thisClass);
      this.writeRef(this.cls.superClass);
      this.writeShort(this.cls.interfaces.length);

      for(int var1 = 0; var1 < this.cls.interfaces.length; ++var1) {
         this.writeRef(this.cls.interfaces[var1]);
      }

   }

   void writeMembers(boolean var1) throws IOException {
      List var2;
      if (!var1) {
         var2 = this.cls.getFields();
      } else {
         var2 = this.cls.getMethods();
      }

      this.writeShort(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Package.Class.Member var4 = (Package.Class.Member)var3.next();
         this.writeMember(var4, var1);
      }

   }

   void writeMember(Package.Class.Member var1, boolean var2) throws IOException {
      if (this.verbose > 2) {
         Utils.log.fine("writeMember " + var1);
      }

      this.writeShort(var1.flags);
      this.writeRef(var1.getDescriptor().nameRef);
      this.writeRef(var1.getDescriptor().typeRef);
      this.writeAttributes(!var2 ? 1 : 2, var1);
   }

   private void reorderBSMandICS(Attribute.Holder var1) {
      Attribute var2 = var1.getAttribute(Package.attrBootstrapMethodsEmpty);
      if (var2 != null) {
         Attribute var3 = var1.getAttribute(Package.attrInnerClassesEmpty);
         if (var3 != null) {
            int var4 = var1.attributes.indexOf(var2);
            int var5 = var1.attributes.indexOf(var3);
            if (var4 > var5) {
               var1.attributes.remove(var2);
               var1.attributes.add(var5, var2);
            }

         }
      }
   }

   void writeAttributes(int var1, Attribute.Holder var2) throws IOException {
      if (var2.attributes == null) {
         this.writeShort(0);
      } else {
         if (var2 instanceof Package.Class) {
            this.reorderBSMandICS(var2);
         }

         this.writeShort(var2.attributes.size());
         Iterator var3 = var2.attributes.iterator();

         while(true) {
            while(var3.hasNext()) {
               Attribute var4 = (Attribute)var3.next();
               var4.finishRefs(this.cpIndex);
               this.writeRef(var4.getNameRef());
               if (var4.layout() != Package.attrCodeEmpty && var4.layout() != Package.attrBootstrapMethodsEmpty && var4.layout() != Package.attrInnerClassesEmpty) {
                  if (this.verbose > 2) {
                     Utils.log.fine("Attribute " + var4.name() + " [" + var4.size() + "]");
                  }

                  this.writeInt(var4.size());
                  this.out.write(var4.bytes());
               } else {
                  DataOutputStream var5 = this.out;

                  assert this.out != this.bufOut;

                  this.buf.reset();
                  this.out = this.bufOut;
                  if ("Code".equals(var4.name())) {
                     Package.Class.Method var6 = (Package.Class.Method)var2;
                     this.writeCode(var6.code);
                  } else if ("BootstrapMethods".equals(var4.name())) {
                     assert var2 == this.cls;

                     this.writeBootstrapMethods(this.cls);
                  } else {
                     if (!"InnerClasses".equals(var4.name())) {
                        throw new AssertionError();
                     }

                     assert var2 == this.cls;

                     this.writeInnerClasses(this.cls);
                  }

                  this.out = var5;
                  if (this.verbose > 2) {
                     Utils.log.fine("Attribute " + var4.name() + " [" + this.buf.size() + "]");
                  }

                  this.writeInt(this.buf.size());
                  this.buf.writeTo(this.out);
               }
            }

            return;
         }
      }
   }

   void writeCode(Code var1) throws IOException {
      var1.finishRefs(this.cpIndex);
      this.writeShort(var1.max_stack);
      this.writeShort(var1.max_locals);
      this.writeInt(var1.bytes.length);
      this.out.write(var1.bytes);
      int var2 = var1.getHandlerCount();
      this.writeShort(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.writeShort(var1.handler_start[var3]);
         this.writeShort(var1.handler_end[var3]);
         this.writeShort(var1.handler_catch[var3]);
         this.writeRef(var1.handler_class[var3]);
      }

      this.writeAttributes(3, var1);
   }

   void writeBootstrapMethods(Package.Class var1) throws IOException {
      List var2 = var1.getBootstrapMethods();
      this.writeShort(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ConstantPool.BootstrapMethodEntry var4 = (ConstantPool.BootstrapMethodEntry)var3.next();
         this.writeRef(var4.bsmRef);
         this.writeShort(var4.argRefs.length);
         ConstantPool.Entry[] var5 = var4.argRefs;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ConstantPool.Entry var8 = var5[var7];
            this.writeRef(var8);
         }
      }

   }

   void writeInnerClasses(Package.Class var1) throws IOException {
      List var2 = var1.getInnerClasses();
      this.writeShort(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Package.InnerClass var4 = (Package.InnerClass)var3.next();
         this.writeRef(var4.thisClass);
         this.writeRef(var4.outerClass);
         this.writeRef(var4.name);
         this.writeShort(var4.flags);
      }

   }
}
