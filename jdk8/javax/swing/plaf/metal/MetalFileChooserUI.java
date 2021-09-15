package javax.swing.plaf.metal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;

public class MetalFileChooserUI extends BasicFileChooserUI {
   private JLabel lookInLabel;
   private JComboBox directoryComboBox;
   private MetalFileChooserUI.DirectoryComboBoxModel directoryComboBoxModel;
   private Action directoryComboBoxAction = new MetalFileChooserUI.DirectoryComboBoxAction();
   private MetalFileChooserUI.FilterComboBoxModel filterComboBoxModel;
   private JTextField fileNameTextField;
   private FilePane filePane;
   private JToggleButton listViewButton;
   private JToggleButton detailsViewButton;
   private JButton approveButton;
   private JButton cancelButton;
   private JPanel buttonPanel;
   private JPanel bottomPanel;
   private JComboBox filterComboBox;
   private static final Dimension hstrut5 = new Dimension(5, 1);
   private static final Dimension hstrut11 = new Dimension(11, 1);
   private static final Dimension vstrut5 = new Dimension(1, 5);
   private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
   private static int PREF_WIDTH = 500;
   private static int PREF_HEIGHT = 326;
   private static Dimension PREF_SIZE;
   private static int MIN_WIDTH;
   private static int MIN_HEIGHT;
   private static int LIST_PREF_WIDTH;
   private static int LIST_PREF_HEIGHT;
   private static Dimension LIST_PREF_SIZE;
   private int lookInLabelMnemonic = 0;
   private String lookInLabelText = null;
   private String saveInLabelText = null;
   private int fileNameLabelMnemonic = 0;
   private String fileNameLabelText = null;
   private int folderNameLabelMnemonic = 0;
   private String folderNameLabelText = null;
   private int filesOfTypeLabelMnemonic = 0;
   private String filesOfTypeLabelText = null;
   private String upFolderToolTipText = null;
   private String upFolderAccessibleName = null;
   private String homeFolderToolTipText = null;
   private String homeFolderAccessibleName = null;
   private String newFolderToolTipText = null;
   private String newFolderAccessibleName = null;
   private String listViewButtonToolTipText = null;
   private String listViewButtonAccessibleName = null;
   private String detailsViewButtonToolTipText = null;
   private String detailsViewButtonAccessibleName = null;
   private MetalFileChooserUI.AlignedLabel fileNameLabel;
   static final int space = 10;

   private void populateFileNameLabel() {
      if (this.getFileChooser().getFileSelectionMode() == 1) {
         this.fileNameLabel.setText(this.folderNameLabelText);
         this.fileNameLabel.setDisplayedMnemonic(this.folderNameLabelMnemonic);
      } else {
         this.fileNameLabel.setText(this.fileNameLabelText);
         this.fileNameLabel.setDisplayedMnemonic(this.fileNameLabelMnemonic);
      }

   }

   public static ComponentUI createUI(JComponent var0) {
      return new MetalFileChooserUI((JFileChooser)var0);
   }

   public MetalFileChooserUI(JFileChooser var1) {
      super(var1);
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
   }

   public void uninstallComponents(JFileChooser var1) {
      var1.removeAll();
      this.bottomPanel = null;
      this.buttonPanel = null;
   }

