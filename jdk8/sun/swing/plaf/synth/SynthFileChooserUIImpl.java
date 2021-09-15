package sun.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import sun.awt.shell.ShellFolder;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;

public class SynthFileChooserUIImpl extends SynthFileChooserUI {
   private JLabel lookInLabel;
   private JComboBox<File> directoryComboBox;
   private SynthFileChooserUIImpl.DirectoryComboBoxModel directoryComboBoxModel;
   private Action directoryComboBoxAction = new SynthFileChooserUIImpl.DirectoryComboBoxAction();
   private SynthFileChooserUIImpl.FilterComboBoxModel filterComboBoxModel;
   private JTextField fileNameTextField;
   private FilePane filePane;
   private JToggleButton listViewButton;
   private JToggleButton detailsViewButton;
   private boolean readOnly;
   private JPanel buttonPanel;
   private JPanel bottomPanel;
   private JComboBox<FileFilter> filterComboBox;
   private static final Dimension hstrut5 = new Dimension(5, 1);
   private static final Insets shrinkwrap = new Insets(0, 0, 0, 0);
   private static Dimension LIST_PREF_SIZE = new Dimension(405, 135);
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
   private SynthFileChooserUIImpl.AlignedLabel fileNameLabel;
   private final PropertyChangeListener modeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent var1) {
         if (SynthFileChooserUIImpl.this.fileNameLabel != null) {
            SynthFileChooserUIImpl.this.populateFileNameLabel();
         }

      }
   };
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

   public SynthFileChooserUIImpl(JFileChooser var1) {
      super(var1);
   }

   protected void installDefaults(JFileChooser var1) {
      super.installDefaults(var1);
      this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
   }

   public void installComponents(JFileChooser var1) {
      super.installComponents(var1);
      this.getContext(var1, 1);
      var1.setLayout(new BorderLayout(0, 11));
      JPanel var3 = new JPanel(new BorderLayout(11, 0));
      JPanel var4 = new JPanel();
      var4.setLayout(new BoxLayout(var4, 2));
      var3.add(var4, "After");
      var1.add(var3, "North");
      this.lookInLabel = new JLabel(this.lookInLabelText);
      this.lookInLabel.setDisplayedMnemonic(this.lookInLabelMnemonic);
      var3.add(this.lookInLabel, "Before");
      this.directoryComboBox = new JComboBox();
      this.directoryComboBox.getAccessibleContext().setAccessibleDescription(this.lookInLabelText);
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
      this.filePane = new FilePane(new SynthFileChooserUIImpl.SynthFileChooserUIAccessor());
      var1.addPropertyChangeListener(this.filePane);
      JPopupMenu var5 = this.filePane.getComponentPopupMenu();
      if (var5 != null) {
         var5.insert((Action)this.getChangeToParentDirectoryAction(), 0);
         if (File.separatorChar == '/') {
            var5.insert((Action)this.getGoHomeAction(), 1);
         }
      }

      FileSystemView var6 = var1.getFileSystemView();
      JButton var7 = new JButton(this.getChangeToParentDirectoryAction());
      var7.setText((String)null);
      var7.setIcon(this.upFolderIcon);
      var7.setToolTipText(this.upFolderToolTipText);
      var7.getAccessibleContext().setAccessibleName(this.upFolderAccessibleName);
      var7.setAlignmentX(0.0F);
      var7.setAlignmentY(0.5F);
      var7.setMargin(shrinkwrap);
      var4.add(var7);
      var4.add(Box.createRigidArea(hstrut5));
      File var8 = var6.getHomeDirectory();
      String var9 = this.homeFolderToolTipText;
      JButton var10 = new JButton(this.homeFolderIcon);
      var10.setToolTipText(var9);
      var10.getAccessibleContext().setAccessibleName(this.homeFolderAccessibleName);
      var10.setAlignmentX(0.0F);
      var10.setAlignmentY(0.5F);
      var10.setMargin(shrinkwrap);
      var10.addActionListener(this.getGoHomeAction());
      var4.add(var10);
      var4.add(Box.createRigidArea(hstrut5));
      if (!this.readOnly) {
         var10 = new JButton(this.filePane.getNewFolderAction());
         var10.setText((String)null);
         var10.setIcon(this.newFolderIcon);
         var10.setToolTipText(this.newFolderToolTipText);
         var10.getAccessibleContext().setAccessibleName(this.newFolderAccessibleName);
         var10.setAlignmentX(0.0F);
         var10.setAlignmentY(0.5F);
         var10.setMargin(shrinkwrap);
         var4.add(var10);
         var4.add(Box.createRigidArea(hstrut5));
      }

      ButtonGroup var11 = new ButtonGroup();
      this.listViewButton = new JToggleButton(this.listViewIcon);
      this.listViewButton.setToolTipText(this.listViewButtonToolTipText);
      this.listViewButton.getAccessibleContext().setAccessibleName(this.listViewButtonAccessibleName);
      this.listViewButton.setSelected(true);
      this.listViewButton.setAlignmentX(0.0F);
      this.listViewButton.setAlignmentY(0.5F);
      this.listViewButton.setMargin(shrinkwrap);
      this.listViewButton.addActionListener(this.filePane.getViewTypeAction(0));
      var4.add(this.listViewButton);
      var11.add(this.listViewButton);
      this.detailsViewButton = new JToggleButton(this.detailsViewIcon);
      this.detailsViewButton.setToolTipText(this.detailsViewButtonToolTipText);
      this.detailsViewButton.getAccessibleContext().setAccessibleName(this.detailsViewButtonAccessibleName);
      this.detailsViewButton.setAlignmentX(0.0F);
      this.detailsViewButton.setAlignmentY(0.5F);
      this.detailsViewButton.setMargin(shrinkwrap);
      this.detailsViewButton.addActionListener(this.filePane.getViewTypeAction(1));
      var4.add(this.detailsViewButton);
      var11.add(this.detailsViewButton);
      this.filePane.addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            if ("viewType".equals(var1.getPropertyName())) {
               int var2 = SynthFileChooserUIImpl.this.filePane.getViewType();
               switch(var2) {
               case 0:
                  SynthFileChooserUIImpl.this.listViewButton.setSelected(true);
                  break;
               case 1:
                  SynthFileChooserUIImpl.this.detailsViewButton.setSelected(true);
               }
            }

         }
      });
      var1.add(this.getAccessoryPanel(), "After");
      JComponent var12 = var1.getAccessory();
      if (var12 != null) {
         this.getAccessoryPanel().add(var12);
      }

      this.filePane.setPreferredSize(LIST_PREF_SIZE);
      var1.add(this.filePane, "Center");
      this.bottomPanel = new JPanel();
      this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, 1));
      var1.add(this.bottomPanel, "South");
      JPanel var13 = new JPanel();
      var13.setLayout(new BoxLayout(var13, 2));
      this.bottomPanel.add(var13);
      this.bottomPanel.add(Box.createRigidArea(new Dimension(1, 5)));
      this.fileNameLabel = new SynthFileChooserUIImpl.AlignedLabel();
      this.populateFileNameLabel();
      var13.add(this.fileNameLabel);
      this.fileNameTextField = new JTextField(35) {
         public Dimension getMaximumSize() {
            return new Dimension(32767, super.getPreferredSize().height);
         }
      };
      var13.add(this.fileNameTextField);
      this.fileNameLabel.setLabelFor(this.fileNameTextField);
      this.fileNameTextField.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
            if (!SynthFileChooserUIImpl.this.getFileChooser().isMultiSelectionEnabled()) {
               SynthFileChooserUIImpl.this.filePane.clearSelection();
            }

         }
      });
      if (var1.isMultiSelectionEnabled()) {
         this.setFileName(this.fileNameString(var1.getSelectedFiles()));
      } else {
         this.setFileName(this.fileNameString(var1.getSelectedFile()));
      }

      JPanel var14 = new JPanel();
      var14.setLayout(new BoxLayout(var14, 2));
      this.bottomPanel.add(var14);
      SynthFileChooserUIImpl.AlignedLabel var15 = new SynthFileChooserUIImpl.AlignedLabel(this.filesOfTypeLabelText);
      var15.setDisplayedMnemonic(this.filesOfTypeLabelMnemonic);
      var14.add(var15);
      this.filterComboBoxModel = this.createFilterComboBoxModel();
      var1.addPropertyChangeListener(this.filterComboBoxModel);
      this.filterComboBox = new JComboBox(this.filterComboBoxModel);
      this.filterComboBox.getAccessibleContext().setAccessibleDescription(this.filesOfTypeLabelText);
      var15.setLabelFor(this.filterComboBox);
      this.filterComboBox.setRenderer(this.createFilterComboBoxRenderer());
      var14.add(this.filterComboBox);
      this.buttonPanel = new JPanel();
      this.buttonPanel.setLayout(new SynthFileChooserUIImpl.ButtonAreaLayout());
      this.buttonPanel.add(this.getApproveButton(var1));
      this.buttonPanel.add(this.getCancelButton(var1));
      if (var1.getControlButtonsAreShown()) {
         this.addControlButtons();
      }

      groupLabels(new SynthFileChooserUIImpl.AlignedLabel[]{this.fileNameLabel, var15});
   }

   protected void installListeners(JFileChooser var1) {
      super.installListeners(var1);
      var1.addPropertyChangeListener("fileSelectionChanged", this.modeListener);
   }

   protected void uninstallListeners(JFileChooser var1) {
      var1.removePropertyChangeListener("fileSelectionChanged", this.modeListener);
      super.uninstallListeners(var1);
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

   public void uninstallUI(JComponent var1) {
      var1.removePropertyChangeListener(this.filterComboBoxModel);
      var1.removePropertyChangeListener(this.filePane);
      if (this.filePane != null) {
         this.filePane.uninstallUI();
         this.filePane = null;
      }

      super.uninstallUI(var1);
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

   private int getMnemonic(String var1, Locale var2) {
      return SwingUtilities2.getUIDefaultsInt(var1, var2);
   }

   public String getFileName() {
      return this.fileNameTextField != null ? this.fileNameTextField.getText() : null;
   }

   public void setFileName(String var1) {
      if (this.fileNameTextField != null) {
         this.fileNameTextField.setText(var1);
      }

   }

   public void rescanCurrentDirectory(JFileChooser var1) {
      this.filePane.rescanCurrentDirectory();
   }

   protected void doSelectedFileChanged(PropertyChangeEvent var1) {
      super.doSelectedFileChanged(var1);
      File var2 = (File)var1.getNewValue();
      JFileChooser var3 = this.getFileChooser();
      if (var2 != null && (var3.isFileSelectionEnabled() && !var2.isDirectory() || var2.isDirectory() && var3.isDirectorySelectionEnabled())) {
         this.setFileName(this.fileNameString(var2));
      }

   }

   protected void doSelectedFilesChanged(PropertyChangeEvent var1) {
      super.doSelectedFilesChanged(var1);
      File[] var2 = (File[])((File[])var1.getNewValue());
      JFileChooser var3 = this.getFileChooser();
      if (var2 != null && var2.length > 0 && (var2.length > 1 || var3.isDirectorySelectionEnabled() || !var2[0].isDirectory())) {
         this.setFileName(this.fileNameString(var2));
      }

   }

   protected void doDirectoryChanged(PropertyChangeEvent var1) {
      super.doDirectoryChanged(var1);
      JFileChooser var2 = this.getFileChooser();
      FileSystemView var3 = var2.getFileSystemView();
      File var4 = var2.getCurrentDirectory();
      if (!this.readOnly && var4 != null) {
         this.getNewFolderAction().setEnabled(this.filePane.canWrite(var4));
      }

      if (var4 != null) {
         JComponent var5 = this.getDirectoryComboBox();
         if (var5 instanceof JComboBox) {
            ComboBoxModel var6 = ((JComboBox)var5).getModel();
            if (var6 instanceof SynthFileChooserUIImpl.DirectoryComboBoxModel) {
               ((SynthFileChooserUIImpl.DirectoryComboBoxModel)var6).addItem(var4);
            }
         }

         if (var2.isDirectorySelectionEnabled() && !var2.isFileSelectionEnabled()) {
            if (var3.isFileSystem(var4)) {
               this.setFileName(var4.getPath());
            } else {
               this.setFileName((String)null);
            }
         }
      }

   }

   protected void doFileSelectionModeChanged(PropertyChangeEvent var1) {
      super.doFileSelectionModeChanged(var1);
      JFileChooser var2 = this.getFileChooser();
      File var3 = var2.getCurrentDirectory();
      if (var3 != null && var2.isDirectorySelectionEnabled() && !var2.isFileSelectionEnabled() && var2.getFileSystemView().isFileSystem(var3)) {
         this.setFileName(var3.getPath());
      } else {
         this.setFileName((String)null);
      }

   }

   protected void doAccessoryChanged(PropertyChangeEvent var1) {
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

   protected void doControlButtonsChanged(PropertyChangeEvent var1) {
      super.doControlButtonsChanged(var1);
      if (this.getFileChooser().getControlButtonsAreShown()) {
         this.addControlButtons();
      } else {
         this.removeControlButtons();
      }

   }

   protected void addControlButtons() {
      if (this.bottomPanel != null) {
         this.bottomPanel.add(this.buttonPanel);
      }

   }

   protected void removeControlButtons() {
      if (this.bottomPanel != null) {
         this.bottomPanel.remove(this.buttonPanel);
      }

   }

   protected ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      FilePane.addActionsToMap(var1, this.filePane.getActions());
      var1.put("fileNameCompletion", this.getFileNameCompletionAction());
      return var1;
   }

   protected JComponent getDirectoryComboBox() {
      return this.directoryComboBox;
   }

   protected Action getDirectoryComboBoxAction() {
      return this.directoryComboBoxAction;
   }

   protected SynthFileChooserUIImpl.DirectoryComboBoxRenderer createDirectoryComboBoxRenderer(JFileChooser var1) {
      return new SynthFileChooserUIImpl.DirectoryComboBoxRenderer(this.directoryComboBox.getRenderer());
   }

   protected SynthFileChooserUIImpl.DirectoryComboBoxModel createDirectoryComboBoxModel(JFileChooser var1) {
      return new SynthFileChooserUIImpl.DirectoryComboBoxModel();
   }

   protected SynthFileChooserUIImpl.FilterComboBoxRenderer createFilterComboBoxRenderer() {
      return new SynthFileChooserUIImpl.FilterComboBoxRenderer(this.filterComboBox.getRenderer());
   }

   protected SynthFileChooserUIImpl.FilterComboBoxModel createFilterComboBoxModel() {
      return new SynthFileChooserUIImpl.FilterComboBoxModel();
   }

   private static void groupLabels(SynthFileChooserUIImpl.AlignedLabel[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1].group = var0;
      }

   }

   private class AlignedLabel extends JLabel {
      private SynthFileChooserUIImpl.AlignedLabel[] group;
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

   protected class FilterComboBoxModel extends AbstractListModel<FileFilter> implements ComboBoxModel<FileFilter>, PropertyChangeListener {
      protected FileFilter[] filters = SynthFileChooserUIImpl.this.getFileChooser().getChoosableFileFilters();

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
            SynthFileChooserUIImpl.this.getFileChooser().setFileFilter((FileFilter)var1);
            this.fireContentsChanged(this, -1, -1);
         }

      }

      public Object getSelectedItem() {
         FileFilter var1 = SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
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
               SynthFileChooserUIImpl.this.getFileChooser().addChoosableFileFilter(var1);
            }
         }

         return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
      }

      public int getSize() {
         return this.filters != null ? this.filters.length : 0;
      }

      public FileFilter getElementAt(int var1) {
         if (var1 > this.getSize() - 1) {
            return SynthFileChooserUIImpl.this.getFileChooser().getFileFilter();
         } else {
            return this.filters != null ? this.filters[var1] : null;
         }
      }
   }

   public class FilterComboBoxRenderer implements ListCellRenderer<FileFilter> {
      private ListCellRenderer<? super FileFilter> delegate;

      private FilterComboBoxRenderer(ListCellRenderer<? super FileFilter> var2) {
         this.delegate = var2;
      }

      public Component getListCellRendererComponent(JList<? extends FileFilter> var1, FileFilter var2, int var3, boolean var4, boolean var5) {
         Component var6 = this.delegate.getListCellRendererComponent(var1, var2, var3, var4, var5);
         String var7 = null;
         if (var2 != null) {
            var7 = var2.getDescription();
         }

         assert var6 instanceof JLabel;

         if (var7 != null) {
            ((JLabel)var6).setText(var7);
         }

         return var6;
      }

      // $FF: synthetic method
      FilterComboBoxRenderer(ListCellRenderer var2, Object var3) {
         this(var2);
      }
   }

   protected class DirectoryComboBoxAction extends AbstractAction {
      protected DirectoryComboBoxAction() {
         super("DirectoryComboBoxAction");
      }

      public void actionPerformed(ActionEvent var1) {
         SynthFileChooserUIImpl.this.directoryComboBox.hidePopup();
         JComponent var2 = SynthFileChooserUIImpl.this.getDirectoryComboBox();
         if (var2 instanceof JComboBox) {
            File var3 = (File)((JComboBox)var2).getSelectedItem();
            SynthFileChooserUIImpl.this.getFileChooser().setCurrentDirectory(var3);
         }

      }
   }

   protected class DirectoryComboBoxModel extends AbstractListModel<File> implements ComboBoxModel<File> {
      Vector<File> directories = new Vector();
      int[] depths = null;
      File selectedDirectory = null;
      JFileChooser chooser = SynthFileChooserUIImpl.this.getFileChooser();
      FileSystemView fsv;

      public DirectoryComboBoxModel() {
         this.fsv = this.chooser.getFileSystemView();
         File var2 = SynthFileChooserUIImpl.this.getFileChooser().getCurrentDirectory();
         if (var2 != null) {
            this.addItem(var2);
         }

      }

      public void addItem(File var1) {
         if (var1 != null) {
            boolean var2 = FilePane.usesShellFolder(this.chooser);
            int var3 = this.directories.size();
            this.directories.clear();
            if (var3 > 0) {
               this.fireIntervalRemoved(this, 0, var3);
            }

            File[] var4 = var2 ? (File[])((File[])ShellFolder.get("fileChooserComboBoxFolders")) : this.fsv.getRoots();
            this.directories.addAll(Arrays.asList(var4));

            File var5;
            try {
               var5 = ShellFolder.getNormalizedFile(var1);
            } catch (IOException var13) {
               var5 = var1;
            }

            try {
               Object var6 = var2 ? ShellFolder.getShellFolder(var5) : var5;
               Object var7 = var6;
               Vector var8 = new Vector(10);

               do {
                  var8.addElement(var7);
               } while((var7 = ((File)var7).getParentFile()) != null);

               int var9 = var8.size();

               label51:
               for(int var10 = 0; var10 < var9; ++var10) {
                  File var15 = (File)var8.get(var10);
                  if (this.directories.contains(var15)) {
                     int var11 = this.directories.indexOf(var15);
                     int var12 = var10 - 1;

                     while(true) {
                        if (var12 < 0) {
                           break label51;
                        }

                        this.directories.insertElementAt(var8.get(var12), var11 + var10 - var12);
                        --var12;
                     }
                  }
               }

               this.calculateDepths();
               this.setSelectedItem(var6);
            } catch (FileNotFoundException var14) {
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
         if (this.icon != null) {
            if (var1.getComponentOrientation().isLeftToRight()) {
               this.icon.paintIcon(var1, var2, var3 + this.depth * 10, var4);
            } else {
               this.icon.paintIcon(var1, var2, var3, var4);
            }
         }

      }

      public int getIconWidth() {
         return (this.icon != null ? this.icon.getIconWidth() : 0) + this.depth * 10;
      }

      public int getIconHeight() {
         return this.icon != null ? this.icon.getIconHeight() : 0;
      }
   }

   private class DirectoryComboBoxRenderer implements ListCellRenderer<File> {
      private ListCellRenderer<? super File> delegate;
      SynthFileChooserUIImpl.IndentIcon ii;

      private DirectoryComboBoxRenderer(ListCellRenderer<? super File> var2) {
         this.ii = SynthFileChooserUIImpl.this.new IndentIcon();
         this.delegate = var2;
      }

      public Component getListCellRendererComponent(JList<? extends File> var1, File var2, int var3, boolean var4, boolean var5) {
         Component var6 = this.delegate.getListCellRendererComponent(var1, var2, var3, var4, var5);

         assert var6 instanceof JLabel;

         JLabel var7 = (JLabel)var6;
         if (var2 == null) {
            var7.setText("");
            return var7;
         } else {
            var7.setText(SynthFileChooserUIImpl.this.getFileChooser().getName(var2));
            Icon var8 = SynthFileChooserUIImpl.this.getFileChooser().getIcon(var2);
            this.ii.icon = var8;
            this.ii.depth = SynthFileChooserUIImpl.this.directoryComboBoxModel.getDepth(var3);
            var7.setIcon(this.ii);
            return var7;
         }
      }

      // $FF: synthetic method
      DirectoryComboBoxRenderer(ListCellRenderer var2, Object var3) {
         this(var2);
      }
   }

   private class SynthFileChooserUIAccessor implements FilePane.FileChooserUIAccessor {
      private SynthFileChooserUIAccessor() {
      }

      public JFileChooser getFileChooser() {
         return SynthFileChooserUIImpl.this.getFileChooser();
      }

      public BasicDirectoryModel getModel() {
         return SynthFileChooserUIImpl.this.getModel();
      }

      public JPanel createList() {
         return null;
      }

      public JPanel createDetailsView() {
         return null;
      }

      public boolean isDirectorySelected() {
         return SynthFileChooserUIImpl.this.isDirectorySelected();
      }

      public File getDirectory() {
         return SynthFileChooserUIImpl.this.getDirectory();
      }

      public Action getChangeToParentDirectoryAction() {
         return SynthFileChooserUIImpl.this.getChangeToParentDirectoryAction();
      }

      public Action getApproveSelectionAction() {
         return SynthFileChooserUIImpl.this.getApproveSelectionAction();
      }

      public Action getNewFolderAction() {
         return SynthFileChooserUIImpl.this.getNewFolderAction();
      }

      public MouseListener createDoubleClickListener(JList var1) {
         return SynthFileChooserUIImpl.this.createDoubleClickListener(this.getFileChooser(), var1);
      }

      public ListSelectionListener createListSelectionListener() {
         return SynthFileChooserUIImpl.this.createListSelectionListener(this.getFileChooser());
      }

      // $FF: synthetic method
      SynthFileChooserUIAccessor(Object var2) {
         this();
      }
   }
}
