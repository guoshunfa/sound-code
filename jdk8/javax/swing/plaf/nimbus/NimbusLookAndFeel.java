package javax.swing.plaf.nimbus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import sun.security.action.GetPropertyAction;
import sun.swing.ImageIconUIResource;
import sun.swing.plaf.GTKKeybindings;
import sun.swing.plaf.WindowsKeybindings;
import sun.swing.plaf.synth.SynthIcon;

public class NimbusLookAndFeel extends SynthLookAndFeel {
   private static final String[] COMPONENT_KEYS = new String[]{"ArrowButton", "Button", "CheckBox", "CheckBoxMenuItem", "ColorChooser", "ComboBox", "DesktopPane", "DesktopIcon", "EditorPane", "FileChooser", "FormattedTextField", "InternalFrame", "InternalFrameTitlePane", "Label", "List", "Menu", "MenuBar", "MenuItem", "OptionPane", "Panel", "PasswordField", "PopupMenu", "PopupMenuSeparator", "ProgressBar", "RadioButton", "RadioButtonMenuItem", "RootPane", "ScrollBar", "ScrollBarTrack", "ScrollBarThumb", "ScrollPane", "Separator", "Slider", "SliderTrack", "SliderThumb", "Spinner", "SplitPane", "TabbedPane", "Table", "TableHeader", "TextArea", "TextField", "TextPane", "ToggleButton", "ToolBar", "ToolTip", "Tree", "Viewport"};
   private NimbusDefaults defaults = new NimbusDefaults();
   private UIDefaults uiDefaults;
   private NimbusLookAndFeel.DefaultsListener defaultsListener = new NimbusLookAndFeel.DefaultsListener();
   private Map<String, Map<String, Object>> compiledDefaults = null;
   private boolean defaultListenerAdded = false;

   public void initialize() {
      super.initialize();
      this.defaults.initialize();
      setStyleFactory(new SynthStyleFactory() {
         public SynthStyle getStyle(JComponent var1, Region var2) {
            return NimbusLookAndFeel.this.defaults.getStyle(var1, var2);
         }
      });
   }

   public void uninitialize() {
      super.uninitialize();
      this.defaults.uninitialize();
      ImageCache.getInstance().flush();
      UIManager.getDefaults().removePropertyChangeListener(this.defaultsListener);
   }

   public UIDefaults getDefaults() {
      if (this.uiDefaults == null) {
         String var1 = this.getSystemProperty("os.name");
         boolean var2 = var1 != null && var1.contains("Windows");
         this.uiDefaults = super.getDefaults();
         this.defaults.initializeDefaults(this.uiDefaults);
         if (var2) {
            WindowsKeybindings.installKeybindings(this.uiDefaults);
         } else {
            GTKKeybindings.installKeybindings(this.uiDefaults);
         }

         this.uiDefaults.put("TitledBorder.titlePosition", 1);
         this.uiDefaults.put("TitledBorder.border", new BorderUIResource(new LoweredBorder()));
         this.uiDefaults.put("TitledBorder.titleColor", this.getDerivedColor("text", 0.0F, 0.0F, 0.23F, 0, true));
         this.uiDefaults.put("TitledBorder.font", new NimbusDefaults.DerivedFont("defaultFont", 1.0F, true, (Boolean)null));
         this.uiDefaults.put("OptionPane.isYesLast", !var2);
         this.uiDefaults.put("Table.scrollPaneCornerComponent", new UIDefaults.ActiveValue() {
            public Object createValue(UIDefaults var1) {
               return new TableScrollPaneCorner();
            }
         });
         this.uiDefaults.put("ToolBarSeparator[Enabled].backgroundPainter", new ToolBarSeparatorPainter());
         String[] var3 = COMPONENT_KEYS;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            String var7 = var6 + ".foreground";
            if (!this.uiDefaults.containsKey(var7)) {
               this.uiDefaults.put(var7, new NimbusLookAndFeel.NimbusProperty(var6, "textForeground"));
            }

            var7 = var6 + ".background";
            if (!this.uiDefaults.containsKey(var7)) {
               this.uiDefaults.put(var7, new NimbusLookAndFeel.NimbusProperty(var6, "background"));
            }

            var7 = var6 + ".font";
            if (!this.uiDefaults.containsKey(var7)) {
               this.uiDefaults.put(var7, new NimbusLookAndFeel.NimbusProperty(var6, "font"));
            }

            var7 = var6 + ".disabledText";
            if (!this.uiDefaults.containsKey(var7)) {
               this.uiDefaults.put(var7, new NimbusLookAndFeel.NimbusProperty(var6, "Disabled", "textForeground"));
            }

            var7 = var6 + ".disabled";
            if (!this.uiDefaults.containsKey(var7)) {
               this.uiDefaults.put(var7, new NimbusLookAndFeel.NimbusProperty(var6, "Disabled", "background"));
            }
         }

         this.uiDefaults.put("FileView.computerIcon", new NimbusLookAndFeel.LinkProperty("FileChooser.homeFolderIcon"));
         this.uiDefaults.put("FileView.directoryIcon", new NimbusLookAndFeel.LinkProperty("FileChooser.directoryIcon"));
         this.uiDefaults.put("FileView.fileIcon", new NimbusLookAndFeel.LinkProperty("FileChooser.fileIcon"));
         this.uiDefaults.put("FileView.floppyDriveIcon", new NimbusLookAndFeel.LinkProperty("FileChooser.floppyDriveIcon"));
         this.uiDefaults.put("FileView.hardDriveIcon", new NimbusLookAndFeel.LinkProperty("FileChooser.hardDriveIcon"));
      }

