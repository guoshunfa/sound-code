package com.sun.java.util.jar.pack;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

abstract class ConstantPool {
   protected static final ConstantPool.Entry[] noRefs = new ConstantPool.Entry[0];
   protected static final ConstantPool.ClassEntry[] noClassRefs = new ConstantPool.ClassEntry[0];
   static final byte[] TAGS_IN_ORDER = new byte[]{1, 3, 4, 5, 6, 8, 7, 13, 12, 9, 10, 11, 15, 16, 17, 18};
   static final byte[] TAG_ORDER = new byte[19];
   static final byte[] NUMBER_TAGS;
   static final byte[] EXTRA_TAGS;
   static final byte[] LOADABLE_VALUE_TAGS;
   static final byte[] ANY_MEMBER_TAGS;
   static final byte[] FIELD_SPECIFIC_TAGS;

   private ConstantPool() {
   }

   static int verbose() {
      return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
   }

   public static synchronized ConstantPool.Utf8Entry getUtf8Entry(String var0) {
      Map var1 = Utils.getTLGlobals().getUtf8Entries();
      ConstantPool.Utf8Entry var2 = (ConstantPool.Utf8Entry)var1.get(var0);
      if (var2 == null) {
         var2 = new ConstantPool.Utf8Entry(var0);
         var1.put(var2.stringValue(), var2);
      }

      return var2;
   }

   public static ConstantPool.ClassEntry getClassEntry(String var0) {
      Map var1 = Utils.getTLGlobals().getClassEntries();
      ConstantPool.ClassEntry var2 = (ConstantPool.ClassEntry)var1.get(var0);
      if (var2 == null) {
         var2 = new ConstantPool.ClassEntry(getUtf8Entry(var0));

         assert var0.equals(var2.stringValue());

         var1.put(var2.stringValue(), var2);
      }

      return var2;
   }

   public static ConstantPool.LiteralEntry getLiteralEntry(Comparable<?> var0) {
      Map var1 = Utils.getTLGlobals().getLiteralEntries();
      Object var2 = (ConstantPool.LiteralEntry)var1.get(var0);
      if (var2 == null) {
         if (var0 instanceof String) {
            var2 = new ConstantPool.StringEntry(getUtf8Entry((String)var0));
         } else {
            var2 = new ConstantPool.NumberEntry((Number)var0);
         }

         var1.put(var0, var2);
      }

      return (ConstantPool.LiteralEntry)var2;
   }

   public static ConstantPool.StringEntry getStringEntry(String var0) {
      return (ConstantPool.StringEntry)getLiteralEntry(var0);
   }

   public static ConstantPool.SignatureEntry getSignatureEntry(String var0) {
      Map var1 = Utils.getTLGlobals().getSignatureEntries();
      ConstantPool.SignatureEntry var2 = (ConstantPool.SignatureEntry)var1.get(var0);
      if (var2 == null) {
         var2 = new ConstantPool.SignatureEntry(var0);

         assert var2.stringValue().equals(var0);

         var1.put(var0, var2);
      }

      return var2;
   }

   public static ConstantPool.SignatureEntry getSignatureEntry(ConstantPool.Utf8Entry var0, ConstantPool.ClassEntry[] var1) {
      return getSignatureEntry(ConstantPool.SignatureEntry.stringValueOf(var0, var1));
   }

   public static ConstantPool.DescriptorEntry getDescriptorEntry(ConstantPool.Utf8Entry var0, ConstantPool.SignatureEntry var1) {
      Map var2 = Utils.getTLGlobals().getDescriptorEntries();
      String var3 = ConstantPool.DescriptorEntry.stringValueOf(var0, var1);
      ConstantPool.DescriptorEntry var4 = (ConstantPool.DescriptorEntry)var2.get(var3);
      if (var4 == null) {
         var4 = new ConstantPool.DescriptorEntry(var0, var1);

         assert var4.stringValue().equals(var3) : var4.stringValue() + " != " + var3;

         var2.put(var3, var4);
      }

      return var4;
   }

   public static ConstantPool.DescriptorEntry getDescriptorEntry(ConstantPool.Utf8Entry var0, ConstantPool.Utf8Entry var1) {
      return getDescriptorEntry(var0, getSignatureEntry(var1.stringValue()));
   }

   public static ConstantPool.MemberEntry getMemberEntry(byte var0, ConstantPool.ClassEntry var1, ConstantPool.DescriptorEntry var2) {
      Map var3 = Utils.getTLGlobals().getMemberEntries();
      String var4 = ConstantPool.MemberEntry.stringValueOf(var0, var1, var2);
      ConstantPool.MemberEntry var5 = (ConstantPool.MemberEntry)var3.get(var4);
      if (var5 == null) {
         var5 = new ConstantPool.MemberEntry(var0, var1, var2);

         assert var5.stringValue().equals(var4) : var5.stringValue() + " != " + var4;

         var3.put(var4, var5);
      }

      return var5;
   }

   public static ConstantPool.MethodHandleEntry getMethodHandleEntry(byte var0, ConstantPool.MemberEntry var1) {
      Map var2 = Utils.getTLGlobals().getMethodHandleEntries();
      String var3 = ConstantPool.MethodHandleEntry.stringValueOf(var0, var1);
      ConstantPool.MethodHandleEntry var4 = (ConstantPool.MethodHandleEntry)var2.get(var3);
      if (var4 == null) {
         var4 = new ConstantPool.MethodHandleEntry(var0, var1);

         assert var4.stringValue().equals(var3);

         var2.put(var3, var4);
      }

      return var4;
   }

   public static ConstantPool.MethodTypeEntry getMethodTypeEntry(ConstantPool.SignatureEntry var0) {
      Map var1 = Utils.getTLGlobals().getMethodTypeEntries();
      String var2 = var0.stringValue();
      ConstantPool.MethodTypeEntry var3 = (ConstantPool.MethodTypeEntry)var1.get(var2);
      if (var3 == null) {
         var3 = new ConstantPool.MethodTypeEntry(var0);

         assert var3.stringValue().equals(var2);

         var1.put(var2, var3);
      }

      return var3;
   }

   public static ConstantPool.MethodTypeEntry getMethodTypeEntry(ConstantPool.Utf8Entry var0) {
      return getMethodTypeEntry(getSignatureEntry(var0.stringValue()));
   }

   public static ConstantPool.InvokeDynamicEntry getInvokeDynamicEntry(ConstantPool.BootstrapMethodEntry var0, ConstantPool.DescriptorEntry var1) {
      Map var2 = Utils.getTLGlobals().getInvokeDynamicEntries();
      String var3 = ConstantPool.InvokeDynamicEntry.stringValueOf(var0, var1);
      ConstantPool.InvokeDynamicEntry var4 = (ConstantPool.InvokeDynamicEntry)var2.get(var3);
      if (var4 == null) {
         var4 = new ConstantPool.InvokeDynamicEntry(var0, var1);

         assert var4.stringValue().equals(var3);

         var2.put(var3, var4);
      }

      return var4;
   }

