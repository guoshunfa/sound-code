package javax.accessibility;

public interface AccessibleExtendedComponent extends AccessibleComponent {
   String getToolTipText();

   String getTitledBorderText();

   AccessibleKeyBinding getAccessibleKeyBinding();
}
