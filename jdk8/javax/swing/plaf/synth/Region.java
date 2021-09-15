package javax.swing.plaf.synth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import sun.awt.AppContext;

public class Region {
   private static final Object UI_TO_REGION_MAP_KEY = new Object();
   private static final Object LOWER_CASE_NAME_MAP_KEY = new Object();
   public static final Region ARROW_BUTTON = new Region("ArrowButton", false);
   public static final Region BUTTON = new Region("Button", false);
   public static final Region CHECK_BOX = new Region("CheckBox", false);
   public static final Region CHECK_BOX_MENU_ITEM = new Region("CheckBoxMenuItem", false);
   public static final Region COLOR_CHOOSER = new Region("ColorChooser", false);
   public static final Region COMBO_BOX = new Region("ComboBox", false);
   public static final Region DESKTOP_PANE = new Region("DesktopPane", false);
   public static final Region DESKTOP_ICON = new Region("DesktopIcon", false);
   public static final Region EDITOR_PANE = new Region("EditorPane", false);
   public static final Region FILE_CHOOSER = new Region("FileChooser", false);
   public static final Region FORMATTED_TEXT_FIELD = new Region("FormattedTextField", false);
   public static final Region INTERNAL_FRAME = new Region("InternalFrame", false);
   public static final Region INTERNAL_FRAME_TITLE_PANE = new Region("InternalFrameTitlePane", false);
   public static final Region LABEL = new Region("Label", false);
   public static final Region LIST = new Region("List", false);
   public static final Region MENU = new Region("Menu", false);
   public static final Region MENU_BAR = new Region("MenuBar", false);
   public static final Region MENU_ITEM = new Region("MenuItem", false);
   public static final Region MENU_ITEM_ACCELERATOR = new Region("MenuItemAccelerator", true);
   public static final Region OPTION_PANE = new Region("OptionPane", false);
   public static final Region PANEL = new Region("Panel", false);
   public static final Region PASSWORD_FIELD = new Region("PasswordField", false);
   public static final Region POPUP_MENU = new Region("PopupMenu", false);
   public static final Region POPUP_MENU_SEPARATOR = new Region("PopupMenuSeparator", false);
   public static final Region PROGRESS_BAR = new Region("ProgressBar", false);
   public static final Region RADIO_BUTTON = new Region("RadioButton", false);
   public static final Region RADIO_BUTTON_MENU_ITEM = new Region("RadioButtonMenuItem", false);
   public static final Region ROOT_PANE = new Region("RootPane", false);
   public static final Region SCROLL_BAR = new Region("ScrollBar", false);
   public static final Region SCROLL_BAR_TRACK = new Region("ScrollBarTrack", true);
   public static final Region SCROLL_BAR_THUMB = new Region("ScrollBarThumb", true);
   public static final Region SCROLL_PANE = new Region("ScrollPane", false);
   public static final Region SEPARATOR = new Region("Separator", false);
   public static final Region SLIDER = new Region("Slider", false);
   public static final Region SLIDER_TRACK = new Region("SliderTrack", true);
   public static final Region SLIDER_THUMB = new Region("SliderThumb", true);
   public static final Region SPINNER = new Region("Spinner", false);
   public static final Region SPLIT_PANE = new Region("SplitPane", false);
   public static final Region SPLIT_PANE_DIVIDER = new Region("SplitPaneDivider", true);
   public static final Region TABBED_PANE = new Region("TabbedPane", false);
   public static final Region TABBED_PANE_TAB = new Region("TabbedPaneTab", true);
   public static final Region TABBED_PANE_TAB_AREA = new Region("TabbedPaneTabArea", true);
   public static final Region TABBED_PANE_CONTENT = new Region("TabbedPaneContent", true);
   public static final Region TABLE = new Region("Table", false);
   public static final Region TABLE_HEADER = new Region("TableHeader", false);
   public static final Region TEXT_AREA = new Region("TextArea", false);
   public static final Region TEXT_FIELD = new Region("TextField", false);
   public static final Region TEXT_PANE = new Region("TextPane", false);
   public static final Region TOGGLE_BUTTON = new Region("ToggleButton", false);
   public static final Region TOOL_BAR = new Region("ToolBar", false);
   public static final Region TOOL_BAR_CONTENT = new Region("ToolBarContent", true);
   public static final Region TOOL_BAR_DRAG_WINDOW = new Region("ToolBarDragWindow", false);
   public static final Region TOOL_TIP = new Region("ToolTip", false);
   public static final Region TOOL_BAR_SEPARATOR = new Region("ToolBarSeparator", false);
   public static final Region TREE = new Region("Tree", false);
   public static final Region TREE_CELL = new Region("TreeCell", true);
   public static final Region VIEWPORT = new Region("Viewport", false);
   private final String name;
   private final boolean subregion;

