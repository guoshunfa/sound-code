package com.sun.org.apache.xml.internal.security.utils.resolver;

import org.w3c.dom.Attr;

public class ResourceResolverContext {
   public final String uriToResolve;
   public final boolean secureValidation;
   public final String baseUri;
   public final Attr attr;

   public ResourceResolverContext(Attr var1, String var2, boolean var3) {
      this.attr = var1;
      this.baseUri = var2;
      this.secureValidation = var3;
      this.uriToResolve = var1 != null ? var1.getValue() : null;
   }
}
