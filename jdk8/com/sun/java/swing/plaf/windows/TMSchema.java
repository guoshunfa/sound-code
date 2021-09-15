package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.EnumMap;
import javax.swing.JComponent;
import sun.awt.windows.ThemeReader;

class TMSchema {
   public static enum TypeEnum {
      BT_IMAGEFILE(TMSchema.Prop.BGTYPE, "imagefile", 0),
      BT_BORDERFILL(TMSchema.Prop.BGTYPE, "borderfill", 1),
      TST_NONE(TMSchema.Prop.TEXTSHADOWTYPE, "none", 0),
      TST_SINGLE(TMSchema.Prop.TEXTSHADOWTYPE, "single", 1),
      TST_CONTINUOUS(TMSchema.Prop.TEXTSHADOWTYPE, "continuous", 2);

      private final TMSchema.Prop prop;
      private final String enumName;
      private final int value;

      private TypeEnum(TMSchema.Prop var3, String var4, int var5) {
         this.prop = var3;
         this.enumName = var4;
         this.value = var5;
      }

      public String toString() {
         return this.prop + "=" + this.enumName + "=" + this.value;
      }

      String getName() {
         return this.enumName;
      }

      static TMSchema.TypeEnum getTypeEnum(TMSchema.Prop var0, int var1) {
         TMSchema.TypeEnum[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            TMSchema.TypeEnum var5 = var2[var4];
            if (var5.prop == var0 && var5.value == var1) {
               return var5;
            }
         }

         return null;
      }
   }

   public static enum Prop {
      COLOR(Color.class, 204),
      SIZE(Dimension.class, 207),
      FLATMENUS(Boolean.class, 1001),
      BORDERONLY(Boolean.class, 2203),
      IMAGECOUNT(Integer.class, 2401),
      BORDERSIZE(Integer.class, 2403),
      PROGRESSCHUNKSIZE(Integer.class, 2411),
      PROGRESSSPACESIZE(Integer.class, 2412),
      TEXTSHADOWOFFSET(Point.class, 3402),
      NORMALSIZE(Dimension.class, 3409),
      SIZINGMARGINS(Insets.class, 3601),
      CONTENTMARGINS(Insets.class, 3602),
      CAPTIONMARGINS(Insets.class, 3603),
      BORDERCOLOR(Color.class, 3801),
      FILLCOLOR(Color.class, 3802),
      TEXTCOLOR(Color.class, 3803),
      TEXTSHADOWCOLOR(Color.class, 3818),
      BGTYPE(Integer.class, 4001),
      TEXTSHADOWTYPE(Integer.class, 4010),
      TRANSITIONDURATIONS(Integer.class, 6000);

      private final Class type;
      private final int value;

      private Prop(Class var3, int var4) {
         this.type = var3;
         this.value = var4;
      }

      public int getValue() {
         return this.value;
      }

      public String toString() {
         return this.name() + "[" + this.type.getName() + "] = " + this.value;
      }
   }

   public static enum State {
      ACTIVE,
      ASSIST,
      BITMAP,
      CHECKED,
      CHECKEDDISABLED,
      CHECKEDHOT,
      CHECKEDNORMAL,
      CHECKEDPRESSED,
      CHECKMARKNORMAL,
      CHECKMARKDISABLED,
      BULLETNORMAL,
      BULLETDISABLED,
      CLOSED,
      DEFAULTED,
      DISABLED,
      DISABLEDHOT,
      DISABLEDPUSHED,
      DOWNDISABLED,
      DOWNHOT,
      DOWNNORMAL,
      DOWNPRESSED,
      FOCUSED,
      HOT,
      HOTCHECKED,
      ICONHOT,
      ICONNORMAL,
      ICONPRESSED,
      ICONSORTEDHOT,
      ICONSORTEDNORMAL,
      ICONSORTEDPRESSED,
      INACTIVE,
      INACTIVENORMAL,
      INACTIVEHOT,
      INACTIVEPUSHED,
      INACTIVEDISABLED,
      LEFTDISABLED,
      LEFTHOT,
      LEFTNORMAL,
      LEFTPRESSED,
      MIXEDDISABLED,
      MIXEDHOT,
      MIXEDNORMAL,
      MIXEDPRESSED,
      NORMAL,
      PRESSED,
      OPENED,
      PUSHED,
      READONLY,
      RIGHTDISABLED,
      RIGHTHOT,
      RIGHTNORMAL,
      RIGHTPRESSED,
      SELECTED,
      UNCHECKEDDISABLED,
      UNCHECKEDHOT,
      UNCHECKEDNORMAL,
      UNCHECKEDPRESSED,
      UPDISABLED,
      UPHOT,
      UPNORMAL,
      UPPRESSED,
      HOVER,
      UPHOVER,
      DOWNHOVER,
      LEFTHOVER,
      RIGHTHOVER,
      SORTEDDOWN,
      SORTEDHOT,
      SORTEDNORMAL,
      SORTEDPRESSED,
      SORTEDUP;

