package org.omg.PortableServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.portable.Delegate;

public abstract class Servant {
   private transient Delegate _delegate = null;

   public final Delegate _get_delegate() {
      if (this._delegate == null) {
         throw new BAD_INV_ORDER("The Servant has not been associated with an ORB instance");
      } else {
         return this._delegate;
      }
   }

   public final void _set_delegate(Delegate var1) {
      this._delegate = var1;
   }

   public final Object _this_object() {
      return this._get_delegate().this_object(this);
   }

   public final Object _this_object(ORB var1) {
      try {
         ((org.omg.CORBA_2_3.ORB)var1).set_delegate(this);
      } catch (ClassCastException var3) {
         throw new BAD_PARAM("POA Servant requires an instance of org.omg.CORBA_2_3.ORB");
      }

      return this._this_object();
   }

   public final ORB _orb() {
      return this._get_delegate().orb(this);
   }

   public final POA _poa() {
      return this._get_delegate().poa(this);
   }

   public final byte[] _object_id() {
      return this._get_delegate().object_id(this);
   }

   public POA _default_POA() {
      return this._get_delegate().default_POA(this);
   }

   public boolean _is_a(String var1) {
      return this._get_delegate().is_a(this, var1);
   }

   public boolean _non_existent() {
      return this._get_delegate().non_existent(this);
   }

   public Object _get_interface_def() {
      Delegate var1 = this._get_delegate();

      try {
         return var1.get_interface_def(this);
      } catch (AbstractMethodError var9) {
         try {
            Class[] var3 = new Class[]{Servant.class};
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

   public abstract String[] _all_interfaces(POA var1, byte[] var2);
}
