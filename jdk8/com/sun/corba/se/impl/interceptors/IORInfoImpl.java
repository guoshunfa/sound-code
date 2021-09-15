package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.legacy.interceptor.IORInfoExt;
import com.sun.corba.se.spi.legacy.interceptor.UnknownType;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public final class IORInfoImpl extends LocalObject implements IORInfo, IORInfoExt {
   private static final int STATE_INITIAL = 0;
   private static final int STATE_ESTABLISHED = 1;
   private static final int STATE_DONE = 2;
   private int state = 0;
   private ObjectAdapter adapter;
   private ORB orb;
   private ORBUtilSystemException orbutilWrapper;
   private InterceptorsSystemException wrapper;
   private OMGSystemException omgWrapper;

   IORInfoImpl(ObjectAdapter var1) {
      this.orb = var1.getORB();
      this.orbutilWrapper = ORBUtilSystemException.get(this.orb, "rpc.protocol");
      this.wrapper = InterceptorsSystemException.get(this.orb, "rpc.protocol");
      this.omgWrapper = OMGSystemException.get(this.orb, "rpc.protocol");
      this.adapter = var1;
   }

   public Policy get_effective_policy(int var1) {
      this.checkState(0, 1);
      return this.adapter.getEffectivePolicy(var1);
   }

   public void add_ior_component(TaggedComponent var1) {
      this.checkState(0);
      if (var1 == null) {
         this.nullParam();
      }

      this.addIORComponentToProfileInternal(var1, this.adapter.getIORTemplate().iterator());
   }

   public void add_ior_component_to_profile(TaggedComponent var1, int var2) {
      this.checkState(0);
      if (var1 == null) {
         this.nullParam();
      }

      this.addIORComponentToProfileInternal(var1, this.adapter.getIORTemplate().iteratorById(var2));
   }

   public int getServerPort(String var1) throws UnknownType {
      this.checkState(0, 1);
      int var2 = this.orb.getLegacyServerSocketManager().legacyGetTransientOrPersistentServerPort(var1);
      if (var2 == -1) {
         throw new UnknownType();
      } else {
         return var2;
      }
   }

   public ObjectAdapter getObjectAdapter() {
      return this.adapter;
   }

   public int manager_id() {
      this.checkState(0, 1);
      return this.adapter.getManagerId();
   }

   public short state() {
      this.checkState(0, 1);
      return this.adapter.getState();
   }

   public ObjectReferenceTemplate adapter_template() {
      this.checkState(1);
      return this.adapter.getAdapterTemplate();
   }

   public ObjectReferenceFactory current_factory() {
      this.checkState(1);
      return this.adapter.getCurrentFactory();
   }

   public void current_factory(ObjectReferenceFactory var1) {
      this.checkState(1);
      this.adapter.setCurrentFactory(var1);
   }

   private void addIORComponentToProfileInternal(TaggedComponent var1, Iterator var2) {
      TaggedComponentFactoryFinder var3 = this.orb.getTaggedComponentFactoryFinder();
      com.sun.corba.se.spi.ior.TaggedComponent var4 = var3.create(this.orb, var1);
      boolean var5 = false;

      while(var2.hasNext()) {
         var5 = true;
         TaggedProfileTemplate var6 = (TaggedProfileTemplate)var2.next();
         var6.add(var4);
      }

      if (!var5) {
         throw this.omgWrapper.invalidProfileId();
      }
   }

   private void nullParam() {
      throw this.orbutilWrapper.nullParam();
   }

   private void checkState(int var1) {
      if (var1 != this.state) {
         throw this.wrapper.badState1(new Integer(var1), new Integer(this.state));
      }
   }

   private void checkState(int var1, int var2) {
      if (var1 != this.state && var2 != this.state) {
         throw this.wrapper.badState2(new Integer(var1), new Integer(var2), new Integer(this.state));
      }
   }

   void makeStateEstablished() {
      this.checkState(0);
      this.state = 1;
   }

   void makeStateDone() {
      this.checkState(1);
      this.state = 2;
   }
}
