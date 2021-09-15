package java.awt;

import java.awt.event.ComponentEvent;
import java.awt.event.InvocationEvent;
import java.awt.peer.DialogPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.util.IdentityArrayList;
import sun.awt.util.IdentityLinkedList;
import sun.security.util.SecurityConstants;

public class Dialog extends Window {
   boolean resizable;
   boolean undecorated;
   private transient boolean initialized;
   public static final Dialog.ModalityType DEFAULT_MODALITY_TYPE;
   boolean modal;
   Dialog.ModalityType modalityType;
   static transient IdentityArrayList<Dialog> modalDialogs;
   transient IdentityArrayList<Window> blockedWindows;
   String title;
   private transient ModalEventFilter modalFilter;
   private transient volatile SecondaryLoop secondaryLoop;
   transient volatile boolean isInHide;
   transient volatile boolean isInDispose;
   private static final String base = "dialog";
   private static int nameCounter;
   private static final long serialVersionUID = 5920926903803293709L;

   public Dialog(Frame var1) {
      this(var1, "", false);
   }

   public Dialog(Frame var1, boolean var2) {
      this(var1, "", var2);
   }

   public Dialog(Frame var1, String var2) {
      this(var1, var2, false);
   }

   public Dialog(Frame var1, String var2, boolean var3) {
      this(var1, var2, var3 ? DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS);
   }

   public Dialog(Frame var1, String var2, boolean var3, GraphicsConfiguration var4) {
      this(var1, var2, var3 ? DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS, var4);
   }

   public Dialog(Dialog var1) {
      this(var1, "", false);
   }

   public Dialog(Dialog var1, String var2) {
      this(var1, var2, false);
   }

   public Dialog(Dialog var1, String var2, boolean var3) {
      this(var1, var2, var3 ? DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS);
   }

   public Dialog(Dialog var1, String var2, boolean var3, GraphicsConfiguration var4) {
      this(var1, var2, var3 ? DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS, var4);
   }

   public Dialog(Window var1) {
      this(var1, "", Dialog.ModalityType.MODELESS);
   }

   public Dialog(Window var1, String var2) {
      this(var1, var2, Dialog.ModalityType.MODELESS);
   }

   public Dialog(Window var1, Dialog.ModalityType var2) {
      this(var1, "", var2);
   }

   public Dialog(Window var1, String var2, Dialog.ModalityType var3) {
      super(var1);
      this.resizable = true;
      this.undecorated = false;
      this.initialized = false;
      this.blockedWindows = new IdentityArrayList();
      this.isInHide = false;
      this.isInDispose = false;
      if (var1 != null && !(var1 instanceof Frame) && !(var1 instanceof Dialog)) {
         throw new IllegalArgumentException("Wrong parent window");
      } else {
         this.title = var2;
         this.setModalityType(var3);
         SunToolkit.checkAndSetPolicy(this);
         this.initialized = true;
      }
   }

   public Dialog(Window var1, String var2, Dialog.ModalityType var3, GraphicsConfiguration var4) {
      super(var1, var4);
      this.resizable = true;
      this.undecorated = false;
      this.initialized = false;
      this.blockedWindows = new IdentityArrayList();
      this.isInHide = false;
      this.isInDispose = false;
      if (var1 != null && !(var1 instanceof Frame) && !(var1 instanceof Dialog)) {
         throw new IllegalArgumentException("wrong owner window");
      } else {
         this.title = var2;
         this.setModalityType(var3);
         SunToolkit.checkAndSetPolicy(this);
         this.initialized = true;
      }
   }

