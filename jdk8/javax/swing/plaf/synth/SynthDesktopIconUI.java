package javax.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class SynthDesktopIconUI extends BasicDesktopIconUI implements SynthUI, PropertyChangeListener {
   private SynthStyle style;
   private SynthDesktopIconUI.Handler handler = new SynthDesktopIconUI.Handler();

   public static ComponentUI createUI(JComponent var0) {
      return new SynthDesktopIconUI();
   }

   protected void installComponents() {
      if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
         this.iconPane = new JToggleButton(this.frame.getTitle(), this.frame.getFrameIcon()) {
            public String getToolTipText() {
               return this.getText();
            }

            public JPopupMenu getComponentPopupMenu() {
               return SynthDesktopIconUI.this.frame.getComponentPopupMenu();
            }
         };
         ToolTipManager.sharedInstance().registerComponent(this.iconPane);
         this.iconPane.setFont(this.desktopIcon.getFont());
         this.iconPane.setBackground(this.desktopIcon.getBackground());
         this.iconPane.setForeground(this.desktopIcon.getForeground());
      } else {
         this.iconPane = new SynthInternalFrameTitlePane(this.frame);
         this.iconPane.setName("InternalFrame.northPane");
      }

      this.desktopIcon.setLayout(new BorderLayout());
      this.desktopIcon.add(this.iconPane, "Center");
   }

   protected void installListeners() {
      super.installListeners();
      this.desktopIcon.addPropertyChangeListener(this);
      if (this.iconPane instanceof JToggleButton) {
         this.frame.addPropertyChangeListener(this);
         ((JToggleButton)this.iconPane).addActionListener(this.handler);
      }

   }

   protected void uninstallListeners() {
      if (this.iconPane instanceof JToggleButton) {
         ((JToggleButton)this.iconPane).removeActionListener(this.handler);
         this.frame.removePropertyChangeListener(this);
      }

      this.desktopIcon.removePropertyChangeListener(this);
      super.uninstallListeners();
   }

   protected void installDefaults() {
      this.updateStyle(this.desktopIcon);
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.desktopIcon, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      return SynthLookAndFeel.getComponentState(var1);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintDesktopIconBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintDesktopIconBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (var1.getSource() instanceof JInternalFrame.JDesktopIcon) {
         if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
            this.updateStyle((JInternalFrame.JDesktopIcon)var1.getSource());
         }
      } else if (var1.getSource() instanceof JInternalFrame) {
         JInternalFrame var2 = (JInternalFrame)var1.getSource();
         if (this.iconPane instanceof JToggleButton) {
            JToggleButton var3 = (JToggleButton)this.iconPane;
            String var4 = var1.getPropertyName();
            if (var4 == "title") {
               var3.setText((String)var1.getNewValue());
            } else if (var4 == "frameIcon") {
               var3.setIcon((Icon)var1.getNewValue());
            } else if (var4 == "icon" || var4 == "selected") {
               var3.setSelected(!var2.isIcon() && var2.isSelected());
            }
         }
      }

   }

   private final class Handler implements ActionListener {
      private Handler() {
      }

      public void actionPerformed(ActionEvent var1) {
         if (var1.getSource() instanceof JToggleButton) {
            JToggleButton var2 = (JToggleButton)var1.getSource();

            try {
               boolean var3 = var2.isSelected();
               if (!var3 && !SynthDesktopIconUI.this.frame.isIconifiable()) {
                  var2.setSelected(true);
               } else {
                  SynthDesktopIconUI.this.frame.setIcon(!var3);
                  if (var3) {
                     SynthDesktopIconUI.this.frame.setSelected(true);
                  }
               }
            } catch (PropertyVetoException var4) {
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
