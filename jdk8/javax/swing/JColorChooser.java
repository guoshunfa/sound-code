package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;

public class JColorChooser extends JComponent implements Accessible {
   private static final String uiClassID = "ColorChooserUI";
   private ColorSelectionModel selectionModel;
   private JComponent previewPanel;
   private AbstractColorChooserPanel[] chooserPanels;
   private boolean dragEnabled;
   public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
   public static final String PREVIEW_PANEL_PROPERTY = "previewPanel";
   public static final String CHOOSER_PANELS_PROPERTY = "chooserPanels";
   protected AccessibleContext accessibleContext;

   public static Color showDialog(Component var0, String var1, Color var2) throws HeadlessException {
      JColorChooser var3 = new JColorChooser(var2 != null ? var2 : Color.white);
      ColorTracker var4 = new ColorTracker(var3);
      JDialog var5 = createDialog(var0, var1, true, var3, var4, (ActionListener)null);
      var5.addComponentListener(new ColorChooserDialog.DisposeOnClose());
      var5.show();
      return var4.getColor();
   }

   public static JDialog createDialog(Component var0, String var1, boolean var2, JColorChooser var3, ActionListener var4, ActionListener var5) throws HeadlessException {
      Window var6 = JOptionPane.getWindowForComponent(var0);
      ColorChooserDialog var7;
      if (var6 instanceof Frame) {
         var7 = new ColorChooserDialog((Frame)var6, var1, var2, var0, var3, var4, var5);
      } else {
         var7 = new ColorChooserDialog((Dialog)var6, var1, var2, var0, var3, var4, var5);
      }

      var7.getAccessibleContext().setAccessibleDescription(var1);
      return var7;
   }

   public JColorChooser() {
      this(Color.white);
   }

   public JColorChooser(Color var1) {
      this((ColorSelectionModel)(new DefaultColorSelectionModel(var1)));
   }

   public JColorChooser(ColorSelectionModel var1) {
      this.previewPanel = ColorChooserComponentFactory.getPreviewPanel();
      this.chooserPanels = new AbstractColorChooserPanel[0];
      this.accessibleContext = null;
      this.selectionModel = var1;
      this.updateUI();
      this.dragEnabled = false;
   }

   public ColorChooserUI getUI() {
      return (ColorChooserUI)this.ui;
   }

   public void setUI(ColorChooserUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ColorChooserUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ColorChooserUI";
   }

   public Color getColor() {
      return this.selectionModel.getSelectedColor();
   }

   public void setColor(Color var1) {
      this.selectionModel.setSelectedColor(var1);
   }

   public void setColor(int var1, int var2, int var3) {
      this.setColor(new Color(var1, var2, var3));
   }

   public void setColor(int var1) {
      this.setColor(var1 >> 16 & 255, var1 >> 8 & 255, var1 & 255);
   }

   public void setDragEnabled(boolean var1) {
      if (var1 && GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         this.dragEnabled = var1;
      }
   }

   public boolean getDragEnabled() {
      return this.dragEnabled;
   }

   public void setPreviewPanel(JComponent var1) {
      if (this.previewPanel != var1) {
         JComponent var2 = this.previewPanel;
         this.previewPanel = var1;
         this.firePropertyChange("previewPanel", var2, var1);
      }

   }

   public JComponent getPreviewPanel() {
      return this.previewPanel;
   }

   public void addChooserPanel(AbstractColorChooserPanel var1) {
      AbstractColorChooserPanel[] var2 = this.getChooserPanels();
      AbstractColorChooserPanel[] var3 = new AbstractColorChooserPanel[var2.length + 1];
      System.arraycopy(var2, 0, var3, 0, var2.length);
      var3[var3.length - 1] = var1;
      this.setChooserPanels(var3);
   }

   public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel var1) {
      int var2 = -1;

      for(int var3 = 0; var3 < this.chooserPanels.length; ++var3) {
         if (this.chooserPanels[var3] == var1) {
            var2 = var3;
            break;
         }
      }

      if (var2 == -1) {
         throw new IllegalArgumentException("chooser panel not in this chooser");
      } else {
         AbstractColorChooserPanel[] var4 = new AbstractColorChooserPanel[this.chooserPanels.length - 1];
         if (var2 == this.chooserPanels.length - 1) {
            System.arraycopy(this.chooserPanels, 0, var4, 0, var4.length);
         } else if (var2 == 0) {
            System.arraycopy(this.chooserPanels, 1, var4, 0, var4.length);
         } else {
            System.arraycopy(this.chooserPanels, 0, var4, 0, var2);
            System.arraycopy(this.chooserPanels, var2 + 1, var4, var2, this.chooserPanels.length - var2 - 1);
         }

         this.setChooserPanels(var4);
         return var1;
      }
   }

   public void setChooserPanels(AbstractColorChooserPanel[] var1) {
      AbstractColorChooserPanel[] var2 = this.chooserPanels;
      this.chooserPanels = var1;
      this.firePropertyChange("chooserPanels", var2, var1);
   }

   public AbstractColorChooserPanel[] getChooserPanels() {
      return this.chooserPanels;
   }

   public ColorSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   public void setSelectionModel(ColorSelectionModel var1) {
      ColorSelectionModel var2 = this.selectionModel;
      this.selectionModel = var1;
      this.firePropertyChange("selectionModel", var2, var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ColorChooserUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      StringBuffer var1 = new StringBuffer("");

      for(int var2 = 0; var2 < this.chooserPanels.length; ++var2) {
         var1.append("[" + this.chooserPanels[var2].toString() + "]");
      }

      String var3 = this.previewPanel != null ? this.previewPanel.toString() : "";
      return super.paramString() + ",chooserPanels=" + var1.toString() + ",previewPanel=" + var3;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JColorChooser.AccessibleJColorChooser();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJColorChooser extends JComponent.AccessibleJComponent {
      protected AccessibleJColorChooser() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.COLOR_CHOOSER;
      }
   }
}
