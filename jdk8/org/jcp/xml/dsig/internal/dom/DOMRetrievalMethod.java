package org.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMRetrievalMethod extends DOMStructure implements RetrievalMethod, DOMURIReference {
   private final List<Transform> transforms;
   private String uri;
   private String type;
   private Attr here;

   public DOMRetrievalMethod(String var1, String var2, List<? extends Transform> var3) {
      if (var1 == null) {
         throw new NullPointerException("uri cannot be null");
      } else {
         if (var3 != null && !var3.isEmpty()) {
            this.transforms = Collections.unmodifiableList(new ArrayList(var3));
            int var4 = 0;

            for(int var5 = this.transforms.size(); var4 < var5; ++var4) {
               if (!(this.transforms.get(var4) instanceof Transform)) {
                  throw new ClassCastException("transforms[" + var4 + "] is not a valid type");
               }
            }
         } else {
            this.transforms = Collections.emptyList();
         }

         this.uri = var1;
         if (!var1.equals("")) {
            try {
               new URI(var1);
            } catch (URISyntaxException var6) {
               throw new IllegalArgumentException(var6.getMessage());
            }
         }

         this.type = var2;
      }
   }

   public DOMRetrievalMethod(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      this.uri = DOMUtils.getAttributeValue(var1, "URI");
      this.type = DOMUtils.getAttributeValue(var1, "Type");
      this.here = var1.getAttributeNodeNS((String)null, "URI");
      boolean var4 = Utils.secureValidation(var2);
      ArrayList var5 = new ArrayList();
      Element var6 = DOMUtils.getFirstChildElement(var1);
      if (var6 != null) {
         String var7 = var6.getLocalName();
         if (!var7.equals("Transforms")) {
            throw new MarshalException("Invalid element name: " + var7 + ", expected Transforms");
         }

         Element var8 = DOMUtils.getFirstChildElement(var6, "Transform");
         var5.add(new DOMTransform(var8, var2, var3));

         for(var8 = DOMUtils.getNextSiblingElement(var8); var8 != null; var8 = DOMUtils.getNextSiblingElement(var8)) {
            String var9 = var8.getLocalName();
            if (!var9.equals("Transform")) {
               throw new MarshalException("Invalid element name: " + var9 + ", expected Transform");
            }

            var5.add(new DOMTransform(var8, var2, var3));
            if (var4 && Policy.restrictNumTransforms(var5.size())) {
               String var10 = "A maximum of " + Policy.maxTransforms() + " transforms per Reference are allowed when secure validation is enabled";
               throw new MarshalException(var10);
            }
         }
      }

      if (var5.isEmpty()) {
         this.transforms = Collections.emptyList();
      } else {
         this.transforms = Collections.unmodifiableList(var5);
      }

   }

   public String getURI() {
      return this.uri;
   }

   public String getType() {
      return this.type;
   }

   public List getTransforms() {
      return this.transforms;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "RetrievalMethod", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttribute(var5, "URI", this.uri);
      DOMUtils.setAttribute(var5, "Type", this.type);
      if (!this.transforms.isEmpty()) {
         Element var6 = DOMUtils.createElement(var4, "Transforms", "http://www.w3.org/2000/09/xmldsig#", var2);
         var5.appendChild(var6);
         Iterator var7 = this.transforms.iterator();

         while(var7.hasNext()) {
            Transform var8 = (Transform)var7.next();
            ((DOMTransform)var8).marshal(var6, var2, var3);
         }
      }

      var1.appendChild(var5);
      this.here = var5.getAttributeNodeNS((String)null, "URI");
   }

   public Node getHere() {
      return this.here;
   }

   public Data dereference(XMLCryptoContext var1) throws URIReferenceException {
      if (var1 == null) {
         throw new NullPointerException("context cannot be null");
      } else {
         URIDereferencer var2 = var1.getURIDereferencer();
         if (var2 == null) {
            var2 = DOMURIDereferencer.INSTANCE;
         }

         Data var3 = var2.dereference(this, var1);

         Transform var5;
         try {
            for(Iterator var4 = this.transforms.iterator(); var4.hasNext(); var3 = ((DOMTransform)var5).transform(var3, var1)) {
               var5 = (Transform)var4.next();
            }
         } catch (Exception var7) {
            throw new URIReferenceException(var7);
         }

         if (var3 instanceof NodeSetData && Utils.secureValidation(var1) && Policy.restrictRetrievalMethodLoops()) {
            NodeSetData var8 = (NodeSetData)var3;
            Iterator var9 = var8.iterator();
            if (var9.hasNext()) {
               Node var6 = (Node)var9.next();
               if ("RetrievalMethod".equals(var6.getLocalName())) {
                  throw new URIReferenceException("It is forbidden to have one RetrievalMethod point to another when secure validation is enabled");
               }
            }
         }

         return var3;
      }
   }

   public XMLStructure dereferenceAsXMLStructure(XMLCryptoContext var1) throws URIReferenceException {
      try {
         ApacheData var2 = (ApacheData)this.dereference(var1);
         DocumentBuilderFactory var3 = DocumentBuilderFactory.newInstance();
         var3.setNamespaceAware(true);
         var3.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
         DocumentBuilder var4 = var3.newDocumentBuilder();
         Document var5 = var4.parse((InputStream)(new ByteArrayInputStream(var2.getXMLSignatureInput().getBytes())));
         Element var6 = var5.getDocumentElement();
         return var6.getLocalName().equals("X509Data") ? new DOMX509Data(var6) : null;
      } catch (Exception var7) {
         throw new URIReferenceException(var7);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof RetrievalMethod)) {
         return false;
      } else {
         RetrievalMethod var2 = (RetrievalMethod)var1;
         boolean var3 = this.type == null ? var2.getType() == null : this.type.equals(var2.getType());
         return this.uri.equals(var2.getURI()) && this.transforms.equals(var2.getTransforms()) && var3;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.type != null) {
         var1 = 31 * var1 + this.type.hashCode();
      }

      var1 = 31 * var1 + this.uri.hashCode();
      var1 = 31 * var1 + this.transforms.hashCode();
      return var1;
   }
}
