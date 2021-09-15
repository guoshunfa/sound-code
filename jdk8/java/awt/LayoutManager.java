package java.awt;

public interface LayoutManager {
   void addLayoutComponent(String var1, Component var2);

   void removeLayoutComponent(Component var1);

   Dimension preferredLayoutSize(Container var1);

   Dimension minimumLayoutSize(Container var1);

   void layoutContainer(Container var1);
}
