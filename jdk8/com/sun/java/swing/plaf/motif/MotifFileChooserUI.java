package com.sun.java.swing.plaf.motif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.SwingUtilities2;

public class MotifFileChooserUI extends BasicFileChooserUI {
   private MotifFileChooserUI.FilterComboBoxModel filterComboBoxModel;
   protected JList<File> directoryList = null;
   protected JList<File> fileList = null;
   protected JTextField pathField = null;
   protected JComboBox<FileFilter> filterComboBox = null;
   protected JTextField filenameTextField = null;
   private static final Dimension hstrut10 = new Dimension(10, 1);
   private static final Dimension vstrut10 = new Dimension(1, 10);
   private static final Insets insets = new Insets(10, 10, 10, 10);
   private static Dimension prefListSize = new Dimension(75, 150);
   private static Dimension WITH_ACCELERATOR_PREF_SIZE = new Dimension(650, 450);
   private static Dimension PREF_SIZE = new Dimension(350, 450);
   private static final int MIN_WIDTH = 200;
   private static final int MIN_HEIGHT = 300;
   private static Dimension PREF_ACC_SIZE = new Dimension(10, 10);
   private static Dimension ZERO_ACC_SIZE = new Dimension(1, 1);
   private static Dimension MAX_SIZE = new Dimension(32767, 32767);
   private static final Insets buttonMargin = new Insets(3, 3, 3, 3);
   private JPanel bottomPanel;
   protected JButton approveButton;
   private String enterFolderNameLabelText = null;
   private int enterFolderNameLabelMnemonic = 0;
   private String enterFileNameLabelText = null;
   private int enterFileNameLabelMnemonic = 0;
   private String filesLabelText = null;
   private int filesLabelMnemonic = 0;
   private String foldersLabelText = null;
   private int foldersLabelMnemonic = 0;
   private String pathLabelText = null;
   private int pathLabelMnemonic = 0;
   private String filterLabelText = null;
   private int filterLabelMnemonic = 0;
   private JLabel fileNameLabel;

   private void populateFileNameLabel() {
      if (this.getFileChooser().getFileSelectionMode() == 1) {
         this.fileNameLabel.setText(this.enterFolderNameLabelText);
         this.fileNameLabel.setDisplayedMnemonic(this.enterFolderNameLabelMnemonic);
      } else {
         this.fileNameLabel.setText(this.enterFileNameLabelText);
         this.fileNameLabel.setDisplayedMnemonic(this.enterFileNameLabelMnemonic);
      }

   }

   private String fileNameString(File var1) {
      if (var1 == null) {
         return null;
      } else {
         JFileChooser var2 = this.getFileChooser();
         return var2.isDirectorySelectionEnabled() && !var2.isFileSelectionEnabled() ? var1.getPath() : var1.getName();
      }
   }

   private String fileNameString(File[] var1) {
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var1 != null && var3 < var1.length; ++var3) {
         if (var3 > 0) {
            var2.append(" ");
         }

         if (var1.length > 1) {
            var2.append("\"");
         }

         var2.append(this.fileNameString(var1[var3]));
         if (var1.length > 1) {
            var2.append("\"");
         }
      }

