package sun.rmi.server;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutput;
import java.rmi.activation.ActivationID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteRef;
import sun.rmi.transport.LiveRef;

public class ActivatableServerRef extends UnicastServerRef2 {
   private static final long serialVersionUID = 2002967993223003793L;
   private ActivationID id;

   public ActivatableServerRef(ActivationID var1, int var2) {
      this(var1, var2, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null);
   }

   public ActivatableServerRef(ActivationID var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) {
      super(new LiveRef(var2, var3, var4));
      this.id = var1;
   }

   public String getRefClass(ObjectOutput var1) {
      return "ActivatableServerRef";
   }

   protected RemoteRef getClientRef() {
      return new ActivatableRef(this.id, new UnicastRef2(this.ref));
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      throw new NotSerializableException("ActivatableServerRef not serializable");
   }
}
