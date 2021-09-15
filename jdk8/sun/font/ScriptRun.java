package sun.font;

public final class ScriptRun {
   private char[] text;
   private int textStart;
   private int textLimit;
   private int scriptStart;
   private int scriptLimit;
   private int scriptCode;
   private int[] stack;
   private int parenSP;
   static final int SURROGATE_START = 65536;
   static final int LEAD_START = 55296;
   static final int LEAD_LIMIT = 56320;
   static final int TAIL_START = 56320;
   static final int TAIL_LIMIT = 57344;
   static final int LEAD_SURROGATE_SHIFT = 10;
   static final int SURROGATE_OFFSET = -56613888;
   static final int DONE = -1;
   private static int[] pairedChars = new int[]{40, 41, 60, 62, 91, 93, 123, 125, 171, 187, 8216, 8217, 8220, 8221, 8249, 8250, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315};
   private static final int pairedCharPower;
   private static final int pairedCharExtra;

   public ScriptRun() {
   }

   public ScriptRun(char[] var1, int var2, int var3) {
      this.init(var1, var2, var3);
   }

   public void init(char[] var1, int var2, int var3) {
      if (var1 != null && var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         this.text = var1;
         this.textStart = var2;
         this.textLimit = var2 + var3;
         this.scriptStart = this.textStart;
         this.scriptLimit = this.textStart;
         this.scriptCode = -1;
         this.parenSP = 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getScriptStart() {
      return this.scriptStart;
   }

   public final int getScriptLimit() {
      return this.scriptLimit;
   }

   public final int getScriptCode() {
      return this.scriptCode;
   }

   public final boolean next() {
      int var1 = this.parenSP;
      if (this.scriptLimit >= this.textLimit) {
         return false;
      } else {
         this.scriptCode = 0;
         this.scriptStart = this.scriptLimit;

         int var2;
         while((var2 = this.nextCodePoint()) != -1) {
            int var3 = ScriptRunData.getScript(var2);
            int var4 = var3 == 0 ? getPairIndex(var2) : -1;
            if (var4 >= 0) {
               if ((var4 & 1) == 0) {
                  if (this.stack == null) {
                     this.stack = new int[32];
                  } else if (this.parenSP == this.stack.length) {
                     int[] var5 = new int[this.stack.length + 32];
                     System.arraycopy(this.stack, 0, var5, 0, this.stack.length);
                     this.stack = var5;
                  }

                  this.stack[this.parenSP++] = var4;
                  this.stack[this.parenSP++] = this.scriptCode;
               } else if (this.parenSP > 0) {
                  int var6 = var4 & -2;

                  while((this.parenSP -= 2) >= 0 && this.stack[this.parenSP] != var6) {
                  }

                  if (this.parenSP >= 0) {
                     var3 = this.stack[this.parenSP + 1];
                  } else {
                     this.parenSP = 0;
                  }

                  if (this.parenSP < var1) {
                     var1 = this.parenSP;
                  }
               }
            }

            if (!sameScript(this.scriptCode, var3)) {
               this.pushback(var2);
               break;
            }

            if (this.scriptCode <= 1 && var3 > 1) {
               for(this.scriptCode = var3; var1 < this.parenSP; var1 += 2) {
                  this.stack[var1 + 1] = this.scriptCode;
               }
            }

            if (var4 > 0 && (var4 & 1) != 0 && this.parenSP > 0) {
               this.parenSP -= 2;
            }
         }

         return true;
      }
   }

   private final int nextCodePoint() {
      if (this.scriptLimit >= this.textLimit) {
         return -1;
      } else {
         int var1 = this.text[this.scriptLimit++];
         if (var1 >= 55296 && var1 < 56320 && this.scriptLimit < this.textLimit) {
            char var2 = this.text[this.scriptLimit];
            if (var2 >= '\udc00' && var2 < '\ue000') {
               ++this.scriptLimit;
               var1 = (var1 << 10) + var2 + -56613888;
            }
         }

         return var1;
      }
   }

   private final void pushback(int var1) {
      if (var1 >= 0) {
         if (var1 >= 65536) {
            this.scriptLimit -= 2;
         } else {
            --this.scriptLimit;
         }
      }

   }

   private static boolean sameScript(int var0, int var1) {
      return var0 == var1 || var0 <= 1 || var1 <= 1;
   }

   private static final byte highBit(int var0) {
      if (var0 <= 0) {
         return -32;
      } else {
         byte var1 = 0;
         if (var0 >= 65536) {
            var0 >>= 16;
            var1 = (byte)(var1 + 16);
         }

         if (var0 >= 256) {
            var0 >>= 8;
            var1 = (byte)(var1 + 8);
         }

         if (var0 >= 16) {
            var0 >>= 4;
            var1 = (byte)(var1 + 4);
         }

         if (var0 >= 4) {
            var0 >>= 2;
            var1 = (byte)(var1 + 2);
         }

         if (var0 >= 2) {
            var0 >>= 1;
            ++var1;
         }

         return var1;
      }
   }

   private static int getPairIndex(int var0) {
      int var1 = pairedCharPower;
      int var2 = 0;
      if (var0 >= pairedChars[pairedCharExtra]) {
         var2 = pairedCharExtra;
      }

      while(var1 > 1) {
         var1 >>= 1;
         if (var0 >= pairedChars[var2 + var1]) {
            var2 += var1;
         }
      }

      if (pairedChars[var2] != var0) {
         var2 = -1;
      }

      return var2;
   }

   static {
      pairedCharPower = 1 << highBit(pairedChars.length);
      pairedCharExtra = pairedChars.length - pairedCharPower;
   }
}
