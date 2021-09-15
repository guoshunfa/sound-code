package javax.imageio.metadata;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class IIOMetadataNode implements Element, NodeList {
   private String nodeName = null;
   private String nodeValue = null;
   private Object userObject = null;
   private IIOMetadataNode parent = null;
   private int numChildren = 0;
   private IIOMetadataNode firstChild = null;
   private IIOMetadataNode lastChild = null;
   private IIOMetadataNode nextSibling = null;
   private IIOMetadataNode previousSibling = null;
   private List attributes = new ArrayList();

   public IIOMetadataNode() {
   }

   public IIOMetadataNode(String var1) {
      this.nodeName = var1;
   }

   private void checkNode(Node var1) throws DOMException {
      if (var1 != null) {
         if (!(var1 instanceof IIOMetadataNode)) {
            throw new IIODOMException((short)4, "Node not an IIOMetadataNode!");
         }
      }
   }

   public String getNodeName() {
      return this.nodeName;
   }

   public String getNodeValue() {
      return this.nodeValue;
   }

   public void setNodeValue(String var1) {
      this.nodeValue = var1;
   }

   public short getNodeType() {
      return 1;
   }

   public Node getParentNode() {
      return this.parent;
   }

   public NodeList getChildNodes() {
      return this;
   }

   public Node getFirstChild() {
      return this.firstChild;
   }

   public Node getLastChild() {
      return this.lastChild;
   }

   public Node getPreviousSibling() {
      return this.previousSibling;
   }

   public Node getNextSibling() {
      return this.nextSibling;
   }

   public NamedNodeMap getAttributes() {
      return new IIONamedNodeMap(this.attributes);
   }

   public Document getOwnerDocument() {
      return null;
   }

   public Node insertBefore(Node var1, Node var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("newChild == null!");
      } else {
         this.checkNode(var1);
         this.checkNode(var2);
         IIOMetadataNode var3 = (IIOMetadataNode)var1;
         IIOMetadataNode var4 = (IIOMetadataNode)var2;
         IIOMetadataNode var5 = null;
         IIOMetadataNode var6 = null;
         if (var2 == null) {
            var5 = this.lastChild;
            var6 = null;
            this.lastChild = var3;
         } else {
            var5 = var4.previousSibling;
            var6 = var4;
         }

         if (var5 != null) {
            var5.nextSibling = var3;
         }

         if (var6 != null) {
            var6.previousSibling = var3;
         }

         var3.parent = this;
         var3.previousSibling = var5;
         var3.nextSibling = var6;
         if (this.firstChild == var4) {
            this.firstChild = var3;
         }

         ++this.numChildren;
         return var3;
      }
   }

   public Node replaceChild(Node var1, Node var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("newChild == null!");
      } else {
         this.checkNode(var1);
         this.checkNode(var2);
         IIOMetadataNode var3 = (IIOMetadataNode)var1;
         IIOMetadataNode var4 = (IIOMetadataNode)var2;
         IIOMetadataNode var5 = var4.previousSibling;
         IIOMetadataNode var6 = var4.nextSibling;
         if (var5 != null) {
            var5.nextSibling = var3;
         }

         if (var6 != null) {
            var6.previousSibling = var3;
         }

         var3.parent = this;
         var3.previousSibling = var5;
         var3.nextSibling = var6;
         if (this.firstChild == var4) {
            this.firstChild = var3;
         }

         if (this.lastChild == var4) {
            this.lastChild = var3;
         }

         var4.parent = null;
         var4.previousSibling = null;
         var4.nextSibling = null;
         return var4;
      }
   }

   public Node removeChild(Node var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("oldChild == null!");
      } else {
         this.checkNode(var1);
         IIOMetadataNode var2 = (IIOMetadataNode)var1;
         IIOMetadataNode var3 = var2.previousSibling;
         IIOMetadataNode var4 = var2.nextSibling;
         if (var3 != null) {
            var3.nextSibling = var4;
         }

         if (var4 != null) {
            var4.previousSibling = var3;
         }

         if (this.firstChild == var2) {
            this.firstChild = var4;
         }

         if (this.lastChild == var2) {
            this.lastChild = var3;
         }

         var2.parent = null;
         var2.previousSibling = null;
         var2.nextSibling = null;
         --this.numChildren;
         return var2;
      }
   }

   public Node appendChild(Node var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("newChild == null!");
      } else {
         this.checkNode(var1);
         return this.insertBefore(var1, (Node)null);
      }
   }

   public boolean hasChildNodes() {
      return this.numChildren > 0;
   }

   public Node cloneNode(boolean var1) {
      IIOMetadataNode var2 = new IIOMetadataNode(this.nodeName);
      var2.setUserObject(this.getUserObject());
      if (var1) {
         for(IIOMetadataNode var3 = this.firstChild; var3 != null; var3 = var3.nextSibling) {
            var2.appendChild(var3.cloneNode(true));
         }
      }

      return var2;
   }

   public void normalize() {
   }

   public boolean isSupported(String var1, String var2) {
      return false;
   }

   public String getNamespaceURI() throws DOMException {
      return null;
   }

   public String getPrefix() {
      return null;
   }

   public void setPrefix(String var1) {
   }

   public String getLocalName() {
      return this.nodeName;
   }

   public String getTagName() {
      return this.nodeName;
   }

   public String getAttribute(String var1) {
      Attr var2 = this.getAttributeNode(var1);
      return var2 == null ? "" : var2.getValue();
   }

   public String getAttributeNS(String var1, String var2) {
      return this.getAttribute(var2);
   }

   public void setAttribute(String var1, String var2) {
      boolean var3 = true;
      char[] var4 = var1.toCharArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5] >= '\ufffe') {
            var3 = false;
            break;
         }
      }

      if (!var3) {
         throw new IIODOMException((short)5, "Attribute name is illegal!");
      } else {
         this.removeAttribute(var1, false);
         this.attributes.add(new IIOAttr(this, var1, var2));
      }
   }

   public void setAttributeNS(String var1, String var2, String var3) {
      this.setAttribute(var2, var3);
   }

   public void removeAttribute(String var1) {
      this.removeAttribute(var1, true);
   }

   private void removeAttribute(String var1, boolean var2) {
      int var3 = this.attributes.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         IIOAttr var5 = (IIOAttr)this.attributes.get(var4);
         if (var1.equals(var5.getName())) {
            var5.setOwnerElement((Element)null);
            this.attributes.remove(var4);
            return;
         }
      }

      if (var2) {
         throw new IIODOMException((short)8, "No such attribute!");
      }
   }

   public void removeAttributeNS(String var1, String var2) {
      this.removeAttribute(var2);
   }

   public Attr getAttributeNode(String var1) {
      Node var2 = this.getAttributes().getNamedItem(var1);
      return (Attr)var2;
   }

   public Attr getAttributeNodeNS(String var1, String var2) {
      return this.getAttributeNode(var2);
   }

   public Attr setAttributeNode(Attr var1) throws DOMException {
      Element var2 = var1.getOwnerElement();
      if (var2 != null) {
         if (var2 == this) {
            return null;
         } else {
            throw new DOMException((short)10, "Attribute is already in use");
         }
      } else {
         IIOAttr var3;
         if (var1 instanceof IIOAttr) {
            var3 = (IIOAttr)var1;
            var3.setOwnerElement(this);
         } else {
            var3 = new IIOAttr(this, var1.getName(), var1.getValue());
         }

         Attr var4 = this.getAttributeNode(var3.getName());
         if (var4 != null) {
            this.removeAttributeNode(var4);
         }

         this.attributes.add(var3);
         return var4;
      }
   }

   public Attr setAttributeNodeNS(Attr var1) {
      return this.setAttributeNode(var1);
   }

   public Attr removeAttributeNode(Attr var1) {
      this.removeAttribute(var1.getName());
      return var1;
   }

   public NodeList getElementsByTagName(String var1) {
      ArrayList var2 = new ArrayList();
      this.getElementsByTagName(var1, var2);
      return new IIONodeList(var2);
   }

   private void getElementsByTagName(String var1, List var2) {
      if (this.nodeName.equals(var1)) {
         var2.add(this);
      }

      for(Node var3 = this.getFirstChild(); var3 != null; var3 = var3.getNextSibling()) {
         ((IIOMetadataNode)var3).getElementsByTagName(var1, var2);
      }

   }

   public NodeList getElementsByTagNameNS(String var1, String var2) {
      return this.getElementsByTagName(var2);
   }

   public boolean hasAttributes() {
      return this.attributes.size() > 0;
   }

   public boolean hasAttribute(String var1) {
      return this.getAttributeNode(var1) != null;
   }

   public boolean hasAttributeNS(String var1, String var2) {
      return this.hasAttribute(var2);
   }

   public int getLength() {
      return this.numChildren;
   }

   public Node item(int var1) {
      if (var1 < 0) {
         return null;
      } else {
         Node var2;
         for(var2 = this.getFirstChild(); var2 != null && var1-- > 0; var2 = var2.getNextSibling()) {
         }

         return var2;
      }
   }

   public Object getUserObject() {
      return this.userObject;
   }

   public void setUserObject(Object var1) {
      this.userObject = var1;
   }

   public void setIdAttribute(String var1, boolean var2) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public void setIdAttributeNS(String var1, String var2, boolean var3) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public void setIdAttributeNode(Attr var1, boolean var2) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public TypeInfo getSchemaTypeInfo() throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Object setUserData(String var1, Object var2, UserDataHandler var3) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Object getUserData(String var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public Object getFeature(String var1, String var2) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public boolean isSameNode(Node var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public boolean isEqualNode(Node var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public String lookupNamespaceURI(String var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public boolean isDefaultNamespace(String var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public String lookupPrefix(String var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public String getTextContent() throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public void setTextContent(String var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public short compareDocumentPosition(Node var1) throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }

   public String getBaseURI() throws DOMException {
      throw new DOMException((short)9, "Method not supported");
   }
}