      private static EnumMap<TMSchema.Part, TMSchema.State[]> stateMap;

      private static synchronized void initStates() {
         stateMap = new EnumMap(TMSchema.Part.class);
         stateMap.put((Enum)TMSchema.Part.EP_EDITTEXT, new TMSchema.State[]{NORMAL, HOT, SELECTED, DISABLED, FOCUSED, READONLY, ASSIST});
         stateMap.put((Enum)TMSchema.Part.BP_PUSHBUTTON, new TMSchema.State[]{NORMAL, HOT, PRESSED, DISABLED, DEFAULTED});
         stateMap.put((Enum)TMSchema.Part.BP_RADIOBUTTON, new TMSchema.State[]{UNCHECKEDNORMAL, UNCHECKEDHOT, UNCHECKEDPRESSED, UNCHECKEDDISABLED, CHECKEDNORMAL, CHECKEDHOT, CHECKEDPRESSED, CHECKEDDISABLED});
         stateMap.put((Enum)TMSchema.Part.BP_CHECKBOX, new TMSchema.State[]{UNCHECKEDNORMAL, UNCHECKEDHOT, UNCHECKEDPRESSED, UNCHECKEDDISABLED, CHECKEDNORMAL, CHECKEDHOT, CHECKEDPRESSED, CHECKEDDISABLED, MIXEDNORMAL, MIXEDHOT, MIXEDPRESSED, MIXEDDISABLED});
         TMSchema.State[] var0 = new TMSchema.State[]{NORMAL, HOT, PRESSED, DISABLED};
         stateMap.put((Enum)TMSchema.Part.CP_COMBOBOX, var0);
         stateMap.put((Enum)TMSchema.Part.CP_DROPDOWNBUTTON, var0);
         stateMap.put((Enum)TMSchema.Part.CP_BACKGROUND, var0);
         stateMap.put((Enum)TMSchema.Part.CP_TRANSPARENTBACKGROUND, var0);
         stateMap.put((Enum)TMSchema.Part.CP_BORDER, var0);
         stateMap.put((Enum)TMSchema.Part.CP_READONLY, var0);
         stateMap.put((Enum)TMSchema.Part.CP_DROPDOWNBUTTONRIGHT, var0);
         stateMap.put((Enum)TMSchema.Part.CP_DROPDOWNBUTTONLEFT, var0);
         stateMap.put((Enum)TMSchema.Part.CP_CUEBANNER, var0);
         stateMap.put((Enum)TMSchema.Part.HP_HEADERITEM, new TMSchema.State[]{NORMAL, HOT, PRESSED, SORTEDNORMAL, SORTEDHOT, SORTEDPRESSED, ICONNORMAL, ICONHOT, ICONPRESSED, ICONSORTEDNORMAL, ICONSORTEDHOT, ICONSORTEDPRESSED});
         stateMap.put((Enum)TMSchema.Part.HP_HEADERSORTARROW, new TMSchema.State[]{SORTEDDOWN, SORTEDUP});
         TMSchema.State[] var1 = new TMSchema.State[]{NORMAL, HOT, PRESSED, DISABLED, HOVER};
         stateMap.put((Enum)TMSchema.Part.SBP_SCROLLBAR, var1);
         stateMap.put((Enum)TMSchema.Part.SBP_THUMBBTNVERT, var1);
         stateMap.put((Enum)TMSchema.Part.SBP_THUMBBTNHORZ, var1);
         stateMap.put((Enum)TMSchema.Part.SBP_GRIPPERVERT, var1);
         stateMap.put((Enum)TMSchema.Part.SBP_GRIPPERHORZ, var1);
         stateMap.put((Enum)TMSchema.Part.SBP_ARROWBTN, new TMSchema.State[]{UPNORMAL, UPHOT, UPPRESSED, UPDISABLED, DOWNNORMAL, DOWNHOT, DOWNPRESSED, DOWNDISABLED, LEFTNORMAL, LEFTHOT, LEFTPRESSED, LEFTDISABLED, RIGHTNORMAL, RIGHTHOT, RIGHTPRESSED, RIGHTDISABLED, UPHOVER, DOWNHOVER, LEFTHOVER, RIGHTHOVER});
         TMSchema.State[] var2 = new TMSchema.State[]{NORMAL, HOT, PRESSED, DISABLED};
         stateMap.put((Enum)TMSchema.Part.SPNP_UP, var2);
         stateMap.put((Enum)TMSchema.Part.SPNP_DOWN, var2);
         stateMap.put((Enum)TMSchema.Part.TVP_GLYPH, new TMSchema.State[]{CLOSED, OPENED});
         TMSchema.State[] var3 = new TMSchema.State[]{NORMAL, HOT, PUSHED, DISABLED, INACTIVENORMAL, INACTIVEHOT, INACTIVEPUSHED, INACTIVEDISABLED};
         if (ThemeReader.getInt(TMSchema.Control.WINDOW.toString(), TMSchema.Part.WP_CLOSEBUTTON.getValue(), 1, TMSchema.Prop.IMAGECOUNT.getValue()) == 10) {
            var3 = new TMSchema.State[]{NORMAL, HOT, PUSHED, DISABLED, null, INACTIVENORMAL, INACTIVEHOT, INACTIVEPUSHED, INACTIVEDISABLED, null};
         }

         stateMap.put((Enum)TMSchema.Part.WP_MINBUTTON, var3);
         stateMap.put((Enum)TMSchema.Part.WP_MAXBUTTON, var3);
         stateMap.put((Enum)TMSchema.Part.WP_RESTOREBUTTON, var3);
         stateMap.put((Enum)TMSchema.Part.WP_CLOSEBUTTON, var3);
         stateMap.put((Enum)TMSchema.Part.TKP_TRACK, new TMSchema.State[]{NORMAL});
         stateMap.put((Enum)TMSchema.Part.TKP_TRACKVERT, new TMSchema.State[]{NORMAL});
         TMSchema.State[] var4 = new TMSchema.State[]{NORMAL, HOT, PRESSED, FOCUSED, DISABLED};
         stateMap.put((Enum)TMSchema.Part.TKP_THUMB, var4);
         stateMap.put((Enum)TMSchema.Part.TKP_THUMBBOTTOM, var4);
         stateMap.put((Enum)TMSchema.Part.TKP_THUMBTOP, var4);
         stateMap.put((Enum)TMSchema.Part.TKP_THUMBVERT, var4);
         stateMap.put((Enum)TMSchema.Part.TKP_THUMBRIGHT, var4);
         TMSchema.State[] var5 = new TMSchema.State[]{NORMAL, HOT, SELECTED, DISABLED, FOCUSED};
         stateMap.put((Enum)TMSchema.Part.TABP_TABITEM, var5);
         stateMap.put((Enum)TMSchema.Part.TABP_TABITEMLEFTEDGE, var5);
         stateMap.put((Enum)TMSchema.Part.TABP_TABITEMRIGHTEDGE, var5);
         stateMap.put((Enum)TMSchema.Part.TP_BUTTON, new TMSchema.State[]{NORMAL, HOT, PRESSED, DISABLED, CHECKED, HOTCHECKED});
         TMSchema.State[] var6 = new TMSchema.State[]{ACTIVE, INACTIVE};
         stateMap.put((Enum)TMSchema.Part.WP_WINDOW, var6);
         stateMap.put((Enum)TMSchema.Part.WP_FRAMELEFT, var6);
         stateMap.put((Enum)TMSchema.Part.WP_FRAMERIGHT, var6);
         stateMap.put((Enum)TMSchema.Part.WP_FRAMEBOTTOM, var6);
         TMSchema.State[] var7 = new TMSchema.State[]{ACTIVE, INACTIVE, DISABLED};
         stateMap.put((Enum)TMSchema.Part.WP_CAPTION, var7);
         stateMap.put((Enum)TMSchema.Part.WP_MINCAPTION, var7);
         stateMap.put((Enum)TMSchema.Part.WP_MAXCAPTION, var7);
         stateMap.put((Enum)TMSchema.Part.MP_BARBACKGROUND, new TMSchema.State[]{ACTIVE, INACTIVE});
         stateMap.put((Enum)TMSchema.Part.MP_BARITEM, new TMSchema.State[]{NORMAL, HOT, PUSHED, DISABLED, DISABLEDHOT, DISABLEDPUSHED});
         stateMap.put((Enum)TMSchema.Part.MP_POPUPCHECK, new TMSchema.State[]{CHECKMARKNORMAL, CHECKMARKDISABLED, BULLETNORMAL, BULLETDISABLED});
         stateMap.put((Enum)TMSchema.Part.MP_POPUPCHECKBACKGROUND, new TMSchema.State[]{DISABLEDPUSHED, NORMAL, BITMAP});
         stateMap.put((Enum)TMSchema.Part.MP_POPUPITEM, new TMSchema.State[]{NORMAL, HOT, DISABLED, DISABLEDHOT});
         stateMap.put((Enum)TMSchema.Part.MP_POPUPSUBMENU, new TMSchema.State[]{NORMAL, DISABLED});
      }

