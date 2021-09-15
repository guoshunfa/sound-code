package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyle;
import sun.font.FontUtilities;
import sun.swing.plaf.synth.DefaultSynthStyle;

final class NimbusDefaults {
   private Map<Region, List<NimbusDefaults.LazyStyle>> m = new HashMap();
   private Map<String, Region> registeredRegions = new HashMap();
   private Map<JComponent, Map<Region, SynthStyle>> overridesCache = new WeakHashMap();
   private DefaultSynthStyle defaultStyle = new DefaultSynthStyle();
   private FontUIResource defaultFont = FontUtilities.getFontConfigFUIR("sans", 0, 12);
   private NimbusDefaults.ColorTree colorTree = new NimbusDefaults.ColorTree();
   private NimbusDefaults.DefaultsListener defaultsListener = new NimbusDefaults.DefaultsListener();
   private Map<DerivedColor, DerivedColor> derivedColors = new HashMap();

   void initialize() {
      UIManager.addPropertyChangeListener(this.defaultsListener);
      UIManager.getDefaults().addPropertyChangeListener(this.colorTree);
   }

   void uninitialize() {
      UIManager.removePropertyChangeListener(this.defaultsListener);
      UIManager.getDefaults().removePropertyChangeListener(this.colorTree);
   }

   NimbusDefaults() {
      this.defaultStyle.setFont(this.defaultFont);
      this.register(Region.ARROW_BUTTON, "ArrowButton");
      this.register(Region.BUTTON, "Button");
      this.register(Region.TOGGLE_BUTTON, "ToggleButton");
      this.register(Region.RADIO_BUTTON, "RadioButton");
      this.register(Region.CHECK_BOX, "CheckBox");
      this.register(Region.COLOR_CHOOSER, "ColorChooser");
      this.register(Region.PANEL, "ColorChooser:\"ColorChooser.previewPanelHolder\"");
      this.register(Region.LABEL, "ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\"");
      this.register(Region.COMBO_BOX, "ComboBox");
      this.register(Region.TEXT_FIELD, "ComboBox:\"ComboBox.textField\"");
      this.register(Region.ARROW_BUTTON, "ComboBox:\"ComboBox.arrowButton\"");
      this.register(Region.LABEL, "ComboBox:\"ComboBox.listRenderer\"");
      this.register(Region.LABEL, "ComboBox:\"ComboBox.renderer\"");
      this.register(Region.SCROLL_PANE, "\"ComboBox.scrollPane\"");
      this.register(Region.FILE_CHOOSER, "FileChooser");
      this.register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrameTitlePane");
      this.register(Region.INTERNAL_FRAME, "InternalFrame");
      this.register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrame:InternalFrameTitlePane");
      this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"");
      this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"");
      this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"");
      this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"");
      this.register(Region.DESKTOP_ICON, "DesktopIcon");
      this.register(Region.DESKTOP_PANE, "DesktopPane");
      this.register(Region.LABEL, "Label");
      this.register(Region.LIST, "List");
      this.register(Region.LABEL, "List:\"List.cellRenderer\"");
      this.register(Region.MENU_BAR, "MenuBar");
      this.register(Region.MENU, "MenuBar:Menu");
      this.register(Region.MENU_ITEM_ACCELERATOR, "MenuBar:Menu:MenuItemAccelerator");
      this.register(Region.MENU_ITEM, "MenuItem");
      this.register(Region.MENU_ITEM_ACCELERATOR, "MenuItem:MenuItemAccelerator");
      this.register(Region.RADIO_BUTTON_MENU_ITEM, "RadioButtonMenuItem");
      this.register(Region.MENU_ITEM_ACCELERATOR, "RadioButtonMenuItem:MenuItemAccelerator");
      this.register(Region.CHECK_BOX_MENU_ITEM, "CheckBoxMenuItem");
      this.register(Region.MENU_ITEM_ACCELERATOR, "CheckBoxMenuItem:MenuItemAccelerator");
      this.register(Region.MENU, "Menu");
      this.register(Region.MENU_ITEM_ACCELERATOR, "Menu:MenuItemAccelerator");
      this.register(Region.POPUP_MENU, "PopupMenu");
      this.register(Region.POPUP_MENU_SEPARATOR, "PopupMenuSeparator");
      this.register(Region.OPTION_PANE, "OptionPane");
      this.register(Region.SEPARATOR, "OptionPane:\"OptionPane.separator\"");
      this.register(Region.PANEL, "OptionPane:\"OptionPane.messageArea\"");
      this.register(Region.LABEL, "OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"");
      this.register(Region.PANEL, "Panel");
      this.register(Region.PROGRESS_BAR, "ProgressBar");
      this.register(Region.SEPARATOR, "Separator");
      this.register(Region.SCROLL_BAR, "ScrollBar");
      this.register(Region.ARROW_BUTTON, "ScrollBar:\"ScrollBar.button\"");
      this.register(Region.SCROLL_BAR_THUMB, "ScrollBar:ScrollBarThumb");
      this.register(Region.SCROLL_BAR_TRACK, "ScrollBar:ScrollBarTrack");
      this.register(Region.SCROLL_PANE, "ScrollPane");
      this.register(Region.VIEWPORT, "Viewport");
      this.register(Region.SLIDER, "Slider");
      this.register(Region.SLIDER_THUMB, "Slider:SliderThumb");
      this.register(Region.SLIDER_TRACK, "Slider:SliderTrack");
      this.register(Region.SPINNER, "Spinner");
      this.register(Region.PANEL, "Spinner:\"Spinner.editor\"");
      this.register(Region.FORMATTED_TEXT_FIELD, "Spinner:Panel:\"Spinner.formattedTextField\"");
      this.register(Region.ARROW_BUTTON, "Spinner:\"Spinner.previousButton\"");
      this.register(Region.ARROW_BUTTON, "Spinner:\"Spinner.nextButton\"");
      this.register(Region.SPLIT_PANE, "SplitPane");
      this.register(Region.SPLIT_PANE_DIVIDER, "SplitPane:SplitPaneDivider");
      this.register(Region.TABBED_PANE, "TabbedPane");
      this.register(Region.TABBED_PANE_TAB, "TabbedPane:TabbedPaneTab");
      this.register(Region.TABBED_PANE_TAB_AREA, "TabbedPane:TabbedPaneTabArea");
      this.register(Region.TABBED_PANE_CONTENT, "TabbedPane:TabbedPaneContent");
      this.register(Region.TABLE, "Table");
      this.register(Region.LABEL, "Table:\"Table.cellRenderer\"");
      this.register(Region.TABLE_HEADER, "TableHeader");
      this.register(Region.LABEL, "TableHeader:\"TableHeader.renderer\"");
      this.register(Region.TEXT_FIELD, "\"Table.editor\"");
      this.register(Region.TEXT_FIELD, "\"Tree.cellEditor\"");
      this.register(Region.TEXT_FIELD, "TextField");
      this.register(Region.FORMATTED_TEXT_FIELD, "FormattedTextField");
      this.register(Region.PASSWORD_FIELD, "PasswordField");
      this.register(Region.TEXT_AREA, "TextArea");
      this.register(Region.TEXT_PANE, "TextPane");
      this.register(Region.EDITOR_PANE, "EditorPane");
      this.register(Region.TOOL_BAR, "ToolBar");
      this.register(Region.BUTTON, "ToolBar:Button");
      this.register(Region.TOGGLE_BUTTON, "ToolBar:ToggleButton");
      this.register(Region.TOOL_BAR_SEPARATOR, "ToolBarSeparator");
      this.register(Region.TOOL_TIP, "ToolTip");
      this.register(Region.TREE, "Tree");
      this.register(Region.TREE_CELL, "Tree:TreeCell");
      this.register(Region.LABEL, "Tree:\"Tree.cellRenderer\"");
      this.register(Region.ROOT_PANE, "RootPane");
   }

   void initializeDefaults(UIDefaults var1) {
      this.addColor(var1, "text", 0, 0, 0, 255);
      this.addColor(var1, "control", 214, 217, 223, 255);
      this.addColor(var1, "nimbusBase", 51, 98, 140, 255);
      this.addColor(var1, "nimbusBlueGrey", "nimbusBase", 0.032459438F, -0.52518797F, 0.19607842F, 0);
      this.addColor(var1, "nimbusOrange", 191, 98, 4, 255);
      this.addColor(var1, "nimbusGreen", 176, 179, 50, 255);
      this.addColor(var1, "nimbusRed", 169, 46, 34, 255);
      this.addColor(var1, "nimbusBorder", "nimbusBlueGrey", 0.0F, -0.017358616F, -0.11372548F, 0);
      this.addColor(var1, "nimbusSelection", "nimbusBase", -0.010750473F, -0.04875779F, -0.007843137F, 0);
      this.addColor(var1, "nimbusInfoBlue", 47, 92, 180, 255);
      this.addColor(var1, "nimbusAlertYellow", 255, 220, 35, 255);
      this.addColor(var1, "nimbusFocus", 115, 164, 209, 255);
      this.addColor(var1, "nimbusSelectedText", 255, 255, 255, 255);
      this.addColor(var1, "nimbusSelectionBackground", 57, 105, 138, 255);
      this.addColor(var1, "nimbusDisabledText", 142, 143, 145, 255);
      this.addColor(var1, "nimbusLightBackground", 255, 255, 255, 255);
      this.addColor(var1, "infoText", "text", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "info", 242, 242, 189, 255);
      this.addColor(var1, "menuText", "text", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "menu", "nimbusBase", 0.021348298F, -0.6150531F, 0.39999998F, 0);
      this.addColor(var1, "scrollbar", "nimbusBlueGrey", -0.006944418F, -0.07296763F, 0.09019607F, 0);
      this.addColor(var1, "controlText", "text", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "controlHighlight", "nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
      this.addColor(var1, "controlLHighlight", "nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
      this.addColor(var1, "controlShadow", "nimbusBlueGrey", -0.0027777553F, -0.0212406F, 0.13333333F, 0);
      this.addColor(var1, "controlDkShadow", "nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
      this.addColor(var1, "textHighlight", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "textHighlightText", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "textInactiveText", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "desktop", "nimbusBase", -0.009207249F, -0.13984653F, -0.07450983F, 0);
      this.addColor(var1, "activeCaption", "nimbusBlueGrey", 0.0F, -0.049920253F, 0.031372547F, 0);
      this.addColor(var1, "inactiveCaption", "nimbusBlueGrey", -0.00505054F, -0.055526316F, 0.039215684F, 0);
      var1.put("defaultFont", new FontUIResource(this.defaultFont));
      var1.put("InternalFrame.titleFont", new NimbusDefaults.DerivedFont("defaultFont", 1.0F, true, (Boolean)null));
      this.addColor(var1, "textForeground", "text", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "background", "control", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TitledBorder.position", "ABOVE_TOP");
      var1.put("FileView.fullRowSelection", Boolean.TRUE);
      var1.put("ArrowButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ArrowButton.size", new Integer(16));
      var1.put("ArrowButton[Disabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ArrowButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ArrowButton[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ArrowButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Button.contentMargins", new InsetsUIResource(6, 14, 6, 14));
      var1.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
      var1.put("Button[Default].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 1, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Default+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 2, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Default+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 3, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Default+Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 4, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      this.addColor(var1, "Button[Default+Pressed].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Button[Default+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 5, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Default+Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 6, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      this.addColor(var1, "Button[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Button[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 7, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 8, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 9, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 10, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 11, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 12, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Button[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 13, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton.contentMargins", new InsetsUIResource(6, 14, 6, 14));
      this.addColor(var1, "ToggleButton[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ToggleButton[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 1, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 2, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 3, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 4, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 5, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 6, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 7, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 8, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Focused+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 9, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Pressed+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 10, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Focused+Pressed+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 11, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 12, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ToggleButton[Focused+MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 13, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      this.addColor(var1, "ToggleButton[Disabled+Selected].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ToggleButton[Disabled+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 14, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("RadioButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "RadioButton[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("RadioButton[Disabled].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Enabled].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Focused].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[MouseOver].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Focused+MouseOver].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Pressed].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Focused+Pressed].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Focused+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 11, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Pressed+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 12, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Focused+Pressed+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 13, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[MouseOver+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 14, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Focused+MouseOver+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 15, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton[Disabled+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 16, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButton.icon", new NimbusIcon("RadioButton", "iconPainter", 18, 18));
      var1.put("CheckBox.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "CheckBox[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("CheckBox[Disabled].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 3, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Enabled].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 4, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Focused].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 5, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[MouseOver].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 6, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Focused+MouseOver].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Pressed].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Focused+Pressed].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Focused+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 11, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Pressed+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 12, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Focused+Pressed+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 13, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[MouseOver+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 14, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Focused+MouseOver+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 15, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox[Disabled+Selected].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 16, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBox.icon", new NimbusIcon("CheckBox", "iconPainter", 18, 18));
      var1.put("ColorChooser.contentMargins", new InsetsUIResource(5, 0, 0, 0));
      this.addColor(var1, "ColorChooser.swatchesDefaultRecentColor", 255, 255, 255, 255);
      var1.put("ColorChooser:\"ColorChooser.previewPanelHolder\".contentMargins", new InsetsUIResource(0, 5, 10, 5));
      var1.put("ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\".contentMargins", new InsetsUIResource(0, 10, 10, 10));
      var1.put("ComboBox.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ComboBox.States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,Editable");
      var1.put("ComboBox.Editable", new ComboBoxEditableState());
      var1.put("ComboBox.forceOpaque", Boolean.TRUE);
      var1.put("ComboBox.buttonWhenNotEditable", Boolean.TRUE);
      var1.put("ComboBox.rendererUseListColors", Boolean.FALSE);
      var1.put("ComboBox.pressedWhenPopupVisible", Boolean.TRUE);
      var1.put("ComboBox.squareButton", Boolean.FALSE);
      var1.put("ComboBox.popupInsets", new InsetsUIResource(-2, 2, 0, 2));
      var1.put("ComboBox.padding", new InsetsUIResource(3, 3, 3, 3));
      var1.put("ComboBox[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 1, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Disabled+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 2, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 3, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 4, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 5, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 6, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 7, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 8, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Enabled+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 9, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Disabled+Editable].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 10, new Insets(6, 5, 6, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Editable+Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 11, new Insets(6, 5, 6, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Editable+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 12, new Insets(5, 5, 5, 5), new Dimension(142, 27), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Editable+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 13, new Insets(4, 5, 5, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox[Editable+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 14, new Insets(4, 5, 5, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.textField\".contentMargins", new InsetsUIResource(0, 6, 0, 3));
      this.addColor(var1, "ComboBox:\"ComboBox.textField\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ComboBox:\"ComboBox.textField\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 1, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 2, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      this.addColor(var1, "ComboBox:\"ComboBox.textField\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 3, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ComboBox:\"ComboBox.arrowButton\".States", "Enabled,MouseOver,Pressed,Disabled,Editable");
      var1.put("ComboBox:\"ComboBox.arrowButton\".Editable", new ComboBoxArrowButtonEditableState());
      var1.put("ComboBox:\"ComboBox.arrowButton\".size", new Integer(19));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Disabled+Editable].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 5, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 6, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 7, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 8, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 9, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 10, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 11, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Disabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 12, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 13, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 14, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ComboBox:\"ComboBox.listRenderer\".contentMargins", new InsetsUIResource(2, 4, 2, 4));
      var1.put("ComboBox:\"ComboBox.listRenderer\".opaque", Boolean.TRUE);
      this.addColor(var1, "ComboBox:\"ComboBox.listRenderer\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "ComboBox:\"ComboBox.listRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "ComboBox:\"ComboBox.listRenderer\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "ComboBox:\"ComboBox.listRenderer\"[Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ComboBox:\"ComboBox.renderer\".contentMargins", new InsetsUIResource(2, 4, 2, 4));
      this.addColor(var1, "ComboBox:\"ComboBox.renderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "ComboBox:\"ComboBox.renderer\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "ComboBox:\"ComboBox.renderer\"[Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("\"ComboBox.scrollPane\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("FileChooser.contentMargins", new InsetsUIResource(10, 10, 10, 10));
      var1.put("FileChooser.opaque", Boolean.TRUE);
      var1.put("FileChooser.usesSingleFilePane", Boolean.TRUE);
      var1.put("FileChooser[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 1, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("FileChooser[Enabled].fileIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 2, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.fileIcon", new NimbusIcon("FileChooser", "fileIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].directoryIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 3, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.directoryIcon", new NimbusIcon("FileChooser", "directoryIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].upFolderIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 4, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.upFolderIcon", new NimbusIcon("FileChooser", "upFolderIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].newFolderIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 5, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.newFolderIcon", new NimbusIcon("FileChooser", "newFolderIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].hardDriveIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 7, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.hardDriveIcon", new NimbusIcon("FileChooser", "hardDriveIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].floppyDriveIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 8, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.floppyDriveIcon", new NimbusIcon("FileChooser", "floppyDriveIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].homeFolderIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 9, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.homeFolderIcon", new NimbusIcon("FileChooser", "homeFolderIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].detailsViewIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 10, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.detailsViewIcon", new NimbusIcon("FileChooser", "detailsViewIconPainter", 16, 16));
      var1.put("FileChooser[Enabled].listViewIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 11, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("FileChooser.listViewIcon", new NimbusIcon("FileChooser", "listViewIconPainter", 16, 16));
      var1.put("InternalFrameTitlePane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("InternalFrameTitlePane.maxFrameIconSize", new DimensionUIResource(18, 18));
      var1.put("InternalFrame.contentMargins", new InsetsUIResource(1, 6, 6, 6));
      var1.put("InternalFrame.States", "Enabled,WindowFocused");
      var1.put("InternalFrame.WindowFocused", new InternalFrameWindowFocusedState());
      var1.put("InternalFrame[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFramePainter", 1, new Insets(25, 6, 6, 6), new Dimension(25, 36), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame[Enabled+WindowFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFramePainter", 2, new Insets(25, 6, 6, 6), new Dimension(25, 36), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane.contentMargins", new InsetsUIResource(3, 0, 3, 0));
      var1.put("InternalFrame:InternalFrameTitlePane.States", "Enabled,WindowFocused");
      var1.put("InternalFrame:InternalFrameTitlePane.WindowFocused", new InternalFrameTitlePaneWindowFocusedState());
      var1.put("InternalFrame:InternalFrameTitlePane.titleAlignment", "CENTER");
      this.addColor(var1, "InternalFrame:InternalFrameTitlePane[Enabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".WindowNotFocused", new InternalFrameTitlePaneMenuButtonWindowNotFocusedState());
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".test", "am InternalFrameTitlePane.menuButton");
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Enabled].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Disabled].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[MouseOver].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Pressed].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Enabled+WindowNotFocused].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[MouseOver+WindowNotFocused].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Pressed+WindowNotFocused].iconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".icon", new NimbusIcon("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"", "iconPainter", 19, 18));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".WindowNotFocused", new InternalFrameTitlePaneIconifyButtonWindowNotFocusedState());
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused,WindowMaximized");
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".WindowNotFocused", new InternalFrameTitlePaneMaximizeButtonWindowNotFocusedState());
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".WindowMaximized", new InternalFrameTitlePaneMaximizeButtonWindowMaximizedState());
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled+WindowMaximized].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 8, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 9, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 10, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 11, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 12, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 13, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 14, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".WindowNotFocused", new InternalFrameTitlePaneCloseButtonWindowNotFocusedState());
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed+WindowNotFocused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("DesktopIcon.contentMargins", new InsetsUIResource(4, 6, 5, 4));
      var1.put("DesktopIcon[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.DesktopIconPainter", 1, new Insets(5, 5, 5, 5), new Dimension(28, 26), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("DesktopPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("DesktopPane.opaque", Boolean.TRUE);
      var1.put("DesktopPane[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.DesktopPanePainter", 1, new Insets(0, 0, 0, 0), new Dimension(300, 232), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("Label.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "Label[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("List.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("List.opaque", Boolean.TRUE);
      this.addColor(var1, "List.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("List.rendererUseListColors", Boolean.FALSE);
      var1.put("List.rendererUseUIBorder", Boolean.TRUE);
      var1.put("List.cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
      var1.put("List.focusCellHighlightBorder", new BorderUIResource(new NimbusDefaults.PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", new Insets(2, 5, 2, 5))));
      this.addColor(var1, "List.dropLineColor", "nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List[Selected].textForeground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List[Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List[Disabled+Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("List:\"List.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("List:\"List.cellRenderer\".opaque", Boolean.TRUE);
      this.addColor(var1, "List:\"List.cellRenderer\"[Selected].textForeground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List:\"List.cellRenderer\"[Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List:\"List.cellRenderer\"[Disabled+Selected].background", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "List:\"List.cellRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("MenuBar.contentMargins", new InsetsUIResource(2, 6, 2, 6));
      var1.put("MenuBar[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuBarPainter", 1, new Insets(1, 0, 0, 0), new Dimension(18, 22), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("MenuBar[Enabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuBarPainter", 2, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("MenuBar:Menu.contentMargins", new InsetsUIResource(1, 4, 2, 4));
      this.addColor(var1, "MenuBar:Menu[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "MenuBar:Menu[Enabled].textForeground", 35, 35, 36, 255);
      this.addColor(var1, "MenuBar:Menu[Selected].textForeground", 255, 255, 255, 255);
      var1.put("MenuBar:Menu[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuBarMenuPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("MenuBar:Menu:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("MenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
      var1.put("MenuItem.textIconGap", new Integer(5));
      this.addColor(var1, "MenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "MenuItem[Enabled].textForeground", 35, 35, 36, 255);
      this.addColor(var1, "MenuItem[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("MenuItem[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("MenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "MenuItem:MenuItemAccelerator[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "MenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("RadioButtonMenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
      var1.put("RadioButtonMenuItem.textIconGap", new Integer(5));
      this.addColor(var1, "RadioButtonMenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "RadioButtonMenuItem[Enabled].textForeground", 35, 35, 36, 255);
      this.addColor(var1, "RadioButtonMenuItem[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("RadioButtonMenuItem[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "RadioButtonMenuItem[MouseOver+Selected].textForeground", 255, 255, 255, 255);
      var1.put("RadioButtonMenuItem[MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 4, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("RadioButtonMenuItem[Disabled+Selected].checkIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButtonMenuItem[Enabled+Selected].checkIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 6, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButtonMenuItem[MouseOver+Selected].checkIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 7, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("RadioButtonMenuItem.checkIcon", new NimbusIcon("RadioButtonMenuItem", "checkIconPainter", 9, 10));
      var1.put("RadioButtonMenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "RadioButtonMenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("CheckBoxMenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
      var1.put("CheckBoxMenuItem.textIconGap", new Integer(5));
      this.addColor(var1, "CheckBoxMenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "CheckBoxMenuItem[Enabled].textForeground", 35, 35, 36, 255);
      this.addColor(var1, "CheckBoxMenuItem[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("CheckBoxMenuItem[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "CheckBoxMenuItem[MouseOver+Selected].textForeground", 255, 255, 255, 255);
      var1.put("CheckBoxMenuItem[MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 4, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("CheckBoxMenuItem[Disabled+Selected].checkIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBoxMenuItem[Enabled+Selected].checkIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 6, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBoxMenuItem[MouseOver+Selected].checkIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 7, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("CheckBoxMenuItem.checkIcon", new NimbusIcon("CheckBoxMenuItem", "checkIconPainter", 9, 10));
      var1.put("CheckBoxMenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "CheckBoxMenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("Menu.contentMargins", new InsetsUIResource(1, 12, 2, 5));
      var1.put("Menu.textIconGap", new Integer(5));
      this.addColor(var1, "Menu[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "Menu[Enabled].textForeground", 35, 35, 36, 255);
      this.addColor(var1, "Menu[Enabled+Selected].textForeground", 255, 255, 255, 255);
      var1.put("Menu[Enabled+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("Menu[Disabled].arrowIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 4, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Menu[Enabled].arrowIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Menu[Enabled+Selected].arrowIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 6, new Insets(1, 1, 1, 1), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Menu.arrowIcon", new NimbusIcon("Menu", "arrowIconPainter", 9, 10));
      var1.put("Menu:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "Menu:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
      var1.put("PopupMenu.contentMargins", new InsetsUIResource(6, 1, 6, 1));
      var1.put("PopupMenu.opaque", Boolean.TRUE);
      var1.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
      var1.put("PopupMenu[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PopupMenuPainter", 1, new Insets(9, 0, 11, 0), new Dimension(220, 313), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("PopupMenu[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PopupMenuPainter", 2, new Insets(11, 2, 11, 2), new Dimension(220, 313), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("PopupMenuSeparator.contentMargins", new InsetsUIResource(1, 0, 2, 0));
      var1.put("PopupMenuSeparator[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PopupMenuSeparatorPainter", 1, new Insets(1, 1, 1, 1), new Dimension(3, 3), true, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("OptionPane.contentMargins", new InsetsUIResource(15, 15, 15, 15));
      var1.put("OptionPane.opaque", Boolean.TRUE);
      var1.put("OptionPane.buttonOrientation", new Integer(4));
      var1.put("OptionPane.messageAnchor", new Integer(17));
      var1.put("OptionPane.separatorPadding", new Integer(0));
      var1.put("OptionPane.sameSizeButtons", Boolean.FALSE);
      var1.put("OptionPane:\"OptionPane.separator\".contentMargins", new InsetsUIResource(1, 0, 0, 0));
      var1.put("OptionPane:\"OptionPane.messageArea\".contentMargins", new InsetsUIResource(0, 0, 10, 0));
      var1.put("OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\".contentMargins", new InsetsUIResource(0, 10, 10, 10));
      var1.put("OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.OptionPaneMessageAreaOptionPaneLabelPainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("OptionPane[Enabled].errorIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 2, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("OptionPane.errorIcon", new NimbusIcon("OptionPane", "errorIconPainter", 48, 48));
      var1.put("OptionPane[Enabled].informationIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 3, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("OptionPane.informationIcon", new NimbusIcon("OptionPane", "informationIconPainter", 48, 48));
      var1.put("OptionPane[Enabled].questionIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 4, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("OptionPane.questionIcon", new NimbusIcon("OptionPane", "questionIconPainter", 48, 48));
      var1.put("OptionPane[Enabled].warningIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 5, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("OptionPane.warningIcon", new NimbusIcon("OptionPane", "warningIconPainter", 48, 48));
      var1.put("Panel.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Panel.opaque", Boolean.TRUE);
      var1.put("ProgressBar.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ProgressBar.States", "Enabled,Disabled,Indeterminate,Finished");
      var1.put("ProgressBar.Indeterminate", new ProgressBarIndeterminateState());
      var1.put("ProgressBar.Finished", new ProgressBarFinishedState());
      var1.put("ProgressBar.tileWhenIndeterminate", Boolean.TRUE);
      var1.put("ProgressBar.tileWidth", new Integer(27));
      var1.put("ProgressBar.paintOutsideClip", Boolean.TRUE);
      var1.put("ProgressBar.rotateText", Boolean.TRUE);
      var1.put("ProgressBar.vertictalSize", new DimensionUIResource(19, 150));
      var1.put("ProgressBar.horizontalSize", new DimensionUIResource(150, 19));
      var1.put("ProgressBar.cycleTime", new Integer(250));
      var1.put("ProgressBar.minBarSize", new DimensionUIResource(6, 6));
      var1.put("ProgressBar.glowWidth", new Integer(2));
      var1.put("ProgressBar[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 1, new Insets(5, 5, 5, 5), new Dimension(29, 19), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      this.addColor(var1, "ProgressBar[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ProgressBar[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 2, new Insets(5, 5, 5, 5), new Dimension(29, 19), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ProgressBar[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 3, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ProgressBar[Enabled+Finished].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 4, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ProgressBar[Enabled+Indeterminate].progressPadding", new Integer(3));
      var1.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 5, new Insets(3, 3, 3, 3), new Dimension(30, 13), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ProgressBar[Disabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 6, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ProgressBar[Disabled+Finished].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 7, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ProgressBar[Disabled+Indeterminate].progressPadding", new Integer(3));
      var1.put("ProgressBar[Disabled+Indeterminate].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 8, new Insets(3, 3, 3, 3), new Dimension(30, 13), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Separator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Separator[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SeparatorPainter", 1, new Insets(0, 40, 0, 40), new Dimension(100, 3), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ScrollBar.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ScrollBar.opaque", Boolean.TRUE);
      var1.put("ScrollBar.incrementButtonGap", new Integer(-8));
      var1.put("ScrollBar.decrementButtonGap", new Integer(-8));
      var1.put("ScrollBar.thumbHeight", new Integer(15));
      var1.put("ScrollBar.minimumThumbSize", new DimensionUIResource(29, 29));
      var1.put("ScrollBar.maximumThumbSize", new DimensionUIResource(1000, 1000));
      var1.put("ScrollBar:\"ScrollBar.button\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ScrollBar:\"ScrollBar.button\".size", new Integer(25));
      var1.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 1, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ScrollBar:\"ScrollBar.button\"[Disabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 2, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 3, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 4, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ScrollBar:ScrollBarThumb.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 2, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 4, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 5, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ScrollBar:ScrollBarTrack.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("ScrollBar:ScrollBarTrack[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarTrackPainter", 1, new Insets(5, 5, 5, 5), new Dimension(18, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollBarTrackPainter", 2, new Insets(5, 10, 5, 9), new Dimension(34, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("ScrollPane.contentMargins", new InsetsUIResource(3, 3, 3, 3));
      var1.put("ScrollPane.useChildTextComponentFocus", Boolean.TRUE);
      var1.put("ScrollPane[Enabled+Focused].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("ScrollPane[Enabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ScrollPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("Viewport.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Viewport.opaque", Boolean.TRUE);
      var1.put("Slider.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Slider.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
      var1.put("Slider.ArrowShape", new SliderArrowShapeState());
      var1.put("Slider.thumbWidth", new Integer(17));
      var1.put("Slider.thumbHeight", new Integer(17));
      var1.put("Slider.trackBorder", new Integer(0));
      var1.put("Slider.paintValue", Boolean.FALSE);
      this.addColor(var1, "Slider.tickColor", 35, 40, 48, 255);
      var1.put("Slider:SliderThumb.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Slider:SliderThumb.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
      var1.put("Slider:SliderThumb.ArrowShape", new SliderThumbArrowShapeState());
      var1.put("Slider:SliderThumb[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 1, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 2, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 3, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 4, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 5, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 6, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 7, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 8, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 9, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 10, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 11, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 12, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 13, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderThumb[ArrowShape+Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 14, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Slider:SliderTrack.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Slider:SliderTrack.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
      var1.put("Slider:SliderTrack.ArrowShape", new SliderTrackArrowShapeState());
      var1.put("Slider:SliderTrack[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderTrackPainter", 1, new Insets(6, 5, 6, 5), new Dimension(23, 17), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0D));
      var1.put("Slider:SliderTrack[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SliderTrackPainter", 2, new Insets(6, 5, 6, 5), new Dimension(23, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Spinner:\"Spinner.editor\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Spinner:Panel:\"Spinner.formattedTextField\".contentMargins", new InsetsUIResource(6, 6, 5, 6));
      this.addColor(var1, "Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 1, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 2, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 3, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "Spinner:Panel:\"Spinner.formattedTextField\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Spinner:Panel:\"Spinner.formattedTextField\"[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 4, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 5, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("Spinner:\"Spinner.previousButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Spinner:\"Spinner.previousButton\".size", new Integer(20));
      var1.put("Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 1, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 2, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 3, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 4, new Insets(3, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 5, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 6, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 7, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Disabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 8, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 9, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Focused].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 10, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 11, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 12, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[MouseOver].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 13, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.previousButton\"[Pressed].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 14, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Spinner:\"Spinner.nextButton\".size", new Integer(20));
      var1.put("Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 1, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 2, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 3, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 4, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 5, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 6, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 7, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Disabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 8, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 9, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Focused].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 10, new Insets(3, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 11, new Insets(3, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 12, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[MouseOver].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 13, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Spinner:\"Spinner.nextButton\"[Pressed].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 14, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("SplitPane.contentMargins", new InsetsUIResource(1, 1, 1, 1));
      var1.put("SplitPane.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
      var1.put("SplitPane.Vertical", new SplitPaneVerticalState());
      var1.put("SplitPane.size", new Integer(10));
      var1.put("SplitPane.dividerSize", new Integer(10));
      var1.put("SplitPane.centerOneTouchButtons", Boolean.TRUE);
      var1.put("SplitPane.oneTouchButtonOffset", new Integer(30));
      var1.put("SplitPane.oneTouchExpandable", Boolean.FALSE);
      var1.put("SplitPane.continuousLayout", Boolean.TRUE);
      var1.put("SplitPane:SplitPaneDivider.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("SplitPane:SplitPaneDivider.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
      var1.put("SplitPane:SplitPaneDivider.Vertical", new SplitPaneDividerVerticalState());
      var1.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 1, new Insets(3, 0, 3, 0), new Dimension(68, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("SplitPane:SplitPaneDivider[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 2, new Insets(3, 0, 3, 0), new Dimension(68, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 3, new Insets(0, 24, 0, 24), new Dimension(68, 10), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 4, new Insets(5, 0, 5, 0), new Dimension(10, 38), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("TabbedPane.tabAreaStatesMatchSelectedTab", Boolean.TRUE);
      var1.put("TabbedPane.nudgeSelectedLabel", Boolean.FALSE);
      var1.put("TabbedPane.tabRunOverlay", new Integer(2));
      var1.put("TabbedPane.tabOverlap", new Integer(-1));
      var1.put("TabbedPane.extendTabsToBase", Boolean.TRUE);
      var1.put("TabbedPane.useBasicArrows", Boolean.TRUE);
      this.addColor(var1, "TabbedPane.shadow", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "TabbedPane.darkShadow", "text", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "TabbedPane.highlight", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TabbedPane:TabbedPaneTab.contentMargins", new InsetsUIResource(2, 8, 3, 8));
      var1.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 1, new Insets(7, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 2, new Insets(7, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 3, new Insets(7, 6, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "TabbedPane:TabbedPaneTab[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 4, new Insets(6, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 5, new Insets(7, 7, 0, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 6, new Insets(7, 7, 0, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 7, new Insets(7, 9, 0, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "TabbedPane:TabbedPaneTab[Pressed+Selected].textForeground", 255, 255, 255, 255);
      var1.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 8, new Insets(7, 9, 0, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 9, new Insets(7, 7, 3, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 10, new Insets(7, 9, 3, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].textForeground", 255, 255, 255, 255);
      var1.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 11, new Insets(7, 9, 3, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTabArea.contentMargins", new InsetsUIResource(3, 10, 4, 10));
      var1.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 1, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 2, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 3, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 4, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TabbedPane:TabbedPaneContent.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Table.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Table.opaque", Boolean.TRUE);
      this.addColor(var1, "Table.textForeground", 35, 35, 36, 255);
      this.addColor(var1, "Table.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Table.showGrid", Boolean.FALSE);
      var1.put("Table.intercellSpacing", new DimensionUIResource(0, 0));
      this.addColor(var1, "Table.alternateRowColor", "nimbusLightBackground", 0.0F, 0.0F, -0.05098039F, 0, false);
      var1.put("Table.rendererUseTableColors", Boolean.TRUE);
      var1.put("Table.rendererUseUIBorder", Boolean.TRUE);
      var1.put("Table.cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
      var1.put("Table.focusCellHighlightBorder", new BorderUIResource(new NimbusDefaults.PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", new Insets(2, 5, 2, 5))));
      this.addColor(var1, "Table.dropLineColor", "nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "Table.dropLineShortColor", "nimbusOrange", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "Table[Enabled+Selected].textForeground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0, false);
      this.addColor(var1, "Table[Enabled+Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0, false);
      this.addColor(var1, "Table[Disabled+Selected].textBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0, false);
      var1.put("Table:\"Table.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Table:\"Table.cellRenderer\".opaque", Boolean.TRUE);
      this.addColor(var1, "Table:\"Table.cellRenderer\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0, false);
      var1.put("TableHeader.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("TableHeader.opaque", Boolean.TRUE);
      var1.put("TableHeader.rightAlignSortArrow", Boolean.TRUE);
      var1.put("TableHeader[Enabled].ascendingSortIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderPainter", 1, new Insets(0, 0, 0, 2), new Dimension(7, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Table.ascendingSortIcon", new NimbusIcon("TableHeader", "ascendingSortIconPainter", 7, 7));
      var1.put("TableHeader[Enabled].descendingSortIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderPainter", 2, new Insets(0, 0, 0, 0), new Dimension(7, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Table.descendingSortIcon", new NimbusIcon("TableHeader", "descendingSortIconPainter", 7, 7));
      var1.put("TableHeader:\"TableHeader.renderer\".contentMargins", new InsetsUIResource(2, 5, 4, 5));
      var1.put("TableHeader:\"TableHeader.renderer\".opaque", Boolean.TRUE);
      var1.put("TableHeader:\"TableHeader.renderer\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Sorted");
      var1.put("TableHeader:\"TableHeader.renderer\".Sorted", new TableHeaderRendererSortedState());
      var1.put("TableHeader:\"TableHeader.renderer\"[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 1, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 2, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 3, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 4, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 5, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 6, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 7, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TableHeader:\"TableHeader.renderer\"[Disabled+Sorted].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 8, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("\"Table.editor\".contentMargins", new InsetsUIResource(3, 5, 3, 5));
      var1.put("\"Table.editor\".opaque", Boolean.TRUE);
      this.addColor(var1, "\"Table.editor\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "\"Table.editor\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("\"Table.editor\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableEditorPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("\"Table.editor\"[Enabled+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TableEditorPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "\"Table.editor\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("\"Tree.cellEditor\".contentMargins", new InsetsUIResource(2, 5, 2, 5));
      var1.put("\"Tree.cellEditor\".opaque", Boolean.TRUE);
      this.addColor(var1, "\"Tree.cellEditor\".background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "\"Tree.cellEditor\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("\"Tree.cellEditor\"[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreeCellEditorPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("\"Tree.cellEditor\"[Enabled+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreeCellEditorPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "\"Tree.cellEditor\"[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
      this.addColor(var1, "TextField.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "TextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextField[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextField[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "TextField[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextField[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "TextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextField[Disabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextField[Focused].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextField[Enabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("FormattedTextField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
      this.addColor(var1, "FormattedTextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("FormattedTextField[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("FormattedTextField[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "FormattedTextField[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("FormattedTextField[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "FormattedTextField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("FormattedTextField[Disabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("FormattedTextField[Focused].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("FormattedTextField[Enabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("PasswordField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
      this.addColor(var1, "PasswordField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("PasswordField[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("PasswordField[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "PasswordField[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("PasswordField[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      this.addColor(var1, "PasswordField[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("PasswordField[Disabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("PasswordField[Focused].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("PasswordField[Enabled].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextArea.contentMargins", new InsetsUIResource(6, 6, 6, 6));
      var1.put("TextArea.States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,NotInScrollPane");
      var1.put("TextArea.NotInScrollPane", new TextAreaNotInScrollPaneState());
      this.addColor(var1, "TextArea[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextArea[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("TextArea[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "TextArea[Disabled+NotInScrollPane].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextArea[Disabled+NotInScrollPane].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("TextArea[Enabled+NotInScrollPane].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 4, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "TextArea[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextArea[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "TextArea[Disabled+NotInScrollPane].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextArea[Disabled+NotInScrollPane].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 6, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextArea[Focused+NotInScrollPane].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 7, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextArea[Enabled+NotInScrollPane].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 8, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
      var1.put("TextPane.contentMargins", new InsetsUIResource(4, 6, 4, 6));
      var1.put("TextPane.opaque", Boolean.TRUE);
      this.addColor(var1, "TextPane[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextPane[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("TextPane[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "TextPane[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("TextPane[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("EditorPane.contentMargins", new InsetsUIResource(4, 6, 4, 6));
      var1.put("EditorPane.opaque", Boolean.TRUE);
      this.addColor(var1, "EditorPane[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("EditorPane[Disabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("EditorPane[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "EditorPane[Selected].textForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("EditorPane[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("ToolBar.contentMargins", new InsetsUIResource(2, 2, 2, 2));
      var1.put("ToolBar.opaque", Boolean.TRUE);
      var1.put("ToolBar.States", "North,East,West,South");
      var1.put("ToolBar.North", new ToolBarNorthState());
      var1.put("ToolBar.East", new ToolBarEastState());
      var1.put("ToolBar.West", new ToolBarWestState());
      var1.put("ToolBar.South", new ToolBarSouthState());
      var1.put("ToolBar[North].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 1, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("ToolBar[South].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 2, new Insets(1, 0, 0, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("ToolBar[East].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 3, new Insets(1, 0, 0, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("ToolBar[West].borderPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 4, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("ToolBar[Enabled].handleIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 5, new Insets(5, 5, 5, 5), new Dimension(11, 38), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar.handleIcon", new NimbusIcon("ToolBar", "handleIconPainter", 11, 38));
      var1.put("ToolBar:Button.contentMargins", new InsetsUIResource(4, 4, 4, 4));
      var1.put("ToolBar:Button[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 2, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:Button[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:Button[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:Button[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:Button[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton.contentMargins", new InsetsUIResource(4, 4, 4, 4));
      var1.put("ToolBar:ToggleButton[Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 2, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Focused+MouseOver].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Focused+Pressed].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 7, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Focused+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 8, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Pressed+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 9, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Focused+Pressed+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 10, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 11, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBar:ToggleButton[Focused+MouseOver+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 12, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      this.addColor(var1, "ToolBar:ToggleButton[Disabled+Selected].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ToolBar:ToggleButton[Disabled+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 13, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0D, Double.POSITIVE_INFINITY));
      var1.put("ToolBarSeparator.contentMargins", new InsetsUIResource(2, 0, 3, 0));
      this.addColor(var1, "ToolBarSeparator.textForeground", "nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
      var1.put("ToolTip.contentMargins", new InsetsUIResource(4, 4, 4, 4));
      var1.put("ToolTip[Enabled].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.ToolTipPainter", 1, new Insets(1, 1, 1, 1), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("Tree.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("Tree.opaque", Boolean.TRUE);
      this.addColor(var1, "Tree.textForeground", "text", 0.0F, 0.0F, 0.0F, 0, false);
      this.addColor(var1, "Tree.textBackground", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0, false);
      this.addColor(var1, "Tree.background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Tree.rendererFillBackground", Boolean.FALSE);
      var1.put("Tree.leftChildIndent", new Integer(12));
      var1.put("Tree.rightChildIndent", new Integer(4));
      var1.put("Tree.drawHorizontalLines", Boolean.FALSE);
      var1.put("Tree.drawVerticalLines", Boolean.FALSE);
      var1.put("Tree.showRootHandles", Boolean.FALSE);
      var1.put("Tree.rendererUseTreeColors", Boolean.TRUE);
      var1.put("Tree.repaintWholeRow", Boolean.TRUE);
      var1.put("Tree.rowHeight", new Integer(0));
      var1.put("Tree.rendererMargins", new InsetsUIResource(2, 0, 1, 5));
      this.addColor(var1, "Tree.selectionForeground", "nimbusSelectedText", 0.0F, 0.0F, 0.0F, 0, false);
      this.addColor(var1, "Tree.selectionBackground", "nimbusSelectionBackground", 0.0F, 0.0F, 0.0F, 0, false);
      this.addColor(var1, "Tree.dropLineColor", "nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Tree:TreeCell.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "Tree:TreeCell[Enabled].background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      this.addColor(var1, "Tree:TreeCell[Enabled+Focused].background", "nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Tree:TreeCell[Enabled+Focused].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "Tree:TreeCell[Enabled+Selected].textForeground", 255, 255, 255, 255);
      var1.put("Tree:TreeCell[Enabled+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      this.addColor(var1, "Tree:TreeCell[Focused+Selected].textForeground", 255, 255, 255, 255);
      var1.put("Tree:TreeCell[Focused+Selected].backgroundPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 4, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0D, 1.0D));
      var1.put("Tree:\"Tree.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
      this.addColor(var1, "Tree:\"Tree.cellRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0F, 0.0F, 0.0F, 0);
      var1.put("Tree[Enabled].leafIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 4, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree.leafIcon", new NimbusIcon("Tree", "leafIconPainter", 16, 16));
      var1.put("Tree[Enabled].closedIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 5, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree.closedIcon", new NimbusIcon("Tree", "closedIconPainter", 16, 16));
      var1.put("Tree[Enabled].openIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 6, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree.openIcon", new NimbusIcon("Tree", "openIconPainter", 16, 16));
      var1.put("Tree[Enabled].collapsedIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree[Enabled+Selected].collapsedIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree.collapsedIcon", new NimbusIcon("Tree", "collapsedIconPainter", 18, 7));
      var1.put("Tree[Enabled].expandedIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree[Enabled+Selected].expandedIconPainter", new NimbusDefaults.LazyPainter("javax.swing.plaf.nimbus.TreePainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0D, 1.0D));
      var1.put("Tree.expandedIcon", new NimbusIcon("Tree", "expandedIconPainter", 18, 7));
      var1.put("RootPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
      var1.put("RootPane.opaque", Boolean.TRUE);
      this.addColor(var1, "RootPane.background", "control", 0.0F, 0.0F, 0.0F, 0);
   }

   void register(Region var1, String var2) {
      if (var1 != null && var2 != null) {
         List var3 = (List)this.m.get(var1);
         if (var3 == null) {
            LinkedList var6 = new LinkedList();
            var6.add(new NimbusDefaults.LazyStyle(var2));
            this.m.put(var1, var6);
         } else {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               NimbusDefaults.LazyStyle var5 = (NimbusDefaults.LazyStyle)var4.next();
               if (var2.equals(var5.prefix)) {
                  return;
               }
            }

            var3.add(new NimbusDefaults.LazyStyle(var2));
         }

         this.registeredRegions.put(var1.getName(), var1);
      } else {
         throw new IllegalArgumentException("Neither Region nor Prefix may be null");
      }
   }

   SynthStyle getStyle(JComponent var1, Region var2) {
      if (var1 != null && var2 != null) {
         List var3 = (List)this.m.get(var2);
         if (var3 != null && var3.size() != 0) {
            NimbusDefaults.LazyStyle var4 = null;
            Iterator var5 = var3.iterator();

            while(true) {
               NimbusDefaults.LazyStyle var6;
               do {
                  do {
                     if (!var5.hasNext()) {
                        return (SynthStyle)(var4 == null ? this.defaultStyle : var4.getStyle(var1, var2));
                     }

                     var6 = (NimbusDefaults.LazyStyle)var5.next();
                  } while(!var6.matches(var1));
               } while(var4 != null && var4.parts.length >= var6.parts.length && (var4.parts.length != var6.parts.length || !var4.simple || var6.simple));

               var4 = var6;
            }
         } else {
            return this.defaultStyle;
         }
      } else {
         throw new IllegalArgumentException("Neither comp nor r may be null");
      }
   }

   public void clearOverridesCache(JComponent var1) {
      this.overridesCache.remove(var1);
   }

   private void addColor(UIDefaults var1, String var2, int var3, int var4, int var5, int var6) {
      ColorUIResource var7 = new ColorUIResource(new Color(var3, var4, var5, var6));
      this.colorTree.addColor(var2, var7);
      var1.put(var2, var7);
   }

   private void addColor(UIDefaults var1, String var2, String var3, float var4, float var5, float var6, int var7) {
      this.addColor(var1, var2, var3, var4, var5, var6, var7, true);
   }

   private void addColor(UIDefaults var1, String var2, String var3, float var4, float var5, float var6, int var7, boolean var8) {
      DerivedColor var9 = this.getDerivedColor(var2, var3, var4, var5, var6, var7, var8);
      var1.put(var2, var9);
   }

   public DerivedColor getDerivedColor(String var1, float var2, float var3, float var4, int var5, boolean var6) {
      return this.getDerivedColor((String)null, var1, var2, var3, var4, var5, var6);
   }

   private DerivedColor getDerivedColor(String var1, String var2, float var3, float var4, float var5, int var6, boolean var7) {
      Object var8;
      if (var7) {
         var8 = new DerivedColor.UIResource(var2, var3, var4, var5, var6);
      } else {
         var8 = new DerivedColor(var2, var3, var4, var5, var6);
      }

      if (this.derivedColors.containsKey(var8)) {
         return (DerivedColor)this.derivedColors.get(var8);
      } else {
         this.derivedColors.put(var8, var8);
         ((DerivedColor)var8).rederiveColor();
         this.colorTree.addColor(var1, (Color)var8);
         return (DerivedColor)var8;
      }
   }

   private static final class PainterBorder implements Border, UIResource {
      private Insets insets;
      private Painter painter;
      private String painterKey;

      PainterBorder(String var1, Insets var2) {
         this.insets = var2;
         this.painterKey = var1;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (this.painter == null) {
            this.painter = (Painter)UIManager.get(this.painterKey);
            if (this.painter == null) {
               return;
            }
         }

         var2.translate(var3, var4);
         if (var2 instanceof Graphics2D) {
            this.painter.paint((Graphics2D)var2, var1, var5, var6);
         } else {
            BufferedImage var7 = new BufferedImage(var5, var6, 2);
            Graphics2D var8 = var7.createGraphics();
            this.painter.paint(var8, var1, var5, var6);
            var8.dispose();
            var2.drawImage(var7, var3, var4, (ImageObserver)null);
            var7 = null;
         }

         var2.translate(-var3, -var4);
      }

      public Insets getBorderInsets(Component var1) {
         return (Insets)this.insets.clone();
      }

      public boolean isBorderOpaque() {
         return false;
      }
   }

   private class DefaultsListener implements PropertyChangeListener {
      private DefaultsListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if ("lookAndFeel".equals(var1.getPropertyName())) {
            NimbusDefaults.this.colorTree.update();
         }

      }

      // $FF: synthetic method
      DefaultsListener(Object var2) {
         this();
      }
   }

   private class ColorTree implements PropertyChangeListener {
      private NimbusDefaults.ColorTree.Node root;
      private Map<String, NimbusDefaults.ColorTree.Node> nodes;

      private ColorTree() {
         this.root = new NimbusDefaults.ColorTree.Node((Color)null, (NimbusDefaults.ColorTree.Node)null);
         this.nodes = new HashMap();
      }

      public Color getColor(String var1) {
         return ((NimbusDefaults.ColorTree.Node)this.nodes.get(var1)).color;
      }

      public void addColor(String var1, Color var2) {
         NimbusDefaults.ColorTree.Node var3 = this.getParentNode(var2);
         NimbusDefaults.ColorTree.Node var4 = new NimbusDefaults.ColorTree.Node(var2, var3);
         var3.children.add(var4);
         if (var1 != null) {
            this.nodes.put(var1, var4);
         }

      }

      private NimbusDefaults.ColorTree.Node getParentNode(Color var1) {
         NimbusDefaults.ColorTree.Node var2 = this.root;
         if (var1 instanceof DerivedColor) {
            String var3 = ((DerivedColor)var1).getUiDefaultParentName();
            NimbusDefaults.ColorTree.Node var4 = (NimbusDefaults.ColorTree.Node)this.nodes.get(var3);
            if (var4 != null) {
               var2 = var4;
            }
         }

         return var2;
      }

      public void update() {
         this.root.update();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         NimbusDefaults.ColorTree.Node var3 = (NimbusDefaults.ColorTree.Node)this.nodes.get(var2);
         if (var3 != null) {
            var3.parent.children.remove(var3);
            Color var4 = (Color)var1.getNewValue();
            NimbusDefaults.ColorTree.Node var5 = this.getParentNode(var4);
            var3.set(var4, var5);
            var5.children.add(var3);
            var3.update();
         }

      }

      // $FF: synthetic method
      ColorTree(Object var2) {
         this();
      }

      class Node {
         Color color;
         NimbusDefaults.ColorTree.Node parent;
         List<NimbusDefaults.ColorTree.Node> children = new LinkedList();

         Node(Color var2, NimbusDefaults.ColorTree.Node var3) {
            this.set(var2, var3);
         }

         public void set(Color var1, NimbusDefaults.ColorTree.Node var2) {
            this.color = var1;
            this.parent = var2;
         }

         public void update() {
            if (this.color instanceof DerivedColor) {
               ((DerivedColor)this.color).rederiveColor();
            }

            Iterator var1 = this.children.iterator();

            while(var1.hasNext()) {
               NimbusDefaults.ColorTree.Node var2 = (NimbusDefaults.ColorTree.Node)var1.next();
               var2.update();
            }

         }
      }
   }

   private final class LazyStyle {
      private String prefix;
      private boolean simple;
      private NimbusDefaults.LazyStyle.Part[] parts;
      private NimbusStyle style;

      private LazyStyle(String var2) {
         this.simple = true;
         if (var2 == null) {
            throw new IllegalArgumentException("The prefix must not be null");
         } else {
            this.prefix = var2;
            String var3 = var2;
            if (var2.endsWith("cellRenderer\"") || var2.endsWith("renderer\"") || var2.endsWith("listRenderer\"")) {
               var3 = var2.substring(var2.lastIndexOf(":\"") + 1);
            }

            List var4 = this.split(var3);
            this.parts = new NimbusDefaults.LazyStyle.Part[var4.size()];

            for(int var5 = 0; var5 < this.parts.length; ++var5) {
               this.parts[var5] = new NimbusDefaults.LazyStyle.Part((String)var4.get(var5));
               if (this.parts[var5].named) {
                  this.simple = false;
               }
            }

         }
      }

      SynthStyle getStyle(JComponent var1, Region var2) {
         if (var1.getClientProperty("Nimbus.Overrides") != null) {
            Object var3 = (Map)NimbusDefaults.this.overridesCache.get(var1);
            Object var4 = null;
            if (var3 == null) {
               var3 = new HashMap();
               NimbusDefaults.this.overridesCache.put(var1, var3);
            } else {
               var4 = (SynthStyle)((Map)var3).get(var2);
            }

            if (var4 == null) {
               var4 = new NimbusStyle(this.prefix, var1);
               ((Map)var3).put(var2, var4);
            }

            return (SynthStyle)var4;
         } else {
            if (this.style == null) {
               this.style = new NimbusStyle(this.prefix, (JComponent)null);
            }

            return this.style;
         }
      }

      boolean matches(JComponent var1) {
         return this.matches(var1, this.parts.length - 1);
      }

      private boolean matches(Component var1, int var2) {
         if (var2 < 0) {
            return true;
         } else if (var1 == null) {
            return false;
         } else {
            String var3 = var1.getName();
            if (this.parts[var2].named && this.parts[var2].s.equals(var3)) {
               return this.matches(var1.getParent(), var2 - 1);
            } else {
               if (!this.parts[var2].named) {
                  Class var4 = this.parts[var2].c;
                  if (var4 != null && var4.isAssignableFrom(var1.getClass())) {
                     return this.matches(var1.getParent(), var2 - 1);
                  }

                  if (var4 == null && NimbusDefaults.this.registeredRegions.containsKey(this.parts[var2].s)) {
                     Region var5 = (Region)NimbusDefaults.this.registeredRegions.get(this.parts[var2].s);
                     Object var6 = var5.isSubregion() ? var1 : var1.getParent();
                     if (var5 == Region.INTERNAL_FRAME_TITLE_PANE && var6 != null && var6 instanceof JInternalFrame.JDesktopIcon) {
                        JInternalFrame.JDesktopIcon var7 = (JInternalFrame.JDesktopIcon)var6;
                        var6 = var7.getInternalFrame();
                     }

                     return this.matches((Component)var6, var2 - 1);
                  }
               }

               return false;
            }
         }
      }

      private List<String> split(String var1) {
         ArrayList var2 = new ArrayList();
         int var3 = 0;
         boolean var4 = false;
         int var5 = 0;

         for(int var6 = 0; var6 < var1.length(); ++var6) {
            char var7 = var1.charAt(var6);
            if (var7 == '[') {
               ++var3;
            } else if (var7 == '"') {
               var4 = !var4;
            } else if (var7 == ']') {
               --var3;
               if (var3 < 0) {
                  throw new RuntimeException("Malformed prefix: " + var1);
               }
            } else if (var7 == ':' && !var4 && var3 == 0) {
               var2.add(var1.substring(var5, var6));
               var5 = var6 + 1;
            }
         }

         if (var5 < var1.length() - 1 && !var4 && var3 == 0) {
            var2.add(var1.substring(var5));
         }

         return var2;
      }

      // $FF: synthetic method
      LazyStyle(String var2, Object var3) {
         this(var2);
      }

      private final class Part {
         private String s;
         private boolean named;
         private Class c;

         Part(String var2) {
            this.named = var2.charAt(0) == '"' && var2.charAt(var2.length() - 1) == '"';
            if (this.named) {
               this.s = var2.substring(1, var2.length() - 1);
            } else {
               this.s = var2;

               try {
                  this.c = Class.forName("javax.swing.J" + var2);
               } catch (Exception var5) {
               }

               try {
                  this.c = Class.forName(var2.replace("_", "."));
               } catch (Exception var4) {
               }
            }

         }
      }
   }

   private static final class LazyPainter implements UIDefaults.LazyValue {
      private int which;
      private AbstractRegionPainter.PaintContext ctx;
      private String className;

      LazyPainter(String var1, int var2, Insets var3, Dimension var4, boolean var5) {
         if (var1 == null) {
            throw new IllegalArgumentException("The className must be specified");
         } else {
            this.className = var1;
            this.which = var2;
            this.ctx = new AbstractRegionPainter.PaintContext(var3, var4, var5);
         }
      }

      LazyPainter(String var1, int var2, Insets var3, Dimension var4, boolean var5, AbstractRegionPainter.PaintContext.CacheMode var6, double var7, double var9) {
         if (var1 == null) {
            throw new IllegalArgumentException("The className must be specified");
         } else {
            this.className = var1;
            this.which = var2;
            this.ctx = new AbstractRegionPainter.PaintContext(var3, var4, var5, var6, var7, var9);
         }
      }

      public Object createValue(UIDefaults var1) {
         try {
            Object var3;
            if (var1 == null || !((var3 = var1.get("ClassLoader")) instanceof ClassLoader)) {
               var3 = Thread.currentThread().getContextClassLoader();
               if (var3 == null) {
                  var3 = ClassLoader.getSystemClassLoader();
               }
            }

            Class var2 = Class.forName(this.className, true, (ClassLoader)var3);
            Constructor var4 = var2.getConstructor(AbstractRegionPainter.PaintContext.class, Integer.TYPE);
            if (var4 == null) {
               throw new NullPointerException("Failed to find the constructor for the class: " + this.className);
            } else {
               return var4.newInstance(this.ctx, this.which);
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      }
   }

   static final class DerivedFont implements UIDefaults.ActiveValue {
      private float sizeOffset;
      private Boolean bold;
      private Boolean italic;
      private String parentKey;

      public DerivedFont(String var1, float var2, Boolean var3, Boolean var4) {
         if (var1 == null) {
            throw new IllegalArgumentException("You must specify a key");
         } else {
            this.parentKey = var1;
            this.sizeOffset = var2;
            this.bold = var3;
            this.italic = var4;
         }
      }

      public Object createValue(UIDefaults var1) {
         Font var2 = var1.getFont(this.parentKey);
         if (var2 != null) {
            float var3 = (float)Math.round(var2.getSize2D() * this.sizeOffset);
            int var4 = var2.getStyle();
            if (this.bold != null) {
               if (this.bold) {
                  var4 |= 1;
               } else {
                  var4 &= -2;
               }
            }

            if (this.italic != null) {
               if (this.italic) {
                  var4 |= 2;
               } else {
                  var4 &= -3;
               }
            }

            return var2.deriveFont(var4, var3);
         } else {
            return null;
         }
      }
   }
}
