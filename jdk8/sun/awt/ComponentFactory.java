package sun.awt;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
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
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import sun.awt.datatransfer.DataTransferer;

public interface ComponentFactory {
   CanvasPeer createCanvas(Canvas var1) throws HeadlessException;

   PanelPeer createPanel(Panel var1) throws HeadlessException;

   WindowPeer createWindow(Window var1) throws HeadlessException;

   FramePeer createFrame(Frame var1) throws HeadlessException;

   DialogPeer createDialog(Dialog var1) throws HeadlessException;

   ButtonPeer createButton(Button var1) throws HeadlessException;

   TextFieldPeer createTextField(TextField var1) throws HeadlessException;

   ChoicePeer createChoice(Choice var1) throws HeadlessException;

   LabelPeer createLabel(Label var1) throws HeadlessException;

   ListPeer createList(List var1) throws HeadlessException;

   CheckboxPeer createCheckbox(Checkbox var1) throws HeadlessException;

   ScrollbarPeer createScrollbar(Scrollbar var1) throws HeadlessException;

   ScrollPanePeer createScrollPane(ScrollPane var1) throws HeadlessException;

   TextAreaPeer createTextArea(TextArea var1) throws HeadlessException;

   FileDialogPeer createFileDialog(FileDialog var1) throws HeadlessException;

   MenuBarPeer createMenuBar(MenuBar var1) throws HeadlessException;

   MenuPeer createMenu(Menu var1) throws HeadlessException;

   PopupMenuPeer createPopupMenu(PopupMenu var1) throws HeadlessException;

   MenuItemPeer createMenuItem(MenuItem var1) throws HeadlessException;

   CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem var1) throws HeadlessException;

   DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var1) throws InvalidDnDOperationException, HeadlessException;

   FontPeer getFontPeer(String var1, int var2);

   RobotPeer createRobot(Robot var1, GraphicsDevice var2) throws AWTException, HeadlessException;

   DataTransferer getDataTransferer();
}
