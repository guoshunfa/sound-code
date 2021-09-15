package javax.swing;

import java.applet.Applet;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.awt.EmbeddedFrame;

class KeyboardManager {
   static KeyboardManager currentManager = new KeyboardManager();
   Hashtable<Container, Hashtable> containerMap = new Hashtable();
   Hashtable<KeyboardManager.ComponentKeyStrokePair, Container> componentKeyStrokeMap = new Hashtable();

   public static KeyboardManager getCurrentManager() {
      return currentManager;
   }

   public static void setCurrentManager(KeyboardManager var0) {
      currentManager = var0;
   }

   public void registerKeyStroke(KeyStroke var1, JComponent var2) {
      Container var3 = getTopAncestor(var2);
      if (var3 != null) {
         Hashtable var4 = (Hashtable)this.containerMap.get(var3);
         if (var4 == null) {
            var4 = this.registerNewTopContainer(var3);
         }

         Object var5 = var4.get(var1);
         if (var5 == null) {
            var4.put(var1, var2);
         } else {
            Vector var6;
            if (var5 instanceof Vector) {
               var6 = (Vector)var5;
               if (!var6.contains(var2)) {
                  var6.addElement(var2);
               }
            } else if (var5 instanceof JComponent) {
               if (var5 != var2) {
                  var6 = new Vector();
                  var6.addElement((JComponent)var5);
                  var6.addElement(var2);
                  var4.put(var1, var6);
               }
            } else {
               System.out.println("Unexpected condition in registerKeyStroke");
               Thread.dumpStack();
            }
         }

         this.componentKeyStrokeMap.put(new KeyboardManager.ComponentKeyStrokePair(var2, var1), var3);
         if (var3 instanceof EmbeddedFrame) {
            ((EmbeddedFrame)var3).registerAccelerator(var1);
         }

      }
   }

   private static Container getTopAncestor(JComponent var0) {
      for(Container var1 = var0.getParent(); var1 != null; var1 = var1.getParent()) {
         if (var1 instanceof Window && ((Window)var1).isFocusableWindow() || var1 instanceof Applet || var1 instanceof JInternalFrame) {
            return var1;
         }
      }

      return null;
   }

   public void unregisterKeyStroke(KeyStroke var1, JComponent var2) {
      KeyboardManager.ComponentKeyStrokePair var3 = new KeyboardManager.ComponentKeyStrokePair(var2, var1);
      Container var4 = (Container)this.componentKeyStrokeMap.get(var3);
      if (var4 != null) {
         Hashtable var5 = (Hashtable)this.containerMap.get(var4);
         if (var5 == null) {
            Thread.dumpStack();
         } else {
            Object var6 = var5.get(var1);
            if (var6 == null) {
               Thread.dumpStack();
            } else {
               if (var6 instanceof JComponent && var6 == var2) {
                  var5.remove(var1);
               } else if (var6 instanceof Vector) {
                  Vector var7 = (Vector)var6;
                  var7.removeElement(var2);
                  if (var7.isEmpty()) {
                     var5.remove(var1);
                  }
               }

               if (var5.isEmpty()) {
                  this.containerMap.remove(var4);
               }

               this.componentKeyStrokeMap.remove(var3);
               if (var4 instanceof EmbeddedFrame) {
                  ((EmbeddedFrame)var4).unregisterAccelerator(var1);
               }

            }
         }
      }
   }

