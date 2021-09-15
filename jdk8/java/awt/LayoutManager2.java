package java.awt;

public interface LayoutManager2 extends LayoutManager {
   void addLayoutComponent(Component var1, Object var2);

   Dimension maximumLayoutSize(Container var1);

   float getLayoutAlignmentX(Container var1);

   float getLayoutAlignmentY(Container var1);

   void invalidateLayout(Container var1);
}
