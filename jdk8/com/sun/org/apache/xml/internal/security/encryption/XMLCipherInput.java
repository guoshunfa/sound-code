package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public class XMLCipherInput {
   private static Logger logger = Logger.getLogger(XMLCipherInput.class.getName());
   private CipherData cipherData;
   private int mode;
   private boolean secureValidation;

   public XMLCipherInput(CipherData var1) throws XMLEncryptionException {
      this.cipherData = var1;
      this.mode = 2;
      if (this.cipherData == null) {
         throw new XMLEncryptionException("CipherData is null");
      }
   }

   public XMLCipherInput(EncryptedType var1) throws XMLEncryptionException {
      this.cipherData = var1 == null ? null : var1.getCipherData();
      this.mode = 2;
      if (this.cipherData == null) {
         throw new XMLEncryptionException("CipherData is null");
      }
   }

   public void setSecureValidation(boolean var1) {
      this.secureValidation = var1;
   }

   public byte[] getBytes() throws XMLEncryptionException {
      return this.mode == 2 ? this.getDecryptBytes() : null;
   }

   private byte[] getDecryptBytes() throws XMLEncryptionException {
      String var1 = null;
      if (this.cipherData.getDataType() == 2) {
         if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Found a reference type CipherData");
         }

         CipherReference var2 = this.cipherData.getCipherReference();
         Attr var3 = var2.getURIAsAttr();
         XMLSignatureInput var4 = null;

         try {
            ResourceResolver var5 = ResourceResolver.getInstance(var3, (String)null, this.secureValidation);
            var4 = var5.resolve(var3, (String)null, this.secureValidation);
         } catch (ResourceResolverException var10) {
            throw new XMLEncryptionException("empty", var10);
         }

         if (var4 != null) {
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, "Managed to resolve URI \"" + var2.getURI() + "\"");
            }
         } else if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Failed to resolve URI \"" + var2.getURI() + "\"");
         }

         Transforms var12 = var2.getTransforms();
         if (var12 != null) {
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, "Have transforms in cipher reference");
            }

            try {
               com.sun.org.apache.xml.internal.security.transforms.Transforms var6 = var12.getDSTransforms();
               var6.setSecureValidation(this.secureValidation);
               var4 = var6.performTransforms(var4);
            } catch (TransformationException var9) {
               throw new XMLEncryptionException("empty", var9);
            }
         }

         try {
            return var4.getBytes();
         } catch (IOException var7) {
            throw new XMLEncryptionException("empty", var7);
         } catch (CanonicalizationException var8) {
            throw new XMLEncryptionException("empty", var8);
         }
      } else if (this.cipherData.getDataType() == 1) {
         var1 = this.cipherData.getCipherValue().getValue();
         if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Encrypted octets:\n" + var1);
         }

         try {
            return Base64.decode(var1);
         } catch (Base64DecodingException var11) {
            throw new XMLEncryptionException("empty", var11);
         }
      } else {
         throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
      }
   }
}
