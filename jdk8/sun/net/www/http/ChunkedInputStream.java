package sun.net.www.http;

import java.io.IOException;
import java.io.InputStream;
import sun.net.www.MessageHeader;

public class ChunkedInputStream extends InputStream implements Hurryable {
   private InputStream in;
   private HttpClient hc;
   private MessageHeader responses;
   private int chunkSize;
   private int chunkRead;
   private byte[] chunkData = new byte[4096];
   private int chunkPos;
   private int chunkCount;
   private byte[] rawData = new byte[32];
   private int rawPos;
   private int rawCount;
   private boolean error;
   private boolean closed;
   private static final int MAX_CHUNK_HEADER_SIZE = 2050;
   static final int STATE_AWAITING_CHUNK_HEADER = 1;
   static final int STATE_READING_CHUNK = 2;
   static final int STATE_AWAITING_CHUNK_EOL = 3;
   static final int STATE_AWAITING_TRAILERS = 4;
   static final int STATE_DONE = 5;
   private int state;

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("stream is closed");
      }
   }

   private void ensureRawAvailable(int var1) {
      if (this.rawCount + var1 > this.rawData.length) {
         int var2 = this.rawCount - this.rawPos;
         if (var2 + var1 > this.rawData.length) {
            byte[] var3 = new byte[var2 + var1];
            if (var2 > 0) {
               System.arraycopy(this.rawData, this.rawPos, var3, 0, var2);
            }

            this.rawData = var3;
         } else if (var2 > 0) {
            System.arraycopy(this.rawData, this.rawPos, this.rawData, 0, var2);
         }

         this.rawCount = var2;
         this.rawPos = 0;
      }

   }

   private void closeUnderlying() throws IOException {
      if (this.in != null) {
         if (!this.error && this.state == 5) {
            this.hc.finished();
         } else if (!this.hurry()) {
            this.hc.closeServer();
         }

         this.in = null;
      }
   }

   private int fastRead(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.chunkSize - this.chunkRead;
      int var5 = var4 < var3 ? var4 : var3;
      if (var5 > 0) {
         int var6;
         try {
            var6 = this.in.read(var1, var2, var5);
         } catch (IOException var8) {
            this.error = true;
            throw var8;
         }

         if (var6 > 0) {
            this.chunkRead += var6;
            if (this.chunkRead >= this.chunkSize) {
               this.state = 3;
            }

            return var6;
         } else {
            this.error = true;
            throw new IOException("Premature EOF");
         }
      } else {
         return 0;
      }
   }

   private void processRaw() throws IOException {
      while(this.state != 5) {
         int var1;
         int var2;
         switch(this.state) {
         case 1:
            var1 = this.rawPos;

            while(var1 < this.rawCount && this.rawData[var1] != 10) {
               ++var1;
               if (var1 - this.rawPos >= 2050) {
                  this.error = true;
                  throw new IOException("Chunk header too long");
               }
            }

            if (var1 >= this.rawCount) {
               return;
            }

            String var3 = new String(this.rawData, this.rawPos, var1 - this.rawPos + 1, "US-ASCII");

            for(var2 = 0; var2 < var3.length() && Character.digit((char)var3.charAt(var2), 16) != -1; ++var2) {
            }

            try {
               this.chunkSize = Integer.parseInt(var3.substring(0, var2), 16);
            } catch (NumberFormatException var8) {
               this.error = true;
               throw new IOException("Bogus chunk size");
            }

            this.rawPos = var1 + 1;
            this.chunkRead = 0;
            if (this.chunkSize > 0) {
               this.state = 2;
            } else {
               this.state = 4;
            }
            break;
         case 2:
            if (this.rawPos >= this.rawCount) {
               return;
            }

            int var4 = Math.min(this.chunkSize - this.chunkRead, this.rawCount - this.rawPos);
            if (this.chunkData.length < this.chunkCount + var4) {
               int var9 = this.chunkCount - this.chunkPos;
               if (this.chunkData.length < var9 + var4) {
                  byte[] var10 = new byte[var9 + var4];
                  System.arraycopy(this.chunkData, this.chunkPos, var10, 0, var9);
                  this.chunkData = var10;
               } else {
                  System.arraycopy(this.chunkData, this.chunkPos, this.chunkData, 0, var9);
               }

               this.chunkPos = 0;
               this.chunkCount = var9;
            }

            System.arraycopy(this.rawData, this.rawPos, this.chunkData, this.chunkCount, var4);
            this.rawPos += var4;
            this.chunkCount += var4;
            this.chunkRead += var4;
            if (this.chunkSize - this.chunkRead <= 0) {
               this.state = 3;
               break;
            }

            return;
         case 3:
            if (this.rawPos + 1 >= this.rawCount) {
               return;
            }

            if (this.rawData[this.rawPos] != 13) {
               this.error = true;
               throw new IOException("missing CR");
            }

            if (this.rawData[this.rawPos + 1] != 10) {
               this.error = true;
               throw new IOException("missing LF");
            }

            this.rawPos += 2;
            this.state = 1;
            break;
         case 4:
            for(var1 = this.rawPos; var1 < this.rawCount && this.rawData[var1] != 10; ++var1) {
            }

            if (var1 >= this.rawCount) {
               return;
            }

            if (var1 == this.rawPos) {
               this.error = true;
               throw new IOException("LF should be proceeded by CR");
            }

            if (this.rawData[var1 - 1] != 13) {
               this.error = true;
               throw new IOException("LF should be proceeded by CR");
            }

            if (var1 == this.rawPos + 1) {
               this.state = 5;
               this.closeUnderlying();
               return;
            }

            String var5 = new String(this.rawData, this.rawPos, var1 - this.rawPos, "US-ASCII");
            var2 = var5.indexOf(58);
            if (var2 == -1) {
               throw new IOException("Malformed tailer - format should be key:value");
            }

            String var6 = var5.substring(0, var2).trim();
            String var7 = var5.substring(var2 + 1, var5.length()).trim();
            this.responses.add(var6, var7);
            this.rawPos = var1 + 1;
         }
      }

   }

   private int readAheadNonBlocking() throws IOException {
      int var1 = this.in.available();
      if (var1 > 0) {
         this.ensureRawAvailable(var1);

         int var2;
         try {
            var2 = this.in.read(this.rawData, this.rawCount, var1);
         } catch (IOException var4) {
            this.error = true;
            throw var4;
         }

         if (var2 < 0) {
            this.error = true;
            return -1;
         }

         this.rawCount += var2;
         this.processRaw();
      }

      return this.chunkCount - this.chunkPos;
   }

   private int readAheadBlocking() throws IOException {
      do {
         if (this.state == 5) {
            return -1;
         }

         this.ensureRawAvailable(32);

         int var1;
         try {
            var1 = this.in.read(this.rawData, this.rawCount, this.rawData.length - this.rawCount);
         } catch (IOException var3) {
            this.error = true;
            throw var3;
         }

         if (var1 < 0) {
            this.error = true;
            throw new IOException("Premature EOF");
         }

         this.rawCount += var1;
         this.processRaw();
      } while(this.chunkCount <= 0);

      return this.chunkCount - this.chunkPos;
   }

   private int readAhead(boolean var1) throws IOException {
      if (this.state == 5) {
         return -1;
      } else {
         if (this.chunkPos >= this.chunkCount) {
            this.chunkCount = 0;
            this.chunkPos = 0;
         }

         return var1 ? this.readAheadBlocking() : this.readAheadNonBlocking();
      }
   }

   public ChunkedInputStream(InputStream var1, HttpClient var2, MessageHeader var3) throws IOException {
      this.in = var1;
      this.responses = var3;
      this.hc = var2;
      this.state = 1;
   }

   public synchronized int read() throws IOException {
      this.ensureOpen();
      return this.chunkPos >= this.chunkCount && this.readAhead(true) <= 0 ? -1 : this.chunkData[this.chunkPos++] & 255;
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.chunkCount - this.chunkPos;
            if (var4 <= 0) {
               if (this.state == 2) {
                  return this.fastRead(var1, var2, var3);
               }

               var4 = this.readAhead(true);
               if (var4 < 0) {
                  return -1;
               }
            }

            int var5 = var4 < var3 ? var4 : var3;
            System.arraycopy(this.chunkData, this.chunkPos, var1, var2, var5);
            this.chunkPos += var5;
            return var5;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized int available() throws IOException {
      this.ensureOpen();
      int var1 = this.chunkCount - this.chunkPos;
      if (var1 > 0) {
         return var1;
      } else {
         var1 = this.readAhead(false);
         return var1 < 0 ? 0 : var1;
      }
   }

   public synchronized void close() throws IOException {
      if (!this.closed) {
         this.closeUnderlying();
         this.closed = true;
      }
   }

   public synchronized boolean hurry() {
      if (this.in != null && !this.error) {
         try {
            this.readAhead(false);
         } catch (Exception var2) {
            return false;
         }

         if (this.error) {
            return false;
         } else {
            return this.state == 5;
         }
      } else {
         return false;
      }
   }
}
