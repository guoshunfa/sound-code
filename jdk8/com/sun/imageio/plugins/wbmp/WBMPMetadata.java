package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class WBMPMetadata extends IIOMetadata {
   static final String nativeMetadataFormatName = "javax_imageio_wbmp_1.0";
   public int wbmpType;
   public int width;
   public int height;

   public WBMPMetadata() {
      super(true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", (String[])null, (String[])null);
   }

   public boolean isReadOnly() {
      return true;
   }

   public Node getAsTree(String var1) {
      if (var1.equals("javax_imageio_wbmp_1.0")) {
         return this.getNativeTree();
      } else if (var1.equals("javax_imageio_1.0")) {
         return this.getStandardTree();
      } else {
         throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
      }
   }

   private Node getNativeTree() {
      IIOMetadataNode var1 = new IIOMetadataNode("javax_imageio_wbmp_1.0");
      this.addChildNode(var1, "WBMPType", new Integer(this.wbmpType));
      this.addChildNode(var1, "Width", new Integer(this.width));
      this.addChildNode(var1, "Height", new Integer(this.height));
      return var1;
   }

   public void setFromTree(String var1, Node var2) {
      throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
   }

   public void mergeTree(String var1, Node var2) {
      throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
   }

   public void reset() {
      throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
   }

   private IIOMetadataNode addChildNode(IIOMetadataNode var1, String var2, Object var3) {
      IIOMetadataNode var4 = new IIOMetadataNode(var2);
      if (var3 != null) {
         var4.setUserObject(var3);
         var4.setNodeValue(ImageUtil.convertObjectToString(var3));
      }

      var1.appendChild(var4);
      return var4;
   }

   protected IIOMetadataNode getStandardChromaNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Chroma");
      IIOMetadataNode var2 = new IIOMetadataNode("BlackIsZero");
      var2.setAttribute("value", "TRUE");
      var1.appendChild(var2);
      return var1;
   }

   protected IIOMetadataNode getStandardDimensionNode() {
      IIOMetadataNode var1 = new IIOMetadataNode("Dimension");
      IIOMetadataNode var2 = null;
      var2 = new IIOMetadataNode("ImageOrientation");
      var2.setAttribute("value", "Normal");
      var1.appendChild(var2);
      return var1;
   }
}
