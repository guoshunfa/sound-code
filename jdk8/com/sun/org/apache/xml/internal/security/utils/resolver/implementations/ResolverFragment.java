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

public class ResolverFragment extends ResourceResolverSpi {
   private static Logger log = Logger.getLogger(ResolverFragment.class.getName());

   public boolean engineIsThreadSafe() {
      return true;
   }

   public XMLSignatureInput engineResolveURI(ResourceResolverContext var1) throws ResourceResolverException {
      Document var2 = var1.attr.getOwnerElement().getOwnerDocument();
      Object var3 = null;
      if (var1.uriToResolve.equals("")) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "ResolverFragment with empty URI (means complete document)");
         }

         var3 = var2;
      } else {
         String var4 = var1.uriToResolve.substring(1);
         var3 = var2.getElementById(var4);
         if (var3 == null) {
            Object[] var8 = new Object[]{var4};
            throw new ResourceResolverException("signature.Verification.MissingID", var8, var1.attr, var1.baseUri);
         }

         if (var1.secureValidation) {
            Element var5 = var1.attr.getOwnerDocument().getDocumentElement();
            if (!XMLUtils.protectAgainstWrappingAttack(var5, var4)) {
               Object[] var6 = new Object[]{var4};
               throw new ResourceResolverException("signature.Verification.MultipleIDs", var6, var1.attr, var1.baseUri);
            }
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Try to catch an Element with ID " + var4 + " and Element was " + var3);
         }
      }

      XMLSignatureInput var7 = new XMLSignatureInput((Node)var3);
      var7.setExcludeComments(true);
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
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Quick fail for null uri");
         }

         return false;
      } else if (var1.uriToResolve.equals("") || var1.uriToResolve.charAt(0) == '#' && !var1.uriToResolve.startsWith("#xpointer(")) {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "State I can resolve reference: \"" + var1.uriToResolve + "\"");
         }

         return true;
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Do not seem to be able to resolve reference: \"" + var1.uriToResolve + "\"");
         }

         return false;
      }
   }
}
