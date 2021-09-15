package java.io;

/** @deprecated */
@Deprecated
public class LineNumberInputStream extends FilterInputStream {
   int pushBack = -1;
   int lineNumber;
   int markLineNumber;
   int markPushBack = -1;

   public LineNumberInputStream(InputStream var1) {
      super(var1);
   }

   public int read() throws IOException {
      int var1 = this.pushBack;
      if (var1 != -1) {
         this.pushBack = -1;
      } else {
         var1 = this.in.read();
      }

      switch(var1) {
      case 13:
         this.pushBack = this.in.read();
         if (this.pushBack == 10) {
            this.pushBack = -1;
         }
      case 10:
         ++this.lineNumber;
         return 10;
      default:
         return var1;
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.read();
            if (var4 == -1) {
               return -1;
            } else {
               var1[var2] = (byte)var4;
               int var5 = 1;

               try {
                  for(; var5 < var3; ++var5) {
                     var4 = this.read();
                     if (var4 == -1) {
                        break;
                     }

                     if (var1 != null) {
                        var1[var2 + var5] = (byte)var4;
                     }
                  }
               } catch (IOException var7) {
               }

               return var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long var1) throws IOException {
      short var3 = 2048;
      long var4 = var1;
      if (var1 <= 0L) {
         return 0L;
      } else {
         int var7;
         for(byte[] var6 = new byte[var3]; var4 > 0L; var4 -= (long)var7) {
            var7 = this.read(var6, 0, (int)Math.min((long)var3, var4));
            if (var7 < 0) {
               break;
            }
         }

         return var1 - var4;
      }
   }

   public void setLineNumber(int var1) {
      this.lineNumber = var1;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int available() throws IOException {
      return this.pushBack == -1 ? super.available() / 2 : super.available() / 2 + 1;
   }

   public void mark(int var1) {
      this.markLineNumber = this.lineNumber;
      this.markPushBack = this.pushBack;
      this.in.mark(var1);
   }

   public void reset() throws IOException {
      this.lineNumber = this.markLineNumber;
      this.pushBack = this.markPushBack;
      this.in.reset();
   }
}
