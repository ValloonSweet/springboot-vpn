package com.orbvpn.api.controller;

import com.orbvpn.api.service.payment.PaymentService;
import com.orbvpn.api.service.payment.coinpayment.CoinPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@RestController
@RequestMapping("/coinpayment/callback")
public class CoinPaymentController {

    private final CoinPaymentService coinPaymentService;
    private final PaymentService paymentService;


    @Autowired
    public CoinPaymentController(CoinPaymentService coinPaymentService,
                                 PaymentService paymentService) {
        this.coinPaymentService = coinPaymentService;
        this.paymentService = paymentService;
    }


    @PostMapping("/success")
    @ResponseBody
    public ResponseEntity<String> success(HttpServletRequest request) {
        log.info("CoinPayment success");
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            log.info("{} = {}", param, request.getParameter(param));

        }
        String hmac = getString(request, "hmac", true);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    public int getInt(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public String getString(HttpServletRequest request, String param, Boolean trim) {
        try {
            String result = request.getParameter(param);
            if(trim) {
                result = result.trim();
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public double getDouble(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
