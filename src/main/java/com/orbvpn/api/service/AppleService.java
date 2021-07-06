package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.AppStoreVerifyReceiptRequest;
import com.orbvpn.api.domain.dto.AppStoreVerifyReceiptResponse;
import com.orbvpn.api.domain.dto.AppStoreVerifyReceiptResponse.LatestReceiptInfo;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AppleService {

  private final Map<String, Integer> groupMap = Map.of(
    "com.orb.monthly.basic", 9,
    "com.orb.monthly.premium", 10,
    "com.orb.monthly.familypremium", 11,
    "com.orb.yearly.basic", 12,
    "com.orb.yearly.premium", 13,
    "com.orb.yearly.familypremium", 14);
  @Value("${app-store.url}")
  private String APP_STORE_URL;
  @Value("${app-store.secret}")
  private String SECRET;

  public int getGroupId(String receipt) {
    AppStoreVerifyReceiptRequest verifyReceiptRequest = new AppStoreVerifyReceiptRequest();
    verifyReceiptRequest.setReceiptData(receipt);
    verifyReceiptRequest.setPassword(SECRET);

    RestTemplate restTemplate = new RestTemplate();

    AppStoreVerifyReceiptResponse response = restTemplate
      .postForObject(APP_STORE_URL + "/verifyReceipt", verifyReceiptRequest,
        AppStoreVerifyReceiptResponse.class);

    LatestReceiptInfo latestReceiptInfo = response.getLatestReceiptInfo().get(0);
    String productSku = latestReceiptInfo.getProductId();

    return groupMap.get(productSku);

  }
}
