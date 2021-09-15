package javax.print.attribute;

import java.io.Serializable;

public class HashDocAttributeSet extends HashAttributeSet implements DocAttributeSet, Serializable {
   private static final long serialVersionUID = -1128534486061432528L;

   public HashDocAttributeSet() {
      super(DocAttribute.class);
   }

   public HashDocAttributeSet(DocAttribute var1) {
      super((Attribute)var1, DocAttribute.class);
   }

   public HashDocAttributeSet(DocAttribute[] var1) {
      super((Attribute[])var1, DocAttribute.class);
   }

   public HashDocAttributeSet(DocAttributeSet var1) {
      super((AttributeSet)var1, DocAttribute.class);
   }
}
