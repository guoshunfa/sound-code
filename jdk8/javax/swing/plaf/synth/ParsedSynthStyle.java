package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import sun.swing.plaf.synth.DefaultSynthStyle;

class ParsedSynthStyle extends DefaultSynthStyle {
   private static SynthPainter DELEGATING_PAINTER_INSTANCE = new ParsedSynthStyle.DelegatingPainter();
   private ParsedSynthStyle.PainterInfo[] _painters;

   private static ParsedSynthStyle.PainterInfo[] mergePainterInfo(ParsedSynthStyle.PainterInfo[] var0, ParsedSynthStyle.PainterInfo[] var1) {
      if (var0 == null) {
         return var1;
      } else if (var1 == null) {
         return var0;
      } else {
         int var2 = var0.length;
         int var3 = var1.length;
         int var4 = 0;
         ParsedSynthStyle.PainterInfo[] var5 = new ParsedSynthStyle.PainterInfo[var2 + var3];
         System.arraycopy(var0, 0, var5, 0, var2);

         for(int var6 = 0; var6 < var3; ++var6) {
            boolean var7 = false;

            for(int var8 = 0; var8 < var2 - var4; ++var8) {
               if (var1[var6].equalsPainter(var0[var8])) {
                  var5[var8] = var1[var6];
                  ++var4;
                  var7 = true;
                  break;
               }
            }

            if (!var7) {
               var5[var2 + var6 - var4] = var1[var6];
            }
         }

         if (var4 > 0) {
            ParsedSynthStyle.PainterInfo[] var9 = var5;
            var5 = new ParsedSynthStyle.PainterInfo[var5.length - var4];
            System.arraycopy(var9, 0, var5, 0, var5.length);
         }

         return var5;
      }
   }

   public ParsedSynthStyle() {
   }

   public ParsedSynthStyle(DefaultSynthStyle var1) {
      super(var1);
      if (var1 instanceof ParsedSynthStyle) {
         ParsedSynthStyle var2 = (ParsedSynthStyle)var1;
         if (var2._painters != null) {
            this._painters = var2._painters;
         }
      }

   }

   public SynthPainter getPainter(SynthContext var1) {
      return DELEGATING_PAINTER_INSTANCE;
   }

   public void setPainters(ParsedSynthStyle.PainterInfo[] var1) {
      this._painters = var1;
   }

   public DefaultSynthStyle addTo(DefaultSynthStyle var1) {
      if (!(var1 instanceof ParsedSynthStyle)) {
         var1 = new ParsedSynthStyle((DefaultSynthStyle)var1);
      }

      ParsedSynthStyle var2 = (ParsedSynthStyle)super.addTo((DefaultSynthStyle)var1);
      var2._painters = mergePainterInfo(var2._painters, this._painters);
      return var2;
   }

   private SynthPainter getBestPainter(SynthContext var1, String var2, int var3) {
      ParsedSynthStyle.StateInfo var4 = (ParsedSynthStyle.StateInfo)this.getStateInfo(var1.getComponentState());
      SynthPainter var5;
      if (var4 != null && (var5 = this.getBestPainter(var4.getPainters(), var2, var3)) != null) {
         return var5;
      } else {
         return (var5 = this.getBestPainter(this._painters, var2, var3)) != null ? var5 : SynthPainter.NULL_PAINTER;
      }
   }

