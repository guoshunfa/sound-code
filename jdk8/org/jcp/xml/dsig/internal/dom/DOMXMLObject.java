package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMXMLObject extends DOMStructure implements XMLObject {
   private final String id;
   private final String mimeType;
   private final String encoding;
   private final List<XMLStructure> content;
   private Element objectElem;

   public DOMXMLObject(List<? extends XMLStructure> var1, String var2, String var3, String var4) {
      if (var1 != null && !var1.isEmpty()) {
         this.content = Collections.unmodifiableList(new ArrayList(var1));
         int var5 = 0;

         for(int var6 = this.content.size(); var5 < var6; ++var5) {
            if (!(this.content.get(var5) instanceof XMLStructure)) {
               throw new ClassCastException("content[" + var5 + "] is not a valid type");
            }
         }
      } else {
         this.content = Collections.emptyList();
      }

      this.id = var2;
      this.mimeType = var3;
      this.encoding = var4;
   }

   public DOMXMLObject(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      this.encoding = DOMUtils.getAttributeValue(var1, "Encoding");
      Attr var4 = var1.getAttributeNodeNS((String)null, "Id");
      if (var4 != null) {
         this.id = var4.getValue();
         var1.setIdAttributeNode(var4, true);
      } else {
         this.id = null;
      }

      this.mimeType = DOMUtils.getAttributeValue(var1, "MimeType");
      NodeList var5 = var1.getChildNodes();
      int var6 = var5.getLength();
      ArrayList var7 = new ArrayList(var6);

      for(int var8 = 0; var8 < var6; ++var8) {
         Node var9 = var5.item(var8);
         if (var9.getNodeType() == 1) {
            Element var10 = (Element)var9;
            String var11 = var10.getLocalName();
            if (var11.equals("Manifest")) {
               var7.add(new DOMManifest(var10, var2, var3));
               continue;
            }

            if (var11.equals("SignatureProperties")) {
               var7.add(new DOMSignatureProperties(var10, var2));
               continue;
            }

            if (var11.equals("X509Data")) {
               var7.add(new DOMX509Data(var10));
               continue;
            }
         }

         var7.add(new javax.xml.crypto.dom.DOMStructure(var9));
      }

      if (var7.isEmpty()) {
         this.content = Collections.emptyList();
      } else {
         this.content = Collections.unmodifiableList(var7);
      }

      this.objectElem = var1;
   }

   public List getContent() {
      return this.content;
   }

   public String getId() {
      return this.id;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = this.objectElem != null ? this.objectElem : null;
      if (var5 == null) {
         var5 = DOMUtils.createElement(var4, "Object", "http://www.w3.org/2000/09/xmldsig#", var2);
         DOMUtils.setAttributeID(var5, "Id", this.id);
         DOMUtils.setAttribute(var5, "MimeType", this.mimeType);
         DOMUtils.setAttribute(var5, "Encoding", this.encoding);
         Iterator var6 = this.content.iterator();

         while(var6.hasNext()) {
            XMLStructure var7 = (XMLStructure)var6.next();
            if (var7 instanceof DOMStructure) {
               ((DOMStructure)var7).marshal(var5, var2, var3);
            } else {
               javax.xml.crypto.dom.DOMStructure var8 = (javax.xml.crypto.dom.DOMStructure)var7;
               DOMUtils.appendChild(var5, var8.getNode());
            }
         }
      }

      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof XMLObject)) {
         return false;
      } else {
         XMLObject var2 = (XMLObject)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         boolean var4 = this.encoding == null ? var2.getEncoding() == null : this.encoding.equals(var2.getEncoding());
         boolean var5 = this.mimeType == null ? var2.getMimeType() == null : this.mimeType.equals(var2.getMimeType());
         List var6 = var2.getContent();
         return var3 && var4 && var5 && this.equalsContent(var6);
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      if (this.encoding != null) {
         var1 = 31 * var1 + this.encoding.hashCode();
      }

      if (this.mimeType != null) {
         var1 = 31 * var1 + this.mimeType.hashCode();
      }

      var1 = 31 * var1 + this.content.hashCode();
      return var1;
   }

   private boolean equalsContent(List<XMLStructure> var1) {
      if (this.content.size() != var1.size()) {
         return false;
      } else {
         int var2 = 0;

         for(int var3 = var1.size(); var2 < var3; ++var2) {
            XMLStructure var4 = (XMLStructure)var1.get(var2);
            XMLStructure var5 = (XMLStructure)this.content.get(var2);
            if (var4 instanceof javax.xml.crypto.dom.DOMStructure) {
               if (!(var5 instanceof javax.xml.crypto.dom.DOMStructure)) {
                  return false;
               }

               Node var6 = ((javax.xml.crypto.dom.DOMStructure)var4).getNode();
               Node var7 = ((javax.xml.crypto.dom.DOMStructure)var5).getNode();
               if (!DOMUtils.nodesEqual(var7, var6)) {
                  return false;
               }
            } else if (!var5.equals(var4)) {
               return false;
            }
         }

         return true;
      }
   }
}
