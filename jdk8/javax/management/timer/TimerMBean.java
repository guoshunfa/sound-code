package javax.management.timer;

import java.util.Date;
import java.util.Vector;
import javax.management.InstanceNotFoundException;

public interface TimerMBean {
   void start();

   void stop();

   Integer addNotification(String var1, String var2, Object var3, Date var4, long var5, long var7, boolean var9) throws IllegalArgumentException;

   Integer addNotification(String var1, String var2, Object var3, Date var4, long var5, long var7) throws IllegalArgumentException;

   Integer addNotification(String var1, String var2, Object var3, Date var4, long var5) throws IllegalArgumentException;

   Integer addNotification(String var1, String var2, Object var3, Date var4) throws IllegalArgumentException;

   void removeNotification(Integer var1) throws InstanceNotFoundException;

   void removeNotifications(String var1) throws InstanceNotFoundException;

   void removeAllNotifications();

   int getNbNotifications();

   Vector<Integer> getAllNotificationIDs();

   Vector<Integer> getNotificationIDs(String var1);

   String getNotificationType(Integer var1);

   String getNotificationMessage(Integer var1);

   Object getNotificationUserData(Integer var1);

   Date getDate(Integer var1);

   Long getPeriod(Integer var1);

   Long getNbOccurences(Integer var1);

   Boolean getFixedRate(Integer var1);

   boolean getSendPastNotifications();

   void setSendPastNotifications(boolean var1);

   boolean isActive();

   boolean isEmpty();
}
