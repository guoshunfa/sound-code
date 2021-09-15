package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.StubConnectImpl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.StubDelegate;
import org.omg.CORBA.ORB;

public class StubDelegateImpl implements StubDelegate {
   static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");
   private StubIORImpl ior = null;

   public StubIORImpl getIOR() {
      return this.ior;
   }

   private void init(Stub var1) {
      if (this.ior == null) {
         this.ior = new StubIORImpl(var1);
      }

   }

   public int hashCode(Stub var1) {
      this.init(var1);
      return this.ior.hashCode();
   }

   public boolean equals(Stub var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else if (!(var2 instanceof Stub)) {
         return false;
      } else {
         Stub var3 = (Stub)var2;
         return var3.hashCode() != var1.hashCode() ? false : var1.toString().equals(var3.toString());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof StubDelegateImpl)) {
         return false;
      } else {
         StubDelegateImpl var2 = (StubDelegateImpl)var1;
         if (this.ior == null) {
            return this.ior == var2.ior;
         } else {
            return this.ior.equals(var2.ior);
         }
      }
   }

   public int hashCode() {
      return this.ior == null ? 0 : this.ior.hashCode();
   }

   public String toString(Stub var1) {
      return this.ior == null ? null : this.ior.toString();
   }

   public void connect(Stub var1, ORB var2) throws RemoteException {
      this.ior = StubConnectImpl.connect(this.ior, var1, var1, var2);
   }

   public void readObject(Stub var1, ObjectInputStream var2) throws IOException, ClassNotFoundException {
      if (this.ior == null) {
         this.ior = new StubIORImpl();
      }

      this.ior.doRead(var2);
   }

   public void writeObject(Stub var1, ObjectOutputStream var2) throws IOException {
      this.init(var1);
      this.ior.doWrite(var2);
   }
}
