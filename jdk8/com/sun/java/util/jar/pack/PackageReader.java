package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class PackageReader extends BandStructure {
   Package pkg;
   byte[] bytes;
   PackageReader.LimitedBuffer in;
   Package.Version packageVersion;
   int[] tagCount = new int[19];
   int numFiles;
   int numAttrDefs;
   int numInnerClasses;
   int numClasses;
   static final int MAGIC_BYTES = 4;
   Map<ConstantPool.Utf8Entry, ConstantPool.SignatureEntry> utf8Signatures;
   static final int NO_FLAGS_YET = 0;
   Comparator<ConstantPool.Entry> entryOutputOrder = new Comparator<ConstantPool.Entry>() {
      public int compare(ConstantPool.Entry var1, ConstantPool.Entry var2) {
         int var3 = PackageReader.this.getOutputIndex(var1);
         int var4 = PackageReader.this.getOutputIndex(var2);
         if (var3 >= 0 && var4 >= 0) {
            return var3 - var4;
         } else if (var3 == var4) {
            return var1.compareTo(var2);
         } else {
            return var3 >= 0 ? -1 : 1;
         }
      }
   };
   Code[] allCodes;
   List<Code> codesWithFlags;
   Map<Package.Class, Set<ConstantPool.Entry>> ldcRefMap = new HashMap();

   PackageReader(Package var1, InputStream var2) throws IOException {
      this.pkg = var1;
      this.in = new PackageReader.LimitedBuffer(var2);
   }

   void read() throws IOException {
      boolean var1 = false;

      try {
         this.readFileHeader();
         this.readBandHeaders();
         this.readConstantPool();
         this.readAttrDefs();
         this.readInnerClasses();
         Package.Class[] var2 = this.readClasses();
         this.readByteCodes();
         this.readFiles();

         assert this.archiveSize1 == 0L || this.in.atLimit();

         assert this.archiveSize1 == 0L || this.in.getBytesServed() == this.archiveSize0 + this.archiveSize1;

         this.all_bands.doneDisbursing();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.reconstructClass(var2[var3]);
         }

         var1 = true;
      } catch (Exception var4) {
         Utils.log.warning("Error on input: " + var4, var4);
         if (this.verbose > 0) {
            Utils.log.info("Stream offsets: served=" + this.in.getBytesServed() + " buffered=" + this.in.buffered + " limit=" + this.in.limit);
         }

         if (var4 instanceof IOException) {
            throw (IOException)var4;
         } else if (var4 instanceof RuntimeException) {
            throw (RuntimeException)var4;
         } else {
            throw new Error("error unpacking", var4);
         }
      }
   }

   void readFileHeader() throws IOException {
      this.readArchiveMagic();
      this.readArchiveHeader();
   }

   private int getMagicInt32() throws IOException {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         var1 <<= 8;
         var1 |= this.archive_magic.getByte() & 255;
      }

      return var1;
   }

   void readArchiveMagic() throws IOException {
      this.in.setReadLimit(19L);
      this.archive_magic.expectLength(4);
      this.archive_magic.readFrom(this.in);
      int var1 = this.getMagicInt32();
      this.pkg.getClass();
      if (-889270259 != var1) {
         StringBuilder var10002 = (new StringBuilder()).append("Unexpected package magic number: got ").append(var1).append("; expected ");
         this.pkg.getClass();
         throw new IOException(var10002.append(-889270259).toString());
      } else {
         this.archive_magic.doneDisbursing();
      }
   }

   void checkArchiveVersion() throws IOException {
      Package.Version var1 = null;
      Package.Version[] var2 = new Package.Version[]{Constants.JAVA8_PACKAGE_VERSION, Constants.JAVA7_PACKAGE_VERSION, Constants.JAVA6_PACKAGE_VERSION, Constants.JAVA5_PACKAGE_VERSION};
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Package.Version var5 = var2[var4];
         if (this.packageVersion.equals(var5)) {
            var1 = var5;
            break;
         }
      }

      if (var1 == null) {
         String var6 = Constants.JAVA8_PACKAGE_VERSION.toString() + "OR" + Constants.JAVA7_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA6_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA5_PACKAGE_VERSION.toString();
         throw new IOException("Unexpected package minor version: got " + this.packageVersion.toString() + "; expected " + var6);
      }
   }

   void readArchiveHeader() throws IOException {
      this.archive_header_0.expectLength(3);
      this.archive_header_0.readFrom(this.in);
      int var1 = this.archive_header_0.getInt();
      int var2 = this.archive_header_0.getInt();
      this.packageVersion = Package.Version.of(var2, var1);
      this.checkArchiveVersion();
      this.initHighestClassVersion(Constants.JAVA7_MAX_CLASS_VERSION);
      this.archiveOptions = this.archive_header_0.getInt();
      this.archive_header_0.doneDisbursing();
      boolean var3 = testBit(this.archiveOptions, 1);
      boolean var4 = testBit(this.archiveOptions, 16);
      boolean var5 = testBit(this.archiveOptions, 2);
      boolean var6 = testBit(this.archiveOptions, 8);
      this.initAttrIndexLimit();
      this.archive_header_S.expectLength(var4 ? 2 : 0);
      this.archive_header_S.readFrom(this.in);
      if (var4) {
         long var7 = (long)this.archive_header_S.getInt();
         long var9 = (long)this.archive_header_S.getInt();
         this.archiveSize1 = (var7 << 32) + (var9 << 32 >>> 32);
         this.in.setReadLimit(this.archiveSize1);
      } else {
         this.archiveSize1 = 0L;
         this.in.setReadLimit(-1L);
      }

      this.archive_header_S.doneDisbursing();
      this.archiveSize0 = this.in.getBytesServed();
      int var13 = 10;
      if (var4) {
         var13 += 5;
      }

      if (var3) {
         var13 += 2;
      }

      if (var5) {
         var13 += 4;
      }

      if (var6) {
         var13 += 4;
      }

      this.archive_header_1.expectLength(var13);
      this.archive_header_1.readFrom(this.in);
      if (var4) {
         this.archiveNextCount = this.archive_header_1.getInt();
         this.pkg.default_modtime = this.archive_header_1.getInt();
         this.numFiles = this.archive_header_1.getInt();
      } else {
         this.archiveNextCount = 0;
         this.numFiles = 0;
      }

      if (var3) {
         this.band_headers.expectLength(this.archive_header_1.getInt());
         this.numAttrDefs = this.archive_header_1.getInt();
      } else {
         this.band_headers.expectLength(0);
         this.numAttrDefs = 0;
      }

      this.readConstantPoolCounts(var5, var6);
      this.numInnerClasses = this.archive_header_1.getInt();
      short var11 = (short)this.archive_header_1.getInt();
      short var12 = (short)this.archive_header_1.getInt();
      this.pkg.defaultClassVersion = Package.Version.of(var12, var11);
      this.numClasses = this.archive_header_1.getInt();
      this.archive_header_1.doneDisbursing();
      if (testBit(this.archiveOptions, 32)) {
         Package var10000 = this.pkg;
         var10000.default_options |= 1;
      }

   }

   void readBandHeaders() throws IOException {
      this.band_headers.readFrom(this.in);
      this.bandHeaderBytePos = 1;
      this.bandHeaderBytes = new byte[this.bandHeaderBytePos + this.band_headers.length()];

      for(int var1 = this.bandHeaderBytePos; var1 < this.bandHeaderBytes.length; ++var1) {
         this.bandHeaderBytes[var1] = (byte)this.band_headers.getByte();
      }

      this.band_headers.doneDisbursing();
   }

   void readConstantPoolCounts(boolean var1, boolean var2) throws IOException {
      for(int var3 = 0; var3 < ConstantPool.TAGS_IN_ORDER.length; ++var3) {
         byte var4 = ConstantPool.TAGS_IN_ORDER[var3];
         if (!var1) {
            switch(var4) {
            case 3:
            case 4:
            case 5:
            case 6:
               continue;
            }
         }

         if (!var2) {
            switch(var4) {
            case 15:
            case 16:
            case 17:
            case 18:
               continue;
            }
         }

         this.tagCount[var4] = this.archive_header_1.getInt();
      }

   }

   protected ConstantPool.Index getCPIndex(byte var1) {
      return this.pkg.cp.getIndexByTag(var1);
   }

   ConstantPool.Index initCPIndex(byte var1, ConstantPool.Entry[] var2) {
      if (this.verbose > 3) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            Utils.log.fine("cp.add " + var2[var3]);
         }
      }

      ConstantPool.Index var4 = ConstantPool.makeIndex(ConstantPool.tagName(var1), var2);
      if (this.verbose > 1) {
         Utils.log.fine("Read " + var4);
      }

      this.pkg.cp.initIndexByTag(var1, var4);
      return var4;
   }

   void checkLegacy(String var1) {
      if (this.packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION)) {
         throw new RuntimeException("unexpected band " + var1);
      }
   }

   void readConstantPool() throws IOException {
      if (this.verbose > 0) {
         Utils.log.info("Reading CP");
      }

      for(int var1 = 0; var1 < ConstantPool.TAGS_IN_ORDER.length; ++var1) {
         byte var2 = ConstantPool.TAGS_IN_ORDER[var1];
         int var3 = this.tagCount[var2];
         ConstantPool.Entry[] var4 = new ConstantPool.Entry[var3];
         if (this.verbose > 0) {
            Utils.log.info("Reading " + var4.length + " " + ConstantPool.tagName(var2) + " entries...");
         }

         int var5;
         int var43;
         long var49;
         long var52;
         long var55;
         switch(var2) {
         case 1:
            this.readUtf8Bands(var4);
            break;
         case 2:
         case 14:
         default:
            throw new AssertionError("unexpected CP tag in package");
         case 3:
            this.cp_Int.expectLength(var4.length);
            this.cp_Int.readFrom(this.in);

            for(var5 = 0; var5 < var4.length; ++var5) {
               var43 = this.cp_Int.getInt();
               var4[var5] = ConstantPool.getLiteralEntry(var43);
            }

            this.cp_Int.doneDisbursing();
            break;
         case 4:
            this.cp_Float.expectLength(var4.length);
            this.cp_Float.readFrom(this.in);

            for(var5 = 0; var5 < var4.length; ++var5) {
               var43 = this.cp_Float.getInt();
               float var51 = Float.intBitsToFloat(var43);
               var4[var5] = ConstantPool.getLiteralEntry(var51);
            }

            this.cp_Float.doneDisbursing();
            break;
         case 5:
            this.cp_Long_hi.expectLength(var4.length);
            this.cp_Long_hi.readFrom(this.in);
            this.cp_Long_lo.expectLength(var4.length);
            this.cp_Long_lo.readFrom(this.in);

            for(var5 = 0; var5 < var4.length; ++var5) {
               var49 = (long)this.cp_Long_hi.getInt();
               var52 = (long)this.cp_Long_lo.getInt();
               var55 = (var49 << 32) + (var52 << 32 >>> 32);
               var4[var5] = ConstantPool.getLiteralEntry(var55);
            }

            this.cp_Long_hi.doneDisbursing();
            this.cp_Long_lo.doneDisbursing();
            break;
         case 6:
            this.cp_Double_hi.expectLength(var4.length);
            this.cp_Double_hi.readFrom(this.in);
            this.cp_Double_lo.expectLength(var4.length);
            this.cp_Double_lo.readFrom(this.in);

            for(var5 = 0; var5 < var4.length; ++var5) {
               var49 = (long)this.cp_Double_hi.getInt();
               var52 = (long)this.cp_Double_lo.getInt();
               var55 = (var49 << 32) + (var52 << 32 >>> 32);
               double var12 = Double.longBitsToDouble(var55);
               var4[var5] = ConstantPool.getLiteralEntry(var12);
            }

            this.cp_Double_hi.doneDisbursing();
            this.cp_Double_lo.doneDisbursing();
            break;
         case 7:
            this.cp_Class.expectLength(var4.length);
            this.cp_Class.readFrom(this.in);
            this.cp_Class.setIndex(this.getCPIndex((byte)1));

            for(var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = ConstantPool.getClassEntry(this.cp_Class.getRef().stringValue());
            }

            this.cp_Class.doneDisbursing();
            break;
         case 8:
            this.cp_String.expectLength(var4.length);
            this.cp_String.readFrom(this.in);
            this.cp_String.setIndex(this.getCPIndex((byte)1));

            for(var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = ConstantPool.getLiteralEntry(this.cp_String.getRef().stringValue());
            }

            this.cp_String.doneDisbursing();
            break;
         case 9:
            this.readMemberRefs(var2, var4, this.cp_Field_class, this.cp_Field_desc);
            break;
         case 10:
            this.readMemberRefs(var2, var4, this.cp_Method_class, this.cp_Method_desc);
            break;
         case 11:
            this.readMemberRefs(var2, var4, this.cp_Imethod_class, this.cp_Imethod_desc);
            break;
         case 12:
            this.cp_Descr_name.expectLength(var4.length);
            this.cp_Descr_name.readFrom(this.in);
            this.cp_Descr_name.setIndex(this.getCPIndex((byte)1));
            this.cp_Descr_type.expectLength(var4.length);
            this.cp_Descr_type.readFrom(this.in);
            this.cp_Descr_type.setIndex(this.getCPIndex((byte)13));

            for(var5 = 0; var5 < var4.length; ++var5) {
               ConstantPool.Entry var47 = this.cp_Descr_name.getRef();
               ConstantPool.Entry var50 = this.cp_Descr_type.getRef();
               var4[var5] = ConstantPool.getDescriptorEntry((ConstantPool.Utf8Entry)var47, (ConstantPool.SignatureEntry)var50);
            }

            this.cp_Descr_name.doneDisbursing();
            this.cp_Descr_type.doneDisbursing();
            break;
         case 13:
            this.readSignatureBands(var4);
            break;
         case 15:
            if (var4.length > 0) {
               this.checkLegacy(this.cp_MethodHandle_refkind.name());
            }

            this.cp_MethodHandle_refkind.expectLength(var4.length);
            this.cp_MethodHandle_refkind.readFrom(this.in);
            this.cp_MethodHandle_member.expectLength(var4.length);
            this.cp_MethodHandle_member.readFrom(this.in);
            this.cp_MethodHandle_member.setIndex(this.getCPIndex((byte)52));

            for(var5 = 0; var5 < var4.length; ++var5) {
               byte var45 = (byte)this.cp_MethodHandle_refkind.getInt();
               ConstantPool.MemberEntry var48 = (ConstantPool.MemberEntry)this.cp_MethodHandle_member.getRef();
               var4[var5] = ConstantPool.getMethodHandleEntry(var45, var48);
            }

            this.cp_MethodHandle_refkind.doneDisbursing();
            this.cp_MethodHandle_member.doneDisbursing();
            break;
         case 16:
            if (var4.length > 0) {
               this.checkLegacy(this.cp_MethodType.name());
            }

            this.cp_MethodType.expectLength(var4.length);
            this.cp_MethodType.readFrom(this.in);
            this.cp_MethodType.setIndex(this.getCPIndex((byte)13));

            for(var5 = 0; var5 < var4.length; ++var5) {
               ConstantPool.SignatureEntry var44 = (ConstantPool.SignatureEntry)this.cp_MethodType.getRef();
               var4[var5] = ConstantPool.getMethodTypeEntry(var44);
            }

            this.cp_MethodType.doneDisbursing();
            break;
         case 17:
            if (var4.length > 0) {
               this.checkLegacy(this.cp_BootstrapMethod_ref.name());
            }

            this.cp_BootstrapMethod_ref.expectLength(var4.length);
            this.cp_BootstrapMethod_ref.readFrom(this.in);
            this.cp_BootstrapMethod_ref.setIndex(this.getCPIndex((byte)15));
            this.cp_BootstrapMethod_arg_count.expectLength(var4.length);
            this.cp_BootstrapMethod_arg_count.readFrom(this.in);
            var5 = this.cp_BootstrapMethod_arg_count.getIntTotal();
            this.cp_BootstrapMethod_arg.expectLength(var5);
            this.cp_BootstrapMethod_arg.readFrom(this.in);
            this.cp_BootstrapMethod_arg.setIndex(this.getCPIndex((byte)51));

            for(var43 = 0; var43 < var4.length; ++var43) {
               ConstantPool.MethodHandleEntry var46 = (ConstantPool.MethodHandleEntry)this.cp_BootstrapMethod_ref.getRef();
               int var8 = this.cp_BootstrapMethod_arg_count.getInt();
               ConstantPool.Entry[] var9 = new ConstantPool.Entry[var8];

               for(int var10 = 0; var10 < var8; ++var10) {
                  var9[var10] = this.cp_BootstrapMethod_arg.getRef();
               }

               var4[var43] = ConstantPool.getBootstrapMethodEntry(var46, var9);
            }

            this.cp_BootstrapMethod_ref.doneDisbursing();
            this.cp_BootstrapMethod_arg_count.doneDisbursing();
            this.cp_BootstrapMethod_arg.doneDisbursing();
            break;
         case 18:
            if (var4.length > 0) {
               this.checkLegacy(this.cp_InvokeDynamic_spec.name());
            }

            this.cp_InvokeDynamic_spec.expectLength(var4.length);
            this.cp_InvokeDynamic_spec.readFrom(this.in);
            this.cp_InvokeDynamic_spec.setIndex(this.getCPIndex((byte)17));
            this.cp_InvokeDynamic_desc.expectLength(var4.length);
            this.cp_InvokeDynamic_desc.readFrom(this.in);
            this.cp_InvokeDynamic_desc.setIndex(this.getCPIndex((byte)12));

            for(var5 = 0; var5 < var4.length; ++var5) {
               ConstantPool.BootstrapMethodEntry var6 = (ConstantPool.BootstrapMethodEntry)this.cp_InvokeDynamic_spec.getRef();
               ConstantPool.DescriptorEntry var7 = (ConstantPool.DescriptorEntry)this.cp_InvokeDynamic_desc.getRef();
               var4[var5] = ConstantPool.getInvokeDynamicEntry(var6, var7);
            }

            this.cp_InvokeDynamic_spec.doneDisbursing();
            this.cp_InvokeDynamic_desc.doneDisbursing();
         }

         ConstantPool.Index var56 = this.initCPIndex(var2, var4);
         if (this.optDumpBands) {
            PrintStream var54 = new PrintStream(getDumpStream(var56, ".idx"));
            Throwable var53 = null;

            try {
               printArrayTo(var54, var56.cpMap, 0, var56.cpMap.length);
            } catch (Throwable var36) {
               var53 = var36;
               throw var36;
            } finally {
               if (var54 != null) {
                  if (var53 != null) {
                     try {
                        var54.close();
                     } catch (Throwable var33) {
                        var53.addSuppressed(var33);
                     }
                  } else {
                     var54.close();
                  }
               }

            }
         }
      }

      this.cp_bands.doneDisbursing();
      if (this.optDumpBands || this.verbose > 1) {
         for(byte var39 = 50; var39 < 54; ++var39) {
            ConstantPool.Index var40 = this.pkg.cp.getIndexByTag(var39);
            if (var40 != null && !var40.isEmpty()) {
               ConstantPool.Entry[] var41 = var40.cpMap;
               if (this.verbose > 1) {
                  Utils.log.info("Index group " + ConstantPool.tagName(var39) + " contains " + var41.length + " entries.");
               }

               if (this.optDumpBands) {
                  PrintStream var42 = new PrintStream(getDumpStream(var40.debugName, var39, ".gidx", var40));
                  Throwable var57 = null;

                  try {
                     printArrayTo(var42, var41, 0, var41.length, true);
                  } catch (Throwable var35) {
                     var57 = var35;
                     throw var35;
                  } finally {
                     if (var42 != null) {
                        if (var57 != null) {
                           try {
                              var42.close();
                           } catch (Throwable var34) {
                              var57.addSuppressed(var34);
                           }
                        } else {
                           var42.close();
                        }
                     }

                  }
               }
            }
         }
      }

      this.setBandIndexes();
   }

   void readUtf8Bands(ConstantPool.Entry[] var1) throws IOException {
      int var2 = var1.length;
      if (var2 != 0) {
         this.cp_Utf8_prefix.expectLength(Math.max(0, var2 - 2));
         this.cp_Utf8_prefix.readFrom(this.in);
         this.cp_Utf8_suffix.expectLength(Math.max(0, var2 - 1));
         this.cp_Utf8_suffix.readFrom(this.in);
         char[][] var5 = new char[var2][];
         int var6 = 0;
         this.cp_Utf8_chars.expectLength(this.cp_Utf8_suffix.getIntTotal());
         this.cp_Utf8_chars.readFrom(this.in);

         int var7;
         int var8;
         int var9;
         int var10;
         for(var7 = 0; var7 < var2; ++var7) {
            var8 = var7 < 1 ? 0 : this.cp_Utf8_suffix.getInt();
            if (var8 == 0 && var7 >= 1) {
               ++var6;
            } else {
               var5[var7] = new char[var8];

               for(var9 = 0; var9 < var8; ++var9) {
                  var10 = this.cp_Utf8_chars.getInt();

                  assert var10 == (char)var10;

                  var5[var7][var9] = (char)var10;
               }
            }
         }

         this.cp_Utf8_chars.doneDisbursing();
         var7 = 0;
         this.cp_Utf8_big_suffix.expectLength(var6);
         this.cp_Utf8_big_suffix.readFrom(this.in);
         this.cp_Utf8_suffix.resetForSecondPass();

         for(var8 = 0; var8 < var2; ++var8) {
            var9 = var8 < 1 ? 0 : this.cp_Utf8_suffix.getInt();
            var10 = var8 < 2 ? 0 : this.cp_Utf8_prefix.getInt();
            if (var9 == 0 && var8 >= 1) {
               assert var5[var8] == null;

               var9 = this.cp_Utf8_big_suffix.getInt();
            } else {
               assert var5[var8] != null;
            }

            if (var7 < var10 + var9) {
               var7 = var10 + var9;
            }
         }

         char[] var14 = new char[var7];
         this.cp_Utf8_suffix.resetForSecondPass();
         this.cp_Utf8_big_suffix.resetForSecondPass();

         for(var9 = 0; var9 < var2; ++var9) {
            if (var9 >= 1) {
               var10 = this.cp_Utf8_suffix.getInt();
               if (var10 == 0) {
                  var10 = this.cp_Utf8_big_suffix.getInt();
                  var5[var9] = new char[var10];
                  if (var10 != 0) {
                     BandStructure.IntBand var11 = this.cp_Utf8_big_chars.newIntBand("(Utf8_big_" + var9 + ")");
                     var11.expectLength(var10);
                     var11.readFrom(this.in);

                     for(int var12 = 0; var12 < var10; ++var12) {
                        int var13 = var11.getInt();

                        assert var13 == (char)var13;

                        var5[var9][var12] = (char)var13;
                     }

                     var11.doneDisbursing();
                  }
               }
            }
         }

         this.cp_Utf8_big_chars.doneDisbursing();
         this.cp_Utf8_prefix.resetForSecondPass();
         this.cp_Utf8_suffix.resetForSecondPass();
         this.cp_Utf8_big_suffix.resetForSecondPass();

         for(var9 = 0; var9 < var2; ++var9) {
            var10 = var9 < 2 ? 0 : this.cp_Utf8_prefix.getInt();
            int var15 = var9 < 1 ? 0 : this.cp_Utf8_suffix.getInt();
            if (var15 == 0 && var9 >= 1) {
               var15 = this.cp_Utf8_big_suffix.getInt();
            }

            System.arraycopy(var5[var9], 0, var14, var10, var15);
            var1[var9] = ConstantPool.getUtf8Entry(new String(var14, 0, var10 + var15));
         }

         this.cp_Utf8_prefix.doneDisbursing();
         this.cp_Utf8_suffix.doneDisbursing();
         this.cp_Utf8_big_suffix.doneDisbursing();
      }
   }

   void readSignatureBands(ConstantPool.Entry[] var1) throws IOException {
      this.cp_Signature_form.expectLength(var1.length);
      this.cp_Signature_form.readFrom(this.in);
      this.cp_Signature_form.setIndex(this.getCPIndex((byte)1));
      int[] var2 = new int[var1.length];

      int var3;
      ConstantPool.Utf8Entry var4;
      for(var3 = 0; var3 < var1.length; ++var3) {
         var4 = (ConstantPool.Utf8Entry)this.cp_Signature_form.getRef();
         var2[var3] = ConstantPool.countClassParts(var4);
      }

      this.cp_Signature_form.resetForSecondPass();
      this.cp_Signature_classes.expectLength(getIntTotal(var2));
      this.cp_Signature_classes.readFrom(this.in);
      this.cp_Signature_classes.setIndex(this.getCPIndex((byte)7));
      this.utf8Signatures = new HashMap();

      for(var3 = 0; var3 < var1.length; ++var3) {
         var4 = (ConstantPool.Utf8Entry)this.cp_Signature_form.getRef();
         ConstantPool.ClassEntry[] var5 = new ConstantPool.ClassEntry[var2[var3]];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = (ConstantPool.ClassEntry)this.cp_Signature_classes.getRef();
         }

         ConstantPool.SignatureEntry var7 = ConstantPool.getSignatureEntry(var4, var5);
         var1[var3] = var7;
         this.utf8Signatures.put(var7.asUtf8Entry(), var7);
      }

      this.cp_Signature_form.doneDisbursing();
      this.cp_Signature_classes.doneDisbursing();
   }

   void readMemberRefs(byte var1, ConstantPool.Entry[] var2, BandStructure.CPRefBand var3, BandStructure.CPRefBand var4) throws IOException {
      var3.expectLength(var2.length);
      var3.readFrom(this.in);
      var3.setIndex(this.getCPIndex((byte)7));
      var4.expectLength(var2.length);
      var4.readFrom(this.in);
      var4.setIndex(this.getCPIndex((byte)12));

      for(int var5 = 0; var5 < var2.length; ++var5) {
         ConstantPool.ClassEntry var6 = (ConstantPool.ClassEntry)var3.getRef();
         ConstantPool.DescriptorEntry var7 = (ConstantPool.DescriptorEntry)var4.getRef();
         var2[var5] = ConstantPool.getMemberEntry(var1, var6, var7);
      }

      var3.doneDisbursing();
      var4.doneDisbursing();
   }

   void readFiles() throws IOException {
      if (this.verbose > 0) {
         Utils.log.info("  ...building " + this.numFiles + " files...");
      }

      this.file_name.expectLength(this.numFiles);
      this.file_size_lo.expectLength(this.numFiles);
      int var1 = this.archiveOptions;
      boolean var2 = testBit(var1, 256);
      boolean var3 = testBit(var1, 64);
      boolean var4 = testBit(var1, 128);
      if (var2) {
         this.file_size_hi.expectLength(this.numFiles);
      }

      if (var3) {
         this.file_modtime.expectLength(this.numFiles);
      }

      if (var4) {
         this.file_options.expectLength(this.numFiles);
      }

      this.file_name.readFrom(this.in);
      this.file_size_hi.readFrom(this.in);
      this.file_size_lo.readFrom(this.in);
      this.file_modtime.readFrom(this.in);
      this.file_options.readFrom(this.in);
      this.file_bits.setInputStreamFrom(this.in);
      Iterator var5 = this.pkg.getClasses().iterator();
      long var6 = 0L;
      long[] var8 = new long[this.numFiles];

      for(int var9 = 0; var9 < this.numFiles; ++var9) {
         long var10 = (long)this.file_size_lo.getInt() << 32 >>> 32;
         if (var2) {
            var10 += (long)this.file_size_hi.getInt() << 32;
         }

         var8[var9] = var10;
         var6 += var10;
      }

      assert this.in.getReadLimit() == -1L || this.in.getReadLimit() == var6;

      byte[] var18 = new byte[65536];

      for(int var19 = 0; var19 < this.numFiles; ++var19) {
         ConstantPool.Utf8Entry var11 = (ConstantPool.Utf8Entry)this.file_name.getRef();
         long var12 = var8[var19];
         Package.File var14 = this.pkg.new File(var11);
         var14.modtime = this.pkg.default_modtime;
         var14.options = this.pkg.default_options;
         if (var3) {
            var14.modtime += this.file_modtime.getInt();
         }

         if (var4) {
            var14.options |= this.file_options.getInt();
         }

         if (this.verbose > 1) {
            Utils.log.fine("Reading " + var12 + " bytes of " + var11.stringValue());
         }

         int var17;
         for(long var15 = var12; var15 > 0L; var15 -= (long)var17) {
            var17 = var18.length;
            if ((long)var17 > var15) {
               var17 = (int)var15;
            }

            var17 = this.file_bits.getInputStream().read(var18, 0, var17);
            if (var17 < 0) {
               throw new EOFException();
            }

            var14.addBytes(var18, 0, var17);
         }

         this.pkg.addFile(var14);
         if (var14.isClassStub()) {
            assert var14.getFileLength() == 0L;

            Package.Class var21 = (Package.Class)var5.next();
            var21.initFile(var14);
         }
      }

      while(var5.hasNext()) {
         Package.Class var20 = (Package.Class)var5.next();
         var20.initFile((Package.File)null);
         var20.file.modtime = this.pkg.default_modtime;
      }

      this.file_name.doneDisbursing();
      this.file_size_hi.doneDisbursing();
      this.file_size_lo.doneDisbursing();
      this.file_modtime.doneDisbursing();
      this.file_options.doneDisbursing();
      this.file_bits.doneDisbursing();
      this.file_bands.doneDisbursing();
      if (this.archiveSize1 != 0L && !this.in.atLimit()) {
         throw new RuntimeException("Predicted archive_size " + this.archiveSize1 + " != " + (this.in.getBytesServed() - this.archiveSize0));
      }
   }

   void readAttrDefs() throws IOException {
      this.attr_definition_headers.expectLength(this.numAttrDefs);
      this.attr_definition_name.expectLength(this.numAttrDefs);
      this.attr_definition_layout.expectLength(this.numAttrDefs);
      this.attr_definition_headers.readFrom(this.in);
      this.attr_definition_name.readFrom(this.in);
      this.attr_definition_layout.readFrom(this.in);
      PrintStream var1 = !this.optDumpBands ? null : new PrintStream(getDumpStream(this.attr_definition_headers, ".def"));
      Throwable var2 = null;

      try {
         for(int var3 = 0; var3 < this.numAttrDefs; ++var3) {
            int var4 = this.attr_definition_headers.getByte();
            ConstantPool.Utf8Entry var5 = (ConstantPool.Utf8Entry)this.attr_definition_name.getRef();
            ConstantPool.Utf8Entry var6 = (ConstantPool.Utf8Entry)this.attr_definition_layout.getRef();
            int var7 = var4 & 3;
            int var8 = (var4 >> 2) - 1;
            Attribute.Layout var9 = new Attribute.Layout(var7, var5.stringValue(), var6.stringValue());
            String var10 = var9.layoutForClassVersion(this.getHighestClassVersion());
            if (!var10.equals(var9.layout())) {
               throw new IOException("Bad attribute layout in archive: " + var9.layout());
            }

            this.setAttributeLayoutIndex(var9, var8);
            if (var1 != null) {
               var1.println(var8 + " " + var9);
            }
         }
      } catch (Throwable var18) {
         var2 = var18;
         throw var18;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var17) {
                  var2.addSuppressed(var17);
               }
            } else {
               var1.close();
            }
         }

      }

      this.attr_definition_headers.doneDisbursing();
      this.attr_definition_name.doneDisbursing();
      this.attr_definition_layout.doneDisbursing();
      this.makeNewAttributeBands();
      this.attr_definition_bands.doneDisbursing();
   }

   void readInnerClasses() throws IOException {
      this.ic_this_class.expectLength(this.numInnerClasses);
      this.ic_this_class.readFrom(this.in);
      this.ic_flags.expectLength(this.numInnerClasses);
      this.ic_flags.readFrom(this.in);
      int var1 = 0;

      int var3;
      for(int var2 = 0; var2 < this.numInnerClasses; ++var2) {
         var3 = this.ic_flags.getInt();
         boolean var4 = (var3 & 65536) != 0;
         if (var4) {
            ++var1;
         }
      }

      this.ic_outer_class.expectLength(var1);
      this.ic_outer_class.readFrom(this.in);
      this.ic_name.expectLength(var1);
      this.ic_name.readFrom(this.in);
      this.ic_flags.resetForSecondPass();
      ArrayList var13 = new ArrayList(this.numInnerClasses);

      for(var3 = 0; var3 < this.numInnerClasses; ++var3) {
         int var14 = this.ic_flags.getInt();
         boolean var5 = (var14 & 65536) != 0;
         var14 &= -65537;
         ConstantPool.ClassEntry var6 = (ConstantPool.ClassEntry)this.ic_this_class.getRef();
         ConstantPool.ClassEntry var7;
         ConstantPool.Utf8Entry var8;
         if (var5) {
            var7 = (ConstantPool.ClassEntry)this.ic_outer_class.getRef();
            var8 = (ConstantPool.Utf8Entry)this.ic_name.getRef();
         } else {
            String var9 = var6.stringValue();
            String[] var10 = Package.parseInnerClassName(var9);

            assert var10 != null;

            String var11 = var10[0];
            String var12 = var10[2];
            if (var11 == null) {
               var7 = null;
            } else {
               var7 = ConstantPool.getClassEntry(var11);
            }

            if (var12 == null) {
               var8 = null;
            } else {
               var8 = ConstantPool.getUtf8Entry(var12);
            }
         }

         Package.InnerClass var15 = new Package.InnerClass(var6, var7, var8, var14);

         assert var5 || var15.predictable;

         var13.add(var15);
      }

      this.ic_flags.doneDisbursing();
      this.ic_this_class.doneDisbursing();
      this.ic_outer_class.doneDisbursing();
      this.ic_name.doneDisbursing();
      this.pkg.setAllInnerClasses(var13);
      this.ic_bands.doneDisbursing();
   }

   void readLocalInnerClasses(Package.Class var1) throws IOException {
      int var2 = this.class_InnerClasses_N.getInt();
      ArrayList var3 = new ArrayList(var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         ConstantPool.ClassEntry var5 = (ConstantPool.ClassEntry)this.class_InnerClasses_RC.getRef();
         int var6 = this.class_InnerClasses_F.getInt();
         if (var6 == 0) {
            Package.InnerClass var7 = this.pkg.getGlobalInnerClass(var5);

            assert var7 != null;

            var3.add(var7);
         } else {
            if (var6 == 65536) {
               var6 = 0;
            }

            ConstantPool.ClassEntry var9 = (ConstantPool.ClassEntry)this.class_InnerClasses_outer_RCN.getRef();
            ConstantPool.Utf8Entry var8 = (ConstantPool.Utf8Entry)this.class_InnerClasses_name_RUN.getRef();
            var3.add(new Package.InnerClass(var5, var9, var8, var6));
         }
      }

      var1.setInnerClasses(var3);
   }

   Package.Class[] readClasses() throws IOException {
      Package.Class[] var1 = new Package.Class[this.numClasses];
      if (this.verbose > 0) {
         Utils.log.info("  ...building " + var1.length + " classes...");
      }

      this.class_this.expectLength(this.numClasses);
      this.class_super.expectLength(this.numClasses);
      this.class_interface_count.expectLength(this.numClasses);
      this.class_this.readFrom(this.in);
      this.class_super.readFrom(this.in);
      this.class_interface_count.readFrom(this.in);
      this.class_interface.expectLength(this.class_interface_count.getIntTotal());
      this.class_interface.readFrom(this.in);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         ConstantPool.ClassEntry var3 = (ConstantPool.ClassEntry)this.class_this.getRef();
         ConstantPool.ClassEntry var4 = (ConstantPool.ClassEntry)this.class_super.getRef();
         ConstantPool.ClassEntry[] var5 = new ConstantPool.ClassEntry[this.class_interface_count.getInt()];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = (ConstantPool.ClassEntry)this.class_interface.getRef();
         }

         if (var4 == var3) {
            var4 = null;
         }

         Package.Class var7 = this.pkg.new Class(0, var3, var4, var5);
         var1[var2] = var7;
      }

      this.class_this.doneDisbursing();
      this.class_super.doneDisbursing();
      this.class_interface_count.doneDisbursing();
      this.class_interface.doneDisbursing();
      this.readMembers(var1);
      this.countAndReadAttrs(0, Arrays.asList(var1));
      this.pkg.trimToSize();
      this.readCodeHeaders();
      return var1;
   }

   private int getOutputIndex(ConstantPool.Entry var1) {
      assert var1.tag != 13;

      int var2 = this.pkg.cp.untypedIndexOf(var1);
      if (var2 >= 0) {
         return var2;
      } else if (var1.tag == 1) {
         ConstantPool.Entry var3 = (ConstantPool.Entry)this.utf8Signatures.get(var1);
         return this.pkg.cp.untypedIndexOf(var3);
      } else {
         return -1;
      }
   }

   void reconstructClass(Package.Class var1) {
      if (this.verbose > 1) {
         Utils.log.fine("reconstruct " + var1);
      }

      Attribute var2 = var1.getAttribute(this.attrClassFileVersion);
      if (var2 != null) {
         var1.removeAttribute(var2);
         var1.version = this.parseClassFileVersionAttr(var2);
      } else {
         var1.version = this.pkg.defaultClassVersion;
      }

      var1.expandSourceFile();
      var1.setCPMap(this.reconstructLocalCPMap(var1));
   }

   ConstantPool.Entry[] reconstructLocalCPMap(Package.Class var1) {
      Set var2 = (Set)this.ldcRefMap.get(var1);
      HashSet var3 = new HashSet();
      var1.visitRefs(0, var3);
      ArrayList var4 = new ArrayList();
      var1.addAttribute(Package.attrBootstrapMethodsEmpty.canonicalInstance());
      ConstantPool.completeReferencesIn(var3, true, var4);
      int var5 = var1.expandLocalICs();
      if (var5 != 0) {
         if (var5 > 0) {
            var1.visitInnerClassRefs(0, var3);
         } else {
            var3.clear();
            var1.visitRefs(0, var3);
         }

         ConstantPool.completeReferencesIn(var3, true, var4);
      }

      if (var4.isEmpty()) {
         var1.attributes.remove(Package.attrBootstrapMethodsEmpty.canonicalInstance());
      } else {
         var3.add(Package.getRefString("BootstrapMethods"));
         Collections.sort(var4);
         var1.setBootstrapMethods(var4);
      }

      int var6 = 0;
      Iterator var7 = var3.iterator();

      while(var7.hasNext()) {
         ConstantPool.Entry var8 = (ConstantPool.Entry)var7.next();
         if (var8.isDoubleWord()) {
            ++var6;
         }
      }

      ConstantPool.Entry[] var14 = new ConstantPool.Entry[1 + var6 + var3.size()];
      int var15 = 1;
      if (var2 != null) {
         assert var3.containsAll(var2);

         ConstantPool.Entry var10;
         for(Iterator var9 = var2.iterator(); var9.hasNext(); var14[var15++] = var10) {
            var10 = (ConstantPool.Entry)var9.next();
         }

         assert var15 == 1 + var2.size();

         var3.removeAll(var2);
         var2 = null;
      }

      HashSet var16 = var3;
      var3 = null;

      ConstantPool.Entry var12;
      for(Iterator var11 = var16.iterator(); var11.hasNext(); var14[var15++] = var12) {
         var12 = (ConstantPool.Entry)var11.next();
      }

      assert var15 == var15 + var16.size();

      Arrays.sort(var14, 1, var15, this.entryOutputOrder);
      Arrays.sort(var14, var15, var15, this.entryOutputOrder);
      int var17;
      if (this.verbose > 3) {
         Utils.log.fine("CP of " + this + " {");

         for(var17 = 0; var17 < var15; ++var17) {
            var12 = var14[var17];
            Utils.log.fine("  " + (var12 == null ? -1 : this.getOutputIndex(var12)) + " : " + var12);
         }

         Utils.log.fine("}");
      }

      var17 = var14.length;
      int var18 = var15;

      while(true) {
         --var18;
         if (var18 < 1) {
            assert var17 == 1;

            return var14;
         }

         ConstantPool.Entry var13 = var14[var18];
         if (var13.isDoubleWord()) {
            --var17;
            var14[var17] = null;
         }

         --var17;
         var14[var17] = var13;
      }
   }

   void readMembers(Package.Class[] var1) throws IOException {
      assert var1.length == this.numClasses;

      this.class_field_count.expectLength(this.numClasses);
      this.class_method_count.expectLength(this.numClasses);
      this.class_field_count.readFrom(this.in);
      this.class_method_count.readFrom(this.in);
      int var2 = this.class_field_count.getIntTotal();
      int var3 = this.class_method_count.getIntTotal();
      this.field_descr.expectLength(var2);
      this.method_descr.expectLength(var3);
      if (this.verbose > 1) {
         Utils.log.fine("expecting #fields=" + var2 + " and #methods=" + var3 + " in #classes=" + this.numClasses);
      }

      ArrayList var4 = new ArrayList(var2);
      this.field_descr.readFrom(this.in);

      int var8;
      for(int var5 = 0; var5 < var1.length; ++var5) {
         Package.Class var6 = var1[var5];
         int var7 = this.class_field_count.getInt();

         for(var8 = 0; var8 < var7; ++var8) {
            Package.Class.Field var9 = var6.new Field(0, (ConstantPool.DescriptorEntry)this.field_descr.getRef());
            var4.add(var9);
         }
      }

      this.class_field_count.doneDisbursing();
      this.field_descr.doneDisbursing();
      this.countAndReadAttrs(1, var4);
      var4 = null;
      ArrayList var11 = new ArrayList(var3);
      this.method_descr.readFrom(this.in);

      for(int var12 = 0; var12 < var1.length; ++var12) {
         Package.Class var13 = var1[var12];
         var8 = this.class_method_count.getInt();

         for(int var14 = 0; var14 < var8; ++var14) {
            Package.Class.Method var10 = var13.new Method(0, (ConstantPool.DescriptorEntry)this.method_descr.getRef());
            var11.add(var10);
         }
      }

      this.class_method_count.doneDisbursing();
      this.method_descr.doneDisbursing();
      this.countAndReadAttrs(2, var11);
      this.allCodes = this.buildCodeAttrs(var11);
   }

   Code[] buildCodeAttrs(List<Package.Class.Method> var1) {
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Package.Class.Method var4 = (Package.Class.Method)var3.next();
         if (var4.getAttribute(this.attrCodeEmpty) != null) {
            var4.code = new Code(var4);
            var2.add(var4.code);
         }
      }

      Code[] var5 = new Code[var2.size()];
      var2.toArray(var5);
      return var5;
   }

   void readCodeHeaders() throws IOException {
      boolean var1 = testBit(this.archiveOptions, 4);
      this.code_headers.expectLength(this.allCodes.length);
      this.code_headers.readFrom(this.in);
      ArrayList var2 = new ArrayList(this.allCodes.length / 10);

      Code var4;
      for(int var3 = 0; var3 < this.allCodes.length; ++var3) {
         var4 = this.allCodes[var3];
         int var5 = this.code_headers.getByte();

         assert var5 == (var5 & 255);

         if (this.verbose > 2) {
            Utils.log.fine("codeHeader " + var4 + " = " + var5);
         }

         if (var5 == 0) {
            var2.add(var4);
         } else {
            var4.setMaxStack(shortCodeHeader_max_stack(var5));
            var4.setMaxNALocals(shortCodeHeader_max_na_locals(var5));
            var4.setHandlerCount(shortCodeHeader_handler_count(var5));

            assert shortCodeHeader(var4) == var5;
         }
      }

      this.code_headers.doneDisbursing();
      this.code_max_stack.expectLength(var2.size());
      this.code_max_na_locals.expectLength(var2.size());
      this.code_handler_count.expectLength(var2.size());
      this.code_max_stack.readFrom(this.in);
      this.code_max_na_locals.readFrom(this.in);
      this.code_handler_count.readFrom(this.in);
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         var4 = (Code)var6.next();
         var4.setMaxStack(this.code_max_stack.getInt());
         var4.setMaxNALocals(this.code_max_na_locals.getInt());
         var4.setHandlerCount(this.code_handler_count.getInt());
      }

      this.code_max_stack.doneDisbursing();
      this.code_max_na_locals.doneDisbursing();
      this.code_handler_count.doneDisbursing();
      this.readCodeHandlers();
      if (var1) {
         this.codesWithFlags = Arrays.asList(this.allCodes);
      } else {
         this.codesWithFlags = var2;
      }

      this.countAttrs(3, this.codesWithFlags);
   }

   void readCodeHandlers() throws IOException {
      int var1 = 0;

      for(int var2 = 0; var2 < this.allCodes.length; ++var2) {
         Code var3 = this.allCodes[var2];
         var1 += var3.getHandlerCount();
      }

      BandStructure.ValueBand[] var7 = new BandStructure.ValueBand[]{this.code_handler_start_P, this.code_handler_end_PO, this.code_handler_catch_PO, this.code_handler_class_RCN};

      int var8;
      for(var8 = 0; var8 < var7.length; ++var8) {
         var7[var8].expectLength(var1);
         var7[var8].readFrom(this.in);
      }

      for(var8 = 0; var8 < this.allCodes.length; ++var8) {
         Code var4 = this.allCodes[var8];
         int var5 = 0;

         for(int var6 = var4.getHandlerCount(); var5 < var6; ++var5) {
            var4.handler_class[var5] = this.code_handler_class_RCN.getRef();
            var4.handler_start[var5] = this.code_handler_start_P.getInt();
            var4.handler_end[var5] = this.code_handler_end_PO.getInt();
            var4.handler_catch[var5] = this.code_handler_catch_PO.getInt();
         }
      }

      for(var8 = 0; var8 < var7.length; ++var8) {
         var7[var8].doneDisbursing();
      }

   }

   void fixupCodeHandlers() {
      for(int var1 = 0; var1 < this.allCodes.length; ++var1) {
         Code var2 = this.allCodes[var1];
         int var3 = 0;

         for(int var4 = var2.getHandlerCount(); var3 < var4; ++var3) {
            int var5 = var2.handler_start[var3];
            var2.handler_start[var3] = var2.decodeBCI(var5);
            var5 += var2.handler_end[var3];
            var2.handler_end[var3] = var2.decodeBCI(var5);
            var5 += var2.handler_catch[var3];
            var2.handler_catch[var3] = var2.decodeBCI(var5);
         }
      }

   }

   void countAndReadAttrs(int var1, Collection<? extends Attribute.Holder> var2) throws IOException {
      this.countAttrs(var1, var2);
      this.readAttrs(var1, var2);
   }

   void countAttrs(int var1, Collection<? extends Attribute.Holder> var2) throws IOException {
      BandStructure.MultiBand var3 = this.attrBands[var1];
      long var4 = this.attrFlagMask[var1];
      if (this.verbose > 1) {
         Utils.log.fine("scanning flags and attrs for " + Attribute.contextName(var1) + "[" + var2.size() + "]");
      }

      List var6 = (List)this.attrDefs.get(var1);
      Attribute.Layout[] var7 = new Attribute.Layout[var6.size()];
      var6.toArray(var7);
      BandStructure.IntBand var8 = getAttrBand(var3, 0);
      BandStructure.IntBand var9 = getAttrBand(var3, 1);
      BandStructure.IntBand var10 = getAttrBand(var3, 2);
      BandStructure.IntBand var11 = getAttrBand(var3, 3);
      BandStructure.IntBand var12 = getAttrBand(var3, 4);
      int var13 = this.attrOverflowMask[var1];
      int var14 = 0;
      boolean var15 = this.haveFlagsHi(var1);
      var8.expectLength(var15 ? var2.size() : 0);
      var8.readFrom(this.in);
      var9.expectLength(var2.size());
      var9.readFrom(this.in);

      assert (var4 & (long)var13) == (long)var13;

      Iterator var16 = var2.iterator();

      while(var16.hasNext()) {
         Attribute.Holder var17 = (Attribute.Holder)var16.next();
         int var18 = var9.getInt();
         var17.flags = var18;
         if ((var18 & var13) != 0) {
            ++var14;
         }
      }

      var10.expectLength(var14);
      var10.readFrom(this.in);
      var11.expectLength(var10.getIntTotal());
      var11.readFrom(this.in);
      int[] var30 = new int[var7.length];
      Iterator var31 = var2.iterator();

      while(true) {
         long var19;
         int var21;
         int var24;
         int var27;
         Attribute.Holder var33;
         do {
            if (!var31.hasNext()) {
               var8.doneDisbursing();
               var9.doneDisbursing();
               var10.doneDisbursing();
               var11.doneDisbursing();
               int var32 = 0;
               boolean var34 = true;

               while(true) {
                  Attribute.Layout var20;
                  int var23;
                  int var35;
                  for(var35 = 0; var35 < var7.length; ++var35) {
                     var20 = var7[var35];
                     if (var20 != null && var34 == this.isPredefinedAttr(var1, var35)) {
                        var21 = var30[var35];
                        if (var21 != 0) {
                           Attribute.Layout.Element[] var36 = var20.getCallables();

                           for(var23 = 0; var23 < var36.length; ++var23) {
                              assert var36[var23].kind == 10;

                              if (var36[var23].flagTest((byte)8)) {
                                 ++var32;
                              }
                           }
                        }
                     }
                  }

                  if (!var34) {
                     var12.expectLength(var32);
                     var12.readFrom(this.in);
                     var34 = true;

                     while(true) {
                        for(var35 = 0; var35 < var7.length; ++var35) {
                           var20 = var7[var35];
                           if (var20 != null && var34 == this.isPredefinedAttr(var1, var35)) {
                              var21 = var30[var35];
                              BandStructure.Band[] var37 = (BandStructure.Band[])this.attrBandTable.get(var20);
                              if (var20 == this.attrInnerClassesEmpty) {
                                 this.class_InnerClasses_N.expectLength(var21);
                                 this.class_InnerClasses_N.readFrom(this.in);
                                 var23 = this.class_InnerClasses_N.getIntTotal();
                                 this.class_InnerClasses_RC.expectLength(var23);
                                 this.class_InnerClasses_RC.readFrom(this.in);
                                 this.class_InnerClasses_F.expectLength(var23);
                                 this.class_InnerClasses_F.readFrom(this.in);
                                 var23 -= this.class_InnerClasses_F.getIntCount(0);
                                 this.class_InnerClasses_outer_RCN.expectLength(var23);
                                 this.class_InnerClasses_outer_RCN.readFrom(this.in);
                                 this.class_InnerClasses_name_RUN.expectLength(var23);
                                 this.class_InnerClasses_name_RUN.readFrom(this.in);
                              } else if (!this.optDebugBands && var21 == 0) {
                                 for(var23 = 0; var23 < var37.length; ++var23) {
                                    var37[var23].doneWithUnusedBand();
                                 }
                              } else {
                                 boolean var38 = var20.hasCallables();
                                 if (!var38) {
                                    this.readAttrBands(var20.elems, var21, new int[0], var37);
                                 } else {
                                    Attribute.Layout.Element[] var39 = var20.getCallables();
                                    int[] var40 = new int[var39.length];
                                    var40[0] = var21;

                                    for(int var26 = 0; var26 < var39.length; ++var26) {
                                       assert var39[var26].kind == 10;

                                       var27 = var40[var26];
                                       var40[var26] = -1;
                                       if (var21 > 0 && var39[var26].flagTest((byte)8)) {
                                          var27 += var12.getInt();
                                       }

                                       this.readAttrBands(var39[var26].body, var27, var40, var37);
                                    }
                                 }

                                 if (this.optDebugBands && var21 == 0) {
                                    for(var24 = 0; var24 < var37.length; ++var24) {
                                       var37[var24].doneDisbursing();
                                    }
                                 }
                              }
                           }
                        }

                        if (!var34) {
                           var12.doneDisbursing();
                           return;
                        }

                        var34 = false;
                     }
                  }

                  var34 = false;
               }
            }

            var33 = (Attribute.Holder)var31.next();

            assert var33.attributes == null;

            var19 = ((long)var33.flags & var4) << 32 >>> 32;
            var33.flags -= (int)var19;

            assert var33.flags == (char)var33.flags;

            assert var1 != 3 || var33.flags == 0;

            if (var15) {
               var19 += (long)var8.getInt() << 32;
            }
         } while(var19 == 0L);

         var21 = 0;
         long var22 = var19 & (long)var13;

         assert var22 >= 0L;

         var19 -= var22;
         if (var22 != 0L) {
            var21 = var10.getInt();
         }

         var24 = 0;
         long var25 = var19;

         for(var27 = 0; var25 != 0L; ++var27) {
            if ((var25 & 1L << var27) != 0L) {
               var25 -= 1L << var27;
               ++var24;
            }
         }

         ArrayList var41 = new ArrayList(var24 + var21);
         var33.attributes = var41;
         var25 = var19;

         int var10002;
         int var28;
         Attribute var29;
         for(var28 = 0; var25 != 0L; ++var28) {
            if ((var25 & 1L << var28) != 0L) {
               var25 -= 1L << var28;
               var10002 = var30[var28]++;
               if (var7[var28] == null) {
                  this.badAttrIndex(var28, var1);
               }

               var29 = var7[var28].canonicalInstance();
               var41.add(var29);
               --var24;
            }
         }

         assert var24 == 0;

         while(var21 > 0) {
            var28 = var11.getInt();
            var10002 = var30[var28]++;
            if (var7[var28] == null) {
               this.badAttrIndex(var28, var1);
            }

            var29 = var7[var28].canonicalInstance();
            var41.add(var29);
            --var21;
         }
      }
   }

   void badAttrIndex(int var1, int var2) throws IOException {
      throw new IOException("Unknown attribute index " + var1 + " for " + Constants.ATTR_CONTEXT_NAME[var2] + " attribute");
   }

   void readAttrs(int var1, Collection<? extends Attribute.Holder> var2) throws IOException {
      HashSet var3 = new HashSet();
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();
      Iterator var5 = var2.iterator();

      label89:
      while(true) {
         final Attribute.Holder var6;
         do {
            if (!var5.hasNext()) {
               var5 = var3.iterator();

               while(true) {
                  Attribute.Layout var14;
                  do {
                     if (!var5.hasNext()) {
                        if (var1 == 0) {
                           this.class_InnerClasses_N.doneDisbursing();
                           this.class_InnerClasses_RC.doneDisbursing();
                           this.class_InnerClasses_F.doneDisbursing();
                           this.class_InnerClasses_outer_RCN.doneDisbursing();
                           this.class_InnerClasses_name_RUN.doneDisbursing();
                        }

                        BandStructure.MultiBand var13 = this.attrBands[var1];

                        for(int var15 = 0; var15 < var13.size(); ++var15) {
                           BandStructure.Band var17 = var13.get(var15);
                           if (var17 instanceof BandStructure.MultiBand) {
                              var17.doneDisbursing();
                           }
                        }

                        var13.doneDisbursing();
                        return;
                     }

                     var14 = (Attribute.Layout)var5.next();
                  } while(var14 == null);

                  BandStructure.Band[] var16 = (BandStructure.Band[])this.attrBandTable.get(var14);

                  for(int var18 = 0; var18 < var16.length; ++var18) {
                     var16[var18].doneDisbursing();
                  }
               }
            }

            var6 = (Attribute.Holder)var5.next();
         } while(var6.attributes == null);

         ListIterator var7 = var6.attributes.listIterator();

         while(true) {
            while(true) {
               if (!var7.hasNext()) {
                  continue label89;
               }

               Attribute var8 = (Attribute)var7.next();
               Attribute.Layout var9 = var8.layout();
               if (var9.bandCount == 0) {
                  if (var9 == this.attrInnerClassesEmpty) {
                     this.readLocalInnerClasses((Package.Class)var6);
                  }
               } else {
                  var3.add(var9);
                  boolean var10 = var1 == 1 && var9 == this.attrConstantValue;
                  if (var10) {
                     this.setConstantValueIndex((Package.Class.Field)var6);
                  }

                  if (this.verbose > 2) {
                     Utils.log.fine("read " + var8 + " in " + var6);
                  }

                  final BandStructure.Band[] var11 = (BandStructure.Band[])this.attrBandTable.get(var9);
                  var4.reset();
                  Object var12 = var8.unparse(new Attribute.ValueStream() {
                     public int getInt(int var1) {
                        return ((BandStructure.IntBand)var11[var1]).getInt();
                     }

                     public ConstantPool.Entry getRef(int var1) {
                        return ((BandStructure.CPRefBand)var11[var1]).getRef();
                     }

                     public int decodeBCI(int var1) {
                        Code var2 = (Code)var6;
                        return var2.decodeBCI(var1);
                     }
                  }, var4);
                  var7.set(var8.addContent(var4.toByteArray(), var12));
                  if (var10) {
                     this.setConstantValueIndex((Package.Class.Field)null);
                  }
               }
            }
         }
      }
   }

   private void readAttrBands(Attribute.Layout.Element[] var1, int var2, int[] var3, BandStructure.Band[] var4) throws IOException {
      for(int var5 = 0; var5 < var1.length; ++var5) {
         Attribute.Layout.Element var6 = var1[var5];
         BandStructure.Band var7 = null;
         if (var6.hasBand()) {
            var7 = var4[var6.bandIndex];
            var7.expectLength(var2);
            var7.readFrom(this.in);
         }

         switch(var6.kind) {
         case 5:
            int var8 = ((BandStructure.IntBand)var7).getIntTotal();
            this.readAttrBands(var6.body, var8, var3, var4);
         case 6:
         case 8:
         default:
            break;
         case 7:
            int var9 = var2;

            for(int var10 = 0; var10 < var6.body.length; ++var10) {
               int var11;
               if (var10 == var6.body.length - 1) {
                  var11 = var9;
               } else {
                  var11 = 0;

                  for(int var12 = var10; var10 == var12 || var10 < var6.body.length && var6.body[var10].flagTest((byte)8); ++var10) {
                     var11 += ((BandStructure.IntBand)var7).getIntCount(var6.body[var10].value);
                  }

                  --var10;
               }

               var9 -= var11;
               this.readAttrBands(var6.body[var10].body, var11, var3, var4);
            }

            assert var9 == 0;
            break;
         case 9:
            assert var6.body.length == 1;

            assert var6.body[0].kind == 10;

            if (!var6.flagTest((byte)8)) {
               assert var3[var6.value] >= 0;

               int var10001 = var6.value;
               var3[var10001] += var2;
            }
            break;
         case 10:
            assert false;
         }
      }

   }

   void readByteCodes() throws IOException {
      this.bc_codes.elementCountForDebug = this.allCodes.length;
      this.bc_codes.setInputStreamFrom(this.in);
      this.readByteCodeOps();
      this.bc_codes.doneDisbursing();
      BandStructure.Band[] var1 = new BandStructure.Band[]{this.bc_case_value, this.bc_byte, this.bc_short, this.bc_local, this.bc_label, this.bc_intref, this.bc_floatref, this.bc_longref, this.bc_doubleref, this.bc_stringref, this.bc_loadablevalueref, this.bc_classref, this.bc_fieldref, this.bc_methodref, this.bc_imethodref, this.bc_indyref, this.bc_thisfield, this.bc_superfield, this.bc_thismethod, this.bc_supermethod, this.bc_initref, this.bc_escref, this.bc_escrefsize, this.bc_escsize};

      int var2;
      for(var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].readFrom(this.in);
      }

      this.bc_escbyte.expectLength(this.bc_escsize.getIntTotal());
      this.bc_escbyte.readFrom(this.in);
      this.expandByteCodeOps();
      this.bc_case_count.doneDisbursing();

      for(var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].doneDisbursing();
      }

      this.bc_escbyte.doneDisbursing();
      this.bc_bands.doneDisbursing();
      this.readAttrs(3, this.codesWithFlags);
      this.fixupCodeHandlers();
      this.code_bands.doneDisbursing();
      this.class_bands.doneDisbursing();
   }

   private void readByteCodeOps() throws IOException {
      byte[] var1 = new byte[4096];
      ArrayList var2 = new ArrayList();

      int var5;
      int var6;
      label84:
      for(int var3 = 0; var3 < this.allCodes.length; ++var3) {
         Code var4 = this.allCodes[var3];
         var5 = 0;

         while(true) {
            var6 = this.bc_codes.getByte();
            if (var5 + 10 > var1.length) {
               var1 = realloc(var1);
            }

            var1[var5] = (byte)var6;
            boolean var7 = false;
            if (var6 == 196) {
               var6 = this.bc_codes.getByte();
               ++var5;
               var1[var5] = (byte)var6;
               var7 = true;
            }

            assert var6 == (255 & var6);

            switch(var6) {
            case 16:
               this.bc_byte.expectMoreLength(1);
               break;
            case 17:
               this.bc_short.expectMoreLength(1);
               break;
            case 132:
               this.bc_local.expectMoreLength(1);
               if (var7) {
                  this.bc_short.expectMoreLength(1);
               } else {
                  this.bc_byte.expectMoreLength(1);
               }
               break;
            case 170:
            case 171:
               this.bc_case_count.expectMoreLength(1);
               var2.add(var6);
               break;
            case 188:
               this.bc_byte.expectMoreLength(1);
               break;
            case 197:
               assert this.getCPRefOpBand(var6) == this.bc_classref;

               this.bc_classref.expectMoreLength(1);
               this.bc_byte.expectMoreLength(1);
               break;
            case 253:
               this.bc_escrefsize.expectMoreLength(1);
               this.bc_escref.expectMoreLength(1);
               break;
            case 254:
               this.bc_escsize.expectMoreLength(1);
               break;
            case 255:
               var4.bytes = realloc(var1, var5);
               continue label84;
            default:
               if (Instruction.isInvokeInitOp(var6)) {
                  this.bc_initref.expectMoreLength(1);
               } else {
                  BandStructure.CPRefBand var8;
                  if (Instruction.isSelfLinkerOp(var6)) {
                     var8 = this.selfOpRefBand(var6);
                     var8.expectMoreLength(1);
                  } else if (Instruction.isBranchOp(var6)) {
                     this.bc_label.expectMoreLength(1);
                  } else if (Instruction.isCPRefOp(var6)) {
                     var8 = this.getCPRefOpBand(var6);
                     var8.expectMoreLength(1);

                     assert var6 != 197;
                  } else if (Instruction.isLocalSlotOp(var6)) {
                     this.bc_local.expectMoreLength(1);
                  }
               }
            }

            ++var5;
         }
      }

      this.bc_case_count.readFrom(this.in);
      Iterator var9 = var2.iterator();

      while(var9.hasNext()) {
         Integer var10 = (Integer)var9.next();
         var5 = var10;
         var6 = this.bc_case_count.getInt();
         this.bc_label.expectMoreLength(1 + var6);
         this.bc_case_value.expectMoreLength(var5 == 170 ? 1 : var6);
      }

      this.bc_case_count.resetForSecondPass();
   }

   private void expandByteCodeOps() throws IOException {
      byte[] var1 = new byte[4096];
      int[] var2 = new int[4096];
      int[] var3 = new int[1024];
      Fixups var4 = new Fixups();

      for(int var5 = 0; var5 < this.allCodes.length; ++var5) {
         Code var6 = this.allCodes[var5];
         byte[] var7 = var6.bytes;
         var6.bytes = null;
         Package.Class var8 = var6.thisClass();
         Object var9 = (Set)this.ldcRefMap.get(var8);
         if (var9 == null) {
            this.ldcRefMap.put(var8, var9 = new HashSet());
         }

         ConstantPool.ClassEntry var10 = var8.thisClass;
         ConstantPool.ClassEntry var11 = var8.superClass;
         ConstantPool.ClassEntry var12 = null;
         int var13 = 0;
         int var14 = 0;
         int var15 = 0;
         boolean var16 = false;
         var4.clear();

         int var18;
         int var19;
         int var21;
         int var33;
         label285:
         for(int var17 = 0; var17 < var7.length; ++var17) {
            var18 = Instruction.getByte(var7, var17);
            var19 = var13;
            var2[var14++] = var13;
            if (var13 + 10 > var1.length) {
               var1 = realloc(var1);
            }

            if (var14 + 10 > var2.length) {
               var2 = realloc(var2);
            }

            if (var15 + 10 > var3.length) {
               var3 = realloc(var3);
            }

            boolean var20 = false;
            if (var18 == 196) {
               var1[var13++] = (byte)var18;
               ++var17;
               var18 = Instruction.getByte(var7, var17);
               var20 = true;
            }

            boolean var23;
            int var35;
            switch(var18) {
            case 16:
            case 188:
               var21 = this.bc_byte.getByte();
               var1[var13++] = (byte)var18;
               var1[var13++] = (byte)var21;
               break;
            case 17:
               var21 = this.bc_short.getInt();
               var1[var13++] = (byte)var18;
               Instruction.setShort(var1, var13, var21);
               var13 += 2;
               break;
            case 132:
               var1[var13++] = (byte)var18;
               var21 = this.bc_local.getInt();
               if (var20) {
                  var33 = this.bc_short.getInt();
                  Instruction.setShort(var1, var13, var21);
                  var13 += 2;
                  Instruction.setShort(var1, var13, var33);
                  var13 += 2;
               } else {
                  byte var34 = (byte)this.bc_byte.getByte();
                  var1[var13++] = (byte)var21;
                  var1[var13++] = (byte)var34;
               }
               break;
            case 170:
            case 171:
               for(var21 = this.bc_case_count.getInt(); var13 + 30 + var21 * 8 > var1.length; var1 = realloc(var1)) {
               }

               var1[var13++] = (byte)var18;
               Arrays.fill((byte[])var1, var13, var13 + 30, (byte)0);
               Instruction.Switch var32 = (Instruction.Switch)Instruction.at(var1, var19);
               var32.setCaseCount(var21);
               if (var18 == 170) {
                  var32.setCaseValue(0, this.bc_case_value.getInt());
               } else {
                  for(var35 = 0; var35 < var21; ++var35) {
                     var32.setCaseValue(var35, this.bc_case_value.getInt());
                  }
               }

               var3[var15++] = var19;
               var13 = var32.getNextPC();
               break;
            case 253:
               var16 = true;
               var21 = this.bc_escrefsize.getInt();
               ConstantPool.Entry var22 = this.bc_escref.getRef();
               if (var21 == 1) {
                  ((Set)var9).add(var22);
               }

               switch(var21) {
               case 1:
                  var4.addU1(var13, var22);
                  break;
               case 2:
                  var4.addU2(var13, var22);
                  break;
               default:
                  assert false;

                  var23 = false;
               }

               var1[var13 + 0] = var1[var13 + 1] = 0;
               var13 += var21;
               break;
            case 254:
               var16 = true;

               for(var21 = this.bc_escsize.getInt(); var13 + var21 > var1.length; var1 = realloc(var1)) {
               }

               while(true) {
                  if (var21-- <= 0) {
                     continue label285;
                  }

                  var1[var13++] = (byte)this.bc_escbyte.getByte();
               }
            default:
               int var38;
               if (Instruction.isInvokeInitOp(var18)) {
                  var21 = var18 - 230;
                  short var41 = 183;
                  ConstantPool.ClassEntry var42;
                  switch(var21) {
                  case 0:
                     var42 = var10;
                     break;
                  case 1:
                     var42 = var11;
                     break;
                  default:
                     assert var21 == 2;

                     var42 = var12;
                  }

                  var1[var13++] = (byte)var41;
                  var38 = this.bc_initref.getInt();
                  ConstantPool.MemberEntry var39 = this.pkg.cp.getOverloadingForIndex((byte)10, var42, "<init>", var38);
                  var4.addU2(var13, var39);
                  var1[var13 + 0] = var1[var13 + 1] = 0;
                  var13 += 2;

                  assert Instruction.opLength(var41) == var13 - var19;
               } else {
                  boolean var25;
                  if (Instruction.isSelfLinkerOp(var18)) {
                     var21 = var18 - 202;
                     boolean var40 = var21 >= 14;
                     if (var40) {
                        var21 -= 14;
                     }

                     var23 = var21 >= 7;
                     if (var23) {
                        var21 -= 7;
                     }

                     var38 = 178 + var21;
                     var25 = Instruction.isFieldOp(var38);
                     ConstantPool.ClassEntry var27 = var40 ? var11 : var10;
                     ConstantPool.Index var28;
                     BandStructure.CPRefBand var43;
                     if (var25) {
                        var43 = var40 ? this.bc_superfield : this.bc_thisfield;
                        var28 = this.pkg.cp.getMemberIndex((byte)9, var27);
                     } else {
                        var43 = var40 ? this.bc_supermethod : this.bc_thismethod;
                        var28 = this.pkg.cp.getMemberIndex((byte)10, var27);
                     }

                     assert var43 == this.selfOpRefBand(var18);

                     ConstantPool.MemberEntry var29 = (ConstantPool.MemberEntry)var43.getRef(var28);
                     if (var23) {
                        var1[var13++] = 42;
                        var19 = var13;
                        var2[var14++] = var13;
                     }

                     var1[var13++] = (byte)var38;
                     var4.addU2(var13, var29);
                     var1[var13 + 0] = var1[var13 + 1] = 0;
                     var13 += 2;

                     assert Instruction.opLength(var38) == var13 - var19;
                  } else if (Instruction.isBranchOp(var18)) {
                     var1[var13++] = (byte)var18;

                     assert !var20;

                     var21 = var19 + Instruction.opLength(var18);

                     for(var3[var15++] = var19; var13 < var21; var1[var13++] = 0) {
                     }
                  } else if (Instruction.isCPRefOp(var18)) {
                     BandStructure.CPRefBand var36 = this.getCPRefOpBand(var18);
                     Object var37 = var36.getRef();
                     if (var37 == null) {
                        if (var36 == this.bc_classref) {
                           var37 = var10;
                        } else {
                           assert false;
                        }
                     }

                     var35 = var18;
                     byte var24 = 2;
                     switch(var18) {
                     case 18:
                     case 233:
                     case 234:
                     case 235:
                     case 240:
                        var35 = 18;
                        var24 = 1;
                        ((Set)var9).add(var37);
                        break;
                     case 19:
                     case 236:
                     case 237:
                     case 238:
                     case 241:
                        var35 = 19;
                        break;
                     case 20:
                     case 239:
                        var35 = 20;
                        break;
                     case 187:
                        var12 = (ConstantPool.ClassEntry)var37;
                        break;
                     case 242:
                        var35 = 183;
                        break;
                     case 243:
                        var35 = 184;
                     }

                     var1[var13++] = (byte)var35;
                     switch(var24) {
                     case 1:
                        var4.addU1(var13, (ConstantPool.Entry)var37);
                        break;
                     case 2:
                        var4.addU2(var13, (ConstantPool.Entry)var37);
                        break;
                     default:
                        assert false;

                        var25 = false;
                     }

                     var1[var13 + 0] = var1[var13 + 1] = 0;
                     var13 += var24;
                     int var26;
                     if (var35 == 197) {
                        var26 = this.bc_byte.getByte();
                        var1[var13++] = (byte)var26;
                     } else if (var35 == 185) {
                        var26 = ((ConstantPool.MemberEntry)var37).descRef.typeRef.computeSize(true);
                        var1[var13++] = (byte)(1 + var26);
                        var1[var13++] = 0;
                     } else if (var35 == 186) {
                        var1[var13++] = 0;
                        var1[var13++] = 0;
                     }

                     assert Instruction.opLength(var35) == var13 - var19;
                  } else if (Instruction.isLocalSlotOp(var18)) {
                     var1[var13++] = (byte)var18;
                     var21 = this.bc_local.getInt();
                     if (var20) {
                        Instruction.setShort(var1, var13, var21);
                        var13 += 2;
                        if (var18 == 132) {
                           var33 = this.bc_short.getInt();
                           Instruction.setShort(var1, var13, var33);
                           var13 += 2;
                        }
                     } else {
                        Instruction.setByte(var1, var13, var21);
                        ++var13;
                        if (var18 == 132) {
                           var33 = this.bc_byte.getByte();
                           Instruction.setByte(var1, var13, var33);
                           ++var13;
                        }
                     }

                     assert Instruction.opLength(var18) == var13 - var19;
                  } else {
                     if (var18 >= 202) {
                        Utils.log.warning("unrecognized bytescode " + var18 + " " + Instruction.byteName(var18));
                     }

                     assert var18 < 202;

                     var1[var13++] = (byte)var18;

                     assert Instruction.opLength(var18) == var13 - var19;
                  }
               }
            }
         }

         var6.setBytes(realloc(var1, var13));
         var6.setInstructionMap(var2, var14);
         Instruction var30 = null;

         for(var18 = 0; var18 < var15; ++var18) {
            var19 = var3[var18];
            var30 = Instruction.at(var6.bytes, var19, var30);
            if (var30 instanceof Instruction.Switch) {
               Instruction.Switch var31 = (Instruction.Switch)var30;
               var31.setDefaultLabel(this.getLabel(this.bc_label, var6, var19));
               var21 = var31.getCaseCount();

               for(var33 = 0; var33 < var21; ++var33) {
                  var31.setCaseLabel(var33, this.getLabel(this.bc_label, var6, var19));
               }
            } else {
               var30.setBranchLabel(this.getLabel(this.bc_label, var6, var19));
            }
         }

         if (var4.size() > 0) {
            if (this.verbose > 2) {
               Utils.log.fine("Fixups in code: " + var4);
            }

            var6.addFixups(var4);
         }
      }

   }

   static class LimitedBuffer extends BufferedInputStream {
      long served;
      int servedPos;
      long limit;
      long buffered;

      public boolean atLimit() {
         boolean var1 = this.getBytesServed() == this.limit;

         assert !var1 || this.limit == this.buffered;

         return var1;
      }

      public long getBytesServed() {
         return this.served + (long)(this.pos - this.servedPos);
      }

      public void setReadLimit(long var1) {
         if (var1 == -1L) {
            this.limit = -1L;
         } else {
            this.limit = this.getBytesServed() + var1;
         }

      }

      public long getReadLimit() {
         return this.limit == -1L ? this.limit : this.limit - this.getBytesServed();
      }

      public int read() throws IOException {
         if (this.pos < this.count) {
            return this.buf[this.pos++] & 255;
         } else {
            this.served += (long)(this.pos - this.servedPos);
            int var1 = super.read();
            this.servedPos = this.pos;
            if (var1 >= 0) {
               ++this.served;
            }

            assert this.served <= this.limit || this.limit == -1L;

            return var1;
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         this.served += (long)(this.pos - this.servedPos);
         int var4 = super.read(var1, var2, var3);
         this.servedPos = this.pos;
         if (var4 >= 0) {
            this.served += (long)var4;
         }

         return var4;
      }

      public long skip(long var1) throws IOException {
         throw new RuntimeException("no skipping");
      }

      LimitedBuffer(InputStream var1) {
         super((InputStream)null, 16384);
         this.servedPos = this.pos;
         super.in = new FilterInputStream(var1) {
            public int read() throws IOException {
               if (LimitedBuffer.this.buffered == LimitedBuffer.this.limit) {
                  return -1;
               } else {
                  ++LimitedBuffer.this.buffered;
                  return super.read();
               }
            }

            public int read(byte[] var1, int var2, int var3) throws IOException {
               if (LimitedBuffer.this.buffered == LimitedBuffer.this.limit) {
                  return -1;
               } else {
                  if (LimitedBuffer.this.limit != -1L) {
                     long var4 = LimitedBuffer.this.limit - LimitedBuffer.this.buffered;
                     if ((long)var3 > var4) {
                        var3 = (int)var4;
                     }
                  }

                  int var6 = super.read(var1, var2, var3);
                  if (var6 >= 0) {
                     PackageReader.LimitedBuffer var10000 = LimitedBuffer.this;
                     var10000.buffered += (long)var6;
                  }

                  return var6;
               }
            }
         };
      }
   }
}
