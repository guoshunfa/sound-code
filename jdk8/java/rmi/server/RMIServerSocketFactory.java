package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;

public interface RMIServerSocketFactory {
   ServerSocket createServerSocket(int var1) throws IOException;
}
