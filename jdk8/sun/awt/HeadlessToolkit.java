package sun.awt;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
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
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import sun.awt.datatransfer.DataTransferer;

public class HeadlessToolkit extends Toolkit implements ComponentFactory, KeyboardFocusManagerPeerProvider {
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
   private Toolkit tk;
   private ComponentFactory componentFactory;

   public HeadlessToolkit(Toolkit var1) {
      this.tk = var1;
      if (var1 instanceof ComponentFactory) {
         this.componentFactory = (ComponentFactory)var1;
      }

   }

   public Toolkit getUnderlyingToolkit() {
      return this.tk;
   }

   public CanvasPeer createCanvas(Canvas var1) {
      return (CanvasPeer)this.createComponent(var1);
   }

   public PanelPeer createPanel(Panel var1) {
      return (PanelPeer)this.createComponent(var1);
   }

   public WindowPeer createWindow(Window var1) throws HeadlessException {
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
         throw new NullPointerException("frame must not be null");
      }
   }

   public PrintJob getPrintJob(Frame var1, String var2, Properties var3) {
      if (var1 != null) {
         throw new HeadlessException();
      } else {
         throw new NullPointerException("frame must not be null");
      }
   }

   public void sync() {
   }

   public void beep() {
      System.out.write(7);
   }

   public EventQueue getSystemEventQueueImpl() {
      return SunToolkit.getSystemEventQueueImplPP();
   }

   public int checkImage(Image var1, int var2, int var3, ImageObserver var4) {
      return this.tk.checkImage(var1, var2, var3, var4);
   }

   public boolean prepareImage(Image var1, int var2, int var3, ImageObserver var4) {
      return this.tk.prepareImage(var1, var2, var3, var4);
   }

   public Image getImage(String var1) {
      return this.tk.getImage(var1);
   }

   public Image getImage(URL var1) {
      return this.tk.getImage(var1);
   }

   public Image createImage(String var1) {
      return this.tk.createImage(var1);
   }

   public Image createImage(URL var1) {
      return this.tk.createImage(var1);
   }

   public Image createImage(byte[] var1, int var2, int var3) {
      return this.tk.createImage(var1, var2, var3);
   }

   public Image createImage(ImageProducer var1) {
      return this.tk.createImage(var1);
   }

   public Image createImage(byte[] var1) {
      return this.tk.createImage(var1);
   }

   public FontPeer getFontPeer(String var1, int var2) {
      return this.componentFactory != null ? this.componentFactory.getFontPeer(var1, var2) : null;
   }

   public DataTransferer getDataTransferer() {
      return null;
   }

   public FontMetrics getFontMetrics(Font var1) {
      return this.tk.getFontMetrics(var1);
   }

   public String[] getFontList() {
      return this.tk.getFontList();
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.tk.addPropertyChangeListener(var1, var2);
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.tk.removePropertyChangeListener(var1, var2);
   }

   public boolean isModalityTypeSupported(Dialog.ModalityType var1) {
      return false;
   }

   public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType var1) {
      return false;
   }

   public boolean isAlwaysOnTopSupported() {
      return false;
   }

   public void addAWTEventListener(AWTEventListener var1, long var2) {
      this.tk.addAWTEventListener(var1, var2);
   }

   public void removeAWTEventListener(AWTEventListener var1) {
      this.tk.removeAWTEventListener(var1);
   }

   public AWTEventListener[] getAWTEventListeners() {
      return this.tk.getAWTEventListeners();
   }

   public AWTEventListener[] getAWTEventListeners(long var1) {
      return this.tk.getAWTEventListeners(var1);
   }

   public boolean isDesktopSupported() {
      return false;
   }

   public DesktopPeer createDesktopPeer(Desktop var1) throws HeadlessException {
      throw new HeadlessException();
   }

   public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
      throw new HeadlessException();
   }
}
