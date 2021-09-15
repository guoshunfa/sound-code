package com.apple.laf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.SwingUtilities2;

public class AquaFileChooserUI extends FileChooserUI {
   protected Icon directoryIcon = null;
   protected Icon fileIcon = null;
   protected Icon computerIcon = null;
   protected Icon hardDriveIcon = null;
   protected Icon floppyDriveIcon = null;
   protected Icon upFolderIcon = null;
   protected Icon homeFolderIcon = null;
   protected Icon listViewIcon = null;
   protected Icon detailsViewIcon = null;
   protected int saveButtonMnemonic = 0;
   protected int openButtonMnemonic = 0;
   protected int cancelButtonMnemonic = 0;
   protected int updateButtonMnemonic = 0;
   protected int helpButtonMnemonic = 0;
   protected int chooseButtonMnemonic = 0;
   private String saveTitleText = null;
   private String openTitleText = null;
   String newFolderTitleText = null;
   protected String saveButtonText = null;
   protected String openButtonText = null;
   protected String cancelButtonText = null;
   protected String updateButtonText = null;
   protected String helpButtonText = null;
   protected String newFolderButtonText = null;
   protected String chooseButtonText = null;
   String newFolderErrorText = null;
   String newFolderExistsErrorText = null;
   protected String fileDescriptionText = null;
   protected String directoryDescriptionText = null;
   protected String saveButtonToolTipText = null;
   protected String openButtonToolTipText = null;
   protected String cancelButtonToolTipText = null;
   protected String updateButtonToolTipText = null;
   protected String helpButtonToolTipText = null;
   protected String chooseItemButtonToolTipText = null;
   protected String chooseFolderButtonToolTipText = null;
   protected String directoryComboBoxToolTipText = null;
   protected String filenameTextFieldToolTipText = null;
   protected String filterComboBoxToolTipText = null;
   protected String openDirectoryButtonToolTipText = null;
   protected String cancelOpenButtonToolTipText = null;
   protected String cancelSaveButtonToolTipText = null;
   protected String cancelChooseButtonToolTipText = null;
   protected String cancelNewFolderButtonToolTipText = null;
   protected String desktopName = null;
   String newFolderDialogPrompt = null;
   String newFolderDefaultName = null;
   private String newFileDefaultName = null;
   String createButtonText = null;
   JFileChooser filechooser = null;
   private MouseListener doubleClickListener = null;
   private PropertyChangeListener propertyChangeListener = null;
   private AncestorListener ancestorListener = null;
   private DropTarget dragAndDropTarget = null;
   private final AquaFileChooserUI.AcceptAllFileFilter acceptAllFileFilter = new AquaFileChooserUI.AcceptAllFileFilter();
   private AquaFileSystemModel model;
   final AquaFileView fileView = new AquaFileView(this);
   boolean selectionInProgress = false;
   private JPanel accessoryPanel = null;
   JComboBox directoryComboBox;
   AquaFileChooserUI.DirectoryComboBoxModel fDirectoryComboBoxModel;
   private final Action directoryComboBoxAction = new AquaFileChooserUI.DirectoryComboBoxAction();
   JTextField filenameTextField;
   AquaFileChooserUI.JTableExtension fFileList;
   private AquaFileChooserUI.FilterComboBoxModel filterComboBoxModel;
   JComboBox filterComboBox;
   private final Action filterComboBoxAction = new AquaFileChooserUI.FilterComboBoxAction();
   private static final Dimension hstrut10 = new Dimension(10, 1);
   private static final Dimension vstrut10 = new Dimension(1, 10);
   private static final int PREF_WIDTH = 550;
   private static final int PREF_HEIGHT = 400;
   private static final int MIN_WIDTH = 400;
   private static final int MIN_HEIGHT = 250;
   private static final int LIST_MIN_WIDTH = 400;
   private static final int LIST_MIN_HEIGHT = 100;
   private static final Dimension LIST_MIN_SIZE = new Dimension(400, 100);
   static String fileNameLabelText = null;
   JLabel fTextFieldLabel = null;
   private static String filesOfTypeLabelText = null;
   private static String newFolderToolTipText = null;
   static String newFolderAccessibleName = null;
   private static final String[] fColumnNames = new String[2];
   JPanel fTextfieldPanel;
   private JPanel fDirectoryPanel;
   private Component fDirectoryPanelSpacer;
   private JPanel fBottomPanel;
   private AquaFileChooserUI.FCSubpanel fSaveFilePanel = null;
   private AquaFileChooserUI.FCSubpanel fOpenFilePanel = null;
   private AquaFileChooserUI.FCSubpanel fOpenDirOrAnyPanel = null;
   private AquaFileChooserUI.FCSubpanel fCustomFilePanel = null;
   private AquaFileChooserUI.FCSubpanel fCustomDirOrAnyPanel = null;
   AquaFileChooserUI.FCSubpanel fSubPanel = null;
   JButton fApproveButton;
   JButton fOpenButton;
   JButton fNewFolderButton;
   private JButton fCancelButton;
   private final AquaFileChooserUI.ApproveSelectionAction fApproveSelectionAction = new AquaFileChooserUI.ApproveSelectionAction();
   protected int fSortColumn = 0;
   protected int fPackageIsTraversable = -1;
   protected int fApplicationIsTraversable = -1;
   protected static final int sGlobalPackageIsTraversable;
   protected static final int sGlobalApplicationIsTraversable;
   protected static final String PACKAGE_TRAVERSABLE_PROPERTY = "JFileChooser.packageIsTraversable";
   protected static final String APPLICATION_TRAVERSABLE_PROPERTY = "JFileChooser.appBundleIsTraversable";
   protected static final String[] sTraversableProperties = new String[]{"always", "never", "conditional"};
   protected static final int kOpenAlways = 0;
   protected static final int kOpenNever = 1;
   protected static final int kOpenConditional = 2;
   AbstractAction[] fButtonActions;
   static final String sDataPrefix = "FileChooser.";
   static final String[] sButtonKinds;
   static final String[] sButtonData;
   static final int kOpen = 0;
   static final int kSave = 1;
   static final int kCancel = 2;
   static final int kOpenDirectory = 3;
   static final int kHelp = 4;
   static final int kNewFolder = 5;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaFileChooserUI((JFileChooser)var0);
   }

   public AquaFileChooserUI(JFileChooser var1) {
      this.fButtonActions = new AbstractAction[]{this.fApproveSelectionAction, this.fApproveSelectionAction, new AquaFileChooserUI.CancelSelectionAction(), new AquaFileChooserUI.OpenSelectionAction(), null, new AquaFileChooserUI.NewFolderAction()};
   }

   public void installUI(JComponent var1) {
      this.accessoryPanel = new JPanel(new BorderLayout());
      this.filechooser = (JFileChooser)var1;
      this.createModel();
      this.installDefaults(this.filechooser);
      this.installComponents(this.filechooser);
      this.installListeners(this.filechooser);
      AquaUtils.enforceComponentOrientation(this.filechooser, ComponentOrientation.getOrientation(Locale.getDefault()));
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallListeners(this.filechooser);
      this.uninstallComponents(this.filechooser);
      this.uninstallDefaults(this.filechooser);
      if (this.accessoryPanel != null) {
         this.accessoryPanel.removeAll();
      }

      this.accessoryPanel = null;
      this.getFileChooser().removeAll();
   }

   protected void installListeners(JFileChooser var1) {
      this.doubleClickListener = this.createDoubleClickListener(var1, this.fFileList);
      this.fFileList.addMouseListener(this.doubleClickListener);
      this.propertyChangeListener = this.createPropertyChangeListener(var1);
      if (this.propertyChangeListener != null) {
         var1.addPropertyChangeListener(this.propertyChangeListener);
      }

      if (this.model != null) {
         var1.addPropertyChangeListener(this.model);
      }

      this.ancestorListener = new AncestorListener() {
         public void ancestorAdded(AncestorEvent var1) {
            AquaFileChooserUI.this.setFocusForMode(AquaFileChooserUI.this.getFileChooser());
            AquaFileChooserUI.this.setDefaultButtonForMode(AquaFileChooserUI.this.getFileChooser());
         }

         public void ancestorRemoved(AncestorEvent var1) {
         }

         public void ancestorMoved(AncestorEvent var1) {
         }
      };
      var1.addAncestorListener(this.ancestorListener);
      var1.registerKeyboardAction(new AquaFileChooserUI.CancelSelectionAction(), KeyStroke.getKeyStroke(27, 0), 1);
      this.dragAndDropTarget = new DropTarget(var1, 1, new AquaFileChooserUI.DnDHandler(), true);
      var1.setDropTarget(this.dragAndDropTarget);
   }

   protected void uninstallListeners(JFileChooser var1) {
      if (this.propertyChangeListener != null) {
         var1.removePropertyChangeListener(this.propertyChangeListener);
      }

      this.fFileList.removeMouseListener(this.doubleClickListener);
      var1.removePropertyChangeListener(this.model);
      var1.unregisterKeyboardAction(KeyStroke.getKeyStroke(27, 0));
      var1.removeAncestorListener(this.ancestorListener);
      var1.setDropTarget((DropTarget)null);
      this.ancestorListener = null;
   }

   protected void installDefaults(JFileChooser var1) {
      this.installIcons(var1);
      this.installStrings(var1);
      this.setPackageIsTraversable(var1.getClientProperty("JFileChooser.packageIsTraversable"));
      this.setApplicationIsTraversable(var1.getClientProperty("JFileChooser.appBundleIsTraversable"));
   }

   protected void installIcons(JFileChooser var1) {
      this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
      this.fileIcon = UIManager.getIcon("FileView.fileIcon");
      this.computerIcon = UIManager.getIcon("FileView.computerIcon");
      this.hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
   }

   String getString(String var1, String var2) {
      String var3 = UIManager.getString(var1);
      return var3 == null ? var2 : var3;
   }

   protected void installStrings(JFileChooser var1) {
      this.fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText");
      this.directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText");
      this.newFolderErrorText = this.getString("FileChooser.newFolderErrorText", "Error occurred during folder creation");
      this.saveButtonText = UIManager.getString("FileChooser.saveButtonText");
      this.openButtonText = UIManager.getString("FileChooser.openButtonText");
      this.cancelButtonText = UIManager.getString("FileChooser.cancelButtonText");
      this.updateButtonText = UIManager.getString("FileChooser.updateButtonText");
      this.helpButtonText = UIManager.getString("FileChooser.helpButtonText");
      this.saveButtonMnemonic = UIManager.getInt("FileChooser.saveButtonMnemonic");
      this.openButtonMnemonic = UIManager.getInt("FileChooser.openButtonMnemonic");
      this.cancelButtonMnemonic = UIManager.getInt("FileChooser.cancelButtonMnemonic");
      this.updateButtonMnemonic = UIManager.getInt("FileChooser.updateButtonMnemonic");
      this.helpButtonMnemonic = UIManager.getInt("FileChooser.helpButtonMnemonic");
      this.chooseButtonMnemonic = UIManager.getInt("FileChooser.chooseButtonMnemonic");
      this.saveButtonToolTipText = UIManager.getString("FileChooser.saveButtonToolTipText");
      this.openButtonToolTipText = UIManager.getString("FileChooser.openButtonToolTipText");
      this.cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText");
      this.updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText");
      this.helpButtonToolTipText = UIManager.getString("FileChooser.helpButtonToolTipText");
      this.saveTitleText = this.getString("FileChooser.saveTitleText", this.saveButtonText);
      this.openTitleText = this.getString("FileChooser.openTitleText", this.openButtonText);
      this.newFolderExistsErrorText = this.getString("FileChooser.newFolderExistsErrorText", "That name is already taken");
      this.chooseButtonText = this.getString("FileChooser.chooseButtonText", "Choose");
      this.newFolderButtonText = this.getString("FileChooser.newFolderButtonText", "New");
      this.newFolderTitleText = this.getString("FileChooser.newFolderTitleText", "New Folder");
      if (var1.getDialogType() == 1) {
         fileNameLabelText = this.getString("FileChooser.saveDialogFileNameLabelText", "Save As:");
      } else {
         fileNameLabelText = this.getString("FileChooser.fileNameLabelText", "Name:");
      }

      filesOfTypeLabelText = this.getString("FileChooser.filesOfTypeLabelText", "Format:");
      this.desktopName = this.getString("FileChooser.desktopName", "Desktop");
      this.newFolderDialogPrompt = this.getString("FileChooser.newFolderPromptText", "Name of new folder:");
      this.newFolderDefaultName = this.getString("FileChooser.untitledFolderName", "untitled folder");
      this.newFileDefaultName = this.getString("FileChooser.untitledFileName", "untitled");
      this.createButtonText = this.getString("FileChooser.createButtonText", "Create");
      fColumnNames[1] = this.getString("FileChooser.byDateText", "Date Modified");
      fColumnNames[0] = this.getString("FileChooser.byNameText", "Name");
      this.chooseItemButtonToolTipText = UIManager.getString("FileChooser.chooseItemButtonToolTipText");
      this.chooseFolderButtonToolTipText = UIManager.getString("FileChooser.chooseFolderButtonToolTipText");
      this.openDirectoryButtonToolTipText = UIManager.getString("FileChooser.openDirectoryButtonToolTipText");
      this.directoryComboBoxToolTipText = UIManager.getString("FileChooser.directoryComboBoxToolTipText");
      this.filenameTextFieldToolTipText = UIManager.getString("FileChooser.filenameTextFieldToolTipText");
      this.filterComboBoxToolTipText = UIManager.getString("FileChooser.filterComboBoxToolTipText");
      this.cancelOpenButtonToolTipText = UIManager.getString("FileChooser.cancelOpenButtonToolTipText");
      this.cancelSaveButtonToolTipText = UIManager.getString("FileChooser.cancelSaveButtonToolTipText");
      this.cancelChooseButtonToolTipText = UIManager.getString("FileChooser.cancelChooseButtonToolTipText");
      this.cancelNewFolderButtonToolTipText = UIManager.getString("FileChooser.cancelNewFolderButtonToolTipText");
      this.newFolderTitleText = UIManager.getString("FileChooser.newFolderTitleText");
      newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText");
      newFolderAccessibleName = this.getString("FileChooser.newFolderAccessibleName", this.newFolderTitleText);
   }

   protected void uninstallDefaults(JFileChooser var1) {
      this.uninstallIcons(var1);
      this.uninstallStrings(var1);
   }

   protected void uninstallIcons(JFileChooser var1) {
      this.directoryIcon = null;
      this.fileIcon = null;
      this.computerIcon = null;
      this.hardDriveIcon = null;
      this.floppyDriveIcon = null;
      this.upFolderIcon = null;
      this.homeFolderIcon = null;
      this.detailsViewIcon = null;
      this.listViewIcon = null;
   }

   protected void uninstallStrings(JFileChooser var1) {
      this.saveTitleText = null;
      this.openTitleText = null;
      this.newFolderTitleText = null;
      this.saveButtonText = null;
      this.openButtonText = null;
      this.cancelButtonText = null;
      this.updateButtonText = null;
      this.helpButtonText = null;
      this.newFolderButtonText = null;
      this.chooseButtonText = null;
      this.cancelOpenButtonToolTipText = null;
      this.cancelSaveButtonToolTipText = null;
      this.cancelChooseButtonToolTipText = null;
      this.cancelNewFolderButtonToolTipText = null;
      this.saveButtonToolTipText = null;
      this.openButtonToolTipText = null;
      this.cancelButtonToolTipText = null;
      this.updateButtonToolTipText = null;
      this.helpButtonToolTipText = null;
      this.chooseItemButtonToolTipText = null;
      this.chooseFolderButtonToolTipText = null;
      this.openDirectoryButtonToolTipText = null;
      this.directoryComboBoxToolTipText = null;
      this.filenameTextFieldToolTipText = null;
      this.filterComboBoxToolTipText = null;
      this.newFolderDefaultName = null;
      this.newFileDefaultName = null;
      this.desktopName = null;
   }

   protected void createModel() {
   }

   AquaFileSystemModel getModel() {
      return this.model;
   }

   protected PropertyChangeListener createPropertyChangeListener(JFileChooser var1) {
      return new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            String var2 = var1.getPropertyName();
            File var3;
            if (var2.equals("SelectedFileChangedProperty")) {
               var3 = (File)var1.getNewValue();
               if (var3 != null) {
                  if (!AquaFileChooserUI.this.selectionInProgress && AquaFileChooserUI.this.getModel().contains(var3)) {
                     AquaFileChooserUI.this.fFileList.setSelectedIndex(AquaFileChooserUI.this.getModel().indexOf(var3));
                  }

                  if (!var3.isDirectory()) {
                     AquaFileChooserUI.this.setFileName(AquaFileChooserUI.this.getFileChooser().getName(var3));
                  }
               }

               AquaFileChooserUI.this.updateButtonState(AquaFileChooserUI.this.getFileChooser());
            } else if (var2.equals("SelectedFilesChangedProperty")) {
               JFileChooser var10 = AquaFileChooserUI.this.getFileChooser();
               if (!var10.isDirectorySelectionEnabled()) {
                  File[] var4 = (File[])((File[])var1.getNewValue());
                  if (var4 != null) {
                     int[] var5 = AquaFileChooserUI.this.fFileList.getSelectedRows();
                     int var6 = var5.length;

                     for(int var7 = 0; var7 < var6; ++var7) {
                        int var8 = var5[var7];
                        File var9 = (File)AquaFileChooserUI.this.fFileList.getValueAt(var8, 0);
                        if (var10.isTraversable(var9)) {
                           AquaFileChooserUI.this.fFileList.removeSelectedIndex(var8);
                        }
                     }
                  }
               }
            } else if (var2.equals("directoryChanged")) {
               AquaFileChooserUI.this.fFileList.clearSelection();
               var3 = AquaFileChooserUI.this.getFileChooser().getCurrentDirectory();
               if (var3 != null) {
                  AquaFileChooserUI.this.fDirectoryComboBoxModel.addItem(var3);
                  AquaFileChooserUI.this.getAction(5).setEnabled(var3.canWrite());
               }

               AquaFileChooserUI.this.updateButtonState(AquaFileChooserUI.this.getFileChooser());
            } else if (var2.equals("fileSelectionChanged")) {
               AquaFileChooserUI.this.fFileList.clearSelection();
               AquaFileChooserUI.this.setBottomPanelForMode(AquaFileChooserUI.this.getFileChooser());
            } else if (var2 == "AccessoryChangedProperty") {
               if (AquaFileChooserUI.this.getAccessoryPanel() != null) {
                  if (var1.getOldValue() != null) {
                     AquaFileChooserUI.this.getAccessoryPanel().remove((JComponent)var1.getOldValue());
                  }

                  JComponent var11 = (JComponent)var1.getNewValue();
                  if (var11 != null) {
                     AquaFileChooserUI.this.getAccessoryPanel().add(var11, "Center");
                  }
               }
            } else if (var2 == "ApproveButtonTextChangedProperty") {
               AquaFileChooserUI.this.updateApproveButton(AquaFileChooserUI.this.getFileChooser());
               AquaFileChooserUI.this.getFileChooser().invalidate();
            } else if (var2 == "DialogTypeChangedProperty") {
               if (AquaFileChooserUI.this.getFileChooser().getDialogType() == 1) {
                  AquaFileChooserUI.fileNameLabelText = AquaFileChooserUI.this.getString("FileChooser.saveDialogFileNameLabelText", "Save As:");
               } else {
                  AquaFileChooserUI.fileNameLabelText = AquaFileChooserUI.this.getString("FileChooser.fileNameLabelText", "Name:");
               }

               AquaFileChooserUI.this.fTextFieldLabel.setText(AquaFileChooserUI.fileNameLabelText);
               AquaFileChooserUI.this.setBottomPanelForMode(AquaFileChooserUI.this.getFileChooser());
            } else if (var2.equals("ApproveButtonMnemonicChangedProperty")) {
               AquaFileChooserUI.this.getApproveButton(AquaFileChooserUI.this.getFileChooser()).setMnemonic(AquaFileChooserUI.this.getApproveButtonMnemonic(AquaFileChooserUI.this.getFileChooser()));
            } else if (var2.equals("JFileChooser.packageIsTraversable")) {
               AquaFileChooserUI.this.setPackageIsTraversable(var1.getNewValue());
            } else if (var2.equals("JFileChooser.appBundleIsTraversable")) {
               AquaFileChooserUI.this.setApplicationIsTraversable(var1.getNewValue());
            } else if (var2.equals("MultiSelectionEnabledChangedProperty")) {
               if (AquaFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                  AquaFileChooserUI.this.fFileList.getSelectionModel().setSelectionMode(2);
               } else {
                  AquaFileChooserUI.this.fFileList.getSelectionModel().setSelectionMode(0);
               }
            } else if (var2.equals("ControlButtonsAreShownChangedProperty")) {
               AquaFileChooserUI.this.doControlButtonsChanged(var1);
            }

         }
      };
   }

   void setPackageIsTraversable(Object var1) {
      int var2 = -1;
      if (var1 != null && var1 instanceof String) {
         var2 = parseTraversableProperty((String)var1);
      }

      if (var2 != -1) {
         this.fPackageIsTraversable = var2;
      } else {
         this.fPackageIsTraversable = sGlobalPackageIsTraversable;
      }

   }

   void setApplicationIsTraversable(Object var1) {
      int var2 = -1;
      if (var1 != null && var1 instanceof String) {
         var2 = parseTraversableProperty((String)var1);
      }

      if (var2 != -1) {
         this.fApplicationIsTraversable = var2;
      } else {
         this.fApplicationIsTraversable = sGlobalApplicationIsTraversable;
      }

   }

   void doControlButtonsChanged(PropertyChangeEvent var1) {
      if (this.getFileChooser().getControlButtonsAreShown()) {
         this.fBottomPanel.add(this.fDirectoryPanelSpacer);
         this.fBottomPanel.add(this.fDirectoryPanel);
      } else {
         this.fBottomPanel.remove(this.fDirectoryPanelSpacer);
         this.fBottomPanel.remove(this.fDirectoryPanel);
      }

   }

   public String getFileName() {
      return this.filenameTextField != null ? this.filenameTextField.getText() : null;
   }

   public String getDirectoryName() {
      return null;
   }

   public void setFileName(String var1) {
      if (this.filenameTextField != null) {
         this.filenameTextField.setText(var1);
      }

   }

   public void setDirectoryName(String var1) {
   }

   public void rescanCurrentDirectory(JFileChooser var1) {
      this.getModel().invalidateFileCache();
      this.getModel().validateFileCache();
   }

   public void ensureFileIsVisible(JFileChooser var1, final File var2) {
      if (var2 == null) {
         this.fFileList.requestFocusInWindow();
         this.fFileList.ensureIndexIsVisible(-1);
      } else {
         this.getModel().runWhenDone(new Runnable() {
            public void run() {
               AquaFileChooserUI.this.fFileList.requestFocusInWindow();
               AquaFileChooserUI.this.fFileList.ensureIndexIsVisible(AquaFileChooserUI.this.getModel().indexOf(var2));
            }
         });
      }
   }

   public JFileChooser getFileChooser() {
      return this.filechooser;
   }

   public JPanel getAccessoryPanel() {
      return this.accessoryPanel;
   }

   protected JButton getApproveButton(JFileChooser var1) {
      return this.fApproveButton;
   }

   public int getApproveButtonMnemonic(JFileChooser var1) {
      return this.fSubPanel.getApproveButtonMnemonic(var1);
   }

   public String getApproveButtonToolTipText(JFileChooser var1) {
      return this.fSubPanel.getApproveButtonToolTipText(var1);
   }

   public String getApproveButtonText(JFileChooser var1) {
      return this.fSubPanel.getApproveButtonText(var1);
   }

   protected String getCancelButtonToolTipText(JFileChooser var1) {
      return this.fSubPanel.getCancelButtonToolTipText(var1);
   }

   boolean isSelectableInList(File var1) {
      return this.fSubPanel.isSelectableInList(this.getFileChooser(), var1);
   }

   boolean isSelectableForMode(JFileChooser var1, File var2) {
      if (var2 == null) {
         return false;
      } else {
         int var3 = var1.getFileSelectionMode();
         if (var3 == 2) {
            return true;
         } else {
            boolean var4 = var1.isTraversable(var2);
            if (var3 == 1) {
               return var4;
            } else {
               return !var4;
            }
         }
      }
   }

   public ListSelectionListener createListSelectionListener(JFileChooser var1) {
      return new AquaFileChooserUI.SelectionListener();
   }

   protected boolean openDirectory(File var1) {
      if (this.getFileChooser().isTraversable(var1)) {
         this.fFileList.clearSelection();
         File var2 = this.fileView.resolveAlias(var1);
         this.getFileChooser().setCurrentDirectory(var2);
         this.updateButtonState(this.getFileChooser());
         return true;
      } else {
         return false;
      }
   }

   protected MouseListener createDoubleClickListener(JFileChooser var1, AquaFileChooserUI.JTableExtension var2) {
      return new AquaFileChooserUI.DoubleClickListener(var2);
   }

   public FileFilter getAcceptAllFileFilter(JFileChooser var1) {
      return this.acceptAllFileFilter;
   }

   public FileView getFileView(JFileChooser var1) {
      return this.fileView;
   }

   public String getDialogTitle(JFileChooser var1) {
      if (var1.getDialogTitle() == null) {
         if (this.getFileChooser().getDialogType() == 0) {
            return this.openTitleText;
         }

         if (this.getFileChooser().getDialogType() == 1) {
            return this.saveTitleText;
         }
      }

      return var1.getDialogTitle();
   }

   File getFirstSelectedItem() {
      File var1 = null;
      int var2 = this.fFileList.getSelectedRow();
      if (var2 >= 0) {
         var1 = (File)((AquaFileSystemModel)this.fFileList.getModel()).getElementAt(var2);
      }

      return var1;
   }

   File makeFile(JFileChooser var1, String var2) {
      File var3 = null;
      if (var2 != null && !var2.equals("")) {
         FileSystemView var4 = var1.getFileSystemView();
         var3 = var4.createFileObject(var2);
         if (!var3.isAbsolute()) {
            var3 = var4.createFileObject(var1.getCurrentDirectory(), var2);
         }
      }

      return var3;
   }

   boolean textfieldIsValid() {
      String var1 = this.getFileName();
      return var1 != null && !var1.equals("");
   }

   public Dimension getPreferredSize(JComponent var1) {
      return new Dimension(550, 400);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return new Dimension(400, 250);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   protected ListCellRenderer createDirectoryComboBoxRenderer(JFileChooser var1) {
      return new AquaComboBoxRendererInternal(this.directoryComboBox) {
         public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
            super.getListCellRendererComponent(var1, var2, var3, var4, var5);
            File var6 = (File)var2;
            if (var6 == null) {
               this.setText("");
               return this;
            } else {
               JFileChooser var7 = AquaFileChooserUI.this.getFileChooser();
               this.setText(var7.getName(var6));
               this.setIcon(var7.getIcon(var6));
               return this;
            }
         }
      };
   }

   protected AquaFileChooserUI.DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser var1) {
      return new AquaFileChooserUI.DirectoryComboBoxModel();
   }

   protected ListCellRenderer createFilterComboBoxRenderer() {
      return new AquaComboBoxRendererInternal(this.filterComboBox) {
         public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
            super.getListCellRendererComponent(var1, var2, var3, var4, var5);
            FileFilter var6 = (FileFilter)var2;
            if (var6 != null) {
               this.setText(var6.getDescription());
            }

            return this;
         }
      };
   }

   protected AquaFileChooserUI.FilterComboBoxModel createFilterComboBoxModel() {
      return new AquaFileChooserUI.FilterComboBoxModel();
   }

   private boolean containsFileFilter(Object var1) {
      return Objects.equals(var1, this.getFileChooser().getFileFilter());
   }

   public void installComponents(JFileChooser var1) {
      var1.setLayout(new BoxLayout(var1, 1));
      var1.add(Box.createRigidArea(vstrut10));
      JPanel var3 = new JPanel();
      var3.setLayout(new BoxLayout(var3, 1));
      var1.add(var3);
      var1.add(Box.createRigidArea(vstrut10));
      this.fTextfieldPanel = new JPanel();
      this.fTextfieldPanel.setLayout(new BorderLayout());
      this.fTextfieldPanel.setVisible(false);
      var3.add(this.fTextfieldPanel);
      JPanel var2 = new JPanel();
      var2.setLayout(new BoxLayout(var2, 1));
      JPanel var4 = new JPanel();
      var4.setLayout(new FlowLayout(1));
      this.fTextFieldLabel = new JLabel(fileNameLabelText);
      var4.add(this.fTextFieldLabel);
      this.filenameTextField = new JTextField();
      this.fTextFieldLabel.setLabelFor(this.filenameTextField);
      this.filenameTextField.addActionListener(this.getAction(0));
      this.filenameTextField.addFocusListener(new AquaFileChooserUI.SaveTextFocusListener());
      Dimension var5 = this.filenameTextField.getMinimumSize();
      Dimension var6 = new Dimension(250, (int)var5.getHeight());
      this.filenameTextField.setPreferredSize(var6);
      this.filenameTextField.setMaximumSize(var6);
      var4.add(this.filenameTextField);
      File var7 = var1.getSelectedFile();
      if (var7 != null) {
         this.setFileName(var1.getName(var7));
      } else if (var1.getDialogType() == 1) {
         this.setFileName(this.newFileDefaultName);
      }

      var2.add(var4);
      JSeparator var8 = new JSeparator() {
         public Dimension getPreferredSize() {
            return new Dimension(((JComponent)this.getParent()).getWidth(), 3);
         }
      };
      var2.add(Box.createRigidArea(new Dimension(1, 8)));
      var2.add(var8);
      var2.add(Box.createRigidArea(new Dimension(1, 7)));
      this.fTextfieldPanel.add(var2, "Center");
      this.directoryComboBox = new JComboBox();
      this.directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
      this.fDirectoryComboBoxModel = this.createDirectoryComboBoxModel(var1);
      this.directoryComboBox.setModel(this.fDirectoryComboBoxModel);
      this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
      this.directoryComboBox.setRenderer(this.createDirectoryComboBoxRenderer(var1));
      this.directoryComboBox.setToolTipText(this.directoryComboBoxToolTipText);
      var6 = new Dimension(250, (int)this.directoryComboBox.getMinimumSize().getHeight());
      this.directoryComboBox.setPreferredSize(var6);
      this.directoryComboBox.setMaximumSize(var6);
      var3.add(this.directoryComboBox);
      JPanel var9 = new JPanel(new BorderLayout());
      var1.add(var9);
      JComponent var10 = var1.getAccessory();
      if (var10 != null) {
         this.getAccessoryPanel().add(var10);
      }

      var9.add(this.getAccessoryPanel(), "Before");
      JPanel var11 = this.createList(var1);
      var11.setMinimumSize(LIST_MIN_SIZE);
      var9.add(var11, "Center");
      this.fBottomPanel = new JPanel();
      this.fBottomPanel.setLayout(new BoxLayout(this.fBottomPanel, 1));
      var1.add(this.fBottomPanel);
      var2 = new JPanel();
      var2.setLayout(new FlowLayout(1));
      var2.setBorder(AquaGroupBorder.getTitlelessBorder());
      JLabel var12 = new JLabel(filesOfTypeLabelText);
      var2.add(var12);
      this.filterComboBoxModel = this.createFilterComboBoxModel();
      var1.addPropertyChangeListener(this.filterComboBoxModel);
      this.filterComboBox = new JComboBox(this.filterComboBoxModel);
      var12.setLabelFor(this.filterComboBox);
      this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
      var6 = new Dimension(220, (int)this.filterComboBox.getMinimumSize().getHeight());
      this.filterComboBox.setPreferredSize(var6);
      this.filterComboBox.setMaximumSize(var6);
      this.filterComboBox.addActionListener(this.filterComboBoxAction);
      this.filterComboBox.setOpaque(false);
      var2.add(this.filterComboBox);
      this.fBottomPanel.add(var2);
      this.fDirectoryPanel = new JPanel();
      this.fDirectoryPanel.setLayout(new BoxLayout(this.fDirectoryPanel, 3));
      JPanel var13 = new JPanel(new BorderLayout());
      JPanel var14 = new JPanel(new FlowLayout(3, 0, 0));
      var14.add(Box.createHorizontalStrut(20));
      this.fNewFolderButton = this.createNewFolderButton();
      var14.add(this.fNewFolderButton);
      var13.add(var14, "Before");
      JPanel var15 = new JPanel(new FlowLayout(4, 0, 0));
      this.fOpenButton = this.createButton(3, this.openButtonText);
      var15.add(this.fOpenButton);
      var15.add(Box.createHorizontalStrut(8));
      this.fCancelButton = this.createButton(2, (String)null);
      var15.add(this.fCancelButton);
      var15.add(Box.createHorizontalStrut(8));
      this.fApproveButton = new JButton();
      this.fApproveButton.addActionListener(this.fApproveSelectionAction);
      var15.add(this.fApproveButton);
      var15.add(Box.createHorizontalStrut(20));
      var13.add(var15, "After");
      this.fDirectoryPanel.add(Box.createVerticalStrut(5));
      this.fDirectoryPanel.add(var13);
      this.fDirectoryPanel.add(Box.createVerticalStrut(12));
      this.fDirectoryPanelSpacer = Box.createRigidArea(hstrut10);
      if (var1.getControlButtonsAreShown()) {
         this.fBottomPanel.add(this.fDirectoryPanelSpacer);
         this.fBottomPanel.add(this.fDirectoryPanel);
      }

      this.setBottomPanelForMode(var1);
      this.filenameTextField.getDocument().addDocumentListener(new AquaFileChooserUI.SaveTextDocumentListener());
   }

   void setDefaultButtonForMode(JFileChooser var1) {
      JButton var2 = this.fSubPanel.getDefaultButton(var1);
      JRootPane var3 = var2.getRootPane();
      if (var3 != null) {
         var3.setDefaultButton(var2);
      }

   }

   void setFocusForMode(JFileChooser var1) {
      JComponent var2 = this.fSubPanel.getFocusComponent(var1);
      if (var2 != null) {
         var2.requestFocus();
      }

   }

   void updateButtonState(JFileChooser var1) {
      this.fSubPanel.updateButtonState(var1, this.getFirstSelectedItem());
      this.updateApproveButton(var1);
   }

   void updateApproveButton(JFileChooser var1) {
      this.fApproveButton.setText(this.getApproveButtonText(var1));
      this.fApproveButton.setToolTipText(this.getApproveButtonToolTipText(var1));
      this.fApproveButton.setMnemonic(this.getApproveButtonMnemonic(var1));
      this.fCancelButton.setToolTipText(this.getCancelButtonToolTipText(var1));
   }

   synchronized AquaFileChooserUI.FCSubpanel getSaveFilePanel() {
      if (this.fSaveFilePanel == null) {
         this.fSaveFilePanel = new AquaFileChooserUI.SaveFilePanel();
      }

      return this.fSaveFilePanel;
   }

   synchronized AquaFileChooserUI.FCSubpanel getOpenFilePanel() {
      if (this.fOpenFilePanel == null) {
         this.fOpenFilePanel = new AquaFileChooserUI.OpenFilePanel();
      }

      return this.fOpenFilePanel;
   }

   synchronized AquaFileChooserUI.FCSubpanel getOpenDirOrAnyPanel() {
      if (this.fOpenDirOrAnyPanel == null) {
         this.fOpenDirOrAnyPanel = new AquaFileChooserUI.OpenDirOrAnyPanel();
      }

      return this.fOpenDirOrAnyPanel;
   }

   synchronized AquaFileChooserUI.FCSubpanel getCustomFilePanel() {
      if (this.fCustomFilePanel == null) {
         this.fCustomFilePanel = new AquaFileChooserUI.CustomFilePanel();
      }

      return this.fCustomFilePanel;
   }

   synchronized AquaFileChooserUI.FCSubpanel getCustomDirOrAnyPanel() {
      if (this.fCustomDirOrAnyPanel == null) {
         this.fCustomDirOrAnyPanel = new AquaFileChooserUI.CustomDirOrAnyPanel();
      }

      return this.fCustomDirOrAnyPanel;
   }

   void setBottomPanelForMode(JFileChooser var1) {
      if (var1.getDialogType() == 1) {
         this.fSubPanel = this.getSaveFilePanel();
      } else if (var1.getDialogType() == 0) {
         if (var1.getFileSelectionMode() == 0) {
            this.fSubPanel = this.getOpenFilePanel();
         } else {
            this.fSubPanel = this.getOpenDirOrAnyPanel();
         }
      } else if (var1.getDialogType() == 2) {
         if (var1.getFileSelectionMode() == 0) {
            this.fSubPanel = this.getCustomFilePanel();
         } else {
            this.fSubPanel = this.getCustomDirOrAnyPanel();
         }
      }

      this.fSubPanel.installPanel(var1, true);
      this.updateApproveButton(var1);
      this.updateButtonState(var1);
      this.setDefaultButtonForMode(var1);
      this.setFocusForMode(var1);
      var1.invalidate();
   }

   JButton createNewFolderButton() {
      JButton var1 = new JButton(this.newFolderButtonText);
      var1.setToolTipText(newFolderToolTipText);
      var1.getAccessibleContext().setAccessibleName(newFolderAccessibleName);
      var1.setHorizontalTextPosition(2);
      var1.setAlignmentX(0.0F);
      var1.setAlignmentY(0.5F);
      var1.addActionListener(this.getAction(5));
      return var1;
   }

   JButton createButton(int var1, String var2) {
      if (var2 == null) {
         var2 = UIManager.getString("FileChooser." + sButtonKinds[var1] + sButtonData[0]);
      }

      int var3 = UIManager.getInt("FileChooser." + sButtonKinds[var1] + sButtonData[1]);
      String var4 = UIManager.getString("FileChooser." + sButtonKinds[var1] + sButtonData[2]);
      JButton var5 = new JButton(var2);
      var5.setMnemonic(var3);
      var5.setToolTipText(var4);
      var5.addActionListener(this.getAction(var1));
      return var5;
   }

   AbstractAction getAction(int var1) {
      return this.fButtonActions[var1];
   }

   public void uninstallComponents(JFileChooser var1) {
   }

   protected JPanel createList(JFileChooser var1) {
      JPanel var2 = new JPanel(new BorderLayout());
      this.fFileList = new AquaFileChooserUI.JTableExtension();
      this.fFileList.setToolTipText((String)null);
      this.fFileList.addMouseListener(new AquaFileChooserUI.FileListMouseListener());
      this.model = new AquaFileSystemModel(var1, this.fFileList, fColumnNames);
      AquaFileChooserUI.MacListSelectionModel var3 = new AquaFileChooserUI.MacListSelectionModel(this.model);
      if (this.getFileChooser().isMultiSelectionEnabled()) {
         var3.setSelectionMode(2);
      } else {
         var3.setSelectionMode(0);
      }

      this.fFileList.setModel(this.model);
      this.fFileList.setSelectionModel(var3);
      this.fFileList.getSelectionModel().addListSelectionListener(this.createListSelectionListener(var1));
      var1.addPropertyChangeListener(this.model);
      this.fFileList.addFocusListener(new AquaFileChooserUI.SaveTextFocusListener());
      AquaFileChooserUI.JSortingTableHeader var4 = new AquaFileChooserUI.JSortingTableHeader(this.fFileList.getColumnModel());
      this.fFileList.setTableHeader(var4);
      this.fFileList.setRowMargin(0);
      this.fFileList.setIntercellSpacing(new Dimension(0, 1));
      this.fFileList.setShowVerticalLines(false);
      this.fFileList.setShowHorizontalLines(false);
      Font var5 = this.fFileList.getFont();
      this.fFileList.setDefaultRenderer(File.class, new AquaFileChooserUI.FileRenderer(var5));
      this.fFileList.setDefaultRenderer(Date.class, new AquaFileChooserUI.DateRenderer(var5));
      FontMetrics var6 = this.fFileList.getFontMetrics(var5);
      this.fFileList.setRowHeight(Math.max(var6.getHeight(), this.fileIcon.getIconHeight() + 2));
      this.fFileList.registerKeyboardAction(new AquaFileChooserUI.CancelSelectionAction(), KeyStroke.getKeyStroke(27, 0), 0);
      this.fFileList.registerKeyboardAction(new AquaFileChooserUI.DefaultButtonAction(), KeyStroke.getKeyStroke(10, 0), 0);
      this.fFileList.setDropTarget(this.dragAndDropTarget);
      JScrollPane var7 = new JScrollPane(this.fFileList, 22, 30);
      var7.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
      var7.setCorner("UPPER_TRAILING_CORNER", new AquaFileChooserUI.ScrollPaneCornerPanel());
      var2.add(var7, "Center");
      return var2;
   }

   static int parseTraversableProperty(String var0) {
      if (var0 == null) {
         return -1;
      } else {
         for(int var1 = 0; var1 < sTraversableProperties.length; ++var1) {
            if (var0.equals(sTraversableProperties[var1])) {
               return var1;
            }
         }

         return -1;
      }
   }

   static {
      Object var0 = UIManager.get("JFileChooser.packageIsTraversable");
      if (var0 != null && var0 instanceof String) {
         sGlobalPackageIsTraversable = parseTraversableProperty((String)var0);
      } else {
         sGlobalPackageIsTraversable = 2;
      }

      var0 = UIManager.get("JFileChooser.appBundleIsTraversable");
      if (var0 != null && var0 instanceof String) {
         sGlobalApplicationIsTraversable = parseTraversableProperty((String)var0);
      } else {
         sGlobalApplicationIsTraversable = 2;
      }

      sButtonKinds = new String[]{"openButton", "saveButton", "cancelButton", "openDirectoryButton", "helpButton", "newFolderButton"};
      sButtonData = new String[]{"Text", "Mnemonic", "ToolTipText"};
   }

   class JTableExtension extends JTable {
      public void setSelectedIndex(int var1) {
         this.getSelectionModel().setSelectionInterval(var1, var1);
      }

      public void removeSelectedIndex(int var1) {
         this.getSelectionModel().removeSelectionInterval(var1, var1);
      }

      public void ensureIndexIsVisible(int var1) {
         Rectangle var2 = this.getCellRect(var1, 0, false);
         if (var2 != null) {
            this.scrollRectToVisible(var2);
         }

      }

      public int locationToIndex(Point var1) {
         return this.rowAtPoint(var1);
      }
   }

   class MacListSelectionModel extends DefaultListSelectionModel {
      AquaFileSystemModel fModel;

      MacListSelectionModel(AquaFileSystemModel var2) {
         this.fModel = var2;
      }

      boolean isSelectableInListIndex(int var1) {
         File var2 = (File)this.fModel.getValueAt(var1, 0);
         return var2 != null && AquaFileChooserUI.this.isSelectableInList(var2);
      }

      void verifySelectionInterval(int var1, int var2, boolean var3) {
         int var4;
         if (var1 > var2) {
            var4 = var2;
            var2 = var1;
            var1 = var4;
         }

         var4 = var1;

         do {
            while(var4 <= var2 && !this.isSelectableInListIndex(var4)) {
               ++var4;
            }

            int var5 = -1;

            for(int var6 = var4; var6 <= var2 && this.isSelectableInListIndex(var6); var5 = var6++) {
            }

            if (var5 < 0) {
               break;
            }

            if (var3) {
               super.setSelectionInterval(var4, var5);
               var3 = false;
            } else {
               super.addSelectionInterval(var4, var5);
            }

            var4 = var5 + 1;
         } while(var4 <= var2);

      }

      public void setAnchorSelectionIndex(int var1) {
         if (this.isSelectableInListIndex(var1)) {
            super.setAnchorSelectionIndex(var1);
         }

      }

      public void setLeadSelectionIndex(int var1) {
         if (this.isSelectableInListIndex(var1)) {
            super.setLeadSelectionIndex(var1);
         }

      }

      public void setSelectionInterval(int var1, int var2) {
         if (var1 != -1 && var2 != -1) {
            if (this.getSelectionMode() != 0 && var1 != var2) {
               this.verifySelectionInterval(var1, var2, true);
            } else if (this.isSelectableInListIndex(var2)) {
               super.setSelectionInterval(var2, var2);
            }

         }
      }

      public void addSelectionInterval(int var1, int var2) {
         if (var1 != -1 && var2 != -1) {
            if (var1 == var2) {
               if (this.isSelectableInListIndex(var2)) {
                  super.addSelectionInterval(var2, var2);
               }

            } else if (this.getSelectionMode() != 2) {
               this.setSelectionInterval(var1, var2);
            } else {
               this.verifySelectionInterval(var1, var2, false);
            }
         }
      }
   }

   class CustomDirOrAnyPanel extends AquaFileChooserUI.DirOrAnyPanel {
      CustomDirOrAnyPanel() {
         super();
      }

      void installPanel(JFileChooser var1, boolean var2) {
         super.installPanel(var1, var2);
         AquaFileChooserUI.this.fTextfieldPanel.setVisible(true);
         AquaFileChooserUI.this.fNewFolderButton.setVisible(true);
      }

      void approveSelection(JFileChooser var1) {
         File var2 = AquaFileChooserUI.this.makeFile(var1, AquaFileChooserUI.this.getFileName());
         if (var2 != null) {
            AquaFileChooserUI.this.selectionInProgress = true;
            AquaFileChooserUI.this.getFileChooser().setSelectedFile(var2);
            AquaFileChooserUI.this.selectionInProgress = false;
         }

         AquaFileChooserUI.this.getFileChooser().approveSelection();
      }

      void updateButtonState(JFileChooser var1, File var2) {
         AquaFileChooserUI.this.getApproveButton(var1).setEnabled(var2 != null || AquaFileChooserUI.this.textfieldIsValid());
         super.updateButtonState(var1, var2);
      }
   }

   class OpenDirOrAnyPanel extends AquaFileChooserUI.DirOrAnyPanel {
      OpenDirOrAnyPanel() {
         super();
      }

      void installPanel(JFileChooser var1, boolean var2) {
         super.installPanel(var1, var2);
         AquaFileChooserUI.this.fTextfieldPanel.setVisible(false);
         AquaFileChooserUI.this.fNewFolderButton.setVisible(false);
      }

      JComponent getFocusComponent(JFileChooser var1) {
         return AquaFileChooserUI.this.fFileList;
      }

      int getApproveButtonMnemonic(JFileChooser var1) {
         return AquaFileChooserUI.this.chooseButtonMnemonic;
      }

      String getApproveButtonToolTipText(JFileChooser var1) {
         String var2;
         if (var1.getFileSelectionMode() == 1) {
            var2 = AquaFileChooserUI.this.chooseFolderButtonToolTipText;
         } else {
            var2 = AquaFileChooserUI.this.chooseItemButtonToolTipText;
         }

         return this.getApproveButtonToolTipText(var1, var2);
      }

      void updateButtonState(JFileChooser var1, File var2) {
         AquaFileChooserUI.this.getApproveButton(var1).setEnabled(var2 != null);
         super.updateButtonState(var1, var2);
      }
   }

   abstract class DirOrAnyPanel extends AquaFileChooserUI.FCSubpanel {
      DirOrAnyPanel() {
         super();
      }

      void installPanel(JFileChooser var1, boolean var2) {
         AquaFileChooserUI.this.fOpenButton.setVisible(false);
      }

      JButton getDefaultButton(JFileChooser var1) {
         return AquaFileChooserUI.this.getApproveButton(var1);
      }

      void updateButtonState(JFileChooser var1, File var2) {
         AquaFileChooserUI.this.fOpenButton.setEnabled(false);
         AquaFileChooserUI.this.setDefaultButtonForMode(var1);
      }
   }

   class OpenFilePanel extends AquaFileChooserUI.FCSubpanel {
      OpenFilePanel() {
         super();
      }

      void installPanel(JFileChooser var1, boolean var2) {
         AquaFileChooserUI.this.fTextfieldPanel.setVisible(false);
         AquaFileChooserUI.this.fOpenButton.setVisible(false);
         AquaFileChooserUI.this.fNewFolderButton.setVisible(false);
         AquaFileChooserUI.this.setDefaultButtonForMode(var1);
      }

      boolean inOpenDirectoryMode(JFileChooser var1, File var2) {
         return var2 != null && var1.isTraversable(var2);
      }

      JComponent getFocusComponent(JFileChooser var1) {
         return AquaFileChooserUI.this.fFileList;
      }

      void updateButtonState(JFileChooser var1, File var2) {
         boolean var3 = var2 != null && !var1.isTraversable(var2);
         AquaFileChooserUI.this.getApproveButton(var1).setEnabled(var3);
      }

      boolean isSelectableInList(JFileChooser var1, File var2) {
         return var2 != null && var1.accept(var2);
      }

      String getApproveButtonText(JFileChooser var1) {
         return this.getApproveButtonText(var1, AquaFileChooserUI.this.openButtonText);
      }

      int getApproveButtonMnemonic(JFileChooser var1) {
         return AquaFileChooserUI.this.openButtonMnemonic;
      }

      String getApproveButtonToolTipText(JFileChooser var1) {
         return this.getApproveButtonToolTipText(var1, AquaFileChooserUI.this.openButtonToolTipText);
      }

      String getCancelButtonToolTipText(JFileChooser var1) {
         return AquaFileChooserUI.this.cancelOpenButtonToolTipText;
      }
   }

   class SaveFilePanel extends AquaFileChooserUI.CustomFilePanel {
      SaveFilePanel() {
         super();
      }

      void installPanel(JFileChooser var1, boolean var2) {
         AquaFileChooserUI.this.fTextfieldPanel.setVisible(true);
         AquaFileChooserUI.this.fOpenButton.setVisible(false);
         AquaFileChooserUI.this.fNewFolderButton.setVisible(true);
      }

      boolean isSelectableInList(JFileChooser var1, File var2) {
         return var1.accept(var2) && var1.isTraversable(var2);
      }

      void approveSelection(JFileChooser var1) {
         File var2 = AquaFileChooserUI.this.makeFile(var1, AquaFileChooserUI.this.getFileName());
         if (var2 != null) {
            AquaFileChooserUI.this.selectionInProgress = true;
            AquaFileChooserUI.this.getFileChooser().setSelectedFile(var2);
            AquaFileChooserUI.this.selectionInProgress = false;
            AquaFileChooserUI.this.getFileChooser().approveSelection();
         }

      }

      void updateButtonState(JFileChooser var1, File var2) {
         boolean var3 = AquaFileChooserUI.this.textfieldIsValid();
         AquaFileChooserUI.this.getApproveButton(var1).setEnabled(var3);
      }

      String getApproveButtonText(JFileChooser var1) {
         return this.getApproveButtonText(var1, AquaFileChooserUI.this.saveButtonText);
      }

      int getApproveButtonMnemonic(JFileChooser var1) {
         return AquaFileChooserUI.this.saveButtonMnemonic;
      }

      String getApproveButtonToolTipText(JFileChooser var1) {
         return this.inOpenDirectoryMode(var1, AquaFileChooserUI.this.getFirstSelectedItem()) ? AquaFileChooserUI.this.openDirectoryButtonToolTipText : this.getApproveButtonToolTipText(var1, AquaFileChooserUI.this.saveButtonToolTipText);
      }

      String getCancelButtonToolTipText(JFileChooser var1) {
         return AquaFileChooserUI.this.cancelSaveButtonToolTipText;
      }
   }

   class CustomFilePanel extends AquaFileChooserUI.FCSubpanel {
      CustomFilePanel() {
         super();
      }

      void installPanel(JFileChooser var1, boolean var2) {
         AquaFileChooserUI.this.fTextfieldPanel.setVisible(true);
         AquaFileChooserUI.this.fOpenButton.setVisible(false);
         AquaFileChooserUI.this.fNewFolderButton.setVisible(true);
      }

      boolean inOpenDirectoryMode(JFileChooser var1, File var2) {
         boolean var3 = var2 != null && var1.isTraversable(var2);
         if (AquaFileChooserUI.this.fFileList.hasFocus()) {
            return var3;
         } else {
            return AquaFileChooserUI.this.textfieldIsValid() ? false : var3;
         }
      }

      void approveSelection(JFileChooser var1) {
         File var2 = AquaFileChooserUI.this.getFirstSelectedItem();
         if (this.inOpenDirectoryMode(var1, var2)) {
            AquaFileChooserUI.this.openDirectory(var2);
         } else {
            var2 = AquaFileChooserUI.this.makeFile(var1, AquaFileChooserUI.this.getFileName());
            if (var2 != null) {
               AquaFileChooserUI.this.selectionInProgress = true;
               AquaFileChooserUI.this.getFileChooser().setSelectedFile(var2);
               AquaFileChooserUI.this.selectionInProgress = false;
            }

            AquaFileChooserUI.this.getFileChooser().approveSelection();
         }

      }

      void updateButtonState(JFileChooser var1, File var2) {
         boolean var3 = true;
         if (!this.inOpenDirectoryMode(var1, var2)) {
            var3 = var2 != null || AquaFileChooserUI.this.textfieldIsValid();
         }

         AquaFileChooserUI.this.getApproveButton(var1).setEnabled(var3);
         AquaFileChooserUI.this.fOpenButton.setEnabled(var2 != null && var1.isTraversable(var2));
         AquaFileChooserUI.this.setDefaultButtonForMode(var1);
      }

      boolean isSelectableInList(JFileChooser var1, File var2) {
         return var2 == null ? false : var1.accept(var2);
      }

      String getApproveButtonToolTipText(JFileChooser var1) {
         return this.inOpenDirectoryMode(var1, AquaFileChooserUI.this.getFirstSelectedItem()) ? AquaFileChooserUI.this.openDirectoryButtonToolTipText : super.getApproveButtonToolTipText(var1);
      }
   }

   abstract class FCSubpanel {
      abstract void installPanel(JFileChooser var1, boolean var2);

      abstract void updateButtonState(JFileChooser var1, File var2);

      boolean isSelectableInList(JFileChooser var1, File var2) {
         if (var2 == null) {
            return false;
         } else {
            return var1.getFileSelectionMode() == 1 ? var1.isTraversable(var2) : var1.accept(var2);
         }
      }

      void approveSelection(JFileChooser var1) {
         var1.approveSelection();
      }

      JButton getDefaultButton(JFileChooser var1) {
         return AquaFileChooserUI.this.fApproveButton;
      }

      JComponent getFocusComponent(JFileChooser var1) {
         return AquaFileChooserUI.this.filenameTextField;
      }

      String getApproveButtonText(JFileChooser var1) {
         return this.getApproveButtonText(var1, AquaFileChooserUI.this.chooseButtonText);
      }

      String getApproveButtonText(JFileChooser var1, String var2) {
         String var3 = var1.getApproveButtonText();
         if (var3 != null) {
            var3.trim();
            if (!var3.equals("")) {
               return var3;
            }
         }

         return var2;
      }

      int getApproveButtonMnemonic(JFileChooser var1) {
         return var1.getApproveButtonMnemonic();
      }

      String getApproveButtonToolTipText(JFileChooser var1) {
         return this.getApproveButtonToolTipText(var1, (String)null);
      }

      String getApproveButtonToolTipText(JFileChooser var1, String var2) {
         String var3 = var1.getApproveButtonToolTipText();
         if (var3 != null) {
            var3.trim();
            if (!var3.equals("")) {
               return var3;
            }
         }

         return var2;
      }

      String getCancelButtonToolTipText(JFileChooser var1) {
         return AquaFileChooserUI.this.cancelChooseButtonToolTipText;
      }
   }

   protected class ScrollPaneCornerPanel extends JPanel {
      final Border border = UIManager.getBorder("TableHeader.cellBorder");

      protected void paintComponent(Graphics var1) {
         this.border.paintBorder(this, var1, 0, 0, this.getWidth() + 1, this.getHeight());
      }
   }

   protected class FileListMouseListener extends MouseAdapter {
      public void mouseClicked(MouseEvent var1) {
         Point var2 = var1.getPoint();
         int var3 = AquaFileChooserUI.this.fFileList.rowAtPoint(var2);
         int var4 = AquaFileChooserUI.this.fFileList.columnAtPoint(var2);
         if (var4 != -1 && var3 != -1) {
            File var5 = (File)((File)AquaFileChooserUI.this.fFileList.getValueAt(var3, 0));
            if (AquaFileChooserUI.this.isSelectableForMode(AquaFileChooserUI.this.getFileChooser(), var5)) {
               AquaFileChooserUI.this.setFileName(AquaFileChooserUI.this.fileView.getName(var5));
            }

         }
      }
   }

   class JSortingTableHeader extends JTableHeader {
      final boolean[] fSortAscending = new boolean[]{true, true};

      public JSortingTableHeader(TableColumnModel var2) {
         super(var2);
         this.setReorderingAllowed(true);
      }

      public void setDraggedColumn(TableColumn var1) {
         if (var1 != null) {
            int var2 = var1.getModelIndex();
            if (var2 != AquaFileChooserUI.this.fSortColumn) {
               AquaFileChooserUI.this.filechooser.firePropertyChange("sortByChanged", AquaFileChooserUI.this.fSortColumn, var2);
               AquaFileChooserUI.this.fSortColumn = var2;
            } else {
               this.fSortAscending[var2] = !this.fSortAscending[var2];
               AquaFileChooserUI.this.filechooser.firePropertyChange("sortAscendingChanged", !this.fSortAscending[var2], this.fSortAscending[var2]);
            }

            this.repaint();
         }

      }

      public TableColumn getDraggedColumn() {
         return null;
      }

      protected TableCellRenderer createDefaultRenderer() {
         AquaFileChooserUI.JSortingTableHeader.AquaTableCellRenderer var1 = new AquaFileChooserUI.JSortingTableHeader.AquaTableCellRenderer();
         var1.setHorizontalAlignment(2);
         return var1;
      }

      class AquaTableCellRenderer extends DefaultTableCellRenderer implements UIResource {
         public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
            if (var1 != null) {
               JTableHeader var7 = var1.getTableHeader();
               if (var7 != null) {
                  this.setForeground(var7.getForeground());
                  this.setBackground(var7.getBackground());
                  this.setFont(UIManager.getFont("TableHeader.font"));
               }
            }

            this.setText(var2 == null ? "" : var2.toString());
            AquaTableHeaderBorder var9 = AquaTableHeaderBorder.getListHeaderBorder();
            var9.setSelected(var6 == AquaFileChooserUI.this.fSortColumn);
            int var8 = var6 == 0 ? 35 : 10;
            var9.setHorizontalShift(var8);
            if (var6 == AquaFileChooserUI.this.fSortColumn) {
               var9.setSortOrder(JSortingTableHeader.this.fSortAscending[var6] ? 1 : -1);
            } else {
               var9.setSortOrder(0);
            }

            this.setBorder(var9);
            return this;
         }
      }
   }

   protected class DirectoryComboBoxAction extends AbstractAction {
      protected DirectoryComboBoxAction() {
         super("DirectoryComboBoxAction");
      }

      public void actionPerformed(ActionEvent var1) {
         AquaFileChooserUI.this.getFileChooser().setCurrentDirectory((File)AquaFileChooserUI.this.directoryComboBox.getSelectedItem());
      }
   }

   protected class FilterComboBoxAction extends AbstractAction {
      protected FilterComboBoxAction() {
         super("FilterComboBoxAction");
      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = AquaFileChooserUI.this.filterComboBox.getSelectedItem();
         if (!AquaFileChooserUI.this.containsFileFilter(var2)) {
            AquaFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)var2);
         }

      }
   }

   protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener {
      protected FileFilter[] filters = AquaFileChooserUI.this.getFileChooser().getChoosableFileFilters();

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "ChoosableFileFilterChangedProperty") {
            this.filters = (FileFilter[])((FileFilter[])var1.getNewValue());
            this.fireContentsChanged(this, -1, -1);
         } else if (var2 == "fileFilterChanged") {
            this.setSelectedItem(var1.getNewValue());
         }

      }

      public void setSelectedItem(Object var1) {
         if (var1 != null && !AquaFileChooserUI.this.containsFileFilter(var1)) {
            AquaFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)var1);
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public Object getSelectedItem() {
         FileFilter var1 = AquaFileChooserUI.this.getFileChooser().getFileFilter();
         boolean var2 = false;
         if (var1 != null) {
            FileFilter[] var3 = this.filters;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               FileFilter var6 = var3[var5];
               if (var6 == var1) {
                  var2 = true;
               }
            }

            if (!var2) {
               AquaFileChooserUI.this.getFileChooser().addChoosableFileFilter(var1);
            }
         }

         return AquaFileChooserUI.this.getFileChooser().getFileFilter();
      }

      public int getSize() {
         return this.filters != null ? this.filters.length : 0;
      }

      public FileFilter getElementAt(int var1) {
         if (var1 > this.getSize() - 1) {
            return AquaFileChooserUI.this.getFileChooser().getFileFilter();
         } else {
            return this.filters != null ? this.filters[var1] : null;
         }
      }
   }

   protected class DirectoryComboBoxModel extends AbstractListModel implements ComboBoxModel {
      Vector<File> fDirectories = new Vector();
      int topIndex = -1;
      int fPathCount = 0;
      File fSelectedDirectory = null;

      public DirectoryComboBoxModel() {
         this.addItem(AquaFileChooserUI.this.getFileChooser().getCurrentDirectory());
      }

      private void removeSelectedDirectory() {
         this.fDirectories.removeAllElements();
         this.fPathCount = 0;
         this.fSelectedDirectory = null;
      }

      void addItem(File var1) {
         if (var1 != null) {
            if (this.fSelectedDirectory != null) {
               this.removeSelectedDirectory();
            }

            File var2 = var1.getAbsoluteFile();

            Vector var3;
            for(var3 = new Vector(10); var2.getParent() != null; var2 = AquaFileChooserUI.this.getFileChooser().getFileSystemView().createFileObject(var2.getParent())) {
               var3.addElement(var2);
            }

            File[] var4 = AquaFileChooserUI.this.getFileChooser().getFileSystemView().getRoots();
            File[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               File var8 = var5[var7];
               var3.addElement(var8);
            }

            this.fPathCount = var3.size();

            for(int var9 = 0; var9 < var3.size(); ++var9) {
               this.fDirectories.addElement(var3.elementAt(var9));
            }

            this.setSelectedItem(this.fDirectories.elementAt(0));
         }
      }

      public void setSelectedItem(Object var1) {
         this.fSelectedDirectory = (File)var1;
         this.fireContentsChanged(this, -1, -1);
      }

      public Object getSelectedItem() {
         return this.fSelectedDirectory;
      }

      public int getSize() {
         return this.fDirectories.size();
      }

      public Object getElementAt(int var1) {
         return this.fDirectories.elementAt(var1);
      }
   }

   protected class DateRenderer extends AquaFileChooserUI.MacFCTableCellRenderer {
      public DateRenderer(Font var2) {
         super(var2);
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         super.getTableCellRendererComponent(var1, var2, var3, false, var5, var6);
         File var7 = (File)AquaFileChooserUI.this.fFileList.getValueAt(var5, 0);
         this.setEnabled(AquaFileChooserUI.this.isSelectableInList(var7));
         DateFormat var8 = DateFormat.getDateTimeInstance(0, 3);
         Date var9 = (Date)var2;
         if (var9 != null) {
            this.setText(var8.format(var9));
         } else {
            this.setText("");
         }

         return this;
      }
   }

   protected class FileRenderer extends AquaFileChooserUI.MacFCTableCellRenderer {
      public FileRenderer(Font var2) {
         super(var2);
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         super.getTableCellRendererComponent(var1, var2, var3, false, var5, var6);
         File var7 = (File)var2;
         JFileChooser var8 = AquaFileChooserUI.this.getFileChooser();
         this.setText(var8.getName(var7));
         this.setIcon(var8.getIcon(var7));
         this.setEnabled(AquaFileChooserUI.this.isSelectableInList(var7));
         return this;
      }
   }

   protected class MacFCTableCellRenderer extends DefaultTableCellRenderer {
      boolean fIsSelected = false;

      public MacFCTableCellRenderer(Font var2) {
         this.setFont(var2);
         this.setIconTextGap(10);
      }

      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         super.getTableCellRendererComponent(var1, var2, var3, false, var5, var6);
         this.fIsSelected = var3;
         return this;
      }

      public boolean isSelected() {
         return this.fIsSelected && this.isEnabled();
      }

      protected String layoutCL(JLabel var1, FontMetrics var2, String var3, Icon var4, Rectangle var5, Rectangle var6, Rectangle var7) {
         return SwingUtilities.layoutCompoundLabel(var1, var2, var3, var4, var1.getVerticalAlignment(), var1.getHorizontalAlignment(), var1.getVerticalTextPosition(), var1.getHorizontalTextPosition(), var5, var6, var7, var1.getIconTextGap());
      }

      protected void paintComponent(Graphics var1) {
         String var2 = this.getText();
         Icon var3 = this.getIcon();
         if (var3 != null && !this.isEnabled()) {
            Icon var4 = this.getDisabledIcon();
            if (var4 != null) {
               var3 = var4;
            }
         }

         if (var3 != null || var2 != null) {
            var1.setColor(this.getBackground());
            var1.fillRect(0, 0, this.getWidth(), this.getHeight());
            FontMetrics var13 = var1.getFontMetrics();
            Insets var5 = this.getInsets((Insets)null);
            var5.left += 10;
            Rectangle var6 = new Rectangle(var5.left, var5.top, this.getWidth() - (var5.left + var5.right), this.getHeight() - (var5.top + var5.bottom));
            Rectangle var7 = new Rectangle();
            Rectangle var8 = new Rectangle();
            String var9 = this.layoutCL(this, var13, var2, var3, var6, var7, var8);
            if (var3 != null) {
               var3.paintIcon(this, var1, var7.x + 5, var7.y);
            }

            if (var2 != null) {
               int var10 = var8.x;
               int var11 = var8.y + var13.getAscent() + 1;
               Color var12;
               if (this.isEnabled()) {
                  var12 = this.getBackground();
                  var1.setColor(var12);
                  var1.fillRect(var10 - 1, var8.y, var8.width + 2, var13.getAscent() + 2);
                  var1.setColor(this.getForeground());
                  SwingUtilities2.drawString(AquaFileChooserUI.this.filechooser, var1, (String)var9, var10, var11);
               } else {
                  var12 = this.getBackground();
                  var1.setColor(var12);
                  var1.fillRect(var10 - 1, var8.y, var8.width + 2, var13.getAscent() + 2);
                  var1.setColor(var12.brighter());
                  SwingUtilities2.drawString(AquaFileChooserUI.this.filechooser, var1, (String)var9, var10, var11);
                  var1.setColor(var12.darker());
                  SwingUtilities2.drawString(AquaFileChooserUI.this.filechooser, var1, (String)var9, var10 + 1, var11 + 1);
               }
            }

         }
      }
   }

   protected class AcceptAllFileFilter extends FileFilter {
      public AcceptAllFileFilter() {
      }

      public boolean accept(File var1) {
         return true;
      }

      public String getDescription() {
         return UIManager.getString("FileChooser.acceptAllFileFilterText");
      }
   }

   protected class UpdateAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JFileChooser var2 = AquaFileChooserUI.this.getFileChooser();
         var2.setCurrentDirectory(var2.getFileSystemView().createFileObject(AquaFileChooserUI.this.getDirectoryName()));
         var2.rescanCurrentDirectory();
      }
   }

   protected class CancelSelectionAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         AquaFileChooserUI.this.getFileChooser().cancelSelection();
      }

      public boolean isEnabled() {
         return AquaFileChooserUI.this.getFileChooser().isEnabled();
      }
   }

   protected class OpenSelectionAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         int var2 = AquaFileChooserUI.this.fFileList.getSelectedRow();
         if (var2 >= 0) {
            File var3 = (File)((AquaFileSystemModel)AquaFileChooserUI.this.fFileList.getModel()).getElementAt(var2);
            if (var3 != null) {
               AquaFileChooserUI.this.openDirectory(var3);
            }
         }

      }
   }

   protected class ApproveSelectionAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         AquaFileChooserUI.this.fSubPanel.approveSelection(AquaFileChooserUI.this.getFileChooser());
      }
   }

   protected class NewFolderAction extends AbstractAction {
      protected NewFolderAction() {
         super(AquaFileChooserUI.newFolderAccessibleName);
      }

      private Object showNewFolderDialog(Component var1, Object var2, String var3, int var4, Icon var5, Object[] var6, Object var7) {
         JOptionPane var8 = new JOptionPane(var2, var4, 2, var5, var6, (Object)null);
         var8.setWantsInput(true);
         var8.setInitialSelectionValue(var7);
         JDialog var9 = var8.createDialog(var1, var3);
         var8.selectInitialValue();
         var9.setVisible(true);
         var9.dispose();
         Object var10 = var8.getValue();
         return var10 != null && !var10.equals(AquaFileChooserUI.this.cancelButtonText) ? var8.getInputValue() : null;
      }

      public void actionPerformed(ActionEvent var1) {
         JFileChooser var2 = AquaFileChooserUI.this.getFileChooser();
         File var3 = var2.getCurrentDirectory();
         File var4 = null;
         String[] var5 = new String[]{AquaFileChooserUI.this.createButtonText, AquaFileChooserUI.this.cancelButtonText};
         String var6 = (String)this.showNewFolderDialog(var2, AquaFileChooserUI.this.newFolderDialogPrompt, AquaFileChooserUI.this.newFolderTitleText, -1, (Icon)null, var5, AquaFileChooserUI.this.newFolderDefaultName);
         if (var6 != null) {
            try {
               var4 = var2.getFileSystemView().createFileObject(var3, var6);
               if (var4.exists()) {
                  JOptionPane.showMessageDialog(var2, AquaFileChooserUI.this.newFolderExistsErrorText, "", 0);
                  return;
               }

               var4.mkdirs();
            } catch (Exception var8) {
               JOptionPane.showMessageDialog(var2, AquaFileChooserUI.this.newFolderErrorText, "", 0);
               return;
            }

            AquaFileChooserUI.this.openDirectory(var4);
         }

      }
   }

   protected class DefaultButtonAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         JRootPane var2 = AquaFileChooserUI.this.getFileChooser().getRootPane();
         JFileChooser var3 = AquaFileChooserUI.this.getFileChooser();
         JButton var4 = var2.getDefaultButton();
         if (var4 != null && SwingUtilities.getRootPane(var4) == var2 && var4.isEnabled()) {
            var4.doClick(20);
         } else if (!var3.getControlButtonsAreShown()) {
            JButton var5 = AquaFileChooserUI.this.fSubPanel.getDefaultButton(var3);
            if (var5 != null) {
               var5.doClick(20);
            }
         } else {
            Toolkit.getDefaultToolkit().beep();
         }

      }

      public boolean isEnabled() {
         return true;
      }
   }

   class DnDHandler extends DropTargetAdapter {
      public void dragEnter(DropTargetDragEvent var1) {
         this.tryToAcceptDrag(var1);
      }

      public void dragOver(DropTargetDragEvent var1) {
         this.tryToAcceptDrag(var1);
      }

      public void dropActionChanged(DropTargetDragEvent var1) {
         this.tryToAcceptDrag(var1);
      }

      public void drop(DropTargetDropEvent var1) {
         if (var1.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            this.handleFileDropEvent(var1);
         } else if (var1.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            this.handleStringDropEvent(var1);
         }
      }

      protected void tryToAcceptDrag(DropTargetDragEvent var1) {
         if (!var1.isDataFlavorSupported(DataFlavor.javaFileListFlavor) && !var1.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            var1.rejectDrag();
         } else {
            var1.acceptDrag(1);
         }
      }

      protected void handleFileDropEvent(DropTargetDropEvent var1) {
         var1.acceptDrop(var1.getDropAction());
         Transferable var2 = var1.getTransferable();

         try {
            List var3 = (List)var2.getTransferData(DataFlavor.javaFileListFlavor);
            this.dropFiles((File[])var3.toArray(new File[var3.size()]));
            var1.dropComplete(true);
         } catch (Exception var4) {
            var1.dropComplete(false);
         }

      }

      protected void handleStringDropEvent(DropTargetDropEvent var1) {
         var1.acceptDrop(var1.getDropAction());
         Transferable var2 = var1.getTransferable();

         String var3;
         try {
            var3 = (String)var2.getTransferData(DataFlavor.stringFlavor);
         } catch (Exception var7) {
            var1.dropComplete(false);
            return;
         }

         File var4;
         try {
            var4 = new File(var3);
            if (var4.exists()) {
               this.dropFiles(new File[]{var4});
               var1.dropComplete(true);
               return;
            }
         } catch (Exception var6) {
         }

         try {
            var4 = new File(new URI(var3));
            if (var4.exists()) {
               this.dropFiles(new File[]{var4});
               var1.dropComplete(true);
               return;
            }
         } catch (Exception var5) {
         }

         var1.dropComplete(false);
      }

      protected void dropFiles(final File[] var1) {
         JFileChooser var2 = AquaFileChooserUI.this.getFileChooser();
         if (var1.length == 1) {
            if (var1[0].isDirectory()) {
               var2.setCurrentDirectory(var1[0]);
               return;
            }

            if (!AquaFileChooserUI.this.isSelectableForMode(var2, var1[0])) {
               return;
            }
         }

         var2.setSelectedFiles(var1);
         File[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            var2.ensureFileIsVisible(var6);
         }

         AquaFileChooserUI.this.getModel().runWhenDone(new Runnable() {
            public void run() {
               AquaFileSystemModel var1x = AquaFileChooserUI.this.getModel();
               File[] var2 = var1;
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  File var5 = var2[var4];
                  int var6 = var1x.indexOf(var5);
                  if (var6 >= 0) {
                     AquaFileChooserUI.this.fFileList.addRowSelectionInterval(var6, var6);
                  }
               }

            }
         });
      }
   }

   protected class DoubleClickListener extends MouseAdapter {
      AquaFileChooserUI.JTableExtension list;

      public DoubleClickListener(AquaFileChooserUI.JTableExtension var2) {
         this.list = var2;
      }

      public void mouseClicked(MouseEvent var1) {
         if (var1.getClickCount() == 2) {
            int var2 = this.list.locationToIndex(var1.getPoint());
            if (var2 >= 0) {
               File var3 = (File)((AquaFileSystemModel)this.list.getModel()).getElementAt(var2);
               if (!AquaFileChooserUI.this.openDirectory(var3)) {
                  if (AquaFileChooserUI.this.isSelectableInList(var3)) {
                     AquaFileChooserUI.this.getFileChooser().approveSelection();
                  }
               }
            }
         }
      }
   }

   protected class SaveTextDocumentListener implements DocumentListener {
      public void insertUpdate(DocumentEvent var1) {
         this.textChanged();
      }

      public void removeUpdate(DocumentEvent var1) {
         this.textChanged();
      }

      public void changedUpdate(DocumentEvent var1) {
      }

      void textChanged() {
         AquaFileChooserUI.this.updateButtonState(AquaFileChooserUI.this.getFileChooser());
      }
   }

   protected class SaveTextFocusListener implements FocusListener {
      public void focusGained(FocusEvent var1) {
         AquaFileChooserUI.this.updateButtonState(AquaFileChooserUI.this.getFileChooser());
      }

      public void focusLost(FocusEvent var1) {
      }
   }

   protected class SelectionListener implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent var1) {
         if (!var1.getValueIsAdjusting()) {
            File var2 = null;
            int var3 = AquaFileChooserUI.this.fFileList.getSelectedRow();
            JFileChooser var4 = AquaFileChooserUI.this.getFileChooser();
            boolean var5 = var4.getDialogType() == 1;
            if (var3 >= 0) {
               var2 = (File)AquaFileChooserUI.this.fFileList.getValueAt(var3, 0);
            }

            AquaFileChooserUI.this.selectionInProgress = true;
            if (!var5 && var4.isMultiSelectionEnabled()) {
               int[] var6 = AquaFileChooserUI.this.fFileList.getSelectedRows();
               int var7 = 0;
               int var9;
               int var10;
               if (var6.length > 0) {
                  int[] var8 = var6;
                  var9 = var6.length;

                  for(var10 = 0; var10 < var9; ++var10) {
                     int var11 = var8[var10];
                     if (AquaFileChooserUI.this.isSelectableForMode(var4, (File)AquaFileChooserUI.this.fFileList.getValueAt(var11, 0))) {
                        ++var7;
                     }
                  }
               }

               if (var7 > 0) {
                  File[] var12 = new File[var7];
                  var9 = 0;

                  for(var10 = 0; var9 < var6.length; ++var9) {
                     var2 = (File)AquaFileChooserUI.this.fFileList.getValueAt(var6[var9], 0);
                     if (AquaFileChooserUI.this.isSelectableForMode(var4, var2)) {
                        if (AquaFileChooserUI.this.fileView.isAlias(var2)) {
                           var2 = AquaFileChooserUI.this.fileView.resolveAlias(var2);
                        }

                        var12[var10++] = var2;
                     }
                  }

                  var4.setSelectedFiles(var12);
               } else {
                  var4.setSelectedFiles((File[])null);
               }
            } else {
               var4.setSelectedFiles((File[])null);
               var4.setSelectedFile(var2);
            }

            AquaFileChooserUI.this.selectionInProgress = false;
         }
      }
   }
}
