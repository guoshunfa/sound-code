package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class ATR implements Serializable {
   private static final long serialVersionUID = 6695383790847736493L;
   private byte[] atr;
   private transient int startHistorical;
   private transient int nHistorical;

   public ATR(byte[] var1) {
      this.atr = (byte[])var1.clone();
      this.parse();
   }

   private void parse() {
      if (this.atr.length >= 2) {
         if (this.atr[0] == 59 || this.atr[0] == 63) {
            int var1 = (this.atr[1] & 240) >> 4;
            int var2 = this.atr[1] & 15;
            int var3 = 2;

            while(var1 != 0 && var3 < this.atr.length) {
               if ((var1 & 1) != 0) {
                  ++var3;
               }

               if ((var1 & 2) != 0) {
                  ++var3;
               }

               if ((var1 & 4) != 0) {
                  ++var3;
               }

               if ((var1 & 8) != 0) {
                  if (var3 >= this.atr.length) {
                     return;
                  }

                  var1 = (this.atr[var3++] & 240) >> 4;
               } else {
                  var1 = 0;
               }
            }

            int var4 = var3 + var2;
            if (var4 == this.atr.length || var4 == this.atr.length - 1) {
               this.startHistorical = var3;
               this.nHistorical = var2;
            }

         }
      }
   }

   public byte[] getBytes() {
      return (byte[])this.atr.clone();
   }

   public byte[] getHistoricalBytes() {
      byte[] var1 = new byte[this.nHistorical];
      System.arraycopy(this.atr, this.startHistorical, var1, 0, this.nHistorical);
      return var1;
   }

   public String toString() {
      return "ATR: " + this.atr.length + " bytes";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ATR)) {
         return false;
      } else {
         ATR var2 = (ATR)var1;
         return Arrays.equals(this.atr, var2.atr);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.atr);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.atr = (byte[])((byte[])var1.readUnshared());
      this.parse();
   }
}
