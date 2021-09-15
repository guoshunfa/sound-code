package sun.launcher.resources;

import java.util.ListResourceBundle;

public final class launcher_zh_HK extends ListResourceBundle {
   protected final Object[][] getContents() {
      return new Object[][]{{"java.launcher.X.macosx.usage", "\n下列是 Mac OS X 特定選項:\n    -XstartOnFirstThread\n                      在第一個 (AppKit) 執行緒執行 main() 方法\n    -Xdock:name=<application name>\"\n                      覆寫結合說明畫面中顯示的預設應用程式名稱\n    -Xdock:icon=<path to icon file>\n                      覆寫結合說明畫面中顯示的預設圖示\n\n"}, {"java.launcher.X.usage", "    -Xmixed           混合模式執行 (預設)\n    -Xint             僅限解譯模式執行\n    -Xbootclasspath:<以 {0} 分隔的目錄和 zip/jar 檔案>\n                      設定啟動安裝類別和資源的搜尋路徑\n    -Xbootclasspath/a:<以 {0} 分隔的目錄和 zip/jar 檔案>\n                      附加在啟動安裝類別路徑的結尾\n    -Xbootclasspath/p:<以 {0} 分隔的目錄和 zip/jar 檔案>\n                      附加在啟動安裝類別路徑的前面\n    -Xdiag            顯示其他的診斷訊息\n    -Xnoclassgc       停用類別資源回收\n    -Xincgc           啟用漸進資源回收\n    -Xloggc:<file>    利用時戳將 GC 狀態記錄至檔案中\n    -Xbatch           停用背景編譯\n    -Xms<size>        設定起始 Java 堆集大小\n    -Xmx<size>        設定 Java 堆集大小上限\n    -Xss<size>        設定 Java 執行緒堆疊大小\n    -Xprof            輸出 CPU 分析資料\n    -Xfuture          啟用最嚴格的檢查，預先作為將來的預設\n    -Xrs              減少 Java/VM 使用作業系統信號 (請參閱文件)\n    -Xcheck:jni       執行其他的 JNI 函數檢查\n    -Xshare:off       不嘗試使用共用類別資料\n    -Xshare:auto      儘可能使用共用類別資料 (預設)\n    -Xshare:on        需要使用共用類別資料，否則失敗。\n    -XshowSettings    顯示所有設定值並繼續\n    -XshowSettings:all\n                      顯示所有設定值並繼續\n    -XshowSettings:vm 顯示所有 VM 相關設定值並繼續\n    -XshowSettings:properties\n                      顯示所有屬性設定值並繼續\n    -XshowSettings:locale\n                      顯示所有地區設定相關設定值並繼續\n\n -X 選項不是標準選項，若有變更不另行通知。\n"}, {"java.launcher.cls.error1", "錯誤: 找不到或無法載入主要類別 {0}"}, {"java.launcher.cls.error2", "錯誤: 主要方法不是類別 {1} 中的 {0}，請定義主要方法為:\n   public static void main(String[] args)"}, {"java.launcher.cls.error3", "錯誤: 主要方法必須傳回類別 {0} 中 void 類型的值，\n請定義主要方法為:\n   public static void main(String[] args)"}, {"java.launcher.cls.error4", "錯誤: 在類別 {0} 中找不到主要方法，請定義主要方法為:\n   public static void main(String[] args)\n或者 JavaFX 應用程式類別必須擴充 {1}"}, {"java.launcher.cls.error5", "錯誤: 遺漏執行此應用程式所需的 JavaFX 程式實際執行元件"}, {"java.launcher.ergo.message1", "                  預設的 VM 為 {0}"}, {"java.launcher.ergo.message2", "                  因為您正在伺服器類別機器上執行。\n"}, {"java.launcher.init.error", "初始化錯誤"}, {"java.launcher.jar.error1", "錯誤: 嘗試開啟檔案 {0} 時發生未預期的錯誤"}, {"java.launcher.jar.error2", "在 {0} 中找不到資訊清單"}, {"java.launcher.jar.error3", "{0} 中沒有主要資訊清單屬性"}, {"java.launcher.javafx.error1", "錯誤: JavaFX launchApplication 方法的簽章錯誤，它\n必須宣告為靜態並傳回 void 類型的值"}, {"java.launcher.opt.datamodel", "    -d{0}\t  使用 {0} 位元資料模型 (如果有的話)\n"}, {"java.launcher.opt.footer", "    -cp <目錄及 zip/jar 檔案的類別搜尋路徑>\n    -classpath <目錄及 zip/jar 檔案的類別搜尋路徑>\n                  使用 {0} 區隔的目錄、JAR 存檔以及 ZIP 存檔清單來搜尋類別檔案。\n    -D<name>=<value>\n                  設定系統屬性\n    -verbose:[class|gc|jni]\n                  啟用詳細資訊輸出\n    -version      列印產品版本並結束\n    -version:<value>\n                  警告: 此功能已不再使用，將會從未來版本中移除。\n                  需要指定的版本才能執行\n    -showversion  列印產品版本並繼續\n    -jre-restrict-search | -no-jre-restrict-search\n                  警告: 此功能已不再使用，將會從未來版本中移除。\n                  在版本搜尋中包括/排除使用者專用 JRE\n    -? -help      列印此說明訊息\n    -X            列印非標準選項的說明\n    -ea[:<packagename>...|:<classname>]\n    -enableassertions[:<packagename>...|:<classname>]\n                  啟用含指定詳細程度的宣告\n    -da[:<packagename>...|:<classname>]\n    -disableassertions[:<packagename>...|:<classname>]\n                  停用含指定詳細程度的宣告\n    -esa | -enablesystemassertions\n                  啟用系統宣告\n    -dsa | -disablesystemassertions\n                  停用系統宣告\n    -agentlib:<libname>[=<options>]\n                  載入原生代理程式程式庫 <libname>，例如 -agentlib:hprof\n                  另請參閱 -agentlib:jdwp=help 與 -agentlib:hprof=help\n    -agentpath:<pathname>[=<options>]\n                  使用完整路徑名稱載入原生代理程式程式庫\n    -javaagent:<jarpath>[=<options>]\n                  載入 Java 程式語言代理程式，請參閱 java.lang.instrument\n    -splash:<imagepath>\n                  顯示指定影像的軟體資訊畫面\n請參閱 http://www.oracle.com/technetwork/java/javase/documentation/index.html 暸解詳細資訊。"}, {"java.launcher.opt.header", "用法: {0} [-options] class [args...]\n           (執行類別)\n   或  {0} [-options] -jar jarfile [args...]\n           (執行 jar 檔案)\n選項包括:\n"}, {"java.launcher.opt.hotspot", "    {0}\t  是 \"{1}\" VM 的同義字  [已不再使用]\n"}, {"java.launcher.opt.vmselect", "    {0}\t  選取 \"{1}\" VM\n"}};
   }
}
