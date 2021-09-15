package sun.management.jdp;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;

public final class JdpBroadcaster {
   private final InetAddress addr;
   private final int port;
   private final DatagramChannel channel;

   public JdpBroadcaster(InetAddress var1, InetAddress var2, int var3, int var4) throws IOException, JdpException {
      this.addr = var1;
      this.port = var3;
      StandardProtocolFamily var5 = var1 instanceof Inet6Address ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET;
      this.channel = DatagramChannel.open(var5);
      this.channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      this.channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, var4);
      if (var2 != null) {
         NetworkInterface var6 = NetworkInterface.getByInetAddress(var2);

         try {
            this.channel.bind(new InetSocketAddress(var2, 0));
         } catch (UnsupportedAddressTypeException var8) {
            throw new JdpException("Unable to bind to source address");
         }

         this.channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, var6);
      }

   }

   public JdpBroadcaster(InetAddress var1, int var2, int var3) throws IOException, JdpException {
      this(var1, (InetAddress)null, var2, var3);
   }

   public void sendPacket(JdpPacket var1) throws IOException {
      byte[] var2 = var1.getPacketData();
      ByteBuffer var3 = ByteBuffer.wrap(var2);
      this.channel.send(var3, new InetSocketAddress(this.addr, this.port));
   }

   public void shutdown() throws IOException {
      this.channel.close();
   }
}
