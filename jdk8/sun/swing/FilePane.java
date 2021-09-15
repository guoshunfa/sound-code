package sun.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultRowSorter;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Position;
import sun.awt.AWTAccessor;
import sun.awt.shell.ShellFolder;
import sun.awt.shell.ShellFolderColumnInfo;

public class FilePane extends JPanel implements PropertyChangeListener {
   public static final String ACTION_APPROVE_SELECTION = "approveSelection";
   public static final String ACTION_CANCEL = "cancelSelection";
   public static final String ACTION_EDIT_FILE_NAME = "editFileName";
   public static final String ACTION_REFRESH = "refresh";
   public static final String ACTION_CHANGE_TO_PARENT_DIRECTORY = "Go Up";
   public static final String ACTION_NEW_FOLDER = "New Folder";
   public static final String ACTION_VIEW_LIST = "viewTypeList";
   public static final String ACTION_VIEW_DETAILS = "viewTypeDetails";
   private Action[] actions;
   public static final int VIEWTYPE_LIST = 0;
   public static final int VIEWTYPE_DETAILS = 1;
   private static final int VIEWTYPE_COUNT = 2;
   private int viewType = -1;
   private JPanel[] viewPanels = new JPanel[2];
   private JPanel currentViewPanel;
   private String[] viewTypeActionNames;
   private String filesListAccessibleName = null;
   private String filesDetailsAccessibleName = null;
   private JPopupMenu contextMenu;
   private JMenu viewMenu;
   private String viewMenuLabelText;
   private String refreshActionLabelText;
   private String newFolderActionLabelText;
   private String kiloByteString;
   private String megaByteString;
   private String gigaByteString;
   private String renameErrorTitleText;
   private String renameErrorText;
   private String renameErrorFileExistsText;
   private static final Cursor waitCursor = Cursor.getPredefinedCursor(3);
   private final KeyListener detailsKeyListener = new KeyAdapter() {
      private final long timeFactor;
      private final StringBuilder typedString = new StringBuilder();
      private long lastTime = 1000L;

      {
         Long var2 = (Long)UIManager.get("Table.timeFactor");
         this.timeFactor = var2 != null ? var2 : 1000L;
      }

      public void keyTyped(KeyEvent var1) {
         BasicDirectoryModel var2 = FilePane.this.getModel();
         int var3 = var2.getSize();
         if (FilePane.this.detailsTable != null && var3 != 0 && !var1.isAltDown() && !var1.isControlDown() && !var1.isMetaDown()) {
            InputMap var4 = FilePane.this.detailsTable.getInputMap(1);
            KeyStroke var5 = KeyStroke.getKeyStrokeForEvent(var1);
            if (var4 == null || var4.get(var5) == null) {
               int var6 = FilePane.this.detailsTable.getSelectionModel().getLeadSelectionIndex();
               if (var6 < 0) {
                  var6 = 0;
               }

               if (var6 >= var3) {
                  var6 = var3 - 1;
               }

               char var7 = var1.getKeyChar();
               long var8 = var1.getWhen();
               if (var8 - this.lastTime < this.timeFactor) {
                  if (this.typedString.length() == 1 && this.typedString.charAt(0) == var7) {
                     ++var6;
                  } else {
                     this.typedString.append(var7);
                  }
               } else {
                  ++var6;
                  this.typedString.setLength(0);
                  this.typedString.append(var7);
               }

               this.lastTime = var8;
               if (var6 >= var3) {
                  var6 = 0;
               }

               int var10 = this.getNextMatch(var6, var3 - 1);
               if (var10 < 0 && var6 > 0) {
                  var10 = this.getNextMatch(0, var6 - 1);
               }

               if (var10 >= 0) {
                  FilePane.this.detailsTable.getSelectionModel().setSelectionInterval(var10, var10);
                  Rectangle var11 = FilePane.this.detailsTable.getCellRect(var10, FilePane.this.detailsTable.convertColumnIndexToView(0), false);
                  FilePane.this.detailsTable.scrollRectToVisible(var11);
               }

            }
         }
      }

      private int getNextMatch(int var1, int var2) {
         BasicDirectoryModel var3 = FilePane.this.getModel();
         JFileChooser var4 = FilePane.this.getFileChooser();
         FilePane.DetailsTableRowSorter var5 = FilePane.this.getRowSorter();
         String var6 = this.typedString.toString().toLowerCase();

         for(int var7 = var1; var7 <= var2; ++var7) {
            File var8 = (File)var3.getElementAt(var5.convertRowIndexToModel(var7));
            String var9 = var4.getName(var8).toLowerCase();
            if (var9.startsWith(var6)) {
               return var7;
            }
         }

         return -1;
      }
   };
   private FocusListener editorFocusListener = new FocusAdapter() {
      public void focusLost(FocusEvent var1) {
         if (!var1.isTemporary()) {
            FilePane.this.applyEdit();
         }

      }
   };
   private static FocusListener repaintListener = new FocusListener() {
      public void focusGained(FocusEvent var1) {
         this.repaintSelection(var1.getSource());
      }

      public void focusLost(FocusEvent var1) {
         this.repaintSelection(var1.getSource());
      }

      private void repaintSelection(Object var1) {
         if (var1 instanceof JList) {
            this.repaintListSelection((JList)var1);
         } else if (var1 instanceof JTable) {
            this.repaintTableSelection((JTable)var1);
         }

      }

      private void repaintListSelection(JList var1) {
         int[] var2 = var1.getSelectedIndices();
         int[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var3[var5];
            Rectangle var7 = var1.getCellBounds(var6, var6);
            var1.repaint(var7);
         }

      }

      private void repaintTableSelection(JTable var1) {
         int var2 = var1.getSelectionModel().getMinSelectionIndex();
         int var3 = var1.getSelectionModel().getMaxSelectionIndex();
         if (var2 != -1 && var3 != -1) {
            int var4 = var1.convertColumnIndexToView(0);
            Rectangle var5 = var1.getCellRect(var2, var4, false);
            Rectangle var6 = var1.getCellRect(var3, var4, false);
            Rectangle var7 = var5.union(var6);
            var1.repaint(var7);
         }
      }
   };
   private boolean smallIconsView = false;
   private Border listViewBorder;
   private Color listViewBackground;
   private boolean listViewWindowsStyle;
   private boolean readOnly;
   private boolean fullRowSelection = false;
   private ListSelectionModel listSelectionModel;
   private JList list;
   private JTable detailsTable;
   private static final int COLUMN_FILENAME = 0;
   private File newFolderFile;
   private FilePane.FileChooserUIAccessor fileChooserUIAccessor;
   private FilePane.DetailsTableModel detailsTableModel;
   private FilePane.DetailsTableRowSorter rowSorter;
   private FilePane.DetailsTableCellEditor tableCellEditor;
   int lastIndex = -1;
   File editFile = null;
   JTextField editCell = null;
   protected Action newFolderAction;
   private FilePane.Handler handler;

   public FilePane(FilePane.FileChooserUIAccessor var1) {
      super(new BorderLayout());
      this.fileChooserUIAccessor = var1;
      this.installDefaults();
      this.createActionMap();
   }

   public void uninstallUI() {
      if (this.getModel() != null) {
         this.getModel().removePropertyChangeListener(this);
      }

   }

