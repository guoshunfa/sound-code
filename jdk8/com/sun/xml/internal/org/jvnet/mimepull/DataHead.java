package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

final class DataHead {
   volatile Chunk head;
   volatile Chunk tail;
   DataFile dataFile;
   private final MIMEPart part;
   boolean readOnce;
   volatile long inMemory;
   private Throwable consumedAt;

   DataHead(MIMEPart part) {
      this.part = part;
   }

   void addBody(ByteBuffer buf) {
      synchronized(this) {
         this.inMemory += (long)buf.limit();
      }

      if (this.tail != null) {
         this.tail = this.tail.createNext(this, buf);
      } else {
         this.head = this.tail = new Chunk(new MemoryData(buf, this.part.msg.config));
      }

   }

   void doneParsing() {
   }

   void moveTo(File f) {
      if (this.dataFile != null) {
         this.dataFile.renameTo(f);
      } else {
         try {
            FileOutputStream os = new FileOutputStream(f);

            try {
               InputStream in = this.readOnce();
               byte[] buf = new byte[8192];

               int len;
               while((len = in.read(buf)) != -1) {
                  os.write(buf, 0, len);
               }
            } finally {
               if (os != null) {
                  os.close();
               }

            }
         } catch (IOException var10) {
            throw new MIMEParsingException(var10);
         }
      }

   }

   void close() {
      this.head = this.tail = null;
      if (this.dataFile != null) {
         this.dataFile.close();
      }

   }

   public InputStream read() {
      if (this.readOnce) {
         throw new IllegalStateException("readOnce() is called before, read() cannot be called later.");
      } else {
         do {
            if (this.tail != null) {
               if (this.head == null) {
                  throw new IllegalStateException("Already read. Probably readOnce() is called before.");
               }

               return new DataHead.ReadMultiStream();
            }
         } while(this.part.msg.makeProgress());

         throw new IllegalStateException("No such MIME Part: " + this.part);
      }
   }

   private boolean unconsumed() {
      if (this.consumedAt != null) {
         AssertionError error = new AssertionError("readOnce() is already called before. See the nested exception from where it's called.");
         error.initCause(this.consumedAt);
         throw error;
      } else {
         this.consumedAt = (new Exception()).fillInStackTrace();
         return true;
      }
   }

   public InputStream readOnce() {
      assert this.unconsumed();

      if (this.readOnce) {
         throw new IllegalStateException("readOnce() is called before. It can only be called once.");
      } else {
         this.readOnce = true;

         do {
            if (this.tail != null) {
               InputStream in = new DataHead.ReadOnceStream();
               this.head = null;
               return in;
            }
         } while(this.part.msg.makeProgress() || this.tail != null);

         throw new IllegalStateException("No such Part: " + this.part);
      }
   }

   final class ReadOnceStream extends DataHead.ReadMultiStream {
      ReadOnceStream() {
         super();
      }

      void adjustInMemoryUsage() {
         synchronized(DataHead.this) {
            DataHead var10000 = DataHead.this;
            var10000.inMemory -= (long)this.current.data.size();
         }
      }
   }

   class ReadMultiStream extends InputStream {
      Chunk current;
      int offset;
      int len;
      byte[] buf;
      boolean closed;

      public ReadMultiStream() {
         this.current = DataHead.this.head;
         this.len = this.current.data.size();
         this.buf = this.current.data.read();
      }

      public int read(byte[] b, int off, int sz) throws IOException {
         if (!this.fetch()) {
            return -1;
         } else {
            sz = Math.min(sz, this.len - this.offset);
            System.arraycopy(this.buf, this.offset, b, off, sz);
            this.offset += sz;
            return sz;
         }
      }

      public int read() throws IOException {
         return !this.fetch() ? -1 : this.buf[this.offset++] & 255;
      }

      void adjustInMemoryUsage() {
      }

      private boolean fetch() throws IOException {
         if (this.closed) {
            throw new IOException("Stream already closed");
         } else if (this.current == null) {
            return false;
         } else {
            while(this.offset == this.len) {
               while(!DataHead.this.part.parsed && this.current.next == null) {
                  DataHead.this.part.msg.makeProgress();
               }

               this.current = this.current.next;
               if (this.current == null) {
                  return false;
               }

               this.adjustInMemoryUsage();
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
         this.closed = true;
      }
   }
}
