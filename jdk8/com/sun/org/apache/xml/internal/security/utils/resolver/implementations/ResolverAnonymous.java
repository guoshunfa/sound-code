package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResolverAnonymous extends ResourceResolverSpi {
   private InputStream inStream = null;

   public boolean engineIsThreadSafe() {
      return true;
   }

   public ResolverAnonymous(String var1) throws FileNotFoundException, IOException {
      this.inStream = new FileInputStream(var1);
   }

   public ResolverAnonymous(InputStream var1) {
      this.inStream = var1;
   }

   public XMLSignatureInput engineResolveURI(ResourceResolverContext var1) {
      return new XMLSignatureInput(this.inStream);
   }

   public boolean engineCanResolveURI(ResourceResolverContext var1) {
      return var1.uriToResolve == null;
   }

   public String[] engineGetPropertyKeys() {
      return new String[0];
   }
}
