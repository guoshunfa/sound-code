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
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMKeyInfo extends DOMStructure implements KeyInfo {
   private final String id;
   private final List<XMLStructure> keyInfoTypes;

   public DOMKeyInfo(List<? extends XMLStructure> var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("content cannot be null");
      } else {
         this.keyInfoTypes = Collections.unmodifiableList(new ArrayList(var1));
         if (this.keyInfoTypes.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
         } else {
            int var3 = 0;

            for(int var4 = this.keyInfoTypes.size(); var3 < var4; ++var3) {
               if (!(this.keyInfoTypes.get(var3) instanceof XMLStructure)) {
                  throw new ClassCastException("content[" + var3 + "] is not a valid KeyInfo type");
               }
            }

            this.id = var2;
         }
      }
   }

   public DOMKeyInfo(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      Attr var4 = var1.getAttributeNodeNS((String)null, "Id");
      if (var4 != null) {
         this.id = var4.getValue();
         var1.setIdAttributeNode(var4, true);
      } else {
         this.id = null;
      }

      NodeList var5 = var1.getChildNodes();
      int var6 = var5.getLength();
      if (var6 < 1) {
         throw new MarshalException("KeyInfo must contain at least one type");
      } else {
         ArrayList var7 = new ArrayList(var6);

         for(int var8 = 0; var8 < var6; ++var8) {
            Node var9 = var5.item(var8);
            if (var9.getNodeType() == 1) {
               Element var10 = (Element)var9;
               String var11 = var10.getLocalName();
               if (var11.equals("X509Data")) {
                  var7.add(new DOMX509Data(var10));
               } else if (var11.equals("KeyName")) {
                  var7.add(new DOMKeyName(var10));
               } else if (var11.equals("KeyValue")) {
                  var7.add(DOMKeyValue.unmarshal(var10));
               } else if (var11.equals("RetrievalMethod")) {
                  var7.add(new DOMRetrievalMethod(var10, var2, var3));
               } else if (var11.equals("PGPData")) {
                  var7.add(new DOMPGPData(var10));
               } else {
                  var7.add(new javax.xml.crypto.dom.DOMStructure(var10));
               }
            }
         }

         this.keyInfoTypes = Collections.unmodifiableList(var7);
      }
   }

   public String getId() {
      return this.id;
   }

   public List getContent() {
      return this.keyInfoTypes;
   }

   public void marshal(XMLStructure var1, XMLCryptoContext var2) throws MarshalException {
      if (var1 == null) {
         throw new NullPointerException("parent is null");
      } else if (!(var1 instanceof javax.xml.crypto.dom.DOMStructure)) {
         throw new ClassCastException("parent must be of type DOMStructure");
      } else {
         Node var3 = ((javax.xml.crypto.dom.DOMStructure)var1).getNode();
         String var4 = DOMUtils.getSignaturePrefix(var2);
         Element var5 = DOMUtils.createElement(DOMUtils.getOwnerDocument(var3), "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", var4);
         if (var4 != null && var4.length() != 0) {
            var5.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + var4, "http://www.w3.org/2000/09/xmldsig#");
         } else {
            var5.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
         }

         this.marshal(var3, var5, (Node)null, var4, (DOMCryptoContext)var2);
      }
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      this.marshal(var1, (Node)null, var2, var3);
   }

   public void marshal(Node var1, Node var2, String var3, DOMCryptoContext var4) throws MarshalException {
      Document var5 = DOMUtils.getOwnerDocument(var1);
      Element var6 = DOMUtils.createElement(var5, "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", var3);
      this.marshal(var1, var6, var2, var3, var4);
   }

   private void marshal(Node var1, Element var2, Node var3, String var4, DOMCryptoContext var5) throws MarshalException {
      Iterator var6 = this.keyInfoTypes.iterator();

      while(var6.hasNext()) {
         XMLStructure var7 = (XMLStructure)var6.next();
         if (var7 instanceof DOMStructure) {
            ((DOMStructure)var7).marshal(var2, var4, var5);
         } else {
            DOMUtils.appendChild(var2, ((javax.xml.crypto.dom.DOMStructure)var7).getNode());
         }
      }

      DOMUtils.setAttributeID(var2, "Id", this.id);
      var1.insertBefore(var2, var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyInfo)) {
         return false;
      } else {
         KeyInfo var2 = (KeyInfo)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         return this.keyInfoTypes.equals(var2.getContent()) && var3;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      var1 = 31 * var1 + this.keyInfoTypes.hashCode();
      return var1;
   }
}
