import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
// TODO: Your additional allowed imports here
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SocialNetworkAnalyzer {
    private class BSTNode {
        int influenceScore; // The key for the BST
        List<String> userIds;
        BSTNode left, right;

        BSTNode(int score, String userId) {
            this.influenceScore = score;
            this.userIds = new ArrayList<>();
            this.userIds.add(userId);
        }
    }

    // TODO: Your instance fields and methods here

    // the normal graph. U -> V means U follows V
    private final Map<String, List<String>> graph = new HashMap<>();
    // same graph but flipped. V -> U means U follows V, so V's list is basically V's followers
    private final Map<String, List<String>> transpose = new HashMap<>();
    // how many followers each user has (the in-degree)
    private final Map<String, Integer> influence = new HashMap<>();
    // top of the BST, the keys are the follower counts
    private BSTNode root;

    /**
     * Adds the user to both graphs and the influence map if they aren't in there yet.
     * Need this because someone like Erin follows nobody but still has to show up as a node.
     * @param userId the user we want to make sure exists
     */
    private void addUser(String userId) {
        if (!graph.containsKey(userId)) {
            graph.put(userId, new ArrayList<>());
            transpose.put(userId, new ArrayList<>());
            influence.put(userId, 0);
        }
    }

    /**
     * Regular BST insert. If the score is already in the tree we just add the user
     * to that node's list, no new node needed.
     * @param node the subtree we're inserting into (null means empty spot, so we make the node here)
     * @param score the follower count, this is the key
     * @param userId the user who has that score
     * @return the subtree's root after inserting
     */
    private BSTNode insert(BSTNode node, int score, String userId) {
        if (node == null) {
            return new BSTNode(score, userId);
        }
        if (score < node.influenceScore) {
            node.left = insert(node.left, score, userId);
        } else if (score > node.influenceScore) {
            node.right = insert(node.right, score, userId);
        } else {
            node.userIds.add(userId);
        }
        return node;
    }

    /**
     * Puts the scores from sortedScores[lo..hi] into the BST starting with the middle one.
     * If I inserted them in sorted order the tree would turn into a linked list,
     * going middle first keeps it balanced so h stays small (around log n).
     * @param sortedScores all the different scores, smallest to biggest
     * @param usersByScore score -> the users with that score
     * @param lo first index of the part we're inserting (inclusive)
     * @param hi last index of the part we're inserting (inclusive)
     */
    private void insertBalanced(List<Integer> sortedScores, Map<Integer, List<String>> usersByScore,
                                int lo, int hi) {
        if (lo > hi) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        int score = sortedScores.get(mid);
        for (String userId : usersByScore.get(score)) {
            root = insert(root, score, userId);
        }
        insertBalanced(sortedScores, usersByScore, lo, mid - 1);
        insertBalanced(sortedScores, usersByScore, mid + 1, hi);
    }

    /**
     * In-order walk for the range query, but it doesn't visit the whole tree.
     * Only go left if there could still be smaller scores in range, and only go right
     * if there could still be bigger ones. Anything outside [min, max] gets skipped,
     * which is how it stays O(h + k) instead of O(n).
     * @param node the node we're on (null when we run off the tree)
     * @param min lowest follower count we want (inclusive)
     * @param max highest follower count we want (inclusive)
     * @param result the list we add the matching users to, lowest score ends up first
     */
    private void rangeQuery(BSTNode node, int min, int max, List<String> result) {
        if (node == null) {
            return;
        }
        if (node.influenceScore > min) {
            rangeQuery(node.left, min, max, result);
        }
        if (node.influenceScore >= min && node.influenceScore <= max) {
            result.addAll(node.userIds);
        }
        if (node.influenceScore < max) {
            rangeQuery(node.right, min, max, result);
        }
    }

    /**
     * Kosaraju pass 1. DFS on the normal graph, and a node gets written down
     * only after all its neighbors are done (that's the finishing order).
     * Did it with my own stack instead of recursion so a huge network doesn't blow the call stack.
     * @param start the node this DFS starts from
     * @param visited nodes we've already seen from earlier DFS runs (gets updated)
     * @param finishOrder nodes get added here as they finish (gets updated)
     */
    private void dfsFinishOrder(String start, Set<String> visited, List<String> finishOrder) {
        Deque<String> nodeStack = new ArrayDeque<>();
        Deque<Iterator<String>> iterStack = new ArrayDeque<>();
        visited.add(start);
        nodeStack.push(start);
        iterStack.push(graph.get(start).iterator());

        while (!nodeStack.isEmpty()) {
            Iterator<String> neighbors = iterStack.peek();
            if (neighbors.hasNext()) {
                String next = neighbors.next();
                if (visited.add(next)) {
                    nodeStack.push(next);
                    iterStack.push(graph.get(next).iterator());
                }
            } else {
                // no neighbors left to check so this node is finished
                finishOrder.add(nodeStack.pop());
                iterStack.pop();
            }
        }
    }

    /**
     * Grabs everything we can get to from start in the flipped graph that we haven't seen yet.
     * Using it twice: pass 2 of Kosaraju (every call = one SCC) and for broadcast reach.
     * @param start the node we start from
     * @param visited nodes we've already seen (gets updated)
     * @return everything we just reached, start included
     */
    private Set<String> collectFromTranspose(String start, Set<String> visited) {
        Set<String> reached = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        visited.add(start);
        stack.push(start);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            reached.add(current);
            for (String follower : transpose.get(current)) {
                if (visited.add(follower)) {
                    stack.push(follower);
                }
            }
        }
        return reached;
    }

    /**
     * Constructor for the SocialNetworkAnalyzer. It takes the network data
     * and builds the internal graph representation.
     * @param followsGraph A map where the key is a user ID and the value is a list
     * of user IDs that the key user follows.
     */
    public SocialNetworkAnalyzer(Map<String, List<String>> followsGraph) {
        // TODO: Your implementation here
        if (followsGraph == null) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : followsGraph.entrySet()) {
            String follower = entry.getKey();
            if (follower == null) {
                continue;
            }
            addUser(follower);

            List<String> following = entry.getValue();
            if (following == null) {
                continue;
            }

            // if the same name shows up twice in the list it's still only one follow
            Set<String> alreadyFollowed = new HashSet<>();
            for (String followed : following) {
                if (followed == null || !alreadyFollowed.add(followed)) {
                    continue;
                }
                addUser(followed);
                graph.get(follower).add(followed);       // follower -> followed
                transpose.get(followed).add(follower);   // followed -> follower
                influence.put(followed, influence.get(followed) + 1);
            }
        }

        // group the users by their score, then build the BST middle score first so it stays balanced
        Map<Integer, List<String>> usersByScore = new HashMap<>();
        for (Map.Entry<String, Integer> entry : influence.entrySet()) {
            List<String> users = usersByScore.get(entry.getValue());
            if (users == null) {
                users = new ArrayList<>();
                usersByScore.put(entry.getValue(), users);
            }
            users.add(entry.getKey());
        }
        List<Integer> sortedScores = new ArrayList<>(usersByScore.keySet());
        Collections.sort(sortedScores);
        insertBalanced(sortedScores, usersByScore, 0, sortedScores.size() - 1);
    }

    /**
     * Finds all the cliques in the social network.
     * A clique is a group of 2 or more users who are all mutually connected.
     * @return A list of sets, where each set represents a clique of user IDs.
     */
    public List<Set<String>> findCliques() {
        // TODO: Your implementation here
        List<Set<String>> cliques = new ArrayList<>();

        // pass 1: DFS on the normal graph to get the finishing order.
        // loop over every node so the disconnected parts of the network don't get missed
        Set<String> visited = new HashSet<>();
        List<String> finishOrder = new ArrayList<>();
        for (String user : graph.keySet()) {
            if (!visited.contains(user)) {
                dfsFinishOrder(user, visited, finishOrder);
            }
        }

        // pass 2: start from whoever finished last and work backwards,
        // this time on the flipped graph. each run gives exactly one SCC
        visited = new HashSet<>();
        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            String user = finishOrder.get(i);
            if (!visited.contains(user)) {
                Set<String> component = collectFromTranspose(user, visited);
                if (component.size() >= 2) {
                    cliques.add(component);
                }
            }
        }
        return cliques;
    }

    /**
     * Calculates the "broadcast reach" of a user's post.
     * This includes all users who follow the given user, directly or indirectly.
     * @param userId The ID of the user whose post reach is being calculated.
     * @return A set of user IDs representing the total audience.
     */
    public Set<String> getBroadcastReach(String userId) {
        // TODO: Your implementation here
        // if the user isn't even in the network there's nobody to reach
        if (userId == null || !transpose.containsKey(userId)) {
            return new HashSet<>();
        }
        // posts travel backwards along the follows, so we walk the flipped graph.
        // the poster counts too since we start on them
        return collectFromTranspose(userId, new HashSet<>());
    }

    /**
     * Finds all users whose follower count is within a specified range with
     * optimal time complexity of O(h+k).
     * @param minFollowers The minimum follower count (inclusive).
     * @param maxFollowers The maximum follower count (inclusive).
     * @return A list of user IDs that fall within the influence range sorted by
     * lowest influence to highest influence. User IDs with the same influence can
     * be returned in any order.
     */
    public List<String> getUsersInInfluenceRange(int minFollowers, int maxFollowers) {
        // TODO: Your implementation here
        List<String> result = new ArrayList<>();
        if (minFollowers > maxFollowers) {
            return result;
        }
        rangeQuery(root, minFollowers, maxFollowers, result);
        return result;
    }
}
