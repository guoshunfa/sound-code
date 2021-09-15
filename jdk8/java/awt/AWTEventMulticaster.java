package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;

public class AWTEventMulticaster implements ComponentListener, ContainerListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, WindowListener, WindowFocusListener, WindowStateListener, ActionListener, ItemListener, AdjustmentListener, TextListener, InputMethodListener, HierarchyListener, HierarchyBoundsListener, MouseWheelListener {
   protected final EventListener a;
   protected final EventListener b;

   protected AWTEventMulticaster(EventListener var1, EventListener var2) {
      this.a = var1;
      this.b = var2;
   }

   protected EventListener remove(EventListener var1) {
      if (var1 == this.a) {
         return this.b;
      } else if (var1 == this.b) {
         return this.a;
      } else {
         EventListener var2 = removeInternal(this.a, var1);
         EventListener var3 = removeInternal(this.b, var1);
         return (EventListener)(var2 == this.a && var3 == this.b ? this : addInternal(var2, var3));
      }
   }

   public void componentResized(ComponentEvent var1) {
      ((ComponentListener)this.a).componentResized(var1);
      ((ComponentListener)this.b).componentResized(var1);
   }

   public void componentMoved(ComponentEvent var1) {
      ((ComponentListener)this.a).componentMoved(var1);
      ((ComponentListener)this.b).componentMoved(var1);
   }

   public void componentShown(ComponentEvent var1) {
      ((ComponentListener)this.a).componentShown(var1);
      ((ComponentListener)this.b).componentShown(var1);
   }

   public void componentHidden(ComponentEvent var1) {
      ((ComponentListener)this.a).componentHidden(var1);
      ((ComponentListener)this.b).componentHidden(var1);
   }

   public void componentAdded(ContainerEvent var1) {
      ((ContainerListener)this.a).componentAdded(var1);
      ((ContainerListener)this.b).componentAdded(var1);
   }

   public void componentRemoved(ContainerEvent var1) {
      ((ContainerListener)this.a).componentRemoved(var1);
      ((ContainerListener)this.b).componentRemoved(var1);
   }

   public void focusGained(FocusEvent var1) {
      ((FocusListener)this.a).focusGained(var1);
      ((FocusListener)this.b).focusGained(var1);
   }

   public void focusLost(FocusEvent var1) {
      ((FocusListener)this.a).focusLost(var1);
      ((FocusListener)this.b).focusLost(var1);
   }

   public void keyTyped(KeyEvent var1) {
      ((KeyListener)this.a).keyTyped(var1);
      ((KeyListener)this.b).keyTyped(var1);
   }

   public void keyPressed(KeyEvent var1) {
      ((KeyListener)this.a).keyPressed(var1);
      ((KeyListener)this.b).keyPressed(var1);
   }

   public void keyReleased(KeyEvent var1) {
      ((KeyListener)this.a).keyReleased(var1);
      ((KeyListener)this.b).keyReleased(var1);
   }

   public void mouseClicked(MouseEvent var1) {
      ((MouseListener)this.a).mouseClicked(var1);
      ((MouseListener)this.b).mouseClicked(var1);
   }

   public void mousePressed(MouseEvent var1) {
      ((MouseListener)this.a).mousePressed(var1);
      ((MouseListener)this.b).mousePressed(var1);
   }

   public void mouseReleased(MouseEvent var1) {
      ((MouseListener)this.a).mouseReleased(var1);
      ((MouseListener)this.b).mouseReleased(var1);
   }

   public void mouseEntered(MouseEvent var1) {
      ((MouseListener)this.a).mouseEntered(var1);
      ((MouseListener)this.b).mouseEntered(var1);
   }

   public void mouseExited(MouseEvent var1) {
      ((MouseListener)this.a).mouseExited(var1);
      ((MouseListener)this.b).mouseExited(var1);
   }

   public void mouseDragged(MouseEvent var1) {
      ((MouseMotionListener)this.a).mouseDragged(var1);
      ((MouseMotionListener)this.b).mouseDragged(var1);
   }

