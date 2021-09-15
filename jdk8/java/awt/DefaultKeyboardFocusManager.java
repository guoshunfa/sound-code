package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.SunToolkit;
import sun.awt.TimedWindowEvent;
import sun.util.logging.PlatformLogger;

public class DefaultKeyboardFocusManager extends KeyboardFocusManager {
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.DefaultKeyboardFocusManager");
   private static final WeakReference<Window> NULL_WINDOW_WR = new WeakReference((Object)null);
   private static final WeakReference<Component> NULL_COMPONENT_WR = new WeakReference((Object)null);
   private WeakReference<Window> realOppositeWindowWR;
   private WeakReference<Component> realOppositeComponentWR;
   private int inSendMessage;
   private LinkedList<KeyEvent> enqueuedKeyEvents;
   private LinkedList<DefaultKeyboardFocusManager.TypeAheadMarker> typeAheadMarkers;
   private boolean consumeNextKeyTyped;
   private Component restoreFocusTo;

   public DefaultKeyboardFocusManager() {
      this.realOppositeWindowWR = NULL_WINDOW_WR;
      this.realOppositeComponentWR = NULL_COMPONENT_WR;
      this.enqueuedKeyEvents = new LinkedList();
      this.typeAheadMarkers = new LinkedList();
   }

   private Window getOwningFrameDialog(Window var1) {
      while(var1 != null && !(var1 instanceof Frame) && !(var1 instanceof Dialog)) {
         var1 = (Window)var1.getParent();
      }

      return var1;
   }

   private void restoreFocus(FocusEvent var1, Window var2) {
      Component var3 = (Component)this.realOppositeComponentWR.get();
      Component var4 = var1.getComponent();
      if ((var2 == null || !this.restoreFocus(var2, var4, false)) && (var3 == null || !this.doRestoreFocus(var3, var4, false)) && (var1.getOppositeComponent() == null || !this.doRestoreFocus(var1.getOppositeComponent(), var4, false))) {
         this.clearGlobalFocusOwnerPriv();
      }

   }

   private void restoreFocus(WindowEvent var1) {
      Window var2 = (Window)this.realOppositeWindowWR.get();
      if ((var2 == null || !this.restoreFocus(var2, (Component)null, false)) && (var1.getOppositeWindow() == null || !this.restoreFocus(var1.getOppositeWindow(), (Component)null, false))) {
         this.clearGlobalFocusOwnerPriv();
      }

   }

   private boolean restoreFocus(Window var1, Component var2, boolean var3) {
      this.restoreFocusTo = null;
      Component var4 = KeyboardFocusManager.getMostRecentFocusOwner(var1);
      if (var4 != null && var4 != var2) {
         if (getHeavyweight(var1) != this.getNativeFocusOwner()) {
            if (!var4.isShowing() || !var4.canBeFocusOwner()) {
               var4 = var4.getNextFocusCandidate();
            }

            if (var4 != null && var4 != var2) {
               if (!var4.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK)) {
                  this.restoreFocusTo = var4;
               }

               return true;
            }
         } else if (this.doRestoreFocus(var4, var2, false)) {
            return true;
         }
      }

