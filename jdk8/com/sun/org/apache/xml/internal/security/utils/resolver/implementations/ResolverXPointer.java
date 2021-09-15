package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ResolverXPointer extends ResourceResolverSpi {
   private static Logger log = Logger.getLogger(ResolverXPointer.class.getName());
   private static final String XP = "#xpointer(id(";
   private static final int XP_LENGTH = "#xpointer(id(".length();

   public boolean engineIsThreadSafe() {
      return true;
   }

   public XMLSignatureInput engineResolveURI(ResourceResolverContext var1) throws ResourceResolverException {
      Object var2 = null;
      Document var3 = var1.attr.getOwnerElement().getOwnerDocument();
      if (isXPointerSlash(var1.uriToResolve)) {
         var2 = var3;
      } else if (isXPointerId(var1.uriToResolve)) {
         String var4 = getXPointerId(var1.uriToResolve);
         var2 = var3.getElementById(var4);
         if (var1.secureValidation) {
            Element var5 = var1.attr.getOwnerDocument().getDocumentElement();
            if (!XMLUtils.protectAgainstWrappingAttack(var5, var4)) {
               Object[] var6 = new Object[]{var4};
               throw new ResourceResolverException("signature.Verification.MultipleIDs", var6, var1.attr, var1.baseUri);
            }
         }

         if (var2 == null) {
            Object[] var8 = new Object[]{var4};
            throw new ResourceResolverException("signature.Verification.MissingID", var8, var1.attr, var1.baseUri);
         }
      }

      XMLSignatureInput var7 = new XMLSignatureInput((Node)var2);
      var7.setMIMEType("text/xml");
      if (var1.baseUri != null && var1.baseUri.length() > 0) {
         var7.setSourceURI(var1.baseUri.concat(var1.uriToResolve));
      } else {
         var7.setSourceURI(var1.uriToResolve);
      }

      return var7;
   }

   public boolean engineCanResolveURI(ResourceResolverContext var1) {
      if (var1.uriToResolve == null) {
         return false;
      } else {
         return isXPointerSlash(var1.uriToResolve) || isXPointerId(var1.uriToResolve);
      }
   }

   private static boolean isXPointerSlash(String var0) {
      return var0.equals("#xpointer(/)");
   }

   private static boolean isXPointerId(String var0) {
      if (var0.startsWith("#xpointer(id(") && var0.endsWith("))")) {
         String var1 = var0.substring(XP_LENGTH, var0.length() - 2);
         int var2 = var1.length() - 1;
         if (var1.charAt(0) == '"' && var1.charAt(var2) == '"' || var1.charAt(0) == '\'' && var1.charAt(var2) == '\'') {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Id = " + var1.substring(1, var2));
            }

            return true;
         }
      }

      return false;
   }

   private static String getXPointerId(String var0) {
      if (var0.startsWith("#xpointer(id(") && var0.endsWith("))")) {
         String var1 = var0.substring(XP_LENGTH, var0.length() - 2);
         int var2 = var1.length() - 1;
         if (var1.charAt(0) == '"' && var1.charAt(var2) == '"' || var1.charAt(0) == '\'' && var1.charAt(var2) == '\'') {
            return var1.substring(1, var2);
         }
      }

      return null;
   }
}
