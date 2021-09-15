package com.apple.laf;

import java.awt.Component;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.peer.MenuComponentPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import sun.awt.SunToolkit;
import sun.lwawt.LWToolkit;
import sun.lwawt.macosx.CMenu;
import sun.lwawt.macosx.CMenuItem;
import sun.lwawt.macosx.LWCToolkit;

final class ScreenMenu extends Menu implements ContainerListener, ComponentListener, ScreenMenuPropertyHandler {
   private transient long fModelPtr;
   private final Hashtable<Component, MenuItem> fItems;
   private final JMenu fInvoker;
   private Component fLastMouseEventTarget;
   private Rectangle fLastTargetRect;
   private volatile Rectangle[] fItemBounds;
   private ScreenMenuPropertyListener fPropertyListener;
   private int[] childHashArray;

   private static native long addMenuListeners(ScreenMenu var0, long var1);

   private static native void removeMenuListeners(long var0);

   ScreenMenu(JMenu var1) {
      super(var1.getText());
      this.fInvoker = var1;
      int var2 = this.fInvoker.getMenuComponentCount();
      if (var2 < 5) {
         var2 = 5;
      }

      this.fItems = new Hashtable(var2);
      this.setEnabled(this.fInvoker.isEnabled());
      this.updateItems();
   }

