package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.EmptyStackException;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.portable.Delegate;

public class DelegateImpl implements Delegate {
   private ORB orb;
   private POASystemException wrapper;
   private POAFactory factory;

   public DelegateImpl(ORB var1, POAFactory var2) {
      this.orb = var1;
      this.wrapper = POASystemException.get(var1, "oa");
      this.factory = var2;
   }

   public org.omg.CORBA.ORB orb(Servant var1) {
      return this.orb;
   }

   public Object this_object(Servant var1) {
      try {
         byte[] var2 = this.orb.peekInvocationInfo().id();
         POA var3 = (POA)this.orb.peekInvocationInfo().oa();
         String var4 = var1._all_interfaces(var3, var2)[0];
         return var3.create_reference_with_id(var2, var4);
      } catch (EmptyStackException var10) {
         POAImpl var5 = null;

         try {
            var5 = (POAImpl)var1._default_POA();
         } catch (ClassCastException var7) {
            throw this.wrapper.defaultPoaNotPoaimpl((Throwable)var7);
         }

         try {
            if (!var5.getPolicies().isImplicitlyActivated() && (!var5.getPolicies().isUniqueIds() || !var5.getPolicies().retainServants())) {
               throw this.wrapper.wrongPoliciesForThisObject();
            } else {
               return var5.servant_to_reference(var1);
            }
         } catch (ServantNotActive var8) {
            throw this.wrapper.thisObjectServantNotActive((Throwable)var8);
         } catch (WrongPolicy var9) {
            throw this.wrapper.thisObjectWrongPolicy((Throwable)var9);
         }
      } catch (ClassCastException var11) {
         throw this.wrapper.defaultPoaNotPoaimpl((Throwable)var11);
      }
   }

   public POA poa(Servant var1) {
      try {
         return (POA)this.orb.peekInvocationInfo().oa();
      } catch (EmptyStackException var4) {
         POA var3 = this.factory.lookupPOA(var1);
         if (var3 != null) {
            return var3;
         } else {
            throw this.wrapper.noContext((Throwable)var4);
         }
      }
   }

   public byte[] object_id(Servant var1) {
      try {
         return this.orb.peekInvocationInfo().id();
      } catch (EmptyStackException var3) {
         throw this.wrapper.noContext((Throwable)var3);
      }
   }

   public POA default_POA(Servant var1) {
      return this.factory.getRootPOA();
   }

   public boolean is_a(Servant var1, String var2) {
      String[] var3 = var1._all_interfaces(this.poa(var1), this.object_id(var1));

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var2.equals(var3[var4])) {
            return true;
         }
      }

      return false;
   }

   public boolean non_existent(Servant var1) {
      try {
         byte[] var2 = this.orb.peekInvocationInfo().id();
         return var2 == null;
      } catch (EmptyStackException var3) {
         throw this.wrapper.noContext((Throwable)var3);
      }
   }

   public Object get_interface_def(Servant var1) {
      throw this.wrapper.methodNotImplemented();
   }
}
