package javax.xml.crypto.dsig.dom;

import java.security.Key;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.w3c.dom.Node;

public class DOMValidateContext extends DOMCryptoContext implements XMLValidateContext {
   private Node node;

   public DOMValidateContext(KeySelector var1, Node var2) {
      if (var1 == null) {
         throw new NullPointerException("key selector is null");
      } else {
         this.init(var2, var1);
      }
   }

   public DOMValidateContext(Key var1, Node var2) {
      if (var1 == null) {
         throw new NullPointerException("validatingKey is null");
      } else {
         this.init(var2, KeySelector.singletonKeySelector(var1));
      }
   }

   private void init(Node var1, KeySelector var2) {
      if (var1 == null) {
         throw new NullPointerException("node is null");
      } else {
         this.node = var1;
         super.setKeySelector(var2);
         if (System.getSecurityManager() != null) {
            super.setProperty("org.jcp.xml.dsig.secureValidation", Boolean.TRUE);
         }

      }
   }

   public void setNode(Node var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.node = var1;
      }
   }

   public Node getNode() {
      return this.node;
   }
}
