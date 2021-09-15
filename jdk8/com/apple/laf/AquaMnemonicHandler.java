package com.apple.laf;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventPostProcessor;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AquaMnemonicHandler {
   static final AquaUtils.RecyclableSingleton<AquaMnemonicHandler.AltProcessor> altProcessor = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaMnemonicHandler.AltProcessor.class);
   protected static boolean isMnemonicHidden = true;

   public static KeyEventPostProcessor getInstance() {
      return (KeyEventPostProcessor)altProcessor.get();
   }

   public static void setMnemonicHidden(boolean var0) {
      if (UIManager.getBoolean("Button.showMnemonics")) {
         isMnemonicHidden = false;
      } else {
         isMnemonicHidden = var0;
      }

   }

   public static boolean isMnemonicHidden() {
      if (UIManager.getBoolean("Button.showMnemonics")) {
         isMnemonicHidden = false;
      }

      return isMnemonicHidden;
   }

   static void repaintMnemonicsInWindow(Window var0) {
      if (var0 != null && var0.isShowing()) {
         Window[] var1 = var0.getOwnedWindows();
         Window[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Window var5 = var2[var4];
            repaintMnemonicsInWindow(var5);
         }

         repaintMnemonicsInContainer(var0);
      }
   }

   static void repaintMnemonicsInContainer(Container var0) {
      for(int var1 = 0; var1 < var0.getComponentCount(); ++var1) {
         Component var2 = var0.getComponent(var1);
         if (var2 != null && var2.isVisible()) {
            if (var2 instanceof AbstractButton && ((AbstractButton)var2).getMnemonic() != 0) {
               var2.repaint();
            } else if (var2 instanceof JLabel && ((JLabel)var2).getDisplayedMnemonic() != 0) {
               var2.repaint();
            } else if (var2 instanceof Container) {
               repaintMnemonicsInContainer((Container)var2);
            }
         }
      }

   }

   static class AltProcessor implements KeyEventPostProcessor {
      public boolean postProcessKeyEvent(KeyEvent var1) {
         if (var1.getKeyCode() != 18) {
            return false;
         } else {
            JRootPane var2 = SwingUtilities.getRootPane(var1.getComponent());
            Window var3 = var2 == null ? null : SwingUtilities.getWindowAncestor(var2);
            switch(var1.getID()) {
            case 401:
               AquaMnemonicHandler.setMnemonicHidden(false);
               break;
            case 402:
               AquaMnemonicHandler.setMnemonicHidden(true);
            }

            AquaMnemonicHandler.repaintMnemonicsInWindow(var3);
            return false;
         }
      }
   }
}
