package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.math.BigInteger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public final class DOMCryptoBinary extends DOMStructure {
   private final BigInteger bigNum;
   private final String value;

   public DOMCryptoBinary(BigInteger var1) {
      if (var1 == null) {
         throw new NullPointerException("bigNum is null");
      } else {
         this.bigNum = var1;
         this.value = Base64.encode(var1);
      }
   }

   public DOMCryptoBinary(Node var1) throws MarshalException {
      this.value = var1.getNodeValue();

      try {
         this.bigNum = Base64.decodeBigIntegerFromText((Text)var1);
      } catch (Exception var3) {
         throw new MarshalException(var3);
      }
   }

   public BigInteger getBigNum() {
      return this.bigNum;
   }

   public void marshal(Node var1, String var2, DOMCryptoContext var3) throws MarshalException {
      var1.appendChild(DOMUtils.getOwnerDocument(var1).createTextNode(this.value));
   }
}
