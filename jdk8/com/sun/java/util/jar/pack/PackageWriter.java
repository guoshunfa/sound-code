package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PackageWriter extends BandStructure {
   Package pkg;
   OutputStream finalOut;
   Package.Version packageVersion;
   Set<ConstantPool.Entry> requiredEntries;
   Map<Attribute.Layout, int[]> backCountTable;
   int[][] attrCounts;
   int[] maxFlags;
   List<Map<Attribute.Layout, int[]>> allLayouts;
   Attribute.Layout[] attrDefsWritten;
   private Code curCode;
   private Package.Class curClass;
   private ConstantPool.Entry[] curCPMap;
   int[] codeHist = new int[256];
   int[] ldcHist = new int[20];

   PackageWriter(Package var1, OutputStream var2) throws IOException {
      this.pkg = var1;
      this.finalOut = var2;
      this.initHighestClassVersion(var1.getHighestClassVersion());
   }

   void write() throws IOException {
      boolean var1 = false;

      try {
         if (this.verbose > 0) {
            Utils.log.info("Setting up constant pool...");
         }

         this.setup();
         if (this.verbose > 0) {
            Utils.log.info("Packing...");
         }

         this.writeConstantPool();
         this.writeFiles();
         this.writeAttrDefs();
         this.writeInnerClasses();
         this.writeClassesAndByteCodes();
         this.writeAttrCounts();
         if (this.verbose > 1) {
            this.printCodeHist();
         }

         if (this.verbose > 0) {
            Utils.log.info("Coding...");
         }

         this.all_bands.chooseBandCodings();
         this.writeFileHeader();
         this.writeAllBandsTo(this.finalOut);
         var1 = true;
      } catch (Exception var3) {
         Utils.log.warning("Error on output: " + var3, var3);
         if (this.verbose > 0) {
            this.finalOut.close();
         }

         if (var3 instanceof IOException) {
            throw (IOException)var3;
         } else if (var3 instanceof RuntimeException) {
            throw (RuntimeException)var3;
         } else {
            throw new Error("error packing", var3);
         }
      }
   }

   void setup() {
      this.requiredEntries = new HashSet();
      this.setArchiveOptions();
      this.trimClassAttributes();
      this.collectAttributeLayouts();
      this.pkg.buildGlobalConstantPool(this.requiredEntries);
      this.setBandIndexes();
      this.makeNewAttributeBands();
      this.collectInnerClasses();
   }

   void chooseDefaultPackageVersion() throws IOException {
      if (this.pkg.packageVersion != null) {
         this.packageVersion = this.pkg.packageVersion;
         if (this.verbose > 0) {
            Utils.log.info("package version overridden with: " + this.packageVersion);
         }

      } else {
         Package.Version var1 = this.getHighestClassVersion();
         if (var1.lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
            this.packageVersion = Constants.JAVA5_PACKAGE_VERSION;
         } else if (var1.equals(Constants.JAVA6_MAX_CLASS_VERSION) || var1.equals(Constants.JAVA7_MAX_CLASS_VERSION) && !this.pkg.cp.haveExtraTags()) {
            this.packageVersion = Constants.JAVA6_PACKAGE_VERSION;
         } else if (var1.equals(Constants.JAVA7_MAX_CLASS_VERSION)) {
            this.packageVersion = Constants.JAVA7_PACKAGE_VERSION;
         } else {
            this.packageVersion = Constants.JAVA8_PACKAGE_VERSION;
         }

         if (this.verbose > 0) {
            Utils.log.info("Highest version class file: " + var1 + " package version: " + this.packageVersion);
         }

      }
   }

   void checkVersion() throws IOException {
      assert this.packageVersion != null;

      if (this.packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION) && testBit(this.archiveOptions, 8)) {
         throw new IOException("Format bits for Java 7 must be zero in previous releases");
      } else if (testBit(this.archiveOptions, -8192)) {
         throw new IOException("High archive option bits are reserved and must be zero: " + Integer.toHexString(this.archiveOptions));
      }
   }

   void setArchiveOptions() {
      int var1 = this.pkg.default_modtime;
      int var2 = this.pkg.default_modtime;
      int var3 = -1;
      int var4 = 0;
      this.archiveOptions |= this.pkg.default_options;

      int var8;
      for(Iterator var5 = this.pkg.files.iterator(); var5.hasNext(); var4 |= var8) {
         Package.File var6 = (Package.File)var5.next();
         int var7 = var6.modtime;
         var8 = var6.options;
         if (var1 == 0) {
            var2 = var7;
            var1 = var7;
         } else {
            if (var1 > var7) {
               var1 = var7;
            }

            if (var2 < var7) {
               var2 = var7;
            }
         }

         var3 &= var8;
      }

      if (this.pkg.default_modtime == 0) {
         this.pkg.default_modtime = var1;
      }

      if (var1 != 0 && var1 != var2) {
         this.archiveOptions |= 64;
      }

      if (!testBit(this.archiveOptions, 32) && var3 != -1) {
         if (testBit(var3, 1)) {
            this.archiveOptions |= 32;
            --var3;
            --var4;
         }

         Package var10000 = this.pkg;
         var10000.default_options |= var3;
         if (var3 != var4 || var3 != this.pkg.default_options) {
            this.archiveOptions |= 128;
         }
      }

      HashMap var14 = new HashMap();
      int var15 = 0;
      Package.Version var16 = null;
      Iterator var17 = this.pkg.classes.iterator();

      Package.Class var9;
      while(var17.hasNext()) {
         var9 = (Package.Class)var17.next();
         Package.Version var10 = var9.getVersion();
         int[] var11 = (int[])var14.get(var10);
         if (var11 == null) {
            var11 = new int[1];
            var14.put(var10, var11);
         }

         int var12 = ++var11[0];
         if (var15 < var12) {
            var15 = var12;
            var16 = var10;
         }
      }

      var14.clear();
      if (var16 == null) {
         var16 = Constants.JAVA_MIN_CLASS_VERSION;
      }

      this.pkg.defaultClassVersion = var16;
      if (this.verbose > 0) {
         Utils.log.info("Consensus version number in segment is " + var16);
      }

      if (this.verbose > 0) {
         Utils.log.info("Highest version number in segment is " + this.pkg.getHighestClassVersion());
      }

      var17 = this.pkg.classes.iterator();

      while(var17.hasNext()) {
         var9 = (Package.Class)var17.next();
         if (!var9.getVersion().equals(var16)) {
            Attribute var20 = this.makeClassFileVersionAttr(var9.getVersion());
            if (this.verbose > 1) {
               Utils.log.fine("Version " + var9.getVersion() + " of " + var9 + " doesn't match package version " + var16);
            }

            var9.addAttribute(var20);
         }
      }

      var17 = this.pkg.files.iterator();

      while(var17.hasNext()) {
         Package.File var18 = (Package.File)var17.next();
         long var21 = var18.getFileLength();
         if (var21 != (long)((int)var21)) {
            this.archiveOptions |= 256;
            if (this.verbose > 0) {
               Utils.log.info("Note: Huge resource file " + var18.getFileName() + " forces 64-bit sizing");
            }
            break;
         }
      }

      var8 = 0;
      int var19 = 0;
      Iterator var22 = this.pkg.classes.iterator();

      while(var22.hasNext()) {
         Package.Class var23 = (Package.Class)var22.next();
         Iterator var24 = var23.getMethods().iterator();

         while(var24.hasNext()) {
            Package.Class.Method var13 = (Package.Class.Method)var24.next();
            if (var13.code != null) {
               if (var13.code.attributeSize() == 0) {
                  ++var19;
               } else if (shortCodeHeader(var13.code) != 0) {
                  var8 += 3;
               }
            }
         }
      }

      if (var8 > var19) {
         this.archiveOptions |= 4;
      }

      if (this.verbose > 0) {
         Utils.log.info("archiveOptions = 0b" + Integer.toBinaryString(this.archiveOptions));
      }

   }

   void writeFileHeader() throws IOException {
      this.chooseDefaultPackageVersion();
      this.writeArchiveMagic();
      this.writeArchiveHeader();
   }

   private void putMagicInt32(int var1) throws IOException {
      int var2 = var1;

      for(int var3 = 0; var3 < 4; ++var3) {
         this.archive_magic.putByte(255 & var2 >>> 24);
         var2 <<= 8;
      }

   }

   void writeArchiveMagic() throws IOException {
      this.pkg.getClass();
      this.putMagicInt32(-889270259);
   }

   void writeArchiveHeader() throws IOException {
      int var1 = 15;
      boolean var2 = testBit(this.archiveOptions, 1);
      if (!var2) {
         var2 |= this.band_headers.length() != 0;
         var2 |= this.attrDefsWritten.length != 0;
         if (var2) {
            this.archiveOptions |= 1;
         }
      }

      if (var2) {
         var1 += 2;
      }

      boolean var3 = testBit(this.archiveOptions, 16);
      if (!var3) {
         var3 |= this.archiveNextCount > 0;
         var3 |= this.pkg.default_modtime != 0;
         if (var3) {
            this.archiveOptions |= 16;
         }
      }

      if (var3) {
         var1 += 5;
      }

      boolean var4 = testBit(this.archiveOptions, 2);
      if (!var4) {
         var4 |= this.pkg.cp.haveNumbers();
         if (var4) {
            this.archiveOptions |= 2;
         }
      }

      if (var4) {
         var1 += 4;
      }

      boolean var5 = testBit(this.archiveOptions, 8);
      if (!var5) {
         var5 |= this.pkg.cp.haveExtraTags();
         if (var5) {
            this.archiveOptions |= 8;
         }
      }

      if (var5) {
         var1 += 4;
      }

      this.checkVersion();
      this.archive_header_0.putInt(this.packageVersion.minor);
      this.archive_header_0.putInt(this.packageVersion.major);
      if (this.verbose > 0) {
         Utils.log.info("Package Version for this segment:" + this.packageVersion);
      }

      this.archive_header_0.putInt(this.archiveOptions);

      assert this.archive_header_0.length() == 3;

      if (var3) {
         assert this.archive_header_S.length() == 0;

         this.archive_header_S.putInt(0);

         assert this.archive_header_S.length() == 1;

         this.archive_header_S.putInt(0);

         assert this.archive_header_S.length() == 2;
      }

      if (var3) {
         this.archive_header_1.putInt(this.archiveNextCount);
         this.archive_header_1.putInt(this.pkg.default_modtime);
         this.archive_header_1.putInt(this.pkg.files.size());
      } else {
         assert this.pkg.files.isEmpty();
      }

      if (var2) {
         this.archive_header_1.putInt(this.band_headers.length());
         this.archive_header_1.putInt(this.attrDefsWritten.length);
      } else {
         assert this.band_headers.length() == 0;

         assert this.attrDefsWritten.length == 0;
      }

      this.writeConstantPoolCounts(var4, var5);
      this.archive_header_1.putInt(this.pkg.getAllInnerClasses().size());
      this.archive_header_1.putInt(this.pkg.defaultClassVersion.minor);
      this.archive_header_1.putInt(this.pkg.defaultClassVersion.major);
      this.archive_header_1.putInt(this.pkg.classes.size());

      assert this.archive_header_0.length() + this.archive_header_S.length() + this.archive_header_1.length() == var1;

      this.archiveSize0 = 0L;
      this.archiveSize1 = this.all_bands.outputSize();
      this.archiveSize0 += this.archive_magic.outputSize();
      this.archiveSize0 += this.archive_header_0.outputSize();
      this.archiveSize0 += this.archive_header_S.outputSize();
      this.archiveSize1 -= this.archiveSize0;
      if (var3) {
         int var7 = (int)(this.archiveSize1 >>> 32);
         int var8 = (int)(this.archiveSize1 >>> 0);
         this.archive_header_S.patchValue(0, var7);
         this.archive_header_S.patchValue(1, var8);
         int var9 = UNSIGNED5.getLength(0);
         this.archiveSize0 += (long)(UNSIGNED5.getLength(var7) - var9);
         this.archiveSize0 += (long)(UNSIGNED5.getLength(var8) - var9);
      }

      if (this.verbose > 1) {
         Utils.log.fine("archive sizes: " + this.archiveSize0 + "+" + this.archiveSize1);
      }

      assert this.all_bands.outputSize() == this.archiveSize0 + this.archiveSize1;

   }

   void writeConstantPoolCounts(boolean var1, boolean var2) throws IOException {
      byte[] var3 = ConstantPool.TAGS_IN_ORDER;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte var6 = var3[var5];
         int var7 = this.pkg.cp.getIndexByTag(var6).size();
         switch(var6) {
         case 1:
            assert var7 <= 0 || this.pkg.cp.getIndexByTag(var6).get(0) == ConstantPool.getUtf8Entry("");
         case 2:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         default:
            break;
         case 3:
         case 4:
         case 5:
         case 6:
            if (!var1) {
               assert var7 == 0;
               continue;
            }
            break;
         case 15:
         case 16:
         case 17:
         case 18:
            if (!var2) {
               assert var7 == 0;
               continue;
            }
         }

         this.archive_header_1.putInt(var7);
      }

   }

   protected ConstantPool.Index getCPIndex(byte var1) {
      return this.pkg.cp.getIndexByTag(var1);
   }

   void writeConstantPool() throws IOException {
      ConstantPool.IndexGroup var1 = this.pkg.cp;
      if (this.verbose > 0) {
         Utils.log.info("Writing CP");
      }

      byte[] var2 = ConstantPool.TAGS_IN_ORDER;
      int var3 = var2.length;

      label643:
      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var2[var4];
         ConstantPool.Index var6 = var1.getIndexByTag(var5);
         ConstantPool.Entry[] var7 = var6.cpMap;
         if (this.verbose > 0) {
            Utils.log.info("Writing " + var7.length + " " + ConstantPool.tagName(var5) + " entries...");
         }

         if (this.optDumpBands) {
            PrintStream var8 = new PrintStream(getDumpStream(var6, ".idx"));
            Throwable var9 = null;

            try {
               printArrayTo(var8, var7, 0, var7.length);
            } catch (Throwable var34) {
               var9 = var34;
               throw var34;
            } finally {
               if (var8 != null) {
                  if (var9 != null) {
                     try {
                        var8.close();
                     } catch (Throwable var32) {
                        var9.addSuppressed(var32);
                     }
                  } else {
                     var8.close();
                  }
               }

            }
         }

         int var11;
         int var42;
         ConstantPool.NumberEntry var54;
         switch(var5) {
         case 1:
            this.writeUtf8Bands(var7);
            break;
         case 2:
         case 14:
         default:
            throw new AssertionError("unexpected CP tag in package");
         case 3:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               var54 = (ConstantPool.NumberEntry)var7[var42];
               int var50 = (Integer)var54.numberValue();
               this.cp_Int.putInt(var50);
               ++var42;
            }
         case 4:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               var54 = (ConstantPool.NumberEntry)var7[var42];
               float var48 = (Float)var54.numberValue();
               var11 = Float.floatToIntBits(var48);
               this.cp_Float.putInt(var11);
               ++var42;
            }
         case 5:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               var54 = (ConstantPool.NumberEntry)var7[var42];
               long var46 = (Long)var54.numberValue();
               this.cp_Long_hi.putInt((int)(var46 >>> 32));
               this.cp_Long_lo.putInt((int)(var46 >>> 0));
               ++var42;
            }
         case 6:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               var54 = (ConstantPool.NumberEntry)var7[var42];
               double var44 = (Double)var54.numberValue();
               long var55 = Double.doubleToLongBits(var44);
               this.cp_Double_hi.putInt((int)(var55 >>> 32));
               this.cp_Double_lo.putInt((int)(var55 >>> 0));
               ++var42;
            }
         case 7:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               ConstantPool.ClassEntry var53 = (ConstantPool.ClassEntry)var7[var42];
               this.cp_Class.putRef(var53.ref);
               ++var42;
            }
         case 8:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               ConstantPool.StringEntry var52 = (ConstantPool.StringEntry)var7[var42];
               this.cp_String.putRef(var52.ref);
               ++var42;
            }
         case 9:
            this.writeMemberRefs(var5, var7, this.cp_Field_class, this.cp_Field_desc);
            break;
         case 10:
            this.writeMemberRefs(var5, var7, this.cp_Method_class, this.cp_Method_desc);
            break;
         case 11:
            this.writeMemberRefs(var5, var7, this.cp_Imethod_class, this.cp_Imethod_desc);
            break;
         case 12:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               ConstantPool.DescriptorEntry var51 = (ConstantPool.DescriptorEntry)var7[var42];
               this.cp_Descr_name.putRef(var51.nameRef);
               this.cp_Descr_type.putRef(var51.typeRef);
               ++var42;
            }
         case 13:
            this.writeSignatureBands(var7);
            break;
         case 15:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               ConstantPool.MethodHandleEntry var49 = (ConstantPool.MethodHandleEntry)var7[var42];
               this.cp_MethodHandle_refkind.putInt(var49.refKind);
               this.cp_MethodHandle_member.putRef(var49.memRef);
               ++var42;
            }
         case 16:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               ConstantPool.MethodTypeEntry var47 = (ConstantPool.MethodTypeEntry)var7[var42];
               this.cp_MethodType.putRef(var47.typeRef);
               ++var42;
            }
         case 17:
            var42 = 0;

            while(true) {
               if (var42 >= var7.length) {
                  continue label643;
               }

               ConstantPool.BootstrapMethodEntry var45 = (ConstantPool.BootstrapMethodEntry)var7[var42];
               this.cp_BootstrapMethod_ref.putRef(var45.bsmRef);
               this.cp_BootstrapMethod_arg_count.putInt(var45.argRefs.length);
               ConstantPool.Entry[] var10 = var45.argRefs;
               var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  ConstantPool.Entry var13 = var10[var12];
                  this.cp_BootstrapMethod_arg.putRef(var13);
               }

               ++var42;
            }
         case 18:
            for(var42 = 0; var42 < var7.length; ++var42) {
               ConstantPool.InvokeDynamicEntry var43 = (ConstantPool.InvokeDynamicEntry)var7[var42];
               this.cp_InvokeDynamic_spec.putRef(var43.bssRef);
               this.cp_InvokeDynamic_desc.putRef(var43.descRef);
            }
         }
      }

      if (this.optDumpBands || this.verbose > 1) {
         for(byte var37 = 50; var37 < 54; ++var37) {
            ConstantPool.Index var38 = var1.getIndexByTag(var37);
            if (var38 != null && !var38.isEmpty()) {
               ConstantPool.Entry[] var39 = var38.cpMap;
               if (this.verbose > 1) {
                  Utils.log.info("Index group " + ConstantPool.tagName(var37) + " contains " + var39.length + " entries.");
               }

               if (this.optDumpBands) {
                  PrintStream var40 = new PrintStream(getDumpStream(var38.debugName, var37, ".gidx", var38));
                  Throwable var41 = null;

                  try {
                     printArrayTo(var40, var39, 0, var39.length, true);
                  } catch (Throwable var33) {
                     var41 = var33;
                     throw var33;
                  } finally {
                     if (var40 != null) {
                        if (var41 != null) {
                           try {
                              var40.close();
                           } catch (Throwable var31) {
                              var41.addSuppressed(var31);
                           }
                        } else {
                           var40.close();
                        }
                     }

                  }
               }
            }
         }
      }

   }

   void writeUtf8Bands(ConstantPool.Entry[] var1) throws IOException {
      if (var1.length != 0) {
         assert var1[0].stringValue().equals("");

         char[][] var4 = new char[var1.length][];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = var1[var5].stringValue().toCharArray();
         }

         int[] var14 = new int[var1.length];
         char[] var6 = new char[0];

         int var7;
         int var8;
         int var10;
         for(var7 = 0; var7 < var4.length; ++var7) {
            var8 = 0;
            char[] var9 = var4[var7];

            for(var10 = Math.min(var9.length, var6.length); var8 < var10 && var9[var8] == var6[var8]; ++var8) {
            }

            var14[var7] = var8;
            if (var7 >= 2) {
               this.cp_Utf8_prefix.putInt(var8);
            } else {
               assert var8 == 0;
            }

            var6 = var9;
         }

         int var16;
         for(var7 = 0; var7 < var4.length; ++var7) {
            char[] var15 = var4[var7];
            var16 = var14[var7];
            var10 = var15.length - var14[var7];
            boolean var11 = false;
            int var12;
            if (var10 == 0) {
               var11 = var7 >= 1;
            } else if (this.optBigStrings && this.effort > 1 && var10 > 100) {
               var12 = 0;

               for(int var13 = 0; var13 < var10; ++var13) {
                  if (var15[var16 + var13] > 127) {
                     ++var12;
                  }
               }

               if (var12 > 100) {
                  var11 = this.tryAlternateEncoding(var7, var12, var15, var16);
               }
            }

            if (var7 < 1) {
               assert !var11;

               assert var10 == 0;
            } else if (var11) {
               this.cp_Utf8_suffix.putInt(0);
               this.cp_Utf8_big_suffix.putInt(var10);
            } else {
               assert var10 != 0;

               this.cp_Utf8_suffix.putInt(var10);

               for(var12 = 0; var12 < var10; ++var12) {
                  char var17 = var15[var16 + var12];
                  this.cp_Utf8_chars.putInt(var17);
               }
            }
         }

         if (this.verbose > 0) {
            var7 = this.cp_Utf8_chars.length();
            var8 = this.cp_Utf8_big_chars.length();
            var16 = var7 + var8;
            Utils.log.info("Utf8string #CHARS=" + var16 + " #PACKEDCHARS=" + var8);
         }

      }
   }

   private boolean tryAlternateEncoding(int var1, int var2, char[] var3, int var4) {
      int var5 = var3.length - var4;
      int[] var6 = new int[var5];

      for(int var7 = 0; var7 < var5; ++var7) {
         var6[var7] = var3[var4 + var7];
      }

      CodingChooser var20 = this.getCodingChooser();
      Coding var8 = this.cp_Utf8_big_chars.regularCoding;
      String var9 = "(Utf8_big_" + var1 + ")";
      int[] var10 = new int[]{0, 0};
      if (this.verbose > 1 || var20.verbose > 1) {
         Utils.log.fine("--- chooseCoding " + var9);
      }

      CodingMethod var13 = var20.choose(var6, var8, var10);
      Coding var14 = this.cp_Utf8_chars.regularCoding;
      if (this.verbose > 1) {
         Utils.log.fine("big string[" + var1 + "] len=" + var5 + " #wide=" + var2 + " size=" + var10[0] + "/z=" + var10[1] + " coding " + var13);
      }

      if (var13 != var14) {
         int var15 = var10[1];
         int[] var16 = var20.computeSize(var14, var6);
         int var17 = var16[1];
         int var18 = Math.max(5, var17 / 1000);
         if (this.verbose > 1) {
            Utils.log.fine("big string[" + var1 + "] normalSize=" + var16[0] + "/z=" + var16[1] + " win=" + (var15 < var17 - var18));
         }

         if (var15 < var17 - var18) {
            BandStructure.IntBand var19 = this.cp_Utf8_big_chars.newIntBand(var9);
            var19.initializeValues(var6);
            return true;
         }
      }

      return false;
   }

   void writeSignatureBands(ConstantPool.Entry[] var1) throws IOException {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         ConstantPool.SignatureEntry var3 = (ConstantPool.SignatureEntry)var1[var2];
         this.cp_Signature_form.putRef(var3.formRef);

         for(int var4 = 0; var4 < var3.classRefs.length; ++var4) {
            this.cp_Signature_classes.putRef(var3.classRefs[var4]);
         }
      }

   }

   void writeMemberRefs(byte var1, ConstantPool.Entry[] var2, BandStructure.CPRefBand var3, BandStructure.CPRefBand var4) throws IOException {
      for(int var5 = 0; var5 < var2.length; ++var5) {
         ConstantPool.MemberEntry var6 = (ConstantPool.MemberEntry)var2[var5];
         var3.putRef(var6.classRef);
         var4.putRef(var6.descRef);
      }

   }

   void writeFiles() throws IOException {
      int var1 = this.pkg.files.size();
      if (var1 != 0) {
         int var2 = this.archiveOptions;
         boolean var3 = testBit(var2, 256);
         boolean var4 = testBit(var2, 64);
         boolean var5 = testBit(var2, 128);
         Iterator var6;
         Package.File var7;
         if (!var5) {
            var6 = this.pkg.files.iterator();

            while(var6.hasNext()) {
               var7 = (Package.File)var6.next();
               if (var7.isClassStub()) {
                  var5 = true;
                  var2 |= 128;
                  this.archiveOptions = var2;
                  break;
               }
            }
         }

         if (var3 || var4 || var5 || !this.pkg.files.isEmpty()) {
            var2 |= 16;
            this.archiveOptions = var2;
         }

         var6 = this.pkg.files.iterator();

         while(var6.hasNext()) {
            var7 = (Package.File)var6.next();
            this.file_name.putRef(var7.name);
            long var8 = var7.getFileLength();
            this.file_size_lo.putInt((int)var8);
            if (var3) {
               this.file_size_hi.putInt((int)(var8 >>> 32));
            }

            if (var4) {
               this.file_modtime.putInt(var7.modtime - this.pkg.default_modtime);
            }

            if (var5) {
               this.file_options.putInt(var7.options);
            }

            var7.writeTo(this.file_bits.collectorStream());
            if (this.verbose > 1) {
               Utils.log.fine("Wrote " + var8 + " bytes of " + var7.name.stringValue());
            }
         }

         if (this.verbose > 0) {
            Utils.log.info("Wrote " + var1 + " resource files");
         }

      }
   }

   void collectAttributeLayouts() {
      this.maxFlags = new int[4];
      this.allLayouts = new FixedList(4);

      int var1;
      for(var1 = 0; var1 < 4; ++var1) {
         this.allLayouts.set(var1, new HashMap());
      }

      Iterator var18 = this.pkg.classes.iterator();

      while(var18.hasNext()) {
         Package.Class var2 = (Package.Class)var18.next();
         this.visitAttributeLayoutsIn(0, var2);
         Iterator var3 = var2.getFields().iterator();

         while(var3.hasNext()) {
            Package.Class.Field var4 = (Package.Class.Field)var3.next();
            this.visitAttributeLayoutsIn(1, var4);
         }

         var3 = var2.getMethods().iterator();

         while(var3.hasNext()) {
            Package.Class.Method var22 = (Package.Class.Method)var3.next();
            this.visitAttributeLayoutsIn(2, var22);
            if (var22.code != null) {
               this.visitAttributeLayoutsIn(3, var22.code);
            }
         }
      }

      for(var1 = 0; var1 < 4; ++var1) {
         int var19 = ((Map)this.allLayouts.get(var1)).size();
         boolean var21 = this.haveFlagsHi(var1);
         if (var19 >= 24) {
            int var5 = 1 << 9 + var1;
            this.archiveOptions |= var5;
            var21 = true;
            if (this.verbose > 0) {
               Utils.log.info("Note: Many " + Attribute.contextName(var1) + " attributes forces 63-bit flags");
            }
         }

         if (this.verbose > 1) {
            Utils.log.fine(Attribute.contextName(var1) + ".maxFlags = 0x" + Integer.toHexString(this.maxFlags[var1]));
            Utils.log.fine(Attribute.contextName(var1) + ".#layouts = " + var19);
         }

         assert this.haveFlagsHi(var1) == var21;
      }

      this.initAttrIndexLimit();

      for(var1 = 0; var1 < 4; ++var1) {
         assert (this.attrFlagMask[var1] & (long)this.maxFlags[var1]) == 0L;
      }

      this.backCountTable = new HashMap();
      this.attrCounts = new int[4][];

      for(var1 = 0; var1 < 4; ++var1) {
         long var20 = ~((long)this.maxFlags[var1] | this.attrFlagMask[var1]);

         assert this.attrIndexLimit[var1] > 0;

         assert this.attrIndexLimit[var1] < 64;

         var20 &= (1L << this.attrIndexLimit[var1]) - 1L;
         int var23 = 0;
         Map var24 = (Map)this.allLayouts.get(var1);
         Map.Entry[] var6 = new Map.Entry[var24.size()];
         var24.entrySet().toArray(var6);
         Arrays.sort(var6, new Comparator<Map.Entry<Attribute.Layout, int[]>>() {
            public int compare(Map.Entry<Attribute.Layout, int[]> var1, Map.Entry<Attribute.Layout, int[]> var2) {
               int var3 = -(((int[])var1.getValue())[0] - ((int[])var2.getValue())[0]);
               return var3 != 0 ? var3 : ((Attribute.Layout)var1.getKey()).compareTo((Attribute.Layout)var2.getKey());
            }
         });
         this.attrCounts[var1] = new int[this.attrIndexLimit[var1] + var6.length];

         for(int var7 = 0; var7 < var6.length; ++var7) {
            Map.Entry var8 = var6[var7];
            Attribute.Layout var9 = (Attribute.Layout)var8.getKey();
            int var10 = ((int[])var8.getValue())[0];
            Integer var12 = (Integer)this.attrIndexTable.get(var9);
            int var11;
            if (var12 != null) {
               var11 = var12;
            } else if (var20 == 0L) {
               var11 = this.setAttributeLayoutIndex(var9, -1);
            } else {
               while((var20 & 1L) == 0L) {
                  var20 >>>= 1;
                  ++var23;
               }

               --var20;
               var11 = this.setAttributeLayoutIndex(var9, var23);
            }

            this.attrCounts[var1][var11] = var10;
            Attribute.Layout.Element[] var13 = var9.getCallables();
            int[] var14 = new int[var13.length];

            for(int var15 = 0; var15 < var13.length; ++var15) {
               assert var13[var15].kind == 10;

               if (!var13[var15].flagTest((byte)8)) {
                  var14[var15] = -1;
               }
            }

            this.backCountTable.put(var9, var14);
            if (var12 == null) {
               ConstantPool.Utf8Entry var25 = ConstantPool.getUtf8Entry(var9.name());
               String var16 = var9.layoutForClassVersion(this.getHighestClassVersion());
               ConstantPool.Utf8Entry var17 = ConstantPool.getUtf8Entry(var16);
               this.requiredEntries.add(var25);
               this.requiredEntries.add(var17);
               if (this.verbose > 0) {
                  if (var11 < this.attrIndexLimit[var1]) {
                     Utils.log.info("Using free flag bit 1<<" + var11 + " for " + var10 + " occurrences of " + var9);
                  } else {
                     Utils.log.info("Using overflow index " + var11 + " for " + var10 + " occurrences of " + var9);
                  }
               }
            }
         }
      }

      this.maxFlags = null;
      this.allLayouts = null;
   }

   void visitAttributeLayoutsIn(int var1, Attribute.Holder var2) {
      int[] var10000 = this.maxFlags;
      var10000[var1] |= var2.flags;
      Iterator var3 = var2.getAttributes().iterator();

      while(var3.hasNext()) {
         Attribute var4 = (Attribute)var3.next();
         Attribute.Layout var5 = var4.layout();
         Map var6 = (Map)this.allLayouts.get(var1);
         int[] var7 = (int[])var6.get(var5);
         if (var7 == null) {
            var6.put(var5, var7 = new int[1]);
         }

         if (var7[0] < Integer.MAX_VALUE) {
            int var10002 = var7[0]++;
         }
      }

   }

   void writeAttrDefs() throws IOException {
      ArrayList var1 = new ArrayList();

      int var2;
      for(var2 = 0; var2 < 4; ++var2) {
         int var3 = ((List)this.attrDefs.get(var2)).size();

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2;
            if (var4 < this.attrIndexLimit[var2]) {
               var5 = var2 | var4 + 1 << 2;

               assert var5 < 256;

               if (!testBit(this.attrDefSeen[var2], 1L << var4)) {
                  continue;
               }
            }

            Attribute.Layout var6 = (Attribute.Layout)((List)this.attrDefs.get(var2)).get(var4);
            var1.add(new Object[]{var5, var6});

            assert Integer.valueOf(var4).equals(this.attrIndexTable.get(var6));
         }
      }

      var2 = var1.size();
      Object[][] var23 = new Object[var2][];
      var1.toArray(var23);
      Arrays.sort(var23, new Comparator<Object[]>() {
         public int compare(Object[] var1, Object[] var2) {
            int var3 = ((Comparable)var1[0]).compareTo(var2[0]);
            if (var3 != 0) {
               return var3;
            } else {
               Integer var4 = (Integer)PackageWriter.this.attrIndexTable.get(var1[1]);
               Integer var5 = (Integer)PackageWriter.this.attrIndexTable.get(var2[1]);

               assert var4 != null;

               assert var5 != null;

               return var4.compareTo(var5);
            }
         }
      });
      this.attrDefsWritten = new Attribute.Layout[var2];
      PrintStream var24 = !this.optDumpBands ? null : new PrintStream(getDumpStream(this.attr_definition_headers, ".def"));
      Throwable var25 = null;

      try {
         int[] var26 = Arrays.copyOf((int[])this.attrIndexLimit, 4);

         for(int var7 = 0; var7 < var23.length; ++var7) {
            int var8 = (Integer)var23[var7][0];
            Attribute.Layout var9 = (Attribute.Layout)var23[var7][1];
            this.attrDefsWritten[var7] = var9;

            assert (var8 & 3) == var9.ctype();

            this.attr_definition_headers.putByte(var8);
            this.attr_definition_name.putRef(ConstantPool.getUtf8Entry(var9.name()));
            String var10 = var9.layoutForClassVersion(this.getHighestClassVersion());
            this.attr_definition_layout.putRef(ConstantPool.getUtf8Entry(var10));
            boolean var11 = false;
            if (!$assertionsDisabled) {
               var11 = true;
               if (false) {
                  throw new AssertionError();
               }
            }

            int var12;
            if (var11) {
               var12 = (var8 >> 2) - 1;
               if (var12 < 0) {
                  var12 = var26[var9.ctype()]++;
               }

               int var13 = (Integer)this.attrIndexTable.get(var9);

               assert var12 == var13;
            }

            if (var24 != null) {
               var12 = (var8 >> 2) - 1;
               var24.println(var12 + " " + var9);
            }
         }
      } catch (Throwable var21) {
         var25 = var21;
         throw var21;
      } finally {
         if (var24 != null) {
            if (var25 != null) {
               try {
                  var24.close();
               } catch (Throwable var20) {
                  var25.addSuppressed(var20);
               }
            } else {
               var24.close();
            }
         }

      }

   }

   void writeAttrCounts() throws IOException {
      for(int var1 = 0; var1 < 4; ++var1) {
         BandStructure.MultiBand var2 = this.attrBands[var1];
         BandStructure.IntBand var3 = getAttrBand(var2, 4);
         Attribute.Layout[] var4 = new Attribute.Layout[((List)this.attrDefs.get(var1)).size()];
         ((List)this.attrDefs.get(var1)).toArray(var4);
         boolean var5 = true;

         while(true) {
            for(int var6 = 0; var6 < var4.length; ++var6) {
               Attribute.Layout var7 = var4[var6];
               if (var7 != null && var5 == this.isPredefinedAttr(var1, var6)) {
                  int var8 = this.attrCounts[var1][var6];
                  if (var8 != 0) {
                     int[] var9 = (int[])this.backCountTable.get(var7);

                     for(int var10 = 0; var10 < var9.length; ++var10) {
                        if (var9[var10] >= 0) {
                           int var11 = var9[var10];
                           var9[var10] = -1;
                           var3.putInt(var11);

                           assert var7.getCallables()[var10].flagTest((byte)8);
                        } else {
                           assert !var7.getCallables()[var10].flagTest((byte)8);
                        }
                     }
                  }
               }
            }

            if (!var5) {
               break;
            }

            var5 = false;
         }
      }

   }

   void trimClassAttributes() {
      Iterator var1 = this.pkg.classes.iterator();

      Package.Class var2;
      do {
         if (!var1.hasNext()) {
            return;
         }

         var2 = (Package.Class)var1.next();
         var2.minimizeSourceFile();
      } while($assertionsDisabled || var2.getAttribute(Package.attrBootstrapMethodsEmpty) == null);

      throw new AssertionError();
   }

   void collectInnerClasses() {
      HashMap var1 = new HashMap();
      Iterator var2 = this.pkg.classes.iterator();

      while(true) {
         Package.Class var3;
         do {
            if (!var2.hasNext()) {
               Package.InnerClass[] var7 = new Package.InnerClass[var1.size()];
               var1.values().toArray(var7);
               var1 = null;
               Arrays.sort((Object[])var7);
               this.pkg.setAllInnerClasses(Arrays.asList(var7));
               Iterator var8 = this.pkg.classes.iterator();

               while(var8.hasNext()) {
                  Package.Class var9 = (Package.Class)var8.next();
                  var9.minimizeLocalICs();
               }

               return;
            }

            var3 = (Package.Class)var2.next();
         } while(!var3.hasInnerClasses());

         Iterator var4 = var3.getInnerClasses().iterator();

         while(var4.hasNext()) {
            Package.InnerClass var5 = (Package.InnerClass)var4.next();
            Package.InnerClass var6 = (Package.InnerClass)var1.put(var5.thisClass, var5);
            if (var6 != null && !var6.equals(var5) && var6.predictable) {
               var1.put(var6.thisClass, var6);
            }
         }
      }
   }

   void writeInnerClasses() throws IOException {
      Iterator var1 = this.pkg.getAllInnerClasses().iterator();

      while(var1.hasNext()) {
         Package.InnerClass var2 = (Package.InnerClass)var1.next();
         int var3 = var2.flags;

         assert (var3 & 65536) == 0;

         if (!var2.predictable) {
            var3 |= 65536;
         }

         this.ic_this_class.putRef(var2.thisClass);
         this.ic_flags.putInt(var3);
         if (!var2.predictable) {
            this.ic_outer_class.putRef(var2.outerClass);
            this.ic_name.putRef(var2.name);
         }
      }

   }

   void writeLocalInnerClasses(Package.Class var1) throws IOException {
      List var2 = var1.getInnerClasses();
      this.class_InnerClasses_N.putInt(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Package.InnerClass var4 = (Package.InnerClass)var3.next();
         this.class_InnerClasses_RC.putRef(var4.thisClass);
         if (var4.equals(this.pkg.getGlobalInnerClass(var4.thisClass))) {
            this.class_InnerClasses_F.putInt(0);
         } else {
            int var5 = var4.flags;
            if (var5 == 0) {
               var5 = 65536;
            }

            this.class_InnerClasses_F.putInt(var5);
            this.class_InnerClasses_outer_RCN.putRef(var4.outerClass);
            this.class_InnerClasses_name_RUN.putRef(var4.name);
         }
      }

   }

   void writeClassesAndByteCodes() throws IOException {
      Package.Class[] var1 = new Package.Class[this.pkg.classes.size()];
      this.pkg.classes.toArray(var1);
      if (this.verbose > 0) {
         Utils.log.info("  ...scanning " + var1.length + " classes...");
      }

      int var2 = 0;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         Package.Class var4 = var1[var3];
         if (this.verbose > 1) {
            Utils.log.fine("Scanning " + var4);
         }

         ConstantPool.ClassEntry var5 = var4.thisClass;
         ConstantPool.ClassEntry var6 = var4.superClass;
         ConstantPool.ClassEntry[] var7 = var4.interfaces;

         assert var6 != var5;

         if (var6 == null) {
            var6 = var5;
         }

         this.class_this.putRef(var5);
         this.class_super.putRef(var6);
         this.class_interface_count.putInt(var4.interfaces.length);

         for(int var8 = 0; var8 < var7.length; ++var8) {
            this.class_interface.putRef(var7[var8]);
         }

         this.writeMembers(var4);
         this.writeAttrs(0, var4, var4);
         ++var2;
         if (this.verbose > 0 && var2 % 1000 == 0) {
            Utils.log.info("Have scanned " + var2 + " classes...");
         }
      }

   }

   void writeMembers(Package.Class var1) throws IOException {
      List var2 = var1.getFields();
      this.class_field_count.putInt(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Package.Class.Field var4 = (Package.Class.Field)var3.next();
         this.field_descr.putRef(var4.getDescriptor());
         this.writeAttrs(1, var4, var1);
      }

      List var6 = var1.getMethods();
      this.class_method_count.putInt(var6.size());
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         Package.Class.Method var5 = (Package.Class.Method)var7.next();
         this.method_descr.putRef(var5.getDescriptor());
         this.writeAttrs(2, var5, var1);

         assert var5.code != null == (var5.getAttribute(this.attrCodeEmpty) != null);

         if (var5.code != null) {
            this.writeCodeHeader(var5.code);
            this.writeByteCodes(var5.code);
         }
      }

   }

   void writeCodeHeader(Code var1) throws IOException {
      boolean var2 = testBit(this.archiveOptions, 4);
      int var3 = var1.attributeSize();
      int var4 = shortCodeHeader(var1);
      if (!var2 && var3 > 0) {
         var4 = 0;
      }

      if (this.verbose > 2) {
         int var5 = var1.getMethod().getArgumentSize();
         Utils.log.fine("Code sizes info " + var1.max_stack + " " + var1.max_locals + " " + var1.getHandlerCount() + " " + var5 + " " + var3 + (var4 > 0 ? " SHORT=" + var4 : ""));
      }

      this.code_headers.putByte(var4);
      if (var4 == 0) {
         this.code_max_stack.putInt(var1.getMaxStack());
         this.code_max_na_locals.putInt(var1.getMaxNALocals());
         this.code_handler_count.putInt(var1.getHandlerCount());
      } else {
         assert var2 || var3 == 0;

         assert var1.getHandlerCount() < this.shortCodeHeader_h_limit;
      }

      this.writeCodeHandlers(var1);
      if (var4 == 0 || var2) {
         this.writeAttrs(3, var1, var1.thisClass());
      }

   }

   void writeCodeHandlers(Code var1) throws IOException {
      int var4 = 0;

      for(int var5 = var1.getHandlerCount(); var4 < var5; ++var4) {
         this.code_handler_class_RCN.putRef(var1.handler_class[var4]);
         int var2 = var1.encodeBCI(var1.handler_start[var4]);
         this.code_handler_start_P.putInt(var2);
         int var3 = var1.encodeBCI(var1.handler_end[var4]) - var2;
         this.code_handler_end_PO.putInt(var3);
         var2 += var3;
         var3 = var1.encodeBCI(var1.handler_catch[var4]) - var2;
         this.code_handler_catch_PO.putInt(var3);
      }

   }

   void writeAttrs(int var1, final Attribute.Holder var2, Package.Class var3) throws IOException {
      BandStructure.MultiBand var4 = this.attrBands[var1];
      BandStructure.IntBand var5 = getAttrBand(var4, 0);
      BandStructure.IntBand var6 = getAttrBand(var4, 1);
      boolean var7 = this.haveFlagsHi(var1);

      assert this.attrIndexLimit[var1] == (var7 ? 63 : 32);

      if (var2.attributes == null) {
         var6.putInt(var2.flags);
         if (var7) {
            var5.putInt(0);
         }

      } else {
         if (this.verbose > 3) {
            Utils.log.fine("Transmitting attrs for " + var2 + " flags=" + Integer.toHexString(var2.flags));
         }

         long var8 = this.attrFlagMask[var1];
         long var10 = 0L;
         int var12 = 0;
         Iterator var13 = var2.attributes.iterator();

         while(true) {
            while(var13.hasNext()) {
               Attribute var14 = (Attribute)var13.next();
               Attribute.Layout var15 = var14.layout();
               int var16 = (Integer)this.attrIndexTable.get(var15);

               assert ((List)this.attrDefs.get(var1)).get(var16) == var15;

               if (this.verbose > 3) {
                  Utils.log.fine("add attr @" + var16 + " " + var14 + " in " + var2);
               }

               if (var16 < this.attrIndexLimit[var1] && testBit(var8, 1L << var16)) {
                  if (this.verbose > 3) {
                     Utils.log.fine("Adding flag bit 1<<" + var16 + " in " + Long.toHexString(var8));
                  }

                  assert !testBit((long)var2.flags, 1L << var16);

                  var10 |= 1L << var16;
                  var8 -= 1L << var16;
               } else {
                  var10 |= 65536L;
                  ++var12;
                  if (this.verbose > 3) {
                     Utils.log.fine("Adding overflow attr #" + var12);
                  }

                  BandStructure.IntBand var17 = getAttrBand(var4, 3);
                  var17.putInt(var16);
               }

               if (var15.bandCount == 0) {
                  if (var15 == this.attrInnerClassesEmpty) {
                     this.writeLocalInnerClasses((Package.Class)var2);
                  }
               } else {
                  assert var14.fixups == null;

                  final BandStructure.Band[] var21 = (BandStructure.Band[])this.attrBandTable.get(var15);

                  assert var21 != null;

                  assert var21.length == var15.bandCount;

                  final int[] var18 = (int[])this.backCountTable.get(var15);

                  assert var18 != null;

                  assert var18.length == var15.getCallables().length;

                  if (this.verbose > 2) {
                     Utils.log.fine("writing " + var14 + " in " + var2);
                  }

                  boolean var19 = var1 == 1 && var15 == this.attrConstantValue;
                  if (var19) {
                     this.setConstantValueIndex((Package.Class.Field)var2);
                  }

                  var14.parse(var3, var14.bytes(), 0, var14.size(), new Attribute.ValueStream() {
                     public void putInt(int var1, int var2x) {
                        ((BandStructure.IntBand)var21[var1]).putInt(var2x);
                     }

                     public void putRef(int var1, ConstantPool.Entry var2x) {
                        ((BandStructure.CPRefBand)var21[var1]).putRef(var2x);
                     }

                     public int encodeBCI(int var1) {
                        Code var2x = (Code)var2;
                        return var2x.encodeBCI(var1);
                     }

                     public void noteBackCall(int var1) {
                        assert var18[var1] >= 0;

                        int var10002 = var18[var1]++;
                     }
                  });
                  if (var19) {
                     this.setConstantValueIndex((Package.Class.Field)null);
                  }
               }
            }

            if (var12 > 0) {
               BandStructure.IntBand var20 = getAttrBand(var4, 2);
               var20.putInt(var12);
            }

            var6.putInt(var2.flags | (int)var10);
            if (var7) {
               var5.putInt((int)(var10 >>> 32));
            } else {
               assert var10 >>> 32 == 0L;
            }

            assert ((long)var2.flags & var10) == 0L : var2 + ".flags=" + Integer.toHexString(var2.flags) + "^" + Long.toHexString(var10);

            return;
         }
      }
   }

   private void beginCode(Code var1) {
      assert this.curCode == null;

      this.curCode = var1;
      this.curClass = var1.m.thisClass();
      this.curCPMap = var1.getCPMap();
   }

   private void endCode() {
      this.curCode = null;
      this.curClass = null;
      this.curCPMap = null;
   }

   private int initOpVariant(Instruction var1, ConstantPool.Entry var2) {
      if (var1.getBC() != 183) {
         return -1;
      } else {
         ConstantPool.MemberEntry var3 = (ConstantPool.MemberEntry)var1.getCPRef(this.curCPMap);
         if (!"<init>".equals(var3.descRef.nameRef.stringValue())) {
            return -1;
         } else {
            ConstantPool.ClassEntry var4 = var3.classRef;
            if (var4 == this.curClass.thisClass) {
               return 230;
            } else if (var4 == this.curClass.superClass) {
               return 231;
            } else {
               return var4 == var2 ? 232 : -1;
            }
         }
      }
   }

   private int selfOpVariant(Instruction var1) {
      int var2 = var1.getBC();
      if (var2 >= 178 && var2 <= 184) {
         ConstantPool.MemberEntry var3 = (ConstantPool.MemberEntry)var1.getCPRef(this.curCPMap);
         if ((var2 == 183 || var2 == 184) && var3.tagEquals(11)) {
            return -1;
         } else {
            ConstantPool.ClassEntry var4 = var3.classRef;
            int var5 = 202 + (var2 - 178);
            if (var4 == this.curClass.thisClass) {
               return var5;
            } else {
               return var4 == this.curClass.superClass ? var5 + 14 : -1;
            }
         }
      } else {
         return -1;
      }
   }

   void writeByteCodes(Code var1) throws IOException {
      this.beginCode(var1);
      ConstantPool.IndexGroup var2 = this.pkg.cp;
      boolean var3 = false;
      ConstantPool.Entry var4 = null;

      int var10002;
      for(Instruction var5 = var1.instructionAt(0); var5 != null; var5 = var5.next()) {
         if (this.verbose > 3) {
            Utils.log.fine(var5.toString());
         }

         if (var5.isNonstandard()) {
            String var15 = var1.getMethod() + " contains an unrecognized bytecode " + var5 + "; please use the pass-file option on this class.";
            Utils.log.warning(var15);
            throw new IOException(var15);
         }

         if (var5.isWide()) {
            if (this.verbose > 1) {
               Utils.log.fine("_wide opcode in " + var1);
               Utils.log.fine(var5.toString());
            }

            this.bc_codes.putByte(196);
            var10002 = this.codeHist[196]++;
         }

         int var6 = var5.getBC();
         if (var6 == 42) {
            Instruction var7 = var1.instructionAt(var5.getNextPC());
            if (this.selfOpVariant(var7) >= 0) {
               var3 = true;
               continue;
            }
         }

         int var16 = this.initOpVariant(var5, var4);
         int var18;
         if (var16 >= 0) {
            if (var3) {
               this.bc_codes.putByte(42);
               var10002 = this.codeHist[42]++;
               var3 = false;
            }

            this.bc_codes.putByte(var16);
            var10002 = this.codeHist[var16]++;
            ConstantPool.MemberEntry var17 = (ConstantPool.MemberEntry)var5.getCPRef(this.curCPMap);
            var18 = var2.getOverloadingIndex(var17);
            this.bc_initref.putInt(var18);
         } else {
            int var8 = this.selfOpVariant(var5);
            if (var8 >= 0) {
               boolean var19 = Instruction.isFieldOp(var6);
               boolean var21 = var8 >= 216;
               boolean var25 = var3;
               var3 = false;
               if (var25) {
                  var8 += 7;
               }

               this.bc_codes.putByte(var8);
               var10002 = this.codeHist[var8]++;
               ConstantPool.MemberEntry var26 = (ConstantPool.MemberEntry)var5.getCPRef(this.curCPMap);
               BandStructure.CPRefBand var24 = this.selfOpRefBand(var8);
               ConstantPool.Index var14 = var2.getMemberIndex(var26.tag, var26.classRef);
               var24.putRef(var26, var14);
            } else {
               assert !var3;

               var10002 = this.codeHist[var6]++;
               int var11;
               int var12;
               int var13;
               switch(var6) {
               case 170:
               case 171:
                  this.bc_codes.putByte(var6);
                  Instruction.Switch var9 = (Instruction.Switch)var5;
                  int var10 = var9.getAlignedPC();
                  var11 = var9.getNextPC();
                  var12 = var9.getCaseCount();
                  this.bc_case_count.putInt(var12);
                  this.putLabel(this.bc_label, var1, var5.getPC(), var9.getDefaultLabel());

                  for(var13 = 0; var13 < var12; ++var13) {
                     this.putLabel(this.bc_label, var1, var5.getPC(), var9.getCaseLabel(var13));
                  }

                  if (var6 == 170) {
                     this.bc_case_value.putInt(var9.getCaseValue(0));
                  } else {
                     for(var13 = 0; var13 < var12; ++var13) {
                        this.bc_case_value.putInt(var9.getCaseValue(var13));
                     }
                  }
                  break;
               default:
                  var18 = var5.getBranchLabel();
                  if (var18 >= 0) {
                     this.bc_codes.putByte(var6);
                     this.putLabel(this.bc_label, var1, var5.getPC(), var18);
                  } else {
                     ConstantPool.Entry var20 = var5.getCPRef(this.curCPMap);
                     if (var20 != null) {
                        if (var6 == 187) {
                           var4 = var20;
                        }

                        if (var6 == 18) {
                           var10002 = this.ldcHist[var20.tag]++;
                        }

                        BandStructure.CPRefBand var22;
                        var12 = var6;
                        label212:
                        switch(var5.getCPTag()) {
                        case 7:
                           if (var20 == this.curClass.thisClass) {
                              var20 = null;
                           }

                           var22 = this.bc_classref;
                           break;
                        case 9:
                           var22 = this.bc_fieldref;
                           break;
                        case 10:
                           if (var20.tagEquals(11)) {
                              if (var6 == 183) {
                                 var12 = 242;
                              }

                              if (var6 == 184) {
                                 var12 = 243;
                              }

                              var22 = this.bc_imethodref;
                           } else {
                              var22 = this.bc_methodref;
                           }
                           break;
                        case 11:
                           var22 = this.bc_imethodref;
                           break;
                        case 18:
                           var22 = this.bc_indyref;
                           break;
                        case 51:
                           switch(var20.tag) {
                           case 3:
                              var22 = this.bc_intref;
                              switch(var6) {
                              case 18:
                                 var12 = 234;
                                 break label212;
                              case 19:
                                 var12 = 237;
                                 break label212;
                              default:
                                 assert false;
                                 break label212;
                              }
                           case 4:
                              var22 = this.bc_floatref;
                              switch(var6) {
                              case 18:
                                 var12 = 235;
                                 break label212;
                              case 19:
                                 var12 = 238;
                                 break label212;
                              default:
                                 assert false;
                                 break label212;
                              }
                           case 5:
                              var22 = this.bc_longref;

                              assert var6 == 20;

                              var12 = 20;
                              break label212;
                           case 6:
                              var22 = this.bc_doubleref;

                              assert var6 == 20;

                              var12 = 239;
                              break label212;
                           case 7:
                              var22 = this.bc_classref;
                              switch(var6) {
                              case 18:
                                 var12 = 233;
                                 break label212;
                              case 19:
                                 var12 = 236;
                                 break label212;
                              default:
                                 assert false;
                                 break label212;
                              }
                           case 8:
                              var22 = this.bc_stringref;
                              switch(var6) {
                              case 18:
                                 var12 = 18;
                                 break label212;
                              case 19:
                                 var12 = 19;
                                 break label212;
                              default:
                                 assert false;
                                 break label212;
                              }
                           default:
                              if (this.getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                                 throw new IOException("bad class file major version for Java 7 ldc");
                              }

                              var22 = this.bc_loadablevalueref;
                              switch(var6) {
                              case 18:
                                 var12 = 240;
                                 break label212;
                              case 19:
                                 var12 = 241;
                                 break label212;
                              default:
                                 assert false;
                                 break label212;
                              }
                           }
                        default:
                           var22 = null;

                           assert false;
                        }

                        if (var20 != null && var22.index != null && !var22.index.contains(var20)) {
                           String var23 = var1.getMethod() + " contains a bytecode " + var5 + " with an unsupported constant reference; please use the pass-file option on this class.";
                           Utils.log.warning(var23);
                           throw new IOException(var23);
                        }

                        this.bc_codes.putByte(var12);
                        var22.putRef(var20);
                        if (var6 == 197) {
                           assert var5.getConstant() == var1.getByte(var5.getPC() + 3);

                           this.bc_byte.putByte(255 & var5.getConstant());
                        } else if (var6 == 185) {
                           assert var5.getLength() == 5;

                           assert var5.getConstant() == 1 + ((ConstantPool.MemberEntry)var20).descRef.typeRef.computeSize(true) << 8;
                        } else if (var6 == 186) {
                           if (this.getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                              throw new IOException("bad class major version for Java 7 invokedynamic");
                           }

                           assert var5.getLength() == 5;

                           assert var5.getConstant() == 0;
                        } else {
                           assert var5.getLength() == (var6 == 18 ? 2 : 3);
                        }
                     } else {
                        var11 = var5.getLocalSlot();
                        if (var11 >= 0) {
                           this.bc_codes.putByte(var6);
                           this.bc_local.putInt(var11);
                           var12 = var5.getConstant();
                           if (var6 == 132) {
                              if (!var5.isWide()) {
                                 this.bc_byte.putByte(255 & var12);
                              } else {
                                 this.bc_short.putInt('\uffff' & var12);
                              }
                           } else {
                              assert var12 == 0;
                           }
                        } else {
                           this.bc_codes.putByte(var6);
                           var12 = var5.getPC() + 1;
                           var13 = var5.getNextPC();
                           if (var12 < var13) {
                              switch(var6) {
                              case 16:
                                 this.bc_byte.putByte(255 & var5.getConstant());
                                 break;
                              case 17:
                                 this.bc_short.putInt('\uffff' & var5.getConstant());
                                 break;
                              case 188:
                                 this.bc_byte.putByte(255 & var5.getConstant());
                                 break;
                              default:
                                 assert false;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      this.bc_codes.putByte(255);
      ++this.bc_codes.elementCountForDebug;
      var10002 = this.codeHist[255]++;
      this.endCode();
   }

   void printCodeHist() {
      assert this.verbose > 0;

      String[] var1 = new String[this.codeHist.length];
      int var2 = 0;

      int var3;
      for(var3 = 0; var3 < this.codeHist.length; ++var3) {
         var2 += this.codeHist[var3];
      }

      for(var3 = 0; var3 < this.codeHist.length; ++var3) {
         if (this.codeHist[var3] == 0) {
            var1[var3] = "";
         } else {
            String var4 = Instruction.byteName(var3);
            String var5 = "" + this.codeHist[var3];
            var5 = "         ".substring(var5.length()) + var5;

            String var6;
            for(var6 = "" + this.codeHist[var3] * 10000 / var2; var6.length() < 4; var6 = "0" + var6) {
            }

            var6 = var6.substring(0, var6.length() - 2) + "." + var6.substring(var6.length() - 2);
            var1[var3] = var5 + "  " + var6 + "%  " + var4;
         }
      }

      Arrays.sort((Object[])var1);
      System.out.println("Bytecode histogram [" + var2 + "]");
      var3 = var1.length;

      while(true) {
         --var3;
         if (var3 < 0) {
            for(var3 = 0; var3 < this.ldcHist.length; ++var3) {
               int var7 = this.ldcHist[var3];
               if (var7 != 0) {
                  System.out.println("ldc " + ConstantPool.tagName(var3) + " " + var7);
               }
            }

            return;
         }

         if (!"".equals(var1[var3])) {
            System.out.println(var1[var3]);
         }
      }
   }
}
