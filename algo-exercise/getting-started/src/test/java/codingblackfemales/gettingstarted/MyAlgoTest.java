package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.container.RunTrigger;
import codingblackfemales.gettingstarted.mylogic.MyAlgoLogic;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoStateImpl;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals ;

public class MyAlgoTest extends AbstractAlgoTest {
    private SimpleAlgoStateImpl algoState;
    private MarketDataService marketDataService;
     OrderService orderService;

    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }
@Before
public void setUp() {
        //initialize marketDataService and OrderService before each test
    marketDataService = new MarketDataService(new RunTrigger());
    orderService = new OrderService(new RunTrigger());
    // initialize the algo state using MarketDataService and orderService

    algoState = new SimpleAlgoStateImpl(marketDataService, orderService);
}

//create a sample market tick with normal speed
    private void createDataTick1() {
        // create a bid and ask level using the default constructors
        BidLevel bidLevel = new BidLevel();
        bidLevel.setPrice(95L);
        bidLevel.setQuantity(100L);

        AskLevel askLevel = new AskLevel();

        askLevel.setPrice(98L);
        askLevel.setQuantity(150L);
        // directly modify bid book and ask book arrays in MarketDataService for testing
        marketDataService.getBidLevel(0);//set at index 0
        marketDataService.getAskLevel(0);// set at index 0

    }
    // create a sample market tick with tighter speed
    private void createDataTick2() {
        BidLevel bidLevel = new BidLevel();
        bidLevel.setPrice(95L);
        bidLevel.setQuantity(100L);
        AskLevel askLevel = new AskLevel();
        askLevel.setPrice(98L);
        askLevel.setQuantity(150L);
      marketDataService.getBidLevel(0);
      marketDataService.getAskLevel(0);
    }
    //Create a sample market tick with a wider spread
     private void createDataTick3() {
       marketDataService.getBidLevel(0);
       marketDataService.getAskLevel(0);
     }

    @Test
    public void testDispatchThroughSequencer() throws Exception {

        //create a sample market data tick....
        send(createTick());

        //simple assert to check we had 3 orders create
         assertEquals(container.getState().getChildOrders().size(), 3);

    }
@Test
public void testBuyOrderPlacement(){
    // step1: Simulate the market data tick with normal speed and evaluate the algo
    createDataTick1();
    var algoLogic = createAlgoLogic();
    algoLogic.evaluate(algoState);

    // verify that a buy order was placed because ask price (95) is below the buy threshold (100)
    assertEquals(1,1, algoState.getChildOrders().size());
    assertEquals(98L, algoState.getChildOrders().getFirst().getPrice());
    assertEquals(150L, algoState.getChildOrders().getFirst().getQuantity());

}
@Test
 public void testSellOrderPlacement(){
    // step;1 Simulate the market data tick with wider spread,triggering a sell
    createDataTick3();
    var algoLogic = createAlgoLogic();

    //Assume that a position has already been bought
    algoState.getChildOrders().add(new ChildOrder(Side.SELL, 12345L, 100L,100L,1 ));

    algoLogic.evaluate(algoState);
// verify that a sell order was placed because bid price (95) exceeds that sell threshold
    assertEquals(2, algoState.getChildOrders().size());
    assertEquals(95L, algoState.getChildOrders().get(1).getPrice());
    assertEquals(105L, algoState.getChildOrders().get(1).getPrice());
}
@Test
public void testThresholdAdjustmentForSpread() {
    //step 1: Simulate teh market data tick for wider spread
    createDataTick3();
    var algoLogic = createAlgoLogic();
    algoLogic.evaluate(algoState);
    //  Verify that the buy/sell thresholds were adjusted due to large spread
    // The spread is large, so thresholds should be adjusted in the logic (lower buy, higher sell)
    // The current MyAlgoLogic logs these threshold changes, so you can check them via logging
    assertEquals(1, algoState.getChildOrders().size()); //Expect a buy order
}
@Test
public void testBuyOrderPlacementWithTightSpread() {
    createDataTick2();
    var algoLogic = createAlgoLogic();
    algoLogic.evaluate(algoState);
    //verify that the buy was placed because the ask price is near the bid price
    assertEquals(1, algoState.getChildOrders().size());
    assertEquals(101L, algoState.getChildOrders().getFirst().getPrice());
    assertEquals(150L, algoState.getChildOrders().getFirst().getQuantity());
}
@Test
public void testCancelOrder()  {
    //step 1; Simulate market data where no new orders are placed but cancel should happen
    createDataTick1();
    var algoLogic = createAlgoLogic();
    //Assume a previous order is already in the system
    algoState.getChildOrders().add(new ChildOrder(Side.BUY, 12456L, 100L, 100L,3));
    algoLogic.evaluate(algoState);

    //verify that the order was cancelled
    assertEquals(1, algoState.getChildOrders().size()); //only one order remain after cancellation



}}







//    @Test
//    public void testAlgoLogic() throws Exception {
//
//        // Step 1: Send the simulated market data tick to the algorithm
//        send(createSampleMarketDataTick());
//
//        // Step 2: Assert the algorithm placed child orders based on the market data
//        // In this case, we expect the algorithm to place an order, depending on the logic
//        assertEquals(1, container.getState().getChildOrders().size());
//
//        // Add additional assertions based on what the algo should do
//        // For example, verifying if it placed a buy or sell order depending on market conditions
//    }

