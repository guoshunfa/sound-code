package sun.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public class DefaultSynthStyle extends SynthStyle implements Cloneable {
   private static final Object PENDING = new Object();
   private boolean opaque;
   private Insets insets;
   private DefaultSynthStyle.StateInfo[] states;
   private Map data;
   private Font font;
   private SynthGraphicsUtils synthGraphics;
   private SynthPainter painter;

   public DefaultSynthStyle() {
   }

   public DefaultSynthStyle(DefaultSynthStyle var1) {
      this.opaque = var1.opaque;
      if (var1.insets != null) {
         this.insets = new Insets(var1.insets.top, var1.insets.left, var1.insets.bottom, var1.insets.right);
      }

      if (var1.states != null) {
         this.states = new DefaultSynthStyle.StateInfo[var1.states.length];

         for(int var2 = var1.states.length - 1; var2 >= 0; --var2) {
            this.states[var2] = (DefaultSynthStyle.StateInfo)var1.states[var2].clone();
         }
      }

      if (var1.data != null) {
         this.data = new HashMap();
         this.data.putAll(var1.data);
      }

      this.font = var1.font;
      this.synthGraphics = var1.synthGraphics;
      this.painter = var1.painter;
   }

   public DefaultSynthStyle(Insets var1, boolean var2, DefaultSynthStyle.StateInfo[] var3, Map var4) {
      this.insets = var1;
      this.opaque = var2;
      this.states = var3;
      this.data = var4;
   }

   public Color getColor(SynthContext var1, ColorType var2) {
      return this.getColor(var1.getComponent(), var1.getRegion(), var1.getComponentState(), var2);
   }

   public Color getColor(JComponent var1, Region var2, int var3, ColorType var4) {
      Color var5;
      if (!var2.isSubregion() && var3 == 1) {
         if (var4 == ColorType.BACKGROUND) {
            return var1.getBackground();
         }

         if (var4 == ColorType.FOREGROUND) {
            return var1.getForeground();
         }

         if (var4 == ColorType.TEXT_FOREGROUND) {
            var5 = var1.getForeground();
            if (!(var5 instanceof UIResource)) {
               return var5;
            }
         }
      }

      var5 = this.getColorForState(var1, var2, var3, var4);
      if (var5 == null) {
         if (var4 == ColorType.BACKGROUND || var4 == ColorType.TEXT_BACKGROUND) {
            return var1.getBackground();
         }

         if (var4 == ColorType.FOREGROUND || var4 == ColorType.TEXT_FOREGROUND) {
            return var1.getForeground();
         }
      }

      return var5;
   }

   protected Color getColorForState(SynthContext var1, ColorType var2) {
      return this.getColorForState(var1.getComponent(), var1.getRegion(), var1.getComponentState(), var2);
   }

   protected Color getColorForState(JComponent var1, Region var2, int var3, ColorType var4) {
      DefaultSynthStyle.StateInfo var5 = this.getStateInfo(var3);
      Color var6;
      if (var5 != null && (var6 = var5.getColor(var4)) != null) {
         return var6;
      } else {
         if (var5 == null || var5.getComponentState() != 0) {
            var5 = this.getStateInfo(0);
            if (var5 != null) {
               return var5.getColor(var4);
            }
         }

         return null;
      }
   }

   public void setFont(Font var1) {
      this.font = var1;
   }

   public Font getFont(SynthContext var1) {
      return this.getFont(var1.getComponent(), var1.getRegion(), var1.getComponentState());
   }

   public Font getFont(JComponent var1, Region var2, int var3) {
      if (!var2.isSubregion() && var3 == 1) {
         return var1.getFont();
      } else {
         Font var4 = var1.getFont();
         return var4 != null && !(var4 instanceof UIResource) ? var4 : this.getFontForState(var1, var2, var3);
      }
   }

   protected Font getFontForState(JComponent var1, Region var2, int var3) {
      if (var1 == null) {
         return this.font;
      } else {
         DefaultSynthStyle.StateInfo var4 = this.getStateInfo(var3);
         Font var5;
         if (var4 != null && (var5 = var4.getFont()) != null) {
            return var5;
         } else {
            if (var4 == null || var4.getComponentState() != 0) {
               var4 = this.getStateInfo(0);
               if (var4 != null && (var5 = var4.getFont()) != null) {
                  return var5;
               }
            }

            return this.font;
         }
      }
   }

   protected Font getFontForState(SynthContext var1) {
      return this.getFontForState(var1.getComponent(), var1.getRegion(), var1.getComponentState());
   }

   public void setGraphicsUtils(SynthGraphicsUtils var1) {
      this.synthGraphics = var1;
   }

   public SynthGraphicsUtils getGraphicsUtils(SynthContext var1) {
      return this.synthGraphics == null ? super.getGraphicsUtils(var1) : this.synthGraphics;
   }

   public void setInsets(Insets var1) {
      this.insets = var1;
   }

   public Insets getInsets(SynthContext var1, Insets var2) {
      if (var2 == null) {
         var2 = new Insets(0, 0, 0, 0);
      }

      if (this.insets != null) {
         var2.left = this.insets.left;
         var2.right = this.insets.right;
         var2.top = this.insets.top;
         var2.bottom = this.insets.bottom;
      } else {
         var2.left = var2.right = var2.top = var2.bottom = 0;
      }

      return var2;
   }

   public void setPainter(SynthPainter var1) {
      this.painter = var1;
   }

   public SynthPainter getPainter(SynthContext var1) {
      return this.painter;
   }

   public void setOpaque(boolean var1) {
      this.opaque = var1;
   }

   public boolean isOpaque(SynthContext var1) {
      return this.opaque;
   }

   public void setData(Map var1) {
      this.data = var1;
   }

   public Map getData() {
      return this.data;
   }

   public Object get(SynthContext var1, Object var2) {
      DefaultSynthStyle.StateInfo var3 = this.getStateInfo(var1.getComponentState());
      if (var3 != null && var3.getData() != null && this.getKeyFromData(var3.getData(), var2) != null) {
         return this.getKeyFromData(var3.getData(), var2);
      } else {
         var3 = this.getStateInfo(0);
         if (var3 != null && var3.getData() != null && this.getKeyFromData(var3.getData(), var2) != null) {
            return this.getKeyFromData(var3.getData(), var2);
         } else {
            return this.getKeyFromData(this.data, var2) != null ? this.getKeyFromData(this.data, var2) : this.getDefaultValue(var1, var2);
         }
      }
   }

   private Object getKeyFromData(Map var1, Object var2) {
      Object var3 = null;
      if (var1 != null) {
         synchronized(var1) {
            var3 = var1.get(var2);
         }

         while(var3 == PENDING) {
            synchronized(var1) {
               try {
                  var1.wait();
               } catch (InterruptedException var11) {
               }

               var3 = var1.get(var2);
            }
         }

         if (var3 instanceof UIDefaults.LazyValue) {
            synchronized(var1) {
               var1.put(var2, PENDING);
            }

            var3 = ((UIDefaults.LazyValue)var3).createValue((UIDefaults)null);
            synchronized(var1) {
               var1.put(var2, var3);
               var1.notifyAll();
            }
         }
      }

      return var3;
   }

   public Object getDefaultValue(SynthContext var1, Object var2) {
      return super.get(var1, var2);
   }

   public Object clone() {
      DefaultSynthStyle var1;
      try {
         var1 = (DefaultSynthStyle)super.clone();
      } catch (CloneNotSupportedException var3) {
         return null;
      }

      if (this.states != null) {
         var1.states = new DefaultSynthStyle.StateInfo[this.states.length];

         for(int var2 = this.states.length - 1; var2 >= 0; --var2) {
            var1.states[var2] = (DefaultSynthStyle.StateInfo)this.states[var2].clone();
         }
      }

      if (this.data != null) {
         var1.data = new HashMap();
         var1.data.putAll(this.data);
      }

      return var1;
   }

   public DefaultSynthStyle addTo(DefaultSynthStyle var1) {
      if (this.insets != null) {
         var1.insets = this.insets;
      }

      if (this.font != null) {
         var1.font = this.font;
      }

      if (this.painter != null) {
         var1.painter = this.painter;
      }

      if (this.synthGraphics != null) {
         var1.synthGraphics = this.synthGraphics;
      }

      var1.opaque = this.opaque;
      if (this.states != null) {
         int var2;
         if (var1.states == null) {
            var1.states = new DefaultSynthStyle.StateInfo[this.states.length];

            for(var2 = this.states.length - 1; var2 >= 0; --var2) {
               if (this.states[var2] != null) {
                  var1.states[var2] = (DefaultSynthStyle.StateInfo)this.states[var2].clone();
               }
            }
         } else {
            var2 = 0;
            int var3 = 0;
            int var4 = var1.states.length;

            int var6;
            int var8;
            for(int var5 = this.states.length - 1; var5 >= 0; --var5) {
               var6 = this.states[var5].getComponentState();
               boolean var7 = false;

               for(var8 = var4 - 1 - var3; var8 >= 0; --var8) {
                  if (var6 == var1.states[var8].getComponentState()) {
                     var1.states[var8] = this.states[var5].addTo(var1.states[var8]);
                     DefaultSynthStyle.StateInfo var9 = var1.states[var4 - 1 - var3];
                     var1.states[var4 - 1 - var3] = var1.states[var8];
                     var1.states[var8] = var9;
                     ++var3;
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  ++var2;
               }
            }

            if (var2 != 0) {
               DefaultSynthStyle.StateInfo[] var11 = new DefaultSynthStyle.StateInfo[var2 + var4];
               var6 = var4;
               System.arraycopy(var1.states, 0, var11, 0, var4);

               for(int var12 = this.states.length - 1; var12 >= 0; --var12) {
                  var8 = this.states[var12].getComponentState();
                  boolean var13 = false;

                  for(int var10 = var4 - 1; var10 >= 0; --var10) {
                     if (var8 == var1.states[var10].getComponentState()) {
                        var13 = true;
                        break;
                     }
                  }

                  if (!var13) {
                     var11[var6++] = (DefaultSynthStyle.StateInfo)this.states[var12].clone();
                  }
               }

               var1.states = var11;
            }
         }
      }

      if (this.data != null) {
         if (var1.data == null) {
            var1.data = new HashMap();
         }

         var1.data.putAll(this.data);
      }

      return var1;
   }

   public void setStateInfo(DefaultSynthStyle.StateInfo[] var1) {
      this.states = var1;
   }

   public DefaultSynthStyle.StateInfo[] getStateInfo() {
      return this.states;
   }

   public DefaultSynthStyle.StateInfo getStateInfo(int var1) {
      if (this.states != null) {
         int var2 = 0;
         int var3 = -1;
         int var4 = -1;
         int var5;
         if (var1 == 0) {
            for(var5 = this.states.length - 1; var5 >= 0; --var5) {
               if (this.states[var5].getComponentState() == 0) {
                  return this.states[var5];
               }
            }

            return null;
         }

         for(var5 = this.states.length - 1; var5 >= 0; --var5) {
            int var6 = this.states[var5].getComponentState();
            if (var6 == 0) {
               if (var4 == -1) {
                  var4 = var5;
               }
            } else if ((var1 & var6) == var6) {
               int var7 = var6 - ((-1431655766 & var6) >>> 1);
               var7 = (var7 & 858993459) + (var7 >>> 2 & 858993459);
               var7 = var7 + (var7 >>> 4) & 252645135;
               var7 += var7 >>> 8;
               var7 += var7 >>> 16;
               var7 &= 255;
               if (var7 > var2) {
                  var3 = var5;
                  var2 = var7;
               }
            }
         }

         if (var3 != -1) {
            return this.states[var3];
         }

         if (var4 != -1) {
            return this.states[var4];
         }
      }

      return null;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(super.toString()).append(',');
      var1.append("data=").append((Object)this.data).append(',');
      var1.append("font=").append((Object)this.font).append(',');
      var1.append("insets=").append((Object)this.insets).append(',');
      var1.append("synthGraphics=").append((Object)this.synthGraphics).append(',');
      var1.append("painter=").append((Object)this.painter).append(',');
      DefaultSynthStyle.StateInfo[] var2 = this.getStateInfo();
      if (var2 != null) {
         var1.append("states[");
         DefaultSynthStyle.StateInfo[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            DefaultSynthStyle.StateInfo var6 = var3[var5];
            var1.append(var6.toString()).append(',');
         }

         var1.append(']').append(',');
      }

      var1.deleteCharAt(var1.length() - 1);
      return var1.toString();
   }

   public static class StateInfo {
      private Map data;
      private Font font;
      private Color[] colors;
      private int state;

      public StateInfo() {
      }

      public StateInfo(int var1, Font var2, Color[] var3) {
         this.state = var1;
         this.font = var2;
         this.colors = var3;
      }

      public StateInfo(DefaultSynthStyle.StateInfo var1) {
         this.state = var1.state;
         this.font = var1.font;
         if (var1.data != null) {
            if (this.data == null) {
               this.data = new HashMap();
            }

            this.data.putAll(var1.data);
         }

         if (var1.colors != null) {
            this.colors = new Color[var1.colors.length];
            System.arraycopy(var1.colors, 0, this.colors, 0, var1.colors.length);
         }

      }

      public Map getData() {
         return this.data;
      }

      public void setData(Map var1) {
         this.data = var1;
      }

      public void setFont(Font var1) {
         this.font = var1;
      }

      public Font getFont() {
         return this.font;
      }

      public void setColors(Color[] var1) {
         this.colors = var1;
      }

      public Color[] getColors() {
         return this.colors;
      }

      public Color getColor(ColorType var1) {
         if (this.colors != null) {
            int var2 = var1.getID();
            if (var2 < this.colors.length) {
               return this.colors[var2];
            }
         }

         return null;
      }

      public DefaultSynthStyle.StateInfo addTo(DefaultSynthStyle.StateInfo var1) {
         if (this.font != null) {
            var1.font = this.font;
         }

         if (this.data != null) {
            if (var1.data == null) {
               var1.data = new HashMap();
            }

            var1.data.putAll(this.data);
         }

         if (this.colors != null) {
            if (var1.colors == null) {
               var1.colors = new Color[this.colors.length];
               System.arraycopy(this.colors, 0, var1.colors, 0, this.colors.length);
            } else {
               if (var1.colors.length < this.colors.length) {
                  Color[] var2 = var1.colors;
                  var1.colors = new Color[this.colors.length];
                  System.arraycopy(var2, 0, var1.colors, 0, var2.length);
               }

               for(int var3 = this.colors.length - 1; var3 >= 0; --var3) {
                  if (this.colors[var3] != null) {
                     var1.colors[var3] = this.colors[var3];
                  }
               }
            }
         }

         return var1;
      }

      public void setComponentState(int var1) {
         this.state = var1;
      }

      public int getComponentState() {
         return this.state;
      }

      private int getMatchCount(int var1) {
         var1 &= this.state;
         var1 -= (-1431655766 & var1) >>> 1;
         var1 = (var1 & 858993459) + (var1 >>> 2 & 858993459);
         var1 = var1 + (var1 >>> 4) & 252645135;
         var1 += var1 >>> 8;
         var1 += var1 >>> 16;
         return var1 & 255;
      }

      public Object clone() {
         return new DefaultSynthStyle.StateInfo(this);
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();
         var1.append(super.toString()).append(',');
         var1.append("state=").append(Integer.toString(this.state)).append(',');
         var1.append("font=").append((Object)this.font).append(',');
         if (this.colors != null) {
            var1.append("colors=").append((Object)Arrays.asList(this.colors)).append(',');
         }

         return var1.toString();
      }
   }
}
