package java.awt.datatransfer;

import java.util.List;

public interface FlavorTable extends FlavorMap {
   List<String> getNativesForFlavor(DataFlavor var1);

   List<DataFlavor> getFlavorsForNative(String var1);
}
