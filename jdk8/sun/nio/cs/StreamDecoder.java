package sun.nio.cs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;

public class StreamDecoder extends Reader {
   private static final int MIN_BYTE_BUFFER_SIZE = 32;
   private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
   private volatile boolean isOpen;
   private boolean haveLeftoverChar;
   private char leftoverChar;
   private static volatile boolean channelsAvailable = true;
   private Charset cs;
   private CharsetDecoder decoder;
   private ByteBuffer bb;
   private InputStream in;
   private ReadableByteChannel ch;

   private void ensureOpen() throws IOException {
      if (!this.isOpen) {
         throw new IOException("Stream closed");
      }
   }

   public static StreamDecoder forInputStreamReader(InputStream var0, Object var1, String var2) throws UnsupportedEncodingException {
      String var3 = var2;
      if (var2 == null) {
         var3 = Charset.defaultCharset().name();
      }

      try {
         if (Charset.isSupported(var3)) {
            return new StreamDecoder(var0, var1, Charset.forName(var3));
         }
      } catch (IllegalCharsetNameException var5) {
      }

      throw new UnsupportedEncodingException(var3);
   }

   public static StreamDecoder forInputStreamReader(InputStream var0, Object var1, Charset var2) {
      return new StreamDecoder(var0, var1, var2);
   }

   public static StreamDecoder forInputStreamReader(InputStream var0, Object var1, CharsetDecoder var2) {
      return new StreamDecoder(var0, var1, var2);
   }

   public static StreamDecoder forDecoder(ReadableByteChannel var0, CharsetDecoder var1, int var2) {
      return new StreamDecoder(var0, var1, var2);
   }

   public String getEncoding() {
      return this.isOpen() ? this.encodingName() : null;
   }

   public int read() throws IOException {
      return this.read0();
   }

   private int read0() throws IOException {
      synchronized(this.lock) {
         if (this.haveLeftoverChar) {
            this.haveLeftoverChar = false;
            return this.leftoverChar;
         } else {
            char[] var2 = new char[2];
            int var3 = this.read(var2, 0, 2);
            switch(var3) {
            case -1:
               return -1;
            case 0:
            default:
               assert false : var3;

               return -1;
            case 2:
               this.leftoverChar = var2[1];
               this.haveLeftoverChar = true;
            case 1:
               return var2[0];
            }
         }
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      int var4 = var2;
      int var5 = var3;
      synchronized(this.lock) {
         this.ensureOpen();
         if (var4 >= 0 && var4 <= var1.length && var5 >= 0 && var4 + var5 <= var1.length && var4 + var5 >= 0) {
            if (var5 == 0) {
               return 0;
            } else {
               byte var7 = 0;
               if (this.haveLeftoverChar) {
                  var1[var4] = this.leftoverChar;
                  ++var4;
                  --var5;
                  this.haveLeftoverChar = false;
                  var7 = 1;
                  if (var5 == 0 || !this.implReady()) {
                     return var7;
                  }
               }

               if (var5 == 1) {
                  int var8 = this.read0();
                  if (var8 == -1) {
                     return var7 == 0 ? -1 : var7;
                  } else {
                     var1[var4] = (char)var8;
                     return var7 + 1;
                  }
               } else {
                  return var7 + this.implRead(var1, var4, var4 + var5);
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public boolean ready() throws IOException {
      synchronized(this.lock) {
         this.ensureOpen();
         return this.haveLeftoverChar || this.implReady();
      }
   }

   public void close() throws IOException {
      synchronized(this.lock) {
         if (this.isOpen) {
            this.implClose();
            this.isOpen = false;
         }
      }
   }

   private boolean isOpen() {
      return this.isOpen;
   }

   private static FileChannel getChannel(FileInputStream var0) {
      if (!channelsAvailable) {
         return null;
      } else {
         try {
            return var0.getChannel();
         } catch (UnsatisfiedLinkError var2) {
            channelsAvailable = false;
            return null;
         }
      }
   }

   StreamDecoder(InputStream var1, Object var2, Charset var3) {
      this(var1, var2, var3.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE));
   }

   StreamDecoder(InputStream var1, Object var2, CharsetDecoder var3) {
      super(var2);
      this.isOpen = true;
      this.haveLeftoverChar = false;
      this.cs = var3.charset();
      this.decoder = var3;
      if (this.ch == null) {
         this.in = var1;
         this.ch = null;
         this.bb = ByteBuffer.allocate(8192);
      }

      this.bb.flip();
   }

   StreamDecoder(ReadableByteChannel var1, CharsetDecoder var2, int var3) {
      this.isOpen = true;
      this.haveLeftoverChar = false;
      this.in = null;
      this.ch = var1;
      this.decoder = var2;
      this.cs = var2.charset();
      this.bb = ByteBuffer.allocate(var3 < 0 ? 8192 : (var3 < 32 ? 32 : var3));
      this.bb.flip();
   }

   private int readBytes() throws IOException {
      this.bb.compact();

      int var1;
      try {
         int var2;
         if (this.ch != null) {
            var1 = this.ch.read(this.bb);
            if (var1 < 0) {
               var2 = var1;
               return var2;
            }
         } else {
            var1 = this.bb.limit();
            var2 = this.bb.position();

            assert var2 <= var1;

            int var3 = var2 <= var1 ? var1 - var2 : 0;

            assert var3 > 0;

            int var4 = this.in.read(this.bb.array(), this.bb.arrayOffset() + var2, var3);
            if (var4 < 0) {
               int var5 = var4;
               return var5;
            }

            if (var4 == 0) {
               throw new IOException("Underlying input stream returned zero bytes");
            }

            assert var4 <= var3 : "n = " + var4 + ", rem = " + var3;

            this.bb.position(var2 + var4);
         }
      } finally {
         this.bb.flip();
      }

      var1 = this.bb.remaining();

      assert var1 != 0 : var1;

      return var1;
   }

   int implRead(char[] var1, int var2, int var3) throws IOException {
      assert var3 - var2 > 1;

      CharBuffer var4 = CharBuffer.wrap(var1, var2, var3 - var2);
      if (var4.position() != 0) {
         var4 = var4.slice();
      }

      boolean var5 = false;

      while(true) {
         CoderResult var6 = this.decoder.decode(this.bb, var4, var5);
         if (var6.isUnderflow()) {
            if (var5 || !var4.hasRemaining() || var4.position() > 0 && !this.inReady()) {
               break;
            }

            int var7 = this.readBytes();
            if (var7 < 0) {
               var5 = true;
               if (var4.position() == 0 && !this.bb.hasRemaining()) {
                  break;
               }

               this.decoder.reset();
            }
         } else {
            if (var6.isOverflow()) {
               assert var4.position() > 0;
               break;
            }

            var6.throwException();
         }
      }

      if (var5) {
         this.decoder.reset();
      }

      if (var4.position() == 0) {
         if (var5) {
            return -1;
         }

         assert false;
      }

      return var4.position();
   }

   String encodingName() {
      return this.cs instanceof HistoricallyNamedCharset ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
   }

   private boolean inReady() {
      try {
         return this.in != null && this.in.available() > 0 || this.ch instanceof FileChannel;
      } catch (IOException var2) {
         return false;
      }
   }

   boolean implReady() {
      return this.bb.hasRemaining() || this.inReady();
   }

   void implClose() throws IOException {
      if (this.ch != null) {
         this.ch.close();
      } else {
         this.in.close();
      }

   }
}
