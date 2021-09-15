package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

public class SnmpAcl implements InetAddressAcl, Serializable {
   private static final long serialVersionUID = -6702287103824397063L;
   static final PermissionImpl READ = new PermissionImpl("READ");
   static final PermissionImpl WRITE = new PermissionImpl("WRITE");
   private AclImpl acl;
   private boolean alwaysAuthorized;
   private String authorizedListFile;
   private Hashtable<InetAddress, Vector<String>> trapDestList;
   private Hashtable<InetAddress, Vector<String>> informDestList;
   private PrincipalImpl owner;

   public SnmpAcl(String var1) throws UnknownHostException, IllegalArgumentException {
      this(var1, (String)null);
   }

   public SnmpAcl(String var1, String var2) throws UnknownHostException, IllegalArgumentException {
      this.acl = null;
      this.alwaysAuthorized = false;
      this.authorizedListFile = null;
      this.trapDestList = null;
      this.informDestList = null;
      this.owner = null;
      this.trapDestList = new Hashtable();
      this.informDestList = new Hashtable();
      this.owner = new PrincipalImpl();

      try {
         this.acl = new AclImpl(this.owner, var1);
         AclEntryImpl var3 = new AclEntryImpl(this.owner);
         var3.addPermission(READ);
         var3.addPermission(WRITE);
         this.acl.addEntry(this.owner, var3);
      } catch (NotOwnerException var4) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "SnmpAcl(String,String)", "Should never get NotOwnerException as the owner is built in this constructor");
         }
      }

      if (var2 == null) {
         this.setDefaultFileName();
      } else {
         this.setAuthorizedListFile(var2);
      }

      this.readAuthorizedListFile();
   }

   public Enumeration<AclEntry> entries() {
      return this.acl.entries();
   }

   public Enumeration<String> communities() {
      HashSet var1 = new HashSet();
      Vector var2 = new Vector();
      Enumeration var3 = this.acl.entries();

      while(var3.hasMoreElements()) {
         AclEntryImpl var4 = (AclEntryImpl)var3.nextElement();
         Enumeration var5 = var4.communities();

         while(var5.hasMoreElements()) {
            var1.add(var5.nextElement());
         }
      }

      String[] var6 = (String[])var1.toArray(new String[0]);

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var2.addElement(var6[var7]);
      }

      return var2.elements();
   }

   public String getName() {
      return this.acl.getName();
   }

   public static PermissionImpl getREAD() {
      return READ;
   }

   public static PermissionImpl getWRITE() {
      return WRITE;
   }

   public static String getDefaultAclFileName() {
      String var0 = System.getProperty("file.separator");
      StringBuffer var1 = (new StringBuffer(System.getProperty("java.home"))).append(var0).append("lib").append(var0).append("snmp.acl");
      return var1.toString();
   }

   public void setAuthorizedListFile(String var1) throws IllegalArgumentException {
      File var2 = new File(var1);
      if (!var2.isFile()) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "setAuthorizedListFile", "ACL file not found: " + var1);
         }

         throw new IllegalArgumentException("The specified file [" + var2 + "] doesn't exist or is not a file, no configuration loaded");
      } else {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "setAuthorizedListFile", "Default file set to " + var1);
         }

         this.authorizedListFile = var1;
      }
   }

   public void rereadTheFile() throws NotOwnerException, UnknownHostException {
      this.alwaysAuthorized = false;
      this.acl.removeAll(this.owner);
      this.trapDestList.clear();
      this.informDestList.clear();
      AclEntryImpl var1 = new AclEntryImpl(this.owner);
      var1.addPermission(READ);
      var1.addPermission(WRITE);
      this.acl.addEntry(this.owner, var1);
      this.readAuthorizedListFile();
   }

   public String getAuthorizedListFile() {
      return this.authorizedListFile;
   }

   public boolean checkReadPermission(InetAddress var1) {
      if (this.alwaysAuthorized) {
         return true;
      } else {
         PrincipalImpl var2 = new PrincipalImpl(var1);
         return this.acl.checkPermission(var2, READ);
      }
   }

   public boolean checkReadPermission(InetAddress var1, String var2) {
      if (this.alwaysAuthorized) {
         return true;
      } else {
         PrincipalImpl var3 = new PrincipalImpl(var1);
         return this.acl.checkPermission(var3, var2, READ);
      }
   }

   public boolean checkCommunity(String var1) {
      return this.acl.checkCommunity(var1);
   }

   public boolean checkWritePermission(InetAddress var1) {
      if (this.alwaysAuthorized) {
         return true;
      } else {
         PrincipalImpl var2 = new PrincipalImpl(var1);
         return this.acl.checkPermission(var2, WRITE);
      }
   }

   public boolean checkWritePermission(InetAddress var1, String var2) {
      if (this.alwaysAuthorized) {
         return true;
      } else {
         PrincipalImpl var3 = new PrincipalImpl(var1);
         return this.acl.checkPermission(var3, var2, WRITE);
      }
   }

   public Enumeration<InetAddress> getTrapDestinations() {
      return this.trapDestList.keys();
   }

   public Enumeration<String> getTrapCommunities(InetAddress var1) {
      Vector var2 = null;
      if ((var2 = (Vector)this.trapDestList.get(var1)) != null) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getTrapCommunities", "[" + var1.toString() + "] is in list");
         }

         return var2.elements();
      } else {
         var2 = new Vector();
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getTrapCommunities", "[" + var1.toString() + "] is not in list");
         }

         return var2.elements();
      }
   }

   public Enumeration<InetAddress> getInformDestinations() {
      return this.informDestList.keys();
   }

   public Enumeration<String> getInformCommunities(InetAddress var1) {
      Vector var2 = null;
      if ((var2 = (Vector)this.informDestList.get(var1)) != null) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getInformCommunities", "[" + var1.toString() + "] is in list");
         }

         return var2.elements();
      } else {
         var2 = new Vector();
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getInformCommunities", "[" + var1.toString() + "] is not in list");
         }

         return var2.elements();
      }
   }

   private void readAuthorizedListFile() {
      this.alwaysAuthorized = false;
      if (this.authorizedListFile == null) {
         if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "alwaysAuthorized set to true");
         }

         this.alwaysAuthorized = true;
      } else {
         Parser var1 = null;

         try {
            var1 = new Parser(new FileInputStream(this.getAuthorizedListFile()));
         } catch (FileNotFoundException var6) {
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "The specified file was not found, authorize everybody");
            }

            this.alwaysAuthorized = true;
            return;
         }

         try {
            JDMSecurityDefs var2 = var1.SecurityDefs();
            var2.buildAclEntries(this.owner, this.acl);
            var2.buildTrapEntries(this.trapDestList);
            var2.buildInformEntries(this.informDestList);
         } catch (ParseException var7) {
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", (String)"Got parsing exception", (Throwable)var7);
            }

            throw new IllegalArgumentException(var7.getMessage());
         } catch (Error var8) {
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", (String)"Got unexpected error", (Throwable)var8);
            }

            throw new IllegalArgumentException(var8.getMessage());
         }

         Enumeration var9 = this.acl.entries();

         while(var9.hasMoreElements()) {
            AclEntryImpl var3 = (AclEntryImpl)var9.nextElement();
            if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "===> " + var3.getPrincipal().toString());
            }

            Enumeration var4 = var3.permissions();

            while(var4.hasMoreElements()) {
               Permission var5 = (Permission)var4.nextElement();
               if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "perm = " + var5);
               }
            }
         }
      }

   }

   private void setDefaultFileName() {
      try {
         this.setAuthorizedListFile(getDefaultAclFileName());
      } catch (IllegalArgumentException var2) {
      }

   }
}
