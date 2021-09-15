package javax.swing.filechooser;

import java.io.File;

public abstract class FileFilter {
   public abstract boolean accept(File var1);

   public abstract String getDescription();
}
