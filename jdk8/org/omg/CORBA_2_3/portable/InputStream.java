package org.omg.CORBA_2_3.portable;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;

public abstract class InputStream extends org.omg.CORBA.portable.InputStream {
   private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowInputStreamSubclass";
   private static final boolean allowSubclass = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         String var1 = System.getProperty("jdk.corba.allowInputStreamSubclass");
         return var1 == null ? false : !var1.equalsIgnoreCase("false");
      }
   });

   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null && !allowSubclass) {
         var0.checkPermission(new SerializablePermission("enableSubclassImplementation"));
      }

      return null;
   }

   private InputStream(Void var1) {
   }

   public InputStream() {
      this(checkPermission());
   }

   public Serializable read_value() {
      throw new NO_IMPLEMENT();
   }

   public Serializable read_value(Class var1) {
      throw new NO_IMPLEMENT();
   }

   public Serializable read_value(BoxedValueHelper var1) {
      throw new NO_IMPLEMENT();
   }

   public Serializable read_value(String var1) {
      throw new NO_IMPLEMENT();
   }

   public Serializable read_value(Serializable var1) {
      throw new NO_IMPLEMENT();
   }

   public Object read_abstract_interface() {
      throw new NO_IMPLEMENT();
   }

   public Object read_abstract_interface(Class var1) {
      throw new NO_IMPLEMENT();
   }
}
