package com.sun.corba.se.impl.encoding;

public final class OSFCodeSetRegistry {
   public static final int ISO_8859_1_VALUE = 65537;
   public static final int UTF_16_VALUE = 65801;
   public static final int UTF_8_VALUE = 83951617;
   public static final int UCS_2_VALUE = 65792;
   public static final int ISO_646_VALUE = 65568;
   public static final OSFCodeSetRegistry.Entry ISO_8859_1 = new OSFCodeSetRegistry.Entry("ISO-8859-1", 65537, true, 1);
   static final OSFCodeSetRegistry.Entry UTF_16BE = new OSFCodeSetRegistry.Entry("UTF-16BE", -1, true, 2);
   static final OSFCodeSetRegistry.Entry UTF_16LE = new OSFCodeSetRegistry.Entry("UTF-16LE", -2, true, 2);
   public static final OSFCodeSetRegistry.Entry UTF_16 = new OSFCodeSetRegistry.Entry("UTF-16", 65801, true, 4);
   public static final OSFCodeSetRegistry.Entry UTF_8 = new OSFCodeSetRegistry.Entry("UTF-8", 83951617, false, 6);
   public static final OSFCodeSetRegistry.Entry UCS_2 = new OSFCodeSetRegistry.Entry("UCS-2", 65792, true, 2);
   public static final OSFCodeSetRegistry.Entry ISO_646 = new OSFCodeSetRegistry.Entry("US-ASCII", 65568, true, 1);

   private OSFCodeSetRegistry() {
   }

   public static OSFCodeSetRegistry.Entry lookupEntry(int var0) {
      switch(var0) {
      case 65537:
         return ISO_8859_1;
      case 65568:
         return ISO_646;
      case 65792:
         return UCS_2;
      case 65801:
         return UTF_16;
      case 83951617:
         return UTF_8;
      default:
         return null;
      }
   }

   public static final class Entry {
      private String javaName;
      private int encodingNum;
      private boolean isFixedWidth;
      private int maxBytesPerChar;

      private Entry(String var1, int var2, boolean var3, int var4) {
         this.javaName = var1;
         this.encodingNum = var2;
         this.isFixedWidth = var3;
         this.maxBytesPerChar = var4;
      }

      public String getName() {
         return this.javaName;
      }

      public int getNumber() {
         return this.encodingNum;
      }

      public boolean isFixedWidth() {
         return this.isFixedWidth;
      }

      public int getMaxBytesPerChar() {
         return this.maxBytesPerChar;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof OSFCodeSetRegistry.Entry)) {
            return false;
         } else {
            OSFCodeSetRegistry.Entry var2 = (OSFCodeSetRegistry.Entry)var1;
            return this.javaName.equals(var2.javaName) && this.encodingNum == var2.encodingNum && this.isFixedWidth == var2.isFixedWidth && this.maxBytesPerChar == var2.maxBytesPerChar;
         }
      }

      public int hashCode() {
         return this.encodingNum;
      }

      // $FF: synthetic method
      Entry(String var1, int var2, boolean var3, int var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
