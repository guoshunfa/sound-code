package com.sun.java.swing.plaf.windows;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;
import javax.swing.plaf.UIResource;

public class WindowsDesktopManager extends DefaultDesktopManager implements Serializable, UIResource {
   private WeakReference<JInternalFrame> currentFrameRef;

   public void activateFrame(JInternalFrame var1) {
      JInternalFrame var2 = this.currentFrameRef != null ? (JInternalFrame)this.currentFrameRef.get() : null;

      try {
         super.activateFrame(var1);
         if (var2 != null && var1 != var2) {
            if (var2.isMaximum() && var1.getClientProperty("JInternalFrame.frameType") != "optionDialog" && !var2.isIcon()) {
               var2.setMaximum(false);
               if (var1.isMaximizable()) {
                  if (!var1.isMaximum()) {
                     var1.setMaximum(true);
                  } else if (var1.isMaximum() && var1.isIcon()) {
                     var1.setIcon(false);
                  } else {
                     var1.setMaximum(false);
                  }
               }
            }

            if (var2.isSelected()) {
               var2.setSelected(false);
            }
         }

         if (!var1.isSelected()) {
            var1.setSelected(true);
         }
      } catch (PropertyVetoException var4) {
      }

      if (var1 != var2) {
         this.currentFrameRef = new WeakReference(var1);
      }

   }
}
