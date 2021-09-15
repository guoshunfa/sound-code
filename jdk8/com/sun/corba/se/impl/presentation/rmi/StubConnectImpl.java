package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class StubConnectImpl {
   static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");

   public static StubIORImpl connect(StubIORImpl var0, Object var1, ObjectImpl var2, ORB var3) throws RemoteException {
      Delegate var4 = null;

      try {
         try {
            var4 = StubAdapter.getDelegate(var2);
            if (var4.orb(var2) != var3) {
               throw wrapper.connectWrongOrb();
            }
         } catch (BAD_OPERATION var11) {
            if (var0 == null) {
               Tie var6 = Utility.getAndForgetTie(var1);
               if (var6 == null) {
                  throw wrapper.connectNoTie();
               }

               ORB var7 = var3;

               try {
                  var7 = var6.orb();
               } catch (BAD_OPERATION var9) {
                  var6.orb(var3);
               } catch (BAD_INV_ORDER var10) {
                  var6.orb(var3);
               }

               if (var7 != var3) {
                  throw wrapper.connectTieWrongOrb();
               }

               var4 = StubAdapter.getDelegate(var6);
               CORBAObjectImpl var8 = new CORBAObjectImpl();
               var8._set_delegate(var4);
               var0 = new StubIORImpl(var8);
            } else {
               var4 = var0.getDelegate(var3);
            }

            StubAdapter.setDelegate(var2, var4);
         }

         return var0;
      } catch (SystemException var12) {
         throw new RemoteException("CORBA SystemException", var12);
      }
   }
}
