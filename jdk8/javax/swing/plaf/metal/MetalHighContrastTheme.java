package javax.swing.plaf.metal;

import javax.swing.UIDefaults;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicBorders;

class MetalHighContrastTheme extends DefaultMetalTheme {
   private static final ColorUIResource primary1 = new ColorUIResource(0, 0, 0);
   private static final ColorUIResource primary2 = new ColorUIResource(204, 204, 204);
   private static final ColorUIResource primary3 = new ColorUIResource(255, 255, 255);
   private static final ColorUIResource primaryHighlight = new ColorUIResource(102, 102, 102);
   private static final ColorUIResource secondary2 = new ColorUIResource(204, 204, 204);
   private static final ColorUIResource secondary3 = new ColorUIResource(255, 255, 255);
   private static final ColorUIResource controlHighlight = new ColorUIResource(102, 102, 102);

   public String getName() {
      return "Contrast";
   }

   protected ColorUIResource getPrimary1() {
      return primary1;
   }

   protected ColorUIResource getPrimary2() {
      return primary2;
   }

   protected ColorUIResource getPrimary3() {
      return primary3;
   }

   public ColorUIResource getPrimaryControlHighlight() {
      return primaryHighlight;
   }

   protected ColorUIResource getSecondary2() {
      return secondary2;
   }

   protected ColorUIResource getSecondary3() {
      return secondary3;
   }

   public ColorUIResource getControlHighlight() {
      return secondary2;
   }

   public ColorUIResource getFocusColor() {
      return this.getBlack();
   }

   public ColorUIResource getTextHighlightColor() {
      return this.getBlack();
   }

   public ColorUIResource getHighlightedTextColor() {
      return this.getWhite();
   }

   public ColorUIResource getMenuSelectedBackground() {
      return this.getBlack();
   }

   public ColorUIResource getMenuSelectedForeground() {
      return this.getWhite();
   }

   public ColorUIResource getAcceleratorForeground() {
      return this.getBlack();
   }

   public ColorUIResource getAcceleratorSelectedForeground() {
      return this.getWhite();
   }

   public void addCustomEntriesToTable(UIDefaults var1) {
      BorderUIResource var2 = new BorderUIResource(new LineBorder(this.getBlack()));
      new BorderUIResource(new LineBorder(this.getWhite()));
      BorderUIResource var4 = new BorderUIResource(new CompoundBorder(var2, new BasicBorders.MarginBorder()));
      Object[] var5 = new Object[]{"ToolTip.border", var2, "TitledBorder.border", var2, "TextField.border", var4, "PasswordField.border", var4, "TextArea.border", var4, "TextPane.border", var4, "EditorPane.border", var4, "ComboBox.background", this.getWindowBackground(), "ComboBox.foreground", this.getUserTextColor(), "ComboBox.selectionBackground", this.getTextHighlightColor(), "ComboBox.selectionForeground", this.getHighlightedTextColor(), "ProgressBar.foreground", this.getUserTextColor(), "ProgressBar.background", this.getWindowBackground(), "ProgressBar.selectionForeground", this.getWindowBackground(), "ProgressBar.selectionBackground", this.getUserTextColor(), "OptionPane.errorDialog.border.background", this.getPrimary1(), "OptionPane.errorDialog.titlePane.foreground", this.getPrimary3(), "OptionPane.errorDialog.titlePane.background", this.getPrimary1(), "OptionPane.errorDialog.titlePane.shadow", this.getPrimary2(), "OptionPane.questionDialog.border.background", this.getPrimary1(), "OptionPane.questionDialog.titlePane.foreground", this.getPrimary3(), "OptionPane.questionDialog.titlePane.background", this.getPrimary1(), "OptionPane.questionDialog.titlePane.shadow", this.getPrimary2(), "OptionPane.warningDialog.border.background", this.getPrimary1(), "OptionPane.warningDialog.titlePane.foreground", this.getPrimary3(), "OptionPane.warningDialog.titlePane.background", this.getPrimary1(), "OptionPane.warningDialog.titlePane.shadow", this.getPrimary2()};
      var1.putDefaults(var5);
   }

   boolean isSystemTheme() {
      return this.getClass() == MetalHighContrastTheme.class;
   }
}
