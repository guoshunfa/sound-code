package org.omg.CORBA_2_3.portable;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;

public abstract class OutputStream extends org.omg.CORBA.portable.OutputStream {
   private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowOutputStreamSubclass";
   private static final boolean allowSubclass = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         String var1 = System.getProperty("jdk.corba.allowOutputStreamSubclass");
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

   private OutputStream(Void var1) {
   }

   public OutputStream() {
      this(checkPermission());
   }

   public void write_value(Serializable var1) {
      throw new NO_IMPLEMENT();
   }

   public void write_value(Serializable var1, Class var2) {
      throw new NO_IMPLEMENT();
   }

   public void write_value(Serializable var1, String var2) {
      throw new NO_IMPLEMENT();
   }

   public void write_value(Serializable var1, BoxedValueHelper var2) {
      throw new NO_IMPLEMENT();
   }

   public void write_abstract_interface(Object var1) {
      throw new NO_IMPLEMENT();
   }
}