   public static ConstantPool.BootstrapMethodEntry getBootstrapMethodEntry(ConstantPool.MethodHandleEntry var0, ConstantPool.Entry[] var1) {
      Map var2 = Utils.getTLGlobals().getBootstrapMethodEntries();
      String var3 = ConstantPool.BootstrapMethodEntry.stringValueOf(var0, var1);
      ConstantPool.BootstrapMethodEntry var4 = (ConstantPool.BootstrapMethodEntry)var2.get(var3);
      if (var4 == null) {
         var4 = new ConstantPool.BootstrapMethodEntry(var0, var1);

         assert var4.stringValue().equals(var3);

         var2.put(var3, var4);
      }

      return var4;
   }

   static boolean isMemberTag(byte var0) {
      switch(var0) {
      case 9:
      case 10:
      case 11:
         return true;
      default:
         return false;
      }
   }

   static byte numberTagOf(Number var0) {
      if (var0 instanceof Integer) {
         return 3;
      } else if (var0 instanceof Float) {
         return 4;
      } else if (var0 instanceof Long) {
         return 5;
      } else if (var0 instanceof Double) {
         return 6;
      } else {
         throw new RuntimeException("bad literal value " + var0);
      }
   }

   static boolean isRefKind(byte var0) {
      return 1 <= var0 && var0 <= 9;
   }

   static String qualifiedStringValue(ConstantPool.Entry var0, ConstantPool.Entry var1) {
      return qualifiedStringValue(var0.stringValue(), var1.stringValue());
   }

   static String qualifiedStringValue(String var0, String var1) {
      assert var0.indexOf(".") < 0;

      return var0 + "." + var1;
   }

   static int compareSignatures(String var0, String var1) {
      return compareSignatures(var0, var1, (String[])null, (String[])null);
   }

   static int compareSignatures(String var0, String var1, String[] var2, String[] var3) {
      char var6 = var0.charAt(0);
      char var7 = var1.charAt(0);
      if (var6 != '(' && var7 == '(') {
         return -1;
      } else if (var7 != '(' && var6 == '(') {
         return 1;
      } else {
         if (var2 == null) {
            var2 = structureSignature(var0);
         }

         if (var3 == null) {
            var3 = structureSignature(var1);
         }

         if (var2.length != var3.length) {
            return var2.length - var3.length;
         } else {
            int var8 = var2.length;
            int var9 = var8;

            int var10;
            do {
               --var9;
               if (var9 < 0) {
                  assert var0.equals(var1);

                  return 0;
               }

               var10 = var2[var9].compareTo(var3[var9]);
            } while(var10 == 0);

            return var10;
         }
      }
   }

   static int countClassParts(ConstantPool.Utf8Entry var0) {
      int var1 = 0;
      String var2 = var0.stringValue();

      for(int var3 = 0; var3 < var2.length(); ++var3) {
         if (var2.charAt(var3) == 'L') {
            ++var1;
         }
      }

      return var1;
   }

   static String flattenSignature(String[] var0) {
      String var1 = var0[0];
      if (var0.length == 1) {
         return var1;
      } else {
         int var2 = var1.length();

         for(int var3 = 1; var3 < var0.length; ++var3) {
            var2 += var0[var3].length();
         }

         char[] var9 = new char[var2];
         int var4 = 0;
         int var5 = 1;

         for(int var6 = 0; var6 < var1.length(); ++var6) {
            char var7 = var1.charAt(var6);
            var9[var4++] = var7;
            if (var7 == 'L') {
               String var8 = var0[var5++];
               var8.getChars(0, var8.length(), var9, var4);
               var4 += var8.length();
            }
         }

         assert var4 == var2;

         assert var5 == var0.length;

         return new String(var9);
      }
   }

   private static int skipTo(char var0, String var1, int var2) {
      var2 = var1.indexOf(var0, var2);
      return var2 >= 0 ? var2 : var1.length();
   }

   static String[] structureSignature(String var0) {
      int var1 = var0.indexOf(76);
      if (var1 < 0) {
         String[] var12 = new String[]{var0};
         return var12;
      } else {
         char[] var2 = null;
         String[] var3 = null;

         for(int var4 = 0; var4 <= 1; ++var4) {
            int var5 = 0;
            int var6 = 1;
            int var7 = 0;
            int var8 = 0;
            int var9 = 0;

            int var11;
            for(int var10 = var1 + 1; var10 > 0; var10 = var0.indexOf(76, var11) + 1) {
               if (var7 < var10) {
                  var7 = skipTo(';', var0, var10);
               }

               if (var8 < var10) {
                  var8 = skipTo('<', var0, var10);
               }

               var11 = var7 < var8 ? var7 : var8;
               if (var4 != 0) {
                  var0.getChars(var9, var10, var2, var5);
                  var3[var6] = var0.substring(var10, var11);
               }

               var5 += var10 - var9;
               ++var6;
               var9 = var11;
            }

            if (var4 != 0) {
               var0.getChars(var9, var0.length(), var2, var5);
               break;
            }

            var5 += var0.length() - var9;
            var2 = new char[var5];
            var3 = new String[var6];
         }

         var3[0] = new String(var2);
         return var3;
      }
   }

   public static ConstantPool.Index makeIndex(String var0, ConstantPool.Entry[] var1) {
      return new ConstantPool.Index(var0, var1);
   }

   public static ConstantPool.Index makeIndex(String var0, Collection<ConstantPool.Entry> var1) {
      return new ConstantPool.Index(var0, var1);
   }

   public static void sort(ConstantPool.Index var0) {
      var0.clearIndex();
      Arrays.sort((Object[])var0.cpMap);
      if (verbose() > 2) {
         System.out.println("sorted " + var0.dumpString());
      }

   }

   public static ConstantPool.Index[] partition(ConstantPool.Index var0, int[] var1) {
      ArrayList var2 = new ArrayList();
      ConstantPool.Entry[] var3 = var0.cpMap;

      assert var1.length == var3.length;

      int var5;
      for(int var4 = 0; var4 < var1.length; ++var4) {
         var5 = var1[var4];
         if (var5 >= 0) {
            while(var5 >= var2.size()) {
               var2.add((Object)null);
            }

            Object var6 = (List)var2.get(var5);
            if (var6 == null) {
               var2.set(var5, var6 = new ArrayList());
            }

            ((List)var6).add(var3[var4]);
         }
      }

      ConstantPool.Index[] var7 = new ConstantPool.Index[var2.size()];

      for(var5 = 0; var5 < var7.length; ++var5) {
         List var8 = (List)var2.get(var5);
         if (var8 != null) {
            var7[var5] = new ConstantPool.Index(var0.debugName + "/part#" + var5, var8);

            assert var7[var5].indexOf((ConstantPool.Entry)var8.get(0)) == 0;
         }
      }

      return var7;
   }

