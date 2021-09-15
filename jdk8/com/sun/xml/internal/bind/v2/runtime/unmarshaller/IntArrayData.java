package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import java.io.IOException;

public final class IntArrayData extends Pcdata {
   private int[] data;
   private int start;
   private int len;
   private StringBuilder literal;

   public IntArrayData(int[] data, int start, int len) {
      this.set(data, start, len);
   }

   public IntArrayData() {
   }

   public void set(int[] data, int start, int len) {
      this.data = data;
      this.start = start;
      this.len = len;
      this.literal = null;
   }

   public int length() {
      return this.getLiteral().length();
   }

   public char charAt(int index) {
      return this.getLiteral().charAt(index);
   }

   public CharSequence subSequence(int start, int end) {
      return this.getLiteral().subSequence(start, end);
   }

   private StringBuilder getLiteral() {
      if (this.literal != null) {
         return this.literal;
      } else {
         this.literal = new StringBuilder();
         int p = this.start;

         for(int i = this.len; i > 0; --i) {
            if (this.literal.length() > 0) {
               this.literal.append(' ');
            }

            this.literal.append(this.data[p++]);
         }

         return this.literal;
      }
   }

   public String toString() {
      return this.literal.toString();
   }

   public void writeTo(UTF8XmlOutput output) throws IOException {
      int p = this.start;

      for(int i = this.len; i > 0; --i) {
         if (i != this.len) {
            output.write(32);
         }

         output.text(this.data[p++]);
      }

   }
}
