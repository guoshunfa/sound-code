package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

public abstract class AbstractColorChooserPanel extends JPanel {
   private final PropertyChangeListener enabledListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent var1) {
         Object var2 = var1.getNewValue();
         if (var2 instanceof Boolean) {
            AbstractColorChooserPanel.this.setEnabled((Boolean)var2);
         }

      }
   };
   private JColorChooser chooser;

   public abstract void updateChooser();

   protected abstract void buildChooser();

   public abstract String getDisplayName();

   public int getMnemonic() {
      return 0;
   }

   public int getDisplayedMnemonicIndex() {
      return -1;
   }

   public abstract Icon getSmallDisplayIcon();

   public abstract Icon getLargeDisplayIcon();

   public void installChooserPanel(JColorChooser var1) {
      if (this.chooser != null) {
         throw new RuntimeException("This chooser panel is already installed");
      } else {
         this.chooser = var1;
         this.chooser.addPropertyChangeListener("enabled", this.enabledListener);
         this.setEnabled(this.chooser.isEnabled());
         this.buildChooser();
         this.updateChooser();
      }
   }

   public void uninstallChooserPanel(JColorChooser var1) {
      this.chooser.removePropertyChangeListener("enabled", this.enabledListener);
      this.chooser = null;
   }

   public ColorSelectionModel getColorSelectionModel() {
      return this.chooser != null ? this.chooser.getSelectionModel() : null;
   }

   protected Color getColorFromModel() {
      ColorSelectionModel var1 = this.getColorSelectionModel();
      return var1 != null ? var1.getSelectedColor() : null;
   }

   void setSelectedColor(Color var1) {
      ColorSelectionModel var2 = this.getColorSelectionModel();
      if (var2 != null) {
         var2.setSelectedColor(var1);
      }

   }

   public void paint(Graphics var1) {
      super.paint(var1);
   }

   int getInt(Object var1, int var2) {
      Object var3 = UIManager.get(var1, this.getLocale());
      if (var3 instanceof Integer) {
         return (Integer)var3;
      } else {
         if (var3 instanceof String) {
            try {
               return Integer.parseInt((String)var3);
            } catch (NumberFormatException var5) {
            }
         }

         return var2;
      }
   }
}
