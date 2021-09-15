package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;

public class ResourceResolverException extends XMLSecurityException {
   private static final long serialVersionUID = 1L;
   private Attr uri = null;
   private String baseURI = null;

   public ResourceResolverException(String var1, Attr var2, String var3) {
      super(var1);
      this.uri = var2;
      this.baseURI = var3;
   }

   public ResourceResolverException(String var1, Object[] var2, Attr var3, String var4) {
      super(var1, var2);
      this.uri = var3;
      this.baseURI = var4;
   }

   public ResourceResolverException(String var1, Exception var2, Attr var3, String var4) {
      super(var1, var2);
      this.uri = var3;
      this.baseURI = var4;
   }

   public ResourceResolverException(String var1, Object[] var2, Exception var3, Attr var4, String var5) {
      super(var1, var2, var3);
      this.uri = var4;
      this.baseURI = var5;
   }

   public void setURI(Attr var1) {
      this.uri = var1;
   }

   public Attr getURI() {
      return this.uri;
   }

   public void setbaseURI(String var1) {
      this.baseURI = var1;
   }

   public String getbaseURI() {
      return this.baseURI;
   }
}
