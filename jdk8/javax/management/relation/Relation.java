package javax.management.relation;

import java.util.List;
import java.util.Map;
import javax.management.ObjectName;

public interface Relation {
   List<ObjectName> getRole(String var1) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException;

   RoleResult getRoles(String[] var1) throws IllegalArgumentException, RelationServiceNotRegisteredException;

   Integer getRoleCardinality(String var1) throws IllegalArgumentException, RoleNotFoundException;

   RoleResult getAllRoles() throws RelationServiceNotRegisteredException;

   RoleList retrieveAllRoles();

   void setRole(Role var1) throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException;

   RoleResult setRoles(RoleList var1) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;

   void handleMBeanUnregistration(ObjectName var1, String var2) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;

   Map<ObjectName, List<String>> getReferencedMBeans();

   String getRelationTypeName();

   ObjectName getRelationServiceName();

   String getRelationId();
}
