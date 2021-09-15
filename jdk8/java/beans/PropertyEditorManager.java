package java.beans;

public class PropertyEditorManager {
   public static void registerEditor(Class<?> var0, Class<?> var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPropertiesAccess();
      }

      ThreadGroupContext.getContext().getPropertyEditorFinder().register(var0, var1);
   }

   public static PropertyEditor findEditor(Class<?> var0) {
      return ThreadGroupContext.getContext().getPropertyEditorFinder().find(var0);
   }

   public static String[] getEditorSearchPath() {
      return ThreadGroupContext.getContext().getPropertyEditorFinder().getPackages();
   }

   public static void setEditorSearchPath(String[] var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPropertiesAccess();
      }

      ThreadGroupContext.getContext().getPropertyEditorFinder().setPackages(var0);
   }
}
