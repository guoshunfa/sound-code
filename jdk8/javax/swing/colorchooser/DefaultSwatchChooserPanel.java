package javax.swing.colorchooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

class DefaultSwatchChooserPanel extends AbstractColorChooserPanel {
   SwatchPanel swatchPanel;
   RecentSwatchPanel recentSwatchPanel;
   MouseListener mainSwatchListener;
   MouseListener recentSwatchListener;
   private KeyListener mainSwatchKeyListener;
   private KeyListener recentSwatchKeyListener;

   public DefaultSwatchChooserPanel() {
      this.setInheritsPopupMenu(true);
   }

   public String getDisplayName() {
      return UIManager.getString("ColorChooser.swatchesNameText", (Locale)this.getLocale());
   }

   public int getMnemonic() {
      return this.getInt("ColorChooser.swatchesMnemonic", -1);
   }

   public int getDisplayedMnemonicIndex() {
      return this.getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1);
   }

   public Icon getSmallDisplayIcon() {
      return null;
   }

   public Icon getLargeDisplayIcon() {
      return null;
   }

   public void installChooserPanel(JColorChooser var1) {
      super.installChooserPanel(var1);
   }

   protected void buildChooser() {
      String var1 = UIManager.getString("ColorChooser.swatchesRecentText", (Locale)this.getLocale());
      GridBagLayout var2 = new GridBagLayout();
      GridBagConstraints var3 = new GridBagConstraints();
      JPanel var4 = new JPanel(var2);
      this.swatchPanel = new MainSwatchPanel();
      this.swatchPanel.putClientProperty("AccessibleName", this.getDisplayName());
      this.swatchPanel.setInheritsPopupMenu(true);
      this.recentSwatchPanel = new RecentSwatchPanel();
      this.recentSwatchPanel.putClientProperty("AccessibleName", var1);
      this.mainSwatchKeyListener = new DefaultSwatchChooserPanel.MainSwatchKeyListener();
      this.mainSwatchListener = new DefaultSwatchChooserPanel.MainSwatchListener();
      this.swatchPanel.addMouseListener(this.mainSwatchListener);
      this.swatchPanel.addKeyListener(this.mainSwatchKeyListener);
      this.recentSwatchListener = new DefaultSwatchChooserPanel.RecentSwatchListener();
      this.recentSwatchKeyListener = new DefaultSwatchChooserPanel.RecentSwatchKeyListener();
      this.recentSwatchPanel.addMouseListener(this.recentSwatchListener);
      this.recentSwatchPanel.addKeyListener(this.recentSwatchKeyListener);
      JPanel var5 = new JPanel(new BorderLayout());
      CompoundBorder var6 = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
      var5.setBorder(var6);
      var5.add(this.swatchPanel, "Center");
      var3.anchor = 25;
      var3.gridwidth = 1;
      var3.gridheight = 2;
      Insets var7 = var3.insets;
      var3.insets = new Insets(0, 0, 0, 10);
      var4.add(var5, var3);
      var3.insets = var7;
      this.recentSwatchPanel.setInheritsPopupMenu(true);
      JPanel var8 = new JPanel(new BorderLayout());
      var8.setBorder(var6);
      var8.setInheritsPopupMenu(true);
      var8.add(this.recentSwatchPanel, "Center");
      JLabel var9 = new JLabel(var1);
      var9.setLabelFor(this.recentSwatchPanel);
      var3.gridwidth = 0;
      var3.gridheight = 1;
      var3.weighty = 1.0D;
      var4.add(var9, var3);
      var3.weighty = 0.0D;
      var3.gridheight = 0;
      var3.insets = new Insets(0, 0, 0, 2);
      var4.add(var8, var3);
      var4.setInheritsPopupMenu(true);
      this.add(var4);
   }

   public void uninstallChooserPanel(JColorChooser var1) {
      super.uninstallChooserPanel(var1);
      this.swatchPanel.removeMouseListener(this.mainSwatchListener);
      this.swatchPanel.removeKeyListener(this.mainSwatchKeyListener);
      this.recentSwatchPanel.removeMouseListener(this.recentSwatchListener);
      this.recentSwatchPanel.removeKeyListener(this.recentSwatchKeyListener);
      this.swatchPanel = null;
      this.recentSwatchPanel = null;
      this.mainSwatchListener = null;
      this.mainSwatchKeyListener = null;
      this.recentSwatchListener = null;
      this.recentSwatchKeyListener = null;
      this.removeAll();
   }

   public void updateChooser() {
   }

   class MainSwatchListener extends MouseAdapter implements Serializable {
      public void mousePressed(MouseEvent var1) {
         if (DefaultSwatchChooserPanel.this.isEnabled()) {
            Color var2 = DefaultSwatchChooserPanel.this.swatchPanel.getColorForLocation(var1.getX(), var1.getY());
            DefaultSwatchChooserPanel.this.setSelectedColor(var2);
            DefaultSwatchChooserPanel.this.swatchPanel.setSelectedColorFromLocation(var1.getX(), var1.getY());
            DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(var2);
            DefaultSwatchChooserPanel.this.swatchPanel.requestFocusInWindow();
         }

      }
   }

   class RecentSwatchListener extends MouseAdapter implements Serializable {
      public void mousePressed(MouseEvent var1) {
         if (DefaultSwatchChooserPanel.this.isEnabled()) {
            Color var2 = DefaultSwatchChooserPanel.this.recentSwatchPanel.getColorForLocation(var1.getX(), var1.getY());
            DefaultSwatchChooserPanel.this.recentSwatchPanel.setSelectedColorFromLocation(var1.getX(), var1.getY());
            DefaultSwatchChooserPanel.this.setSelectedColor(var2);
            DefaultSwatchChooserPanel.this.recentSwatchPanel.requestFocusInWindow();
         }

      }
   }

   private class MainSwatchKeyListener extends KeyAdapter {
      private MainSwatchKeyListener() {
      }

      public void keyPressed(KeyEvent var1) {
         if (32 == var1.getKeyCode()) {
            Color var2 = DefaultSwatchChooserPanel.this.swatchPanel.getSelectedColor();
            DefaultSwatchChooserPanel.this.setSelectedColor(var2);
            DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(var2);
         }

      }

      // $FF: synthetic method
      MainSwatchKeyListener(Object var2) {
         this();
      }
   }

   private class RecentSwatchKeyListener extends KeyAdapter {
      private RecentSwatchKeyListener() {
      }

      public void keyPressed(KeyEvent var1) {
         if (32 == var1.getKeyCode()) {
            Color var2 = DefaultSwatchChooserPanel.this.recentSwatchPanel.getSelectedColor();
            DefaultSwatchChooserPanel.this.setSelectedColor(var2);
         }

      }

      // $FF: synthetic method
      RecentSwatchKeyListener(Object var2) {
         this();
      }
   }
}