      return var2.toString();
   }

   public MotifFileChooserUI(JFileChooser var1) {
      super(var1);
   }

   public String getFileName() {
      return this.filenameTextField != null ? this.filenameTextField.getText() : null;
   }

   public void setFileName(String var1) {
      if (this.filenameTextField != null) {
         this.filenameTextField.setText(var1);
      }

   }

   public String getDirectoryName() {
      return this.pathField.getText();
   }

   public void setDirectoryName(String var1) {
      this.pathField.setText(var1);
   }

   public void ensureFileIsVisible(JFileChooser var1, File var2) {
   }

   public void rescanCurrentDirectory(JFileChooser var1) {
      this.getModel().validateFileCache();
   }

   public PropertyChangeListener createPropertyChangeListener(JFileChooser var1) {
      return new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            String var2 = var1.getPropertyName();
            if (var2.equals("SelectedFileChangedProperty")) {
               File var3 = (File)var1.getNewValue();
               if (var3 != null) {
                  MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getFileChooser().getName(var3));
               }
            } else {
               JFileChooser var4;
               if (var2.equals("SelectedFilesChangedProperty")) {
                  File[] var7 = (File[])((File[])var1.getNewValue());
                  var4 = MotifFileChooserUI.this.getFileChooser();
                  if (var7 != null && var7.length > 0 && (var7.length > 1 || var4.isDirectorySelectionEnabled() || !var7[0].isDirectory())) {
                     MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.fileNameString(var7));
                  }
               } else if (var2.equals("fileFilterChanged")) {
                  MotifFileChooserUI.this.fileList.clearSelection();
               } else if (var2.equals("directoryChanged")) {
                  MotifFileChooserUI.this.directoryList.clearSelection();
                  ListSelectionModel var8 = MotifFileChooserUI.this.directoryList.getSelectionModel();
                  if (var8 instanceof DefaultListSelectionModel) {
                     ((DefaultListSelectionModel)var8).moveLeadSelectionIndex(0);
                     var8.setAnchorSelectionIndex(0);
                  }

                  MotifFileChooserUI.this.fileList.clearSelection();
                  var8 = MotifFileChooserUI.this.fileList.getSelectionModel();
                  if (var8 instanceof DefaultListSelectionModel) {
                     ((DefaultListSelectionModel)var8).moveLeadSelectionIndex(0);
                     var8.setAnchorSelectionIndex(0);
                  }

                  File var9 = MotifFileChooserUI.this.getFileChooser().getCurrentDirectory();
                  if (var9 != null) {
                     try {
                        MotifFileChooserUI.this.setDirectoryName(ShellFolder.getNormalizedFile((File)var1.getNewValue()).getPath());
                     } catch (IOException var6) {
                        MotifFileChooserUI.this.setDirectoryName(((File)var1.getNewValue()).getAbsolutePath());
                     }

                     if (MotifFileChooserUI.this.getFileChooser().getFileSelectionMode() == 1 && !MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                        MotifFileChooserUI.this.setFileName(MotifFileChooserUI.this.getDirectoryName());
                     }
                  }
               } else if (var2.equals("fileSelectionChanged")) {
                  if (MotifFileChooserUI.this.fileNameLabel != null) {
                     MotifFileChooserUI.this.populateFileNameLabel();
                  }

                  MotifFileChooserUI.this.directoryList.clearSelection();
               } else if (var2.equals("MultiSelectionEnabledChangedProperty")) {
                  if (MotifFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
                     MotifFileChooserUI.this.fileList.setSelectionMode(2);
                  } else {
                     MotifFileChooserUI.this.fileList.setSelectionMode(0);
                     MotifFileChooserUI.this.fileList.clearSelection();
                     MotifFileChooserUI.this.getFileChooser().setSelectedFiles((File[])null);
                  }
               } else if (var2.equals("AccessoryChangedProperty")) {
                  if (MotifFileChooserUI.this.getAccessoryPanel() != null) {
                     if (var1.getOldValue() != null) {
                        MotifFileChooserUI.this.getAccessoryPanel().remove((JComponent)var1.getOldValue());
                     }

                     JComponent var10 = (JComponent)var1.getNewValue();
                     if (var10 != null) {
                        MotifFileChooserUI.this.getAccessoryPanel().add(var10, "Center");
                        MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(MotifFileChooserUI.PREF_ACC_SIZE);
                        MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MotifFileChooserUI.MAX_SIZE);
                     } else {
                        MotifFileChooserUI.this.getAccessoryPanel().setPreferredSize(MotifFileChooserUI.ZERO_ACC_SIZE);
                        MotifFileChooserUI.this.getAccessoryPanel().setMaximumSize(MotifFileChooserUI.ZERO_ACC_SIZE);
                     }
                  }
               } else if (!var2.equals("ApproveButtonTextChangedProperty") && !var2.equals("ApproveButtonToolTipTextChangedProperty") && !var2.equals("DialogTypeChangedProperty")) {
                  if (var2.equals("ControlButtonsAreShownChangedProperty")) {
                     MotifFileChooserUI.this.doControlButtonsChanged(var1);
                  } else if (var2.equals("componentOrientation")) {
                     ComponentOrientation var11 = (ComponentOrientation)var1.getNewValue();
                     var4 = (JFileChooser)var1.getSource();
                     if (var11 != (ComponentOrientation)var1.getOldValue()) {
                        var4.applyComponentOrientation(var11);
                     }
                  }
               } else {
                  MotifFileChooserUI.this.approveButton.setText(MotifFileChooserUI.this.getApproveButtonText(MotifFileChooserUI.this.getFileChooser()));
                  MotifFileChooserUI.this.approveButton.setToolTipText(MotifFileChooserUI.this.getApproveButtonToolTipText(MotifFileChooserUI.this.getFileChooser()));
               }
            }

         }
      };
   }

   public static ComponentUI createUI(JComponent var0) {
      return new MotifFileChooserUI((JFileChooser)var0);
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
   }

   public void uninstallUI(JComponent var1) {
      var1.removePropertyChangeListener(this.filterComboBoxModel);
      this.approveButton.removeActionListener(this.getApproveSelectionAction());
      this.filenameTextField.removeActionListener(this.getApproveSelectionAction());
      super.uninstallUI(var1);
   }

   public void installComponents(JFileChooser var1) {
      var1.setLayout(new BorderLayout(10, 10));
      var1.setAlignmentX(0.5F);
      JPanel var2 = new JPanel() {
         public Insets getInsets() {
            return MotifFileChooserUI.insets;
         }
      };
      var2.setInheritsPopupMenu(true);
      this.align(var2);
      var2.setLayout(new BoxLayout(var2, 3));
      var1.add(var2, "Center");
      JLabel var3 = new JLabel(this.pathLabelText);
      var3.setDisplayedMnemonic(this.pathLabelMnemonic);
      this.align(var3);
      var2.add(var3);
      File var4 = var1.getCurrentDirectory();
      String var5 = null;
      if (var4 != null) {
         var5 = var4.getPath();
      }

      this.pathField = new JTextField(var5) {
         public Dimension getMaximumSize() {
            Dimension var1 = super.getMaximumSize();
            var1.height = this.getPreferredSize().height;
            return var1;
         }
      };
      this.pathField.setInheritsPopupMenu(true);
      var3.setLabelFor(this.pathField);
      this.align(this.pathField);
      this.pathField.addActionListener(this.getUpdateAction());
      var2.add(this.pathField);
      var2.add(Box.createRigidArea(vstrut10));
      JPanel var6 = new JPanel();
      var6.setLayout(new BoxLayout(var6, 2));
      this.align(var6);
      JPanel var7 = new JPanel();
      var7.setLayout(new BoxLayout(var7, 3));
      this.align(var7);
      var3 = new JLabel(this.filterLabelText);
      var3.setDisplayedMnemonic(this.filterLabelMnemonic);
      this.align(var3);
      var7.add(var3);
      this.filterComboBox = new JComboBox<FileFilter>() {
         public Dimension getMaximumSize() {
            Dimension var1 = super.getMaximumSize();
            var1.height = this.getPreferredSize().height;
            return var1;
         }
      };
      this.filterComboBox.setInheritsPopupMenu(true);
      var3.setLabelFor(this.filterComboBox);
      this.filterComboBoxModel = this.createFilterComboBoxModel();
      this.filterComboBox.setModel(this.filterComboBoxModel);
      this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
      var1.addPropertyChangeListener(this.filterComboBoxModel);
      this.align(this.filterComboBox);
      var7.add(this.filterComboBox);
      var3 = new JLabel(this.foldersLabelText);
      var3.setDisplayedMnemonic(this.foldersLabelMnemonic);
      this.align(var3);
      var7.add(var3);
      JScrollPane var8 = this.createDirectoryList();
      var8.getVerticalScrollBar().setFocusable(false);
      var8.getHorizontalScrollBar().setFocusable(false);
      var8.setInheritsPopupMenu(true);
      var3.setLabelFor(var8.getViewport().getView());
      var7.add(var8);
      var7.setInheritsPopupMenu(true);
      JPanel var9 = new JPanel();
      this.align(var9);
      var9.setLayout(new BoxLayout(var9, 3));
      var9.setInheritsPopupMenu(true);
      var3 = new JLabel(this.filesLabelText);
      var3.setDisplayedMnemonic(this.filesLabelMnemonic);
      this.align(var3);
      var9.add(var3);
      var8 = this.createFilesList();
      var3.setLabelFor(var8.getViewport().getView());
      var9.add(var8);
      var8.setInheritsPopupMenu(true);
      var6.add(var7);
      var6.add(Box.createRigidArea(hstrut10));
      var6.add(var9);
      var6.setInheritsPopupMenu(true);
      JPanel var10 = this.getAccessoryPanel();
      JComponent var11 = var1.getAccessory();
      if (var10 != null) {
         if (var11 == null) {
            var10.setPreferredSize(ZERO_ACC_SIZE);
            var10.setMaximumSize(ZERO_ACC_SIZE);
         } else {
            this.getAccessoryPanel().add(var11, "Center");
            var10.setPreferredSize(PREF_ACC_SIZE);
            var10.setMaximumSize(MAX_SIZE);
         }

         this.align(var10);
         var6.add(var10);
         var10.setInheritsPopupMenu(true);
      }

      var2.add(var6);
      var2.add(Box.createRigidArea(vstrut10));
      this.fileNameLabel = new JLabel();
      this.populateFileNameLabel();
      this.align(this.fileNameLabel);
      var2.add(this.fileNameLabel);
      this.filenameTextField = new JTextField() {
         public Dimension getMaximumSize() {
            Dimension var1 = super.getMaximumSize();
            var1.height = this.getPreferredSize().height;
            return var1;
         }
      };
      this.filenameTextField.setInheritsPopupMenu(true);
      this.fileNameLabel.setLabelFor(this.filenameTextField);
      this.filenameTextField.addActionListener(this.getApproveSelectionAction());
      this.align(this.filenameTextField);
      this.filenameTextField.setAlignmentX(0.0F);
      var2.add(this.filenameTextField);
      this.bottomPanel = this.getBottomPanel();
      this.bottomPanel.add(new JSeparator(), "North");
      JPanel var12 = new JPanel();
      this.align(var12);
      var12.setLayout(new BoxLayout(var12, 2));
      var12.add(Box.createGlue());
      this.approveButton = new JButton(this.getApproveButtonText(var1)) {
         public Dimension getMaximumSize() {
            return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
         }
      };
      this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var1));
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var1));
      this.approveButton.setInheritsPopupMenu(true);
      this.align(this.approveButton);
      this.approveButton.setMargin(buttonMargin);
      this.approveButton.addActionListener(this.getApproveSelectionAction());
      var12.add(this.approveButton);
      var12.add(Box.createGlue());
      JButton var13 = new JButton(this.updateButtonText) {
         public Dimension getMaximumSize() {
            return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
         }
      };
      var13.setMnemonic(this.updateButtonMnemonic);
      var13.setToolTipText(this.updateButtonToolTipText);
      var13.setInheritsPopupMenu(true);
      this.align(var13);
      var13.setMargin(buttonMargin);
      var13.addActionListener(this.getUpdateAction());
      var12.add(var13);
      var12.add(Box.createGlue());
      JButton var14 = new JButton(this.cancelButtonText) {
         public Dimension getMaximumSize() {
            return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
         }
      };
      var14.setMnemonic(this.cancelButtonMnemonic);
      var14.setToolTipText(this.cancelButtonToolTipText);
      var14.setInheritsPopupMenu(true);
      this.align(var14);
      var14.setMargin(buttonMargin);
      var14.addActionListener(this.getCancelSelectionAction());
      var12.add(var14);
      var12.add(Box.createGlue());
      JButton var15 = new JButton(this.helpButtonText) {
         public Dimension getMaximumSize() {
            return new Dimension(MotifFileChooserUI.MAX_SIZE.width, this.getPreferredSize().height);
         }
      };
      var15.setMnemonic(this.helpButtonMnemonic);
      var15.setToolTipText(this.helpButtonToolTipText);
      this.align(var15);
      var15.setMargin(buttonMargin);
      var15.setEnabled(false);
      var15.setInheritsPopupMenu(true);
      var12.add(var15);
      var12.add(Box.createGlue());
      var12.setInheritsPopupMenu(true);
      this.bottomPanel.add(var12, "South");
      this.bottomPanel.setInheritsPopupMenu(true);
      if (var1.getControlButtonsAreShown()) {
         var1.add(this.bottomPanel, "South");
      }

   }

   protected JPanel getBottomPanel() {
      if (this.bottomPanel == null) {
         this.bottomPanel = new JPanel(new BorderLayout(0, 4));
      }

      return this.bottomPanel;
   }

   private void doControlButtonsChanged(PropertyChangeEvent var1) {
      if (this.getFileChooser().getControlButtonsAreShown()) {
         this.getFileChooser().add(this.bottomPanel, "South");
      } else {
         this.getFileChooser().remove(this.getBottomPanel());
      }

   }

   public void uninstallComponents(JFileChooser var1) {
      var1.removeAll();
      this.bottomPanel = null;
      if (this.filterComboBoxModel != null) {
         var1.removePropertyChangeListener(this.filterComboBoxModel);
      }

   }

   protected void installStrings(JFileChooser var1) {
      super.installStrings(var1);
      Locale var2 = var1.getLocale();
      this.enterFolderNameLabelText = UIManager.getString("FileChooser.enterFolderNameLabelText", (Locale)var2);
      this.enterFolderNameLabelMnemonic = this.getMnemonic("FileChooser.enterFolderNameLabelMnemonic", var2);
      this.enterFileNameLabelText = UIManager.getString("FileChooser.enterFileNameLabelText", (Locale)var2);
      this.enterFileNameLabelMnemonic = this.getMnemonic("FileChooser.enterFileNameLabelMnemonic", var2);
      this.filesLabelText = UIManager.getString("FileChooser.filesLabelText", (Locale)var2);
      this.filesLabelMnemonic = this.getMnemonic("FileChooser.filesLabelMnemonic", var2);
      this.foldersLabelText = UIManager.getString("FileChooser.foldersLabelText", (Locale)var2);
      this.foldersLabelMnemonic = this.getMnemonic("FileChooser.foldersLabelMnemonic", var2);
      this.pathLabelText = UIManager.getString("FileChooser.pathLabelText", (Locale)var2);
      this.pathLabelMnemonic = this.getMnemonic("FileChooser.pathLabelMnemonic", var2);
      this.filterLabelText = UIManager.getString("FileChooser.filterLabelText", (Locale)var2);
      this.filterLabelMnemonic = this.getMnemonic("FileChooser.filterLabelMnemonic", var2);
   }

   private Integer getMnemonic(String var1, Locale var2) {
      return SwingUtilities2.getUIDefaultsInt(var1, var2);
   }

   protected void installIcons(JFileChooser var1) {
   }

   protected void uninstallIcons(JFileChooser var1) {
   }

   protected JScrollPane createFilesList() {
      this.fileList = new JList();
      if (this.getFileChooser().isMultiSelectionEnabled()) {
         this.fileList.setSelectionMode(2);
      } else {
         this.fileList.setSelectionMode(0);
      }

      this.fileList.setModel(new MotifFileChooserUI.MotifFileListModel());
      this.fileList.getSelectionModel().removeSelectionInterval(0, 0);
      this.fileList.setCellRenderer(new MotifFileChooserUI.FileCellRenderer());
      this.fileList.addListSelectionListener(this.createListSelectionListener(this.getFileChooser()));
      this.fileList.addMouseListener(this.createDoubleClickListener(this.getFileChooser(), this.fileList));
      this.fileList.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent var1) {
            JFileChooser var2 = MotifFileChooserUI.this.getFileChooser();
            if (SwingUtilities.isLeftMouseButton(var1) && !var2.isMultiSelectionEnabled()) {
               int var3 = SwingUtilities2.loc2IndexFileList(MotifFileChooserUI.this.fileList, var1.getPoint());
               if (var3 >= 0) {
                  File var4 = (File)MotifFileChooserUI.this.fileList.getModel().getElementAt(var3);
                  MotifFileChooserUI.this.setFileName(var2.getName(var4));
               }
            }

         }
      });
      this.align(this.fileList);
      JScrollPane var1 = new JScrollPane(this.fileList);
      var1.setPreferredSize(prefListSize);
      var1.setMaximumSize(MAX_SIZE);
      this.align(var1);
      this.fileList.setInheritsPopupMenu(true);
      var1.setInheritsPopupMenu(true);
      return var1;
   }

   protected JScrollPane createDirectoryList() {
      this.directoryList = new JList();
      this.align(this.directoryList);
      this.directoryList.setCellRenderer(new MotifFileChooserUI.DirectoryCellRenderer());
      this.directoryList.setModel(new MotifFileChooserUI.MotifDirectoryListModel());
      this.directoryList.getSelectionModel().removeSelectionInterval(0, 0);
      this.directoryList.addMouseListener(this.createDoubleClickListener(this.getFileChooser(), this.directoryList));
      this.directoryList.addListSelectionListener(this.createListSelectionListener(this.getFileChooser()));
      this.directoryList.setInheritsPopupMenu(true);
      JScrollPane var1 = new JScrollPane(this.directoryList);
      var1.setMaximumSize(MAX_SIZE);
      var1.setPreferredSize(prefListSize);
      var1.setInheritsPopupMenu(true);
      this.align(var1);
      return var1;
   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = this.getFileChooser().getAccessory() != null ? WITH_ACCELERATOR_PREF_SIZE : PREF_SIZE;
      Dimension var3 = var1.getLayout().preferredLayoutSize(var1);
      return var3 != null ? new Dimension(var3.width < var2.width ? var2.width : var3.width, var3.height < var2.height ? var2.height : var3.height) : var2;
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

   protected MotifFileChooserUI.FilterComboBoxModel createFilterComboBoxModel() {
      return new MotifFileChooserUI.FilterComboBoxModel();
   }

   protected MotifFileChooserUI.FilterComboBoxRenderer createFilterComboBoxRenderer() {
      return new MotifFileChooserUI.FilterComboBoxRenderer();
   }

   protected JButton getApproveButton(JFileChooser var1) {
      return this.approveButton;
   }

   protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener {
      protected FileFilter[] filters = MotifFileChooserUI.this.getFileChooser().getChoosableFileFilters();

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("ChoosableFileFilterChangedProperty")) {
            this.filters = (FileFilter[])((FileFilter[])var1.getNewValue());
            this.fireContentsChanged(this, -1, -1);
         } else if (var2.equals("fileFilterChanged")) {
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public void setSelectedItem(Object var1) {
         if (var1 != null) {
            MotifFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)var1);
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public Object getSelectedItem() {
         FileFilter var1 = MotifFileChooserUI.this.getFileChooser().getFileFilter();
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
               MotifFileChooserUI.this.getFileChooser().addChoosableFileFilter(var1);
            }
         }

         return MotifFileChooserUI.this.getFileChooser().getFileFilter();
      }

      public int getSize() {
         return this.filters != null ? this.filters.length : 0;
      }

      public FileFilter getElementAt(int var1) {
         if (var1 > this.getSize() - 1) {
            return MotifFileChooserUI.this.getFileChooser().getFileFilter();
         } else {
            return this.filters != null ? this.filters[var1] : null;
         }
      }
   }

   public class FilterComboBoxRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var2 != null && var2 instanceof FileFilter) {
            this.setText(((FileFilter)var2).getDescription());
         }

         return this;
      }
   }

   protected class MotifFileListModel extends AbstractListModel<File> implements ListDataListener {
      public MotifFileListModel() {
         MotifFileChooserUI.this.getModel().addListDataListener(this);
      }

      public int getSize() {
         return MotifFileChooserUI.this.getModel().getFiles().size();
      }

      public boolean contains(Object var1) {
         return MotifFileChooserUI.this.getModel().getFiles().contains(var1);
      }

      public int indexOf(Object var1) {
         return MotifFileChooserUI.this.getModel().getFiles().indexOf(var1);
      }

      public File getElementAt(int var1) {
         return (File)MotifFileChooserUI.this.getModel().getFiles().elementAt(var1);
      }

      public void intervalAdded(ListDataEvent var1) {
         this.fireIntervalAdded(this, var1.getIndex0(), var1.getIndex1());
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.fireIntervalRemoved(this, var1.getIndex0(), var1.getIndex1());
      }

      public void fireContentsChanged() {
         this.fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getFiles().size() - 1);
      }

      public void contentsChanged(ListDataEvent var1) {
         this.fireContentsChanged();
      }
   }

   protected class MotifDirectoryListModel extends AbstractListModel<File> implements ListDataListener {
      public MotifDirectoryListModel() {
         MotifFileChooserUI.this.getModel().addListDataListener(this);
      }

      public int getSize() {
         return MotifFileChooserUI.this.getModel().getDirectories().size();
      }

      public File getElementAt(int var1) {
         return (File)MotifFileChooserUI.this.getModel().getDirectories().elementAt(var1);
      }

      public void intervalAdded(ListDataEvent var1) {
         this.fireIntervalAdded(this, var1.getIndex0(), var1.getIndex1());
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.fireIntervalRemoved(this, var1.getIndex0(), var1.getIndex1());
      }

      public void fireContentsChanged() {
         this.fireContentsChanged(this, 0, MotifFileChooserUI.this.getModel().getDirectories().size() - 1);
      }

      public void contentsChanged(ListDataEvent var1) {
         this.fireContentsChanged();
      }
   }

   protected class DirectoryCellRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         this.setText(MotifFileChooserUI.this.getFileChooser().getName((File)var2));
         this.setInheritsPopupMenu(true);
         return this;
      }
   }

   protected class FileCellRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         this.setText(MotifFileChooserUI.this.getFileChooser().getName((File)var2));
         this.setInheritsPopupMenu(true);
         return this;
      }
   }
}
