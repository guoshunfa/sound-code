package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.protocol.CorbaClientDelegateImpl;
import com.sun.corba.se.impl.transport.CorbaContactInfoListImpl;
import com.sun.corba.se.impl.transport.ReadTCPTimeoutsImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;

public abstract class TransportDefault {
   private TransportDefault() {
   }

   public static CorbaContactInfoListFactory makeCorbaContactInfoListFactory(final ORB var0) {
      return new CorbaContactInfoListFactory() {
         public void setORB(ORB var1) {
         }

         public CorbaContactInfoList create(IOR var1) {
            return new CorbaContactInfoListImpl(var0, var1);
         }
      };
   }

   public static ClientDelegateFactory makeClientDelegateFactory(final ORB var0) {
      return new ClientDelegateFactory() {
         public CorbaClientDelegate create(CorbaContactInfoList var1) {
            return new CorbaClientDelegateImpl(var0, var1);
         }
      };
   }

   public static IORTransformer makeIORTransformer(ORB var0) {
      return null;
   }

   public static ReadTimeoutsFactory makeReadTimeoutsFactory() {
      return new ReadTimeoutsFactory() {
         public ReadTimeouts create(int var1, int var2, int var3, int var4) {
            return new ReadTCPTimeoutsImpl(var1, var2, var3, var4);
         }
      };
   }
}
