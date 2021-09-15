package javax.swing;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;

public class JFileChooser extends JComponent implements Accessible {
   private static final String uiClassID = "FileChooserUI";
   public static final int OPEN_DIALOG = 0;
   public static final int SAVE_DIALOG = 1;
   public static final int CUSTOM_DIALOG = 2;
   public static final int CANCEL_OPTION = 1;
   public static final int APPROVE_OPTION = 0;
   public static final int ERROR_OPTION = -1;
   public static final int FILES_ONLY = 0;
   public static final int DIRECTORIES_ONLY = 1;
   public static final int FILES_AND_DIRECTORIES = 2;
   public static final String CANCEL_SELECTION = "CancelSelection";
   public static final String APPROVE_SELECTION = "ApproveSelection";
   public static final String APPROVE_BUTTON_TEXT_CHANGED_PROPERTY = "ApproveButtonTextChangedProperty";
   public static final String APPROVE_BUTTON_TOOL_TIP_TEXT_CHANGED_PROPERTY = "ApproveButtonToolTipTextChangedProperty";
   public static final String APPROVE_BUTTON_MNEMONIC_CHANGED_PROPERTY = "ApproveButtonMnemonicChangedProperty";
   public static final String CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY = "ControlButtonsAreShownChangedProperty";
   public static final String DIRECTORY_CHANGED_PROPERTY = "directoryChanged";
   public static final String SELECTED_FILE_CHANGED_PROPERTY = "SelectedFileChangedProperty";
   public static final String SELECTED_FILES_CHANGED_PROPERTY = "SelectedFilesChangedProperty";
   public static final String MULTI_SELECTION_ENABLED_CHANGED_PROPERTY = "MultiSelectionEnabledChangedProperty";
   public static final String FILE_SYSTEM_VIEW_CHANGED_PROPERTY = "FileSystemViewChanged";
   public static final String FILE_VIEW_CHANGED_PROPERTY = "fileViewChanged";
   public static final String FILE_HIDING_CHANGED_PROPERTY = "FileHidingChanged";
   public static final String FILE_FILTER_CHANGED_PROPERTY = "fileFilterChanged";
   public static final String FILE_SELECTION_MODE_CHANGED_PROPERTY = "fileSelectionChanged";
   public static final String ACCESSORY_CHANGED_PROPERTY = "AccessoryChangedProperty";
   public static final String ACCEPT_ALL_FILE_FILTER_USED_CHANGED_PROPERTY = "acceptAllFileFilterUsedChanged";
   public static final String DIALOG_TITLE_CHANGED_PROPERTY = "DialogTitleChangedProperty";
   public static final String DIALOG_TYPE_CHANGED_PROPERTY = "DialogTypeChangedProperty";
   public static final String CHOOSABLE_FILE_FILTER_CHANGED_PROPERTY = "ChoosableFileFilterChangedProperty";
   private String dialogTitle;
   private String approveButtonText;
   private String approveButtonToolTipText;
   private int approveButtonMnemonic;
   private Vector<FileFilter> filters;
   private JDialog dialog;
   private int dialogType;
   private int returnValue;
   private JComponent accessory;
   private FileView fileView;
   private boolean controlsShown;
   private boolean useFileHiding;
   private static final String SHOW_HIDDEN_PROP = "awt.file.showHiddenFiles";
   private transient PropertyChangeListener showFilesListener;
   private int fileSelectionMode;
   private boolean multiSelectionEnabled;
   private boolean useAcceptAllFileFilter;
   private boolean dragEnabled;
   private FileFilter fileFilter;
   private FileSystemView fileSystemView;
   private File currentDirectory;
   private File selectedFile;
   private File[] selectedFiles;
   protected AccessibleContext accessibleContext;

   public JFileChooser() {
      this((File)null, (FileSystemView)null);
   }

   public JFileChooser(String var1) {
      this(var1, (FileSystemView)null);
   }

   public JFileChooser(File var1) {
      this(var1, (FileSystemView)null);
   }

   public JFileChooser(FileSystemView var1) {
      this((File)null, var1);
   }

