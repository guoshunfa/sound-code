package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.KeyboardFocusManager;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.ScrollPaneAdjustable;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.peer.ComponentPeer;
import java.awt.peer.MenuComponentPeer;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import sun.misc.Unsafe;

public final class AWTAccessor {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static AWTAccessor.ComponentAccessor componentAccessor;
   private static AWTAccessor.ContainerAccessor containerAccessor;
   private static AWTAccessor.WindowAccessor windowAccessor;
   private static AWTAccessor.AWTEventAccessor awtEventAccessor;
   private static AWTAccessor.InputEventAccessor inputEventAccessor;
   private static AWTAccessor.MouseEventAccessor mouseEventAccessor;
   private static AWTAccessor.FrameAccessor frameAccessor;
   private static AWTAccessor.KeyboardFocusManagerAccessor kfmAccessor;
   private static AWTAccessor.MenuComponentAccessor menuComponentAccessor;
   private static AWTAccessor.EventQueueAccessor eventQueueAccessor;
   private static AWTAccessor.PopupMenuAccessor popupMenuAccessor;
   private static AWTAccessor.FileDialogAccessor fileDialogAccessor;
   private static AWTAccessor.ScrollPaneAdjustableAccessor scrollPaneAdjustableAccessor;
   private static AWTAccessor.CheckboxMenuItemAccessor checkboxMenuItemAccessor;
   private static AWTAccessor.CursorAccessor cursorAccessor;
   private static AWTAccessor.MenuBarAccessor menuBarAccessor;
   private static AWTAccessor.MenuItemAccessor menuItemAccessor;
   private static AWTAccessor.MenuAccessor menuAccessor;
   private static AWTAccessor.KeyEventAccessor keyEventAccessor;
   private static AWTAccessor.ClientPropertyKeyAccessor clientPropertyKeyAccessor;
   private static AWTAccessor.SystemTrayAccessor systemTrayAccessor;
   private static AWTAccessor.TrayIconAccessor trayIconAccessor;
   private static AWTAccessor.DefaultKeyboardFocusManagerAccessor defaultKeyboardFocusManagerAccessor;
   private static AWTAccessor.SequencedEventAccessor sequencedEventAccessor;
   private static AWTAccessor.ToolkitAccessor toolkitAccessor;
   private static AWTAccessor.InvocationEventAccessor invocationEventAccessor;
   private static AWTAccessor.SystemColorAccessor systemColorAccessor;
   private static AWTAccessor.AccessibleContextAccessor accessibleContextAccessor;

   private AWTAccessor() {
   }

   public static void setComponentAccessor(AWTAccessor.ComponentAccessor var0) {
      componentAccessor = var0;
   }

   public static AWTAccessor.ComponentAccessor getComponentAccessor() {
      if (componentAccessor == null) {
         unsafe.ensureClassInitialized(Component.class);
      }

      return componentAccessor;
   }

   public static void setContainerAccessor(AWTAccessor.ContainerAccessor var0) {
      containerAccessor = var0;
   }

   public static AWTAccessor.ContainerAccessor getContainerAccessor() {
      if (containerAccessor == null) {
         unsafe.ensureClassInitialized(Container.class);
      }

      return containerAccessor;
   }

   public static void setWindowAccessor(AWTAccessor.WindowAccessor var0) {
      windowAccessor = var0;
   }

   public static AWTAccessor.WindowAccessor getWindowAccessor() {
      if (windowAccessor == null) {
         unsafe.ensureClassInitialized(Window.class);
      }

      return windowAccessor;
   }

   public static void setAWTEventAccessor(AWTAccessor.AWTEventAccessor var0) {
      awtEventAccessor = var0;
   }

   public static AWTAccessor.AWTEventAccessor getAWTEventAccessor() {
      if (awtEventAccessor == null) {
         unsafe.ensureClassInitialized(AWTEvent.class);
      }

      return awtEventAccessor;
   }

   public static void setInputEventAccessor(AWTAccessor.InputEventAccessor var0) {
      inputEventAccessor = var0;
   }

