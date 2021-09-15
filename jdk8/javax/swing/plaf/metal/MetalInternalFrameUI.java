package javax.swing.plaf.metal;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MetalInternalFrameUI extends BasicInternalFrameUI {
   private static final PropertyChangeListener metalPropertyChangeListener = new MetalInternalFrameUI.MetalPropertyChangeHandler();
   private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
   protected static String IS_PALETTE = "JInternalFrame.isPalette";
   private static String IS_PALETTE_KEY = "JInternalFrame.isPalette";
   private static String FRAME_TYPE = "JInternalFrame.frameType";
   private static String NORMAL_FRAME = "normal";
   private static String PALETTE_FRAME = "palette";
   private static String OPTION_DIALOG = "optionDialog";

   public MetalInternalFrameUI(JInternalFrame var1) {
      super(var1);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new MetalInternalFrameUI((JInternalFrame)var0);
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      Object var2 = var1.getClientProperty(IS_PALETTE_KEY);
      if (var2 != null) {
         this.setPalette((Boolean)var2);
      }

      Container var3 = this.frame.getContentPane();
      this.stripContentBorder(var3);
   }

   public void uninstallUI(JComponent var1) {
      this.frame = (JInternalFrame)var1;
      Container var2 = ((JInternalFrame)((JInternalFrame)var1)).getContentPane();
      if (var2 instanceof JComponent) {
         JComponent var3 = (JComponent)var2;
         if (var3.getBorder() == handyEmptyBorder) {
            var3.setBorder((Border)null);
         }
      }

      super.uninstallUI(var1);
   }

   protected void installListeners() {
      super.installListeners();
      this.frame.addPropertyChangeListener(metalPropertyChangeListener);
   }

   protected void uninstallListeners() {
      this.frame.removePropertyChangeListener(metalPropertyChangeListener);
      super.uninstallListeners();
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      ActionMap var1 = SwingUtilities.getUIActionMap(this.frame);
      if (var1 != null) {
         var1.remove("showSystemMenu");
      }

   }

   protected void uninstallKeyboardActions() {
      super.uninstallKeyboardActions();
   }

   protected void uninstallComponents() {
      this.titlePane = null;
      super.uninstallComponents();
   }

   private void stripContentBorder(Object var1) {
      if (var1 instanceof JComponent) {
         JComponent var2 = (JComponent)var1;
         Border var3 = var2.getBorder();
         if (var3 == null || var3 instanceof UIResource) {
            var2.setBorder(handyEmptyBorder);
         }
      }

   }

   protected JComponent createNorthPane(JInternalFrame var1) {
      return new MetalInternalFrameTitlePane(var1);
   }

   private void setFrameType(String var1) {
      if (var1.equals(OPTION_DIALOG)) {
         LookAndFeel.installBorder(this.frame, "InternalFrame.optionDialogBorder");
         ((MetalInternalFrameTitlePane)this.titlePane).setPalette(false);
      } else if (var1.equals(PALETTE_FRAME)) {
         LookAndFeel.installBorder(this.frame, "InternalFrame.paletteBorder");
         ((MetalInternalFrameTitlePane)this.titlePane).setPalette(true);
      } else {
         LookAndFeel.installBorder(this.frame, "InternalFrame.border");
         ((MetalInternalFrameTitlePane)this.titlePane).setPalette(false);
      }

   }

   public void setPalette(boolean var1) {
      if (var1) {
         LookAndFeel.installBorder(this.frame, "InternalFrame.paletteBorder");
      } else {
         LookAndFeel.installBorder(this.frame, "InternalFrame.border");
      }

      ((MetalInternalFrameTitlePane)this.titlePane).setPalette(var1);
   }

   protected MouseInputAdapter createBorderListener(JInternalFrame var1) {
      return new MetalInternalFrameUI.BorderListener1();
   }

   private class BorderListener1 extends BasicInternalFrameUI.BorderListener implements SwingConstants {
      private BorderListener1() {
         super();
      }

      Rectangle getIconBounds() {
         boolean var1 = MetalUtils.isLeftToRight(MetalInternalFrameUI.this.frame);
         int var2 = var1 ? 5 : MetalInternalFrameUI.this.titlePane.getWidth() - 5;
         Rectangle var3 = null;
         Icon var4 = MetalInternalFrameUI.this.frame.getFrameIcon();
         if (var4 != null) {
            if (!var1) {
               var2 -= var4.getIconWidth();
            }

            int var5 = MetalInternalFrameUI.this.titlePane.getHeight() / 2 - var4.getIconHeight() / 2;
            var3 = new Rectangle(var2, var5, var4.getIconWidth(), var4.getIconHeight());
         }

         return var3;
      }

      public void mouseClicked(MouseEvent var1) {
         if (var1.getClickCount() == 2 && var1.getSource() == MetalInternalFrameUI.this.getNorthPane() && MetalInternalFrameUI.this.frame.isClosable() && !MetalInternalFrameUI.this.frame.isIcon()) {
            Rectangle var2 = this.getIconBounds();
            if (var2 != null && var2.contains(var1.getX(), var1.getY())) {
               MetalInternalFrameUI.this.frame.doDefaultCloseAction();
            } else {
               super.mouseClicked(var1);
            }
         } else {
            super.mouseClicked(var1);
         }

      }

      // $FF: synthetic method
      BorderListener1(Object var2) {
         this();
      }
   }

   private static class MetalPropertyChangeHandler implements PropertyChangeListener {
      private MetalPropertyChangeHandler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         JInternalFrame var3 = (JInternalFrame)var1.getSource();
         if (var3.getUI() instanceof MetalInternalFrameUI) {
            MetalInternalFrameUI var4 = (MetalInternalFrameUI)var3.getUI();
            if (var2.equals(MetalInternalFrameUI.FRAME_TYPE)) {
               if (var1.getNewValue() instanceof String) {
                  var4.setFrameType((String)var1.getNewValue());
               }
            } else if (var2.equals(MetalInternalFrameUI.IS_PALETTE_KEY)) {
               if (var1.getNewValue() != null) {
                  var4.setPalette((Boolean)var1.getNewValue());
               } else {
                  var4.setPalette(false);
               }
            } else if (var2.equals("contentPane")) {
               var4.stripContentBorder(var1.getNewValue());
            }

         }
      }

      // $FF: synthetic method
      MetalPropertyChangeHandler(Object var1) {
         this();
      }
   }
}
