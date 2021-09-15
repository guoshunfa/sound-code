package jdk.internal.util.xml.impl;

import java.io.Reader;

public class Input {
   public String pubid;
   public String sysid;
   public String xmlenc;
   public char xmlver;
   public Reader src;
   public char[] chars;
   public int chLen;
   public int chIdx;
   public Input next;

   public Input(int var1) {
      this.chars = new char[var1];
      this.chLen = this.chars.length;
   }

   public Input(char[] var1) {
      this.chars = var1;
      this.chLen = this.chars.length;
   }

   public Input() {
   }
}
