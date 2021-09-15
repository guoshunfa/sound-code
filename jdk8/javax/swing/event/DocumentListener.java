package javax.swing.event;

import java.util.EventListener;

public interface DocumentListener extends EventListener {
   void insertUpdate(DocumentEvent var1);

   void removeUpdate(DocumentEvent var1);

   void changedUpdate(DocumentEvent var1);
}
