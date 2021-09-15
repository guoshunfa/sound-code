package org.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMSignatureProperty extends DOMStructure implements SignatureProperty {
   private final String id;
   private final String target;
   private final List<XMLStructure> content;

   public DOMSignatureProperty(List<? extends XMLStructure> var1, String var2, String var3) {
      if (var2 == null) {
         throw new NullPointerException("target cannot be null");
      } else if (var1 == null) {
         throw new NullPointerException("content cannot be null");
      } else if (var1.isEmpty()) {
         throw new IllegalArgumentException("content cannot be empty");
      } else {
         this.content = Collections.unmodifiableList(new ArrayList(var1));
         int var4 = 0;

         for(int var5 = this.content.size(); var4 < var5; ++var4) {
            if (!(this.content.get(var4) instanceof XMLStructure)) {
               throw new ClassCastException("content[" + var4 + "] is not a valid type");
            }
         }

         this.target = var2;
         this.id = var3;
      }
   }

   public DOMSignatureProperty(Element var1, XMLCryptoContext var2) throws MarshalException {
      this.target = DOMUtils.getAttributeValue(var1, "Target");
      if (this.target == null) {
         throw new MarshalException("target cannot be null");
      } else {
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
            var6.add(new javax.xml.crypto.dom.DOMStructure(var4.item(var7)));
         }

         if (var6.isEmpty()) {
            throw new MarshalException("content cannot be empty");
         } else {
            this.content = Collections.unmodifiableList(var6);
         }
      }
   }

   public List getContent() {
      return this.content;
   }

   public String getId() {
      return this.id;
   }

   public String getTarget() {
      return this.target;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "SignatureProperty", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttributeID(var5, "Id", this.id);
      DOMUtils.setAttribute(var5, "Target", this.target);
      Iterator var6 = this.content.iterator();

      while(var6.hasNext()) {
         XMLStructure var7 = (XMLStructure)var6.next();
         DOMUtils.appendChild(var5, ((javax.xml.crypto.dom.DOMStructure)var7).getNode());
      }

      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SignatureProperty)) {
         return false;
      } else {
         SignatureProperty var2 = (SignatureProperty)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         List var4 = var2.getContent();
         return this.equalsContent(var4) && this.target.equals(var2.getTarget()) && var3;
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      var1 = 31 * var1 + this.target.hashCode();
      var1 = 31 * var1 + this.content.hashCode();
      return var1;
   }

   private boolean equalsContent(List<XMLStructure> var1) {
      int var2 = var1.size();
      if (this.content.size() != var2) {
         return false;
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            XMLStructure var4 = (XMLStructure)var1.get(var3);
            XMLStructure var5 = (XMLStructure)this.content.get(var3);
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
