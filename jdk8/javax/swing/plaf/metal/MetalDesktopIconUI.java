package javax.swing.plaf.metal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class MetalDesktopIconUI extends BasicDesktopIconUI {
   JButton button;
   JLabel label;
   MetalDesktopIconUI.TitleListener titleListener;
   private int width;

   public static ComponentUI createUI(JComponent var0) {
      return new MetalDesktopIconUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      LookAndFeel.installColorsAndFont(this.desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
      this.width = UIManager.getInt("DesktopIcon.width");
   }

   protected void installComponents() {
      this.frame = this.desktopIcon.getInternalFrame();
      Icon var1 = this.frame.getFrameIcon();
      String var2 = this.frame.getTitle();
      this.button = new JButton(var2, var1);
      this.button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            MetalDesktopIconUI.this.deiconize();
         }
      });
      this.button.setFont(this.desktopIcon.getFont());
      this.button.setBackground(this.desktopIcon.getBackground());
      this.button.setForeground(this.desktopIcon.getForeground());
      int var3 = this.button.getPreferredSize().height;
      MetalBumps var4 = new MetalBumps(var3 / 3, var3, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
      this.label = new JLabel(var4);
      this.label.setBorder(new MatteBorder(0, 2, 0, 1, this.desktopIcon.getBackground()));
      this.desktopIcon.setLayout(new BorderLayout(2, 0));
      this.desktopIcon.add(this.button, "Center");
      this.desktopIcon.add(this.label, "West");
   }

   protected void uninstallComponents() {
      this.desktopIcon.setLayout((LayoutManager)null);
      this.desktopIcon.remove(this.label);
      this.desktopIcon.remove(this.button);
      this.button = null;
      this.frame = null;
   }

   protected void installListeners() {
      super.installListeners();
      this.desktopIcon.getInternalFrame().addPropertyChangeListener(this.titleListener = new MetalDesktopIconUI.TitleListener());
   }

   protected void uninstallListeners() {
      this.desktopIcon.getInternalFrame().removePropertyChangeListener(this.titleListener);
      this.titleListener = null;
      super.uninstallListeners();
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return new Dimension(this.width, this.desktopIcon.getLayout().minimumLayoutSize(this.desktopIcon).height);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return this.getMinimumSize(var1);
   }

   class TitleListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getPropertyName().equals("title")) {
            MetalDesktopIconUI.this.button.setText((String)var1.getNewValue());
         }

         if (var1.getPropertyName().equals("frameIcon")) {
            MetalDesktopIconUI.this.button.setIcon((Icon)var1.getNewValue());
         }

      }
   }
}
