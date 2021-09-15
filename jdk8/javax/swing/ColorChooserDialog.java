package javax.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Locale;
import sun.swing.SwingUtilities2;

class ColorChooserDialog extends JDialog {
   private Color initialColor;
   private JColorChooser chooserPane;
   private JButton cancelButton;

   public ColorChooserDialog(Dialog var1, String var2, boolean var3, Component var4, JColorChooser var5, ActionListener var6, ActionListener var7) throws HeadlessException {
      super(var1, var2, var3);
      this.initColorChooserDialog(var4, var5, var6, var7);
   }

   public ColorChooserDialog(Frame var1, String var2, boolean var3, Component var4, JColorChooser var5, ActionListener var6, ActionListener var7) throws HeadlessException {
      super(var1, var2, var3);
      this.initColorChooserDialog(var4, var5, var6, var7);
   }

   protected void initColorChooserDialog(Component var1, JColorChooser var2, ActionListener var3, ActionListener var4) {
      this.chooserPane = var2;
      Locale var5 = this.getLocale();
      String var6 = UIManager.getString("ColorChooser.okText", (Locale)var5);
      String var7 = UIManager.getString("ColorChooser.cancelText", (Locale)var5);
      String var8 = UIManager.getString("ColorChooser.resetText", (Locale)var5);
      Container var9 = this.getContentPane();
      var9.setLayout(new BorderLayout());
      var9.add((Component)var2, (Object)"Center");
      JPanel var10 = new JPanel();
      var10.setLayout(new FlowLayout(1));
      JButton var11 = new JButton(var6);
      this.getRootPane().setDefaultButton(var11);
      var11.getAccessibleContext().setAccessibleDescription(var6);
      var11.setActionCommand("OK");
      var11.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            ColorChooserDialog.this.hide();
         }
      });
      if (var3 != null) {
         var11.addActionListener(var3);
      }

      var10.add(var11);
      this.cancelButton = new JButton(var7);
      this.cancelButton.getAccessibleContext().setAccessibleDescription(var7);
      AbstractAction var12 = new AbstractAction() {
         public void actionPerformed(ActionEvent var1) {
            ((AbstractButton)var1.getSource()).fireActionPerformed(var1);
         }
      };
      KeyStroke var13 = KeyStroke.getKeyStroke(27, 0);
      InputMap var14 = this.cancelButton.getInputMap(2);
      ActionMap var15 = this.cancelButton.getActionMap();
      if (var14 != null && var15 != null) {
         var14.put(var13, "cancel");
         var15.put("cancel", var12);
      }

      this.cancelButton.setActionCommand("cancel");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            ColorChooserDialog.this.hide();
         }
      });
      if (var4 != null) {
         this.cancelButton.addActionListener(var4);
      }

      var10.add(this.cancelButton);
      JButton var16 = new JButton(var8);
      var16.getAccessibleContext().setAccessibleDescription(var8);
      var16.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            ColorChooserDialog.this.reset();
         }
      });
      int var17 = SwingUtilities2.getUIDefaultsInt("ColorChooser.resetMnemonic", var5, -1);
      if (var17 != -1) {
         var16.setMnemonic(var17);
      }

      var10.add(var16);
      var9.add((Component)var10, (Object)"South");
      if (JDialog.isDefaultLookAndFeelDecorated()) {
         boolean var18 = UIManager.getLookAndFeel().getSupportsWindowDecorations();
         if (var18) {
            this.getRootPane().setWindowDecorationStyle(5);
         }
      }

      this.applyComponentOrientation(((Component)(var1 == null ? this.getRootPane() : var1)).getComponentOrientation());
      this.pack();
      this.setLocationRelativeTo(var1);
      this.addWindowListener(new ColorChooserDialog.Closer());
   }

   public void show() {
      this.initialColor = this.chooserPane.getColor();
      super.show();
   }

   public void reset() {
      this.chooserPane.setColor(this.initialColor);
   }

   static class DisposeOnClose extends ComponentAdapter implements Serializable {
      public void componentHidden(ComponentEvent var1) {
         Window var2 = (Window)var1.getComponent();
         var2.dispose();
      }
   }

   class Closer extends WindowAdapter implements Serializable {
      public void windowClosing(WindowEvent var1) {
         ColorChooserDialog.this.cancelButton.doClick(0);
         Window var2 = var1.getWindow();
         var2.hide();
      }
   }
}
