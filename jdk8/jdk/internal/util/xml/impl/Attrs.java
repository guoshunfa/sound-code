package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.Attributes;

public class Attrs implements Attributes {
   String[] mItems = new String[64];
   private char mLength;
   private char mAttrIdx = 0;

   public void setLength(char var1) {
      if (var1 > (char)(this.mItems.length >> 3)) {
         this.mItems = new String[var1 << 3];
      }

      this.mLength = var1;
   }

   public int getLength() {
      return this.mLength;
   }

   public String getURI(int var1) {
      return var1 >= 0 && var1 < this.mLength ? this.mItems[var1 << 3] : null;
   }

   public String getLocalName(int var1) {
      return var1 >= 0 && var1 < this.mLength ? this.mItems[(var1 << 3) + 2] : null;
   }

   public String getQName(int var1) {
      return var1 >= 0 && var1 < this.mLength ? this.mItems[(var1 << 3) + 1] : null;
   }

   public String getType(int var1) {
      return var1 >= 0 && var1 < this.mItems.length >> 3 ? this.mItems[(var1 << 3) + 4] : null;
   }

   public String getValue(int var1) {
      return var1 >= 0 && var1 < this.mLength ? this.mItems[(var1 << 3) + 3] : null;
   }

   public int getIndex(String var1, String var2) {
      char var3 = this.mLength;

      for(char var4 = 0; var4 < var3; ++var4) {
         if (this.mItems[var4 << 3].equals(var1) && this.mItems[(var4 << 3) + 2].equals(var2)) {
            return var4;
         }
      }

      return -1;
   }

   int getIndexNullNS(String var1, String var2) {
      char var3 = this.mLength;
      char var4;
      if (var1 != null) {
         for(var4 = 0; var4 < var3; ++var4) {
            if (this.mItems[var4 << 3].equals(var1) && this.mItems[(var4 << 3) + 2].equals(var2)) {
               return var4;
            }
         }
      } else {
         for(var4 = 0; var4 < var3; ++var4) {
            if (this.mItems[(var4 << 3) + 2].equals(var2)) {
               return var4;
            }
         }
      }

      return -1;
   }

   public int getIndex(String var1) {
      char var2 = this.mLength;

      for(char var3 = 0; var3 < var2; ++var3) {
         if (this.mItems[(var3 << 3) + 1].equals(var1)) {
            return var3;
         }
      }

      return -1;
   }

   public String getType(String var1, String var2) {
      int var3 = this.getIndex(var1, var2);
      return var3 >= 0 ? this.mItems[(var3 << 3) + 4] : null;
   }

   public String getType(String var1) {
      int var2 = this.getIndex(var1);
      return var2 >= 0 ? this.mItems[(var2 << 3) + 4] : null;
   }

   public String getValue(String var1, String var2) {
      int var3 = this.getIndex(var1, var2);
      return var3 >= 0 ? this.mItems[(var3 << 3) + 3] : null;
   }

   public String getValue(String var1) {
      int var2 = this.getIndex(var1);
      return var2 >= 0 ? this.mItems[(var2 << 3) + 3] : null;
   }

   public boolean isDeclared(int var1) {
      if (var1 >= 0 && var1 < this.mLength) {
         return this.mItems[(var1 << 3) + 5] != null;
      } else {
         throw new ArrayIndexOutOfBoundsException("");
      }
   }

   public boolean isDeclared(String var1) {
      int var2 = this.getIndex(var1);
      if (var2 < 0) {
         throw new IllegalArgumentException("");
      } else {
         return this.mItems[(var2 << 3) + 5] != null;
      }
   }

   public boolean isDeclared(String var1, String var2) {
      int var3 = this.getIndex(var1, var2);
      if (var3 < 0) {
         throw new IllegalArgumentException("");
      } else {
         return this.mItems[(var3 << 3) + 5] != null;
      }
   }

   public boolean isSpecified(int var1) {
      if (var1 >= 0 && var1 < this.mLength) {
         String var2 = this.mItems[(var1 << 3) + 5];
         return var2 != null ? var2.charAt(0) == 'd' : true;
      } else {
         throw new ArrayIndexOutOfBoundsException("");
      }
   }

   public boolean isSpecified(String var1, String var2) {
      int var3 = this.getIndex(var1, var2);
      if (var3 < 0) {
         throw new IllegalArgumentException("");
      } else {
         String var4 = this.mItems[(var3 << 3) + 5];
         return var4 != null ? var4.charAt(0) == 'd' : true;
      }
   }

   public boolean isSpecified(String var1) {
      int var2 = this.getIndex(var1);
      if (var2 < 0) {
         throw new IllegalArgumentException("");
      } else {
         String var3 = this.mItems[(var2 << 3) + 5];
         return var3 != null ? var3.charAt(0) == 'd' : true;
      }
   }
}
