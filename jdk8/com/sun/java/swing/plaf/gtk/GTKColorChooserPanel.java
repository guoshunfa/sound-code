package com.sun.java.swing.plaf.gtk;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;

class GTKColorChooserPanel extends AbstractColorChooserPanel implements ChangeListener {
   private static final float PI_3 = 1.0471976F;
   private GTKColorChooserPanel.ColorTriangle triangle;
   private JLabel lastLabel;
   private JLabel label;
   private JSpinner hueSpinner;
   private JSpinner saturationSpinner;
   private JSpinner valueSpinner;
   private JSpinner redSpinner;
   private JSpinner greenSpinner;
   private JSpinner blueSpinner;
   private JTextField colorNameTF;
   private boolean settingColor;
   private float hue;
   private float saturation;
   private float brightness;
   private static final int FLAGS_CHANGED_ANGLE = 1;
   private static final int FLAGS_DRAGGING = 2;
   private static final int FLAGS_DRAGGING_TRIANGLE = 4;
   private static final int FLAGS_SETTING_COLOR = 8;
   private static final int FLAGS_FOCUSED_WHEEL = 16;
   private static final int FLAGS_FOCUSED_TRIANGLE = 32;

   static void compositeRequestFocus(Component var0, boolean var1) {
      if (var0 instanceof Container) {
         Container var2 = (Container)var0;
         if (var2.isFocusCycleRoot()) {
            FocusTraversalPolicy var3 = var2.getFocusTraversalPolicy();
            Component var4 = var3.getDefaultComponent(var2);
            if (var4 != null) {
               var4.requestFocus();
               return;
            }
         }

         Container var6 = var2.getFocusCycleRootAncestor();
         if (var6 != null) {
            FocusTraversalPolicy var7 = var6.getFocusTraversalPolicy();
            Component var5;
            if (var1) {
               var5 = var7.getComponentAfter(var6, var2);
            } else {
               var5 = var7.getComponentBefore(var6, var2);
            }

            if (var5 != null) {
               var5.requestFocus();
               return;
            }
         }
      }

      var0.requestFocus();
   }

   public String getDisplayName() {
      return (String)UIManager.get("GTKColorChooserPanel.nameText");
   }

   public int getMnemonic() {
      String var1 = (String)UIManager.get("GTKColorChooserPanel.mnemonic");
      if (var1 != null) {
         try {
            int var2 = Integer.parseInt(var1);
            return var2;
         } catch (NumberFormatException var3) {
         }
      }

      return -1;
   }

   public int getDisplayedMnemonicIndex() {
      String var1 = (String)UIManager.get("GTKColorChooserPanel.displayedMnemonicIndex");
      if (var1 != null) {
         try {
            int var2 = Integer.parseInt(var1);
            return var2;
         } catch (NumberFormatException var3) {
         }
      }

      return -1;
   }

   public Icon getSmallDisplayIcon() {
      return null;
   }

   public Icon getLargeDisplayIcon() {
      return null;
   }

   public void uninstallChooserPanel(JColorChooser var1) {
      super.uninstallChooserPanel(var1);
      this.removeAll();
   }

