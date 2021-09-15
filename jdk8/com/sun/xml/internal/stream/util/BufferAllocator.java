package com.sun.xml.internal.stream.util;

public class BufferAllocator {
   public static int SMALL_SIZE_LIMIT = 128;
   public static int MEDIUM_SIZE_LIMIT = 2048;
   public static int LARGE_SIZE_LIMIT = 8192;
   char[] smallCharBuffer;
   char[] mediumCharBuffer;
   char[] largeCharBuffer;
   byte[] smallByteBuffer;
   byte[] mediumByteBuffer;
   byte[] largeByteBuffer;

   public char[] getCharBuffer(int size) {
      char[] buffer;
      if (size <= SMALL_SIZE_LIMIT) {
         buffer = this.smallCharBuffer;
         this.smallCharBuffer = null;
         return buffer;
      } else if (size <= MEDIUM_SIZE_LIMIT) {
         buffer = this.mediumCharBuffer;
         this.mediumCharBuffer = null;
         return buffer;
      } else if (size <= LARGE_SIZE_LIMIT) {
         buffer = this.largeCharBuffer;
         this.largeCharBuffer = null;
         return buffer;
      } else {
         return null;
      }
   }

   public void returnCharBuffer(char[] c) {
      if (c != null) {
         if (c.length <= SMALL_SIZE_LIMIT) {
            this.smallCharBuffer = c;
         } else if (c.length <= MEDIUM_SIZE_LIMIT) {
            this.mediumCharBuffer = c;
         } else if (c.length <= LARGE_SIZE_LIMIT) {
            this.largeCharBuffer = c;
         }

      }
   }

   public byte[] getByteBuffer(int size) {
      byte[] buffer;
      if (size <= SMALL_SIZE_LIMIT) {
         buffer = this.smallByteBuffer;
         this.smallByteBuffer = null;
         return buffer;
      } else if (size <= MEDIUM_SIZE_LIMIT) {
         buffer = this.mediumByteBuffer;
         this.mediumByteBuffer = null;
         return buffer;
      } else if (size <= LARGE_SIZE_LIMIT) {
         buffer = this.largeByteBuffer;
         this.largeByteBuffer = null;
         return buffer;
      } else {
         return null;
      }
   }

   public void returnByteBuffer(byte[] b) {
      if (b != null) {
         if (b.length <= SMALL_SIZE_LIMIT) {
            this.smallByteBuffer = b;
         } else if (b.length <= MEDIUM_SIZE_LIMIT) {
            this.mediumByteBuffer = b;
         } else if (b.length <= LARGE_SIZE_LIMIT) {
            this.largeByteBuffer = b;
         }

      }
   }
}
