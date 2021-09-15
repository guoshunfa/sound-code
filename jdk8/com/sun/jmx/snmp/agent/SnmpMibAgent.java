package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.ServiceNotFoundException;

public abstract class SnmpMibAgent implements SnmpMibAgentMBean, MBeanRegistration, Serializable {
   protected String mibName;
   protected MBeanServer server;
   private ObjectName adaptorName;
   private transient SnmpMibHandler adaptor;

   public abstract void init() throws IllegalAccessException;

   public abstract ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception;

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
   }

   public void postDeregister() {
   }

   public abstract void get(SnmpMibRequest var1) throws SnmpStatusException;

   public abstract void getNext(SnmpMibRequest var1) throws SnmpStatusException;

   public abstract void getBulk(SnmpMibRequest var1, int var2, int var3) throws SnmpStatusException;

   public abstract void set(SnmpMibRequest var1) throws SnmpStatusException;

   public abstract void check(SnmpMibRequest var1) throws SnmpStatusException;

   public abstract long[] getRootOid();

   public MBeanServer getMBeanServer() {
      return this.server;
   }

   public SnmpMibHandler getSnmpAdaptor() {
      return this.adaptor;
   }

   public void setSnmpAdaptor(SnmpMibHandler var1) {
      if (this.adaptor != null) {
         this.adaptor.removeMib(this);
      }

      this.adaptor = var1;
      if (this.adaptor != null) {
         this.adaptor.addMib(this);
      }

   }

   public void setSnmpAdaptor(SnmpMibHandler var1, SnmpOid[] var2) {
      if (this.adaptor != null) {
         this.adaptor.removeMib(this);
      }

      this.adaptor = var1;
      if (this.adaptor != null) {
         this.adaptor.addMib(this, var2);
      }

   }

   public void setSnmpAdaptor(SnmpMibHandler var1, String var2) {
      if (this.adaptor != null) {
         this.adaptor.removeMib(this, var2);
      }

      this.adaptor = var1;
      if (this.adaptor != null) {
         this.adaptor.addMib(this, var2);
      }

   }

   public void setSnmpAdaptor(SnmpMibHandler var1, String var2, SnmpOid[] var3) {
      if (this.adaptor != null) {
         this.adaptor.removeMib(this, var2);
      }

      this.adaptor = var1;
      if (this.adaptor != null) {
         this.adaptor.addMib(this, var2, var3);
      }

   }

   public ObjectName getSnmpAdaptorName() {
      return this.adaptorName;
   }

   public void setSnmpAdaptorName(ObjectName var1) throws InstanceNotFoundException, ServiceNotFoundException {
      if (this.server == null) {
         throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
      } else {
         if (this.adaptor != null) {
            this.adaptor.removeMib(this);
         }

         Object[] var2 = new Object[]{this};
         String[] var3 = new String[]{"com.sun.jmx.snmp.agent.SnmpMibAgent"};

         try {
            this.adaptor = (SnmpMibHandler)((SnmpMibHandler)this.server.invoke(var1, "addMib", var2, var3));
         } catch (InstanceNotFoundException var5) {
            throw new InstanceNotFoundException(var1.toString());
         } catch (ReflectionException var6) {
            throw new ServiceNotFoundException(var1.toString());
         } catch (MBeanException var7) {
         }

         this.adaptorName = var1;
      }
   }

   public void setSnmpAdaptorName(ObjectName var1, SnmpOid[] var2) throws InstanceNotFoundException, ServiceNotFoundException {
      if (this.server == null) {
         throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
      } else {
         if (this.adaptor != null) {
            this.adaptor.removeMib(this);
         }

         Object[] var3 = new Object[]{this, var2};
         String[] var4 = new String[]{"com.sun.jmx.snmp.agent.SnmpMibAgent", var2.getClass().getName()};

         try {
            this.adaptor = (SnmpMibHandler)((SnmpMibHandler)this.server.invoke(var1, "addMib", var3, var4));
         } catch (InstanceNotFoundException var6) {
            throw new InstanceNotFoundException(var1.toString());
         } catch (ReflectionException var7) {
            throw new ServiceNotFoundException(var1.toString());
         } catch (MBeanException var8) {
         }

         this.adaptorName = var1;
      }
   }

   public void setSnmpAdaptorName(ObjectName var1, String var2) throws InstanceNotFoundException, ServiceNotFoundException {
      if (this.server == null) {
         throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
      } else {
         if (this.adaptor != null) {
            this.adaptor.removeMib(this, var2);
         }

         Object[] var3 = new Object[]{this, var2};
         String[] var4 = new String[]{"com.sun.jmx.snmp.agent.SnmpMibAgent", "java.lang.String"};

         try {
            this.adaptor = (SnmpMibHandler)((SnmpMibHandler)this.server.invoke(var1, "addMib", var3, var4));
         } catch (InstanceNotFoundException var6) {
            throw new InstanceNotFoundException(var1.toString());
         } catch (ReflectionException var7) {
            throw new ServiceNotFoundException(var1.toString());
         } catch (MBeanException var8) {
         }

         this.adaptorName = var1;
      }
   }

   public void setSnmpAdaptorName(ObjectName var1, String var2, SnmpOid[] var3) throws InstanceNotFoundException, ServiceNotFoundException {
      if (this.server == null) {
         throw new ServiceNotFoundException(this.mibName + " is not registered in the MBean server");
      } else {
         if (this.adaptor != null) {
            this.adaptor.removeMib(this, var2);
         }

         Object[] var4 = new Object[]{this, var2, var3};
         String[] var5 = new String[]{"com.sun.jmx.snmp.agent.SnmpMibAgent", "java.lang.String", var3.getClass().getName()};

         try {
            this.adaptor = (SnmpMibHandler)((SnmpMibHandler)this.server.invoke(var1, "addMib", var4, var5));
         } catch (InstanceNotFoundException var7) {
            throw new InstanceNotFoundException(var1.toString());
         } catch (ReflectionException var8) {
            throw new ServiceNotFoundException(var1.toString());
         } catch (MBeanException var9) {
         }

         this.adaptorName = var1;
      }
   }

   public boolean getBindingState() {
      return this.adaptor != null;
   }

   public String getMibName() {
      return this.mibName;
   }

   public static SnmpMibRequest newMibRequest(SnmpPdu var0, Vector<SnmpVarBind> var1, int var2, Object var3) {
      return new SnmpMibRequestImpl((SnmpEngine)null, var0, var1, var2, var3, (String)null, 0, getSecurityModel(var2), (byte[])null, (byte[])null);
   }

   public static SnmpMibRequest newMibRequest(SnmpEngine var0, SnmpPdu var1, Vector<SnmpVarBind> var2, int var3, Object var4, String var5, int var6, int var7, byte[] var8, byte[] var9) {
      return new SnmpMibRequestImpl(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   void getBulkWithGetNext(SnmpMibRequest var1, int var2, int var3) throws SnmpStatusException {
      Vector var4 = var1.getSubList();
      int var5 = var4.size();
      int var6 = Math.max(Math.min(var2, var5), 0);
      int var7 = Math.max(var3, 0);
      int var10000 = var5 - var6;
      if (var5 != 0) {
         this.getNext(var1);
         Vector var9 = this.splitFrom(var4, var6);
         SnmpMibRequestImpl var10 = new SnmpMibRequestImpl(var1.getEngine(), var1.getPdu(), var9, 1, var1.getUserData(), var1.getPrincipal(), var1.getSecurityLevel(), var1.getSecurityModel(), var1.getContextName(), var1.getAccessContextName());

         for(int var11 = 2; var11 <= var7; ++var11) {
            this.getNext(var10);
            this.concatVector(var1, var9);
         }
      }

   }

   private Vector<SnmpVarBind> splitFrom(Vector<SnmpVarBind> var1, int var2) {
      int var3 = var1.size();
      Vector var4 = new Vector(var3 - var2);
      int var5 = var2;

      for(Enumeration var6 = var1.elements(); var6.hasMoreElements(); --var5) {
         SnmpVarBind var7 = (SnmpVarBind)var6.nextElement();
         if (var5 <= 0) {
            var4.addElement(new SnmpVarBind(var7.oid, var7.value));
         }
      }

      return var4;
   }

   private void concatVector(SnmpMibRequest var1, Vector<SnmpVarBind> var2) {
      Enumeration var3 = var2.elements();

      while(var3.hasMoreElements()) {
         SnmpVarBind var4 = (SnmpVarBind)var3.nextElement();
         var1.addVarBind(new SnmpVarBind(var4.oid, var4.value));
      }

   }

   private static int getSecurityModel(int var0) {
      switch(var0) {
      case 0:
         return 1;
      default:
         return 2;
      }
   }
}
