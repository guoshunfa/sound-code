package javax.swing.plaf;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public abstract class TextUI extends ComponentUI {
   public abstract Rectangle modelToView(JTextComponent var1, int var2) throws BadLocationException;

   public abstract Rectangle modelToView(JTextComponent var1, int var2, Position.Bias var3) throws BadLocationException;

   public abstract int viewToModel(JTextComponent var1, Point var2);

   public abstract int viewToModel(JTextComponent var1, Point var2, Position.Bias[] var3);

   public abstract int getNextVisualPositionFrom(JTextComponent var1, int var2, Position.Bias var3, int var4, Position.Bias[] var5) throws BadLocationException;

   public abstract void damageRange(JTextComponent var1, int var2, int var3);

   public abstract void damageRange(JTextComponent var1, int var2, int var3, Position.Bias var4, Position.Bias var5);

   public abstract EditorKit getEditorKit(JTextComponent var1);

   public abstract View getRootView(JTextComponent var1);

   public String getToolTipText(JTextComponent var1, Point var2) {
      return null;
   }
}
