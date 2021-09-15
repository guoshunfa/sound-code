package com.sun.corba.se.impl.orbutil;

import java.net.MalformedURLException;

public interface RepositoryIdInterface {
   Class getClassFromType() throws ClassNotFoundException;

   Class getClassFromType(String var1) throws ClassNotFoundException, MalformedURLException;

   Class getClassFromType(Class var1, String var2) throws ClassNotFoundException, MalformedURLException;

   String getClassName();
}
