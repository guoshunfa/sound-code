package sun.management.jdp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class JdpPacketWriter {
   private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
   private final DataOutputStream pkt;

   public JdpPacketWriter() throws IOException {
      this.pkt = new DataOutputStream(this.baos);
      this.pkt.writeInt(JdpGenericPacket.getMagic());
      this.pkt.writeShort(JdpGenericPacket.getVersion());
   }

   public void addEntry(String var1) throws IOException {
      this.pkt.writeUTF(var1);
   }

   public void addEntry(String var1, String var2) throws IOException {
      if (var2 != null) {
         this.addEntry(var1);
         this.addEntry(var2);
      }

   }

   public byte[] getPacketBytes() {
      return this.baos.toByteArray();
   }
}
