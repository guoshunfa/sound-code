package com.sun.xml.internal.fastinfoset.util;

public class CharArrayString extends CharArray {
   protected String _s;

   public CharArrayString(String s) {
      this(s, true);
   }

   public CharArrayString(String s, boolean createArray) {
      this._s = s;
      if (createArray) {
         this.ch = this._s.toCharArray();
         this.start = 0;
         this.length = this.ch.length;
      }

   }

   public String toString() {
      return this._s;
   }

   public int hashCode() {
      return this._s.hashCode();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof CharArrayString) {
         CharArrayString chas = (CharArrayString)obj;
         return this._s.equals(chas._s);
      } else {
         if (obj instanceof CharArray) {
            CharArray cha = (CharArray)obj;
            if (this.length == cha.length) {
               int n = this.length;
               int i = this.start;
               int var5 = cha.start;

               do {
                  if (n-- == 0) {
                     return true;
                  }
               } while(this.ch[i++] == cha.ch[var5++]);

               return false;
            }
         }

         return false;
      }
   }
}
