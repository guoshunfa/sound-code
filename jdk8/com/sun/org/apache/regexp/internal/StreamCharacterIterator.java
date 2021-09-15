package com.sun.org.apache.regexp.internal;

import java.io.IOException;
import java.io.InputStream;

public final class StreamCharacterIterator implements CharacterIterator {
   private final InputStream is;
   private final StringBuffer buff;
   private boolean closed;

   public StreamCharacterIterator(InputStream is) {
      this.is = is;
      this.buff = new StringBuffer(512);
      this.closed = false;
   }

   public String substring(int beginIndex, int endIndex) {
      try {
         this.ensure(endIndex);
         return this.buff.toString().substring(beginIndex, endIndex);
      } catch (IOException var4) {
         throw new StringIndexOutOfBoundsException(var4.getMessage());
      }
   }

   public String substring(int beginIndex) {
      try {
         this.readAll();
         return this.buff.toString().substring(beginIndex);
      } catch (IOException var3) {
         throw new StringIndexOutOfBoundsException(var3.getMessage());
      }
   }

   public char charAt(int pos) {
      try {
         this.ensure(pos);
         return this.buff.charAt(pos);
      } catch (IOException var3) {
         throw new StringIndexOutOfBoundsException(var3.getMessage());
      }
   }

   public boolean isEnd(int pos) {
      if (this.buff.length() > pos) {
         return false;
      } else {
         try {
            this.ensure(pos);
            return this.buff.length() <= pos;
         } catch (IOException var3) {
            throw new StringIndexOutOfBoundsException(var3.getMessage());
         }
      }
   }

   private int read(int n) throws IOException {
      if (this.closed) {
         return 0;
      } else {
         int i = n;

         while(true) {
            --i;
            if (i < 0) {
               break;
            }

            int c = this.is.read();
            if (c < 0) {
               this.closed = true;
               break;
            }

            this.buff.append((char)c);
         }

         return n - i;
      }
   }

   private void readAll() throws IOException {
      while(!this.closed) {
         this.read(1000);
      }

   }

   private void ensure(int idx) throws IOException {
      if (!this.closed) {
         if (idx >= this.buff.length()) {
            this.read(idx + 1 - this.buff.length());
         }
      }
   }
}
