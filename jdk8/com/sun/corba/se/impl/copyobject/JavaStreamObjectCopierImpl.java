package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import org.omg.CORBA.ORB;

public class JavaStreamObjectCopierImpl implements ObjectCopier {
   private ORB orb;

   public JavaStreamObjectCopierImpl(ORB var1) {
      this.orb = var1;
   }

   public Object copy(Object var1) {
      if (var1 instanceof Remote) {
         return Utility.autoConnect(var1, this.orb, true);
      } else {
         try {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream(10000);
            ObjectOutputStream var3 = new ObjectOutputStream(var2);
            var3.writeObject(var1);
            byte[] var4 = var2.toByteArray();
            ByteArrayInputStream var5 = new ByteArrayInputStream(var4);
            ObjectInputStream var6 = new ObjectInputStream(var5);
            return var6.readObject();
         } catch (Exception var7) {
            System.out.println("Failed with exception:" + var7);
            return null;
         }
      }
   }
}
