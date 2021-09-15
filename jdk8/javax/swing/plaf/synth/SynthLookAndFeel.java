package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthFileChooserUI;

public class SynthLookAndFeel extends BasicLookAndFeel {
   static final Insets EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(0, 0, 0, 0);
   private static final Object STYLE_FACTORY_KEY = new StringBuffer("com.sun.java.swing.plaf.gtk.StyleCache");
   private static final Object SELECTED_UI_KEY = new StringBuilder("selectedUI");
   private static final Object SELECTED_UI_STATE_KEY = new StringBuilder("selectedUIState");
   private static SynthStyleFactory lastFactory;
   private static AppContext lastContext;
   private SynthStyleFactory factory = new DefaultSynthStyleFactory();
   private Map<String, Object> defaultsMap;
   private SynthLookAndFeel.Handler _handler = new SynthLookAndFeel.Handler();
   private static ReferenceQueue<LookAndFeel> queue = new ReferenceQueue();

   static ComponentUI getSelectedUI() {
      return (ComponentUI)AppContext.getAppContext().get(SELECTED_UI_KEY);
   }

   static void setSelectedUI(ComponentUI var0, boolean var1, boolean var2, boolean var3, boolean var4) {
      byte var5 = 0;
      int var7;
      if (var1) {
         var7 = 512;
         if (var2) {
            var7 |= 256;
         }
      } else if (var4 && var3) {
         var7 = var5 | 3;
         if (var2) {
            var7 |= 256;
         }
      } else if (var3) {
         var7 = var5 | 1;
         if (var2) {
            var7 |= 256;
         }
      } else {
         var7 = var5 | 8;
      }

      AppContext var6 = AppContext.getAppContext();
      var6.put(SELECTED_UI_KEY, var0);
      var6.put(SELECTED_UI_STATE_KEY, var7);
   }

   static int getSelectedUIState() {
      Integer var0 = (Integer)AppContext.getAppContext().get(SELECTED_UI_STATE_KEY);
      return var0 == null ? 0 : var0;
   }

   static void resetSelectedUI() {
      AppContext.getAppContext().remove(SELECTED_UI_KEY);
   }

   public static void setStyleFactory(SynthStyleFactory var0) {
      Class var1 = SynthLookAndFeel.class;
      synchronized(SynthLookAndFeel.class) {
         AppContext var2 = AppContext.getAppContext();
         lastFactory = var0;
         lastContext = var2;
         var2.put(STYLE_FACTORY_KEY, var0);
      }
   }

   public static SynthStyleFactory getStyleFactory() {
      Class var0 = SynthLookAndFeel.class;
      synchronized(SynthLookAndFeel.class) {
         AppContext var1 = AppContext.getAppContext();
         if (lastContext == var1) {
            return lastFactory;
         } else {
            lastContext = var1;
            lastFactory = (SynthStyleFactory)var1.get(STYLE_FACTORY_KEY);
            return lastFactory;
         }
      }
   }

   static int getComponentState(Component var0) {
      if (var0.isEnabled()) {
         return var0.isFocusOwner() ? 257 : 1;
      } else {
         return 8;
      }
   }

   public static SynthStyle getStyle(JComponent var0, Region var1) {
      return getStyleFactory().getStyle(var0, var1);
   }

   static boolean shouldUpdateStyle(PropertyChangeEvent var0) {
      LookAndFeel var1 = UIManager.getLookAndFeel();
      return var1 instanceof SynthLookAndFeel && ((SynthLookAndFeel)var1).shouldUpdateStyleOnEvent(var0);
   }

   static SynthStyle updateStyle(SynthContext var0, SynthUI var1) {
      SynthStyle var2 = getStyle(var0.getComponent(), var0.getRegion());
      SynthStyle var3 = var0.getStyle();
      if (var2 != var3) {
         if (var3 != null) {
            var3.uninstallDefaults(var0);
         }

         var0.setStyle(var2);
         var2.installDefaults(var0, var1);
      }

      return var2;
   }

   public static void updateStyles(Component var0) {
      if (var0 instanceof JComponent) {
         String var1 = var0.getName();
         var0.setName((String)null);
         if (var1 != null) {
            var0.setName(var1);
         }

         ((JComponent)var0).revalidate();
      }

      Component[] var6 = null;
      if (var0 instanceof JMenu) {
         var6 = ((JMenu)var0).getMenuComponents();
      } else if (var0 instanceof Container) {
         var6 = ((Container)var0).getComponents();
      }

      if (var6 != null) {
         Component[] var2 = var6;
         int var3 = var6.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            updateStyles(var5);
         }
      }

