package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class BasicColorChooserUI extends ColorChooserUI {
   protected JColorChooser chooser;
   JTabbedPane tabbedPane;
   JPanel singlePanel;
   JPanel previewPanelHolder;
   JComponent previewPanel;
   boolean isMultiPanel = false;
   private static TransferHandler defaultTransferHandler = new BasicColorChooserUI.ColorTransferHandler();
   protected AbstractColorChooserPanel[] defaultChoosers;
   protected ChangeListener previewListener;
   protected PropertyChangeListener propertyChangeListener;
   private BasicColorChooserUI.Handler handler;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicColorChooserUI();
   }

   protected AbstractColorChooserPanel[] createDefaultChoosers() {
      AbstractColorChooserPanel[] var1 = ColorChooserComponentFactory.getDefaultChooserPanels();
      return var1;
   }

   protected void uninstallDefaultChoosers() {
      AbstractColorChooserPanel[] var1 = this.chooser.getChooserPanels();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.chooser.removeChooserPanel(var1[var2]);
      }

   }

   public void installUI(JComponent var1) {
      this.chooser = (JColorChooser)var1;
      super.installUI(var1);
      this.installDefaults();
      this.installListeners();
      this.tabbedPane = new JTabbedPane();
      this.tabbedPane.setName("ColorChooser.tabPane");
      this.tabbedPane.setInheritsPopupMenu(true);
      this.tabbedPane.getAccessibleContext().setAccessibleDescription(this.tabbedPane.getName());
      this.singlePanel = new JPanel(new CenterLayout());
      this.singlePanel.setName("ColorChooser.panel");
      this.singlePanel.setInheritsPopupMenu(true);
      this.chooser.setLayout(new BorderLayout());
      this.defaultChoosers = this.createDefaultChoosers();
      this.chooser.setChooserPanels(this.defaultChoosers);
      this.previewPanelHolder = new JPanel(new CenterLayout());
      this.previewPanelHolder.setName("ColorChooser.previewPanelHolder");
      if (DefaultLookup.getBoolean(this.chooser, this, "ColorChooser.showPreviewPanelText", true)) {
         String var2 = UIManager.getString("ColorChooser.previewText", (Locale)this.chooser.getLocale());
         this.previewPanelHolder.setBorder(new TitledBorder(var2));
      }

      this.previewPanelHolder.setInheritsPopupMenu(true);
      this.installPreviewPanel();
      this.chooser.applyComponentOrientation(var1.getComponentOrientation());
   }

   public void uninstallUI(JComponent var1) {
      this.chooser.remove(this.tabbedPane);
      this.chooser.remove(this.singlePanel);
      this.chooser.remove(this.previewPanelHolder);
      this.uninstallDefaultChoosers();
      this.uninstallListeners();
      this.uninstallPreviewPanel();
      this.uninstallDefaults();
      this.previewPanelHolder = null;
      this.previewPanel = null;
      this.defaultChoosers = null;
      this.chooser = null;
      this.tabbedPane = null;
      this.handler = null;
   }

   protected void installPreviewPanel() {
      JComponent var1 = this.chooser.getPreviewPanel();
      if (var1 == null) {
         var1 = ColorChooserComponentFactory.getPreviewPanel();
      } else if (JPanel.class.equals(var1.getClass()) && 0 == var1.getComponentCount()) {
         var1 = null;
      }

      this.previewPanel = var1;
      if (var1 != null) {
         this.chooser.add(this.previewPanelHolder, "South");
         var1.setForeground(this.chooser.getColor());
         this.previewPanelHolder.add(var1);
         var1.addMouseListener(this.getHandler());
         var1.setInheritsPopupMenu(true);
      }

   }

   protected void uninstallPreviewPanel() {
      if (this.previewPanel != null) {
         this.previewPanel.removeMouseListener(this.getHandler());
         this.previewPanelHolder.remove(this.previewPanel);
      }

      this.chooser.remove(this.previewPanelHolder);
   }

   protected void installDefaults() {
      LookAndFeel.installColorsAndFont(this.chooser, "ColorChooser.background", "ColorChooser.foreground", "ColorChooser.font");
      LookAndFeel.installProperty(this.chooser, "opaque", Boolean.TRUE);
      TransferHandler var1 = this.chooser.getTransferHandler();
      if (var1 == null || var1 instanceof UIResource) {
         this.chooser.setTransferHandler(defaultTransferHandler);
      }

   }

   protected void uninstallDefaults() {
      if (this.chooser.getTransferHandler() instanceof UIResource) {
         this.chooser.setTransferHandler((TransferHandler)null);
      }

   }

   protected void installListeners() {
      this.propertyChangeListener = this.createPropertyChangeListener();
      this.chooser.addPropertyChangeListener(this.propertyChangeListener);
      this.previewListener = this.getHandler();
      this.chooser.getSelectionModel().addChangeListener(this.previewListener);
   }

   private BasicColorChooserUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicColorChooserUI.Handler();
      }

      return this.handler;
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   protected void uninstallListeners() {
      this.chooser.removePropertyChangeListener(this.propertyChangeListener);
      this.chooser.getSelectionModel().removeChangeListener(this.previewListener);
      this.previewListener = null;
   }

   private void selectionChanged(ColorSelectionModel var1) {
      JComponent var2 = this.chooser.getPreviewPanel();
      if (var2 != null) {
         var2.setForeground(var1.getSelectedColor());
         var2.repaint();
      }

      AbstractColorChooserPanel[] var3 = this.chooser.getChooserPanels();
      if (var3 != null) {
         AbstractColorChooserPanel[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            AbstractColorChooserPanel var7 = var4[var6];
            if (var7 != null) {
               var7.updateChooser();
            }
         }
      }

   }

   static class ColorTransferHandler extends TransferHandler implements UIResource {
      ColorTransferHandler() {
         super("color");
      }
   }

   public class PropertyHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicColorChooserUI.this.getHandler().propertyChange(var1);
      }
   }

   private class Handler implements ChangeListener, MouseListener, PropertyChangeListener {
      private Handler() {
      }

      public void stateChanged(ChangeEvent var1) {
         BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)var1.getSource());
      }

      public void mousePressed(MouseEvent var1) {
         if (BasicColorChooserUI.this.chooser.getDragEnabled()) {
            TransferHandler var2 = BasicColorChooserUI.this.chooser.getTransferHandler();
            var2.exportAsDrag(BasicColorChooserUI.this.chooser, var1, 1);
         }

      }

      public void mouseReleased(MouseEvent var1) {
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "chooserPanels") {
            AbstractColorChooserPanel[] var3 = (AbstractColorChooserPanel[])((AbstractColorChooserPanel[])var1.getOldValue());
            AbstractColorChooserPanel[] var4 = (AbstractColorChooserPanel[])((AbstractColorChooserPanel[])var1.getNewValue());

            int var5;
            for(var5 = 0; var5 < var3.length; ++var5) {
               Container var6 = var3[var5].getParent();
               if (var6 != null) {
                  Container var7 = var6.getParent();
                  if (var7 != null) {
                     var7.remove(var6);
                  }

                  var3[var5].uninstallChooserPanel(BasicColorChooserUI.this.chooser);
               }
            }

            var5 = var4.length;
            if (var5 == 0) {
               BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
               return;
            }

            int var15;
            if (var5 == 1) {
               BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
               JPanel var16 = new JPanel(new CenterLayout());
               var16.setInheritsPopupMenu(true);
               var16.add(var4[0]);
               BasicColorChooserUI.this.singlePanel.add(var16, "Center");
               BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.singlePanel);
            } else {
               if (var3.length < 2) {
                  BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.singlePanel);
                  BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.tabbedPane, "Center");
               }

               for(var15 = 0; var15 < var4.length; ++var15) {
                  JPanel var17 = new JPanel(new CenterLayout());
                  var17.setInheritsPopupMenu(true);
                  String var8 = var4[var15].getDisplayName();
                  int var9 = var4[var15].getMnemonic();
                  var17.add(var4[var15]);
                  BasicColorChooserUI.this.tabbedPane.addTab(var8, var17);
                  if (var9 > 0) {
                     BasicColorChooserUI.this.tabbedPane.setMnemonicAt(var15, var9);
                     int var10 = var4[var15].getDisplayedMnemonicIndex();
                     if (var10 >= 0) {
                        BasicColorChooserUI.this.tabbedPane.setDisplayedMnemonicIndexAt(var15, var10);
                     }
                  }
               }
            }

            BasicColorChooserUI.this.chooser.applyComponentOrientation(BasicColorChooserUI.this.chooser.getComponentOrientation());

            for(var15 = 0; var15 < var4.length; ++var15) {
               var4[var15].installChooserPanel(BasicColorChooserUI.this.chooser);
            }
         } else if (var2 == "previewPanel") {
            BasicColorChooserUI.this.uninstallPreviewPanel();
            BasicColorChooserUI.this.installPreviewPanel();
         } else if (var2 == "selectionModel") {
            ColorSelectionModel var11 = (ColorSelectionModel)var1.getOldValue();
            var11.removeChangeListener(BasicColorChooserUI.this.previewListener);
            ColorSelectionModel var13 = (ColorSelectionModel)var1.getNewValue();
            var13.addChangeListener(BasicColorChooserUI.this.previewListener);
            BasicColorChooserUI.this.selectionChanged(var13);
         } else if (var2 == "componentOrientation") {
            ComponentOrientation var12 = (ComponentOrientation)var1.getNewValue();
            JColorChooser var14 = (JColorChooser)var1.getSource();
            if (var12 != (ComponentOrientation)var1.getOldValue()) {
               var14.applyComponentOrientation(var12);
               var14.updateUI();
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