   public static AWTAccessor.InputEventAccessor getInputEventAccessor() {
      if (inputEventAccessor == null) {
         unsafe.ensureClassInitialized(InputEvent.class);
      }

      return inputEventAccessor;
   }

   public static void setMouseEventAccessor(AWTAccessor.MouseEventAccessor var0) {
      mouseEventAccessor = var0;
   }

   public static AWTAccessor.MouseEventAccessor getMouseEventAccessor() {
      if (mouseEventAccessor == null) {
         unsafe.ensureClassInitialized(MouseEvent.class);
      }

      return mouseEventAccessor;
   }

   public static void setFrameAccessor(AWTAccessor.FrameAccessor var0) {
      frameAccessor = var0;
   }

   public static AWTAccessor.FrameAccessor getFrameAccessor() {
      if (frameAccessor == null) {
         unsafe.ensureClassInitialized(Frame.class);
      }

      return frameAccessor;
   }

   public static void setKeyboardFocusManagerAccessor(AWTAccessor.KeyboardFocusManagerAccessor var0) {
      kfmAccessor = var0;
   }

   public static AWTAccessor.KeyboardFocusManagerAccessor getKeyboardFocusManagerAccessor() {
      if (kfmAccessor == null) {
         unsafe.ensureClassInitialized(KeyboardFocusManager.class);
      }

      return kfmAccessor;
   }

   public static void setMenuComponentAccessor(AWTAccessor.MenuComponentAccessor var0) {
      menuComponentAccessor = var0;
   }

   public static AWTAccessor.MenuComponentAccessor getMenuComponentAccessor() {
      if (menuComponentAccessor == null) {
         unsafe.ensureClassInitialized(MenuComponent.class);
      }

      return menuComponentAccessor;
   }

   public static void setEventQueueAccessor(AWTAccessor.EventQueueAccessor var0) {
      eventQueueAccessor = var0;
   }

   public static AWTAccessor.EventQueueAccessor getEventQueueAccessor() {
      if (eventQueueAccessor == null) {
         unsafe.ensureClassInitialized(EventQueue.class);
      }

      return eventQueueAccessor;
   }

   public static void setPopupMenuAccessor(AWTAccessor.PopupMenuAccessor var0) {
      popupMenuAccessor = var0;
   }

   public static AWTAccessor.PopupMenuAccessor getPopupMenuAccessor() {
      if (popupMenuAccessor == null) {
         unsafe.ensureClassInitialized(PopupMenu.class);
      }

      return popupMenuAccessor;
   }

   public static void setFileDialogAccessor(AWTAccessor.FileDialogAccessor var0) {
      fileDialogAccessor = var0;
   }

   public static AWTAccessor.FileDialogAccessor getFileDialogAccessor() {
      if (fileDialogAccessor == null) {
         unsafe.ensureClassInitialized(FileDialog.class);
      }

      return fileDialogAccessor;
   }

   public static void setScrollPaneAdjustableAccessor(AWTAccessor.ScrollPaneAdjustableAccessor var0) {
      scrollPaneAdjustableAccessor = var0;
   }

   public static AWTAccessor.ScrollPaneAdjustableAccessor getScrollPaneAdjustableAccessor() {
      if (scrollPaneAdjustableAccessor == null) {
         unsafe.ensureClassInitialized(ScrollPaneAdjustable.class);
      }

      return scrollPaneAdjustableAccessor;
   }

   public static void setCheckboxMenuItemAccessor(AWTAccessor.CheckboxMenuItemAccessor var0) {
      checkboxMenuItemAccessor = var0;
   }

   public static AWTAccessor.CheckboxMenuItemAccessor getCheckboxMenuItemAccessor() {
      if (checkboxMenuItemAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.CheckboxMenuItemAccessor.class);
      }

