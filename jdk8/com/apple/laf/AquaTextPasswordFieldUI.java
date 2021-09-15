package com.apple.laf;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class AquaTextPasswordFieldUI extends AquaTextFieldUI {
   static final AquaUtils.RecyclableSingleton<AquaTextPasswordFieldUI.CapsLockSymbolPainter> capsLockPainter = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaTextPasswordFieldUI.CapsLockSymbolPainter.class);

   static AquaTextPasswordFieldUI.CapsLockSymbolPainter getCapsLockPainter() {
      return (AquaTextPasswordFieldUI.CapsLockSymbolPainter)capsLockPainter.get();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTextPasswordFieldUI();
   }

   protected String getPropertyPrefix() {
      return "PasswordField";
   }

   public View create(Element var1) {
      return new AquaTextPasswordFieldUI.AquaPasswordView(var1);
   }

   protected void installListeners() {
      super.installListeners();
      this.getComponent().addKeyListener(getCapsLockPainter());
   }

   protected void uninstallListeners() {
      this.getComponent().removeKeyListener(getCapsLockPainter());
      super.uninstallListeners();
   }

   protected void paintBackgroundSafely(Graphics var1) {
      super.paintBackgroundSafely(var1);
      JTextComponent var2 = this.getComponent();
      if (var2 != null) {
         if (var2.isFocusOwner()) {
            boolean var3 = Toolkit.getDefaultToolkit().getLockingKeyState(20);
            if (var3) {
               Rectangle var4 = var2.getBounds();
               getCapsLockPainter().paintBorder(var2, var1, var4.x, var4.y, var4.width, var4.height);
            }
         }
      }
   }

   static class CapsLockSymbolPainter extends KeyAdapter implements Border, UIResource {
      protected Shape capsLockShape;

      protected Shape getCapsLockShape() {
         if (this.capsLockShape != null) {
            return this.capsLockShape;
         } else {
            RoundRectangle2D.Double var1 = new RoundRectangle2D.Double(0.5D, 0.5D, 16.0D, 16.0D, 8.0D, 8.0D);
            GeneralPath var2 = new GeneralPath(var1);
            var2.setWindingRule(0);
            var2.moveTo(8.5D, 2.0D);
            var2.lineTo(4.0D, 7.0D);
            var2.lineTo(6.25D, 7.0D);
            var2.lineTo(6.25D, 10.25D);
            var2.lineTo(10.75D, 10.25D);
            var2.lineTo(10.75D, 7.0D);
            var2.lineTo(13.0D, 7.0D);
            var2.lineTo(8.5D, 2.0D);
            var2.moveTo(10.75D, 12.0D);
            var2.lineTo(6.25D, 12.0D);
            var2.lineTo(6.25D, 14.25D);
            var2.lineTo(10.75D, 14.25D);
            var2.lineTo(10.75D, 12.0D);
            return this.capsLockShape = var2;
         }
      }

      public Insets getBorderInsets(Component var1) {
         return new Insets(0, 0, 0, 0);
      }

      public boolean isBorderOpaque() {
         return false;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         var2 = var2.create(var5 - 23, var6 / 2 - 8, 18, 18);
         var2.setColor(UIManager.getColor("PasswordField.capsLockIconColor"));
         ((Graphics2D)var2).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         ((Graphics2D)var2).fill(this.getCapsLockShape());
         var2.dispose();
      }

      public void keyPressed(KeyEvent var1) {
         this.update(var1);
      }

      public void keyReleased(KeyEvent var1) {
         this.update(var1);
      }

      void update(KeyEvent var1) {
         if (20 == var1.getKeyCode()) {
            var1.getComponent().repaint();
         }
      }
   }

   protected class AquaPasswordView extends PasswordView {
      public AquaPasswordView(Element var2) {
         super(var2);
         this.setupDefaultEchoCharacter();
      }

      protected void setupDefaultEchoCharacter() {
         Character var1 = (Character)UIManager.getDefaults().get(AquaTextPasswordFieldUI.this.getPropertyPrefix() + ".echoChar");
         if (var1 != null) {
            LookAndFeel.installProperty(AquaTextPasswordFieldUI.this.getComponent(), "echoChar", var1);
         }

      }
   }
}