   public JFileChooser(File var1, FileSystemView var2) {
      this.dialogTitle = null;
      this.approveButtonText = null;
      this.approveButtonToolTipText = null;
      this.approveButtonMnemonic = 0;
      this.filters = new Vector(5);
      this.dialog = null;
      this.dialogType = 0;
      this.returnValue = -1;
      this.accessory = null;
      this.fileView = null;
      this.controlsShown = true;
      this.useFileHiding = true;
      this.showFilesListener = null;
      this.fileSelectionMode = 0;
      this.multiSelectionEnabled = false;
      this.useAcceptAllFileFilter = true;
      this.dragEnabled = false;
      this.fileFilter = null;
      this.fileSystemView = null;
      this.currentDirectory = null;
      this.selectedFile = null;
      this.accessibleContext = null;
      this.setup(var2);
      this.setCurrentDirectory(var1);
   }

   public JFileChooser(String var1, FileSystemView var2) {
      this.dialogTitle = null;
      this.approveButtonText = null;
      this.approveButtonToolTipText = null;
      this.approveButtonMnemonic = 0;
      this.filters = new Vector(5);
      this.dialog = null;
      this.dialogType = 0;
      this.returnValue = -1;
      this.accessory = null;
      this.fileView = null;
      this.controlsShown = true;
      this.useFileHiding = true;
      this.showFilesListener = null;
      this.fileSelectionMode = 0;
      this.multiSelectionEnabled = false;
      this.useAcceptAllFileFilter = true;
      this.dragEnabled = false;
      this.fileFilter = null;
      this.fileSystemView = null;
      this.currentDirectory = null;
      this.selectedFile = null;
      this.accessibleContext = null;
      this.setup(var2);
      if (var1 == null) {
         this.setCurrentDirectory((File)null);
      } else {
         this.setCurrentDirectory(this.fileSystemView.createFileObject(var1));
      }

   }

   protected void setup(FileSystemView var1) {
      this.installShowFilesListener();
      this.installHierarchyListener();
      if (var1 == null) {
         var1 = FileSystemView.getFileSystemView();
      }

      this.setFileSystemView(var1);
      this.updateUI();
      if (this.isAcceptAllFileFilterUsed()) {
         this.setFileFilter(this.getAcceptAllFileFilter());
      }

      this.enableEvents(16L);
   }

   private void installHierarchyListener() {
      this.addHierarchyListener(new HierarchyListener() {
         public void hierarchyChanged(HierarchyEvent var1) {
            if ((var1.getChangeFlags() & 1L) == 1L) {
               JFileChooser var2 = JFileChooser.this;
               JRootPane var3 = SwingUtilities.getRootPane(var2);
               if (var3 != null) {
                  var3.setDefaultButton(var2.getUI().getDefaultButton(var2));
               }
            }

         }
      });
   }

   private void installShowFilesListener() {
      Toolkit var1 = Toolkit.getDefaultToolkit();
      Object var2 = var1.getDesktopProperty("awt.file.showHiddenFiles");
      if (var2 instanceof Boolean) {
         this.useFileHiding = !(Boolean)var2;
         this.showFilesListener = new JFileChooser.WeakPCL(this);
         var1.addPropertyChangeListener("awt.file.showHiddenFiles", this.showFilesListener);
      }

   }

   public void setDragEnabled(boolean var1) {
      if (var1 && GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         this.dragEnabled = var1;
      }
   }

   public boolean getDragEnabled() {
      return this.dragEnabled;
   }

   public File getSelectedFile() {
      return this.selectedFile;
   }

   public void setSelectedFile(File var1) {
      File var2 = this.selectedFile;
      this.selectedFile = var1;
      if (this.selectedFile != null) {
         if (var1.isAbsolute() && !this.getFileSystemView().isParent(this.getCurrentDirectory(), this.selectedFile)) {
            this.setCurrentDirectory(this.selectedFile.getParentFile());
         }

         if (!this.isMultiSelectionEnabled() || this.selectedFiles == null || this.selectedFiles.length == 1) {
            this.ensureFileIsVisible(this.selectedFile);
         }
      }

      this.firePropertyChange("SelectedFileChangedProperty", var2, this.selectedFile);
   }

