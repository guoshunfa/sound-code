package sun.print;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class AttributeClass {
   private String myName;
   private int myType;
   private int nameLen;
   private Object myValue;
   public static final int TAG_UNSUPPORTED_VALUE = 16;
   public static final int TAG_INT = 33;
   public static final int TAG_BOOL = 34;
   public static final int TAG_ENUM = 35;
   public static final int TAG_OCTET = 48;
   public static final int TAG_DATE = 49;
   public static final int TAG_RESOLUTION = 50;
   public static final int TAG_RANGE_INTEGER = 51;
   public static final int TAG_TEXT_LANGUAGE = 53;
   public static final int TAG_NAME_LANGUAGE = 54;
   public static final int TAG_TEXT_WO_LANGUAGE = 65;
   public static final int TAG_NAME_WO_LANGUAGE = 66;
   public static final int TAG_KEYWORD = 68;
   public static final int TAG_URI = 69;
   public static final int TAG_CHARSET = 71;
   public static final int TAG_NATURALLANGUAGE = 72;
   public static final int TAG_MIME_MEDIATYPE = 73;
   public static final int TAG_MEMBER_ATTRNAME = 74;
   public static final AttributeClass ATTRIBUTES_CHARSET = new AttributeClass("attributes-charset", 71, "utf-8");
   public static final AttributeClass ATTRIBUTES_NATURAL_LANGUAGE = new AttributeClass("attributes-natural-language", 72, "en");

   protected AttributeClass(String var1, int var2, Object var3) {
      this.myName = var1;
      this.myType = var2;
      this.nameLen = var1.length();
      this.myValue = var3;
   }

   public byte getType() {
      return (byte)this.myType;
   }

   public char[] getLenChars() {
      char[] var1 = new char[]{'\u0000', (char)this.nameLen};
      return var1;
   }

   public Object getObjectValue() {
      return this.myValue;
   }

   public int getIntValue() {
      byte[] var1 = (byte[])((byte[])this.myValue);
      if (var1 == null) {
         return 0;
      } else {
         byte[] var2 = new byte[4];

         for(int var3 = 0; var3 < 4; ++var3) {
            var2[var3] = var1[var3 + 1];
         }

         return this.convertToInt(var2);
      }
   }

   public int[] getArrayOfIntValues() {
      byte[] var1 = (byte[])((byte[])this.myValue);
      if (var1 != null) {
         ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
         int var3 = var2.available();
         var2.mark(var3);
         var2.skip((long)(var3 - 1));
         int var4 = var2.read();
         var2.reset();
         int[] var5 = new int[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var2.read();
            if (var7 != 4) {
               return null;
            }

            byte[] var8 = new byte[var7];
            var2.read(var8, 0, var7);
            var5[var6] = this.convertToInt(var8);
         }

         return var5;
      } else {
         return null;
      }
   }

   public int[] getIntRangeValue() {
      int[] var1 = new int[]{0, 0};
      byte[] var2 = (byte[])((byte[])this.myValue);
      if (var2 != null) {
         byte var3 = 4;

         for(int var4 = 0; var4 < 2; ++var4) {
            byte[] var5 = new byte[var3];

            for(int var6 = 0; var6 < var3; ++var6) {
               var5[var6] = var2[var6 + 4 * var4 + 1];
            }

            var1[var4] = this.convertToInt(var5);
         }
      }

      return var1;
   }

   public String getStringValue() {
      String var1 = null;
      byte[] var2 = (byte[])((byte[])this.myValue);
      if (var2 != null) {
         ByteArrayInputStream var3 = new ByteArrayInputStream(var2);
         int var4 = var3.read();
         byte[] var5 = new byte[var4];
         var3.read(var5, 0, var4);

         try {
            var1 = new String(var5, "UTF-8");
         } catch (UnsupportedEncodingException var7) {
         }
      }

      return var1;
   }

   public String[] getArrayOfStringValues() {
      byte[] var1 = (byte[])((byte[])this.myValue);
      if (var1 == null) {
         return null;
      } else {
         ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
         int var3 = var2.available();
         var2.mark(var3);
         var2.skip((long)(var3 - 1));
         int var4 = var2.read();
         var2.reset();
         String[] var5 = new String[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var2.read();
            byte[] var8 = new byte[var7];
            var2.read(var8, 0, var7);

            try {
               var5[var6] = new String(var8, "UTF-8");
            } catch (UnsupportedEncodingException var10) {
            }
         }

         return var5;
      }
   }

   public byte getByteValue() {
      byte[] var1 = (byte[])((byte[])this.myValue);
      return var1 != null && var1.length >= 2 ? var1[1] : 0;
   }

   public String getName() {
      return this.myName;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof AttributeClass)) {
         return false;
      } else if (this == var1) {
         return true;
      } else {
         AttributeClass var2 = (AttributeClass)var1;
         return this.myType == var2.getType() && Objects.equals(this.myName, var2.getName()) && Objects.equals(this.myValue, var2.getObjectValue());
      }
   }

   public int hashCode() {
      return Objects.hash(this.myType, this.myName, this.myValue);
   }

   public String toString() {
      return this.myName;
   }

   private int unsignedByteToInt(byte var1) {
      return var1 & 255;
   }

   private int convertToInt(byte[] var1) {
      byte var2 = 0;
      byte var3 = 0;
      int var5 = var3 + 1;
      int var4 = var2 + (this.unsignedByteToInt(var1[var3]) << 24);
      var4 += this.unsignedByteToInt(var1[var5++]) << 16;
      var4 += this.unsignedByteToInt(var1[var5++]) << 8;
      var4 += this.unsignedByteToInt(var1[var5++]) << 0;
      return var4;
   }
}