   public void installComponents(JFileChooser var1) {
      FileSystemView var2 = var1.getFileSystemView();
      var1.setBorder(new EmptyBorder(12, 12, 11, 11));
      var1.setLayout(new BorderLayout(0, 11));
      this.filePane = new FilePane(new MetalFileChooserUI.MetalFileChooserUIAccessor());
      var1.addPropertyChangeListener(this.filePane);
      JPanel var3 = new JPanel(new BorderLayout(11, 0));
      JPanel var4 = new JPanel();
      var4.setLayout(new BoxLayout(var4, 2));
      var3.add(var4, "After");
      var1.add(var3, "North");
      this.lookInLabel = new JLabel(this.lookInLabelText);
      this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
      var3.add(this.lookInLabel, "Before");
      this.directoryComboBox = new JComboBox() {
         public Dimension getPreferredSize() {
            Dimension var1 = super.getPreferredSize();
            var1.width = 150;
            return var1;
         }
      };
      this.directoryComboBox.putClientProperty("AccessibleDescription", this.lookInLabelText);
      this.directoryComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
      this.lookInLabel.setLabelFor(this.directoryComboBox);
      this.directoryComboBoxModel = this.createDirectoryComboBoxModel(var1);
      this.directoryComboBox.setModel(this.directoryComboBoxModel);
      this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
      this.directoryComboBox.setRenderer(this.createDirectoryComboBoxRenderer(var1));
      this.directoryComboBox.setAlignmentX(0.0F);
      this.directoryComboBox.setAlignmentY(0.0F);
      this.directoryComboBox.setMaximumRowCount(8);
      var3.add(this.directoryComboBox, "Center");
      JButton var5 = new JButton(this.getChangeToParentDirectoryAction());
      var5.setText((String)null);
      var5.setIcon(this.upFolderIcon);
      var5.setToolTipText(this.upFolderToolTipText);
      var5.putClientProperty("AccessibleName", this.upFolderAccessibleName);
      var5.setAlignmentX(0.0F);
      var5.setAlignmentY(0.5F);
      var5.setMargin(shrinkwrap);
      var4.add(var5);
      var4.add(Box.createRigidArea(hstrut5));
      File var6 = var2.getHomeDirectory();
      String var7 = this.homeFolderToolTipText;
      JButton var8 = new JButton(this.homeFolderIcon);
      var8.setToolTipText(var7);
      var8.putClientProperty("AccessibleName", this.homeFolderAccessibleName);
      var8.setAlignmentX(0.0F);
      var8.setAlignmentY(0.5F);
      var8.setMargin(shrinkwrap);
      var8.addActionListener(this.getGoHomeAction());
      var4.add(var8);
      var4.add(Box.createRigidArea(hstrut5));
      if (!UIManager.getBoolean("FileChooser.readOnly")) {
         var8 = new JButton(this.filePane.getNewFolderAction());
         var8.setText((String)null);
         var8.setIcon(this.newFolderIcon);
         var8.setToolTipText(this.newFolderToolTipText);
         var8.putClientProperty("AccessibleName", this.newFolderAccessibleName);
         var8.setAlignmentX(0.0F);
         var8.setAlignmentY(0.5F);
         var8.setMargin(shrinkwrap);
      }

      var4.add(var8);
      var4.add(Box.createRigidArea(hstrut5));
      ButtonGroup var9 = new ButtonGroup();
      this.listViewButton = new JToggleButton(this.listViewIcon);
      this.listViewButton.setToolTipText(this.listViewButtonToolTipText);
      this.listViewButton.putClientProperty("AccessibleName", this.listViewButtonAccessibleName);
      this.listViewButton.setSelected(true);
      this.listViewButton.setAlignmentX(0.0F);
      this.listViewButton.setAlignmentY(0.5F);
      this.listViewButton.setMargin(shrinkwrap);
      this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
      var4.add(this.listViewButton);
      var9.add(this.listViewButton);
      this.detailsViewButton = new JToggleButton(this.detailsViewIcon);
      this.detailsViewButton.setToolTipText(this.detailsViewButtonToolTipText);
      this.detailsViewButton.putClientProperty("AccessibleName", this.detailsViewButtonAccessibleName);
      this.detailsViewButton.setAlignmentX(0.0F);
      this.detailsViewButton.setAlignmentY(0.5F);
      this.detailsViewButton.setMargin(shrinkwrap);
      this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
      var4.add(this.detailsViewButton);
      var9.add(this.detailsViewButton);
      this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            if ("viewType".equals(var1.getPropertyName())) {
               int var2 = MetalFileChooserUI.this.filePane.getViewType();
               switch(var2) {
               case 0:
                  MetalFileChooserUI.this.listViewButton.setSelected(true);
                  break;
               case 1:
                  MetalFileChooserUI.this.detailsViewButton.setSelected(true);
               }
            }

         }
      });
      var1.add(this.getAccessoryPanel(), "After");
      JComponent var10 = var1.getAccessory();
      if (var10 != null) {
         this.getAccessoryPanel().add(var10);
      }

      this.filePane.setPreferredSize(LIST_PREF_SIZE);
      var1.add(this.filePane, "Center");
      JPanel var11 = this.getBottomPanel();
      var11.setLayout(new BoxLayout(var11, 1));
      var1.add(var11, "South");
      JPanel var12 = new JPanel();
      var12.setLayout(new BoxLayout(var12, 2));
      var11.add(var12);
      var11.add(Box.createRigidArea(vstrut5));
      this.fileNameLabel = new MetalFileChooserUI.AlignedLabel();
      this.populateFileNameLabel();
      var12.add(this.fileNameLabel);
      this.fileNameTextField = new JTextField(35) {
         public Dimension getMaximumSize() {
            return new Dimension(32767, super.getPreferredSize().height);
         }
      };
      var12.add(this.fileNameTextField);
      this.fileNameLabel.setLabelFor(this.fileNameTextField);
      this.fileNameTextField.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
            if (!MetalFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
               MetalFileChooserUI.this.filePane.clearSelection();
            }

         }
      });
      if (var1.isMultiSelectionEnabled()) {
         this.setFileName(this.fileNameString(var1.getSelectedFiles()));
      } else {
         this.setFileName(this.fileNameString(var1.getSelectedFile()));
      }

      JPanel var13 = new JPanel();
      var13.setLayout(new BoxLayout(var13, 2));
      var11.add(var13);
      MetalFileChooserUI.AlignedLabel var14 = new MetalFileChooserUI.AlignedLabel(this.filesOfTypeLabelText);
      var14.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
      var13.add(var14);
      this.filterComboBoxModel = this.createFilterComboBoxModel();
      var1.addPropertyChangeListener(this.filterComboBoxModel);
      this.filterComboBox = new JComboBox(this.filterComboBoxModel);
      this.filterComboBox.putClientProperty("AccessibleDescription", this.filesOfTypeLabelText);
      var14.setLabelFor(this.filterComboBox);
      this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
      var13.add(this.filterComboBox);
      this.getButtonPanel().setLayout(new MetalFileChooserUI.ButtonAreaLayout());
      this.approveButton = new JButton(this.getApproveButtonText(var1));
      this.approveButton.addActionListener(this.getApproveSelectionAction());
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var1));
      this.getButtonPanel().add(this.approveButton);
      this.cancelButton = new JButton(this.cancelButtonText);
      this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
      this.cancelButton.addActionListener(this.getCancelSelectionAction());
      this.getButtonPanel().add(this.cancelButton);
      if (var1.getControlButtonsAreShown()) {
         this.addControlButtons();
      }

      groupLabels(new MetalFileChooserUI.AlignedLabel[]{this.fileNameLabel, var14});
   }

   protected JPanel getButtonPanel() {
      if (this.buttonPanel == null) {
         this.buttonPanel = new JPanel();
      }

      return this.buttonPanel;
   }

   protected JPanel getBottomPanel() {
      if (this.bottomPanel == null) {
         this.bottomPanel = new JPanel();
      }

      return this.bottomPanel;
   }

   protected void installStrings(JFileChooser var1) {
      super.installStrings(var1);
      Locale var2 = var1.getLocale();
      this.lookInLabelMnemonic = this.getMnemonic("FileChooser.lookInLabelMnemonic", var2);
      this.lookInLabelText = UIManager.getString("FileChooser.lookInLabelText", (Locale)var2);
      this.saveInLabelText = UIManager.getString("FileChooser.saveInLabelText", (Locale)var2);
      this.fileNameLabelMnemonic = this.getMnemonic("FileChooser.fileNameLabelMnemonic", var2);
      this.fileNameLabelText = UIManager.getString("FileChooser.fileNameLabelText", (Locale)var2);
      this.folderNameLabelMnemonic = this.getMnemonic("FileChooser.folderNameLabelMnemonic", var2);
      this.folderNameLabelText = UIManager.getString("FileChooser.folderNameLabelText", (Locale)var2);
      this.filesOfTypeLabelMnemonic = this.getMnemonic("FileChooser.filesOfTypeLabelMnemonic", var2);
      this.filesOfTypeLabelText = UIManager.getString("FileChooser.filesOfTypeLabelText", (Locale)var2);
      this.upFolderToolTipText = UIManager.getString("FileChooser.upFolderToolTipText", (Locale)var2);
      this.upFolderAccessibleName = UIManager.getString("FileChooser.upFolderAccessibleName", (Locale)var2);
      this.homeFolderToolTipText = UIManager.getString("FileChooser.homeFolderToolTipText", (Locale)var2);
      this.homeFolderAccessibleName = UIManager.getString("FileChooser.homeFolderAccessibleName", (Locale)var2);
      this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", (Locale)var2);
      this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", (Locale)var2);
      this.listViewButtonToolTipText = UIManager.getString("FileChooser.listViewButtonToolTipText", (Locale)var2);
      this.listViewButtonAccessibleName = UIManager.getString("FileChooser.listViewButtonAccessibleName", (Locale)var2);
      this.detailsViewButtonToolTipText = UIManager.getString("FileChooser.detailsViewButtonToolTipText", (Locale)var2);
      this.detailsViewButtonAccessibleName = UIManager.getString("FileChooser.detailsViewButtonAccessibleName", (Locale)var2);
   }

   private Integer getMnemonic(String var1, Locale var2) {
      return SwingUtilities2.getUIDefaultsInt(var1, var2);
   }

   protected void installListeners(JFileChooser var1) {
      super.installListeners(var1);
      ActionMap var2 = this.getActionMap();
      SwingUtilities.replaceUIActionMap(var1, var2);
   }

   protected ActionMap getActionMap() {
      return this.createActionMap();
   }

   protected ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      FilePane.addActionsToMap(var1, this.filePane.getActions());
      return var1;
   }

   protected JPanel createList(JFileChooser var1) {
      return this.filePane.createList();
   }

   protected JPanel createDetailsView(JFileChooser var1) {
      return this.filePane.createDetailsView();
   }

   public ListSelectionListener createListSelectionListener(JFileChooser var1) {
      return super.createListSelectionListener(var1);
   }

   public void uninstallUI(JComponent var1) {
      var1.removePropertyChangeListener(this.filterComboBoxModel);
      var1.removePropertyChangeListener(this.filePane);
      this.cancelButton.removeActionListener(this.getCancelSelectionAction());
      this.approveButton.removeActionListener(this.getApproveSelectionAction());
      this.fileNameTextField.removeActionListener(this.getApproveSelectionAction());
      if (this.filePane != null) {
         this.filePane.uninstallUI();
         this.filePane = null;
      }

      super.uninstallUI(var1);
   }

   public Dimension getPreferredSize(JComponent var1) {
      int var2 = PREF_SIZE.width;
      Dimension var3 = var1.getLayout().preferredLayoutSize(var1);
      return var3 != null ? new Dimension(var3.width < var2 ? var2 : var3.width, var3.height < PREF_SIZE.height ? PREF_SIZE.height : var3.height) : new Dimension(var2, PREF_SIZE.height);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return new Dimension(MIN_WIDTH, MIN_HEIGHT);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   private String fileNameString(File var1) {
      if (var1 == null) {
         return null;
      } else {
         JFileChooser var2 = this.getFileChooser();
         return (!var2.isDirectorySelectionEnabled() || var2.isFileSelectionEnabled()) && (!var2.isDirectorySelectionEnabled() || !var2.isFileSelectionEnabled() || !var2.getFileSystemView().isFileSystemRoot(var1)) ? var1.getName() : var1.getPath();
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

   private void doSelectedFileChanged(PropertyChangeEvent var1) {
      File var2 = (File)var1.getNewValue();
      JFileChooser var3 = this.getFileChooser();
      if (var2 != null && (var3.isFileSelectionEnabled() && !var2.isDirectory() || var2.isDirectory() && var3.isDirectorySelectionEnabled())) {
         this.setFileName(this.fileNameString(var2));
      }

   }

   private void doSelectedFilesChanged(PropertyChangeEvent var1) {
      File[] var2 = (File[])((File[])var1.getNewValue());
      JFileChooser var3 = this.getFileChooser();
      if (var2 != null && var2.length > 0 && (var2.length > 1 || var3.isDirectorySelectionEnabled() || !var2[0].isDirectory())) {
         this.setFileName(this.fileNameString(var2));
      }

   }

   private void doDirectoryChanged(PropertyChangeEvent var1) {
      JFileChooser var2 = this.getFileChooser();
      FileSystemView var3 = var2.getFileSystemView();
      this.clearIconCache();
      File var4 = var2.getCurrentDirectory();
      if (var4 != null) {
         this.directoryComboBoxModel.addItem(var4);
         if (var2.isDirectorySelectionEnabled() && !var2.isFileSelectionEnabled()) {
            if (var3.isFileSystem(var4)) {
               this.setFileName(var4.getPath());
            } else {
               this.setFileName((String)null);
            }
         }
      }

   }

   private void doFilterChanged(PropertyChangeEvent var1) {
      this.clearIconCache();
   }

   private void doFileSelectionModeChanged(PropertyChangeEvent var1) {
      if (this.fileNameLabel != null) {
         this.populateFileNameLabel();
      }

      this.clearIconCache();
      JFileChooser var2 = this.getFileChooser();
      File var3 = var2.getCurrentDirectory();
      if (var3 != null && var2.isDirectorySelectionEnabled() && !var2.isFileSelectionEnabled() && var2.getFileSystemView().isFileSystem(var3)) {
         this.setFileName(var3.getPath());
      } else {
         this.setFileName((String)null);
      }

   }

   private void doAccessoryChanged(PropertyChangeEvent var1) {
      if (this.getAccessoryPanel() != null) {
         if (var1.getOldValue() != null) {
            this.getAccessoryPanel().remove((JComponent)var1.getOldValue());
         }

         JComponent var2 = (JComponent)var1.getNewValue();
         if (var2 != null) {
            this.getAccessoryPanel().add(var2, "Center");
         }
      }

   }

   private void doApproveButtonTextChanged(PropertyChangeEvent var1) {
      JFileChooser var2 = this.getFileChooser();
      this.approveButton.setText(this.getApproveButtonText(var2));
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var2));
   }

   private void doDialogTypeChanged(PropertyChangeEvent var1) {
      JFileChooser var2 = this.getFileChooser();
      this.approveButton.setText(this.getApproveButtonText(var2));
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var2));
      if (var2.getDialogType() == 1) {
         this.lookInLabel.setText(this.saveInLabelText);
      } else {
         this.lookInLabel.setText(this.lookInLabelText);
      }

   }

   private void doApproveButtonMnemonicChanged(PropertyChangeEvent var1) {
   }

   private void doControlButtonsChanged(PropertyChangeEvent var1) {
      if (this.getFileChooser().getControlButtonsAreShown()) {
         this.addControlButtons();
      } else {
         this.removeControlButtons();
      }

   }

   public PropertyChangeListener createPropertyChangeListener(JFileChooser var1) {
      return new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            String var2 = var1.getPropertyName();
            if (var2.equals("SelectedFileChangedProperty")) {
               MetalFileChooserUI.this.doSelectedFileChanged(var1);
            } else if (var2.equals("SelectedFilesChangedProperty")) {
               MetalFileChooserUI.this.doSelectedFilesChanged(var1);
            } else if (var2.equals("directoryChanged")) {
               MetalFileChooserUI.this.doDirectoryChanged(var1);
            } else if (var2.equals("fileFilterChanged")) {
               MetalFileChooserUI.this.doFilterChanged(var1);
            } else if (var2.equals("fileSelectionChanged")) {
               MetalFileChooserUI.this.doFileSelectionModeChanged(var1);
            } else if (var2.equals("AccessoryChangedProperty")) {
               MetalFileChooserUI.this.doAccessoryChanged(var1);
            } else if (!var2.equals("ApproveButtonTextChangedProperty") && !var2.equals("ApproveButtonToolTipTextChangedProperty")) {
               if (var2.equals("DialogTypeChangedProperty")) {
                  MetalFileChooserUI.this.doDialogTypeChanged(var1);
               } else if (var2.equals("ApproveButtonMnemonicChangedProperty")) {
                  MetalFileChooserUI.this.doApproveButtonMnemonicChanged(var1);
               } else if (var2.equals("ControlButtonsAreShownChangedProperty")) {
                  MetalFileChooserUI.this.doControlButtonsChanged(var1);
               } else if (var2.equals("componentOrientation")) {
                  ComponentOrientation var3 = (ComponentOrientation)var1.getNewValue();
                  JFileChooser var4 = (JFileChooser)var1.getSource();
                  if (var3 != var1.getOldValue()) {
                     var4.applyComponentOrientation(var3);
                  }
               } else if (var2 == "FileChooser.useShellFolder") {
                  MetalFileChooserUI.this.doDirectoryChanged(var1);
               } else if (var2.equals("ancestor") && var1.getOldValue() == null && var1.getNewValue() != null) {
                  MetalFileChooserUI.this.fileNameTextField.selectAll();
                  MetalFileChooserUI.this.fileNameTextField.requestFocus();
               }
            } else {
               MetalFileChooserUI.this.doApproveButtonTextChanged(var1);
            }

         }
      };
   }

   protected void removeControlButtons() {
      this.getBottomPanel().remove(this.getButtonPanel());
   }

   protected void addControlButtons() {
      this.getBottomPanel().add(this.getButtonPanel());
   }

   public void ensureFileIsVisible(JFileChooser var1, File var2) {
      this.filePane.ensureFileIsVisible(var1, var2);
   }

   public void rescanCurrentDirectory(JFileChooser var1) {
      this.filePane.rescanCurrentDirectory();
   }

   public String getFileName() {
      return this.fileNameTextField != null ? this.fileNameTextField.getText() : null;
   }

   public void setFileName(String var1) {
      if (this.fileNameTextField != null) {
         this.fileNameTextField.setText(var1);
      }

   }

   protected void setDirectorySelected(boolean var1) {
      super.setDirectorySelected(var1);
      JFileChooser var2 = this.getFileChooser();
      if (var1) {
         if (this.approveButton != null) {
            this.approveButton.setText(this.directoryOpenButtonText);
            this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
         }
      } else if (this.approveButton != null) {
         this.approveButton.setText(this.getApproveButtonText(var2));
         this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var2));
      }

   }

   public String getDirectoryName() {
      return null;
   }

   public void setDirectoryName(String var1) {
   }

   protected MetalFileChooserUI.DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser var1) {
      return new MetalFileChooserUI.DirectoryComboBoxRenderer();
   }

   protected MetalFileChooserUI.DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser var1) {
      return new MetalFileChooserUI.DirectoryComboBoxModel();
   }

   protected MetalFileChooserUI.FilterComboBoxRenderer createFilterComboBoxRenderer() {
      return new MetalFileChooserUI.FilterComboBoxRenderer();
   }

   protected MetalFileChooserUI.FilterComboBoxModel createFilterComboBoxModel() {
      return new MetalFileChooserUI.FilterComboBoxModel();
   }

   public void valueChanged(ListSelectionEvent var1) {
      JFileChooser var2 = this.getFileChooser();
      File var3 = var2.getSelectedFile();
      if (!var1.getValueIsAdjusting() && var3 != null && !this.getFileChooser().isTraversable(var3)) {
         this.setFileName(this.fileNameString(var3));
      }

   }

   protected JButton getApproveButton(JFileChooser var1) {
      return this.approveButton;
   }

   private static void groupLabels(MetalFileChooserUI.AlignedLabel[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1].group = var0;
      }

   }

   static {
      PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
      MIN_WIDTH = 500;
      MIN_HEIGHT = 326;
      LIST_PREF_WIDTH = 405;
      LIST_PREF_HEIGHT = 135;
      LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
   }

   private class AlignedLabel extends JLabel {
      private MetalFileChooserUI.AlignedLabel[] group;
      private int maxWidth = 0;

      AlignedLabel() {
         this.setAlignmentX(0.0F);
      }

      AlignedLabel(String var2) {
         super(var2);
         this.setAlignmentX(0.0F);
      }

      public Dimension getPreferredSize() {
         Dimension var1 = super.getPreferredSize();
         return new Dimension(this.getMaxWidth() + 11, var1.height);
      }

      private int getMaxWidth() {
         if (this.maxWidth == 0 && this.group != null) {
            int var1 = 0;

            int var2;
            for(var2 = 0; var2 < this.group.length; ++var2) {
               var1 = Math.max(this.group[var2].getSuperPreferredWidth(), var1);
            }

            for(var2 = 0; var2 < this.group.length; ++var2) {
               this.group[var2].maxWidth = var1;
            }
         }

         return this.maxWidth;
      }

      private int getSuperPreferredWidth() {
         return super.getPreferredSize().width;
      }
   }

   private static class ButtonAreaLayout implements LayoutManager {
      private int hGap;
      private int topMargin;

      private ButtonAreaLayout() {
         this.hGap = 5;
         this.topMargin = 17;
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void layoutContainer(Container var1) {
         Component[] var2 = var1.getComponents();
         if (var2 != null && var2.length > 0) {
            int var3 = var2.length;
            Dimension[] var4 = new Dimension[var3];
            Insets var5 = var1.getInsets();
            int var6 = var5.top + this.topMargin;
            int var7 = 0;

            int var8;
            for(var8 = 0; var8 < var3; ++var8) {
               var4[var8] = var2[var8].getPreferredSize();
               var7 = Math.max(var7, var4[var8].width);
            }

            int var9;
            if (var1.getComponentOrientation().isLeftToRight()) {
               var8 = var1.getSize().width - var5.left - var7;
               var9 = this.hGap + var7;
            } else {
               var8 = var5.left;
               var9 = -(this.hGap + var7);
            }

            for(int var10 = var3 - 1; var10 >= 0; --var10) {
               var2[var10].setBounds(var8, var6, var7, var4[var10].height);
               var8 -= var9;
            }
         }

      }

      public Dimension minimumLayoutSize(Container var1) {
         if (var1 != null) {
            Component[] var2 = var1.getComponents();
            if (var2 != null && var2.length > 0) {
               int var3 = var2.length;
               int var4 = 0;
               Insets var5 = var1.getInsets();
               int var6 = this.topMargin + var5.top + var5.bottom;
               int var7 = var5.left + var5.right;
               int var8 = 0;

               for(int var9 = 0; var9 < var3; ++var9) {
                  Dimension var10 = var2[var9].getPreferredSize();
                  var4 = Math.max(var4, var10.height);
                  var8 = Math.max(var8, var10.width);
               }

               return new Dimension(var7 + var3 * var8 + (var3 - 1) * this.hGap, var6 + var4);
            }
         }

         return new Dimension(0, 0);
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public void removeLayoutComponent(Component var1) {
      }

      // $FF: synthetic method
      ButtonAreaLayout(Object var1) {
         this();
      }
   }

   protected class DirectoryComboBoxAction extends AbstractAction {
      protected DirectoryComboBoxAction() {
         super("DirectoryComboBoxAction");
      }

      public void actionPerformed(ActionEvent var1) {
         MetalFileChooserUI.this.directoryComboBox.hidePopup();
         File var2 = (File)MetalFileChooserUI.this.directoryComboBox.getSelectedItem();
         if (!MetalFileChooserUI.this.getFileChooser().getCurrentDirectory().equals(var2)) {
            MetalFileChooserUI.this.getFileChooser().setCurrentDirectory(var2);
         }

      }
   }

   protected class FilterComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object>, PropertyChangeListener {
      protected FileFilter[] filters = MetalFileChooserUI.this.getFileChooser().getChoosableFileFilters();

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
            MetalFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)var1);
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public Object getSelectedItem() {
         FileFilter var1 = MetalFileChooserUI.this.getFileChooser().getFileFilter();
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
               MetalFileChooserUI.this.getFileChooser().addChoosableFileFilter(var1);
            }
         }

         return MetalFileChooserUI.this.getFileChooser().getFileFilter();
      }

      public int getSize() {
         return this.filters != null ? this.filters.length : 0;
      }

      public Object getElementAt(int var1) {
         if (var1 > this.getSize() - 1) {
            return MetalFileChooserUI.this.getFileChooser().getFileFilter();
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

   protected class DirectoryComboBoxModel extends AbstractListModel<Object> implements ComboBoxModel<Object> {
      Vector<File> directories = new Vector();
      int[] depths = null;
      File selectedDirectory = null;
      JFileChooser chooser = MetalFileChooserUI.this.getFileChooser();
      FileSystemView fsv;

      public DirectoryComboBoxModel() {
         this.fsv = this.chooser.getFileSystemView();
         File var2 = MetalFileChooserUI.this.getFileChooser().getCurrentDirectory();
         if (var2 != null) {
            this.addItem(var2);
         }

      }

      private void addItem(File var1) {
         if (var1 != null) {
            boolean var2 = FilePane.usesShellFolder(this.chooser);
            this.directories.clear();
            File[] var3 = var2 ? (File[])((File[])ShellFolder.get("fileChooserComboBoxFolders")) : this.fsv.getRoots();
            this.directories.addAll(Arrays.asList(var3));

            File var4;
            try {
               var4 = ShellFolder.getNormalizedFile(var1);
            } catch (IOException var12) {
               var4 = var1;
            }

            try {
               Object var5 = var2 ? ShellFolder.getShellFolder(var4) : var4;
               Object var6 = var5;
               Vector var7 = new Vector(10);

               do {
                  var7.addElement(var6);
               } while((var6 = ((File)var6).getParentFile()) != null);

               int var8 = var7.size();

               label48:
               for(int var9 = 0; var9 < var8; ++var9) {
                  File var14 = (File)var7.get(var9);
                  if (this.directories.contains(var14)) {
                     int var10 = this.directories.indexOf(var14);
                     int var11 = var9 - 1;

                     while(true) {
                        if (var11 < 0) {
                           break label48;
                        }

                        this.directories.insertElementAt(var7.get(var11), var10 + var9 - var11);
                        --var11;
                     }
                  }
               }

               this.calculateDepths();
               this.setSelectedItem(var5);
            } catch (FileNotFoundException var13) {
               this.calculateDepths();
            }

         }
      }

      private void calculateDepths() {
         this.depths = new int[this.directories.size()];

         for(int var1 = 0; var1 < this.depths.length; ++var1) {
            File var2 = (File)this.directories.get(var1);
            File var3 = var2.getParentFile();
            this.depths[var1] = 0;
            if (var3 != null) {
               for(int var4 = var1 - 1; var4 >= 0; --var4) {
                  if (var3.equals(this.directories.get(var4))) {
                     this.depths[var1] = this.depths[var4] + 1;
                     break;
                  }
               }
            }
         }

      }

      public int getDepth(int var1) {
         return this.depths != null && var1 >= 0 && var1 < this.depths.length ? this.depths[var1] : 0;
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

   class IndentIcon implements Icon {
      Icon icon = null;
      int depth = 0;

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         if (var1.getComponentOrientation().isLeftToRight()) {
            this.icon.paintIcon(var1, var2, var3 + this.depth * 10, var4);
         } else {
            this.icon.paintIcon(var1, var2, var3, var4);
         }

      }

      public int getIconWidth() {
         return this.icon.getIconWidth() + this.depth * 10;
      }

      public int getIconHeight() {
         return this.icon.getIconHeight();
      }
   }

   class DirectoryComboBoxRenderer extends DefaultListCellRenderer {
      MetalFileChooserUI.IndentIcon ii = MetalFileChooserUI.this.new IndentIcon();

      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var2 == null) {
            this.setText("");
            return this;
         } else {
            File var6 = (File)var2;
            this.setText(MetalFileChooserUI.this.getFileChooser().getName(var6));
            Icon var7 = MetalFileChooserUI.this.getFileChooser().getIcon(var6);
            this.ii.icon = var7;
            this.ii.depth = MetalFileChooserUI.this.directoryComboBoxModel.getDepth(var3);
            this.setIcon(this.ii);
            return this;
         }
      }
   }

   protected class FileRenderer extends DefaultListCellRenderer {
   }

   protected class SingleClickListener extends MouseAdapter {
      public SingleClickListener(JList var2) {
      }
   }

   private class MetalFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
      private MetalFileChooserUIAccessor() {
      }

      public JFileChooser getFileChooser() {
         return MetalFileChooserUI.this.getFileChooser();
      }

      public BasicDirectoryModel getModel() {
         return MetalFileChooserUI.this.getModel();
      }

      public JPanel createList() {
         return MetalFileChooserUI.this.createList(this.getFileChooser());
      }

      public JPanel createDetailsView() {
         return MetalFileChooserUI.this.createDetailsView(this.getFileChooser());
      }

      public boolean isDirectorySelected() {
         return MetalFileChooserUI.this.isDirectorySelected();
      }

      public File getDirectory() {
         return MetalFileChooserUI.this.getDirectory();
      }

      public Action getChangeToParentDirectoryAction() {
         return MetalFileChooserUI.this.getChangeToParentDirectoryAction();
      }

      public Action getApproveSelectionAction() {
         return MetalFileChooserUI.this.getApproveSelectionAction();
      }

      public Action getNewFolderAction() {
         return MetalFileChooserUI.this.getNewFolderAction();
      }

      public MouseListener createDoubleClickListener(JList var1) {
         return MetalFileChooserUI.this.createDoubleClickListener(this.getFileChooser(), var1);
      }

      public ListSelectionListener createListSelectionListener() {
         return MetalFileChooserUI.this.createListSelectionListener(this.getFileChooser());
      }

      // $FF: synthetic method
      MetalFileChooserUIAccessor(Object var2) {
         this();
      }
   }
}
