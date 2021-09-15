package sun.awt.im;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.im.spi.InputMethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.awt.AppContext;
import sun.awt.InputMethodSupport;
import sun.awt.SunToolkit;

class ExecutableInputMethodManager extends InputMethodManager implements Runnable {
   private InputContext currentInputContext;
   private String triggerMenuString;
   private InputMethodPopupMenu selectionMenu;
   private static String selectInputMethodMenuTitle;
   private InputMethodLocator hostAdapterLocator;
   private int javaInputMethodCount;
   private Vector<InputMethodLocator> javaInputMethodLocatorList;
   private Component requestComponent;
   private InputContext requestInputContext;
   private static final String preferredIMNode = "/sun/awt/im/preferredInputMethod";
   private static final String descriptorKey = "descriptor";
   private Hashtable<String, InputMethodLocator> preferredLocatorCache = new Hashtable();
   private Preferences userRoot;

   ExecutableInputMethodManager() {
      Toolkit var1 = Toolkit.getDefaultToolkit();

      try {
         if (var1 instanceof InputMethodSupport) {
            InputMethodDescriptor var2 = ((InputMethodSupport)var1).getInputMethodAdapterDescriptor();
            if (var2 != null) {
               this.hostAdapterLocator = new InputMethodLocator(var2, (ClassLoader)null, (Locale)null);
            }
         }
      } catch (AWTException var3) {
      }

      this.javaInputMethodLocatorList = new Vector();
      this.initializeInputMethodLocatorList();
   }

   synchronized void initialize() {
      selectInputMethodMenuTitle = Toolkit.getProperty("AWT.InputMethodSelectionMenu", "Select Input Method");
      this.triggerMenuString = selectInputMethodMenuTitle;
   }

   public void run() {
      while(!this.hasMultipleInputMethods()) {
         try {
            synchronized(this) {
               this.wait();
            }
         } catch (InterruptedException var4) {
         }
      }

      while(true) {
         this.waitForChangeRequest();
         this.initializeInputMethodLocatorList();

         try {
            if (this.requestComponent != null) {
               this.showInputMethodMenuOnRequesterEDT(this.requestComponent);
            } else {
               EventQueue.invokeAndWait(new Runnable() {
                  public void run() {
                     ExecutableInputMethodManager.this.showInputMethodMenu();
                  }
               });
            }
         } catch (InterruptedException var5) {
         } catch (InvocationTargetException var6) {
         }
      }
   }

   private void showInputMethodMenuOnRequesterEDT(Component var1) throws InterruptedException, InvocationTargetException {
      if (var1 != null) {
         class AWTInvocationLock {
         }

         AWTInvocationLock var2 = new AWTInvocationLock();
         InvocationEvent var3 = new InvocationEvent(var1, new Runnable() {
            public void run() {
               ExecutableInputMethodManager.this.showInputMethodMenu();
            }
         }, var2, true);
         AppContext var4 = SunToolkit.targetToAppContext(var1);
         synchronized(var2) {
            SunToolkit.postEvent(var4, var3);

            while(true) {
               if (var3.isDispatched()) {
                  break;
               }

               var2.wait();
            }
         }

         Throwable var5 = var3.getThrowable();
         if (var5 != null) {
            throw new InvocationTargetException(var5);
         }
      }
   }

   void setInputContext(InputContext var1) {
      if (this.currentInputContext != null && var1 != null) {
      }

      this.currentInputContext = var1;
   }

   public synchronized void notifyChangeRequest(Component var1) {
      if (var1 instanceof Frame || var1 instanceof Dialog) {
         if (this.requestComponent == null) {
            this.requestComponent = var1;
            this.notify();
         }
      }
   }

   public synchronized void notifyChangeRequestByHotKey(Component var1) {
      while(!(var1 instanceof Frame) && !(var1 instanceof Dialog)) {
         if (var1 == null) {
            return;
         }

         var1 = ((Component)var1).getParent();
      }

      this.notifyChangeRequest((Component)var1);
   }

   public String getTriggerMenuString() {
      return this.triggerMenuString;
   }

   boolean hasMultipleInputMethods() {
      return this.hostAdapterLocator != null && this.javaInputMethodCount > 0 || this.javaInputMethodCount > 1;
   }

   private synchronized void waitForChangeRequest() {
      while(true) {
         try {
            if (this.requestComponent == null) {
               this.wait();
               continue;
            }
         } catch (InterruptedException var2) {
         }

         return;
      }
   }

