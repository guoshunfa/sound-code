package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class InflaterOutputStream extends FilterOutputStream {
   protected final Inflater inf;
   protected final byte[] buf;
   private final byte[] wbuf;
   private boolean usesDefaultInflater;
   private boolean closed;

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public InflaterOutputStream(OutputStream var1) {
      this(var1, new Inflater());
      this.usesDefaultInflater = true;
   }

   public InflaterOutputStream(OutputStream var1, Inflater var2) {
      this(var1, var2, 512);
   }

   public InflaterOutputStream(OutputStream var1, Inflater var2, int var3) {
      super(var1);
      this.wbuf = new byte[1];
      this.usesDefaultInflater = false;
      this.closed = false;
      if (var1 == null) {
         throw new NullPointerException("Null output");
      } else if (var2 == null) {
         throw new NullPointerException("Null inflater");
      } else if (var3 <= 0) {
         throw new IllegalArgumentException("Buffer size < 1");
      } else {
         this.inf = var2;
         this.buf = new byte[var3];
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         try {
            this.finish();
         } finally {
            this.out.close();
            this.closed = true;
         }
      }

   }

   public void flush() throws IOException {
      this.ensureOpen();
      if (!this.inf.finished()) {
         try {
            while(true) {
               if (!this.inf.finished() && !this.inf.needsInput()) {
                  int var1 = this.inf.inflate(this.buf, 0, this.buf.length);
                  if (var1 >= 1) {
                     this.out.write(this.buf, 0, var1);
                     continue;
                  }
               }

               super.flush();
               break;
            }
         } catch (DataFormatException var3) {
            String var2 = var3.getMessage();
            if (var2 == null) {
               var2 = "Invalid ZLIB data format";
            }

            throw new ZipException(var2);
         }
      }

   }

   public void finish() throws IOException {
      this.ensureOpen();
      this.flush();
      if (this.usesDefaultInflater) {
         this.inf.end();
      }

   }

   public void write(int var1) throws IOException {
      this.wbuf[0] = (byte)var1;
      this.write(this.wbuf, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException("Null buffer for read");
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 != 0) {
            try {
               while(true) {
                  if (this.inf.needsInput()) {
                     if (var3 < 1) {
                        break;
                     }

                     int var7 = var3 < 512 ? var3 : 512;
                     this.inf.setInput(var1, var2, var7);
                     var2 += var7;
                     var3 -= var7;
                  }

                  int var4;
                  do {
                     var4 = this.inf.inflate(this.buf, 0, this.buf.length);
                     if (var4 > 0) {
                        this.out.write(this.buf, 0, var4);
                     }
                  } while(var4 > 0);

                  if (this.inf.finished()) {
                     break;
                  }

                  if (this.inf.needsDictionary()) {
                     throw new ZipException("ZLIB dictionary missing");
                  }
               }

            } catch (DataFormatException var6) {
               String var5 = var6.getMessage();
               if (var5 == null) {
                  var5 = "Invalid ZLIB data format";
               }

               throw new ZipException(var5);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