   public File[] getSelectedFiles() {
      return this.selectedFiles == null ? new File[0] : (File[])this.selectedFiles.clone();
   }

   public void setSelectedFiles(File[] var1) {
      File[] var2 = this.selectedFiles;
      if (var1 != null && var1.length != 0) {
         this.selectedFiles = (File[])var1.clone();
         this.setSelectedFile(this.selectedFiles[0]);
      } else {
         var1 = null;
         this.selectedFiles = null;
         this.setSelectedFile((File)null);
      }

      this.firePropertyChange("SelectedFilesChangedProperty", var2, var1);
   }

   public File getCurrentDirectory() {
      return this.currentDirectory;
   }

   public void setCurrentDirectory(File var1) {
      File var2 = this.currentDirectory;
      if (var1 != null && !var1.exists()) {
         var1 = this.currentDirectory;
      }

      if (var1 == null) {
         var1 = this.getFileSystemView().getDefaultDirectory();
      }

      if (this.currentDirectory == null || !this.currentDirectory.equals(var1)) {
         for(File var3 = null; !this.isTraversable(var1) && var3 != var1; var1 = this.getFileSystemView().getParentDirectory(var1)) {
            var3 = var1;
         }

         this.currentDirectory = var1;
         this.firePropertyChange("directoryChanged", var2, this.currentDirectory);
      }
   }

   public void changeToParentDirectory() {
      this.selectedFile = null;
      File var1 = this.getCurrentDirectory();
      this.setCurrentDirectory(this.getFileSystemView().getParentDirectory(var1));
   }

   public void rescanCurrentDirectory() {
      this.getUI().rescanCurrentDirectory(this);
   }

   public void ensureFileIsVisible(File var1) {
      this.getUI().ensureFileIsVisible(this, var1);
   }

   public int showOpenDialog(Component var1) throws HeadlessException {
      this.setDialogType(0);
      return this.showDialog(var1, (String)null);
   }

   public int showSaveDialog(Component var1) throws HeadlessException {
      this.setDialogType(1);
      return this.showDialog(var1, (String)null);
   }

