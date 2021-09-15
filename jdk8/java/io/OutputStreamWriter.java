package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.StreamEncoder;

public class OutputStreamWriter extends Writer {
   private final StreamEncoder se;

   public OutputStreamWriter(OutputStream var1, String var2) throws UnsupportedEncodingException {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("charsetName");
      } else {
         this.se = StreamEncoder.forOutputStreamWriter(var1, this, (String)var2);
      }
   }

   public OutputStreamWriter(OutputStream var1) {
      super(var1);

      try {
         this.se = StreamEncoder.forOutputStreamWriter(var1, this, (String)((String)null));
      } catch (UnsupportedEncodingException var3) {
         throw new Error(var3);
      }
   }

   public OutputStreamWriter(OutputStream var1, Charset var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("charset");
      } else {
         this.se = StreamEncoder.forOutputStreamWriter(var1, this, (Charset)var2);
      }
   }

   public OutputStreamWriter(OutputStream var1, CharsetEncoder var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("charset encoder");
      } else {
         this.se = StreamEncoder.forOutputStreamWriter(var1, this, (CharsetEncoder)var2);
      }
   }

   public String getEncoding() {
      return this.se.getEncoding();
   }

   void flushBuffer() throws IOException {
      this.se.flushBuffer();
   }

   public void write(int var1) throws IOException {
      this.se.write(var1);
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      this.se.write(var1, var2, var3);
   }

   public void write(String var1, int var2, int var3) throws IOException {
      this.se.write(var1, var2, var3);
   }

   public void flush() throws IOException {
      this.se.flush();
   }

   public void close() throws IOException {
      this.se.close();
   }
}
