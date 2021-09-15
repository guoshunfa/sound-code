package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadAllStream extends InputStream {
   @NotNull
   private final ReadAllStream.MemoryStream memStream = new ReadAllStream.MemoryStream();
   @NotNull
   private final ReadAllStream.FileStream fileStream = new ReadAllStream.FileStream();
   private boolean readAll;
   private boolean closed;
   private static final Logger LOGGER = Logger.getLogger(ReadAllStream.class.getName());

   public void readAll(InputStream in, long inMemory) throws IOException {
      assert !this.readAll;

      this.readAll = true;
      boolean eof = this.memStream.readAll(in, inMemory);
      if (!eof) {
         this.fileStream.readAll(in);
      }

   }

   public int read() throws IOException {
      int ch = this.memStream.read();
      if (ch == -1) {
         ch = this.fileStream.read();
      }

      return ch;
   }

   public int read(byte[] b, int off, int sz) throws IOException {
      int len = this.memStream.read(b, off, sz);
      if (len == -1) {
         len = this.fileStream.read(b, off, sz);
      }

      return len;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.memStream.close();
         this.fileStream.close();
         this.closed = true;
      }

   }

   private static class MemoryStream extends InputStream {
      private ReadAllStream.MemoryStream.Chunk head;
      private ReadAllStream.MemoryStream.Chunk tail;
      private int curOff;

      private MemoryStream() {
      }

      private void add(byte[] buf, int len) {
         if (this.tail != null) {
            this.tail = this.tail.createNext(buf, 0, len);
         } else {
            this.head = this.tail = new ReadAllStream.MemoryStream.Chunk(buf, 0, len);
         }

      }

      boolean readAll(InputStream in, long inMemory) throws IOException {
         long total = 0L;

         do {
            byte[] buf = new byte[8192];
            int read = this.fill(in, buf);
            total += (long)read;
            if (read != 0) {
               this.add(buf, read);
            }

            if (read != buf.length) {
               return true;
            }
         } while(total <= inMemory);

         return false;
      }

      private int fill(InputStream in, byte[] buf) throws IOException {
         int read;
         int total;
         for(total = 0; total < buf.length && (read = in.read(buf, total, buf.length - total)) != -1; total += read) {
         }

         return total;
      }

      public int read() throws IOException {
         return !this.fetch() ? -1 : this.head.buf[this.curOff++] & 255;
      }

      public int read(byte[] b, int off, int sz) throws IOException {
         if (!this.fetch()) {
            return -1;
         } else {
            sz = Math.min(sz, this.head.len - (this.curOff - this.head.off));
            System.arraycopy(this.head.buf, this.curOff, b, off, sz);
            this.curOff += sz;
            return sz;
         }
      }

      private boolean fetch() {
         if (this.head == null) {
            return false;
         } else {
            if (this.curOff == this.head.off + this.head.len) {
               this.head = this.head.next;
               if (this.head == null) {
                  return false;
               }

               this.curOff = this.head.off;
            }

            return true;
         }
      }

      // $FF: synthetic method
      MemoryStream(Object x0) {
         this();
      }

      private static final class Chunk {
         ReadAllStream.MemoryStream.Chunk next;
         final byte[] buf;
         final int off;
         final int len;

         public Chunk(byte[] buf, int off, int len) {
            this.buf = buf;
            this.off = off;
            this.len = len;
         }

         public ReadAllStream.MemoryStream.Chunk createNext(byte[] buf, int off, int len) {
            return this.next = new ReadAllStream.MemoryStream.Chunk(buf, off, len);
         }
      }
   }

   private static class FileStream extends InputStream {
      @Nullable
      private File tempFile;
      @Nullable
      private FileInputStream fin;

      private FileStream() {
      }

      void readAll(InputStream in) throws IOException {
         this.tempFile = File.createTempFile("jaxws", ".bin");
         FileOutputStream fileOut = new FileOutputStream(this.tempFile);

         try {
            byte[] buf = new byte[8192];

            int len;
            while((len = in.read(buf)) != -1) {
               fileOut.write(buf, 0, len);
            }
         } finally {
            fileOut.close();
         }

         this.fin = new FileInputStream(this.tempFile);
      }

      public int read() throws IOException {
         return this.fin != null ? this.fin.read() : -1;
      }

      public int read(byte[] b, int off, int sz) throws IOException {
         return this.fin != null ? this.fin.read(b, off, sz) : -1;
      }

      public void close() throws IOException {
         if (this.fin != null) {
            this.fin.close();
         }

         if (this.tempFile != null) {
            boolean success = this.tempFile.delete();
            if (!success) {
               ReadAllStream.LOGGER.log(Level.INFO, (String)"File {0} could not be deleted", (Object)this.tempFile);
            }
         }

      }

      // $FF: synthetic method
      FileStream(Object x0) {
         this();
      }
   }
}
