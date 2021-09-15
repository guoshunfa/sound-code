package javax.imageio.metadata;

import java.util.Iterator;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class IIONamedNodeMap implements NamedNodeMap {
   List nodes;

   public IIONamedNodeMap(List var1) {
      this.nodes = var1;
   }

   public int getLength() {
      return this.nodes.size();
   }

   public Node getNamedItem(String var1) {
      Iterator var2 = this.nodes.iterator();

      Node var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Node)var2.next();
      } while(!var1.equals(var3.getNodeName()));

      return var3;
   }

   public Node item(int var1) {
      Node var2 = (Node)this.nodes.get(var1);
      return var2;
   }

   public Node removeNamedItem(String var1) {
      throw new DOMException((short)7, "This NamedNodeMap is read-only!");
   }

   public Node setNamedItem(Node var1) {
      throw new DOMException((short)7, "This NamedNodeMap is read-only!");
   }

   public Node getNamedItemNS(String var1, String var2) {
      return this.getNamedItem(var2);
   }

   public Node setNamedItemNS(Node var1) {
      return this.setNamedItem(var1);
   }

   public Node removeNamedItemNS(String var1, String var2) {
      return this.removeNamedItem(var2);
   }
}
