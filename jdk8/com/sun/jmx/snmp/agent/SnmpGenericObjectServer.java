package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import java.util.Enumeration;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

public class SnmpGenericObjectServer {
   protected final MBeanServer server;

   public SnmpGenericObjectServer(MBeanServer var1) {
      this.server = var1;
   }

   public void get(SnmpGenericMetaServer var1, ObjectName var2, SnmpMibSubRequest var3, int var4) throws SnmpStatusException {
      int var5 = var3.getSize();
      Object var6 = var3.getUserData();
      String[] var7 = new String[var5];
      SnmpVarBind[] var8 = new SnmpVarBind[var5];
      long[] var9 = new long[var5];
      int var10 = 0;
      Enumeration var11 = var3.getElements();

      while(var11.hasMoreElements()) {
         SnmpVarBind var12 = (SnmpVarBind)var11.nextElement();

         try {
            long var13 = var12.oid.getOidArc(var4);
            var7[var10] = var1.getAttributeName(var13);
            var8[var10] = var12;
            var9[var10] = var13;
            var1.checkGetAccess(var13, var6);
            ++var10;
         } catch (SnmpStatusException var21) {
            var3.registerGetException(var12, var21);
         }
      }

      var11 = null;
      short var23 = 224;

      AttributeList var22;
      try {
         var22 = this.server.getAttributes(var2, var7);
      } catch (InstanceNotFoundException var18) {
         var22 = new AttributeList();
      } catch (ReflectionException var19) {
         var22 = new AttributeList();
      } catch (Exception var20) {
         var22 = new AttributeList();
      }

      Iterator var24 = var22.iterator();

      for(int var14 = 0; var14 < var10; ++var14) {
         if (!var24.hasNext()) {
            SnmpStatusException var15 = new SnmpStatusException(var23);
            var3.registerGetException(var8[var14], var15);
         } else {
            Attribute var25;
            for(var25 = (Attribute)var24.next(); var14 < var10 && !var7[var14].equals(var25.getName()); ++var14) {
               SnmpStatusException var16 = new SnmpStatusException(var23);
               var3.registerGetException(var8[var14], var16);
            }

            if (var14 == var10) {
               break;
            }

            try {
               var8[var14].value = var1.buildSnmpValue(var9[var14], var25.getValue());
            } catch (SnmpStatusException var17) {
               var3.registerGetException(var8[var14], var17);
            }
         }
      }

   }

   public SnmpValue get(SnmpGenericMetaServer var1, ObjectName var2, long var3, Object var5) throws SnmpStatusException {
      String var6 = var1.getAttributeName(var3);
      Object var7 = null;

      try {
         var7 = this.server.getAttribute(var2, var6);
      } catch (MBeanException var10) {
         Exception var9 = var10.getTargetException();
         if (var9 instanceof SnmpStatusException) {
            throw (SnmpStatusException)var9;
         }

         throw new SnmpStatusException(224);
      } catch (Exception var11) {
         throw new SnmpStatusException(224);
      }

      return var1.buildSnmpValue(var3, var7);
   }

   public void set(SnmpGenericMetaServer var1, ObjectName var2, SnmpMibSubRequest var3, int var4) throws SnmpStatusException {
      int var5 = var3.getSize();
      AttributeList var6 = new AttributeList(var5);
      String[] var7 = new String[var5];
      SnmpVarBind[] var8 = new SnmpVarBind[var5];
      long[] var9 = new long[var5];
      int var10 = 0;
      Enumeration var11 = var3.getElements();

      while(var11.hasMoreElements()) {
         SnmpVarBind var12 = (SnmpVarBind)var11.nextElement();

         try {
            long var13 = var12.oid.getOidArc(var4);
            String var15 = var1.getAttributeName(var13);
            Object var16 = var1.buildAttributeValue(var13, var12.value);
            Attribute var17 = new Attribute(var15, var16);
            var6.add(var17);
            var7[var10] = var15;
            var8[var10] = var12;
            var9[var10] = var13;
            ++var10;
         } catch (SnmpStatusException var22) {
            var3.registerSetException(var12, var22);
         }
      }

      byte var24 = 6;

      AttributeList var23;
      try {
         var23 = this.server.setAttributes(var2, var6);
      } catch (InstanceNotFoundException var19) {
         var23 = new AttributeList();
         var24 = 18;
      } catch (ReflectionException var20) {
         var24 = 18;
         var23 = new AttributeList();
      } catch (Exception var21) {
         var23 = new AttributeList();
      }

      Iterator var25 = var23.iterator();

      for(int var14 = 0; var14 < var10; ++var14) {
         if (!var25.hasNext()) {
            SnmpStatusException var26 = new SnmpStatusException(var24);
            var3.registerSetException(var8[var14], var26);
         } else {
            Attribute var27;
            for(var27 = (Attribute)var25.next(); var14 < var10 && !var7[var14].equals(var27.getName()); ++var14) {
               SnmpStatusException var28 = new SnmpStatusException(6);
               var3.registerSetException(var8[var14], var28);
            }

            if (var14 == var10) {
               break;
            }

            try {
               var8[var14].value = var1.buildSnmpValue(var9[var14], var27.getValue());
            } catch (SnmpStatusException var18) {
               var3.registerSetException(var8[var14], var18);
            }
         }
      }

   }

