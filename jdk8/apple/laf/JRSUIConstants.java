package apple.laf;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public final class JRSUIConstants {
   public static final int FOCUS_SIZE = 4;

   private static native long getPtrForConstant(int var0);

   static JRSUIConstants.Hit getHit(int var0) {
      switch(var0) {
      case 0:
         return JRSUIConstants.Hit.NONE;
      case 1:
         return JRSUIConstants.Hit.HIT;
      case 2:
         return JRSUIConstants.ScrollBarHit.THUMB;
      case 3:
         return JRSUIConstants.ScrollBarHit.TRACK_MIN;
      case 4:
         return JRSUIConstants.ScrollBarHit.TRACK_MAX;
      case 5:
         return JRSUIConstants.ScrollBarHit.ARROW_MIN;
      case 6:
         return JRSUIConstants.ScrollBarHit.ARROW_MAX;
      case 7:
         return JRSUIConstants.ScrollBarHit.ARROW_MAX_INSIDE;
      case 8:
         return JRSUIConstants.ScrollBarHit.ARROW_MIN_INSIDE;
      default:
         return JRSUIConstants.Hit.UNKNOWN;
      }
   }

   static String getConstantName(Object var0) {
      Class var1 = var0.getClass();

      try {
         Field[] var2 = var1.getFields();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field var5 = var2[var4];
            if (var5.get((Object)null) == var0) {
               return var5.getName();
            }
         }
      } catch (Exception var6) {
      }

      return var1.getSimpleName();
   }

   public static class ScrollBarHit extends JRSUIConstants.Hit {
      private static final int _thumb = 2;
      public static final JRSUIConstants.ScrollBarHit THUMB = new JRSUIConstants.ScrollBarHit(2);
      private static final int _trackMin = 3;
      public static final JRSUIConstants.ScrollBarHit TRACK_MIN = new JRSUIConstants.ScrollBarHit(3);
      private static final int _trackMax = 4;
      public static final JRSUIConstants.ScrollBarHit TRACK_MAX = new JRSUIConstants.ScrollBarHit(4);
      private static final int _arrowMin = 5;
      public static final JRSUIConstants.ScrollBarHit ARROW_MIN = new JRSUIConstants.ScrollBarHit(5);
      private static final int _arrowMax = 6;
      public static final JRSUIConstants.ScrollBarHit ARROW_MAX = new JRSUIConstants.ScrollBarHit(6);
      private static final int _arrowMaxInside = 7;
      public static final JRSUIConstants.ScrollBarHit ARROW_MAX_INSIDE = new JRSUIConstants.ScrollBarHit(7);
      private static final int _arrowMinInside = 8;
      public static final JRSUIConstants.ScrollBarHit ARROW_MIN_INSIDE = new JRSUIConstants.ScrollBarHit(8);

      ScrollBarHit(int var1) {
         super(var1);
      }
   }

   public static class Hit {
      private static final int _unknown = -1;
      public static final JRSUIConstants.Hit UNKNOWN = new JRSUIConstants.Hit(-1);
      private static final int _none = 0;
      public static final JRSUIConstants.Hit NONE = new JRSUIConstants.Hit(0);
      private static final int _hit = 1;
      public static final JRSUIConstants.Hit HIT = new JRSUIConstants.Hit(1);
      final int hit;

      Hit(int var1) {
         this.hit = var1;
      }

      public boolean isHit() {
         return this.hit > 0;
      }

      public String toString() {
         return JRSUIConstants.getConstantName(this);
      }
   }

   public static class Widget extends JRSUIConstants.Property {
      private static final byte SHIFT = 43;
      private static final byte SIZE = 7;
      private static final long MASK = 1117103813820416L;
      private static final JRSUIConstants.PropertyEncoding widget = new JRSUIConstants.PropertyEncoding(1117103813820416L, (byte)43);
      private static final byte _background = 1;
      public static final JRSUIConstants.Widget BACKGROUND = new JRSUIConstants.Widget((byte)1);
      private static final byte _buttonBevel = 2;
      public static final JRSUIConstants.Widget BUTTON_BEVEL = new JRSUIConstants.Widget((byte)2);
      private static final byte _buttonBevelInset = 3;
      public static final JRSUIConstants.Widget BUTTON_BEVEL_INSET = new JRSUIConstants.Widget((byte)3);
      private static final byte _buttonBevelRound = 4;
      public static final JRSUIConstants.Widget BUTTON_BEVEL_ROUND = new JRSUIConstants.Widget((byte)4);
      private static final byte _buttonCheckBox = 5;
      public static final JRSUIConstants.Widget BUTTON_CHECK_BOX = new JRSUIConstants.Widget((byte)5);
      private static final byte _buttonComboBox = 6;
      public static final JRSUIConstants.Widget BUTTON_COMBO_BOX = new JRSUIConstants.Widget((byte)6);
      private static final byte _buttonComboBoxInset = 7;
      public static final JRSUIConstants.Widget BUTTON_COMBO_BOX_INSET = new JRSUIConstants.Widget((byte)7);
      private static final byte _buttonDisclosure = 8;
      public static final JRSUIConstants.Widget BUTTON_DISCLOSURE = new JRSUIConstants.Widget((byte)8);
      private static final byte _buttonListHeader = 9;
      public static final JRSUIConstants.Widget BUTTON_LIST_HEADER = new JRSUIConstants.Widget((byte)9);
      private static final byte _buttonLittleArrows = 10;
      public static final JRSUIConstants.Widget BUTTON_LITTLE_ARROWS = new JRSUIConstants.Widget((byte)10);
      private static final byte _buttonPopDown = 11;
      public static final JRSUIConstants.Widget BUTTON_POP_DOWN = new JRSUIConstants.Widget((byte)11);
      private static final byte _buttonPopDownInset = 12;
      public static final JRSUIConstants.Widget BUTTON_POP_DOWN_INSET = new JRSUIConstants.Widget((byte)12);
      private static final byte _buttonPopDownSquare = 13;
      public static final JRSUIConstants.Widget BUTTON_POP_DOWN_SQUARE = new JRSUIConstants.Widget((byte)13);
      private static final byte _buttonPopUp = 14;
      public static final JRSUIConstants.Widget BUTTON_POP_UP = new JRSUIConstants.Widget((byte)14);
      private static final byte _buttonPopUpInset = 15;
      public static final JRSUIConstants.Widget BUTTON_POP_UP_INSET = new JRSUIConstants.Widget((byte)15);
      private static final byte _buttonPopUpSquare = 16;
      public static final JRSUIConstants.Widget BUTTON_POP_UP_SQUARE = new JRSUIConstants.Widget((byte)16);
      private static final byte _buttonPush = 17;
      public static final JRSUIConstants.Widget BUTTON_PUSH = new JRSUIConstants.Widget((byte)17);
      private static final byte _buttonPushScope = 18;
      public static final JRSUIConstants.Widget BUTTON_PUSH_SCOPE = new JRSUIConstants.Widget((byte)18);
      private static final byte _buttonPushScope2 = 19;
      public static final JRSUIConstants.Widget BUTTON_PUSH_SCOPE2 = new JRSUIConstants.Widget((byte)19);
      private static final byte _buttonPushTextured = 20;
      public static final JRSUIConstants.Widget BUTTON_PUSH_TEXTURED = new JRSUIConstants.Widget((byte)20);
      private static final byte _buttonPushInset = 21;
      public static final JRSUIConstants.Widget BUTTON_PUSH_INSET = new JRSUIConstants.Widget((byte)21);
      private static final byte _buttonPushInset2 = 22;
      public static final JRSUIConstants.Widget BUTTON_PUSH_INSET2 = new JRSUIConstants.Widget((byte)22);
      private static final byte _buttonRadio = 23;
      public static final JRSUIConstants.Widget BUTTON_RADIO = new JRSUIConstants.Widget((byte)23);
      private static final byte _buttonRound = 24;
      public static final JRSUIConstants.Widget BUTTON_ROUND = new JRSUIConstants.Widget((byte)24);
      private static final byte _buttonRoundHelp = 25;
      public static final JRSUIConstants.Widget BUTTON_ROUND_HELP = new JRSUIConstants.Widget((byte)25);
      private static final byte _buttonRoundInset = 26;
      public static final JRSUIConstants.Widget BUTTON_ROUND_INSET = new JRSUIConstants.Widget((byte)26);
      private static final byte _buttonRoundInset2 = 27;
      public static final JRSUIConstants.Widget BUTTON_ROUND_INSET2 = new JRSUIConstants.Widget((byte)27);
      private static final byte _buttonSearchFieldCancel = 28;
      public static final JRSUIConstants.Widget BUTTON_SEARCH_FIELD_CANCEL = new JRSUIConstants.Widget((byte)28);
      private static final byte _buttonSearchFieldFind = 29;
      public static final JRSUIConstants.Widget BUTTON_SEARCH_FIELD_FIND = new JRSUIConstants.Widget((byte)29);
      private static final byte _buttonSegmented = 30;
      public static final JRSUIConstants.Widget BUTTON_SEGMENTED = new JRSUIConstants.Widget((byte)30);
      private static final byte _buttonSegmentedInset = 31;
      public static final JRSUIConstants.Widget BUTTON_SEGMENTED_INSET = new JRSUIConstants.Widget((byte)31);
      private static final byte _buttonSegmentedInset2 = 32;
      public static final JRSUIConstants.Widget BUTTON_SEGMENTED_INSET2 = new JRSUIConstants.Widget((byte)32);
      private static final byte _buttonSegmentedSCurve = 33;
      public static final JRSUIConstants.Widget BUTTON_SEGMENTED_SCURVE = new JRSUIConstants.Widget((byte)33);
      private static final byte _buttonSegmentedTextured = 34;
      public static final JRSUIConstants.Widget BUTTON_SEGMENTED_TEXTURED = new JRSUIConstants.Widget((byte)34);
      private static final byte _buttonSegmentedToolbar = 35;
      public static final JRSUIConstants.Widget BUTTON_SEGMENTED_TOOLBAR = new JRSUIConstants.Widget((byte)35);
      private static final byte _dial = 36;
      public static final JRSUIConstants.Widget DIAL = new JRSUIConstants.Widget((byte)36);
      private static final byte _disclosureTriangle = 37;
      public static final JRSUIConstants.Widget DISCLOSURE_TRIANGLE = new JRSUIConstants.Widget((byte)37);
      private static final byte _dividerGrabber = 38;
      public static final JRSUIConstants.Widget DIVIDER_GRABBER = new JRSUIConstants.Widget((byte)38);
      private static final byte _dividerSeparatorBar = 39;
      public static final JRSUIConstants.Widget DIVIDER_SEPARATOR_BAR = new JRSUIConstants.Widget((byte)39);
      private static final byte _dividerSplitter = 40;
      public static final JRSUIConstants.Widget DIVIDER_SPLITTER = new JRSUIConstants.Widget((byte)40);
      private static final byte _focus = 41;
      public static final JRSUIConstants.Widget FOCUS = new JRSUIConstants.Widget((byte)41);
      private static final byte _frameGroupBox = 42;
      public static final JRSUIConstants.Widget FRAME_GROUP_BOX = new JRSUIConstants.Widget((byte)42);
      private static final byte _frameGroupBoxSecondary = 43;
      public static final JRSUIConstants.Widget FRAME_GROUP_BOX_SECONDARY = new JRSUIConstants.Widget((byte)43);
      private static final byte _frameListBox = 44;
      public static final JRSUIConstants.Widget FRAME_LIST_BOX = new JRSUIConstants.Widget((byte)44);
      private static final byte _framePlacard = 45;
      public static final JRSUIConstants.Widget FRAME_PLACARD = new JRSUIConstants.Widget((byte)45);
      private static final byte _frameTextField = 46;
      public static final JRSUIConstants.Widget FRAME_TEXT_FIELD = new JRSUIConstants.Widget((byte)46);
      private static final byte _frameTextFieldRound = 47;
      public static final JRSUIConstants.Widget FRAME_TEXT_FIELD_ROUND = new JRSUIConstants.Widget((byte)47);
      private static final byte _frameWell = 48;
      public static final JRSUIConstants.Widget FRAME_WELL = new JRSUIConstants.Widget((byte)48);
      private static final byte _growBox = 49;
      public static final JRSUIConstants.Widget GROW_BOX = new JRSUIConstants.Widget((byte)49);
      private static final byte _growBoxTextured = 50;
      public static final JRSUIConstants.Widget GROW_BOX_TEXTURED = new JRSUIConstants.Widget((byte)50);
      private static final byte _gradient = 51;
      public static final JRSUIConstants.Widget GRADIENT = new JRSUIConstants.Widget((byte)51);
      private static final byte _menu = 52;
      public static final JRSUIConstants.Widget MENU = new JRSUIConstants.Widget((byte)52);
      private static final byte _menuItem = 53;
      public static final JRSUIConstants.Widget MENU_ITEM = new JRSUIConstants.Widget((byte)53);
      private static final byte _menuBar = 54;
      public static final JRSUIConstants.Widget MENU_BAR = new JRSUIConstants.Widget((byte)54);
      private static final byte _menuTitle = 55;
      public static final JRSUIConstants.Widget MENU_TITLE = new JRSUIConstants.Widget((byte)55);
      private static final byte _progressBar = 56;
      public static final JRSUIConstants.Widget PROGRESS_BAR = new JRSUIConstants.Widget((byte)56);
      private static final byte _progressIndeterminateBar = 57;
      public static final JRSUIConstants.Widget PROGRESS_INDETERMINATE_BAR = new JRSUIConstants.Widget((byte)57);
      private static final byte _progressRelevance = 58;
      public static final JRSUIConstants.Widget PROGRESS_RELEVANCE = new JRSUIConstants.Widget((byte)58);
      private static final byte _progressSpinner = 59;
      public static final JRSUIConstants.Widget PROGRESS_SPINNER = new JRSUIConstants.Widget((byte)59);
      private static final byte _scrollBar = 60;
      public static final JRSUIConstants.Widget SCROLL_BAR = new JRSUIConstants.Widget((byte)60);
      private static final byte _scrollColumnSizer = 61;
      public static final JRSUIConstants.Widget SCROLL_COLUMN_SIZER = new JRSUIConstants.Widget((byte)61);
      private static final byte _slider = 62;
      public static final JRSUIConstants.Widget SLIDER = new JRSUIConstants.Widget((byte)62);
      private static final byte _sliderThumb = 63;
      public static final JRSUIConstants.Widget SLIDER_THUMB = new JRSUIConstants.Widget((byte)63);
      private static final byte _synchronization = 64;
      public static final JRSUIConstants.Widget SYNCHRONIZATION = new JRSUIConstants.Widget((byte)64);
      private static final byte _tab = 65;
      public static final JRSUIConstants.Widget TAB = new JRSUIConstants.Widget((byte)65);
      private static final byte _titleBarCloseBox = 66;
      public static final JRSUIConstants.Widget TITLE_BAR_CLOSE_BOX = new JRSUIConstants.Widget((byte)66);
      private static final byte _titleBarCollapseBox = 67;
      public static final JRSUIConstants.Widget TITLE_BAR_COLLAPSE_BOX = new JRSUIConstants.Widget((byte)67);
      private static final byte _titleBarZoomBox = 68;
      public static final JRSUIConstants.Widget TITLE_BAR_ZOOM_BOX = new JRSUIConstants.Widget((byte)68);
      private static final byte _titleBarToolbarButton = 69;
      public static final JRSUIConstants.Widget TITLE_BAR_TOOLBAR_BUTTON = new JRSUIConstants.Widget((byte)69);
      private static final byte _toolbarItemWell = 70;
      public static final JRSUIConstants.Widget TOOLBAR_ITEM_WELL = new JRSUIConstants.Widget((byte)70);
      private static final byte _windowFrame = 71;
      public static final JRSUIConstants.Widget WINDOW_FRAME = new JRSUIConstants.Widget((byte)71);

      Widget(byte var1) {
         super(widget, var1);
      }
   }

   public static class Animating extends JRSUIConstants.Property {
      private static final byte SHIFT = 42;
      private static final byte SIZE = 1;
      private static final long MASK = 4398046511104L;
      private static final JRSUIConstants.PropertyEncoding animating = new JRSUIConstants.PropertyEncoding(4398046511104L, (byte)42);
      private static final byte _no = 0;
      public static final JRSUIConstants.Animating NO = new JRSUIConstants.Animating((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.Animating YES = new JRSUIConstants.Animating((byte)1);

      Animating(byte var1) {
         super(animating, var1);
      }
   }

   public static class BooleanValue extends JRSUIConstants.Property {
      private static final byte SHIFT = 41;
      private static final byte SIZE = 1;
      private static final long MASK = 2199023255552L;
      private static final JRSUIConstants.PropertyEncoding booleanValue = new JRSUIConstants.PropertyEncoding(2199023255552L, (byte)41);
      private static final byte _no = 0;
      public static final JRSUIConstants.BooleanValue NO = new JRSUIConstants.BooleanValue((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.BooleanValue YES = new JRSUIConstants.BooleanValue((byte)1);

      BooleanValue(byte var1) {
         super(booleanValue, var1);
      }
   }

   public static class ShowArrows extends JRSUIConstants.Property {
      private static final byte SHIFT = 40;
      private static final byte SIZE = 1;
      private static final long MASK = 1099511627776L;
      private static final JRSUIConstants.PropertyEncoding showArrows = new JRSUIConstants.PropertyEncoding(1099511627776L, (byte)40);
      private static final byte _no = 0;
      public static final JRSUIConstants.ShowArrows NO = new JRSUIConstants.ShowArrows((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.ShowArrows YES = new JRSUIConstants.ShowArrows((byte)1);

      ShowArrows(byte var1) {
         super(showArrows, var1);
      }
   }

   public static class WindowClipCorners extends JRSUIConstants.Property {
      private static final byte SHIFT = 39;
      private static final byte SIZE = 1;
      private static final long MASK = 549755813888L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(549755813888L, (byte)39);
      private static final byte _no = 0;
      public static final JRSUIConstants.WindowClipCorners NO = new JRSUIConstants.WindowClipCorners((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.WindowClipCorners YES = new JRSUIConstants.WindowClipCorners((byte)1);

      WindowClipCorners(byte var1) {
         super(focused, var1);
      }
   }

   public static class WindowTitleBarSeparator extends JRSUIConstants.Property {
      private static final byte SHIFT = 38;
      private static final byte SIZE = 1;
      private static final long MASK = 274877906944L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(274877906944L, (byte)38);
      private static final byte _no = 0;
      public static final JRSUIConstants.WindowTitleBarSeparator NO = new JRSUIConstants.WindowTitleBarSeparator((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.WindowTitleBarSeparator YES = new JRSUIConstants.WindowTitleBarSeparator((byte)1);

      WindowTitleBarSeparator(byte var1) {
         super(focused, var1);
      }
   }

   public static class NothingToScroll extends JRSUIConstants.Property {
      private static final byte SHIFT = 37;
      private static final byte SIZE = 1;
      private static final long MASK = 137438953472L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(137438953472L, (byte)37);
      private static final byte _no = 0;
      public static final JRSUIConstants.NothingToScroll NO = new JRSUIConstants.NothingToScroll((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.NothingToScroll YES = new JRSUIConstants.NothingToScroll((byte)1);

      NothingToScroll(byte var1) {
         super(focused, var1);
      }
   }

   public static class SegmentLeadingSeparator extends JRSUIConstants.Property {
      private static final byte SHIFT = 36;
      private static final byte SIZE = 1;
      private static final long MASK = 68719476736L;
      private static final JRSUIConstants.PropertyEncoding leadingSeparator = new JRSUIConstants.PropertyEncoding(68719476736L, (byte)36);
      private static final byte _no = 0;
      public static final JRSUIConstants.SegmentLeadingSeparator NO = new JRSUIConstants.SegmentLeadingSeparator((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.SegmentLeadingSeparator YES = new JRSUIConstants.SegmentLeadingSeparator((byte)1);

      SegmentLeadingSeparator(byte var1) {
         super(leadingSeparator, var1);
      }
   }

   public static class SegmentTrailingSeparator extends JRSUIConstants.Property {
      private static final byte SHIFT = 35;
      private static final byte SIZE = 1;
      private static final long MASK = 34359738368L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(34359738368L, (byte)35);
      private static final byte _no = 0;
      public static final JRSUIConstants.SegmentTrailingSeparator NO = new JRSUIConstants.SegmentTrailingSeparator((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.SegmentTrailingSeparator YES = new JRSUIConstants.SegmentTrailingSeparator((byte)1);

      SegmentTrailingSeparator(byte var1) {
         super(focused, var1);
      }
   }

   public static class FrameOnly extends JRSUIConstants.Property {
      private static final byte SHIFT = 34;
      private static final byte SIZE = 1;
      private static final long MASK = 17179869184L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(17179869184L, (byte)34);
      private static final byte _no = 0;
      public static final JRSUIConstants.FrameOnly NO = new JRSUIConstants.FrameOnly((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.FrameOnly YES = new JRSUIConstants.FrameOnly((byte)1);

      FrameOnly(byte var1) {
         super(focused, var1);
      }
   }

   public static class ArrowsOnly extends JRSUIConstants.Property {
      private static final byte SHIFT = 33;
      private static final byte SIZE = 1;
      private static final long MASK = 8589934592L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(8589934592L, (byte)33);
      private static final byte _no = 0;
      public static final JRSUIConstants.ArrowsOnly NO = new JRSUIConstants.ArrowsOnly((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.ArrowsOnly YES = new JRSUIConstants.ArrowsOnly((byte)1);

      ArrowsOnly(byte var1) {
         super(focused, var1);
      }
   }

   public static class NoIndicator extends JRSUIConstants.Property {
      private static final byte SHIFT = 32;
      private static final byte SIZE = 1;
      private static final long MASK = 4294967296L;
      private static final JRSUIConstants.PropertyEncoding noIndicator = new JRSUIConstants.PropertyEncoding(4294967296L, (byte)32);
      private static final byte _no = 0;
      public static final JRSUIConstants.NoIndicator NO = new JRSUIConstants.NoIndicator((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.NoIndicator YES = new JRSUIConstants.NoIndicator((byte)1);

      NoIndicator(byte var1) {
         super(noIndicator, var1);
      }
   }

   public static class IndicatorOnly extends JRSUIConstants.Property {
      private static final byte SHIFT = 31;
      private static final byte SIZE = 1;
      private static final long MASK = 2147483648L;
      private static final JRSUIConstants.PropertyEncoding indicatorOnly = new JRSUIConstants.PropertyEncoding(2147483648L, (byte)31);
      private static final byte _no = 0;
      public static final JRSUIConstants.IndicatorOnly NO = new JRSUIConstants.IndicatorOnly((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.IndicatorOnly YES = new JRSUIConstants.IndicatorOnly((byte)1);

      IndicatorOnly(byte var1) {
         super(indicatorOnly, var1);
      }
   }

   public static class Focused extends JRSUIConstants.Property {
      private static final byte SHIFT = 30;
      private static final byte SIZE = 1;
      private static final long MASK = 1073741824L;
      private static final JRSUIConstants.PropertyEncoding focused = new JRSUIConstants.PropertyEncoding(1073741824L, (byte)30);
      private static final byte _no = 0;
      public static final JRSUIConstants.Focused NO = new JRSUIConstants.Focused((byte)0);
      private static final byte _yes = 1;
      public static final JRSUIConstants.Focused YES = new JRSUIConstants.Focused((byte)1);

      Focused(byte var1) {
         super(focused, var1);
      }
   }

   public static class WindowType extends JRSUIConstants.Property {
      private static final byte SHIFT = 28;
      private static final byte SIZE = 2;
      private static final long MASK = 805306368L;
      private static final JRSUIConstants.PropertyEncoding windowType = new JRSUIConstants.PropertyEncoding(805306368L, (byte)28);
      private static final byte _document = 1;
      public static final JRSUIConstants.WindowType DOCUMENT = new JRSUIConstants.WindowType((byte)1);
      private static final byte _utility = 2;
      public static final JRSUIConstants.WindowType UTILITY = new JRSUIConstants.WindowType((byte)2);
      private static final byte _titlelessUtility = 3;
      public static final JRSUIConstants.WindowType TITLELESS_UTILITY = new JRSUIConstants.WindowType((byte)3);

      WindowType(byte var1) {
         super(windowType, var1);
      }
   }

   public static class Variant extends JRSUIConstants.Property {
      private static final byte SHIFT = 24;
      private static final byte SIZE = 4;
      private static final long MASK = 251658240L;
      private static final JRSUIConstants.PropertyEncoding variant = new JRSUIConstants.PropertyEncoding(251658240L, (byte)24);
      private static final byte _menuGlyph = 1;
      public static final JRSUIConstants.Variant MENU_GLYPH = new JRSUIConstants.Variant((byte)1);
      private static final byte _menuPopup = 2;
      public static final JRSUIConstants.Variant MENU_POPUP = new JRSUIConstants.Variant((byte)2);
      private static final byte _menuPulldown = 3;
      public static final JRSUIConstants.Variant MENU_PULLDOWN = new JRSUIConstants.Variant((byte)3);
      private static final byte _menuHierarchical = 4;
      public static final JRSUIConstants.Variant MENU_HIERARCHICAL = new JRSUIConstants.Variant((byte)4);
      private static final byte _gradientListBackgroundEven = 5;
      public static final JRSUIConstants.Variant GRADIENT_LIST_BACKGROUND_EVEN = new JRSUIConstants.Variant((byte)5);
      private static final byte _gradientListBackgroundOdd = 6;
      public static final JRSUIConstants.Variant GRADIENT_LIST_BACKGROUND_ODD = new JRSUIConstants.Variant((byte)6);
      private static final byte _gradientSideBar = 7;
      public static final JRSUIConstants.Variant GRADIENT_SIDE_BAR = new JRSUIConstants.Variant((byte)7);
      private static final byte _gradientSideBarSelection = 8;
      public static final JRSUIConstants.Variant GRADIENT_SIDE_BAR_SELECTION = new JRSUIConstants.Variant((byte)8);
      private static final byte _gradientSideBarFocusedSelection = 9;
      public static final JRSUIConstants.Variant GRADIENT_SIDE_BAR_FOCUSED_SELECTION = new JRSUIConstants.Variant((byte)9);

      Variant(byte var1) {
         super(variant, var1);
      }
   }

   public static class ScrollBarPart extends JRSUIConstants.Property {
      private static final byte SHIFT = 20;
      private static final byte SIZE = 4;
      private static final long MASK = 15728640L;
      private static final JRSUIConstants.PropertyEncoding scrollBarPart = new JRSUIConstants.PropertyEncoding(15728640L, (byte)20);
      private static final byte _none = 1;
      public static final JRSUIConstants.ScrollBarPart NONE = new JRSUIConstants.ScrollBarPart((byte)1);
      private static final byte _thumb = 2;
      public static final JRSUIConstants.ScrollBarPart THUMB = new JRSUIConstants.ScrollBarPart((byte)2);
      private static final byte _arrowMin = 3;
      public static final JRSUIConstants.ScrollBarPart ARROW_MIN = new JRSUIConstants.ScrollBarPart((byte)3);
      private static final byte _arrowMax = 4;
      public static final JRSUIConstants.ScrollBarPart ARROW_MAX = new JRSUIConstants.ScrollBarPart((byte)4);
      private static final byte _arrowMaxInside = 5;
      public static final JRSUIConstants.ScrollBarPart ARROW_MAX_INSIDE = new JRSUIConstants.ScrollBarPart((byte)5);
      private static final byte _arrowMinInside = 6;
      public static final JRSUIConstants.ScrollBarPart ARROW_MIN_INSIDE = new JRSUIConstants.ScrollBarPart((byte)6);
      private static final byte _trackMin = 7;
      public static final JRSUIConstants.ScrollBarPart TRACK_MIN = new JRSUIConstants.ScrollBarPart((byte)7);
      private static final byte _trackMax = 8;
      public static final JRSUIConstants.ScrollBarPart TRACK_MAX = new JRSUIConstants.ScrollBarPart((byte)8);

      ScrollBarPart(byte var1) {
         super(scrollBarPart, var1);
      }
   }

   public static class SegmentPosition extends JRSUIConstants.Property {
      private static final byte SHIFT = 17;
      private static final byte SIZE = 3;
      private static final long MASK = 917504L;
      private static final JRSUIConstants.PropertyEncoding segmentPosition = new JRSUIConstants.PropertyEncoding(917504L, (byte)17);
      private static final byte _first = 1;
      public static final JRSUIConstants.SegmentPosition FIRST = new JRSUIConstants.SegmentPosition((byte)1);
      private static final byte _middle = 2;
      public static final JRSUIConstants.SegmentPosition MIDDLE = new JRSUIConstants.SegmentPosition((byte)2);
      private static final byte _last = 3;
      public static final JRSUIConstants.SegmentPosition LAST = new JRSUIConstants.SegmentPosition((byte)3);
      private static final byte _only = 4;
      public static final JRSUIConstants.SegmentPosition ONLY = new JRSUIConstants.SegmentPosition((byte)4);

      SegmentPosition(byte var1) {
         super(segmentPosition, var1);
      }
   }

   public static class AlignmentHorizontal extends JRSUIConstants.Property {
      private static final byte SHIFT = 15;
      private static final byte SIZE = 2;
      private static final long MASK = 98304L;
      private static final JRSUIConstants.PropertyEncoding alignmentHorizontal = new JRSUIConstants.PropertyEncoding(98304L, (byte)15);
      private static final byte _left = 1;
      public static final JRSUIConstants.AlignmentHorizontal LEFT = new JRSUIConstants.AlignmentHorizontal((byte)1);
      private static final byte _center = 2;
      public static final JRSUIConstants.AlignmentHorizontal CENTER = new JRSUIConstants.AlignmentHorizontal((byte)2);
      private static final byte _right = 3;
      public static final JRSUIConstants.AlignmentHorizontal RIGHT = new JRSUIConstants.AlignmentHorizontal((byte)3);

      AlignmentHorizontal(byte var1) {
         super(alignmentHorizontal, var1);
      }
   }

   public static class AlignmentVertical extends JRSUIConstants.Property {
      private static final byte SHIFT = 13;
      private static final byte SIZE = 2;
      private static final long MASK = 24576L;
      private static final JRSUIConstants.PropertyEncoding alignmentVertical = new JRSUIConstants.PropertyEncoding(24576L, (byte)13);
      private static final byte _top = 1;
      public static final JRSUIConstants.AlignmentVertical TOP = new JRSUIConstants.AlignmentVertical((byte)1);
      private static final byte _center = 2;
      public static final JRSUIConstants.AlignmentVertical CENTER = new JRSUIConstants.AlignmentVertical((byte)2);
      private static final byte _bottom = 3;
      public static final JRSUIConstants.AlignmentVertical BOTTOM = new JRSUIConstants.AlignmentVertical((byte)3);

      AlignmentVertical(byte var1) {
         super(alignmentVertical, var1);
      }
   }

   public static class Orientation extends JRSUIConstants.Property {
      private static final byte SHIFT = 11;
      private static final byte SIZE = 2;
      private static final long MASK = 6144L;
      private static final JRSUIConstants.PropertyEncoding orientation = new JRSUIConstants.PropertyEncoding(6144L, (byte)11);
      private static final byte _horizontal = 1;
      public static final JRSUIConstants.Orientation HORIZONTAL = new JRSUIConstants.Orientation((byte)1);
      private static final byte _vertical = 2;
      public static final JRSUIConstants.Orientation VERTICAL = new JRSUIConstants.Orientation((byte)2);

      Orientation(byte var1) {
         super(orientation, var1);
      }
   }

   public static class Direction extends JRSUIConstants.Property {
      private static final byte SHIFT = 7;
      private static final byte SIZE = 4;
      private static final long MASK = 1920L;
      private static final JRSUIConstants.PropertyEncoding direction = new JRSUIConstants.PropertyEncoding(1920L, (byte)7);
      private static final byte _none = 1;
      public static final JRSUIConstants.Direction NONE = new JRSUIConstants.Direction((byte)1);
      private static final byte _up = 2;
      public static final JRSUIConstants.Direction UP = new JRSUIConstants.Direction((byte)2);
      private static final byte _down = 3;
      public static final JRSUIConstants.Direction DOWN = new JRSUIConstants.Direction((byte)3);
      private static final byte _left = 4;
      public static final JRSUIConstants.Direction LEFT = new JRSUIConstants.Direction((byte)4);
      private static final byte _right = 5;
      public static final JRSUIConstants.Direction RIGHT = new JRSUIConstants.Direction((byte)5);
      private static final byte _north = 6;
      public static final JRSUIConstants.Direction NORTH = new JRSUIConstants.Direction((byte)6);
      private static final byte _south = 7;
      public static final JRSUIConstants.Direction SOUTH = new JRSUIConstants.Direction((byte)7);
      private static final byte _east = 8;
      public static final JRSUIConstants.Direction EAST = new JRSUIConstants.Direction((byte)8);
      private static final byte _west = 9;
      public static final JRSUIConstants.Direction WEST = new JRSUIConstants.Direction((byte)9);

      Direction(byte var1) {
         super(direction, var1);
      }
   }

   public static class State extends JRSUIConstants.Property {
      private static final byte SHIFT = 3;
      private static final byte SIZE = 4;
      private static final long MASK = 120L;
      private static final JRSUIConstants.PropertyEncoding state = new JRSUIConstants.PropertyEncoding(120L, (byte)3);
      private static final byte _active = 1;
      public static final JRSUIConstants.State ACTIVE = new JRSUIConstants.State((byte)1);
      private static final byte _inactive = 2;
      public static final JRSUIConstants.State INACTIVE = new JRSUIConstants.State((byte)2);
      private static final byte _disabled = 3;
      public static final JRSUIConstants.State DISABLED = new JRSUIConstants.State((byte)3);
      private static final byte _pressed = 4;
      public static final JRSUIConstants.State PRESSED = new JRSUIConstants.State((byte)4);
      private static final byte _pulsed = 5;
      public static final JRSUIConstants.State PULSED = new JRSUIConstants.State((byte)5);
      private static final byte _rollover = 6;
      public static final JRSUIConstants.State ROLLOVER = new JRSUIConstants.State((byte)6);
      private static final byte _drag = 7;
      public static final JRSUIConstants.State DRAG = new JRSUIConstants.State((byte)7);

      State(byte var1) {
         super(state, var1);
      }
   }

   public static class Size extends JRSUIConstants.Property {
      private static final byte SHIFT = 0;
      private static final byte SIZE = 3;
      private static final long MASK = 7L;
      private static final JRSUIConstants.PropertyEncoding size = new JRSUIConstants.PropertyEncoding(7L, (byte)0);
      private static final byte _mini = 1;
      public static final JRSUIConstants.Size MINI = new JRSUIConstants.Size((byte)1);
      private static final byte _small = 2;
      public static final JRSUIConstants.Size SMALL = new JRSUIConstants.Size((byte)2);
      private static final byte _regular = 3;
      public static final JRSUIConstants.Size REGULAR = new JRSUIConstants.Size((byte)3);
      private static final byte _large = 4;
      public static final JRSUIConstants.Size LARGE = new JRSUIConstants.Size((byte)4);

      Size(byte var1) {
         super(size, var1);
      }
   }

   static class Property {
      final JRSUIConstants.PropertyEncoding encoding;
      final long value;
      final byte ordinal;

      Property(JRSUIConstants.PropertyEncoding var1, byte var2) {
         this.encoding = var1;
         this.value = (long)var2 << var1.shift;
         this.ordinal = var2;
      }

      public long apply(long var1) {
         return var1 & ~this.encoding.mask | this.value;
      }

      public String toString() {
         return JRSUIConstants.getConstantName(this);
      }
   }

   static class PropertyEncoding {
      final long mask;
      final byte shift;

      PropertyEncoding(long var1, byte var3) {
         this.mask = var1;
         this.shift = var3;
      }
   }

   static class DoubleValue {
      protected static final byte TYPE_CODE = 1;
      final double doubleValue;

      DoubleValue(double var1) {
         this.doubleValue = var1;
      }

      public byte getTypeCode() {
         return 1;
      }

      public void putValueInBuffer(ByteBuffer var1) {
         var1.putDouble(this.doubleValue);
      }

      public boolean equals(Object var1) {
         return var1 instanceof JRSUIConstants.DoubleValue && ((JRSUIConstants.DoubleValue)var1).doubleValue == this.doubleValue;
      }

      public int hashCode() {
         long var1 = Double.doubleToLongBits(this.doubleValue);
         return (int)(var1 ^ var1 >>> 32);
      }

      public String toString() {
         return Double.toString(this.doubleValue);
      }
   }

   static class Key {
      protected static final int _value = 20;
      public static final JRSUIConstants.Key VALUE = new JRSUIConstants.Key(20);
      protected static final int _thumbProportion = 24;
      public static final JRSUIConstants.Key THUMB_PROPORTION = new JRSUIConstants.Key(24);
      protected static final int _thumbStart = 25;
      public static final JRSUIConstants.Key THUMB_START = new JRSUIConstants.Key(25);
      protected static final int _windowTitleBarHeight = 28;
      public static final JRSUIConstants.Key WINDOW_TITLE_BAR_HEIGHT = new JRSUIConstants.Key(28);
      protected static final int _animationFrame = 23;
      public static final JRSUIConstants.Key ANIMATION_FRAME = new JRSUIConstants.Key(23);
      final int constant;
      private long ptr;

      private Key(int var1) {
         this.constant = var1;
      }

      long getConstantPtr() {
         if (this.ptr != 0L) {
            return this.ptr;
         } else {
            this.ptr = JRSUIConstants.getPtrForConstant(this.constant);
            if (this.ptr != 0L) {
               return this.ptr;
            } else {
               throw new RuntimeException("Constant not implemented in native: " + this);
            }
         }
      }

      public String toString() {
         return JRSUIConstants.getConstantName(this) + (this.ptr == 0L ? "(unlinked)" : "");
      }
   }
}
