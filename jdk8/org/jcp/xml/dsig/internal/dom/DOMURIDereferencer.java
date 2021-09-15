package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class DOMURIDereferencer implements URIDereferencer {
   static final URIDereferencer INSTANCE = new DOMURIDereferencer();

   private DOMURIDereferencer() {
      Init.init();
   }

   public Data dereference(URIReference var1, XMLCryptoContext var2) throws URIReferenceException {
      if (var1 == null) {
         throw new NullPointerException("uriRef cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("context cannot be null");
      } else {
         DOMURIReference var3 = (DOMURIReference)var1;
         Attr var4 = (Attr)var3.getHere();
         String var5 = var1.getURI();
         DOMCryptoContext var6 = (DOMCryptoContext)var2;
         String var7 = var2.getBaseURI();
         boolean var8 = Utils.secureValidation(var2);
         if (var8 && Policy.restrictReferenceUriScheme(var5)) {
            throw new URIReferenceException("Uri " + var5 + " is forbidden when secure validation is enabled");
         } else {
            if (var5 != null && var5.length() != 0 && var5.charAt(0) == '#') {
               String var9 = var5.substring(1);
               if (var9.startsWith("xpointer(id(")) {
                  int var10 = var9.indexOf(39);
                  int var11 = var9.indexOf(39, var10 + 1);
                  var9 = var9.substring(var10 + 1, var11);
               }

               Element var15 = var4.getOwnerDocument().getElementById(var9);
               if (var15 == null) {
                  var15 = var6.getElementById(var9);
               }

               if (var15 != null) {
                  if (var8 && Policy.restrictDuplicateIds()) {
                     Element var17 = var15.getOwnerDocument().getDocumentElement();
                     if (!XMLUtils.protectAgainstWrappingAttack(var17, (Element)var15, var9)) {
                        String var12 = "Multiple Elements with the same ID " + var9 + " detected when secure validation is enabled";
                        throw new URIReferenceException(var12);
                     }
                  }

                  XMLSignatureInput var18 = new XMLSignatureInput(var15);
                  if (!var5.substring(1).startsWith("xpointer(id(")) {
                     var18.setExcludeComments(true);
                  }

                  var18.setMIMEType("text/xml");
                  if (var7 != null && var7.length() > 0) {
                     var18.setSourceURI(var7.concat(var4.getNodeValue()));
                  } else {
                     var18.setSourceURI(var4.getNodeValue());
                  }

                  return new ApacheNodeSetData(var18);
               }
            }

            try {
               ResourceResolver var14 = ResourceResolver.getInstance(var4, var7, false);
               XMLSignatureInput var16 = var14.resolve(var4, var7, false);
               return (Data)(var16.isOctetStream() ? new ApacheOctetStreamData(var16) : new ApacheNodeSetData(var16));
            } catch (Exception var13) {
               throw new URIReferenceException(var13);
            }
         }
      }
   }
}
