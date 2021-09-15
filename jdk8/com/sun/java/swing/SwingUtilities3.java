package com.sun.java.swing;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import sun.awt.AppContext;
import sun.awt.EventQueueDelegate;
import sun.awt.SunToolkit;

public class SwingUtilities3 {
   private static final Object DELEGATE_REPAINT_MANAGER_KEY = new StringBuilder("DelegateRepaintManagerKey");
   private static final Map<Container, Boolean> vsyncedMap = Collections.synchronizedMap(new WeakHashMap());

   public static void setDelegateRepaintManager(JComponent var0, RepaintManager var1) {
      AppContext.getAppContext().put(DELEGATE_REPAINT_MANAGER_KEY, Boolean.TRUE);
      var0.putClientProperty(DELEGATE_REPAINT_MANAGER_KEY, var1);
   }

   public static void setVsyncRequested(Container var0, boolean var1) {
      assert var0 instanceof Applet || var0 instanceof Window;

      if (var1) {
         vsyncedMap.put(var0, Boolean.TRUE);
      } else {
         vsyncedMap.remove(var0);
      }

   }

   public static boolean isVsyncRequested(Container var0) {
      assert var0 instanceof Applet || var0 instanceof Window;

      return Boolean.TRUE == vsyncedMap.get(var0);
   }

   public static RepaintManager getDelegateRepaintManager(Component var0) {
      RepaintManager var1 = null;
      if (Boolean.TRUE == SunToolkit.targetToAppContext(var0).get(DELEGATE_REPAINT_MANAGER_KEY)) {
         while(var1 == null && var0 != null) {
            while(var0 != null && !(var0 instanceof JComponent)) {
               var0 = ((Component)var0).getParent();
            }

            if (var0 != null) {
               var1 = (RepaintManager)((JComponent)var0).getClientProperty(DELEGATE_REPAINT_MANAGER_KEY);
               var0 = ((Component)var0).getParent();
            }
         }
      }

      return var1;
   }

   public static void setEventQueueDelegate(Map<String, Map<String, Object>> var0) {
      EventQueueDelegate.setDelegate(new SwingUtilities3.EventQueueDelegateFromMap(var0));
   }

   private static class EventQueueDelegateFromMap implements EventQueueDelegate.Delegate {
      private final AWTEvent[] afterDispatchEventArgument;
      private final Object[] afterDispatchHandleArgument;
      private final Callable<Void> afterDispatchCallable;
      private final AWTEvent[] beforeDispatchEventArgument;
      private final Callable<Object> beforeDispatchCallable;
      private final EventQueue[] getNextEventEventQueueArgument;
      private final Callable<AWTEvent> getNextEventCallable;

      public EventQueueDelegateFromMap(Map<String, Map<String, Object>> var1) {
         Map var2 = (Map)var1.get("afterDispatch");
         this.afterDispatchEventArgument = (AWTEvent[])((AWTEvent[])var2.get("event"));
         this.afterDispatchHandleArgument = (Object[])((Object[])var2.get("handle"));
         this.afterDispatchCallable = (Callable)var2.get("method");
         var2 = (Map)var1.get("beforeDispatch");
         this.beforeDispatchEventArgument = (AWTEvent[])((AWTEvent[])var2.get("event"));
         this.beforeDispatchCallable = (Callable)var2.get("method");
         var2 = (Map)var1.get("getNextEvent");
         this.getNextEventEventQueueArgument = (EventQueue[])((EventQueue[])var2.get("eventQueue"));
         this.getNextEventCallable = (Callable)var2.get("method");
      }

      public void afterDispatch(AWTEvent var1, Object var2) throws InterruptedException {
         this.afterDispatchEventArgument[0] = var1;
         this.afterDispatchHandleArgument[0] = var2;

         try {
            this.afterDispatchCallable.call();
         } catch (InterruptedException var4) {
            throw var4;
         } catch (RuntimeException var5) {
            throw var5;
         } catch (Exception var6) {
            throw new RuntimeException(var6);
         }
      }

      public Object beforeDispatch(AWTEvent var1) throws InterruptedException {
         this.beforeDispatchEventArgument[0] = var1;

         try {
            return this.beforeDispatchCallable.call();
         } catch (InterruptedException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      }

      public AWTEvent getNextEvent(EventQueue var1) throws InterruptedException {
         this.getNextEventEventQueueArgument[0] = var1;

         try {
            return (AWTEvent)this.getNextEventCallable.call();
         } catch (InterruptedException var3) {
            throw var3;
         } catch (RuntimeException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      }
   }
}
