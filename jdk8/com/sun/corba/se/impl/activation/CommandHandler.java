package com.sun.corba.se.impl.activation;

import java.io.PrintStream;
import org.omg.CORBA.ORB;

public interface CommandHandler {
   boolean shortHelp = true;
   boolean longHelp = false;
   boolean parseError = true;
   boolean commandDone = false;

   String getCommandName();

   void printCommandHelp(PrintStream var1, boolean var2);

   boolean processCommand(String[] var1, ORB var2, PrintStream var3);
}
