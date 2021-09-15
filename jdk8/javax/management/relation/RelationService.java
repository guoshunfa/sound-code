package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class RelationService extends NotificationBroadcasterSupport implements RelationServiceMBean, MBeanRegistration, NotificationListener {
   private Map<String, Object> myRelId2ObjMap = new HashMap();
   private Map<String, String> myRelId2RelTypeMap = new HashMap();
   private Map<ObjectName, String> myRelMBeanObjName2RelIdMap = new HashMap();
   private Map<String, RelationType> myRelType2ObjMap = new HashMap();
   private Map<String, List<String>> myRelType2RelIdsMap = new HashMap();
   private final Map<ObjectName, Map<String, List<String>>> myRefedMBeanObjName2RelIdsMap = new HashMap();
   private boolean myPurgeFlag = true;
   private final AtomicLong atomicSeqNo = new AtomicLong();
   private ObjectName myObjName = null;
   private MBeanServer myMBeanServer = null;
   private MBeanServerNotificationFilter myUnregNtfFilter = null;
   private List<MBeanServerNotification> myUnregNtfList = new ArrayList();

   public RelationService(boolean var1) {
      JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "RelationService");
      this.setPurgeFlag(var1);
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "RelationService");
   }

   public void isActive() throws RelationServiceNotRegisteredException {
      if (this.myMBeanServer == null) {
         String var1 = "Relation Service not registered in the MBean Server.";
         throw new RelationServiceNotRegisteredException(var1);
      }
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      this.myMBeanServer = var1;
      this.myObjName = var2;
      return var2;
   }

   public void postRegister(Boolean var1) {
   }

   public void preDeregister() throws Exception {
   }

   public void postDeregister() {
   }

   public boolean getPurgeFlag() {
      return this.myPurgeFlag;
   }

   public void setPurgeFlag(boolean var1) {
      this.myPurgeFlag = var1;
   }

   public void createRelationType(String var1, RoleInfo[] var2) throws IllegalArgumentException, InvalidRelationTypeException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelationType", (Object)var1);
         RelationTypeSupport var4 = new RelationTypeSupport(var1, var2);
         this.addRelationTypeInt(var4);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelationType");
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public void addRelationType(RelationType var1) throws IllegalArgumentException, InvalidRelationTypeException {
      if (var1 == null) {
         String var7 = "Invalid parameter.";
         throw new IllegalArgumentException(var7);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationType");
         List var2 = var1.getRoleInfos();
         if (var2 == null) {
            String var8 = "No role info provided.";
            throw new InvalidRelationTypeException(var8);
         } else {
            RoleInfo[] var3 = new RoleInfo[var2.size()];
            int var4 = 0;

            for(Iterator var5 = var2.iterator(); var5.hasNext(); ++var4) {
               RoleInfo var6 = (RoleInfo)var5.next();
               var3[var4] = var6;
            }

            RelationTypeSupport.checkRoleInfos(var3);
            this.addRelationTypeInt(var1);
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationType");
         }
      }
   }

   public List<String> getAllRelationTypeNames() {
      synchronized(this.myRelType2ObjMap) {
         ArrayList var1 = new ArrayList(this.myRelType2ObjMap.keySet());
         return var1;
      }
   }

   public List<RoleInfo> getRoleInfos(String var1) throws IllegalArgumentException, RelationTypeNotFoundException {
      if (var1 == null) {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfos", (Object)var1);
         RelationType var2 = this.getRelationType(var1);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfos");
         return var2.getRoleInfos();
      }
   }

   public RoleInfo getRoleInfo(String var1, String var2) throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfo", new Object[]{var1, var2});
         RelationType var5 = this.getRelationType(var1);
         RoleInfo var4 = var5.getRoleInfo(var2);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfo");
         return var4;
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public void removeRelationType(String var1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException {
      this.isActive();
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelationType", (Object)var1);
         this.getRelationType(var1);
         ArrayList var3 = null;
         synchronized(this.myRelType2RelIdsMap) {
            List var5 = (List)this.myRelType2RelIdsMap.get(var1);
            if (var5 != null) {
               var3 = new ArrayList(var5);
            }
         }

         synchronized(this.myRelType2ObjMap) {
            this.myRelType2ObjMap.remove(var1);
         }

         synchronized(this.myRelType2RelIdsMap) {
            this.myRelType2RelIdsMap.remove(var1);
         }

         if (var3 != null) {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               String var13 = (String)var4.next();

               try {
                  this.removeRelation(var13);
               } catch (RelationNotFoundException var9) {
                  throw new RuntimeException(var9.getMessage());
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelationType");
      }
   }

   public void createRelation(String var1, String var2, RoleList var3) throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException {
      this.isActive();
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelation", new Object[]{var1, var2, var3});
         RelationSupport var5 = new RelationSupport(var1, this.myObjName, var2, var3);
         this.addRelationInt(true, var5, (ObjectName)null, var1, var2, var3);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelation");
      } else {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   public void addRelation(ObjectName var1) throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException {
      String var2;
      if (var1 == null) {
         var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelation", (Object)var1);
         this.isActive();
         if (!this.myMBeanServer.isInstanceOf(var1, "javax.management.relation.Relation")) {
            var2 = "This MBean does not implement the Relation interface.";
            throw new NoSuchMethodException(var2);
         } else {
            try {
               var2 = (String)((String)this.myMBeanServer.getAttribute(var1, "RelationId"));
            } catch (MBeanException var19) {
               throw new RuntimeException(var19.getTargetException().getMessage());
            } catch (ReflectionException var20) {
               throw new RuntimeException(var20.getMessage());
            } catch (AttributeNotFoundException var21) {
               throw new RuntimeException(var21.getMessage());
            }

            if (var2 == null) {
               String var22 = "This MBean does not provide a relation id.";
               throw new InvalidRelationIdException(var22);
            } else {
               ObjectName var3;
               try {
                  var3 = (ObjectName)((ObjectName)this.myMBeanServer.getAttribute(var1, "RelationServiceName"));
               } catch (MBeanException var16) {
                  throw new RuntimeException(var16.getTargetException().getMessage());
               } catch (ReflectionException var17) {
                  throw new RuntimeException(var17.getMessage());
               } catch (AttributeNotFoundException var18) {
                  throw new RuntimeException(var18.getMessage());
               }

               boolean var4 = false;
               if (var3 == null) {
                  var4 = true;
               } else if (!var3.equals(this.myObjName)) {
                  var4 = true;
               }

               String var5;
               if (var4) {
                  var5 = "The Relation Service referenced in the MBean is not the current one.";
                  throw new InvalidRelationServiceException(var5);
               } else {
                  try {
                     var5 = (String)((String)this.myMBeanServer.getAttribute(var1, "RelationTypeName"));
                  } catch (MBeanException var13) {
                     throw new RuntimeException(var13.getTargetException().getMessage());
                  } catch (ReflectionException var14) {
                     throw new RuntimeException(var14.getMessage());
                  } catch (AttributeNotFoundException var15) {
                     throw new RuntimeException(var15.getMessage());
                  }

                  if (var5 == null) {
                     String var23 = "No relation type provided.";
                     throw new RelationTypeNotFoundException(var23);
                  } else {
                     RoleList var6;
                     try {
                        var6 = (RoleList)((RoleList)this.myMBeanServer.invoke(var1, "retrieveAllRoles", (Object[])null, (String[])null));
                     } catch (MBeanException var11) {
                        throw new RuntimeException(var11.getTargetException().getMessage());
                     } catch (ReflectionException var12) {
                        throw new RuntimeException(var12.getMessage());
                     }

                     this.addRelationInt(false, (RelationSupport)null, var1, var2, var5, var6);
                     synchronized(this.myRelMBeanObjName2RelIdMap) {
                        this.myRelMBeanObjName2RelIdMap.put(var1, var2);
                     }

                     try {
                        this.myMBeanServer.setAttribute(var1, new Attribute("RelationServiceManagementFlag", Boolean.TRUE));
                     } catch (Exception var9) {
                     }

                     ArrayList var7 = new ArrayList();
                     var7.add(var1);
                     this.updateUnregistrationListener(var7, (List)null);
                     JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelation");
                  }
               }
            }
         }
      }
   }

   public ObjectName isRelationMBean(String var1) throws IllegalArgumentException, RelationNotFoundException {
      if (var1 == null) {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelationMBean", (Object)var1);
         Object var2 = this.getRelation(var1);
         return var2 instanceof ObjectName ? (ObjectName)var2 : null;
      }
   }

   public String isRelation(ObjectName var1) throws IllegalArgumentException {
      String var2;
      if (var1 == null) {
         var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelation", (Object)var1);
         var2 = null;
         synchronized(this.myRelMBeanObjName2RelIdMap) {
            String var4 = (String)this.myRelMBeanObjName2RelIdMap.get(var1);
            if (var4 != null) {
               var2 = var4;
            }

            return var2;
         }
      }
   }

   public Boolean hasRelation(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "hasRelation", (Object)var1);

         try {
            this.getRelation(var1);
            return true;
         } catch (RelationNotFoundException var3) {
            return false;
         }
      }
   }

   public List<String> getAllRelationIds() {
      synchronized(this.myRelId2ObjMap) {
         ArrayList var1 = new ArrayList(this.myRelId2ObjMap.keySet());
         return var1;
      }
   }

   public Integer checkRoleReading(String var1, String var2) throws IllegalArgumentException, RelationTypeNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleReading", new Object[]{var1, var2});
         RelationType var4 = this.getRelationType(var2);

         Integer var7;
         try {
            RoleInfo var5 = var4.getRoleInfo(var1);
            var7 = this.checkRoleInt(1, var1, (List)null, var5, false);
         } catch (RoleInfoNotFoundException var6) {
            var7 = 1;
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleReading");
         return var7;
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public Integer checkRoleWriting(Role var1, String var2, Boolean var3) throws IllegalArgumentException, RelationTypeNotFoundException {
      if (var1 != null && var2 != null && var3 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleWriting", new Object[]{var1, var2, var3});
         RelationType var11 = this.getRelationType(var2);
         String var5 = var1.getRoleName();
         List var6 = var1.getRoleValue();
         boolean var7 = true;
         if (var3) {
            var7 = false;
         }

         RoleInfo var8;
         try {
            var8 = var11.getRoleInfo(var5);
         } catch (RoleInfoNotFoundException var10) {
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
            return 1;
         }

         Integer var9 = this.checkRoleInt(2, var5, var6, var8, var7);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
         return var9;
      } else {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   public void sendRelationCreationNotification(String var1) throws IllegalArgumentException, RelationNotFoundException {
      if (var1 == null) {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationCreationNotification", (Object)var1);
         StringBuilder var2 = new StringBuilder("Creation of relation ");
         var2.append(var1);
         this.sendNotificationInt(1, var2.toString(), var1, (List)null, (String)null, (List)null, (List)null);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationCreationNotification");
      }
   }

   public void sendRoleUpdateNotification(String var1, Role var2, List<ObjectName> var3) throws IllegalArgumentException, RelationNotFoundException {
      String var4;
      if (var1 != null && var2 != null && var3 != null) {
         if (!(var3 instanceof ArrayList)) {
            var3 = new ArrayList((Collection)var3);
         }

         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRoleUpdateNotification", new Object[]{var1, var2, var3});
         var4 = var2.getRoleName();
         List var5 = var2.getRoleValue();
         String var6 = Role.roleValueToString(var5);
         String var7 = Role.roleValueToString((List)var3);
         StringBuilder var8 = new StringBuilder("Value of role ");
         var8.append(var4);
         var8.append(" has changed\nOld value:\n");
         var8.append(var7);
         var8.append("\nNew value:\n");
         var8.append(var6);
         this.sendNotificationInt(2, var8.toString(), var1, (List)null, var4, var5, (List)var3);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRoleUpdateNotification");
      } else {
         var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   public void sendRelationRemovalNotification(String var1, List<ObjectName> var2) throws IllegalArgumentException, RelationNotFoundException {
      if (var1 == null) {
         String var3 = "Invalid parameter";
         throw new IllegalArgumentException(var3);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationRemovalNotification", new Object[]{var1, var2});
         this.sendNotificationInt(3, "Removal of relation " + var1, var1, var2, (String)null, (List)null, (List)null);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationRemovalNotification");
      }
   }

   public void updateRoleMap(String var1, Role var2, List<ObjectName> var3) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
      if (var1 != null && var2 != null && var3 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateRoleMap", new Object[]{var1, var2, var3});
         this.isActive();
         this.getRelation(var1);
         String var5 = var2.getRoleName();
         List var6 = var2.getRoleValue();
         ArrayList var7 = new ArrayList(var3);
         ArrayList var8 = new ArrayList();
         Iterator var9 = var6.iterator();

         boolean var12;
         while(var9.hasNext()) {
            ObjectName var10 = (ObjectName)var9.next();
            int var11 = var7.indexOf(var10);
            if (var11 == -1) {
               var12 = this.addNewMBeanReference(var10, var1, var5);
               if (var12) {
                  var8.add(var10);
               }
            } else {
               var7.remove(var11);
            }
         }

         ArrayList var15 = new ArrayList();
         Iterator var13 = var7.iterator();

         while(var13.hasNext()) {
            ObjectName var14 = (ObjectName)var13.next();
            var12 = this.removeMBeanReference(var14, var1, var5, false);
            if (var12) {
               var15.add(var14);
            }
         }

         this.updateUnregistrationListener(var8, var15);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateRoleMap");
      } else {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   public void removeRelation(String var1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
      this.isActive();
      if (var1 == null) {
         String var19 = "Invalid parameter.";
         throw new IllegalArgumentException(var19);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelation", (Object)var1);
         Object var2 = this.getRelation(var1);
         ArrayList var3;
         if (var2 instanceof ObjectName) {
            var3 = new ArrayList();
            var3.add((ObjectName)var2);
            this.updateUnregistrationListener((List)null, var3);
         }

         this.sendRelationRemovalNotification(var1, (List)null);
         var3 = new ArrayList();
         ArrayList var4 = new ArrayList();
         synchronized(this.myRefedMBeanObjName2RelIdsMap) {
            Iterator var6 = this.myRefedMBeanObjName2RelIdsMap.keySet().iterator();

            ObjectName var7;
            while(var6.hasNext()) {
               var7 = (ObjectName)var6.next();
               Map var8 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(var7);
               if (var8.containsKey(var1)) {
                  var8.remove(var1);
                  var3.add(var7);
               }

               if (var8.isEmpty()) {
                  var4.add(var7);
               }
            }

            var6 = var4.iterator();

            while(var6.hasNext()) {
               var7 = (ObjectName)var6.next();
               this.myRefedMBeanObjName2RelIdsMap.remove(var7);
            }
         }

         synchronized(this.myRelId2ObjMap) {
            this.myRelId2ObjMap.remove(var1);
         }

         if (var2 instanceof ObjectName) {
            synchronized(this.myRelMBeanObjName2RelIdMap) {
               this.myRelMBeanObjName2RelIdMap.remove((ObjectName)var2);
            }
         }

         String var5;
         synchronized(this.myRelId2RelTypeMap) {
            var5 = (String)this.myRelId2RelTypeMap.get(var1);
            this.myRelId2RelTypeMap.remove(var1);
         }

         synchronized(this.myRelType2RelIdsMap) {
            List var20 = (List)this.myRelType2RelIdsMap.get(var5);
            if (var20 != null) {
               var20.remove(var1);
               if (var20.isEmpty()) {
                  this.myRelType2RelIdsMap.remove(var5);
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelation");
      }
   }

   public void purgeRelations() throws RelationServiceNotRegisteredException {
      JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "purgeRelations");
      this.isActive();
      ArrayList var1;
      synchronized(this.myRefedMBeanObjName2RelIdsMap) {
         var1 = new ArrayList(this.myUnregNtfList);
         this.myUnregNtfList = new ArrayList();
      }

      ArrayList var2 = new ArrayList();
      HashMap var3 = new HashMap();
      synchronized(this.myRefedMBeanObjName2RelIdsMap) {
         Iterator var5 = var1.iterator();

         while(true) {
            if (!var5.hasNext()) {
               break;
            }

            MBeanServerNotification var6 = (MBeanServerNotification)var5.next();
            ObjectName var7 = var6.getMBeanName();
            var2.add(var7);
            Map var8 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(var7);
            var3.put(var7, var8);
            this.myRefedMBeanObjName2RelIdsMap.remove(var7);
         }
      }

      this.updateUnregistrationListener((List)null, var2);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         MBeanServerNotification var17 = (MBeanServerNotification)var4.next();
         ObjectName var18 = var17.getMBeanName();
         Map var19 = (Map)var3.get(var18);
         Iterator var20 = var19.entrySet().iterator();

         while(var20.hasNext()) {
            Map.Entry var9 = (Map.Entry)var20.next();
            String var10 = (String)var9.getKey();
            List var11 = (List)var9.getValue();

            try {
               this.handleReferenceUnregistration(var10, var18, var11);
            } catch (RelationNotFoundException var13) {
               throw new RuntimeException(var13.getMessage());
            } catch (RoleNotFoundException var14) {
               throw new RuntimeException(var14.getMessage());
            }
         }
      }

      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "purgeRelations");
   }

   public Map<String, List<String>> findReferencingRelations(ObjectName var1, String var2, String var3) throws IllegalArgumentException {
      if (var1 == null) {
         String var18 = "Invalid parameter.";
         throw new IllegalArgumentException(var18);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findReferencingRelations", new Object[]{var1, var2, var3});
         HashMap var4 = new HashMap();
         synchronized(this.myRefedMBeanObjName2RelIdsMap) {
            Map var6 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(var1);
            if (var6 != null) {
               Set var7 = var6.keySet();
               ArrayList var8;
               Iterator var9;
               String var10;
               if (var2 == null) {
                  var8 = new ArrayList(var7);
               } else {
                  var8 = new ArrayList();
                  var9 = var7.iterator();

                  while(var9.hasNext()) {
                     var10 = (String)var9.next();
                     String var11;
                     synchronized(this.myRelId2RelTypeMap) {
                        var11 = (String)this.myRelId2RelTypeMap.get(var10);
                     }

                     if (var11.equals(var2)) {
                        var8.add(var10);
                     }
                  }
               }

               var9 = var8.iterator();

               while(var9.hasNext()) {
                  var10 = (String)var9.next();
                  List var17 = (List)var6.get(var10);
                  if (var3 == null) {
                     var4.put(var10, new ArrayList(var17));
                  } else if (var17.contains(var3)) {
                     ArrayList var12 = new ArrayList();
                     var12.add(var3);
                     var4.put(var10, var12);
                  }
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findReferencingRelations");
         return var4;
      }
   }

   public Map<ObjectName, List<String>> findAssociatedMBeans(ObjectName var1, String var2, String var3) throws IllegalArgumentException {
      if (var1 == null) {
         String var14 = "Invalid parameter.";
         throw new IllegalArgumentException(var14);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findAssociatedMBeans", new Object[]{var1, var2, var3});
         Map var4 = this.findReferencingRelations(var1, var2, var3);
         HashMap var5 = new HashMap();
         Iterator var6 = var4.keySet().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();

            Map var8;
            try {
               var8 = this.getReferencedMBeans(var7);
            } catch (RelationNotFoundException var12) {
               throw new RuntimeException(var12.getMessage());
            }

            Iterator var9 = var8.keySet().iterator();

            while(var9.hasNext()) {
               ObjectName var10 = (ObjectName)var9.next();
               if (!var10.equals(var1)) {
                  List var11 = (List)var5.get(var10);
                  if (var11 == null) {
                     ArrayList var13 = new ArrayList();
                     var13.add(var7);
                     var5.put(var10, var13);
                  } else {
                     var11.add(var7);
                  }
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findAssociatedMBeans");
         return var5;
      }
   }

   public List<String> findRelationsOfType(String var1) throws IllegalArgumentException, RelationTypeNotFoundException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findRelationsOfType");
         this.getRelationType(var1);
         ArrayList var3;
         synchronized(this.myRelType2RelIdsMap) {
            List var5 = (List)this.myRelType2RelIdsMap.get(var1);
            if (var5 == null) {
               var3 = new ArrayList();
            } else {
               var3 = new ArrayList(var5);
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findRelationsOfType");
         return var3;
      }
   }

   public List<ObjectName> getRole(String var1, String var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRole", new Object[]{var1, var2});
         this.isActive();
         Object var12 = this.getRelation(var1);
         Object var4;
         if (var12 instanceof RelationSupport) {
            var4 = (List)Util.cast(((RelationSupport)var12).getRoleInt(var2, true, this, false));
         } else {
            Object[] var5 = new Object[]{var2};
            String[] var6 = new String[]{"java.lang.String"};

            try {
               List var7 = (List)Util.cast(this.myMBeanServer.invoke((ObjectName)var12, "getRole", var5, var6));
               if (var7 != null && !(var7 instanceof ArrayList)) {
                  var4 = new ArrayList(var7);
               } else {
                  var4 = var7;
               }
            } catch (InstanceNotFoundException var9) {
               throw new RuntimeException(var9.getMessage());
            } catch (ReflectionException var10) {
               throw new RuntimeException(var10.getMessage());
            } catch (MBeanException var11) {
               Exception var8 = var11.getTargetException();
               if (var8 instanceof RoleNotFoundException) {
                  throw (RoleNotFoundException)var8;
               }

               throw new RuntimeException(var8.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRole");
         return (List)var4;
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public RoleResult getRoles(String var1, String[] var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", (Object)var1);
         this.isActive();
         Object var12 = this.getRelation(var1);
         RoleResult var4;
         if (var12 instanceof RelationSupport) {
            var4 = ((RelationSupport)var12).getRolesInt(var2, true, this);
         } else {
            Object[] var5 = new Object[]{var2};
            String[] var6 = new String[1];

            try {
               var6[0] = var2.getClass().getName();
            } catch (Exception var11) {
            }

            try {
               var4 = (RoleResult)((RoleResult)this.myMBeanServer.invoke((ObjectName)var12, "getRoles", var5, var6));
            } catch (InstanceNotFoundException var8) {
               throw new RuntimeException(var8.getMessage());
            } catch (ReflectionException var9) {
               throw new RuntimeException(var9.getMessage());
            } catch (MBeanException var10) {
               throw new RuntimeException(var10.getTargetException().getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
         return var4;
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public RoleResult getAllRoles(String var1) throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException {
      if (var1 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", (Object)var1);
         Object var2 = this.getRelation(var1);
         RoleResult var3;
         if (var2 instanceof RelationSupport) {
            var3 = ((RelationSupport)var2).getAllRolesInt(true, this);
         } else {
            try {
               var3 = (RoleResult)((RoleResult)this.myMBeanServer.getAttribute((ObjectName)var2, "AllRoles"));
            } catch (Exception var5) {
               throw new RuntimeException(var5.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
         return var3;
      }
   }

   public Integer getRoleCardinality(String var1, String var2) throws IllegalArgumentException, RelationNotFoundException, RoleNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleCardinality", new Object[]{var1, var2});
         Object var12 = this.getRelation(var1);
         Integer var4;
         if (var12 instanceof RelationSupport) {
            var4 = ((RelationSupport)var12).getRoleCardinality(var2);
         } else {
            Object[] var5 = new Object[]{var2};
            String[] var6 = new String[]{"java.lang.String"};

            try {
               var4 = (Integer)((Integer)this.myMBeanServer.invoke((ObjectName)var12, "getRoleCardinality", var5, var6));
            } catch (InstanceNotFoundException var9) {
               throw new RuntimeException(var9.getMessage());
            } catch (ReflectionException var10) {
               throw new RuntimeException(var10.getMessage());
            } catch (MBeanException var11) {
               Exception var8 = var11.getTargetException();
               if (var8 instanceof RoleNotFoundException) {
                  throw (RoleNotFoundException)var8;
               }

               throw new RuntimeException(var8.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleCardinality");
         return var4;
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public void setRole(String var1, Role var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRole", new Object[]{var1, var2});
         this.isActive();
         Object var14 = this.getRelation(var1);
         if (var14 instanceof RelationSupport) {
            try {
               ((RelationSupport)var14).setRoleInt(var2, true, this, false);
            } catch (RelationTypeNotFoundException var8) {
               throw new RuntimeException(var8.getMessage());
            }
         } else {
            Object[] var4 = new Object[]{var2};
            String[] var5 = new String[]{"javax.management.relation.Role"};

            try {
               this.myMBeanServer.setAttribute((ObjectName)var14, new Attribute("Role", var2));
            } catch (InstanceNotFoundException var9) {
               throw new RuntimeException(var9.getMessage());
            } catch (ReflectionException var10) {
               throw new RuntimeException(var10.getMessage());
            } catch (MBeanException var11) {
               Exception var7 = var11.getTargetException();
               if (var7 instanceof RoleNotFoundException) {
                  throw (RoleNotFoundException)var7;
               }

               if (var7 instanceof InvalidRoleValueException) {
                  throw (InvalidRoleValueException)var7;
               }

               throw new RuntimeException(var7.getMessage());
            } catch (AttributeNotFoundException var12) {
               throw new RuntimeException(var12.getMessage());
            } catch (InvalidAttributeValueException var13) {
               throw new RuntimeException(var13.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRole");
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public RoleResult setRoles(String var1, RoleList var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
      if (var1 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRoles", new Object[]{var1, var2});
         this.isActive();
         Object var12 = this.getRelation(var1);
         RoleResult var4;
         if (var12 instanceof RelationSupport) {
            try {
               var4 = ((RelationSupport)var12).setRolesInt(var2, true, this);
            } catch (RelationTypeNotFoundException var11) {
               throw new RuntimeException(var11.getMessage());
            }
         } else {
            Object[] var5 = new Object[]{var2};
            String[] var6 = new String[]{"javax.management.relation.RoleList"};

            try {
               var4 = (RoleResult)((RoleResult)this.myMBeanServer.invoke((ObjectName)var12, "setRoles", var5, var6));
            } catch (InstanceNotFoundException var8) {
               throw new RuntimeException(var8.getMessage());
            } catch (ReflectionException var9) {
               throw new RuntimeException(var9.getMessage());
            } catch (MBeanException var10) {
               throw new RuntimeException(var10.getTargetException().getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRoles");
         return var4;
      } else {
         String var3 = "Invalid parameter.";
         throw new IllegalArgumentException(var3);
      }
   }

   public Map<ObjectName, List<String>> getReferencedMBeans(String var1) throws IllegalArgumentException, RelationNotFoundException {
      if (var1 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getReferencedMBeans", (Object)var1);
         Object var2 = this.getRelation(var1);
         Map var3;
         if (var2 instanceof RelationSupport) {
            var3 = ((RelationSupport)var2).getReferencedMBeans();
         } else {
            try {
               var3 = (Map)Util.cast(this.myMBeanServer.getAttribute((ObjectName)var2, "ReferencedMBeans"));
            } catch (Exception var5) {
               throw new RuntimeException(var5.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getReferencedMBeans");
         return var3;
      }
   }

   public String getRelationTypeName(String var1) throws IllegalArgumentException, RelationNotFoundException {
      if (var1 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationTypeName", (Object)var1);
         Object var2 = this.getRelation(var1);
         String var3;
         if (var2 instanceof RelationSupport) {
            var3 = ((RelationSupport)var2).getRelationTypeName();
         } else {
            try {
               var3 = (String)((String)this.myMBeanServer.getAttribute((ObjectName)var2, "RelationTypeName"));
            } catch (Exception var5) {
               throw new RuntimeException(var5.getMessage());
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationTypeName");
         return var3;
      }
   }

   public void handleNotification(Notification var1, Object var2) {
      if (var1 == null) {
         String var17 = "Invalid parameter.";
         throw new IllegalArgumentException(var17);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleNotification", (Object)var1);
         if (var1 instanceof MBeanServerNotification) {
            MBeanServerNotification var3 = (MBeanServerNotification)var1;
            String var4 = var1.getType();
            if (var4.equals("JMX.mbean.unregistered")) {
               ObjectName var5 = ((MBeanServerNotification)var1).getMBeanName();
               boolean var6 = false;
               synchronized(this.myRefedMBeanObjName2RelIdsMap) {
                  if (this.myRefedMBeanObjName2RelIdsMap.containsKey(var5)) {
                     synchronized(this.myUnregNtfList) {
                        this.myUnregNtfList.add(var3);
                     }

                     var6 = true;
                  }

                  if (var6 && this.myPurgeFlag) {
                     try {
                        this.purgeRelations();
                     } catch (Exception var14) {
                        throw new RuntimeException(var14.getMessage());
                     }
                  }
               }

               String var7;
               synchronized(this.myRelMBeanObjName2RelIdMap) {
                  var7 = (String)this.myRelMBeanObjName2RelIdMap.get(var5);
               }

               if (var7 != null) {
                  try {
                     this.removeRelation(var7);
                  } catch (Exception var12) {
                     throw new RuntimeException(var12.getMessage());
                  }
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleNotification");
      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getNotificationInfo");
      String var1 = "javax.management.relation.RelationNotification";
      String[] var2 = new String[]{"jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.update.basic", "jmx.relation.update.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean"};
      String var3 = "Sent when a relation is created, updated or deleted.";
      MBeanNotificationInfo var4 = new MBeanNotificationInfo(var2, var1, var3);
      JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getNotificationInfo");
      return new MBeanNotificationInfo[]{var4};
   }

   private void addRelationTypeInt(RelationType var1) throws IllegalArgumentException, InvalidRelationTypeException {
      String var2;
      if (var1 == null) {
         var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationTypeInt");
         var2 = var1.getRelationTypeName();

         try {
            RelationType var3 = this.getRelationType(var2);
            if (var3 != null) {
               String var4 = "There is already a relation type in the Relation Service with name ";
               StringBuilder var5 = new StringBuilder(var4);
               var5.append(var2);
               throw new InvalidRelationTypeException(var5.toString());
            }
         } catch (RelationTypeNotFoundException var8) {
         }

         synchronized(this.myRelType2ObjMap) {
            this.myRelType2ObjMap.put(var2, var1);
         }

         if (var1 instanceof RelationTypeSupport) {
            ((RelationTypeSupport)var1).setRelationServiceFlag(true);
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationTypeInt");
      }
   }

   RelationType getRelationType(String var1) throws IllegalArgumentException, RelationTypeNotFoundException {
      if (var1 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationType", (Object)var1);
         RelationType var2;
         synchronized(this.myRelType2ObjMap) {
            var2 = (RelationType)this.myRelType2ObjMap.get(var1);
         }

         if (var2 == null) {
            String var3 = "No relation type created in the Relation Service with the name ";
            StringBuilder var4 = new StringBuilder(var3);
            var4.append(var1);
            throw new RelationTypeNotFoundException(var4.toString());
         } else {
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationType");
            return var2;
         }
      }
   }

   Object getRelation(String var1) throws IllegalArgumentException, RelationNotFoundException {
      if (var1 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelation", (Object)var1);
         Object var2;
         synchronized(this.myRelId2ObjMap) {
            var2 = this.myRelId2ObjMap.get(var1);
         }

         if (var2 == null) {
            String var3 = "No relation associated to relation id " + var1;
            throw new RelationNotFoundException(var3);
         } else {
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelation");
            return var2;
         }
      }
   }

   private boolean addNewMBeanReference(ObjectName var1, String var2, String var3) throws IllegalArgumentException {
      if (var1 != null && var2 != null && var3 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addNewMBeanReference", new Object[]{var1, var2, var3});
         boolean var10 = false;
         synchronized(this.myRefedMBeanObjName2RelIdsMap) {
            Map var6 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(var1);
            ArrayList var7;
            if (var6 == null) {
               var10 = true;
               var7 = new ArrayList();
               var7.add(var3);
               HashMap var11 = new HashMap();
               var11.put(var2, var7);
               this.myRefedMBeanObjName2RelIdsMap.put(var1, var11);
            } else {
               List var12 = (List)var6.get(var2);
               if (var12 == null) {
                  var7 = new ArrayList();
                  var7.add(var3);
                  var6.put(var2, var7);
               } else {
                  var12.add(var3);
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addNewMBeanReference");
         return var10;
      } else {
         String var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }

   private boolean removeMBeanReference(ObjectName var1, String var2, String var3, boolean var4) throws IllegalArgumentException {
      if (var1 != null && var2 != null && var3 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeMBeanReference", new Object[]{var1, var2, var3, var4});
         boolean var12 = false;
         synchronized(this.myRefedMBeanObjName2RelIdsMap) {
            Map var7 = (Map)this.myRefedMBeanObjName2RelIdsMap.get(var1);
            if (var7 == null) {
               JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
               return true;
            }

            List var8 = null;
            if (!var4) {
               var8 = (List)var7.get(var2);
               int var9 = var8.indexOf(var3);
               if (var9 != -1) {
                  var8.remove(var9);
               }
            }

            if (var8.isEmpty() || var4) {
               var7.remove(var2);
            }

            if (var7.isEmpty()) {
               this.myRefedMBeanObjName2RelIdsMap.remove(var1);
               var12 = true;
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
         return var12;
      } else {
         String var5 = "Invalid parameter.";
         throw new IllegalArgumentException(var5);
      }
   }

   private void updateUnregistrationListener(List<ObjectName> var1, List<ObjectName> var2) throws RelationServiceNotRegisteredException {
      if (var1 == null || var2 == null || !var1.isEmpty() || !var2.isEmpty()) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateUnregistrationListener", new Object[]{var1, var2});
         this.isActive();
         if (var1 != null || var2 != null) {
            boolean var3 = false;
            if (this.myUnregNtfFilter == null) {
               this.myUnregNtfFilter = new MBeanServerNotificationFilter();
               var3 = true;
            }

            synchronized(this.myUnregNtfFilter) {
               Iterator var5;
               ObjectName var6;
               if (var1 != null) {
                  var5 = var1.iterator();

                  while(var5.hasNext()) {
                     var6 = (ObjectName)var5.next();
                     this.myUnregNtfFilter.enableObjectName(var6);
                  }
               }

               if (var2 != null) {
                  var5 = var2.iterator();

                  while(var5.hasNext()) {
                     var6 = (ObjectName)var5.next();
                     this.myUnregNtfFilter.disableObjectName(var6);
                  }
               }

               if (var3) {
                  try {
                     this.myMBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, (NotificationListener)this, this.myUnregNtfFilter, (Object)null);
                  } catch (InstanceNotFoundException var8) {
                     throw new RelationServiceNotRegisteredException(var8.getMessage());
                  }
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateUnregistrationListener");
      }
   }

   private void addRelationInt(boolean var1, RelationSupport var2, ObjectName var3, String var4, String var5, RoleList var6) throws IllegalArgumentException, RelationServiceNotRegisteredException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException {
      if (var4 != null && var5 != null && (!var1 || var2 != null && var3 == null) && (var1 || var3 != null && var2 == null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationInt", new Object[]{var1, var2, var3, var4, var5, var6});
         this.isActive();

         try {
            Object var27 = this.getRelation(var4);
            if (var27 != null) {
               String var29 = "There is already a relation with id ";
               StringBuilder var30 = new StringBuilder(var29);
               var30.append(var4);
               throw new InvalidRelationIdException(var30.toString());
            }
         } catch (RelationNotFoundException var25) {
         }

         RelationType var28 = this.getRelationType(var5);
         ArrayList var8 = new ArrayList(var28.getRoleInfos());
         Iterator var9;
         Role var10;
         if (var6 != null) {
            var9 = var6.asList().iterator();

            while(var9.hasNext()) {
               var10 = (Role)var9.next();
               String var11 = var10.getRoleName();
               List var12 = var10.getRoleValue();

               RoleInfo var13;
               try {
                  var13 = var28.getRoleInfo(var11);
               } catch (RoleInfoNotFoundException var24) {
                  throw new RoleNotFoundException(var24.getMessage());
               }

               Integer var14 = this.checkRoleInt(2, var11, var12, var13, false);
               int var15 = var14;
               if (var15 != 0) {
                  throwRoleProblemException(var15, var11);
               }

               int var16 = var8.indexOf(var13);
               var8.remove(var16);
            }
         }

         this.initializeMissingRoles(var1, var2, var3, var4, var5, var8);
         synchronized(this.myRelId2ObjMap) {
            if (var1) {
               this.myRelId2ObjMap.put(var4, var2);
            } else {
               this.myRelId2ObjMap.put(var4, var3);
            }
         }

         synchronized(this.myRelId2RelTypeMap) {
            this.myRelId2RelTypeMap.put(var4, var5);
         }

         synchronized(this.myRelType2RelIdsMap) {
            Object var31 = (List)this.myRelType2RelIdsMap.get(var5);
            boolean var32 = false;
            if (var31 == null) {
               var32 = true;
               var31 = new ArrayList();
            }

            ((List)var31).add(var4);
            if (var32) {
               this.myRelType2RelIdsMap.put(var5, var31);
            }
         }

         var9 = var6.asList().iterator();

         while(var9.hasNext()) {
            var10 = (Role)var9.next();
            ArrayList var33 = new ArrayList();

            try {
               this.updateRoleMap(var4, var10, var33);
            } catch (RelationNotFoundException var21) {
            }
         }

         try {
            this.sendRelationCreationNotification(var4);
         } catch (RelationNotFoundException var20) {
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationInt");
      } else {
         String var7 = "Invalid parameter.";
         throw new IllegalArgumentException(var7);
      }
   }

   private Integer checkRoleInt(int var1, String var2, List<ObjectName> var3, RoleInfo var4, boolean var5) throws IllegalArgumentException {
      String var6;
      if (var2 != null && var4 != null && (var1 != 2 || var3 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleInt", new Object[]{var1, var2, var3, var4, var5});
         var6 = var4.getName();
         if (!var2.equals(var6)) {
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
            return 1;
         } else {
            boolean var7;
            if (var1 == 1) {
               var7 = var4.isReadable();
               if (!var7) {
                  JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                  return 2;
               } else {
                  JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                  return new Integer(0);
               }
            } else {
               if (var5) {
                  var7 = var4.isWritable();
                  if (!var7) {
                     JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                     return new Integer(3);
                  }
               }

               int var15 = var3.size();
               boolean var8 = var4.checkMinDegree(var15);
               if (!var8) {
                  JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                  return new Integer(4);
               } else {
                  boolean var9 = var4.checkMaxDegree(var15);
                  if (!var9) {
                     JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                     return new Integer(5);
                  } else {
                     String var10 = var4.getRefMBeanClassName();
                     Iterator var11 = var3.iterator();

                     while(var11.hasNext()) {
                        ObjectName var12 = (ObjectName)var11.next();
                        if (var12 == null) {
                           JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                           return new Integer(7);
                        }

                        try {
                           boolean var13 = this.myMBeanServer.isInstanceOf(var12, var10);
                           if (!var13) {
                              JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                              return new Integer(6);
                           }
                        } catch (InstanceNotFoundException var14) {
                           JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                           return new Integer(7);
                        }
                     }

                     JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                     return new Integer(0);
                  }
               }
            }
         }
      } else {
         var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      }
   }

   private void initializeMissingRoles(boolean var1, RelationSupport var2, ObjectName var3, String var4, String var5, List<RoleInfo> var6) throws IllegalArgumentException, RelationServiceNotRegisteredException, InvalidRoleValueException {
      if ((!var1 || var2 != null && var3 == null) && (var1 || var3 != null && var2 == null) && var4 != null && var5 != null && var6 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "initializeMissingRoles", new Object[]{var1, var2, var3, var4, var5, var6});
         this.isActive();
         Iterator var24 = var6.iterator();

         while(var24.hasNext()) {
            RoleInfo var8 = (RoleInfo)var24.next();
            String var9 = var8.getName();
            ArrayList var10 = new ArrayList();
            Role var11 = new Role(var9, var10);
            if (var1) {
               try {
                  var2.setRoleInt(var11, true, this, false);
               } catch (RoleNotFoundException var16) {
                  throw new RuntimeException(var16.getMessage());
               } catch (RelationNotFoundException var17) {
                  throw new RuntimeException(var17.getMessage());
               } catch (RelationTypeNotFoundException var18) {
                  throw new RuntimeException(var18.getMessage());
               }
            } else {
               Object[] var12 = new Object[]{var11};
               String[] var13 = new String[]{"javax.management.relation.Role"};

               try {
                  this.myMBeanServer.setAttribute(var3, new Attribute("Role", var11));
               } catch (InstanceNotFoundException var19) {
                  throw new RuntimeException(var19.getMessage());
               } catch (ReflectionException var20) {
                  throw new RuntimeException(var20.getMessage());
               } catch (MBeanException var21) {
                  Exception var15 = var21.getTargetException();
                  if (var15 instanceof InvalidRoleValueException) {
                     throw (InvalidRoleValueException)var15;
                  }

                  throw new RuntimeException(var15.getMessage());
               } catch (AttributeNotFoundException var22) {
                  throw new RuntimeException(var22.getMessage());
               } catch (InvalidAttributeValueException var23) {
                  throw new RuntimeException(var23.getMessage());
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "initializeMissingRoles");
      } else {
         String var7 = "Invalid parameter.";
         throw new IllegalArgumentException(var7);
      }
   }

   static void throwRoleProblemException(int var0, String var1) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException {
      if (var1 == null) {
         String var6 = "Invalid parameter.";
         throw new IllegalArgumentException(var6);
      } else {
         byte var2 = 0;
         String var3 = null;
         switch(var0) {
         case 1:
            var3 = " does not exist in relation.";
            var2 = 1;
            break;
         case 2:
            var3 = " is not readable.";
            var2 = 1;
            break;
         case 3:
            var3 = " is not writable.";
            var2 = 1;
            break;
         case 4:
            var3 = " has a number of MBean references less than the expected minimum degree.";
            var2 = 2;
            break;
         case 5:
            var3 = " has a number of MBean references greater than the expected maximum degree.";
            var2 = 2;
            break;
         case 6:
            var3 = " has an MBean reference to an MBean not of the expected class of references for that role.";
            var2 = 2;
            break;
         case 7:
            var3 = " has a reference to null or to an MBean not registered.";
            var2 = 2;
         }

         StringBuilder var4 = new StringBuilder(var1);
         var4.append(var3);
         String var5 = var4.toString();
         if (var2 == 1) {
            throw new RoleNotFoundException(var5);
         } else if (var2 == 2) {
            throw new InvalidRoleValueException(var5);
         }
      }
   }

   private void sendNotificationInt(int var1, String var2, String var3, List<ObjectName> var4, String var5, List<ObjectName> var6, List<ObjectName> var7) throws IllegalArgumentException, RelationNotFoundException {
      String var8;
      if (var2 != null && var3 != null && (var1 == 3 || var4 == null) && (var1 != 2 || var5 != null && var6 != null && var7 != null)) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendNotificationInt", new Object[]{var1, var2, var3, var4, var5, var6, var7});
         synchronized(this.myRelId2RelTypeMap) {
            var8 = (String)this.myRelId2RelTypeMap.get(var3);
         }

         ObjectName var9 = this.isRelationMBean(var3);
         String var10 = null;
         if (var9 != null) {
            switch(var1) {
            case 1:
               var10 = "jmx.relation.creation.mbean";
               break;
            case 2:
               var10 = "jmx.relation.update.mbean";
               break;
            case 3:
               var10 = "jmx.relation.removal.mbean";
            }
         } else {
            switch(var1) {
            case 1:
               var10 = "jmx.relation.creation.basic";
               break;
            case 2:
               var10 = "jmx.relation.update.basic";
               break;
            case 3:
               var10 = "jmx.relation.removal.basic";
            }
         }

         Long var11 = this.atomicSeqNo.incrementAndGet();
         Date var12 = new Date();
         long var13 = var12.getTime();
         RelationNotification var15 = null;
         if (!var10.equals("jmx.relation.creation.basic") && !var10.equals("jmx.relation.creation.mbean") && !var10.equals("jmx.relation.removal.basic") && !var10.equals("jmx.relation.removal.mbean")) {
            if (var10.equals("jmx.relation.update.basic") || var10.equals("jmx.relation.update.mbean")) {
               var15 = new RelationNotification(var10, this, var11, var13, var2, var3, var8, var9, var5, var6, var7);
            }
         } else {
            var15 = new RelationNotification(var10, this, var11, var13, var2, var3, var8, var9, var4);
         }

         this.sendNotification(var15);
         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendNotificationInt");
      } else {
         var8 = "Invalid parameter.";
         throw new IllegalArgumentException(var8);
      }
   }

   private void handleReferenceUnregistration(String var1, ObjectName var2, List<String> var3) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException, RoleNotFoundException {
      String var4;
      if (var1 != null && var3 != null && var2 != null) {
         JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleReferenceUnregistration", new Object[]{var1, var2, var3});
         this.isActive();
         var4 = this.getRelationTypeName(var1);
         Object var5 = this.getRelation(var1);
         boolean var6 = false;
         Iterator var7 = var3.iterator();

         String var8;
         while(var7.hasNext()) {
            var8 = (String)var7.next();
            if (var6) {
               break;
            }

            int var9 = this.getRoleCardinality(var1, var8);
            int var10 = var9 - 1;

            RoleInfo var11;
            try {
               var11 = this.getRoleInfo(var4, var8);
            } catch (RelationTypeNotFoundException var18) {
               throw new RuntimeException(var18.getMessage());
            } catch (RoleInfoNotFoundException var19) {
               throw new RuntimeException(var19.getMessage());
            }

            boolean var12 = var11.checkMinDegree(var10);
            if (!var12) {
               var6 = true;
            }
         }

         if (var6) {
            this.removeRelation(var1);
         } else {
            var7 = var3.iterator();

            while(var7.hasNext()) {
               var8 = (String)var7.next();
               if (var5 instanceof RelationSupport) {
                  try {
                     ((RelationSupport)var5).handleMBeanUnregistrationInt(var2, var8, true, this);
                  } catch (RelationTypeNotFoundException var16) {
                     throw new RuntimeException(var16.getMessage());
                  } catch (InvalidRoleValueException var17) {
                     throw new RuntimeException(var17.getMessage());
                  }
               } else {
                  Object[] var20 = new Object[]{var2, var8};
                  String[] var21 = new String[]{"javax.management.ObjectName", "java.lang.String"};

                  try {
                     this.myMBeanServer.invoke((ObjectName)var5, "handleMBeanUnregistration", var20, var21);
                  } catch (InstanceNotFoundException var13) {
                     throw new RuntimeException(var13.getMessage());
                  } catch (ReflectionException var14) {
                     throw new RuntimeException(var14.getMessage());
                  } catch (MBeanException var15) {
                     Exception var22 = var15.getTargetException();
                     throw new RuntimeException(var22.getMessage());
                  }
               }
            }
         }

         JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleReferenceUnregistration");
      } else {
         var4 = "Invalid parameter.";
         throw new IllegalArgumentException(var4);
      }
   }
}
