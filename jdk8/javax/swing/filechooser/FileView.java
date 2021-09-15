package javax.swing.filechooser;

import java.io.File;
import javax.swing.Icon;

public abstract class FileView {
   public String getName(File var1) {
      return null;
   }

   public String getDescription(File var1) {
      return null;
   }

   public String getTypeDescription(File var1) {
      return null;
   }

   public Icon getIcon(File var1) {
      return null;
   }

   public Boolean isTraversable(File var1) {
      return null;
   }
}
