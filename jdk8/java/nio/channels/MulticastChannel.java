package java.nio.channels;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface MulticastChannel extends NetworkChannel {
   void close() throws IOException;

   MembershipKey join(InetAddress var1, NetworkInterface var2) throws IOException;

   MembershipKey join(InetAddress var1, NetworkInterface var2, InetAddress var3) throws IOException;
}
