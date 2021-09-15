package java.awt.datatransfer;

import java.util.Map;

public interface FlavorMap {
   Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] var1);

   Map<String, DataFlavor> getFlavorsForNatives(String[] var1);
}
