package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public final class Encoded {
   public byte[] buf;
   public int len;
   private static final byte[][] entities = new byte[128][];
   private static final byte[][] attributeEntities = new byte[128][];

   public Encoded() {
   }

   public Encoded(String text) {
      this.set(text);
   }

   public void ensureSize(int size) {
      if (this.buf == null || this.buf.length < size) {
         this.buf = new byte[size];
      }

   }

   public final void set(String text) {
      int length = text.length();
      this.ensureSize(length * 3 + 1);
      int ptr = 0;

      for(int i = 0; i < length; ++i) {
         char chr = text.charAt(i);
         if (chr > 127) {
            if (chr > 2047) {
               if ('\ud800' <= chr && chr <= '\udfff') {
                  int var10000 = (chr & 1023) << 10;
                  ++i;
                  int uc = (var10000 | text.charAt(i) & 1023) + 65536;
                  this.buf[ptr++] = (byte)(240 | uc >> 18);
                  this.buf[ptr++] = (byte)(128 | uc >> 12 & 63);
                  this.buf[ptr++] = (byte)(128 | uc >> 6 & 63);
                  this.buf[ptr++] = (byte)(128 + (uc & 63));
                  continue;
               }

               this.buf[ptr++] = (byte)(224 + (chr >> 12));
               this.buf[ptr++] = (byte)(128 + (chr >> 6 & 63));
            } else {
               this.buf[ptr++] = (byte)(192 + (chr >> 6));
            }

            this.buf[ptr++] = (byte)(128 + (chr & 63));
         } else {
            this.buf[ptr++] = (byte)chr;
         }
      }

      this.len = ptr;
   }

   public final void setEscape(String text, boolean isAttribute) {
      int length = text.length();
      this.ensureSize(length * 6 + 1);
      int ptr = 0;

      for(int i = 0; i < length; ++i) {
         char chr = text.charAt(i);
         int ptr1;
         if (chr > 127) {
            if (chr > 2047) {
               if ('\ud800' <= chr && chr <= '\udfff') {
                  int var10000 = (chr & 1023) << 10;
                  ++i;
                  int uc = (var10000 | text.charAt(i) & 1023) + 65536;
                  this.buf[ptr++] = (byte)(240 | uc >> 18);
                  this.buf[ptr++] = (byte)(128 | uc >> 12 & 63);
                  this.buf[ptr++] = (byte)(128 | uc >> 6 & 63);
                  this.buf[ptr++] = (byte)(128 + (uc & 63));
                  continue;
               }

               ptr1 = ptr + 1;
               this.buf[ptr] = (byte)(224 + (chr >> 12));
               this.buf[ptr1++] = (byte)(128 + (chr >> 6 & 63));
            } else {
               ptr1 = ptr + 1;
               this.buf[ptr] = (byte)(192 + (chr >> 6));
            }

            this.buf[ptr1++] = (byte)(128 + (chr & 63));
         } else {
            byte[] ent;
            if ((ent = attributeEntities[chr]) != null) {
               if (!isAttribute && entities[chr] == null) {
                  ptr1 = ptr + 1;
                  this.buf[ptr] = (byte)chr;
               } else {
                  ptr1 = this.writeEntity(ent, ptr);
               }
            } else {
               ptr1 = ptr + 1;
               this.buf[ptr] = (byte)chr;
            }
         }

         ptr = ptr1;
      }

      this.len = ptr;
   }

   private int writeEntity(byte[] entity, int ptr) {
      System.arraycopy(entity, 0, this.buf, ptr, entity.length);
      return ptr + entity.length;
   }

   public final void write(UTF8XmlOutput out) throws IOException {
      out.write(this.buf, 0, this.len);
   }

   public void append(char b) {
      this.buf[this.len++] = (byte)b;
   }

   public void compact() {
      byte[] b = new byte[this.len];
      System.arraycopy(this.buf, 0, b, 0, this.len);
      this.buf = b;
   }

   private static void add(char c, String s, boolean attOnly) {
      byte[] image = UTF8XmlOutput.toBytes(s);
      attributeEntities[c] = image;
      if (!attOnly) {
         entities[c] = image;
      }

   }

   static {
      add('&', "&amp;", false);
      add('<', "&lt;", false);
      add('>', "&gt;", false);
      add('"', "&quot;", true);
      add('\t', "&#x9;", true);
      add('\r', "&#xD;", false);
      add('\n', "&#xA;", true);
   }
}
