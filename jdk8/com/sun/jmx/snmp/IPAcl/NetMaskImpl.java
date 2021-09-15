package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

class NetMaskImpl extends PrincipalImpl implements Group, Serializable {
   private static final long serialVersionUID = -7332541893877932896L;
   protected byte[] subnet = null;
   protected int prefix = -1;

   public NetMaskImpl() throws UnknownHostException {
   }

   private byte[] extractSubNet(byte[] var1) {
      int var2 = var1.length;
      Object var3 = null;
      int var5;
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
         JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "BINARY ARRAY :");
         StringBuffer var4 = new StringBuffer();

         for(var5 = 0; var5 < var2; ++var5) {
            var4.append((var1[var5] & 255) + ":");
         }

         JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", var4.toString());
      }

      int var13 = this.prefix / 8;
      if (var13 == var2) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "The mask is the complete address, strange..." + var2);
         }

         return var1;
      } else if (var13 > var2) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "The number of covered byte is longer than the address. BUG");
         }

         throw new IllegalArgumentException("The number of covered byte is longer than the address.");
      } else {
         var5 = var13;
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Partially covered index : " + var13);
         }

         byte var6 = var1[var13];
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Partially covered byte : " + var6);
         }

         int var7 = this.prefix % 8;
         boolean var8 = false;
         int var14;
         if (var7 == 0) {
            var14 = var13;
         } else {
            var14 = var13 + 1;
         }

         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Remains : " + var7);
         }

         byte var9 = 0;

         for(int var10 = 0; var10 < var7; ++var10) {
            var9 = (byte)(var9 | 1 << 7 - var10);
         }

         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Mask value : " + (var9 & 255));
         }

         byte var15 = (byte)(var6 & var9);
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Masked byte : " + (var15 & 255));
         }

         byte[] var12 = new byte[var14];
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Resulting subnet : ");
         }

         for(int var11 = 0; var11 < var5; ++var11) {
            var12[var11] = var1[var11];
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", (var12[var11] & 255) + ":");
            }
         }

         if (var7 != 0) {
            var12[var5] = var15;
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Last subnet byte : " + (var12[var5] & 255));
            }
         }

         return var12;
      }
   }

   public NetMaskImpl(String var1, int var2) throws UnknownHostException {
      super(var1);
      this.prefix = var2;
      this.subnet = this.extractSubNet(this.getAddress().getAddress());
   }

   public boolean addMember(Principal var1) {
      return true;
   }

   public int hashCode() {
      return super.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof PrincipalImpl) && !(var1 instanceof NetMaskImpl)) {
         return false;
      } else {
         PrincipalImpl var2 = (PrincipalImpl)var1;
         InetAddress var3 = var2.getAddress();
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "Received Address : " + var3);
         }

         byte[] var4 = var3.getAddress();

         for(int var5 = 0; var5 < this.subnet.length; ++var5) {
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "(recAddr[i]) : " + (var4[var5] & 255));
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "(recAddr[i] & subnet[i]) : " + (var4[var5] & this.subnet[var5] & 255) + " subnet[i] : " + (this.subnet[var5] & 255));
            }

            if ((var4[var5] & this.subnet[var5]) != this.subnet[var5]) {
               if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "FALSE");
               }

               return false;
            }
         }

         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "TRUE");
         }

         return true;
      }
   }

   public boolean isMember(Principal var1) {
      return (var1.hashCode() & super.hashCode()) == var1.hashCode();
   }

   public Enumeration<? extends Principal> members() {
      Vector var1 = new Vector(1);
      var1.addElement(this);
      return var1.elements();
   }

   public boolean removeMember(Principal var1) {
      return true;
   }

   public String toString() {
      return "NetMaskImpl :" + super.getAddress().toString() + "/" + this.prefix;
   }
}
