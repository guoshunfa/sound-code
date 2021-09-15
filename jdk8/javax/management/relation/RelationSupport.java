package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class RelationSupport implements RelationSupportMBean, MBeanRegistration {
   private String myRelId = null;
   private ObjectName myRelServiceName = null;
   private MBeanServer myRelServiceMBeanServer = null;
   private String myRelTypeName = null;
   private final Map<String, Role> myRoleName2ValueMap = new HashMap();
   private final AtomicBoolean myInRelServFlg = new AtomicBoolean();

   public RelationSupport(String var1, ObjectName var2, String var3, RoleList var4) throws InvalidRoleValueException, IllegalArgumentException {
      JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
      this.initMembers(var1, var2, (MBeanServer)null, var3, var4);
      JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
   }

   public RelationSupport(String var1, ObjectName var2, MBeanServer var3, String var4, RoleList var5) throws InvalidRoleValueException, IllegalArgumentException {
      if (var3 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
         this.initMembers(var1, var2, var3, var4, var5);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
      }
   }

   public List<ObjectName> getRole(String var1) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException {
      if (var1 == null) {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRole", (Object)var1);
         List var2 = (List)Util.cast(this.getRoleInt(var1, false, (RelationService)null, false));
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRole");
         return var2;
      }
   }

   public RoleResult getRoles(String[] var1) throws IllegalArgumentException, RelationServiceNotRegisteredException {
      if (var1 == null) {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoles");
         RoleResult var2 = this.getRolesInt(var1, false, (RelationService)null);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoles");
         return var2;
      }
   }

   public RoleResult getAllRoles() throws RelationServiceNotRegisteredException {
      JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRoles");
      RoleResult var1 = null;

      try {
         var1 = this.getAllRolesInt(false, (RelationService)null);
      } catch (IllegalArgumentException var3) {
      }

      JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRoles");
      return var1;
   }

   public RoleList retrieveAllRoles() {
      JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "retrieveAllRoles");
      RoleList var1;
      synchronized(this.myRoleName2ValueMap) {
         var1 = new RoleList(new ArrayList(this.myRoleName2ValueMap.values()));
      }

      JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "retrieveAllRoles");
      return var1;
   }

   public Integer getRoleCardinality(String var1) throws IllegalArgumentException, RoleNotFoundException {
      if (var1 == null) {
         String var7 = "Invalid parameter.";
         throw new IllegalArgumentException(var7);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleCardinality", (Object)var1);
         Role var2;
         synchronized(this.myRoleName2ValueMap) {
            var2 = (Role)this.myRoleName2ValueMap.get(var1);
         }

         if (var2 == null) {
            byte var3 = 1;

            try {
               RelationService.throwRoleProblemException(var3, var1);
            } catch (InvalidRoleValueException var5) {
            }
         }

         List var8 = var2.getRoleValue();
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleCardinality");
         return var8.size();
      }
   }

   public void setRole(Role var1) throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRole", (Object)var1);
         this.setRoleInt(var1, false, (RelationService)null, false);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRole");
      }
   }

   public RoleResult setRoles(RoleList var1) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
      if (var1 == null) {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoles", (Object)var1);
         RoleResult var2 = this.setRolesInt(var1, false, (RelationService)null);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoles");
         return var2;
      }
   }

   public void handleMBeanUnregistration(ObjectName var1, String var2) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistration", new Object[]{var1, var2});
         this.handleMBeanUnregistrationInt(var1, var2, false, (RelationService)null);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistration");
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public Map<ObjectName, List<String>> getReferencedMBeans() {
      JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getReferencedMBeans");
      HashMap var1 = new HashMap();
      synchronized(this.myRoleName2ValueMap) {
         Iterator var3 = this.myRoleName2ValueMap.values().iterator();

         while(var3.hasNext()) {
            Role var4 = (Role)var3.next();
            String var5 = var4.getRoleName();
            List var6 = var4.getRoleValue();
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               ObjectName var8 = (ObjectName)var7.next();
               Object var9 = (List)var1.get(var8);
               boolean var10 = false;
               if (var9 == null) {
                  var10 = true;
                  var9 = new ArrayList();
               }

               ((List)var9).add(var5);
               if (var10) {
                  var1.put(var8, var9);
               }
            }
         }
      }

      JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getReferencedMBeans");
      return var1;
   }

   public String getRelationTypeName() {
      return this.myRelTypeName;
   }

   public ObjectName getRelationServiceName() {
      return this.myRelServiceName;
   }

   public String getRelationId() {
      return this.myRelId;
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      this.myRelServiceMBeanServer = var1;
      return var2;
   }

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
   }

   public void postDeregister() {
   }

   public Boolean isInRelationService() {
      return this.myInRelServFlg.get();
   }

   public void setRelationServiceManagementFlag(Boolean var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         this.myInRelServFlg.set(var1);
      }
   }

   Object getRoleInt(String var1, boolean var2, RelationService var3, boolean var4) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException {
      if (var1 != null && (!var2 || var3 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleInt", (Object)var1);
         boolean var17 = false;
         Role var6;
         synchronized(this.myRoleName2ValueMap) {
            var6 = (Role)this.myRoleName2ValueMap.get(var1);
         }

         int var18;
         if (var6 == null) {
            var18 = 1;
         } else {
            Integer var7;
            if (var2) {
               try {
                  var7 = var3.checkRoleReading(var1, this.myRelTypeName);
               } catch (RelationTypeNotFoundException var15) {
                  throw new RuntimeException(var15.getMessage());
               }
            } else {
               Object[] var8 = new Object[]{var1, this.myRelTypeName};
               String[] var9 = new String[]{"java.lang.String", "java.lang.String"};

               try {
                  var7 = (Integer)((Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleReading", var8, var9));
               } catch (MBeanException var12) {
                  throw new RuntimeException("incorrect relation type");
               } catch (ReflectionException var13) {
                  throw new RuntimeException(var13.getMessage());
               } catch (InstanceNotFoundException var14) {
                  throw new RelationServiceNotRegisteredException(var14.getMessage());
               }
            }

            var18 = var7;
         }

         Object var19;
         if (var18 == 0) {
            if (!var4) {
               var19 = new ArrayList(var6.getRoleValue());
            } else {
               var19 = (Role)((Role)var6.clone());
            }
         } else {
            if (!var4) {
               try {
                  RelationService.throwRoleProblemException(var18, var1);
                  return null;
               } catch (InvalidRoleValueException var11) {
                  throw new RuntimeException(var11.getMessage());
               }
            }

            var19 = new RoleUnresolved(var1, (List)null, var18);
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleInt");
         return var19;
      } else {
         String var5 = "Invalid parameter.";
         throw new IllegalArgumentException(var5);
      }
   }

   RoleResult getRolesInt(String[] var1, boolean var2, RelationService var3) throws IllegalArgumentException, RelationServiceNotRegisteredException {
      if (var1 != null && (!var2 || var3 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRolesInt");
         RoleList var13 = new RoleList();
         RoleUnresolvedList var5 = new RoleUnresolvedList();

         for(int var6 = 0; var6 < var1.length; ++var6) {
            String var7 = var1[var6];

            Object var8;
            try {
               var8 = this.getRoleInt(var7, var2, var3, true);
            } catch (RoleNotFoundException var12) {
               return null;
            }

            if (var8 instanceof Role) {
               try {
                  var13.add((Role)var8);
               } catch (IllegalArgumentException var11) {
                  throw new RuntimeException(var11.getMessage());
               }
            } else if (var8 instanceof RoleUnresolved) {
               try {
                  var5.add((RoleUnresolved)var8);
               } catch (IllegalArgumentException var10) {
                  throw new RuntimeException(var10.getMessage());
               }
            }
         }

         RoleResult var14 = new RoleResult(var13, var5);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRolesInt");
         return var14;
      } else {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   RoleResult getAllRolesInt(boolean var1, RelationService var2) throws IllegalArgumentException, RelationServiceNotRegisteredException {
      if (var1 && var2 == null) {
         String var7 = "Invalid parameter.";
         throw new IllegalArgumentException(var7);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRolesInt");
         ArrayList var3;
         synchronized(this.myRoleName2ValueMap) {
            var3 = new ArrayList(this.myRoleName2ValueMap.keySet());
         }

         String[] var4 = new String[var3.size()];
         var3.toArray(var4);
         RoleResult var5 = this.getRolesInt(var4, var1, var2);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRolesInt");
         return var5;
      }
   }

   Object setRoleInt(Role var1, boolean var2, RelationService var3, boolean var4) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
      String var5;
      if (var1 != null && (!var2 || var3 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoleInt", new Object[]{var1, var2, var3, var4});
         var5 = var1.getRoleName();
         boolean var6 = false;
         Role var7;
         synchronized(this.myRoleName2ValueMap) {
            var7 = (Role)this.myRoleName2ValueMap.get(var5);
         }

         Object var8;
         Boolean var9;
         if (var7 == null) {
            var9 = true;
            var8 = new ArrayList();
         } else {
            var9 = false;
            var8 = var7.getRoleValue();
         }

         int var20;
         try {
            Integer var10;
            if (var2) {
               var10 = var3.checkRoleWriting(var1, this.myRelTypeName, var9);
            } else {
               Object[] var22 = new Object[]{var1, this.myRelTypeName, var9};
               String[] var12 = new String[]{"javax.management.relation.Role", "java.lang.String", "java.lang.Boolean"};
               var10 = (Integer)((Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleWriting", var22, var12));
            }

            var20 = var10;
         } catch (MBeanException var16) {
            Exception var11 = var16.getTargetException();
            if (var11 instanceof RelationTypeNotFoundException) {
               throw (RelationTypeNotFoundException)var11;
            }

            throw new RuntimeException(var11.getMessage());
         } catch (ReflectionException var17) {
            throw new RuntimeException(var17.getMessage());
         } catch (RelationTypeNotFoundException var18) {
            throw new RuntimeException(var18.getMessage());
         } catch (InstanceNotFoundException var19) {
            throw new RelationServiceNotRegisteredException(var19.getMessage());
         }

         Object var21 = null;
         if (var20 == 0) {
            if (!var9) {
               this.sendRoleUpdateNotification(var1, (List)var8, var2, var3);
               this.updateRelationServiceMap(var1, (List)var8, var2, var3);
            }

            synchronized(this.myRoleName2ValueMap) {
               this.myRoleName2ValueMap.put(var5, (Role)((Role)var1.clone()));
            }

            if (var4) {
               var21 = var1;
            }
         } else {
            if (!var4) {
               RelationService.throwRoleProblemException(var20, var5);
               return null;
            }

            var21 = new RoleUnresolved(var5, var1.getRoleValue(), var20);
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoleInt");
         return var21;
      } else {
         var5 = "Invalid parameter.";
         throw new IllegalArgumentException(var5);
      }
   }

   private void sendRoleUpdateNotification(Role var1, List<ObjectName> var2, boolean var3, RelationService var4) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
      if (var1 == null || var2 == null || var3 && var4 == null) {
         String var13 = "Invalid parameter.";
         throw new IllegalArgumentException(var13);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "sendRoleUpdateNotification", new Object[]{var1, var2, var3, var4});
         if (var3) {
            try {
               var4.sendRoleUpdateNotification(this.myRelId, var1, var2);
            } catch (RelationNotFoundException var9) {
               throw new RuntimeException(var9.getMessage());
            }
         } else {
            Object[] var5 = new Object[]{this.myRelId, var1, var2};
            String[] var6 = new String[]{"java.lang.String", "javax.management.relation.Role", "java.util.List"};

            try {
               this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "sendRoleUpdateNotification", var5, var6);
            } catch (ReflectionException var10) {
               throw new RuntimeException(var10.getMessage());
            } catch (InstanceNotFoundException var11) {
               throw new RelationServiceNotRegisteredException(var11.getMessage());
            } catch (MBeanException var12) {
               Exception var8 = var12.getTargetException();
               if (var8 instanceof RelationNotFoundException) {
                  throw (RelationNotFoundException)var8;
               }

               throw new RuntimeException(var8.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "sendRoleUpdateNotification");
      }
   }

   private void updateRelationServiceMap(Role var1, List<ObjectName> var2, boolean var3, RelationService var4) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
      if (var1 == null || var2 == null || var3 && var4 == null) {
         String var13 = "Invalid parameter.";
         throw new IllegalArgumentException(var13);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "updateRelationServiceMap", new Object[]{var1, var2, var3, var4});
         if (var3) {
            try {
               var4.updateRoleMap(this.myRelId, var1, var2);
            } catch (RelationNotFoundException var9) {
               throw new RuntimeException(var9.getMessage());
            }
         } else {
            Object[] var5 = new Object[]{this.myRelId, var1, var2};
            String[] var6 = new String[]{"java.lang.String", "javax.management.relation.Role", "java.util.List"};

            try {
               this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "updateRoleMap", var5, var6);
            } catch (ReflectionException var10) {
               throw new RuntimeException(var10.getMessage());
            } catch (InstanceNotFoundException var11) {
               throw new RelationServiceNotRegisteredException(var11.getMessage());
            } catch (MBeanException var12) {
               Exception var8 = var12.getTargetException();
               if (var8 instanceof RelationNotFoundException) {
                  throw (RelationNotFoundException)var8;
               }

               throw new RuntimeException(var8.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "updateRelationServiceMap");
      }
   }

   RoleResult setRolesInt(RoleList var1, boolean var2, RelationService var3) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
      if (var1 != null && (!var2 || var3 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRolesInt", new Object[]{var1, var2, var3});
         RoleList var14 = new RoleList();
         RoleUnresolvedList var5 = new RoleUnresolvedList();
         Iterator var6 = var1.asList().iterator();

         while(var6.hasNext()) {
            Role var7 = (Role)var6.next();
            Object var8 = null;

            try {
               var8 = this.setRoleInt(var7, var2, var3, true);
            } catch (RoleNotFoundException var12) {
            } catch (InvalidRoleValueException var13) {
            }

            if (var8 instanceof Role) {
               try {
                  var14.add((Role)var8);
               } catch (IllegalArgumentException var11) {
                  throw new RuntimeException(var11.getMessage());
               }
            } else if (var8 instanceof RoleUnresolved) {
               try {
                  var5.add((RoleUnresolved)var8);
               } catch (IllegalArgumentException var10) {
                  throw new RuntimeException(var10.getMessage());
               }
            }
         }

         RoleResult var15 = new RoleResult(var14, var5);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRolesInt");
         return var15;
      } else {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   private void initMembers(String var1, ObjectName var2, MBeanServer var3, String var4, RoleList var5) throws InvalidRoleValueException, IllegalArgumentException {
      if (var1 != null && var2 != null && var4 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initMembers", new Object[]{var1, var2, var3, var4, var5});
         this.myRelId = var1;
         this.myRelServiceName = var2;
         this.myRelServiceMBeanServer = var3;
         this.myRelTypeName = var4;
         this.initRoleMap(var5);
         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initMembers");
      } else {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      }
   }

   private void initRoleMap(RoleList var1) throws InvalidRoleValueException {
      if (var1 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initRoleMap", (Object)var1);
         synchronized(this.myRoleName2ValueMap) {
            Iterator var3 = var1.asList().iterator();

            while(true) {
               if (!var3.hasNext()) {
                  break;
               }

               Role var4 = (Role)var3.next();
               String var5 = var4.getRoleName();
               if (this.myRoleName2ValueMap.containsKey(var5)) {
                  StringBuilder var6 = new StringBuilder("Role name ");
                  var6.append(var5);
                  var6.append(" used for two roles.");
                  throw new InvalidRoleValueException(var6.toString());
               }

               this.myRoleName2ValueMap.put(var5, (Role)((Role)var4.clone()));
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initRoleMap");
      }
   }

   void handleMBeanUnregistrationInt(ObjectName var1, String var2, boolean var3, RelationService var4) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
      if (var1 != null && var2 != null && (!var3 || var4 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistrationInt", new Object[]{var1, var2, var3, var4});
         Role var11;
         synchronized(this.myRoleName2ValueMap) {
            var11 = (Role)this.myRoleName2ValueMap.get(var2);
         }

         if (var11 == null) {
            StringBuilder var12 = new StringBuilder();
            String var13 = "No role with name ";
            var12.append(var13);
            var12.append(var2);
            throw new RoleNotFoundException(var12.toString());
         } else {
            List var6 = var11.getRoleValue();
            ArrayList var7 = new ArrayList(var6);
            var7.remove(var1);
            Role var8 = new Role(var2, var7);
            this.setRoleInt(var8, var3, var4, false);
            JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistrationInt");
         }
      } else {
         String var5 = "Invalid parameter.";
         throw new IllegalArgumentException(var5);
      }
   }
}