   protected JFileChooser getFileChooser() {
      return this.fileChooserUIAccessor.getFileChooser();
   }

   protected BasicDirectoryModel getModel() {
      return this.fileChooserUIAccessor.getModel();
   }

   public int getViewType() {
      return this.viewType;
   }

   public void setViewType(int var1) {
      if (var1 != this.viewType) {
         int var2 = this.viewType;
         this.viewType = var1;
         JPanel var3 = null;
         Object var4 = null;
         switch(var1) {
         case 0:
            if (this.viewPanels[var1] == null) {
               var3 = this.fileChooserUIAccessor.createList();
               if (var3 == null) {
                  var3 = this.createList();
               }

               this.list = (JList)this.findChildComponent(var3, JList.class);
               if (this.listSelectionModel == null) {
                  this.listSelectionModel = this.list.getSelectionModel();
                  if (this.detailsTable != null) {
                     this.detailsTable.setSelectionModel(this.listSelectionModel);
                  }
               } else {
                  this.list.setSelectionModel(this.listSelectionModel);
               }
            }

            this.list.setLayoutOrientation(1);
            var4 = this.list;
            break;
         case 1:
            if (this.viewPanels[var1] == null) {
               var3 = this.fileChooserUIAccessor.createDetailsView();
               if (var3 == null) {
                  var3 = this.createDetailsView();
               }

               this.detailsTable = (JTable)this.findChildComponent(var3, JTable.class);
               this.detailsTable.setRowHeight(Math.max(this.detailsTable.getFont().getSize() + 4, 17));
               if (this.listSelectionModel != null) {
                  this.detailsTable.setSelectionModel(this.listSelectionModel);
               }
            }

            var4 = this.detailsTable;
         }

         if (var3 != null) {
            this.viewPanels[var1] = var3;
            recursivelySetInheritsPopupMenu(var3, true);
         }

         boolean var5 = false;
         if (this.currentViewPanel != null) {
            Component var6 = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            var5 = var6 == this.detailsTable || var6 == this.list;
            this.remove(this.currentViewPanel);
         }

         this.currentViewPanel = this.viewPanels[var1];
         this.add(this.currentViewPanel, "Center");
         if (var5 && var4 != null) {
            ((Component)var4).requestFocusInWindow();
         }

         this.revalidate();
         this.repaint();
         this.updateViewMenu();
         this.firePropertyChange("viewType", var2, var1);
      }
   }

   public Action getViewTypeAction(int var1) {
      return new FilePane.ViewTypeAction(var1);
   }

