package javax.swing;

final class LegacyLayoutFocusTraversalPolicy extends LayoutFocusTraversalPolicy {
   LegacyLayoutFocusTraversalPolicy(DefaultFocusManager var1) {
      super(new CompareTabOrderComparator(var1));
   }
}
