package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMManifest extends DOMStructure implements Manifest {
   private final List<Reference> references;
   private final String id;

   public DOMManifest(List<? extends Reference> var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("references cannot be null");
      } else {
         this.references = Collections.unmodifiableList(new ArrayList(var1));
         if (this.references.isEmpty()) {
            throw new IllegalArgumentException("list of references must contain at least one entry");
         } else {
            int var3 = 0;

            for(int var4 = this.references.size(); var3 < var4; ++var3) {
               if (!(this.references.get(var3) instanceof Reference)) {
                  throw new ClassCastException("references[" + var3 + "] is not a valid type");
               }
            }

            this.id = var2;
         }
      }
   }

   public DOMManifest(Element var1, XMLCryptoContext var2, Provider var3) throws MarshalException {
      Attr var4 = var1.getAttributeNodeNS((String)null, "Id");
      if (var4 != null) {
         this.id = var4.getValue();
         var1.setIdAttributeNode(var4, true);
      } else {
         this.id = null;
      }

      boolean var5 = Utils.secureValidation(var2);
      Element var6 = DOMUtils.getFirstChildElement(var1, "Reference");
      ArrayList var7 = new ArrayList();
      var7.add(new DOMReference(var6, var2, var3));

      for(var6 = DOMUtils.getNextSiblingElement(var6); var6 != null; var6 = DOMUtils.getNextSiblingElement(var6)) {
         String var8 = var6.getLocalName();
         if (!var8.equals("Reference")) {
            throw new MarshalException("Invalid element name: " + var8 + ", expected Reference");
         }

         var7.add(new DOMReference(var6, var2, var3));
         if (var5 && Policy.restrictNumReferences(var7.size())) {
            String var9 = "A maximum of " + Policy.maxReferences() + " references per Manifest are allowed when secure validation is enabled";
            throw new MarshalException(var9);
         }
      }

      this.references = Collections.unmodifiableList(var7);
   }

   public String getId() {
      return this.id;
   }

   public List getReferences() {
      return this.references;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "Manifest", "http://www.w3.org/2000/09/xmldsig#", var2);
      DOMUtils.setAttributeID(var5, "Id", this.id);
      Iterator var6 = this.references.iterator();

      while(var6.hasNext()) {
         Reference var7 = (Reference)var6.next();
         ((DOMReference)var7).marshal(var5, var2, var3);
      }

      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Manifest)) {
         return false;
      } else {
         Manifest var2 = (Manifest)var1;
         boolean var3 = this.id == null ? var2.getId() == null : this.id.equals(var2.getId());
         return var3 && this.references.equals(var2.getReferences());
      }
   }

   public int hashCode() {
      int var1 = 17;
      if (this.id != null) {
         var1 = 31 * var1 + this.id.hashCode();
      }

      var1 = 31 * var1 + this.references.hashCode();
      return var1;
   }
}
