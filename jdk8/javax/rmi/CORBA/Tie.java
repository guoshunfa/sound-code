package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InvokeHandler;

public interface Tie extends InvokeHandler {
   Object thisObject();

   void deactivate() throws NoSuchObjectException;

   ORB orb();

   void orb(ORB var1);

   void setTarget(Remote var1);

   Remote getTarget();
}