   public static ConstantPool.Index[] partitionByTag(ConstantPool.Index var0) {
      ConstantPool.Entry[] var1 = var0.cpMap;
      int[] var2 = new int[var1.length];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ConstantPool.Entry var4 = var1[var3];
         var2[var3] = var4 == null ? -1 : var4.tag;
      }

      ConstantPool.Index[] var5 = partition(var0, var2);

      for(int var6 = 0; var6 < var5.length; ++var6) {
         if (var5[var6] != null) {
            var5[var6].debugName = tagName(var6);
         }
      }

      if (var5.length < 19) {
         ConstantPool.Index[] var7 = new ConstantPool.Index[19];
         System.arraycopy(var5, 0, var7, 0, var5.length);
         var5 = var7;
      }

      return var5;
   }

   public static void completeReferencesIn(Set<ConstantPool.Entry> var0, boolean var1) {
      completeReferencesIn(var0, var1, (List)null);
   }

   public static void completeReferencesIn(Set<ConstantPool.Entry> var0, boolean var1, List<ConstantPool.BootstrapMethodEntry> var2) {
      var0.remove((Object)null);
      ListIterator var3 = (new ArrayList(var0)).listIterator(var0.size());

      while(var3.hasPrevious()) {
         Object var4 = (ConstantPool.Entry)var3.previous();
         var3.remove();

         assert var4 != null;

         if (var1 && ((ConstantPool.Entry)var4).tag == 13) {
            ConstantPool.SignatureEntry var5 = (ConstantPool.SignatureEntry)var4;
            ConstantPool.Utf8Entry var6 = var5.asUtf8Entry();
            var0.remove(var5);
            var0.add(var6);
            var4 = var6;
         }

         if (var2 != null && ((ConstantPool.Entry)var4).tag == 17) {
            ConstantPool.BootstrapMethodEntry var7 = (ConstantPool.BootstrapMethodEntry)var4;
            var0.remove(var7);
            if (!var2.contains(var7)) {
               var2.add(var7);
            }
         }

         int var8 = 0;

         while(true) {
            ConstantPool.Entry var9 = ((ConstantPool.Entry)var4).getRef(var8);
            if (var9 == null) {
               break;
            }

            if (var0.add(var9)) {
               var3.add(var9);
            }

            ++var8;
         }
      }

   }

   static double percent(int var0, int var1) {
      return (double)((int)(10000.0D * (double)var0 / (double)var1 + 0.5D)) / 100.0D;
   }

   public static String tagName(int var0) {
      switch(var0) {
      case 0:
         return "**None";
      case 1:
         return "Utf8";
      case 2:
      case 14:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
      default:
         return "tag#" + var0;
      case 3:
         return "Integer";
      case 4:
         return "Float";
      case 5:
         return "Long";
      case 6:
         return "Double";
      case 7:
         return "Class";
      case 8:
         return "String";
      case 9:
         return "Fieldref";
      case 10:
         return "Methodref";
      case 11:
         return "InterfaceMethodref";
      case 12:
         return "NameandType";
      case 13:
         return "*Signature";
      case 15:
         return "MethodHandle";
      case 16:
         return "MethodType";
      case 17:
         return "*BootstrapMethod";
      case 18:
         return "InvokeDynamic";
      case 50:
         return "**All";
      case 51:
         return "**LoadableValue";
      case 52:
         return "**AnyMember";
      case 53:
         return "*FieldSpecific";
      }
   }

   public static String refKindName(int var0) {
      switch(var0) {
      case 1:
         return "getField";
      case 2:
         return "getStatic";
      case 3:
         return "putField";
      case 4:
         return "putStatic";
      case 5:
         return "invokeVirtual";
      case 6:
         return "invokeStatic";
      case 7:
         return "invokeSpecial";
      case 8:
         return "newInvokeSpecial";
      case 9:
         return "invokeInterface";
      default:
         return "refKind#" + var0;
      }
   }

   private static boolean verifyTagOrder(byte[] var0) {
      byte var1 = -1;
      byte[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var2[var4];
         byte var6 = TAG_ORDER[var5];

         assert var6 > 0 : "tag not found: " + var5;

         assert TAGS_IN_ORDER[var6 - 1] == var5 : "tag repeated: " + var5 + " => " + var6 + " => " + TAGS_IN_ORDER[var6 - 1];

         assert var1 < var6 : "tags not in order: " + Arrays.toString(var0) + " at " + var5;

         var1 = var6;
      }

      return true;
   }

   static {
      for(int var0 = 0; var0 < TAGS_IN_ORDER.length; ++var0) {
         TAG_ORDER[TAGS_IN_ORDER[var0]] = (byte)(var0 + 1);
      }

      NUMBER_TAGS = new byte[]{3, 4, 5, 6};
      EXTRA_TAGS = new byte[]{15, 16, 17, 18};
      LOADABLE_VALUE_TAGS = new byte[]{3, 4, 5, 6, 8, 7, 15, 16};
      ANY_MEMBER_TAGS = new byte[]{9, 10, 11};
      FIELD_SPECIFIC_TAGS = new byte[]{3, 4, 5, 6, 8};

      assert verifyTagOrder(TAGS_IN_ORDER) && verifyTagOrder(NUMBER_TAGS) && verifyTagOrder(EXTRA_TAGS) && verifyTagOrder(LOADABLE_VALUE_TAGS) && verifyTagOrder(ANY_MEMBER_TAGS) && verifyTagOrder(FIELD_SPECIFIC_TAGS);
   }

   public static class IndexGroup {
      private ConstantPool.Index[] indexByTag = new ConstantPool.Index[19];
      private ConstantPool.Index[] indexByTagGroup;
      private int[] untypedFirstIndexByTag;
      private int totalSizeQQ;
      private ConstantPool.Index[][] indexByTagAndClass;

      private ConstantPool.Index makeTagGroupIndex(byte var1, byte[] var2) {
         if (this.indexByTagGroup == null) {
            this.indexByTagGroup = new ConstantPool.Index[4];
         }

         int var3 = var1 - 50;

         assert this.indexByTagGroup[var3] == null;

         int var4 = 0;
         ConstantPool.Entry[] var5 = null;

         for(int var6 = 1; var6 <= 2; ++var6) {
            this.untypedIndexOf((ConstantPool.Entry)null);
            byte[] var7 = var2;
            int var8 = var2.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               byte var10 = var7[var9];
               ConstantPool.Index var11 = this.indexByTag[var10];
               if (var11 != null) {
                  int var12 = var11.cpMap.length;
                  if (var12 != 0) {
                     if (!$assertionsDisabled) {
                        if (var1 == 50) {
                           if (var4 != this.untypedFirstIndexByTag[var10]) {
                              throw new AssertionError();
                           }
                        } else if (var4 >= this.untypedFirstIndexByTag[var10]) {
                           throw new AssertionError();
                        }
                     }

                     if (var5 != null) {
                        assert var5[var4] == null;

                        assert var5[var4 + var12 - 1] == null;

                        System.arraycopy(var11.cpMap, 0, var5, var4, var12);
                     }

                     var4 += var12;
                  }
               }
            }

            if (var5 == null) {
               assert var6 == 1;

               var5 = new ConstantPool.Entry[var4];
               var4 = 0;
            }
         }

         this.indexByTagGroup[var3] = new ConstantPool.Index(ConstantPool.tagName(var1), var5);
         return this.indexByTagGroup[var3];
      }

      public int untypedIndexOf(ConstantPool.Entry var1) {
         if (this.untypedFirstIndexByTag == null) {
            this.untypedFirstIndexByTag = new int[20];
            int var2 = 0;

            for(int var3 = 0; var3 < ConstantPool.TAGS_IN_ORDER.length; ++var3) {
               byte var4 = ConstantPool.TAGS_IN_ORDER[var3];
               ConstantPool.Index var5 = this.indexByTag[var4];
               if (var5 != null) {
                  int var6 = var5.cpMap.length;
                  this.untypedFirstIndexByTag[var4] = var2;
                  var2 += var6;
               }
            }

            this.untypedFirstIndexByTag[19] = var2;
         }

         if (var1 == null) {
            return -1;
         } else {
            byte var7 = var1.tag;
            ConstantPool.Index var8 = this.indexByTag[var7];
            if (var8 == null) {
               return -1;
            } else {
               int var9 = var8.findIndexOf(var1);
               if (var9 >= 0) {
                  var9 += this.untypedFirstIndexByTag[var7];
               }

               return var9;
            }
         }
      }

      public void initIndexByTag(byte var1, ConstantPool.Index var2) {
         assert this.indexByTag[var1] == null;

         ConstantPool.Entry[] var3 = var2.cpMap;

         for(int var4 = 0; var4 < var3.length; ++var4) {
            assert var3[var4].tag == var1;
         }

         assert var1 != 1 || var3.length == 0 || var3[0].stringValue().equals("");

         this.indexByTag[var1] = var2;
         this.untypedFirstIndexByTag = null;
         this.indexByTagGroup = null;
         if (this.indexByTagAndClass != null) {
            this.indexByTagAndClass[var1] = null;
         }

      }

      public ConstantPool.Index getIndexByTag(byte var1) {
         if (var1 >= 50) {
            return this.getIndexByTagGroup(var1);
         } else {
            ConstantPool.Index var2 = this.indexByTag[var1];
            if (var2 == null) {
               var2 = new ConstantPool.Index(ConstantPool.tagName(var1), new ConstantPool.Entry[0]);
               this.indexByTag[var1] = var2;
            }

            return var2;
         }
      }

      private ConstantPool.Index getIndexByTagGroup(byte var1) {
         if (this.indexByTagGroup != null) {
            ConstantPool.Index var2 = this.indexByTagGroup[var1 - 50];
            if (var2 != null) {
               return var2;
            }
         }

         switch(var1) {
         case 50:
            return this.makeTagGroupIndex((byte)50, ConstantPool.TAGS_IN_ORDER);
         case 51:
            return this.makeTagGroupIndex((byte)51, ConstantPool.LOADABLE_VALUE_TAGS);
         case 52:
            return this.makeTagGroupIndex((byte)52, ConstantPool.ANY_MEMBER_TAGS);
         case 53:
            return null;
         default:
            throw new AssertionError("bad tag group " + var1);
         }
      }

      public ConstantPool.Index getMemberIndex(byte var1, ConstantPool.ClassEntry var2) {
         if (var2 == null) {
            throw new RuntimeException("missing class reference for " + ConstantPool.tagName(var1));
         } else {
            if (this.indexByTagAndClass == null) {
               this.indexByTagAndClass = new ConstantPool.Index[19][];
            }

            ConstantPool.Index var3 = this.getIndexByTag((byte)7);
            ConstantPool.Index[] var4 = this.indexByTagAndClass[var1];
            if (var4 == null) {
               ConstantPool.Index var5 = this.getIndexByTag(var1);
               int[] var6 = new int[var5.size()];

               int var7;
               for(var7 = 0; var7 < var6.length; ++var7) {
                  ConstantPool.MemberEntry var8 = (ConstantPool.MemberEntry)var5.get(var7);
                  int var9 = var3.indexOf(var8.classRef);
                  var6[var7] = var9;
               }

               var4 = ConstantPool.partition(var5, var6);

               for(var7 = 0; var7 < var4.length; ++var7) {
                  assert var4[var7] == null || var4[var7].assertIsSorted();
               }

               this.indexByTagAndClass[var1] = var4;
            }

            int var10 = var3.indexOf(var2);
            return var4[var10];
         }
      }

      public int getOverloadingIndex(ConstantPool.MemberEntry var1) {
         ConstantPool.Index var2 = this.getMemberIndex(var1.tag, var1.classRef);
         ConstantPool.Utf8Entry var3 = var1.descRef.nameRef;
         int var4 = 0;

         for(int var5 = 0; var5 < var2.cpMap.length; ++var5) {
            ConstantPool.MemberEntry var6 = (ConstantPool.MemberEntry)var2.cpMap[var5];
            if (var6.equals(var1)) {
               return var4;
            }

            if (var6.descRef.nameRef.equals(var3)) {
               ++var4;
            }
         }

         throw new RuntimeException("should not reach here");
      }

      public ConstantPool.MemberEntry getOverloadingForIndex(byte var1, ConstantPool.ClassEntry var2, String var3, int var4) {
         assert var3.equals(var3.intern());

         ConstantPool.Index var5 = this.getMemberIndex(var1, var2);
         int var6 = 0;

         for(int var7 = 0; var7 < var5.cpMap.length; ++var7) {
            ConstantPool.MemberEntry var8 = (ConstantPool.MemberEntry)var5.cpMap[var7];
            if (var8.descRef.nameRef.stringValue().equals(var3)) {
               if (var6 == var4) {
                  return var8;
               }

               ++var6;
            }
         }

         throw new RuntimeException("should not reach here");
      }

      public boolean haveNumbers() {
         byte[] var1 = ConstantPool.NUMBER_TAGS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            byte var4 = var1[var3];
            if (this.getIndexByTag(var4).size() > 0) {
               return true;
            }
         }

         return false;
      }

      public boolean haveExtraTags() {
         byte[] var1 = ConstantPool.EXTRA_TAGS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            byte var4 = var1[var3];
            if (this.getIndexByTag(var4).size() > 0) {
               return true;
            }
         }

         return false;
      }
   }

   public static final class Index extends AbstractList<ConstantPool.Entry> {
      protected String debugName;
      protected ConstantPool.Entry[] cpMap;
      protected boolean flattenSigs;
      protected ConstantPool.Entry[] indexKey;
      protected int[] indexValue;

      protected ConstantPool.Entry[] getMap() {
         return this.cpMap;
      }

      protected Index(String var1) {
         this.debugName = var1;
      }

      protected Index(String var1, ConstantPool.Entry[] var2) {
         this(var1);
         this.setMap(var2);
      }

      protected void setMap(ConstantPool.Entry[] var1) {
         this.clearIndex();
         this.cpMap = var1;
      }

      protected Index(String var1, Collection<ConstantPool.Entry> var2) {
         this(var1);
         this.setMap(var2);
      }

      protected void setMap(Collection<ConstantPool.Entry> var1) {
         this.cpMap = new ConstantPool.Entry[var1.size()];
         var1.toArray(this.cpMap);
         this.setMap(this.cpMap);
      }

      public int size() {
         return this.cpMap.length;
      }

      public ConstantPool.Entry get(int var1) {
         return this.cpMap[var1];
      }

      public ConstantPool.Entry getEntry(int var1) {
         return this.cpMap[var1];
      }

      private int findIndexOf(ConstantPool.Entry var1) {
         if (this.indexKey == null) {
            this.initializeIndex();
         }

         int var2 = this.findIndexLocation(var1);
         if (this.indexKey[var2] != var1) {
            if (this.flattenSigs && var1.tag == 13) {
               ConstantPool.SignatureEntry var4 = (ConstantPool.SignatureEntry)var1;
               return this.findIndexOf(var4.asUtf8Entry());
            } else {
               return -1;
            }
         } else {
            int var3 = this.indexValue[var2];

            assert var1.equals(this.cpMap[var3]);

            return var3;
         }
      }

      public boolean contains(ConstantPool.Entry var1) {
         return this.findIndexOf(var1) >= 0;
      }

      public int indexOf(ConstantPool.Entry var1) {
         int var2 = this.findIndexOf(var1);
         if (var2 < 0 && ConstantPool.verbose() > 0) {
            System.out.println("not found: " + var1);
            System.out.println("       in: " + this.dumpString());
            Thread.dumpStack();
         }

         assert var2 >= 0;

         return var2;
      }

      public int lastIndexOf(ConstantPool.Entry var1) {
         return this.indexOf(var1);
      }

      public boolean assertIsSorted() {
         for(int var1 = 1; var1 < this.cpMap.length; ++var1) {
            if (this.cpMap[var1 - 1].compareTo(this.cpMap[var1]) > 0) {
               System.out.println("Not sorted at " + (var1 - 1) + "/" + var1 + ": " + this.dumpString());
               return false;
            }
         }

         return true;
      }

      protected void clearIndex() {
         this.indexKey = null;
         this.indexValue = null;
      }

      private int findIndexLocation(ConstantPool.Entry var1) {
         int var2 = this.indexKey.length;
         int var3 = var1.hashCode();
         int var4 = var3 & var2 - 1;
         int var5 = (var3 >>> 8 | 1) & var2 - 1;

         while(true) {
            ConstantPool.Entry var6 = this.indexKey[var4];
            if (var6 == var1 || var6 == null) {
               return var4;
            }

            var4 += var5;
            if (var4 >= var2) {
               var4 -= var2;
            }
         }
      }

      private void initializeIndex() {
         if (ConstantPool.verbose() > 2) {
            System.out.println("initialize Index " + this.debugName + " [" + this.size() + "]");
         }

         int var1 = (int)((double)(this.cpMap.length + 10) * 1.5D);

         int var2;
         for(var2 = 1; var2 < var1; var2 <<= 1) {
         }

         this.indexKey = new ConstantPool.Entry[var2];
         this.indexValue = new int[var2];

         for(int var3 = 0; var3 < this.cpMap.length; ++var3) {
            ConstantPool.Entry var4 = this.cpMap[var3];
            if (var4 != null) {
               int var5 = this.findIndexLocation(var4);

               assert this.indexKey[var5] == null;

               this.indexKey[var5] = var4;
               this.indexValue[var5] = var3;
            }
         }

      }

      public ConstantPool.Entry[] toArray(ConstantPool.Entry[] var1) {
         int var2 = this.size();
         if (var1.length < var2) {
            return (ConstantPool.Entry[])super.toArray(var1);
         } else {
            System.arraycopy(this.cpMap, 0, var1, 0, var2);
            if (var1.length > var2) {
               var1[var2] = null;
            }

            return var1;
         }
      }

      public ConstantPool.Entry[] toArray() {
         return this.toArray(new ConstantPool.Entry[this.size()]);
      }

      public Object clone() {
         return new ConstantPool.Index(this.debugName, (ConstantPool.Entry[])this.cpMap.clone());
      }

      public String toString() {
         return "Index " + this.debugName + " [" + this.size() + "]";
      }

      public String dumpString() {
         String var1 = this.toString();
         var1 = var1 + " {\n";

         for(int var2 = 0; var2 < this.cpMap.length; ++var2) {
            var1 = var1 + "    " + var2 + ": " + this.cpMap[var2] + "\n";
         }

         var1 = var1 + "}";
         return var1;
      }
   }

   public static class BootstrapMethodEntry extends ConstantPool.Entry {
      final ConstantPool.MethodHandleEntry bsmRef;
      final ConstantPool.Entry[] argRefs;

      public ConstantPool.Entry getRef(int var1) {
         if (var1 == 0) {
            return this.bsmRef;
         } else {
            return var1 - 1 < this.argRefs.length ? this.argRefs[var1 - 1] : null;
         }
      }

      protected int computeValueHash() {
         int var1 = this.bsmRef.hashCode();
         return Arrays.hashCode((Object[])this.argRefs) + (var1 << 8) ^ var1;
      }

      BootstrapMethodEntry(ConstantPool.MethodHandleEntry var1, ConstantPool.Entry[] var2) {
         super((byte)17);
         this.bsmRef = var1;
         this.argRefs = (ConstantPool.Entry[])var2.clone();
         this.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == ConstantPool.BootstrapMethodEntry.class) {
            ConstantPool.BootstrapMethodEntry var2 = (ConstantPool.BootstrapMethodEntry)var1;
            return this.bsmRef.eq(var2.bsmRef) && Arrays.equals((Object[])this.argRefs, (Object[])var2.argRefs);
         } else {
            return false;
         }
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.BootstrapMethodEntry var3 = (ConstantPool.BootstrapMethodEntry)var1;
            if (Utils.SORT_BSS_BSM_MAJOR) {
               var2 = this.bsmRef.compareTo(var3.bsmRef);
            }

            if (var2 == 0) {
               var2 = compareArgArrays(this.argRefs, var3.argRefs);
            }

            if (var2 == 0) {
               var2 = this.bsmRef.compareTo(var3.bsmRef);
            }
         }

         return var2;
      }

      public String stringValue() {
         return stringValueOf(this.bsmRef, this.argRefs);
      }

      static String stringValueOf(ConstantPool.MethodHandleEntry var0, ConstantPool.Entry[] var1) {
         StringBuffer var2 = new StringBuffer(var0.stringValue());
         char var3 = '<';
         boolean var4 = false;
         ConstantPool.Entry[] var5 = var1;
         int var6 = var1.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ConstantPool.Entry var8 = var5[var7];
            var2.append(var3).append(var8.stringValue());
            var3 = ';';
         }

         if (var3 == '<') {
            var2.append(var3);
         }

         var2.append('>');
         return var2.toString();
      }

      static int compareArgArrays(ConstantPool.Entry[] var0, ConstantPool.Entry[] var1) {
         int var2 = var0.length - var1.length;
         if (var2 != 0) {
            return var2;
         } else {
            for(int var3 = 0; var3 < var0.length; ++var3) {
               var2 = var0[var3].compareTo(var1[var3]);
               if (var2 != 0) {
                  break;
               }
            }

            return var2;
         }
      }
   }

   public static class InvokeDynamicEntry extends ConstantPool.Entry {
      final ConstantPool.BootstrapMethodEntry bssRef;
      final ConstantPool.DescriptorEntry descRef;

      public ConstantPool.Entry getRef(int var1) {
         if (var1 == 0) {
            return this.bssRef;
         } else {
            return var1 == 1 ? this.descRef : null;
         }
      }

      protected int computeValueHash() {
         int var1 = this.descRef.hashCode();
         return this.bssRef.hashCode() + (var1 << 8) ^ var1;
      }

      InvokeDynamicEntry(ConstantPool.BootstrapMethodEntry var1, ConstantPool.DescriptorEntry var2) {
         super((byte)18);
         this.bssRef = var1;
         this.descRef = var2;
         this.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == ConstantPool.InvokeDynamicEntry.class) {
            ConstantPool.InvokeDynamicEntry var2 = (ConstantPool.InvokeDynamicEntry)var1;
            return this.bssRef.eq(var2.bssRef) && this.descRef.eq(var2.descRef);
         } else {
            return false;
         }
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.InvokeDynamicEntry var3 = (ConstantPool.InvokeDynamicEntry)var1;
            if (Utils.SORT_INDY_BSS_MAJOR) {
               var2 = this.bssRef.compareTo(var3.bssRef);
            }

            if (var2 == 0) {
               var2 = this.descRef.compareTo(var3.descRef);
            }

            if (var2 == 0) {
               var2 = this.bssRef.compareTo(var3.bssRef);
            }
         }

         return var2;
      }

      public String stringValue() {
         return stringValueOf(this.bssRef, this.descRef);
      }

      static String stringValueOf(ConstantPool.BootstrapMethodEntry var0, ConstantPool.DescriptorEntry var1) {
         return "Indy:" + var0.stringValue() + "." + var1.stringValue();
      }
   }

   public static class MethodTypeEntry extends ConstantPool.Entry {
      final ConstantPool.SignatureEntry typeRef;

      public ConstantPool.Entry getRef(int var1) {
         return var1 == 0 ? this.typeRef : null;
      }

      protected int computeValueHash() {
         return this.typeRef.hashCode() + this.tag;
      }

      MethodTypeEntry(ConstantPool.SignatureEntry var1) {
         super((byte)16);
         this.typeRef = var1;
         this.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == ConstantPool.MethodTypeEntry.class) {
            ConstantPool.MethodTypeEntry var2 = (ConstantPool.MethodTypeEntry)var1;
            return this.typeRef.eq(var2.typeRef);
         } else {
            return false;
         }
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.MethodTypeEntry var3 = (ConstantPool.MethodTypeEntry)var1;
            var2 = this.typeRef.compareTo(var3.typeRef);
         }

         return var2;
      }

      public String stringValue() {
         return this.typeRef.stringValue();
      }
   }

   public static class MethodHandleEntry extends ConstantPool.Entry {
      final int refKind;
      final ConstantPool.MemberEntry memRef;

      public ConstantPool.Entry getRef(int var1) {
         return var1 == 0 ? this.memRef : null;
      }

      protected int computeValueHash() {
         int var1 = this.refKind;
         return this.memRef.hashCode() + (var1 << 8) ^ var1;
      }

      MethodHandleEntry(byte var1, ConstantPool.MemberEntry var2) {
         super((byte)15);

         assert ConstantPool.isRefKind(var1);

         this.refKind = var1;
         this.memRef = var2;
         this.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == ConstantPool.MethodHandleEntry.class) {
            ConstantPool.MethodHandleEntry var2 = (ConstantPool.MethodHandleEntry)var1;
            return this.refKind == var2.refKind && this.memRef.eq(var2.memRef);
         } else {
            return false;
         }
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.MethodHandleEntry var3 = (ConstantPool.MethodHandleEntry)var1;
            if (Utils.SORT_HANDLES_KIND_MAJOR) {
               var2 = this.refKind - var3.refKind;
            }

            if (var2 == 0) {
               var2 = this.memRef.compareTo(var3.memRef);
            }

            if (var2 == 0) {
               var2 = this.refKind - var3.refKind;
            }
         }

         return var2;
      }

      public static String stringValueOf(int var0, ConstantPool.MemberEntry var1) {
         return ConstantPool.refKindName(var0) + ":" + var1.stringValue();
      }

      public String stringValue() {
         return stringValueOf(this.refKind, this.memRef);
      }
   }

   public static class SignatureEntry extends ConstantPool.Entry {
      final ConstantPool.Utf8Entry formRef;
      final ConstantPool.ClassEntry[] classRefs;
      String value;
      ConstantPool.Utf8Entry asUtf8Entry;

      public ConstantPool.Entry getRef(int var1) {
         if (var1 == 0) {
            return this.formRef;
         } else {
            return var1 - 1 < this.classRefs.length ? this.classRefs[var1 - 1] : null;
         }
      }

      SignatureEntry(String var1) {
         super((byte)13);
         var1 = var1.intern();
         this.value = var1;
         String[] var2 = ConstantPool.structureSignature(var1);
         this.formRef = ConstantPool.getUtf8Entry(var2[0]);
         this.classRefs = new ConstantPool.ClassEntry[var2.length - 1];

         for(int var3 = 1; var3 < var2.length; ++var3) {
            this.classRefs[var3 - 1] = ConstantPool.getClassEntry(var2[var3]);
         }

         this.hashCode();
      }

      protected int computeValueHash() {
         this.stringValue();
         return this.value.hashCode() + this.tag;
      }

      public ConstantPool.Utf8Entry asUtf8Entry() {
         if (this.asUtf8Entry == null) {
            this.asUtf8Entry = ConstantPool.getUtf8Entry(this.stringValue());
         }

         return this.asUtf8Entry;
      }

      public boolean equals(Object var1) {
         return var1 != null && var1.getClass() == ConstantPool.SignatureEntry.class && ((ConstantPool.SignatureEntry)var1).value.equals(this.value);
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.SignatureEntry var3 = (ConstantPool.SignatureEntry)var1;
            var2 = ConstantPool.compareSignatures(this.value, var3.value);
         }

         return var2;
      }

      public String stringValue() {
         if (this.value == null) {
            this.value = stringValueOf(this.formRef, this.classRefs);
         }

         return this.value;
      }

      static String stringValueOf(ConstantPool.Utf8Entry var0, ConstantPool.ClassEntry[] var1) {
         String[] var2 = new String[1 + var1.length];
         var2[0] = var0.stringValue();

         for(int var3 = 1; var3 < var2.length; ++var3) {
            var2[var3] = var1[var3 - 1].stringValue();
         }

         return ConstantPool.flattenSignature(var2).intern();
      }

      public int computeSize(boolean var1) {
         String var2 = this.formRef.stringValue();
         byte var3 = 0;
         int var4 = 1;
         if (this.isMethod()) {
            var3 = 1;
            var4 = var2.indexOf(41);
         }

         int var5 = 0;

         for(int var6 = var3; var6 < var4; ++var6) {
            label31:
            switch(var2.charAt(var6)) {
            case ';':
               continue;
            case 'D':
            case 'J':
               if (var1) {
                  ++var5;
               }
               break;
            case '[':
               while(true) {
                  if (var2.charAt(var6) != '[') {
                     break label31;
                  }

                  ++var6;
               }
            default:
               assert 0 <= "BSCIJFDZLV([".indexOf(var2.charAt(var6));
            }

            ++var5;
         }

         return var5;
      }

      public boolean isMethod() {
         return this.formRef.stringValue().charAt(0) == '(';
      }

      public byte getLiteralTag() {
         switch(this.formRef.stringValue().charAt(0)) {
         case 'B':
         case 'C':
         case 'S':
         case 'Z':
            return 3;
         case 'D':
            return 6;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         default:
            assert false;

            return 0;
         case 'F':
            return 4;
         case 'I':
            return 3;
         case 'J':
            return 5;
         case 'L':
            return 8;
         }
      }

      public String prettyString() {
         String var1;
         if (this.isMethod()) {
            var1 = this.formRef.stringValue();
            var1 = var1.substring(0, 1 + var1.indexOf(41));
         } else {
            var1 = "/" + this.formRef.stringValue();
         }

         int var2;
         while((var2 = var1.indexOf(59)) >= 0) {
            var1 = var1.substring(0, var2) + var1.substring(var2 + 1);
         }

         return var1;
      }
   }

   public static class MemberEntry extends ConstantPool.Entry {
      final ConstantPool.ClassEntry classRef;
      final ConstantPool.DescriptorEntry descRef;

      public ConstantPool.Entry getRef(int var1) {
         if (var1 == 0) {
            return this.classRef;
         } else {
            return var1 == 1 ? this.descRef : null;
         }
      }

      protected int computeValueHash() {
         int var1 = this.descRef.hashCode();
         return this.classRef.hashCode() + (var1 << 8) ^ var1;
      }

      MemberEntry(byte var1, ConstantPool.ClassEntry var2, ConstantPool.DescriptorEntry var3) {
         super(var1);

         assert ConstantPool.isMemberTag(var1);

         this.classRef = var2;
         this.descRef = var3;
         this.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == ConstantPool.MemberEntry.class) {
            ConstantPool.MemberEntry var2 = (ConstantPool.MemberEntry)var1;
            return this.classRef.eq(var2.classRef) && this.descRef.eq(var2.descRef);
         } else {
            return false;
         }
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.MemberEntry var3 = (ConstantPool.MemberEntry)var1;
            if (Utils.SORT_MEMBERS_DESCR_MAJOR) {
               var2 = this.descRef.compareTo(var3.descRef);
            }

            if (var2 == 0) {
               var2 = this.classRef.compareTo(var3.classRef);
            }

            if (var2 == 0) {
               var2 = this.descRef.compareTo(var3.descRef);
            }
         }

         return var2;
      }

      public String stringValue() {
         return stringValueOf(this.tag, this.classRef, this.descRef);
      }

      static String stringValueOf(byte var0, ConstantPool.ClassEntry var1, ConstantPool.DescriptorEntry var2) {
         assert ConstantPool.isMemberTag(var0);

         String var3;
         switch(var0) {
         case 9:
            var3 = "Field:";
            break;
         case 10:
            var3 = "Method:";
            break;
         case 11:
            var3 = "IMethod:";
            break;
         default:
            var3 = var0 + "???";
         }

         return var3 + ConstantPool.qualifiedStringValue((ConstantPool.Entry)var1, (ConstantPool.Entry)var2);
      }

      public boolean isMethod() {
         return this.descRef.isMethod();
      }
   }

   public static class DescriptorEntry extends ConstantPool.Entry {
      final ConstantPool.Utf8Entry nameRef;
      final ConstantPool.SignatureEntry typeRef;

      public ConstantPool.Entry getRef(int var1) {
         if (var1 == 0) {
            return this.nameRef;
         } else {
            return var1 == 1 ? this.typeRef : null;
         }
      }

      DescriptorEntry(ConstantPool.Entry var1, ConstantPool.Entry var2) {
         super((byte)12);
         if (var2 instanceof ConstantPool.Utf8Entry) {
            var2 = ConstantPool.getSignatureEntry(((ConstantPool.Entry)var2).stringValue());
         }

         this.nameRef = (ConstantPool.Utf8Entry)var1;
         this.typeRef = (ConstantPool.SignatureEntry)var2;
         this.hashCode();
      }

      protected int computeValueHash() {
         int var1 = this.typeRef.hashCode();
         return this.nameRef.hashCode() + (var1 << 8) ^ var1;
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == ConstantPool.DescriptorEntry.class) {
            ConstantPool.DescriptorEntry var2 = (ConstantPool.DescriptorEntry)var1;
            return this.nameRef.eq(var2.nameRef) && this.typeRef.eq(var2.typeRef);
         } else {
            return false;
         }
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            ConstantPool.DescriptorEntry var3 = (ConstantPool.DescriptorEntry)var1;
            var2 = this.typeRef.compareTo(var3.typeRef);
            if (var2 == 0) {
               var2 = this.nameRef.compareTo(var3.nameRef);
            }
         }

         return var2;
      }

      public String stringValue() {
         return stringValueOf(this.nameRef, this.typeRef);
      }

      static String stringValueOf(ConstantPool.Entry var0, ConstantPool.Entry var1) {
         return ConstantPool.qualifiedStringValue(var1, var0);
      }

      public String prettyString() {
         return this.nameRef.stringValue() + this.typeRef.prettyString();
      }

      public boolean isMethod() {
         return this.typeRef.isMethod();
      }

      public byte getLiteralTag() {
         return this.typeRef.getLiteralTag();
      }
   }

   public static class ClassEntry extends ConstantPool.Entry {
      final ConstantPool.Utf8Entry ref;

      public ConstantPool.Entry getRef(int var1) {
         return var1 == 0 ? this.ref : null;
      }

      protected int computeValueHash() {
         return this.ref.hashCode() + this.tag;
      }

      ClassEntry(ConstantPool.Entry var1) {
         super((byte)7);
         this.ref = (ConstantPool.Utf8Entry)var1;
         this.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 != null && var1.getClass() == ConstantPool.ClassEntry.class && ((ConstantPool.ClassEntry)var1).ref.eq(this.ref);
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            var2 = this.ref.compareTo(((ConstantPool.ClassEntry)var1).ref);
         }

         return var2;
      }

      public String stringValue() {
         return this.ref.stringValue();
      }
   }

   public static class StringEntry extends ConstantPool.LiteralEntry {
      final ConstantPool.Utf8Entry ref;

      public ConstantPool.Entry getRef(int var1) {
         return var1 == 0 ? this.ref : null;
      }

      StringEntry(ConstantPool.Entry var1) {
         super((byte)8);
         this.ref = (ConstantPool.Utf8Entry)var1;
         this.hashCode();
      }

      protected int computeValueHash() {
         return this.ref.hashCode() + this.tag;
      }

      public boolean equals(Object var1) {
         return var1 != null && var1.getClass() == ConstantPool.StringEntry.class && ((ConstantPool.StringEntry)var1).ref.eq(this.ref);
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            var2 = this.ref.compareTo(((ConstantPool.StringEntry)var1).ref);
         }

         return var2;
      }

      public Comparable<?> literalValue() {
         return this.ref.stringValue();
      }

      public String stringValue() {
         return this.ref.stringValue();
      }
   }

   public static class NumberEntry extends ConstantPool.LiteralEntry {
      final Number value;

      NumberEntry(Number var1) {
         super(ConstantPool.numberTagOf(var1));
         this.value = var1;
         this.hashCode();
      }

      protected int computeValueHash() {
         return this.value.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 != null && var1.getClass() == ConstantPool.NumberEntry.class && ((ConstantPool.NumberEntry)var1).value.equals(this.value);
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            Comparable var3 = (Comparable)this.value;
            var2 = var3.compareTo(((ConstantPool.NumberEntry)var1).value);
         }

         return var2;
      }

      public Number numberValue() {
         return this.value;
      }

      public Comparable<?> literalValue() {
         return (Comparable)this.value;
      }

      public String stringValue() {
         return this.value.toString();
      }
   }

   public abstract static class LiteralEntry extends ConstantPool.Entry {
      protected LiteralEntry(byte var1) {
         super(var1);
      }

      public abstract Comparable<?> literalValue();
   }

   public static class Utf8Entry extends ConstantPool.Entry {
      final String value;

      Utf8Entry(String var1) {
         super((byte)1);
         this.value = var1.intern();
         this.hashCode();
      }

      protected int computeValueHash() {
         return this.value.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 != null && var1.getClass() == ConstantPool.Utf8Entry.class && ((ConstantPool.Utf8Entry)var1).value.equals(this.value);
      }

      public int compareTo(Object var1) {
         int var2 = this.superCompareTo(var1);
         if (var2 == 0) {
            var2 = this.value.compareTo(((ConstantPool.Utf8Entry)var1).value);
         }

         return var2;
      }

      public String stringValue() {
         return this.value;
      }
   }

   public abstract static class Entry implements Comparable<Object> {
      protected final byte tag;
      protected int valueHash;

      protected Entry(byte var1) {
         this.tag = var1;
      }

      public final byte getTag() {
         return this.tag;
      }

      public final boolean tagEquals(int var1) {
         return this.getTag() == var1;
      }

      public ConstantPool.Entry getRef(int var1) {
         return null;
      }

      public boolean eq(ConstantPool.Entry var1) {
         assert var1 != null;

         return this == var1 || this.equals(var1);
      }

      public abstract boolean equals(Object var1);

      public final int hashCode() {
         if (this.valueHash == 0) {
            this.valueHash = this.computeValueHash();
            if (this.valueHash == 0) {
               this.valueHash = 1;
            }
         }

         return this.valueHash;
      }

      protected abstract int computeValueHash();

      public abstract int compareTo(Object var1);

      protected int superCompareTo(Object var1) {
         ConstantPool.Entry var2 = (ConstantPool.Entry)var1;
         return this.tag != var2.tag ? ConstantPool.TAG_ORDER[this.tag] - ConstantPool.TAG_ORDER[var2.tag] : 0;
      }

      public final boolean isDoubleWord() {
         return this.tag == 6 || this.tag == 5;
      }

      public final boolean tagMatches(int var1) {
         if (this.tag == var1) {
            return true;
         } else {
            byte[] var2;
            switch(var1) {
            case 13:
               return this.tag == 1;
            case 50:
               return true;
            case 51:
               var2 = ConstantPool.LOADABLE_VALUE_TAGS;
               break;
            case 52:
               var2 = ConstantPool.ANY_MEMBER_TAGS;
               break;
            case 53:
               var2 = ConstantPool.FIELD_SPECIFIC_TAGS;
               break;
            default:
               return false;
            }

            byte[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               byte var6 = var3[var5];
               if (var6 == this.tag) {
                  return true;
               }
            }

            return false;
         }
      }

      public String toString() {
         String var1 = this.stringValue();
         if (ConstantPool.verbose() > 4) {
            if (this.valueHash != 0) {
               var1 = var1 + " hash=" + this.valueHash;
            }

            var1 = var1 + " id=" + System.identityHashCode(this);
         }

         return ConstantPool.tagName(this.tag) + "=" + var1;
      }

      public abstract String stringValue();
   }
}
