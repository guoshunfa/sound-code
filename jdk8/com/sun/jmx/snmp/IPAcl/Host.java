package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.NotOwnerException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

abstract class Host extends SimpleNode implements Serializable {
   public Host(int var1) {
      super(var1);
   }

   public Host(Parser var1, int var2) {
      super(var1, var2);
   }

   protected abstract PrincipalImpl createAssociatedPrincipal() throws UnknownHostException;

   protected abstract String getHname();

   public void buildAclEntries(PrincipalImpl var1, AclImpl var2) {
      PrincipalImpl var3 = null;

      try {
         var3 = this.createAssociatedPrincipal();
      } catch (UnknownHostException var6) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", (String)"Cannot create ACL entry; got exception", (Throwable)var6);
         }

         throw new IllegalArgumentException("Cannot create ACL entry for " + var6.getMessage());
      }

      AclEntryImpl var4 = null;

      try {
         var4 = new AclEntryImpl(var3);
         this.registerPermission(var4);
         var2.addEntry(var1, var4);
      } catch (UnknownHostException var7) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", (String)"Cannot create ACL entry; got exception", (Throwable)var7);
         }

      } catch (NotOwnerException var8) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", (String)"Cannot create ACL entry; got exception", (Throwable)var8);
         }

      }
   }

   private void registerPermission(AclEntryImpl var1) {
      JDMHost var2 = (JDMHost)this.jjtGetParent();
      JDMManagers var3 = (JDMManagers)var2.jjtGetParent();
      JDMAclItem var4 = (JDMAclItem)var3.jjtGetParent();
      JDMAccess var5 = var4.getAccess();
      var5.putPermission(var1);
      JDMCommunities var6 = var4.getCommunities();
      var6.buildCommunities(var1);
   }

   public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> var1) {
      JDMHostTrap var2 = (JDMHostTrap)this.jjtGetParent();
      JDMTrapInterestedHost var3 = (JDMTrapInterestedHost)var2.jjtGetParent();
      JDMTrapItem var4 = (JDMTrapItem)var3.jjtGetParent();
      JDMTrapCommunity var5 = var4.getCommunity();
      String var6 = var5.getCommunity();
      InetAddress var7 = null;

      try {
         var7 = InetAddress.getByName(this.getHname());
      } catch (UnknownHostException var9) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildTrapEntries", (String)"Cannot create TRAP entry; got exception", (Throwable)var9);
         }

         return;
      }

      Vector var8 = null;
      if (var1.containsKey(var7)) {
         var8 = (Vector)var1.get(var7);
         if (!var8.contains(var6)) {
            var8.addElement(var6);
         }
      } else {
         var8 = new Vector();
         var8.addElement(var6);
         var1.put(var7, var8);
      }

   }

   public void buildInformEntries(Hashtable<InetAddress, Vector<String>> var1) {
      JDMHostInform var2 = (JDMHostInform)this.jjtGetParent();
      JDMInformInterestedHost var3 = (JDMInformInterestedHost)var2.jjtGetParent();
      JDMInformItem var4 = (JDMInformItem)var3.jjtGetParent();
      JDMInformCommunity var5 = var4.getCommunity();
      String var6 = var5.getCommunity();
      InetAddress var7 = null;

      try {
         var7 = InetAddress.getByName(this.getHname());
      } catch (UnknownHostException var9) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildTrapEntries", (String)"Cannot create INFORM entry; got exception", (Throwable)var9);
         }

         return;
      }

      Vector var8 = null;
      if (var1.containsKey(var7)) {
         var8 = (Vector)var1.get(var7);
         if (!var8.contains(var6)) {
            var8.addElement(var6);
         }
      } else {
         var8 = new Vector();
         var8.addElement(var6);
         var1.put(var7, var8);
      }

   }
}
