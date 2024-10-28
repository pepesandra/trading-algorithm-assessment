package codingblackfemales.gettingstarted.mylogic;
import messages.order.Side;

class Position {
    private final Side side;
    private final long price;
    private final long quantity;

    public Position(Side side, long price, long quantity) {
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Position{" +
                "side=" + side +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}