      return this.uiDefaults;
   }

   public static NimbusStyle getStyle(JComponent var0, Region var1) {
      return (NimbusStyle)SynthLookAndFeel.getStyle(var0, var1);
   }

   public String getName() {
      return "Nimbus";
   }

   public String getID() {
      return "Nimbus";
   }

   public String getDescription() {
      return "Nimbus Look and Feel";
   }

   public boolean shouldUpdateStyleOnAncestorChanged() {
      return true;
   }

   protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if ("name" != var2 && "ancestor" != var2 && "Nimbus.Overrides" != var2 && "Nimbus.Overrides.InheritDefaults" != var2 && "JComponent.sizeVariant" != var2) {
         return super.shouldUpdateStyleOnEvent(var1);
      } else {
         JComponent var3 = (JComponent)var1.getSource();
         this.defaults.clearOverridesCache(var3);
         return true;
      }
   }

   public void register(Region var1, String var2) {
      this.defaults.register(var1, var2);
   }

   private String getSystemProperty(String var1) {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var1)));
   }

   public Icon getDisabledIcon(JComponent var1, Icon var2) {
      if (var2 instanceof SynthIcon) {
         SynthIcon var3 = (SynthIcon)var2;
         BufferedImage var4 = EffectUtils.createCompatibleTranslucentImage(var3.getIconWidth(), var3.getIconHeight());
         Graphics2D var5 = var4.createGraphics();
         var3.paintIcon(var1, var5, 0, 0);
         var5.dispose();
         return new ImageIconUIResource(GrayFilter.createDisabledImage(var4));
      } else {
         return super.getDisabledIcon(var1, var2);
      }
   }

   public Color getDerivedColor(String var1, float var2, float var3, float var4, int var5, boolean var6) {
      return this.defaults.getDerivedColor(var1, var2, var3, var4, var5, var6);
   }

   protected final Color getDerivedColor(Color var1, Color var2, float var3, boolean var4) {
      int var5 = deriveARGB(var1, var2, var3);
      return (Color)(var4 ? new ColorUIResource(var5) : new Color(var5));
   }

   protected final Color getDerivedColor(Color var1, Color var2, float var3) {
      return this.getDerivedColor(var1, var2, var3, true);
   }

   static Object resolveToolbarConstraint(JToolBar var0) {
      if (var0 != null) {
         Container var1 = var0.getParent();
         if (var1 != null) {
            LayoutManager var2 = var1.getLayout();
            if (var2 instanceof BorderLayout) {
               BorderLayout var3 = (BorderLayout)var2;
               Object var4 = var3.getConstraints(var0);
               if (var4 != "South" && var4 != "East" && var4 != "West") {
                  return "North";
               }

               return var4;
            }
         }
      }

      return "North";
   }

   static int deriveARGB(Color var0, Color var1, float var2) {
      int var3 = var0.getRed() + Math.round((float)(var1.getRed() - var0.getRed()) * var2);
      int var4 = var0.getGreen() + Math.round((float)(var1.getGreen() - var0.getGreen()) * var2);
      int var5 = var0.getBlue() + Math.round((float)(var1.getBlue() - var0.getBlue()) * var2);
      int var6 = var0.getAlpha() + Math.round((float)(var1.getAlpha() - var0.getAlpha()) * var2);
      return (var6 & 255) << 24 | (var3 & 255) << 16 | (var4 & 255) << 8 | var5 & 255;
   }

   static String parsePrefix(String var0) {
      if (var0 == null) {
         return null;
      } else {
         boolean var1 = false;

         for(int var2 = 0; var2 < var0.length(); ++var2) {
            char var3 = var0.charAt(var2);
            if (var3 == '"') {
               var1 = !var1;
            } else if ((var3 == '[' || var3 == '.') && !var1) {
               return var0.substring(0, var2);
            }
         }

         return null;
      }
   }

   Map<String, Object> getDefaultsForPrefix(String var1) {
      if (this.compiledDefaults == null) {
         this.compiledDefaults = new HashMap();
         Iterator var2 = UIManager.getDefaults().entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            if (var3.getKey() instanceof String) {
               this.addDefault((String)var3.getKey(), var3.getValue());
            }
         }

         if (!this.defaultListenerAdded) {
            UIManager.getDefaults().addPropertyChangeListener(this.defaultsListener);
            this.defaultListenerAdded = true;
         }
      }

      return (Map)this.compiledDefaults.get(var1);
   }

   private void addDefault(String var1, Object var2) {
      if (this.compiledDefaults != null) {
         String var3 = parsePrefix(var1);
         if (var3 != null) {
            Object var4 = (Map)this.compiledDefaults.get(var3);
            if (var4 == null) {
               var4 = new HashMap();
               this.compiledDefaults.put(var3, var4);
            }

            ((Map)var4).put(var1, var2);
         }

      }
   }

   private class DefaultsListener implements PropertyChangeListener {
      private DefaultsListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("UIDefaults".equals(var2)) {
            NimbusLookAndFeel.this.compiledDefaults = null;
         } else {
            NimbusLookAndFeel.this.addDefault(var2, var1.getNewValue());
         }

      }

      // $FF: synthetic method
      DefaultsListener(Object var2) {
         this();
      }
   }

   private class NimbusProperty implements UIDefaults.ActiveValue, UIResource {
      private String prefix;
      private String state;
      private String suffix;
      private boolean isFont;

      private NimbusProperty(String var2, String var3) {
         this.state = null;
         this.prefix = var2;
         this.suffix = var3;
         this.isFont = "font".equals(var3);
      }

      private NimbusProperty(String var2, String var3, String var4) {
         this(var2, var4);
         this.state = var3;
      }

      public Object createValue(UIDefaults var1) {
         Object var2 = null;
         if (this.state != null) {
            var2 = NimbusLookAndFeel.this.uiDefaults.get(this.prefix + "[" + this.state + "]." + this.suffix);
         }

         if (var2 == null) {
            var2 = NimbusLookAndFeel.this.uiDefaults.get(this.prefix + "[Enabled]." + this.suffix);
         }

         if (var2 == null) {
            if (this.isFont) {
               var2 = NimbusLookAndFeel.this.uiDefaults.get("defaultFont");
            } else {
               var2 = NimbusLookAndFeel.this.uiDefaults.get(this.suffix);
            }
         }

         return var2;
      }

      // $FF: synthetic method
      NimbusProperty(String var2, String var3, Object var4) {
         this(var2, var3);
      }

      // $FF: synthetic method
      NimbusProperty(String var2, String var3, String var4, Object var5) {
         this(var2, var3, (String)var4);
      }
   }

   private class LinkProperty implements UIDefaults.ActiveValue, UIResource {
      private String dstPropName;

      private LinkProperty(String var2) {
         this.dstPropName = var2;
      }

      public Object createValue(UIDefaults var1) {
         return UIManager.get(this.dstPropName);
      }

      // $FF: synthetic method
      LinkProperty(String var2, Object var3) {
         this(var2);
      }
   }
}