      if (var3) {
         this.clearGlobalFocusOwnerPriv();
         return true;
      } else {
         return false;
      }
   }

   private boolean restoreFocus(Component var1, boolean var2) {
      return this.doRestoreFocus(var1, (Component)null, var2);
   }

   private boolean doRestoreFocus(Component var1, Component var2, boolean var3) {
      boolean var4 = true;
      if (var1 != var2 && var1.isShowing() && var1.canBeFocusOwner() && (var4 = var1.requestFocus(false, CausedFocusEvent.Cause.ROLLBACK))) {
         return true;
      } else if (!var4 && this.getGlobalFocusedWindow() != SunToolkit.getContainingWindow(var1)) {
         this.restoreFocusTo = var1;
         return true;
      } else {
         Component var5 = var1.getNextFocusCandidate();
         if (var5 != null && var5 != var2 && var5.requestFocusInWindow(CausedFocusEvent.Cause.ROLLBACK)) {
            return true;
         } else if (var3) {
            this.clearGlobalFocusOwnerPriv();
            return true;
         } else {
            return false;
         }
      }
   }

   static boolean sendMessage(Component var0, AWTEvent var1) {
      var1.isPosted = true;
      AppContext var2 = AppContext.getAppContext();
      final AppContext var3 = var0.appContext;
      final DefaultKeyboardFocusManager.DefaultKeyboardFocusManagerSentEvent var4 = new DefaultKeyboardFocusManager.DefaultKeyboardFocusManagerSentEvent(var1, var2);
      if (var2 == var3) {
         var4.dispatch();
      } else {
         if (var3.isDisposed()) {
            return false;
         }

         SunToolkit.postEvent(var3, var4);
         if (EventQueue.isDispatchThread()) {
            EventDispatchThread var5 = (EventDispatchThread)Thread.currentThread();
            var5.pumpEvents(1007, new Conditional() {
               public boolean evaluate() {
                  return !var4.dispatched && !var3.isDisposed();
               }
            });
         } else {
            synchronized(var4) {
               while(!var4.dispatched && !var3.isDisposed()) {
                  try {
                     var4.wait(1000L);
                  } catch (InterruptedException var8) {
                     break;
                  }
               }
            }
         }
      }

      return var4.dispatched;
   }

   private boolean repostIfFollowsKeyEvents(WindowEvent var1) {
      if (!(var1 instanceof TimedWindowEvent)) {
         return false;
      } else {
         TimedWindowEvent var2 = (TimedWindowEvent)var1;
         long var3 = var2.getWhen();
         synchronized(this) {
            KeyEvent var6 = this.enqueuedKeyEvents.isEmpty() ? null : (KeyEvent)this.enqueuedKeyEvents.getFirst();
            if (var6 != null && var3 >= var6.getWhen()) {
               DefaultKeyboardFocusManager.TypeAheadMarker var7 = this.typeAheadMarkers.isEmpty() ? null : (DefaultKeyboardFocusManager.TypeAheadMarker)this.typeAheadMarkers.getFirst();
               if (var7 != null) {
                  Window var8 = var7.untilFocused.getContainingWindow();
                  if (var8 != null && var8.isFocused()) {
                     SunToolkit.postEvent(AppContext.getAppContext(), new SequencedEvent(var1));
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public boolean dispatchEvent(AWTEvent var1) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINE) && (var1 instanceof WindowEvent || var1 instanceof FocusEvent)) {
         focusLog.fine("" + var1);
      }

      Window var4;
      Window var7;
      WindowEvent var15;
      Window var16;
      Window var18;
      boolean var19;
      Window var20;
      Component var21;
      switch(var1.getID()) {
      case 205:
         var15 = (WindowEvent)var1;
         var16 = this.getGlobalActiveWindow();
         var4 = var15.getWindow();
         if (var16 != var4) {
            if (var16 != null) {
               var19 = sendMessage(var16, new WindowEvent(var16, 206, var4));
               if (!var19) {
                  this.setGlobalActiveWindow((Window)null);
               }

               if (this.getGlobalActiveWindow() != null) {
                  return true;
               }
            }

            this.setGlobalActiveWindow(var4);
            if (var4 == this.getGlobalActiveWindow()) {
               return this.typeAheadAssertions(var4, var15);
            }
         }
         break;
      case 206:
         var15 = (WindowEvent)var1;
         var16 = this.getGlobalActiveWindow();
         if (var16 != null && var16 == var1.getSource()) {
            this.setGlobalActiveWindow((Window)null);
            if (this.getGlobalActiveWindow() == null) {
               var15.setSource(var16);
               return this.typeAheadAssertions(var16, var15);
            }
         }
         break;
      case 207:
         if (!this.repostIfFollowsKeyEvents((WindowEvent)var1)) {
            var15 = (WindowEvent)var1;
            var16 = this.getGlobalFocusedWindow();
            var4 = var15.getWindow();
            if (var4 != var16) {
               if (var4.isFocusableWindow() && var4.isVisible() && var4.isDisplayable()) {
                  if (var16 != null) {
                     var19 = sendMessage(var16, new WindowEvent(var16, 208, var4));
                     if (!var19) {
                        this.setGlobalFocusOwner((Component)null);
                        this.setGlobalFocusedWindow((Window)null);
                     }
                  }

                  var18 = this.getOwningFrameDialog(var4);
                  var20 = this.getGlobalActiveWindow();
                  if (var18 != var20) {
                     sendMessage(var18, new WindowEvent(var18, 205, var20));
                     if (var18 != this.getGlobalActiveWindow()) {
                        this.restoreFocus(var15);
                        return true;
                     }
                  }

                  this.setGlobalFocusedWindow(var4);
                  if (var4 == this.getGlobalFocusedWindow()) {
                     if (this.inSendMessage == 0) {
                        var21 = KeyboardFocusManager.getMostRecentFocusOwner(var4);
                        boolean var23 = this.restoreFocusTo != null && var21 == this.restoreFocusTo;
                        if (var21 == null && var4.isFocusableWindow()) {
                           var21 = var4.getFocusTraversalPolicy().getInitialComponent(var4);
                        }

                        Component var9 = null;
                        Class var10 = KeyboardFocusManager.class;
                        synchronized(KeyboardFocusManager.class) {
                           var9 = var4.setTemporaryLostComponent((Component)null);
                        }

                        if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                           focusLog.finer("tempLost {0}, toFocus {1}", var9, var21);
                        }

                        if (var9 != null) {
                           var9.requestFocusInWindow(var23 && var9 == var21 ? CausedFocusEvent.Cause.ROLLBACK : CausedFocusEvent.Cause.ACTIVATION);
                        }

                        if (var21 != null && var21 != var9) {
                           var21.requestFocusInWindow(CausedFocusEvent.Cause.ACTIVATION);
                        }
                     }

                     this.restoreFocusTo = null;
                     var7 = (Window)this.realOppositeWindowWR.get();
                     if (var7 != var15.getOppositeWindow()) {
                        var15 = new WindowEvent(var4, 207, var7);
                     }

                     return this.typeAheadAssertions(var4, var15);
                  }

                  this.restoreFocus(var15);
               } else {
                  this.restoreFocus(var15);
               }
            }
         }
         break;
      case 208:
         if (!this.repostIfFollowsKeyEvents((WindowEvent)var1)) {
            var15 = (WindowEvent)var1;
            var16 = this.getGlobalFocusedWindow();
            var4 = var15.getWindow();
            var18 = this.getGlobalActiveWindow();
            var20 = var15.getOppositeWindow();
            if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
               focusLog.fine("Active {0}, Current focused {1}, losing focus {2} opposite {3}", var18, var16, var4, var20);
            }

            if (var16 != null && (this.inSendMessage != 0 || var4 != var18 || var20 != var16)) {
               var21 = this.getGlobalFocusOwner();
               if (var21 != null) {
                  Object var22 = null;
                  if (var20 != null) {
                     var22 = var20.getTemporaryLostComponent();
                     if (var22 == null) {
                        var22 = var20.getMostRecentFocusOwner();
                     }
                  }

                  if (var22 == null) {
                     var22 = var20;
                  }

                  sendMessage(var21, new CausedFocusEvent(var21, 1005, true, (Component)var22, CausedFocusEvent.Cause.ACTIVATION));
               }

               this.setGlobalFocusedWindow((Window)null);
               if (this.getGlobalFocusedWindow() != null) {
                  this.restoreFocus(var16, (Component)null, true);
               } else {
                  var15.setSource(var16);
                  this.realOppositeWindowWR = var20 != null ? new WeakReference(var16) : NULL_WINDOW_WR;
                  this.typeAheadAssertions(var16, var15);
                  if (var20 == null) {
                     sendMessage(var18, new WindowEvent(var18, 206, (Window)null));
                     if (this.getGlobalActiveWindow() != null) {
                        this.restoreFocus(var16, (Component)null, true);
                     }
                  }
               }
            }
         }
         break;
      case 400:
      case 401:
      case 402:
         return this.typeAheadAssertions((Component)null, var1);
      case 1004:
         this.restoreFocusTo = null;
         Object var13 = (FocusEvent)var1;
         CausedFocusEvent.Cause var14 = var13 instanceof CausedFocusEvent ? ((CausedFocusEvent)var13).getCause() : CausedFocusEvent.Cause.UNKNOWN;
         Component var17 = this.getGlobalFocusOwner();
         Component var5 = ((FocusEvent)var13).getComponent();
         if (var17 == var5) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
               focusLog.fine("Skipping {0} because focus owner is the same", var1);
            }

            this.dequeueKeyEvents(-1L, var5);
         } else {
            if (var17 != null) {
               boolean var6 = sendMessage(var17, new CausedFocusEvent(var17, 1005, ((FocusEvent)var13).isTemporary(), var5, var14));
               if (!var6) {
                  this.setGlobalFocusOwner((Component)null);
                  if (!((FocusEvent)var13).isTemporary()) {
                     this.setGlobalPermanentFocusOwner((Component)null);
                  }
               }
            }

            var20 = SunToolkit.getContainingWindow(var5);
            var7 = this.getGlobalFocusedWindow();
            if (var20 != null && var20 != var7) {
               sendMessage(var20, new WindowEvent(var20, 207, var7));
               if (var20 != this.getGlobalFocusedWindow()) {
                  this.dequeueKeyEvents(-1L, var5);
                  break;
               }
            }

            if (!var5.isFocusable() || !var5.isShowing() || !var5.isEnabled() && !var14.equals(CausedFocusEvent.Cause.UNKNOWN)) {
               this.dequeueKeyEvents(-1L, var5);
               if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                  if (var20 == null) {
                     this.restoreFocus((FocusEvent)var13, var7);
                  } else {
                     this.restoreFocus((FocusEvent)var13, var20);
                  }

                  setMostRecentFocusOwner(var20, (Component)null);
               }
            } else {
               this.setGlobalFocusOwner(var5);
               if (var5 != this.getGlobalFocusOwner()) {
                  this.dequeueKeyEvents(-1L, var5);
                  if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                     this.restoreFocus((FocusEvent)var13, var20);
                  }
               } else {
                  if (!((FocusEvent)var13).isTemporary()) {
                     this.setGlobalPermanentFocusOwner(var5);
                     if (var5 != this.getGlobalPermanentFocusOwner()) {
                        this.dequeueKeyEvents(-1L, var5);
                        if (KeyboardFocusManager.isAutoFocusTransferEnabled()) {
                           this.restoreFocus((FocusEvent)var13, var20);
                        }
                        break;
                     }
                  }

                  this.setNativeFocusOwner(getHeavyweight(var5));
                  Component var8 = (Component)this.realOppositeComponentWR.get();
                  if (var8 != null && var8 != ((FocusEvent)var13).getOppositeComponent()) {
                     var13 = new CausedFocusEvent(var5, 1004, ((FocusEvent)var13).isTemporary(), var8, var14);
                     ((AWTEvent)var13).isPosted = true;
                  }

                  return this.typeAheadAssertions(var5, (AWTEvent)var13);
               }
            }
         }
         break;
      case 1005:
         FocusEvent var2 = (FocusEvent)var1;
         Component var3 = this.getGlobalFocusOwner();
         if (var3 == null) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
               focusLog.fine("Skipping {0} because focus owner is null", var1);
            }
         } else if (var3 == var2.getOppositeComponent()) {
            if (focusLog.isLoggable(PlatformLogger.Level.FINE)) {
               focusLog.fine("Skipping {0} because current focus owner is equal to opposite", var1);
            }
         } else {
            this.setGlobalFocusOwner((Component)null);
            if (this.getGlobalFocusOwner() != null) {
               this.restoreFocus(var3, true);
            } else {
               if (!var2.isTemporary()) {
                  this.setGlobalPermanentFocusOwner((Component)null);
                  if (this.getGlobalPermanentFocusOwner() != null) {
                     this.restoreFocus(var3, true);
                     break;
                  }
               } else {
                  var4 = var3.getContainingWindow();
                  if (var4 != null) {
                     var4.setTemporaryLostComponent(var3);
                  }
               }

               this.setNativeFocusOwner((Component)null);
               var2.setSource(var3);
               this.realOppositeComponentWR = var2.getOppositeComponent() != null ? new WeakReference(var3) : NULL_COMPONENT_WR;
               return this.typeAheadAssertions(var3, var2);
            }
         }
         break;
      default:
         return false;
      }

      return true;
   }

   public boolean dispatchKeyEvent(KeyEvent var1) {
      Component var2 = var1.isPosted ? this.getFocusOwner() : var1.getComponent();
      if (var2 != null && var2.isShowing() && var2.canBeFocusOwner() && !var1.isConsumed()) {
         Component var3 = var1.getComponent();
         if (var3 != null && var3.isEnabled()) {
            this.redispatchEvent(var3, var1);
         }
      }

      boolean var8 = false;
      java.util.List var4 = this.getKeyEventPostProcessors();
      if (var4 != null) {
         for(Iterator var5 = var4.iterator(); !var8 && var5.hasNext(); var8 = ((KeyEventPostProcessor)var5.next()).postProcessKeyEvent(var1)) {
         }
      }

      if (!var8) {
         this.postProcessKeyEvent(var1);
      }

      Component var9 = var1.getComponent();
      ComponentPeer var6 = var9.getPeer();
      if (var6 == null || var6 instanceof LightweightPeer) {
         Container var7 = var9.getNativeContainer();
         if (var7 != null) {
            var6 = var7.getPeer();
         }
      }

      if (var6 != null) {
         var6.handleEvent(var1);
      }

      return true;
   }

   public boolean postProcessKeyEvent(KeyEvent var1) {
      if (!var1.isConsumed()) {
         Component var2 = var1.getComponent();
         Container var3 = (Container)((Container)(var2 instanceof Container ? var2 : var2.getParent()));
         if (var3 != null) {
            var3.postProcessKeyEvent(var1);
         }
      }

      return true;
   }

   private void pumpApprovedKeyEvents() {
      KeyEvent var1;
      do {
         var1 = null;
         synchronized(this) {
            if (this.enqueuedKeyEvents.size() != 0) {
               var1 = (KeyEvent)this.enqueuedKeyEvents.getFirst();
               if (this.typeAheadMarkers.size() != 0) {
                  DefaultKeyboardFocusManager.TypeAheadMarker var3 = (DefaultKeyboardFocusManager.TypeAheadMarker)this.typeAheadMarkers.getFirst();
                  if (var1.getWhen() > var3.after) {
                     var1 = null;
                  }
               }

               if (var1 != null) {
                  if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                     focusLog.finer("Pumping approved event {0}", var1);
                  }

                  this.enqueuedKeyEvents.removeFirst();
               }
            }
         }

         if (var1 != null) {
            this.preDispatchKeyEvent(var1);
         }
      } while(var1 != null);

   }

   void dumpMarkers() {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
         focusLog.finest(">>> Markers dump, time: {0}", System.currentTimeMillis());
         synchronized(this) {
            if (this.typeAheadMarkers.size() != 0) {
               Iterator var2 = this.typeAheadMarkers.iterator();

               while(var2.hasNext()) {
                  DefaultKeyboardFocusManager.TypeAheadMarker var3 = (DefaultKeyboardFocusManager.TypeAheadMarker)var2.next();
                  focusLog.finest("    {0}", var3);
               }
            }
         }
      }

   }

   private boolean typeAheadAssertions(Component var1, AWTEvent var2) {
      this.pumpApprovedKeyEvents();
      switch(var2.getID()) {
      case 400:
      case 401:
      case 402:
         KeyEvent var3 = (KeyEvent)var2;
         synchronized(this) {
            if (var2.isPosted && this.typeAheadMarkers.size() != 0) {
               DefaultKeyboardFocusManager.TypeAheadMarker var10 = (DefaultKeyboardFocusManager.TypeAheadMarker)this.typeAheadMarkers.getFirst();
               if (var3.getWhen() > var10.after) {
                  if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
                     focusLog.finer("Storing event {0} because of marker {1}", var3, var10);
                  }

                  this.enqueuedKeyEvents.addLast(var3);
                  return true;
               }
            }
         }

         return this.preDispatchKeyEvent(var3);
      case 1004:
         if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("Markers before FOCUS_GAINED on {0}", var1);
         }

         this.dumpMarkers();
         synchronized(this) {
            boolean var4 = false;
            if (this.hasMarker(var1)) {
               for(Iterator var5 = this.typeAheadMarkers.iterator(); var5.hasNext(); var5.remove()) {
                  if (((DefaultKeyboardFocusManager.TypeAheadMarker)var5.next()).untilFocused == var1) {
                     var4 = true;
                  } else if (var4) {
                     break;
                  }
               }
            } else if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
               focusLog.finer("Event without marker {0}", var2);
            }
         }

         focusLog.finest("Markers after FOCUS_GAINED");
         this.dumpMarkers();
         this.redispatchEvent(var1, var2);
         this.pumpApprovedKeyEvents();
         return true;
      default:
         this.redispatchEvent(var1, var2);
         return true;
      }
   }

   private boolean hasMarker(Component var1) {
      Iterator var2 = this.typeAheadMarkers.iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((DefaultKeyboardFocusManager.TypeAheadMarker)var2.next()).untilFocused != var1);

      return true;
   }

   void clearMarkers() {
      synchronized(this) {
         this.typeAheadMarkers.clear();
      }
   }

   private boolean preDispatchKeyEvent(KeyEvent var1) {
      Component var2;
      if (var1.isPosted) {
         var2 = this.getFocusOwner();
         var1.setSource(var2 != null ? var2 : this.getFocusedWindow());
      }

      if (var1.getSource() == null) {
         return true;
      } else {
         EventQueue.setCurrentEventAndMostRecentTime(var1);
         if (KeyboardFocusManager.isProxyActive(var1)) {
            var2 = (Component)var1.getSource();
            Container var6 = var2.getNativeContainer();
            if (var6 != null) {
               ComponentPeer var4 = var6.getPeer();
               if (var4 != null) {
                  var4.handleEvent(var1);
                  var1.consume();
               }
            }

            return true;
         } else {
            java.util.List var5 = this.getKeyEventDispatchers();
            if (var5 != null) {
               Iterator var3 = var5.iterator();

               while(var3.hasNext()) {
                  if (((KeyEventDispatcher)var3.next()).dispatchKeyEvent(var1)) {
                     return true;
                  }
               }
            }

            return this.dispatchKeyEvent(var1);
         }
      }
   }

   private void consumeNextKeyTyped(KeyEvent var1) {
      this.consumeNextKeyTyped = true;
   }

   private void consumeTraversalKey(KeyEvent var1) {
      var1.consume();
      this.consumeNextKeyTyped = var1.getID() == 401 && !var1.isActionKey();
   }

   private boolean consumeProcessedKeyEvent(KeyEvent var1) {
      if (var1.getID() == 400 && this.consumeNextKeyTyped) {
         var1.consume();
         this.consumeNextKeyTyped = false;
         return true;
      } else {
         return false;
      }
   }

   public void processKeyEvent(Component var1, KeyEvent var2) {
      if (!this.consumeProcessedKeyEvent(var2)) {
         if (var2.getID() != 400) {
            if (var1.getFocusTraversalKeysEnabled() && !var2.isConsumed()) {
               AWTKeyStroke var3 = AWTKeyStroke.getAWTKeyStrokeForEvent(var2);
               AWTKeyStroke var4 = AWTKeyStroke.getAWTKeyStroke(var3.getKeyCode(), var3.getModifiers(), !var3.isOnKeyRelease());
               Set var5 = var1.getFocusTraversalKeys(0);
               boolean var6 = var5.contains(var3);
               boolean var7 = var5.contains(var4);
               if (!var6 && !var7) {
                  if (var2.getID() == 401) {
                     this.consumeNextKeyTyped = false;
                  }

                  var5 = var1.getFocusTraversalKeys(1);
                  var6 = var5.contains(var3);
                  var7 = var5.contains(var4);
                  if (!var6 && !var7) {
                     var5 = var1.getFocusTraversalKeys(2);
                     var6 = var5.contains(var3);
                     var7 = var5.contains(var4);
                     if (!var6 && !var7) {
                        if (var1 instanceof Container && ((Container)var1).isFocusCycleRoot()) {
                           var5 = var1.getFocusTraversalKeys(3);
                           var6 = var5.contains(var3);
                           var7 = var5.contains(var4);
                           if (var6 || var7) {
                              this.consumeTraversalKey(var2);
                              if (var6) {
                                 this.downFocusCycle((Container)var1);
                                 return;
                              }
                           }

                        }
                     } else {
                        this.consumeTraversalKey(var2);
                        if (var6) {
                           this.upFocusCycle(var1);
                        }

                     }
                  } else {
                     this.consumeTraversalKey(var2);
                     if (var6) {
                        this.focusPreviousComponent(var1);
                     }

                  }
               } else {
                  this.consumeTraversalKey(var2);
                  if (var6) {
                     this.focusNextComponent(var1);
                  }

               }
            }
         }
      }
   }

   protected synchronized void enqueueKeyEvents(long var1, Component var3) {
      if (var3 != null) {
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("Enqueue at {0} for {1}", var1, var3);
         }

         int var4 = 0;
         int var5 = this.typeAheadMarkers.size();

         for(ListIterator var6 = this.typeAheadMarkers.listIterator(var5); var5 > 0; --var5) {
            DefaultKeyboardFocusManager.TypeAheadMarker var7 = (DefaultKeyboardFocusManager.TypeAheadMarker)var6.previous();
            if (var7.after <= var1) {
               var4 = var5;
               break;
            }
         }

         this.typeAheadMarkers.add(var4, new DefaultKeyboardFocusManager.TypeAheadMarker(var1, var3));
      }
   }

   protected synchronized void dequeueKeyEvents(long var1, Component var3) {
      if (var3 != null) {
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("Dequeue at {0} for {1}", var1, var3);
         }

         ListIterator var5 = this.typeAheadMarkers.listIterator(var1 >= 0L ? this.typeAheadMarkers.size() : 0);
         DefaultKeyboardFocusManager.TypeAheadMarker var4;
         if (var1 < 0L) {
            while(var5.hasNext()) {
               var4 = (DefaultKeyboardFocusManager.TypeAheadMarker)var5.next();
               if (var4.untilFocused == var3) {
                  var5.remove();
                  return;
               }
            }
         } else {
            while(var5.hasPrevious()) {
               var4 = (DefaultKeyboardFocusManager.TypeAheadMarker)var5.previous();
               if (var4.untilFocused == var3 && var4.after == var1) {
                  var5.remove();
                  return;
               }
            }
         }

      }
   }

   protected synchronized void discardKeyEvents(Component var1) {
      if (var1 != null) {
         long var2 = -1L;
         Iterator var4 = this.typeAheadMarkers.iterator();

         while(var4.hasNext()) {
            DefaultKeyboardFocusManager.TypeAheadMarker var5 = (DefaultKeyboardFocusManager.TypeAheadMarker)var4.next();
            Object var6 = var5.untilFocused;

            boolean var7;
            for(var7 = var6 == var1; !var7 && var6 != null && !(var6 instanceof Window); var7 = var6 == var1) {
               var6 = ((Component)var6).getParent();
            }

            if (var7) {
               if (var2 < 0L) {
                  var2 = var5.after;
               }

               var4.remove();
            } else if (var2 >= 0L) {
               this.purgeStampedEvents(var2, var5.after);
               var2 = -1L;
            }
         }

         this.purgeStampedEvents(var2, -1L);
      }
   }

   private void purgeStampedEvents(long var1, long var3) {
      if (var1 >= 0L) {
         Iterator var5 = this.enqueuedKeyEvents.iterator();

         while(var5.hasNext()) {
            KeyEvent var6 = (KeyEvent)var5.next();
            long var7 = var6.getWhen();
            if (var1 < var7 && (var3 < 0L || var7 <= var3)) {
               var5.remove();
            }

            if (var3 >= 0L && var7 > var3) {
               break;
            }
         }

      }
   }

   public void focusPreviousComponent(Component var1) {
      if (var1 != null) {
         var1.transferFocusBackward();
      }

   }

   public void focusNextComponent(Component var1) {
      if (var1 != null) {
         var1.transferFocus();
      }

   }

   public void upFocusCycle(Component var1) {
      if (var1 != null) {
         var1.transferFocusUpCycle();
      }

   }

   public void downFocusCycle(Container var1) {
      if (var1 != null && var1.isFocusCycleRoot()) {
         var1.transferFocusDownCycle();
      }

   }

   static {
      AWTAccessor.setDefaultKeyboardFocusManagerAccessor(new AWTAccessor.DefaultKeyboardFocusManagerAccessor() {
         public void consumeNextKeyTyped(DefaultKeyboardFocusManager var1, KeyEvent var2) {
            var1.consumeNextKeyTyped(var2);
         }
      });
   }

   private static class DefaultKeyboardFocusManagerSentEvent extends SentEvent {
      private static final long serialVersionUID = -2924743257508701758L;

      public DefaultKeyboardFocusManagerSentEvent(AWTEvent var1, AppContext var2) {
         super(var1, var2);
      }

      public final void dispatch() {
         KeyboardFocusManager var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
         DefaultKeyboardFocusManager var2 = var1 instanceof DefaultKeyboardFocusManager ? (DefaultKeyboardFocusManager)var1 : null;
         if (var2 != null) {
            synchronized(var2) {
               var2.inSendMessage++;
            }
         }

         super.dispatch();
         if (var2 != null) {
            synchronized(var2) {
               var2.inSendMessage--;
            }
         }

      }
   }

   private static class TypeAheadMarker {
      long after;
      Component untilFocused;

      TypeAheadMarker(long var1, Component var3) {
         this.after = var1;
         this.untilFocused = var3;
      }

      public String toString() {
         return ">>> Marker after " + this.after + " on " + this.untilFocused;
      }
   }
}
