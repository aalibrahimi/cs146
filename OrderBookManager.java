import java.util.*;

public class OrderBookManager {

    public static class Order {
        String id;
        double price;
        int quantity;
        boolean isBuyOrder;

        public Order(String id, double price, int quantity, boolean isBuyOrder) {
            // Sanity checks up front so we never end up with garbage in the book.
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Order ID cannot be null or empty.");
            }
            if (price <= 0) {
                throw new IllegalArgumentException("Order price must be greater than zero.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Order quantity must be greater than zero.");
            }

            this.id = id;
            this.price = price;
            this.quantity = quantity;
            this.isBuyOrder = isBuyOrder;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s %d @ %.2f", id, isBuyOrder ? "BUY" : "SELL", quantity, price);
        }
    }

    private PriorityQueue<Order> buyOrders;  // max-heap, highest bid sits on top
    private PriorityQueue<Order> sellOrders; // min-heap, cheapest ask sits on top
    private Map<String, Order> orderMap;     // id -> order so cancels dont have to scan the heaps

    public OrderBookManager() {
        // Buys: we want the biggest price first, so flip the comparator.
        buyOrders = new PriorityQueue<>((a, b) -> Double.compare(b.price, a.price));
        // Sells: smallest price first, which is just the natural order.
        sellOrders = new PriorityQueue<>((a, b) -> Double.compare(a.price, b.price));
        orderMap = new HashMap<>();
    }

    /**
     * Drops an order into the book. It goes in the lookup map and whichever
     * heap matches its side.
     *
     * Throws if the order is null or if we already have one with that id,
     * since duplicate ids would make cancel/lookup a mess.
     */
    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        if (orderMap.containsKey(order.id)) {
            throw new IllegalArgumentException("An order with ID '" + order.id + "' already exists.");
        }

        orderMap.put(order.id, order);
        heapFor(order).add(order);
    }

    /**
     * Pulls an order out of the book by id.
     *
     * Throws if the id is blank or we cant find it. Heads up: PriorityQueue.remove
     * is O(n), fine for now but worth revisiting if cancels get heavy.
     */
    public void cancelOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        Order order = orderMap.remove(orderId);
        if (order == null) {
            throw new IllegalArgumentException("No order found with ID '" + orderId + "'.");
        }
        heapFor(order).remove(order);
    }

    /**
     * Runs the matching loop. Keeps trading the best buy against the best sell
     * as long as the buy price is at or above the sell price.
     *
     * Partial fills are fine. If one side has more quantity than the other,
     * whatever is left stays in the book for the next round.
     *
     * Ties at the same price get matched in whatever order the heap hands them
     * out, we dont care about time priority here.
     *
     * Returns how many trades actually went through (0 if nothing crossed).
     */
    public int processTrades() {
        int trades = 0;

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order bestBuy = buyOrders.peek();
            Order bestSell = sellOrders.peek();

            // No cross, no trade. Nothing deeper in the book will match either.
            if (bestBuy.price < bestSell.price) {
                break;
            }

            int tradedQty = Math.min(bestBuy.quantity, bestSell.quantity);
            bestBuy.quantity -= tradedQty;
            bestSell.quantity -= tradedQty;
            trades++;

            // The heaps only care about price, so bumping quantity in place is safe.
            // Only pop an order once its fully used up.
            if (bestBuy.quantity == 0) {
                buyOrders.poll();
                orderMap.remove(bestBuy.id);
            }
            if (bestSell.quantity == 0) {
                sellOrders.poll();
                orderMap.remove(bestSell.id);
            }
        }

        return trades;
    }

    /**
     * Grabs the k priciest orders in the book, buys and sells mixed together,
     * sorted highest price first.
     *
     * k <= 0 gives you an empty list. k bigger than the book just gives you everything.
     * Ties are arbitrary, so if the cutoff lands in a group of equal prices you
     * might get any of them.
     *
     * Just sorts a copy of everything, which is O(n log n). Good enough unless
     * the book gets huge.
     */
    public List<Order> getTopKOrders(int k) {
        List<Order> result = new ArrayList<>();
        if (k <= 0) {
            return result;
        }

        List<Order> all = new ArrayList<>(orderMap.values());
        all.sort((a, b) -> Double.compare(b.price, a.price));

        int limit = Math.min(k, all.size());
        for (int i = 0; i < limit; i++) {
            result.add(all.get(i));
        }
        return result;
    }

    // Little helper so we dont keep writing the same ternary everywhere.
    private PriorityQueue<Order> heapFor(Order order) {
        return order.isBuyOrder ? buyOrders : sellOrders;
    }
}