package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.SocketOrChannelConnectionImpl;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketFactoryConnectionImpl extends SocketOrChannelConnectionImpl {
   public SocketFactoryConnectionImpl(ORB var1, CorbaContactInfo var2, boolean var3, boolean var4) {
      super(var1, var3, var4);
      this.contactInfo = var2;
      boolean var5 = !var3;
      SocketInfo var6 = ((SocketFactoryContactInfoImpl)var2).socketInfo;

      try {
         this.socket = var1.getORBData().getLegacySocketFactory().createSocket(var6);
         this.socketChannel = this.socket.getChannel();
         if (this.socketChannel != null) {
            this.socketChannel.configureBlocking(var5);
         } else {
            this.setUseSelectThreadToWait(false);
         }

         if (var1.transportDebugFlag) {
            this.dprint(".initialize: connection created: " + this.socket);
         }
      } catch (GetEndPointInfoAgainException var8) {
         throw this.wrapper.connectFailure((Throwable)var8, var6.getType(), var6.getHost(), Integer.toString(var6.getPort()));
      } catch (Exception var9) {
         throw this.wrapper.connectFailure((Throwable)var9, var6.getType(), var6.getHost(), Integer.toString(var6.getPort()));
      }

      this.state = 1;
   }

   public String toString() {
      synchronized(this.stateEvent) {
         return "SocketFactoryConnectionImpl[ " + (this.socketChannel == null ? this.socket.toString() : this.socketChannel.toString()) + " " + this.getStateString(this.state) + " " + this.shouldUseSelectThreadToWait() + " " + this.shouldUseWorkerThreadForEvent() + "]";
      }
   }

   public void dprint(String var1) {
      ORBUtility.dprint("SocketFactoryConnectionImpl", var1);
   }
}
