import java.util.*;

public class SocialMediaBackend {
    ArrayList<String> friendList;
    LinkedList<String> messageThread;
    // last in, first out  ( first thing goes to the bottom, and when consuming it reads from the top first)
    Stack<String> actionHistory;
    Queue<String> notificationQueue;


    public SocialMediaBackend() {
        friendList = new ArrayList<>();
        messageThread = new LinkedList<>();
        actionHistory = new Stack<>();
        notificationQueue = new LinkedList<>();
        // --- IMPLEMENT TASK 1 (ArrayList) --- Add 5 friends to the friendlist. 2) Then print out the third friend. 3) Remove a friend.
        System.out.println("TASK 1");
        Collections.addAll(friendList, "john", "henry", "lisa", "sophia", "bob");
        // 2) Print out the third friend ( we just have to use a get method)
        System.out.println("Third Friend: " + friendList.get(2));
        // 3) Remove a friend
        System.out.println("Removing friend: " + friendList.remove("bob"));

        // =============== FINAL OUTPUT =========================== //
        System.out.println("List of Friends:" + friendList + "\n\n");

       
        // --- IMPLEMENT TASK 2 (LinkedList) ---
        System.out.println("TASK 2");
        messageThread.add("Hey how are you doing?");
        messageThread.add("How this school year been for you so far?");
        messageThread.add("Do you work on alongside your school work?");
        
        messageThread.addFirst("IMPORTANTE!!");
        for ( String message: messageThread ) {
            System.out.println(message );
        }
        System.out.println();

        // --- IMPLEMENT TASK 3 (Stack) ---
        System.out.println("TASK 3");
        // task is to 1) push 3 actions to action history ( "updated bio", "change profile picture", " deleted post"). 
        // then we pop the most recent action and print "undoing action" { action name }
        actionHistory.push("updated bio");
        actionHistory.push("deleted post");
        actionHistory.push("undoing action");

        actionHistory.pop();

        System.out.println(actionHistory + "\n");

      
        // --- IMPLEMENT TASK 4 (Queue) ---
        System.out.println("TASK 4");
        // offer 4 notificiation to the notification que. And those notifs are "new like" "new comment" "friend request" "retweet"
        // after poll the first two notifs and print them. after that, peek the next notif in line and printing it without removal of it

        notificationQueue.offer("new like");
        notificationQueue.offer("new comment");
        notificationQueue.offer("friend request");
        notificationQueue.offer("retweet");
         
        System.out.println(notificationQueue.poll());
        System.out.println(notificationQueue.poll());

        System.out.println(notificationQueue.peek());



    }

    public static void main( String[] args) {
        new SocialMediaBackend();
    }

}
