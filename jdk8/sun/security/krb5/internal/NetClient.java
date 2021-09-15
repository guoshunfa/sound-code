package sun.security.krb5.internal;

import java.io.IOException;

public abstract class NetClient implements AutoCloseable {
   public static NetClient getInstance(String var0, String var1, int var2, int var3) throws IOException {
      return (NetClient)(var0.equals("TCP") ? new TCPClient(var1, var2, var3) : new UDPClient(var1, var2, var3));
   }

   public abstract void send(byte[] var1) throws IOException;

   public abstract byte[] receive() throws IOException;

   public abstract void close() throws IOException;
}
