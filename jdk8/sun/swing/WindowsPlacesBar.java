package sun.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import sun.awt.OSInfo;
import sun.awt.shell.ShellFolder;

public class WindowsPlacesBar extends JToolBar implements ActionListener, PropertyChangeListener {
   JFileChooser fc;
   JToggleButton[] buttons;
   ButtonGroup buttonGroup;
   File[] files;
   final Dimension buttonSize;

   public WindowsPlacesBar(JFileChooser var1, boolean var2) {
      super(1);
      this.fc = var1;
      this.setFloatable(false);
      this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
      boolean var3 = OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0;
      if (var2) {
         this.buttonSize = new Dimension(83, 69);
         this.putClientProperty("XPStyle.subAppName", "placesbar");
         this.setBorder(new EmptyBorder(1, 1, 1, 1));
      } else {
         this.buttonSize = new Dimension(83, var3 ? 65 : 54);
         this.setBorder(new BevelBorder(1, UIManager.getColor("ToolBar.highlight"), UIManager.getColor("ToolBar.background"), UIManager.getColor("ToolBar.darkShadow"), UIManager.getColor("ToolBar.shadow")));
      }

      Color var4 = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
      this.setBackground(var4);
      FileSystemView var5 = var1.getFileSystemView();
      this.files = (File[])((File[])ShellFolder.get("fileChooserShortcutPanelFolders"));
      this.buttons = new JToggleButton[this.files.length];
      this.buttonGroup = new ButtonGroup();

      for(int var6 = 0; var6 < this.files.length; ++var6) {
         if (var5.isFileSystemRoot(this.files[var6])) {
            this.files[var6] = var5.createFileObject(this.files[var6].getAbsolutePath());
         }

         String var7 = var5.getSystemDisplayName(this.files[var6]);
         int var8 = var7.lastIndexOf(File.separatorChar);
         if (var8 >= 0 && var8 < var7.length() - 1) {
            var7 = var7.substring(var8 + 1);
         }

         Object var9;
         if (this.files[var6] instanceof ShellFolder) {
            ShellFolder var10 = (ShellFolder)this.files[var6];
            Image var11 = var10.getIcon(true);
            if (var11 == null) {
               var11 = (Image)ShellFolder.get("shell32LargeIcon 1");
            }

            var9 = var11 == null ? null : new ImageIcon(var11, var10.getFolderType());
         } else {
            var9 = var5.getSystemIcon(this.files[var6]);
         }

         this.buttons[var6] = new JToggleButton(var7, (Icon)var9);
         if (var2) {
            this.buttons[var6].putClientProperty("XPStyle.subAppName", "placesbar");
         } else {
            Color var12 = new Color(UIManager.getColor("List.selectionForeground").getRGB());
            this.buttons[var6].setContentAreaFilled(false);
            this.buttons[var6].setForeground(var12);
         }

         this.buttons[var6].setMargin(new Insets(3, 2, 1, 2));
         this.buttons[var6].setFocusPainted(false);
         this.buttons[var6].setIconTextGap(0);
         this.buttons[var6].setHorizontalTextPosition(0);
         this.buttons[var6].setVerticalTextPosition(3);
         this.buttons[var6].setAlignmentX(0.5F);
         this.buttons[var6].setPreferredSize(this.buttonSize);
         this.buttons[var6].setMaximumSize(this.buttonSize);
         this.buttons[var6].addActionListener(this);
         this.add(this.buttons[var6]);
         if (var6 < this.files.length - 1 && var2) {
            this.add(Box.createRigidArea(new Dimension(1, 1)));
         }

         this.buttonGroup.add(this.buttons[var6]);
      }

      this.doDirectoryChanged(var1.getCurrentDirectory());
   }

   protected void doDirectoryChanged(File var1) {
      for(int var2 = 0; var2 < this.buttons.length; ++var2) {
         JToggleButton var3 = this.buttons[var2];
         if (this.files[var2].equals(var1)) {
            var3.setSelected(true);
            break;
         }

         if (var3.isSelected()) {
            this.buttonGroup.remove(var3);
            var3.setSelected(false);
            this.buttonGroup.add(var3);
         }
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 == "directoryChanged") {
         this.doDirectoryChanged(this.fc.getCurrentDirectory());
      }

   }

   public void actionPerformed(ActionEvent var1) {
      JToggleButton var2 = (JToggleButton)var1.getSource();

      for(int var3 = 0; var3 < this.buttons.length; ++var3) {
         if (var2 == this.buttons[var3]) {
            this.fc.setCurrentDirectory(this.files[var3]);
            break;
         }
      }

   }

   public Dimension getPreferredSize() {
      Dimension var1 = super.getMinimumSize();
      Dimension var2 = super.getPreferredSize();
      int var3 = var1.height;
      if (this.buttons != null && this.buttons.length > 0 && this.buttons.length < 5) {
         JToggleButton var4 = this.buttons[0];
         if (var4 != null) {
            int var5 = 5 * (var4.getPreferredSize().height + 1);
            if (var5 > var3) {
               var3 = var5;
            }
         }
      }

      if (var3 > var2.height) {
         var2 = new Dimension(var2.width, var3);
      }

      return var2;
   }
}
