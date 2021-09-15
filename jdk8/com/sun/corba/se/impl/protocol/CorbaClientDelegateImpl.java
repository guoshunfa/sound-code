package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class CorbaClientDelegateImpl extends CorbaClientDelegate {
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private CorbaContactInfoList contactInfoList;

   public CorbaClientDelegateImpl(ORB var1, CorbaContactInfoList var2) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.contactInfoList = var2;
   }

   public Broker getBroker() {
      return this.orb;
   }

   public ContactInfoList getContactInfoList() {
      return this.contactInfoList;
   }

   public OutputStream request(Object var1, String var2, boolean var3) {
      ClientInvocationInfo var4 = this.orb.createOrIncrementInvocationInfo();
      Iterator var5 = var4.getContactInfoListIterator();
      if (var5 == null) {
         var5 = this.contactInfoList.iterator();
         var4.setContactInfoListIterator(var5);
      }

      if (!var5.hasNext()) {
         throw ((CorbaContactInfoListIterator)var5).getFailureException();
      } else {
         CorbaContactInfo var6 = (CorbaContactInfo)var5.next();
         ClientRequestDispatcher var7 = var6.getClientRequestDispatcher();
         var4.setClientRequestDispatcher(var7);
         return (OutputStream)var7.beginRequest(var1, var2, !var3, var6);
      }
   }

   public InputStream invoke(Object var1, OutputStream var2) throws ApplicationException, RemarshalException {
      ClientRequestDispatcher var3 = this.getClientRequestDispatcher();
      return (InputStream)var3.marshalingComplete(var1, (OutputObject)var2);
   }

   public void releaseReply(Object var1, InputStream var2) {
      ClientRequestDispatcher var3 = this.getClientRequestDispatcher();
      var3.endRequest(this.orb, var1, (InputObject)var2);
      this.orb.releaseOrDecrementInvocationInfo();
   }

   private ClientRequestDispatcher getClientRequestDispatcher() {
      return ((CorbaInvocationInfo)this.orb.getInvocationInfo()).getClientRequestDispatcher();
   }

   public Object get_interface_def(Object var1) {
      InputStream var2 = null;
      Object var3 = null;

      try {
         Object var5;
         try {
            OutputStream var4 = this.request((Object)null, "_interface", true);
            var2 = this.invoke((Object)null, var4);
            var5 = var2.read_Object();
            if (!var5._is_a("IDL:omg.org/CORBA/InterfaceDef:1.0")) {
               throw this.wrapper.wrongInterfaceDef(CompletionStatus.COMPLETED_MAYBE);
            }

            try {
               var3 = (Object)JDKBridge.loadClass("org.omg.CORBA._InterfaceDefStub").newInstance();
            } catch (Exception var12) {
               throw this.wrapper.noInterfaceDefStub((Throwable)var12);
            }

            Delegate var6 = StubAdapter.getDelegate(var5);
            StubAdapter.setDelegate(var3, var6);
         } catch (ApplicationException var13) {
            throw this.wrapper.applicationExceptionInSpecialMethod((Throwable)var13);
         } catch (RemarshalException var14) {
            var5 = this.get_interface_def(var1);
            return var5;
         }
      } finally {
         this.releaseReply((Object)null, var2);
      }

      return var3;
   }

   public boolean is_a(Object var1, String var2) {
      String[] var3 = StubAdapter.getTypeIds(var1);
      String var4 = this.contactInfoList.getTargetIOR().getTypeId();
      if (var2.equals(var4)) {
         return true;
      } else {
         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var2.equals(var3[var5])) {
               return true;
            }
         }

         InputStream var15 = null;

         boolean var7;
         try {
            OutputStream var6 = this.request((Object)null, "_is_a", true);
            var6.write_string(var2);
            var15 = this.invoke((Object)null, var6);
            var7 = var15.read_boolean();
            return var7;
         } catch (ApplicationException var12) {
            throw this.wrapper.applicationExceptionInSpecialMethod((Throwable)var12);
         } catch (RemarshalException var13) {
            var7 = this.is_a(var1, var2);
         } finally {
            this.releaseReply((Object)null, var15);
         }

         return var7;
      }
   }

   public boolean non_existent(Object var1) {
      InputStream var2 = null;

      boolean var4;
      try {
         OutputStream var3 = this.request((Object)null, "_non_existent", true);
         var2 = this.invoke((Object)null, var3);
         var4 = var2.read_boolean();
         return var4;
      } catch (ApplicationException var9) {
         throw this.wrapper.applicationExceptionInSpecialMethod((Throwable)var9);
      } catch (RemarshalException var10) {
         var4 = this.non_existent(var1);
      } finally {
         this.releaseReply((Object)null, var2);
      }

      return var4;
   }

   public Object duplicate(Object var1) {
      return var1;
   }

   public void release(Object var1) {
   }

   public boolean is_equivalent(Object var1, Object var2) {
      if (var2 == null) {
         return false;
      } else if (!StubAdapter.isStub(var2)) {
         return false;
      } else {
         Delegate var3 = StubAdapter.getDelegate(var2);
         if (var3 == null) {
            return false;
         } else if (var3 == this) {
            return true;
         } else if (!(var3 instanceof CorbaClientDelegateImpl)) {
            return false;
         } else {
            CorbaClientDelegateImpl var4 = (CorbaClientDelegateImpl)var3;
            CorbaContactInfoList var5 = (CorbaContactInfoList)var4.getContactInfoList();
            return this.contactInfoList.getTargetIOR().isEquivalent(var5.getTargetIOR());
         }
      }
   }

   public boolean equals(Object var1, java.lang.Object var2) {
      if (var2 == null) {
         return false;
      } else if (!StubAdapter.isStub(var2)) {
         return false;
      } else {
         Delegate var3 = StubAdapter.getDelegate(var2);
         if (var3 == null) {
            return false;
         } else if (var3 instanceof CorbaClientDelegateImpl) {
            CorbaClientDelegateImpl var4 = (CorbaClientDelegateImpl)var3;
            IOR var5 = var4.contactInfoList.getTargetIOR();
            return this.contactInfoList.getTargetIOR().equals(var5);
         } else {
            return false;
         }
      }
   }

   public int hashCode(Object var1) {
      return this.hashCode();
   }

   public int hash(Object var1, int var2) {
      int var3 = this.hashCode();
      return var3 > var2 ? 0 : var3;
   }

   public Request request(Object var1, String var2) {
      return new RequestImpl(this.orb, var1, (Context)null, var2, (NVList)null, (NamedValue)null, (ExceptionList)null, (ContextList)null);
   }

   public Request create_request(Object var1, Context var2, String var3, NVList var4, NamedValue var5) {
      return new RequestImpl(this.orb, var1, var2, var3, var4, var5, (ExceptionList)null, (ContextList)null);
   }

   public Request create_request(Object var1, Context var2, String var3, NVList var4, NamedValue var5, ExceptionList var6, ContextList var7) {
      return new RequestImpl(this.orb, var1, var2, var3, var4, var5, var6, var7);
   }

   public org.omg.CORBA.ORB orb(Object var1) {
      return this.orb;
   }

   public boolean is_local(Object var1) {
      return this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal();
   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      return this.contactInfoList.getLocalClientRequestDispatcher().servant_preinvoke(var1, var2, var3);
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
      this.contactInfoList.getLocalClientRequestDispatcher().servant_postinvoke(var1, var2);
   }

   public String get_codebase(Object var1) {
      return this.contactInfoList.getTargetIOR() != null ? this.contactInfoList.getTargetIOR().getProfile().getCodebase() : null;
   }

   public String toString(Object var1) {
      return this.contactInfoList.getTargetIOR().stringify();
   }

   public int hashCode() {
      return this.contactInfoList.hashCode();
   }
}
