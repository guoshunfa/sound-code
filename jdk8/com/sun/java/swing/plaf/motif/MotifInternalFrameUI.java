package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MotifInternalFrameUI extends BasicInternalFrameUI {
   Color color;
   Color highlight;
   Color shadow;
   MotifInternalFrameTitlePane titlePane;
   /** @deprecated */
   @Deprecated
   protected KeyStroke closeMenuKey;

   public static ComponentUI createUI(JComponent var0) {
      return new MotifInternalFrameUI((JInternalFrame)var0);
   }

   public MotifInternalFrameUI(JInternalFrame var1) {
      super(var1);
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.setColors((JInternalFrame)var1);
   }

   protected void installDefaults() {
      Border var1 = this.frame.getBorder();
      this.frame.setLayout(this.internalFrameLayout = this.createLayoutManager());
      if (var1 == null || var1 instanceof UIResource) {
         this.frame.setBorder(new MotifBorders.InternalFrameBorder(this.frame));
      }

   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      this.closeMenuKey = KeyStroke.getKeyStroke(27, 0);
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.frame);
      this.frame.setLayout((LayoutManager)null);
      this.internalFrameLayout = null;
   }

   private JInternalFrame getFrame() {
      return this.frame;
   }

   public JComponent createNorthPane(JInternalFrame var1) {
      this.titlePane = new MotifInternalFrameTitlePane(var1);
      return this.titlePane;
   }

   public Dimension getMaximumSize(JComponent var1) {
      return Toolkit.getDefaultToolkit().getScreenSize();
   }

   protected void uninstallKeyboardActions() {
      super.uninstallKeyboardActions();
      if (this.isKeyBindingRegistered()) {
         JInternalFrame.JDesktopIcon var1 = this.frame.getDesktopIcon();
         SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
         SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)null);
      }

   }

   protected void setupMenuOpenKey() {
      super.setupMenuOpenKey();
      ActionMap var1 = SwingUtilities.getUIActionMap(this.frame);
      if (var1 != null) {
         var1.put("showSystemMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent var1) {
               MotifInternalFrameUI.this.titlePane.showSystemMenu();
            }

            public boolean isEnabled() {
               return MotifInternalFrameUI.this.isKeyBindingActive();
            }
         });
      }

   }

   protected void setupMenuCloseKey() {
      ActionMap var1 = SwingUtilities.getUIActionMap(this.frame);
      if (var1 != null) {
         var1.put("hideSystemMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent var1) {
               MotifInternalFrameUI.this.titlePane.hideSystemMenu();
            }

            public boolean isEnabled() {
               return MotifInternalFrameUI.this.isKeyBindingActive();
            }
         });
      }

      JInternalFrame.JDesktopIcon var2 = this.frame.getDesktopIcon();
      InputMap var3 = SwingUtilities.getUIInputMap(var2, 2);
      if (var3 == null) {
         Object[] var4 = (Object[])((Object[])UIManager.get("DesktopIcon.windowBindings"));
         if (var4 != null) {
            ComponentInputMap var5 = LookAndFeel.makeComponentInputMap(var2, var4);
            SwingUtilities.replaceUIInputMap(var2, 2, var5);
         }
      }

      ActionMap var6 = SwingUtilities.getUIActionMap(var2);
      if (var6 == null) {
         ActionMapUIResource var7 = new ActionMapUIResource();
         var7.put("hideSystemMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent var1) {
               JInternalFrame.JDesktopIcon var2 = MotifInternalFrameUI.this.getFrame().getDesktopIcon();
               MotifDesktopIconUI var3 = (MotifDesktopIconUI)var2.getUI();
               var3.hideSystemMenu();
            }

            public boolean isEnabled() {
               return MotifInternalFrameUI.this.isKeyBindingActive();
            }
         });
         SwingUtilities.replaceUIActionMap(var2, var7);
      }

   }

   protected void activateFrame(JInternalFrame var1) {
      super.activateFrame(var1);
      this.setColors(var1);
   }

   protected void deactivateFrame(JInternalFrame var1) {
      this.setColors(var1);
      super.deactivateFrame(var1);
   }

   void setColors(JInternalFrame var1) {
      if (var1.isSelected()) {
         this.color = UIManager.getColor("InternalFrame.activeTitleBackground");
      } else {
         this.color = UIManager.getColor("InternalFrame.inactiveTitleBackground");
      }

      this.highlight = this.color.brighter();
      this.shadow = this.color.darker().darker();
      this.titlePane.setColors(this.color, this.highlight, this.shadow);
   }
}
