package org.omg.PortableServer;

import org.omg.CORBA.ServerRequest;

public abstract class DynamicImplementation extends Servant {
   public abstract void invoke(ServerRequest var1);
}
