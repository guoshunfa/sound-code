package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMKeyName extends DOMStructure implements KeyName {
   private final String name;

   public DOMKeyName(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name cannot be null");
      } else {
         this.name = var1;
      }
   }

   public DOMKeyName(Element var1) {
      this.name = var1.getFirstChild().getNodeValue();
   }

   public String getName() {
      return this.name;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      Document var4 = DOMUtils.getOwnerDocument(var1);
      Element var5 = DOMUtils.createElement(var4, "KeyName", "http://www.w3.org/2000/09/xmldsig#", var2);
      var5.appendChild(var4.createTextNode(this.name));
      var1.appendChild(var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyName)) {
         return false;
      } else {
         KeyName var2 = (KeyName)var1;
         return this.name.equals(var2.getName());
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 31 * var1 + this.name.hashCode();
      return var2;
   }
}
