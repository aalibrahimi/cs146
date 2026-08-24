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
// Review for Stacks, Que, Lists
/*
        1)  Abstract Data Types
            - a data type defined by its behaviour ( what it does ) - not its implementation
            - ( behaviour e.g accelerate, stopping, steering )
            - ( implementation e.g electric, gas )
            - list, stacks, ques are abstractions

        2) list
            E.G
                - Analogy: grocery list
                - Concept: a sequential collection of elements
                - Access: by index
                - Example: java.util.Arraylist
                - Operations; add, remove, get, size
                - Implementations: Array or linked list

        (for loop ? )    Lists: Array based ( need to differentiate between array and arraylist in code syntax)
                - Analogy: think of lockers, each with a number; to access locker #5,  you can open it direcly
                - Pros: Fast Random Access, O(1)
                - Cons: Slow insertions and deletions, O(n)

                ( ArrayCopy is a method used when  you want to resize an array, it copies the elements from the old array to a new one )

       ( indexing ? )   Lists: Linked List based
                - Analogy: A train where each car holds a piece of data and has a physical connection to the next car
                - Pros: Fast insertions and deletions, O(1)
                  - if you need to find the node to be deleted O(n)
                - Cons: slow sequential access, O(n)

        ===================================================================================================================

        3) Stack (LIFO, last in first out)
            - Analogy: a stack of plates
            - Concept: a collection of elements with two main operations: push and pop
                - push: Add to the top
                - pop: Remove the top
                - peek: Look at the top without removing it
                - isEmpyty: Check if the stack is empty
                - size: Get the number of elements in the stack
            - Access: only the top element
            - Example: java.util.Stack
            - Operations: push, pop, peek, isEmpty, size
            - Implementation: Array or linked list

        4) Queue (FIFO, first in first out)
            - Analogy: a line of people waiting for a bus
            - Concept: a collection of elements with two main operations: offer and poll
                - enqueue: Add to the back ( apppend a new node aft4r tail, the move tail to the new node )
                - dequeue: Remove from the front ( extract data from head, advance head = head.next if the list bcomes empty, set tail = null )
            - Access: only the front element
            - Example: java.util.Queue
            - Operations: offer, poll, peek, isEmpty, size
            - Implementation: Array or linked list

            Array-Based Queue ( Naive Implementation ) ( GOT LOST HERE - BREAK IT DOWN WITH AI LATER )
              - uses an array with a tail index tracking next 
              - enqueue: insert element at array[tail], then increment tail
              - dequeue: remove element at array[head], then increment head
*/          