   private static Map<String, Region> getUItoRegionMap() {
      AppContext var0 = AppContext.getAppContext();
      Object var1 = (Map)var0.get(UI_TO_REGION_MAP_KEY);
      if (var1 == null) {
         var1 = new HashMap();
         ((Map)var1).put("ArrowButtonUI", ARROW_BUTTON);
         ((Map)var1).put("ButtonUI", BUTTON);
         ((Map)var1).put("CheckBoxUI", CHECK_BOX);
         ((Map)var1).put("CheckBoxMenuItemUI", CHECK_BOX_MENU_ITEM);
         ((Map)var1).put("ColorChooserUI", COLOR_CHOOSER);
         ((Map)var1).put("ComboBoxUI", COMBO_BOX);
         ((Map)var1).put("DesktopPaneUI", DESKTOP_PANE);
         ((Map)var1).put("DesktopIconUI", DESKTOP_ICON);
         ((Map)var1).put("EditorPaneUI", EDITOR_PANE);
         ((Map)var1).put("FileChooserUI", FILE_CHOOSER);
         ((Map)var1).put("FormattedTextFieldUI", FORMATTED_TEXT_FIELD);
         ((Map)var1).put("InternalFrameUI", INTERNAL_FRAME);
         ((Map)var1).put("InternalFrameTitlePaneUI", INTERNAL_FRAME_TITLE_PANE);
         ((Map)var1).put("LabelUI", LABEL);
         ((Map)var1).put("ListUI", LIST);
         ((Map)var1).put("MenuUI", MENU);
         ((Map)var1).put("MenuBarUI", MENU_BAR);
         ((Map)var1).put("MenuItemUI", MENU_ITEM);
         ((Map)var1).put("OptionPaneUI", OPTION_PANE);
         ((Map)var1).put("PanelUI", PANEL);
         ((Map)var1).put("PasswordFieldUI", PASSWORD_FIELD);
         ((Map)var1).put("PopupMenuUI", POPUP_MENU);
         ((Map)var1).put("PopupMenuSeparatorUI", POPUP_MENU_SEPARATOR);
         ((Map)var1).put("ProgressBarUI", PROGRESS_BAR);
         ((Map)var1).put("RadioButtonUI", RADIO_BUTTON);
         ((Map)var1).put("RadioButtonMenuItemUI", RADIO_BUTTON_MENU_ITEM);
         ((Map)var1).put("RootPaneUI", ROOT_PANE);
         ((Map)var1).put("ScrollBarUI", SCROLL_BAR);
         ((Map)var1).put("ScrollPaneUI", SCROLL_PANE);
         ((Map)var1).put("SeparatorUI", SEPARATOR);
         ((Map)var1).put("SliderUI", SLIDER);
         ((Map)var1).put("SpinnerUI", SPINNER);
         ((Map)var1).put("SplitPaneUI", SPLIT_PANE);
         ((Map)var1).put("TabbedPaneUI", TABBED_PANE);
         ((Map)var1).put("TableUI", TABLE);
         ((Map)var1).put("TableHeaderUI", TABLE_HEADER);
         ((Map)var1).put("TextAreaUI", TEXT_AREA);
         ((Map)var1).put("TextFieldUI", TEXT_FIELD);
         ((Map)var1).put("TextPaneUI", TEXT_PANE);
         ((Map)var1).put("ToggleButtonUI", TOGGLE_BUTTON);
         ((Map)var1).put("ToolBarUI", TOOL_BAR);
         ((Map)var1).put("ToolTipUI", TOOL_TIP);
         ((Map)var1).put("ToolBarSeparatorUI", TOOL_BAR_SEPARATOR);
         ((Map)var1).put("TreeUI", TREE);
         ((Map)var1).put("ViewportUI", VIEWPORT);
         var0.put(UI_TO_REGION_MAP_KEY, var1);
      }

      return (Map)var1;
   }

   private static Map<Region, String> getLowerCaseNameMap() {
      AppContext var0 = AppContext.getAppContext();
      Object var1 = (Map)var0.get(LOWER_CASE_NAME_MAP_KEY);
      if (var1 == null) {
         var1 = new HashMap();
         var0.put(LOWER_CASE_NAME_MAP_KEY, var1);
      }

      return (Map)var1;
   }

   static Region getRegion(JComponent var0) {
      return (Region)getUItoRegionMap().get(var0.getUIClassID());
   }

   static void registerUIs(UIDefaults var0) {
      Iterator var1 = getUItoRegionMap().keySet().iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         var0.put(var2, "javax.swing.plaf.synth.SynthLookAndFeel");
      }

   }

   private Region(String var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException("You must specify a non-null name");
      } else {
         this.name = var1;
         this.subregion = var2;
      }
   }

   protected Region(String var1, String var2, boolean var3) {
      this(var1, var3);
      if (var2 != null) {
         getUItoRegionMap().put(var2, this);
      }

   }

   public boolean isSubregion() {
      return this.subregion;
   }

   public String getName() {
      return this.name;
   }

   String getLowerCaseName() {
      Map var1 = getLowerCaseNameMap();
      String var2 = (String)var1.get(this);
      if (var2 == null) {
         var2 = this.name.toLowerCase(Locale.ENGLISH);
         var1.put(this, var2);
      }

      return var2;
   }

   public String toString() {
      return this.name;
   }
}
