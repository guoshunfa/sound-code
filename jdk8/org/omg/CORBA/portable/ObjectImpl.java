package org.omg.CORBA.portable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.BAD_OPERATION;
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

public abstract class ObjectImpl implements Object {
   private transient Delegate __delegate;

   public Delegate _get_delegate() {
      if (this.__delegate == null) {
         throw new BAD_OPERATION("The delegate has not been set!");
      } else {
         return this.__delegate;
      }
   }

   public void _set_delegate(Delegate var1) {
      this.__delegate = var1;
   }

   public abstract String[] _ids();

   public Object _duplicate() {
      return this._get_delegate().duplicate(this);
   }

   public void _release() {
      this._get_delegate().release(this);
   }

   public boolean _is_a(String var1) {
      return this._get_delegate().is_a(this, var1);
   }

   public boolean _is_equivalent(Object var1) {
      return this._get_delegate().is_equivalent(this, var1);
   }

   public boolean _non_existent() {
      return this._get_delegate().non_existent(this);
   }

   public int _hash(int var1) {
      return this._get_delegate().hash(this, var1);
   }

   public Request _request(String var1) {
      return this._get_delegate().request(this, var1);
   }

   public Request _create_request(Context var1, String var2, NVList var3, NamedValue var4) {
      return this._get_delegate().create_request(this, var1, var2, var3, var4);
   }

   public Request _create_request(Context var1, String var2, NVList var3, NamedValue var4, ExceptionList var5, ContextList var6) {
      return this._get_delegate().create_request(this, var1, var2, var3, var4, var5, var6);
   }

   public Object _get_interface_def() {
      Delegate var1 = this._get_delegate();

      try {
         return var1.get_interface_def(this);
      } catch (NO_IMPLEMENT var9) {
         try {
            Class[] var3 = new Class[]{Object.class};
            Method var10 = var1.getClass().getMethod("get_interface", var3);
            java.lang.Object[] var5 = new java.lang.Object[]{this};
            return (Object)var10.invoke(var1, var5);
         } catch (InvocationTargetException var6) {
            Throwable var4 = var6.getTargetException();
            if (var4 instanceof Error) {
               throw (Error)var4;
            } else if (var4 instanceof RuntimeException) {
               throw (RuntimeException)var4;
            } else {
               throw new NO_IMPLEMENT();
            }
         } catch (RuntimeException var7) {
            throw var7;
         } catch (Exception var8) {
            throw new NO_IMPLEMENT();
         }
      }
   }

   public ORB _orb() {
      return this._get_delegate().orb(this);
   }

   public Policy _get_policy(int var1) {
      return this._get_delegate().get_policy(this, var1);
   }

   public DomainManager[] _get_domain_managers() {
      return this._get_delegate().get_domain_managers(this);
   }

   public Object _set_policy_override(Policy[] var1, SetOverrideType var2) {
      return this._get_delegate().set_policy_override(this, var1, var2);
   }

   public boolean _is_local() {
      return this._get_delegate().is_local(this);
   }

   public ServantObject _servant_preinvoke(String var1, Class var2) {
      return this._get_delegate().servant_preinvoke(this, var1, var2);
   }

   public void _servant_postinvoke(ServantObject var1) {
      this._get_delegate().servant_postinvoke(this, var1);
   }

   public OutputStream _request(String var1, boolean var2) {
      return this._get_delegate().request(this, var1, var2);
   }

   public InputStream _invoke(OutputStream var1) throws ApplicationException, RemarshalException {
      return this._get_delegate().invoke(this, var1);
   }

   public void _releaseReply(InputStream var1) {
      this._get_delegate().releaseReply(this, var1);
   }

   public String toString() {
      return this.__delegate != null ? this.__delegate.toString(this) : this.getClass().getName() + ": no delegate set";
   }

   public int hashCode() {
      return this.__delegate != null ? this.__delegate.hashCode(this) : super.hashCode();
   }

   public boolean equals(java.lang.Object var1) {
      if (this.__delegate != null) {
         return this.__delegate.equals(this, var1);
      } else {
         return this == var1;
      }
   }
}
