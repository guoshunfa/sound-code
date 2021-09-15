package sun.awt.im;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

public class InputContext extends java.awt.im.InputContext implements ComponentListener, WindowListener {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.im.InputContext");
   private InputMethodLocator inputMethodLocator;
   private InputMethod inputMethod;
   private boolean inputMethodCreationFailed;
   private HashMap<InputMethodLocator, InputMethod> usedInputMethods;
   private Component currentClientComponent;
   private Component awtFocussedComponent;
   private boolean isInputMethodActive;
   private Character.Subset[] characterSubsets = null;
   private boolean compositionAreaHidden = false;
   private static InputContext inputMethodWindowContext;
   private static InputMethod previousInputMethod = null;
   private boolean clientWindowNotificationEnabled = false;
   private Window clientWindowListened;
   private Rectangle clientWindowLocation = null;
   private HashMap<InputMethod, Boolean> perInputMethodState;
   private static AWTKeyStroke inputMethodSelectionKey;
   private static boolean inputMethodSelectionKeyInitialized = false;
   private static final String inputMethodSelectionKeyPath = "/java/awt/im/selectionKey";
   private static final String inputMethodSelectionKeyCodeName = "keyCode";
   private static final String inputMethodSelectionKeyModifiersName = "modifiers";

   protected InputContext() {
      InputMethodManager var1 = InputMethodManager.getInstance();
      Class var2 = InputContext.class;
      synchronized(InputContext.class) {
         if (!inputMethodSelectionKeyInitialized) {
            inputMethodSelectionKeyInitialized = true;
            if (var1.hasMultipleInputMethods()) {
               this.initializeInputMethodSelectionKey();
            }
         }
      }

      this.selectInputMethod(var1.getDefaultKeyboardLocale());
   }

   public synchronized boolean selectInputMethod(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.inputMethod != null) {
            if (this.inputMethod.setLocale(var1)) {
               return true;
            }
         } else if (this.inputMethodLocator != null && this.inputMethodLocator.isLocaleAvailable(var1)) {
            this.inputMethodLocator = this.inputMethodLocator.deriveLocator(var1);
            return true;
         }

