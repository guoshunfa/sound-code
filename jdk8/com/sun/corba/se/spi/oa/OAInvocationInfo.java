package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class OAInvocationInfo extends ServantObject {
   private Object servantContainer;
   private ObjectAdapter oa;
   private byte[] oid;
   private CookieHolder cookieHolder;
   private String operation;
   private ObjectCopierFactory factory;

   public OAInvocationInfo(ObjectAdapter var1, byte[] var2) {
      this.oa = var1;
      this.oid = var2;
   }

   public OAInvocationInfo(OAInvocationInfo var1, String var2) {
      this.servant = var1.servant;
      this.servantContainer = var1.servantContainer;
      this.cookieHolder = var1.cookieHolder;
      this.oa = var1.oa;
      this.oid = var1.oid;
      this.factory = var1.factory;
      this.operation = var2;
   }

   public ObjectAdapter oa() {
      return this.oa;
   }

   public byte[] id() {
      return this.oid;
   }

   public Object getServantContainer() {
      return this.servantContainer;
   }

   public CookieHolder getCookieHolder() {
      if (this.cookieHolder == null) {
         this.cookieHolder = new CookieHolder();
      }

      return this.cookieHolder;
   }

   public String getOperation() {
      return this.operation;
   }

   public ObjectCopierFactory getCopierFactory() {
      return this.factory;
   }

   public void setOperation(String var1) {
      this.operation = var1;
   }

   public void setCopierFactory(ObjectCopierFactory var1) {
      this.factory = var1;
   }

   public void setServant(Object var1) {
      this.servantContainer = var1;
      if (var1 instanceof Tie) {
         this.servant = ((Tie)var1).getTarget();
      } else {
         this.servant = var1;
      }

   }
}
