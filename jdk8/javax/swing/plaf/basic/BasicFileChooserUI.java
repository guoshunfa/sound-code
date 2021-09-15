package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.UIResource;
import sun.awt.shell.ShellFolder;
import sun.swing.DefaultLookup;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicFileChooserUI extends FileChooserUI {
   protected Icon directoryIcon = null;
   protected Icon fileIcon = null;
   protected Icon computerIcon = null;
   protected Icon hardDriveIcon = null;
   protected Icon floppyDriveIcon = null;
   protected Icon newFolderIcon = null;
   protected Icon upFolderIcon = null;
   protected Icon homeFolderIcon = null;
   protected Icon listViewIcon = null;
   protected Icon detailsViewIcon = null;
   protected Icon viewMenuIcon = null;
   protected int saveButtonMnemonic = 0;
   protected int openButtonMnemonic = 0;
   protected int cancelButtonMnemonic = 0;
   protected int updateButtonMnemonic = 0;
   protected int helpButtonMnemonic = 0;
   protected int directoryOpenButtonMnemonic = 0;
   protected String saveButtonText = null;
   protected String openButtonText = null;
   protected String cancelButtonText = null;
   protected String updateButtonText = null;
   protected String helpButtonText = null;
   protected String directoryOpenButtonText = null;
   private String openDialogTitleText = null;
   private String saveDialogTitleText = null;
   protected String saveButtonToolTipText = null;
   protected String openButtonToolTipText = null;
   protected String cancelButtonToolTipText = null;
   protected String updateButtonToolTipText = null;
   protected String helpButtonToolTipText = null;
   protected String directoryOpenButtonToolTipText = null;
   private Action approveSelectionAction = new BasicFileChooserUI.ApproveSelectionAction();
   private Action cancelSelectionAction = new BasicFileChooserUI.CancelSelectionAction();
   private Action updateAction = new BasicFileChooserUI.UpdateAction();
   private Action newFolderAction;
   private Action goHomeAction = new BasicFileChooserUI.GoHomeAction();
   private Action changeToParentDirectoryAction = new BasicFileChooserUI.ChangeToParentDirectoryAction();
   private String newFolderErrorSeparator = null;
   private String newFolderErrorText = null;
   private String newFolderParentDoesntExistTitleText = null;
   private String newFolderParentDoesntExistText = null;
   private String fileDescriptionText = null;
   private String directoryDescriptionText = null;
   private JFileChooser filechooser = null;
   private boolean directorySelected = false;
   private File directory = null;
   private PropertyChangeListener propertyChangeListener = null;
   private BasicFileChooserUI.AcceptAllFileFilter acceptAllFileFilter = new BasicFileChooserUI.AcceptAllFileFilter();
   private FileFilter actualFileFilter = null;
   private BasicFileChooserUI.GlobFilter globFilter = null;
   private BasicDirectoryModel model = null;
   private BasicFileChooserUI.BasicFileView fileView = new BasicFileChooserUI.BasicFileView();
   private boolean usesSingleFilePane;
   private boolean readOnly;
   private JPanel accessoryPanel = null;
   private BasicFileChooserUI.Handler handler;
   private static final TransferHandler defaultTransferHandler = new BasicFileChooserUI.FileTransferHandler();

   public static ComponentUI createUI(JComponent var0) {
      return new BasicFileChooserUI((JFileChooser)var0);
   }

   public BasicFileChooserUI(JFileChooser var1) {
   }

   public void installUI(JComponent var1) {
      this.accessoryPanel = new JPanel(new BorderLayout());
      this.filechooser = (JFileChooser)var1;
      this.createModel();
      this.clearIconCache();
      this.installDefaults(this.filechooser);
      this.installComponents(this.filechooser);
      this.installListeners(this.filechooser);
      this.filechooser.applyComponentOrientation(this.filechooser.getComponentOrientation());
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
      this.handler = null;
   }

   public void installComponents(JFileChooser var1) {
   }

   public void uninstallComponents(JFileChooser var1) {
   }

   protected void installListeners(JFileChooser var1) {
      this.propertyChangeListener = this.createPropertyChangeListener(var1);
      if (this.propertyChangeListener != null) {
         var1.addPropertyChangeListener(this.propertyChangeListener);
      }

      var1.addPropertyChangeListener(this.getModel());
      InputMap var2 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(var1, 1, var2);
      ActionMap var3 = this.getActionMap();
      SwingUtilities.replaceUIActionMap(var1, var3);
   }

   InputMap getInputMap(int var1) {
      return var1 == 1 ? (InputMap)DefaultLookup.get(this.getFileChooser(), this, "FileChooser.ancestorInputMap") : null;
   }

   ActionMap getActionMap() {
      return this.createActionMap();
   }

   ActionMap createActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      UIAction var2 = new UIAction("refresh") {
         public void actionPerformed(ActionEvent var1) {
            BasicFileChooserUI.this.getFileChooser().rescanCurrentDirectory();
         }
      };
      var1.put("approveSelection", this.getApproveSelectionAction());
      var1.put("cancelSelection", this.getCancelSelectionAction());
      var1.put("refresh", var2);
      var1.put("Go Up", this.getChangeToParentDirectoryAction());
      return var1;
   }

   protected void uninstallListeners(JFileChooser var1) {
      if (this.propertyChangeListener != null) {
         var1.removePropertyChangeListener(this.propertyChangeListener);
      }

      var1.removePropertyChangeListener(this.getModel());
      SwingUtilities.replaceUIInputMap(var1, 1, (InputMap)null);
      SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
   }

   protected void installDefaults(JFileChooser var1) {
      this.installIcons(var1);
      this.installStrings(var1);
      this.usesSingleFilePane = UIManager.getBoolean("FileChooser.usesSingleFilePane");
      this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
      TransferHandler var2 = var1.getTransferHandler();
      if (var2 == null || var2 instanceof UIResource) {
         var1.setTransferHandler(defaultTransferHandler);
      }

      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
   }

   protected void installIcons(JFileChooser var1) {
      this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
      this.fileIcon = UIManager.getIcon("FileView.fileIcon");
      this.computerIcon = UIManager.getIcon("FileView.computerIcon");
      this.hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
      this.floppyDriveIcon = UIManager.getIcon("FileView.floppyDriveIcon");
      this.newFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
      this.upFolderIcon = UIManager.getIcon("FileChooser.upFolderIcon");
      this.homeFolderIcon = UIManager.getIcon("FileChooser.homeFolderIcon");
      this.detailsViewIcon = UIManager.getIcon("FileChooser.detailsViewIcon");
      this.listViewIcon = UIManager.getIcon("FileChooser.listViewIcon");
      this.viewMenuIcon = UIManager.getIcon("FileChooser.viewMenuIcon");
   }

   protected void installStrings(JFileChooser var1) {
      Locale var2 = var1.getLocale();
      this.newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", (Locale)var2);
      this.newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", (Locale)var2);
      this.newFolderParentDoesntExistTitleText = UIManager.getString("FileChooser.newFolderParentDoesntExistTitleText", (Locale)var2);
      this.newFolderParentDoesntExistText = UIManager.getString("FileChooser.newFolderParentDoesntExistText", (Locale)var2);
      this.fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText", (Locale)var2);
      this.directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText", (Locale)var2);
      this.saveButtonText = UIManager.getString("FileChooser.saveButtonText", (Locale)var2);
      this.openButtonText = UIManager.getString("FileChooser.openButtonText", (Locale)var2);
      this.saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText", (Locale)var2);
      this.openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText", (Locale)var2);
      this.cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", (Locale)var2);
      this.updateButtonText = UIManager.getString("FileChooser.updateButtonText", (Locale)var2);
      this.helpButtonText = UIManager.getString("FileChooser.helpButtonText", (Locale)var2);
      this.directoryOpenButtonText = UIManager.getString("FileChooser.directoryOpenButtonText", (Locale)var2);
      this.saveButtonMnemonic = this.getMnemonic("FileChooser.saveButtonMnemonic", var2);
      this.openButtonMnemonic = this.getMnemonic("FileChooser.openButtonMnemonic", var2);
      this.cancelButtonMnemonic = this.getMnemonic("FileChooser.cancelButtonMnemonic", var2);
      this.updateButtonMnemonic = this.getMnemonic("FileChooser.updateButtonMnemonic", var2);
      this.helpButtonMnemonic = this.getMnemonic("FileChooser.helpButtonMnemonic", var2);
      this.directoryOpenButtonMnemonic = this.getMnemonic("FileChooser.directoryOpenButtonMnemonic", var2);
      this.saveButtonToolTipText = UIManager.getString("FileChooser.saveButtonToolTipText", (Locale)var2);
      this.openButtonToolTipText = UIManager.getString("FileChooser.openButtonToolTipText", (Locale)var2);
      this.cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText", (Locale)var2);
      this.updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText", (Locale)var2);
      this.helpButtonToolTipText = UIManager.getString("FileChooser.helpButtonToolTipText", (Locale)var2);
      this.directoryOpenButtonToolTipText = UIManager.getString("FileChooser.directoryOpenButtonToolTipText", (Locale)var2);
   }

   protected void uninstallDefaults(JFileChooser var1) {
      this.uninstallIcons(var1);
      this.uninstallStrings(var1);
      if (var1.getTransferHandler() instanceof UIResource) {
         var1.setTransferHandler((TransferHandler)null);
      }

   }

   protected void uninstallIcons(JFileChooser var1) {
      this.directoryIcon = null;
      this.fileIcon = null;
      this.computerIcon = null;
      this.hardDriveIcon = null;
      this.floppyDriveIcon = null;
      this.newFolderIcon = null;
      this.upFolderIcon = null;
      this.homeFolderIcon = null;
      this.detailsViewIcon = null;
      this.listViewIcon = null;
      this.viewMenuIcon = null;
   }

   protected void uninstallStrings(JFileChooser var1) {
      this.saveButtonText = null;
      this.openButtonText = null;
      this.cancelButtonText = null;
      this.updateButtonText = null;
      this.helpButtonText = null;
      this.directoryOpenButtonText = null;
      this.saveButtonToolTipText = null;
      this.openButtonToolTipText = null;
      this.cancelButtonToolTipText = null;
      this.updateButtonToolTipText = null;
      this.helpButtonToolTipText = null;
      this.directoryOpenButtonToolTipText = null;
   }

   protected void createModel() {
      if (this.model != null) {
         this.model.invalidateFileCache();
      }

      this.model = new BasicDirectoryModel(this.getFileChooser());
   }

   public BasicDirectoryModel getModel() {
      return this.model;
   }

   public PropertyChangeListener createPropertyChangeListener(JFileChooser var1) {
      return null;
   }

   public String getFileName() {
      return null;
   }

   public String getDirectoryName() {
      return null;
   }

   public void setFileName(String var1) {
   }

   public void setDirectoryName(String var1) {
   }

   public void rescanCurrentDirectory(JFileChooser var1) {
   }

   public void ensureFileIsVisible(JFileChooser var1, File var2) {
   }

   public JFileChooser getFileChooser() {
      return this.filechooser;
   }

   public JPanel getAccessoryPanel() {
      return this.accessoryPanel;
   }

   protected JButton getApproveButton(JFileChooser var1) {
      return null;
   }

   public JButton getDefaultButton(JFileChooser var1) {
      return this.getApproveButton(var1);
   }

   public String getApproveButtonToolTipText(JFileChooser var1) {
      String var2 = var1.getApproveButtonToolTipText();
      if (var2 != null) {
         return var2;
      } else if (var1.getDialogType() == 0) {
         return this.openButtonToolTipText;
      } else {
         return var1.getDialogType() == 1 ? this.saveButtonToolTipText : null;
      }
   }

   public void clearIconCache() {
      this.fileView.clearIconCache();
   }

   private BasicFileChooserUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicFileChooserUI.Handler();
      }

      return this.handler;
   }

   protected MouseListener createDoubleClickListener(JFileChooser var1, JList var2) {
      return new BasicFileChooserUI.Handler(var2);
   }

   public ListSelectionListener createListSelectionListener(JFileChooser var1) {
      return this.getHandler();
   }

   protected boolean isDirectorySelected() {
      return this.directorySelected;
   }

   protected void setDirectorySelected(boolean var1) {
      this.directorySelected = var1;
   }

   protected File getDirectory() {
      return this.directory;
   }

   protected void setDirectory(File var1) {
      this.directory = var1;
   }

   private int getMnemonic(String var1, Locale var2) {
      return SwingUtilities2.getUIDefaultsInt(var1, var2);
   }

   public FileFilter getAcceptAllFileFilter(JFileChooser var1) {
      return this.acceptAllFileFilter;
   }

   public FileView getFileView(JFileChooser var1) {
      return this.fileView;
   }

   public String getDialogTitle(JFileChooser var1) {
      String var2 = var1.getDialogTitle();
      if (var2 != null) {
         return var2;
      } else if (var1.getDialogType() == 0) {
         return this.openDialogTitleText;
      } else {
         return var1.getDialogType() == 1 ? this.saveDialogTitleText : this.getApproveButtonText(var1);
      }
   }

   public int getApproveButtonMnemonic(JFileChooser var1) {
      int var2 = var1.getApproveButtonMnemonic();
      if (var2 > 0) {
         return var2;
      } else if (var1.getDialogType() == 0) {
         return this.openButtonMnemonic;
      } else {
         return var1.getDialogType() == 1 ? this.saveButtonMnemonic : var2;
      }
   }

   public String getApproveButtonText(JFileChooser var1) {
      String var2 = var1.getApproveButtonText();
      if (var2 != null) {
         return var2;
      } else if (var1.getDialogType() == 0) {
         return this.openButtonText;
      } else {
         return var1.getDialogType() == 1 ? this.saveButtonText : null;
      }
   }

   public Action getNewFolderAction() {
      if (this.newFolderAction == null) {
         this.newFolderAction = new BasicFileChooserUI.NewFolderAction();
         if (this.readOnly) {
            this.newFolderAction.setEnabled(false);
         }
      }

      return this.newFolderAction;
   }

   public Action getGoHomeAction() {
      return this.goHomeAction;
   }

   public Action getChangeToParentDirectoryAction() {
      return this.changeToParentDirectoryAction;
   }

   public Action getApproveSelectionAction() {
      return this.approveSelectionAction;
   }

   public Action getCancelSelectionAction() {
      return this.cancelSelectionAction;
   }

   public Action getUpdateAction() {
      return this.updateAction;
   }

   private void resetGlobFilter() {
      if (this.actualFileFilter != null) {
         JFileChooser var1 = this.getFileChooser();
         FileFilter var2 = var1.getFileFilter();
         if (var2 != null && var2.equals(this.globFilter)) {
            var1.setFileFilter(this.actualFileFilter);
            var1.removeChoosableFileFilter(this.globFilter);
         }

         this.actualFileFilter = null;
      }

   }

   private static boolean isGlobPattern(String var0) {
      return File.separatorChar == '\\' && (var0.indexOf(42) >= 0 || var0.indexOf(63) >= 0) || File.separatorChar == '/' && (var0.indexOf(42) >= 0 || var0.indexOf(63) >= 0 || var0.indexOf(91) >= 0);
   }

   private void changeDirectory(File var1) {
      JFileChooser var2 = this.getFileChooser();
      if (var1 != null && FilePane.usesShellFolder(var2)) {
         try {
            ShellFolder var3 = ShellFolder.getShellFolder((File)var1);
            if (var3.isLink()) {
               ShellFolder var4 = var3.getLinkLocation();
               if (var4 != null) {
                  if (!var2.isTraversable(var4)) {
                     return;
                  }

                  var1 = var4;
               } else {
                  var1 = var3;
               }
            }
         } catch (FileNotFoundException var5) {
            return;
         }
      }

      var2.setCurrentDirectory((File)var1);
      if (var2.getFileSelectionMode() == 2 && var2.getFileSystemView().isFileSystem((File)var1)) {
         this.setFileName(((File)var1).getAbsolutePath());
      }

   }

   static class FileTransferHandler extends TransferHandler implements UIResource {
      protected Transferable createTransferable(JComponent var1) {
         Object[] var2 = null;
         if (var1 instanceof JList) {
            var2 = ((JList)var1).getSelectedValues();
         } else if (var1 instanceof JTable) {
            JTable var3 = (JTable)var1;
            int[] var4 = var3.getSelectedRows();
            if (var4 != null) {
               var2 = new Object[var4.length];

               for(int var5 = 0; var5 < var4.length; ++var5) {
                  var2[var5] = var3.getValueAt(var4[var5], 0);
               }
            }
         }

         if (var2 != null && var2.length != 0) {
            StringBuffer var10 = new StringBuffer();
            StringBuffer var11 = new StringBuffer();
            var11.append("<html>\n<body>\n<ul>\n");
            Object[] var12 = var2;
            int var6 = var2.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Object var8 = var12[var7];
               String var9 = var8 == null ? "" : var8.toString();
               var10.append(var9 + "\n");
               var11.append("  <li>" + var9 + "\n");
            }

            var10.deleteCharAt(var10.length() - 1);
            var11.append("</ul>\n</body>\n</html>");
            return new BasicFileChooserUI.FileTransferHandler.FileTransferable(var10.toString(), var11.toString(), var2);
         } else {
            return null;
         }
      }

      public int getSourceActions(JComponent var1) {
         return 1;
      }

      static class FileTransferable extends BasicTransferable {
         Object[] fileData;

         FileTransferable(String var1, String var2, Object[] var3) {
            super(var1, var2);
            this.fileData = var3;
         }

         protected DataFlavor[] getRicherFlavors() {
            DataFlavor[] var1 = new DataFlavor[]{DataFlavor.javaFileListFlavor};
            return var1;
         }

         protected Object getRicherData(DataFlavor var1) {
            if (!DataFlavor.javaFileListFlavor.equals(var1)) {
               return null;
            } else {
               ArrayList var2 = new ArrayList();
               Object[] var3 = this.fileData;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Object var6 = var3[var5];
                  var2.add(var6);
               }

               return var2;
            }
         }
      }
   }

   protected class BasicFileView extends FileView {
      protected Hashtable<File, Icon> iconCache = new Hashtable();

      public BasicFileView() {
      }

      public void clearIconCache() {
         this.iconCache = new Hashtable();
      }

      public String getName(File var1) {
         String var2 = null;
         if (var1 != null) {
            var2 = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemDisplayName(var1);
         }

         return var2;
      }

      public String getDescription(File var1) {
         return var1.getName();
      }

      public String getTypeDescription(File var1) {
         String var2 = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemTypeDescription(var1);
         if (var2 == null) {
            if (var1.isDirectory()) {
               var2 = BasicFileChooserUI.this.directoryDescriptionText;
            } else {
               var2 = BasicFileChooserUI.this.fileDescriptionText;
            }
         }

         return var2;
      }

      public Icon getCachedIcon(File var1) {
         return (Icon)this.iconCache.get(var1);
      }

      public void cacheIcon(File var1, Icon var2) {
         if (var1 != null && var2 != null) {
            this.iconCache.put(var1, var2);
         }
      }

      public Icon getIcon(File var1) {
         Icon var2 = this.getCachedIcon(var1);
         if (var2 != null) {
            return var2;
         } else {
            var2 = BasicFileChooserUI.this.fileIcon;
            if (var1 != null) {
               FileSystemView var3 = BasicFileChooserUI.this.getFileChooser().getFileSystemView();
               if (var3.isFloppyDrive(var1)) {
                  var2 = BasicFileChooserUI.this.floppyDriveIcon;
               } else if (var3.isDrive(var1)) {
                  var2 = BasicFileChooserUI.this.hardDriveIcon;
               } else if (var3.isComputerNode(var1)) {
                  var2 = BasicFileChooserUI.this.computerIcon;
               } else if (var1.isDirectory()) {
                  var2 = BasicFileChooserUI.this.directoryIcon;
               }
            }

            this.cacheIcon(var1, var2);
            return var2;
         }
      }

      public Boolean isHidden(File var1) {
         String var2 = var1.getName();
         return var2 != null && var2.charAt(0) == '.' ? Boolean.TRUE : Boolean.FALSE;
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
         JFileChooser var2 = BasicFileChooserUI.this.getFileChooser();
         var2.setCurrentDirectory(var2.getFileSystemView().createFileObject(BasicFileChooserUI.this.getDirectoryName()));
         var2.rescanCurrentDirectory();
      }
   }

   protected class CancelSelectionAction extends AbstractAction {
      public void actionPerformed(ActionEvent var1) {
         BasicFileChooserUI.this.getFileChooser().cancelSelection();
      }
   }

   class GlobFilter extends FileFilter {
      Pattern pattern;
      String globPattern;

      public void setPattern(String var1) {
         char[] var2 = var1.toCharArray();
         char[] var3 = new char[var2.length * 2];
         boolean var4 = File.separatorChar == '\\';
         boolean var5 = false;
         int var6 = 0;
         this.globPattern = var1;
         int var7;
         if (var4) {
            var7 = var2.length;
            if (var1.endsWith("*.*")) {
               var7 -= 2;
            }

            for(int var8 = 0; var8 < var7; ++var8) {
               switch(var2[var8]) {
               case '*':
                  var3[var6++] = '.';
                  var3[var6++] = '*';
                  break;
               case '?':
                  var3[var6++] = '.';
                  break;
               case '\\':
                  var3[var6++] = '\\';
                  var3[var6++] = '\\';
                  break;
               default:
                  if ("+()^$.{}[]".indexOf(var2[var8]) >= 0) {
                     var3[var6++] = '\\';
                  }

                  var3[var6++] = var2[var8];
               }
            }
         } else {
            for(var7 = 0; var7 < var2.length; ++var7) {
               int var10001;
               switch(var2[var7]) {
               case '*':
                  if (!var5) {
                     var3[var6++] = '.';
                  }

                  var3[var6++] = '*';
                  break;
               case '?':
                  var3[var6++] = (char)(var5 ? 63 : 46);
                  break;
               case '[':
                  var5 = true;
                  var3[var6++] = var2[var7];
                  if (var7 < var2.length - 1) {
                     switch(var2[var7 + 1]) {
                     case '!':
                     case '^':
                        var3[var6++] = '^';
                        ++var7;
                        break;
                     case ']':
                        var10001 = var6++;
                        ++var7;
                        var3[var10001] = var2[var7];
                     }
                  }
                  break;
               case '\\':
                  if (var7 == 0 && var2.length > 1 && var2[1] == '~') {
                     var10001 = var6++;
                     ++var7;
                     var3[var10001] = var2[var7];
                  } else {
                     var3[var6++] = '\\';
                     if (var7 < var2.length - 1 && "*?[]".indexOf(var2[var7 + 1]) >= 0) {
                        var10001 = var6++;
                        ++var7;
                        var3[var10001] = var2[var7];
                        continue;
                     }

                     var3[var6++] = '\\';
                  }
                  break;
               case ']':
                  var3[var6++] = var2[var7];
                  var5 = false;
                  break;
               default:
                  if (!Character.isLetterOrDigit(var2[var7])) {
                     var3[var6++] = '\\';
                  }

                  var3[var6++] = var2[var7];
               }
            }
         }

         this.pattern = Pattern.compile(new String(var3, 0, var6), 2);
      }

      public boolean accept(File var1) {
         if (var1 == null) {
            return false;
         } else {
            return var1.isDirectory() ? true : this.pattern.matcher(var1.getName()).matches();
         }
      }

      public String getDescription() {
         return this.globPattern;
      }
   }

   protected class ApproveSelectionAction extends AbstractAction {
      protected ApproveSelectionAction() {
         super("approveSelection");
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicFileChooserUI.this.isDirectorySelected()) {
            File var2 = BasicFileChooserUI.this.getDirectory();
            if (var2 != null) {
               try {
                  var2 = ShellFolder.getNormalizedFile(var2);
               } catch (IOException var19) {
               }

               BasicFileChooserUI.this.changeDirectory(var2);
               return;
            }
         }

         JFileChooser var21 = BasicFileChooserUI.this.getFileChooser();
         String var3 = BasicFileChooserUI.this.getFileName();
         FileSystemView var4 = var21.getFileSystemView();
         File var5 = var21.getCurrentDirectory();
         if (var3 != null) {
            int var6;
            for(var6 = var3.length() - 1; var6 >= 0 && var3.charAt(var6) <= ' '; --var6) {
            }

            var3 = var3.substring(0, var6 + 1);
         }

         if (var3 != null && var3.length() != 0) {
            File var22 = null;
            File[] var7 = null;
            if (File.separatorChar == '/') {
               if (var3.startsWith("~/")) {
                  var3 = System.getProperty("user.home") + var3.substring(1);
               } else if (var3.equals("~")) {
                  var3 = System.getProperty("user.home");
               }
            }

            if (var21.isMultiSelectionEnabled() && var3.length() > 1 && var3.charAt(0) == '"' && var3.charAt(var3.length() - 1) == '"') {
               ArrayList var23 = new ArrayList();
               String[] var24 = var3.substring(1, var3.length() - 1).split("\" \"");
               Arrays.sort((Object[])var24);
               File[] var25 = null;
               int var26 = 0;
               String[] var27 = var24;
               int var28 = var24.length;

               for(int var14 = 0; var14 < var28; ++var14) {
                  String var15 = var27[var14];
                  File var16 = var4.createFileObject(var15);
                  if (!var16.isAbsolute()) {
                     if (var25 == null) {
                        var25 = var4.getFiles(var5, false);
                        Arrays.sort((Object[])var25);
                     }

                     for(int var17 = 0; var17 < var25.length; ++var17) {
                        int var18 = (var26 + var17) % var25.length;
                        if (var25[var18].getName().equals(var15)) {
                           var16 = var25[var18];
                           var26 = var18 + 1;
                           break;
                        }
                     }
                  }

                  var23.add(var16);
               }

               if (!var23.isEmpty()) {
                  var7 = (File[])var23.toArray(new File[var23.size()]);
               }

               BasicFileChooserUI.this.resetGlobFilter();
            } else {
               var22 = var4.createFileObject(var3);
               if (!var22.isAbsolute()) {
                  var22 = var4.getChild(var5, var3);
               }

               FileFilter var8 = var21.getFileFilter();
               if (!var22.exists() && BasicFileChooserUI.isGlobPattern(var3)) {
                  BasicFileChooserUI.this.changeDirectory(var22.getParentFile());
                  if (BasicFileChooserUI.this.globFilter == null) {
                     BasicFileChooserUI.this.globFilter = BasicFileChooserUI.this.new GlobFilter();
                  }

                  try {
                     BasicFileChooserUI.this.globFilter.setPattern(var22.getName());
                     if (!(var8 instanceof BasicFileChooserUI.GlobFilter)) {
                        BasicFileChooserUI.this.actualFileFilter = var8;
                     }

                     var21.setFileFilter((FileFilter)null);
                     var21.setFileFilter(BasicFileChooserUI.this.globFilter);
                     return;
                  } catch (PatternSyntaxException var20) {
                  }
               }

               BasicFileChooserUI.this.resetGlobFilter();
               boolean var9 = var22 != null && var22.isDirectory();
               boolean var10 = var22 != null && var21.isTraversable(var22);
               boolean var11 = var21.isDirectorySelectionEnabled();
               boolean var12 = var21.isFileSelectionEnabled();
               boolean var13 = var1 != null && (var1.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
               if (var9 && var10 && (var13 || !var11)) {
                  BasicFileChooserUI.this.changeDirectory(var22);
                  return;
               }

               if ((var9 || !var12) && (!var9 || !var11) && (!var11 || var22.exists())) {
                  var22 = null;
               }
            }

            if (var7 == null && var22 == null) {
               if (var21.isMultiSelectionEnabled()) {
                  var21.setSelectedFiles((File[])null);
               } else {
                  var21.setSelectedFile((File)null);
               }

               var21.cancelSelection();
            } else {
               if (var7 == null && !var21.isMultiSelectionEnabled()) {
                  var21.setSelectedFile(var22);
               } else {
                  if (var7 == null) {
                     var7 = new File[]{var22};
                  }

                  var21.setSelectedFiles(var7);
                  var21.setSelectedFiles(var7);
               }

               var21.approveSelection();
            }

         } else {
            BasicFileChooserUI.this.resetGlobFilter();
         }
      }
   }

   protected class ChangeToParentDirectoryAction extends AbstractAction {
      protected ChangeToParentDirectoryAction() {
         super("Go Up");
         this.putValue("ActionCommandKey", "Go Up");
      }

      public void actionPerformed(ActionEvent var1) {
         BasicFileChooserUI.this.getFileChooser().changeToParentDirectory();
      }
   }

   protected class GoHomeAction extends AbstractAction {
      protected GoHomeAction() {
         super("Go Home");
      }

      public void actionPerformed(ActionEvent var1) {
         JFileChooser var2 = BasicFileChooserUI.this.getFileChooser();
         BasicFileChooserUI.this.changeDirectory(var2.getFileSystemView().getHomeDirectory());
      }
   }

   protected class NewFolderAction extends AbstractAction {
      protected NewFolderAction() {
         super("New Folder");
      }

      public void actionPerformed(ActionEvent var1) {
         if (!BasicFileChooserUI.this.readOnly) {
            JFileChooser var2 = BasicFileChooserUI.this.getFileChooser();
            File var3 = var2.getCurrentDirectory();
            if (!var3.exists()) {
               JOptionPane.showMessageDialog(var2, BasicFileChooserUI.this.newFolderParentDoesntExistText, BasicFileChooserUI.this.newFolderParentDoesntExistTitleText, 2);
            } else {
               try {
                  File var4 = var2.getFileSystemView().createNewFolder(var3);
                  if (var2.isMultiSelectionEnabled()) {
                     var2.setSelectedFiles(new File[]{var4});
                  } else {
                     var2.setSelectedFile(var4);
                  }
               } catch (IOException var6) {
                  JOptionPane.showMessageDialog(var2, BasicFileChooserUI.this.newFolderErrorText + BasicFileChooserUI.this.newFolderErrorSeparator + var6, BasicFileChooserUI.this.newFolderErrorText, 0);
                  return;
               }

               var2.rescanCurrentDirectory();
            }
         }
      }
   }

   protected class SelectionListener implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent var1) {
         BasicFileChooserUI.this.getHandler().valueChanged(var1);
      }
   }

   protected class DoubleClickListener extends MouseAdapter {
      BasicFileChooserUI.Handler handler;

      public DoubleClickListener(JList var2) {
         this.handler = BasicFileChooserUI.this.new Handler(var2);
      }

      public void mouseEntered(MouseEvent var1) {
         this.handler.mouseEntered(var1);
      }

      public void mouseClicked(MouseEvent var1) {
         this.handler.mouseClicked(var1);
      }
   }

   private class Handler implements MouseListener, ListSelectionListener {
      JList list;

      Handler() {
      }

      Handler(JList var2) {
         this.list = var2;
      }

      public void mouseClicked(MouseEvent var1) {
         if (this.list != null && SwingUtilities.isLeftMouseButton(var1) && var1.getClickCount() % 2 == 0) {
            int var2 = SwingUtilities2.loc2IndexFileList(this.list, var1.getPoint());
            if (var2 >= 0) {
               File var3 = (File)this.list.getModel().getElementAt(var2);

               try {
                  var3 = ShellFolder.getNormalizedFile(var3);
               } catch (IOException var5) {
               }

               if (BasicFileChooserUI.this.getFileChooser().isTraversable(var3)) {
                  this.list.clearSelection();
                  BasicFileChooserUI.this.changeDirectory(var3);
               } else {
                  BasicFileChooserUI.this.getFileChooser().approveSelection();
               }
            }
         }

      }

      public void mouseEntered(MouseEvent var1) {
         if (this.list != null) {
            TransferHandler var2 = BasicFileChooserUI.this.getFileChooser().getTransferHandler();
            TransferHandler var3 = this.list.getTransferHandler();
            if (var2 != var3) {
               this.list.setTransferHandler(var2);
            }

            if (BasicFileChooserUI.this.getFileChooser().getDragEnabled() != this.list.getDragEnabled()) {
               this.list.setDragEnabled(BasicFileChooserUI.this.getFileChooser().getDragEnabled());
            }
         }

      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
      }

      public void valueChanged(ListSelectionEvent var1) {
         if (!var1.getValueIsAdjusting()) {
            JFileChooser var2 = BasicFileChooserUI.this.getFileChooser();
            FileSystemView var3 = var2.getFileSystemView();
            JList var4 = (JList)var1.getSource();
            int var5 = var2.getFileSelectionMode();
            boolean var6 = BasicFileChooserUI.this.usesSingleFilePane && var5 == 0;
            if (var2.isMultiSelectionEnabled()) {
               File[] var7 = null;
               Object[] var8 = var4.getSelectedValues();
               if (var8 != null) {
                  if (var8.length == 1 && ((File)var8[0]).isDirectory() && var2.isTraversable((File)var8[0]) && (var6 || !var3.isFileSystem((File)var8[0]))) {
                     BasicFileChooserUI.this.setDirectorySelected(true);
                     BasicFileChooserUI.this.setDirectory((File)var8[0]);
                  } else {
                     ArrayList var9 = new ArrayList(var8.length);
                     Object[] var10 = var8;
                     int var11 = var8.length;
                     int var12 = 0;

                     while(true) {
                        if (var12 >= var11) {
                           if (var9.size() > 0) {
                              var7 = (File[])var9.toArray(new File[var9.size()]);
                           }

                           BasicFileChooserUI.this.setDirectorySelected(false);
                           break;
                        }

                        Object var13 = var10[var12];
                        File var14 = (File)var13;
                        boolean var15 = var14.isDirectory();
                        if (var2.isFileSelectionEnabled() && !var15 || var2.isDirectorySelectionEnabled() && var3.isFileSystem(var14) && var15) {
                           var9.add(var14);
                        }

                        ++var12;
                     }
                  }
               }

               var2.setSelectedFiles(var7);
            } else {
               File var16 = (File)var4.getSelectedValue();
               if (var16 != null && var16.isDirectory() && var2.isTraversable(var16) && (var6 || !var3.isFileSystem(var16))) {
                  BasicFileChooserUI.this.setDirectorySelected(true);
                  BasicFileChooserUI.this.setDirectory(var16);
                  if (BasicFileChooserUI.this.usesSingleFilePane) {
                     var2.setSelectedFile((File)null);
                  }
               } else {
                  BasicFileChooserUI.this.setDirectorySelected(false);
                  if (var16 != null) {
                     var2.setSelectedFile(var16);
                  }
               }
            }
         }

      }
   }
}
