package sun.security.krb5.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;

class UDPClient extends NetClient {
   InetAddress iaddr;
   int iport;
   int bufSize = 65507;
   DatagramSocket dgSocket;
   DatagramPacket dgPacketIn;

   UDPClient(String var1, int var2, int var3) throws UnknownHostException, SocketException {
      this.iaddr = InetAddress.getByName(var1);
      this.iport = var2;
      this.dgSocket = new DatagramSocket();
      this.dgSocket.setSoTimeout(var3);
      this.dgSocket.connect(this.iaddr, this.iport);
   }

   public void send(byte[] var1) throws IOException {
      DatagramPacket var2 = new DatagramPacket(var1, var1.length, this.iaddr, this.iport);
      this.dgSocket.send(var2);
   }

   public byte[] receive() throws IOException {
      byte[] var1 = new byte[this.bufSize];
      this.dgPacketIn = new DatagramPacket(var1, var1.length);

      try {
         this.dgSocket.receive(this.dgPacketIn);
      } catch (SocketException var3) {
         if (var3 instanceof PortUnreachableException) {
            throw var3;
         }

         this.dgSocket.receive(this.dgPacketIn);
      }

      byte[] var2 = new byte[this.dgPacketIn.getLength()];
      System.arraycopy(this.dgPacketIn.getData(), 0, var2, 0, this.dgPacketIn.getLength());
      return var2;
   }

   public void close() {
      this.dgSocket.close();
   }
}
