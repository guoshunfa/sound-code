package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;
import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientDelegate;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.portable.Delegate;
import org.omg.PortableInterceptor.ObjectReferenceFactory;

public class TOAImpl extends ObjectAdapterBase implements TOA {
   private TransientObjectManager servants;

   public TOAImpl(ORB var1, TransientObjectManager var2, String var3) {
      super(var1);
      this.servants = var2;
      int var4 = this.getORB().getTransientServerId();
      byte var5 = 2;
      JIDLObjectKeyTemplate var6 = new JIDLObjectKeyTemplate(var1, var5, var4);
      Policies var7 = Policies.defaultPolicies;
      this.initializeTemplate(var6, true, var7, var3, (String)null, var6.getObjectAdapterId());
   }

   public ObjectCopierFactory getObjectCopierFactory() {
      CopierManager var1 = this.getORB().getCopierManager();
      return var1.getDefaultObjectCopierFactory();
   }

   public Object getLocalServant(byte[] var1) {
      return (Object)((Object)this.servants.lookupServant(var1));
   }

   public void getInvocationServant(OAInvocationInfo var1) {
      java.lang.Object var2 = this.servants.lookupServant(var1.id());
      if (var2 == null) {
         var2 = new NullServantImpl(this.lifecycleWrapper().nullServant());
      }

      var1.setServant(var2);
   }

   public void returnServant() {
   }

   public String[] getInterfaces(java.lang.Object var1, byte[] var2) {
      return StubAdapter.getTypeIds(var1);
   }

   public Policy getEffectivePolicy(int var1) {
      return null;
   }

   public int getManagerId() {
      return -1;
   }

   public short getState() {
      return 1;
   }

   public void enter() throws OADestroyed {
   }

   public void exit() {
   }

   public void connect(Object var1) {
      byte[] var2 = this.servants.storeServant(var1, (java.lang.Object)null);
      String var3 = StubAdapter.getTypeIds(var1)[0];
      ObjectReferenceFactory var4 = this.getCurrentFactory();
      Object var5 = var4.make_object(var3, var2);
      Delegate var6 = StubAdapter.getDelegate(var5);
      CorbaContactInfoList var7 = (CorbaContactInfoList)((ClientDelegate)var6).getContactInfoList();
      LocalClientRequestDispatcher var8 = var7.getLocalClientRequestDispatcher();
      if (var8 instanceof JIDLLocalCRDImpl) {
         JIDLLocalCRDImpl var9 = (JIDLLocalCRDImpl)var8;
         var9.setServant(var1);
         StubAdapter.setDelegate(var1, var6);
      } else {
         throw new RuntimeException("TOAImpl.connect can not be called on " + var8);
      }
   }

   public void disconnect(Object var1) {
      Delegate var2 = StubAdapter.getDelegate(var1);
      CorbaContactInfoList var3 = (CorbaContactInfoList)((ClientDelegate)var2).getContactInfoList();
      LocalClientRequestDispatcher var4 = var3.getLocalClientRequestDispatcher();
      if (var4 instanceof JIDLLocalCRDImpl) {
         JIDLLocalCRDImpl var5 = (JIDLLocalCRDImpl)var4;
         byte[] var6 = var5.getObjectId();
         this.servants.deleteServant(var6);
         var5.unexport();
      } else {
         throw new RuntimeException("TOAImpl.disconnect can not be called on " + var4);
      }
   }
}
