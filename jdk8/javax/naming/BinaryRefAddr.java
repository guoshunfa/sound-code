package javax.naming;

public class BinaryRefAddr extends RefAddr {
   private byte[] buf;
   private static final long serialVersionUID = -3415254970957330361L;

   public BinaryRefAddr(String var1, byte[] var2) {
      this(var1, var2, 0, var2.length);
   }

   public BinaryRefAddr(String var1, byte[] var2, int var3, int var4) {
      super(var1);
      this.buf = null;
      this.buf = new byte[var4];
      System.arraycopy(var2, var3, this.buf, 0, var4);
   }

   public Object getContent() {
      return this.buf;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof BinaryRefAddr) {
         BinaryRefAddr var2 = (BinaryRefAddr)var1;
         if (this.addrType.compareTo(var2.addrType) == 0) {
            if (this.buf == null && var2.buf == null) {
               return true;
            }

            if (this.buf != null && var2.buf != null && this.buf.length == var2.buf.length) {
               for(int var3 = 0; var3 < this.buf.length; ++var3) {
                  if (this.buf[var3] != var2.buf[var3]) {
                     return false;
                  }
               }

               return true;
            }

            return false;
         }
      }

      return false;
   }

   public int hashCode() {
      int var1 = this.addrType.hashCode();

      for(int var2 = 0; var2 < this.buf.length; ++var2) {
         var1 += this.buf[var2];
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("Address Type: " + this.addrType + "\n");
      var1.append("AddressContents: ");

      for(int var2 = 0; var2 < this.buf.length && var2 < 32; ++var2) {
         var1.append(Integer.toHexString(this.buf[var2]) + " ");
      }

      if (this.buf.length >= 32) {
         var1.append(" ...\n");
      }

      return var1.toString();
   }
}
