package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public final class NimbusStyle extends SynthStyle {
   public static final String LARGE_KEY = "large";
   public static final String SMALL_KEY = "small";
   public static final String MINI_KEY = "mini";
   public static final double LARGE_SCALE = 1.15D;
   public static final double SMALL_SCALE = 0.857D;
   public static final double MINI_SCALE = 0.714D;
   private static final Object NULL = '\u0000';
   private static final Color DEFAULT_COLOR;
   private static final Comparator<NimbusStyle.RuntimeState> STATE_COMPARATOR;
   private String prefix;
   private SynthPainter painter;
   private NimbusStyle.Values values;
   private NimbusStyle.CacheKey tmpKey = new NimbusStyle.CacheKey("", 0);
   private WeakReference<JComponent> component;

   NimbusStyle(String var1, JComponent var2) {
      if (var2 != null) {
         this.component = new WeakReference(var2);
      }

      this.prefix = var1;
      this.painter = new SynthPainterImpl(this);
   }

   public void installDefaults(SynthContext var1) {
      this.validate();
      super.installDefaults(var1);
   }

   private void validate() {
      if (this.values == null) {
         this.values = new NimbusStyle.Values();
         Object var1 = ((NimbusLookAndFeel)UIManager.getLookAndFeel()).getDefaultsForPrefix(this.prefix);
         String var9;
         if (this.component != null) {
            Object var2 = ((JComponent)this.component.get()).getClientProperty("Nimbus.Overrides");
            if (var2 instanceof UIDefaults) {
               Object var3 = ((JComponent)this.component.get()).getClientProperty("Nimbus.Overrides.InheritDefaults");
               boolean var4 = var3 instanceof Boolean ? (Boolean)var3 : true;
               UIDefaults var5 = (UIDefaults)var2;
               TreeMap var6 = new TreeMap();
               Iterator var7 = var5.keySet().iterator();

               while(var7.hasNext()) {
                  Object var8 = var7.next();
                  if (var8 instanceof String) {
                     var9 = (String)var8;
                     if (var9.startsWith(this.prefix)) {
                        var6.put(var9, var5.get(var9));
                     }
                  }
               }

               if (var4) {
                  ((Map)var1).putAll(var6);
               } else {
                  var1 = var6;
               }
            }
         }

         ArrayList var19 = new ArrayList();
         HashMap var20 = new HashMap();
         ArrayList var21 = new ArrayList();
         String var22 = (String)((Map)var1).get(this.prefix + ".States");
         String var26;
         if (var22 != null) {
            String[] var23 = var22.split(",");

            int var25;
            State var29;
            for(var25 = 0; var25 < var23.length; ++var25) {
               var23[var25] = var23[var25].trim();
               if (!State.isStandardStateName(var23[var25])) {
                  var26 = this.prefix + "." + var23[var25];
                  var29 = (State)((Map)var1).get(var26);
                  if (var29 != null) {
                     var19.add(var29);
                  }
               } else {
                  var19.add(State.getStandardState(var23[var25]));
               }
            }

            if (var19.size() > 0) {
               this.values.stateTypes = (State[])var19.toArray(new State[var19.size()]);
            }

            var25 = 1;

            for(Iterator var28 = var19.iterator(); var28.hasNext(); var25 <<= 1) {
               var29 = (State)var28.next();
               var20.put(var29.getName(), var25);
            }
         } else {
            var19.add(State.Enabled);
            var19.add(State.MouseOver);
            var19.add(State.Pressed);
            var19.add(State.Disabled);
            var19.add(State.Focused);
            var19.add(State.Selected);
            var19.add(State.Default);
            var20.put("Enabled", 1);
            var20.put("MouseOver", 2);
            var20.put("Pressed", 4);
            var20.put("Disabled", 8);
            var20.put("Focused", 256);
            var20.put("Selected", 512);
            var20.put("Default", 1024);
         }

         Iterator var24 = ((Map)var1).keySet().iterator();

         while(true) {
            while(true) {
               String var27;
               do {
                  do {
                     if (!var24.hasNext()) {
                        Collections.sort(var21, STATE_COMPARATOR);
                        this.values.states = (NimbusStyle.RuntimeState[])var21.toArray(new NimbusStyle.RuntimeState[var21.size()]);
                        return;
                     }

                     var27 = (String)var24.next();
                     var26 = var27.substring(this.prefix.length());
                  } while(var26.indexOf(34) != -1);
               } while(var26.indexOf(58) != -1);

               var26 = var26.substring(1);
               var9 = null;
               String var10 = null;
               int var11 = var26.indexOf(93);
               if (var11 < 0) {
                  var10 = var26;
               } else {
                  var9 = var26.substring(0, var11);
                  var10 = var26.substring(var11 + 2);
               }

               if (var9 == null) {
                  if ("contentMargins".equals(var10)) {
                     this.values.contentMargins = (Insets)((Map)var1).get(var27);
                  } else if (!"States".equals(var10)) {
                     this.values.defaults.put(var10, ((Map)var1).get(var27));
                  }
               } else {
                  boolean var12 = false;
                  int var13 = 0;
                  String[] var14 = var9.split("\\+");
                  String[] var15 = var14;
                  int var16 = var14.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     String var18 = var15[var17];
                     if (!var20.containsKey(var18)) {
                        var12 = true;
                        break;
                     }

                     var13 |= (Integer)var20.get(var18);
                  }

                  if (!var12) {
                     NimbusStyle.RuntimeState var30 = null;
                     Iterator var31 = var21.iterator();

                     while(var31.hasNext()) {
                        NimbusStyle.RuntimeState var32 = (NimbusStyle.RuntimeState)var31.next();
                        if (var32.state == var13) {
                           var30 = var32;
                           break;
                        }
                     }

                     if (var30 == null) {
                        var30 = new NimbusStyle.RuntimeState(var13, var9);
                        var21.add(var30);
                     }

                     if ("backgroundPainter".equals(var10)) {
                        var30.backgroundPainter = this.getPainter((Map)var1, var27);
                     } else if ("foregroundPainter".equals(var10)) {
                        var30.foregroundPainter = this.getPainter((Map)var1, var27);
                     } else if ("borderPainter".equals(var10)) {
                        var30.borderPainter = this.getPainter((Map)var1, var27);
                     } else {
                        var30.defaults.put(var10, ((Map)var1).get(var27));
                     }
                  }
               }
            }
         }
      }
   }

   private Painter getPainter(Map<String, Object> var1, String var2) {
      Object var3 = var1.get(var2);
      if (var3 instanceof UIDefaults.LazyValue) {
         var3 = ((UIDefaults.LazyValue)var3).createValue(UIManager.getDefaults());
      }

      return var3 instanceof Painter ? (Painter)var3 : null;
   }

   public Insets getInsets(SynthContext var1, Insets var2) {
      if (var2 == null) {
         var2 = new Insets(0, 0, 0, 0);
      }

      NimbusStyle.Values var3 = this.getValues(var1);
      if (var3.contentMargins == null) {
         var2.bottom = var2.top = var2.left = var2.right = 0;
         return var2;
      } else {
         var2.bottom = var3.contentMargins.bottom;
         var2.top = var3.contentMargins.top;
         var2.left = var3.contentMargins.left;
         var2.right = var3.contentMargins.right;
         String var4 = (String)var1.getComponent().getClientProperty("JComponent.sizeVariant");
         if (var4 != null) {
            if ("large".equals(var4)) {
               var2.bottom = (int)((double)var2.bottom * 1.15D);
               var2.top = (int)((double)var2.top * 1.15D);
               var2.left = (int)((double)var2.left * 1.15D);
               var2.right = (int)((double)var2.right * 1.15D);
            } else if ("small".equals(var4)) {
               var2.bottom = (int)((double)var2.bottom * 0.857D);
               var2.top = (int)((double)var2.top * 0.857D);
               var2.left = (int)((double)var2.left * 0.857D);
               var2.right = (int)((double)var2.right * 0.857D);
            } else if ("mini".equals(var4)) {
               var2.bottom = (int)((double)var2.bottom * 0.714D);
               var2.top = (int)((double)var2.top * 0.714D);
               var2.left = (int)((double)var2.left * 0.714D);
               var2.right = (int)((double)var2.right * 0.714D);
            }
         }

         return var2;
      }
   }

   protected Color getColorForState(SynthContext var1, ColorType var2) {
      String var3 = null;
      if (var2 == ColorType.BACKGROUND) {
         var3 = "background";
      } else if (var2 == ColorType.FOREGROUND) {
         var3 = "textForeground";
      } else if (var2 == ColorType.TEXT_BACKGROUND) {
         var3 = "textBackground";
      } else if (var2 == ColorType.TEXT_FOREGROUND) {
         var3 = "textForeground";
      } else if (var2 == ColorType.FOCUS) {
         var3 = "focus";
      } else {
         if (var2 == null) {
            return DEFAULT_COLOR;
         }

         var3 = var2.toString();
      }

      Color var4 = (Color)this.get(var1, var3);
      if (var4 == null) {
         var4 = DEFAULT_COLOR;
      }

      return var4;
   }

   protected Font getFontForState(SynthContext var1) {
      Font var2 = (Font)this.get(var1, "font");
      if (var2 == null) {
         var2 = UIManager.getFont("defaultFont");
      }

      String var3 = (String)var1.getComponent().getClientProperty("JComponent.sizeVariant");
      if (var3 != null) {
         if ("large".equals(var3)) {
            var2 = var2.deriveFont((float)Math.round((double)var2.getSize2D() * 1.15D));
         } else if ("small".equals(var3)) {
            var2 = var2.deriveFont((float)Math.round((double)var2.getSize2D() * 0.857D));
         } else if ("mini".equals(var3)) {
            var2 = var2.deriveFont((float)Math.round((double)var2.getSize2D() * 0.714D));
         }
      }

      return var2;
   }

   public SynthPainter getPainter(SynthContext var1) {
      return this.painter;
   }

   public boolean isOpaque(SynthContext var1) {
      if ("Table.cellRenderer".equals(var1.getComponent().getName())) {
         return true;
      } else {
         Boolean var2 = (Boolean)this.get(var1, "opaque");
         return var2 == null ? false : var2;
      }
   }

   public Object get(SynthContext var1, Object var2) {
      NimbusStyle.Values var3 = this.getValues(var1);
      String var4 = var2.toString();
      String var5 = var4.substring(var4.indexOf(".") + 1);
      Object var6 = null;
      int var7 = this.getExtendedState(var1, var3);
      this.tmpKey.init(var5, var7);
      var6 = var3.cache.get(this.tmpKey);
      boolean var8 = var6 != null;
      if (!var8) {
         NimbusStyle.RuntimeState var9 = null;

         for(int[] var10 = new int[]{-1}; var6 == null && (var9 = this.getNextState(var3.states, var10, var7)) != null; var6 = var9.defaults.get(var5)) {
         }

         if (var6 == null && var3.defaults != null) {
            var6 = var3.defaults.get(var5);
         }

         if (var6 == null) {
            var6 = UIManager.get(var4);
         }

         if (var6 == null && var5.equals("focusInputMap")) {
            var6 = super.get(var1, var4);
         }

         var3.cache.put(new NimbusStyle.CacheKey(var5, var7), var6 == null ? NULL : var6);
      }

      return var6 == NULL ? null : var6;
   }

   public Painter getBackgroundPainter(SynthContext var1) {
      NimbusStyle.Values var2 = this.getValues(var1);
      int var3 = this.getExtendedState(var1, var2);
      Painter var4 = null;
      this.tmpKey.init("backgroundPainter$$instance", var3);
      var4 = (Painter)var2.cache.get(this.tmpKey);
      if (var4 != null) {
         return var4;
      } else {
         NimbusStyle.RuntimeState var5 = null;
         int[] var6 = new int[]{-1};

         while((var5 = this.getNextState(var2.states, var6, var3)) != null) {
            if (var5.backgroundPainter != null) {
               var4 = var5.backgroundPainter;
               break;
            }
         }

         if (var4 == null) {
            var4 = (Painter)this.get(var1, "backgroundPainter");
         }

         if (var4 != null) {
            var2.cache.put(new NimbusStyle.CacheKey("backgroundPainter$$instance", var3), var4);
         }

         return var4;
      }
   }

   public Painter getForegroundPainter(SynthContext var1) {
      NimbusStyle.Values var2 = this.getValues(var1);
      int var3 = this.getExtendedState(var1, var2);
      Painter var4 = null;
      this.tmpKey.init("foregroundPainter$$instance", var3);
      var4 = (Painter)var2.cache.get(this.tmpKey);
      if (var4 != null) {
         return var4;
      } else {
         NimbusStyle.RuntimeState var5 = null;
         int[] var6 = new int[]{-1};

         while((var5 = this.getNextState(var2.states, var6, var3)) != null) {
            if (var5.foregroundPainter != null) {
               var4 = var5.foregroundPainter;
               break;
            }
         }

         if (var4 == null) {
            var4 = (Painter)this.get(var1, "foregroundPainter");
         }

         if (var4 != null) {
            var2.cache.put(new NimbusStyle.CacheKey("foregroundPainter$$instance", var3), var4);
         }

         return var4;
      }
   }

   public Painter getBorderPainter(SynthContext var1) {
      NimbusStyle.Values var2 = this.getValues(var1);
      int var3 = this.getExtendedState(var1, var2);
      Painter var4 = null;
      this.tmpKey.init("borderPainter$$instance", var3);
      var4 = (Painter)var2.cache.get(this.tmpKey);
      if (var4 != null) {
         return var4;
      } else {
         NimbusStyle.RuntimeState var5 = null;
         int[] var6 = new int[]{-1};

         while((var5 = this.getNextState(var2.states, var6, var3)) != null) {
            if (var5.borderPainter != null) {
               var4 = var5.borderPainter;
               break;
            }
         }

         if (var4 == null) {
            var4 = (Painter)this.get(var1, "borderPainter");
         }

         if (var4 != null) {
            var2.cache.put(new NimbusStyle.CacheKey("borderPainter$$instance", var3), var4);
         }

         return var4;
      }
   }

   private NimbusStyle.Values getValues(SynthContext var1) {
      this.validate();
      return this.values;
   }

   private boolean contains(String[] var1, String var2) {
      assert var2 != null;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var2.equals(var1[var3])) {
            return true;
         }
      }

      return false;
   }

   private int getExtendedState(SynthContext var1, NimbusStyle.Values var2) {
      JComponent var3 = var1.getComponent();
      int var4 = 0;
      int var5 = 1;
      Object var6 = var3.getClientProperty("Nimbus.State");
      int var10;
      if (var6 != null) {
         String var7 = var6.toString();
         String[] var8 = var7.split("\\+");
         int var11;
         if (var2.stateTypes == null) {
            String[] var9 = var8;
            var10 = var8.length;

            for(var11 = 0; var11 < var10; ++var11) {
               String var12 = var9[var11];
               State.StandardState var13 = State.getStandardState(var12);
               if (var13 != null) {
                  var4 |= var13.getState();
               }
            }
         } else {
            State[] var16 = var2.stateTypes;
            var10 = var16.length;

            for(var11 = 0; var11 < var10; ++var11) {
               State var19 = var16[var11];
               if (this.contains(var8, var19.getName())) {
                  var4 |= var5;
               }

               var5 <<= 1;
            }
         }
      } else {
         if (var2.stateTypes == null) {
            return var1.getComponentState();
         }

         int var14 = var1.getComponentState();
         State[] var15 = var2.stateTypes;
         int var17 = var15.length;

         for(var10 = 0; var10 < var17; ++var10) {
            State var18 = var15[var10];
            if (var18.isInState(var3, var14)) {
               var4 |= var5;
            }

            var5 <<= 1;
         }
      }

      return var4;
   }

   private NimbusStyle.RuntimeState getNextState(NimbusStyle.RuntimeState[] var1, int[] var2, int var3) {
      if (var1 != null && var1.length > 0) {
         int var4 = 0;
         int var5 = -1;
         int var6 = -1;
         int var7;
         if (var3 == 0) {
            for(var7 = var1.length - 1; var7 >= 0; --var7) {
               if (var1[var7].state == 0) {
                  var2[0] = var7;
                  return var1[var7];
               }
            }

            var2[0] = -1;
            return null;
         }

         var7 = var2 != null && var2[0] != -1 ? var2[0] : var1.length;

         for(int var8 = var7 - 1; var8 >= 0; --var8) {
            int var9 = var1[var8].state;
            if (var9 == 0) {
               if (var6 == -1) {
                  var6 = var8;
               }
            } else if ((var3 & var9) == var9) {
               int var10 = var9 - ((-1431655766 & var9) >>> 1);
               var10 = (var10 & 858993459) + (var10 >>> 2 & 858993459);
               var10 = var10 + (var10 >>> 4) & 252645135;
               var10 += var10 >>> 8;
               var10 += var10 >>> 16;
               var10 &= 255;
               if (var10 > var4) {
                  var5 = var8;
                  var4 = var10;
               }
            }
         }

         if (var5 != -1) {
            var2[0] = var5;
            return var1[var5];
         }

         if (var6 != -1) {
            var2[0] = var6;
            return var1[var6];
         }
      }

      var2[0] = -1;
      return null;
   }

   static {
      DEFAULT_COLOR = new ColorUIResource(Color.BLACK);
      STATE_COMPARATOR = new Comparator<NimbusStyle.RuntimeState>() {
         public int compare(NimbusStyle.RuntimeState var1, NimbusStyle.RuntimeState var2) {
            return var1.state - var2.state;
         }
      };
   }

   private static final class CacheKey {
      private String key;
      private int xstate;

      CacheKey(Object var1, int var2) {
         this.init(var1, var2);
      }

      void init(Object var1, int var2) {
         this.key = var1.toString();
         this.xstate = var2;
      }

      public boolean equals(Object var1) {
         NimbusStyle.CacheKey var2 = (NimbusStyle.CacheKey)var1;
         if (var1 == null) {
            return false;
         } else if (this.xstate != var2.xstate) {
            return false;
         } else {
            return this.key.equals(var2.key);
         }
      }

      public int hashCode() {
         byte var1 = 3;
         int var2 = 29 * var1 + this.key.hashCode();
         var2 = 29 * var2 + this.xstate;
         return var2;
      }
   }

   private static final class Values {
      State[] stateTypes;
      NimbusStyle.RuntimeState[] states;
      Insets contentMargins;
      UIDefaults defaults;
      Map<NimbusStyle.CacheKey, Object> cache;

      private Values() {
         this.stateTypes = null;
         this.states = null;
         this.defaults = new UIDefaults(10, 0.7F);
         this.cache = new HashMap();
      }

      // $FF: synthetic method
      Values(Object var1) {
         this();
      }
   }

   private final class RuntimeState implements Cloneable {
      int state;
      Painter backgroundPainter;
      Painter foregroundPainter;
      Painter borderPainter;
      String stateName;
      UIDefaults defaults;

      private RuntimeState(int var2, String var3) {
         this.defaults = new UIDefaults(10, 0.7F);
         this.state = var2;
         this.stateName = var3;
      }

      public String toString() {
         return this.stateName;
      }

      public NimbusStyle.RuntimeState clone() {
         NimbusStyle.RuntimeState var1 = NimbusStyle.this.new RuntimeState(this.state, this.stateName);
         var1.backgroundPainter = this.backgroundPainter;
         var1.foregroundPainter = this.foregroundPainter;
         var1.borderPainter = this.borderPainter;
         var1.defaults.putAll(this.defaults);
         return var1;
      }

      // $FF: synthetic method
      RuntimeState(int var2, String var3, Object var4) {
         this(var2, var3);
      }
   }
}
