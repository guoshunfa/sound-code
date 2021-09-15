package com.sun.management.jmx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** @deprecated */
@Deprecated
public class JMProperties {
   /** @deprecated */
   @Deprecated
   public static final String MLET_LIB_DIR = "jmx.mlet.library.dir";
   /** @deprecated */
   @Deprecated
   public static final String JMX_SPEC_NAME = "jmx.specification.name";
   /** @deprecated */
   @Deprecated
   public static final String JMX_SPEC_VERSION = "jmx.specification.version";
   /** @deprecated */
   @Deprecated
   public static final String JMX_SPEC_VENDOR = "jmx.specification.vendor";
   /** @deprecated */
   @Deprecated
   public static final String JMX_IMPL_NAME = "jmx.implementation.name";
   /** @deprecated */
   @Deprecated
   public static final String JMX_IMPL_VENDOR = "jmx.implementation.vendor";
   /** @deprecated */
   @Deprecated
   public static final String JMX_IMPL_VERSION = "jmx.implementation.version";

   private JMProperties() {
   }

   /** @deprecated */
   @Deprecated
   public static void load(String var0) throws IOException {
      Properties var1 = new Properties(System.getProperties());
      FileInputStream var2 = new FileInputStream(var0);
      var1.load((InputStream)var2);
      var2.close();
      System.setProperties(var1);
   }
}
