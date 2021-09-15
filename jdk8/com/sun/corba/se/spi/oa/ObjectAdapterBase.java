package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public abstract class ObjectAdapterBase extends LocalObject implements ObjectAdapter {
   private ORB orb;
   private final POASystemException _iorWrapper;
   private final POASystemException _invocationWrapper;
   private final POASystemException _lifecycleWrapper;
   private final OMGSystemException _omgInvocationWrapper;
   private final OMGSystemException _omgLifecycleWrapper;
   private IORTemplate iortemp;
   private byte[] adapterId;
   private ObjectReferenceTemplate adapterTemplate;
   private ObjectReferenceFactory currentFactory;

   public ObjectAdapterBase(ORB var1) {
      this.orb = var1;
      this._iorWrapper = POASystemException.get(var1, "oa.ior");
      this._lifecycleWrapper = POASystemException.get(var1, "oa.lifecycle");
      this._omgLifecycleWrapper = OMGSystemException.get(var1, "oa.lifecycle");
      this._invocationWrapper = POASystemException.get(var1, "oa.invocation");
      this._omgInvocationWrapper = OMGSystemException.get(var1, "oa.invocation");
   }

   public final POASystemException iorWrapper() {
      return this._iorWrapper;
   }

   public final POASystemException lifecycleWrapper() {
      return this._lifecycleWrapper;
   }

   public final OMGSystemException omgLifecycleWrapper() {
      return this._omgLifecycleWrapper;
   }

   public final POASystemException invocationWrapper() {
      return this._invocationWrapper;
   }

   public final OMGSystemException omgInvocationWrapper() {
      return this._omgInvocationWrapper;
   }

   public final void initializeTemplate(ObjectKeyTemplate var1, boolean var2, Policies var3, String var4, String var5, ObjectAdapterId var6) {
      this.adapterId = var1.getAdapterId();
      this.iortemp = IORFactories.makeIORTemplate(var1);
      this.orb.getCorbaTransportManager().addToIORTemplate(this.iortemp, var3, var4, var5, var6);
      this.adapterTemplate = IORFactories.makeObjectReferenceTemplate(this.orb, this.iortemp);
      this.currentFactory = this.adapterTemplate;
      if (var2) {
         PIHandler var7 = this.orb.getPIHandler();
         if (var7 != null) {
            var7.objectAdapterCreated(this);
         }
      }

      this.iortemp.makeImmutable();
   }

   public final Object makeObject(String var1, byte[] var2) {
      return this.currentFactory.make_object(var1, var2);
   }

   public final byte[] getAdapterId() {
      return this.adapterId;
   }

   public final ORB getORB() {
      return this.orb;
   }

   public abstract Policy getEffectivePolicy(int var1);

   public final IORTemplate getIORTemplate() {
      return this.iortemp;
   }

   public abstract int getManagerId();

   public abstract short getState();

   public final ObjectReferenceTemplate getAdapterTemplate() {
      return this.adapterTemplate;
   }

   public final ObjectReferenceFactory getCurrentFactory() {
      return this.currentFactory;
   }

   public final void setCurrentFactory(ObjectReferenceFactory var1) {
      this.currentFactory = var1;
   }

   public abstract Object getLocalServant(byte[] var1);

   public abstract void getInvocationServant(OAInvocationInfo var1);

   public abstract void returnServant();

   public abstract void enter() throws OADestroyed;

   public abstract void exit();

   protected abstract ObjectCopierFactory getObjectCopierFactory();

   public OAInvocationInfo makeInvocationInfo(byte[] var1) {
      OAInvocationInfo var2 = new OAInvocationInfo(this, var1);
      var2.setCopierFactory(this.getObjectCopierFactory());
      return var2;
   }

   public abstract String[] getInterfaces(java.lang.Object var1, byte[] var2);
}
