package javax.swing.text;

import java.util.Enumeration;

public interface MutableAttributeSet extends AttributeSet {
   void addAttribute(Object var1, Object var2);

   void addAttributes(AttributeSet var1);

   void removeAttribute(Object var1);

   void removeAttributes(Enumeration<?> var1);

   void removeAttributes(AttributeSet var1);

   void setResolveParent(AttributeSet var1);
}
