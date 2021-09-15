package com.sun.jmx.snmp.defaults;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class SnmpProperties {
   public static final String MLET_LIB_DIR = "jmx.mlet.library.dir";
   public static final String ACL_FILE = "jdmk.acl.file";
   public static final String SECURITY_FILE = "jdmk.security.file";
   public static final String UACL_FILE = "jdmk.uacl.file";
   public static final String MIB_CORE_FILE = "mibcore.file";
   public static final String JMX_SPEC_NAME = "jmx.specification.name";
   public static final String JMX_SPEC_VERSION = "jmx.specification.version";
   public static final String JMX_SPEC_VENDOR = "jmx.specification.vendor";
   public static final String JMX_IMPL_NAME = "jmx.implementation.name";
   public static final String JMX_IMPL_VENDOR = "jmx.implementation.vendor";
   public static final String JMX_IMPL_VERSION = "jmx.implementation.version";
   public static final String SSL_CIPHER_SUITE = "jdmk.ssl.cipher.suite.";

   private SnmpProperties() {
   }

   public static void load(String var0) throws IOException {
      Properties var1 = new Properties();
      FileInputStream var2 = new FileInputStream(var0);
      var1.load((InputStream)var2);
      var2.close();
      Enumeration var3 = var1.keys();

      while(var3.hasMoreElements()) {
         String var4 = (String)var3.nextElement();
         System.setProperty(var4, var1.getProperty(var4));
      }

   }
}
