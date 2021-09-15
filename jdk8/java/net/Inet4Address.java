package java.net;

import java.io.ObjectStreamException;

public final class Inet4Address extends InetAddress {
   static final int INADDRSZ = 4;
   private static final long serialVersionUID = 3286316764910316507L;

   Inet4Address() {
      this.holder().hostName = null;
      this.holder().address = 0;
      this.holder().family = 1;
   }

   Inet4Address(String var1, byte[] var2) {
      this.holder().hostName = var1;
      this.holder().family = 1;
      if (var2 != null && var2.length == 4) {
         int var3 = var2[3] & 255;
         var3 |= var2[2] << 8 & '\uff00';
         var3 |= var2[1] << 16 & 16711680;
         var3 |= var2[0] << 24 & -16777216;
         this.holder().address = var3;
      }

      this.holder().originalHostName = var1;
   }

   Inet4Address(String var1, int var2) {
      this.holder().hostName = var1;
      this.holder().family = 1;
      this.holder().address = var2;
      this.holder().originalHostName = var1;
   }

   private Object writeReplace() throws ObjectStreamException {
      InetAddress var1 = new InetAddress();
      var1.holder().hostName = this.holder().getHostName();
      var1.holder().address = this.holder().getAddress();
      var1.holder().family = 2;
      return var1;
   }

   public boolean isMulticastAddress() {
      return (this.holder().getAddress() & -268435456) == -536870912;
   }

   public boolean isAnyLocalAddress() {
      return this.holder().getAddress() == 0;
   }

   public boolean isLoopbackAddress() {
      byte[] var1 = this.getAddress();
      return var1[0] == 127;
   }

   public boolean isLinkLocalAddress() {
      int var1 = this.holder().getAddress();
      return (var1 >>> 24 & 255) == 169 && (var1 >>> 16 & 255) == 254;
   }

   public boolean isSiteLocalAddress() {
      int var1 = this.holder().getAddress();
      return (var1 >>> 24 & 255) == 10 || (var1 >>> 24 & 255) == 172 && (var1 >>> 16 & 240) == 16 || (var1 >>> 24 & 255) == 192 && (var1 >>> 16 & 255) == 168;
   }

   public boolean isMCGlobal() {
      byte[] var1 = this.getAddress();
      return (var1[0] & 255) >= 224 && (var1[0] & 255) <= 238 && ((var1[0] & 255) != 224 || var1[1] != 0 || var1[2] != 0);
   }

   public boolean isMCNodeLocal() {
      return false;
   }

   public boolean isMCLinkLocal() {
      int var1 = this.holder().getAddress();
      return (var1 >>> 24 & 255) == 224 && (var1 >>> 16 & 255) == 0 && (var1 >>> 8 & 255) == 0;
   }

   public boolean isMCSiteLocal() {
      int var1 = this.holder().getAddress();
      return (var1 >>> 24 & 255) == 239 && (var1 >>> 16 & 255) == 255;
   }

   public boolean isMCOrgLocal() {
      int var1 = this.holder().getAddress();
      return (var1 >>> 24 & 255) == 239 && (var1 >>> 16 & 255) >= 192 && (var1 >>> 16 & 255) <= 195;
   }

   public byte[] getAddress() {
      int var1 = this.holder().getAddress();
      byte[] var2 = new byte[]{(byte)(var1 >>> 24 & 255), (byte)(var1 >>> 16 & 255), (byte)(var1 >>> 8 & 255), (byte)(var1 & 255)};
      return var2;
   }

   public String getHostAddress() {
      return numericToTextFormat(this.getAddress());
   }

   public int hashCode() {
      return this.holder().getAddress();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof Inet4Address && ((InetAddress)var1).holder().getAddress() == this.holder().getAddress();
   }

   static String numericToTextFormat(byte[] var0) {
      return (var0[0] & 255) + "." + (var0[1] & 255) + "." + (var0[2] & 255) + "." + (var0[3] & 255);
   }

   private static native void init();

   static {
      init();
   }
}