   public void mouseMoved(MouseEvent var1) {
      ((MouseMotionListener)this.a).mouseMoved(var1);
      ((MouseMotionListener)this.b).mouseMoved(var1);
   }

   public void windowOpened(WindowEvent var1) {
      ((WindowListener)this.a).windowOpened(var1);
      ((WindowListener)this.b).windowOpened(var1);
   }

   public void windowClosing(WindowEvent var1) {
      ((WindowListener)this.a).windowClosing(var1);
      ((WindowListener)this.b).windowClosing(var1);
   }

   public void windowClosed(WindowEvent var1) {
      ((WindowListener)this.a).windowClosed(var1);
      ((WindowListener)this.b).windowClosed(var1);
   }

   public void windowIconified(WindowEvent var1) {
      ((WindowListener)this.a).windowIconified(var1);
      ((WindowListener)this.b).windowIconified(var1);
   }

   public void windowDeiconified(WindowEvent var1) {
      ((WindowListener)this.a).windowDeiconified(var1);
      ((WindowListener)this.b).windowDeiconified(var1);
   }

   public void windowActivated(WindowEvent var1) {
      ((WindowListener)this.a).windowActivated(var1);
      ((WindowListener)this.b).windowActivated(var1);
   }

   public void windowDeactivated(WindowEvent var1) {
      ((WindowListener)this.a).windowDeactivated(var1);
      ((WindowListener)this.b).windowDeactivated(var1);
   }

   public void windowStateChanged(WindowEvent var1) {
      ((WindowStateListener)this.a).windowStateChanged(var1);
      ((WindowStateListener)this.b).windowStateChanged(var1);
   }

   public void windowGainedFocus(WindowEvent var1) {
      ((WindowFocusListener)this.a).windowGainedFocus(var1);
      ((WindowFocusListener)this.b).windowGainedFocus(var1);
   }

   public void windowLostFocus(WindowEvent var1) {
      ((WindowFocusListener)this.a).windowLostFocus(var1);
      ((WindowFocusListener)this.b).windowLostFocus(var1);
   }

   public void actionPerformed(ActionEvent var1) {
      ((ActionListener)this.a).actionPerformed(var1);
      ((ActionListener)this.b).actionPerformed(var1);
   }

   public void itemStateChanged(ItemEvent var1) {
      ((ItemListener)this.a).itemStateChanged(var1);
      ((ItemListener)this.b).itemStateChanged(var1);
   }

   public void adjustmentValueChanged(AdjustmentEvent var1) {
      ((AdjustmentListener)this.a).adjustmentValueChanged(var1);
      ((AdjustmentListener)this.b).adjustmentValueChanged(var1);
   }

   public void textValueChanged(TextEvent var1) {
      ((TextListener)this.a).textValueChanged(var1);
      ((TextListener)this.b).textValueChanged(var1);
   }

   public void inputMethodTextChanged(InputMethodEvent var1) {
      ((InputMethodListener)this.a).inputMethodTextChanged(var1);
      ((InputMethodListener)this.b).inputMethodTextChanged(var1);
   }

   public void caretPositionChanged(InputMethodEvent var1) {
      ((InputMethodListener)this.a).caretPositionChanged(var1);
      ((InputMethodListener)this.b).caretPositionChanged(var1);
   }

   public void hierarchyChanged(HierarchyEvent var1) {
      ((HierarchyListener)this.a).hierarchyChanged(var1);
      ((HierarchyListener)this.b).hierarchyChanged(var1);
   }

   public void ancestorMoved(HierarchyEvent var1) {
      ((HierarchyBoundsListener)this.a).ancestorMoved(var1);
      ((HierarchyBoundsListener)this.b).ancestorMoved(var1);
   }

   public void ancestorResized(HierarchyEvent var1) {
      ((HierarchyBoundsListener)this.a).ancestorResized(var1);
      ((HierarchyBoundsListener)this.b).ancestorResized(var1);
   }

