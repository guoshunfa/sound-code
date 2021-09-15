package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;

public class LegacyServerSocketManagerImpl implements LegacyServerSocketManager {
   protected ORB orb;
   private ORBUtilSystemException wrapper;

   public LegacyServerSocketManagerImpl(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
   }

   public int legacyGetTransientServerPort(String var1) {
      return this.legacyGetServerPort(var1, false);
   }

   public synchronized int legacyGetPersistentServerPort(String var1) {
      if (this.orb.getORBData().getServerIsORBActivated()) {
         return this.legacyGetServerPort(var1, true);
      } else if (this.orb.getORBData().getPersistentPortInitialized()) {
         return this.orb.getORBData().getPersistentServerPort();
      } else {
         throw this.wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
      }
   }

   public synchronized int legacyGetTransientOrPersistentServerPort(String var1) {
      return this.legacyGetServerPort(var1, this.orb.getORBData().getServerIsORBActivated());
   }

   public synchronized LegacyServerSocketEndPointInfo legacyGetEndpoint(String var1) {
      Iterator var2 = this.getAcceptorIterator();

      LegacyServerSocketEndPointInfo var3;
      do {
         if (!var2.hasNext()) {
            throw new INTERNAL("No acceptor for: " + var1);
         }

         var3 = this.cast(var2.next());
      } while(var3 == null || !var1.equals(var3.getName()));

      return var3;
   }

   public boolean legacyIsLocalServerPort(int var1) {
      Iterator var2 = this.getAcceptorIterator();

      LegacyServerSocketEndPointInfo var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = this.cast(var2.next());
      } while(var3 == null || var3.getPort() != var1);

      return true;
   }

   private int legacyGetServerPort(String var1, boolean var2) {
      Iterator var3 = this.getAcceptorIterator();

      LegacyServerSocketEndPointInfo var4;
      do {
         if (!var3.hasNext()) {
            return -1;
         }

         var4 = this.cast(var3.next());
      } while(var4 == null || !var4.getType().equals(var1));

      if (var2) {
         return var4.getLocatorPort();
      } else {
         return var4.getPort();
      }
   }

   private Iterator getAcceptorIterator() {
      Collection var1 = this.orb.getCorbaTransportManager().getAcceptors((String)null, (ObjectAdapterId)null);
      if (var1 != null) {
         return var1.iterator();
      } else {
         throw this.wrapper.getServerPortCalledBeforeEndpointsInitialized();
      }
   }

   private LegacyServerSocketEndPointInfo cast(Object var1) {
      return var1 instanceof LegacyServerSocketEndPointInfo ? (LegacyServerSocketEndPointInfo)var1 : null;
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("LegacyServerSocketManagerImpl", var1);
   }
}
