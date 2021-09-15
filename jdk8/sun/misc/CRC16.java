package sun.misc;

public class CRC16 {
   public int value = 0;

   public void update(byte var1) {
      int var2 = var1;

      for(int var4 = 7; var4 >= 0; --var4) {
         var2 <<= 1;
         int var3 = var2 >>> 8 & 1;
         if ((this.value & '耀') != 0) {
            this.value = (this.value << 1) + var3 ^ 4129;
         } else {
            this.value = (this.value << 1) + var3;
         }
      }

      this.value &= 65535;
   }

   public void reset() {
      this.value = 0;
   }
}
