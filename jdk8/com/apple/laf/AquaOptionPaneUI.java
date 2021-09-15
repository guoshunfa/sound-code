package com.apple.laf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class AquaOptionPaneUI extends BasicOptionPaneUI {
   private static final int kOKCancelButtonWidth = 79;
   private static final int kButtonHeight = 23;
   private static final int kDialogSmallPadding = 4;
   private static final int kDialogLargePadding = 23;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaOptionPaneUI();
   }

   protected Container createButtonArea() {
      Container var1 = super.createButtonArea();
      var1.setLayout(new AquaOptionPaneUI.AquaButtonAreaLayout(true, 4));
      return var1;
   }

   protected Container createMessageArea() {
      JPanel var1 = new JPanel();
      var1.setBorder(UIManager.getBorder("OptionPane.messageAreaBorder"));
      var1.setLayout(new BoxLayout(var1, 0));
      JPanel var2 = new JPanel();
      Icon var3 = this.getIcon();
      if (var3 != null) {
         JLabel var4 = new JLabel(var3);
         var4.setVerticalAlignment(1);
         JPanel var5 = new JPanel();
         var5.add(var4);
         var1.add(var5);
         var1.add(Box.createHorizontalStrut(23));
      }

      var2.setLayout(new GridBagLayout());
      GridBagConstraints var6 = new GridBagConstraints();
      var6.gridx = var6.gridy = 0;
      var6.gridwidth = 0;
      var6.gridheight = 1;
      var6.anchor = 17;
      var6.insets = new Insets(0, 0, 3, 0);
      this.addMessageComponents(var2, var6, this.getMessage(), this.getMaxCharactersPerLineCount(), false);
      var1.add(var2);
      return var1;
   }

   public static class AquaButtonAreaLayout extends BasicOptionPaneUI.ButtonAreaLayout {
      public AquaButtonAreaLayout(boolean var1, int var2) {
         super(true, var2);
      }

      public void layoutContainer(Container var1) {
         Component[] var2 = var1.getComponents();
         if (var2 != null && 0 < var2.length) {
            int var3 = var2.length;
            int var4 = var1.getInsets().top;
            Dimension var5 = new Dimension(79, 23);

            int var6;
            for(var6 = 0; var6 < var3; ++var6) {
               Dimension var7 = var2[var6].getPreferredSize();
               var5.width = Math.max(var5.width, var7.width);
               var5.height = Math.max(var5.height, var7.height);
            }

            int var10001 = var5.width * var3 + (var3 - 1) * this.padding;
            var6 = var1.getSize().width - var10001;
            int var9 = var5.width + this.padding;

            for(int var8 = var3 - 1; var8 >= 0; --var8) {
               var2[var8].setBounds(var6, var4, var5.width, var5.height);
               var6 += var9;
            }

         }
      }
   }
}
