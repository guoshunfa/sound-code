package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.spi.orb.ORB;

public class SocketFactoryAcceptorImpl extends SocketOrChannelAcceptorImpl {
   public SocketFactoryAcceptorImpl(ORB var1, int var2, String var3, String var4) {
      super(var1, var2, var3, var4);
   }

   public boolean initialize() {
      if (this.initialized) {
         return false;
      } else {
         if (this.orb.transportDebugFlag) {
            this.dprint("initialize: " + this);
         }

         try {
            this.serverSocket = this.orb.getORBData().getLegacySocketFactory().createServerSocket(this.type, this.port);
            this.internalInitialize();
         } catch (Throwable var2) {
            throw this.wrapper.createListenerFailed((Throwable)var2, Integer.toString(this.port));
         }

         this.initialized = true;
         return true;
      }
   }

   protected String toStringName() {
      return "SocketFactoryAcceptorImpl";
   }

   protected void dprint(String var1) {
      ORBUtility.dprint(this.toStringName(), var1);
   }
}
