package sun.net.httpserver;

import java.io.IOException;
import java.io.OutputStream;

class PlaceholderOutputStream extends OutputStream {
   OutputStream wrapped;

   PlaceholderOutputStream(OutputStream var1) {
      this.wrapped = var1;
   }

   void setWrappedStream(OutputStream var1) {
      this.wrapped = var1;
   }

   boolean isWrapped() {
      return this.wrapped != null;
   }

   private void checkWrap() throws IOException {
      if (this.wrapped == null) {
         throw new IOException("response headers not sent yet");
      }
   }

   public void write(int var1) throws IOException {
      this.checkWrap();
      this.wrapped.write(var1);
   }

   public void write(byte[] var1) throws IOException {
      this.checkWrap();
      this.wrapped.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.checkWrap();
      this.wrapped.write(var1, var2, var3);
   }

   public void flush() throws IOException {
      this.checkWrap();
      this.wrapped.flush();
   }

   public void close() throws IOException {
      this.checkWrap();
      this.wrapped.close();
   }
}
