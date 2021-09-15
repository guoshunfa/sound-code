package javax.xml.crypto.dom;

import javax.xml.crypto.XMLStructure;
import org.w3c.dom.Node;

public class DOMStructure implements XMLStructure {
   private final Node node;

   public DOMStructure(Node var1) {
      if (var1 == null) {
         throw new NullPointerException("node cannot be null");
      } else {
         this.node = var1;
      }
   }

   public Node getNode() {
      return this.node;
   }

   public boolean isFeatureSupported(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return false;
      }
   }
}
