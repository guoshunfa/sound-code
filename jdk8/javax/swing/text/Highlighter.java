package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;

public interface Highlighter {
   void install(JTextComponent var1);

   void deinstall(JTextComponent var1);

   void paint(Graphics var1);

   Object addHighlight(int var1, int var2, Highlighter.HighlightPainter var3) throws BadLocationException;

   void removeHighlight(Object var1);

   void removeAllHighlights();

   void changeHighlight(Object var1, int var2, int var3) throws BadLocationException;

   Highlighter.Highlight[] getHighlights();

   public interface Highlight {
      int getStartOffset();

      int getEndOffset();

      Highlighter.HighlightPainter getPainter();
   }

   public interface HighlightPainter {
      void paint(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5);
   }
}
