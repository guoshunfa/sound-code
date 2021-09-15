package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketFactoryContactInfoImpl extends SocketOrChannelContactInfoImpl {
   protected ORBUtilSystemException wrapper;
   protected SocketInfo socketInfo;

   public SocketFactoryContactInfoImpl() {
   }

   public SocketFactoryContactInfoImpl(ORB var1, CorbaContactInfoList var2, IOR var3, short var4, SocketInfo var5) {
      super(var1, var2);
      this.effectiveTargetIOR = var3;
      this.addressingDisposition = var4;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
      this.socketInfo = var1.getORBData().getLegacySocketFactory().getEndPointInfo(var1, var3, var5);
      this.socketType = this.socketInfo.getType();
      this.hostname = this.socketInfo.getHost();
      this.port = this.socketInfo.getPort();
   }

   public Connection createConnection() {
      SocketFactoryConnectionImpl var1 = new SocketFactoryConnectionImpl(this.orb, this, this.orb.getORBData().connectionSocketUseSelectThreadToWait(), this.orb.getORBData().connectionSocketUseWorkerThreadForEvent());
      return var1;
   }

   public String toString() {
      return "SocketFactoryContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]";
   }
}
