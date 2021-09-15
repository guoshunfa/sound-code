package com.apple.laf;

import java.awt.Container;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.util.Vector;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;

public class AquaInternalFrameManager extends DefaultDesktopManager {
   JInternalFrame fCurrentFrame;
   JInternalFrame fInitialFrame;
   AquaInternalFramePaneUI fCurrentPaneUI;
   Vector<JInternalFrame> fChildFrames = new Vector(1);

   public void closeFrame(JInternalFrame var1) {
      if (var1 == this.fCurrentFrame) {
         this.activateNextFrame();
      }

      this.fChildFrames.removeElement(var1);
      super.closeFrame(var1);
   }

   public void deiconifyFrame(JInternalFrame var1) {
      JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
      var1.reshape(var2.getX(), var2.getY(), var1.getWidth(), var1.getHeight());
      super.deiconifyFrame(var1);
   }

   void addIcon(Container var1, JInternalFrame.JDesktopIcon var2) {
      var1.add(var2);
   }

   public void iconifyFrame(JInternalFrame var1) {
      JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
      Rectangle var4 = this.getBoundsForIconOf(var1);
      var2.setBounds(var4.x, var4.y, var4.width, var4.height);
      Container var3 = var1.getParent();
      if (var3 != null) {
         var3.remove(var1);
         this.addIcon(var3, var2);
         var3.repaint(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
      }
   }

   public void activateFrame(JInternalFrame var1) {
      try {
         if (var1 != null) {
            super.activateFrame(var1);
         }

         if (this.fChildFrames.indexOf(var1) == -1) {
            this.fChildFrames.addElement(var1);
         }

         if (this.fCurrentFrame != null && var1 != this.fCurrentFrame && this.fCurrentFrame.isSelected()) {
            this.fCurrentFrame.setSelected(false);
         }

         if (var1 != null && !var1.isSelected()) {
            var1.setSelected(true);
         }

         this.fCurrentFrame = var1;
      } catch (PropertyVetoException var3) {
      }

   }

   private void switchFrame(boolean var1) {
      if (this.fCurrentFrame == null) {
         if (this.fInitialFrame != null) {
            this.activateFrame(this.fInitialFrame);
         }

      } else {
         int var2 = this.fChildFrames.size();
         if (var2 > 1) {
            int var3 = this.fChildFrames.indexOf(this.fCurrentFrame);
            if (var3 == -1) {
               this.fCurrentFrame = null;
            } else {
               int var4;
               if (var1) {
                  var4 = var3 + 1;
                  if (var4 == var2) {
                     var4 = 0;
                  }
               } else {
                  var4 = var3 - 1;
                  if (var4 == -1) {
                     var4 = var2 - 1;
                  }
               }

               JInternalFrame var5 = (JInternalFrame)this.fChildFrames.elementAt(var4);
               this.activateFrame(var5);
               this.fCurrentFrame = var5;
            }
         }
      }
   }

   public void activateNextFrame() {
      this.switchFrame(true);
   }

   public void activateNextFrame(JInternalFrame var1) {
      this.fInitialFrame = var1;
      this.switchFrame(true);
   }

   public void activatePreviousFrame() {
      this.switchFrame(false);
   }
}