      public static synchronized int getValue(TMSchema.Part var0, TMSchema.State var1) {
         if (stateMap == null) {
            initStates();
         }

         Enum[] var2 = (Enum[])stateMap.get(var0);
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var1 == var2[var3]) {
                  return var3 + 1;
               }
            }
         }

         return var1 != null && var1 != NORMAL ? 0 : 1;
      }
   }

   public static enum Part {
      MENU(TMSchema.Control.MENU, 0),
      MP_BARBACKGROUND(TMSchema.Control.MENU, 7),
      MP_BARITEM(TMSchema.Control.MENU, 8),
      MP_POPUPBACKGROUND(TMSchema.Control.MENU, 9),
      MP_POPUPBORDERS(TMSchema.Control.MENU, 10),
      MP_POPUPCHECK(TMSchema.Control.MENU, 11),
      MP_POPUPCHECKBACKGROUND(TMSchema.Control.MENU, 12),
      MP_POPUPGUTTER(TMSchema.Control.MENU, 13),
      MP_POPUPITEM(TMSchema.Control.MENU, 14),
      MP_POPUPSEPARATOR(TMSchema.Control.MENU, 15),
      MP_POPUPSUBMENU(TMSchema.Control.MENU, 16),
      BP_PUSHBUTTON(TMSchema.Control.BUTTON, 1),
      BP_RADIOBUTTON(TMSchema.Control.BUTTON, 2),
      BP_CHECKBOX(TMSchema.Control.BUTTON, 3),
      BP_GROUPBOX(TMSchema.Control.BUTTON, 4),
      CP_COMBOBOX(TMSchema.Control.COMBOBOX, 0),
      CP_DROPDOWNBUTTON(TMSchema.Control.COMBOBOX, 1),
      CP_BACKGROUND(TMSchema.Control.COMBOBOX, 2),
      CP_TRANSPARENTBACKGROUND(TMSchema.Control.COMBOBOX, 3),
      CP_BORDER(TMSchema.Control.COMBOBOX, 4),
      CP_READONLY(TMSchema.Control.COMBOBOX, 5),
      CP_DROPDOWNBUTTONRIGHT(TMSchema.Control.COMBOBOX, 6),
      CP_DROPDOWNBUTTONLEFT(TMSchema.Control.COMBOBOX, 7),
      CP_CUEBANNER(TMSchema.Control.COMBOBOX, 8),
      EP_EDIT(TMSchema.Control.EDIT, 0),
      EP_EDITTEXT(TMSchema.Control.EDIT, 1),
      HP_HEADERITEM(TMSchema.Control.HEADER, 1),
      HP_HEADERSORTARROW(TMSchema.Control.HEADER, 4),
      LBP_LISTBOX(TMSchema.Control.LISTBOX, 0),
      LVP_LISTVIEW(TMSchema.Control.LISTVIEW, 0),
      PP_PROGRESS(TMSchema.Control.PROGRESS, 0),
      PP_BAR(TMSchema.Control.PROGRESS, 1),
      PP_BARVERT(TMSchema.Control.PROGRESS, 2),
      PP_CHUNK(TMSchema.Control.PROGRESS, 3),
      PP_CHUNKVERT(TMSchema.Control.PROGRESS, 4),
      RP_GRIPPER(TMSchema.Control.REBAR, 1),
      RP_GRIPPERVERT(TMSchema.Control.REBAR, 2),
      SBP_SCROLLBAR(TMSchema.Control.SCROLLBAR, 0),
      SBP_ARROWBTN(TMSchema.Control.SCROLLBAR, 1),
      SBP_THUMBBTNHORZ(TMSchema.Control.SCROLLBAR, 2),
      SBP_THUMBBTNVERT(TMSchema.Control.SCROLLBAR, 3),
      SBP_LOWERTRACKHORZ(TMSchema.Control.SCROLLBAR, 4),
      SBP_UPPERTRACKHORZ(TMSchema.Control.SCROLLBAR, 5),
      SBP_LOWERTRACKVERT(TMSchema.Control.SCROLLBAR, 6),
      SBP_UPPERTRACKVERT(TMSchema.Control.SCROLLBAR, 7),
      SBP_GRIPPERHORZ(TMSchema.Control.SCROLLBAR, 8),
      SBP_GRIPPERVERT(TMSchema.Control.SCROLLBAR, 9),
      SBP_SIZEBOX(TMSchema.Control.SCROLLBAR, 10),
      SPNP_UP(TMSchema.Control.SPIN, 1),
      SPNP_DOWN(TMSchema.Control.SPIN, 2),
      TABP_TABITEM(TMSchema.Control.TAB, 1),
      TABP_TABITEMLEFTEDGE(TMSchema.Control.TAB, 2),
      TABP_TABITEMRIGHTEDGE(TMSchema.Control.TAB, 3),
      TABP_PANE(TMSchema.Control.TAB, 9),
      TP_TOOLBAR(TMSchema.Control.TOOLBAR, 0),
      TP_BUTTON(TMSchema.Control.TOOLBAR, 1),
      TP_SEPARATOR(TMSchema.Control.TOOLBAR, 5),
      TP_SEPARATORVERT(TMSchema.Control.TOOLBAR, 6),
      TKP_TRACK(TMSchema.Control.TRACKBAR, 1),
      TKP_TRACKVERT(TMSchema.Control.TRACKBAR, 2),
      TKP_THUMB(TMSchema.Control.TRACKBAR, 3),
      TKP_THUMBBOTTOM(TMSchema.Control.TRACKBAR, 4),
      TKP_THUMBTOP(TMSchema.Control.TRACKBAR, 5),
      TKP_THUMBVERT(TMSchema.Control.TRACKBAR, 6),
      TKP_THUMBLEFT(TMSchema.Control.TRACKBAR, 7),
      TKP_THUMBRIGHT(TMSchema.Control.TRACKBAR, 8),
      TKP_TICS(TMSchema.Control.TRACKBAR, 9),
      TKP_TICSVERT(TMSchema.Control.TRACKBAR, 10),
      TVP_TREEVIEW(TMSchema.Control.TREEVIEW, 0),
      TVP_GLYPH(TMSchema.Control.TREEVIEW, 2),
      WP_WINDOW(TMSchema.Control.WINDOW, 0),
      WP_CAPTION(TMSchema.Control.WINDOW, 1),
      WP_MINCAPTION(TMSchema.Control.WINDOW, 3),
      WP_MAXCAPTION(TMSchema.Control.WINDOW, 5),
      WP_FRAMELEFT(TMSchema.Control.WINDOW, 7),
      WP_FRAMERIGHT(TMSchema.Control.WINDOW, 8),
      WP_FRAMEBOTTOM(TMSchema.Control.WINDOW, 9),
      WP_SYSBUTTON(TMSchema.Control.WINDOW, 13),
      WP_MDISYSBUTTON(TMSchema.Control.WINDOW, 14),
      WP_MINBUTTON(TMSchema.Control.WINDOW, 15),
      WP_MDIMINBUTTON(TMSchema.Control.WINDOW, 16),
      WP_MAXBUTTON(TMSchema.Control.WINDOW, 17),
      WP_CLOSEBUTTON(TMSchema.Control.WINDOW, 18),
      WP_MDICLOSEBUTTON(TMSchema.Control.WINDOW, 20),
      WP_RESTOREBUTTON(TMSchema.Control.WINDOW, 21),
      WP_MDIRESTOREBUTTON(TMSchema.Control.WINDOW, 22);

      private final TMSchema.Control control;
      private final int value;

      private Part(TMSchema.Control var3, int var4) {
         this.control = var3;
         this.value = var4;
      }

      public int getValue() {
         return this.value;
      }

      public String getControlName(Component var1) {
         String var2 = "";
         if (var1 instanceof JComponent) {
            JComponent var3 = (JComponent)var1;
            String var4 = (String)var3.getClientProperty("XPStyle.subAppName");
            if (var4 != null) {
               var2 = var4 + "::";
            }
         }

         return var2 + this.control.toString();
      }

      public String toString() {
         return this.control.toString() + "." + this.name();
      }
   }

   public static enum Control {
      BUTTON,
      COMBOBOX,
      EDIT,
      HEADER,
      LISTBOX,
      LISTVIEW,
      MENU,
      PROGRESS,
      REBAR,
      SCROLLBAR,
      SPIN,
      TAB,
      TOOLBAR,
      TRACKBAR,
      TREEVIEW,
      WINDOW;
   }
}
