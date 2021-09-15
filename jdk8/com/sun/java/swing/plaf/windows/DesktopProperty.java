package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class DesktopProperty implements UIDefaults.ActiveValue {
   private static boolean updatePending;
   private static final ReferenceQueue<DesktopProperty> queue = new ReferenceQueue();
   private DesktopProperty.WeakPCL pcl;
   private final String key;
   private Object value;
   private final Object fallback;

   static void flushUnreferencedProperties() {
      DesktopProperty.WeakPCL var0;
      while((var0 = (DesktopProperty.WeakPCL)queue.poll()) != null) {
         var0.dispose();
      }

   }

   private static synchronized void setUpdatePending(boolean var0) {
      updatePending = var0;
   }

   private static synchronized boolean isUpdatePending() {
      return updatePending;
   }

   private static void updateAllUIs() {
      Class var0 = UIManager.getLookAndFeel().getClass();
      if (var0.getPackage().equals(DesktopProperty.class.getPackage())) {
         XPStyle.invalidateStyle();
      }

      Frame[] var1 = Frame.getFrames();
      Frame[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Frame var5 = var2[var4];
         updateWindowUI(var5);
      }

   }

   private static void updateWindowUI(Window var0) {
      SwingUtilities.updateComponentTreeUI(var0);
      Window[] var1 = var0.getOwnedWindows();
      Window[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Window var5 = var2[var4];
         updateWindowUI(var5);
      }

   }

   public DesktopProperty(String var1, Object var2) {
      this.key = var1;
      this.fallback = var2;
      flushUnreferencedProperties();
   }

   public Object createValue(UIDefaults var1) {
      if (this.value == null) {
         this.value = this.configureValue(this.getValueFromDesktop());
         if (this.value == null) {
            this.value = this.configureValue(this.getDefaultValue());
         }
      }

      return this.value;
   }

   protected Object getValueFromDesktop() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      if (this.pcl == null) {
         this.pcl = new DesktopProperty.WeakPCL(this, this.getKey(), UIManager.getLookAndFeel());
         var1.addPropertyChangeListener(this.getKey(), this.pcl);
      }

      return var1.getDesktopProperty(this.getKey());
   }

   protected Object getDefaultValue() {
      return this.fallback;
   }

   public void invalidate(LookAndFeel var1) {
      this.invalidate();
   }

   public void invalidate() {
      this.value = null;
   }

   protected void updateUI() {
      if (!isUpdatePending()) {
         setUpdatePending(true);
         Runnable var1 = new Runnable() {
            public void run() {
               DesktopProperty.updateAllUIs();
               DesktopProperty.setUpdatePending(false);
            }
         };
         SwingUtilities.invokeLater(var1);
      }

   }

   protected Object configureValue(Object var1) {
      if (var1 != null) {
         if (var1 instanceof Color) {
            return new ColorUIResource((Color)var1);
         }

         if (var1 instanceof Font) {
            return new FontUIResource((Font)var1);
         }

         if (var1 instanceof UIDefaults.LazyValue) {
            var1 = ((UIDefaults.LazyValue)var1).createValue((UIDefaults)null);
         } else if (var1 instanceof UIDefaults.ActiveValue) {
            var1 = ((UIDefaults.ActiveValue)var1).createValue((UIDefaults)null);
         }
      }

      return var1;
   }

   protected String getKey() {
      return this.key;
   }

   private static class WeakPCL extends WeakReference<DesktopProperty> implements PropertyChangeListener {
      private String key;
      private LookAndFeel laf;

      WeakPCL(DesktopProperty var1, String var2, LookAndFeel var3) {
         super(var1, DesktopProperty.queue);
         this.key = var2;
         this.laf = var3;
      }

      public void propertyChange(PropertyChangeEvent var1) {
         DesktopProperty var2 = (DesktopProperty)this.get();
         if (var2 != null && this.laf == UIManager.getLookAndFeel()) {
            var2.invalidate(this.laf);
            var2.updateUI();
         } else {
            this.dispose();
         }

      }

      void dispose() {
         Toolkit.getDefaultToolkit().removePropertyChangeListener(this.key, this);
      }
   }
}
