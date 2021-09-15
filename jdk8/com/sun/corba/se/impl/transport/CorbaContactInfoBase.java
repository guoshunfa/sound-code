package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.nio.ByteBuffer;
import sun.corba.OutputStreamFactory;

public abstract class CorbaContactInfoBase implements CorbaContactInfo {
   protected ORB orb;
   protected CorbaContactInfoList contactInfoList;
   protected IOR effectiveTargetIOR;
   protected short addressingDisposition;
   protected OutboundConnectionCache connectionCache;

   public Broker getBroker() {
      return this.orb;
   }

   public ContactInfoList getContactInfoList() {
      return this.contactInfoList;
   }

   public ClientRequestDispatcher getClientRequestDispatcher() {
      int var1 = this.getEffectiveProfile().getObjectKeyTemplate().getSubcontractId();
      RequestDispatcherRegistry var2 = this.orb.getRequestDispatcherRegistry();
      return var2.getClientRequestDispatcher(var1);
   }

   public void setConnectionCache(OutboundConnectionCache var1) {
      this.connectionCache = var1;
   }

   public OutboundConnectionCache getConnectionCache() {
      return this.connectionCache;
   }

   public MessageMediator createMessageMediator(Broker var1, ContactInfo var2, Connection var3, String var4, boolean var5) {
      CorbaMessageMediatorImpl var6 = new CorbaMessageMediatorImpl((ORB)var1, var2, var3, GIOPVersion.chooseRequestVersion((ORB)var1, this.effectiveTargetIOR), this.effectiveTargetIOR, ((CorbaConnection)var3).getNextRequestId(), this.getAddressingDisposition(), var4, var5);
      return var6;
   }

   public MessageMediator createMessageMediator(Broker var1, Connection var2) {
      ORB var3 = (ORB)var1;
      CorbaConnection var4 = (CorbaConnection)var2;
      if (var3.transportDebugFlag) {
         if (var4.shouldReadGiopHeaderOnly()) {
            this.dprint(".createMessageMediator: waiting for message header on connection: " + var4);
         } else {
            this.dprint(".createMessageMediator: waiting for message on connection: " + var4);
         }
      }

      MessageBase var5 = null;
      if (var4.shouldReadGiopHeaderOnly()) {
         var5 = MessageBase.readGIOPHeader(var3, var4);
      } else {
         var5 = MessageBase.readGIOPMessage(var3, var4);
      }

      ByteBuffer var6 = var5.getByteBuffer();
      var5.setByteBuffer((ByteBuffer)null);
      CorbaMessageMediatorImpl var7 = new CorbaMessageMediatorImpl(var3, var4, var5, var6);
      return var7;
   }

   public MessageMediator finishCreatingMessageMediator(Broker var1, Connection var2, MessageMediator var3) {
      ORB var4 = (ORB)var1;
      CorbaConnection var5 = (CorbaConnection)var2;
      CorbaMessageMediator var6 = (CorbaMessageMediator)var3;
      if (var4.transportDebugFlag) {
         this.dprint(".finishCreatingMessageMediator: waiting for message body on connection: " + var5);
      }

      Message var7 = var6.getDispatchHeader();
      var7.setByteBuffer(var6.getDispatchBuffer());
      var7 = MessageBase.readGIOPBody(var4, var5, var7);
      ByteBuffer var8 = var7.getByteBuffer();
      var7.setByteBuffer((ByteBuffer)null);
      var6.setDispatchHeader(var7);
      var6.setDispatchBuffer(var8);
      return var6;
   }

   public OutputObject createOutputObject(MessageMediator var1) {
      CorbaMessageMediator var2 = (CorbaMessageMediator)var1;
      CDROutputObject var3 = OutputStreamFactory.newCDROutputObject(this.orb, var1, var2.getRequestHeader(), var2.getStreamFormatVersion());
      var1.setOutputObject(var3);
      return var3;
   }

   public InputObject createInputObject(Broker var1, MessageMediator var2) {
      CorbaMessageMediator var3 = (CorbaMessageMediator)var2;
      return new CDRInputObject((ORB)var1, (CorbaConnection)var2.getConnection(), var3.getDispatchBuffer(), var3.getDispatchHeader());
   }

   public short getAddressingDisposition() {
      return this.addressingDisposition;
   }

   public void setAddressingDisposition(short var1) {
      this.addressingDisposition = var1;
   }

   public IOR getTargetIOR() {
      return this.contactInfoList.getTargetIOR();
   }

   public IOR getEffectiveTargetIOR() {
      return this.effectiveTargetIOR;
   }

   public IIOPProfile getEffectiveProfile() {
      return this.effectiveTargetIOR.getProfile();
   }

   public String toString() {
      return "CorbaContactInfoBase[]";
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaContactInfoBase", var1);
   }
}
