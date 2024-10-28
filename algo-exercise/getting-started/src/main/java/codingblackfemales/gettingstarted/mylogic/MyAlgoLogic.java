package codingblackfemales.gettingstarted.mylogic;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    //adding some extra fields
    // max orders allowed in the order book
    private static final long max_orders = 10;
    //  to buy when the ask price is less than this
    private static final long openingBuyThreshold = 100;
    // to sell when the bid price is more than this
    private static final long openingSellThreshold = 100;
    static long buy_threshold = openingBuyThreshold;
     static long sell_threshold = openingSellThreshold;
    private static final long max_position = 500;
    private static long currentPosition = 0;



    @Override
    public Action evaluate(SimpleAlgoState state) {

        var orderBookAsString = Util.orderBookToString(state);

        logger.info("[MYALGO] The state of the order book is:\n{}", orderBookAsString);
        var totalOrderCount = state.getChildOrders().size();
//check the total number of active child orders

        //check if we should stop placing more orders to avoid exceeding limit

        if (totalOrderCount >= max_orders) {
            logger.info("[MYALGO] Maximum orders reached.");
            return new NoAction();
        }

//get the best bid and ask price from the order book
        BidLevel bestBid = state.getBidAt(0); // best bid at the highest level
        AskLevel bestAsk = state.getAskAt(0); // best ask at the lowest level

        if (bestBid != null && bestAsk != null) {
            long bidPrice = bestBid.price;
            long askPrice = bestAsk.price;

            logger.info("[MYALGO] The best bid price is: {}, best ask price is : {}", bidPrice, askPrice);

// calculate the spread between bid and ask prices

            long spread = askPrice - bidPrice;
// adjust the buy/sell threshold based on the spread
            if (spread <= 5) { // tight spread,adjust threshold more aggressively

                //   aggressive buy and sell when there is a large spread
                buy_threshold = openingBuyThreshold - 5;
                sell_threshold = openingSellThreshold + 5;
                logger.info("[MYALGO] Tight spread, adjusting thresholds: buy_threshold={}, sell_threshold={}", buy_threshold, sell_threshold);

            } else {
                //small spread revert back to the original
                buy_threshold = openingBuyThreshold;
                sell_threshold = openingSellThreshold;

            logger.info("[MYALGO] Adjusted buy threshold :{}Adjusted sell threshold{}", buy_threshold, sell_threshold);
        }

        //setting a buy order when the ask price is less than the buy threshold and position is within the limit
        if (askPrice < buy_threshold && currentPosition + bestAsk.getQuantity() <= max_position) {
            logger.info("[MYALGO] place a buy order for quantity at {}at price: {}", bestAsk.getQuantity(), askPrice);
            currentPosition += bestAsk.getQuantity();
            return new CreateChildOrder(Side.BUY, bestAsk.getQuantity(), askPrice);
        }
        // setting a sell order when the bid price is more than the sell threshold
        if (bidPrice > sell_threshold && currentPosition > 0) {
            logger.info("[MYALGO] place a sell order for quantity at {}at price{}", bestBid.getQuantity(), bidPrice);
            currentPosition -= bestBid.getQuantity();
            return new CreateChildOrder(Side.SELL, bestBid.getQuantity(), bestBid.price);
        }
    }

    // cancelling an existing order if conditions are met
        var activeOrders = state.getChildOrders();
            if(activeOrders.size()>0){
        var orderToCancel = activeOrders.stream().findFirst().get();
        logger.info("[MYALGO] cancel the order: {}", orderToCancel);
        return new CancelChildOrder(orderToCancel);


    }

            // if no condition is met

            return NoAction.NoAction;
}
        }