   public int showDialog(Component var1, String var2) throws HeadlessException {
      if (this.dialog != null) {
         return -1;
      } else {
         if (var2 != null) {
            this.setApproveButtonText(var2);
            this.setDialogType(2);
         }

         this.dialog = this.createDialog(var1);
         this.dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
               JFileChooser.this.returnValue = 1;
            }
         });
         this.returnValue = -1;
         this.rescanCurrentDirectory();
         this.dialog.show();
         this.firePropertyChange("JFileChooserDialogIsClosingProperty", this.dialog, (Object)null);
         this.dialog.getContentPane().removeAll();
         this.dialog.dispose();
         this.dialog = null;
         return this.returnValue;
      }
   }

   protected JDialog createDialog(Component var1) throws HeadlessException {
      FileChooserUI var2 = this.getUI();
      String var3 = var2.getDialogTitle(this);
      this.putClientProperty("AccessibleDescription", var3);
      Window var5 = JOptionPane.getWindowForComponent(var1);
      JDialog var4;
      if (var5 instanceof Frame) {
         var4 = new JDialog((Frame)var5, var3, true);
      } else {
         var4 = new JDialog((Dialog)var5, var3, true);
      }

      var4.setComponentOrientation(this.getComponentOrientation());
      Container var6 = var4.getContentPane();
      var6.setLayout(new BorderLayout());
      var6.add((Component)this, (Object)"Center");
      if (JDialog.isDefaultLookAndFeelDecorated()) {
         boolean var7 = UIManager.getLookAndFeel().getSupportsWindowDecorations();
         if (var7) {
            var4.getRootPane().setWindowDecorationStyle(6);
         }
      }

      var4.pack();
      var4.setLocationRelativeTo(var1);
      return var4;
   }

   public boolean getControlButtonsAreShown() {
      return this.controlsShown;
   }

   public void setControlButtonsAreShown(boolean var1) {
      if (this.controlsShown != var1) {
         boolean var2 = this.controlsShown;
         this.controlsShown = var1;
         this.firePropertyChange("ControlButtonsAreShownChangedProperty", var2, this.controlsShown);
      }
   }

   public int getDialogType() {
      return this.dialogType;
   }

   public void setDialogType(int var1) {
      if (this.dialogType != var1) {
         if (var1 != 0 && var1 != 1 && var1 != 2) {
            throw new IllegalArgumentException("Incorrect Dialog Type: " + var1);
         } else {
            int var2 = this.dialogType;
            this.dialogType = var1;
            if (var1 == 0 || var1 == 1) {
               this.setApproveButtonText((String)null);
            }

            this.firePropertyChange("DialogTypeChangedProperty", var2, var1);
         }
      }
   }

   public void setDialogTitle(String var1) {
      String var2 = this.dialogTitle;
      this.dialogTitle = var1;
      if (this.dialog != null) {
         this.dialog.setTitle(var1);
      }

      this.firePropertyChange("DialogTitleChangedProperty", var2, var1);
   }

   public String getDialogTitle() {
      return this.dialogTitle;
   }

   public void setApproveButtonToolTipText(String var1) {
      if (this.approveButtonToolTipText != var1) {
         String var2 = this.approveButtonToolTipText;
         this.approveButtonToolTipText = var1;
         this.firePropertyChange("ApproveButtonToolTipTextChangedProperty", var2, this.approveButtonToolTipText);
      }
   }

   public String getApproveButtonToolTipText() {
      return this.approveButtonToolTipText;
   }

   public int getApproveButtonMnemonic() {
      return this.approveButtonMnemonic;
   }

   public void setApproveButtonMnemonic(int var1) {
      if (this.approveButtonMnemonic != var1) {
         int var2 = this.approveButtonMnemonic;
         this.approveButtonMnemonic = var1;
         this.firePropertyChange("ApproveButtonMnemonicChangedProperty", var2, this.approveButtonMnemonic);
      }
   }

   public void setApproveButtonMnemonic(char var1) {
      int var2 = var1;
      if (var1 >= 'a' && var1 <= 'z') {
         var2 = var1 - 32;
      }

      this.setApproveButtonMnemonic(var2);
   }

   public void setApproveButtonText(String var1) {
      if (this.approveButtonText != var1) {
         String var2 = this.approveButtonText;
         this.approveButtonText = var1;
         this.firePropertyChange("ApproveButtonTextChangedProperty", var2, var1);
      }
   }

   public String getApproveButtonText() {
      return this.approveButtonText;
   }

   public FileFilter[] getChoosableFileFilters() {
      FileFilter[] var1 = new FileFilter[this.filters.size()];
      this.filters.copyInto(var1);
      return var1;
   }

   public void addChoosableFileFilter(FileFilter var1) {
      if (var1 != null && !this.filters.contains(var1)) {
         FileFilter[] var2 = this.getChoosableFileFilters();
         this.filters.addElement(var1);
         this.firePropertyChange("ChoosableFileFilterChangedProperty", var2, this.getChoosableFileFilters());
         if (this.fileFilter == null && this.filters.size() == 1) {
            this.setFileFilter(var1);
         }
      }

   }

   public boolean removeChoosableFileFilter(FileFilter var1) {
      int var2 = this.filters.indexOf(var1);
      if (var2 < 0) {
         return false;
      } else {
         if (this.getFileFilter() == var1) {
            FileFilter var3 = this.getAcceptAllFileFilter();
            if (this.isAcceptAllFileFilterUsed() && var3 != var1) {
               this.setFileFilter(var3);
            } else if (var2 > 0) {
               this.setFileFilter((FileFilter)this.filters.get(0));
            } else if (this.filters.size() > 1) {
               this.setFileFilter((FileFilter)this.filters.get(1));
            } else {
               this.setFileFilter((FileFilter)null);
            }
         }

         FileFilter[] var4 = this.getChoosableFileFilters();
         this.filters.removeElement(var1);
         this.firePropertyChange("ChoosableFileFilterChangedProperty", var4, this.getChoosableFileFilters());
         return true;
      }
   }

   public void resetChoosableFileFilters() {
      FileFilter[] var1 = this.getChoosableFileFilters();
      this.setFileFilter((FileFilter)null);
      this.filters.removeAllElements();
      if (this.isAcceptAllFileFilterUsed()) {
         this.addChoosableFileFilter(this.getAcceptAllFileFilter());
      }

      this.firePropertyChange("ChoosableFileFilterChangedProperty", var1, this.getChoosableFileFilters());
   }

   public FileFilter getAcceptAllFileFilter() {
      FileFilter var1 = null;
      if (this.getUI() != null) {
         var1 = this.getUI().getAcceptAllFileFilter(this);
      }

      return var1;
   }

   public boolean isAcceptAllFileFilterUsed() {
      return this.useAcceptAllFileFilter;
   }

   public void setAcceptAllFileFilterUsed(boolean var1) {
      boolean var2 = this.useAcceptAllFileFilter;
      this.useAcceptAllFileFilter = var1;
      if (!var1) {
         this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
      } else {
         this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
         this.addChoosableFileFilter(this.getAcceptAllFileFilter());
      }

      this.firePropertyChange("acceptAllFileFilterUsedChanged", var2, this.useAcceptAllFileFilter);
   }

   public JComponent getAccessory() {
      return this.accessory;
   }

   public void setAccessory(JComponent var1) {
      JComponent var2 = this.accessory;
      this.accessory = var1;
      this.firePropertyChange("AccessoryChangedProperty", var2, this.accessory);
   }

   public void setFileSelectionMode(int var1) {
      if (this.fileSelectionMode != var1) {
         if (var1 != 0 && var1 != 1 && var1 != 2) {
            throw new IllegalArgumentException("Incorrect Mode for file selection: " + var1);
         } else {
            int var2 = this.fileSelectionMode;
            this.fileSelectionMode = var1;
            this.firePropertyChange("fileSelectionChanged", var2, this.fileSelectionMode);
         }
      }
   }

   public int getFileSelectionMode() {
      return this.fileSelectionMode;
   }

   public boolean isFileSelectionEnabled() {
      return this.fileSelectionMode == 0 || this.fileSelectionMode == 2;
   }

   public boolean isDirectorySelectionEnabled() {
      return this.fileSelectionMode == 1 || this.fileSelectionMode == 2;
   }

   public void setMultiSelectionEnabled(boolean var1) {
      if (this.multiSelectionEnabled != var1) {
         boolean var2 = this.multiSelectionEnabled;
         this.multiSelectionEnabled = var1;
         this.firePropertyChange("MultiSelectionEnabledChangedProperty", var2, this.multiSelectionEnabled);
      }
   }

   public boolean isMultiSelectionEnabled() {
      return this.multiSelectionEnabled;
   }

   public boolean isFileHidingEnabled() {
      return this.useFileHiding;
   }

   public void setFileHidingEnabled(boolean var1) {
      if (this.showFilesListener != null) {
         Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this.showFilesListener);
         this.showFilesListener = null;
      }

      boolean var2 = this.useFileHiding;
      this.useFileHiding = var1;
      this.firePropertyChange("FileHidingChanged", var2, this.useFileHiding);
   }

   public void setFileFilter(FileFilter var1) {
      FileFilter var2 = this.fileFilter;
      this.fileFilter = var1;
      if (var1 != null) {
         if (this.isMultiSelectionEnabled() && this.selectedFiles != null && this.selectedFiles.length > 0) {
            Vector var3 = new Vector();
            boolean var4 = false;
            File[] var5 = this.selectedFiles;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File var8 = var5[var7];
               if (var1.accept(var8)) {
                  var3.add(var8);
               } else {
                  var4 = true;
               }
            }

            if (var4) {
               this.setSelectedFiles(var3.size() == 0 ? null : (File[])var3.toArray(new File[var3.size()]));
            }
         } else if (this.selectedFile != null && !var1.accept(this.selectedFile)) {
            this.setSelectedFile((File)null);
         }
      }

      this.firePropertyChange("fileFilterChanged", var2, this.fileFilter);
   }

   public FileFilter getFileFilter() {
      return this.fileFilter;
   }

   public void setFileView(FileView var1) {
      FileView var2 = this.fileView;
      this.fileView = var1;
      this.firePropertyChange("fileViewChanged", var2, var1);
   }

   public FileView getFileView() {
      return this.fileView;
   }

   public String getName(File var1) {
      String var2 = null;
      if (var1 != null) {
         if (this.getFileView() != null) {
            var2 = this.getFileView().getName(var1);
         }

         FileView var3 = this.getUI().getFileView(this);
         if (var2 == null && var3 != null) {
            var2 = var3.getName(var1);
         }
      }

      return var2;
   }

   public String getDescription(File var1) {
      String var2 = null;
      if (var1 != null) {
         if (this.getFileView() != null) {
            var2 = this.getFileView().getDescription(var1);
         }

         FileView var3 = this.getUI().getFileView(this);
         if (var2 == null && var3 != null) {
            var2 = var3.getDescription(var1);
         }
      }

      return var2;
   }

   public String getTypeDescription(File var1) {
      String var2 = null;
      if (var1 != null) {
         if (this.getFileView() != null) {
            var2 = this.getFileView().getTypeDescription(var1);
         }

         FileView var3 = this.getUI().getFileView(this);
         if (var2 == null && var3 != null) {
            var2 = var3.getTypeDescription(var1);
         }
      }

      return var2;
   }

   public Icon getIcon(File var1) {
      Icon var2 = null;
      if (var1 != null) {
         if (this.getFileView() != null) {
            var2 = this.getFileView().getIcon(var1);
         }

         FileView var3 = this.getUI().getFileView(this);
         if (var2 == null && var3 != null) {
            var2 = var3.getIcon(var1);
         }
      }

      return var2;
   }

   public boolean isTraversable(File var1) {
      Boolean var2 = null;
      if (var1 != null) {
         if (this.getFileView() != null) {
            var2 = this.getFileView().isTraversable(var1);
         }

         FileView var3 = this.getUI().getFileView(this);
         if (var2 == null && var3 != null) {
            var2 = var3.isTraversable(var1);
         }

         if (var2 == null) {
            var2 = this.getFileSystemView().isTraversable(var1);
         }
      }

      return var2 != null && var2;
   }

   public boolean accept(File var1) {
      boolean var2 = true;
      if (var1 != null && this.fileFilter != null) {
         var2 = this.fileFilter.accept(var1);
      }

      return var2;
   }

   public void setFileSystemView(FileSystemView var1) {
      FileSystemView var2 = this.fileSystemView;
      this.fileSystemView = var1;
      this.firePropertyChange("FileSystemViewChanged", var2, this.fileSystemView);
   }

   public FileSystemView getFileSystemView() {
      return this.fileSystemView;
   }

   public void approveSelection() {
      this.returnValue = 0;
      if (this.dialog != null) {
         this.dialog.setVisible(false);
      }

      this.fireActionPerformed("ApproveSelection");
   }

   public void cancelSelection() {
      this.returnValue = 1;
      if (this.dialog != null) {
         this.dialog.setVisible(false);
      }

      this.fireActionPerformed("CancelSelection");
   }

   public void addActionListener(ActionListener var1) {
      this.listenerList.add(ActionListener.class, var1);
   }

   public void removeActionListener(ActionListener var1) {
      this.listenerList.remove(ActionListener.class, var1);
   }

   public ActionListener[] getActionListeners() {
      return (ActionListener[])this.listenerList.getListeners(ActionListener.class);
   }

   protected void fireActionPerformed(String var1) {
      Object[] var2 = this.listenerList.getListenerList();
      long var3 = EventQueue.getMostRecentEventTime();
      int var5 = 0;
      AWTEvent var6 = EventQueue.getCurrentEvent();
      if (var6 instanceof InputEvent) {
         var5 = ((InputEvent)var6).getModifiers();
      } else if (var6 instanceof ActionEvent) {
         var5 = ((ActionEvent)var6).getModifiers();
      }

      ActionEvent var7 = null;

      for(int var8 = var2.length - 2; var8 >= 0; var8 -= 2) {
         if (var2[var8] == ActionListener.class) {
            if (var7 == null) {
               var7 = new ActionEvent(this, 1001, var1, var3, var5);
            }

            ((ActionListener)var2[var8 + 1]).actionPerformed(var7);
         }
      }

   }

   public void updateUI() {
      if (this.isAcceptAllFileFilterUsed()) {
         this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
      }

      FileChooserUI var1 = (FileChooserUI)UIManager.getUI(this);
      if (this.fileSystemView == null) {
         this.setFileSystemView(FileSystemView.getFileSystemView());
      }

      this.setUI(var1);
      if (this.isAcceptAllFileFilterUsed()) {
         this.addChoosableFileFilter(this.getAcceptAllFileFilter());
      }

   }

   public String getUIClassID() {
      return "FileChooserUI";
   }

   public FileChooserUI getUI() {
      return (FileChooserUI)this.ui;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.installShowFilesListener();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      FileSystemView var2 = null;
      if (this.isAcceptAllFileFilterUsed()) {
         this.removeChoosableFileFilter(this.getAcceptAllFileFilter());
      }

      if (this.fileSystemView.equals(FileSystemView.getFileSystemView())) {
         var2 = this.fileSystemView;
         this.fileSystemView = null;
      }

      var1.defaultWriteObject();
      if (var2 != null) {
         this.fileSystemView = var2;
      }

      if (this.isAcceptAllFileFilterUsed()) {
         this.addChoosableFileFilter(this.getAcceptAllFileFilter());
      }

      if (this.getUIClassID().equals("FileChooserUI")) {
         byte var3 = JComponent.getWriteObjCounter(this);
         --var3;
         JComponent.setWriteObjCounter(this, var3);
         if (var3 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.approveButtonText != null ? this.approveButtonText : "";
      String var2 = this.dialogTitle != null ? this.dialogTitle : "";
      String var3;
      if (this.dialogType == 0) {
         var3 = "OPEN_DIALOG";
      } else if (this.dialogType == 1) {
         var3 = "SAVE_DIALOG";
      } else if (this.dialogType == 2) {
         var3 = "CUSTOM_DIALOG";
      } else {
         var3 = "";
      }

      String var4;
      if (this.returnValue == 1) {
         var4 = "CANCEL_OPTION";
      } else if (this.returnValue == 0) {
         var4 = "APPROVE_OPTION";
      } else if (this.returnValue == -1) {
         var4 = "ERROR_OPTION";
      } else {
         var4 = "";
      }

      String var5 = this.useFileHiding ? "true" : "false";
      String var6;
      if (this.fileSelectionMode == 0) {
         var6 = "FILES_ONLY";
      } else if (this.fileSelectionMode == 1) {
         var6 = "DIRECTORIES_ONLY";
      } else if (this.fileSelectionMode == 2) {
         var6 = "FILES_AND_DIRECTORIES";
      } else {
         var6 = "";
      }

      String var7 = this.currentDirectory != null ? this.currentDirectory.toString() : "";
      String var8 = this.selectedFile != null ? this.selectedFile.toString() : "";
      return super.paramString() + ",approveButtonText=" + var1 + ",currentDirectory=" + var7 + ",dialogTitle=" + var2 + ",dialogType=" + var3 + ",fileSelectionMode=" + var6 + ",returnValue=" + var4 + ",selectedFile=" + var8 + ",useFileHiding=" + var5;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JFileChooser.AccessibleJFileChooser();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJFileChooser extends JComponent.AccessibleJComponent {
      protected AccessibleJFileChooser() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.FILE_CHOOSER;
      }
   }

   private static class WeakPCL implements PropertyChangeListener {
      WeakReference<JFileChooser> jfcRef;

      public WeakPCL(JFileChooser var1) {
         this.jfcRef = new WeakReference(var1);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         assert var1.getPropertyName().equals("awt.file.showHiddenFiles");

         JFileChooser var2 = (JFileChooser)this.jfcRef.get();
         if (var2 == null) {
            Toolkit.getDefaultToolkit().removePropertyChangeListener("awt.file.showHiddenFiles", this);
         } else {
            boolean var3 = var2.useFileHiding;
            var2.useFileHiding = !(Boolean)var1.getNewValue();
            var2.firePropertyChange("FileHidingChanged", var3, var2.useFileHiding);
         }

      }
   }
}