   private static boolean needsUpdate(Component[] var0, int[] var1) {
      if (var0 != null && var1 != null) {
         if (var1.length != var0.length) {
            return true;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               int var3 = getHashCode(var0[var2]);
               if (var3 != var1[var2]) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private void updateItems() {
      int var1 = this.fInvoker.getMenuComponentCount();
      Component[] var2 = this.fInvoker.getMenuComponents();
      if (needsUpdate(var2, this.childHashArray)) {
         this.removeAll();
         this.fItems.clear();
         if (var1 <= 0) {
            return;
         }

         this.childHashArray = new int[var1];

         for(int var3 = 0; var3 < var1; ++var3) {
            this.addItem(var2[var3]);
            this.childHashArray[var3] = getHashCode(var2[var3]);
         }
      }

   }

   public void invokeOpenLater() {
      final JMenu var1 = this.fInvoker;
      if (var1 == null) {
         System.err.println("invoker is null!");
      } else {
         try {
            LWCToolkit.invokeAndWait((Runnable)(new Runnable() {
               public void run() {
                  var1.setSelected(true);
                  var1.validate();
                  ScreenMenu.this.updateItems();
                  ScreenMenu.this.fItemBounds = new Rectangle[var1.getMenuComponentCount()];
               }
            }), var1);
         } catch (Exception var3) {
            System.err.println((Object)var3);
            var3.printStackTrace();
         }

      }
   }

   public void invokeMenuClosing() {
      final JMenu var1 = this.fInvoker;
      if (var1 != null) {
         try {
            LWCToolkit.invokeAndWait((Runnable)(new Runnable() {
               public void run() {
                  var1.setSelected(false);
                  if (ScreenMenu.this.fItemBounds != null) {
                     for(int var1x = 0; var1x < ScreenMenu.this.fItemBounds.length; ++var1x) {
                        ScreenMenu.this.fItemBounds[var1x] = null;
                     }
                  }

                  ScreenMenu.this.fItemBounds = null;
               }
            }), var1);
         } catch (Exception var3) {
            var3.printStackTrace();
         }

      }
   }

   public void handleItemTargeted(int var1, int var2, int var3, int var4, int var5) {
      if (this.fItemBounds != null && var1 >= 0 && var1 <= this.fItemBounds.length - 1) {
         Rectangle var6 = new Rectangle(var3, var2, var5 - var3, var4 - var2);
         this.fItemBounds[var1] = var6;
      }
   }

   public void handleMouseEvent(final int var1, final int var2, final int var3, final int var4, final long var5) {
      if (var1 != 0) {
         if (this.fItemBounds != null) {
            SunToolkit.executeOnEventHandlerThread(this.fInvoker, new Runnable() {
               public void run() {
                  Component var1x = null;
                  Rectangle var2x = null;

                  for(int var3x = 0; var3x < ScreenMenu.this.fItemBounds.length; ++var3x) {
                     Rectangle var4x = ScreenMenu.this.fItemBounds[var3x];
                     if (var4x != null && var4x.contains(var2, var3)) {
                        var1x = ScreenMenu.this.fInvoker.getMenuComponent(var3x);
                        var2x = var4x;
                        break;
                     }
                  }

                  if (var1x != null || ScreenMenu.this.fLastMouseEventTarget != null) {
                     if (var1x != ScreenMenu.this.fLastMouseEventTarget) {
                        if (ScreenMenu.this.fLastMouseEventTarget != null) {
                           LWToolkit.postEvent(new MouseEvent(ScreenMenu.this.fLastMouseEventTarget, 505, var5, var4, var2 - ScreenMenu.this.fLastTargetRect.x, var3 - ScreenMenu.this.fLastTargetRect.y, 0, false));
                        }

                        if (var1x != null) {
                           LWToolkit.postEvent(new MouseEvent(var1x, 504, var5, var4, var2 - var2x.x, var3 - var2x.y, 0, false));
                        }

                        ScreenMenu.this.fLastMouseEventTarget = var1x;
                        ScreenMenu.this.fLastTargetRect = var2x;
                     }

                     if (var1x != null) {
                        LWToolkit.postEvent(new MouseEvent(var1x, var1, var5, var4, var2 - var2x.x, var3 - var2x.y, 0, false));
                     }
                  }
               }
            });
         }
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         super.addNotify();
         if (this.fModelPtr == 0L) {
            this.fInvoker.getPopupMenu().addContainerListener(this);
            this.fInvoker.addComponentListener(this);
            this.fPropertyListener = new ScreenMenuPropertyListener(this);
            this.fInvoker.addPropertyChangeListener(this.fPropertyListener);
            Icon var2 = this.fInvoker.getIcon();
            if (var2 != null) {
               this.setIcon(var2);
            }

            String var3 = this.fInvoker.getToolTipText();
            if (var3 != null) {
               this.setToolTipText(var3);
            }

            MenuComponentPeer var4 = this.getPeer();
            if (var4 instanceof CMenu) {
               CMenu var5 = (CMenu)var4;
               long var6 = var5.getNativeMenu();
               this.fModelPtr = addMenuListeners(this, var6);
            }
         }

      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         super.removeNotify();
         this.fItems.clear();
         if (this.fModelPtr != 0L) {
            removeMenuListeners(this.fModelPtr);
            this.fModelPtr = 0L;
            this.fInvoker.getPopupMenu().removeContainerListener(this);
            this.fInvoker.removeComponentListener(this);
            this.fInvoker.removePropertyChangeListener(this.fPropertyListener);
         }

      }
   }

   public void componentAdded(ContainerEvent var1) {
      this.addItem(var1.getChild());
   }

   public void componentRemoved(ContainerEvent var1) {
      Component var2 = var1.getChild();
      MenuItem var3 = (MenuItem)this.fItems.remove(var2);
      if (var3 != null) {
         this.remove(var3);
      }
   }

   public void componentResized(ComponentEvent var1) {
   }

   public void componentMoved(ComponentEvent var1) {
   }

   public void componentShown(ComponentEvent var1) {
      this.setVisible(true);
   }

   public void componentHidden(ComponentEvent var1) {
      this.setVisible(false);
   }

   private void setVisible(boolean var1) {
      MenuContainer var2 = this.getParent();
      if (var2 != null && var2 instanceof ScreenMenu) {
         ScreenMenu var3 = (ScreenMenu)var2;
         var3.setChildVisible(this.fInvoker, var1);
      }

   }

   public void setChildVisible(JMenuItem var1, boolean var2) {
      this.fItems.remove(var1);
      this.updateItems();
   }

   public void setAccelerator(KeyStroke var1) {
   }

   public void setIndeterminate(boolean var1) {
   }

   public void setToolTipText(String var1) {
      MenuComponentPeer var2 = this.getPeer();
      if (var2 instanceof CMenuItem) {
         CMenuItem var3 = (CMenuItem)var2;
         var3.setToolTipText(var1);
      }
   }

   public void setIcon(Icon var1) {
      MenuComponentPeer var2 = this.getPeer();
      if (var2 instanceof CMenuItem) {
         CMenuItem var3 = (CMenuItem)var2;
         Image var4 = null;
         if (var1 != null && var1.getIconWidth() > 0 && var1.getIconHeight() > 0) {
            var4 = AquaIcon.getImageForIcon(var1);
         }

         var3.setImage(var4);
      }
   }

   private static int getHashCode(Component var0) {
      int var1 = var0.hashCode();
      if (var0 instanceof JMenuItem) {
         JMenuItem var2 = (JMenuItem)var0;
         String var3 = var2.getText();
         if (var3 != null) {
            var1 ^= var3.hashCode();
         }

         Icon var4 = var2.getIcon();
         if (var4 != null) {
            var1 ^= var4.hashCode();
         }

         Icon var5 = var2.getDisabledIcon();
         if (var5 != null) {
            var1 ^= var5.hashCode();
         }

         Action var6 = var2.getAction();
         if (var6 != null) {
            var1 ^= var6.hashCode();
         }

         KeyStroke var7 = var2.getAccelerator();
         if (var7 != null) {
            var1 ^= var7.hashCode();
         }

         var1 ^= Boolean.valueOf(var2.isVisible()).hashCode();
         var1 ^= Boolean.valueOf(var2.isEnabled()).hashCode();
         var1 ^= Boolean.valueOf(var2.isSelected()).hashCode();
      } else if (var0 instanceof JSeparator) {
         var1 ^= "-".hashCode();
      }

      return var1;
   }

   private void addItem(Component var1) {
      if (var1.isVisible()) {
         Object var2 = (MenuItem)this.fItems.get(var1);
         if (var2 == null) {
            if (var1 instanceof JMenu) {
               var2 = new ScreenMenu((JMenu)var1);
            } else if (var1 instanceof JCheckBoxMenuItem) {
               var2 = new ScreenMenuItemCheckbox((JCheckBoxMenuItem)var1);
            } else if (var1 instanceof JRadioButtonMenuItem) {
               var2 = new ScreenMenuItemCheckbox((JRadioButtonMenuItem)var1);
            } else if (var1 instanceof JMenuItem) {
               var2 = new ScreenMenuItem((JMenuItem)var1);
            } else if (var1 instanceof JPopupMenu.Separator || var1 instanceof JSeparator) {
               var2 = new MenuItem("-");
            }

            if (var2 != null) {
               this.fItems.put(var1, var2);
            }
         }

         if (var2 != null) {
            this.add((MenuItem)var2);
         }

      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
   }
}
