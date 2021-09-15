package java.io;

public class LineNumberReader extends BufferedReader {
   private int lineNumber = 0;
   private int markedLineNumber;
   private boolean skipLF;
   private boolean markedSkipLF;
   private static final int maxSkipBufferSize = 8192;
   private char[] skipBuffer = null;

   public LineNumberReader(Reader var1) {
      super(var1);
   }

   public LineNumberReader(Reader var1, int var2) {
      super(var1, var2);
   }

   public void setLineNumber(int var1) {
      this.lineNumber = var1;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int read() throws IOException {
      synchronized(this.lock) {
         int var2 = super.read();
         if (this.skipLF) {
            if (var2 == 10) {
               var2 = super.read();
            }

            this.skipLF = false;
         }

         switch(var2) {
         case 13:
            this.skipLF = true;
         case 10:
            ++this.lineNumber;
            return 10;
         default:
            return var2;
         }
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         int var5 = super.read(var1, var2, var3);

         for(int var6 = var2; var6 < var2 + var5; ++var6) {
            char var7 = var1[var6];
            if (this.skipLF) {
               this.skipLF = false;
               if (var7 == '\n') {
                  continue;
               }
            }

            switch(var7) {
            case '\r':
               this.skipLF = true;
            case '\n':
               ++this.lineNumber;
            }
         }

         return var5;
      }
   }

   public String readLine() throws IOException {
      synchronized(this.lock) {
         String var2 = super.readLine(this.skipLF);
         this.skipLF = false;
         if (var2 != null) {
            ++this.lineNumber;
         }

         return var2;
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("skip() value is negative");
      } else {
         int var3 = (int)Math.min(var1, 8192L);
         synchronized(this.lock) {
            if (this.skipBuffer == null || this.skipBuffer.length < var3) {
               this.skipBuffer = new char[var3];
            }

            long var5;
            int var7;
            for(var5 = var1; var5 > 0L; var5 -= (long)var7) {
               var7 = this.read(this.skipBuffer, 0, (int)Math.min(var5, (long)var3));
               if (var7 == -1) {
                  break;
               }
            }

            return var1 - var5;
         }
      }
   }

   public void mark(int var1) throws IOException {
      synchronized(this.lock) {
         super.mark(var1);
         this.markedLineNumber = this.lineNumber;
         this.markedSkipLF = this.skipLF;
      }
   }

   public void reset() throws IOException {
      synchronized(this.lock) {
         super.reset();
         this.lineNumber = this.markedLineNumber;
         this.skipLF = this.markedSkipLF;
      }
   }
}
