package javax.swing.text;

import java.awt.Color;
import java.awt.Font;

public interface StyledDocument extends Document {
   Style addStyle(String var1, Style var2);

   void removeStyle(String var1);

   Style getStyle(String var1);

   void setCharacterAttributes(int var1, int var2, AttributeSet var3, boolean var4);

   void setParagraphAttributes(int var1, int var2, AttributeSet var3, boolean var4);

   void setLogicalStyle(int var1, Style var2);

   Style getLogicalStyle(int var1);

   Element getParagraphElement(int var1);

   Element getCharacterElement(int var1);

   Color getForeground(AttributeSet var1);

   Color getBackground(AttributeSet var1);

   Font getFont(AttributeSet var1);
}
