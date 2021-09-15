package javax.imageio.metadata;

import javax.imageio.IIOException;
import org.w3c.dom.Node;

public class IIOInvalidTreeException extends IIOException {
   protected Node offendingNode = null;

   public IIOInvalidTreeException(String var1, Node var2) {
      super(var1);
      this.offendingNode = var2;
   }

   public IIOInvalidTreeException(String var1, Throwable var2, Node var3) {
      super(var1, var2);
      this.offendingNode = var3;
   }

   public Node getOffendingNode() {
      return this.offendingNode;
   }
}
