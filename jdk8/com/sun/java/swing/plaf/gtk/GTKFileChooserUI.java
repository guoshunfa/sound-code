package com.sun.java.swing.plaf.gtk;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import sun.awt.shell.ShellFolder;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthFileChooserUI;

class GTKFileChooserUI extends SynthFileChooserUI {
   private JPanel accessoryPanel = null;
   private String newFolderButtonText = null;
   private String newFolderErrorSeparator = null;
   private String newFolderErrorText = null;
   private String newFolderDialogText = null;
   private String newFolderNoDirectoryErrorTitleText = null;
   private String newFolderNoDirectoryErrorText = null;
   private String deleteFileButtonText = null;
   private String renameFileButtonText = null;
   private String newFolderButtonToolTipText = null;
   private String deleteFileButtonToolTipText = null;
   private String renameFileButtonToolTipText = null;
   private int newFolderButtonMnemonic = 0;
   private int deleteFileButtonMnemonic = 0;
   private int renameFileButtonMnemonic = 0;
   private int foldersLabelMnemonic = 0;
   private int filesLabelMnemonic = 0;
   private String renameFileDialogText = null;
   private String renameFileErrorTitle = null;
   private String renameFileErrorText = null;
   private JComboBox filterComboBox;
   private GTKFileChooserUI.FilterComboBoxModel filterComboBoxModel;
   private JPanel rightPanel;
   private JList directoryList;
   private JList fileList;
   private JLabel pathField;
   private JTextField fileNameTextField;
   private static final Dimension hstrut3 = new Dimension(3, 1);
   private static final Dimension vstrut10 = new Dimension(1, 10);
   private static Dimension prefListSize = new Dimension(75, 150);
   private static Dimension PREF_SIZE = new Dimension(435, 360);
   private static final int MIN_WIDTH = 200;
   private static final int MIN_HEIGHT = 300;
   private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);
   private static Dimension MAX_SIZE = new Dimension(32767, 32767);
   private static final Insets buttonMargin = new Insets(3, 3, 3, 3);
   private String filesLabelText = null;
   private String foldersLabelText = null;
   private String pathLabelText = null;
   private String filterLabelText = null;
   private int pathLabelMnemonic = 0;
   private int filterLabelMnemonic = 0;
   private JComboBox directoryComboBox;
   private GTKFileChooserUI.DirectoryComboBoxModel directoryComboBoxModel;
   private Action directoryComboBoxAction = new GTKFileChooserUI.DirectoryComboBoxAction();
   private JPanel bottomButtonPanel;
   private GTKFileChooserUI.GTKDirectoryModel model = null;
   private Action newFolderAction;
   private boolean readOnly;
   private boolean showDirectoryIcons;
   private boolean showFileIcons;
   private GTKFileChooserUI.GTKFileView fileView = new GTKFileChooserUI.GTKFileView();
   private PropertyChangeListener gtkFCPropertyChangeListener;
   private Action approveSelectionAction = new GTKFileChooserUI.GTKApproveSelectionAction();
   private GTKFileChooserUI.GTKDirectoryListModel directoryListModel;

   public GTKFileChooserUI(JFileChooser var1) {
      super(var1);
   }

   protected ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      var1.put("approveSelection", this.getApproveSelectionAction());
      var1.put("cancelSelection", this.getCancelSelectionAction());
      var1.put("Go Up", this.getChangeToParentDirectoryAction());
      var1.put("fileNameCompletion", this.getFileNameCompletionAction());
      return var1;
   }

   public String getFileName() {
      JFileChooser var1 = this.getFileChooser();
      String var2 = this.fileNameTextField != null ? this.fileNameTextField.getText() : null;
      if (!var1.isMultiSelectionEnabled()) {
         return var2;
      } else {
         int var3 = var1.getFileSelectionMode();
         JList var4 = var3 == 1 ? this.directoryList : this.fileList;
         Object[] var5 = var4.getSelectedValues();
         int var6 = var5.length;
         Vector var7 = new Vector(var6 + 1);

         for(int var8 = 0; var8 < var6; ++var8) {
            File var9 = (File)var5[var8];
            var7.add(var9.getName());
         }

         if (var2 != null && !var7.contains(var2)) {
            var7.add(var2);
         }

         StringBuffer var10 = new StringBuffer();
         var6 = var7.size();

         for(int var11 = 0; var11 < var6; ++var11) {
            if (var11 > 0) {
               var10.append(" ");
            }

            if (var6 > 1) {
               var10.append("\"");
            }

            var10.append((String)var7.get(var11));
            if (var6 > 1) {
               var10.append("\"");
            }
         }

         return var10.toString();
      }
   }

   public void setFileName(String var1) {
      if (this.fileNameTextField != null) {
         this.fileNameTextField.setText(var1);
      }

   }

   public void setDirectoryName(String var1) {
      this.pathField.setText(var1);
   }

   public void ensureFileIsVisible(JFileChooser var1, File var2) {
   }

   public void rescanCurrentDirectory(JFileChooser var1) {
      this.getModel().validateFileCache();
   }

   public JPanel getAccessoryPanel() {
      return this.accessoryPanel;
   }

   public FileView getFileView(JFileChooser var1) {
      return this.fileView;
   }

   private void updateDefaultButton() {
      JFileChooser var1 = this.getFileChooser();
      JRootPane var2 = SwingUtilities.getRootPane(var1);
      if (var2 != null) {
         if (var1.getControlButtonsAreShown()) {
            if (var2.getDefaultButton() == null) {
               var2.setDefaultButton(this.getApproveButton(var1));
               this.getCancelButton(var1).setDefaultCapable(false);
            }
         } else if (var2.getDefaultButton() == this.getApproveButton(var1)) {
            var2.setDefaultButton((JButton)null);
         }

      }
   }

   protected void doSelectedFileChanged(PropertyChangeEvent var1) {
      super.doSelectedFileChanged(var1);
      File var2 = (File)var1.getNewValue();
      if (var2 != null) {
         this.setFileName(this.getFileChooser().getName(var2));
      }

   }

   protected void doDirectoryChanged(PropertyChangeEvent var1) {
      this.directoryList.clearSelection();
      ListSelectionModel var2 = this.directoryList.getSelectionModel();
      if (var2 instanceof DefaultListSelectionModel) {
         ((DefaultListSelectionModel)var2).moveLeadSelectionIndex(0);
         var2.setAnchorSelectionIndex(0);
      }

      this.fileList.clearSelection();
      var2 = this.fileList.getSelectionModel();
      if (var2 instanceof DefaultListSelectionModel) {
         ((DefaultListSelectionModel)var2).moveLeadSelectionIndex(0);
         var2.setAnchorSelectionIndex(0);
      }

      File var3 = this.getFileChooser().getCurrentDirectory();
      if (var3 != null) {
         try {
            this.setDirectoryName(ShellFolder.getNormalizedFile((File)var1.getNewValue()).getPath());
         } catch (IOException var5) {
            this.setDirectoryName(((File)var1.getNewValue()).getAbsolutePath());
         }

         if (this.getFileChooser().getFileSelectionMode() == 1 && !this.getFileChooser().isMultiSelectionEnabled()) {
            this.setFileName(this.pathField.getText());
         }

         this.directoryComboBoxModel.addItem(var3);
         this.directoryListModel.directoryChanged();
      }

      super.doDirectoryChanged(var1);
   }

   protected void doAccessoryChanged(PropertyChangeEvent var1) {
      if (this.getAccessoryPanel() != null) {
         if (var1.getOldValue() != null) {
            this.getAccessoryPanel().remove((JComponent)var1.getOldValue());
         }

         JComponent var2 = (JComponent)var1.getNewValue();
         if (var2 != null) {
            this.getAccessoryPanel().add(var2, "Center");
            this.getAccessoryPanel().setPreferredSize(var2.getPreferredSize());
            this.getAccessoryPanel().setMaximumSize(MAX_SIZE);
         } else {
            this.getAccessoryPanel().setPreferredSize(ZERO_ACC_SIZE);
            this.getAccessoryPanel().setMaximumSize(ZERO_ACC_SIZE);
         }
      }

   }

   protected void doFileSelectionModeChanged(PropertyChangeEvent var1) {
      this.directoryList.clearSelection();
      this.rightPanel.setVisible((Integer)var1.getNewValue() != 1);
      super.doFileSelectionModeChanged(var1);
   }

   protected void doMultiSelectionChanged(PropertyChangeEvent var1) {
      if (this.getFileChooser().isMultiSelectionEnabled()) {
         this.fileList.setSelectionMode(2);
      } else {
         this.fileList.setSelectionMode(0);
         this.fileList.clearSelection();
      }

      super.doMultiSelectionChanged(var1);
   }

   protected void doControlButtonsChanged(PropertyChangeEvent var1) {
      super.doControlButtonsChanged(var1);
      JFileChooser var2 = this.getFileChooser();
      if (var2.getControlButtonsAreShown()) {
         var2.add(this.bottomButtonPanel, "South");
      } else {
         var2.remove(this.bottomButtonPanel);
      }

      this.updateDefaultButton();
   }

   protected void doAncestorChanged(PropertyChangeEvent var1) {
      if (var1.getOldValue() == null && var1.getNewValue() != null) {
         this.fileNameTextField.selectAll();
         this.fileNameTextField.requestFocus();
         this.updateDefaultButton();
      }

      super.doAncestorChanged(var1);
   }

   public ListSelectionListener createListSelectionListener(JFileChooser var1) {
      return new GTKFileChooserUI.SelectionListener();
   }

   protected MouseListener createDoubleClickListener(JFileChooser var1, JList var2) {
      return new GTKFileChooserUI.DoubleClickListener(var2);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new GTKFileChooserUI((JFileChooser)var0);
   }

   public void installUI(JComponent var1) {
      this.accessoryPanel = new JPanel(new BorderLayout(10, 10));
      this.accessoryPanel.setName("GTKFileChooser.accessoryPanel");
      super.installUI(var1);
   }

   public void uninstallUI(JComponent var1) {
      var1.removePropertyChangeListener(this.filterComboBoxModel);
      super.uninstallUI(var1);
      if (this.accessoryPanel != null) {
         this.accessoryPanel.removeAll();
      }

      this.accessoryPanel = null;
      this.getFileChooser().removeAll();
   }

   public void installComponents(JFileChooser var1) {
      super.installComponents(var1);
      boolean var2 = var1.getComponentOrientation().isLeftToRight();
      var1.setLayout(new BorderLayout());
      var1.setAlignmentX(0.5F);
      JPanel var3 = new JPanel(new FlowLayout(3, 0, 0));
      var3.setBorder(new EmptyBorder(10, 10, 0, 10));
      var3.setName("GTKFileChooser.topButtonPanel");
      JButton var4;
      if (!UIManager.getBoolean("FileChooser.readOnly")) {
         var4 = new JButton(this.getNewFolderAction());
         var4.setName("GTKFileChooser.newFolderButton");
         var4.setMnemonic(this.newFolderButtonMnemonic);
         var4.setToolTipText(this.newFolderButtonToolTipText);
         var4.setText(this.newFolderButtonText);
         var3.add(var4);
      }

      var4 = new JButton(this.deleteFileButtonText);
      var4.setName("GTKFileChooser.deleteFileButton");
      var4.setMnemonic(this.deleteFileButtonMnemonic);
      var4.setToolTipText(this.deleteFileButtonToolTipText);
      var4.setEnabled(false);
      var3.add(var4);
      GTKFileChooserUI.RenameFileAction var5 = new GTKFileChooserUI.RenameFileAction();
      JButton var6 = new JButton(var5);
      if (this.readOnly) {
         var5.setEnabled(false);
      }

      var6.setText(this.renameFileButtonText);
      var6.setName("GTKFileChooser.renameFileButton");
      var6.setMnemonic(this.renameFileButtonMnemonic);
      var6.setToolTipText(this.renameFileButtonToolTipText);
      var3.add(var6);
      var1.add(var3, "North");
      JPanel var7 = new JPanel();
      var7.setBorder(new EmptyBorder(0, 10, 10, 10));
      var7.setName("GTKFileChooser.interiorPanel");
      this.align(var7);
      var7.setLayout(new BoxLayout(var7, 3));
      var1.add(var7, "Center");
      JPanel var8 = new JPanel(new FlowLayout(1, 0, 0) {
         public void layoutContainer(Container var1) {
            super.layoutContainer(var1);
            JComboBox var2 = GTKFileChooserUI.this.directoryComboBox;
            if (var2.getWidth() > var1.getWidth()) {
               var2.setBounds(0, var2.getY(), var1.getWidth(), var2.getHeight());
            }

         }
      });
      var8.setBorder(new EmptyBorder(0, 0, 4, 0));
      var8.setName("GTKFileChooser.directoryComboBoxPanel");
      this.directoryComboBoxModel = this.createDirectoryComboBoxModel(var1);
      this.directoryComboBox = new JComboBox(this.directoryComboBoxModel);
      this.directoryComboBox.setName("GTKFileChooser.directoryComboBox");
      this.directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
      this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
      this.directoryComboBox.setMaximumRowCount(8);
      var8.add(this.directoryComboBox);
      var7.add(var8);
      JPanel var9 = new JPanel(new BorderLayout());
      var9.setName("GTKFileChooser.centerPanel");
      JSplitPane var10 = new JSplitPane();
      var10.setName("GTKFileChooser.splitPanel");
      var10.setDividerLocation((PREF_SIZE.width - 8) / 2);
      JPanel var11 = new JPanel(new GridBagLayout());
      var11.setName("GTKFileChooser.directoryListPanel");
      TableCellRenderer var12 = (new JTableHeader()).getDefaultRenderer();
      JLabel var13 = (JLabel)var12.getTableCellRendererComponent((JTable)null, this.foldersLabelText, false, false, 0, 0);
      var13.setName("GTKFileChooser.directoryListLabel");
      var11.add(var13, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
      var11.add(this.createDirectoryList(), new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
      var13.setDisplayedMnemonic(this.foldersLabelMnemonic);
      var13.setLabelFor(this.directoryList);
      this.rightPanel = new JPanel(new GridBagLayout());
      this.rightPanel.setName("GTKFileChooser.fileListPanel");
      var12 = (new JTableHeader()).getDefaultRenderer();
      JLabel var14 = (JLabel)var12.getTableCellRendererComponent((JTable)null, this.filesLabelText, false, false, 0, 0);
      var14.setName("GTKFileChooser.fileListLabel");
      this.rightPanel.add(var14, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 0, 0, 0), 0, 0));
      this.rightPanel.add(this.createFilesList(), new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
      var14.setDisplayedMnemonic(this.filesLabelMnemonic);
      var14.setLabelFor(this.fileList);
      var10.add(var11, var2 ? "left" : "right");
      var10.add(this.rightPanel, var2 ? "right" : "left");
      var9.add(var10, "Center");
      JPanel var15 = this.getAccessoryPanel();
      JComponent var16 = var1.getAccessory();
      if (var15 != null) {
         if (var16 == null) {
            var15.setPreferredSize(ZERO_ACC_SIZE);
            var15.setMaximumSize(ZERO_ACC_SIZE);
         } else {
            this.getAccessoryPanel().add(var16, "Center");
            var15.setPreferredSize(var16.getPreferredSize());
            var15.setMaximumSize(MAX_SIZE);
         }

         this.align(var15);
         var9.add(var15, "After");
      }

      var7.add(var9);
      var7.add(Box.createRigidArea(vstrut10));
      JPanel var17 = new JPanel(new FlowLayout(3, 0, 0));
      var17.setBorder(new EmptyBorder(0, 0, 4, 0));
      JLabel var18 = new JLabel(this.pathLabelText);
      var18.setName("GTKFileChooser.pathFieldLabel");
      var18.setDisplayedMnemonic(this.pathLabelMnemonic);
      this.align(var18);
      var17.add(var18);
      var17.add(Box.createRigidArea(hstrut3));
      File var19 = var1.getCurrentDirectory();
      String var20 = null;
      if (var19 != null) {
         var20 = var19.getPath();
      }

      this.pathField = new JLabel(var20) {
         public Dimension getMaximumSize() {
            Dimension var1 = super.getMaximumSize();
            var1.height = this.getPreferredSize().height;
            return var1;
         }
      };
      this.pathField.setName("GTKFileChooser.pathField");
      this.align(this.pathField);
      var17.add(this.pathField);
      var7.add(var17);
      this.fileNameTextField = new JTextField() {
         public Dimension getMaximumSize() {
            Dimension var1 = super.getMaximumSize();
            var1.height = this.getPreferredSize().height;
            return var1;
         }
      };
      var18.setLabelFor(this.fileNameTextField);
      Set var21 = this.fileNameTextField.getFocusTraversalKeys(0);
      HashSet var27 = new HashSet(var21);
      var27.remove(KeyStroke.getKeyStroke(9, 0));
      this.fileNameTextField.setFocusTraversalKeys(0, var27);
      this.fileNameTextField.setName("GTKFileChooser.fileNameTextField");
      this.fileNameTextField.getActionMap().put("fileNameCompletionAction", this.getFileNameCompletionAction());
      this.fileNameTextField.getInputMap().put(KeyStroke.getKeyStroke(9, 0), "fileNameCompletionAction");
      var7.add(this.fileNameTextField);
      JPanel var22 = new JPanel();
      var22.setLayout(new FlowLayout(3, 0, 0));
      var22.setBorder(new EmptyBorder(0, 0, 4, 0));
      JLabel var23 = new JLabel(this.filterLabelText);
      var23.setName("GTKFileChooser.filterLabel");
      var23.setDisplayedMnemonic(this.filterLabelMnemonic);
      var22.add(var23);
      this.filterComboBoxModel = this.createFilterComboBoxModel();
      var1.addPropertyChangeListener(this.filterComboBoxModel);
      this.filterComboBox = new JComboBox(this.filterComboBoxModel);
      this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
      var23.setLabelFor(this.filterComboBox);
      var7.add(Box.createRigidArea(vstrut10));
      var7.add(var22);
      var7.add(this.filterComboBox);
      this.bottomButtonPanel = new JPanel(new FlowLayout(4));
      this.bottomButtonPanel.setName("GTKFileChooser.bottomButtonPanel");
      this.align(this.bottomButtonPanel);
      JPanel var24 = new JPanel(new GridLayout(1, 2, 5, 0));
      JButton var25 = this.getCancelButton(var1);
      this.align(var25);
      var25.setMargin(buttonMargin);
      var24.add(var25);
      JButton var26 = this.getApproveButton(var1);
      this.align(var26);
      var26.setMargin(buttonMargin);
      var24.add(var26);
      this.bottomButtonPanel.add(var24);
      if (var1.getControlButtonsAreShown()) {
         var1.add(this.bottomButtonPanel, "South");
      }

   }

   protected void installListeners(JFileChooser var1) {
      super.installListeners(var1);
      this.gtkFCPropertyChangeListener = new GTKFileChooserUI.GTKFCPropertyChangeListener();
      var1.addPropertyChangeListener(this.gtkFCPropertyChangeListener);
   }

   private int getMnemonic(String var1, Locale var2) {
      return SwingUtilities2.getUIDefaultsInt(var1, var2);
   }

   protected void uninstallListeners(JFileChooser var1) {
      super.uninstallListeners(var1);
      if (this.gtkFCPropertyChangeListener != null) {
         var1.removePropertyChangeListener(this.gtkFCPropertyChangeListener);
      }

   }

   protected void installDefaults(JFileChooser var1) {
      super.installDefaults(var1);
      this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
      this.showDirectoryIcons = Boolean.TRUE.equals(var1.getClientProperty("GTKFileChooser.showDirectoryIcons"));
      this.showFileIcons = Boolean.TRUE.equals(var1.getClientProperty("GTKFileChooser.showFileIcons"));
   }

   protected void installIcons(JFileChooser var1) {
      this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
      this.fileIcon = UIManager.getIcon("FileView.fileIcon");
   }

   protected void installStrings(JFileChooser var1) {
      super.installStrings(var1);
      Locale var2 = var1.getLocale();
      this.newFolderDialogText = UIManager.getString("FileChooser.newFolderDialogText", (Locale)var2);
      this.newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", (Locale)var2);
      this.newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", (Locale)var2);
      this.newFolderButtonText = UIManager.getString("FileChooser.newFolderButtonText", (Locale)var2);
      this.newFolderNoDirectoryErrorTitleText = UIManager.getString("FileChooser.newFolderNoDirectoryErrorTitleText", (Locale)var2);
      this.newFolderNoDirectoryErrorText = UIManager.getString("FileChooser.newFolderNoDirectoryErrorText", (Locale)var2);
      this.deleteFileButtonText = UIManager.getString("FileChooser.deleteFileButtonText", (Locale)var2);
      this.renameFileButtonText = UIManager.getString("FileChooser.renameFileButtonText", (Locale)var2);
      this.newFolderButtonMnemonic = this.getMnemonic("FileChooser.newFolderButtonMnemonic", var2);
      this.deleteFileButtonMnemonic = this.getMnemonic("FileChooser.deleteFileButtonMnemonic", var2);
      this.renameFileButtonMnemonic = this.getMnemonic("FileChooser.renameFileButtonMnemonic", var2);
      this.newFolderButtonToolTipText = UIManager.getString("FileChooser.newFolderButtonToolTipText", (Locale)var2);
      this.deleteFileButtonToolTipText = UIManager.getString("FileChooser.deleteFileButtonToolTipText", (Locale)var2);
      this.renameFileButtonToolTipText = UIManager.getString("FileChooser.renameFileButtonToolTipText", (Locale)var2);
      this.renameFileDialogText = UIManager.getString("FileChooser.renameFileDialogText", (Locale)var2);
      this.renameFileErrorTitle = UIManager.getString("FileChooser.renameFileErrorTitle", (Locale)var2);
      this.renameFileErrorText = UIManager.getString("FileChooser.renameFileErrorText", (Locale)var2);
      this.foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", (Locale)var2);
      this.foldersLabelMnemonic = this.getMnemonic("FileChooser.foldersLabelMnemonic", var2);
      this.filesLabelText = UIManager.getString("FileChooser.filesLabelText", (Locale)var2);
      this.filesLabelMnemonic = this.getMnemonic("FileChooser.filesLabelMnemonic", var2);
      this.pathLabelText = UIManager.getString("FileChooser.pathLabelText", (Locale)var2);
      this.pathLabelMnemonic = this.getMnemonic("FileChooser.pathLabelMnemonic", var2);
      this.filterLabelText = UIManager.getString("FileChooser.filterLabelText", (Locale)var2);
      this.filterLabelMnemonic = UIManager.getInt("FileChooser.filterLabelMnemonic");
   }

   protected void uninstallStrings(JFileChooser var1) {
      super.uninstallStrings(var1);
      this.newFolderButtonText = null;
      this.deleteFileButtonText = null;
      this.renameFileButtonText = null;
      this.newFolderButtonToolTipText = null;
      this.deleteFileButtonToolTipText = null;
      this.renameFileButtonToolTipText = null;
      this.renameFileDialogText = null;
      this.renameFileErrorTitle = null;
      this.renameFileErrorText = null;
      this.foldersLabelText = null;
      this.filesLabelText = null;
      this.pathLabelText = null;
      this.newFolderDialogText = null;
      this.newFolderErrorText = null;
      this.newFolderErrorSeparator = null;
   }

   protected JScrollPane createFilesList() {
      this.fileList = new JList();
      this.fileList.setName("GTKFileChooser.fileList");
      this.fileList.putClientProperty("AccessibleName", this.filesLabelText);
      if (this.getFileChooser().isMultiSelectionEnabled()) {
         this.fileList.setSelectionMode(2);
      } else {
         this.fileList.setSelectionMode(0);
      }

      this.fileList.setModel(new GTKFileChooserUI.GTKFileListModel());
      this.fileList.getSelectionModel().removeSelectionInterval(0, 0);
      this.fileList.setCellRenderer(new GTKFileChooserUI.FileCellRenderer());
      this.fileList.addListSelectionListener(this.createListSelectionListener(this.getFileChooser()));
      this.fileList.addMouseListener(this.createDoubleClickListener(this.getFileChooser(), this.fileList));
      this.align(this.fileList);
      JScrollPane var1 = new JScrollPane(this.fileList);
      var1.setVerticalScrollBarPolicy(22);
      var1.setName("GTKFileChooser.fileListScrollPane");
      var1.setPreferredSize(prefListSize);
      var1.setMaximumSize(MAX_SIZE);
      this.align(var1);
      return var1;
   }

   protected JScrollPane createDirectoryList() {
      this.directoryList = new JList();
      this.directoryList.setName("GTKFileChooser.directoryList");
      this.directoryList.putClientProperty("AccessibleName", this.foldersLabelText);
      this.align(this.directoryList);
      this.directoryList.setCellRenderer(new GTKFileChooserUI.DirectoryCellRenderer());
      this.directoryListModel = new GTKFileChooserUI.GTKDirectoryListModel();
      this.directoryList.getSelectionModel().removeSelectionInterval(0, 0);
      this.directoryList.setModel(this.directoryListModel);
      this.directoryList.addMouseListener(this.createDoubleClickListener(this.getFileChooser(), this.directoryList));
      this.directoryList.addListSelectionListener(this.createListSelectionListener(this.getFileChooser()));
      JScrollPane var1 = new JScrollPane(this.directoryList);
      var1.setVerticalScrollBarPolicy(22);
      var1.setName("GTKFileChooser.directoryListScrollPane");
      var1.setMaximumSize(MAX_SIZE);
      var1.setPreferredSize(prefListSize);
      this.align(var1);
      return var1;
   }

   protected void createModel() {
      this.model = new GTKFileChooserUI.GTKDirectoryModel();
   }

   public BasicDirectoryModel getModel() {
      return this.model;
   }

   public Action getApproveSelectionAction() {
      return this.approveSelectionAction;
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = new Dimension(PREF_SIZE);
      JComponent var3 = this.getFileChooser().getAccessory();
      if (var3 != null) {
         var2.width += var3.getPreferredSize().width + 20;
      }

      Dimension var4 = var1.getLayout().preferredLayoutSize(var1);
      return var4 != null ? new Dimension(var4.width < var2.width ? var2.width : var4.width, var4.height < var2.height ? var2.height : var4.height) : var2;
   }

   public Dimension getMinimumSize(JComponent var1) {
      return new Dimension(200, 300);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   protected void align(JComponent var1) {
      var1.setAlignmentX(0.0F);
      var1.setAlignmentY(0.0F);
   }

   public Action getNewFolderAction() {
      if (this.newFolderAction == null) {
         this.newFolderAction = new GTKFileChooserUI.NewFolderAction();
         this.newFolderAction.setEnabled(!this.readOnly);
      }

      return this.newFolderAction;
   }

   protected GTKFileChooserUI.DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser var1) {
      return new GTKFileChooserUI.DirectoryComboBoxModel();
   }

   protected GTKFileChooserUI.FilterComboBoxRenderer createFilterComboBoxRenderer() {
      return new GTKFileChooserUI.FilterComboBoxRenderer();
   }

   protected GTKFileChooserUI.FilterComboBoxModel createFilterComboBoxModel() {
      return new GTKFileChooserUI.FilterComboBoxModel();
   }

   protected class FilterComboBoxModel extends AbstractListModel implements ComboBoxModel, PropertyChangeListener {
      protected FileFilter[] filters = GTKFileChooserUI.this.getFileChooser().getChoosableFileFilters();

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "ChoosableFileFilterChangedProperty") {
            this.filters = (FileFilter[])((FileFilter[])var1.getNewValue());
            this.fireContentsChanged(this, -1, -1);
         } else if (var2 == "fileFilterChanged") {
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public void setSelectedItem(Object var1) {
         if (var1 != null) {
            GTKFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)var1);
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public Object getSelectedItem() {
         FileFilter var1 = GTKFileChooserUI.this.getFileChooser().getFileFilter();
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
               GTKFileChooserUI.this.getFileChooser().addChoosableFileFilter(var1);
            }
         }

         return GTKFileChooserUI.this.getFileChooser().getFileFilter();
      }

      public int getSize() {
         return this.filters != null ? this.filters.length : 0;
      }

      public Object getElementAt(int var1) {
         if (var1 > this.getSize() - 1) {
            return GTKFileChooserUI.this.getFileChooser().getFileFilter();
         } else {
            return this.filters != null ? this.filters[var1] : null;
         }
      }
   }

   public class FilterComboBoxRenderer extends DefaultListCellRenderer {
      public String getName() {
         String var1 = super.getName();
         return var1 == null ? "ComboBox.renderer" : var1;
      }

      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         this.setName("ComboBox.listRenderer");
         if (var2 != null) {
            if (var2 instanceof FileFilter) {
               this.setText(((FileFilter)var2).getDescription());
            }
         } else {
            this.setText("");
         }

         return this;
      }
   }

   private class RenameFileAction extends AbstractAction {
      protected RenameFileAction() {
         super("editFileName");
      }

      public void actionPerformed(ActionEvent var1) {
         if (!GTKFileChooserUI.this.getFileName().equals("")) {
            JFileChooser var2 = GTKFileChooserUI.this.getFileChooser();
            File var3 = var2.getCurrentDirectory();
            String var4 = (String)JOptionPane.showInputDialog(var2, (new MessageFormat(GTKFileChooserUI.this.renameFileDialogText)).format(new Object[]{GTKFileChooserUI.this.getFileName()}), GTKFileChooserUI.this.renameFileButtonText, -1, (Icon)null, (Object[])null, GTKFileChooserUI.this.getFileName());
            if (var4 != null) {
               File var5 = var2.getFileSystemView().createFileObject(var3, GTKFileChooserUI.this.getFileName());
               File var6 = var2.getFileSystemView().createFileObject(var3, var4);
               if (var5 != null && var6 != null && GTKFileChooserUI.this.getModel().renameFile(var5, var6)) {
                  GTKFileChooserUI.this.setFileName(GTKFileChooserUI.this.getFileChooser().getName(var6));
                  var2.rescanCurrentDirectory();
               } else {
                  JOptionPane.showMessageDialog(var2, (new MessageFormat(GTKFileChooserUI.this.renameFileErrorText)).format(new Object[]{GTKFileChooserUI.this.getFileName(), var4}), GTKFileChooserUI.this.renameFileErrorTitle, 0);
               }
            }

         }
      }
   }

   private class GTKApproveSelectionAction extends BasicFileChooserUI.ApproveSelectionAction {
      private GTKApproveSelectionAction() {
         super();
      }

      public void actionPerformed(ActionEvent var1) {
         if (GTKFileChooserUI.this.isDirectorySelected()) {
            File var2 = GTKFileChooserUI.this.getDirectory();

            try {
               if (var2 != null) {
                  var2 = ShellFolder.getNormalizedFile(var2);
               }
            } catch (IOException var4) {
            }

            if (GTKFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(var2)) {
               GTKFileChooserUI.this.directoryList.clearSelection();
               GTKFileChooserUI.this.fileList.clearSelection();
               ListSelectionModel var3 = GTKFileChooserUI.this.fileList.getSelectionModel();
               if (var3 instanceof DefaultListSelectionModel) {
                  ((DefaultListSelectionModel)var3).moveLeadSelectionIndex(0);
                  var3.setAnchorSelectionIndex(0);
               }

               GTKFileChooserUI.this.rescanCurrentDirectory(GTKFileChooserUI.this.getFileChooser());
               return;
            }
         }

         super.actionPerformed(var1);
      }

      // $FF: synthetic method
      GTKApproveSelectionAction(Object var2) {
         this();
      }
   }

   private class NewFolderAction extends AbstractAction {
      protected NewFolderAction() {
         super("New Folder");
      }

      public void actionPerformed(ActionEvent var1) {
         if (!GTKFileChooserUI.this.readOnly) {
            JFileChooser var2 = GTKFileChooserUI.this.getFileChooser();
            File var3 = var2.getCurrentDirectory();
            String var4 = JOptionPane.showInputDialog(var2, GTKFileChooserUI.this.newFolderDialogText, GTKFileChooserUI.this.newFolderButtonText, -1);
            if (var4 != null) {
               if (!var3.exists()) {
                  JOptionPane.showMessageDialog(var2, MessageFormat.format(GTKFileChooserUI.this.newFolderNoDirectoryErrorText, var4), GTKFileChooserUI.this.newFolderNoDirectoryErrorTitleText, 0);
                  return;
               }

               File var5 = var2.getFileSystemView().createFileObject(var3, var4);
               if (var5 == null || !var5.mkdir()) {
                  JOptionPane.showMessageDialog(var2, GTKFileChooserUI.this.newFolderErrorText + GTKFileChooserUI.this.newFolderErrorSeparator + " \"" + var4 + "\"", GTKFileChooserUI.this.newFolderErrorText, 0);
               }

               var2.rescanCurrentDirectory();
            }

         }
      }
   }

   protected class DirectoryComboBoxAction extends AbstractAction {
      protected DirectoryComboBoxAction() {
         super("DirectoryComboBoxAction");
      }

      public void actionPerformed(ActionEvent var1) {
         File var2 = (File)GTKFileChooserUI.this.directoryComboBox.getSelectedItem();
         GTKFileChooserUI.this.getFileChooser().setCurrentDirectory(var2);
      }
   }

   protected class DirectoryComboBoxModel extends AbstractListModel implements ComboBoxModel {
      Vector<File> directories = new Vector();
      File selectedDirectory = null;
      JFileChooser chooser = GTKFileChooserUI.this.getFileChooser();
      FileSystemView fsv;

      public DirectoryComboBoxModel() {
         this.fsv = this.chooser.getFileSystemView();
         File var2 = GTKFileChooserUI.this.getFileChooser().getCurrentDirectory();
         if (var2 != null) {
            this.addItem(var2);
         }

      }

      private void addItem(File var1) {
         if (var1 != null) {
            int var2 = this.directories.size();
            this.directories.clear();
            if (var2 > 0) {
               this.fireIntervalRemoved(this, 0, var2);
            }

            File var3;
            try {
               var3 = this.fsv.createFileObject(ShellFolder.getNormalizedFile(var1).getPath());
            } catch (IOException var6) {
               var3 = var1;
            }

            File var4 = var3;

            do {
               this.directories.add(var4);
            } while((var4 = var4.getParentFile()) != null);

            int var5 = this.directories.size();
            if (var5 > 0) {
               this.fireIntervalAdded(this, 0, var5);
            }

            this.setSelectedItem(var3);
         }
      }

      public void setSelectedItem(Object var1) {
         this.selectedDirectory = (File)var1;
         this.fireContentsChanged(this, -1, -1);
      }

      public Object getSelectedItem() {
         return this.selectedDirectory;
      }

      public int getSize() {
         return this.directories.size();
      }

      public Object getElementAt(int var1) {
         return this.directories.elementAt(var1);
      }
   }

   protected class DirectoryCellRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (GTKFileChooserUI.this.showDirectoryIcons) {
            this.setIcon(GTKFileChooserUI.this.getFileChooser().getIcon((File)var2));
            this.setText(GTKFileChooserUI.this.getFileChooser().getName((File)var2));
         } else {
            this.setText(GTKFileChooserUI.this.getFileChooser().getName((File)var2) + "/");
         }

         return this;
      }
   }

   protected class FileCellRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         this.setText(GTKFileChooserUI.this.getFileChooser().getName((File)var2));
         if (GTKFileChooserUI.this.showFileIcons) {
            this.setIcon(GTKFileChooserUI.this.getFileChooser().getIcon((File)var2));
         }

         return this;
      }
   }

   protected class GTKFileListModel extends AbstractListModel implements ListDataListener {
      public GTKFileListModel() {
         GTKFileChooserUI.this.getModel().addListDataListener(this);
      }

      public int getSize() {
         return GTKFileChooserUI.this.getModel().getFiles().size();
      }

      public boolean contains(Object var1) {
         return GTKFileChooserUI.this.getModel().getFiles().contains(var1);
      }

      public int indexOf(Object var1) {
         return GTKFileChooserUI.this.getModel().getFiles().indexOf(var1);
      }

      public Object getElementAt(int var1) {
         return GTKFileChooserUI.this.getModel().getFiles().elementAt(var1);
      }

      public void intervalAdded(ListDataEvent var1) {
         this.fireIntervalAdded(this, var1.getIndex0(), var1.getIndex1());
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.fireIntervalRemoved(this, var1.getIndex0(), var1.getIndex1());
      }

      public void fireContentsChanged() {
         this.fireContentsChanged(this, 0, GTKFileChooserUI.this.getModel().getFiles().size() - 1);
      }

      public void contentsChanged(ListDataEvent var1) {
         this.fireContentsChanged();
      }
   }

   protected class GTKDirectoryListModel extends AbstractListModel implements ListDataListener {
      File curDir;

      public GTKDirectoryListModel() {
         GTKFileChooserUI.this.getModel().addListDataListener(this);
         this.directoryChanged();
      }

      public int getSize() {
         return GTKFileChooserUI.this.getModel().getDirectories().size() + 1;
      }

      public Object getElementAt(int var1) {
         return var1 > 0 ? GTKFileChooserUI.this.getModel().getDirectories().elementAt(var1 - 1) : this.curDir;
      }

      public void intervalAdded(ListDataEvent var1) {
         this.fireIntervalAdded(this, var1.getIndex0(), var1.getIndex1());
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.fireIntervalRemoved(this, var1.getIndex0(), var1.getIndex1());
      }

      public void fireContentsChanged() {
         this.fireContentsChanged(this, 0, GTKFileChooserUI.this.getModel().getDirectories().size() - 1);
      }

      public void contentsChanged(ListDataEvent var1) {
         this.fireContentsChanged();
      }

      private void directoryChanged() {
         this.curDir = GTKFileChooserUI.this.getFileChooser().getFileSystemView().createFileObject(GTKFileChooserUI.this.getFileChooser().getCurrentDirectory(), ".");
      }
   }

   private class GTKDirectoryModel extends BasicDirectoryModel {
      FileSystemView fsv;
      private Comparator<File> fileComparator = new Comparator<File>() {
         public int compare(File var1, File var2) {
            return GTKDirectoryModel.this.fsv.getSystemDisplayName(var1).compareTo(GTKDirectoryModel.this.fsv.getSystemDisplayName(var2));
         }
      };

      public GTKDirectoryModel() {
         super(GTKFileChooserUI.this.getFileChooser());
      }

      protected void sort(Vector<? extends File> var1) {
         this.fsv = GTKFileChooserUI.this.getFileChooser().getFileSystemView();
         Collections.sort(var1, this.fileComparator);
      }
   }

   private class GTKFCPropertyChangeListener implements PropertyChangeListener {
      private GTKFCPropertyChangeListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("GTKFileChooser.showDirectoryIcons")) {
            GTKFileChooserUI.this.showDirectoryIcons = Boolean.TRUE.equals(var1.getNewValue());
         } else if (var2.equals("GTKFileChooser.showFileIcons")) {
            GTKFileChooserUI.this.showFileIcons = Boolean.TRUE.equals(var1.getNewValue());
         }

      }

      // $FF: synthetic method
      GTKFCPropertyChangeListener(Object var2) {
         this();
      }
   }

   protected class SelectionListener implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent var1) {
         if (!var1.getValueIsAdjusting()) {
            JFileChooser var2 = GTKFileChooserUI.this.getFileChooser();
            JList var3 = (JList)var1.getSource();
            if (var2.isMultiSelectionEnabled()) {
               File[] var4 = null;
               Object[] var5 = var3.getSelectedValues();
               if (var5 != null) {
                  if (var5.length == 1 && ((File)var5[0]).isDirectory() && var2.isTraversable((File)var5[0]) && (var2.getFileSelectionMode() != 1 || !var2.getFileSystemView().isFileSystem((File)var5[0]))) {
                     GTKFileChooserUI.this.setDirectorySelected(true);
                     GTKFileChooserUI.this.setDirectory((File)var5[0]);
                  } else {
                     ArrayList var6 = new ArrayList(var5.length);
                     Object[] var7 = var5;
                     int var8 = var5.length;
                     int var9 = 0;

                     while(true) {
                        if (var9 >= var8) {
                           if (var6.size() > 0) {
                              var4 = (File[])var6.toArray(new File[var6.size()]);
                           }

                           GTKFileChooserUI.this.setDirectorySelected(false);
                           break;
                        }

                        Object var10 = var7[var9];
                        File var11 = (File)var10;
                        if (var2.isFileSelectionEnabled() && var11.isFile() || var2.isDirectorySelectionEnabled() && var11.isDirectory()) {
                           var6.add(var11);
                        }

                        ++var9;
                     }
                  }
               }

               var2.setSelectedFiles(var4);
            } else {
               File var12 = (File)var3.getSelectedValue();
               if (var12 != null && var12.isDirectory() && var2.isTraversable(var12) && (var2.getFileSelectionMode() == 0 || !var2.getFileSystemView().isFileSystem(var12))) {
                  GTKFileChooserUI.this.setDirectorySelected(true);
                  GTKFileChooserUI.this.setDirectory(var12);
               } else {
                  GTKFileChooserUI.this.setDirectorySelected(false);
                  if (var12 != null) {
                     var2.setSelectedFile(var12);
                  }
               }
            }
         }

      }
   }

   class DoubleClickListener extends MouseAdapter {
      JList list;

      public DoubleClickListener(JList var2) {
         this.list = var2;
      }

      public void mouseClicked(MouseEvent var1) {
         if (SwingUtilities.isLeftMouseButton(var1) && var1.getClickCount() == 2) {
            int var2 = this.list.locationToIndex(var1.getPoint());
            if (var2 >= 0) {
               File var3 = (File)this.list.getModel().getElementAt(var2);

               try {
                  var3 = ShellFolder.getNormalizedFile(var3);
               } catch (IOException var5) {
               }

               if (GTKFileChooserUI.this.getFileChooser().isTraversable(var3)) {
                  this.list.clearSelection();
                  if (GTKFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(var3)) {
                     GTKFileChooserUI.this.rescanCurrentDirectory(GTKFileChooserUI.this.getFileChooser());
                  } else {
                     GTKFileChooserUI.this.getFileChooser().setCurrentDirectory(var3);
                  }
               } else {
                  GTKFileChooserUI.this.getFileChooser().approveSelection();
               }
            }
         }

      }

      public void mouseEntered(MouseEvent var1) {
         if (this.list != null) {
            TransferHandler var2 = GTKFileChooserUI.this.getFileChooser().getTransferHandler();
            TransferHandler var3 = this.list.getTransferHandler();
            if (var2 != var3) {
               this.list.setTransferHandler(var2);
            }

            if (GTKFileChooserUI.this.getFileChooser().getDragEnabled() != this.list.getDragEnabled()) {
               this.list.setDragEnabled(GTKFileChooserUI.this.getFileChooser().getDragEnabled());
            }
         }

      }
   }

   private class GTKFileView extends BasicFileChooserUI.BasicFileView {
      public GTKFileView() {
         super();
         this.iconCache = null;
      }

      public void clearIconCache() {
      }

      public Icon getCachedIcon(File var1) {
         return null;
      }

      public void cacheIcon(File var1, Icon var2) {
      }

      public Icon getIcon(File var1) {
         return var1 != null && var1.isDirectory() ? GTKFileChooserUI.this.directoryIcon : GTKFileChooserUI.this.fileIcon;
      }
   }
}