   public void mouseWheelMoved(MouseWheelEvent var1) {
      ((MouseWheelListener)this.a).mouseWheelMoved(var1);
      ((MouseWheelListener)this.b).mouseWheelMoved(var1);
   }

   public static ComponentListener add(ComponentListener var0, ComponentListener var1) {
      return (ComponentListener)addInternal(var0, var1);
   }

   public static ContainerListener add(ContainerListener var0, ContainerListener var1) {
      return (ContainerListener)addInternal(var0, var1);
   }

   public static FocusListener add(FocusListener var0, FocusListener var1) {
      return (FocusListener)addInternal(var0, var1);
   }

   public static KeyListener add(KeyListener var0, KeyListener var1) {
      return (KeyListener)addInternal(var0, var1);
   }

   public static MouseListener add(MouseListener var0, MouseListener var1) {
      return (MouseListener)addInternal(var0, var1);
   }

   public static MouseMotionListener add(MouseMotionListener var0, MouseMotionListener var1) {
      return (MouseMotionListener)addInternal(var0, var1);
   }

   public static WindowListener add(WindowListener var0, WindowListener var1) {
      return (WindowListener)addInternal(var0, var1);
   }

   public static WindowStateListener add(WindowStateListener var0, WindowStateListener var1) {
      return (WindowStateListener)addInternal(var0, var1);
   }

   public static WindowFocusListener add(WindowFocusListener var0, WindowFocusListener var1) {
      return (WindowFocusListener)addInternal(var0, var1);
   }

   public static ActionListener add(ActionListener var0, ActionListener var1) {
      return (ActionListener)addInternal(var0, var1);
   }

   public static ItemListener add(ItemListener var0, ItemListener var1) {
      return (ItemListener)addInternal(var0, var1);
   }

   public static AdjustmentListener add(AdjustmentListener var0, AdjustmentListener var1) {
      return (AdjustmentListener)addInternal(var0, var1);
   }

   public static TextListener add(TextListener var0, TextListener var1) {
      return (TextListener)addInternal(var0, var1);
   }

   public static InputMethodListener add(InputMethodListener var0, InputMethodListener var1) {
      return (InputMethodListener)addInternal(var0, var1);
   }

   public static HierarchyListener add(HierarchyListener var0, HierarchyListener var1) {
      return (HierarchyListener)addInternal(var0, var1);
   }

   public static HierarchyBoundsListener add(HierarchyBoundsListener var0, HierarchyBoundsListener var1) {
      return (HierarchyBoundsListener)addInternal(var0, var1);
   }

   public static MouseWheelListener add(MouseWheelListener var0, MouseWheelListener var1) {
      return (MouseWheelListener)addInternal(var0, var1);
   }

   public static ComponentListener remove(ComponentListener var0, ComponentListener var1) {
      return (ComponentListener)removeInternal(var0, var1);
   }

   public static ContainerListener remove(ContainerListener var0, ContainerListener var1) {
      return (ContainerListener)removeInternal(var0, var1);
   }

   public static FocusListener remove(FocusListener var0, FocusListener var1) {
      return (FocusListener)removeInternal(var0, var1);
   }

   public static KeyListener remove(KeyListener var0, KeyListener var1) {
      return (KeyListener)removeInternal(var0, var1);
   }

   public static MouseListener remove(MouseListener var0, MouseListener var1) {
      return (MouseListener)removeInternal(var0, var1);
   }

   public static MouseMotionListener remove(MouseMotionListener var0, MouseMotionListener var1) {
      return (MouseMotionListener)removeInternal(var0, var1);
   }

   public static WindowListener remove(WindowListener var0, WindowListener var1) {
      return (WindowListener)removeInternal(var0, var1);
   }

   public static WindowStateListener remove(WindowStateListener var0, WindowStateListener var1) {
      return (WindowStateListener)removeInternal(var0, var1);
   }

   public static WindowFocusListener remove(WindowFocusListener var0, WindowFocusListener var1) {
      return (WindowFocusListener)removeInternal(var0, var1);
   }

