package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.BootstrapServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.CorbaClientRequestDispatcherImpl;
import com.sun.corba.se.impl.protocol.CorbaServerRequestDispatcherImpl;
import com.sun.corba.se.impl.protocol.FullServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.INSServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.InfoOnlyServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.impl.protocol.MinimalServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.POALocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public final class RequestDispatcherDefault {
   private RequestDispatcherDefault() {
   }

   public static ClientRequestDispatcher makeClientRequestDispatcher() {
      return new CorbaClientRequestDispatcherImpl();
   }

   public static CorbaServerRequestDispatcher makeServerRequestDispatcher(ORB var0) {
      return new CorbaServerRequestDispatcherImpl(var0);
   }

   public static CorbaServerRequestDispatcher makeBootstrapServerRequestDispatcher(ORB var0) {
      return new BootstrapServerRequestDispatcher(var0);
   }

   public static CorbaServerRequestDispatcher makeINSServerRequestDispatcher(ORB var0) {
      return new INSServerRequestDispatcher(var0);
   }

   public static LocalClientRequestDispatcherFactory makeMinimalServantCacheLocalClientRequestDispatcherFactory(final ORB var0) {
      return new LocalClientRequestDispatcherFactory() {
         public LocalClientRequestDispatcher create(int var1, IOR var2) {
            return new MinimalServantCacheLocalCRDImpl(var0, var1, var2);
         }
      };
   }

   public static LocalClientRequestDispatcherFactory makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(final ORB var0) {
      return new LocalClientRequestDispatcherFactory() {
         public LocalClientRequestDispatcher create(int var1, IOR var2) {
            return new InfoOnlyServantCacheLocalCRDImpl(var0, var1, var2);
         }
      };
   }

   public static LocalClientRequestDispatcherFactory makeFullServantCacheLocalClientRequestDispatcherFactory(final ORB var0) {
      return new LocalClientRequestDispatcherFactory() {
         public LocalClientRequestDispatcher create(int var1, IOR var2) {
            return new FullServantCacheLocalCRDImpl(var0, var1, var2);
         }
      };
   }

   public static LocalClientRequestDispatcherFactory makeJIDLLocalClientRequestDispatcherFactory(final ORB var0) {
      return new LocalClientRequestDispatcherFactory() {
         public LocalClientRequestDispatcher create(int var1, IOR var2) {
            return new JIDLLocalCRDImpl(var0, var1, var2);
         }
      };
   }

   public static LocalClientRequestDispatcherFactory makePOALocalClientRequestDispatcherFactory(final ORB var0) {
      return new LocalClientRequestDispatcherFactory() {
         public LocalClientRequestDispatcher create(int var1, IOR var2) {
            return new POALocalCRDImpl(var0, var1, var2);
         }
      };
   }
}
