package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MotifSplitPaneDivider extends BasicSplitPaneDivider {
   private static final Cursor defaultCursor = Cursor.getPredefinedCursor(0);
   public static final int minimumThumbSize = 6;
   public static final int defaultDividerSize = 18;
   protected static final int pad = 6;
   private int hThumbOffset = 30;
   private int vThumbOffset = 40;
   protected int hThumbWidth = 12;
   protected int hThumbHeight = 18;
   protected int vThumbWidth = 18;
   protected int vThumbHeight = 12;
   protected Color highlightColor = UIManager.getColor("SplitPane.highlight");
   protected Color shadowColor = UIManager.getColor("SplitPane.shadow");
   protected Color focusedColor = UIManager.getColor("SplitPane.activeThumb");

   public MotifSplitPaneDivider(BasicSplitPaneUI var1) {
      super(var1);
      this.setDividerSize(this.hThumbWidth + 6);
   }

   public void setDividerSize(int var1) {
      Insets var2 = this.getInsets();
      int var3 = 0;
      if (this.getBasicSplitPaneUI().getOrientation() == 1) {
         if (var2 != null) {
            var3 = var2.left + var2.right;
         }
      } else if (var2 != null) {
         var3 = var2.top + var2.bottom;
      }

      if (var1 < 12 + var3) {
         this.setDividerSize(12 + var3);
      } else {
         this.vThumbHeight = this.hThumbWidth = var1 - 6 - var3;
         super.setDividerSize(var1);
      }

   }

   public void paint(Graphics var1) {
      Color var2 = this.getBackground();
      Dimension var3 = this.getSize();
      var1.setColor(this.getBackground());
      var1.fillRect(0, 0, var3.width, var3.height);
      int var4;
      int var5;
      int var6;
      if (this.getBasicSplitPaneUI().getOrientation() == 1) {
         var4 = var3.width / 2;
         var5 = var4 - this.hThumbWidth / 2;
         var6 = this.hThumbOffset;
         var1.setColor(this.shadowColor);
         var1.drawLine(var4 - 1, 0, var4 - 1, var3.height);
         var1.setColor(this.highlightColor);
         var1.drawLine(var4, 0, var4, var3.height);
         var1.setColor(this.splitPane.hasFocus() ? this.focusedColor : this.getBackground());
         var1.fillRect(var5 + 1, var6 + 1, this.hThumbWidth - 2, this.hThumbHeight - 1);
         var1.setColor(this.highlightColor);
         var1.drawLine(var5, var6, var5 + this.hThumbWidth - 1, var6);
         var1.drawLine(var5, var6 + 1, var5, var6 + this.hThumbHeight - 1);
         var1.setColor(this.shadowColor);
         var1.drawLine(var5 + 1, var6 + this.hThumbHeight - 1, var5 + this.hThumbWidth - 1, var6 + this.hThumbHeight - 1);
         var1.drawLine(var5 + this.hThumbWidth - 1, var6 + 1, var5 + this.hThumbWidth - 1, var6 + this.hThumbHeight - 2);
      } else {
         var4 = var3.height / 2;
         var5 = var3.width - this.vThumbOffset;
         var6 = var3.height / 2 - this.vThumbHeight / 2;
         var1.setColor(this.shadowColor);
         var1.drawLine(0, var4 - 1, var3.width, var4 - 1);
         var1.setColor(this.highlightColor);
         var1.drawLine(0, var4, var3.width, var4);
         var1.setColor(this.splitPane.hasFocus() ? this.focusedColor : this.getBackground());
         var1.fillRect(var5 + 1, var6 + 1, this.vThumbWidth - 1, this.vThumbHeight - 1);
         var1.setColor(this.highlightColor);
         var1.drawLine(var5, var6, var5 + this.vThumbWidth, var6);
         var1.drawLine(var5, var6 + 1, var5, var6 + this.vThumbHeight);
         var1.setColor(this.shadowColor);
         var1.drawLine(var5 + 1, var6 + this.vThumbHeight, var5 + this.vThumbWidth, var6 + this.vThumbHeight);
         var1.drawLine(var5 + this.vThumbWidth, var6 + 1, var5 + this.vThumbWidth, var6 + this.vThumbHeight - 1);
      }

      super.paint(var1);
   }

   public Dimension getMinimumSize() {
      return this.getPreferredSize();
   }

   public void setBasicSplitPaneUI(BasicSplitPaneUI var1) {
      if (this.splitPane != null) {
         this.splitPane.removePropertyChangeListener(this);
         if (this.mouseHandler != null) {
            this.splitPane.removeMouseListener(this.mouseHandler);
            this.splitPane.removeMouseMotionListener(this.mouseHandler);
            this.removeMouseListener(this.mouseHandler);
            this.removeMouseMotionListener(this.mouseHandler);
            this.mouseHandler = null;
         }
      }

      this.splitPaneUI = var1;
      if (var1 != null) {
         this.splitPane = var1.getSplitPane();
         if (this.splitPane != null) {
            if (this.mouseHandler == null) {
               this.mouseHandler = new MotifSplitPaneDivider.MotifMouseHandler();
            }

            this.splitPane.addMouseListener(this.mouseHandler);
            this.splitPane.addMouseMotionListener(this.mouseHandler);
            this.addMouseListener(this.mouseHandler);
            this.addMouseMotionListener(this.mouseHandler);
            this.splitPane.addPropertyChangeListener(this);
            if (this.splitPane.isOneTouchExpandable()) {
               this.oneTouchExpandableChanged();
            }
         }
      } else {
         this.splitPane = null;
      }

   }

   private boolean isInThumb(int var1, int var2) {
      Dimension var3 = this.getSize();
      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      if (this.getBasicSplitPaneUI().getOrientation() == 1) {
         var8 = var3.width / 2;
         var4 = var8 - this.hThumbWidth / 2;
         var5 = this.hThumbOffset;
         var6 = this.hThumbWidth;
         var7 = this.hThumbHeight;
      } else {
         var8 = var3.height / 2;
         var4 = var3.width - this.vThumbOffset;
         var5 = var3.height / 2 - this.vThumbHeight / 2;
         var6 = this.vThumbWidth;
         var7 = this.vThumbHeight;
      }

      return var1 >= var4 && var1 < var4 + var6 && var2 >= var5 && var2 < var5 + var7;
   }

   private BasicSplitPaneDivider.DragController getDragger() {
      return this.dragger;
   }

   private JSplitPane getSplitPane() {
      return this.splitPane;
   }

   private class MotifMouseHandler extends BasicSplitPaneDivider.MouseHandler {
      private MotifMouseHandler() {
         super();
      }

      public void mousePressed(MouseEvent var1) {
         if (var1.getSource() == MotifSplitPaneDivider.this && MotifSplitPaneDivider.this.getDragger() == null && MotifSplitPaneDivider.this.getSplitPane().isEnabled() && MotifSplitPaneDivider.this.isInThumb(var1.getX(), var1.getY())) {
            super.mousePressed(var1);
         }

      }

      public void mouseMoved(MouseEvent var1) {
         if (MotifSplitPaneDivider.this.getDragger() == null) {
            if (!MotifSplitPaneDivider.this.isInThumb(var1.getX(), var1.getY())) {
               if (MotifSplitPaneDivider.this.getCursor() != MotifSplitPaneDivider.defaultCursor) {
                  MotifSplitPaneDivider.this.setCursor(MotifSplitPaneDivider.defaultCursor);
               }

            } else {
               super.mouseMoved(var1);
            }
         }
      }

      // $FF: synthetic method
      MotifMouseHandler(Object var2) {
         this();
      }
   }
}
