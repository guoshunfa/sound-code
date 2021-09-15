package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public interface POAPolicyMediator {
   Policies getPolicies();

   int getScid();

   int getServerId();

   Object getInvocationServant(byte[] var1, String var2) throws ForwardRequest;

   void returnServant();

   void etherealizeAll();

   void clearAOM();

   ServantManager getServantManager() throws WrongPolicy;

   void setServantManager(ServantManager var1) throws WrongPolicy;

   Servant getDefaultServant() throws NoServant, WrongPolicy;

   void setDefaultServant(Servant var1) throws WrongPolicy;

   void activateObject(byte[] var1, Servant var2) throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy;

   Servant deactivateObject(byte[] var1) throws ObjectNotActive, WrongPolicy;

   byte[] newSystemId() throws WrongPolicy;

   byte[] servantToId(Servant var1) throws ServantNotActive, WrongPolicy;

   Servant idToServant(byte[] var1) throws ObjectNotActive, WrongPolicy;
}
