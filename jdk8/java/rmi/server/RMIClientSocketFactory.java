package java.rmi.server;

import java.io.IOException;
import java.net.Socket;

public interface RMIClientSocketFactory {
   Socket createSocket(String var1, int var2) throws IOException;
}
