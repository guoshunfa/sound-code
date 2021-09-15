package javax.swing.plaf.metal;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public abstract class MetalTheme {
   static final int CONTROL_TEXT_FONT = 0;
   static final int SYSTEM_TEXT_FONT = 1;
   static final int USER_TEXT_FONT = 2;
   static final int MENU_TEXT_FONT = 3;
   static final int WINDOW_TITLE_FONT = 4;
   static final int SUB_TEXT_FONT = 5;
   static ColorUIResource white = new ColorUIResource(255, 255, 255);
   private static ColorUIResource black = new ColorUIResource(0, 0, 0);

   public abstract String getName();

   protected abstract ColorUIResource getPrimary1();

   protected abstract ColorUIResource getPrimary2();

   protected abstract ColorUIResource getPrimary3();

   protected abstract ColorUIResource getSecondary1();

   protected abstract ColorUIResource getSecondary2();

   protected abstract ColorUIResource getSecondary3();

   public abstract FontUIResource getControlTextFont();

   public abstract FontUIResource getSystemTextFont();

   public abstract FontUIResource getUserTextFont();

   public abstract FontUIResource getMenuTextFont();

   public abstract FontUIResource getWindowTitleFont();

   public abstract FontUIResource getSubTextFont();

   protected ColorUIResource getWhite() {
      return white;
   }

   protected ColorUIResource getBlack() {
      return black;
   }

   public ColorUIResource getFocusColor() {
      return this.getPrimary2();
   }

   public ColorUIResource getDesktopColor() {
      return this.getPrimary2();
   }

   public ColorUIResource getControl() {
      return this.getSecondary3();
   }

   public ColorUIResource getControlShadow() {
      return this.getSecondary2();
   }

   public ColorUIResource getControlDarkShadow() {
      return this.getSecondary1();
   }

   public ColorUIResource getControlInfo() {
      return this.getBlack();
   }

   public ColorUIResource getControlHighlight() {
      return this.getWhite();
   }

   public ColorUIResource getControlDisabled() {
      return this.getSecondary2();
   }

   public ColorUIResource getPrimaryControl() {
      return this.getPrimary3();
   }

   public ColorUIResource getPrimaryControlShadow() {
      return this.getPrimary2();
   }

   public ColorUIResource getPrimaryControlDarkShadow() {
      return this.getPrimary1();
   }

   public ColorUIResource getPrimaryControlInfo() {
      return this.getBlack();
   }

   public ColorUIResource getPrimaryControlHighlight() {
      return this.getWhite();
   }

   public ColorUIResource getSystemTextColor() {
      return this.getBlack();
   }

   public ColorUIResource getControlTextColor() {
      return this.getControlInfo();
   }

   public ColorUIResource getInactiveControlTextColor() {
      return this.getControlDisabled();
   }

   public ColorUIResource getInactiveSystemTextColor() {
      return this.getSecondary2();
   }

   public ColorUIResource getUserTextColor() {
      return this.getBlack();
   }

   public ColorUIResource getTextHighlightColor() {
      return this.getPrimary3();
   }

   public ColorUIResource getHighlightedTextColor() {
      return this.getControlTextColor();
   }

   public ColorUIResource getWindowBackground() {
      return this.getWhite();
   }

   public ColorUIResource getWindowTitleBackground() {
      return this.getPrimary3();
   }

   public ColorUIResource getWindowTitleForeground() {
      return this.getBlack();
   }

   public ColorUIResource getWindowTitleInactiveBackground() {
      return this.getSecondary3();
   }

   public ColorUIResource getWindowTitleInactiveForeground() {
      return this.getBlack();
   }

   public ColorUIResource getMenuBackground() {
      return this.getSecondary3();
   }

   public ColorUIResource getMenuForeground() {
      return this.getBlack();
   }

   public ColorUIResource getMenuSelectedBackground() {
      return this.getPrimary2();
   }

   public ColorUIResource getMenuSelectedForeground() {
      return this.getBlack();
   }

   public ColorUIResource getMenuDisabledForeground() {
      return this.getSecondary2();
   }

   public ColorUIResource getSeparatorBackground() {
      return this.getWhite();
   }

   public ColorUIResource getSeparatorForeground() {
      return this.getPrimary1();
   }

   public ColorUIResource getAcceleratorForeground() {
      return this.getPrimary1();
   }

   public ColorUIResource getAcceleratorSelectedForeground() {
      return this.getBlack();
   }

   public void addCustomEntriesToTable(UIDefaults var1) {
   }

   void install() {
   }

   boolean isSystemTheme() {
      return false;
   }
}
