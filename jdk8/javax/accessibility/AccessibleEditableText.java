package javax.accessibility;

import javax.swing.text.AttributeSet;

public interface AccessibleEditableText extends AccessibleText {
   void setTextContents(String var1);

   void insertTextAtIndex(int var1, String var2);

   String getTextRange(int var1, int var2);

   void delete(int var1, int var2);

   void cut(int var1, int var2);

   void paste(int var1);

   void replaceText(int var1, int var2, String var3);

   void selectText(int var1, int var2);

   void setAttributes(int var1, int var2, AttributeSet var3);
}
