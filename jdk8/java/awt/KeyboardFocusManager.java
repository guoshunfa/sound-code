package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.KeyboardFocusManagerPeerProvider;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;

public abstract class KeyboardFocusManager implements KeyEventDispatcher, KeyEventPostProcessor {
   private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.KeyboardFocusManager");
   transient KeyboardFocusManagerPeer peer;
   private static final PlatformLogger log;
   public static final int FORWARD_TRAVERSAL_KEYS = 0;
   public static final int BACKWARD_TRAVERSAL_KEYS = 1;
   public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
   public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
   static final int TRAVERSAL_KEY_LENGTH = 4;
   private static Component focusOwner;
   private static Component permanentFocusOwner;
   private static Window focusedWindow;
   private static Window activeWindow;
   private FocusTraversalPolicy defaultPolicy = new DefaultFocusTraversalPolicy();
   private static final String[] defaultFocusTraversalKeyPropertyNames;
   private static final AWTKeyStroke[][] defaultFocusTraversalKeyStrokes;
   private Set<AWTKeyStroke>[] defaultFocusTraversalKeys = new Set[4];
   private static Container currentFocusCycleRoot;
   private VetoableChangeSupport vetoableSupport;
   private PropertyChangeSupport changeSupport;
   private LinkedList<KeyEventDispatcher> keyEventDispatchers;
   private LinkedList<KeyEventPostProcessor> keyEventPostProcessors;
   private static Map<Window, WeakReference<Component>> mostRecentFocusOwners;
   private static AWTPermission replaceKeyboardFocusManagerPermission;
   transient SequencedEvent currentSequencedEvent = null;
   private static LinkedList<KeyboardFocusManager.HeavyweightFocusRequest> heavyweightRequests;
   private static LinkedList<KeyboardFocusManager.LightweightFocusRequest> currentLightweightRequests;
   private static boolean clearingCurrentLightweightRequests;
   private static boolean allowSyncFocusRequests;
   private static Component newFocusOwner;
   private static volatile boolean disableRestoreFocus;
   static final int SNFH_FAILURE = 0;
   static final int SNFH_SUCCESS_HANDLED = 1;
   static final int SNFH_SUCCESS_PROCEED = 2;
   static Field proxyActive;

   private static native void initIDs();

   public static KeyboardFocusManager getCurrentKeyboardFocusManager() {
      return getCurrentKeyboardFocusManager(AppContext.getAppContext());
   }

