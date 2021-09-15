package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;

public abstract class SynthFileChooserUI extends BasicFileChooserUI implements SynthUI {
   private JButton approveButton;
   private JButton cancelButton;
   private SynthStyle style;
   private Action fileNameCompletionAction = new SynthFileChooserUI.FileNameCompletionAction();
   private FileFilter actualFileFilter = null;
   private SynthFileChooserUI.GlobFilter globFilter = null;
   private String fileNameCompletionString;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthFileChooserUIImpl((JFileChooser)var0);
   }

   public SynthFileChooserUI(JFileChooser var1) {
      super(var1);
   }

   public SynthContext getContext(JComponent var1) {
      return new SynthContext(var1, Region.FILE_CHOOSER, this.style, this.getComponentState(var1));
   }

   protected SynthContext getContext(JComponent var1, int var2) {
      Region var3 = SynthLookAndFeel.getRegion(var1);
      return new SynthContext(var1, Region.FILE_CHOOSER, this.style, var2);
   }

   private Region getRegion(JComponent var1) {
      return SynthLookAndFeel.getRegion(var1);
   }

   private int getComponentState(JComponent var1) {
      if (var1.isEnabled()) {
         return var1.isFocusOwner() ? 257 : 1;
      } else {
         return 8;
      }
   }

   private void updateStyle(JComponent var1) {
      SynthStyle var2 = SynthLookAndFeel.getStyleFactory().getStyle(var1, Region.FILE_CHOOSER);
      if (var2 != this.style) {
         if (this.style != null) {
            this.style.uninstallDefaults(this.getContext(var1, 1));
         }

         this.style = var2;
         SynthContext var3 = this.getContext(var1, 1);
         this.style.installDefaults(var3);
         Border var4 = var1.getBorder();
         if (var4 == null || var4 instanceof UIResource) {
            var1.setBorder(new SynthFileChooserUI.UIBorder(this.style.getInsets(var3, (Insets)null)));
         }

         this.directoryIcon = this.style.getIcon(var3, "FileView.directoryIcon");
         this.fileIcon = this.style.getIcon(var3, "FileView.fileIcon");
         this.computerIcon = this.style.getIcon(var3, "FileView.computerIcon");
         this.hardDriveIcon = this.style.getIcon(var3, "FileView.hardDriveIcon");
         this.floppyDriveIcon = this.style.getIcon(var3, "FileView.floppyDriveIcon");
         this.newFolderIcon = this.style.getIcon(var3, "FileChooser.newFolderIcon");
         this.upFolderIcon = this.style.getIcon(var3, "FileChooser.upFolderIcon");
         this.homeFolderIcon = this.style.getIcon(var3, "FileChooser.homeFolderIcon");
         this.detailsViewIcon = this.style.getIcon(var3, "FileChooser.detailsViewIcon");
         this.listViewIcon = this.style.getIcon(var3, "FileChooser.listViewIcon");
      }

   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      SwingUtilities.replaceUIActionMap(var1, this.createActionMap());
   }

   public void installComponents(JFileChooser var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.cancelButton = new JButton(this.cancelButtonText);
      this.cancelButton.setName("SynthFileChooser.cancelButton");
      this.cancelButton.setIcon(var2.getStyle().getIcon(var2, "FileChooser.cancelIcon"));
      this.cancelButton.setMnemonic(this.cancelButtonMnemonic);
      this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
      this.cancelButton.addActionListener(this.getCancelSelectionAction());
      this.approveButton = new JButton(this.getApproveButtonText(var1));
      this.approveButton.setName("SynthFileChooser.approveButton");
      this.approveButton.setIcon(var2.getStyle().getIcon(var2, "FileChooser.okIcon"));
      this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var1));
      this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var1));
      this.approveButton.addActionListener(this.getApproveSelectionAction());
   }

   public void uninstallComponents(JFileChooser var1) {
      var1.removeAll();
   }

   protected void installListeners(JFileChooser var1) {
      super.installListeners(var1);
      this.getModel().addListDataListener(new ListDataListener() {
         public void contentsChanged(ListDataEvent var1) {
            SynthFileChooserUI.this.new DelayedSelectionUpdater();
         }

         public void intervalAdded(ListDataEvent var1) {
            SynthFileChooserUI.this.new DelayedSelectionUpdater();
         }

         public void intervalRemoved(ListDataEvent var1) {
         }
      });
   }

   protected abstract ActionMap createActionMap();

   protected void installDefaults(JFileChooser var1) {
      super.installDefaults(var1);
      this.updateStyle(var1);
   }

   protected void uninstallDefaults(JFileChooser var1) {
      super.uninstallDefaults(var1);
      SynthContext var2 = this.getContext(this.getFileChooser(), 1);
      this.style.uninstallDefaults(var2);
      this.style = null;
   }

   protected void installIcons(JFileChooser var1) {
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      if (var2.isOpaque()) {
         var1.setColor(this.style.getColor(var3, ColorType.BACKGROUND));
         var1.fillRect(0, 0, var2.getWidth(), var2.getHeight());
      }

      this.style.getPainter(var3).paintFileChooserBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
   }

   protected void paint(SynthContext var1, Graphics var2) {
   }

   public abstract void setFileName(String var1);

   public abstract String getFileName();

   protected void doSelectedFileChanged(PropertyChangeEvent var1) {
   }

   protected void doSelectedFilesChanged(PropertyChangeEvent var1) {
   }

   protected void doDirectoryChanged(PropertyChangeEvent var1) {
   }

   protected void doAccessoryChanged(PropertyChangeEvent var1) {
   }

   protected void doFileSelectionModeChanged(PropertyChangeEvent var1) {
   }

   protected void doMultiSelectionChanged(PropertyChangeEvent var1) {
      if (!this.getFileChooser().isMultiSelectionEnabled()) {
         this.getFileChooser().setSelectedFiles((File[])null);
      }

   }

   protected void doControlButtonsChanged(PropertyChangeEvent var1) {
      if (this.getFileChooser().getControlButtonsAreShown()) {
         this.approveButton.setText(this.getApproveButtonText(this.getFileChooser()));
         this.approveButton.setToolTipText(this.getApproveButtonToolTipText(this.getFileChooser()));
         this.approveButton.setMnemonic(this.getApproveButtonMnemonic(this.getFileChooser()));
      }

   }

   protected void doAncestorChanged(PropertyChangeEvent var1) {
   }

   public PropertyChangeListener createPropertyChangeListener(JFileChooser var1) {
      return new SynthFileChooserUI.SynthFCPropertyChangeListener();
   }

   private void updateFileNameCompletion() {
      if (this.fileNameCompletionString != null && this.fileNameCompletionString.equals(this.getFileName())) {
         File[] var1 = (File[])this.getModel().getFiles().toArray(new File[0]);
         String var2 = this.getCommonStartString(var1);
         if (var2 != null && var2.startsWith(this.fileNameCompletionString)) {
            this.setFileName(var2);
         }

         this.fileNameCompletionString = null;
      }

   }

   private String getCommonStartString(File[] var1) {
      String var2 = null;
      String var3 = null;
      int var4 = 0;
      if (var1.length == 0) {
         return null;
      } else {
         while(true) {
            for(int var5 = 0; var5 < var1.length; ++var5) {
               String var6 = var1[var5].getName();
               if (var5 == 0) {
                  if (var6.length() == var4) {
                     return var2;
                  }

                  var3 = var6.substring(0, var4 + 1);
               }

               if (!var6.startsWith(var3)) {
                  return var2;
               }
            }

            var2 = var3;
            ++var4;
         }
      }
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
      return File.separatorChar == '\\' && var0.indexOf(42) >= 0 || File.separatorChar == '/' && (var0.indexOf(42) >= 0 || var0.indexOf(63) >= 0 || var0.indexOf(91) >= 0);
   }

   public Action getFileNameCompletionAction() {
      return this.fileNameCompletionAction;
   }

   protected JButton getApproveButton(JFileChooser var1) {
      return this.approveButton;
   }

   protected JButton getCancelButton(JFileChooser var1) {
      return this.cancelButton;
   }

   public void clearIconCache() {
   }

   private class UIBorder extends AbstractBorder implements UIResource {
      private Insets _insets;

      UIBorder(Insets var2) {
         if (var2 != null) {
            this._insets = new Insets(var2.top, var2.left, var2.bottom, var2.right);
         } else {
            this._insets = null;
         }

      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (var1 instanceof JComponent) {
            JComponent var7 = (JComponent)var1;
            SynthContext var8 = SynthFileChooserUI.this.getContext(var7);
            SynthStyle var9 = var8.getStyle();
            if (var9 != null) {
               var9.getPainter(var8).paintFileChooserBorder(var8, var2, var3, var4, var5, var6);
            }

         }
      }

      public Insets getBorderInsets(Component var1, Insets var2) {
         if (var2 == null) {
            var2 = new Insets(0, 0, 0, 0);
         }

         if (this._insets != null) {
            var2.top = this._insets.top;
            var2.bottom = this._insets.bottom;
            var2.left = this._insets.left;
            var2.right = this._insets.right;
         } else {
            var2.top = var2.bottom = var2.right = var2.left = 0;
         }

         return var2;
      }

      public boolean isBorderOpaque() {
         return false;
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
               if (var2[var8] == '*') {
                  var3[var6++] = '.';
               }

               var3[var6++] = var2[var8];
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

   private class FileNameCompletionAction extends AbstractAction {
      protected FileNameCompletionAction() {
         super("fileNameCompletion");
      }

      public void actionPerformed(ActionEvent var1) {
         JFileChooser var2 = SynthFileChooserUI.this.getFileChooser();
         String var3 = SynthFileChooserUI.this.getFileName();
         if (var3 != null) {
            var3 = var3.trim();
         }

         SynthFileChooserUI.this.resetGlobFilter();
         if (var3 != null && !var3.equals("") && (!var2.isMultiSelectionEnabled() || !var3.startsWith("\""))) {
            FileFilter var4 = var2.getFileFilter();
            if (SynthFileChooserUI.this.globFilter == null) {
               SynthFileChooserUI.this.globFilter = SynthFileChooserUI.this.new GlobFilter();
            }

            try {
               SynthFileChooserUI.this.globFilter.setPattern(!SynthFileChooserUI.isGlobPattern(var3) ? var3 + "*" : var3);
               if (!(var4 instanceof SynthFileChooserUI.GlobFilter)) {
                  SynthFileChooserUI.this.actualFileFilter = var4;
               }

               var2.setFileFilter((FileFilter)null);
               var2.setFileFilter(SynthFileChooserUI.this.globFilter);
               SynthFileChooserUI.this.fileNameCompletionString = var3;
            } catch (PatternSyntaxException var6) {
            }

         }
      }
   }

   private class SynthFCPropertyChangeListener implements PropertyChangeListener {
      private SynthFCPropertyChangeListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("fileSelectionChanged")) {
            SynthFileChooserUI.this.doFileSelectionModeChanged(var1);
         } else if (var2.equals("SelectedFileChangedProperty")) {
            SynthFileChooserUI.this.doSelectedFileChanged(var1);
         } else if (var2.equals("SelectedFilesChangedProperty")) {
            SynthFileChooserUI.this.doSelectedFilesChanged(var1);
         } else if (var2.equals("directoryChanged")) {
            SynthFileChooserUI.this.doDirectoryChanged(var1);
         } else if (var2 == "MultiSelectionEnabledChangedProperty") {
            SynthFileChooserUI.this.doMultiSelectionChanged(var1);
         } else if (var2 == "AccessoryChangedProperty") {
            SynthFileChooserUI.this.doAccessoryChanged(var1);
         } else if (var2 != "ApproveButtonTextChangedProperty" && var2 != "ApproveButtonToolTipTextChangedProperty" && var2 != "DialogTypeChangedProperty" && var2 != "ControlButtonsAreShownChangedProperty") {
            if (var2.equals("componentOrientation")) {
               ComponentOrientation var3 = (ComponentOrientation)var1.getNewValue();
               JFileChooser var4 = (JFileChooser)var1.getSource();
               if (var3 != (ComponentOrientation)var1.getOldValue()) {
                  var4.applyComponentOrientation(var3);
               }
            } else if (var2.equals("ancestor")) {
               SynthFileChooserUI.this.doAncestorChanged(var1);
            }
         } else {
            SynthFileChooserUI.this.doControlButtonsChanged(var1);
         }

      }

      // $FF: synthetic method
      SynthFCPropertyChangeListener(Object var2) {
         this();
      }
   }

   private class DelayedSelectionUpdater implements Runnable {
      DelayedSelectionUpdater() {
         SwingUtilities.invokeLater(this);
      }

      public void run() {
         SynthFileChooserUI.this.updateFileNameCompletion();
      }
   }
}