   protected void buildChooser() {
      this.triangle = new GTKColorChooserPanel.ColorTriangle();
      this.triangle.setName("GTKColorChooserPanel.triangle");
      this.label = new GTKColorChooserPanel.OpaqueLabel();
      this.label.setName("GTKColorChooserPanel.colorWell");
      this.label.setOpaque(true);
      this.label.setMinimumSize(new Dimension(67, 32));
      this.label.setPreferredSize(new Dimension(67, 32));
      this.label.setMaximumSize(new Dimension(67, 32));
      this.lastLabel = new GTKColorChooserPanel.OpaqueLabel();
      this.lastLabel.setName("GTKColorChooserPanel.lastColorWell");
      this.lastLabel.setOpaque(true);
      this.lastLabel.setMinimumSize(new Dimension(67, 32));
      this.lastLabel.setPreferredSize(new Dimension(67, 32));
      this.lastLabel.setMaximumSize(new Dimension(67, 32));
      this.hueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));
      this.configureSpinner(this.hueSpinner, "GTKColorChooserPanel.hueSpinner");
      this.saturationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
      this.configureSpinner(this.saturationSpinner, "GTKColorChooserPanel.saturationSpinner");
      this.valueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
      this.configureSpinner(this.valueSpinner, "GTKColorChooserPanel.valueSpinner");
      this.redSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
      this.configureSpinner(this.redSpinner, "GTKColorChooserPanel.redSpinner");
      this.greenSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
      this.configureSpinner(this.greenSpinner, "GTKColorChooserPanel.greenSpinner");
      this.blueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
      this.configureSpinner(this.blueSpinner, "GTKColorChooserPanel.blueSpinner");
      this.colorNameTF = new JTextField(8);
      this.setLayout(new GridBagLayout());
      this.add(this, "GTKColorChooserPanel.hue", this.hueSpinner, -1, -1);
      this.add(this, "GTKColorChooserPanel.red", this.redSpinner, -1, -1);
      this.add(this, "GTKColorChooserPanel.saturation", this.saturationSpinner, -1, -1);
      this.add(this, "GTKColorChooserPanel.green", this.greenSpinner, -1, -1);
      this.add(this, "GTKColorChooserPanel.value", this.valueSpinner, -1, -1);
      this.add(this, "GTKColorChooserPanel.blue", this.blueSpinner, -1, -1);
      this.add(new JSeparator(0), new GridBagConstraints(1, 3, 4, 1, 1.0D, 0.0D, 21, 2, new Insets(14, 0, 0, 0), 0, 0));
      this.add(this, "GTKColorChooserPanel.colorName", this.colorNameTF, 0, 4);
      this.add(this.triangle, new GridBagConstraints(0, 0, 1, 5, 0.0D, 0.0D, 21, 0, new Insets(14, 20, 2, 9), 0, 0));
      Box var1 = Box.createHorizontalBox();
      var1.add(this.lastLabel);
      var1.add(this.label);
      this.add(var1, new GridBagConstraints(0, 5, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
      this.add(new JSeparator(0), new GridBagConstraints(0, 6, 5, 1, 1.0D, 0.0D, 21, 2, new Insets(12, 0, 0, 0), 0, 0));
   }

   private void configureSpinner(JSpinner var1, String var2) {
      var1.addChangeListener(this);
      var1.setName(var2);
      JComponent var3 = var1.getEditor();
      if (var3 instanceof JSpinner.DefaultEditor) {
         JFormattedTextField var4 = ((JSpinner.DefaultEditor)var3).getTextField();
         var4.setFocusLostBehavior(1);
      }

   }

   private void add(Container var1, String var2, JComponent var3, int var4, int var5) {
      JLabel var6 = new JLabel(UIManager.getString(var2 + "Text", (Locale)this.getLocale()));
      String var7 = (String)UIManager.get(var2 + "Mnemonic", this.getLocale());
      if (var7 != null) {
         try {
            var6.setDisplayedMnemonic(Integer.parseInt(var7));
         } catch (NumberFormatException var11) {
         }

         String var8 = (String)UIManager.get(var2 + "MnemonicIndex", this.getLocale());
         if (var8 != null) {
            try {
               var6.setDisplayedMnemonicIndex(Integer.parseInt(var8));
            } catch (NumberFormatException var10) {
            }
         }
      }

      var6.setLabelFor(var3);
      if (var4 < 0) {
         var4 = var1.getComponentCount() % 4;
      }

      if (var5 < 0) {
         var5 = var1.getComponentCount() / 4;
      }

      GridBagConstraints var12 = new GridBagConstraints(var4 + 1, var5, 1, 1, 0.0D, 0.0D, 24, 0, new Insets(4, 0, 0, 4), 0, 0);
      if (var5 == 0) {
         var12.insets.top = 14;
      }

      var1.add((Component)var6, (Object)var12);
      ++var12.gridx;
      var1.add((Component)var3, (Object)var12);
   }

   public void updateChooser() {
      if (!this.settingColor) {
         this.lastLabel.setBackground(this.getColorFromModel());
         this.setColor(this.getColorFromModel(), true, true, false);
      }

   }

   private void setRed(int var1) {
      this.setRGB(var1 << 16 | this.getColor().getGreen() << 8 | this.getColor().getBlue());
   }

   private void setGreen(int var1) {
      this.setRGB(this.getColor().getRed() << 16 | var1 << 8 | this.getColor().getBlue());
   }

   private void setBlue(int var1) {
      this.setRGB(this.getColor().getRed() << 16 | this.getColor().getGreen() << 8 | var1);
   }

   private void setHue(float var1, boolean var2) {
      this.setHSB(var1, this.saturation, this.brightness);
      if (var2) {
         this.settingColor = true;
         this.hueSpinner.setValue((int)(var1 * 360.0F));
         this.settingColor = false;
      }

   }

   private float getHue() {
      return this.hue;
   }

   private void setSaturation(float var1) {
      this.setHSB(this.hue, var1, this.brightness);
   }

   private float getSaturation() {
      return this.saturation;
   }

   private void setBrightness(float var1) {
      this.setHSB(this.hue, this.saturation, var1);
   }

   private float getBrightness() {
      return this.brightness;
   }

   private void setSaturationAndBrightness(float var1, float var2, boolean var3) {
      this.setHSB(this.hue, var1, var2);
      if (var3) {
         this.settingColor = true;
         this.saturationSpinner.setValue((int)(var1 * 255.0F));
         this.valueSpinner.setValue((int)(var2 * 255.0F));
         this.settingColor = false;
      }

   }

   private void setRGB(int var1) {
      Color var2 = new Color(var1);
      this.setColor(var2, false, true, true);
      this.settingColor = true;
      this.hueSpinner.setValue((int)(this.hue * 360.0F));
      this.saturationSpinner.setValue((int)(this.saturation * 255.0F));
      this.valueSpinner.setValue((int)(this.brightness * 255.0F));
      this.settingColor = false;
   }

   private void setHSB(float var1, float var2, float var3) {
      Color var4 = Color.getHSBColor(var1, var2, var3);
      this.hue = var1;
      this.saturation = var2;
      this.brightness = var3;
      this.setColor(var4, false, false, true);
      this.settingColor = true;
      this.redSpinner.setValue(var4.getRed());
      this.greenSpinner.setValue(var4.getGreen());
      this.blueSpinner.setValue(var4.getBlue());
      this.settingColor = false;
   }

   private void setColor(Color var1, boolean var2, boolean var3, boolean var4) {
      if (var1 == null) {
         var1 = Color.BLACK;
      }

      this.settingColor = true;
      if (var3) {
         float[] var5 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), (float[])null);
         this.hue = var5[0];
         this.saturation = var5[1];
         this.brightness = var5[2];
      }

      if (var4) {
         ColorSelectionModel var6 = this.getColorSelectionModel();
         if (var6 != null) {
            var6.setSelectedColor(var1);
         }
      }

      this.triangle.setColor(this.hue, this.saturation, this.brightness);
      this.label.setBackground(var1);
      String var7 = Integer.toHexString(var1.getRGB() & 16777215 | 16777216);
      this.colorNameTF.setText("#" + var7.substring(1));
      if (var2) {
         this.redSpinner.setValue(var1.getRed());
         this.greenSpinner.setValue(var1.getGreen());
         this.blueSpinner.setValue(var1.getBlue());
         this.hueSpinner.setValue((int)(this.hue * 360.0F));
         this.saturationSpinner.setValue((int)(this.saturation * 255.0F));
         this.valueSpinner.setValue((int)(this.brightness * 255.0F));
      }

      this.settingColor = false;
   }

   public Color getColor() {
      return this.label.getBackground();
   }

   public void stateChanged(ChangeEvent var1) {
      if (!this.settingColor) {
         Color var2 = this.getColor();
         if (var1.getSource() == this.hueSpinner) {
            this.setHue(((Number)this.hueSpinner.getValue()).floatValue() / 360.0F, false);
         } else if (var1.getSource() == this.saturationSpinner) {
            this.setSaturation(((Number)this.saturationSpinner.getValue()).floatValue() / 255.0F);
         } else if (var1.getSource() == this.valueSpinner) {
            this.setBrightness(((Number)this.valueSpinner.getValue()).floatValue() / 255.0F);
         } else if (var1.getSource() == this.redSpinner) {
            this.setRed(((Number)this.redSpinner.getValue()).intValue());
         } else if (var1.getSource() == this.greenSpinner) {
            this.setGreen(((Number)this.greenSpinner.getValue()).intValue());
         } else if (var1.getSource() == this.blueSpinner) {
            this.setBlue(((Number)this.blueSpinner.getValue()).intValue());
         }

      }
   }

   private class OpaqueLabel extends JLabel {
      private OpaqueLabel() {
      }

      public boolean isOpaque() {
         return true;
      }

      // $FF: synthetic method
      OpaqueLabel(Object var2) {
         this();
      }
   }

   private static class ColorAction extends AbstractAction {
      private int type;

      ColorAction(String var1, int var2) {
         super(var1);
         this.type = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         GTKColorChooserPanel.ColorTriangle var2 = (GTKColorChooserPanel.ColorTriangle)var1.getSource();
         if (var2.isWheelFocused()) {
            float var3 = var2.getGTKColorChooserPanel().getHue();
            switch(this.type) {
            case 0:
            case 2:
               var2.incrementHue(true);
               break;
            case 1:
            case 3:
               var2.incrementHue(false);
               break;
            case 4:
               var2.focusTriangle();
               break;
            case 5:
               GTKColorChooserPanel.compositeRequestFocus(var2, false);
            }
         } else {
            int var5 = 0;
            int var4 = 0;
            switch(this.type) {
            case 0:
               --var4;
               break;
            case 1:
               ++var4;
               break;
            case 2:
               --var5;
               break;
            case 3:
               ++var5;
               break;
            case 4:
               GTKColorChooserPanel.compositeRequestFocus(var2, true);
               return;
            case 5:
               var2.focusWheel();
               return;
            }

            var2.adjustSB(var2.getColorX() + var5, var2.getColorY() + var4, true);
         }

      }
   }

   private class ColorTriangle extends JPanel {
      private Image wheelImage;
      private Image triangleImage;
      private double angle;
      private int flags;
      private int circleX;
      private int circleY;

      public ColorTriangle() {
         this.enableEvents(4L);
         this.enableEvents(16L);
         this.enableEvents(32L);
         this.setMinimumSize(new Dimension(this.getWheelRadius() * 2 + 2, this.getWheelRadius() * 2 + 2));
         this.setPreferredSize(new Dimension(this.getWheelRadius() * 2 + 2, this.getWheelRadius() * 2 + 2));
         this.setFocusTraversalKeysEnabled(false);
         this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
         this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
         this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
         this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
         this.getInputMap().put(KeyStroke.getKeyStroke("KP_UP"), "up");
         this.getInputMap().put(KeyStroke.getKeyStroke("KP_DOWN"), "down");
         this.getInputMap().put(KeyStroke.getKeyStroke("KP_LEFT"), "left");
         this.getInputMap().put(KeyStroke.getKeyStroke("KP_RIGHT"), "right");
         this.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "focusNext");
         this.getInputMap().put(KeyStroke.getKeyStroke("shift TAB"), "focusLast");
         Object var2 = (ActionMap)UIManager.get("GTKColorChooserPanel.actionMap");
         if (var2 == null) {
            var2 = new ActionMapUIResource();
            ((ActionMap)var2).put("left", new GTKColorChooserPanel.ColorAction("left", 2));
            ((ActionMap)var2).put("right", new GTKColorChooserPanel.ColorAction("right", 3));
            ((ActionMap)var2).put("up", new GTKColorChooserPanel.ColorAction("up", 0));
            ((ActionMap)var2).put("down", new GTKColorChooserPanel.ColorAction("down", 1));
            ((ActionMap)var2).put("focusNext", new GTKColorChooserPanel.ColorAction("focusNext", 4));
            ((ActionMap)var2).put("focusLast", new GTKColorChooserPanel.ColorAction("focusLast", 5));
            UIManager.getLookAndFeelDefaults().put("GTKColorChooserPanel.actionMap", var2);
         }

         SwingUtilities.replaceUIActionMap(this, (ActionMap)var2);
      }

      GTKColorChooserPanel getGTKColorChooserPanel() {
         return GTKColorChooserPanel.this;
      }

      void focusWheel() {
         this.setFocusType(1);
      }

      void focusTriangle() {
         this.setFocusType(2);
      }

      boolean isWheelFocused() {
         return this.isSet(16);
      }

      public void setColor(float var1, float var2, float var3) {
         if (!this.isSet(8)) {
            this.setAngleFromHue(var1);
            this.setSaturationAndBrightness(var2, var3);
         }
      }

      public Color getColor() {
         return GTKColorChooserPanel.this.getColor();
      }

      int getColorX() {
         return this.circleX + this.getIndicatorSize() / 2 - this.getWheelXOrigin();
      }

      int getColorY() {
         return this.circleY + this.getIndicatorSize() / 2 - this.getWheelYOrigin();
      }

      protected void processEvent(AWTEvent var1) {
         if (var1.getID() != 501 && (!this.isSet(2) && !this.isSet(4) || var1.getID() != 506)) {
            if (var1.getID() == 502) {
               this.setFlag(4, false);
               this.setFlag(2, false);
            } else if (var1.getID() == 1005) {
               this.setFocusType(0);
            } else if (var1.getID() == 1004) {
               if (!this.isSet(32) && !this.isSet(16)) {
                  this.setFlag(16, true);
                  this.setFocusType(1);
               }

               this.repaint();
            }
         } else {
            int var2 = this.getWheelRadius();
            int var3 = ((MouseEvent)var1).getX() - var2;
            int var4 = ((MouseEvent)var1).getY() - var2;
            if (!this.hasFocus()) {
               this.requestFocus();
            }

            if (!this.isSet(4) && this.adjustHue(var3, var4, var1.getID() == 501)) {
               this.setFlag(2, true);
               this.setFocusType(1);
            } else if (this.adjustSB(var3, var4, var1.getID() == 501)) {
               this.setFlag(4, true);
               this.setFocusType(2);
            } else {
               this.setFocusType(2);
            }
         }

         super.processEvent(var1);
      }

      public void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         int var2 = this.getWheelRadius();
         int var3 = this.getWheelWidth();
         Image var4 = this.getImage(var2);
         var1.drawImage(var4, this.getWheelXOrigin() - var2, this.getWheelYOrigin() - var2, (ImageObserver)null);
         if (this.hasFocus() && this.isSet(16)) {
            var1.setColor(Color.BLACK);
            var1.drawOval(this.getWheelXOrigin() - var2, this.getWheelYOrigin() - var2, 2 * var2, 2 * var2);
            var1.drawOval(this.getWheelXOrigin() - var2 + var3, this.getWheelYOrigin() - var2 + var3, 2 * (var2 - var3), 2 * (var2 - var3));
         }

         if (Math.toDegrees(6.283185307179586D - this.angle) > 20.0D && Math.toDegrees(6.283185307179586D - this.angle) < 201.0D) {
            var1.setColor(Color.BLACK);
         } else {
            var1.setColor(Color.WHITE);
         }

         int var5 = (int)(Math.cos(this.angle) * (double)var2);
         int var6 = (int)(Math.sin(this.angle) * (double)var2);
         int var7 = (int)(Math.cos(this.angle) * (double)(var2 - var3));
         int var8 = (int)(Math.sin(this.angle) * (double)(var2 - var3));
         var1.drawLine(var5 + var2, var6 + var2, var7 + var2, var8 + var2);
         if (this.hasFocus() && this.isSet(32)) {
            Graphics var9 = var1.create();
            int var10 = this.getTriangleCircumscribedRadius();
            int var11 = (int)((double)(3 * var10) / Math.sqrt(3.0D));
            var9.translate(this.getWheelXOrigin(), this.getWheelYOrigin());
            ((Graphics2D)var9).rotate(this.angle + 1.5707963267948966D);
            var9.setColor(Color.BLACK);
            var9.drawLine(0, -var10, var11 / 2, var10 / 2);
            var9.drawLine(var11 / 2, var10 / 2, -var11 / 2, var10 / 2);
            var9.drawLine(-var11 / 2, var10 / 2, 0, -var10);
            var9.dispose();
         }

         var1.setColor(Color.BLACK);
         var1.drawOval(this.circleX, this.circleY, this.getIndicatorSize() - 1, this.getIndicatorSize() - 1);
         var1.setColor(Color.WHITE);
         var1.drawOval(this.circleX + 1, this.circleY + 1, this.getIndicatorSize() - 3, this.getIndicatorSize() - 3);
      }

      private Image getImage(int var1) {
         if (!this.isSet(1) && this.wheelImage != null && this.wheelImage.getWidth((ImageObserver)null) == var1 * 2) {
            return this.wheelImage;
         } else {
            if (this.wheelImage == null || this.wheelImage.getWidth((ImageObserver)null) != var1) {
               this.wheelImage = this.getWheelImage(var1);
            }

            int var2 = this.getTriangleCircumscribedRadius();
            int var3 = (int)((double)var2 * 3.0D / 2.0D);
            int var4 = (int)((double)(2 * var3) / Math.sqrt(3.0D));
            if (this.triangleImage == null || this.triangleImage.getWidth((ImageObserver)null) != var4) {
               this.triangleImage = new BufferedImage(var4, var4, 2);
            }

            Graphics var5 = this.triangleImage.getGraphics();
            var5.setColor(new Color(0, 0, 0, 0));
            var5.fillRect(0, 0, var4, var4);
            var5.translate(var4 / 2, 0);
            this.paintTriangle(var5, var3, this.getColor());
            var5.translate(-var4 / 2, 0);
            var5.dispose();
            var5 = this.wheelImage.getGraphics();
            var5.setColor(new Color(0, 0, 0, 0));
            var5.fillOval(this.getWheelWidth(), this.getWheelWidth(), 2 * (var1 - this.getWheelWidth()), 2 * (var1 - this.getWheelWidth()));
            double var6 = Math.toRadians(-30.0D) + this.angle;
            var5.translate(var1, var1);
            ((Graphics2D)var5).rotate(var6);
            var5.drawImage(this.triangleImage, -var4 / 2, this.getWheelWidth() - var1, (ImageObserver)null);
            ((Graphics2D)var5).rotate(-var6);
            var5.translate(var4 / 2, var1 - this.getWheelWidth());
            this.setFlag(1, false);
            return this.wheelImage;
         }
      }

      private void paintTriangle(Graphics var1, int var2, Color var3) {
         float[] var4 = Color.RGBtoHSB(var3.getRed(), var3.getGreen(), var3.getBlue(), (float[])null);
         float var5 = var4[0];
         double var6 = (double)var2;

         for(int var8 = 0; var8 < var2; ++var8) {
            int var9 = (int)((double)var8 * Math.tan(Math.toRadians(30.0D)));
            float var10 = (float)(var9 * 2);
            if (var9 > 0) {
               float var11 = (float)((double)var8 / var6);

               for(int var12 = -var9; var12 <= var9; ++var12) {
                  float var13 = (float)var12 / var10 + 0.5F;
                  var1.setColor(Color.getHSBColor(var5, var13, var11));
                  var1.fillRect(var12, var8, 1, 1);
               }
            } else {
               var1.setColor(var3);
               var1.fillRect(0, var8, 1, 1);
            }
         }

      }

      private Image getWheelImage(int var1) {
         int var2 = var1 - this.getWheelWidth();
         int var3 = var1 * 2;
         BufferedImage var4 = new BufferedImage(var3, var3, 2);

         for(int var5 = -var1; var5 < var1; ++var5) {
            int var6 = var5 * var5;

            for(int var7 = -var1; var7 < var1; ++var7) {
               double var8 = Math.sqrt((double)(var6 + var7 * var7));
               if (var8 < (double)var1 && var8 > (double)var2) {
                  int var10 = this.colorWheelLocationToRGB(var7, var5, var8) | -16777216;
                  var4.setRGB(var7 + var1, var5 + var1, var10);
               }
            }
         }

         this.wheelImage = var4;
         return this.wheelImage;
      }

      boolean adjustSB(int var1, int var2, boolean var3) {
         int var4 = this.getWheelRadius() - this.getWheelWidth();
         boolean var5 = false;
         var2 = -var2;
         if (!var3 || var1 >= -var4 && var1 <= var4 && var2 >= -var4 && var2 <= var4) {
            int var6 = var4 * 3 / 2;
            double var7 = Math.cos(this.angle) * (double)var1 - Math.sin(this.angle) * (double)var2;
            double var9 = Math.sin(this.angle) * (double)var1 + Math.cos(this.angle) * (double)var2;
            if (var7 < (double)(-(var4 / 2))) {
               if (var3) {
                  return false;
               }

               var7 = (double)(-var4 / 2);
               var5 = true;
            } else if ((int)var7 > var4) {
               if (var3) {
                  return false;
               }

               var7 = (double)var4;
               var5 = true;
            }

            int var11 = (int)(((double)var6 - var7 - (double)var4 / 2.0D) * Math.tan(Math.toRadians(30.0D)));
            if (var9 <= (double)(-var11)) {
               if (var3) {
                  return false;
               }

               var9 = (double)(-var11);
               var5 = true;
            } else if (var9 > (double)var11) {
               if (var3) {
                  return false;
               }

               var9 = (double)var11;
               var5 = true;
            }

            double var12 = Math.cos(Math.toRadians(-30.0D)) * var7 - Math.sin(Math.toRadians(-30.0D)) * var9;
            double var14 = Math.sin(Math.toRadians(-30.0D)) * var7 + Math.cos(Math.toRadians(-30.0D)) * var9;
            float var16 = Math.min(1.0F, (float)(((double)var4 - var14) / (double)var6));
            float var17 = (float)(Math.tan(Math.toRadians(30.0D)) * ((double)var4 - var14));
            float var18 = Math.min(1.0F, (float)(var12 / (double)var17 / 2.0D + 0.5D));
            this.setFlag(8, true);
            if (var5) {
               this.setSaturationAndBrightness(var18, var16);
            } else {
               this.setSaturationAndBrightness(var18, var16, var1 + this.getWheelXOrigin(), this.getWheelYOrigin() - var2);
            }

            GTKColorChooserPanel.this.setSaturationAndBrightness(var18, var16, true);
            this.setFlag(8, false);
            return true;
         } else {
            return false;
         }
      }

      private void setSaturationAndBrightness(float var1, float var2) {
         int var3 = this.getTriangleCircumscribedRadius();
         int var4 = var3 * 3 / 2;
         double var5 = (double)(var2 * (float)var4);
         double var7 = var5 * Math.tan(Math.toRadians(30.0D));
         double var9 = 2.0D * var7 * (double)var1 - var7;
         var5 -= (double)var3;
         double var11 = Math.cos(Math.toRadians(-60.0D) - this.angle) * var5 - Math.sin(Math.toRadians(-60.0D) - this.angle) * var9;
         double var13 = Math.sin(Math.toRadians(-60.0D) - this.angle) * var5 + Math.cos(Math.toRadians(-60.0D) - this.angle) * var9;
         int var15 = (int)var11 + this.getWheelXOrigin();
         int var16 = this.getWheelYOrigin() - (int)var13;
         this.setSaturationAndBrightness(var1, var2, var15, var16);
      }

      private void setSaturationAndBrightness(float var1, float var2, int var3, int var4) {
         var3 -= this.getIndicatorSize() / 2;
         var4 -= this.getIndicatorSize() / 2;
         int var5 = Math.min(var3, this.circleX);
         int var6 = Math.min(var4, this.circleY);
         this.repaint(var5, var6, Math.max(this.circleX, var3) - var5 + this.getIndicatorSize() + 1, Math.max(this.circleY, var4) - var6 + this.getIndicatorSize() + 1);
         this.circleX = var3;
         this.circleY = var4;
      }

      private boolean adjustHue(int var1, int var2, boolean var3) {
         double var4 = Math.sqrt((double)(var1 * var1 + var2 * var2));
         int var6 = this.getWheelRadius();
         if (var3 && (var4 < (double)(var6 - this.getWheelWidth()) || var4 >= (double)var6)) {
            return false;
         } else {
            double var7;
            if (var1 == 0) {
               if (var2 > 0) {
                  var7 = 1.5707963267948966D;
               } else {
                  var7 = 4.71238898038469D;
               }
            } else {
               var7 = Math.atan((double)var2 / (double)var1);
               if (var1 < 0) {
                  var7 += 3.141592653589793D;
               } else if (var7 < 0.0D) {
                  var7 += 6.283185307179586D;
               }
            }

            this.setFlag(8, true);
            GTKColorChooserPanel.this.setHue((float)(1.0D - var7 / 3.141592653589793D / 2.0D), true);
            this.setFlag(8, false);
            this.setHueAngle(var7);
            this.setSaturationAndBrightness(GTKColorChooserPanel.this.getSaturation(), GTKColorChooserPanel.this.getBrightness());
            return true;
         }
      }

      private void setAngleFromHue(float var1) {
         this.setHueAngle((1.0D - (double)var1) * 3.141592653589793D * 2.0D);
      }

      private void setHueAngle(double var1) {
         double var3 = this.angle;
         this.angle = var1;
         if (var1 != var3) {
            this.setFlag(1, true);
            this.repaint();
         }

      }

      private int getIndicatorSize() {
         return 8;
      }

      private int getTriangleCircumscribedRadius() {
         return 72;
      }

      private int getWheelXOrigin() {
         return 85;
      }

      private int getWheelYOrigin() {
         return 85;
      }

      private int getWheelWidth() {
         return 13;
      }

      private void setFocusType(int var1) {
         if (var1 == 0) {
            this.setFlag(16, false);
            this.setFlag(32, false);
            this.repaint();
         } else {
            byte var2 = 16;
            byte var3 = 32;
            if (var1 == 2) {
               var2 = 32;
               var3 = 16;
            }

            if (!this.isSet(var2)) {
               this.setFlag(var2, true);
               this.repaint();
               this.setFlag(var3, false);
            }
         }

      }

      private int getWheelRadius() {
         return 85;
      }

      private void setFlag(int var1, boolean var2) {
         if (var2) {
            this.flags |= var1;
         } else {
            this.flags &= ~var1;
         }

      }

      private boolean isSet(int var1) {
         return (this.flags & var1) == var1;
      }

      private int colorWheelLocationToRGB(int var1, int var2, double var3) {
         double var5 = Math.acos((double)var1 / var3);
         int var7;
         if (var5 < 1.0471975803375244D) {
            if (var2 < 0) {
               var7 = 16711680 | Math.min(255, (int)(255.0D * var5 / 1.0471975803375244D)) << 8;
            } else {
               var7 = 16711680 | Math.min(255, (int)(255.0D * var5 / 1.0471975803375244D));
            }
         } else if (var5 < 2.094395160675049D) {
            --var5;
            if (var2 < 0) {
               var7 = '\uff00' | Math.max(0, 255 - (int)(255.0D * var5 / 1.0471975803375244D)) << 16;
            } else {
               var7 = 255 | Math.max(0, 255 - (int)(255.0D * var5 / 1.0471975803375244D)) << 16;
            }
         } else {
            var5 -= 2.094395160675049D;
            if (var2 < 0) {
               var7 = '\uff00' | Math.min(255, (int)(255.0D * var5 / 1.0471975803375244D));
            } else {
               var7 = 255 | Math.min(255, (int)(255.0D * var5 / 1.0471975803375244D)) << 8;
            }
         }

         return var7;
      }

      void incrementHue(boolean var1) {
         float var2 = GTKColorChooserPanel.this.triangle.getGTKColorChooserPanel().getHue();
         if (var1) {
            var2 += 0.0027777778F;
         } else {
            var2 -= 0.0027777778F;
         }

         if (var2 > 1.0F) {
            --var2;
         } else if (var2 < 0.0F) {
            ++var2;
         }

         this.getGTKColorChooserPanel().setHue(var2, true);
      }
   }
}
