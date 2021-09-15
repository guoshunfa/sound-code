package sun.rmi.server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;

public interface Dispatcher {
   void dispatch(Remote var1, RemoteCall var2) throws IOException;
}
