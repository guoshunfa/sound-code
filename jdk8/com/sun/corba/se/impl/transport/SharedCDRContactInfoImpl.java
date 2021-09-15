package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.SharedCDRClientRequestDispatcherImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import sun.corba.OutputStreamFactory;

public class SharedCDRContactInfoImpl extends CorbaContactInfoBase {
   private static int requestId = 0;
   protected ORBUtilSystemException wrapper;

   public SharedCDRContactInfoImpl(ORB var1, CorbaContactInfoList var2, IOR var3, short var4) {
      this.orb = var1;
      this.contactInfoList = var2;
      this.effectiveTargetIOR = var3;
      this.addressingDisposition = var4;
   }

   public ClientRequestDispatcher getClientRequestDispatcher() {
      return new SharedCDRClientRequestDispatcherImpl();
   }

   public boolean isConnectionBased() {
      return false;
   }

   public boolean shouldCacheConnection() {
      return false;
   }

   public String getConnectionCacheType() {
      throw this.getWrapper().methodShouldNotBeCalled();
   }

   public Connection createConnection() {
      throw this.getWrapper().methodShouldNotBeCalled();
   }

   public MessageMediator createMessageMediator(Broker var1, ContactInfo var2, Connection var3, String var4, boolean var5) {
      if (var3 != null) {
         throw new RuntimeException("connection is not null");
      } else {
         CorbaMessageMediatorImpl var6 = new CorbaMessageMediatorImpl((ORB)var1, var2, (Connection)null, GIOPVersion.chooseRequestVersion((ORB)var1, this.effectiveTargetIOR), this.effectiveTargetIOR, requestId++, this.getAddressingDisposition(), var4, var5);
         return var6;
      }
   }

   public OutputObject createOutputObject(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;
      CDROutputObject var3 = OutputStreamFactory.newCDROutputObject(this.orb, var1, var2.getRequestHeader(), var2.getStreamFormatVersion(), 0);
      var1.setOutputObject(var3);
      return var3;
   }

   public String getMonitoringName() {
      throw this.getWrapper().methodShouldNotBeCalled();
   }

   public String toString() {
      return "SharedCDRContactInfoImpl[]";
   }

   protected ORBUtilSystemException getWrapper() {
      if (this.wrapper == null) {
         this.wrapper = ORBUtilSystemException.get(this.orb, "rpc.transport");
      }

      return this.wrapper;
   }
}
