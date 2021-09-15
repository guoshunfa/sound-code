package java.io;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.StreamDecoder;

public class InputStreamReader extends Reader {
   private final StreamDecoder sd;

   public InputStreamReader(InputStream var1) {
      super(var1);

      try {
         this.sd = StreamDecoder.forInputStreamReader(var1, this, (String)((String)null));
      } catch (UnsupportedEncodingException var3) {
         throw new Error(var3);
      }
   }

   public InputStreamReader(InputStream var1, String var2) throws UnsupportedEncodingException {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("charsetName");
      } else {
         this.sd = StreamDecoder.forInputStreamReader(var1, this, (String)var2);
      }
   }

   public InputStreamReader(InputStream var1, Charset var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("charset");
      } else {
         this.sd = StreamDecoder.forInputStreamReader(var1, this, (Charset)var2);
      }
   }

   public InputStreamReader(InputStream var1, CharsetDecoder var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("charset decoder");
      } else {
         this.sd = StreamDecoder.forInputStreamReader(var1, this, (CharsetDecoder)var2);
      }
   }

   public String getEncoding() {
      return this.sd.getEncoding();
   }

   public int read() throws IOException {
      return this.sd.read();
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      return this.sd.read(var1, var2, var3);
   }

   public boolean ready() throws IOException {
      return this.sd.ready();
   }

   public void close() throws IOException {
      this.sd.close();
   }
}