         InputMethodLocator var2 = InputMethodManager.getInstance().findInputMethod(var1);
         if (var2 != null) {
            this.changeInputMethod(var2);
            return true;
         } else {
            if (this.inputMethod == null && this.inputMethodLocator != null) {
               this.inputMethod = this.getInputMethod();
               if (this.inputMethod != null) {
                  return this.inputMethod.setLocale(var1);
               }
            }

            return false;
         }
      }
   }

   public Locale getLocale() {
      if (this.inputMethod != null) {
         return this.inputMethod.getLocale();
      } else {
         return this.inputMethodLocator != null ? this.inputMethodLocator.getLocale() : null;
      }
   }

   public void setCharacterSubsets(Character.Subset[] var1) {
      if (var1 == null) {
         this.characterSubsets = null;
      } else {
         this.characterSubsets = new Character.Subset[var1.length];
         System.arraycopy(var1, 0, this.characterSubsets, 0, this.characterSubsets.length);
      }

      if (this.inputMethod != null) {
         this.inputMethod.setCharacterSubsets(var1);
      }

   }

   public synchronized void reconvert() {
      InputMethod var1 = this.getInputMethod();
      if (var1 == null) {
         throw new UnsupportedOperationException();
      } else {
         var1.reconvert();
      }
   }

   public void dispatchEvent(AWTEvent var1) {
      if (!(var1 instanceof InputMethodEvent)) {
         if (var1 instanceof FocusEvent) {
            Component var2 = ((FocusEvent)var1).getOppositeComponent();
            if (var2 != null && getComponentWindow(var2) instanceof InputMethodWindow && var2.getInputContext() == this) {
               return;
            }
         }

         InputMethod var4 = this.getInputMethod();
         int var3 = var1.getID();
         switch(var3) {
         case 401:
            if (this.checkInputMethodSelectionKey((KeyEvent)var1)) {
               InputMethodManager.getInstance().notifyChangeRequestByHotKey((Component)var1.getSource());
               break;
            }
         default:
            if (var4 != null && var1 instanceof InputEvent) {
               var4.dispatchEvent(var1);
            }
            break;
         case 1004:
            this.focusGained((Component)var1.getSource());
            break;
         case 1005:
            this.focusLost((Component)var1.getSource(), ((FocusEvent)var1).isTemporary());
         }

      }
   }

   private void focusGained(Component var1) {
      synchronized(var1.getTreeLock()) {
         synchronized(this) {
            if (!"sun.awt.im.CompositionArea".equals(var1.getClass().getName()) && !(getComponentWindow(var1) instanceof InputMethodWindow)) {
               if (!var1.isDisplayable()) {
                  return;
               }

               if (this.inputMethod != null && this.currentClientComponent != null && this.currentClientComponent != var1) {
                  if (!this.isInputMethodActive) {
                     this.activateInputMethod(false);
                  }

                  this.endComposition();
                  this.deactivateInputMethod(false);
               }

               this.currentClientComponent = var1;
            }

            this.awtFocussedComponent = var1;
            if (this.inputMethod instanceof InputMethodAdapter) {
               ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(var1);
            }

            if (!this.isInputMethodActive) {
               this.activateInputMethod(true);
            }

            InputMethodContext var4 = (InputMethodContext)this;
            if (!var4.isCompositionAreaVisible()) {
               InputMethodRequests var5 = var1.getInputMethodRequests();
               if (var5 != null && var4.useBelowTheSpotInput()) {
                  var4.setCompositionAreaUndecorated(true);
               } else {
                  var4.setCompositionAreaUndecorated(false);
               }
            }

            if (this.compositionAreaHidden) {
               ((InputMethodContext)this).setCompositionAreaVisible(true);
               this.compositionAreaHidden = false;
            }

         }
      }
   }

   private void activateInputMethod(boolean var1) {
      if (inputMethodWindowContext != null && inputMethodWindowContext != this && inputMethodWindowContext.inputMethodLocator != null && !inputMethodWindowContext.inputMethodLocator.sameInputMethod(this.inputMethodLocator) && inputMethodWindowContext.inputMethod != null) {
         inputMethodWindowContext.inputMethod.hideWindows();
      }

      inputMethodWindowContext = this;
      if (this.inputMethod != null) {
         if (previousInputMethod != this.inputMethod && previousInputMethod instanceof InputMethodAdapter) {
            ((InputMethodAdapter)previousInputMethod).stopListening();
         }

         previousInputMethod = null;
         if (log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("Current client component " + this.currentClientComponent);
         }

         if (this.inputMethod instanceof InputMethodAdapter) {
            ((InputMethodAdapter)this.inputMethod).setClientComponent(this.currentClientComponent);
         }

         this.inputMethod.activate();
         this.isInputMethodActive = true;
         if (this.perInputMethodState != null) {
            Boolean var2 = (Boolean)this.perInputMethodState.remove(this.inputMethod);
            if (var2 != null) {
               this.clientWindowNotificationEnabled = var2;
            }
         }

         if (this.clientWindowNotificationEnabled) {
            if (!this.addedClientWindowListeners()) {
               this.addClientWindowListeners();
            }

            synchronized(this) {
               if (this.clientWindowListened != null) {
                  this.notifyClientWindowChange(this.clientWindowListened);
               }
            }
         } else if (this.addedClientWindowListeners()) {
            this.removeClientWindowListeners();
         }
      }

      InputMethodManager.getInstance().setInputContext(this);
      ((InputMethodContext)this).grabCompositionArea(var1);
   }

   static Window getComponentWindow(Component var0) {
      while(var0 != null) {
         if (var0 instanceof Window) {
            return (Window)var0;
         }

         var0 = ((Component)var0).getParent();
      }

      return null;
   }

   private void focusLost(Component var1, boolean var2) {
      synchronized(var1.getTreeLock()) {
         synchronized(this) {
            if (this.isInputMethodActive) {
               this.deactivateInputMethod(var2);
            }

            this.awtFocussedComponent = null;
            if (this.inputMethod instanceof InputMethodAdapter) {
               ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent((Component)null);
            }

            InputMethodContext var5 = (InputMethodContext)this;
            if (var5.isCompositionAreaVisible()) {
               var5.setCompositionAreaVisible(false);
               this.compositionAreaHidden = true;
            }
         }

      }
   }

   private boolean checkInputMethodSelectionKey(KeyEvent var1) {
      if (inputMethodSelectionKey != null) {
         AWTKeyStroke var2 = AWTKeyStroke.getAWTKeyStrokeForEvent(var1);
         return inputMethodSelectionKey.equals(var2);
      } else {
         return false;
      }
   }

   private void deactivateInputMethod(boolean var1) {
      InputMethodManager.getInstance().setInputContext((InputContext)null);
      if (this.inputMethod != null) {
         this.isInputMethodActive = false;
         this.inputMethod.deactivate(var1);
         previousInputMethod = this.inputMethod;
      }

   }

   synchronized void changeInputMethod(InputMethodLocator var1) {
      if (this.inputMethodLocator == null) {
         this.inputMethodLocator = var1;
         this.inputMethodCreationFailed = false;
      } else {
         Locale var2;
         if (this.inputMethodLocator.sameInputMethod(var1)) {
            var2 = var1.getLocale();
            if (var2 != null && this.inputMethodLocator.getLocale() != var2) {
               if (this.inputMethod != null) {
                  this.inputMethod.setLocale(var2);
               }

               this.inputMethodLocator = var1;
            }

         } else {
            var2 = this.inputMethodLocator.getLocale();
            boolean var3 = this.isInputMethodActive;
            boolean var4 = false;
            boolean var5 = false;
            if (this.inputMethod != null) {
               try {
                  var5 = this.inputMethod.isCompositionEnabled();
                  var4 = true;
               } catch (UnsupportedOperationException var8) {
               }

               if (this.currentClientComponent != null) {
                  if (!this.isInputMethodActive) {
                     this.activateInputMethod(false);
                  }

                  this.endComposition();
                  this.deactivateInputMethod(false);
                  if (this.inputMethod instanceof InputMethodAdapter) {
                     ((InputMethodAdapter)this.inputMethod).setClientComponent((Component)null);
                  }
               }

               var2 = this.inputMethod.getLocale();
               if (this.usedInputMethods == null) {
                  this.usedInputMethods = new HashMap(5);
               }

               if (this.perInputMethodState == null) {
                  this.perInputMethodState = new HashMap(5);
               }

               this.usedInputMethods.put(this.inputMethodLocator.deriveLocator((Locale)null), this.inputMethod);
               this.perInputMethodState.put(this.inputMethod, this.clientWindowNotificationEnabled);
               this.enableClientWindowNotification(this.inputMethod, false);
               if (this == inputMethodWindowContext) {
                  this.inputMethod.hideWindows();
                  inputMethodWindowContext = null;
               }

               this.inputMethodLocator = null;
               this.inputMethod = null;
               this.inputMethodCreationFailed = false;
            }

            if (var1.getLocale() == null && var2 != null && var1.isLocaleAvailable(var2)) {
               var1 = var1.deriveLocator(var2);
            }

            this.inputMethodLocator = var1;
            this.inputMethodCreationFailed = false;
            if (var3) {
               this.inputMethod = this.getInputMethodInstance();
               if (this.inputMethod instanceof InputMethodAdapter) {
                  ((InputMethodAdapter)this.inputMethod).setAWTFocussedComponent(this.awtFocussedComponent);
               }

               this.activateInputMethod(true);
            }

            if (var4) {
               this.inputMethod = this.getInputMethod();
               if (this.inputMethod != null) {
                  try {
                     this.inputMethod.setCompositionEnabled(var5);
                  } catch (UnsupportedOperationException var7) {
                  }
               }
            }

         }
      }
   }

   Component getClientComponent() {
      return this.currentClientComponent;
   }

   public synchronized void removeNotify(Component var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.inputMethod == null) {
         if (var1 == this.currentClientComponent) {
            this.currentClientComponent = null;
         }

      } else {
         if (var1 == this.awtFocussedComponent) {
            this.focusLost(var1, false);
         }

         if (var1 == this.currentClientComponent) {
            if (this.isInputMethodActive) {
               this.deactivateInputMethod(false);
            }

            this.inputMethod.removeNotify();
            if (this.clientWindowNotificationEnabled && this.addedClientWindowListeners()) {
               this.removeClientWindowListeners();
            }

            this.currentClientComponent = null;
            if (this.inputMethod instanceof InputMethodAdapter) {
               ((InputMethodAdapter)this.inputMethod).setClientComponent((Component)null);
            }

            if (EventQueue.isDispatchThread()) {
               ((InputMethodContext)this).releaseCompositionArea();
            } else {
               EventQueue.invokeLater(new Runnable() {
                  public void run() {
                     ((InputMethodContext)InputContext.this).releaseCompositionArea();
                  }
               });
            }
         }

      }
   }

   public synchronized void dispose() {
      if (this.currentClientComponent != null) {
         throw new IllegalStateException("Can't dispose InputContext while it's active");
      } else {
         if (this.inputMethod != null) {
            if (this == inputMethodWindowContext) {
               this.inputMethod.hideWindows();
               inputMethodWindowContext = null;
            }

            if (this.inputMethod == previousInputMethod) {
               previousInputMethod = null;
            }

            if (this.clientWindowNotificationEnabled) {
               if (this.addedClientWindowListeners()) {
                  this.removeClientWindowListeners();
               }

               this.clientWindowNotificationEnabled = false;
            }

            this.inputMethod.dispose();
            if (this.clientWindowNotificationEnabled) {
               this.enableClientWindowNotification(this.inputMethod, false);
            }

            this.inputMethod = null;
         }

         this.inputMethodLocator = null;
         if (this.usedInputMethods != null && !this.usedInputMethods.isEmpty()) {
            Iterator var1 = this.usedInputMethods.values().iterator();
            this.usedInputMethods = null;

            while(var1.hasNext()) {
               ((InputMethod)var1.next()).dispose();
            }
         }

         this.clientWindowNotificationEnabled = false;
         this.clientWindowListened = null;
         this.perInputMethodState = null;
      }
   }

   public synchronized Object getInputMethodControlObject() {
      InputMethod var1 = this.getInputMethod();
      return var1 != null ? var1.getControlObject() : null;
   }

   public void setCompositionEnabled(boolean var1) {
      InputMethod var2 = this.getInputMethod();
      if (var2 == null) {
         throw new UnsupportedOperationException();
      } else {
         var2.setCompositionEnabled(var1);
      }
   }

   public boolean isCompositionEnabled() {
      InputMethod var1 = this.getInputMethod();
      if (var1 == null) {
         throw new UnsupportedOperationException();
      } else {
         return var1.isCompositionEnabled();
      }
   }

   public String getInputMethodInfo() {
      InputMethod var1 = this.getInputMethod();
      if (var1 == null) {
         throw new UnsupportedOperationException("Null input method");
      } else {
         String var2 = null;
         if (var1 instanceof InputMethodAdapter) {
            var2 = ((InputMethodAdapter)var1).getNativeInputMethodInfo();
         }

         if (var2 == null && this.inputMethodLocator != null) {
            var2 = this.inputMethodLocator.getDescriptor().getInputMethodDisplayName(this.getLocale(), SunToolkit.getStartupLocale());
         }

         return var2 != null && !var2.equals("") ? var2 : var1.toString() + "-" + var1.getLocale().toString();
      }
   }

   public void disableNativeIM() {
      InputMethod var1 = this.getInputMethod();
      if (var1 != null && var1 instanceof InputMethodAdapter) {
         ((InputMethodAdapter)var1).stopListening();
      }

   }

   private synchronized InputMethod getInputMethod() {
      if (this.inputMethod != null) {
         return this.inputMethod;
      } else if (this.inputMethodCreationFailed) {
         return null;
      } else {
         this.inputMethod = this.getInputMethodInstance();
         return this.inputMethod;
      }
   }

   private InputMethod getInputMethodInstance() {
      InputMethodLocator var1 = this.inputMethodLocator;
      if (var1 == null) {
         this.inputMethodCreationFailed = true;
         return null;
      } else {
         Locale var2 = var1.getLocale();
         InputMethod var3 = null;
         if (this.usedInputMethods != null) {
            var3 = (InputMethod)this.usedInputMethods.remove(var1.deriveLocator((Locale)null));
            if (var3 != null) {
               if (var2 != null) {
                  var3.setLocale(var2);
               }

               var3.setCharacterSubsets(this.characterSubsets);
               Boolean var4 = (Boolean)this.perInputMethodState.remove(var3);
               if (var4 != null) {
                  this.enableClientWindowNotification(var3, var4);
               }

               ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot(!(var3 instanceof InputMethodAdapter) || ((InputMethodAdapter)var3).supportsBelowTheSpot());
               return var3;
            }
         }

         try {
            var3 = var1.getDescriptor().createInputMethod();
            if (var2 != null) {
               var3.setLocale(var2);
            }

            var3.setInputMethodContext((InputMethodContext)this);
            var3.setCharacterSubsets(this.characterSubsets);
         } catch (Exception var5) {
            this.logCreationFailed(var5);
            this.inputMethodCreationFailed = true;
            if (var3 != null) {
               var3 = null;
            }
         } catch (LinkageError var6) {
            this.logCreationFailed(var6);
            this.inputMethodCreationFailed = true;
         }

         ((InputMethodContext)this).setInputMethodSupportsBelowTheSpot(!(var3 instanceof InputMethodAdapter) || ((InputMethodAdapter)var3).supportsBelowTheSpot());
         return var3;
      }
   }

   private void logCreationFailed(Throwable var1) {
      PlatformLogger var2 = PlatformLogger.getLogger("sun.awt.im");
      if (var2.isLoggable(PlatformLogger.Level.CONFIG)) {
         String var3 = Toolkit.getProperty("AWT.InputMethodCreationFailed", "Could not create {0}. Reason: {1}");
         Object[] var4 = new Object[]{this.inputMethodLocator.getDescriptor().getInputMethodDisplayName((Locale)null, Locale.getDefault()), var1.getLocalizedMessage()};
         MessageFormat var5 = new MessageFormat(var3);
         var2.config(var5.format(var4));
      }

   }

   InputMethodLocator getInputMethodLocator() {
      return this.inputMethod != null ? this.inputMethodLocator.deriveLocator(this.inputMethod.getLocale()) : this.inputMethodLocator;
   }

   public synchronized void endComposition() {
      if (this.inputMethod != null) {
         this.inputMethod.endComposition();
      }

   }

   synchronized void enableClientWindowNotification(InputMethod var1, boolean var2) {
      if (var1 != this.inputMethod) {
         if (this.perInputMethodState == null) {
            this.perInputMethodState = new HashMap(5);
         }

         this.perInputMethodState.put(var1, var2);
      } else {
         if (this.clientWindowNotificationEnabled != var2) {
            this.clientWindowLocation = null;
            this.clientWindowNotificationEnabled = var2;
         }

         if (this.clientWindowNotificationEnabled) {
            if (!this.addedClientWindowListeners()) {
               this.addClientWindowListeners();
            }

            if (this.clientWindowListened != null) {
               this.clientWindowLocation = null;
               this.notifyClientWindowChange(this.clientWindowListened);
            }
         } else if (this.addedClientWindowListeners()) {
            this.removeClientWindowListeners();
         }

      }
   }

   private synchronized void notifyClientWindowChange(Window var1) {
      if (this.inputMethod != null) {
         if (!var1.isVisible() || var1 instanceof Frame && ((Frame)var1).getState() == 1) {
            this.clientWindowLocation = null;
            this.inputMethod.notifyClientWindowChange((Rectangle)null);
         } else {
            Rectangle var2 = var1.getBounds();
            if (this.clientWindowLocation == null || !this.clientWindowLocation.equals(var2)) {
               this.clientWindowLocation = var2;
               this.inputMethod.notifyClientWindowChange(this.clientWindowLocation);
            }

         }
      }
   }

   private synchronized void addClientWindowListeners() {
      Component var1 = this.getClientComponent();
      if (var1 != null) {
         Window var2 = getComponentWindow(var1);
         if (var2 != null) {
            var2.addComponentListener(this);
            var2.addWindowListener(this);
            this.clientWindowListened = var2;
         }
      }
   }

   private synchronized void removeClientWindowListeners() {
      this.clientWindowListened.removeComponentListener(this);
      this.clientWindowListened.removeWindowListener(this);
      this.clientWindowListened = null;
   }

   private boolean addedClientWindowListeners() {
      return this.clientWindowListened != null;
   }

   public void componentResized(ComponentEvent var1) {
      this.notifyClientWindowChange((Window)var1.getComponent());
   }

   public void componentMoved(ComponentEvent var1) {
      this.notifyClientWindowChange((Window)var1.getComponent());
   }

   public void componentShown(ComponentEvent var1) {
      this.notifyClientWindowChange((Window)var1.getComponent());
   }

   public void componentHidden(ComponentEvent var1) {
      this.notifyClientWindowChange((Window)var1.getComponent());
   }

   public void windowOpened(WindowEvent var1) {
   }

   public void windowClosing(WindowEvent var1) {
   }

   public void windowClosed(WindowEvent var1) {
   }

   public void windowIconified(WindowEvent var1) {
      this.notifyClientWindowChange(var1.getWindow());
   }

   public void windowDeiconified(WindowEvent var1) {
      this.notifyClientWindowChange(var1.getWindow());
   }

   public void windowActivated(WindowEvent var1) {
   }

   public void windowDeactivated(WindowEvent var1) {
   }

   private void initializeInputMethodSelectionKey() {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            Preferences var1 = Preferences.userRoot();
            InputContext.inputMethodSelectionKey = InputContext.this.getInputMethodSelectionKeyStroke(var1);
            if (InputContext.inputMethodSelectionKey == null) {
               var1 = Preferences.systemRoot();
               InputContext.inputMethodSelectionKey = InputContext.this.getInputMethodSelectionKeyStroke(var1);
            }

            return null;
         }
      });
   }

   private AWTKeyStroke getInputMethodSelectionKeyStroke(Preferences var1) {
      try {
         if (var1.nodeExists("/java/awt/im/selectionKey")) {
            Preferences var2 = var1.node("/java/awt/im/selectionKey");
            int var3 = var2.getInt("keyCode", 0);
            if (var3 != 0) {
               int var4 = var2.getInt("modifiers", 0);
               return AWTKeyStroke.getAWTKeyStroke(var3, var4);
            }
         }
      } catch (BackingStoreException var5) {
      }

      return null;
   }
}
