package com.orbvpn.api.service.payment;

import com.orbvpn.api.config.PayPalClient;
import com.orbvpn.api.domain.dto.PaypalApprovePaymentResponse;
import com.orbvpn.api.domain.dto.PaypalCreatePaymentResponse;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.exception.PaymentException;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaypalService {

  private final PayPalClient paypalClient;

  public PaypalCreatePaymentResponse createPayment(Payment payment) throws IOException {

    OrdersCreateRequest request = new OrdersCreateRequest();
    request.prefer("return=representation");
    request.requestBody(buildRequestBody(payment.getPrice().toString()));
    //3. Call PayPal to set up a transaction
    HttpResponse<Order> response = paypalClient.client().execute(request);
    PaypalCreatePaymentResponse paypalResponse = new PaypalCreatePaymentResponse();
    if (response.statusCode() == 201) {
      paypalResponse.setOrderId(response.result().id());
    } else {
      throw new PaymentException("Can not create paypal order");
    }

    return paypalResponse;
  }

  public PaypalApprovePaymentResponse approvePayment(String orderId) {
    OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
    request.requestBody(new OrderRequest());
    PaypalApprovePaymentResponse response = new PaypalApprovePaymentResponse();
    //3. Call PayPal to capture an order
    try {
      HttpResponse<Order> paypalResponse = paypalClient.client().execute(request);
      response.setSuccess(paypalResponse.statusCode() == HttpStatus.SC_OK);
    } catch (Exception ex) {
      log.error(ex.getMessage());
      response.setSuccess(false);
      response.setErrorMessage(ex.getMessage());
    }
    //4. Save the capture ID to your database. Implement logic to save capture to your database for future reference.
    return response;
  }

  private OrderRequest buildRequestBody(String amount) {
    OrderRequest orderRequest = new OrderRequest();
    orderRequest.checkoutPaymentIntent("CAPTURE");

    List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
    PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
      .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(amount));
    purchaseUnitRequests.add(purchaseUnitRequest);
    orderRequest.purchaseUnits(purchaseUnitRequests);
    return orderRequest;
  }
}
