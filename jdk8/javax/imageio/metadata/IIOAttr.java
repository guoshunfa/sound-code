package javax.imageio.metadata;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

class IIOAttr extends IIOMetadataNode implements Attr {
   Element owner;
   String name;
   String value;

   public IIOAttr(Element var1, String var2, String var3) {
      this.owner = var1;
      this.name = var2;
      this.value = var3;
   }

   public String getName() {
      return this.name;
   }

   public String getNodeName() {
      return this.name;
   }

   public short getNodeType() {
      return 2;
   }

   public boolean getSpecified() {
      return true;
   }

   public String getValue() {
      return this.value;
   }

   public String getNodeValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public void setNodeValue(String var1) {
      this.value = var1;
   }

   public Element getOwnerElement() {
      return this.owner;
   }

   public void setOwnerElement(Element var1) {
      this.owner = var1;
   }

   public boolean isId() {
      return false;
   }
}
