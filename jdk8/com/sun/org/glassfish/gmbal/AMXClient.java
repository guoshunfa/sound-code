package com.sun.org.glassfish.gmbal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanInfo;

public class AMXClient implements AMXMBeanInterface {
   public static final ObjectName NULL_OBJECTNAME = makeObjectName("null:type=Null,name=Null");
   private MBeanServerConnection server;
   private ObjectName oname;

   private static ObjectName makeObjectName(String str) {
      try {
         return new ObjectName(str);
      } catch (MalformedObjectNameException var2) {
         return null;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof AMXClient)) {
         return false;
      } else {
         AMXClient other = (AMXClient)obj;
         return this.oname.equals(other.oname);
      }
   }

   public int hashCode() {
      int hash = 5;
      int hash = 47 * hash + (this.oname != null ? this.oname.hashCode() : 0);
      return hash;
   }

   public String toString() {
      return "AMXClient[" + this.oname + "]";
   }

   private <T> T fetchAttribute(String name, Class<T> type) {
      try {
         Object obj = this.server.getAttribute(this.oname, name);
         return NULL_OBJECTNAME.equals(obj) ? null : type.cast(obj);
      } catch (JMException var4) {
         throw new GmbalException("Exception in fetchAttribute", var4);
      } catch (IOException var5) {
         throw new GmbalException("Exception in fetchAttribute", var5);
      }
   }

   public AMXClient(MBeanServerConnection server, ObjectName oname) {
      this.server = server;
      this.oname = oname;
   }

   private AMXClient makeAMX(ObjectName on) {
      return on == null ? null : new AMXClient(this.server, on);
   }

   public String getName() {
      return (String)this.fetchAttribute("Name", String.class);
   }

   public Map<String, ?> getMeta() {
      try {
         ModelMBeanInfo mbi = (ModelMBeanInfo)this.server.getMBeanInfo(this.oname);
         Descriptor desc = mbi.getMBeanDescriptor();
         Map<String, Object> result = new HashMap();
         String[] var4 = desc.getFieldNames();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String str = var4[var6];
            result.put(str, desc.getFieldValue(str));
         }

         return result;
      } catch (MBeanException var8) {
         throw new GmbalException("Exception in getMeta", var8);
      } catch (RuntimeOperationsException var9) {
         throw new GmbalException("Exception in getMeta", var9);
      } catch (InstanceNotFoundException var10) {
         throw new GmbalException("Exception in getMeta", var10);
      } catch (IntrospectionException var11) {
         throw new GmbalException("Exception in getMeta", var11);
      } catch (ReflectionException var12) {
         throw new GmbalException("Exception in getMeta", var12);
      } catch (IOException var13) {
         throw new GmbalException("Exception in getMeta", var13);
      }
   }

   public AMXClient getParent() {
      ObjectName res = (ObjectName)this.fetchAttribute("Parent", ObjectName.class);
      return this.makeAMX(res);
   }

   public AMXClient[] getChildren() {
      ObjectName[] onames = (ObjectName[])this.fetchAttribute("Children", ObjectName[].class);
      return this.makeAMXArray(onames);
   }

   private AMXClient[] makeAMXArray(ObjectName[] onames) {
      AMXClient[] result = new AMXClient[onames.length];
      int ctr = 0;
      ObjectName[] var4 = onames;
      int var5 = onames.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ObjectName on = var4[var6];
         result[ctr++] = this.makeAMX(on);
      }

      return result;
   }

   public Object getAttribute(String attribute) {
      try {
         return this.server.getAttribute(this.oname, attribute);
      } catch (MBeanException var3) {
         throw new GmbalException("Exception in getAttribute", var3);
      } catch (AttributeNotFoundException var4) {
         throw new GmbalException("Exception in getAttribute", var4);
      } catch (ReflectionException var5) {
         throw new GmbalException("Exception in getAttribute", var5);
      } catch (InstanceNotFoundException var6) {
         throw new GmbalException("Exception in getAttribute", var6);
      } catch (IOException var7) {
         throw new GmbalException("Exception in getAttribute", var7);
      }
   }

   public void setAttribute(String name, Object value) {
      Attribute attr = new Attribute(name, value);
      this.setAttribute(attr);
   }

   public void setAttribute(Attribute attribute) {
      try {
         this.server.setAttribute(this.oname, attribute);
      } catch (InstanceNotFoundException var3) {
         throw new GmbalException("Exception in setAttribute", var3);
      } catch (AttributeNotFoundException var4) {
         throw new GmbalException("Exception in setAttribute", var4);
      } catch (InvalidAttributeValueException var5) {
         throw new GmbalException("Exception in setAttribute", var5);
      } catch (MBeanException var6) {
         throw new GmbalException("Exception in setAttribute", var6);
      } catch (ReflectionException var7) {
         throw new GmbalException("Exception in setAttribute", var7);
      } catch (IOException var8) {
         throw new GmbalException("Exception in setAttribute", var8);
      }
   }

   public AttributeList getAttributes(String[] attributes) {
      try {
         return this.server.getAttributes(this.oname, attributes);
      } catch (InstanceNotFoundException var3) {
         throw new GmbalException("Exception in getAttributes", var3);
      } catch (ReflectionException var4) {
         throw new GmbalException("Exception in getAttributes", var4);
      } catch (IOException var5) {
         throw new GmbalException("Exception in getAttributes", var5);
      }
   }

   public AttributeList setAttributes(AttributeList attributes) {
      try {
         return this.server.setAttributes(this.oname, attributes);
      } catch (InstanceNotFoundException var3) {
         throw new GmbalException("Exception in setAttributes", var3);
      } catch (ReflectionException var4) {
         throw new GmbalException("Exception in setAttributes", var4);
      } catch (IOException var5) {
         throw new GmbalException("Exception in setAttributes", var5);
      }
   }

   public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
      try {
         return this.server.invoke(this.oname, actionName, params, signature);
      } catch (InstanceNotFoundException var5) {
         throw new GmbalException("Exception in invoke", var5);
      } catch (IOException var6) {
         throw new GmbalException("Exception in invoke", var6);
      }
   }

   public MBeanInfo getMBeanInfo() {
      try {
         return this.server.getMBeanInfo(this.oname);
      } catch (InstanceNotFoundException var2) {
         throw new GmbalException("Exception in invoke", var2);
      } catch (IntrospectionException var3) {
         throw new GmbalException("Exception in invoke", var3);
      } catch (ReflectionException var4) {
         throw new GmbalException("Exception in invoke", var4);
      } catch (IOException var5) {
         throw new GmbalException("Exception in invoke", var5);
      }
   }

   public ObjectName objectName() {
      return this.oname;
   }
}
