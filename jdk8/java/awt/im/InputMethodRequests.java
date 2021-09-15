package java.awt.im;

import java.awt.Rectangle;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;

public interface InputMethodRequests {
   Rectangle getTextLocation(TextHitInfo var1);

   TextHitInfo getLocationOffset(int var1, int var2);

   int getInsertPositionOffset();

   AttributedCharacterIterator getCommittedText(int var1, int var2, AttributedCharacterIterator.Attribute[] var3);

   int getCommittedTextLength();

   AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] var1);

   AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] var1);
}
