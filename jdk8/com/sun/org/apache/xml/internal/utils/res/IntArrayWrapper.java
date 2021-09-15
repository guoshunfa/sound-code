package com.sun.org.apache.xml.internal.utils.res;

public class IntArrayWrapper {
   private int[] m_int;

   public IntArrayWrapper(int[] arg) {
      this.m_int = arg;
   }

   public int getInt(int index) {
      return this.m_int[index];
   }

   public int getLength() {
      return this.m_int.length;
   }
}
