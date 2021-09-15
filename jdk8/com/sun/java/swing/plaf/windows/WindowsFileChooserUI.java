package com.sun.java.swing.plaf.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;
import sun.swing.WindowsPlacesBar;

public class WindowsFileChooserUI extends BasicFileChooserUI {
   private JPanel centerPanel;
   private JLabel lookInLabel;
   private JComboBox<File> directoryComboBox;
   private WindowsFileChooserUI.DirectoryComboBoxModel directoryComboBoxModel;
   private ActionListener directoryComboBoxAction = new WindowsFileChooserUI.DirectoryComboBoxAction();
   private WindowsFileChooserUI.FilterComboBoxModel filterComboBoxModel;
   private JTextField filenameTextField;
   private FilePane filePane;
   private WindowsPlacesBar placesBar;
   private JButton approveButton;
   private JButton cancelButton;
   private JPanel buttonPanel;
   private JPanel bottomPanel;
   private JComboBox<FileFilter> filterComboBox;
   private static final Dimension hstrut10 = new Dimension(10, 1);
   private static final Dimension vstrut4 = new Dimension(1, 4);
   private static final Dimension vstrut6 = new Dimension(1, 6);
   private static final Dimension vstrut8 = new Dimension(1, 8);
   private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
   private static int PREF_WIDTH = 425;
   private static int PREF_HEIGHT = 245;
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
   private String newFolderToolTipText = null;
   private String newFolderAccessibleName = null;
   private String viewMenuButtonToolTipText = null;
   private String viewMenuButtonAccessibleName = null;
   private BasicFileChooserUI.BasicFileView fileView = new WindowsFileChooserUI.WindowsFileView();
   private JLabel fileNameLabel;
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
      return new WindowsFileChooserUI((JFileChooser)var0);
   }

   public WindowsFileChooserUI(JFileChooser var1) {
      super(var1);
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
   }

   public void uninstallComponents(JFileChooser var1) {
      var1.removeAll();
   }

   public void installComponents(JFileChooser var1) {
      this.filePane = new FilePane(new WindowsFileChooserUI.WindowsFileChooserUIAccessor());
      var1.addPropertyChangeListener(this.filePane);
      FileSystemView var2 = var1.getFileSystemView();
      var1.setBorder(new EmptyBorder(4, 10, 10, 10));
      var1.setLayout(new BorderLayout(8, 8));
      this.updateUseShellFolder();
      JToolBar var3 = new JToolBar();
      var3.setFloatable(false);
      var3.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
      var1.add(var3, "North");
      this.lookInLabel = new JLabel(this.lookInLabelText, 11) {
         public Dimension getPreferredSize() {
            return this.getMinimumSize();
         }

         public Dimension getMinimumSize() {
            Dimension var1 = super.getPreferredSize();
            if (WindowsFileChooserUI.this.placesBar != null) {
               var1.width = Math.max(var1.width, WindowsFileChooserUI.this.placesBar.getWidth());
            }

            return var1;
         }
      };
      this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
      this.lookInLabel.setAlignmentX(0.0F);
      this.lookInLabel.setAlignmentY(0.5F);
      var3.add(this.lookInLabel);
      var3.add(Box.createRigidArea(new Dimension(8, 0)));
      this.directoryComboBox = new JComboBox<File>() {
         public Dimension getMinimumSize() {
            Dimension var1 = super.getMinimumSize();
            var1.width = 60;
            return var1;
         }

         public Dimension getPreferredSize() {
            Dimension var1 = super.getPreferredSize();
            var1.width = 150;
            return var1;
         }
      };
      this.directoryComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
      this.lookInLabel.setLabelFor(this.directoryComboBox);
      this.directoryComboBoxModel = this.createDirectoryComboBoxModel(var1);
      this.directoryComboBox.setModel(this.directoryComboBoxModel);
      this.directoryComboBox.addActionListener(this.directoryComboBoxAction);
      this.directoryComboBox.setRenderer(this.createDirectoryComboBoxRenderer(var1));
      this.directoryComboBox.setAlignmentX(0.0F);
      this.directoryComboBox.setAlignmentY(0.5F);
      this.directoryComboBox.setMaximumRowCount(8);
      var3.add(this.directoryComboBox);
      var3.add(Box.createRigidArea(hstrut10));
      JButton var4 = createToolButton(this.getChangeToParentDirectoryAction(), this.upFolderIcon, this.upFolderToolTipText, this.upFolderAccessibleName);
      var3.add(var4);
      if (!UIManager.getBoolean("FileChooser.readOnly")) {
         JButton var5 = createToolButton(this.filePane.getNewFolderAction(), this.newFolderIcon, this.newFolderToolTipText, this.newFolderAccessibleName);
         var3.add(var5);
      }

      ButtonGroup var19 = new ButtonGroup();
      final JPopupMenu var6 = new JPopupMenu();
      final JRadioButtonMenuItem var7 = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(0));
      var7.setSelected(this.filePane.getViewType() == 0);
      var6.add((JMenuItem)var7);
      var19.add(var7);
      final JRadioButtonMenuItem var8 = new JRadioButtonMenuItem(this.filePane.getViewTypeAction(1));
      var8.setSelected(this.filePane.getViewType() == 1);
      var6.add((JMenuItem)var8);
      var19.add(var8);
      BufferedImage var9 = new BufferedImage(this.viewMenuIcon.getIconWidth() + 7, this.viewMenuIcon.getIconHeight(), 2);
      Graphics var10 = var9.getGraphics();
      this.viewMenuIcon.paintIcon(this.filePane, var10, 0, 0);
      int var11 = var9.getWidth() - 5;
      int var12 = var9.getHeight() / 2 - 1;
      var10.setColor(Color.BLACK);
      var10.fillPolygon(new int[]{var11, var11 + 5, var11 + 2}, new int[]{var12, var12, var12 + 3}, 3);
      final JButton var13 = createToolButton((Action)null, new ImageIcon(var9), this.viewMenuButtonToolTipText, this.viewMenuButtonAccessibleName);
      var13.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent var1) {
            if (SwingUtilities.isLeftMouseButton(var1) && !var13.isSelected()) {
               var13.setSelected(true);
               var6.show(var13, 0, var13.getHeight());
            }

         }
      });
      var13.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent var1) {
            if (var1.getKeyCode() == 32 && var13.getModel().isRollover()) {
               var13.setSelected(true);
               var6.show(var13, 0, var13.getHeight());
            }

         }
      });
      var6.addPopupMenuListener(new PopupMenuListener() {
         public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
         }

         public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  var13.setSelected(false);
               }
            });
         }

         public void popupMenuCanceled(PopupMenuEvent var1) {
         }
      });
      var3.add(var13);
      var3.add(Box.createRigidArea(new Dimension(80, 0)));
      this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            if ("viewType".equals(var1.getPropertyName())) {
               switch(WindowsFileChooserUI.this.filePane.getViewType()) {
               case 0:
                  var7.setSelected(true);
                  break;
               case 1:
                  var8.setSelected(true);
               }
            }

         }
      });
      this.centerPanel = new JPanel(new BorderLayout());
      this.centerPanel.add(this.getAccessoryPanel(), "After");
      JComponent var14 = var1.getAccessory();
      if (var14 != null) {
         this.getAccessoryPanel().add(var14);
      }

      this.filePane.setPreferredSize(LIST_PREF_SIZE);
      this.centerPanel.add(this.filePane, "Center");
      var1.add(this.centerPanel, "Center");
      this.getBottomPanel().setLayout(new BoxLayout(this.getBottomPanel(), 2));
      this.centerPanel.add(this.getBottomPanel(), "South");
      JPanel var15 = new JPanel();
      var15.setLayout(new BoxLayout(var15, 3));
      var15.add(Box.createRigidArea(vstrut4));
      this.fileNameLabel = new JLabel();
      this.populateFileNameLabel();
      this.fileNameLabel.setAlignmentY(0.0F);
      var15.add(this.fileNameLabel);
      var15.add(Box.createRigidArea(new Dimension(1, 12)));
      JLabel var16 = new JLabel(this.filesOfTypeLabelText);
      var16.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
      var15.add(var16);
      this.getBottomPanel().add(var15);
      this.getBottomPanel().add(Box.createRigidArea(new Dimension(15, 0)));
      JPanel var17 = new JPanel();
      var17.add(Box.createRigidArea(vstrut8));
      var17.setLayout(new BoxLayout(var17, 1));
      this.filenameTextField = new JTextField(35) {
         public Dimension getMaximumSize() {
            return new Dimension(32767, super.getPreferredSize().height);
         }
      };
      this.fileNameLabel.setLabelFor(this.filenameTextField);
      this.filenameTextField.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
            if (!WindowsFileChooserUI.this.getFileChooser().isMultiSelectionEnabled()) {
               WindowsFileChooserUI.this.filePane.clearSelection();
            }

         }
      });
      if (var1.isMultiSelectionEnabled()) {
         this.setFileName(this.fileNameString(var1.getSelectedFiles()));
      } else {
         this.setFileName(this.fileNameString(var1.getSelectedFile()));
      }

      var17.add(this.filenameTextField);
      var17.add(Box.createRigidArea(vstrut8));
      this.filterComboBoxModel = this.createFilterComboBoxModel();
      var1.addPropertyChangeListener(this.filterComboBoxModel);
      this.filterComboBox = new JComboBox(this.filterComboBoxModel);
      var16.setLabelFor(this.filterComboBox);
      this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
      var17.add(this.filterComboBox);
      this.getBottomPanel().add(var17);
      this.getBottomPanel().add(Box.createRigidArea(new Dimension(30, 0)));
      this.getButtonPanel().setLayout(new BoxLayout(this.getButtonPanel(), 1));
      this.approveButton = new JButton(this.getApproveButtonText(var1)) {
         public Dimension getMaximumSize() {
            return WindowsFileChooserUI.this.approveButton.getPreferredSize().width > WindowsFileChooserUI.this.cancelButton.getPreferredSize().width ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize();
         }
      };
      Insets var18 = this.approveButton.getMargin();
      InsetsUIResource var20 = new InsetsUIResource(var18.top, var18.left + 5, var18.bottom, var18.right + 5);
      this.approveButton.setMargin(var20);
      this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var1));
      this.approveButton.addActionListener(this.getApproveSelectionAction());
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var1));
      this.getButtonPanel().add(Box.createRigidArea(vstrut6));
      this.getButtonPanel().add(this.approveButton);
      this.getButtonPanel().add(Box.createRigidArea(vstrut4));
      this.cancelButton = new JButton(this.cancelButtonText) {
         public Dimension getMaximumSize() {
            return WindowsFileChooserUI.this.approveButton.getPreferredSize().width > WindowsFileChooserUI.this.cancelButton.getPreferredSize().width ? WindowsFileChooserUI.this.approveButton.getPreferredSize() : WindowsFileChooserUI.this.cancelButton.getPreferredSize();
         }
      };
      this.cancelButton.setMargin(var20);
      this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
      this.cancelButton.addActionListener(this.getCancelSelectionAction());
      this.getButtonPanel().add(this.cancelButton);
      if (var1.getControlButtonsAreShown()) {
         this.addControlButtons();
      }

   }

   private void updateUseShellFolder() {
      JFileChooser var1 = this.getFileChooser();
      if (FilePane.usesShellFolder(var1)) {
         if (this.placesBar == null && !UIManager.getBoolean("FileChooser.noPlacesBar")) {
            this.placesBar = new WindowsPlacesBar(var1, XPStyle.getXP() != null);
            var1.add(this.placesBar, "Before");
            var1.addPropertyChangeListener(this.placesBar);
         }
      } else if (this.placesBar != null) {
         var1.remove(this.placesBar);
         var1.removePropertyChangeListener(this.placesBar);
         this.placesBar = null;
      }

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
      this.newFolderToolTipText = UIManager.getString("FileChooser.newFolderToolTipText", (Locale)var2);
      this.newFolderAccessibleName = UIManager.getString("FileChooser.newFolderAccessibleName", (Locale)var2);
      this.viewMenuButtonToolTipText = UIManager.getString("FileChooser.viewMenuButtonToolTipText", (Locale)var2);
      this.viewMenuButtonAccessibleName = UIManager.getString("FileChooser.viewMenuButtonAccessibleName", (Locale)var2);
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
      if (this.placesBar != null) {
         var1.removePropertyChangeListener(this.placesBar);
      }

      this.cancelButton.removeActionListener(this.getCancelSelectionAction());
      this.approveButton.removeActionListener(this.getApproveSelectionAction());
      this.filenameTextField.removeActionListener(this.getApproveSelectionAction());
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
      this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var2));
   }

   private void doDialogTypeChanged(PropertyChangeEvent var1) {
      JFileChooser var2 = this.getFileChooser();
      this.approveButton.setText(this.getApproveButtonText(var2));
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var2));
      this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var2));
      if (var2.getDialogType() == 1) {
         this.lookInLabel.setText(this.saveInLabelText);
      } else {
         this.lookInLabel.setText(this.lookInLabelText);
      }

   }

   private void doApproveButtonMnemonicChanged(PropertyChangeEvent var1) {
      this.approveButton.setMnemonic(this.getApproveButtonMnemonic(this.getFileChooser()));
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
               WindowsFileChooserUI.this.doSelectedFileChanged(var1);
            } else if (var2.equals("SelectedFilesChangedProperty")) {
               WindowsFileChooserUI.this.doSelectedFilesChanged(var1);
            } else if (var2.equals("directoryChanged")) {
               WindowsFileChooserUI.this.doDirectoryChanged(var1);
            } else if (var2.equals("fileFilterChanged")) {
               WindowsFileChooserUI.this.doFilterChanged(var1);
            } else if (var2.equals("fileSelectionChanged")) {
               WindowsFileChooserUI.this.doFileSelectionModeChanged(var1);
            } else if (var2.equals("AccessoryChangedProperty")) {
               WindowsFileChooserUI.this.doAccessoryChanged(var1);
            } else if (!var2.equals("ApproveButtonTextChangedProperty") && !var2.equals("ApproveButtonToolTipTextChangedProperty")) {
               if (var2.equals("DialogTypeChangedProperty")) {
                  WindowsFileChooserUI.this.doDialogTypeChanged(var1);
               } else if (var2.equals("ApproveButtonMnemonicChangedProperty")) {
                  WindowsFileChooserUI.this.doApproveButtonMnemonicChanged(var1);
               } else if (var2.equals("ControlButtonsAreShownChangedProperty")) {
                  WindowsFileChooserUI.this.doControlButtonsChanged(var1);
               } else if (var2 == "FileChooser.useShellFolder") {
                  WindowsFileChooserUI.this.updateUseShellFolder();
                  WindowsFileChooserUI.this.doDirectoryChanged(var1);
               } else if (var2.equals("componentOrientation")) {
                  ComponentOrientation var3 = (ComponentOrientation)var1.getNewValue();
                  JFileChooser var4 = (JFileChooser)var1.getSource();
                  if (var3 != var1.getOldValue()) {
                     var4.applyComponentOrientation(var3);
                  }
               } else if (var2.equals("ancestor") && var1.getOldValue() == null && var1.getNewValue() != null) {
                  WindowsFileChooserUI.this.filenameTextField.selectAll();
                  WindowsFileChooserUI.this.filenameTextField.requestFocus();
               }
            } else {
               WindowsFileChooserUI.this.doApproveButtonTextChanged(var1);
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
      return this.filenameTextField != null ? this.filenameTextField.getText() : null;
   }

   public void setFileName(String var1) {
      if (this.filenameTextField != null) {
         this.filenameTextField.setText(var1);
      }

   }

   protected void setDirectorySelected(boolean var1) {
      super.setDirectorySelected(var1);
      JFileChooser var2 = this.getFileChooser();
      if (var1) {
         this.approveButton.setText(this.directoryOpenButtonText);
         this.approveButton.setToolTipText(this.directoryOpenButtonToolTipText);
         this.approveButton.setMnemonic(this.directoryOpenButtonMnemonic);
      } else {
         this.approveButton.setText(this.getApproveButtonText(var2));
         this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var2));
         this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var2));
      }

   }

   public String getDirectoryName() {
      return null;
   }

   public void setDirectoryName(String var1) {
   }

   protected WindowsFileChooserUI.DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser var1) {
      return new WindowsFileChooserUI.DirectoryComboBoxRenderer();
   }

   private static JButton createToolButton(Action var0, Icon var1, String var2, String var3) {
      final JButton var4 = new JButton(var0);
      var4.setText((String)null);
      var4.setIcon(var1);
      var4.setToolTipText(var2);
      var4.setRequestFocusEnabled(false);
      var4.putClientProperty("AccessibleName", var3);
      var4.putClientProperty(WindowsLookAndFeel.HI_RES_DISABLED_ICON_CLIENT_KEY, Boolean.TRUE);
      var4.setAlignmentX(0.0F);
      var4.setAlignmentY(0.5F);
      var4.setMargin(shrinkwrap);
      var4.setFocusPainted(false);
      var4.setModel(new DefaultButtonModel() {
         public void setPressed(boolean var1) {
            if (!var1 || this.isRollover()) {
               super.setPressed(var1);
            }

         }

         public void setRollover(boolean var1) {
            if (var1 && !this.isRollover()) {
               Component[] var2 = var4.getParent().getComponents();
               int var3 = var2.length;

               for(int var4x = 0; var4x < var3; ++var4x) {
                  Component var5 = var2[var4x];
                  if (var5 instanceof JButton && var5 != var4) {
                     ((JButton)var5).getModel().setRollover(false);
                  }
               }
            }

            super.setRollover(var1);
         }

         public void setSelected(boolean var1) {
            super.setSelected(var1);
            if (var1) {
               this.stateMask |= 5;
            } else {
               this.stateMask &= -6;
            }

         }
      });
      var4.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
            var4.getModel().setRollover(true);
         }

         public void focusLost(FocusEvent var1) {
            var4.getModel().setRollover(false);
         }
      });
      return var4;
   }

   protected WindowsFileChooserUI.DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser var1) {
      return new WindowsFileChooserUI.DirectoryComboBoxModel();
   }

   protected WindowsFileChooserUI.FilterComboBoxRenderer createFilterComboBoxRenderer() {
      return new WindowsFileChooserUI.FilterComboBoxRenderer();
   }

   protected WindowsFileChooserUI.FilterComboBoxModel createFilterComboBoxModel() {
      return new WindowsFileChooserUI.FilterComboBoxModel();
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

   public FileView getFileView(JFileChooser var1) {
      return this.fileView;
   }

   static {
      PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);
      MIN_WIDTH = 425;
      MIN_HEIGHT = 245;
      LIST_PREF_WIDTH = 444;
      LIST_PREF_HEIGHT = 138;
      LIST_PREF_SIZE = new Dimension(LIST_PREF_WIDTH, LIST_PREF_HEIGHT);
   }

   protected class WindowsFileView extends BasicFileChooserUI.BasicFileView {
      protected WindowsFileView() {
         super();
      }

      public Icon getIcon(File var1) {
         Icon var2 = this.getCachedIcon(var1);
         if (var2 != null) {
            return var2;
         } else {
            if (var1 != null) {
               var2 = WindowsFileChooserUI.this.getFileChooser().getFileSystemView().getSystemIcon(var1);
            }

            if (var2 == null) {
               var2 = super.getIcon(var1);
            }

            this.cacheIcon(var1, var2);
            return var2;
         }
      }
   }

   protected class DirectoryComboBoxAction implements ActionListener {
      public void actionPerformed(ActionEvent var1) {
         File var2 = (File)WindowsFileChooserUI.this.directoryComboBox.getSelectedItem();
         WindowsFileChooserUI.this.getFileChooser().setCurrentDirectory(var2);
      }
   }

   protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener {
      protected FileFilter[] filters = WindowsFileChooserUI.this.getFileChooser().getChoosableFileFilters();

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
            WindowsFileChooserUI.this.getFileChooser().setFileFilter((FileFilter)var1);
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public Object getSelectedItem() {
         FileFilter var1 = WindowsFileChooserUI.this.getFileChooser().getFileFilter();
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
               WindowsFileChooserUI.this.getFileChooser().addChoosableFileFilter(var1);
            }
         }

         return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
      }

      public int getSize() {
         return this.filters != null ? this.filters.length : 0;
      }

      public FileFilter getElementAt(int var1) {
         if (var1 > this.getSize() - 1) {
            return WindowsFileChooserUI.this.getFileChooser().getFileFilter();
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

   protected class DirectoryComboBoxModel extends AbstractListModel<File> implements ComboBoxModel<File> {
      Vector<File> directories = new Vector();
      int[] depths = null;
      File selectedDirectory = null;
      JFileChooser chooser = WindowsFileChooserUI.this.getFileChooser();
      FileSystemView fsv;

      public DirectoryComboBoxModel() {
         this.fsv = this.chooser.getFileSystemView();
         File var2 = WindowsFileChooserUI.this.getFileChooser().getCurrentDirectory();
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
               var4 = var1.getCanonicalFile();
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

      public File getElementAt(int var1) {
         return (File)this.directories.elementAt(var1);
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
      WindowsFileChooserUI.IndentIcon ii = WindowsFileChooserUI.this.new IndentIcon();

      public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
         super.getListCellRendererComponent(var1, var2, var3, var4, var5);
         if (var2 == null) {
            this.setText("");
            return this;
         } else {
            File var6 = (File)var2;
            this.setText(WindowsFileChooserUI.this.getFileChooser().getName(var6));
            Icon var7 = WindowsFileChooserUI.this.getFileChooser().getIcon(var6);
            this.ii.icon = var7;
            this.ii.depth = WindowsFileChooserUI.this.directoryComboBoxModel.getDepth(var3);
            this.setIcon(this.ii);
            return this;
         }
      }
   }

   protected class FileRenderer extends DefaultListCellRenderer {
   }

   protected class SingleClickListener extends MouseAdapter {
   }

   protected class WindowsNewFolderAction extends BasicFileChooserUI.NewFolderAction {
      protected WindowsNewFolderAction() {
         super();
      }
   }

   private class WindowsFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
      private WindowsFileChooserUIAccessor() {
      }

      public JFileChooser getFileChooser() {
         return WindowsFileChooserUI.this.getFileChooser();
      }

      public BasicDirectoryModel getModel() {
         return WindowsFileChooserUI.this.getModel();
      }

      public JPanel createList() {
         return WindowsFileChooserUI.this.createList(this.getFileChooser());
      }

      public JPanel createDetailsView() {
         return WindowsFileChooserUI.this.createDetailsView(this.getFileChooser());
      }

      public boolean isDirectorySelected() {
         return WindowsFileChooserUI.this.isDirectorySelected();
      }

      public File getDirectory() {
         return WindowsFileChooserUI.this.getDirectory();
      }

      public Action getChangeToParentDirectoryAction() {
         return WindowsFileChooserUI.this.getChangeToParentDirectoryAction();
      }

      public Action getApproveSelectionAction() {
         return WindowsFileChooserUI.this.getApproveSelectionAction();
      }

      public Action getNewFolderAction() {
         return WindowsFileChooserUI.this.getNewFolderAction();
      }

      public MouseListener createDoubleClickListener(JList var1) {
         return WindowsFileChooserUI.this.createDoubleClickListener(this.getFileChooser(), var1);
      }

      public ListSelectionListener createListSelectionListener() {
         return WindowsFileChooserUI.this.createListSelectionListener(this.getFileChooser());
      }

      // $FF: synthetic method
      WindowsFileChooserUIAccessor(Object var2) {
         this();
      }
   }
}
