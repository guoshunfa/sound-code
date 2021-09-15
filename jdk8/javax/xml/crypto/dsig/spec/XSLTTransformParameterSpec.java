package javax.xml.crypto.dsig.spec;

import javax.xml.crypto.XMLStructure;

public final class XSLTTransformParameterSpec implements TransformParameterSpec {
   private XMLStructure stylesheet;

   public XSLTTransformParameterSpec(XMLStructure var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.stylesheet = var1;
      }
   }

   public XMLStructure getStylesheet() {
      return this.stylesheet;
   }
}
