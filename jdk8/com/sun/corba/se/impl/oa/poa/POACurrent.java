package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import java.util.EmptyStackException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.PortableServer.Current;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class POACurrent extends ObjectImpl implements Current {
   private ORB orb;
   private POASystemException wrapper;

   public POACurrent(ORB var1) {
      this.orb = var1;
      this.wrapper = POASystemException.get(var1, "oa.invocation");
   }

   public String[] _ids() {
      String[] var1 = new String[]{"IDL:omg.org/PortableServer/Current:1.0"};
      return var1;
   }

   public POA get_POA() throws NoContext {
      POA var1 = (POA)((POA)this.peekThrowNoContext().oa());
      this.throwNoContextIfNull(var1);
      return var1;
   }

   public byte[] get_object_id() throws NoContext {
      byte[] var1 = this.peekThrowNoContext().id();
      this.throwNoContextIfNull(var1);
      return var1;
   }

   public ObjectAdapter getOA() {
      ObjectAdapter var1 = this.peekThrowInternal().oa();
      this.throwInternalIfNull(var1);
      return var1;
   }

   public byte[] getObjectId() {
      byte[] var1 = this.peekThrowInternal().id();
      this.throwInternalIfNull(var1);
      return var1;
   }

   Servant getServant() {
      Servant var1 = (Servant)((Servant)this.peekThrowInternal().getServantContainer());
      return var1;
   }

   CookieHolder getCookieHolder() {
      CookieHolder var1 = this.peekThrowInternal().getCookieHolder();
      this.throwInternalIfNull(var1);
      return var1;
   }

   public String getOperation() {
      String var1 = this.peekThrowInternal().getOperation();
      this.throwInternalIfNull(var1);
      return var1;
   }

   void setServant(Servant var1) {
      this.peekThrowInternal().setServant(var1);
   }

   private OAInvocationInfo peekThrowNoContext() throws NoContext {
      OAInvocationInfo var1 = null;

      try {
         var1 = this.orb.peekInvocationInfo();
         return var1;
      } catch (EmptyStackException var3) {
         throw new NoContext();
      }
   }

   private OAInvocationInfo peekThrowInternal() {
      OAInvocationInfo var1 = null;

      try {
         var1 = this.orb.peekInvocationInfo();
         return var1;
      } catch (EmptyStackException var3) {
         throw this.wrapper.poacurrentUnbalancedStack((Throwable)var3);
      }
   }

   private void throwNoContextIfNull(Object var1) throws NoContext {
      if (var1 == null) {
         throw new NoContext();
      }
   }

   private void throwInternalIfNull(Object var1) {
      if (var1 == null) {
         throw this.wrapper.poacurrentNullField(CompletionStatus.COMPLETED_MAYBE);
      }
   }
}
