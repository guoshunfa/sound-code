package javax.xml.crypto.dsig.dom;

import java.security.Key;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLSignContext;
import org.w3c.dom.Node;

public class DOMSignContext extends DOMCryptoContext implements XMLSignContext {
   private Node parent;
   private Node nextSibling;

   public DOMSignContext(Key var1, Node var2) {
      if (var1 == null) {
         throw new NullPointerException("signingKey cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("parent cannot be null");
      } else {
         this.setKeySelector(KeySelector.singletonKeySelector(var1));
         this.parent = var2;
      }
   }

   public DOMSignContext(Key var1, Node var2, Node var3) {
      if (var1 == null) {
         throw new NullPointerException("signingKey cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("parent cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("nextSibling cannot be null");
      } else {
         this.setKeySelector(KeySelector.singletonKeySelector(var1));
         this.parent = var2;
         this.nextSibling = var3;
      }
   }

   public DOMSignContext(KeySelector var1, Node var2) {
      if (var1 == null) {
         throw new NullPointerException("key selector cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("parent cannot be null");
      } else {
         this.setKeySelector(var1);
         this.parent = var2;
      }
   }

   public DOMSignContext(KeySelector var1, Node var2, Node var3) {
      if (var1 == null) {
         throw new NullPointerException("key selector cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("parent cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("nextSibling cannot be null");
      } else {
         this.setKeySelector(var1);
         this.parent = var2;
         this.nextSibling = var3;
      }
   }

   public void setParent(Node var1) {
      if (var1 == null) {
         throw new NullPointerException("parent is null");
      } else {
         this.parent = var1;
      }
   }

   public void setNextSibling(Node var1) {
      this.nextSibling = var1;
   }

   public Node getParent() {
      return this.parent;
   }

   public Node getNextSibling() {
      return this.nextSibling;
   }
}
