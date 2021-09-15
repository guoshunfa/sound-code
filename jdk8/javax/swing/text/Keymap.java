package javax.swing.text;

import javax.swing.Action;
import javax.swing.KeyStroke;

public interface Keymap {
   String getName();

   Action getDefaultAction();

   void setDefaultAction(Action var1);

   Action getAction(KeyStroke var1);

   KeyStroke[] getBoundKeyStrokes();

   Action[] getBoundActions();

   KeyStroke[] getKeyStrokesForAction(Action var1);

   boolean isLocallyDefined(KeyStroke var1);

   void addActionForKeyStroke(KeyStroke var1, Action var2);

   void removeKeyStrokeBinding(KeyStroke var1);

   void removeBindings();

   Keymap getResolveParent();

   void setResolveParent(Keymap var1);
}
