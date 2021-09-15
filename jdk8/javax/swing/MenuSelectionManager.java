package javax.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MenuSelectionManager {
   private Vector<MenuElement> selection = new Vector();
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;
   private static final StringBuilder MENU_SELECTION_MANAGER_KEY = new StringBuilder("javax.swing.MenuSelectionManager");
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();

   public static MenuSelectionManager defaultManager() {
      synchronized(MENU_SELECTION_MANAGER_KEY) {
         AppContext var1 = AppContext.getAppContext();
         MenuSelectionManager var2 = (MenuSelectionManager)var1.get(MENU_SELECTION_MANAGER_KEY);
         if (var2 == null) {
            var2 = new MenuSelectionManager();
            var1.put(MENU_SELECTION_MANAGER_KEY, var2);
            Object var3 = var1.get(SwingUtilities2.MENU_SELECTION_MANAGER_LISTENER_KEY);
            if (var3 != null && var3 instanceof ChangeListener) {
               var2.addChangeListener((ChangeListener)var3);
            }
         }

         return var2;
      }
   }

   public void setSelectedPath(MenuElement[] var1) {
      int var4 = this.selection.size();
      int var5 = 0;
      if (var1 == null) {
         var1 = new MenuElement[0];
      }

      int var2 = 0;

      int var3;
      for(var3 = var1.length; var2 < var3 && var2 < var4 && this.selection.elementAt(var2) == var1[var2]; ++var2) {
         ++var5;
      }

      for(var2 = var4 - 1; var2 >= var5; --var2) {
         MenuElement var6 = (MenuElement)this.selection.elementAt(var2);
         this.selection.removeElementAt(var2);
         var6.menuSelectionChanged(false);
      }

      var2 = var5;

      for(var3 = var1.length; var2 < var3; ++var2) {
         if (var1[var2] != null) {
            this.selection.addElement(var1[var2]);
            var1[var2].menuSelectionChanged(true);
         }
      }

      this.fireStateChanged();
   }

   public MenuElement[] getSelectedPath() {
      MenuElement[] var1 = new MenuElement[this.selection.size()];
      int var2 = 0;

      for(int var3 = this.selection.size(); var2 < var3; ++var2) {
         var1[var2] = (MenuElement)this.selection.elementAt(var2);
      }

      return var1;
   }

   public void clearSelectedPath() {
      if (this.selection.size() > 0) {
         this.setSelectedPath((MenuElement[])null);
      }

   }

   public void addChangeListener(ChangeListener var1) {
      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public void processMouseEvent(MouseEvent var1) {
      Point var4 = var1.getPoint();
      Component var18 = var1.getComponent();
      if (var18 == null || var18.isShowing()) {
         int var19 = var1.getID();
         int var20 = var1.getModifiers();
         if (var19 != 504 && var19 != 505 || (var20 & 28) == 0) {
            if (var18 != null) {
               SwingUtilities.convertPointToScreen(var4, var18);
            }

            int var2 = var4.x;
            int var3 = var4.y;
            Vector var16 = (Vector)this.selection.clone();
            int var17 = var16.size();
            boolean var21 = false;

            for(int var5 = var17 - 1; var5 >= 0 && !var21; --var5) {
               MenuElement var13 = (MenuElement)var16.elementAt(var5);
               MenuElement[] var14 = var13.getSubElements();
               MenuElement[] var15 = null;
               int var7 = 0;

               for(int var8 = var14.length; var7 < var8 && !var21; ++var7) {
                  if (var14[var7] != null) {
                     Component var9 = var14[var7].getComponent();
                     if (var9.isShowing()) {
                        int var11;
                        int var12;
                        if (var9 instanceof JComponent) {
                           var11 = var9.getWidth();
                           var12 = var9.getHeight();
                        } else {
                           Rectangle var10 = var9.getBounds();
                           var11 = var10.width;
                           var12 = var10.height;
                        }

                        var4.x = var2;
                        var4.y = var3;
                        SwingUtilities.convertPointFromScreen(var4, var9);
                        if (var4.x >= 0 && var4.x < var11 && var4.y >= 0 && var4.y < var12) {
                           if (var15 == null) {
                              var15 = new MenuElement[var5 + 2];

                              for(int var22 = 0; var22 <= var5; ++var22) {
                                 var15[var22] = (MenuElement)var16.elementAt(var22);
                              }
                           }

                           var15[var5 + 1] = var14[var7];
                           MenuElement[] var23 = this.getSelectedPath();
                           if (var23[var23.length - 1] != var15[var5 + 1] && (var23.length < 2 || var23[var23.length - 2] != var15[var5 + 1])) {
                              Component var24 = var23[var23.length - 1].getComponent();
                              MouseEvent var25 = new MouseEvent(var24, 505, var1.getWhen(), var1.getModifiers(), var4.x, var4.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
                              AWTAccessor.MouseEventAccessor var26 = AWTAccessor.getMouseEventAccessor();
                              var26.setCausedByTouchEvent(var25, var26.isCausedByTouchEvent(var1));
                              var23[var23.length - 1].processMouseEvent(var25, var15, this);
                              MouseEvent var27 = new MouseEvent(var9, 504, var1.getWhen(), var1.getModifiers(), var4.x, var4.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
                              var26.setCausedByTouchEvent(var27, var26.isCausedByTouchEvent(var1));
                              var14[var7].processMouseEvent(var27, var15, this);
                           }

                           MouseEvent var28 = new MouseEvent(var9, var1.getID(), var1.getWhen(), var1.getModifiers(), var4.x, var4.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
                           AWTAccessor.MouseEventAccessor var29 = AWTAccessor.getMouseEventAccessor();
                           var29.setCausedByTouchEvent(var28, var29.isCausedByTouchEvent(var1));
                           var14[var7].processMouseEvent(var28, var15, this);
                           var21 = true;
                           var1.consume();
                        }
                     }
                  }
               }
            }

         }
      }
   }

   private void printMenuElementArray(MenuElement[] var1) {
      this.printMenuElementArray(var1, false);
   }

   private void printMenuElementArray(MenuElement[] var1, boolean var2) {
      System.out.println("Path is(");
      int var3 = 0;

      for(int var4 = var1.length; var3 < var4; ++var3) {
         for(int var5 = 0; var5 <= var3; ++var5) {
            System.out.print("  ");
         }

         MenuElement var6 = var1[var3];
         if (var6 instanceof JMenuItem) {
            System.out.println(((JMenuItem)var6).getText() + ", ");
         } else if (var6 instanceof JMenuBar) {
            System.out.println("JMenuBar, ");
         } else if (var6 instanceof JPopupMenu) {
            System.out.println("JPopupMenu, ");
         } else if (var6 == null) {
            System.out.println("NULL , ");
         } else {
            System.out.println("" + var6 + ", ");
         }
      }

      System.out.println(")");
      if (var2) {
         Thread.dumpStack();
      }

   }

   public Component componentForPoint(Component var1, Point var2) {
      Point var5 = var2;
      SwingUtilities.convertPointToScreen(var2, var1);
      int var3 = var2.x;
      int var4 = var2.y;
      Vector var16 = (Vector)this.selection.clone();
      int var17 = var16.size();

      for(int var6 = var17 - 1; var6 >= 0; --var6) {
         MenuElement var14 = (MenuElement)var16.elementAt(var6);
         MenuElement[] var15 = var14.getSubElements();
         int var8 = 0;

         for(int var9 = var15.length; var8 < var9; ++var8) {
            if (var15[var8] != null) {
               Component var10 = var15[var8].getComponent();
               if (var10.isShowing()) {
                  int var12;
                  int var13;
                  if (var10 instanceof JComponent) {
                     var12 = var10.getWidth();
                     var13 = var10.getHeight();
                  } else {
                     Rectangle var11 = var10.getBounds();
                     var12 = var11.width;
                     var13 = var11.height;
                  }

                  var5.x = var3;
                  var5.y = var4;
                  SwingUtilities.convertPointFromScreen(var5, var10);
                  if (var5.x >= 0 && var5.x < var12 && var5.y >= 0 && var5.y < var13) {
                     return var10;
                  }
               }
            }
         }
      }

      return null;
   }

   public void processKeyEvent(KeyEvent var1) {
      MenuElement[] var2 = new MenuElement[0];
      var2 = (MenuElement[])this.selection.toArray(var2);
      int var3 = var2.length;
      if (var3 >= 1) {
         MenuElement[] var4;
         for(int var5 = var3 - 1; var5 >= 0; --var5) {
            MenuElement var6 = var2[var5];
            MenuElement[] var7 = var6.getSubElements();
            var4 = null;

            for(int var8 = 0; var8 < var7.length; ++var8) {
               if (var7[var8] != null && var7[var8].getComponent().isShowing() && var7[var8].getComponent().isEnabled()) {
                  if (var4 == null) {
                     var4 = new MenuElement[var5 + 2];
                     System.arraycopy(var2, 0, var4, 0, var5 + 1);
                  }

                  var4[var5 + 1] = var7[var8];
                  var7[var8].processKeyEvent(var1, var4, this);
                  if (var1.isConsumed()) {
                     return;
                  }
               }
            }
         }

         var4 = new MenuElement[]{var2[0]};
         var4[0].processKeyEvent(var1, var4, this);
         if (!var1.isConsumed()) {
            ;
         }
      }
   }

   public boolean isComponentPartOfCurrentMenu(Component var1) {
      if (this.selection.size() > 0) {
         MenuElement var2 = (MenuElement)this.selection.elementAt(0);
         return this.isComponentPartOfCurrentMenu(var2, var1);
      } else {
         return false;
      }
   }

   private boolean isComponentPartOfCurrentMenu(MenuElement var1, Component var2) {
      if (var1 == null) {
         return false;
      } else if (var1.getComponent() == var2) {
         return true;
      } else {
         MenuElement[] var3 = var1.getSubElements();
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            if (this.isComponentPartOfCurrentMenu(var3[var4], var2)) {
               return true;
            }
         }

         return false;
      }
   }
}