   static synchronized KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext var0) {
      Object var1 = (KeyboardFocusManager)var0.get(KeyboardFocusManager.class);
      if (var1 == null) {
         var1 = new DefaultKeyboardFocusManager();
         var0.put(KeyboardFocusManager.class, var1);
      }

      return (KeyboardFocusManager)var1;
   }

   public static void setCurrentKeyboardFocusManager(KeyboardFocusManager var0) throws SecurityException {
      checkReplaceKFMPermission();
      KeyboardFocusManager var1 = null;
      Class var2 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         AppContext var3 = AppContext.getAppContext();
         if (var0 != null) {
            var1 = getCurrentKeyboardFocusManager(var3);
            var3.put(KeyboardFocusManager.class, var0);
         } else {
            var1 = getCurrentKeyboardFocusManager(var3);
            var3.remove(KeyboardFocusManager.class);
         }
      }

      if (var1 != null) {
         var1.firePropertyChange("managingFocus", Boolean.TRUE, Boolean.FALSE);
      }

      if (var0 != null) {
         var0.firePropertyChange("managingFocus", Boolean.FALSE, Boolean.TRUE);
      }

   }

   final void setCurrentSequencedEvent(SequencedEvent var1) {
      Class var2 = SequencedEvent.class;
      synchronized(SequencedEvent.class) {
         assert var1 == null || this.currentSequencedEvent == null;

         this.currentSequencedEvent = var1;
      }
   }

   final SequencedEvent getCurrentSequencedEvent() {
      Class var1 = SequencedEvent.class;
      synchronized(SequencedEvent.class) {
         return this.currentSequencedEvent;
      }
   }

   static Set<AWTKeyStroke> initFocusTraversalKeysSet(String var0, Set<AWTKeyStroke> var1) {
      StringTokenizer var2 = new StringTokenizer(var0, ",");

      while(var2.hasMoreTokens()) {
         var1.add(AWTKeyStroke.getAWTKeyStroke(var2.nextToken()));
      }

      return var1.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(var1);
   }

   public KeyboardFocusManager() {
      for(int var1 = 0; var1 < 4; ++var1) {
         HashSet var2 = new HashSet();

         for(int var3 = 0; var3 < defaultFocusTraversalKeyStrokes[var1].length; ++var3) {
            var2.add(defaultFocusTraversalKeyStrokes[var1][var3]);
         }

         this.defaultFocusTraversalKeys[var1] = var2.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(var2);
      }

      this.initPeer();
   }

   private void initPeer() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      KeyboardFocusManagerPeerProvider var2 = (KeyboardFocusManagerPeerProvider)var1;
      this.peer = var2.getKeyboardFocusManagerPeer();
   }

   public Component getFocusOwner() {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         if (focusOwner == null) {
            return null;
         } else {
            return focusOwner.appContext == AppContext.getAppContext() ? focusOwner : null;
         }
      }
   }

   protected Component getGlobalFocusOwner() throws SecurityException {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         this.checkKFMSecurity();
         return focusOwner;
      }
   }

   protected void setGlobalFocusOwner(Component var1) throws SecurityException {
      Component var2 = null;
      boolean var3 = false;
      if (var1 == null || var1.isFocusable()) {
         Class var4 = KeyboardFocusManager.class;
         synchronized(KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            var2 = this.getFocusOwner();

            try {
               this.fireVetoableChange("focusOwner", var2, var1);
            } catch (PropertyVetoException var7) {
               return;
            }

            focusOwner = var1;
            if (var1 != null && (this.getCurrentFocusCycleRoot() == null || !var1.isFocusCycleRoot(this.getCurrentFocusCycleRoot()))) {
               Container var5 = var1.getFocusCycleRootAncestor();
               if (var5 == null && var1 instanceof Window) {
                  var5 = (Container)var1;
               }

               if (var5 != null) {
                  this.setGlobalCurrentFocusCycleRootPriv(var5);
               }
            }

            var3 = true;
         }
      }

      if (var3) {
         this.firePropertyChange("focusOwner", var2, var1);
      }

   }

   public void clearFocusOwner() {
      if (this.getFocusOwner() != null) {
         this.clearGlobalFocusOwner();
      }

   }

   public void clearGlobalFocusOwner() throws SecurityException {
      checkReplaceKFMPermission();
      if (!GraphicsEnvironment.isHeadless()) {
         Toolkit.getDefaultToolkit();
         this._clearGlobalFocusOwner();
      }

   }

   private void _clearGlobalFocusOwner() {
      Window var1 = markClearGlobalFocusOwner();
      this.peer.clearGlobalFocusOwner(var1);
   }

   void clearGlobalFocusOwnerPriv() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            KeyboardFocusManager.this.clearGlobalFocusOwner();
            return null;
         }
      });
   }

   Component getNativeFocusOwner() {
      return this.peer.getCurrentFocusOwner();
   }

   void setNativeFocusOwner(Component var1) {
      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
         focusLog.finest("Calling peer {0} setCurrentFocusOwner for {1}", String.valueOf((Object)this.peer), String.valueOf((Object)var1));
      }

      this.peer.setCurrentFocusOwner(var1);
   }

   Window getNativeFocusedWindow() {
      return this.peer.getCurrentFocusedWindow();
   }

   public Component getPermanentFocusOwner() {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         if (permanentFocusOwner == null) {
            return null;
         } else {
            return permanentFocusOwner.appContext == AppContext.getAppContext() ? permanentFocusOwner : null;
         }
      }
   }

   protected Component getGlobalPermanentFocusOwner() throws SecurityException {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         this.checkKFMSecurity();
         return permanentFocusOwner;
      }
   }

   protected void setGlobalPermanentFocusOwner(Component var1) throws SecurityException {
      Component var2 = null;
      boolean var3 = false;
      if (var1 == null || var1.isFocusable()) {
         Class var4 = KeyboardFocusManager.class;
         synchronized(KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            var2 = this.getPermanentFocusOwner();

            try {
               this.fireVetoableChange("permanentFocusOwner", var2, var1);
            } catch (PropertyVetoException var7) {
               return;
            }

            permanentFocusOwner = var1;
            setMostRecentFocusOwner(var1);
            var3 = true;
         }
      }

      if (var3) {
         this.firePropertyChange("permanentFocusOwner", var2, var1);
      }

   }

   public Window getFocusedWindow() {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         if (focusedWindow == null) {
            return null;
         } else {
            return focusedWindow.appContext == AppContext.getAppContext() ? focusedWindow : null;
         }
      }
   }

   protected Window getGlobalFocusedWindow() throws SecurityException {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         this.checkKFMSecurity();
         return focusedWindow;
      }
   }

   protected void setGlobalFocusedWindow(Window var1) throws SecurityException {
      Window var2 = null;
      boolean var3 = false;
      if (var1 == null || var1.isFocusableWindow()) {
         Class var4 = KeyboardFocusManager.class;
         synchronized(KeyboardFocusManager.class) {
            this.checkKFMSecurity();
            var2 = this.getFocusedWindow();

            try {
               this.fireVetoableChange("focusedWindow", var2, var1);
            } catch (PropertyVetoException var7) {
               return;
            }

            focusedWindow = var1;
            var3 = true;
         }
      }

      if (var3) {
         this.firePropertyChange("focusedWindow", var2, var1);
      }

   }

   public Window getActiveWindow() {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         if (activeWindow == null) {
            return null;
         } else {
            return activeWindow.appContext == AppContext.getAppContext() ? activeWindow : null;
         }
      }
   }

   protected Window getGlobalActiveWindow() throws SecurityException {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         this.checkKFMSecurity();
         return activeWindow;
      }
   }

   protected void setGlobalActiveWindow(Window var1) throws SecurityException {
      Class var3 = KeyboardFocusManager.class;
      Window var2;
      synchronized(KeyboardFocusManager.class) {
         this.checkKFMSecurity();
         var2 = this.getActiveWindow();
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            focusLog.finer("Setting global active window to " + var1 + ", old active " + var2);
         }

         try {
            this.fireVetoableChange("activeWindow", var2, var1);
         } catch (PropertyVetoException var6) {
            return;
         }

         activeWindow = var1;
      }

      this.firePropertyChange("activeWindow", var2, var1);
   }

   public synchronized FocusTraversalPolicy getDefaultFocusTraversalPolicy() {
      return this.defaultPolicy;
   }

   public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("default focus traversal policy cannot be null");
      } else {
         FocusTraversalPolicy var2;
         synchronized(this) {
            var2 = this.defaultPolicy;
            this.defaultPolicy = var1;
         }

         this.firePropertyChange("defaultFocusTraversalPolicy", var2, var1);
      }
   }

   public void setDefaultFocusTraversalKeys(int var1, Set<? extends AWTKeyStroke> var2) {
      if (var1 >= 0 && var1 < 4) {
         if (var2 == null) {
            throw new IllegalArgumentException("cannot set null Set of default focus traversal keys");
         } else {
            Set var3;
            synchronized(this) {
               Iterator var5 = var2.iterator();

               while(var5.hasNext()) {
                  AWTKeyStroke var6 = (AWTKeyStroke)var5.next();
                  if (var6 == null) {
                     throw new IllegalArgumentException("cannot set null focus traversal key");
                  }

                  if (var6.getKeyChar() != '\uffff') {
                     throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
                  }

                  for(int var7 = 0; var7 < 4; ++var7) {
                     if (var7 != var1 && this.defaultFocusTraversalKeys[var7].contains(var6)) {
                        throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
                     }
                  }
               }

               var3 = this.defaultFocusTraversalKeys[var1];
               this.defaultFocusTraversalKeys[var1] = Collections.unmodifiableSet(new HashSet(var2));
            }

            this.firePropertyChange(defaultFocusTraversalKeyPropertyNames[var1], var3, var2);
         }
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int var1) {
      if (var1 >= 0 && var1 < 4) {
         return this.defaultFocusTraversalKeys[var1];
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public Container getCurrentFocusCycleRoot() {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         if (currentFocusCycleRoot == null) {
            return null;
         } else {
            return currentFocusCycleRoot.appContext == AppContext.getAppContext() ? currentFocusCycleRoot : null;
         }
      }
   }

   protected Container getGlobalCurrentFocusCycleRoot() throws SecurityException {
      Class var1 = KeyboardFocusManager.class;
      synchronized(KeyboardFocusManager.class) {
         this.checkKFMSecurity();
         return currentFocusCycleRoot;
      }
   }

   public void setGlobalCurrentFocusCycleRoot(Container var1) throws SecurityException {
      checkReplaceKFMPermission();
      Class var3 = KeyboardFocusManager.class;
      Container var2;
      synchronized(KeyboardFocusManager.class) {
         var2 = this.getCurrentFocusCycleRoot();
         currentFocusCycleRoot = var1;
      }

      this.firePropertyChange("currentFocusCycleRoot", var2, var1);
   }

   void setGlobalCurrentFocusCycleRootPriv(final Container var1) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            KeyboardFocusManager.this.setGlobalCurrentFocusCycleRoot(var1);
            return null;
         }
      });
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.changeSupport == null) {
               this.changeSupport = new PropertyChangeSupport(this);
            }

            this.changeSupport.addPropertyChangeListener(var1);
         }
      }

   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.changeSupport != null) {
               this.changeSupport.removePropertyChangeListener(var1);
            }
         }
      }

   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      if (this.changeSupport == null) {
         this.changeSupport = new PropertyChangeSupport(this);
      }

      return this.changeSupport.getPropertyChangeListeners();
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null) {
         synchronized(this) {
            if (this.changeSupport == null) {
               this.changeSupport = new PropertyChangeSupport(this);
            }

            this.changeSupport.addPropertyChangeListener(var1, var2);
         }
      }

   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null) {
         synchronized(this) {
            if (this.changeSupport != null) {
               this.changeSupport.removePropertyChangeListener(var1, var2);
            }
         }
      }

   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      if (this.changeSupport == null) {
         this.changeSupport = new PropertyChangeSupport(this);
      }

      return this.changeSupport.getPropertyChangeListeners(var1);
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (var2 != var3) {
         PropertyChangeSupport var4 = this.changeSupport;
         if (var4 != null) {
            var4.firePropertyChange(var1, var2, var3);
         }

      }
   }

   public void addVetoableChangeListener(VetoableChangeListener var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.vetoableSupport == null) {
               this.vetoableSupport = new VetoableChangeSupport(this);
            }

            this.vetoableSupport.addVetoableChangeListener(var1);
         }
      }

   }

   public void removeVetoableChangeListener(VetoableChangeListener var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.vetoableSupport != null) {
               this.vetoableSupport.removeVetoableChangeListener(var1);
            }
         }
      }

   }

   public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
      if (this.vetoableSupport == null) {
         this.vetoableSupport = new VetoableChangeSupport(this);
      }

      return this.vetoableSupport.getVetoableChangeListeners();
   }

   public void addVetoableChangeListener(String var1, VetoableChangeListener var2) {
      if (var2 != null) {
         synchronized(this) {
            if (this.vetoableSupport == null) {
               this.vetoableSupport = new VetoableChangeSupport(this);
            }

            this.vetoableSupport.addVetoableChangeListener(var1, var2);
         }
      }

   }

   public void removeVetoableChangeListener(String var1, VetoableChangeListener var2) {
      if (var2 != null) {
         synchronized(this) {
            if (this.vetoableSupport != null) {
               this.vetoableSupport.removeVetoableChangeListener(var1, var2);
            }
         }
      }

   }

   public synchronized VetoableChangeListener[] getVetoableChangeListeners(String var1) {
      if (this.vetoableSupport == null) {
         this.vetoableSupport = new VetoableChangeSupport(this);
      }

      return this.vetoableSupport.getVetoableChangeListeners(var1);
   }

   protected void fireVetoableChange(String var1, Object var2, Object var3) throws PropertyVetoException {
      if (var2 != var3) {
         VetoableChangeSupport var4 = this.vetoableSupport;
         if (var4 != null) {
            var4.fireVetoableChange(var1, var2, var3);
         }

      }
   }

   public void addKeyEventDispatcher(KeyEventDispatcher var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.keyEventDispatchers == null) {
               this.keyEventDispatchers = new LinkedList();
            }

            this.keyEventDispatchers.add(var1);
         }
      }

   }

   public void removeKeyEventDispatcher(KeyEventDispatcher var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.keyEventDispatchers != null) {
               this.keyEventDispatchers.remove(var1);
            }
         }
      }

   }

   protected synchronized java.util.List<KeyEventDispatcher> getKeyEventDispatchers() {
      return this.keyEventDispatchers != null ? (java.util.List)this.keyEventDispatchers.clone() : null;
   }

   public void addKeyEventPostProcessor(KeyEventPostProcessor var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.keyEventPostProcessors == null) {
               this.keyEventPostProcessors = new LinkedList();
            }

            this.keyEventPostProcessors.add(var1);
         }
      }

   }

   public void removeKeyEventPostProcessor(KeyEventPostProcessor var1) {
      if (var1 != null) {
         synchronized(this) {
            if (this.keyEventPostProcessors != null) {
               this.keyEventPostProcessors.remove(var1);
            }
         }
      }

   }

   protected java.util.List<KeyEventPostProcessor> getKeyEventPostProcessors() {
      return this.keyEventPostProcessors != null ? (java.util.List)this.keyEventPostProcessors.clone() : null;
   }

   static void setMostRecentFocusOwner(Component var0) {
      Object var1;
      for(var1 = var0; var1 != null && !(var1 instanceof Window); var1 = ((Component)var1).parent) {
      }

      if (var1 != null) {
         setMostRecentFocusOwner((Window)var1, var0);
      }

   }

   static synchronized void setMostRecentFocusOwner(Window var0, Component var1) {
      WeakReference var2 = null;
      if (var1 != null) {
         var2 = new WeakReference(var1);
      }

      mostRecentFocusOwners.put(var0, var2);
   }

   static void clearMostRecentFocusOwner(Component var0) {
      if (var0 != null) {
         Container var1;
         synchronized(var0.getTreeLock()) {
            var1 = var0.getParent();

            while(true) {
               if (var1 == null || var1 instanceof Window) {
                  break;
               }

               var1 = var1.getParent();
            }
         }

         Class var2 = KeyboardFocusManager.class;
         synchronized(KeyboardFocusManager.class) {
            if (var1 != null && getMostRecentFocusOwner((Window)var1) == var0) {
               setMostRecentFocusOwner((Window)var1, (Component)null);
            }

            if (var1 != null) {
               Window var3 = (Window)var1;
               if (var3.getTemporaryLostComponent() == var0) {
                  var3.setTemporaryLostComponent((Component)null);
               }
            }

         }
      }
   }

   static synchronized Component getMostRecentFocusOwner(Window var0) {
      WeakReference var1 = (WeakReference)mostRecentFocusOwners.get(var0);
      return var1 == null ? null : (Component)var1.get();
   }

   public abstract boolean dispatchEvent(AWTEvent var1);

   public final void redispatchEvent(Component var1, AWTEvent var2) {
      var2.focusManagerIsDispatching = true;
      var1.dispatchEvent(var2);
      var2.focusManagerIsDispatching = false;
   }

   public abstract boolean dispatchKeyEvent(KeyEvent var1);

   public abstract boolean postProcessKeyEvent(KeyEvent var1);

   public abstract void processKeyEvent(Component var1, KeyEvent var2);

   protected abstract void enqueueKeyEvents(long var1, Component var3);

   protected abstract void dequeueKeyEvents(long var1, Component var3);

   protected abstract void discardKeyEvents(Component var1);

   public abstract void focusNextComponent(Component var1);

   public abstract void focusPreviousComponent(Component var1);

   public abstract void upFocusCycle(Component var1);

   public abstract void downFocusCycle(Container var1);

   public final void focusNextComponent() {
      Component var1 = this.getFocusOwner();
      if (var1 != null) {
         this.focusNextComponent(var1);
      }

   }

   public final void focusPreviousComponent() {
      Component var1 = this.getFocusOwner();
      if (var1 != null) {
         this.focusPreviousComponent(var1);
      }

   }

   public final void upFocusCycle() {
      Component var1 = this.getFocusOwner();
      if (var1 != null) {
         this.upFocusCycle(var1);
      }

   }

   public final void downFocusCycle() {
      Component var1 = this.getFocusOwner();
      if (var1 instanceof Container) {
         this.downFocusCycle((Container)var1);
      }

   }

   void dumpRequests() {
      System.err.println(">>> Requests dump, time: " + System.currentTimeMillis());
      synchronized(heavyweightRequests) {
         Iterator var2 = heavyweightRequests.iterator();

         while(true) {
            if (!var2.hasNext()) {
               break;
            }

            KeyboardFocusManager.HeavyweightFocusRequest var3 = (KeyboardFocusManager.HeavyweightFocusRequest)var2.next();
            System.err.println(">>> Req: " + var3);
         }
      }

      System.err.println("");
   }

   static boolean processSynchronousLightweightTransfer(Component var0, Component var1, boolean var2, boolean var3, long var4) {
      Window var6 = SunToolkit.getContainingWindow(var0);
      if (var6 != null && var6.syncLWRequests) {
         if (var1 == null) {
            var1 = var0;
         }

         KeyboardFocusManager var7 = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(var1));
         FocusEvent var8 = null;
         FocusEvent var9 = null;
         Component var10 = var7.getGlobalFocusOwner();
         synchronized(heavyweightRequests) {
            KeyboardFocusManager.HeavyweightFocusRequest var12 = getLastHWRequest();
            if (var12 == null && var0 == var7.getNativeFocusOwner() && allowSyncFocusRequests) {
               if (var1 == var10) {
                  return true;
               }

               var7.enqueueKeyEvents(var4, var1);
               var12 = new KeyboardFocusManager.HeavyweightFocusRequest(var0, var1, var2, CausedFocusEvent.Cause.UNKNOWN);
               heavyweightRequests.add(var12);
               if (var10 != null) {
                  var8 = new FocusEvent(var10, 1005, var2, var1);
               }

               var9 = new FocusEvent(var1, 1004, var2, var10);
            }
         }

         boolean var11 = false;
         boolean var23 = clearingCurrentLightweightRequests;
         Throwable var13 = null;

         try {
            clearingCurrentLightweightRequests = false;
            synchronized(Component.LOCK) {
               if (var8 != null && var10 != null) {
                  var8.isPosted = true;
                  var13 = dispatchAndCatchException(var13, var10, var8);
                  var11 = true;
               }

               if (var9 != null && var1 != null) {
                  var9.isPosted = true;
                  var13 = dispatchAndCatchException(var13, var1, var9);
                  var11 = true;
               }
            }
         } finally {
            clearingCurrentLightweightRequests = var23;
         }

         if (var13 instanceof RuntimeException) {
            throw (RuntimeException)var13;
         } else if (var13 instanceof Error) {
            throw (Error)var13;
         } else {
            return var11;
         }
      } else {
         return false;
      }
   }

   static int shouldNativelyFocusHeavyweight(Component var0, Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         if (var0 == null) {
            log.fine("Assertion (heavyweight != null) failed");
         }

         if (var4 == 0L) {
            log.fine("Assertion (time != 0) failed");
         }
      }

      if (var1 == null) {
         var1 = var0;
      }

      KeyboardFocusManager var7 = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(var1));
      KeyboardFocusManager var8 = getCurrentKeyboardFocusManager();
      Component var9 = var8.getGlobalFocusOwner();
      Component var10 = var8.getNativeFocusOwner();
      Window var11 = var8.getNativeFocusedWindow();
      if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
         focusLog.finer("SNFH for {0} in {1}", String.valueOf((Object)var1), String.valueOf((Object)var0));
      }

      if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
         focusLog.finest("0. Current focus owner {0}", String.valueOf((Object)var9));
         focusLog.finest("0. Native focus owner {0}", String.valueOf((Object)var10));
         focusLog.finest("0. Native focused window {0}", String.valueOf((Object)var11));
      }

      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var13 = getLastHWRequest();
         if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
            focusLog.finest("Request {0}", String.valueOf((Object)var13));
         }

         if (var13 == null && var0 == var10 && var0.getContainingWindow() == var11) {
            if (var1 == var9) {
               if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                  focusLog.finest("1. SNFH_FAILURE for {0}", String.valueOf((Object)var1));
               }

               return 0;
            } else {
               var7.enqueueKeyEvents(var4, var1);
               var13 = new KeyboardFocusManager.HeavyweightFocusRequest(var0, var1, var2, var6);
               heavyweightRequests.add(var13);
               CausedFocusEvent var17;
               if (var9 != null) {
                  var17 = new CausedFocusEvent(var9, 1005, var2, var1, var6);
                  SunToolkit.postEvent(var9.appContext, var17);
               }

               var17 = new CausedFocusEvent(var1, 1004, var2, var9, var6);
               SunToolkit.postEvent(var1.appContext, var17);
               if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                  focusLog.finest("2. SNFH_HANDLED for {0}", String.valueOf((Object)var1));
               }

               return 1;
            }
         } else if (var13 != null && var13.heavyweight == var0) {
            if (var13.addLightweightRequest(var1, var2, var6)) {
               var7.enqueueKeyEvents(var4, var1);
            }

            if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
               focusLog.finest("3. SNFH_HANDLED for lightweight" + var1 + " in " + var0);
            }

            return 1;
         } else {
            if (!var3) {
               if (var13 == KeyboardFocusManager.HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
                  int var14 = heavyweightRequests.size();
                  var13 = var14 >= 2 ? (KeyboardFocusManager.HeavyweightFocusRequest)heavyweightRequests.get(var14 - 2) : null;
               }

               if (focusedWindowChanged(var0, (Component)(var13 != null ? var13.heavyweight : var11))) {
                  if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
                     focusLog.finest("4. SNFH_FAILURE for " + var1);
                  }

                  return 0;
               }
            }

            var7.enqueueKeyEvents(var4, var1);
            heavyweightRequests.add(new KeyboardFocusManager.HeavyweightFocusRequest(var0, var1, var2, var6));
            if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
               focusLog.finest("5. SNFH_PROCEED for " + var1);
            }

            return 2;
         }
      }
   }

   static Window markClearGlobalFocusOwner() {
      Window var0 = getCurrentKeyboardFocusManager().getNativeFocusedWindow();
      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var2 = getLastHWRequest();
         if (var2 == KeyboardFocusManager.HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
            return null;
         } else {
            heavyweightRequests.add(KeyboardFocusManager.HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER);

            Object var3;
            for(var3 = var2 != null ? SunToolkit.getContainingWindow(var2.heavyweight) : var0; var3 != null && !(var3 instanceof Frame) && !(var3 instanceof Dialog); var3 = ((Component)var3).getParent_NoClientCode()) {
            }

            return (Window)var3;
         }
      }
   }

   Component getCurrentWaitingRequest(Component var1) {
      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var3 = getFirstHWRequest();
         if (var3 != null && var3.heavyweight == var1) {
            KeyboardFocusManager.LightweightFocusRequest var4 = (KeyboardFocusManager.LightweightFocusRequest)var3.lightweightRequests.getFirst();
            if (var4 != null) {
               return var4.component;
            }
         }

         return null;
      }
   }

   static boolean isAutoFocusTransferEnabled() {
      synchronized(heavyweightRequests) {
         return heavyweightRequests.size() == 0 && !disableRestoreFocus && null == currentLightweightRequests;
      }
   }

   static boolean isAutoFocusTransferEnabledFor(Component var0) {
      return isAutoFocusTransferEnabled() && var0.isAutoFocusTransferOnDisposal();
   }

   private static Throwable dispatchAndCatchException(Throwable var0, Component var1, FocusEvent var2) {
      Object var3 = null;

      try {
         var1.dispatchEvent(var2);
      } catch (RuntimeException var5) {
         var3 = var5;
      } catch (Error var6) {
         var3 = var6;
      }

      if (var3 != null) {
         if (var0 != null) {
            handleException(var0);
         }

         return (Throwable)var3;
      } else {
         return var0;
      }
   }

   private static void handleException(Throwable var0) {
      var0.printStackTrace();
   }

   static void processCurrentLightweightRequests() {
      KeyboardFocusManager var0 = getCurrentKeyboardFocusManager();
      LinkedList var1 = null;
      Component var2 = var0.getGlobalFocusOwner();
      if (var2 == null || var2.appContext == AppContext.getAppContext()) {
         synchronized(heavyweightRequests) {
            if (currentLightweightRequests == null) {
               return;
            }

            clearingCurrentLightweightRequests = true;
            disableRestoreFocus = true;
            var1 = currentLightweightRequests;
            allowSyncFocusRequests = var1.size() < 2;
            currentLightweightRequests = null;
         }

         Throwable var3 = null;

         try {
            if (var1 != null) {
               Component var4 = null;
               Component var5 = null;
               Iterator var6 = var1.iterator();

               while(var6.hasNext()) {
                  var5 = var0.getGlobalFocusOwner();
                  KeyboardFocusManager.LightweightFocusRequest var7 = (KeyboardFocusManager.LightweightFocusRequest)var6.next();
                  if (!var6.hasNext()) {
                     disableRestoreFocus = false;
                  }

                  CausedFocusEvent var8 = null;
                  if (var5 != null) {
                     var8 = new CausedFocusEvent(var5, 1005, var7.temporary, var7.component, var7.cause);
                  }

                  CausedFocusEvent var9 = new CausedFocusEvent(var7.component, 1004, var7.temporary, var5 == null ? var4 : var5, var7.cause);
                  if (var5 != null) {
                     var8.isPosted = true;
                     var3 = dispatchAndCatchException(var3, var5, var8);
                  }

                  var9.isPosted = true;
                  var3 = dispatchAndCatchException(var3, var7.component, var9);
                  if (var0.getGlobalFocusOwner() == var7.component) {
                     var4 = var7.component;
                  }
               }
            }
         } finally {
            clearingCurrentLightweightRequests = false;
            disableRestoreFocus = false;
            var1 = null;
            allowSyncFocusRequests = true;
         }

         if (var3 instanceof RuntimeException) {
            throw (RuntimeException)var3;
         } else if (var3 instanceof Error) {
            throw (Error)var3;
         }
      }
   }

   static FocusEvent retargetUnexpectedFocusEvent(FocusEvent var0) {
      synchronized(heavyweightRequests) {
         if (removeFirstRequest()) {
            return (FocusEvent)retargetFocusEvent(var0);
         } else {
            Component var2 = var0.getComponent();
            Component var3 = var0.getOppositeComponent();
            boolean var4 = false;
            if (var0.getID() == 1005 && (var3 == null || isTemporary(var3, var2))) {
               var4 = true;
            }

            return new CausedFocusEvent(var2, var0.getID(), var4, var3, CausedFocusEvent.Cause.NATIVE_SYSTEM);
         }
      }
   }

   static FocusEvent retargetFocusGained(FocusEvent var0) {
      assert var0.getID() == 1004;

      Component var1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
      Component var2 = var0.getComponent();
      Component var3 = var0.getOppositeComponent();
      Component var4 = getHeavyweight(var2);
      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var6 = getFirstHWRequest();
         if (var6 == KeyboardFocusManager.HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
            return retargetUnexpectedFocusEvent(var0);
         } else {
            if (var2 != null && var4 == null && var6 != null && var2 == var6.getFirstLightweightRequest().component) {
               var2 = var6.heavyweight;
               var4 = var2;
            }

            if (var6 != null && var4 == var6.heavyweight) {
               heavyweightRequests.removeFirst();
               KeyboardFocusManager.LightweightFocusRequest var7 = (KeyboardFocusManager.LightweightFocusRequest)var6.lightweightRequests.removeFirst();
               Component var8 = var7.component;
               if (var1 != null) {
                  newFocusOwner = var8;
               }

               boolean var9 = var3 != null && !isTemporary(var8, var3) ? var7.temporary : false;
               if (var6.lightweightRequests.size() > 0) {
                  currentLightweightRequests = var6.lightweightRequests;
                  EventQueue.invokeLater(new Runnable() {
                     public void run() {
                        KeyboardFocusManager.processCurrentLightweightRequests();
                     }
                  });
               }

               return new CausedFocusEvent(var8, 1004, var9, var3, var7.cause);
            } else {
               return (FocusEvent)(var1 == null || var1.getContainingWindow() != var2 || var6 != null && var2 == var6.heavyweight ? retargetUnexpectedFocusEvent(var0) : new CausedFocusEvent(var1, 1004, false, (Component)null, CausedFocusEvent.Cause.ACTIVATION));
            }
         }
      }
   }

   static FocusEvent retargetFocusLost(FocusEvent var0) {
      assert ((FocusEvent)var0).getID() == 1005;

      Component var1 = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
      Component var2 = ((FocusEvent)var0).getOppositeComponent();
      Component var3 = getHeavyweight(var2);
      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var5 = getFirstHWRequest();
         if (var5 == KeyboardFocusManager.HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
            if (var1 != null) {
               heavyweightRequests.removeFirst();
               return new CausedFocusEvent(var1, 1005, false, (Component)null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
            }
         } else {
            if (var2 == null) {
               if (var1 != null) {
                  return new CausedFocusEvent(var1, 1005, true, (Component)null, CausedFocusEvent.Cause.ACTIVATION);
               }

               return (FocusEvent)var0;
            }

            if (var5 != null && (var3 == var5.heavyweight || var3 == null && var2 == var5.getFirstLightweightRequest().component)) {
               if (var1 == null) {
                  return (FocusEvent)var0;
               }

               KeyboardFocusManager.LightweightFocusRequest var6 = (KeyboardFocusManager.LightweightFocusRequest)var5.lightweightRequests.getFirst();
               boolean var7 = isTemporary(var2, var1) ? true : var6.temporary;
               return new CausedFocusEvent(var1, 1005, var7, var6.component, var6.cause);
            }

            if (focusedWindowChanged(var2, var1)) {
               if (!((FocusEvent)var0).isTemporary() && var1 != null) {
                  var0 = new CausedFocusEvent(var1, 1005, true, var2, CausedFocusEvent.Cause.ACTIVATION);
               }

               return (FocusEvent)var0;
            }
         }

         return retargetUnexpectedFocusEvent((FocusEvent)var0);
      }
   }

   static AWTEvent retargetFocusEvent(AWTEvent var0) {
      if (clearingCurrentLightweightRequests) {
         return (AWTEvent)var0;
      } else {
         KeyboardFocusManager var1 = getCurrentKeyboardFocusManager();
         if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
            if (var0 instanceof FocusEvent || var0 instanceof WindowEvent) {
               focusLog.finer(">>> {0}", String.valueOf(var0));
            }

            if (focusLog.isLoggable(PlatformLogger.Level.FINER) && var0 instanceof KeyEvent) {
               focusLog.finer("    focus owner is {0}", String.valueOf((Object)var1.getGlobalFocusOwner()));
               focusLog.finer(">>> {0}", String.valueOf(var0));
            }
         }

         synchronized(heavyweightRequests) {
            if (newFocusOwner != null && ((AWTEvent)var0).getID() == 1005) {
               FocusEvent var3 = (FocusEvent)var0;
               if (var1.getGlobalFocusOwner() == var3.getComponent() && var3.getOppositeComponent() == newFocusOwner) {
                  newFocusOwner = null;
                  return (AWTEvent)var0;
               }
            }
         }

         processCurrentLightweightRequests();
         switch(((AWTEvent)var0).getID()) {
         case 1004:
            var0 = retargetFocusGained((FocusEvent)var0);
            break;
         case 1005:
            var0 = retargetFocusLost((FocusEvent)var0);
         }

         return (AWTEvent)var0;
      }
   }

   void clearMarkers() {
   }

   static boolean removeFirstRequest() {
      KeyboardFocusManager var0 = getCurrentKeyboardFocusManager();
      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var2 = getFirstHWRequest();
         if (var2 != null) {
            heavyweightRequests.removeFirst();
            if (var2.lightweightRequests != null) {
               Iterator var3 = var2.lightweightRequests.iterator();

               while(var3.hasNext()) {
                  var0.dequeueKeyEvents(-1L, ((KeyboardFocusManager.LightweightFocusRequest)var3.next()).component);
               }
            }
         }

         if (heavyweightRequests.size() == 0) {
            var0.clearMarkers();
         }

         return heavyweightRequests.size() > 0;
      }
   }

   static void removeLastFocusRequest(Component var0) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && var0 == null) {
         log.fine("Assertion (heavyweight != null) failed");
      }

      KeyboardFocusManager var1 = getCurrentKeyboardFocusManager();
      synchronized(heavyweightRequests) {
         KeyboardFocusManager.HeavyweightFocusRequest var3 = getLastHWRequest();
         if (var3 != null && var3.heavyweight == var0) {
            heavyweightRequests.removeLast();
         }

         if (heavyweightRequests.size() == 0) {
            var1.clearMarkers();
         }

      }
   }

   private static boolean focusedWindowChanged(Component var0, Component var1) {
      Window var2 = SunToolkit.getContainingWindow(var0);
      Window var3 = SunToolkit.getContainingWindow(var1);
      if (var2 == null && var3 == null) {
         return true;
      } else if (var2 == null) {
         return true;
      } else if (var3 == null) {
         return true;
      } else {
         return var2 != var3;
      }
   }

   private static boolean isTemporary(Component var0, Component var1) {
      Window var2 = SunToolkit.getContainingWindow(var0);
      Window var3 = SunToolkit.getContainingWindow(var1);
      if (var2 == null && var3 == null) {
         return false;
      } else if (var2 == null) {
         return true;
      } else if (var3 == null) {
         return false;
      } else {
         return var2 != var3;
      }
   }

   static Component getHeavyweight(Component var0) {
      if (var0 != null && var0.getPeer() != null) {
         return (Component)(var0.getPeer() instanceof LightweightPeer ? var0.getNativeContainer() : var0);
      } else {
         return null;
      }
   }

   private static boolean isProxyActiveImpl(KeyEvent var0) {
      if (proxyActive == null) {
         proxyActive = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
               Field var1 = null;

               try {
                  var1 = KeyEvent.class.getDeclaredField("isProxyActive");
                  if (var1 != null) {
                     var1.setAccessible(true);
                  }
               } catch (NoSuchFieldException var3) {
                  assert false;
               }

               return var1;
            }
         });
      }

      try {
         return proxyActive.getBoolean(var0);
      } catch (IllegalAccessException var2) {
         assert false;

         return false;
      }
   }

   static boolean isProxyActive(KeyEvent var0) {
      return !GraphicsEnvironment.isHeadless() ? isProxyActiveImpl(var0) : false;
   }

   private static KeyboardFocusManager.HeavyweightFocusRequest getLastHWRequest() {
      synchronized(heavyweightRequests) {
         return heavyweightRequests.size() > 0 ? (KeyboardFocusManager.HeavyweightFocusRequest)heavyweightRequests.getLast() : null;
      }
   }

   private static KeyboardFocusManager.HeavyweightFocusRequest getFirstHWRequest() {
      synchronized(heavyweightRequests) {
         return heavyweightRequests.size() > 0 ? (KeyboardFocusManager.HeavyweightFocusRequest)heavyweightRequests.getFirst() : null;
      }
   }

   private static void checkReplaceKFMPermission() throws SecurityException {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         if (replaceKeyboardFocusManagerPermission == null) {
            replaceKeyboardFocusManagerPermission = new AWTPermission("replaceKeyboardFocusManager");
         }

         var0.checkPermission(replaceKeyboardFocusManagerPermission);
      }

   }

   private void checkKFMSecurity() throws SecurityException {
      if (this != getCurrentKeyboardFocusManager()) {
         checkReplaceKFMPermission();
      }

   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setKeyboardFocusManagerAccessor(new AWTAccessor.KeyboardFocusManagerAccessor() {
         public int shouldNativelyFocusHeavyweight(Component var1, Component var2, boolean var3, boolean var4, long var5, CausedFocusEvent.Cause var7) {
            return KeyboardFocusManager.shouldNativelyFocusHeavyweight(var1, var2, var3, var4, var5, var7);
         }

         public boolean processSynchronousLightweightTransfer(Component var1, Component var2, boolean var3, boolean var4, long var5) {
            return KeyboardFocusManager.processSynchronousLightweightTransfer(var1, var2, var3, var4, var5);
         }

         public void removeLastFocusRequest(Component var1) {
            KeyboardFocusManager.removeLastFocusRequest(var1);
         }

         public void setMostRecentFocusOwner(Window var1, Component var2) {
            KeyboardFocusManager.setMostRecentFocusOwner(var1, var2);
         }

         public KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext var1) {
            return KeyboardFocusManager.getCurrentKeyboardFocusManager(var1);
         }

         public Container getCurrentFocusCycleRoot() {
            return KeyboardFocusManager.currentFocusCycleRoot;
         }
      });
      log = PlatformLogger.getLogger("java.awt.KeyboardFocusManager");
      defaultFocusTraversalKeyPropertyNames = new String[]{"forwardDefaultFocusTraversalKeys", "backwardDefaultFocusTraversalKeys", "upCycleDefaultFocusTraversalKeys", "downCycleDefaultFocusTraversalKeys"};
      defaultFocusTraversalKeyStrokes = new AWTKeyStroke[][]{{AWTKeyStroke.getAWTKeyStroke(9, 0, false), AWTKeyStroke.getAWTKeyStroke(9, 130, false)}, {AWTKeyStroke.getAWTKeyStroke(9, 65, false), AWTKeyStroke.getAWTKeyStroke(9, 195, false)}, new AWTKeyStroke[0], new AWTKeyStroke[0]};
      mostRecentFocusOwners = new WeakHashMap();
      heavyweightRequests = new LinkedList();
      allowSyncFocusRequests = true;
      newFocusOwner = null;
   }

   private static final class HeavyweightFocusRequest {
      final Component heavyweight;
      final LinkedList<KeyboardFocusManager.LightweightFocusRequest> lightweightRequests;
      static final KeyboardFocusManager.HeavyweightFocusRequest CLEAR_GLOBAL_FOCUS_OWNER = new KeyboardFocusManager.HeavyweightFocusRequest();

      private HeavyweightFocusRequest() {
         this.heavyweight = null;
         this.lightweightRequests = null;
      }

      HeavyweightFocusRequest(Component var1, Component var2, boolean var3, CausedFocusEvent.Cause var4) {
         if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE) && var1 == null) {
            KeyboardFocusManager.log.fine("Assertion (heavyweight != null) failed");
         }

         this.heavyweight = var1;
         this.lightweightRequests = new LinkedList();
         this.addLightweightRequest(var2, var3, var4);
      }

      boolean addLightweightRequest(Component var1, boolean var2, CausedFocusEvent.Cause var3) {
         if (KeyboardFocusManager.log.isLoggable(PlatformLogger.Level.FINE)) {
            if (this == CLEAR_GLOBAL_FOCUS_OWNER) {
               KeyboardFocusManager.log.fine("Assertion (this != HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) failed");
            }

            if (var1 == null) {
               KeyboardFocusManager.log.fine("Assertion (descendant != null) failed");
            }
         }

         Component var4 = this.lightweightRequests.size() > 0 ? ((KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getLast()).component : null;
         if (var1 != var4) {
            this.lightweightRequests.add(new KeyboardFocusManager.LightweightFocusRequest(var1, var2, var3));
            return true;
         } else {
            return false;
         }
      }

      KeyboardFocusManager.LightweightFocusRequest getFirstLightweightRequest() {
         return this == CLEAR_GLOBAL_FOCUS_OWNER ? null : (KeyboardFocusManager.LightweightFocusRequest)this.lightweightRequests.getFirst();
      }

      public String toString() {
         boolean var1 = true;
         String var2 = "HeavyweightFocusRequest[heavweight=" + this.heavyweight + ",lightweightRequests=";
         if (this.lightweightRequests == null) {
            var2 = var2 + null;
         } else {
            var2 = var2 + "[";

            KeyboardFocusManager.LightweightFocusRequest var4;
            for(Iterator var3 = this.lightweightRequests.iterator(); var3.hasNext(); var2 = var2 + var4) {
               var4 = (KeyboardFocusManager.LightweightFocusRequest)var3.next();
               if (var1) {
                  var1 = false;
               } else {
                  var2 = var2 + ",";
               }
            }

            var2 = var2 + "]";
         }

         var2 = var2 + "]";
         return var2;
      }
   }

   private static final class LightweightFocusRequest {
      final Component component;
      final boolean temporary;
      final CausedFocusEvent.Cause cause;

      LightweightFocusRequest(Component var1, boolean var2, CausedFocusEvent.Cause var3) {
         this.component = var1;
         this.temporary = var2;
         this.cause = var3;
      }

      public String toString() {
         return "LightweightFocusRequest[component=" + this.component + ",temporary=" + this.temporary + ", cause=" + this.cause + "]";
      }
   }
}