   private static void recursivelySetInheritsPopupMenu(Container var0, boolean var1) {
      if (var0 instanceof JComponent) {
         ((JComponent)var0).setInheritsPopupMenu(var1);
      }

      int var2 = var0.getComponentCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         recursivelySetInheritsPopupMenu((Container)var0.getComponent(var3), var1);
      }

   }

   protected void installDefaults() {
      Locale var1 = this.getFileChooser().getLocale();
      this.listViewBorder = UIManager.getBorder("FileChooser.listViewBorder");
      this.listViewBackground = UIManager.getColor("FileChooser.listViewBackground");
      this.listViewWindowsStyle = UIManager.getBoolean("FileChooser.listViewWindowsStyle");
      this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
      this.viewMenuLabelText = UIManager.getString("FileChooser.viewMenuLabelText", (Locale)var1);
      this.refreshActionLabelText = UIManager.getString("FileChooser.refreshActionLabelText", (Locale)var1);
      this.newFolderActionLabelText = UIManager.getString("FileChooser.newFolderActionLabelText", (Locale)var1);
      this.viewTypeActionNames = new String[2];
      this.viewTypeActionNames[0] = UIManager.getString("FileChooser.listViewActionLabelText", (Locale)var1);
      this.viewTypeActionNames[1] = UIManager.getString("FileChooser.detailsViewActionLabelText", (Locale)var1);
      this.kiloByteString = UIManager.getString("FileChooser.fileSizeKiloBytes", (Locale)var1);
      this.megaByteString = UIManager.getString("FileChooser.fileSizeMegaBytes", (Locale)var1);
      this.gigaByteString = UIManager.getString("FileChooser.fileSizeGigaBytes", (Locale)var1);
      this.fullRowSelection = UIManager.getBoolean("FileView.fullRowSelection");
      this.filesListAccessibleName = UIManager.getString("FileChooser.filesListAccessibleName", (Locale)var1);
      this.filesDetailsAccessibleName = UIManager.getString("FileChooser.filesDetailsAccessibleName", (Locale)var1);
      this.renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText", (Locale)var1);
      this.renameErrorText = UIManager.getString("FileChooser.renameErrorText", (Locale)var1);
      this.renameErrorFileExistsText = UIManager.getString("FileChooser.renameErrorFileExistsText", (Locale)var1);
   }

   public Action[] getActions() {
      if (this.actions == null) {
         ArrayList var1 = new ArrayList(8);

         class FilePaneAction extends AbstractAction {
            FilePaneAction(String var2) {
               this(var2, var2);
            }

            FilePaneAction(String var2, String var3) {
               super(var2);
               this.putValue("ActionCommandKey", var3);
            }

            public void actionPerformed(ActionEvent var1) {
               String var2 = (String)this.getValue("ActionCommandKey");
               if (var2 == "cancelSelection") {
                  if (FilePane.this.editFile != null) {
                     FilePane.this.cancelEdit();
                  } else {
                     FilePane.this.getFileChooser().cancelSelection();
                  }
               } else if (var2 == "editFileName") {
                  JFileChooser var3 = FilePane.this.getFileChooser();
                  int var4 = FilePane.this.listSelectionModel.getMinSelectionIndex();
                  if (var4 >= 0 && FilePane.this.editFile == null && (!var3.isMultiSelectionEnabled() || var3.getSelectedFiles().length <= 1)) {
                     FilePane.this.editFileName(var4);
                  }
               } else if (var2 == "refresh") {
                  FilePane.this.getFileChooser().rescanCurrentDirectory();
               }

            }

            public boolean isEnabled() {
               String var1 = (String)this.getValue("ActionCommandKey");
               if (var1 == "cancelSelection") {
                  return FilePane.this.getFileChooser().isEnabled();
               } else if (var1 != "editFileName") {
                  return true;
               } else {
                  return !FilePane.this.readOnly && FilePane.this.getFileChooser().isEnabled();
               }
            }
         }

         var1.add(new FilePaneAction("cancelSelection"));
         var1.add(new FilePaneAction("editFileName"));
         var1.add(new FilePaneAction(this.refreshActionLabelText, "refresh"));
         Action var2 = this.fileChooserUIAccessor.getApproveSelectionAction();
         if (var2 != null) {
            var1.add(var2);
         }

         var2 = this.fileChooserUIAccessor.getChangeToParentDirectoryAction();
         if (var2 != null) {
            var1.add(var2);
         }

         var2 = this.getNewFolderAction();
         if (var2 != null) {
            var1.add(var2);
         }

         var2 = this.getViewTypeAction(0);
         if (var2 != null) {
            var1.add(var2);
         }

         var2 = this.getViewTypeAction(1);
         if (var2 != null) {
            var1.add(var2);
         }

         this.actions = (Action[])var1.toArray(new Action[var1.size()]);
      }

      return this.actions;
   }

   protected void createActionMap() {
      addActionsToMap(super.getActionMap(), this.getActions());
   }

   public static void addActionsToMap(ActionMap var0, Action[] var1) {
      if (var0 != null && var1 != null) {
         Action[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Action var5 = var2[var4];
            String var6 = (String)var5.getValue("ActionCommandKey");
            if (var6 == null) {
               var6 = (String)var5.getValue("Name");
            }

            var0.put(var6, var5);
         }
      }

   }

   private void updateListRowCount(JList var1) {
      if (this.smallIconsView) {
         var1.setVisibleRowCount(this.getModel().getSize() / 3);
      } else {
         var1.setVisibleRowCount(-1);
      }

   }

   public JPanel createList() {
      JPanel var1 = new JPanel(new BorderLayout());
      final JFileChooser var2 = this.getFileChooser();
      final JList var3 = new JList<Object>() {
         public int getNextMatch(String var1, int var2x, Position.Bias var3) {
            ListModel var4 = this.getModel();
            int var5 = var4.getSize();
            if (var1 != null && var2x >= 0 && var2x < var5) {
               boolean var6 = var3 == Position.Bias.Backward;
               int var7 = var2x;

               while(true) {
                  if (var6) {
                     if (var7 < 0) {
                        break;
                     }
                  } else if (var7 >= var5) {
                     break;
                  }

                  String var8 = var2.getName((File)var4.getElementAt(var7));
                  if (var8.regionMatches(true, 0, var1, 0, var1.length())) {
                     return var7;
                  }

                  var7 += var6 ? -1 : 1;
               }

               return -1;
            } else {
               throw new IllegalArgumentException();
            }
         }
      };
      var3.setCellRenderer(new FilePane.FileRenderer());
      var3.setLayoutOrientation(1);
      var3.putClientProperty("List.isFileList", Boolean.TRUE);
      if (this.listViewWindowsStyle) {
         var3.addFocusListener(repaintListener);
      }

      this.updateListRowCount(var3);
      this.getModel().addListDataListener(new ListDataListener() {
         public void intervalAdded(ListDataEvent var1) {
            FilePane.this.updateListRowCount(var3);
         }

         public void intervalRemoved(ListDataEvent var1) {
            FilePane.this.updateListRowCount(var3);
         }

         public void contentsChanged(ListDataEvent var1) {
            if (FilePane.this.isShowing()) {
               FilePane.this.clearSelection();
            }

            FilePane.this.updateListRowCount(var3);
         }
      });
      this.getModel().addPropertyChangeListener(this);
      if (var2.isMultiSelectionEnabled()) {
         var3.setSelectionMode(2);
      } else {
         var3.setSelectionMode(0);
      }

      var3.setModel(new FilePane.SortableListModel());
      var3.addListSelectionListener(this.createListSelectionListener());
      var3.addMouseListener(this.getMouseHandler());
      JScrollPane var4 = new JScrollPane(var3);
      if (this.listViewBackground != null) {
         var3.setBackground(this.listViewBackground);
      }

      if (this.listViewBorder != null) {
         var4.setBorder(this.listViewBorder);
      }

      var3.putClientProperty("AccessibleName", this.filesListAccessibleName);
      var1.add(var4, "Center");
      return var1;
   }

   private FilePane.DetailsTableModel getDetailsTableModel() {
      if (this.detailsTableModel == null) {
         this.detailsTableModel = new FilePane.DetailsTableModel(this.getFileChooser());
      }

      return this.detailsTableModel;
   }

   private void updateDetailsColumnModel(JTable var1) {
      if (var1 != null) {
         ShellFolderColumnInfo[] var2 = this.detailsTableModel.getColumns();
         DefaultTableColumnModel var3 = new DefaultTableColumnModel();

         for(int var4 = 0; var4 < var2.length; ++var4) {
            ShellFolderColumnInfo var5 = var2[var4];
            TableColumn var6 = new TableColumn(var4);
            String var7 = var5.getTitle();
            if (var7 != null && var7.startsWith("FileChooser.") && var7.endsWith("HeaderText")) {
               String var8 = UIManager.getString(var7, (Locale)var1.getLocale());
               if (var8 != null) {
                  var7 = var8;
               }
            }

            var6.setHeaderValue(var7);
            Integer var9 = var5.getWidth();
            if (var9 != null) {
               var6.setPreferredWidth(var9);
            }

            var3.addColumn(var6);
         }

         if (!this.readOnly && var3.getColumnCount() > 0) {
            var3.getColumn(0).setCellEditor(this.getDetailsTableCellEditor());
         }

         var1.setColumnModel(var3);
      }

   }

   private FilePane.DetailsTableRowSorter getRowSorter() {
      if (this.rowSorter == null) {
         this.rowSorter = new FilePane.DetailsTableRowSorter();
      }

      return this.rowSorter;
   }

   private FilePane.DetailsTableCellEditor getDetailsTableCellEditor() {
      if (this.tableCellEditor == null) {
         this.tableCellEditor = new FilePane.DetailsTableCellEditor(new JTextField());
      }

      return this.tableCellEditor;
   }

   public JPanel createDetailsView() {
      final JFileChooser var1 = this.getFileChooser();
      JPanel var2 = new JPanel(new BorderLayout());
      JTable var3 = new JTable(this.getDetailsTableModel()) {
         protected boolean processKeyBinding(KeyStroke var1x, KeyEvent var2, int var3, boolean var4) {
            if (var2.getKeyCode() == 27 && this.getCellEditor() == null) {
               var1.dispatchEvent(var2);
               return true;
            } else {
               return super.processKeyBinding(var1x, var2, var3, var4);
            }
         }

         public void tableChanged(TableModelEvent var1x) {
            super.tableChanged(var1x);
            if (var1x.getFirstRow() == -1) {
               FilePane.this.updateDetailsColumnModel(this);
            }

         }
      };
      var3.setRowSorter(this.getRowSorter());
      var3.setAutoCreateColumnsFromModel(false);
      var3.setComponentOrientation(var1.getComponentOrientation());
      var3.setAutoResizeMode(0);
      var3.setShowGrid(false);
      var3.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
      var3.addKeyListener(this.detailsKeyListener);
      Font var4 = this.list.getFont();
      var3.setFont(var4);
      var3.setIntercellSpacing(new Dimension(0, 0));
      FilePane.AlignableTableHeaderRenderer var5 = new FilePane.AlignableTableHeaderRenderer(var3.getTableHeader().getDefaultRenderer());
      var3.getTableHeader().setDefaultRenderer(var5);
      FilePane.DetailsTableCellRenderer var6 = new FilePane.DetailsTableCellRenderer(var1);
      var3.setDefaultRenderer(Object.class, var6);
      var3.getColumnModel().getSelectionModel().setSelectionMode(0);
      var3.addMouseListener(this.getMouseHandler());
      var3.putClientProperty("Table.isFileList", Boolean.TRUE);
      if (this.listViewWindowsStyle) {
         var3.addFocusListener(repaintListener);
      }

      ActionMap var7 = SwingUtilities.getUIActionMap(var3);
      var7.remove("selectNextRowCell");
      var7.remove("selectPreviousRowCell");
      var7.remove("selectNextColumnCell");
      var7.remove("selectPreviousColumnCell");
      var3.setFocusTraversalKeys(0, (Set)null);
      var3.setFocusTraversalKeys(1, (Set)null);
      JScrollPane var8 = new JScrollPane(var3);
      var8.setComponentOrientation(var1.getComponentOrientation());
      LookAndFeel.installColors(var8.getViewport(), "Table.background", "Table.foreground");
      var8.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent var1) {
            JScrollPane var2 = (JScrollPane)var1.getComponent();
            FilePane.this.fixNameColumnWidth(var2.getViewport().getSize().width);
            var2.removeComponentListener(this);
         }
      });
      var8.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent var1) {
            JScrollPane var2 = (JScrollPane)var1.getComponent();
            JTable var3 = (JTable)var2.getViewport().getView();
            if (!var1.isShiftDown() || var3.getSelectionModel().getSelectionMode() == 0) {
               FilePane.this.clearSelection();
               TableCellEditor var4 = var3.getCellEditor();
               if (var4 != null) {
                  var4.stopCellEditing();
               }
            }

         }
      });
      var3.setForeground(this.list.getForeground());
      var3.setBackground(this.list.getBackground());
      if (this.listViewBorder != null) {
         var8.setBorder(this.listViewBorder);
      }

      var2.add(var8, "Center");
      this.detailsTableModel.fireTableStructureChanged();
      var3.putClientProperty("AccessibleName", this.filesDetailsAccessibleName);
      return var2;
   }

   private void fixNameColumnWidth(int var1) {
      TableColumn var2 = this.detailsTable.getColumnModel().getColumn(0);
      int var3 = this.detailsTable.getPreferredSize().width;
      if (var3 < var1) {
         var2.setPreferredWidth(var2.getPreferredWidth() + var1 - var3);
      }

   }

   public ListSelectionListener createListSelectionListener() {
      return this.fileChooserUIAccessor.createListSelectionListener();
   }

   private int getEditIndex() {
      return this.lastIndex;
   }

   private void setEditIndex(int var1) {
      this.lastIndex = var1;
   }

   private void resetEditIndex() {
      this.lastIndex = -1;
   }

   private void cancelEdit() {
      if (this.editFile != null) {
         this.editFile = null;
         this.list.remove(this.editCell);
         this.repaint();
      } else if (this.detailsTable != null && this.detailsTable.isEditing()) {
         this.detailsTable.getCellEditor().cancelCellEditing();
      }

   }

   private void editFileName(int var1) {
      JFileChooser var2 = this.getFileChooser();
      File var3 = var2.getCurrentDirectory();
      if (!this.readOnly && this.canWrite(var3)) {
         this.ensureIndexIsVisible(var1);
         switch(this.viewType) {
         case 0:
            this.editFile = (File)this.getModel().getElementAt(this.getRowSorter().convertRowIndexToModel(var1));
            Rectangle var4 = this.list.getCellBounds(var1, var1);
            if (this.editCell == null) {
               this.editCell = new JTextField();
               this.editCell.setName("Tree.cellEditor");
               this.editCell.addActionListener(new FilePane.EditActionListener());
               this.editCell.addFocusListener(this.editorFocusListener);
               this.editCell.setNextFocusableComponent(this.list);
            }

            this.list.add(this.editCell);
            this.editCell.setText(var2.getName(this.editFile));
            ComponentOrientation var5 = this.list.getComponentOrientation();
            this.editCell.setComponentOrientation(var5);
            Icon var6 = var2.getIcon(this.editFile);
            int var7 = var6 == null ? 20 : var6.getIconWidth() + 4;
            if (var5.isLeftToRight()) {
               this.editCell.setBounds(var7 + var4.x, var4.y, var4.width - var7, var4.height);
            } else {
               this.editCell.setBounds(var4.x, var4.y, var4.width - var7, var4.height);
            }

            this.editCell.requestFocus();
            this.editCell.selectAll();
            break;
         case 1:
            this.detailsTable.editCellAt(var1, 0);
         }

      }
   }

   private void applyEdit() {
      if (this.editFile != null && this.editFile.exists()) {
         JFileChooser var1 = this.getFileChooser();
         String var2 = var1.getName(this.editFile);
         String var3 = this.editFile.getName();
         String var4 = this.editCell.getText().trim();
         if (!var4.equals(var2)) {
            String var5 = var4;
            int var6 = var3.length();
            int var7 = var2.length();
            if (var6 > var7 && var3.charAt(var7) == '.') {
               var5 = var4 + var3.substring(var7);
            }

            FileSystemView var8 = var1.getFileSystemView();
            File var9 = var8.createFileObject(this.editFile.getParentFile(), var5);
            if (var9.exists()) {
               JOptionPane.showMessageDialog(var1, MessageFormat.format(this.renameErrorFileExistsText, var3), this.renameErrorTitleText, 0);
            } else if (this.getModel().renameFile(this.editFile, var9)) {
               if (var8.isParent(var1.getCurrentDirectory(), var9)) {
                  if (var1.isMultiSelectionEnabled()) {
                     var1.setSelectedFiles(new File[]{var9});
                  } else {
                     var1.setSelectedFile(var9);
                  }
               }
            } else {
               JOptionPane.showMessageDialog(var1, MessageFormat.format(this.renameErrorText, var3), this.renameErrorTitleText, 0);
            }
         }
      }

      if (this.detailsTable != null && this.detailsTable.isEditing()) {
         this.detailsTable.getCellEditor().stopCellEditing();
      }

      this.cancelEdit();
   }

   public Action getNewFolderAction() {
      if (!this.readOnly && this.newFolderAction == null) {
         this.newFolderAction = new AbstractAction(this.newFolderActionLabelText) {
            private Action basicNewFolderAction;

            {
               this.putValue("ActionCommandKey", "New Folder");
               File var3 = FilePane.this.getFileChooser().getCurrentDirectory();
               if (var3 != null) {
                  this.setEnabled(FilePane.this.canWrite(var3));
               }

            }

            public void actionPerformed(ActionEvent var1) {
               if (this.basicNewFolderAction == null) {
                  this.basicNewFolderAction = FilePane.this.fileChooserUIAccessor.getNewFolderAction();
               }

               JFileChooser var2 = FilePane.this.getFileChooser();
               File var3 = var2.getSelectedFile();
               this.basicNewFolderAction.actionPerformed(var1);
               File var4 = var2.getSelectedFile();
               if (var4 != null && !var4.equals(var3) && var4.isDirectory()) {
                  FilePane.this.newFolderFile = var4;
               }

            }
         };
      }

      return this.newFolderAction;
   }

   void setFileSelected() {
      int var3;
      int var4;
      if (this.getFileChooser().isMultiSelectionEnabled() && !this.isDirectorySelected()) {
         File[] var11 = this.getFileChooser().getSelectedFiles();
         Object[] var12 = this.list.getSelectedValues();
         this.listSelectionModel.setValueIsAdjusting(true);

         try {
            var3 = this.listSelectionModel.getLeadSelectionIndex();
            var4 = this.listSelectionModel.getAnchorSelectionIndex();
            Arrays.sort((Object[])var11);
            Arrays.sort(var12);
            int var5 = 0;
            int var6 = 0;

            while(var5 < var11.length && var6 < var12.length) {
               int var7 = var11[var5].compareTo((File)var12[var6]);
               if (var7 < 0) {
                  this.doSelectFile(var11[var5++]);
               } else if (var7 > 0) {
                  this.doDeselectFile(var12[var6++]);
               } else {
                  ++var5;
                  ++var6;
               }
            }

            while(var5 < var11.length) {
               this.doSelectFile(var11[var5++]);
            }

            while(var6 < var12.length) {
               this.doDeselectFile(var12[var6++]);
            }

            if (this.listSelectionModel instanceof DefaultListSelectionModel) {
               ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(var3);
               this.listSelectionModel.setAnchorSelectionIndex(var4);
            }
         } finally {
            this.listSelectionModel.setValueIsAdjusting(false);
         }
      } else {
         JFileChooser var1 = this.getFileChooser();
         File var2;
         if (this.isDirectorySelected()) {
            var2 = this.getDirectory();
         } else {
            var2 = var1.getSelectedFile();
         }

         if (var2 != null && (var3 = this.getModel().indexOf(var2)) >= 0) {
            var4 = this.getRowSorter().convertRowIndexToView(var3);
            this.listSelectionModel.setSelectionInterval(var4, var4);
            this.ensureIndexIsVisible(var4);
         } else {
            this.clearSelection();
         }
      }

   }

   private void doSelectFile(File var1) {
      int var2 = this.getModel().indexOf(var1);
      if (var2 >= 0) {
         var2 = this.getRowSorter().convertRowIndexToView(var2);
         this.listSelectionModel.addSelectionInterval(var2, var2);
      }

   }

   private void doDeselectFile(Object var1) {
      int var2 = this.getRowSorter().convertRowIndexToView(this.getModel().indexOf(var1));
      this.listSelectionModel.removeSelectionInterval(var2, var2);
   }

   private void doSelectedFileChanged(PropertyChangeEvent var1) {
      this.applyEdit();
      File var2 = (File)var1.getNewValue();
      JFileChooser var3 = this.getFileChooser();
      if (var2 != null && (var3.isFileSelectionEnabled() && !var2.isDirectory() || var2.isDirectory() && var3.isDirectorySelectionEnabled())) {
         this.setFileSelected();
      }

   }

   private void doSelectedFilesChanged(PropertyChangeEvent var1) {
      this.applyEdit();
      File[] var2 = (File[])((File[])var1.getNewValue());
      JFileChooser var3 = this.getFileChooser();
      if (var2 != null && var2.length > 0 && (var2.length > 1 || var3.isDirectorySelectionEnabled() || !var2[0].isDirectory())) {
         this.setFileSelected();
      }

   }

   private void doDirectoryChanged(PropertyChangeEvent var1) {
      this.getDetailsTableModel().updateColumnInfo();
      JFileChooser var2 = this.getFileChooser();
      FileSystemView var3 = var2.getFileSystemView();
      this.applyEdit();
      this.resetEditIndex();
      this.ensureIndexIsVisible(0);
      File var4 = var2.getCurrentDirectory();
      if (var4 != null) {
         if (!this.readOnly) {
            this.getNewFolderAction().setEnabled(this.canWrite(var4));
         }

         this.fileChooserUIAccessor.getChangeToParentDirectoryAction().setEnabled(!var3.isRoot(var4));
      }

      if (this.list != null) {
         this.list.clearSelection();
      }

   }

   private void doFilterChanged(PropertyChangeEvent var1) {
      this.applyEdit();
      this.resetEditIndex();
      this.clearSelection();
   }

   private void doFileSelectionModeChanged(PropertyChangeEvent var1) {
      this.applyEdit();
      this.resetEditIndex();
      this.clearSelection();
   }

   private void doMultiSelectionChanged(PropertyChangeEvent var1) {
      if (this.getFileChooser().isMultiSelectionEnabled()) {
         this.listSelectionModel.setSelectionMode(2);
      } else {
         this.listSelectionModel.setSelectionMode(0);
         this.clearSelection();
         this.getFileChooser().setSelectedFiles((File[])null);
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (this.viewType == -1) {
         this.setViewType(0);
      }

      String var2 = var1.getPropertyName();
      if (var2.equals("SelectedFileChangedProperty")) {
         this.doSelectedFileChanged(var1);
      } else if (var2.equals("SelectedFilesChangedProperty")) {
         this.doSelectedFilesChanged(var1);
      } else if (var2.equals("directoryChanged")) {
         this.doDirectoryChanged(var1);
      } else if (var2.equals("fileFilterChanged")) {
         this.doFilterChanged(var1);
      } else if (var2.equals("fileSelectionChanged")) {
         this.doFileSelectionModeChanged(var1);
      } else if (var2.equals("MultiSelectionEnabledChangedProperty")) {
         this.doMultiSelectionChanged(var1);
      } else if (var2.equals("CancelSelection")) {
         this.applyEdit();
      } else if (var2.equals("busy")) {
         this.setCursor((Boolean)var1.getNewValue() ? waitCursor : null);
      } else if (var2.equals("componentOrientation")) {
         ComponentOrientation var3 = (ComponentOrientation)var1.getNewValue();
         JFileChooser var4 = (JFileChooser)var1.getSource();
         if (var3 != var1.getOldValue()) {
            var4.applyComponentOrientation(var3);
         }

         if (this.detailsTable != null) {
            this.detailsTable.setComponentOrientation(var3);
            this.detailsTable.getParent().getParent().setComponentOrientation(var3);
         }
      }

   }

   private void ensureIndexIsVisible(int var1) {
      if (var1 >= 0) {
         if (this.list != null) {
            this.list.ensureIndexIsVisible(var1);
         }

         if (this.detailsTable != null) {
            this.detailsTable.scrollRectToVisible(this.detailsTable.getCellRect(var1, 0, true));
         }
      }

   }

   public void ensureFileIsVisible(JFileChooser var1, File var2) {
      int var3 = this.getModel().indexOf(var2);
      if (var3 >= 0) {
         this.ensureIndexIsVisible(this.getRowSorter().convertRowIndexToView(var3));
      }

   }

   public void rescanCurrentDirectory() {
      this.getModel().validateFileCache();
   }

   public void clearSelection() {
      if (this.listSelectionModel != null) {
         this.listSelectionModel.clearSelection();
         if (this.listSelectionModel instanceof DefaultListSelectionModel) {
            ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(0);
            this.listSelectionModel.setAnchorSelectionIndex(0);
         }
      }

   }

   public JMenu getViewMenu() {
      if (this.viewMenu == null) {
         this.viewMenu = new JMenu(this.viewMenuLabelText);
         ButtonGroup var1 = new ButtonGroup();

         for(int var2 = 0; var2 < 2; ++var2) {
            JRadioButtonMenuItem var3 = new JRadioButtonMenuItem(new FilePane.ViewTypeAction(var2));
            var1.add(var3);
            this.viewMenu.add((JMenuItem)var3);
         }

         this.updateViewMenu();
      }

      return this.viewMenu;
   }

   private void updateViewMenu() {
      if (this.viewMenu != null) {
         Component[] var1 = this.viewMenu.getMenuComponents();
         Component[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            if (var5 instanceof JRadioButtonMenuItem) {
               JRadioButtonMenuItem var6 = (JRadioButtonMenuItem)var5;
               if (((FilePane.ViewTypeAction)var6.getAction()).viewType == this.viewType) {
                  var6.setSelected(true);
               }
            }
         }
      }

   }

   public JPopupMenu getComponentPopupMenu() {
      JPopupMenu var1 = this.getFileChooser().getComponentPopupMenu();
      if (var1 != null) {
         return var1;
      } else {
         JMenu var2 = this.getViewMenu();
         if (this.contextMenu == null) {
            this.contextMenu = new JPopupMenu();
            if (var2 != null) {
               this.contextMenu.add((JMenuItem)var2);
               if (this.listViewWindowsStyle) {
                  this.contextMenu.addSeparator();
               }
            }

            ActionMap var3 = this.getActionMap();
            Action var4 = var3.get("refresh");
            Action var5 = var3.get("New Folder");
            if (var4 != null) {
               this.contextMenu.add(var4);
               if (this.listViewWindowsStyle && var5 != null) {
                  this.contextMenu.addSeparator();
               }
            }

            if (var5 != null) {
               this.contextMenu.add(var5);
            }
         }

         if (var2 != null) {
            var2.getPopupMenu().setInvoker(var2);
         }

         return this.contextMenu;
      }
   }

   protected FilePane.Handler getMouseHandler() {
      if (this.handler == null) {
         this.handler = new FilePane.Handler();
      }

      return this.handler;
   }

   protected boolean isDirectorySelected() {
      return this.fileChooserUIAccessor.isDirectorySelected();
   }

   protected File getDirectory() {
      return this.fileChooserUIAccessor.getDirectory();
   }

   private Component findChildComponent(Container var1, Class var2) {
      int var3 = var1.getComponentCount();

      for(int var4 = 0; var4 < var3; ++var4) {
         Component var5 = var1.getComponent(var4);
         if (var2.isInstance(var5)) {
            return var5;
         }

         if (var5 instanceof Container) {
            Component var6 = this.findChildComponent((Container)var5, var2);
            if (var6 != null) {
               return var6;
            }
         }
      }

      return null;
   }

   public boolean canWrite(File var1) {
      if (!var1.exists()) {
         return false;
      } else {
         try {
            if (var1 instanceof ShellFolder) {
               return var1.canWrite();
            } else if (usesShellFolder(this.getFileChooser())) {
               try {
                  return ShellFolder.getShellFolder(var1).canWrite();
               } catch (FileNotFoundException var3) {
                  return false;
               }
            } else {
               return var1.canWrite();
            }
         } catch (SecurityException var4) {
            return false;
         }
      }
   }

   public static boolean usesShellFolder(JFileChooser var0) {
      Boolean var1 = (Boolean)var0.getClientProperty("FileChooser.useShellFolder");
      return var1 == null ? var0.getFileSystemView().equals(FileSystemView.getFileSystemView()) : var1;
   }

   public interface FileChooserUIAccessor {
      JFileChooser getFileChooser();

      BasicDirectoryModel getModel();

      JPanel createList();

      JPanel createDetailsView();

      boolean isDirectorySelected();

      File getDirectory();

      Action getApproveSelectionAction();

      Action getChangeToParentDirectoryAction();

      Action getNewFolderAction();

      MouseListener createDoubleClickListener(JList var1);

      ListSelectionListener createListSelectionListener();
   }

   private class Handler implements MouseListener {
      private MouseListener doubleClickListener;

      private Handler() {
      }

      public void mouseClicked(MouseEvent var1) {
         JComponent var2 = (JComponent)var1.getSource();
         int var3;
         if (var2 instanceof JList) {
            var3 = SwingUtilities2.loc2IndexFileList(FilePane.this.list, var1.getPoint());
         } else {
            if (!(var2 instanceof JTable)) {
               return;
            }

            JTable var4 = (JTable)var2;
            Point var5 = var1.getPoint();
            var3 = var4.rowAtPoint(var5);
            boolean var6 = SwingUtilities2.pointOutsidePrefSize(var4, var3, var4.columnAtPoint(var5), var5);
            if (var6 && !FilePane.this.fullRowSelection) {
               return;
            }

            if (var3 >= 0 && FilePane.this.list != null && FilePane.this.listSelectionModel.isSelectedIndex(var3)) {
               Rectangle var7 = FilePane.this.list.getCellBounds(var3, var3);
               MouseEvent var8 = new MouseEvent(FilePane.this.list, var1.getID(), var1.getWhen(), var1.getModifiers(), var7.x + 1, var7.y + var7.height / 2, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), var1.getButton());
               AWTAccessor.MouseEventAccessor var9 = AWTAccessor.getMouseEventAccessor();
               var9.setCausedByTouchEvent(var8, var9.isCausedByTouchEvent(var1));
               var1 = var8;
            }
         }

         if (var3 >= 0 && SwingUtilities.isLeftMouseButton(var1)) {
            JFileChooser var10 = FilePane.this.getFileChooser();
            if (var1.getClickCount() == 1 && var2 instanceof JList) {
               if ((!var10.isMultiSelectionEnabled() || var10.getSelectedFiles().length <= 1) && var3 >= 0 && FilePane.this.listSelectionModel.isSelectedIndex(var3) && FilePane.this.getEditIndex() == var3 && FilePane.this.editFile == null) {
                  FilePane.this.editFileName(var3);
               } else if (var3 >= 0) {
                  FilePane.this.setEditIndex(var3);
               } else {
                  FilePane.this.resetEditIndex();
               }
            } else if (var1.getClickCount() == 2) {
               FilePane.this.resetEditIndex();
            }
         }

         if (this.getDoubleClickListener() != null) {
            this.getDoubleClickListener().mouseClicked(var1);
         }

      }

      public void mouseEntered(MouseEvent var1) {
         JComponent var2 = (JComponent)var1.getSource();
         if (var2 instanceof JTable) {
            JTable var3 = (JTable)var1.getSource();
            TransferHandler var4 = FilePane.this.getFileChooser().getTransferHandler();
            TransferHandler var5 = var3.getTransferHandler();
            if (var4 != var5) {
               var3.setTransferHandler(var4);
            }

            boolean var6 = FilePane.this.getFileChooser().getDragEnabled();
            if (var6 != var3.getDragEnabled()) {
               var3.setDragEnabled(var6);
            }
         } else if (var2 instanceof JList && this.getDoubleClickListener() != null) {
            this.getDoubleClickListener().mouseEntered(var1);
         }

      }

      public void mouseExited(MouseEvent var1) {
         if (var1.getSource() instanceof JList && this.getDoubleClickListener() != null) {
            this.getDoubleClickListener().mouseExited(var1);
         }

      }

      public void mousePressed(MouseEvent var1) {
         if (var1.getSource() instanceof JList && this.getDoubleClickListener() != null) {
            this.getDoubleClickListener().mousePressed(var1);
         }

      }

      public void mouseReleased(MouseEvent var1) {
         if (var1.getSource() instanceof JList && this.getDoubleClickListener() != null) {
            this.getDoubleClickListener().mouseReleased(var1);
         }

      }

      private MouseListener getDoubleClickListener() {
         if (this.doubleClickListener == null && FilePane.this.list != null) {
            this.doubleClickListener = FilePane.this.fileChooserUIAccessor.createDoubleClickListener(FilePane.this.list);
         }

         return this.doubleClickListener;
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   protected class FileRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         if (FilePane.this.listViewWindowsStyle && !var1.isFocusOwner()) {
            var4 = false;
         }

         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         File var6 = (File)var2;
         String var7 = FilePane.this.getFileChooser().getName(var6);
         this.setText(var7);
         this.setFont(var1.getFont());
         Icon var8 = FilePane.this.getFileChooser().getIcon(var6);
         if (var8 != null) {
            this.setIcon(var8);
         } else if (FilePane.this.getFileChooser().getFileSystemView().isTraversable(var6)) {
            this.setText(var7 + File.separator);
         }

         return this;
      }
   }

   class EditActionListener implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         FilePane.this.applyEdit();
      }
   }

   private class DelayedSelectionUpdater implements Runnable {
      File editFile;

      DelayedSelectionUpdater() {
         this((File)null);
      }

      DelayedSelectionUpdater(File var2) {
         this.editFile = var2;
         if (FilePane.this.isShowing()) {
            SwingUtilities.invokeLater(this);
         }

      }

      public void run() {
         FilePane.this.setFileSelected();
         if (this.editFile != null) {
            FilePane.this.editFileName(FilePane.this.getRowSorter().convertRowIndexToView(FilePane.this.getModel().indexOf(this.editFile)));
            this.editFile = null;
         }

      }
   }

   private class AlignableTableHeaderRenderer implements TableCellRenderer {
      TableCellRenderer wrappedRenderer;

      public AlignableTableHeaderRenderer(TableCellRenderer var2) {
         this.wrappedRenderer = var2;
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         Component var7 = this.wrappedRenderer.getTableCellRendererComponent(var1, var2, var3, var4, var5, var6);
         int var8 = var1.convertColumnIndexToModel(var6);
         ShellFolderColumnInfo var9 = FilePane.this.detailsTableModel.getColumns()[var8];
         Integer var10 = var9.getAlignment();
         if (var10 == null) {
            var10 = 0;
         }

         if (var7 instanceof JLabel) {
            ((JLabel)var7).setHorizontalAlignment(var10);
         }

         return var7;
      }
   }

   class DetailsTableCellRenderer extends DefaultTableCellRenderer {
      JFileChooser chooser;
      DateFormat df;

      DetailsTableCellRenderer(JFileChooser var2) {
         this.chooser = var2;
         this.df = DateFormat.getDateTimeInstance(3, 3, var2.getLocale());
      }

      public void setBounds(int var1, int var2, int var3, int var4) {
         if (this.getHorizontalAlignment() == 10 && !FilePane.this.fullRowSelection) {
            var3 = Math.min(var3, this.getPreferredSize().width + 4);
         } else {
            var1 -= 4;
         }

         super.setBounds(var1, var2, var3, var4);
      }

      public Insets getInsets(Insets var1) {
         var1 = super.getInsets(var1);
         var1.left += 4;
         var1.right += 4;
         return var1;
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         if ((var1.convertColumnIndexToModel(var6) != 0 || FilePane.this.listViewWindowsStyle && !var1.isFocusOwner()) && !FilePane.this.fullRowSelection) {
            var3 = false;
         }

         super.getTableCellRendererComponent(var1, var2, var3, var4, var5, var6);
         this.setIcon((Icon)null);
         int var7 = var1.convertColumnIndexToModel(var6);
         ShellFolderColumnInfo var8 = FilePane.this.detailsTableModel.getColumns()[var7];
         Integer var9 = var8.getAlignment();
         if (var9 == null) {
            var9 = var2 instanceof Number ? 4 : 10;
         }

         this.setHorizontalAlignment(var9);
         String var10;
         if (var2 == null) {
            var10 = "";
         } else if (var2 instanceof File) {
            File var11 = (File)var2;
            var10 = this.chooser.getName(var11);
            Icon var12 = this.chooser.getIcon(var11);
            this.setIcon(var12);
         } else if (var2 instanceof Long) {
            long var13 = (Long)var2 / 1024L;
            if (FilePane.this.listViewWindowsStyle) {
               var10 = MessageFormat.format(FilePane.this.kiloByteString, var13 + 1L);
            } else if (var13 < 1024L) {
               var10 = MessageFormat.format(FilePane.this.kiloByteString, var13 == 0L ? 1L : var13);
            } else {
               var13 /= 1024L;
               if (var13 < 1024L) {
                  var10 = MessageFormat.format(FilePane.this.megaByteString, var13);
               } else {
                  var13 /= 1024L;
                  var10 = MessageFormat.format(FilePane.this.gigaByteString, var13);
               }
            }
         } else if (var2 instanceof Date) {
            var10 = this.df.format((Date)var2);
         } else {
            var10 = var2.toString();
         }

         this.setText(var10);
         return this;
      }
   }

   private class DetailsTableCellEditor extends DefaultCellEditor {
      private final JTextField tf;

      public DetailsTableCellEditor(JTextField var2) {
         super(var2);
         this.tf = var2;
         var2.setName("Table.editor");
         var2.addFocusListener(FilePane.this.editorFocusListener);
      }

      public Component getTableCellEditorComponent(JTable var1, Object var2, boolean var3, int var4, int var5) {
         Component var6 = super.getTableCellEditorComponent(var1, var2, var3, var4, var5);
         if (var2 instanceof File) {
            this.tf.setText(FilePane.this.getFileChooser().getName((File)var2));
            this.tf.selectAll();
         }

         return var6;
      }
   }

   private class DirectoriesFirstComparatorWrapper implements Comparator<File> {
      private Comparator comparator;
      private int column;

      public DirectoriesFirstComparatorWrapper(int var2, Comparator var3) {
         this.column = var2;
         this.comparator = var3;
      }

      public int compare(File var1, File var2) {
         if (var1 != null && var2 != null) {
            boolean var3 = FilePane.this.getFileChooser().isTraversable(var1);
            boolean var4 = FilePane.this.getFileChooser().isTraversable(var2);
            if (var3 && !var4) {
               return -1;
            }

            if (!var3 && var4) {
               return 1;
            }
         }

         return FilePane.this.detailsTableModel.getColumns()[this.column].isCompareByColumn() ? this.comparator.compare(FilePane.this.getDetailsTableModel().getFileColumnValue(var1, this.column), FilePane.this.getDetailsTableModel().getFileColumnValue(var2, this.column)) : this.comparator.compare(var1, var2);
      }
   }

   private class DetailsTableRowSorter extends TableRowSorter<TableModel> {
      public DetailsTableRowSorter() {
         this.setModelWrapper(new FilePane.DetailsTableRowSorter.SorterModelWrapper());
      }

      public void updateComparators(ShellFolderColumnInfo[] var1) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            Object var3 = var1[var2].getComparator();
            if (var3 != null) {
               var3 = FilePane.this.new DirectoriesFirstComparatorWrapper(var2, (Comparator)var3);
            }

            this.setComparator(var2, (Comparator)var3);
         }

      }

      public void sort() {
         ShellFolder.invoke(new Callable<Void>() {
            public Void call() {
               FilePane.DetailsTableRowSorter.super.sort();
               return null;
            }
         });
      }

      public void modelStructureChanged() {
         super.modelStructureChanged();
         this.updateComparators(FilePane.this.detailsTableModel.getColumns());
      }

      private class SorterModelWrapper extends DefaultRowSorter.ModelWrapper<TableModel, Integer> {
         private SorterModelWrapper() {
         }

         public TableModel getModel() {
            return FilePane.this.getDetailsTableModel();
         }

         public int getColumnCount() {
            return FilePane.this.getDetailsTableModel().getColumnCount();
         }

         public int getRowCount() {
            return FilePane.this.getDetailsTableModel().getRowCount();
         }

         public Object getValueAt(int var1, int var2) {
            return FilePane.this.getModel().getElementAt(var1);
         }

         public Integer getIdentifier(int var1) {
            return var1;
         }

         // $FF: synthetic method
         SorterModelWrapper(Object var2) {
            this();
         }
      }
   }

   class DetailsTableModel extends AbstractTableModel implements ListDataListener {
      JFileChooser chooser;
      BasicDirectoryModel directoryModel;
      ShellFolderColumnInfo[] columns;
      int[] columnMap;

      DetailsTableModel(JFileChooser var2) {
         this.chooser = var2;
         this.directoryModel = FilePane.this.getModel();
         this.directoryModel.addListDataListener(this);
         this.updateColumnInfo();
      }

      void updateColumnInfo() {
         Object var1 = this.chooser.getCurrentDirectory();
         if (var1 != null && FilePane.usesShellFolder(this.chooser)) {
            try {
               var1 = ShellFolder.getShellFolder((File)var1);
            } catch (FileNotFoundException var6) {
            }
         }

         ShellFolderColumnInfo[] var2 = ShellFolder.getFolderColumns((File)var1);
         ArrayList var3 = new ArrayList();
         this.columnMap = new int[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            ShellFolderColumnInfo var5 = var2[var4];
            if (var5.isVisible()) {
               this.columnMap[var3.size()] = var4;
               var3.add(var5);
            }
         }

         this.columns = new ShellFolderColumnInfo[var3.size()];
         var3.toArray(this.columns);
         this.columnMap = Arrays.copyOf(this.columnMap, this.columns.length);
         List var7 = FilePane.this.rowSorter == null ? null : FilePane.this.rowSorter.getSortKeys();
         this.fireTableStructureChanged();
         this.restoreSortKeys(var7);
      }

      private void restoreSortKeys(List<? extends RowSorter.SortKey> var1) {
         if (var1 != null) {
            for(int var2 = 0; var2 < var1.size(); ++var2) {
               RowSorter.SortKey var3 = (RowSorter.SortKey)var1.get(var2);
               if (var3.getColumn() >= this.columns.length) {
                  var1 = null;
                  break;
               }
            }

            if (var1 != null) {
               FilePane.this.rowSorter.setSortKeys(var1);
            }
         }

      }

      public int getRowCount() {
         return this.directoryModel.getSize();
      }

      public int getColumnCount() {
         return this.columns.length;
      }

      public Object getValueAt(int var1, int var2) {
         return this.getFileColumnValue((File)this.directoryModel.getElementAt(var1), var2);
      }

      private Object getFileColumnValue(File var1, int var2) {
         return var2 == 0 ? var1 : ShellFolder.getFolderColumnValue(var1, this.columnMap[var2]);
      }

      public void setValueAt(Object var1, int var2, int var3) {
         if (var3 == 0) {
            final JFileChooser var4 = FilePane.this.getFileChooser();
            File var5 = (File)this.getValueAt(var2, var3);
            if (var5 != null) {
               String var6 = var4.getName(var5);
               String var7 = var5.getName();
               String var8 = ((String)var1).trim();
               if (!var8.equals(var6)) {
                  String var9 = var8;
                  int var10 = var7.length();
                  int var11 = var6.length();
                  if (var10 > var11 && var7.charAt(var11) == '.') {
                     var9 = var8 + var7.substring(var11);
                  }

                  FileSystemView var12 = var4.getFileSystemView();
                  final File var13 = var12.createFileObject(var5.getParentFile(), var9);
                  if (var13.exists()) {
                     JOptionPane.showMessageDialog(var4, MessageFormat.format(FilePane.this.renameErrorFileExistsText, var7), FilePane.this.renameErrorTitleText, 0);
                  } else if (FilePane.this.getModel().renameFile(var5, var13)) {
                     if (var12.isParent(var4.getCurrentDirectory(), var13)) {
                        SwingUtilities.invokeLater(new Runnable() {
                           public void run() {
                              if (var4.isMultiSelectionEnabled()) {
                                 var4.setSelectedFiles(new File[]{var13});
                              } else {
                                 var4.setSelectedFile(var13);
                              }

                           }
                        });
                     }
                  } else {
                     JOptionPane.showMessageDialog(var4, MessageFormat.format(FilePane.this.renameErrorText, var7), FilePane.this.renameErrorTitleText, 0);
                  }
               }
            }
         }

      }

      public boolean isCellEditable(int var1, int var2) {
         File var3 = FilePane.this.getFileChooser().getCurrentDirectory();
         return !FilePane.this.readOnly && var2 == 0 && FilePane.this.canWrite(var3);
      }

      public void contentsChanged(ListDataEvent var1) {
         FilePane.this.new DelayedSelectionUpdater();
         this.fireTableDataChanged();
      }

      public void intervalAdded(ListDataEvent var1) {
         int var2 = var1.getIndex0();
         int var3 = var1.getIndex1();
         if (var2 == var3) {
            File var4 = (File)FilePane.this.getModel().getElementAt(var2);
            if (var4.equals(FilePane.this.newFolderFile)) {
               FilePane.this.new DelayedSelectionUpdater(var4);
               FilePane.this.newFolderFile = null;
            }
         }

         this.fireTableRowsInserted(var1.getIndex0(), var1.getIndex1());
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.fireTableRowsDeleted(var1.getIndex0(), var1.getIndex1());
      }

      public ShellFolderColumnInfo[] getColumns() {
         return this.columns;
      }
   }

   private class SortableListModel extends AbstractListModel<Object> implements TableModelListener, RowSorterListener {
      public SortableListModel() {
         FilePane.this.getDetailsTableModel().addTableModelListener(this);
         FilePane.this.getRowSorter().addRowSorterListener(this);
      }

      public int getSize() {
         return FilePane.this.getModel().getSize();
      }

      public Object getElementAt(int var1) {
         return FilePane.this.getModel().getElementAt(FilePane.this.getRowSorter().convertRowIndexToModel(var1));
      }

      public void tableChanged(TableModelEvent var1) {
         this.fireContentsChanged(this, 0, this.getSize());
      }

      public void sorterChanged(RowSorterEvent var1) {
         this.fireContentsChanged(this, 0, this.getSize());
      }
   }

   class ViewTypeAction extends AbstractAction {
      private int viewType;

      ViewTypeAction(int var2) {
         super(FilePane.this.viewTypeActionNames[var2]);
         this.viewType = var2;
         String var3;
         switch(var2) {
         case 0:
            var3 = "viewTypeList";
            break;
         case 1:
            var3 = "viewTypeDetails";
            break;
         default:
            var3 = (String)this.getValue("Name");
         }

         this.putValue("ActionCommandKey", var3);
      }

      public void actionPerformed(ActionEvent var1) {
         FilePane.this.setViewType(this.viewType);
      }
   }
}
