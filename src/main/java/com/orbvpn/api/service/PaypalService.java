package com.orbvpn.api.service;

import com.orbvpn.api.config.PayPalClient;
import com.orbvpn.api.domain.dto.PaypalCreateOrderResponse;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaypalService {

  private final PayPalClient paypalClient;

  public PaypalCreateOrderResponse createOrder(int groupId) throws IOException {

    OrdersCreateRequest request = new OrdersCreateRequest();
    request.prefer("return=representation");
    request.requestBody(buildRequestBody("7.99", ""));
    //3. Call PayPal to set up a transaction
    HttpResponse<Order> response = paypalClient.client().execute(request);
    PaypalCreateOrderResponse paypalResponse = new PaypalCreateOrderResponse();
    if (response.statusCode() == 201) {
      paypalResponse.setOrderId(response.result().id());
    }

    return paypalResponse;
  }

  private OrderRequest buildRequestBody(String amount, String capture) {
    OrderRequest orderRequest = new OrderRequest();
    orderRequest.checkoutPaymentIntent(capture);

//    ApplicationContext applicationContext = new ApplicationContext().brandName(brandName).landingPage(landingPage);
//    orderRequest.applicationContext(applicationContext);
//
//    List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<PurchaseUnitRequest>();
//    PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().referenceId(referenceId)
//      .description(description).customId(customId).softDescriptor(descriptor)
//      .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(amount));
//    purchaseUnitRequests.add(purchaseUnitRequest);
//    orderRequest.purchaseUnits(purchaseUnitRequests);
    return orderRequest;
  }
}
