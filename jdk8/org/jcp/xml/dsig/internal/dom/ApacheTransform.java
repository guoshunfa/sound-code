package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ApacheTransform extends TransformService {
   private static Logger log;
   private Transform apacheTransform;
   protected Document ownerDoc;
   protected Element transformElem;
   protected TransformParameterSpec params;

   public final AlgorithmParameterSpec getParameterSpec() {
      return this.params;
   }

   public void init(XMLStructure var1, XMLCryptoContext var2) throws InvalidAlgorithmParameterException {
      if (var2 != null && !(var2 instanceof DOMCryptoContext)) {
         throw new ClassCastException("context must be of type DOMCryptoContext");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof javax.xml.crypto.dom.DOMStructure)) {
         throw new ClassCastException("parent must be of type DOMStructure");
      } else {
         this.transformElem = (Element)((javax.xml.crypto.dom.DOMStructure)var1).getNode();
         this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
      }
   }

   public void marshalParams(XMLStructure var1, XMLCryptoContext var2) throws MarshalException {
      if (var2 != null && !(var2 instanceof DOMCryptoContext)) {
         throw new ClassCastException("context must be of type DOMCryptoContext");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof javax.xml.crypto.dom.DOMStructure)) {
         throw new ClassCastException("parent must be of type DOMStructure");
      } else {
         this.transformElem = (Element)((javax.xml.crypto.dom.DOMStructure)var1).getNode();
         this.ownerDoc = DOMUtils.getOwnerDocument(this.transformElem);
      }
   }

   public Data transform(Data var1, XMLCryptoContext var2) throws TransformException {
      if (var1 == null) {
         throw new NullPointerException("data must not be null");
      } else {
         return this.transformIt(var1, var2, (OutputStream)null);
      }
   }

   public Data transform(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException {
      if (var1 == null) {
         throw new NullPointerException("data must not be null");
      } else if (var3 == null) {
         throw new NullPointerException("output stream must not be null");
      } else {
         return this.transformIt(var1, var2, var3);
      }
   }

   private Data transformIt(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException {
      if (this.ownerDoc == null) {
         throw new TransformException("transform must be marshalled");
      } else {
         if (this.apacheTransform == null) {
            try {
               this.apacheTransform = new Transform(this.ownerDoc, this.getAlgorithm(), this.transformElem.getChildNodes());
               this.apacheTransform.setElement(this.transformElem, var2.getBaseURI());
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Created transform for algorithm: " + this.getAlgorithm());
               }
            } catch (Exception var8) {
               throw new TransformException("Couldn't find Transform for: " + this.getAlgorithm(), var8);
            }
         }

         if (Utils.secureValidation(var2)) {
            String var4 = this.getAlgorithm();
            if (Policy.restrictAlg(var4)) {
               throw new TransformException("Transform " + var4 + " is forbidden when secure validation is enabled");
            }
         }

         XMLSignatureInput var9;
         if (var1 instanceof ApacheData) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "ApacheData = true");
            }

            var9 = ((ApacheData)var1).getXMLSignatureInput();
         } else if (var1 instanceof NodeSetData) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "isNodeSet() = true");
            }

            if (var1 instanceof DOMSubTreeData) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "DOMSubTreeData = true");
               }

               DOMSubTreeData var5 = (DOMSubTreeData)var1;
               var9 = new XMLSignatureInput(var5.getRoot());
               var9.setExcludeComments(var5.excludeComments());
            } else {
               Set var10 = Utils.toNodeSet(((NodeSetData)var1).iterator());
               var9 = new XMLSignatureInput(var10);
            }
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "isNodeSet() = false");
            }

            try {
               var9 = new XMLSignatureInput(((OctetStreamData)var1).getOctetStream());
            } catch (Exception var7) {
               throw new TransformException(var7);
            }
         }

         try {
            if (var3 != null) {
               var9 = this.apacheTransform.performTransform(var9, var3);
               if (!var9.isNodeSet() && !var9.isElement()) {
                  return null;
               }
            } else {
               var9 = this.apacheTransform.performTransform(var9);
            }

            return (Data)(var9.isOctetStream() ? new ApacheOctetStreamData(var9) : new ApacheNodeSetData(var9));
         } catch (Exception var6) {
            throw new TransformException(var6);
         }
      }
   }

   public final boolean isFeatureSupported(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return false;
      }
   }

   static {
      Init.init();
      log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
   }
}
