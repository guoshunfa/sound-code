package javax.swing.colorchooser;

import java.awt.Color;
import javax.swing.event.ChangeListener;

public interface ColorSelectionModel {
   Color getSelectedColor();

   void setSelectedColor(Color var1);

   void addChangeListener(ChangeListener var1);

   void removeChangeListener(ChangeListener var1);
}
