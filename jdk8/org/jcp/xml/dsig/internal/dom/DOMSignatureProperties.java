package org.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMSignatureProperties extends DOMStructure implements SignatureProperties {
   private final String id;
   private final List<SignatureProperty> properties;

   public DOMSignatureProperties(List<? extends SignatureProperty> var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("properties cannot be null");
      } else if (var1.isEmpty()) {
         throw new IllegalArgumentException("properties cannot be empty");
      } else {
         this.properties = Collections.unmodifiableList(new ArrayList(var1));
         int var3 = 0;

         for(int var4 = this.properties.size(); var3 < var4; ++var3) {
            if (!(this.properties.get(var3) instanceof SignatureProperty)) {
               throw new ClassCastException("properties[" + var3 + "] is not a valid type");
            }
         }

         this.id = var2;
      }
   }

   public DOMSignatureProperties(Element var1, XMLCryptoContext var2) throws MarshalException {
      Attr var3 = var1.getAttributeNodeNS((String)null, "Id");
      if (var3 != null) {
         this.id = var3.getValue();
         var1.setIdAttributeNode(var3, true);
      } else {
         this.id = null;
      }

      NodeList var4 = var1.getChildNodes();
      int var5 = var4.getLength();
      ArrayList var6 = new ArrayList(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         Node var8 = var4.item(var7);
         if (var8.getNodeType() == 1) {
            String var9 = var8.getLocalName();
            if (!var9.equals("SignatureProperty")) {
               throw new MarshalException("Invalid element name: " + var9 + ", expected SignatureProperty");
            }

            var6.add(new DOMSignatureProperty((Element)var8, var2));
         }
      }

      if (var6.isEmpty()) {
         throw new MarshalException("properties cannot be empty");
      } else {
         this.properties = Collections.unmodifiableList(var6);
      }
   }

   public List getProperties() {
      return this.properties;
   }

   public String getId() {
      return this.id;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "SignatureProperties", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttributeID(var5, "Id", this.id);
      Iterator var6 = this.properties.iterator();

      while(var6.hasNext()) {
         SignatureProperty var7 = (SignatureProperty)var6.next();
         ((DOMSignatureProperty)var7).marshal(var5, var2, var3);
      }

      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SignatureProperties)) {
         return false;
      } else {
         SignatureProperties var2 = (SignatureProperties)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         return this.properties.equals(var2.getProperties()) && var3;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      var1 = 31 * var1 + this.properties.hashCode();
      return var1;
   }
}