   public SnmpValue set(SnmpGenericMetaServer var1, ObjectName var2, SnmpValue var3, long var4, Object var6) throws SnmpStatusException {
      String var7 = var1.getAttributeName(var4);
      Object var8 = var1.buildAttributeValue(var4, var3);
      Attribute var9 = new Attribute(var7, var8);
      Object var10 = null;

      try {
         this.server.setAttribute(var2, var9);
         var10 = this.server.getAttribute(var2, var7);
      } catch (InvalidAttributeValueException var13) {
         throw new SnmpStatusException(10);
      } catch (InstanceNotFoundException var14) {
         throw new SnmpStatusException(18);
      } catch (ReflectionException var15) {
         throw new SnmpStatusException(18);
      } catch (MBeanException var16) {
         Exception var12 = var16.getTargetException();
         if (var12 instanceof SnmpStatusException) {
            throw (SnmpStatusException)var12;
         }

         throw new SnmpStatusException(6);
      } catch (Exception var17) {
         throw new SnmpStatusException(6);
      }

      return var1.buildSnmpValue(var4, var10);
   }

   public void check(SnmpGenericMetaServer var1, ObjectName var2, SnmpMibSubRequest var3, int var4) throws SnmpStatusException {
      Object var5 = var3.getUserData();
      Enumeration var6 = var3.getElements();

      while(var6.hasMoreElements()) {
         SnmpVarBind var7 = (SnmpVarBind)var6.nextElement();

         try {
            long var8 = var7.oid.getOidArc(var4);
            this.check(var1, var2, var7.value, var8, var5);
         } catch (SnmpStatusException var10) {
            var3.registerCheckException(var7, var10);
         }
      }

   }

   public void check(SnmpGenericMetaServer var1, ObjectName var2, SnmpValue var3, long var4, Object var6) throws SnmpStatusException {
      var1.checkSetAccess(var3, var4, var6);

      try {
         String var7 = var1.getAttributeName(var4);
         Object var16 = var1.buildAttributeValue(var4, var3);
         Object[] var9 = new Object[1];
         String[] var10 = new String[1];
         var9[0] = var16;
         var10[0] = var16.getClass().getName();
         this.server.invoke(var2, "check" + var7, var9, var10);
      } catch (SnmpStatusException var11) {
         throw var11;
      } catch (InstanceNotFoundException var12) {
         throw new SnmpStatusException(18);
      } catch (ReflectionException var13) {
      } catch (MBeanException var14) {
         Exception var8 = var14.getTargetException();
         if (var8 instanceof SnmpStatusException) {
            throw (SnmpStatusException)var8;
         }

         throw new SnmpStatusException(6);
      } catch (Exception var15) {
         throw new SnmpStatusException(6);
      }

   }

   public void registerTableEntry(SnmpMibTable var1, SnmpOid var2, ObjectName var3, Object var4) throws SnmpStatusException {
      if (var3 == null) {
         throw new SnmpStatusException(18);
      } else {
         try {
            if (var4 != null && !this.server.isRegistered(var3)) {
               this.server.registerMBean(var4, var3);
            }

         } catch (InstanceAlreadyExistsException var6) {
            throw new SnmpStatusException(18);
         } catch (MBeanRegistrationException var7) {
            throw new SnmpStatusException(6);
         } catch (NotCompliantMBeanException var8) {
            throw new SnmpStatusException(5);
         } catch (RuntimeOperationsException var9) {
            throw new SnmpStatusException(5);
         } catch (Exception var10) {
            throw new SnmpStatusException(5);
         }
      }
   }
}
