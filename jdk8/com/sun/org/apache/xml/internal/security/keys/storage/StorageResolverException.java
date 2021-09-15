package com.sun.org.apache.xml.internal.security.keys.storage;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class StorageResolverException extends XMLSecurityException {
   private static final long serialVersionUID = 1L;

   public StorageResolverException() {
   }

   public StorageResolverException(String var1) {
      super(var1);
   }

   public StorageResolverException(String var1, Object[] var2) {
      super(var1, var2);
   }

   public StorageResolverException(String var1, Exception var2) {
      super(var1, var2);
   }

   public StorageResolverException(String var1, Object[] var2, Exception var3) {
      super(var1, var2, var3);
   }
}