   String constructComponentName() {
      Class var1 = Dialog.class;
      synchronized(Dialog.class) {
         return "dialog" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.parent != null && this.parent.getPeer() == null) {
            this.parent.addNotify();
         }

         if (this.peer == null) {
            this.peer = this.getToolkit().createDialog(this);
         }

         super.addNotify();
      }
   }

   public boolean isModal() {
      return this.isModal_NoClientCode();
   }

   final boolean isModal_NoClientCode() {
      return this.modalityType != Dialog.ModalityType.MODELESS;
   }

   public void setModal(boolean var1) {
      this.modal = var1;
      this.setModalityType(var1 ? DEFAULT_MODALITY_TYPE : Dialog.ModalityType.MODELESS);
   }

   public Dialog.ModalityType getModalityType() {
      return this.modalityType;
   }

   public void setModalityType(Dialog.ModalityType var1) {
      if (var1 == null) {
         var1 = Dialog.ModalityType.MODELESS;
      }

      if (!Toolkit.getDefaultToolkit().isModalityTypeSupported(var1)) {
         var1 = Dialog.ModalityType.MODELESS;
      }

      if (this.modalityType != var1) {
         this.checkModalityPermission(var1);
         this.modalityType = var1;
         this.modal = this.modalityType != Dialog.ModalityType.MODELESS;
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      String var2 = this.title;
      synchronized(this) {
         this.title = var1;
         DialogPeer var4 = (DialogPeer)this.peer;
         if (var4 != null) {
            var4.setTitle(var1);
         }
      }

      this.firePropertyChange("title", var2, var1);
   }

   private boolean conditionalShow(Component var1, AtomicLong var2) {
      this.closeSplashScreen();
      boolean var3;
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.addNotify();
         }

         this.validateUnconditionally();
         if (this.visible) {
            this.toFront();
            var3 = false;
         } else {
            var3 = true;
            this.visible = true;
            if (!this.isModal()) {
               checkShouldBeBlocked(this);
            } else {
               modalDialogs.add(this);
               this.modalShow();
            }

            if (var1 != null && var2 != null && this.isFocusable() && this.isEnabled() && !this.isModalBlocked()) {
               var2.set(Toolkit.getEventQueue().getMostRecentKeyEventTime());
               KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(var2.get(), var1);
            }

            this.mixOnShowing();
            this.peer.setVisible(true);
            if (this.isModalBlocked()) {
               this.modalBlocker.toFront();
            }

            this.setLocationByPlatform(false);

            for(int var5 = 0; var5 < this.ownedWindowList.size(); ++var5) {
               Window var6 = (Window)((WeakReference)this.ownedWindowList.elementAt(var5)).get();
               if (var6 != null && var6.showWithParent) {
                  var6.show();
                  var6.showWithParent = false;
               }
            }

            Window.updateChildFocusableWindowState(this);
            this.createHierarchyEvents(1400, this, this.parent, 4L, Toolkit.enabledOnToolkit(32768L));
            if (this.componentListener != null || (this.eventMask & 1L) != 0L || Toolkit.enabledOnToolkit(1L)) {
               ComponentEvent var9 = new ComponentEvent(this, 102);
               Toolkit.getEventQueue().postEvent(var9);
            }
         }
      }

      if (var3 && (this.state & 1) == 0) {
         this.postWindowEvent(200);
         this.state |= 1;
      }

      return var3;
   }

   public void setVisible(boolean var1) {
      super.setVisible(var1);
   }

   /** @deprecated */
   @Deprecated
   public void show() {
      if (!this.initialized) {
         throw new IllegalStateException("The dialog component has not been initialized properly");
      } else {
         this.beforeFirstShow = false;
         if (!this.isModal()) {
            this.conditionalShow((Component)null, (AtomicLong)null);
         } else {
            AppContext var1 = AppContext.getAppContext();
            AtomicLong var2 = new AtomicLong();
            Component var3 = null;

            try {
               var3 = this.getMostRecentFocusOwner();
               if (this.conditionalShow(var3, var2)) {
                  this.modalFilter = ModalEventFilter.createFilterForDialog(this);
                  Conditional var4 = new Conditional() {
                     public boolean evaluate() {
                        return Dialog.this.windowClosingException == null;
                     }
                  };
                  Iterator var5;
                  AppContext var6;
                  EventQueue var7;
                  if (this.modalityType == Dialog.ModalityType.TOOLKIT_MODAL) {
                     var5 = AppContext.getAppContexts().iterator();

                     while(var5.hasNext()) {
                        var6 = (AppContext)var5.next();
                        if (var6 != var1) {
                           var7 = (EventQueue)var6.get(AppContext.EVENT_QUEUE_KEY);
                           Runnable var8 = new Runnable() {
                              public void run() {
                              }
                           };
                           var7.postEvent(new InvocationEvent(this, var8));
                           EventDispatchThread var9 = var7.getDispatchThread();
                           var9.addEventFilter(this.modalFilter);
                        }
                     }
                  }

                  this.modalityPushed();

                  try {
                     EventQueue var18 = (EventQueue)AccessController.doPrivileged(new PrivilegedAction<EventQueue>() {
                        public EventQueue run() {
                           return Toolkit.getDefaultToolkit().getSystemEventQueue();
                        }
                     });
                     this.secondaryLoop = var18.createSecondaryLoop(var4, this.modalFilter, 0L);
                     if (!this.secondaryLoop.enter()) {
                        this.secondaryLoop = null;
                     }
                  } finally {
                     this.modalityPopped();
                  }

                  if (this.modalityType == Dialog.ModalityType.TOOLKIT_MODAL) {
                     var5 = AppContext.getAppContexts().iterator();

                     while(var5.hasNext()) {
                        var6 = (AppContext)var5.next();
                        if (var6 != var1) {
                           var7 = (EventQueue)var6.get(AppContext.EVENT_QUEUE_KEY);
                           EventDispatchThread var19 = var7.getDispatchThread();
                           var19.removeEventFilter(this.modalFilter);
                        }
                     }
                  }

                  if (this.windowClosingException != null) {
                     this.windowClosingException.fillInStackTrace();
                     throw this.windowClosingException;
                  }
               }
            } finally {
               if (var3 != null) {
                  KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(var2.get(), var3);
               }

            }
         }

      }
   }

   final void modalityPushed() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      if (var1 instanceof SunToolkit) {
         SunToolkit var2 = (SunToolkit)var1;
         var2.notifyModalityPushed(this);
      }

   }

   final void modalityPopped() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      if (var1 instanceof SunToolkit) {
         SunToolkit var2 = (SunToolkit)var1;
         var2.notifyModalityPopped(this);
      }

   }

   void interruptBlocking() {
      if (this.isModal()) {
         this.disposeImpl();
      } else if (this.windowClosingException != null) {
         this.windowClosingException.fillInStackTrace();
         this.windowClosingException.printStackTrace();
         this.windowClosingException = null;
      }

   }

   private void hideAndDisposePreHandler() {
      this.isInHide = true;
      synchronized(this.getTreeLock()) {
         if (this.secondaryLoop != null) {
            this.modalHide();
            if (this.modalFilter != null) {
               this.modalFilter.disable();
            }

            modalDialogs.remove(this);
         }

      }
   }

   private void hideAndDisposeHandler() {
      if (this.secondaryLoop != null) {
         this.secondaryLoop.exit();
         this.secondaryLoop = null;
      }

      this.isInHide = false;
   }

   /** @deprecated */
   @Deprecated
   public void hide() {
      this.hideAndDisposePreHandler();
      super.hide();
      if (!this.isInDispose) {
         this.hideAndDisposeHandler();
      }

   }

   void doDispose() {
      this.isInDispose = true;
      super.doDispose();
      this.hideAndDisposeHandler();
      this.isInDispose = false;
   }

   public void toBack() {
      super.toBack();
      if (this.visible) {
         synchronized(this.getTreeLock()) {
            Iterator var2 = this.blockedWindows.iterator();

            while(var2.hasNext()) {
               Window var3 = (Window)var2.next();
               var3.toBack_NoClientCode();
            }
         }
      }

   }

   public boolean isResizable() {
      return this.resizable;
   }

   public void setResizable(boolean var1) {
      boolean var2 = false;
      synchronized(this) {
         this.resizable = var1;
         DialogPeer var4 = (DialogPeer)this.peer;
         if (var4 != null) {
            var4.setResizable(var1);
            var2 = true;
         }
      }

      if (var2) {
         this.invalidateIfValid();
      }

   }

   public void setUndecorated(boolean var1) {
      synchronized(this.getTreeLock()) {
         if (this.isDisplayable()) {
            throw new IllegalComponentStateException("The dialog is displayable.");
         } else {
            if (!var1) {
               if (this.getOpacity() < 1.0F) {
                  throw new IllegalComponentStateException("The dialog is not opaque");
               }

               if (this.getShape() != null) {
                  throw new IllegalComponentStateException("The dialog does not have a default shape");
               }

               Color var3 = this.getBackground();
               if (var3 != null && var3.getAlpha() < 255) {
                  throw new IllegalComponentStateException("The dialog background color is not opaque");
               }
            }

            this.undecorated = var1;
         }
      }
   }

   public boolean isUndecorated() {
      return this.undecorated;
   }

   public void setOpacity(float var1) {
      synchronized(this.getTreeLock()) {
         if (var1 < 1.0F && !this.isUndecorated()) {
            throw new IllegalComponentStateException("The dialog is decorated");
         } else {
            super.setOpacity(var1);
         }
      }
   }

   public void setShape(Shape var1) {
      synchronized(this.getTreeLock()) {
         if (var1 != null && !this.isUndecorated()) {
            throw new IllegalComponentStateException("The dialog is decorated");
         } else {
            super.setShape(var1);
         }
      }
   }

   public void setBackground(Color var1) {
      synchronized(this.getTreeLock()) {
         if (var1 != null && var1.getAlpha() < 255 && !this.isUndecorated()) {
            throw new IllegalComponentStateException("The dialog is decorated");
         } else {
            super.setBackground(var1);
         }
      }
   }

   protected String paramString() {
      String var1 = super.paramString() + "," + this.modalityType;
      if (this.title != null) {
         var1 = var1 + ",title=" + this.title;
      }

      return var1;
   }

   private static native void initIDs();

   void modalShow() {
      IdentityArrayList var1 = new IdentityArrayList();
      Iterator var2 = modalDialogs.iterator();

      while(true) {
         Dialog var3;
         Object var4;
         do {
            do {
               if (!var2.hasNext()) {
                  for(int var10 = 0; var10 < var1.size(); ++var10) {
                     var3 = (Dialog)var1.get(var10);
                     if (var3.isModalBlocked()) {
                        Dialog var13 = var3.getModalBlocker();
                        if (!var1.contains(var13)) {
                           var1.add(var10 + 1, var13);
                        }
                     }
                  }

                  if (var1.size() > 0) {
                     ((Dialog)var1.get(0)).blockWindow(this);
                  }

                  IdentityArrayList var11 = new IdentityArrayList(var1);

                  for(int var12 = 0; var12 < var11.size(); ++var12) {
                     Window var14 = (Window)var11.get(var12);
                     Window[] var5 = var14.getOwnedWindows_NoClientCode();
                     Window[] var6 = var5;
                     int var7 = var5.length;

                     for(int var8 = 0; var8 < var7; ++var8) {
                        Window var9 = var6[var8];
                        var11.add(var9);
                     }
                  }

                  IdentityLinkedList var15 = new IdentityLinkedList();
                  IdentityArrayList var16 = Window.getAllUnblockedWindows();
                  Iterator var17 = var16.iterator();

                  while(true) {
                     Window var18;
                     Dialog var19;
                     do {
                        do {
                           do {
                              if (!var17.hasNext()) {
                                 this.blockWindows(var15);
                                 if (!this.isModalBlocked()) {
                                    this.updateChildrenBlocking();
                                 }

                                 return;
                              }

                              var18 = (Window)var17.next();
                           } while(!this.shouldBlock(var18));
                        } while(var11.contains(var18));

                        if (!(var18 instanceof Dialog) || !((Dialog)var18).isModal_NoClientCode()) {
                           break;
                        }

                        var19 = (Dialog)var18;
                     } while(var19.shouldBlock(this) && modalDialogs.indexOf(var19) > modalDialogs.indexOf(this));

                     var15.add(var18);
                  }
               }

               var3 = (Dialog)var2.next();
            } while(!var3.shouldBlock(this));

            for(var4 = var3; var4 != null && var4 != this; var4 = ((Window)var4).getOwner_NoClientCode()) {
            }
         } while(var4 != this && this.shouldBlock(var3) && this.modalityType.compareTo(var3.getModalityType()) >= 0);

         var1.add(var3);
      }
   }

   void modalHide() {
      IdentityArrayList var1 = new IdentityArrayList();
      int var2 = this.blockedWindows.size();

      int var3;
      Window var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = (Window)this.blockedWindows.get(0);
         var1.add(var4);
         this.unblockWindow(var4);
      }

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = (Window)var1.get(var3);
         if (var4 instanceof Dialog && ((Dialog)var4).isModal_NoClientCode()) {
            Dialog var5 = (Dialog)var4;
            var5.modalShow();
         } else {
            checkShouldBeBlocked(var4);
         }
      }

   }

   boolean shouldBlock(Window var1) {
      if (this.isVisible_NoClientCode() && (var1.isVisible_NoClientCode() || var1.isInShow) && !this.isInHide && var1 != this && this.isModal_NoClientCode()) {
         if (var1 instanceof Dialog && ((Dialog)var1).isInHide) {
            return false;
         } else {
            Object var3;
            for(Dialog var2 = this; var2 != null; var2 = var2.getModalBlocker()) {
               for(var3 = var1; var3 != null && var3 != var2; var3 = ((Component)var3).getParent_NoClientCode()) {
               }

               if (var3 == var2) {
                  return false;
               }
            }

            switch(this.modalityType) {
            case MODELESS:
               return false;
            case DOCUMENT_MODAL:
               if (!var1.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE)) {
                  return this.getDocumentRoot() == var1.getDocumentRoot();
               }

               for(var3 = this; var3 != null && var3 != var1; var3 = ((Component)var3).getParent_NoClientCode()) {
               }

               return var3 == var1;
            case APPLICATION_MODAL:
               return !var1.isModalExcluded(Dialog.ModalExclusionType.APPLICATION_EXCLUDE) && this.appContext == var1.appContext;
            case TOOLKIT_MODAL:
               return !var1.isModalExcluded(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
            default:
               return false;
            }
         }
      } else {
         return false;
      }
   }

   void blockWindow(Window var1) {
      if (!var1.isModalBlocked()) {
         var1.setModalBlocked(this, true, true);
         this.blockedWindows.add(var1);
      }

   }

   void blockWindows(java.util.List<Window> var1) {
      DialogPeer var2 = (DialogPeer)this.peer;
      if (var2 != null) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Window var4 = (Window)var3.next();
            if (!var4.isModalBlocked()) {
               var4.setModalBlocked(this, true, false);
            } else {
               var3.remove();
            }
         }

         var2.blockWindows(var1);
         this.blockedWindows.addAll(var1);
      }
   }

   void unblockWindow(Window var1) {
      if (var1.isModalBlocked() && this.blockedWindows.contains(var1)) {
         this.blockedWindows.remove(var1);
         var1.setModalBlocked(this, false, true);
      }

   }

   static void checkShouldBeBlocked(Window var0) {
      synchronized(var0.getTreeLock()) {
         for(int var2 = 0; var2 < modalDialogs.size(); ++var2) {
            Dialog var3 = (Dialog)modalDialogs.get(var2);
            if (var3.shouldBlock(var0)) {
               var3.blockWindow(var0);
               break;
            }
         }

      }
   }

   private void checkModalityPermission(Dialog.ModalityType var1) {
      if (var1 == Dialog.ModalityType.TOOLKIT_MODAL) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(SecurityConstants.AWT.TOOLKIT_MODALITY_PERMISSION);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      ObjectInputStream.GetField var2 = var1.readFields();
      Dialog.ModalityType var3 = (Dialog.ModalityType)var2.get("modalityType", (Object)null);

      try {
         this.checkModalityPermission(var3);
      } catch (AccessControlException var5) {
         var3 = DEFAULT_MODALITY_TYPE;
      }

      if (var3 == null) {
         this.modal = var2.get("modal", false);
         this.setModal(this.modal);
      } else {
         this.modalityType = var3;
      }

      this.resizable = var2.get("resizable", true);
      this.undecorated = var2.get("undecorated", false);
      this.title = (String)var2.get("title", "");
      this.blockedWindows = new IdentityArrayList();
      SunToolkit.checkAndSetPolicy(this);
      this.initialized = true;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Dialog.AccessibleAWTDialog();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      DEFAULT_MODALITY_TYPE = Dialog.ModalityType.APPLICATION_MODAL;
      modalDialogs = new IdentityArrayList();
      nameCounter = 0;
   }

   protected class AccessibleAWTDialog extends Window.AccessibleAWTWindow {
      private static final long serialVersionUID = 4837230331833941201L;

      protected AccessibleAWTDialog() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.DIALOG;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (Dialog.this.getFocusOwner() != null) {
            var1.add(AccessibleState.ACTIVE);
         }

         if (Dialog.this.isModal()) {
            var1.add(AccessibleState.MODAL);
         }

         if (Dialog.this.isResizable()) {
            var1.add(AccessibleState.RESIZABLE);
         }

         return var1;
      }
   }

   public static enum ModalExclusionType {
      NO_EXCLUDE,
      APPLICATION_EXCLUDE,
      TOOLKIT_EXCLUDE;
   }

   public static enum ModalityType {
      MODELESS,
      DOCUMENT_MODAL,
      APPLICATION_MODAL,
      TOOLKIT_MODAL;
   }
}
