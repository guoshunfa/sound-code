package org.omg.PortableServer;

public interface ServantActivatorOperations extends ServantManagerOperations {
   Servant incarnate(byte[] var1, POA var2) throws ForwardRequest;

   void etherealize(byte[] var1, POA var2, Servant var3, boolean var4, boolean var5);
}
