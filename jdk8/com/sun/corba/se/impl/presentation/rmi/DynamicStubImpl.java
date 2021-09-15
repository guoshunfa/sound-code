package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class DynamicStubImpl extends ObjectImpl implements DynamicStub, Serializable {
   private static final long serialVersionUID = 4852612040012087675L;
   private String[] typeIds;
   private StubIORImpl ior;
   private DynamicStub self = null;

   public void setSelf(DynamicStub var1) {
      this.self = var1;
   }

   public DynamicStub getSelf() {
      return this.self;
   }

   public DynamicStubImpl(String[] var1) {
      this.typeIds = var1;
      this.ior = null;
   }

   public void setDelegate(Delegate var1) {
      this._set_delegate(var1);
   }

   public Delegate getDelegate() {
      return this._get_delegate();
   }

   public ORB getORB() {
      return this._orb();
   }

   public String[] _ids() {
      return this.typeIds;
   }

   public String[] getTypeIds() {
      return this._ids();
   }

   public void connect(ORB var1) throws RemoteException {
      this.ior = StubConnectImpl.connect(this.ior, this.self, this, var1);
   }

   public boolean isLocal() {
      return this._is_local();
   }

   public OutputStream request(String var1, boolean var2) {
      return this._request(var1, var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.ior = new StubIORImpl();
      this.ior.doRead(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.ior == null) {
         this.ior = new StubIORImpl(this);
      }

      this.ior.doWrite(var1);
   }

   public Object readResolve() {
      String var1 = this.ior.getRepositoryId();
      String var2 = RepositoryId.cache.getId(var1).getClassName();
      Class var3 = null;

      try {
         var3 = JDKBridge.loadClass(var2, (String)null, (ClassLoader)null);
      } catch (ClassNotFoundException var7) {
      }

      PresentationManager var4 = com.sun.corba.se.spi.orb.ORB.getPresentationManager();
      PresentationManager.ClassData var5 = var4.getClassData(var3);
      InvocationHandlerFactoryImpl var6 = (InvocationHandlerFactoryImpl)var5.getInvocationHandlerFactory();
      return var6.getInvocationHandler(this);
   }
}
