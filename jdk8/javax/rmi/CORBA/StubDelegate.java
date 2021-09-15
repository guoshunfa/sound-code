package javax.rmi.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;

public interface StubDelegate {
   int hashCode(Stub var1);

   boolean equals(Stub var1, Object var2);

   String toString(Stub var1);

   void connect(Stub var1, ORB var2) throws RemoteException;

   void readObject(Stub var1, ObjectInputStream var2) throws IOException, ClassNotFoundException;

   void writeObject(Stub var1, ObjectOutputStream var2) throws IOException;
}
