package javax.swing.plaf;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public abstract class FileChooserUI extends ComponentUI {
   public abstract FileFilter getAcceptAllFileFilter(JFileChooser var1);

   public abstract FileView getFileView(JFileChooser var1);

   public abstract String getApproveButtonText(JFileChooser var1);

   public abstract String getDialogTitle(JFileChooser var1);

   public abstract void rescanCurrentDirectory(JFileChooser var1);

   public abstract void ensureFileIsVisible(JFileChooser var1, File var2);

   public JButton getDefaultButton(JFileChooser var1) {
      return null;
   }
}
