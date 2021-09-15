package javax.imageio.metadata;

import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class IIONodeList implements NodeList {
   List nodes;

   public IIONodeList(List var1) {
      this.nodes = var1;
   }

   public int getLength() {
      return this.nodes.size();
   }

   public Node item(int var1) {
      return var1 >= 0 && var1 <= this.nodes.size() ? (Node)this.nodes.get(var1) : null;
   }
}
