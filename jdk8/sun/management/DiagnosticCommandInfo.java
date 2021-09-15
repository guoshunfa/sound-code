package sun.management;

import java.util.List;

class DiagnosticCommandInfo {
   private final String name;
   private final String description;
   private final String impact;
   private final String permissionClass;
   private final String permissionName;
   private final String permissionAction;
   private final boolean enabled;
   private final List<DiagnosticCommandArgumentInfo> arguments;

   String getName() {
      return this.name;
   }

   String getDescription() {
      return this.description;
   }

   String getImpact() {
      return this.impact;
   }

   String getPermissionClass() {
      return this.permissionClass;
   }

   String getPermissionName() {
      return this.permissionName;
   }

   String getPermissionAction() {
      return this.permissionAction;
   }

   boolean isEnabled() {
      return this.enabled;
   }

   List<DiagnosticCommandArgumentInfo> getArgumentsInfo() {
      return this.arguments;
   }

   DiagnosticCommandInfo(String var1, String var2, String var3, String var4, String var5, String var6, boolean var7, List<DiagnosticCommandArgumentInfo> var8) {
      this.name = var1;
      this.description = var2;
      this.impact = var3;
      this.permissionClass = var4;
      this.permissionName = var5;
      this.permissionAction = var6;
      this.enabled = var7;
      this.arguments = var8;
   }
}
