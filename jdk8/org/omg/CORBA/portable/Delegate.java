package org.omg.CORBA.portable;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

public abstract class Delegate {
   public abstract Object get_interface_def(Object var1);

   public abstract Object duplicate(Object var1);

   public abstract void release(Object var1);

   public abstract boolean is_a(Object var1, String var2);

   public abstract boolean non_existent(Object var1);

   public abstract boolean is_equivalent(Object var1, Object var2);

   public abstract int hash(Object var1, int var2);

   public abstract Request request(Object var1, String var2);

   public abstract Request create_request(Object var1, Context var2, String var3, NVList var4, NamedValue var5);

   public abstract Request create_request(Object var1, Context var2, String var3, NVList var4, NamedValue var5, ExceptionList var6, ContextList var7);

   public ORB orb(Object var1) {
      throw new NO_IMPLEMENT();
   }

   public Policy get_policy(Object var1, int var2) {
      throw new NO_IMPLEMENT();
   }

   public DomainManager[] get_domain_managers(Object var1) {
      throw new NO_IMPLEMENT();
   }

   public Object set_policy_override(Object var1, Policy[] var2, SetOverrideType var3) {
      throw new NO_IMPLEMENT();
   }

   public boolean is_local(Object var1) {
      return false;
   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      return null;
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
   }

   public OutputStream request(Object var1, String var2, boolean var3) {
      throw new NO_IMPLEMENT();
   }

   public InputStream invoke(Object var1, OutputStream var2) throws ApplicationException, RemarshalException {
      throw new NO_IMPLEMENT();
   }

   public void releaseReply(Object var1, InputStream var2) {
      throw new NO_IMPLEMENT();
   }

   public String toString(Object var1) {
      return var1.getClass().getName() + ":" + this.toString();
   }

   public int hashCode(Object var1) {
      return System.identityHashCode(var1);
   }

   public boolean equals(Object var1, java.lang.Object var2) {
      return var1 == var2;
   }
}