   public static ActionListener remove(ActionListener var0, ActionListener var1) {
      return (ActionListener)removeInternal(var0, var1);
   }

   public static ItemListener remove(ItemListener var0, ItemListener var1) {
      return (ItemListener)removeInternal(var0, var1);
   }

   public static AdjustmentListener remove(AdjustmentListener var0, AdjustmentListener var1) {
      return (AdjustmentListener)removeInternal(var0, var1);
   }

   public static TextListener remove(TextListener var0, TextListener var1) {
      return (TextListener)removeInternal(var0, var1);
   }

   public static InputMethodListener remove(InputMethodListener var0, InputMethodListener var1) {
      return (InputMethodListener)removeInternal(var0, var1);
   }

   public static HierarchyListener remove(HierarchyListener var0, HierarchyListener var1) {
      return (HierarchyListener)removeInternal(var0, var1);
   }

   public static HierarchyBoundsListener remove(HierarchyBoundsListener var0, HierarchyBoundsListener var1) {
      return (HierarchyBoundsListener)removeInternal(var0, var1);
   }

   public static MouseWheelListener remove(MouseWheelListener var0, MouseWheelListener var1) {
      return (MouseWheelListener)removeInternal(var0, var1);
   }

   protected static EventListener addInternal(EventListener var0, EventListener var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (EventListener)(var1 == null ? var0 : new AWTEventMulticaster(var0, var1));
      }
   }

   protected static EventListener removeInternal(EventListener var0, EventListener var1) {
      if (var0 != var1 && var0 != null) {
         return var0 instanceof AWTEventMulticaster ? ((AWTEventMulticaster)var0).remove(var1) : var0;
      } else {
         return null;
      }
   }

   protected void saveInternal(ObjectOutputStream var1, String var2) throws IOException {
      if (this.a instanceof AWTEventMulticaster) {
         ((AWTEventMulticaster)this.a).saveInternal(var1, var2);
      } else if (this.a instanceof Serializable) {
         var1.writeObject(var2);
         var1.writeObject(this.a);
      }

      if (this.b instanceof AWTEventMulticaster) {
         ((AWTEventMulticaster)this.b).saveInternal(var1, var2);
      } else if (this.b instanceof Serializable) {
         var1.writeObject(var2);
         var1.writeObject(this.b);
      }

   }

   protected static void save(ObjectOutputStream var0, String var1, EventListener var2) throws IOException {
      if (var2 != null) {
         if (var2 instanceof AWTEventMulticaster) {
            ((AWTEventMulticaster)var2).saveInternal(var0, var1);
         } else if (var2 instanceof Serializable) {
            var0.writeObject(var1);
            var0.writeObject(var2);
         }

      }
   }

   private static int getListenerCount(EventListener var0, Class<?> var1) {
      if (var0 instanceof AWTEventMulticaster) {
         AWTEventMulticaster var2 = (AWTEventMulticaster)var0;
         return getListenerCount(var2.a, var1) + getListenerCount(var2.b, var1);
      } else {
         return var1.isInstance(var0) ? 1 : 0;
      }
   }

   private static int populateListenerArray(EventListener[] var0, EventListener var1, int var2) {
      if (var1 instanceof AWTEventMulticaster) {
         AWTEventMulticaster var3 = (AWTEventMulticaster)var1;
         int var4 = populateListenerArray(var0, var3.a, var2);
         return populateListenerArray(var0, var3.b, var4);
      } else if (var0.getClass().getComponentType().isInstance(var1)) {
         var0[var2] = var1;
         return var2 + 1;
      } else {
         return var2;
      }
   }

   public static <T extends EventListener> T[] getListeners(EventListener var0, Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("Listener type should not be null");
      } else {
         int var2 = getListenerCount(var0, var1);
         EventListener[] var3 = (EventListener[])((EventListener[])Array.newInstance(var1, var2));
         populateListenerArray(var3, var0, 0);
         return var3;
      }
   }
}
