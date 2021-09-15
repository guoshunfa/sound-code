package sun.awt;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.util.Map;
import java.util.Properties;
import sun.awt.datatransfer.DataTransferer;

public class HToolkit extends SunToolkit implements ComponentFactory {
   private static final KeyboardFocusManagerPeer kfmPeer = new KeyboardFocusManagerPeer() {
      public void setCurrentFocusedWindow(Window var1) {
      }

      public Window getCurrentFocusedWindow() {
         return null;
      }

      public void setCurrentFocusOwner(Component var1) {
      }

      public Component getCurrentFocusOwner() {
         return null;
      }

      public void clearGlobalFocusOwner(Window var1) {
      }
   };

   public WindowPeer createWindow(Window var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public FramePeer createLightweightFrame(LightweightFrame var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public FramePeer createFrame(Frame var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public DialogPeer createDialog(Dialog var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public ButtonPeer createButton(Button var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public TextFieldPeer createTextField(TextField var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public ChoicePeer createChoice(Choice var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public LabelPeer createLabel(Label var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public ListPeer createList(List var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public CheckboxPeer createCheckbox(Checkbox var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public ScrollbarPeer createScrollbar(Scrollbar var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public ScrollPanePeer createScrollPane(ScrollPane var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public TextAreaPeer createTextArea(TextArea var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public FileDialogPeer createFileDialog(FileDialog var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public MenuBarPeer createMenuBar(MenuBar var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public MenuPeer createMenu(Menu var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public PopupMenuPeer createPopupMenu(PopupMenu var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public MenuItemPeer createMenuItem(MenuItem var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException {
      throw new InvalidDnDOperationException("Headless environment");
   }

   public RobotPeer createRobot(Robot var1, GraphicsDevice var2) throws AWTException, HeadlessException {
      throw new HeadlessException();
   }

   public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() {
      return kfmPeer;
   }

   public TrayIconPeer createTrayIcon(TrayIcon var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public SystemTrayPeer createSystemTray(SystemTray var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public boolean isTraySupported() {
      return false;
   }

   public DataTransferer getDataTransferer() {
      return null;
   }

   public GlobalCursorManager getGlobalCursorManager() throws HeadlessException {
      throw new HeadlessException();
   }

   protected void loadSystemColors(int[] var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public ColorModel getColorModel() throws HeadlessException {
      throw new HeadlessException();
   }

   public int getScreenResolution() throws HeadlessException {
      throw new HeadlessException();
   }

   public Map mapInputMethodHighlight(InputMethodHighlight var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public int getMenuShortcutKeyMask() throws HeadlessException {
      throw new HeadlessException();
   }

   public boolean getLockingKeyState(int var1) throws UnsupportedOperationException {
      throw new HeadlessException();
   }

   public void setLockingKeyState(int var1, boolean var2) throws UnsupportedOperationException {
      throw new HeadlessException();
   }

   public Cursor createCustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException, HeadlessException {
      throw new HeadlessException();
   }

   public Dimension getBestCursorSize(int var1, int var2) throws HeadlessException {
      throw new HeadlessException();
   }

   public int getMaximumCursorColors() throws HeadlessException {
      throw new HeadlessException();
   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      return null;
   }

   public int getScreenHeight() throws HeadlessException {
      throw new HeadlessException();
   }

   public int getScreenWidth() throws HeadlessException {
      throw new HeadlessException();
   }

   public Dimension getScreenSize() throws HeadlessException {
      throw new HeadlessException();
   }

   public Insets getScreenInsets(GraphicsConfiguration var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public void setDynamicLayout(boolean var1) throws HeadlessException {
      throw new HeadlessException();
   }

   protected boolean isDynamicLayoutSet() throws HeadlessException {
      throw new HeadlessException();
   }

   public boolean isDynamicLayoutActive() throws HeadlessException {
      throw new HeadlessException();
   }

   public Clipboard getSystemClipboard() throws HeadlessException {
      throw new HeadlessException();
   }

   public PrintJob getPrintJob(Frame var1, String var2, JobAttributes var3, PageAttributes var4) {
      if (var1 != null) {
         throw new HeadlessException();
      } else {
         throw new IllegalArgumentException("PrintJob not supported in a headless environment");
      }
   }

   public PrintJob getPrintJob(Frame var1, String var2, Properties var3) {
      if (var1 != null) {
         throw new HeadlessException();
      } else {
         throw new IllegalArgumentException("PrintJob not supported in a headless environment");
      }
   }

   public void sync() {
   }

   protected boolean syncNativeQueue(long var1) {
      return false;
   }

   public void beep() {
      System.out.write(7);
   }

   public FontPeer getFontPeer(String var1, int var2) {
      return (FontPeer)null;
   }

   public boolean isModalityTypeSupported(Dialog.ModalityType var1) {
      return false;
   }

   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType var1) {
      return false;
   }

   public boolean isDesktopSupported() {
      return false;
   }

   public DesktopPeer createDesktopPeer(Desktop var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public boolean isWindowOpacityControlSupported() {
      return false;
   }

   public boolean isWindowShapingSupported() {
      return false;
   }

   public boolean isWindowTranslucencySupported() {
      return false;
   }

   public void grab(Window var1) {
   }

   public void ungrab(Window var1) {
   }

   protected boolean syncNativeQueue() {
      return false;
   }

   public InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException {
      return (InputMethodDescriptor)null;
   }
}
