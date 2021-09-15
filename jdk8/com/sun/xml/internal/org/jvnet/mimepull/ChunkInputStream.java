package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;

final class ChunkInputStream extends InputStream {
   Chunk current;
   int offset;
   int len;
   final MIMEMessage msg;
   final MIMEPart part;
   byte[] buf;

   public ChunkInputStream(MIMEMessage msg, MIMEPart part, Chunk startPos) {
      this.current = startPos;
      this.len = this.current.data.size();
      this.buf = this.current.data.read();
      this.msg = msg;
      this.part = part;
   }

   public int read(byte[] b, int off, int sz) throws IOException {
      if (!this.fetch()) {
         return -1;
      } else {
         sz = Math.min(sz, this.len - this.offset);
         System.arraycopy(this.buf, this.offset, b, off, sz);
         return sz;
      }
   }

   public int read() throws IOException {
      return !this.fetch() ? -1 : this.buf[this.offset++] & 255;
   }

   private boolean fetch() {
      if (this.current == null) {
         throw new IllegalStateException("Stream already closed");
      } else {
         while(this.offset == this.len) {
            while(!this.part.parsed && this.current.next == null) {
               this.msg.makeProgress();
            }

            this.current = this.current.next;
            if (this.current == null) {
               return false;
            }

            this.offset = 0;
            this.buf = this.current.data.read();
            this.len = this.current.data.size();
         }

         return true;
      }
   }

   public void close() throws IOException {
      super.close();
      this.current = null;
   }
}
