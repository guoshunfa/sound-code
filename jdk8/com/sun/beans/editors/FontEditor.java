package com.sun.beans.editors;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public class FontEditor extends Panel implements PropertyEditor {
   private static final long serialVersionUID = 6732704486002715933L;
   private Font font;
   private Toolkit toolkit;
   private String sampleText = "Abcde...";
   private Label sample;
   private Choice familyChoser;
   private Choice styleChoser;
   private Choice sizeChoser;
   private String[] fonts;
   private String[] styleNames = new String[]{"plain", "bold", "italic"};
   private int[] styles = new int[]{0, 1, 2};
   private int[] pointSizes = new int[]{3, 5, 8, 10, 12, 14, 18, 24, 36, 48};
   private PropertyChangeSupport support = new PropertyChangeSupport(this);

   public FontEditor() {
      this.setLayout((LayoutManager)null);
      this.toolkit = Toolkit.getDefaultToolkit();
      this.fonts = this.toolkit.getFontList();
      this.familyChoser = new Choice();

      int var1;
      for(var1 = 0; var1 < this.fonts.length; ++var1) {
         this.familyChoser.addItem(this.fonts[var1]);
      }

      this.add(this.familyChoser);
      this.familyChoser.reshape(20, 5, 100, 30);
      this.styleChoser = new Choice();

      for(var1 = 0; var1 < this.styleNames.length; ++var1) {
         this.styleChoser.addItem(this.styleNames[var1]);
      }

      this.add(this.styleChoser);
      this.styleChoser.reshape(145, 5, 70, 30);
      this.sizeChoser = new Choice();

      for(var1 = 0; var1 < this.pointSizes.length; ++var1) {
         this.sizeChoser.addItem("" + this.pointSizes[var1]);
      }

      this.add(this.sizeChoser);
      this.sizeChoser.reshape(220, 5, 70, 30);
      this.resize(300, 40);
   }

   public Dimension preferredSize() {
      return new Dimension(300, 40);
   }

   public void setValue(Object var1) {
      this.font = (Font)var1;
      if (this.font != null) {
         this.changeFont(this.font);

         int var2;
         for(var2 = 0; var2 < this.fonts.length; ++var2) {
            if (this.fonts[var2].equals(this.font.getFamily())) {
               this.familyChoser.select(var2);
               break;
            }
         }

         for(var2 = 0; var2 < this.styleNames.length; ++var2) {
            if (this.font.getStyle() == this.styles[var2]) {
               this.styleChoser.select(var2);
               break;
            }
         }

         for(var2 = 0; var2 < this.pointSizes.length; ++var2) {
            if (this.font.getSize() <= this.pointSizes[var2]) {
               this.sizeChoser.select(var2);
               break;
            }
         }

      }
   }

   private void changeFont(Font var1) {
      this.font = var1;
      if (this.sample != null) {
         this.remove(this.sample);
      }

      this.sample = new Label(this.sampleText);
      this.sample.setFont(this.font);
      this.add(this.sample);
      Container var2 = this.getParent();
      if (var2 != null) {
         var2.invalidate();
         var2.layout();
      }

      this.invalidate();
      this.layout();
      this.repaint();
      this.support.firePropertyChange("", (Object)null, (Object)null);
   }

   public Object getValue() {
      return this.font;
   }

   public String getJavaInitializationString() {
      return this.font == null ? "null" : "new java.awt.Font(\"" + this.font.getName() + "\", " + this.font.getStyle() + ", " + this.font.getSize() + ")";
   }

   public boolean action(Event var1, Object var2) {
      String var3 = this.familyChoser.getSelectedItem();
      int var4 = this.styles[this.styleChoser.getSelectedIndex()];
      int var5 = this.pointSizes[this.sizeChoser.getSelectedIndex()];

      try {
         Font var6 = new Font(var3, var4, var5);
         this.changeFont(var6);
      } catch (Exception var7) {
         System.err.println("Couldn't create font " + var3 + "-" + this.styleNames[var4] + "-" + var5);
      }

      return false;
   }

   public boolean isPaintable() {
      return true;
   }

   public void paintValue(Graphics var1, Rectangle var2) {
      Font var3 = var1.getFont();
      var1.setFont(this.font);
      FontMetrics var4 = var1.getFontMetrics();
      int var5 = (var2.height - var4.getAscent()) / 2;
      var1.drawString((String)this.sampleText, 0, var2.height - var5);
      var1.setFont(var3);
   }

   public String getAsText() {
      if (this.font == null) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.font.getName());
         var1.append(' ');
         boolean var2 = this.font.isBold();
         if (var2) {
            var1.append("BOLD");
         }

         boolean var3 = this.font.isItalic();
         if (var3) {
            var1.append("ITALIC");
         }

         if (var2 || var3) {
            var1.append(' ');
         }

         var1.append(this.font.getSize());
         return var1.toString();
      }
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      this.setValue(var1 == null ? null : Font.decode(var1));
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
