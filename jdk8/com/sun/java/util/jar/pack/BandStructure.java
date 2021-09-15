package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class BandStructure {
   static final int MAX_EFFORT = 9;
   static final int MIN_EFFORT = 1;
   static final int DEFAULT_EFFORT = 5;
   PropMap p200 = Utils.currentPropMap();
   int verbose;
   int effort;
   boolean optDumpBands;
   boolean optDebugBands;
   boolean optVaryCodings;
   boolean optBigStrings;
   private Package.Version highestClassVersion;
   private final boolean isReader;
   static final Coding BYTE1 = Coding.of(1, 256);
   static final Coding CHAR3 = Coding.of(3, 128);
   static final Coding BCI5 = Coding.of(5, 4);
   static final Coding BRANCH5 = Coding.of(5, 4, 2);
   static final Coding UNSIGNED5 = Coding.of(5, 64);
   static final Coding UDELTA5;
   static final Coding SIGNED5;
   static final Coding DELTA5;
   static final Coding MDELTA5;
   private static final Coding[] basicCodings;
   private static final Map<Coding, Integer> basicCodingIndexes;
   protected byte[] bandHeaderBytes;
   protected int bandHeaderBytePos;
   protected int bandHeaderBytePos0;
   static final int SHORT_BAND_HEURISTIC = 100;
   public static final int NO_PHASE = 0;
   public static final int COLLECT_PHASE = 1;
   public static final int FROZEN_PHASE = 3;
   public static final int WRITE_PHASE = 5;
   public static final int EXPECT_PHASE = 2;
   public static final int READ_PHASE = 4;
   public static final int DISBURSE_PHASE = 6;
   public static final int DONE_PHASE = 8;
   private final List<BandStructure.CPRefBand> allKQBands;
   private List<Object[]> needPredefIndex;
   private CodingChooser codingChooser;
   static final byte[] defaultMetaCoding;
   static final byte[] noMetaCoding;
   BandStructure.ByteCounter outputCounter;
   protected int archiveOptions;
   protected long archiveSize0;
   protected long archiveSize1;
   protected int archiveNextCount;
   static final int AH_LENGTH_0 = 3;
   static final int AH_LENGTH_MIN = 15;
   static final int AH_LENGTH_S = 2;
   static final int AH_ARCHIVE_SIZE_HI = 0;
   static final int AH_ARCHIVE_SIZE_LO = 1;
   static final int AH_FILE_HEADER_LEN = 5;
   static final int AH_SPECIAL_FORMAT_LEN = 2;
   static final int AH_CP_NUMBER_LEN = 4;
   static final int AH_CP_EXTRA_LEN = 4;
   static final int AB_FLAGS_HI = 0;
   static final int AB_FLAGS_LO = 1;
   static final int AB_ATTR_COUNT = 2;
   static final int AB_ATTR_INDEXES = 3;
   static final int AB_ATTR_CALLS = 4;
   private static final boolean NULL_IS_OK = true;
   BandStructure.MultiBand all_bands;
   BandStructure.ByteBand archive_magic;
   BandStructure.IntBand archive_header_0;
   BandStructure.IntBand archive_header_S;
   BandStructure.IntBand archive_header_1;
   BandStructure.ByteBand band_headers;
   BandStructure.MultiBand cp_bands;
   BandStructure.IntBand cp_Utf8_prefix;
   BandStructure.IntBand cp_Utf8_suffix;
   BandStructure.IntBand cp_Utf8_chars;
   BandStructure.IntBand cp_Utf8_big_suffix;
   BandStructure.MultiBand cp_Utf8_big_chars;
   BandStructure.IntBand cp_Int;
   BandStructure.IntBand cp_Float;
   BandStructure.IntBand cp_Long_hi;
   BandStructure.IntBand cp_Long_lo;
   BandStructure.IntBand cp_Double_hi;
   BandStructure.IntBand cp_Double_lo;
   BandStructure.CPRefBand cp_String;
   BandStructure.CPRefBand cp_Class;
   BandStructure.CPRefBand cp_Signature_form;
   BandStructure.CPRefBand cp_Signature_classes;
   BandStructure.CPRefBand cp_Descr_name;
   BandStructure.CPRefBand cp_Descr_type;
   BandStructure.CPRefBand cp_Field_class;
   BandStructure.CPRefBand cp_Field_desc;
   BandStructure.CPRefBand cp_Method_class;
   BandStructure.CPRefBand cp_Method_desc;
   BandStructure.CPRefBand cp_Imethod_class;
   BandStructure.CPRefBand cp_Imethod_desc;
   BandStructure.IntBand cp_MethodHandle_refkind;
   BandStructure.CPRefBand cp_MethodHandle_member;
   BandStructure.CPRefBand cp_MethodType;
   BandStructure.CPRefBand cp_BootstrapMethod_ref;
   BandStructure.IntBand cp_BootstrapMethod_arg_count;
   BandStructure.CPRefBand cp_BootstrapMethod_arg;
   BandStructure.CPRefBand cp_InvokeDynamic_spec;
   BandStructure.CPRefBand cp_InvokeDynamic_desc;
   BandStructure.MultiBand attr_definition_bands;
   BandStructure.ByteBand attr_definition_headers;
   BandStructure.CPRefBand attr_definition_name;
   BandStructure.CPRefBand attr_definition_layout;
   BandStructure.MultiBand ic_bands;
   BandStructure.CPRefBand ic_this_class;
   BandStructure.IntBand ic_flags;
   BandStructure.CPRefBand ic_outer_class;
   BandStructure.CPRefBand ic_name;
   BandStructure.MultiBand class_bands;
   BandStructure.CPRefBand class_this;
   BandStructure.CPRefBand class_super;
   BandStructure.IntBand class_interface_count;
   BandStructure.CPRefBand class_interface;
   BandStructure.IntBand class_field_count;
   BandStructure.IntBand class_method_count;
   BandStructure.CPRefBand field_descr;
   BandStructure.MultiBand field_attr_bands;
   BandStructure.IntBand field_flags_hi;
   BandStructure.IntBand field_flags_lo;
   BandStructure.IntBand field_attr_count;
   BandStructure.IntBand field_attr_indexes;
   BandStructure.IntBand field_attr_calls;
   BandStructure.CPRefBand field_ConstantValue_KQ;
   BandStructure.CPRefBand field_Signature_RS;
   BandStructure.MultiBand field_metadata_bands;
   BandStructure.MultiBand field_type_metadata_bands;
   BandStructure.CPRefBand method_descr;
   BandStructure.MultiBand method_attr_bands;
   BandStructure.IntBand method_flags_hi;
   BandStructure.IntBand method_flags_lo;
   BandStructure.IntBand method_attr_count;
   BandStructure.IntBand method_attr_indexes;
   BandStructure.IntBand method_attr_calls;
   BandStructure.IntBand method_Exceptions_N;
   BandStructure.CPRefBand method_Exceptions_RC;
   BandStructure.CPRefBand method_Signature_RS;
   BandStructure.MultiBand method_metadata_bands;
   BandStructure.IntBand method_MethodParameters_NB;
   BandStructure.CPRefBand method_MethodParameters_name_RUN;
   BandStructure.IntBand method_MethodParameters_flag_FH;
   BandStructure.MultiBand method_type_metadata_bands;
   BandStructure.MultiBand class_attr_bands;
   BandStructure.IntBand class_flags_hi;
   BandStructure.IntBand class_flags_lo;
   BandStructure.IntBand class_attr_count;
   BandStructure.IntBand class_attr_indexes;
   BandStructure.IntBand class_attr_calls;
   BandStructure.CPRefBand class_SourceFile_RUN;
   BandStructure.CPRefBand class_EnclosingMethod_RC;
   BandStructure.CPRefBand class_EnclosingMethod_RDN;
   BandStructure.CPRefBand class_Signature_RS;
   BandStructure.MultiBand class_metadata_bands;
   BandStructure.IntBand class_InnerClasses_N;
   BandStructure.CPRefBand class_InnerClasses_RC;
   BandStructure.IntBand class_InnerClasses_F;
   BandStructure.CPRefBand class_InnerClasses_outer_RCN;
   BandStructure.CPRefBand class_InnerClasses_name_RUN;
   BandStructure.IntBand class_ClassFile_version_minor_H;
   BandStructure.IntBand class_ClassFile_version_major_H;
   BandStructure.MultiBand class_type_metadata_bands;
   BandStructure.MultiBand code_bands;
   BandStructure.ByteBand code_headers;
   BandStructure.IntBand code_max_stack;
   BandStructure.IntBand code_max_na_locals;
   BandStructure.IntBand code_handler_count;
   BandStructure.IntBand code_handler_start_P;
   BandStructure.IntBand code_handler_end_PO;
   BandStructure.IntBand code_handler_catch_PO;
   BandStructure.CPRefBand code_handler_class_RCN;
   BandStructure.MultiBand code_attr_bands;
   BandStructure.IntBand code_flags_hi;
   BandStructure.IntBand code_flags_lo;
   BandStructure.IntBand code_attr_count;
   BandStructure.IntBand code_attr_indexes;
   BandStructure.IntBand code_attr_calls;
   BandStructure.MultiBand stackmap_bands;
   BandStructure.IntBand code_StackMapTable_N;
   BandStructure.IntBand code_StackMapTable_frame_T;
   BandStructure.IntBand code_StackMapTable_local_N;
   BandStructure.IntBand code_StackMapTable_stack_N;
   BandStructure.IntBand code_StackMapTable_offset;
   BandStructure.IntBand code_StackMapTable_T;
   BandStructure.CPRefBand code_StackMapTable_RC;
   BandStructure.IntBand code_StackMapTable_P;
   BandStructure.IntBand code_LineNumberTable_N;
   BandStructure.IntBand code_LineNumberTable_bci_P;
   BandStructure.IntBand code_LineNumberTable_line;
   BandStructure.IntBand code_LocalVariableTable_N;
   BandStructure.IntBand code_LocalVariableTable_bci_P;
   BandStructure.IntBand code_LocalVariableTable_span_O;
   BandStructure.CPRefBand code_LocalVariableTable_name_RU;
   BandStructure.CPRefBand code_LocalVariableTable_type_RS;
   BandStructure.IntBand code_LocalVariableTable_slot;
   BandStructure.IntBand code_LocalVariableTypeTable_N;
   BandStructure.IntBand code_LocalVariableTypeTable_bci_P;
   BandStructure.IntBand code_LocalVariableTypeTable_span_O;
   BandStructure.CPRefBand code_LocalVariableTypeTable_name_RU;
   BandStructure.CPRefBand code_LocalVariableTypeTable_type_RS;
   BandStructure.IntBand code_LocalVariableTypeTable_slot;
   BandStructure.MultiBand code_type_metadata_bands;
   BandStructure.MultiBand bc_bands;
   BandStructure.ByteBand bc_codes;
   BandStructure.IntBand bc_case_count;
   BandStructure.IntBand bc_case_value;
   BandStructure.ByteBand bc_byte;
   BandStructure.IntBand bc_short;
   BandStructure.IntBand bc_local;
   BandStructure.IntBand bc_label;
   BandStructure.CPRefBand bc_intref;
   BandStructure.CPRefBand bc_floatref;
   BandStructure.CPRefBand bc_longref;
   BandStructure.CPRefBand bc_doubleref;
   BandStructure.CPRefBand bc_stringref;
   BandStructure.CPRefBand bc_loadablevalueref;
   BandStructure.CPRefBand bc_classref;
   BandStructure.CPRefBand bc_fieldref;
   BandStructure.CPRefBand bc_methodref;
   BandStructure.CPRefBand bc_imethodref;
   BandStructure.CPRefBand bc_indyref;
   BandStructure.CPRefBand bc_thisfield;
   BandStructure.CPRefBand bc_superfield;
   BandStructure.CPRefBand bc_thismethod;
   BandStructure.CPRefBand bc_supermethod;
   BandStructure.IntBand bc_initref;
   BandStructure.CPRefBand bc_escref;
   BandStructure.IntBand bc_escrefsize;
   BandStructure.IntBand bc_escsize;
   BandStructure.ByteBand bc_escbyte;
   BandStructure.MultiBand file_bands;
   BandStructure.CPRefBand file_name;
   BandStructure.IntBand file_size_hi;
   BandStructure.IntBand file_size_lo;
   BandStructure.IntBand file_modtime;
   BandStructure.IntBand file_options;
   BandStructure.ByteBand file_bits;
   protected BandStructure.MultiBand[] metadataBands;
   protected BandStructure.MultiBand[] typeMetadataBands;
   public static final int ADH_CONTEXT_MASK = 3;
   public static final int ADH_BIT_SHIFT = 2;
   public static final int ADH_BIT_IS_LSB = 1;
   public static final int ATTR_INDEX_OVERFLOW = -1;
   public int[] attrIndexLimit;
   protected long[] attrFlagMask;
   protected long[] attrDefSeen;
   protected int[] attrOverflowMask;
   protected int attrClassFileVersionMask;
   protected Map<Attribute.Layout, BandStructure.Band[]> attrBandTable;
   protected final Attribute.Layout attrCodeEmpty;
   protected final Attribute.Layout attrInnerClassesEmpty;
   protected final Attribute.Layout attrClassFileVersion;
   protected final Attribute.Layout attrConstantValue;
   Map<Attribute.Layout, Integer> attrIndexTable;
   protected List<List<Attribute.Layout>> attrDefs;
   protected BandStructure.MultiBand[] attrBands;
   private static final int[][] shortCodeLimits;
   public final int shortCodeHeader_h_limit;
   static final int LONG_CODE_HEADER = 0;
   static int nextSeqForDebug;
   static File dumpDir;
   private Map<BandStructure.Band, BandStructure.Band> prevForAssertMap;
   static LinkedList<String> bandSequenceList;

   protected abstract ConstantPool.Index getCPIndex(byte var1);

   public void initHighestClassVersion(Package.Version var1) throws IOException {
      if (this.highestClassVersion != null) {
         throw new IOException("Highest class major version is already initialized to " + this.highestClassVersion + "; new setting is " + var1);
      } else {
         this.highestClassVersion = var1;
         this.adjustToClassVersion();
      }
   }

   public Package.Version getHighestClassVersion() {
      return this.highestClassVersion;
   }

   protected BandStructure() {
      this.verbose = this.p200.getInteger("com.sun.java.util.jar.pack.verbose");
      this.effort = this.p200.getInteger("pack.effort");
      if (this.effort == 0) {
         this.effort = 5;
      }

      this.optDumpBands = this.p200.getBoolean("com.sun.java.util.jar.pack.dump.bands");
      this.optDebugBands = this.p200.getBoolean("com.sun.java.util.jar.pack.debug.bands");
      this.optVaryCodings = !this.p200.getBoolean("com.sun.java.util.jar.pack.no.vary.codings");
      this.optBigStrings = !this.p200.getBoolean("com.sun.java.util.jar.pack.no.big.strings");
      this.highestClassVersion = null;
      this.isReader = this instanceof PackageReader;
      this.allKQBands = new ArrayList();
      this.needPredefIndex = new ArrayList();
      this.all_bands = (BandStructure.MultiBand)(new BandStructure.MultiBand("(package)", UNSIGNED5)).init();
      this.archive_magic = this.all_bands.newByteBand("archive_magic");
      this.archive_header_0 = this.all_bands.newIntBand("archive_header_0", UNSIGNED5);
      this.archive_header_S = this.all_bands.newIntBand("archive_header_S", UNSIGNED5);
      this.archive_header_1 = this.all_bands.newIntBand("archive_header_1", UNSIGNED5);
      this.band_headers = this.all_bands.newByteBand("band_headers");
      this.cp_bands = this.all_bands.newMultiBand("(constant_pool)", DELTA5);
      this.cp_Utf8_prefix = this.cp_bands.newIntBand("cp_Utf8_prefix");
      this.cp_Utf8_suffix = this.cp_bands.newIntBand("cp_Utf8_suffix", UNSIGNED5);
      this.cp_Utf8_chars = this.cp_bands.newIntBand("cp_Utf8_chars", CHAR3);
      this.cp_Utf8_big_suffix = this.cp_bands.newIntBand("cp_Utf8_big_suffix");
      this.cp_Utf8_big_chars = this.cp_bands.newMultiBand("(cp_Utf8_big_chars)", DELTA5);
      this.cp_Int = this.cp_bands.newIntBand("cp_Int", UDELTA5);
      this.cp_Float = this.cp_bands.newIntBand("cp_Float", UDELTA5);
      this.cp_Long_hi = this.cp_bands.newIntBand("cp_Long_hi", UDELTA5);
      this.cp_Long_lo = this.cp_bands.newIntBand("cp_Long_lo");
      this.cp_Double_hi = this.cp_bands.newIntBand("cp_Double_hi", UDELTA5);
      this.cp_Double_lo = this.cp_bands.newIntBand("cp_Double_lo");
      this.cp_String = this.cp_bands.newCPRefBand("cp_String", UDELTA5, (byte)1);
      this.cp_Class = this.cp_bands.newCPRefBand("cp_Class", UDELTA5, (byte)1);
      this.cp_Signature_form = this.cp_bands.newCPRefBand("cp_Signature_form", (byte)1);
      this.cp_Signature_classes = this.cp_bands.newCPRefBand("cp_Signature_classes", UDELTA5, (byte)7);
      this.cp_Descr_name = this.cp_bands.newCPRefBand("cp_Descr_name", (byte)1);
      this.cp_Descr_type = this.cp_bands.newCPRefBand("cp_Descr_type", UDELTA5, (byte)13);
      this.cp_Field_class = this.cp_bands.newCPRefBand("cp_Field_class", (byte)7);
      this.cp_Field_desc = this.cp_bands.newCPRefBand("cp_Field_desc", UDELTA5, (byte)12);
      this.cp_Method_class = this.cp_bands.newCPRefBand("cp_Method_class", (byte)7);
      this.cp_Method_desc = this.cp_bands.newCPRefBand("cp_Method_desc", UDELTA5, (byte)12);
      this.cp_Imethod_class = this.cp_bands.newCPRefBand("cp_Imethod_class", (byte)7);
      this.cp_Imethod_desc = this.cp_bands.newCPRefBand("cp_Imethod_desc", UDELTA5, (byte)12);
      this.cp_MethodHandle_refkind = this.cp_bands.newIntBand("cp_MethodHandle_refkind", DELTA5);
      this.cp_MethodHandle_member = this.cp_bands.newCPRefBand("cp_MethodHandle_member", UDELTA5, (byte)52);
      this.cp_MethodType = this.cp_bands.newCPRefBand("cp_MethodType", UDELTA5, (byte)13);
      this.cp_BootstrapMethod_ref = this.cp_bands.newCPRefBand("cp_BootstrapMethod_ref", DELTA5, (byte)15);
      this.cp_BootstrapMethod_arg_count = this.cp_bands.newIntBand("cp_BootstrapMethod_arg_count", UDELTA5);
      this.cp_BootstrapMethod_arg = this.cp_bands.newCPRefBand("cp_BootstrapMethod_arg", DELTA5, (byte)51);
      this.cp_InvokeDynamic_spec = this.cp_bands.newCPRefBand("cp_InvokeDynamic_spec", DELTA5, (byte)17);
      this.cp_InvokeDynamic_desc = this.cp_bands.newCPRefBand("cp_InvokeDynamic_desc", UDELTA5, (byte)12);
      this.attr_definition_bands = this.all_bands.newMultiBand("(attr_definition_bands)", UNSIGNED5);
      this.attr_definition_headers = this.attr_definition_bands.newByteBand("attr_definition_headers");
      this.attr_definition_name = this.attr_definition_bands.newCPRefBand("attr_definition_name", (byte)1);
      this.attr_definition_layout = this.attr_definition_bands.newCPRefBand("attr_definition_layout", (byte)1);
      this.ic_bands = this.all_bands.newMultiBand("(ic_bands)", DELTA5);
      this.ic_this_class = this.ic_bands.newCPRefBand("ic_this_class", UDELTA5, (byte)7);
      this.ic_flags = this.ic_bands.newIntBand("ic_flags", UNSIGNED5);
      this.ic_outer_class = this.ic_bands.newCPRefBand("ic_outer_class", DELTA5, (byte)7, true);
      this.ic_name = this.ic_bands.newCPRefBand("ic_name", DELTA5, (byte)1, true);
      this.class_bands = this.all_bands.newMultiBand("(class_bands)", DELTA5);
      this.class_this = this.class_bands.newCPRefBand("class_this", (byte)7);
      this.class_super = this.class_bands.newCPRefBand("class_super", (byte)7);
      this.class_interface_count = this.class_bands.newIntBand("class_interface_count");
      this.class_interface = this.class_bands.newCPRefBand("class_interface", (byte)7);
      this.class_field_count = this.class_bands.newIntBand("class_field_count");
      this.class_method_count = this.class_bands.newIntBand("class_method_count");
      this.field_descr = this.class_bands.newCPRefBand("field_descr", (byte)12);
      this.field_attr_bands = this.class_bands.newMultiBand("(field_attr_bands)", UNSIGNED5);
      this.field_flags_hi = this.field_attr_bands.newIntBand("field_flags_hi");
      this.field_flags_lo = this.field_attr_bands.newIntBand("field_flags_lo");
      this.field_attr_count = this.field_attr_bands.newIntBand("field_attr_count");
      this.field_attr_indexes = this.field_attr_bands.newIntBand("field_attr_indexes");
      this.field_attr_calls = this.field_attr_bands.newIntBand("field_attr_calls");
      this.field_ConstantValue_KQ = this.field_attr_bands.newCPRefBand("field_ConstantValue_KQ", (byte)53);
      this.field_Signature_RS = this.field_attr_bands.newCPRefBand("field_Signature_RS", (byte)13);
      this.field_metadata_bands = this.field_attr_bands.newMultiBand("(field_metadata_bands)", UNSIGNED5);
      this.field_type_metadata_bands = this.field_attr_bands.newMultiBand("(field_type_metadata_bands)", UNSIGNED5);
      this.method_descr = this.class_bands.newCPRefBand("method_descr", MDELTA5, (byte)12);
      this.method_attr_bands = this.class_bands.newMultiBand("(method_attr_bands)", UNSIGNED5);
      this.method_flags_hi = this.method_attr_bands.newIntBand("method_flags_hi");
      this.method_flags_lo = this.method_attr_bands.newIntBand("method_flags_lo");
      this.method_attr_count = this.method_attr_bands.newIntBand("method_attr_count");
      this.method_attr_indexes = this.method_attr_bands.newIntBand("method_attr_indexes");
      this.method_attr_calls = this.method_attr_bands.newIntBand("method_attr_calls");
      this.method_Exceptions_N = this.method_attr_bands.newIntBand("method_Exceptions_N");
      this.method_Exceptions_RC = this.method_attr_bands.newCPRefBand("method_Exceptions_RC", (byte)7);
      this.method_Signature_RS = this.method_attr_bands.newCPRefBand("method_Signature_RS", (byte)13);
      this.method_metadata_bands = this.method_attr_bands.newMultiBand("(method_metadata_bands)", UNSIGNED5);
      this.method_MethodParameters_NB = this.method_attr_bands.newIntBand("method_MethodParameters_NB", BYTE1);
      this.method_MethodParameters_name_RUN = this.method_attr_bands.newCPRefBand("method_MethodParameters_name_RUN", UNSIGNED5, (byte)1, true);
      this.method_MethodParameters_flag_FH = this.method_attr_bands.newIntBand("method_MethodParameters_flag_FH");
      this.method_type_metadata_bands = this.method_attr_bands.newMultiBand("(method_type_metadata_bands)", UNSIGNED5);
      this.class_attr_bands = this.class_bands.newMultiBand("(class_attr_bands)", UNSIGNED5);
      this.class_flags_hi = this.class_attr_bands.newIntBand("class_flags_hi");
      this.class_flags_lo = this.class_attr_bands.newIntBand("class_flags_lo");
      this.class_attr_count = this.class_attr_bands.newIntBand("class_attr_count");
      this.class_attr_indexes = this.class_attr_bands.newIntBand("class_attr_indexes");
      this.class_attr_calls = this.class_attr_bands.newIntBand("class_attr_calls");
      this.class_SourceFile_RUN = this.class_attr_bands.newCPRefBand("class_SourceFile_RUN", UNSIGNED5, (byte)1, true);
      this.class_EnclosingMethod_RC = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RC", (byte)7);
      this.class_EnclosingMethod_RDN = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RDN", UNSIGNED5, (byte)12, true);
      this.class_Signature_RS = this.class_attr_bands.newCPRefBand("class_Signature_RS", (byte)13);
      this.class_metadata_bands = this.class_attr_bands.newMultiBand("(class_metadata_bands)", UNSIGNED5);
      this.class_InnerClasses_N = this.class_attr_bands.newIntBand("class_InnerClasses_N");
      this.class_InnerClasses_RC = this.class_attr_bands.newCPRefBand("class_InnerClasses_RC", (byte)7);
      this.class_InnerClasses_F = this.class_attr_bands.newIntBand("class_InnerClasses_F");
      this.class_InnerClasses_outer_RCN = this.class_attr_bands.newCPRefBand("class_InnerClasses_outer_RCN", UNSIGNED5, (byte)7, true);
      this.class_InnerClasses_name_RUN = this.class_attr_bands.newCPRefBand("class_InnerClasses_name_RUN", UNSIGNED5, (byte)1, true);
      this.class_ClassFile_version_minor_H = this.class_attr_bands.newIntBand("class_ClassFile_version_minor_H");
      this.class_ClassFile_version_major_H = this.class_attr_bands.newIntBand("class_ClassFile_version_major_H");
      this.class_type_metadata_bands = this.class_attr_bands.newMultiBand("(class_type_metadata_bands)", UNSIGNED5);
      this.code_bands = this.class_bands.newMultiBand("(code_bands)", UNSIGNED5);
      this.code_headers = this.code_bands.newByteBand("code_headers");
      this.code_max_stack = this.code_bands.newIntBand("code_max_stack", UNSIGNED5);
      this.code_max_na_locals = this.code_bands.newIntBand("code_max_na_locals", UNSIGNED5);
      this.code_handler_count = this.code_bands.newIntBand("code_handler_count", UNSIGNED5);
      this.code_handler_start_P = this.code_bands.newIntBand("code_handler_start_P", BCI5);
      this.code_handler_end_PO = this.code_bands.newIntBand("code_handler_end_PO", BRANCH5);
      this.code_handler_catch_PO = this.code_bands.newIntBand("code_handler_catch_PO", BRANCH5);
      this.code_handler_class_RCN = this.code_bands.newCPRefBand("code_handler_class_RCN", UNSIGNED5, (byte)7, true);
      this.code_attr_bands = this.class_bands.newMultiBand("(code_attr_bands)", UNSIGNED5);
      this.code_flags_hi = this.code_attr_bands.newIntBand("code_flags_hi");
      this.code_flags_lo = this.code_attr_bands.newIntBand("code_flags_lo");
      this.code_attr_count = this.code_attr_bands.newIntBand("code_attr_count");
      this.code_attr_indexes = this.code_attr_bands.newIntBand("code_attr_indexes");
      this.code_attr_calls = this.code_attr_bands.newIntBand("code_attr_calls");
      this.stackmap_bands = this.code_attr_bands.newMultiBand("(StackMapTable_bands)", UNSIGNED5);
      this.code_StackMapTable_N = this.stackmap_bands.newIntBand("code_StackMapTable_N");
      this.code_StackMapTable_frame_T = this.stackmap_bands.newIntBand("code_StackMapTable_frame_T", BYTE1);
      this.code_StackMapTable_local_N = this.stackmap_bands.newIntBand("code_StackMapTable_local_N");
      this.code_StackMapTable_stack_N = this.stackmap_bands.newIntBand("code_StackMapTable_stack_N");
      this.code_StackMapTable_offset = this.stackmap_bands.newIntBand("code_StackMapTable_offset", UNSIGNED5);
      this.code_StackMapTable_T = this.stackmap_bands.newIntBand("code_StackMapTable_T", BYTE1);
      this.code_StackMapTable_RC = this.stackmap_bands.newCPRefBand("code_StackMapTable_RC", (byte)7);
      this.code_StackMapTable_P = this.stackmap_bands.newIntBand("code_StackMapTable_P", BCI5);
      this.code_LineNumberTable_N = this.code_attr_bands.newIntBand("code_LineNumberTable_N");
      this.code_LineNumberTable_bci_P = this.code_attr_bands.newIntBand("code_LineNumberTable_bci_P", BCI5);
      this.code_LineNumberTable_line = this.code_attr_bands.newIntBand("code_LineNumberTable_line");
      this.code_LocalVariableTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTable_N");
      this.code_LocalVariableTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTable_bci_P", BCI5);
      this.code_LocalVariableTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTable_span_O", BRANCH5);
      this.code_LocalVariableTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_name_RU", (byte)1);
      this.code_LocalVariableTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_type_RS", (byte)13);
      this.code_LocalVariableTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTable_slot");
      this.code_LocalVariableTypeTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_N");
      this.code_LocalVariableTypeTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_bci_P", BCI5);
      this.code_LocalVariableTypeTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_span_O", BRANCH5);
      this.code_LocalVariableTypeTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_name_RU", (byte)1);
      this.code_LocalVariableTypeTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_type_RS", (byte)13);
      this.code_LocalVariableTypeTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_slot");
      this.code_type_metadata_bands = this.code_attr_bands.newMultiBand("(code_type_metadata_bands)", UNSIGNED5);
      this.bc_bands = this.all_bands.newMultiBand("(byte_codes)", UNSIGNED5);
      this.bc_codes = this.bc_bands.newByteBand("bc_codes");
      this.bc_case_count = this.bc_bands.newIntBand("bc_case_count");
      this.bc_case_value = this.bc_bands.newIntBand("bc_case_value", DELTA5);
      this.bc_byte = this.bc_bands.newByteBand("bc_byte");
      this.bc_short = this.bc_bands.newIntBand("bc_short", DELTA5);
      this.bc_local = this.bc_bands.newIntBand("bc_local");
      this.bc_label = this.bc_bands.newIntBand("bc_label", BRANCH5);
      this.bc_intref = this.bc_bands.newCPRefBand("bc_intref", DELTA5, (byte)3);
      this.bc_floatref = this.bc_bands.newCPRefBand("bc_floatref", DELTA5, (byte)4);
      this.bc_longref = this.bc_bands.newCPRefBand("bc_longref", DELTA5, (byte)5);
      this.bc_doubleref = this.bc_bands.newCPRefBand("bc_doubleref", DELTA5, (byte)6);
      this.bc_stringref = this.bc_bands.newCPRefBand("bc_stringref", DELTA5, (byte)8);
      this.bc_loadablevalueref = this.bc_bands.newCPRefBand("bc_loadablevalueref", DELTA5, (byte)51);
      this.bc_classref = this.bc_bands.newCPRefBand("bc_classref", UNSIGNED5, (byte)7, true);
      this.bc_fieldref = this.bc_bands.newCPRefBand("bc_fieldref", DELTA5, (byte)9);
      this.bc_methodref = this.bc_bands.newCPRefBand("bc_methodref", (byte)10);
      this.bc_imethodref = this.bc_bands.newCPRefBand("bc_imethodref", DELTA5, (byte)11);
      this.bc_indyref = this.bc_bands.newCPRefBand("bc_indyref", DELTA5, (byte)18);
      this.bc_thisfield = this.bc_bands.newCPRefBand("bc_thisfield", (byte)0);
      this.bc_superfield = this.bc_bands.newCPRefBand("bc_superfield", (byte)0);
      this.bc_thismethod = this.bc_bands.newCPRefBand("bc_thismethod", (byte)0);
      this.bc_supermethod = this.bc_bands.newCPRefBand("bc_supermethod", (byte)0);
      this.bc_initref = this.bc_bands.newIntBand("bc_initref");
      this.bc_escref = this.bc_bands.newCPRefBand("bc_escref", (byte)50);
      this.bc_escrefsize = this.bc_bands.newIntBand("bc_escrefsize");
      this.bc_escsize = this.bc_bands.newIntBand("bc_escsize");
      this.bc_escbyte = this.bc_bands.newByteBand("bc_escbyte");
      this.file_bands = this.all_bands.newMultiBand("(file_bands)", UNSIGNED5);
      this.file_name = this.file_bands.newCPRefBand("file_name", (byte)1);
      this.file_size_hi = this.file_bands.newIntBand("file_size_hi");
      this.file_size_lo = this.file_bands.newIntBand("file_size_lo");
      this.file_modtime = this.file_bands.newIntBand("file_modtime", DELTA5);
      this.file_options = this.file_bands.newIntBand("file_options");
      this.file_bits = this.file_bands.newByteBand("file_bits");
      this.metadataBands = new BandStructure.MultiBand[4];
      this.metadataBands[0] = this.class_metadata_bands;
      this.metadataBands[1] = this.field_metadata_bands;
      this.metadataBands[2] = this.method_metadata_bands;
      this.typeMetadataBands = new BandStructure.MultiBand[4];
      this.typeMetadataBands[0] = this.class_type_metadata_bands;
      this.typeMetadataBands[1] = this.field_type_metadata_bands;
      this.typeMetadataBands[2] = this.method_type_metadata_bands;
      this.typeMetadataBands[3] = this.code_type_metadata_bands;
      this.attrIndexLimit = new int[4];
      this.attrFlagMask = new long[4];
      this.attrDefSeen = new long[4];
      this.attrOverflowMask = new int[4];
      this.attrBandTable = new HashMap();
      this.attrIndexTable = new HashMap();
      this.attrDefs = new FixedList(4);

      int var1;
      for(var1 = 0; var1 < 4; ++var1) {
         assert this.attrIndexLimit[var1] == 0;

         this.attrIndexLimit[var1] = 32;
         this.attrDefs.set(var1, new ArrayList(Collections.nCopies(this.attrIndexLimit[var1], (Attribute.Layout)null)));
      }

      this.attrInnerClassesEmpty = this.predefineAttribute(23, 0, (BandStructure.Band[])null, "InnerClasses", "");

      assert this.attrInnerClassesEmpty == Package.attrInnerClassesEmpty;

      this.predefineAttribute(17, 0, new BandStructure.Band[]{this.class_SourceFile_RUN}, "SourceFile", "RUNH");
      this.predefineAttribute(18, 0, new BandStructure.Band[]{this.class_EnclosingMethod_RC, this.class_EnclosingMethod_RDN}, "EnclosingMethod", "RCHRDNH");
      this.attrClassFileVersion = this.predefineAttribute(24, 0, new BandStructure.Band[]{this.class_ClassFile_version_minor_H, this.class_ClassFile_version_major_H}, ".ClassFile.version", "HH");
      this.predefineAttribute(19, 0, new BandStructure.Band[]{this.class_Signature_RS}, "Signature", "RSH");
      this.predefineAttribute(20, 0, (BandStructure.Band[])null, "Deprecated", "");
      this.predefineAttribute(16, 0, (BandStructure.Band[])null, ".Overflow", "");
      this.attrConstantValue = this.predefineAttribute(17, 1, new BandStructure.Band[]{this.field_ConstantValue_KQ}, "ConstantValue", "KQH");
      this.predefineAttribute(19, 1, new BandStructure.Band[]{this.field_Signature_RS}, "Signature", "RSH");
      this.predefineAttribute(20, 1, (BandStructure.Band[])null, "Deprecated", "");
      this.predefineAttribute(16, 1, (BandStructure.Band[])null, ".Overflow", "");
      this.attrCodeEmpty = this.predefineAttribute(17, 2, (BandStructure.Band[])null, "Code", "");
      this.predefineAttribute(18, 2, new BandStructure.Band[]{this.method_Exceptions_N, this.method_Exceptions_RC}, "Exceptions", "NH[RCH]");
      this.predefineAttribute(26, 2, new BandStructure.Band[]{this.method_MethodParameters_NB, this.method_MethodParameters_name_RUN, this.method_MethodParameters_flag_FH}, "MethodParameters", "NB[RUNHFH]");

      assert this.attrCodeEmpty == Package.attrCodeEmpty;

      this.predefineAttribute(19, 2, new BandStructure.Band[]{this.method_Signature_RS}, "Signature", "RSH");
      this.predefineAttribute(20, 2, (BandStructure.Band[])null, "Deprecated", "");
      this.predefineAttribute(16, 2, (BandStructure.Band[])null, ".Overflow", "");

      for(var1 = 0; var1 < 4; ++var1) {
         BandStructure.MultiBand var2 = this.metadataBands[var1];
         if (var1 != 3) {
            this.predefineAttribute(21, Constants.ATTR_CONTEXT_NAME[var1] + "_RVA_", var2, Attribute.lookup((Map)null, var1, "RuntimeVisibleAnnotations"));
            this.predefineAttribute(22, Constants.ATTR_CONTEXT_NAME[var1] + "_RIA_", var2, Attribute.lookup((Map)null, var1, "RuntimeInvisibleAnnotations"));
            if (var1 == 2) {
               this.predefineAttribute(23, "method_RVPA_", var2, Attribute.lookup((Map)null, var1, "RuntimeVisibleParameterAnnotations"));
               this.predefineAttribute(24, "method_RIPA_", var2, Attribute.lookup((Map)null, var1, "RuntimeInvisibleParameterAnnotations"));
               this.predefineAttribute(25, "method_AD_", var2, Attribute.lookup((Map)null, var1, "AnnotationDefault"));
            }
         }

         BandStructure.MultiBand var3 = this.typeMetadataBands[var1];
         this.predefineAttribute(27, Constants.ATTR_CONTEXT_NAME[var1] + "_RVTA_", var3, Attribute.lookup((Map)null, var1, "RuntimeVisibleTypeAnnotations"));
         this.predefineAttribute(28, Constants.ATTR_CONTEXT_NAME[var1] + "_RITA_", var3, Attribute.lookup((Map)null, var1, "RuntimeInvisibleTypeAnnotations"));
      }

      Attribute.Layout var4 = Attribute.lookup((Map)null, 3, "StackMapTable").layout();
      this.predefineAttribute(0, 3, this.stackmap_bands.toArray(), var4.name(), var4.layout());
      this.predefineAttribute(1, 3, new BandStructure.Band[]{this.code_LineNumberTable_N, this.code_LineNumberTable_bci_P, this.code_LineNumberTable_line}, "LineNumberTable", "NH[PHH]");
      this.predefineAttribute(2, 3, new BandStructure.Band[]{this.code_LocalVariableTable_N, this.code_LocalVariableTable_bci_P, this.code_LocalVariableTable_span_O, this.code_LocalVariableTable_name_RU, this.code_LocalVariableTable_type_RS, this.code_LocalVariableTable_slot}, "LocalVariableTable", "NH[PHOHRUHRSHH]");
      this.predefineAttribute(3, 3, new BandStructure.Band[]{this.code_LocalVariableTypeTable_N, this.code_LocalVariableTypeTable_bci_P, this.code_LocalVariableTypeTable_span_O, this.code_LocalVariableTypeTable_name_RU, this.code_LocalVariableTypeTable_type_RS, this.code_LocalVariableTypeTable_slot}, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
      this.predefineAttribute(16, 3, (BandStructure.Band[])null, ".Overflow", "");

      int var5;
      for(var5 = 0; var5 < 4; ++var5) {
         this.attrDefSeen[var5] = 0L;
      }

      for(var5 = 0; var5 < 4; ++var5) {
         this.attrOverflowMask[var5] = 65536;
         this.attrIndexLimit[var5] = 0;
      }

      this.attrClassFileVersionMask = 16777216;
      this.attrBands = new BandStructure.MultiBand[4];
      this.attrBands[0] = this.class_attr_bands;
      this.attrBands[1] = this.field_attr_bands;
      this.attrBands[2] = this.method_attr_bands;
      this.attrBands[3] = this.code_attr_bands;
      this.shortCodeHeader_h_limit = shortCodeLimits.length;
   }

   public static Coding codingForIndex(int var0) {
      return var0 < basicCodings.length ? basicCodings[var0] : null;
   }

   public static int indexOf(Coding var0) {
      Integer var1 = (Integer)basicCodingIndexes.get(var0);
      return var1 == null ? 0 : var1;
   }

   public static Coding[] getBasicCodings() {
      return (Coding[])basicCodings.clone();
   }

   protected CodingMethod getBandHeader(int var1, Coding var2) {
      CodingMethod[] var3 = new CodingMethod[]{null};
      this.bandHeaderBytes[--this.bandHeaderBytePos] = (byte)var1;
      this.bandHeaderBytePos0 = this.bandHeaderBytePos;
      this.bandHeaderBytePos = parseMetaCoding(this.bandHeaderBytes, this.bandHeaderBytePos, var2, var3);
      return var3[0];
   }

   public static int parseMetaCoding(byte[] var0, int var1, Coding var2, CodingMethod[] var3) {
      if ((var0[var1] & 255) == 0) {
         var3[0] = var2;
         return var1 + 1;
      } else {
         int var4 = Coding.parseMetaCoding(var0, var1, var2, var3);
         if (var4 > var1) {
            return var4;
         } else {
            var4 = PopulationCoding.parseMetaCoding(var0, var1, var2, var3);
            if (var4 > var1) {
               return var4;
            } else {
               var4 = AdaptiveCoding.parseMetaCoding(var0, var1, var2, var3);
               if (var4 > var1) {
                  return var4;
               } else {
                  throw new RuntimeException("Bad meta-coding op " + (var0[var1] & 255));
               }
            }
         }
      }
   }

   static boolean phaseIsRead(int var0) {
      return var0 % 2 == 0;
   }

   static int phaseCmp(int var0, int var1) {
      assert var0 % 2 == var1 % 2 || var0 % 8 == 0 || var1 % 8 == 0;

      return var0 - var1;
   }

   static int getIntTotal(int[] var0) {
      int var1 = 0;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1 += var0[var2];
      }

      return var1;
   }

   int encodeRef(ConstantPool.Entry var1, ConstantPool.Index var2) {
      if (var2 == null) {
         throw new RuntimeException("null index for " + var1.stringValue());
      } else {
         int var3 = var2.indexOf(var1);
         if (this.verbose > 2) {
            Utils.log.fine("putRef " + var3 + " => " + var1);
         }

         return var3;
      }
   }

   ConstantPool.Entry decodeRef(int var1, ConstantPool.Index var2) {
      if (var1 < 0 || var1 >= var2.size()) {
         Utils.log.warning("decoding bad ref " + var1 + " in " + var2);
      }

      ConstantPool.Entry var3 = var2.getEntry(var1);
      if (this.verbose > 2) {
         Utils.log.fine("getRef " + var1 + " => " + var3);
      }

      return var3;
   }

   protected CodingChooser getCodingChooser() {
      if (this.codingChooser == null) {
         this.codingChooser = new CodingChooser(this.effort, basicCodings);
         if (this.codingChooser.stress != null && this instanceof PackageWriter) {
            ArrayList var1 = ((PackageWriter)this).pkg.classes;
            if (!var1.isEmpty()) {
               Package.Class var2 = (Package.Class)var1.get(0);
               this.codingChooser.addStressSeed(var2.getName().hashCode());
            }
         }
      }

      return this.codingChooser;
   }

   public CodingMethod chooseCoding(int[] var1, int var2, int var3, Coding var4, String var5, int[] var6) {
      assert this.optVaryCodings;

      if (this.effort <= 1) {
         return var4;
      } else {
         CodingChooser var7 = this.getCodingChooser();
         if (this.verbose > 1 || var7.verbose > 1) {
            Utils.log.fine("--- chooseCoding " + var5);
         }

         return var7.choose(var1, var2, var3, var4, var6);
      }
   }

   protected static int decodeEscapeValue(int var0, Coding var1) {
      if (var1.B() != 1 && var1.L() != 0) {
         int var2;
         if (var1.S() != 0) {
            if (-256 <= var0 && var0 <= -1 && var1.min() <= -256) {
               var2 = -1 - var0;
               if ($assertionsDisabled || var2 >= 0 && var2 < 256) {
                  return var2;
               }

               throw new AssertionError();
            }
         } else {
            var2 = var1.L();
            if (var2 <= var0 && var0 <= var2 + 255 && var1.max() >= var2 + 255) {
               int var3 = var0 - var2;
               if ($assertionsDisabled || var3 >= 0 && var3 < 256) {
                  return var3;
               }

               throw new AssertionError();
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static int encodeEscapeValue(int var0, Coding var1) {
      assert var0 >= 0 && var0 < 256;

      assert var1.B() > 1 && var1.L() > 0;

      int var2;
      if (var1.S() != 0) {
         assert var1.min() <= -256;

         var2 = -1 - var0;
      } else {
         int var3 = var1.L();

         assert var1.max() >= var3 + 255;

         var2 = var0 + var3;
      }

      assert decodeEscapeValue(var2, var1) == var0 : var1 + " XB=" + var0 + " X=" + var2;

      return var2;
   }

   void writeAllBandsTo(OutputStream var1) throws IOException {
      this.outputCounter = new BandStructure.ByteCounter(var1);
      BandStructure.ByteCounter var4 = this.outputCounter;
      this.all_bands.writeTo(var4);
      if (this.verbose > 0) {
         long var2 = this.outputCounter.getCount();
         Utils.log.info("Wrote total of " + var2 + " bytes.");

         assert var2 == this.archiveSize0 + this.archiveSize1;
      }

      this.outputCounter = null;
   }

   static BandStructure.IntBand getAttrBand(BandStructure.MultiBand var0, int var1) {
      BandStructure.IntBand var2 = (BandStructure.IntBand)var0.get(var1);
      switch(var1) {
      case 0:
         assert var2.name().endsWith("_flags_hi");
         break;
      case 1:
         assert var2.name().endsWith("_flags_lo");
         break;
      case 2:
         assert var2.name().endsWith("_attr_count");
         break;
      case 3:
         assert var2.name().endsWith("_attr_indexes");
         break;
      case 4:
         assert var2.name().endsWith("_attr_calls");
         break;
      default:
         assert false;
      }

      return var2;
   }

   protected void setBandIndexes() {
      Iterator var1 = this.needPredefIndex.iterator();

      while(var1.hasNext()) {
         Object[] var2 = (Object[])var1.next();
         BandStructure.CPRefBand var3 = (BandStructure.CPRefBand)var2[0];
         Byte var4 = (Byte)var2[1];
         var3.setIndex(this.getCPIndex(var4));
      }

      this.needPredefIndex = null;
      if (this.verbose > 3) {
         printCDecl(this.all_bands);
      }

   }

   protected void setBandIndex(BandStructure.CPRefBand var1, byte var2) {
      Object[] var3 = new Object[]{var1, var2};
      if (var2 == 53) {
         this.allKQBands.add(var1);
      } else if (this.needPredefIndex != null) {
         this.needPredefIndex.add(var3);
      } else {
         var1.setIndex(this.getCPIndex(var2));
      }

   }

   protected void setConstantValueIndex(Package.Class.Field var1) {
      ConstantPool.Index var2 = null;
      if (var1 != null) {
         byte var3 = var1.getLiteralTag();
         var2 = this.getCPIndex(var3);
         if (this.verbose > 2) {
            Utils.log.fine("setConstantValueIndex " + var1 + " " + ConstantPool.tagName(var3) + " => " + var2);
         }

         assert var2 != null;
      }

      Iterator var5 = this.allKQBands.iterator();

      while(var5.hasNext()) {
         BandStructure.CPRefBand var4 = (BandStructure.CPRefBand)var5.next();
         var4.setIndex(var2);
      }

   }

   private void adjustToClassVersion() throws IOException {
      if (this.getHighestClassVersion().lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
         if (this.verbose > 0) {
            Utils.log.fine("Legacy package version");
         }

         this.undefineAttribute(0, 3);
      }

   }

   protected void initAttrIndexLimit() {
      for(int var1 = 0; var1 < 4; ++var1) {
         assert this.attrIndexLimit[var1] == 0;

         this.attrIndexLimit[var1] = this.haveFlagsHi(var1) ? 63 : 32;
         List var2 = (List)this.attrDefs.get(var1);

         assert var2.size() == 32;

         int var3 = this.attrIndexLimit[var1] - var2.size();
         var2.addAll(Collections.nCopies(var3, (Attribute.Layout)null));
      }

   }

   protected boolean haveFlagsHi(int var1) {
      int var2 = 1 << 9 + var1;
      switch(var1) {
      case 0:
         assert var2 == 512;
         break;
      case 1:
         assert var2 == 1024;
         break;
      case 2:
         assert var2 == 2048;
         break;
      case 3:
         assert var2 == 4096;
         break;
      default:
         assert false;
      }

      return testBit(this.archiveOptions, var2);
   }

   protected List<Attribute.Layout> getPredefinedAttrs(int var1) {
      assert this.attrIndexLimit[var1] != 0;

      ArrayList var2 = new ArrayList(this.attrIndexLimit[var1]);

      for(int var3 = 0; var3 < this.attrIndexLimit[var1]; ++var3) {
         if (!testBit(this.attrDefSeen[var1], 1L << var3)) {
            Attribute.Layout var4 = (Attribute.Layout)((List)this.attrDefs.get(var1)).get(var3);
            if (var4 != null) {
               assert this.isPredefinedAttr(var1, var3);

               var2.add(var4);
            }
         }
      }

      return var2;
   }

   protected boolean isPredefinedAttr(int var1, int var2) {
      assert this.attrIndexLimit[var1] != 0;

      if (var2 >= this.attrIndexLimit[var1]) {
         return false;
      } else if (testBit(this.attrDefSeen[var1], 1L << var2)) {
         return false;
      } else {
         return ((List)this.attrDefs.get(var1)).get(var2) != null;
      }
   }

   protected void adjustSpecialAttrMasks() {
      this.attrClassFileVersionMask = (int)((long)this.attrClassFileVersionMask & ~this.attrDefSeen[0]);

      for(int var1 = 0; var1 < 4; ++var1) {
         int[] var10000 = this.attrOverflowMask;
         var10000[var1] = (int)((long)var10000[var1] & ~this.attrDefSeen[var1]);
      }

   }

   protected Attribute makeClassFileVersionAttr(Package.Version var1) {
      return this.attrClassFileVersion.addContent(var1.asBytes());
   }

   protected Package.Version parseClassFileVersionAttr(Attribute var1) {
      assert var1.layout() == this.attrClassFileVersion;

      assert var1.size() == 4;

      return Package.Version.of(var1.bytes());
   }

   private boolean assertBandOKForElems(BandStructure.Band[] var1, Attribute.Layout.Element[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         assert this.assertBandOKForElem(var1, var2[var3]);
      }

      return true;
   }

   private boolean assertBandOKForElem(BandStructure.Band[] var1, Attribute.Layout.Element var2) {
      BandStructure.Band var3 = null;
      if (var2.bandIndex != -1) {
         var3 = var1[var2.bandIndex];
      }

      Coding var4 = UNSIGNED5;
      boolean var5 = true;
      switch(var2.kind) {
      case 1:
         if (var2.flagTest((byte)1)) {
            var4 = SIGNED5;
         } else if (var2.len == 1) {
            var4 = BYTE1;
         }
         break;
      case 2:
         if (!var2.flagTest((byte)2)) {
            var4 = BCI5;
         } else {
            var4 = BRANCH5;
         }
         break;
      case 3:
         var4 = BRANCH5;
         break;
      case 4:
         if (var2.len == 1) {
            var4 = BYTE1;
         }
         break;
      case 5:
         if (var2.len == 1) {
            var4 = BYTE1;
         }

         this.assertBandOKForElems(var1, var2.body);
         break;
      case 6:
         var5 = false;

         assert var3 instanceof BandStructure.CPRefBand;

         assert ((BandStructure.CPRefBand)var3).nullOK == var2.flagTest((byte)4);
         break;
      case 7:
         if (var2.flagTest((byte)1)) {
            var4 = SIGNED5;
         } else if (var2.len == 1) {
            var4 = BYTE1;
         }

         this.assertBandOKForElems(var1, var2.body);
         break;
      case 8:
         assert var3 == null;

         this.assertBandOKForElems(var1, var2.body);
         return true;
      case 9:
         assert var3 == null;

         return true;
      case 10:
         assert var3 == null;

         this.assertBandOKForElems(var1, var2.body);
         return true;
      default:
         assert false;
      }

      assert var3.regularCoding == var4 : var2 + " // " + var3;

      assert !var5 || var3 instanceof BandStructure.IntBand;

      return true;
   }

   private Attribute.Layout predefineAttribute(int var1, int var2, BandStructure.Band[] var3, String var4, String var5) {
      Attribute.Layout var6 = Attribute.find(var2, var4, var5).layout();
      if (var1 >= 0) {
         this.setAttributeLayoutIndex(var6, var1);
      }

      if (var3 == null) {
         var3 = new BandStructure.Band[0];
      }

      assert this.attrBandTable.get(var6) == null;

      this.attrBandTable.put(var6, var3);

      assert var6.bandCount == var3.length : var6 + " // " + Arrays.asList(var3);

      assert this.assertBandOKForElems(var3, var6.elems);

      return var6;
   }

   private Attribute.Layout predefineAttribute(int var1, String var2, BandStructure.MultiBand var3, Attribute var4) {
      Attribute.Layout var5 = var4.layout();
      int var6 = var5.ctype();
      return this.predefineAttribute(var1, var6, this.makeNewAttributeBands(var2, var5, var3), var5.name(), var5.layout());
   }

   private void undefineAttribute(int var1, int var2) {
      if (this.verbose > 1) {
         System.out.println("Removing predefined " + Constants.ATTR_CONTEXT_NAME[var2] + " attribute on bit " + var1);
      }

      List var3 = (List)this.attrDefs.get(var2);
      Attribute.Layout var4 = (Attribute.Layout)var3.get(var1);

      assert var4 != null;

      var3.set(var1, (Object)null);
      this.attrIndexTable.put(var4, (Object)null);

      assert var1 < 64;

      long[] var10000 = this.attrDefSeen;
      var10000[var2] &= ~(1L << var1);
      var10000 = this.attrFlagMask;
      var10000[var2] &= ~(1L << var1);
      BandStructure.Band[] var5 = (BandStructure.Band[])this.attrBandTable.get(var4);

      for(int var6 = 0; var6 < var5.length; ++var6) {
         var5[var6].doneWithUnusedBand();
      }

   }

   void makeNewAttributeBands() {
      this.adjustSpecialAttrMasks();

      for(int var1 = 0; var1 < 4; ++var1) {
         String var2 = Constants.ATTR_CONTEXT_NAME[var1];
         BandStructure.MultiBand var3 = this.attrBands[var1];
         long var4 = this.attrDefSeen[var1];

         assert (var4 & ~this.attrFlagMask[var1]) == 0L;

         for(int var6 = 0; var6 < ((List)this.attrDefs.get(var1)).size(); ++var6) {
            Attribute.Layout var7 = (Attribute.Layout)((List)this.attrDefs.get(var1)).get(var6);
            if (var7 != null && var7.bandCount != 0) {
               if (var6 < this.attrIndexLimit[var1] && !testBit(var4, 1L << var6)) {
                  assert this.attrBandTable.get(var7) != null;
               } else {
                  int var8 = var3.size();
                  String var9 = var2 + "_" + var7.name() + "_";
                  if (this.verbose > 1) {
                     Utils.log.fine("Making new bands for " + var7);
                  }

                  BandStructure.Band[] var10 = this.makeNewAttributeBands(var9, var7, var3);

                  assert var10.length == var7.bandCount;

                  BandStructure.Band[] var11 = (BandStructure.Band[])this.attrBandTable.put(var7, var10);
                  if (var11 != null) {
                     for(int var12 = 0; var12 < var11.length; ++var12) {
                        var11[var12].doneWithUnusedBand();
                     }
                  }
               }
            }
         }
      }

   }

   private BandStructure.Band[] makeNewAttributeBands(String var1, Attribute.Layout var2, BandStructure.MultiBand var3) {
      int var4 = var3.size();
      this.makeNewAttributeBands(var1, var2.elems, var3);
      int var5 = var3.size() - var4;
      BandStructure.Band[] var6 = new BandStructure.Band[var5];

      for(int var7 = 0; var7 < var5; ++var7) {
         var6[var7] = var3.get(var4 + var7);
      }

      return var6;
   }

   private void makeNewAttributeBands(String var1, Attribute.Layout.Element[] var2, BandStructure.MultiBand var3) {
      for(int var4 = 0; var4 < var2.length; ++var4) {
         Attribute.Layout.Element var5 = var2[var4];
         String var6 = var1 + var3.size() + "_" + var5.layout;
         int var7;
         if ((var7 = var6.indexOf(91)) > 0) {
            var6 = var6.substring(0, var7);
         }

         if ((var7 = var6.indexOf(40)) > 0) {
            var6 = var6.substring(0, var7);
         }

         if (var6.endsWith("H")) {
            var6 = var6.substring(0, var6.length() - 1);
         }

         Object var10;
         switch(var5.kind) {
         case 1:
            var10 = this.newElemBand(var5, var6, var3);
            break;
         case 2:
            if (!var5.flagTest((byte)2)) {
               var10 = var3.newIntBand(var6, BCI5);
            } else {
               var10 = var3.newIntBand(var6, BRANCH5);
            }
            break;
         case 3:
            var10 = var3.newIntBand(var6, BRANCH5);
            break;
         case 4:
            assert !var5.flagTest((byte)1);

            var10 = this.newElemBand(var5, var6, var3);
            break;
         case 5:
            assert !var5.flagTest((byte)1);

            var10 = this.newElemBand(var5, var6, var3);
            this.makeNewAttributeBands(var1, var5.body, var3);
            break;
         case 6:
            byte var8 = var5.refKind;
            boolean var9 = var5.flagTest((byte)4);
            var10 = var3.newCPRefBand(var6, UNSIGNED5, var8, var9);
            break;
         case 7:
            var10 = this.newElemBand(var5, var6, var3);
            this.makeNewAttributeBands(var1, var5.body, var3);
            break;
         case 8:
            if (!var5.flagTest((byte)8)) {
               this.makeNewAttributeBands(var1, var5.body, var3);
            }
         case 9:
            continue;
         case 10:
            this.makeNewAttributeBands(var1, var5.body, var3);
            continue;
         default:
            assert false;
            continue;
         }

         if (this.verbose > 1) {
            Utils.log.fine("New attribute band " + var10);
         }
      }

   }

   private BandStructure.Band newElemBand(Attribute.Layout.Element var1, String var2, BandStructure.MultiBand var3) {
      if (var1.flagTest((byte)1)) {
         return var3.newIntBand(var2, SIGNED5);
      } else {
         return var1.len == 1 ? var3.newIntBand(var2, BYTE1) : var3.newIntBand(var2, UNSIGNED5);
      }
   }

   protected int setAttributeLayoutIndex(Attribute.Layout var1, int var2) {
      int var3 = var1.ctype;

      assert -1 <= var2 && var2 < this.attrIndexLimit[var3];

      List var4 = (List)this.attrDefs.get(var3);
      if (var2 == -1) {
         var2 = var4.size();
         var4.add(var1);
         if (this.verbose > 0) {
            Utils.log.info("Adding new attribute at " + var1 + ": " + var2);
         }

         this.attrIndexTable.put(var1, var2);
         return var2;
      } else if (testBit(this.attrDefSeen[var3], 1L << var2)) {
         throw new RuntimeException("Multiple explicit definition at " + var2 + ": " + var1);
      } else {
         long[] var10000 = this.attrDefSeen;
         var10000[var3] |= 1L << var2;

         assert 0 <= var2 && var2 < this.attrIndexLimit[var3];

         if (this.verbose > (this.attrClassFileVersionMask == 0 ? 2 : 0)) {
            Utils.log.fine("Fixing new attribute at " + var2 + ": " + var1 + (var4.get(var2) == null ? "" : "; replacing " + var4.get(var2)));
         }

         var10000 = this.attrFlagMask;
         var10000[var3] |= 1L << var2;
         this.attrIndexTable.put(var4.get(var2), (Object)null);
         var4.set(var2, var1);
         this.attrIndexTable.put(var1, var2);
         return var2;
      }
   }

   static int shortCodeHeader(Code var0) {
      int var1 = var0.max_stack;
      int var2 = var0.max_locals;
      int var3 = var0.handler_class.length;
      if (var3 >= shortCodeLimits.length) {
         return 0;
      } else {
         int var4 = var0.getMethod().getArgumentSize();

         assert var2 >= var4;

         if (var2 < var4) {
            return 0;
         } else {
            int var5 = var2 - var4;
            int var6 = shortCodeLimits[var3][0];
            int var7 = shortCodeLimits[var3][1];
            if (var1 < var6 && var5 < var7) {
               int var8 = shortCodeHeader_h_base(var3);
               var8 += var1 + var6 * var5;
               if (var8 > 255) {
                  return 0;
               } else {
                  assert shortCodeHeader_max_stack(var8) == var1;

                  assert shortCodeHeader_max_na_locals(var8) == var5;

                  assert shortCodeHeader_handler_count(var8) == var3;

                  return var8;
               }
            } else {
               return 0;
            }
         }
      }
   }

   static int shortCodeHeader_handler_count(int var0) {
      assert var0 > 0 && var0 <= 255;

      int var1;
      for(var1 = 0; var0 >= shortCodeHeader_h_base(var1 + 1); ++var1) {
      }

      return var1;
   }

   static int shortCodeHeader_max_stack(int var0) {
      int var1 = shortCodeHeader_handler_count(var0);
      int var2 = shortCodeLimits[var1][0];
      return (var0 - shortCodeHeader_h_base(var1)) % var2;
   }

   static int shortCodeHeader_max_na_locals(int var0) {
      int var1 = shortCodeHeader_handler_count(var0);
      int var2 = shortCodeLimits[var1][0];
      return (var0 - shortCodeHeader_h_base(var1)) / var2;
   }

   private static int shortCodeHeader_h_base(int var0) {
      assert var0 <= shortCodeLimits.length;

      int var1 = 1;

      for(int var2 = 0; var2 < var0; ++var2) {
         int var3 = shortCodeLimits[var2][0];
         int var4 = shortCodeLimits[var2][1];
         var1 += var3 * var4;
      }

      return var1;
   }

   protected void putLabel(BandStructure.IntBand var1, Code var2, int var3, int var4) {
      var1.putInt(var2.encodeBCI(var4) - var2.encodeBCI(var3));
   }

   protected int getLabel(BandStructure.IntBand var1, Code var2, int var3) {
      return var2.decodeBCI(var1.getInt() + var2.encodeBCI(var3));
   }

   protected BandStructure.CPRefBand getCPRefOpBand(int var1) {
      switch(Instruction.getCPRefOpTag(var1)) {
      case 7:
         return this.bc_classref;
      case 9:
         return this.bc_fieldref;
      case 10:
         return this.bc_methodref;
      case 11:
         return this.bc_imethodref;
      case 18:
         return this.bc_indyref;
      case 51:
         switch(var1) {
         case 18:
         case 19:
            return this.bc_stringref;
         case 20:
            return this.bc_longref;
         case 233:
         case 236:
            return this.bc_classref;
         case 234:
         case 237:
            return this.bc_intref;
         case 235:
         case 238:
            return this.bc_floatref;
         case 239:
            return this.bc_doubleref;
         case 240:
         case 241:
            return this.bc_loadablevalueref;
         }
      default:
         assert false;

         return null;
      }
   }

   protected BandStructure.CPRefBand selfOpRefBand(int var1) {
      assert Instruction.isSelfLinkerOp(var1);

      int var2 = var1 - 202;
      boolean var3 = var2 >= 14;
      if (var3) {
         var2 -= 14;
      }

      boolean var4 = var2 >= 7;
      if (var4) {
         var2 -= 7;
      }

      int var5 = 178 + var2;
      boolean var6 = Instruction.isFieldOp(var5);
      if (!var3) {
         return var6 ? this.bc_thisfield : this.bc_thismethod;
      } else {
         return var6 ? this.bc_superfield : this.bc_supermethod;
      }
   }

   static OutputStream getDumpStream(BandStructure.Band var0, String var1) throws IOException {
      return getDumpStream(var0.name, var0.seqForDebug, var1, var0);
   }

   static OutputStream getDumpStream(ConstantPool.Index var0, String var1) throws IOException {
      if (var0.size() == 0) {
         return new ByteArrayOutputStream();
      } else {
         byte var2 = ConstantPool.TAG_ORDER[var0.cpMap[0].tag];
         return getDumpStream(var0.debugName, var2, var1, var0);
      }
   }

   static OutputStream getDumpStream(String var0, int var1, String var2, Object var3) throws IOException {
      if (dumpDir == null) {
         dumpDir = File.createTempFile("BD_", "", new File("."));
         dumpDir.delete();
         if (dumpDir.mkdir()) {
            Utils.log.info("Dumping bands to " + dumpDir);
         }
      }

      var0 = var0.replace('(', ' ').replace(')', ' ');
      var0 = var0.replace('/', ' ');
      var0 = var0.replace('*', ' ');
      var0 = var0.trim().replace(' ', '_');
      var0 = (10000 + var1 + "_" + var0).substring(1);
      File var4 = new File(dumpDir, var0 + var2);
      Utils.log.info("Dumping " + var3 + " to " + var4);
      return new BufferedOutputStream(new FileOutputStream(var4));
   }

   static boolean assertCanChangeLength(BandStructure.Band var0) {
      switch(var0.phase) {
      case 1:
      case 4:
         return true;
      default:
         return false;
      }
   }

   static boolean assertPhase(BandStructure.Band var0, int var1) {
      if (var0.phase() != var1) {
         Utils.log.warning("phase expected " + var1 + " was " + var0.phase() + " in " + var0);
         return false;
      } else {
         return true;
      }
   }

   static int verbose() {
      return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
   }

   static boolean assertPhaseChangeOK(BandStructure.Band var0, int var1, int var2) {
      switch(var1 * 10 + var2) {
      case 1:
         assert !var0.isReader();

         assert var0.capacity() >= 0;

         assert var0.length() == 0;

         return true;
      case 2:
         assert var0.isReader();

         assert var0.capacity() < 0;

         return true;
      case 13:
      case 33:
         assert var0.length() == 0;

         return true;
      case 15:
      case 35:
         return true;
      case 24:
         assert Math.max(0, var0.capacity()) >= var0.valuesExpected();

         assert var0.length() <= 0;

         return true;
      case 46:
         assert var0.valuesRemainingForDebug() == var0.length();

         return true;
      case 58:
         return true;
      case 68:
         assert assertDoneDisbursing(var0);

         return true;
      default:
         if (var1 == var2) {
            Utils.log.warning("Already in phase " + var1);
         } else {
            Utils.log.warning("Unexpected phase " + var1 + " -> " + var2);
         }

         return false;
      }
   }

   private static boolean assertDoneDisbursing(BandStructure.Band var0) {
      if (var0.phase != 6) {
         Utils.log.warning("assertDoneDisbursing: still in phase " + var0.phase + ": " + var0);
         if (verbose() <= 1) {
            return false;
         }
      }

      int var1 = var0.valuesRemainingForDebug();
      if (var1 > 0) {
         Utils.log.warning("assertDoneDisbursing: " + var1 + " values left in " + var0);
         if (verbose() <= 1) {
            return false;
         }
      }

      if (var0 instanceof BandStructure.MultiBand) {
         BandStructure.MultiBand var2 = (BandStructure.MultiBand)var0;

         for(int var3 = 0; var3 < var2.bandCount; ++var3) {
            BandStructure.Band var4 = var2.bands[var3];
            if (var4.phase != 8) {
               Utils.log.warning("assertDoneDisbursing: sub-band still in phase " + var4.phase + ": " + var4);
               if (verbose() <= 1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static void printCDecl(BandStructure.Band var0) {
      if (!(var0 instanceof BandStructure.MultiBand)) {
         String var7 = "NULL";
         if (var0 instanceof BandStructure.CPRefBand) {
            ConstantPool.Index var8 = ((BandStructure.CPRefBand)var0).index;
            if (var8 != null) {
               var7 = "INDEX(" + var8.debugName + ")";
            }
         }

         Coding[] var9 = new Coding[]{BYTE1, CHAR3, BCI5, BRANCH5, UNSIGNED5, UDELTA5, SIGNED5, DELTA5, MDELTA5};
         String[] var3 = new String[]{"BYTE1", "CHAR3", "BCI5", "BRANCH5", "UNSIGNED5", "UDELTA5", "SIGNED5", "DELTA5", "MDELTA5"};
         Coding var4 = var0.regularCoding;
         int var5 = Arrays.asList(var9).indexOf(var4);
         String var6;
         if (var5 >= 0) {
            var6 = var3[var5];
         } else {
            var6 = "CODING" + var4.keyString();
         }

         System.out.println("  BAND_INIT(\"" + var0.name() + "\", " + var6 + ", " + var7 + "),");
      } else {
         BandStructure.MultiBand var1 = (BandStructure.MultiBand)var0;

         for(int var2 = 0; var2 < var1.bandCount; ++var2) {
            printCDecl(var1.bands[var2]);
         }

      }
   }

   boolean notePrevForAssert(BandStructure.Band var1, BandStructure.Band var2) {
      if (this.prevForAssertMap == null) {
         this.prevForAssertMap = new HashMap();
      }

      this.prevForAssertMap.put(var1, var2);
      return true;
   }

   private boolean assertReadyToReadFrom(BandStructure.Band var1, InputStream var2) throws IOException {
      BandStructure.Band var3 = (BandStructure.Band)this.prevForAssertMap.get(var1);
      if (var3 != null && phaseCmp(var3.phase(), 6) < 0) {
         Utils.log.warning("Previous band not done reading.");
         Utils.log.info("    Previous band: " + var3);
         Utils.log.info("        Next band: " + var1);

         assert this.verbose > 0;
      }

      String var4 = var1.name;
      if (this.optDebugBands && !var4.startsWith("(")) {
         assert bandSequenceList != null;

         String var5 = (String)bandSequenceList.removeFirst();
         if (!var5.equals(var4)) {
            Utils.log.warning("Expected " + var4 + " but read: " + var5);
            return false;
         }

         Utils.log.info("Read band in sequence: " + var4);
      }

      return true;
   }

   private boolean assertValidCPRefs(BandStructure.CPRefBand var1) {
      if (var1.index == null) {
         return true;
      } else {
         int var2 = var1.index.size() + 1;

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            int var4 = var1.valueAtForDebug(var3);
            if (var4 < 0 || var4 >= var2) {
               Utils.log.warning("CP ref out of range [" + var3 + "] = " + var4 + " in " + var1);
               return false;
            }
         }

         return true;
      }
   }

   private boolean assertReadyToWriteTo(BandStructure.Band var1, OutputStream var2) throws IOException {
      BandStructure.Band var3 = (BandStructure.Band)this.prevForAssertMap.get(var1);
      if (var3 != null && phaseCmp(var3.phase(), 8) < 0) {
         Utils.log.warning("Previous band not done writing.");
         Utils.log.info("    Previous band: " + var3);
         Utils.log.info("        Next band: " + var1);

         assert this.verbose > 0;
      }

      String var4 = var1.name;
      if (this.optDebugBands && !var4.startsWith("(")) {
         if (bandSequenceList == null) {
            bandSequenceList = new LinkedList();
         }

         bandSequenceList.add(var4);
      }

      return true;
   }

   protected static boolean testBit(int var0, int var1) {
      return (var0 & var1) != 0;
   }

   protected static int setBit(int var0, int var1, boolean var2) {
      return var2 ? var0 | var1 : var0 & ~var1;
   }

   protected static boolean testBit(long var0, long var2) {
      return (var0 & var2) != 0L;
   }

   protected static long setBit(long var0, long var2, boolean var4) {
      return var4 ? var0 | var2 : var0 & ~var2;
   }

   static void printArrayTo(PrintStream var0, int[] var1, int var2, int var3) {
      int var4 = var3 - var2;

      for(int var5 = 0; var5 < var4; ++var5) {
         if (var5 % 10 == 0) {
            var0.println();
         } else {
            var0.print(" ");
         }

         var0.print(var1[var2 + var5]);
      }

      var0.println();
   }

   static void printArrayTo(PrintStream var0, ConstantPool.Entry[] var1, int var2, int var3) {
      printArrayTo(var0, var1, var2, var3, false);
   }

   static void printArrayTo(PrintStream var0, ConstantPool.Entry[] var1, int var2, int var3, boolean var4) {
      StringBuffer var5 = new StringBuffer();
      int var6 = var3 - var2;

      for(int var7 = 0; var7 < var6; ++var7) {
         ConstantPool.Entry var8 = var1[var2 + var7];
         var0.print(var2 + var7);
         var0.print("=");
         if (var4) {
            var0.print((int)var8.tag);
            var0.print(":");
         }

         String var9 = var8.stringValue();
         var5.setLength(0);

         for(int var10 = 0; var10 < var9.length(); ++var10) {
            char var11 = var9.charAt(var10);
            if (var11 >= ' ' && var11 <= '~' && var11 != '\\') {
               var5.append(var11);
            } else if (var11 == '\\') {
               var5.append("\\\\");
            } else if (var11 == '\n') {
               var5.append("\\n");
            } else if (var11 == '\t') {
               var5.append("\\t");
            } else if (var11 == '\r') {
               var5.append("\\r");
            } else {
               String var12 = "000" + Integer.toHexString(var11);
               var5.append("\\u").append(var12.substring(var12.length() - 4));
            }
         }

         var0.println((Object)var5);
      }

   }

   protected static Object[] realloc(Object[] var0, int var1) {
      Class var2 = var0.getClass().getComponentType();
      Object[] var3 = (Object[])((Object[])Array.newInstance(var2, var1));
      System.arraycopy(var0, 0, var3, 0, Math.min(var0.length, var1));
      return var3;
   }

   protected static Object[] realloc(Object[] var0) {
      return realloc(var0, Math.max(10, var0.length * 2));
   }

   protected static int[] realloc(int[] var0, int var1) {
      if (var1 == 0) {
         return Constants.noInts;
      } else if (var0 == null) {
         return new int[var1];
      } else {
         int[] var2 = new int[var1];
         System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
         return var2;
      }
   }

   protected static int[] realloc(int[] var0) {
      return realloc(var0, Math.max(10, var0.length * 2));
   }

   protected static byte[] realloc(byte[] var0, int var1) {
      if (var1 == 0) {
         return Constants.noBytes;
      } else if (var0 == null) {
         return new byte[var1];
      } else {
         byte[] var2 = new byte[var1];
         System.arraycopy(var0, 0, var2, 0, Math.min(var0.length, var1));
         return var2;
      }
   }

   protected static byte[] realloc(byte[] var0) {
      return realloc(var0, Math.max(10, var0.length * 2));
   }

   static {
      UDELTA5 = UNSIGNED5.getDeltaCoding();
      SIGNED5 = Coding.of(5, 64, 1);
      DELTA5 = SIGNED5.getDeltaCoding();
      MDELTA5 = Coding.of(5, 64, 2).getDeltaCoding();
      basicCodings = new Coding[]{null, Coding.of(1, 256, 0), Coding.of(1, 256, 1), Coding.of(1, 256, 0).getDeltaCoding(), Coding.of(1, 256, 1).getDeltaCoding(), Coding.of(2, 256, 0), Coding.of(2, 256, 1), Coding.of(2, 256, 0).getDeltaCoding(), Coding.of(2, 256, 1).getDeltaCoding(), Coding.of(3, 256, 0), Coding.of(3, 256, 1), Coding.of(3, 256, 0).getDeltaCoding(), Coding.of(3, 256, 1).getDeltaCoding(), Coding.of(4, 256, 0), Coding.of(4, 256, 1), Coding.of(4, 256, 0).getDeltaCoding(), Coding.of(4, 256, 1).getDeltaCoding(), Coding.of(5, 4, 0), Coding.of(5, 4, 1), Coding.of(5, 4, 2), Coding.of(5, 16, 0), Coding.of(5, 16, 1), Coding.of(5, 16, 2), Coding.of(5, 32, 0), Coding.of(5, 32, 1), Coding.of(5, 32, 2), Coding.of(5, 64, 0), Coding.of(5, 64, 1), Coding.of(5, 64, 2), Coding.of(5, 128, 0), Coding.of(5, 128, 1), Coding.of(5, 128, 2), Coding.of(5, 4, 0).getDeltaCoding(), Coding.of(5, 4, 1).getDeltaCoding(), Coding.of(5, 4, 2).getDeltaCoding(), Coding.of(5, 16, 0).getDeltaCoding(), Coding.of(5, 16, 1).getDeltaCoding(), Coding.of(5, 16, 2).getDeltaCoding(), Coding.of(5, 32, 0).getDeltaCoding(), Coding.of(5, 32, 1).getDeltaCoding(), Coding.of(5, 32, 2).getDeltaCoding(), Coding.of(5, 64, 0).getDeltaCoding(), Coding.of(5, 64, 1).getDeltaCoding(), Coding.of(5, 64, 2).getDeltaCoding(), Coding.of(5, 128, 0).getDeltaCoding(), Coding.of(5, 128, 1).getDeltaCoding(), Coding.of(5, 128, 2).getDeltaCoding(), Coding.of(2, 192, 0), Coding.of(2, 224, 0), Coding.of(2, 240, 0), Coding.of(2, 248, 0), Coding.of(2, 252, 0), Coding.of(2, 8, 0).getDeltaCoding(), Coding.of(2, 8, 1).getDeltaCoding(), Coding.of(2, 16, 0).getDeltaCoding(), Coding.of(2, 16, 1).getDeltaCoding(), Coding.of(2, 32, 0).getDeltaCoding(), Coding.of(2, 32, 1).getDeltaCoding(), Coding.of(2, 64, 0).getDeltaCoding(), Coding.of(2, 64, 1).getDeltaCoding(), Coding.of(2, 128, 0).getDeltaCoding(), Coding.of(2, 128, 1).getDeltaCoding(), Coding.of(2, 192, 0).getDeltaCoding(), Coding.of(2, 192, 1).getDeltaCoding(), Coding.of(2, 224, 0).getDeltaCoding(), Coding.of(2, 224, 1).getDeltaCoding(), Coding.of(2, 240, 0).getDeltaCoding(), Coding.of(2, 240, 1).getDeltaCoding(), Coding.of(2, 248, 0).getDeltaCoding(), Coding.of(2, 248, 1).getDeltaCoding(), Coding.of(3, 192, 0), Coding.of(3, 224, 0), Coding.of(3, 240, 0), Coding.of(3, 248, 0), Coding.of(3, 252, 0), Coding.of(3, 8, 0).getDeltaCoding(), Coding.of(3, 8, 1).getDeltaCoding(), Coding.of(3, 16, 0).getDeltaCoding(), Coding.of(3, 16, 1).getDeltaCoding(), Coding.of(3, 32, 0).getDeltaCoding(), Coding.of(3, 32, 1).getDeltaCoding(), Coding.of(3, 64, 0).getDeltaCoding(), Coding.of(3, 64, 1).getDeltaCoding(), Coding.of(3, 128, 0).getDeltaCoding(), Coding.of(3, 128, 1).getDeltaCoding(), Coding.of(3, 192, 0).getDeltaCoding(), Coding.of(3, 192, 1).getDeltaCoding(), Coding.of(3, 224, 0).getDeltaCoding(), Coding.of(3, 224, 1).getDeltaCoding(), Coding.of(3, 240, 0).getDeltaCoding(), Coding.of(3, 240, 1).getDeltaCoding(), Coding.of(3, 248, 0).getDeltaCoding(), Coding.of(3, 248, 1).getDeltaCoding(), Coding.of(4, 192, 0), Coding.of(4, 224, 0), Coding.of(4, 240, 0), Coding.of(4, 248, 0), Coding.of(4, 252, 0), Coding.of(4, 8, 0).getDeltaCoding(), Coding.of(4, 8, 1).getDeltaCoding(), Coding.of(4, 16, 0).getDeltaCoding(), Coding.of(4, 16, 1).getDeltaCoding(), Coding.of(4, 32, 0).getDeltaCoding(), Coding.of(4, 32, 1).getDeltaCoding(), Coding.of(4, 64, 0).getDeltaCoding(), Coding.of(4, 64, 1).getDeltaCoding(), Coding.of(4, 128, 0).getDeltaCoding(), Coding.of(4, 128, 1).getDeltaCoding(), Coding.of(4, 192, 0).getDeltaCoding(), Coding.of(4, 192, 1).getDeltaCoding(), Coding.of(4, 224, 0).getDeltaCoding(), Coding.of(4, 224, 1).getDeltaCoding(), Coding.of(4, 240, 0).getDeltaCoding(), Coding.of(4, 240, 1).getDeltaCoding(), Coding.of(4, 248, 0).getDeltaCoding(), Coding.of(4, 248, 1).getDeltaCoding(), null};

      assert basicCodings[0] == null;

      assert basicCodings[1] != null;

      assert basicCodings[115] != null;

      HashMap var0 = new HashMap();

      int var1;
      Coding var2;
      for(var1 = 0; var1 < basicCodings.length; ++var1) {
         var2 = basicCodings[var1];
         if (var2 != null) {
            assert var1 >= 1;

            assert var1 <= 115;

            var0.put(var2, var1);
         }
      }

      basicCodingIndexes = var0;
      defaultMetaCoding = new byte[]{0};
      noMetaCoding = new byte[0];
      boolean var4 = false;
      if (!$assertionsDisabled) {
         var4 = true;
         if (false) {
            throw new AssertionError();
         }
      }

      if (var4) {
         for(var1 = 0; var1 < basicCodings.length; ++var1) {
            var2 = basicCodings[var1];
            if (var2 != null && var2.B() != 1 && var2.L() != 0) {
               for(int var3 = 0; var3 <= 255; ++var3) {
                  encodeEscapeValue(var3, var2);
               }
            }
         }
      }

      shortCodeLimits = new int[][]{{12, 12}, {8, 8}, {7, 7}};
      dumpDir = null;
      bandSequenceList = null;
   }

   private static class ByteCounter extends FilterOutputStream {
      private long count;

      public ByteCounter(OutputStream var1) {
         super(var1);
      }

      public long getCount() {
         return this.count;
      }

      public void setCount(long var1) {
         this.count = var1;
      }

      public void write(int var1) throws IOException {
         ++this.count;
         if (this.out != null) {
            this.out.write(var1);
         }

      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.count += (long)var3;
         if (this.out != null) {
            this.out.write(var1, var2, var3);
         }

      }

      public String toString() {
         return String.valueOf(this.getCount());
      }
   }

   class MultiBand extends BandStructure.Band {
      BandStructure.Band[] bands = new BandStructure.Band[10];
      int bandCount = 0;
      private int cap = -1;

      MultiBand(String var2, Coding var3) {
         super(var2, var3);
      }

      public BandStructure.Band init() {
         super.init();
         this.setCapacity(0);
         if (this.phase() == 2) {
            this.setPhase(4);
            this.setPhase(6);
         }

         return this;
      }

      int size() {
         return this.bandCount;
      }

      BandStructure.Band get(int var1) {
         assert var1 < this.bandCount;

         return this.bands[var1];
      }

      BandStructure.Band[] toArray() {
         return (BandStructure.Band[])((BandStructure.Band[])BandStructure.realloc((Object[])this.bands, this.bandCount));
      }

      void add(BandStructure.Band var1) {
         assert this.bandCount == 0 || BandStructure.this.notePrevForAssert(var1, this.bands[this.bandCount - 1]);

         if (this.bandCount == this.bands.length) {
            this.bands = (BandStructure.Band[])((BandStructure.Band[])BandStructure.realloc((Object[])this.bands));
         }

         this.bands[this.bandCount++] = var1;
      }

      BandStructure.ByteBand newByteBand(String var1) {
         BandStructure.ByteBand var2 = BandStructure.this.new ByteBand(var1);
         var2.init();
         this.add(var2);
         return var2;
      }

      BandStructure.IntBand newIntBand(String var1) {
         BandStructure.IntBand var2 = BandStructure.this.new IntBand(var1, this.regularCoding);
         var2.init();
         this.add(var2);
         return var2;
      }

      BandStructure.IntBand newIntBand(String var1, Coding var2) {
         BandStructure.IntBand var3 = BandStructure.this.new IntBand(var1, var2);
         var3.init();
         this.add(var3);
         return var3;
      }

      BandStructure.MultiBand newMultiBand(String var1, Coding var2) {
         BandStructure.MultiBand var3 = BandStructure.this.new MultiBand(var1, var2);
         var3.init();
         this.add(var3);
         return var3;
      }

      BandStructure.CPRefBand newCPRefBand(String var1, byte var2) {
         BandStructure.CPRefBand var3 = BandStructure.this.new CPRefBand(var1, this.regularCoding, var2);
         var3.init();
         this.add(var3);
         return var3;
      }

      BandStructure.CPRefBand newCPRefBand(String var1, Coding var2, byte var3) {
         BandStructure.CPRefBand var4 = BandStructure.this.new CPRefBand(var1, var2, var3);
         var4.init();
         this.add(var4);
         return var4;
      }

      BandStructure.CPRefBand newCPRefBand(String var1, Coding var2, byte var3, boolean var4) {
         BandStructure.CPRefBand var5 = BandStructure.this.new CPRefBand(var1, var2, var3, var4);
         var5.init();
         this.add(var5);
         return var5;
      }

      int bandCount() {
         return this.bandCount;
      }

      public int capacity() {
         return this.cap;
      }

      public void setCapacity(int var1) {
         this.cap = var1;
      }

      public int length() {
         return 0;
      }

      public int valuesRemainingForDebug() {
         return 0;
      }

      protected void chooseBandCodings() throws IOException {
         for(int var1 = 0; var1 < this.bandCount; ++var1) {
            BandStructure.Band var2 = this.bands[var1];
            var2.chooseBandCodings();
         }

      }

      protected long computeOutputSize() {
         long var1 = 0L;

         for(int var3 = 0; var3 < this.bandCount; ++var3) {
            BandStructure.Band var4 = this.bands[var3];
            long var5 = var4.outputSize();

            assert var5 >= 0L : var4;

            var1 += var5;
         }

         return var1;
      }

      protected void writeDataTo(OutputStream var1) throws IOException {
         long var2 = 0L;
         if (BandStructure.this.outputCounter != null) {
            var2 = BandStructure.this.outputCounter.getCount();
         }

         for(int var4 = 0; var4 < this.bandCount; ++var4) {
            BandStructure.Band var5 = this.bands[var4];
            var5.writeTo(var1);
            if (BandStructure.this.outputCounter != null) {
               long var6 = BandStructure.this.outputCounter.getCount();
               long var8 = var6 - var2;
               var2 = var6;
               if (BandStructure.this.verbose > 0 && var8 > 0L || BandStructure.this.verbose > 1) {
                  Utils.log.info("  ...wrote " + var8 + " bytes from " + var5);
               }
            }
         }

      }

      protected void readDataFrom(InputStream var1) throws IOException {
         assert false;

         for(int var2 = 0; var2 < this.bandCount; ++var2) {
            BandStructure.Band var3 = this.bands[var2];
            var3.readFrom(var1);
            if (BandStructure.this.verbose > 0 && var3.length() > 0 || BandStructure.this.verbose > 1) {
               Utils.log.info("  ...read " + var3);
            }
         }

      }

      public String toString() {
         return "{" + this.bandCount() + " bands: " + super.toString() + "}";
      }
   }

   class CPRefBand extends BandStructure.ValueBand {
      ConstantPool.Index index;
      boolean nullOK;

      public CPRefBand(String var2, Coding var3, byte var4, boolean var5) {
         super(var2, var3);
         this.nullOK = var5;
         if (var4 != 0) {
            BandStructure.this.setBandIndex(this, var4);
         }

      }

      public CPRefBand(String var2, Coding var3, byte var4) {
         this(var2, var3, var4, false);
      }

      public CPRefBand(String var2, Coding var3, Object var4) {
         this(var2, var3, (byte)0, false);
      }

      public void setIndex(ConstantPool.Index var1) {
         this.index = var1;
      }

      protected void readDataFrom(InputStream var1) throws IOException {
         super.readDataFrom(var1);

         assert BandStructure.this.assertValidCPRefs(this);

      }

      public void putRef(ConstantPool.Entry var1) {
         this.addValue(this.encodeRefOrNull(var1, this.index));
      }

      public void putRef(ConstantPool.Entry var1, ConstantPool.Index var2) {
         assert this.index == null;

         this.addValue(this.encodeRefOrNull(var1, var2));
      }

      public void putRef(ConstantPool.Entry var1, byte var2) {
         this.putRef(var1, BandStructure.this.getCPIndex(var2));
      }

      public ConstantPool.Entry getRef() {
         if (this.index == null) {
            Utils.log.warning("No index for " + this);
         }

         assert this.index != null;

         return this.decodeRefOrNull(this.getValue(), this.index);
      }

      public ConstantPool.Entry getRef(ConstantPool.Index var1) {
         assert this.index == null;

         return this.decodeRefOrNull(this.getValue(), var1);
      }

      public ConstantPool.Entry getRef(byte var1) {
         return this.getRef(BandStructure.this.getCPIndex(var1));
      }

      private int encodeRefOrNull(ConstantPool.Entry var1, ConstantPool.Index var2) {
         int var3;
         if (var1 == null) {
            var3 = -1;
         } else {
            var3 = BandStructure.this.encodeRef(var1, var2);
         }

         return (this.nullOK ? 1 : 0) + var3;
      }

      private ConstantPool.Entry decodeRefOrNull(int var1, ConstantPool.Index var2) {
         int var3 = var1 - (this.nullOK ? 1 : 0);
         return var3 == -1 ? null : BandStructure.this.decodeRef(var3, var2);
      }
   }

   class IntBand extends BandStructure.ValueBand {
      public IntBand(String var2, Coding var3) {
         super(var2, var3);
      }

      public void putInt(int var1) {
         assert this.phase() == 1;

         this.addValue(var1);
      }

      public int getInt() {
         return this.getValue();
      }

      public int getIntTotal() {
         assert this.phase() == 6;

         assert this.valuesRemainingForDebug() == this.length();

         int var1 = 0;

         for(int var2 = this.length(); var2 > 0; --var2) {
            var1 += this.getInt();
         }

         this.resetForSecondPass();
         return var1;
      }

      public int getIntCount(int var1) {
         assert this.phase() == 6;

         assert this.valuesRemainingForDebug() == this.length();

         int var2 = 0;

         for(int var3 = this.length(); var3 > 0; --var3) {
            if (this.getInt() == var1) {
               ++var2;
            }
         }

         this.resetForSecondPass();
         return var2;
      }
   }

   class ByteBand extends BandStructure.Band {
      private ByteArrayOutputStream bytes;
      private ByteArrayOutputStream bytesForDump;
      private InputStream in;

      public ByteBand(String var2) {
         super(var2, BandStructure.BYTE1);
      }

      public int capacity() {
         return this.bytes == null ? -1 : Integer.MAX_VALUE;
      }

      protected void setCapacity(int var1) {
         assert this.bytes == null;

         this.bytes = new ByteArrayOutputStream(var1);
      }

      public void destroy() {
         this.lengthForDebug = this.length();
         this.bytes = null;
      }

      public int length() {
         return this.bytes == null ? -1 : this.bytes.size();
      }

      public void reset() {
         this.bytes.reset();
      }

      protected int valuesRemainingForDebug() {
         return this.bytes == null ? -1 : ((ByteArrayInputStream)this.in).available();
      }

      protected void chooseBandCodings() throws IOException {
         assert BandStructure.decodeEscapeValue(this.regularCoding.min(), this.regularCoding) < 0;

         assert BandStructure.decodeEscapeValue(this.regularCoding.max(), this.regularCoding) < 0;

      }

      protected long computeOutputSize() {
         return (long)this.bytes.size();
      }

      public void writeDataTo(OutputStream var1) throws IOException {
         if (this.length() != 0) {
            this.bytes.writeTo(var1);
            if (BandStructure.this.optDumpBands) {
               this.dumpBand();
            }

            this.destroy();
         }
      }

      private void dumpBand() throws IOException {
         assert BandStructure.this.optDumpBands;

         OutputStream var1 = BandStructure.getDumpStream((BandStructure.Band)this, ".bnd");
         Throwable var2 = null;

         try {
            if (this.bytesForDump != null) {
               this.bytesForDump.writeTo(var1);
            } else {
               this.bytes.writeTo(var1);
            }
         } catch (Throwable var11) {
            var2 = var11;
            throw var11;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var10) {
                     var2.addSuppressed(var10);
                  }
               } else {
                  var1.close();
               }
            }

         }

      }

      public void readDataFrom(InputStream var1) throws IOException {
         int var2 = this.valuesExpected();
         if (var2 != 0) {
            if (BandStructure.this.verbose > 1) {
               this.lengthForDebug = var2;
               Utils.log.fine("Reading band " + this);
               this.lengthForDebug = -1;
            }

            int var4;
            for(byte[] var3 = new byte[Math.min(var2, 16384)]; var2 > 0; var2 -= var4) {
               var4 = var1.read(var3, 0, Math.min(var2, var3.length));
               if (var4 < 0) {
                  throw new EOFException();
               }

               this.bytes.write(var3, 0, var4);
            }

            if (BandStructure.this.optDumpBands) {
               this.dumpBand();
            }

         }
      }

      public void readyToDisburse() {
         this.in = new ByteArrayInputStream(this.bytes.toByteArray());
         super.readyToDisburse();
      }

      public void doneDisbursing() {
         super.doneDisbursing();
         if (BandStructure.this.optDumpBands && this.bytesForDump != null && this.bytesForDump.size() > 0) {
            try {
               this.dumpBand();
            } catch (IOException var2) {
               throw new RuntimeException(var2);
            }
         }

         this.in = null;
         this.bytes = null;
         this.bytesForDump = null;
      }

      public void setInputStreamFrom(InputStream var1) throws IOException {
         assert this.bytes == null;

         assert BandStructure.this.assertReadyToReadFrom(this, var1);

         this.setPhase(4);
         this.in = var1;
         if (BandStructure.this.optDumpBands) {
            this.bytesForDump = new ByteArrayOutputStream();
            this.in = new FilterInputStream(var1) {
               public int read() throws IOException {
                  int var1 = this.in.read();
                  if (var1 >= 0) {
                     ByteBand.this.bytesForDump.write(var1);
                  }

                  return var1;
               }

               public int read(byte[] var1, int var2, int var3) throws IOException {
                  int var4 = this.in.read(var1, var2, var3);
                  if (var4 >= 0) {
                     ByteBand.this.bytesForDump.write(var1, var2, var4);
                  }

                  return var4;
               }
            };
         }

         super.readyToDisburse();
      }

      public OutputStream collectorStream() {
         assert this.phase() == 1;

         assert this.bytes != null;

         return this.bytes;
      }

      public InputStream getInputStream() {
         assert this.phase() == 6;

         assert this.in != null;

         return this.in;
      }

      public int getByte() throws IOException {
         int var1 = this.getInputStream().read();
         if (var1 < 0) {
            throw new EOFException();
         } else {
            return var1;
         }
      }

      public void putByte(int var1) throws IOException {
         assert var1 == (var1 & 255);

         this.collectorStream().write(var1);
      }

      public String toString() {
         return "byte " + super.toString();
      }
   }

   class ValueBand extends BandStructure.Band {
      private int[] values;
      private int length;
      private int valuesDisbursed;
      private CodingMethod bandCoding;
      private byte[] metaCoding;

      protected ValueBand(String var2, Coding var3) {
         super(var2, var3);
      }

      public int capacity() {
         return this.values == null ? -1 : this.values.length;
      }

      protected void setCapacity(int var1) {
         assert this.length <= var1;

         if (var1 == -1) {
            this.values = null;
         } else {
            this.values = BandStructure.realloc(this.values, var1);
         }
      }

      public int length() {
         return this.length;
      }

      protected int valuesRemainingForDebug() {
         return this.length - this.valuesDisbursed;
      }

      protected int valueAtForDebug(int var1) {
         return this.values[var1];
      }

      void patchValue(int var1, int var2) {
         assert this == BandStructure.this.archive_header_S;

         assert var1 == 0 || var1 == 1;

         assert var1 < this.length;

         this.values[var1] = var2;
         this.outputSize = -1L;
      }

      protected void initializeValues(int[] var1) {
         assert BandStructure.assertCanChangeLength(this);

         assert this.length == 0;

         this.values = var1;
         this.length = var1.length;
      }

      protected void addValue(int var1) {
         assert BandStructure.assertCanChangeLength(this);

         if (this.length == this.values.length) {
            this.setCapacity(this.length < 1000 ? this.length * 10 : this.length * 2);
         }

         this.values[this.length++] = var1;
      }

      private boolean canVaryCoding() {
         if (!BandStructure.this.optVaryCodings) {
            return false;
         } else if (this.length == 0) {
            return false;
         } else if (this == BandStructure.this.archive_header_0) {
            return false;
         } else if (this == BandStructure.this.archive_header_S) {
            return false;
         } else if (this == BandStructure.this.archive_header_1) {
            return false;
         } else {
            return this.regularCoding.min() <= -256 || this.regularCoding.max() >= 256;
         }
      }

      private boolean shouldVaryCoding() {
         assert this.canVaryCoding();

         return BandStructure.this.effort >= 9 || this.length >= 100;
      }

      protected void chooseBandCodings() throws IOException {
         boolean var1 = this.canVaryCoding();
         if (var1 && this.shouldVaryCoding()) {
            int[] var2 = new int[]{0, 0};
            this.bandCoding = BandStructure.this.chooseCoding(this.values, 0, this.length, this.regularCoding, this.name(), var2);
            this.outputSize = (long)var2[0];
            if (this.outputSize == 0L) {
               this.outputSize = -1L;
            }
         } else {
            if (this.regularCoding.canRepresent(this.values, 0, this.length)) {
               this.bandCoding = this.regularCoding;
            } else {
               assert var1;

               if (BandStructure.this.verbose > 1) {
                  Utils.log.fine("regular coding fails in band " + this.name());
               }

               this.bandCoding = BandStructure.UNSIGNED5;
            }

            this.outputSize = -1L;
         }

         if (this.bandCoding != this.regularCoding) {
            this.metaCoding = this.bandCoding.getMetaCoding(this.regularCoding);
            if (BandStructure.this.verbose > 1) {
               Utils.log.fine("alternate coding " + this + " " + this.bandCoding);
            }
         } else if (var1 && BandStructure.decodeEscapeValue(this.values[0], this.regularCoding) >= 0) {
            this.metaCoding = BandStructure.defaultMetaCoding;
         } else {
            this.metaCoding = BandStructure.noMetaCoding;
         }

         if (this.metaCoding.length > 0 && (BandStructure.this.verbose > 2 || BandStructure.this.verbose > 1 && this.metaCoding.length > 1)) {
            StringBuffer var4 = new StringBuffer();

            for(int var3 = 0; var3 < this.metaCoding.length; ++var3) {
               if (var3 == 1) {
                  var4.append(" /");
               }

               var4.append(" ").append(this.metaCoding[var3] & 255);
            }

            Utils.log.fine("   meta-coding " + var4);
         }

         assert this.outputSize < 0L || !(this.bandCoding instanceof Coding) || this.outputSize == (long)((Coding)this.bandCoding).getLength(this.values, 0, this.length) : this.bandCoding + " : " + this.outputSize + " != " + ((Coding)this.bandCoding).getLength(this.values, 0, this.length) + " ?= " + BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length);

         if (this.metaCoding.length > 0) {
            if (this.outputSize >= 0L) {
               this.outputSize += (long)this.computeEscapeSize();
            }

            for(int var5 = 1; var5 < this.metaCoding.length; ++var5) {
               BandStructure.this.band_headers.putByte(this.metaCoding[var5] & 255);
            }
         }

      }

      protected long computeOutputSize() {
         this.outputSize = (long)BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length);

         assert this.outputSize < 2147483647L;

         this.outputSize += (long)this.computeEscapeSize();
         return this.outputSize;
      }

      protected int computeEscapeSize() {
         if (this.metaCoding.length == 0) {
            return 0;
         } else {
            int var1 = this.metaCoding[0] & 255;
            int var2 = BandStructure.encodeEscapeValue(var1, this.regularCoding);
            return this.regularCoding.setD(0).getLength(var2);
         }
      }

      protected void writeDataTo(OutputStream var1) throws IOException {
         if (this.length != 0) {
            long var2 = 0L;
            if (var1 == BandStructure.this.outputCounter) {
               var2 = BandStructure.this.outputCounter.getCount();
            }

            if (this.metaCoding.length > 0) {
               int var4 = this.metaCoding[0] & 255;
               int var5 = BandStructure.encodeEscapeValue(var4, this.regularCoding);
               this.regularCoding.setD(0).writeTo(var1, var5);
            }

            this.bandCoding.writeArrayTo(var1, this.values, 0, this.length);

            assert var1 != BandStructure.this.outputCounter || this.outputSize == BandStructure.this.outputCounter.getCount() - var2 : this.outputSize + " != " + BandStructure.this.outputCounter.getCount() + "-" + var2;

            if (BandStructure.this.optDumpBands) {
               this.dumpBand();
            }

         }
      }

      protected void readDataFrom(InputStream var1) throws IOException {
         this.length = this.valuesExpected();
         if (this.length != 0) {
            if (BandStructure.this.verbose > 1) {
               Utils.log.fine("Reading band " + this);
            }

            if (!this.canVaryCoding()) {
               this.bandCoding = this.regularCoding;
               this.metaCoding = BandStructure.noMetaCoding;
            } else {
               assert var1.markSupported();

               var1.mark(5);
               int var2 = this.regularCoding.setD(0).readFrom(var1);
               int var3 = BandStructure.decodeEscapeValue(var2, this.regularCoding);
               if (var3 < 0) {
                  var1.reset();
                  this.bandCoding = this.regularCoding;
                  this.metaCoding = BandStructure.noMetaCoding;
               } else if (var3 == 0) {
                  this.bandCoding = this.regularCoding;
                  this.metaCoding = BandStructure.defaultMetaCoding;
               } else {
                  if (BandStructure.this.verbose > 2) {
                     Utils.log.fine("found X=" + var2 + " => XB=" + var3);
                  }

                  this.bandCoding = BandStructure.this.getBandHeader(var3, this.regularCoding);
                  int var4 = BandStructure.this.bandHeaderBytePos0;
                  int var5 = BandStructure.this.bandHeaderBytePos;
                  this.metaCoding = new byte[var5 - var4];
                  System.arraycopy(BandStructure.this.bandHeaderBytes, var4, this.metaCoding, 0, this.metaCoding.length);
               }
            }

            if (this.bandCoding != this.regularCoding && BandStructure.this.verbose > 1) {
               Utils.log.fine(this.name() + ": irregular coding " + this.bandCoding);
            }

            this.bandCoding.readArrayFrom(var1, this.values, 0, this.length);
            if (BandStructure.this.optDumpBands) {
               this.dumpBand();
            }

         }
      }

      public void doneDisbursing() {
         super.doneDisbursing();
         this.values = null;
      }

      private void dumpBand() throws IOException {
         assert BandStructure.this.optDumpBands;

         PrintStream var1 = new PrintStream(BandStructure.getDumpStream((BandStructure.Band)this, ".txt"));
         Throwable var2 = null;

         try {
            String var3 = this.bandCoding == this.regularCoding ? "" : " irregular";
            var1.print("# length=" + this.length + " size=" + this.outputSize() + var3 + " coding=" + this.bandCoding);
            if (this.metaCoding != BandStructure.noMetaCoding) {
               StringBuffer var4 = new StringBuffer();

               for(int var5 = 0; var5 < this.metaCoding.length; ++var5) {
                  if (var5 == 1) {
                     var4.append(" /");
                  }

                  var4.append(" ").append(this.metaCoding[var5] & 255);
               }

               var1.print(" //header: " + var4);
            }

            BandStructure.printArrayTo(var1, (int[])this.values, 0, this.length);
         } catch (Throwable var29) {
            var2 = var29;
            throw var29;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var26) {
                     var2.addSuppressed(var26);
                  }
               } else {
                  var1.close();
               }
            }

         }

         OutputStream var31 = BandStructure.getDumpStream((BandStructure.Band)this, ".bnd");
         var2 = null;

         try {
            this.bandCoding.writeArrayTo(var31, this.values, 0, this.length);
         } catch (Throwable var27) {
            var2 = var27;
            throw var27;
         } finally {
            if (var31 != null) {
               if (var2 != null) {
                  try {
                     var31.close();
                  } catch (Throwable var25) {
                     var2.addSuppressed(var25);
                  }
               } else {
                  var31.close();
               }
            }

         }

      }

      protected int getValue() {
         assert this.phase() == 6;

         if (BandStructure.this.optDebugBands && this.length == 0 && this.valuesDisbursed == this.length) {
            return 0;
         } else {
            assert this.valuesDisbursed <= this.length;

            return this.values[this.valuesDisbursed++];
         }
      }

      public void resetForSecondPass() {
         assert this.phase() == 6;

         assert this.valuesDisbursed == this.length();

         this.valuesDisbursed = 0;
      }
   }

   abstract class Band {
      private int phase = 0;
      private final String name;
      private int valuesExpected;
      protected long outputSize = -1L;
      public final Coding regularCoding;
      public final int seqForDebug;
      public int elementCountForDebug;
      protected int lengthForDebug = -1;

      protected Band(String var2, Coding var3) {
         this.name = var2;
         this.regularCoding = var3;
         this.seqForDebug = ++BandStructure.nextSeqForDebug;
         if (BandStructure.this.verbose > 2) {
            Utils.log.fine("Band " + this.seqForDebug + " is " + var2);
         }

      }

      public BandStructure.Band init() {
         if (BandStructure.this.isReader) {
            this.readyToExpect();
         } else {
            this.readyToCollect();
         }

         return this;
      }

      boolean isReader() {
         return BandStructure.this.isReader;
      }

      int phase() {
         return this.phase;
      }

      String name() {
         return this.name;
      }

      public abstract int capacity();

      protected abstract void setCapacity(int var1);

      public abstract int length();

      protected abstract int valuesRemainingForDebug();

      public final int valuesExpected() {
         return this.valuesExpected;
      }

      public final void writeTo(OutputStream var1) throws IOException {
         assert BandStructure.this.assertReadyToWriteTo(this, var1);

         this.setPhase(5);
         this.writeDataTo(var1);
         this.doneWriting();
      }

      abstract void chooseBandCodings() throws IOException;

      public final long outputSize() {
         if (this.outputSize >= 0L) {
            long var1 = this.outputSize;

            assert var1 == this.computeOutputSize();

            return var1;
         } else {
            return this.computeOutputSize();
         }
      }

      protected abstract long computeOutputSize();

      protected abstract void writeDataTo(OutputStream var1) throws IOException;

      void expectLength(int var1) {
         assert BandStructure.assertPhase(this, 2);

         assert this.valuesExpected == 0;

         assert var1 >= 0;

         this.valuesExpected = var1;
      }

      void expectMoreLength(int var1) {
         assert BandStructure.assertPhase(this, 2);

         this.valuesExpected += var1;
      }

      private void readyToCollect() {
         this.setCapacity(1);
         this.setPhase(1);
      }

      protected void doneWriting() {
         assert BandStructure.assertPhase(this, 5);

         this.setPhase(8);
      }

      private void readyToExpect() {
         this.setPhase(2);
      }

      public final void readFrom(InputStream var1) throws IOException {
         assert BandStructure.this.assertReadyToReadFrom(this, var1);

         this.setCapacity(this.valuesExpected());
         this.setPhase(4);
         this.readDataFrom(var1);
         this.readyToDisburse();
      }

      protected abstract void readDataFrom(InputStream var1) throws IOException;

      protected void readyToDisburse() {
         if (BandStructure.this.verbose > 1) {
            Utils.log.fine("readyToDisburse " + this);
         }

         this.setPhase(6);
      }

      public void doneDisbursing() {
         assert BandStructure.assertPhase(this, 6);

         this.setPhase(8);
      }

      public final void doneWithUnusedBand() {
         if (BandStructure.this.isReader) {
            assert BandStructure.assertPhase(this, 2);

            assert this.valuesExpected() == 0;

            this.setPhase(4);
            this.setPhase(6);
            this.setPhase(8);
         } else {
            this.setPhase(3);
         }

      }

      protected void setPhase(int var1) {
         assert BandStructure.assertPhaseChangeOK(this, this.phase, var1);

         this.phase = var1;
      }

      public String toString() {
         int var1 = this.lengthForDebug != -1 ? this.lengthForDebug : this.length();
         String var2 = this.name;
         if (var1 != 0) {
            var2 = var2 + "[" + var1 + "]";
         }

         if (this.elementCountForDebug != 0) {
            var2 = var2 + "(" + this.elementCountForDebug + ")";
         }

         return var2;
      }
   }
}
