package sun.management.jmxremote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Enumeration;

public final class LocalRMIServerSocketFactory implements RMIServerSocketFactory {
   public ServerSocket createServerSocket(int var1) throws IOException {
      return new ServerSocket(var1) {
         public Socket accept() throws IOException {
            Socket var1 = super.accept();
            InetAddress var2 = var1.getInetAddress();
            if (var2 == null) {
               String var12 = "";
               if (var1.isClosed()) {
                  var12 = " Socket is closed.";
               } else if (!var1.isConnected()) {
                  var12 = " Socket is not connected";
               }

               try {
                  var1.close();
               } catch (Exception var9) {
               }

               throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported. Couldn't determine client address." + var12);
            } else if (var2.isLoopbackAddress()) {
               return var1;
            } else {
               Enumeration var4;
               try {
                  var4 = NetworkInterface.getNetworkInterfaces();
               } catch (SocketException var11) {
                  try {
                     var1.close();
                  } catch (IOException var8) {
                  }

                  throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.", var11);
               }

               while(var4.hasMoreElements()) {
                  NetworkInterface var5 = (NetworkInterface)var4.nextElement();
                  Enumeration var6 = var5.getInetAddresses();

                  while(var6.hasMoreElements()) {
                     InetAddress var7 = (InetAddress)var6.nextElement();
                     if (var7.equals(var2)) {
                        return var1;
                     }
                  }
               }

               try {
                  var1.close();
               } catch (IOException var10) {
               }

               throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.");
            }
         }
      };
   }

   public boolean equals(Object var1) {
      return var1 instanceof LocalRMIServerSocketFactory;
   }

   public int hashCode() {
      return this.getClass().hashCode();
   }
}
