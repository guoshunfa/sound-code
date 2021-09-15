package javax.management.relation;

import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;

public interface RelationServiceMBean {
   void isActive() throws RelationServiceNotRegisteredException;

   boolean getPurgeFlag();

   void setPurgeFlag(boolean var1);

   void createRelationType(String var1, RoleInfo[] var2) throws IllegalArgumentException, InvalidRelationTypeException;

   void addRelationType(RelationType var1) throws IllegalArgumentException, InvalidRelationTypeException;

   List<String> getAllRelationTypeNames();

   List<RoleInfo> getRoleInfos(String var1) throws IllegalArgumentException, RelationTypeNotFoundException;

   RoleInfo getRoleInfo(String var1, String var2) throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException;

   void removeRelationType(String var1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException;

   void createRelation(String var1, String var2, RoleList var3) throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException;

   void addRelation(ObjectName var1) throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException;

   ObjectName isRelationMBean(String var1) throws IllegalArgumentException, RelationNotFoundException;

   String isRelation(ObjectName var1) throws IllegalArgumentException;

   Boolean hasRelation(String var1) throws IllegalArgumentException;

   List<String> getAllRelationIds();

   Integer checkRoleReading(String var1, String var2) throws IllegalArgumentException, RelationTypeNotFoundException;

   Integer checkRoleWriting(Role var1, String var2, Boolean var3) throws IllegalArgumentException, RelationTypeNotFoundException;

   void sendRelationCreationNotification(String var1) throws IllegalArgumentException, RelationNotFoundException;

   void sendRoleUpdateNotification(String var1, Role var2, List<ObjectName> var3) throws IllegalArgumentException, RelationNotFoundException;

   void sendRelationRemovalNotification(String var1, List<ObjectName> var2) throws IllegalArgumentException, RelationNotFoundException;

   void updateRoleMap(String var1, Role var2, List<ObjectName> var3) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException;

   void removeRelation(String var1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;

   void purgeRelations() throws RelationServiceNotRegisteredException;

   Map<String, List<String>> findReferencingRelations(ObjectName var1, String var2, String var3) throws IllegalArgumentException;

   Map<ObjectName, List<String>> findAssociatedMBeans(ObjectName var1, String var2, String var3) throws IllegalArgumentException;

   List<String> findRelationsOfType(String var1) throws IllegalArgumentException, RelationTypeNotFoundException;

   List<ObjectName> getRole(String var1, String var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException;

   RoleResult getRoles(String var1, String[] var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;

   RoleResult getAllRoles(String var1) throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException;

   Integer getRoleCardinality(String var1, String var2) throws IllegalArgumentException, RelationNotFoundException, RoleNotFoundException;

   void setRole(String var1, Role var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException, RelationTypeNotFoundException;

   RoleResult setRoles(String var1, RoleList var2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;

   Map<ObjectName, List<String>> getReferencedMBeans(String var1) throws IllegalArgumentException, RelationNotFoundException;

   String getRelationTypeName(String var1) throws IllegalArgumentException, RelationNotFoundException;
}