   private void initializeInputMethodLocatorList() {
      synchronized(this.javaInputMethodLocatorList) {
         this.javaInputMethodLocatorList.clear();

         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() {
                  Iterator var1 = ServiceLoader.loadInstalled(InputMethodDescriptor.class).iterator();

                  while(var1.hasNext()) {
                     InputMethodDescriptor var2 = (InputMethodDescriptor)var1.next();
                     ClassLoader var3 = var2.getClass().getClassLoader();
                     ExecutableInputMethodManager.this.javaInputMethodLocatorList.add(new InputMethodLocator(var2, var3, (Locale)null));
                  }

                  return null;
               }
            });
         } catch (PrivilegedActionException var4) {
            var4.printStackTrace();
         }

         this.javaInputMethodCount = this.javaInputMethodLocatorList.size();
      }

      if (this.hasMultipleInputMethods()) {
         if (this.userRoot == null) {
            this.userRoot = this.getUserRoot();
         }
      } else {
         this.triggerMenuString = null;
      }

   }

   private void showInputMethodMenu() {
      if (!this.hasMultipleInputMethods()) {
         this.requestComponent = null;
      } else {
         this.selectionMenu = InputMethodPopupMenu.getInstance(this.requestComponent, selectInputMethodMenuTitle);
         this.selectionMenu.removeAll();
         String var1 = this.getCurrentSelection();
         if (this.hostAdapterLocator != null) {
            this.selectionMenu.addOneInputMethodToMenu(this.hostAdapterLocator, var1);
            this.selectionMenu.addSeparator();
         }

         for(int var2 = 0; var2 < this.javaInputMethodLocatorList.size(); ++var2) {
            InputMethodLocator var3 = (InputMethodLocator)this.javaInputMethodLocatorList.get(var2);
            this.selectionMenu.addOneInputMethodToMenu(var3, var1);
         }

         synchronized(this) {
            this.selectionMenu.addToComponent(this.requestComponent);
            this.requestInputContext = this.currentInputContext;
            this.selectionMenu.show(this.requestComponent, 60, 80);
            this.requestComponent = null;
         }
      }
   }

   private String getCurrentSelection() {
      InputContext var1 = this.currentInputContext;
      if (var1 != null) {
         InputMethodLocator var2 = var1.getInputMethodLocator();
         if (var2 != null) {
            return var2.getActionCommandString();
         }
      }

      return null;
   }

   synchronized void changeInputMethod(String var1) {
      InputMethodLocator var2 = null;
      String var3 = var1;
      String var4 = null;
      int var5 = var1.indexOf(10);
      if (var5 != -1) {
         var4 = var1.substring(var5 + 1);
         var3 = var1.substring(0, var5);
      }

      String var8;
      if (this.hostAdapterLocator.getActionCommandString().equals(var3)) {
         var2 = this.hostAdapterLocator;
      } else {
         for(int var6 = 0; var6 < this.javaInputMethodLocatorList.size(); ++var6) {
            InputMethodLocator var7 = (InputMethodLocator)this.javaInputMethodLocatorList.get(var6);
            var8 = var7.getActionCommandString();
            if (var8.equals(var3)) {
               var2 = var7;
               break;
            }
         }
      }

      if (var2 != null && var4 != null) {
         String var11 = "";
         String var12 = "";
         var8 = "";
         int var9 = var4.indexOf(95);
         if (var9 == -1) {
            var11 = var4;
         } else {
            var11 = var4.substring(0, var9);
            int var10 = var9 + 1;
            var9 = var4.indexOf(95, var10);
            if (var9 == -1) {
               var12 = var4.substring(var10);
            } else {
               var12 = var4.substring(var10, var9);
               var8 = var4.substring(var9 + 1);
            }
         }

         Locale var13 = new Locale(var11, var12, var8);
         var2 = var2.deriveLocator(var13);
      }

      if (var2 != null) {
         if (this.requestInputContext != null) {
            this.requestInputContext.changeInputMethod(var2);
            this.requestInputContext = null;
            this.putPreferredInputMethod(var2);
         }

      }
   }

   InputMethodLocator findInputMethod(Locale var1) {
      InputMethodLocator var2 = this.getPreferredInputMethod(var1);
      if (var2 != null) {
         return var2;
      } else if (this.hostAdapterLocator != null && this.hostAdapterLocator.isLocaleAvailable(var1)) {
         return this.hostAdapterLocator.deriveLocator(var1);
      } else {
         this.initializeInputMethodLocatorList();

         for(int var3 = 0; var3 < this.javaInputMethodLocatorList.size(); ++var3) {
            InputMethodLocator var4 = (InputMethodLocator)this.javaInputMethodLocatorList.get(var3);
            if (var4.isLocaleAvailable(var1)) {
               return var4.deriveLocator(var1);
            }
         }

         return null;
      }
   }

   Locale getDefaultKeyboardLocale() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      return var1 instanceof InputMethodSupport ? ((InputMethodSupport)var1).getDefaultKeyboardLocale() : Locale.getDefault();
   }

   private synchronized InputMethodLocator getPreferredInputMethod(Locale var1) {
      InputMethodLocator var2 = null;
      if (!this.hasMultipleInputMethods()) {
         return null;
      } else {
         var2 = (InputMethodLocator)this.preferredLocatorCache.get(var1.toString().intern());
         if (var2 != null) {
            return var2;
         } else {
            String var3 = this.findPreferredInputMethodNode(var1);
            String var4 = this.readPreferredInputMethod(var3);
            if (var4 != null) {
               Locale var5;
               if (this.hostAdapterLocator != null && this.hostAdapterLocator.getDescriptor().getClass().getName().equals(var4)) {
                  var5 = this.getAdvertisedLocale(this.hostAdapterLocator, var1);
                  if (var5 != null) {
                     var2 = this.hostAdapterLocator.deriveLocator(var5);
                     this.preferredLocatorCache.put(var1.toString().intern(), var2);
                  }

                  return var2;
               }

               for(int var6 = 0; var6 < this.javaInputMethodLocatorList.size(); ++var6) {
                  InputMethodLocator var7 = (InputMethodLocator)this.javaInputMethodLocatorList.get(var6);
                  InputMethodDescriptor var8 = var7.getDescriptor();
                  if (var8.getClass().getName().equals(var4)) {
                     var5 = this.getAdvertisedLocale(var7, var1);
                     if (var5 != null) {
                        var2 = var7.deriveLocator(var5);
                        this.preferredLocatorCache.put(var1.toString().intern(), var2);
                     }

                     return var2;
                  }
               }

               this.writePreferredInputMethod(var3, (String)null);
            }

            return null;
         }
      }
   }

   private String findPreferredInputMethodNode(Locale var1) {
      if (this.userRoot == null) {
         return null;
      } else {
         for(String var2 = "/sun/awt/im/preferredInputMethod/" + this.createLocalePath(var1); !var2.equals("/sun/awt/im/preferredInputMethod"); var2 = var2.substring(0, var2.lastIndexOf(47))) {
            try {
               if (this.userRoot.nodeExists(var2) && this.readPreferredInputMethod(var2) != null) {
                  return var2;
               }
            } catch (BackingStoreException var4) {
            }
         }

         return null;
      }
   }

   private String readPreferredInputMethod(String var1) {
      return this.userRoot != null && var1 != null ? this.userRoot.node(var1).get("descriptor", (String)null) : null;
   }

   private synchronized void putPreferredInputMethod(InputMethodLocator var1) {
      InputMethodDescriptor var2 = var1.getDescriptor();
      Locale var3 = var1.getLocale();
      if (var3 == null) {
         try {
            Locale[] var4 = var2.getAvailableLocales();
            if (var4.length != 1) {
               return;
            }

            var3 = var4[0];
         } catch (AWTException var5) {
            return;
         }
      }

      if (var3.equals(Locale.JAPAN)) {
         var3 = Locale.JAPANESE;
      }

      if (var3.equals(Locale.KOREA)) {
         var3 = Locale.KOREAN;
      }

      if (var3.equals(new Locale("th", "TH"))) {
         var3 = new Locale("th");
      }

      String var6 = "/sun/awt/im/preferredInputMethod/" + this.createLocalePath(var3);
      this.writePreferredInputMethod(var6, var2.getClass().getName());
      this.preferredLocatorCache.put(var3.toString().intern(), var1.deriveLocator(var3));
   }

   private String createLocalePath(Locale var1) {
      String var2 = var1.getLanguage();
      String var3 = var1.getCountry();
      String var4 = var1.getVariant();
      String var5 = null;
      if (!var4.equals("")) {
         var5 = "_" + var2 + "/_" + var3 + "/_" + var4;
      } else if (!var3.equals("")) {
         var5 = "_" + var2 + "/_" + var3;
      } else {
         var5 = "_" + var2;
      }

      return var5;
   }

   private void writePreferredInputMethod(String var1, String var2) {
      if (this.userRoot != null) {
         Preferences var3 = this.userRoot.node(var1);
         if (var2 != null) {
            var3.put("descriptor", var2);
         } else {
            var3.remove("descriptor");
         }
      }

   }

   private Preferences getUserRoot() {
      return (Preferences)AccessController.doPrivileged(new PrivilegedAction<Preferences>() {
         public Preferences run() {
            return Preferences.userRoot();
         }
      });
   }

   private Locale getAdvertisedLocale(InputMethodLocator var1, Locale var2) {
      Locale var3 = null;
      if (var1.isLocaleAvailable(var2)) {
         var3 = var2;
      } else if (var2.getLanguage().equals("ja")) {
         if (var1.isLocaleAvailable(Locale.JAPAN)) {
            var3 = Locale.JAPAN;
         } else if (var1.isLocaleAvailable(Locale.JAPANESE)) {
            var3 = Locale.JAPANESE;
         }
      } else if (var2.getLanguage().equals("ko")) {
         if (var1.isLocaleAvailable(Locale.KOREA)) {
            var3 = Locale.KOREA;
         } else if (var1.isLocaleAvailable(Locale.KOREAN)) {
            var3 = Locale.KOREAN;
         }
      } else if (var2.getLanguage().equals("th")) {
         if (var1.isLocaleAvailable(new Locale("th", "TH"))) {
            var3 = new Locale("th", "TH");
         } else if (var1.isLocaleAvailable(new Locale("th"))) {
            var3 = new Locale("th");
         }
      }

      return var3;
   }
}
