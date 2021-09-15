package javax.swing.text.rtf;

import java.io.IOException;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

interface RTFAttribute {
   int D_CHARACTER = 0;
   int D_PARAGRAPH = 1;
   int D_SECTION = 2;
   int D_DOCUMENT = 3;
   int D_META = 4;

   int domain();

   Object swingName();

   String rtfName();

   boolean set(MutableAttributeSet var1);

   boolean set(MutableAttributeSet var1, int var2);

   boolean setDefault(MutableAttributeSet var1);

   boolean write(AttributeSet var1, RTFGenerator var2, boolean var3) throws IOException;

   boolean writeValue(Object var1, RTFGenerator var2, boolean var3) throws IOException;
}
