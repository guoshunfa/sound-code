package javax.accessibility;

import java.awt.Rectangle;

public interface AccessibleExtendedText {
   int LINE = 4;
   int ATTRIBUTE_RUN = 5;

   String getTextRange(int var1, int var2);

   AccessibleTextSequence getTextSequenceAt(int var1, int var2);

   AccessibleTextSequence getTextSequenceAfter(int var1, int var2);

   AccessibleTextSequence getTextSequenceBefore(int var1, int var2);

   Rectangle getTextBounds(int var1, int var2);
}
