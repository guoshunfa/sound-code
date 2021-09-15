package sun.management.jdp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JdpPacketReader {
   private final DataInputStream pkt;
   private Map<String, String> pmap = null;

   public JdpPacketReader(byte[] var1) throws JdpException {
      ByteArrayInputStream var2 = new ByteArrayInputStream(var1);
      this.pkt = new DataInputStream(var2);

      try {
         int var3 = this.pkt.readInt();
         JdpGenericPacket.checkMagic(var3);
      } catch (IOException var5) {
         throw new JdpException("Invalid JDP packet received, bad magic");
      }

      try {
         short var6 = this.pkt.readShort();
         JdpGenericPacket.checkVersion(var6);
      } catch (IOException var4) {
         throw new JdpException("Invalid JDP packet received, bad protocol version");
      }
   }

   public String getEntry() throws EOFException, JdpException {
      try {
         short var1 = this.pkt.readShort();
         if (var1 < 1 && var1 > this.pkt.available()) {
            throw new JdpException("Broken JDP packet. Invalid entry length field.");
         } else {
            byte[] var2 = new byte[var1];
            if (this.pkt.read(var2) != var1) {
               throw new JdpException("Broken JDP packet. Unable to read entry.");
            } else {
               return new String(var2, "UTF-8");
            }
         }
      } catch (EOFException var3) {
         throw var3;
      } catch (UnsupportedEncodingException var4) {
         throw new JdpException("Broken JDP packet. Unable to decode entry.");
      } catch (IOException var5) {
         throw new JdpException("Broken JDP packet. Unable to read entry.");
      }
   }

   public Map<String, String> getDiscoveryDataAsMap() throws JdpException {
      if (this.pmap != null) {
         return this.pmap;
      } else {
         String var1 = null;
         String var2 = null;
         HashMap var3 = new HashMap();

         try {
            while(true) {
               var1 = this.getEntry();
               var2 = this.getEntry();
               var3.put(var1, var2);
            }
         } catch (EOFException var5) {
            if (var2 == null) {
               throw new JdpException("Broken JDP packet. Key without value." + var1);
            } else {
               this.pmap = Collections.unmodifiableMap(var3);
               return this.pmap;
            }
         }
      }
   }
}
