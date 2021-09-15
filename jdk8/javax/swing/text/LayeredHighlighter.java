package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;

public abstract class LayeredHighlighter implements Highlighter {
   public abstract void paintLayeredHighlights(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6);

   public abstract static class LayerPainter implements Highlighter.HighlightPainter {
      public abstract Shape paintLayer(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6);
   }
}
