package com.sun.security.auth;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;
import sun.security.x509.X500Name;

/** @deprecated */
@Exported(false)
@Deprecated
public class X500Principal implements Principal, Serializable {
   private static final long serialVersionUID = -8222422609431628648L;
   private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
      public ResourceBundle run() {
         return ResourceBundle.getBundle("sun.security.util.AuthResources");
      }
   });
   private String name;
   private transient X500Name thisX500Name;

   public X500Principal(String var1) {
      if (var1 == null) {
         throw new NullPointerException(rb.getString("provided.null.name"));
      } else {
         try {
            this.thisX500Name = new X500Name(var1);
         } catch (Exception var3) {
            throw new IllegalArgumentException(var3.toString());
         }

         this.name = var1;
      }
   }

   public String getName() {
      return this.thisX500Name.getName();
   }

   public String toString() {
      return this.thisX500Name.toString();
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (var1 instanceof X500Principal) {
         X500Principal var2 = (X500Principal)var1;

         try {
            X500Name var3 = new X500Name(var2.getName());
            return this.thisX500Name.equals(var3);
         } catch (Exception var4) {
            return false;
         }
      } else {
         return var1 instanceof Principal ? var1.equals(this.thisX500Name) : false;
      }
   }

   public int hashCode() {
      return this.thisX500Name.hashCode();
   }

   private void readObject(ObjectInputStream var1) throws IOException, NotActiveException, ClassNotFoundException {
      var1.defaultReadObject();
      this.thisX500Name = new X500Name(this.name);
   }
}