      var0.repaint();
   }

   public static Region getRegion(JComponent var0) {
      return Region.getRegion(var0);
   }

   static Insets getPaintingInsets(SynthContext var0, Insets var1) {
      if (var0.isSubregion()) {
         var1 = var0.getStyle().getInsets(var0, var1);
      } else {
         var1 = var0.getComponent().getInsets(var1);
      }

      return var1;
   }

   static void update(SynthContext var0, Graphics var1) {
      paintRegion(var0, var1, (Rectangle)null);
   }

   static void updateSubregion(SynthContext var0, Graphics var1, Rectangle var2) {
      paintRegion(var0, var1, var2);
   }

   private static void paintRegion(SynthContext var0, Graphics var1, Rectangle var2) {
      JComponent var3 = var0.getComponent();
      SynthStyle var4 = var0.getStyle();
      int var5;
      int var6;
      int var7;
      int var8;
      if (var2 == null) {
         var5 = 0;
         var6 = 0;
         var7 = var3.getWidth();
         var8 = var3.getHeight();
      } else {
         var5 = var2.x;
         var6 = var2.y;
         var7 = var2.width;
         var8 = var2.height;
      }

      boolean var9 = var0.isSubregion();
      if (var9 && var4.isOpaque(var0) || !var9 && var3.isOpaque()) {
         var1.setColor(var4.getColor(var0, ColorType.BACKGROUND));
         var1.fillRect(var5, var6, var7, var8);
      }

   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   static Object getUIOfType(ComponentUI var0, Class var1) {
      return var1.isInstance(var0) ? var0 : null;
   }

   public static ComponentUI createUI(JComponent var0) {
      String var1 = var0.getUIClassID().intern();
      if (var1 == "ButtonUI") {
         return SynthButtonUI.createUI(var0);
      } else if (var1 == "CheckBoxUI") {
         return SynthCheckBoxUI.createUI(var0);
      } else if (var1 == "CheckBoxMenuItemUI") {
         return SynthCheckBoxMenuItemUI.createUI(var0);
      } else if (var1 == "ColorChooserUI") {
         return SynthColorChooserUI.createUI(var0);
      } else if (var1 == "ComboBoxUI") {
         return SynthComboBoxUI.createUI(var0);
      } else if (var1 == "DesktopPaneUI") {
         return SynthDesktopPaneUI.createUI(var0);
      } else if (var1 == "DesktopIconUI") {
         return SynthDesktopIconUI.createUI(var0);
      } else if (var1 == "EditorPaneUI") {
         return SynthEditorPaneUI.createUI(var0);
      } else if (var1 == "FileChooserUI") {
         return SynthFileChooserUI.createUI(var0);
      } else if (var1 == "FormattedTextFieldUI") {
         return SynthFormattedTextFieldUI.createUI(var0);
      } else if (var1 == "InternalFrameUI") {
         return SynthInternalFrameUI.createUI(var0);
      } else if (var1 == "LabelUI") {
         return SynthLabelUI.createUI(var0);
      } else if (var1 == "ListUI") {
         return SynthListUI.createUI(var0);
      } else if (var1 == "MenuBarUI") {
         return SynthMenuBarUI.createUI(var0);
      } else if (var1 == "MenuUI") {
         return SynthMenuUI.createUI(var0);
      } else if (var1 == "MenuItemUI") {
         return SynthMenuItemUI.createUI(var0);
      } else if (var1 == "OptionPaneUI") {
         return SynthOptionPaneUI.createUI(var0);
      } else if (var1 == "PanelUI") {
         return SynthPanelUI.createUI(var0);
      } else if (var1 == "PasswordFieldUI") {
         return SynthPasswordFieldUI.createUI(var0);
      } else if (var1 == "PopupMenuSeparatorUI") {
         return SynthSeparatorUI.createUI(var0);
      } else if (var1 == "PopupMenuUI") {
         return SynthPopupMenuUI.createUI(var0);
      } else if (var1 == "ProgressBarUI") {
         return SynthProgressBarUI.createUI(var0);
      } else if (var1 == "RadioButtonUI") {
         return SynthRadioButtonUI.createUI(var0);
      } else if (var1 == "RadioButtonMenuItemUI") {
         return SynthRadioButtonMenuItemUI.createUI(var0);
      } else if (var1 == "RootPaneUI") {
         return SynthRootPaneUI.createUI(var0);
      } else if (var1 == "ScrollBarUI") {
         return SynthScrollBarUI.createUI(var0);
      } else if (var1 == "ScrollPaneUI") {
         return SynthScrollPaneUI.createUI(var0);
      } else if (var1 == "SeparatorUI") {
         return SynthSeparatorUI.createUI(var0);
      } else if (var1 == "SliderUI") {
         return SynthSliderUI.createUI(var0);
      } else if (var1 == "SpinnerUI") {
         return SynthSpinnerUI.createUI(var0);
      } else if (var1 == "SplitPaneUI") {
         return SynthSplitPaneUI.createUI(var0);
      } else if (var1 == "TabbedPaneUI") {
         return SynthTabbedPaneUI.createUI(var0);
      } else if (var1 == "TableUI") {
         return SynthTableUI.createUI(var0);
      } else if (var1 == "TableHeaderUI") {
         return SynthTableHeaderUI.createUI(var0);
      } else if (var1 == "TextAreaUI") {
         return SynthTextAreaUI.createUI(var0);
      } else if (var1 == "TextFieldUI") {
         return SynthTextFieldUI.createUI(var0);
      } else if (var1 == "TextPaneUI") {
         return SynthTextPaneUI.createUI(var0);
      } else if (var1 == "ToggleButtonUI") {
         return SynthToggleButtonUI.createUI(var0);
      } else if (var1 == "ToolBarSeparatorUI") {
         return SynthSeparatorUI.createUI(var0);
      } else if (var1 == "ToolBarUI") {
         return SynthToolBarUI.createUI(var0);
      } else if (var1 == "ToolTipUI") {
         return SynthToolTipUI.createUI(var0);
      } else if (var1 == "TreeUI") {
         return SynthTreeUI.createUI(var0);
      } else {
         return var1 == "ViewportUI" ? SynthViewportUI.createUI(var0) : null;
      }
   }

   public void load(InputStream var1, Class<?> var2) throws ParseException {
      if (var2 == null) {
         throw new IllegalArgumentException("You must supply a valid resource base Class");
      } else {
         if (this.defaultsMap == null) {
            this.defaultsMap = new HashMap();
         }

         (new SynthParser()).parse(var1, (DefaultSynthStyleFactory)this.factory, (URL)null, var2, this.defaultsMap);
      }
   }

   public void load(URL var1) throws ParseException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("You must supply a valid Synth set URL");
      } else {
         if (this.defaultsMap == null) {
            this.defaultsMap = new HashMap();
         }

         InputStream var2 = var1.openStream();
         (new SynthParser()).parse(var2, (DefaultSynthStyleFactory)this.factory, var1, (Class)null, this.defaultsMap);
      }
   }

   public void initialize() {
      super.initialize();
      DefaultLookup.setDefaultLookup(new SynthDefaultLookup());
      setStyleFactory(this.factory);
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this._handler);
   }

   public void uninitialize() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(this._handler);
      super.uninitialize();
   }

   public UIDefaults getDefaults() {
      UIDefaults var1 = new UIDefaults(60, 0.75F);
      Region.registerUIs(var1);
      var1.setDefaultLocale(Locale.getDefault());
      var1.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
      var1.addResourceBundle("com.sun.swing.internal.plaf.synth.resources.synth");
      var1.put("TabbedPane.isTabRollover", Boolean.TRUE);
      var1.put("ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10));
      var1.put("ColorChooser.swatchesDefaultRecentColor", Color.RED);
      var1.put("ColorChooser.swatchesSwatchSize", new Dimension(10, 10));
      var1.put("html.pendingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"));
      var1.put("html.missingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-failed.png"));
      var1.put("PopupMenu.selectedWindowInputMapBindings", new Object[]{"ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "SPACE", "return"});
      var1.put("PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[]{"LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent"});
      flushUnreferenced();
      Object var2 = getAATextInfo();
      var1.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var2);
      new SynthLookAndFeel.AATextListener(this);
      if (this.defaultsMap != null) {
         var1.putAll(this.defaultsMap);
      }

      return var1;
   }

   public boolean isSupportedLookAndFeel() {
      return true;
   }

   public boolean isNativeLookAndFeel() {
      return false;
   }

   public String getDescription() {
      return "Synth look and feel";
   }

   public String getName() {
      return "Synth look and feel";
   }

   public String getID() {
      return "Synth";
   }

   public boolean shouldUpdateStyleOnAncestorChanged() {
      return false;
   }

   protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if ("name" != var2 && "componentOrientation" != var2) {
         return "ancestor" == var2 && var1.getNewValue() != null ? this.shouldUpdateStyleOnAncestorChanged() : false;
      } else {
         return true;
      }
   }

   private static Object getAATextInfo() {
      String var0 = Locale.getDefault().getLanguage();
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.desktop")));
      boolean var2 = Locale.CHINESE.getLanguage().equals(var0) || Locale.JAPANESE.getLanguage().equals(var0) || Locale.KOREAN.getLanguage().equals(var0);
      boolean var3 = "gnome".equals(var1);
      boolean var4 = SwingUtilities2.isLocalDisplay();
      boolean var5 = var4 && (!var3 || !var2);
      SwingUtilities2.AATextInfo var6 = SwingUtilities2.AATextInfo.getAATextInfo(var5);
      return var6;
   }

   private static void flushUnreferenced() {
      SynthLookAndFeel.AATextListener var0;
      while((var0 = (SynthLookAndFeel.AATextListener)queue.poll()) != null) {
         var0.dispose();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      throw new NotSerializableException(this.getClass().getName());
   }

   private class Handler implements PropertyChangeListener {
      private Handler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         Object var3 = var1.getNewValue();
         Object var4 = var1.getOldValue();
         if ("focusOwner" == var2) {
            if (var4 instanceof JComponent) {
               this.repaintIfBackgroundsDiffer((JComponent)var4);
            }

            if (var3 instanceof JComponent) {
               this.repaintIfBackgroundsDiffer((JComponent)var3);
            }
         } else if ("managingFocus" == var2) {
            KeyboardFocusManager var5 = (KeyboardFocusManager)var1.getSource();
            if (var3.equals(Boolean.FALSE)) {
               var5.removePropertyChangeListener(SynthLookAndFeel.this._handler);
            } else {
               var5.addPropertyChangeListener(SynthLookAndFeel.this._handler);
            }
         }

      }

      private void repaintIfBackgroundsDiffer(JComponent var1) {
         ComponentUI var2 = (ComponentUI)var1.getClientProperty(SwingUtilities2.COMPONENT_UI_PROPERTY_KEY);
         if (var2 instanceof SynthUI) {
            SynthUI var3 = (SynthUI)var2;
            SynthContext var4 = var3.getContext(var1);
            SynthStyle var5 = var4.getStyle();
            int var6 = var4.getComponentState();
            Color var7 = var5.getColor(var4, ColorType.BACKGROUND);
            var6 ^= 256;
            var4.setComponentState(var6);
            Color var8 = var5.getColor(var4, ColorType.BACKGROUND);
            var6 ^= 256;
            var4.setComponentState(var6);
            if (var7 != null && !var7.equals(var8)) {
               var1.repaint();
            }

            var4.dispose();
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private static class AATextListener extends WeakReference<LookAndFeel> implements PropertyChangeListener {
      private String key = "awt.font.desktophints";
      private static boolean updatePending;

      AATextListener(LookAndFeel var1) {
         super(var1, SynthLookAndFeel.queue);
         Toolkit var2 = Toolkit.getDefaultToolkit();
         var2.addPropertyChangeListener(this.key, this);
      }

      public void propertyChange(PropertyChangeEvent var1) {
         UIDefaults var2 = UIManager.getLookAndFeelDefaults();
         if (var2.getBoolean("Synth.doNotSetTextAA")) {
            this.dispose();
         } else {
            LookAndFeel var3 = (LookAndFeel)this.get();
            if (var3 != null && var3 == UIManager.getLookAndFeel()) {
               Object var4 = SynthLookAndFeel.getAATextInfo();
               var2.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, var4);
               this.updateUI();
            } else {
               this.dispose();
            }
         }
      }

      void dispose() {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         var1.removePropertyChangeListener(this.key, this);
      }

      private static void updateWindowUI(Window var0) {
         SynthLookAndFeel.updateStyles(var0);
         Window[] var1 = var0.getOwnedWindows();
         Window[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Window var5 = var2[var4];
            updateWindowUI(var5);
         }

      }

      private static void updateAllUIs() {
         Frame[] var0 = Frame.getFrames();
         Frame[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Frame var4 = var1[var3];
            updateWindowUI(var4);
         }

      }

      private static synchronized void setUpdatePending(boolean var0) {
         updatePending = var0;
      }

      private static synchronized boolean isUpdatePending() {
         return updatePending;
      }

      protected void updateUI() {
         if (!isUpdatePending()) {
            setUpdatePending(true);
            Runnable var1 = new Runnable() {
               public void run() {
                  SynthLookAndFeel.AATextListener.updateAllUIs();
                  SynthLookAndFeel.AATextListener.setUpdatePending(false);
               }
            };
            SwingUtilities.invokeLater(var1);
         }

      }
   }
}