      return checkboxMenuItemAccessor;
   }

   public static void setCursorAccessor(AWTAccessor.CursorAccessor var0) {
      cursorAccessor = var0;
   }

   public static AWTAccessor.CursorAccessor getCursorAccessor() {
      if (cursorAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.CursorAccessor.class);
      }

      return cursorAccessor;
   }

   public static void setMenuBarAccessor(AWTAccessor.MenuBarAccessor var0) {
      menuBarAccessor = var0;
   }

   public static AWTAccessor.MenuBarAccessor getMenuBarAccessor() {
      if (menuBarAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.MenuBarAccessor.class);
      }

      return menuBarAccessor;
   }

   public static void setMenuItemAccessor(AWTAccessor.MenuItemAccessor var0) {
      menuItemAccessor = var0;
   }

   public static AWTAccessor.MenuItemAccessor getMenuItemAccessor() {
      if (menuItemAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.MenuItemAccessor.class);
      }

      return menuItemAccessor;
   }

   public static void setMenuAccessor(AWTAccessor.MenuAccessor var0) {
      menuAccessor = var0;
   }

   public static AWTAccessor.MenuAccessor getMenuAccessor() {
      if (menuAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.MenuAccessor.class);
      }

      return menuAccessor;
   }

   public static void setKeyEventAccessor(AWTAccessor.KeyEventAccessor var0) {
      keyEventAccessor = var0;
   }

   public static AWTAccessor.KeyEventAccessor getKeyEventAccessor() {
      if (keyEventAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.KeyEventAccessor.class);
      }

      return keyEventAccessor;
   }

   public static void setClientPropertyKeyAccessor(AWTAccessor.ClientPropertyKeyAccessor var0) {
      clientPropertyKeyAccessor = var0;
   }

   public static AWTAccessor.ClientPropertyKeyAccessor getClientPropertyKeyAccessor() {
      if (clientPropertyKeyAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.ClientPropertyKeyAccessor.class);
      }

      return clientPropertyKeyAccessor;
   }

   public static void setSystemTrayAccessor(AWTAccessor.SystemTrayAccessor var0) {
      systemTrayAccessor = var0;
   }

   public static AWTAccessor.SystemTrayAccessor getSystemTrayAccessor() {
      if (systemTrayAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.SystemTrayAccessor.class);
      }

      return systemTrayAccessor;
   }

   public static void setTrayIconAccessor(AWTAccessor.TrayIconAccessor var0) {
      trayIconAccessor = var0;
   }

   public static AWTAccessor.TrayIconAccessor getTrayIconAccessor() {
      if (trayIconAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.TrayIconAccessor.class);
      }

      return trayIconAccessor;
   }

   public static void setDefaultKeyboardFocusManagerAccessor(AWTAccessor.DefaultKeyboardFocusManagerAccessor var0) {
      defaultKeyboardFocusManagerAccessor = var0;
   }

   public static AWTAccessor.DefaultKeyboardFocusManagerAccessor getDefaultKeyboardFocusManagerAccessor() {
      if (defaultKeyboardFocusManagerAccessor == null) {
         unsafe.ensureClassInitialized(AWTAccessor.DefaultKeyboardFocusManagerAccessor.class);
      }

      return defaultKeyboardFocusManagerAccessor;
   }

   public static void setSequencedEventAccessor(AWTAccessor.SequencedEventAccessor var0) {
      sequencedEventAccessor = var0;
   }

   public static AWTAccessor.SequencedEventAccessor getSequencedEventAccessor() {
      return sequencedEventAccessor;
   }

   public static void setToolkitAccessor(AWTAccessor.ToolkitAccessor var0) {
      toolkitAccessor = var0;
   }

   public static AWTAccessor.ToolkitAccessor getToolkitAccessor() {
      if (toolkitAccessor == null) {
         unsafe.ensureClassInitialized(Toolkit.class);
      }

      return toolkitAccessor;
   }

   public static void setInvocationEventAccessor(AWTAccessor.InvocationEventAccessor var0) {
      invocationEventAccessor = var0;
   }

   public static AWTAccessor.InvocationEventAccessor getInvocationEventAccessor() {
      return invocationEventAccessor;
   }

   public static AWTAccessor.SystemColorAccessor getSystemColorAccessor() {
      if (systemColorAccessor == null) {
         unsafe.ensureClassInitialized(SystemColor.class);
      }

      return systemColorAccessor;
   }

   public static void setSystemColorAccessor(AWTAccessor.SystemColorAccessor var0) {
      systemColorAccessor = var0;
   }

   public static AWTAccessor.AccessibleContextAccessor getAccessibleContextAccessor() {
      if (accessibleContextAccessor == null) {
         unsafe.ensureClassInitialized(AccessibleContext.class);
      }

      return accessibleContextAccessor;
   }

   public static void setAccessibleContextAccessor(AWTAccessor.AccessibleContextAccessor var0) {
      accessibleContextAccessor = var0;
   }

   public interface AccessibleContextAccessor {
      void setAppContext(AccessibleContext var1, AppContext var2);

      AppContext getAppContext(AccessibleContext var1);
   }

   public interface SystemColorAccessor {
      void updateSystemColors();
   }

   public interface InvocationEventAccessor {
      void dispose(InvocationEvent var1);
   }

   public interface ToolkitAccessor {
      void setPlatformResources(ResourceBundle var1);
   }

   public interface SequencedEventAccessor {
      AWTEvent getNested(AWTEvent var1);

      boolean isSequencedEvent(AWTEvent var1);
   }

   public interface DefaultKeyboardFocusManagerAccessor {
      void consumeNextKeyTyped(DefaultKeyboardFocusManager var1, KeyEvent var2);
   }

   public interface TrayIconAccessor {
      void addNotify(TrayIcon var1) throws AWTException;

      void removeNotify(TrayIcon var1);
   }

   public interface SystemTrayAccessor {
      void firePropertyChange(SystemTray var1, String var2, Object var3, Object var4);
   }

   public interface ClientPropertyKeyAccessor {
      Object getJComponent_TRANSFER_HANDLER();
   }

   public interface KeyEventAccessor {
      void setRawCode(KeyEvent var1, long var2);

      void setPrimaryLevelUnicode(KeyEvent var1, long var2);

      void setExtendedKeyCode(KeyEvent var1, long var2);

      Component getOriginalSource(KeyEvent var1);
   }

   public interface MenuAccessor {
      Vector getItems(Menu var1);
   }

   public interface MenuItemAccessor {
      boolean isEnabled(MenuItem var1);

      String getActionCommandImpl(MenuItem var1);

      boolean isItemEnabled(MenuItem var1);

      String getLabel(MenuItem var1);

      MenuShortcut getShortcut(MenuItem var1);
   }

   public interface MenuBarAccessor {
      Menu getHelpMenu(MenuBar var1);

      Vector getMenus(MenuBar var1);
   }

   public interface CursorAccessor {
      long getPData(Cursor var1);

      void setPData(Cursor var1, long var2);

      int getType(Cursor var1);
   }

   public interface CheckboxMenuItemAccessor {
      boolean getState(CheckboxMenuItem var1);
   }

   public interface ScrollPaneAdjustableAccessor {
      void setTypedValue(ScrollPaneAdjustable var1, int var2, int var3);
   }

   public interface FileDialogAccessor {
      void setFiles(FileDialog var1, File[] var2);

      void setFile(FileDialog var1, String var2);

      void setDirectory(FileDialog var1, String var2);

      boolean isMultipleMode(FileDialog var1);
   }

   public interface PopupMenuAccessor {
      boolean isTrayIconPopup(PopupMenu var1);
   }

   public interface EventQueueAccessor {
      Thread getDispatchThread(EventQueue var1);

      boolean isDispatchThreadImpl(EventQueue var1);

      void removeSourceEvents(EventQueue var1, Object var2, boolean var3);

      boolean noEvents(EventQueue var1);

      void wakeup(EventQueue var1, boolean var2);

      void invokeAndWait(Object var1, Runnable var2) throws InterruptedException, InvocationTargetException;

      void setFwDispatcher(EventQueue var1, FwDispatcher var2);

      long getMostRecentEventTime(EventQueue var1);
   }

   public interface MenuComponentAccessor {
      AppContext getAppContext(MenuComponent var1);

      void setAppContext(MenuComponent var1, AppContext var2);

      MenuContainer getParent(MenuComponent var1);

      Font getFont_NoClientCode(MenuComponent var1);

      <T extends MenuComponentPeer> T getPeer(MenuComponent var1);
   }

   public interface KeyboardFocusManagerAccessor {
      int shouldNativelyFocusHeavyweight(Component var1, Component var2, boolean var3, boolean var4, long var5, CausedFocusEvent.Cause var7);

      boolean processSynchronousLightweightTransfer(Component var1, Component var2, boolean var3, boolean var4, long var5);

      void removeLastFocusRequest(Component var1);

      void setMostRecentFocusOwner(Window var1, Component var2);

      KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext var1);

      Container getCurrentFocusCycleRoot();
   }

   public interface FrameAccessor {
      void setExtendedState(Frame var1, int var2);

      int getExtendedState(Frame var1);

      Rectangle getMaximizedBounds(Frame var1);
   }

   public interface MouseEventAccessor {
      boolean isCausedByTouchEvent(MouseEvent var1);

      void setCausedByTouchEvent(MouseEvent var1, boolean var2);
   }

   public interface InputEventAccessor {
      int[] getButtonDownMasks();
   }

   public interface AWTEventAccessor {
      void setPosted(AWTEvent var1);

      void setSystemGenerated(AWTEvent var1);

      boolean isSystemGenerated(AWTEvent var1);

      AccessControlContext getAccessControlContext(AWTEvent var1);

      byte[] getBData(AWTEvent var1);

      void setBData(AWTEvent var1, byte[] var2);
   }

   public interface WindowAccessor {
      float getOpacity(Window var1);

      void setOpacity(Window var1, float var2);

      Shape getShape(Window var1);

      void setShape(Window var1, Shape var2);

      void setOpaque(Window var1, boolean var2);

      void updateWindow(Window var1);

      Dimension getSecurityWarningSize(Window var1);

      void setSecurityWarningSize(Window var1, int var2, int var3);

      void setSecurityWarningPosition(Window var1, Point2D var2, float var3, float var4);

      Point2D calculateSecurityWarningPosition(Window var1, double var2, double var4, double var6, double var8);

      void setLWRequestStatus(Window var1, boolean var2);

      boolean isAutoRequestFocus(Window var1);

      boolean isTrayIconWindow(Window var1);

      void setTrayIconWindow(Window var1, boolean var2);

      Window[] getOwnedWindows(Window var1);
   }

   public interface ContainerAccessor {
      void validateUnconditionally(Container var1);

      Component findComponentAt(Container var1, int var2, int var3, boolean var4);
   }

   public interface ComponentAccessor {
      void setBackgroundEraseDisabled(Component var1, boolean var2);

      boolean getBackgroundEraseDisabled(Component var1);

      Rectangle getBounds(Component var1);

      void setMixingCutoutShape(Component var1, Shape var2);

      void setGraphicsConfiguration(Component var1, GraphicsConfiguration var2);

      boolean requestFocus(Component var1, CausedFocusEvent.Cause var2);

      boolean canBeFocusOwner(Component var1);

      boolean isVisible(Component var1);

      void setRequestFocusController(RequestFocusController var1);

      AppContext getAppContext(Component var1);

      void setAppContext(Component var1, AppContext var2);

      Container getParent(Component var1);

      void setParent(Component var1, Container var2);

      void setSize(Component var1, int var2, int var3);

      Point getLocation(Component var1);

      void setLocation(Component var1, int var2, int var3);

      boolean isEnabled(Component var1);

      boolean isDisplayable(Component var1);

      Cursor getCursor(Component var1);

      ComponentPeer getPeer(Component var1);

      void setPeer(Component var1, ComponentPeer var2);

      boolean isLightweight(Component var1);

      boolean getIgnoreRepaint(Component var1);

      int getWidth(Component var1);

      int getHeight(Component var1);

      int getX(Component var1);

      int getY(Component var1);

      Color getForeground(Component var1);

      Color getBackground(Component var1);

      void setBackground(Component var1, Color var2);

      Font getFont(Component var1);

      void processEvent(Component var1, AWTEvent var2);

      AccessControlContext getAccessControlContext(Component var1);

      void revalidateSynchronously(Component var1);
   }
}
