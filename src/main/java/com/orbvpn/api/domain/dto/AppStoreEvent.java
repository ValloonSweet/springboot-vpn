package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AppStoreEvent {
  private String environment;
  private String auto_renew_status;
  private String password;
  private String auto_renew_product_id;
  private String notification_type;
  private String latest_receipt;

  private LatestReceiptInfo latest_receipt_info;

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class  LatestReceiptInfo {
    private String original_purchase_date_pst;
    private String quantity;
    private String unique_vendor_identifier;
    private String original_purchase_date_ms;
    private String expires_date_formatted;
    private String is_in_intro_offer_period;
    private String purchase_date_ms;
    private String expires_date_formatted_pst;
    private String is_trial_period;
    private String item_id;
    private String unique_identifier;
    private String original_transaction_id;
    private String expires_date;
    private String app_item_id;
    private String transaction_id;
    private String bvrs;
    private String web_order_line_item_id;
    private String version_external_identifier;
    private String bid;
    private String product_id;
    private String purchase_date;
    private String purchase_date_pst;
    private String original_purchase_date;
  }
}


