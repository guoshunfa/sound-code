package com.sun.org.apache.xerces.internal.xni;

public class XMLString {
   public char[] ch;
   public int offset;
   public int length;

   public XMLString() {
   }

   public XMLString(char[] ch, int offset, int length) {
      this.setValues(ch, offset, length);
   }

   public XMLString(XMLString string) {
      this.setValues(string);
   }

   public void setValues(char[] ch, int offset, int length) {
      this.ch = ch;
      this.offset = offset;
      this.length = length;
   }

   public void setValues(XMLString s) {
      this.setValues(s.ch, s.offset, s.length);
   }

   public void clear() {
      this.ch = null;
      this.offset = 0;
      this.length = -1;
   }

   public boolean equals(char[] ch, int offset, int length) {
      if (ch == null) {
         return false;
      } else if (this.length != length) {
         return false;
      } else {
         for(int i = 0; i < length; ++i) {
            if (this.ch[this.offset + i] != ch[offset + i]) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(String s) {
      if (s == null) {
         return false;
      } else if (this.length != s.length()) {
         return false;
      } else {
         for(int i = 0; i < this.length; ++i) {
            if (this.ch[this.offset + i] != s.charAt(i)) {
               return false;
            }
         }

         return true;
      }
   }

   public String toString() {
      return this.length > 0 ? new String(this.ch, this.offset, this.length) : "";
   }
}