   private SynthPainter getBestPainter(ParsedSynthStyle.PainterInfo[] var1, String var2, int var3) {
      if (var1 != null) {
         SynthPainter var4 = null;
         SynthPainter var5 = null;

         for(int var6 = var1.length - 1; var6 >= 0; --var6) {
            ParsedSynthStyle.PainterInfo var7 = var1[var6];
            if (var7.getMethod() == var2) {
               if (var7.getDirection() == var3) {
                  return var7.getPainter();
               }

               if (var5 == null && var7.getDirection() == -1) {
                  var5 = var7.getPainter();
               }
            } else if (var4 == null && var7.getMethod() == null) {
               var4 = var7.getPainter();
            }
         }

         if (var5 != null) {
            return var5;
         } else {
            return var4;
         }
      } else {
         return null;
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(super.toString());
      if (this._painters != null) {
         var1.append(",painters=[");

         for(int var2 = 0; var2 < this._painters.length; ++var2) {
            var1.append(this._painters[var2].toString());
         }

         var1.append("]");
      }

      return var1.toString();
   }

   private static class DelegatingPainter extends SynthPainter {
      private DelegatingPainter() {
      }

      private static SynthPainter getPainter(SynthContext var0, String var1, int var2) {
         return ((ParsedSynthStyle)var0.getStyle()).getBestPainter(var0, var1, var2);
      }

      public void paintArrowButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "arrowbuttonbackground", -1).paintArrowButtonBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintArrowButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "arrowbuttonborder", -1).paintArrowButtonBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintArrowButtonForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "arrowbuttonforeground", var7).paintArrowButtonForeground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "buttonbackground", -1).paintButtonBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "buttonborder", -1).paintButtonBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintCheckBoxMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "checkboxmenuitembackground", -1).paintCheckBoxMenuItemBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintCheckBoxMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "checkboxmenuitemborder", -1).paintCheckBoxMenuItemBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintCheckBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "checkboxbackground", -1).paintCheckBoxBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintCheckBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "checkboxborder", -1).paintCheckBoxBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintColorChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "colorchooserbackground", -1).paintColorChooserBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintColorChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "colorchooserborder", -1).paintColorChooserBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintComboBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "comboboxbackground", -1).paintComboBoxBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintComboBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "comboboxborder", -1).paintComboBoxBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintDesktopIconBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "desktopiconbackground", -1).paintDesktopIconBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintDesktopIconBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "desktopiconborder", -1).paintDesktopIconBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintDesktopPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "desktoppanebackground", -1).paintDesktopPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintDesktopPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "desktoppaneborder", -1).paintDesktopPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintEditorPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "editorpanebackground", -1).paintEditorPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintEditorPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "editorpaneborder", -1).paintEditorPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintFileChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "filechooserbackground", -1).paintFileChooserBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintFileChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "filechooserborder", -1).paintFileChooserBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintFormattedTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "formattedtextfieldbackground", -1).paintFormattedTextFieldBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintFormattedTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "formattedtextfieldborder", -1).paintFormattedTextFieldBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintInternalFrameTitlePaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "internalframetitlepanebackground", -1).paintInternalFrameTitlePaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintInternalFrameTitlePaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "internalframetitlepaneborder", -1).paintInternalFrameTitlePaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintInternalFrameBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "internalframebackground", -1).paintInternalFrameBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintInternalFrameBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "internalframeborder", -1).paintInternalFrameBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintLabelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "labelbackground", -1).paintLabelBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintLabelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "labelborder", -1).paintLabelBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintListBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "listbackground", -1).paintListBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintListBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "listborder", -1).paintListBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintMenuBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "menubarbackground", -1).paintMenuBarBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintMenuBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "menubarborder", -1).paintMenuBarBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "menuitembackground", -1).paintMenuItemBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "menuitemborder", -1).paintMenuItemBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "menubackground", -1).paintMenuBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "menuborder", -1).paintMenuBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintOptionPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "optionpanebackground", -1).paintOptionPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintOptionPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "optionpaneborder", -1).paintOptionPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintPanelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "panelbackground", -1).paintPanelBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintPanelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "panelborder", -1).paintPanelBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintPasswordFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "passwordfieldbackground", -1).paintPasswordFieldBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintPasswordFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "passwordfieldborder", -1).paintPasswordFieldBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintPopupMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "popupmenubackground", -1).paintPopupMenuBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintPopupMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "popupmenuborder", -1).paintPopupMenuBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "progressbarbackground", -1).paintProgressBarBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "progressbarbackground", var7).paintProgressBarBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "progressbarborder", -1).paintProgressBarBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "progressbarborder", var7).paintProgressBarBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintProgressBarForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "progressbarforeground", var7).paintProgressBarForeground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintRadioButtonMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "radiobuttonmenuitembackground", -1).paintRadioButtonMenuItemBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintRadioButtonMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "radiobuttonmenuitemborder", -1).paintRadioButtonMenuItemBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintRadioButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "radiobuttonbackground", -1).paintRadioButtonBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintRadioButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "radiobuttonborder", -1).paintRadioButtonBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintRootPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "rootpanebackground", -1).paintRootPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintRootPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "rootpaneborder", -1).paintRootPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "scrollbarbackground", -1).paintScrollBarBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "scrollbarbackground", var7).paintScrollBarBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "scrollbarborder", -1).paintScrollBarBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "scrollbarborder", var7).paintScrollBarBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintScrollBarThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "scrollbarthumbbackground", var7).paintScrollBarThumbBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintScrollBarThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "scrollbarthumbborder", var7).paintScrollBarThumbBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "scrollbartrackbackground", -1).paintScrollBarTrackBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "scrollbartrackbackground", var7).paintScrollBarTrackBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "scrollbartrackborder", -1).paintScrollBarTrackBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "scrollbartrackborder", var7).paintScrollBarTrackBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintScrollPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "scrollpanebackground", -1).paintScrollPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintScrollPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "scrollpaneborder", -1).paintScrollPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "separatorbackground", -1).paintSeparatorBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "separatorbackground", var7).paintSeparatorBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "separatorborder", -1).paintSeparatorBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "separatorborder", var7).paintSeparatorBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSeparatorForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "separatorforeground", var7).paintSeparatorForeground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "sliderbackground", -1).paintSliderBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "sliderbackground", var7).paintSliderBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "sliderborder", -1).paintSliderBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "sliderborder", var7).paintSliderBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSliderThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "sliderthumbbackground", var7).paintSliderThumbBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSliderThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "sliderthumbborder", var7).paintSliderThumbBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "slidertrackbackground", -1).paintSliderTrackBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "slidertrackbackground", var7).paintSliderTrackBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "slidertrackborder", -1).paintSliderTrackBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "slidertrackborder", var7).paintSliderTrackBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSpinnerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "spinnerbackground", -1).paintSpinnerBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintSpinnerBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "spinnerborder", -1).paintSpinnerBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "splitpanedividerbackground", -1).paintSplitPaneDividerBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "splitpanedividerbackground", var7).paintSplitPaneDividerBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSplitPaneDividerForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "splitpanedividerforeground", var7).paintSplitPaneDividerForeground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSplitPaneDragDivider(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "splitpanedragdivider", var7).paintSplitPaneDragDivider(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintSplitPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "splitpanebackground", -1).paintSplitPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintSplitPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "splitpaneborder", -1).paintSplitPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTabbedPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tabbedpanebackground", -1).paintTabbedPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTabbedPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tabbedpaneborder", -1).paintTabbedPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tabbedpanetabareabackground", -1).paintTabbedPaneTabAreaBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "tabbedpanetabareabackground", var7).paintTabbedPaneTabAreaBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tabbedpanetabareaborder", -1).paintTabbedPaneTabAreaBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "tabbedpanetabareaborder", var7).paintTabbedPaneTabAreaBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "tabbedpanetabbackground", -1).paintTabbedPaneTabBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         getPainter(var1, "tabbedpanetabbackground", var8).paintTabbedPaneTabBackground(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "tabbedpanetabborder", -1).paintTabbedPaneTabBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         getPainter(var1, "tabbedpanetabborder", var8).paintTabbedPaneTabBorder(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public void paintTabbedPaneContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tabbedpanecontentbackground", -1).paintTabbedPaneContentBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTabbedPaneContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tabbedpanecontentborder", -1).paintTabbedPaneContentBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTableHeaderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tableheaderbackground", -1).paintTableHeaderBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTableHeaderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tableheaderborder", -1).paintTableHeaderBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTableBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tablebackground", -1).paintTableBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTableBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tableborder", -1).paintTableBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTextAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "textareabackground", -1).paintTextAreaBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTextAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "textareaborder", -1).paintTextAreaBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTextPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "textpanebackground", -1).paintTextPaneBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTextPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "textpaneborder", -1).paintTextPaneBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "textfieldbackground", -1).paintTextFieldBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "textfieldborder", -1).paintTextFieldBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintToggleButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "togglebuttonbackground", -1).paintToggleButtonBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintToggleButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "togglebuttonborder", -1).paintToggleButtonBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "toolbarbackground", -1).paintToolBarBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "toolbarbackground", var7).paintToolBarBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "toolbarborder", -1).paintToolBarBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "toolbarborder", var7).paintToolBarBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "toolbarcontentbackground", -1).paintToolBarContentBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "toolbarcontentbackground", var7).paintToolBarContentBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "toolbarcontentborder", -1).paintToolBarContentBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "toolbarcontentborder", var7).paintToolBarContentBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "toolbardragwindowbackground", -1).paintToolBarDragWindowBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "toolbardragwindowbackground", var7).paintToolBarDragWindowBackground(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "toolbardragwindowborder", -1).paintToolBarDragWindowBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         getPainter(var1, "toolbardragwindowborder", var7).paintToolBarDragWindowBorder(var1, var2, var3, var4, var5, var6, var7);
      }

      public void paintToolTipBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tooltipbackground", -1).paintToolTipBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintToolTipBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "tooltipborder", -1).paintToolTipBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTreeBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "treebackground", -1).paintTreeBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTreeBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "treeborder", -1).paintTreeBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTreeCellBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "treecellbackground", -1).paintTreeCellBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintTreeCellBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "treecellborder", -1).paintTreeCellBorder(var1, var2, var3, var4, var5, var6);
      }

      public void paintTreeCellFocus(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "treecellfocus", -1).paintTreeCellFocus(var1, var2, var3, var4, var5, var6);
      }

      public void paintViewportBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "viewportbackground", -1).paintViewportBackground(var1, var2, var3, var4, var5, var6);
      }

      public void paintViewportBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         getPainter(var1, "viewportborder", -1).paintViewportBorder(var1, var2, var3, var4, var5, var6);
      }

      // $FF: synthetic method
      DelegatingPainter(Object var1) {
         this();
      }
   }

   private static class AggregatePainter extends SynthPainter {
      private List<SynthPainter> painters = new LinkedList();

      AggregatePainter(SynthPainter var1) {
         this.painters.add(var1);
      }

      void addPainter(SynthPainter var1) {
         if (var1 != null) {
            this.painters.add(var1);
         }

      }

      public void paintArrowButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintArrowButtonBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintArrowButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintArrowButtonBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintArrowButtonForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintArrowButtonForeground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintButtonBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintButtonBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintCheckBoxMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintCheckBoxMenuItemBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintCheckBoxMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintCheckBoxMenuItemBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintCheckBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintCheckBoxBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintCheckBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintCheckBoxBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintColorChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintColorChooserBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintColorChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintColorChooserBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintComboBoxBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintComboBoxBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintComboBoxBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintComboBoxBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintDesktopIconBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintDesktopIconBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintDesktopIconBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintDesktopIconBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintDesktopPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintDesktopPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintDesktopPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintDesktopPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintEditorPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintEditorPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintEditorPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintEditorPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintFileChooserBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintFileChooserBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintFileChooserBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintFileChooserBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintFormattedTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintFormattedTextFieldBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintFormattedTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintFormattedTextFieldBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintInternalFrameTitlePaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintInternalFrameTitlePaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintInternalFrameTitlePaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintInternalFrameTitlePaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintInternalFrameBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintInternalFrameBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintInternalFrameBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintInternalFrameBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintLabelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintLabelBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintLabelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintLabelBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintListBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintListBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintListBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintListBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintMenuBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintMenuBarBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintMenuBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintMenuBarBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintMenuItemBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintMenuItemBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintMenuBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintMenuBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintOptionPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintOptionPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintOptionPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintOptionPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintPanelBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintPanelBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintPanelBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintPanelBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintPasswordFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintPasswordFieldBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintPasswordFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintPasswordFieldBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintPopupMenuBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintPopupMenuBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintPopupMenuBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintPopupMenuBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintProgressBarBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintProgressBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintProgressBarBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintProgressBarBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintProgressBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintProgressBarBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintProgressBarForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintProgressBarForeground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintRadioButtonMenuItemBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintRadioButtonMenuItemBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintRadioButtonMenuItemBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintRadioButtonMenuItemBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintRadioButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintRadioButtonBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintRadioButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintRadioButtonBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintRootPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintRootPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintRootPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintRootPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintScrollBarBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintScrollBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintScrollBarBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintScrollBarBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintScrollBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintScrollBarBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintScrollBarThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintScrollBarThumbBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintScrollBarThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintScrollBarThumbBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintScrollBarTrackBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintScrollBarTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintScrollBarTrackBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintScrollBarTrackBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintScrollBarTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintScrollBarTrackBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintScrollPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintScrollPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintScrollPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintScrollPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSeparatorBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSeparatorBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSeparatorBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSeparatorBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSeparatorBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSeparatorBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSeparatorForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSeparatorForeground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSliderBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSliderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSliderBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSliderBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSliderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSliderBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSliderThumbBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSliderThumbBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSliderThumbBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSliderThumbBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSliderTrackBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSliderTrackBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSliderTrackBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSliderTrackBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSliderTrackBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSliderTrackBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSpinnerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSpinnerBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSpinnerBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSpinnerBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSplitPaneDividerBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSplitPaneDividerBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSplitPaneDividerBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSplitPaneDividerForeground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSplitPaneDividerForeground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSplitPaneDragDivider(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintSplitPaneDragDivider(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintSplitPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSplitPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintSplitPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintSplitPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTabbedPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTabbedPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTabbedPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTabbedPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTabbedPaneTabAreaBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTabbedPaneTabAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintTabbedPaneTabAreaBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTabbedPaneTabAreaBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTabbedPaneTabAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintTabbedPaneTabAreaBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintTabbedPaneTabBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintTabbedPaneTabBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         Iterator var9 = this.painters.iterator();

         while(var9.hasNext()) {
            SynthPainter var10 = (SynthPainter)var9.next();
            var10.paintTabbedPaneTabBackground(var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }

      public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintTabbedPaneTabBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintTabbedPaneTabBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         Iterator var9 = this.painters.iterator();

         while(var9.hasNext()) {
            SynthPainter var10 = (SynthPainter)var9.next();
            var10.paintTabbedPaneTabBorder(var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }

      public void paintTabbedPaneContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTabbedPaneContentBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTabbedPaneContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTabbedPaneContentBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTableHeaderBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTableHeaderBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTableHeaderBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTableHeaderBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTableBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTableBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTableBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTableBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTextAreaBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTextAreaBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTextAreaBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTextAreaBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTextPaneBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTextPaneBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTextPaneBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTextPaneBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTextFieldBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTextFieldBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTextFieldBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTextFieldBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToggleButtonBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToggleButtonBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToggleButtonBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToggleButtonBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolBarBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintToolBarBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolBarBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintToolBarBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolBarContentBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarContentBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintToolBarContentBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolBarContentBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarContentBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintToolBarContentBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolBarDragWindowBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarDragWindowBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintToolBarDragWindowBackground(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolBarDragWindowBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolBarDragWindowBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6, int var7) {
         Iterator var8 = this.painters.iterator();

         while(var8.hasNext()) {
            SynthPainter var9 = (SynthPainter)var8.next();
            var9.paintToolBarDragWindowBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }

      public void paintToolTipBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolTipBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintToolTipBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintToolTipBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTreeBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTreeBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTreeBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTreeBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTreeCellBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTreeCellBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTreeCellBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTreeCellBorder(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintTreeCellFocus(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintTreeCellFocus(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintViewportBackground(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintViewportBackground(var1, var2, var3, var4, var5, var6);
         }

      }

      public void paintViewportBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
         Iterator var7 = this.painters.iterator();

         while(var7.hasNext()) {
            SynthPainter var8 = (SynthPainter)var7.next();
            var8.paintViewportBorder(var1, var2, var3, var4, var5, var6);
         }

      }
   }

   static class PainterInfo {
      private String _method;
      private SynthPainter _painter;
      private int _direction;

      PainterInfo(String var1, SynthPainter var2, int var3) {
         if (var1 != null) {
            this._method = var1.intern();
         }

         this._painter = var2;
         this._direction = var3;
      }

      void addPainter(SynthPainter var1) {
         if (!(this._painter instanceof ParsedSynthStyle.AggregatePainter)) {
            this._painter = new ParsedSynthStyle.AggregatePainter(this._painter);
         }

         ((ParsedSynthStyle.AggregatePainter)this._painter).addPainter(var1);
      }

      String getMethod() {
         return this._method;
      }

      SynthPainter getPainter() {
         return this._painter;
      }

      int getDirection() {
         return this._direction;
      }

      boolean equalsPainter(ParsedSynthStyle.PainterInfo var1) {
         return this._method == var1._method && this._direction == var1._direction;
      }

      public String toString() {
         return "PainterInfo {method=" + this._method + ",direction=" + this._direction + ",painter=" + this._painter + "}";
      }
   }

   static class StateInfo extends DefaultSynthStyle.StateInfo {
      private ParsedSynthStyle.PainterInfo[] _painterInfo;

      public StateInfo() {
      }

      public StateInfo(DefaultSynthStyle.StateInfo var1) {
         super(var1);
         if (var1 instanceof ParsedSynthStyle.StateInfo) {
            this._painterInfo = ((ParsedSynthStyle.StateInfo)var1)._painterInfo;
         }

      }

      public void setPainters(ParsedSynthStyle.PainterInfo[] var1) {
         this._painterInfo = var1;
      }

      public ParsedSynthStyle.PainterInfo[] getPainters() {
         return this._painterInfo;
      }

      public Object clone() {
         return new ParsedSynthStyle.StateInfo(this);
      }

      public DefaultSynthStyle.StateInfo addTo(DefaultSynthStyle.StateInfo var1) {
         Object var3;
         if (!(var1 instanceof ParsedSynthStyle.StateInfo)) {
            var3 = new ParsedSynthStyle.StateInfo(var1);
         } else {
            var3 = super.addTo(var1);
            ParsedSynthStyle.StateInfo var2 = (ParsedSynthStyle.StateInfo)var3;
            var2._painterInfo = ParsedSynthStyle.mergePainterInfo(var2._painterInfo, this._painterInfo);
         }

         return (DefaultSynthStyle.StateInfo)var3;
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer(super.toString());
         var1.append(",painters=[");
         if (this._painterInfo != null) {
            for(int var2 = 0; var2 < this._painterInfo.length; ++var2) {
               var1.append("    ").append(this._painterInfo[var2].toString());
            }
         }

         var1.append("]");
         return var1.toString();
      }
   }
}
