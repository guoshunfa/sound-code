package com.sun.beans.editors;

import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public class ColorEditor extends Panel implements PropertyEditor {
   private static final long serialVersionUID = 1781257185164716054L;
   private String[] colorNames = new String[]{" ", "white", "lightGray", "gray", "darkGray", "black", "red", "pink", "orange", "yellow", "green", "magenta", "cyan", "blue"};
   private Color[] colors;
   private Canvas sample;
   private int sampleHeight;
   private int sampleWidth;
   private int hPad;
   private int ourWidth;
   private Color color;
   private TextField text;
   private Choice choser;
   private PropertyChangeSupport support;

   public ColorEditor() {
      this.colors = new Color[]{null, Color.white, Color.lightGray, Color.gray, Color.darkGray, Color.black, Color.red, Color.pink, Color.orange, Color.yellow, Color.green, Color.magenta, Color.cyan, Color.blue};
      this.sampleHeight = 20;
      this.sampleWidth = 40;
      this.hPad = 5;
      this.support = new PropertyChangeSupport(this);
      this.setLayout((LayoutManager)null);
      this.ourWidth = this.hPad;
      Panel var1 = new Panel();
      var1.setLayout((LayoutManager)null);
      var1.setBackground(Color.black);
      this.sample = new Canvas();
      var1.add(this.sample);
      this.sample.reshape(2, 2, this.sampleWidth, this.sampleHeight);
      this.add(var1);
      var1.reshape(this.ourWidth, 2, this.sampleWidth + 4, this.sampleHeight + 4);
      this.ourWidth += this.sampleWidth + 4 + this.hPad;
      this.text = new TextField("", 14);
      this.add(this.text);
      this.text.reshape(this.ourWidth, 0, 100, 30);
      this.ourWidth += 100 + this.hPad;
      this.choser = new Choice();
      boolean var2 = false;

      for(int var3 = 0; var3 < this.colorNames.length; ++var3) {
         this.choser.addItem(this.colorNames[var3]);
      }

      this.add(this.choser);
      this.choser.reshape(this.ourWidth, 0, 100, 30);
      this.ourWidth += 100 + this.hPad;
      this.resize(this.ourWidth, 40);
   }

   public void setValue(Object var1) {
      Color var2 = (Color)var1;
      this.changeColor(var2);
   }

   public Dimension preferredSize() {
      return new Dimension(this.ourWidth, 40);
   }

   public boolean keyUp(Event var1, int var2) {
      if (var1.target == this.text) {
         try {
            this.setAsText(this.text.getText());
         } catch (IllegalArgumentException var4) {
         }
      }

      return false;
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         this.changeColor((Color)null);
      } else {
         int var2 = var1.indexOf(44);
         int var3 = var1.indexOf(44, var2 + 1);
         if (var2 >= 0 && var3 >= 0) {
            try {
               int var4 = Integer.parseInt(var1.substring(0, var2));
               int var5 = Integer.parseInt(var1.substring(var2 + 1, var3));
               int var6 = Integer.parseInt(var1.substring(var3 + 1));
               Color var7 = new Color(var4, var5, var6);
               this.changeColor(var7);
            } catch (Exception var8) {
               throw new IllegalArgumentException(var1);
            }
         } else {
            throw new IllegalArgumentException(var1);
         }
      }
   }

   public boolean action(Event var1, Object var2) {
      if (var1.target == this.choser) {
         this.changeColor(this.colors[this.choser.getSelectedIndex()]);
      }

      return false;
   }

   public String getJavaInitializationString() {
      return this.color != null ? "new java.awt.Color(" + this.color.getRGB() + ",true)" : "null";
   }

   private void changeColor(Color var1) {
      if (var1 == null) {
         this.color = null;
         this.text.setText("");
      } else {
         this.color = var1;
         this.text.setText("" + var1.getRed() + "," + var1.getGreen() + "," + var1.getBlue());
         int var2 = 0;

         for(int var3 = 0; var3 < this.colorNames.length; ++var3) {
            if (this.color.equals(this.colors[var3])) {
               var2 = var3;
            }
         }

         this.choser.select(var2);
         this.sample.setBackground(this.color);
         this.sample.repaint();
         this.support.firePropertyChange("", (Object)null, (Object)null);
      }
   }

   public Object getValue() {
      return this.color;
   }

   public boolean isPaintable() {
      return true;
   }

   public void paintValue(Graphics var1, Rectangle var2) {
      Color var3 = var1.getColor();
      var1.setColor(Color.black);
      var1.drawRect(var2.x, var2.y, var2.width - 3, var2.height - 3);
      var1.setColor(this.color);
      var1.fillRect(var2.x + 1, var2.y + 1, var2.width - 4, var2.height - 4);
      var1.setColor(var3);
   }

   public String getAsText() {
      return this.color != null ? this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue() : null;
   }

   public String[] getTags() {
      return null;
   }

   public Component getCustomEditor() {
      return this;
   }

   public boolean supportsCustomEditor() {
      return true;
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.support.addPropertyChangeListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.support.removePropertyChangeListener(var1);
   }
}
