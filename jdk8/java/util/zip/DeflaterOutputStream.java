package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DeflaterOutputStream extends FilterOutputStream {
   protected Deflater def;
   protected byte[] buf;
   private boolean closed;
   private final boolean syncFlush;
   boolean usesDefaultDeflater;

   public DeflaterOutputStream(OutputStream var1, Deflater var2, int var3, boolean var4) {
      super(var1);
      this.closed = false;
      this.usesDefaultDeflater = false;
      if (var1 != null && var2 != null) {
         if (var3 <= 0) {
            throw new IllegalArgumentException("buffer size <= 0");
         } else {
            this.def = var2;
            this.buf = new byte[var3];
            this.syncFlush = var4;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public DeflaterOutputStream(OutputStream var1, Deflater var2, int var3) {
      this(var1, var2, var3, false);
   }

   public DeflaterOutputStream(OutputStream var1, Deflater var2, boolean var3) {
      this(var1, var2, 512, var3);
   }

   public DeflaterOutputStream(OutputStream var1, Deflater var2) {
      this(var1, var2, 512, false);
   }

   public DeflaterOutputStream(OutputStream var1, boolean var2) {
      this(var1, new Deflater(), 512, var2);
      this.usesDefaultDeflater = true;
   }

   public DeflaterOutputStream(OutputStream var1) {
      this(var1, false);
      this.usesDefaultDeflater = true;
   }

   public void write(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)(var1 & 255)};
      this.write(var2, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.def.finished()) {
         throw new IOException("write beyond end of stream");
      } else if ((var2 | var3 | var2 + var3 | var1.length - (var2 + var3)) < 0) {
         throw new IndexOutOfBoundsException();
      } else if (var3 != 0) {
         if (!this.def.finished()) {
            this.def.setInput(var1, var2, var3);

            while(!this.def.needsInput()) {
               this.deflate();
            }
         }

      }
   }

   public void finish() throws IOException {
      if (!this.def.finished()) {
         this.def.finish();

         while(!this.def.finished()) {
            this.deflate();
         }
      }

   }

   public void close() throws IOException {
      if (!this.closed) {
         this.finish();
         if (this.usesDefaultDeflater) {
            this.def.end();
         }

         this.out.close();
         this.closed = true;
      }

   }

   protected void deflate() throws IOException {
      int var1 = this.def.deflate(this.buf, 0, this.buf.length);
      if (var1 > 0) {
         this.out.write(this.buf, 0, var1);
      }

   }

   public void flush() throws IOException {
      if (this.syncFlush && !this.def.finished()) {
         boolean var1 = false;

         int var2;
         while((var2 = this.def.deflate(this.buf, 0, this.buf.length, 2)) > 0) {
            this.out.write(this.buf, 0, var2);
            if (var2 < this.buf.length) {
               break;
            }
         }
      }

      this.out.flush();
   }
}
