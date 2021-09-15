package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.jcp.xml.dsig.internal.DigesterOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMReference extends DOMStructure implements Reference, DOMURIReference {
   private static boolean useC14N11 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11");
      }
   });
   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
   private final DigestMethod digestMethod;
   private final String id;
   private final List<Transform> transforms;
   private List<Transform> allTransforms;
   private final Data appliedTransformData;
   private Attr here;
   private final String uri;
   private final String type;
   private byte[] digestValue;
   private byte[] calcDigestValue;
   private Element refElem;
   private boolean digested;
   private boolean validated;
   private boolean validationStatus;
   private Data derefData;
   private InputStream dis;
   private MessageDigest md;
   private Provider provider;

   public DOMReference(String var1, String var2, DigestMethod var3, List<? extends Transform> var4, String var5, Provider var6) {
      this(var1, var2, var3, (List)null, (Data)null, var4, var5, (byte[])null, var6);
   }

   public DOMReference(String var1, String var2, DigestMethod var3, List<? extends Transform> var4, Data var5, List<? extends Transform> var6, String var7, Provider var8) {
      this(var1, var2, var3, var4, var5, var6, var7, (byte[])null, var8);
   }

   public DOMReference(String var1, String var2, DigestMethod var3, List<? extends Transform> var4, Data var5, List<? extends Transform> var6, String var7, byte[] var8, Provider var9) {
      this.digested = false;
      this.validated = false;
      if (var3 == null) {
         throw new NullPointerException("DigestMethod must be non-null");
      } else {
         int var10;
         int var11;
         if (var4 == null) {
            this.allTransforms = new ArrayList();
         } else {
            this.allTransforms = new ArrayList(var4);
            var10 = 0;

            for(var11 = this.allTransforms.size(); var10 < var11; ++var10) {
               if (!(this.allTransforms.get(var10) instanceof Transform)) {
                  throw new ClassCastException("appliedTransforms[" + var10 + "] is not a valid type");
               }
            }
         }

         if (var6 == null) {
            this.transforms = Collections.emptyList();
         } else {
            this.transforms = new ArrayList(var6);
            var10 = 0;

            for(var11 = this.transforms.size(); var10 < var11; ++var10) {
               if (!(this.transforms.get(var10) instanceof Transform)) {
                  throw new ClassCastException("transforms[" + var10 + "] is not a valid type");
               }
            }

            this.allTransforms.addAll(this.transforms);
         }

         this.digestMethod = var3;
         this.uri = var1;
         if (var1 != null && !var1.equals("")) {
            try {
               new URI(var1);
            } catch (URISyntaxException var12) {
               throw new IllegalArgumentException(var12.getMessage());
            }
         }

         this.type = var2;
         this.id = var7;
         if (var8 != null) {
            this.digestValue = (byte[])((byte[])var8.clone());
            this.digested = true;
         }

         this.appliedTransformData = var5;
         this.provider = var9;
      }
   }

   public DOMReference(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      this.digested = false;
      this.validated = false;
      boolean var4 = Utils.secureValidation(var2);
      Element var5 = DOMUtils.getFirstChildElement(var1);
      ArrayList var6 = new ArrayList(5);
      String var8;
      if (var5.getLocalName().equals("Transforms")) {
         Element var7 = DOMUtils.getFirstChildElement(var5, "Transform");
         var6.add(new DOMTransform(var7, var2, var3));

         for(var7 = DOMUtils.getNextSiblingElement(var7); var7 != null; var7 = DOMUtils.getNextSiblingElement(var7)) {
            var8 = var7.getLocalName();
            if (!var8.equals("Transform")) {
               throw new MarshalException("Invalid element name: " + var8 + ", expected Transform");
            }

            var6.add(new DOMTransform(var7, var2, var3));
            if (var4 && Policy.restrictNumTransforms(var6.size())) {
               String var9 = "A maximum of " + Policy.maxTransforms() + " transforms per Reference are allowed when secure validation is enabled";
               throw new MarshalException(var9);
            }
         }

         var5 = DOMUtils.getNextSiblingElement(var5);
      }

      if (!var5.getLocalName().equals("DigestMethod")) {
         throw new MarshalException("Invalid element name: " + var5.getLocalName() + ", expected DigestMethod");
      } else {
         this.digestMethod = DOMDigestMethod.unmarshal(var5);
         var8 = this.digestMethod.getAlgorithm();
         if (var4 && Policy.restrictAlg(var8)) {
            throw new MarshalException("It is forbidden to use algorithm " + var8 + " when secure validation is enabled");
         } else {
            Element var12 = DOMUtils.getNextSiblingElement(var5, "DigestValue");

            try {
               this.digestValue = Base64.decode(var12);
            } catch (Base64DecodingException var11) {
               throw new MarshalException(var11);
            }

            if (DOMUtils.getNextSiblingElement(var12) != null) {
               throw new MarshalException("Unexpected element after DigestValue element");
            } else {
               this.uri = DOMUtils.getAttributeValue(var1, "URI");
               Attr var10 = var1.getAttributeNodeNS((String)null, "Id");
               if (var10 != null) {
                  this.id = var10.getValue();
                  var1.setIdAttributeNode(var10, true);
               } else {
                  this.id = null;
               }

               this.type = DOMUtils.getAttributeValue(var1, "Type");
               this.here = var1.getAttributeNodeNS((String)null, "URI");
               this.refElem = var1;
               this.transforms = var6;
               this.allTransforms = var6;
               this.appliedTransformData = null;
               this.provider = var3;
            }
         }
      }
   }

   public DigestMethod getDigestMethod() {
      return this.digestMethod;
   }

   public String getId() {
      return this.id;
   }

   public String getURI() {
      return this.uri;
   }

   public String getType() {
      return this.type;
   }

   public List getTransforms() {
      return Collections.unmodifiableList(this.allTransforms);
   }

   public byte[] getDigestValue() {
      return this.digestValue == null ? null : (byte[])((byte[])this.digestValue.clone());
   }

   public byte[] getCalculatedDigestValue() {
      return this.calcDigestValue == null ? null : (byte[])((byte[])this.calcDigestValue.clone());
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Marshalling Reference");
      }

      Document var4 = DOMUtils.getOwnerDocument(var1);
      this.refElem = DOMUtils.createElement(var4, "Reference", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttributeID(this.refElem, "Id", this.id);
      DOMUtils.setAttribute(this.refElem, "URI", this.uri);
      DOMUtils.setAttribute(this.refElem, "Type", this.type);
      Element var5;
      if (!this.allTransforms.isEmpty()) {
         var5 = DOMUtils.createElement(var4, "Transforms", "http://www.w3.org/2000/09/xmldsig#", var2);
         this.refElem.appendChild(var5);
         Iterator var6 = this.allTransforms.iterator();

         while(var6.hasNext()) {
            Transform var7 = (Transform)var6.next();
            ((DOMStructure)var7).marshal(var5, var2, var3);
         }
      }

      ((DOMDigestMethod)this.digestMethod).marshal(this.refElem, var2, var3);
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Adding digestValueElem");
      }

      var5 = DOMUtils.createElement(var4, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", var2);
      if (this.digestValue != null) {
         var5.appendChild(var4.createTextNode(Base64.encode(this.digestValue)));
      }

      this.refElem.appendChild(var5);
      var1.appendChild(this.refElem);
      this.here = this.refElem.getAttributeNodeNS((String)null, "URI");
   }

   public void digest(XMLSignContext var1) throws XMLSignatureException {
      Data var2 = null;
      if (this.appliedTransformData == null) {
         var2 = this.dereference(var1);
      } else {
         var2 = this.appliedTransformData;
      }

      this.digestValue = this.transform(var2, var1);
      String var3 = Base64.encode(this.digestValue);
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Reference object uri = " + this.uri);
      }

      Element var4 = DOMUtils.getLastChildElement(this.refElem);
      if (var4 == null) {
         throw new XMLSignatureException("DigestValue element expected");
      } else {
         DOMUtils.removeAllChildren(var4);
         var4.appendChild(this.refElem.getOwnerDocument().createTextNode(var3));
         this.digested = true;
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Reference digesting completed");
         }

      }
   }

   public boolean validate(XMLValidateContext var1) throws XMLSignatureException {
      if (var1 == null) {
         throw new NullPointerException("validateContext cannot be null");
      } else if (this.validated) {
         return this.validationStatus;
      } else {
         Data var2 = this.dereference(var1);
         this.calcDigestValue = this.transform(var2, var1);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Expected digest: " + Base64.encode(this.digestValue));
            log.log(Level.FINE, "Actual digest: " + Base64.encode(this.calcDigestValue));
         }

         this.validationStatus = Arrays.equals(this.digestValue, this.calcDigestValue);
         this.validated = true;
         return this.validationStatus;
      }
   }

   public Data getDereferencedData() {
      return this.derefData;
   }

   public InputStream getDigestInputStream() {
      return this.dis;
   }

   private Data dereference(XMLCryptoContext var1) throws XMLSignatureException {
      Data var2 = null;
      URIDereferencer var3 = var1.getURIDereferencer();
      if (var3 == null) {
         var3 = DOMURIDereferencer.INSTANCE;
      }

      try {
         var2 = var3.dereference(this, var1);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "URIDereferencer class name: " + var3.getClass().getName());
            log.log(Level.FINE, "Data class name: " + var2.getClass().getName());
         }

         return var2;
      } catch (URIReferenceException var5) {
         throw new XMLSignatureException(var5);
      }
   }

   private byte[] transform(Data var1, XMLCryptoContext var2) throws XMLSignatureException {
      if (this.md == null) {
         try {
            this.md = MessageDigest.getInstance(((DOMDigestMethod)this.digestMethod).getMessageDigestAlgorithm());
         } catch (NoSuchAlgorithmException var33) {
            throw new XMLSignatureException(var33);
         }
      }

      this.md.reset();
      Boolean var4 = (Boolean)var2.getProperty("javax.xml.crypto.dsig.cacheReference");
      DigesterOutputStream var3;
      if (var4 != null && var4) {
         this.derefData = copyDerefData(var1);
         var3 = new DigesterOutputStream(this.md, true);
      } else {
         var3 = new DigesterOutputStream(this.md);
      }

      UnsyncBufferedOutputStream var5 = null;
      Data var6 = var1;

      byte[] var42;
      try {
         var5 = new UnsyncBufferedOutputStream(var3);
         int var7 = 0;

         for(int var8 = this.transforms.size(); var7 < var8; ++var7) {
            DOMTransform var9 = (DOMTransform)this.transforms.get(var7);
            if (var7 < var8 - 1) {
               var6 = var9.transform(var6, var2);
            } else {
               var6 = var9.transform(var6, var2, (OutputStream)var5);
            }
         }

         if (var6 != null) {
            boolean var41 = useC14N11;
            String var43 = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
            Boolean var10;
            if (var2 instanceof XMLSignContext) {
               if (!var41) {
                  var10 = (Boolean)var2.getProperty("com.sun.org.apache.xml.internal.security.useC14N11");
                  var41 = var10 != null && var10;
                  if (var41) {
                     var43 = "http://www.w3.org/2006/12/xml-c14n11";
                  }
               } else {
                  var43 = "http://www.w3.org/2006/12/xml-c14n11";
               }
            }

            XMLSignatureInput var40;
            TransformService var44;
            if (var6 instanceof ApacheData) {
               var40 = ((ApacheData)var6).getXMLSignatureInput();
            } else if (var6 instanceof OctetStreamData) {
               var40 = new XMLSignatureInput(((OctetStreamData)var6).getOctetStream());
            } else {
               if (!(var6 instanceof NodeSetData)) {
                  throw new XMLSignatureException("unrecognized Data type");
               }

               var10 = null;
               if (this.provider == null) {
                  var44 = TransformService.getInstance(var43, "DOM");
               } else {
                  try {
                     var44 = TransformService.getInstance(var43, "DOM", this.provider);
                  } catch (NoSuchAlgorithmException var32) {
                     var44 = TransformService.getInstance(var43, "DOM");
                  }
               }

               var6 = var44.transform(var6, var2);
               var40 = new XMLSignatureInput(((OctetStreamData)var6).getOctetStream());
            }

            if (var2 instanceof XMLSignContext && var41 && !var40.isOctetStream() && !var40.isOutputStreamSet()) {
               var10 = null;
               if (this.provider == null) {
                  var44 = TransformService.getInstance(var43, "DOM");
               } else {
                  try {
                     var44 = TransformService.getInstance(var43, "DOM", this.provider);
                  } catch (NoSuchAlgorithmException var31) {
                     var44 = TransformService.getInstance(var43, "DOM");
                  }
               }

               DOMTransform var11 = new DOMTransform(var44);
               Element var12 = null;
               String var13 = DOMUtils.getSignaturePrefix(var2);
               if (this.allTransforms.isEmpty()) {
                  var12 = DOMUtils.createElement(this.refElem.getOwnerDocument(), "Transforms", "http://www.w3.org/2000/09/xmldsig#", var13);
                  this.refElem.insertBefore(var12, DOMUtils.getFirstChildElement(this.refElem));
               } else {
                  var12 = DOMUtils.getFirstChildElement(this.refElem);
               }

               var11.marshal(var12, var13, (DOMCryptoContext)var2);
               this.allTransforms.add(var11);
               var40.updateOutputStream(var5, true);
            } else {
               var40.updateOutputStream(var5);
            }
         }

         var5.flush();
         if (var4 != null && var4) {
            this.dis = var3.getInputStream();
         }

         var42 = var3.getDigestValue();
      } catch (NoSuchAlgorithmException var34) {
         throw new XMLSignatureException(var34);
      } catch (TransformException var35) {
         throw new XMLSignatureException(var35);
      } catch (MarshalException var36) {
         throw new XMLSignatureException(var36);
      } catch (IOException var37) {
         throw new XMLSignatureException(var37);
      } catch (CanonicalizationException var38) {
         throw new XMLSignatureException(var38);
      } finally {
         if (var5 != null) {
            try {
               var5.close();
            } catch (IOException var30) {
               throw new XMLSignatureException(var30);
            }
         }

         if (var3 != null) {
            try {
               var3.close();
            } catch (IOException var29) {
               throw new XMLSignatureException(var29);
            }
         }

      }

      return var42;
   }

   public Node getHere() {
      return this.here;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Reference)) {
         return false;
      } else {
         Reference var2 = (Reference)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         boolean var4 = this.uri == null ? var2.getURI() == null : this.uri.equals(var2.getURI());
         boolean var5 = this.type == null ? var2.getType() == null : this.type.equals(var2.getType());
         boolean var6 = Arrays.equals(this.digestValue, var2.getDigestValue());
         return this.digestMethod.equals(var2.getDigestMethod()) && var3 && var4 && var5 && this.allTransforms.equals(var2.getTransforms()) && var6;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      if (this.uri != null) {
         var1 = 31 * var1 + this.uri.hashCode();
      }

      if (this.type != null) {
         var1 = 31 * var1 + this.type.hashCode();
      }

      if (this.digestValue != null) {
         var1 = 31 * var1 + Arrays.hashCode(this.digestValue);
      }

      var1 = 31 * var1 + this.digestMethod.hashCode();
      var1 = 31 * var1 + this.allTransforms.hashCode();
      return var1;
   }

   boolean isDigested() {
      return this.digested;
   }

   private static Data copyDerefData(Data var0) {
      if (var0 instanceof ApacheData) {
         ApacheData var1 = (ApacheData)var0;
         XMLSignatureInput var2 = var1.getXMLSignatureInput();
         if (var2.isNodeSet()) {
            try {
               final Set var3 = var2.getNodeSet();
               return new NodeSetData() {
                  public Iterator iterator() {
                     return var3.iterator();
                  }
               };
            } catch (Exception var4) {
               log.log(Level.WARNING, "cannot cache dereferenced data: " + var4);
               return null;
            }
         }

         if (var2.isElement()) {
            return new DOMSubTreeData(var2.getSubNode(), var2.isExcludeComments());
         }

         if (var2.isOctetStream() || var2.isByteArray()) {
            try {
               return new OctetStreamData(var2.getOctetStream(), var2.getSourceURI(), var2.getMIMEType());
            } catch (IOException var5) {
               log.log(Level.WARNING, "cannot cache dereferenced data: " + var5);
               return null;
            }
         }
      }

      return var0;
   }
}
