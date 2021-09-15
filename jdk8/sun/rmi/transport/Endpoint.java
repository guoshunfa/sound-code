package sun.rmi.transport;

import java.rmi.RemoteException;

public interface Endpoint {
   Channel getChannel();

   void exportObject(Target var1) throws RemoteException;

   Transport getInboundTransport();

   Transport getOutboundTransport();
}
