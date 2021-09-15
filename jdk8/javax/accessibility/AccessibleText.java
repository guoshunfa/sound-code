package javax.accessibility;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.text.AttributeSet;

public interface AccessibleText {
   int CHARACTER = 1;
   int WORD = 2;
   int SENTENCE = 3;

   int getIndexAtPoint(Point var1);

   Rectangle getCharacterBounds(int var1);

   int getCharCount();

   int getCaretPosition();

   String getAtIndex(int var1, int var2);

   String getAfterIndex(int var1, int var2);

   String getBeforeIndex(int var1, int var2);

   AttributeSet getCharacterAttribute(int var1);

   int getSelectionStart();

   int getSelectionEnd();

   String getSelectedText();
}
