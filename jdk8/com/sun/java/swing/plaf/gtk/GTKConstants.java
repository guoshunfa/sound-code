package com.sun.java.swing.plaf.gtk;

public interface GTKConstants {
   int UNDEFINED = -100;

   public static enum Orientation {
      HORIZONTAL,
      VERTICAL;
   }

   public static enum ArrowType {
      UP,
      DOWN,
      LEFT,
      RIGHT;
   }

   public static enum PositionType {
      LEFT,
      RIGHT,
      TOP,
      BOTTOM;
   }

   public static enum ExpanderStyle {
      COLLAPSED,
      SEMI_COLLAPSED,
      SEMI_EXPANDED,
      EXPANDED;
   }

   public static enum StateType {
      NORMAL,
      ACTIVE,
      PRELIGHT,
      SELECTED,
      INSENSITIVE;
   }

   public static enum ShadowType {
      NONE,
      IN,
      OUT,
      ETCHED_IN,
      ETCHED_OUT;
   }

   public static enum TextDirection {
      NONE,
      LTR,
      RTL;
   }

   public static enum IconSize {
      INVALID,
      MENU,
      SMALL_TOOLBAR,
      LARGE_TOOLBAR,
      BUTTON,
      DND,
      DIALOG;
   }
}
