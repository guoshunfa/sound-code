package sun.rmi.server;

import java.io.ObjectOutput;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteRef;
import sun.misc.ObjectInputFilter;
import sun.rmi.transport.LiveRef;

public class UnicastServerRef2 extends UnicastServerRef {
   private static final long serialVersionUID = -2289703812660767614L;

   public UnicastServerRef2() {
   }

   public UnicastServerRef2(LiveRef var1) {
      super(var1);
   }

   public UnicastServerRef2(LiveRef var1, ObjectInputFilter var2) {
      super(var1, var2);
   }

   public UnicastServerRef2(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3) {
      super(new LiveRef(var1, var2, var3));
   }

   public UnicastServerRef2(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3, ObjectInputFilter var4) {
      super(new LiveRef(var1, var2, var3), var4);
   }

   public String getRefClass(ObjectOutput var1) {
      return "UnicastServerRef2";
   }

   protected RemoteRef getClientRef() {
      return new UnicastRef2(this.ref);
   }
}
