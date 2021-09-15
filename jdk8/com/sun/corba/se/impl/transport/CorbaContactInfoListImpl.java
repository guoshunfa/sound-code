package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.protocol.NotLocalLocalCRDImpl;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CorbaContactInfoListImpl implements CorbaContactInfoList {
   protected ORB orb;
   protected LocalClientRequestDispatcher LocalClientRequestDispatcher;
   protected IOR targetIOR;
   protected IOR effectiveTargetIOR;
   protected List effectiveTargetIORContactInfoList;
   protected ContactInfo primaryContactInfo;

   public CorbaContactInfoListImpl(ORB var1) {
      this.orb = var1;
   }

   public CorbaContactInfoListImpl(ORB var1, IOR var2) {
      this(var1);
      this.setTargetIOR(var2);
   }

   public synchronized Iterator iterator() {
      this.createContactInfoList();
      return new CorbaContactInfoListIteratorImpl(this.orb, this, this.primaryContactInfo, this.effectiveTargetIORContactInfoList);
   }

   public synchronized void setTargetIOR(IOR var1) {
      this.targetIOR = var1;
      this.setEffectiveTargetIOR(var1);
   }

   public synchronized IOR getTargetIOR() {
      return this.targetIOR;
   }

   public synchronized void setEffectiveTargetIOR(IOR var1) {
      this.effectiveTargetIOR = var1;
      this.effectiveTargetIORContactInfoList = null;
      if (this.primaryContactInfo != null && this.orb.getORBData().getIIOPPrimaryToContactInfo() != null) {
         this.orb.getORBData().getIIOPPrimaryToContactInfo().reset(this.primaryContactInfo);
      }

      this.primaryContactInfo = null;
      this.setLocalSubcontract();
   }

   public synchronized IOR getEffectiveTargetIOR() {
      return this.effectiveTargetIOR;
   }

   public synchronized LocalClientRequestDispatcher getLocalClientRequestDispatcher() {
      return this.LocalClientRequestDispatcher;
   }

   public synchronized int hashCode() {
      return this.targetIOR.hashCode();
   }

   protected void createContactInfoList() {
      if (this.effectiveTargetIORContactInfoList == null) {
         this.effectiveTargetIORContactInfoList = new ArrayList();
         IIOPProfile var1 = this.effectiveTargetIOR.getProfile();
         String var2 = ((IIOPProfileTemplate)var1.getTaggedProfileTemplate()).getPrimaryAddress().getHost().toLowerCase();
         int var3 = ((IIOPProfileTemplate)var1.getTaggedProfileTemplate()).getPrimaryAddress().getPort();
         this.primaryContactInfo = this.createContactInfo("IIOP_CLEAR_TEXT", var2, var3);
         if (var1.isLocal()) {
            SharedCDRContactInfoImpl var4 = new SharedCDRContactInfoImpl(this.orb, this, this.effectiveTargetIOR, this.orb.getORBData().getGIOPAddressDisposition());
            this.effectiveTargetIORContactInfoList.add(var4);
         } else {
            this.addRemoteContactInfos(this.effectiveTargetIOR, this.effectiveTargetIORContactInfoList);
         }

      }
   }

   protected void addRemoteContactInfos(IOR var1, List var2) {
      List var4 = this.orb.getORBData().getIORToSocketInfo().getSocketInfo(var1);
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         SocketInfo var6 = (SocketInfo)var5.next();
         String var7 = var6.getType();
         String var8 = var6.getHost().toLowerCase();
         int var9 = var6.getPort();
         ContactInfo var3 = this.createContactInfo(var7, var8, var9);
         var2.add(var3);
      }

   }

   protected ContactInfo createContactInfo(String var1, String var2, int var3) {
      return new SocketOrChannelContactInfoImpl(this.orb, this, this.effectiveTargetIOR, this.orb.getORBData().getGIOPAddressDisposition(), var1, var2, var3);
   }

   protected void setLocalSubcontract() {
      if (!this.effectiveTargetIOR.getProfile().isLocal()) {
         this.LocalClientRequestDispatcher = new NotLocalLocalCRDImpl();
      } else {
         int var1 = this.effectiveTargetIOR.getProfile().getObjectKeyTemplate().getSubcontractId();
         LocalClientRequestDispatcherFactory var2 = this.orb.getRequestDispatcherRegistry().getLocalClientRequestDispatcherFactory(var1);
         this.LocalClientRequestDispatcher = var2.create(var1, this.effectiveTargetIOR);
      }
   }
}
