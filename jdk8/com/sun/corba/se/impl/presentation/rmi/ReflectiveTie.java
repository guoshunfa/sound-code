package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public final class ReflectiveTie extends Servant implements Tie {
   private Remote target = null;
   private PresentationManager pm;
   private PresentationManager.ClassData classData = null;
   private ORBUtilSystemException wrapper = null;

   public ReflectiveTie(PresentationManager var1, ORBUtilSystemException var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(new DynamicAccessPermission("access"));
      }

      this.pm = var1;
      this.wrapper = var2;
   }

   public String[] _all_interfaces(POA var1, byte[] var2) {
      return this.classData.getTypeIds();
   }

   public void setTarget(Remote var1) {
      this.target = var1;
      if (var1 == null) {
         this.classData = null;
      } else {
         Class var2 = var1.getClass();
         this.classData = this.pm.getClassData(var2);
      }

   }

   public Remote getTarget() {
      return this.target;
   }

   public Object thisObject() {
      return this._this_object();
   }

   public void deactivate() {
      try {
         this._poa().deactivate_object(this._poa().servant_to_id(this));
      } catch (WrongPolicy var2) {
      } catch (ObjectNotActive var3) {
      } catch (ServantNotActive var4) {
      }

   }

   public ORB orb() {
      return this._orb();
   }

   public void orb(ORB var1) {
      try {
         com.sun.corba.se.spi.orb.ORB var2 = (com.sun.corba.se.spi.orb.ORB)var1;
         ((org.omg.CORBA_2_3.ORB)var1).set_delegate(this);
      } catch (ClassCastException var3) {
         throw this.wrapper.badOrbForServant((Throwable)var3);
      }
   }

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      Method var4 = null;
      DynamicMethodMarshaller var5 = null;

      try {
         org.omg.CORBA_2_3.portable.InputStream var6 = (org.omg.CORBA_2_3.portable.InputStream)var2;
         var4 = this.classData.getIDLNameTranslator().getMethod(var1);
         if (var4 == null) {
            throw this.wrapper.methodNotFoundInTie(var1, this.target.getClass().getName());
         } else {
            var5 = this.pm.getDynamicMethodMarshaller(var4);
            java.lang.Object[] var13 = var5.readArguments(var6);
            java.lang.Object var14 = var4.invoke(this.target, var13);
            org.omg.CORBA_2_3.portable.OutputStream var9 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
            var5.writeResult(var9, var14);
            return var9;
         }
      } catch (IllegalAccessException var10) {
         throw this.wrapper.invocationErrorInReflectiveTie((Throwable)var10, var4.getName(), var4.getDeclaringClass().getName());
      } catch (IllegalArgumentException var11) {
         throw this.wrapper.invocationErrorInReflectiveTie((Throwable)var11, var4.getName(), var4.getDeclaringClass().getName());
      } catch (InvocationTargetException var12) {
         Throwable var7 = var12.getCause();
         if (var7 instanceof SystemException) {
            throw (SystemException)var7;
         } else if (var7 instanceof Exception && var5.isDeclaredException(var7)) {
            org.omg.CORBA_2_3.portable.OutputStream var8 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
            var5.writeException(var8, (Exception)var7);
            return var8;
         } else {
            throw new UnknownException(var7);
         }
      }
   }
}
