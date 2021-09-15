package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class SynthInternalFrameUI extends BasicInternalFrameUI implements SynthUI, PropertyChangeListener {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthInternalFrameUI((JInternalFrame)var0);
   }

   protected SynthInternalFrameUI(JInternalFrame var1) {
      super(var1);
   }

   public void installDefaults() {
      this.frame.setLayout(this.internalFrameLayout = this.createLayoutManager());
      this.updateStyle(this.frame);
   }

   protected void installListeners() {
      super.installListeners();
      this.frame.addPropertyChangeListener(this);
   }

   protected void uninstallComponents() {
      if (this.frame.getComponentPopupMenu() instanceof UIResource) {
         this.frame.setComponentPopupMenu((JPopupMenu)null);
      }

      super.uninstallComponents();
   }

   protected void uninstallListeners() {
      this.frame.removePropertyChangeListener(this);
      super.uninstallListeners();
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         Icon var4 = this.frame.getFrameIcon();
         if (var4 == null || var4 instanceof UIResource) {
            this.frame.setFrameIcon(var2.getStyle().getIcon(var2, "InternalFrame.icon"));
         }

         if (var3 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.frame, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      if (this.frame.getLayout() == this.internalFrameLayout) {
         this.frame.setLayout((LayoutManager)null);
      }

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

   protected JComponent createNorthPane(JInternalFrame var1) {
      this.titlePane = new SynthInternalFrameTitlePane(var1);
      this.titlePane.setName("InternalFrame.northPane");
      return this.titlePane;
   }

   protected ComponentListener createComponentListener() {
      return (ComponentListener)(UIManager.getBoolean("InternalFrame.useTaskBar") ? new BasicInternalFrameUI.ComponentHandler() {
         public void componentResized(ComponentEvent var1) {
            if (SynthInternalFrameUI.this.frame != null && SynthInternalFrameUI.this.frame.isMaximum()) {
               JDesktopPane var2 = (JDesktopPane)var1.getSource();
               Component[] var3 = var2.getComponents();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Component var6 = var3[var5];
                  if (var6 instanceof SynthDesktopPaneUI.TaskBar) {
                     SynthInternalFrameUI.this.frame.setBounds(0, 0, var2.getWidth(), var2.getHeight() - var6.getHeight());
                     SynthInternalFrameUI.this.frame.revalidate();
                     break;
                  }
               }
            }

            JInternalFrame var7 = SynthInternalFrameUI.this.frame;
            SynthInternalFrameUI.this.frame = null;
            super.componentResized(var1);
            SynthInternalFrameUI.this.frame = var7;
         }
      } : super.createComponentListener());
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintInternalFrameBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
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
      var1.getPainter().paintInternalFrameBorder(var1, var2, var3, var4, var5, var6);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      SynthStyle var2 = this.style;
      JInternalFrame var3 = (JInternalFrame)var1.getSource();
      String var4 = var1.getPropertyName();
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle(var3);
      }

      if (this.style == var2 && (var4 == "maximum" || var4 == "selected")) {
         SynthContext var5 = this.getContext(var3, 1);
         this.style.uninstallDefaults(var5);
         this.style.installDefaults(var5, this);
      }

   }
}
