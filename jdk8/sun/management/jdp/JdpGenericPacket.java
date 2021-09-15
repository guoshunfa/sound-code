package sun.management.jdp;

public abstract class JdpGenericPacket implements JdpPacket {
   private static final int MAGIC = -1056969150;
   private static final short PROTOCOL_VERSION = 1;

   protected JdpGenericPacket() {
   }

   public static void checkMagic(int var0) throws JdpException {
      if (var0 != -1056969150) {
         throw new JdpException("Invalid JDP magic header: " + var0);
      }
   }

   public static void checkVersion(short var0) throws JdpException {
      if (var0 > 1) {
         throw new JdpException("Unsupported protocol version: " + var0);
      }
   }

   public static int getMagic() {
      return -1056969150;
   }

   public static short getVersion() {
      return 1;
   }
}
