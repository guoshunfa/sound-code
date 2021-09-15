package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ApacheCanonicalizer extends TransformService {
   private static Logger log;
   protected Canonicalizer apacheCanonicalizer;
   private Transform apacheTransform;
   protected String inclusiveNamespaces;
   protected C14NMethodParameterSpec params;
   protected Document ownerDoc;
   protected Element transformElem;

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

   public Data canonicalize(Data var1, XMLCryptoContext var2) throws TransformException {
      return this.canonicalize(var1, var2, (OutputStream)null);
   }

   public Data canonicalize(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException {
      if (this.apacheCanonicalizer == null) {
         try {
            this.apacheCanonicalizer = Canonicalizer.getInstance(this.getAlgorithm());
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Created canonicalizer for algorithm: " + this.getAlgorithm());
            }
         } catch (InvalidCanonicalizerException var8) {
            throw new TransformException("Couldn't find Canonicalizer for: " + this.getAlgorithm() + ": " + var8.getMessage(), var8);
         }
      }

      if (var3 != null) {
         this.apacheCanonicalizer.setWriter(var3);
      } else {
         this.apacheCanonicalizer.setWriter(new ByteArrayOutputStream());
      }

      try {
         Set var4 = null;
         if (var1 instanceof ApacheData) {
            XMLSignatureInput var5 = ((ApacheData)var1).getXMLSignatureInput();
            if (var5.isElement()) {
               if (this.inclusiveNamespaces != null) {
                  return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(var5.getSubNode(), this.inclusiveNamespaces)));
               }

               return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(var5.getSubNode())));
            }

            if (!var5.isNodeSet()) {
               return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(var5.getOctetStream()))));
            }

            var4 = var5.getNodeSet();
         } else {
            if (var1 instanceof DOMSubTreeData) {
               DOMSubTreeData var10 = (DOMSubTreeData)var1;
               if (this.inclusiveNamespaces != null) {
                  return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(var10.getRoot(), this.inclusiveNamespaces)));
               }

               return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeSubtree(var10.getRoot())));
            }

            if (!(var1 instanceof NodeSetData)) {
               return new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(((OctetStreamData)var1).getOctetStream()))));
            }

            NodeSetData var9 = (NodeSetData)var1;
            Set var6 = Utils.toNodeSet(var9.iterator());
            var4 = var6;
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Canonicalizing " + var6.size() + " nodes");
            }
         }

         return this.inclusiveNamespaces != null ? new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeXPathNodeSet(var4, this.inclusiveNamespaces))) : new OctetStreamData(new ByteArrayInputStream(this.apacheCanonicalizer.canonicalizeXPathNodeSet(var4)));
      } catch (Exception var7) {
         throw new TransformException(var7);
      }
   }

   public Data transform(Data var1, XMLCryptoContext var2, OutputStream var3) throws TransformException {
      if (var1 == null) {
         throw new NullPointerException("data must not be null");
      } else if (var3 == null) {
         throw new NullPointerException("output stream must not be null");
      } else if (this.ownerDoc == null) {
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

         XMLSignatureInput var4;
         if (var1 instanceof ApacheData) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "ApacheData = true");
            }

            var4 = ((ApacheData)var1).getXMLSignatureInput();
         } else if (var1 instanceof NodeSetData) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "isNodeSet() = true");
            }

            if (var1 instanceof DOMSubTreeData) {
               DOMSubTreeData var5 = (DOMSubTreeData)var1;
               var4 = new XMLSignatureInput(var5.getRoot());
               var4.setExcludeComments(var5.excludeComments());
            } else {
               Set var9 = Utils.toNodeSet(((NodeSetData)var1).iterator());
               var4 = new XMLSignatureInput(var9);
            }
         } else {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "isNodeSet() = false");
            }

            try {
               var4 = new XMLSignatureInput(((OctetStreamData)var1).getOctetStream());
            } catch (Exception var7) {
               throw new TransformException(var7);
            }
         }

         try {
            var4 = this.apacheTransform.performTransform(var4, var3);
            if (!var4.isNodeSet() && !var4.isElement()) {
               return null;
            } else {
               return (Data)(var4.isOctetStream() ? new ApacheOctetStreamData(var4) : new ApacheNodeSetData(var4));
            }
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
