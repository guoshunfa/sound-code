package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class CodeSetComponentInfo {
   private CodeSetComponentInfo.CodeSetComponent forCharData;
   private CodeSetComponentInfo.CodeSetComponent forWCharData;
   public static final CodeSetComponentInfo JAVASOFT_DEFAULT_CODESETS;
   public static final CodeSetComponentInfo.CodeSetContext LOCAL_CODE_SETS;

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CodeSetComponentInfo)) {
         return false;
      } else {
         CodeSetComponentInfo var2 = (CodeSetComponentInfo)var1;
         return this.forCharData.equals(var2.forCharData) && this.forWCharData.equals(var2.forWCharData);
      }
   }

   public int hashCode() {
      return this.forCharData.hashCode() ^ this.forWCharData.hashCode();
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("CodeSetComponentInfo(");
      var1.append("char_data:");
      var1.append(this.forCharData.toString());
      var1.append(" wchar_data:");
      var1.append(this.forWCharData.toString());
      var1.append(")");
      return var1.toString();
   }

   public CodeSetComponentInfo() {
      this.forCharData = JAVASOFT_DEFAULT_CODESETS.forCharData;
      this.forWCharData = JAVASOFT_DEFAULT_CODESETS.forWCharData;
   }

   public CodeSetComponentInfo(CodeSetComponentInfo.CodeSetComponent var1, CodeSetComponentInfo.CodeSetComponent var2) {
      this.forCharData = var1;
      this.forWCharData = var2;
   }

   public void read(MarshalInputStream var1) {
      this.forCharData = new CodeSetComponentInfo.CodeSetComponent();
      this.forCharData.read(var1);
      this.forWCharData = new CodeSetComponentInfo.CodeSetComponent();
      this.forWCharData.read(var1);
   }

   public void write(MarshalOutputStream var1) {
      this.forCharData.write(var1);
      this.forWCharData.write(var1);
   }

   public CodeSetComponentInfo.CodeSetComponent getCharComponent() {
      return this.forCharData;
   }

   public CodeSetComponentInfo.CodeSetComponent getWCharComponent() {
      return this.forWCharData;
   }

   public static CodeSetComponentInfo.CodeSetComponent createFromString(String var0) {
      ORBUtilSystemException var1 = ORBUtilSystemException.get("rpc.encoding");
      if (var0 != null && var0.length() != 0) {
         StringTokenizer var2 = new StringTokenizer(var0, ", ", false);
         boolean var3 = false;
         Object var4 = null;

         int var9;
         int[] var10;
         try {
            var9 = Integer.decode(var2.nextToken());
            if (OSFCodeSetRegistry.lookupEntry(var9) == null) {
               throw var1.unknownNativeCodeset(new Integer(var9));
            }

            ArrayList var5 = new ArrayList(10);

            while(var2.hasMoreTokens()) {
               Integer var6 = Integer.decode(var2.nextToken());
               if (OSFCodeSetRegistry.lookupEntry(var6) == null) {
                  throw var1.unknownConversionCodeSet(var6);
               }

               var5.add(var6);
            }

            var10 = new int[var5.size()];

            for(int var11 = 0; var11 < var10.length; ++var11) {
               var10[var11] = (Integer)var5.get(var11);
            }
         } catch (NumberFormatException var7) {
            throw var1.invalidCodeSetNumber((Throwable)var7);
         } catch (NoSuchElementException var8) {
            throw var1.invalidCodeSetString((Throwable)var8, var0);
         }

         return new CodeSetComponentInfo.CodeSetComponent(var9, var10);
      } else {
         throw var1.badCodeSetString();
      }
   }

   static {
      CodeSetComponentInfo.CodeSetComponent var0 = new CodeSetComponentInfo.CodeSetComponent(OSFCodeSetRegistry.ISO_8859_1.getNumber(), new int[]{OSFCodeSetRegistry.UTF_8.getNumber(), OSFCodeSetRegistry.ISO_646.getNumber()});
      CodeSetComponentInfo.CodeSetComponent var1 = new CodeSetComponentInfo.CodeSetComponent(OSFCodeSetRegistry.UTF_16.getNumber(), new int[]{OSFCodeSetRegistry.UCS_2.getNumber()});
      JAVASOFT_DEFAULT_CODESETS = new CodeSetComponentInfo(var0, var1);
      LOCAL_CODE_SETS = new CodeSetComponentInfo.CodeSetContext(OSFCodeSetRegistry.ISO_8859_1.getNumber(), OSFCodeSetRegistry.UTF_16.getNumber());
   }

   public static final class CodeSetContext {
      private int char_data;
      private int wchar_data;

      public CodeSetContext() {
      }

      public CodeSetContext(int var1, int var2) {
         this.char_data = var1;
         this.wchar_data = var2;
      }

      public void read(MarshalInputStream var1) {
         this.char_data = var1.read_ulong();
         this.wchar_data = var1.read_ulong();
      }

      public void write(MarshalOutputStream var1) {
         var1.write_ulong(this.char_data);
         var1.write_ulong(this.wchar_data);
      }

      public int getCharCodeSet() {
         return this.char_data;
      }

      public int getWCharCodeSet() {
         return this.wchar_data;
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();
         var1.append("CodeSetContext char set: ");
         var1.append(Integer.toHexString(this.char_data));
         var1.append(" wchar set: ");
         var1.append(Integer.toHexString(this.wchar_data));
         return var1.toString();
      }
   }

   public static final class CodeSetComponent {
      int nativeCodeSet;
      int[] conversionCodeSets;

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof CodeSetComponentInfo.CodeSetComponent)) {
            return false;
         } else {
            CodeSetComponentInfo.CodeSetComponent var2 = (CodeSetComponentInfo.CodeSetComponent)var1;
            return this.nativeCodeSet == var2.nativeCodeSet && Arrays.equals(this.conversionCodeSets, var2.conversionCodeSets);
         }
      }

      public int hashCode() {
         int var1 = this.nativeCodeSet;

         for(int var2 = 0; var2 < this.conversionCodeSets.length; ++var2) {
            var1 = 37 * var1 + this.conversionCodeSets[var2];
         }

         return var1;
      }

      public CodeSetComponent() {
      }

      public CodeSetComponent(int var1, int[] var2) {
         this.nativeCodeSet = var1;
         if (var2 == null) {
            this.conversionCodeSets = new int[0];
         } else {
            this.conversionCodeSets = var2;
         }

      }

      public void read(MarshalInputStream var1) {
         this.nativeCodeSet = var1.read_ulong();
         int var2 = var1.read_long();
         this.conversionCodeSets = new int[var2];
         var1.read_ulong_array(this.conversionCodeSets, 0, var2);
      }

      public void write(MarshalOutputStream var1) {
         var1.write_ulong(this.nativeCodeSet);
         var1.write_long(this.conversionCodeSets.length);
         var1.write_ulong_array(this.conversionCodeSets, 0, this.conversionCodeSets.length);
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer("CodeSetComponent(");
         var1.append("native:");
         var1.append(Integer.toHexString(this.nativeCodeSet));
         var1.append(" conversion:");
         if (this.conversionCodeSets == null) {
            var1.append("null");
         } else {
            for(int var2 = 0; var2 < this.conversionCodeSets.length; ++var2) {
               var1.append(Integer.toHexString(this.conversionCodeSets[var2]));
               var1.append(' ');
            }
         }

         var1.append(")");
         return var1.toString();
      }
   }
}