   public boolean fireKeyboardAction(KeyEvent var1, boolean var2, Container var3) {
      if (var1.isConsumed()) {
         System.out.println("Acquired pre-used event!");
         Thread.dumpStack();
      }

      KeyStroke var5 = null;
      KeyStroke var4;
      if (var1.getID() == 400) {
         var4 = KeyStroke.getKeyStroke(var1.getKeyChar());
      } else {
         if (var1.getKeyCode() != var1.getExtendedKeyCode()) {
            var5 = KeyStroke.getKeyStroke(var1.getExtendedKeyCode(), var1.getModifiers(), !var2);
         }

         var4 = KeyStroke.getKeyStroke(var1.getKeyCode(), var1.getModifiers(), !var2);
      }

      Hashtable var6 = (Hashtable)this.containerMap.get(var3);
      if (var6 != null) {
         Object var7 = null;
         if (var5 != null) {
            var7 = var6.get(var5);
            if (var7 != null) {
               var4 = var5;
            }
         }

         if (var7 == null) {
            var7 = var6.get(var4);
         }

         if (var7 != null) {
            if (var7 instanceof JComponent) {
               JComponent var8 = (JComponent)var7;
               if (var8.isShowing() && var8.isEnabled()) {
                  this.fireBinding(var8, var4, var1, var2);
               }
            } else if (var7 instanceof Vector) {
               Vector var11 = (Vector)var7;

               for(int var9 = var11.size() - 1; var9 >= 0; --var9) {
                  JComponent var10 = (JComponent)var11.elementAt(var9);
                  if (var10.isShowing() && var10.isEnabled()) {
                     this.fireBinding(var10, var4, var1, var2);
                     if (var1.isConsumed()) {
                        return true;
                     }
                  }
               }
            } else {
               System.out.println("Unexpected condition in fireKeyboardAction " + var7);
               Thread.dumpStack();
            }
         }
      }

      if (var1.isConsumed()) {
         return true;
      } else {
         if (var6 != null) {
            Vector var12 = (Vector)var6.get(JMenuBar.class);
            if (var12 != null) {
               Enumeration var13 = var12.elements();

               do {
                  JMenuBar var14;
                  do {
                     do {
                        if (!var13.hasMoreElements()) {
                           return var1.isConsumed();
                        }

                        var14 = (JMenuBar)var13.nextElement();
                     } while(!var14.isShowing());
                  } while(!var14.isEnabled());

                  boolean var15 = var5 != null && !var5.equals(var4);
                  if (var15) {
                     this.fireBinding(var14, var5, var1, var2);
                  }

                  if (!var15 || !var1.isConsumed()) {
                     this.fireBinding(var14, var4, var1, var2);
                  }
               } while(!var1.isConsumed());

               return true;
            }
         }

         return var1.isConsumed();
      }
   }

   void fireBinding(JComponent var1, KeyStroke var2, KeyEvent var3, boolean var4) {
      if (var1.processKeyBinding(var2, var3, 2, var4)) {
         var3.consume();
      }

   }

   public void registerMenuBar(JMenuBar var1) {
      Container var2 = getTopAncestor(var1);
      if (var2 != null) {
         Hashtable var3 = (Hashtable)this.containerMap.get(var2);
         if (var3 == null) {
            var3 = this.registerNewTopContainer(var2);
         }

         Vector var4 = (Vector)var3.get(JMenuBar.class);
         if (var4 == null) {
            var4 = new Vector();
            var3.put(JMenuBar.class, var4);
         }

         if (!var4.contains(var1)) {
            var4.addElement(var1);
         }

      }
   }

   public void unregisterMenuBar(JMenuBar var1) {
      Container var2 = getTopAncestor(var1);
      if (var2 != null) {
         Hashtable var3 = (Hashtable)this.containerMap.get(var2);
         if (var3 != null) {
            Vector var4 = (Vector)var3.get(JMenuBar.class);
            if (var4 != null) {
               var4.removeElement(var1);
               if (var4.isEmpty()) {
                  var3.remove(JMenuBar.class);
                  if (var3.isEmpty()) {
                     this.containerMap.remove(var2);
                  }
               }
            }
         }

      }
   }

   protected Hashtable registerNewTopContainer(Container var1) {
      Hashtable var2 = new Hashtable();
      this.containerMap.put(var1, var2);
      return var2;
   }

   class ComponentKeyStrokePair {
      Object component;
      Object keyStroke;

      public ComponentKeyStrokePair(Object var2, Object var3) {
         this.component = var2;
         this.keyStroke = var3;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof KeyboardManager.ComponentKeyStrokePair)) {
            return false;
         } else {
            KeyboardManager.ComponentKeyStrokePair var2 = (KeyboardManager.ComponentKeyStrokePair)var1;
            return this.component.equals(var2.component) && this.keyStroke.equals(var2.keyStroke);
         }
      }

      public int hashCode() {
         return this.component.hashCode() * this.keyStroke.hashCode();
      }
   }
}
