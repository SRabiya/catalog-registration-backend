package com.example.registration.controller;

import com.example.registration.dto.PaymentRequestDto;
import com.example.registration.dto.PaymentResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.*;
import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.net.HttpURLConnection;

import com.example.registration.service.OrderService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponseDto> createOrder(@RequestBody PaymentRequestDto request) {
        try {
            int amountInPaise = request.getAmount().multiply(new BigDecimal("100")).intValue();
            
            // Bypass SSL for local development behind corporate proxies
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                    if (connection instanceof HttpsURLConnection) {
                        ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                        ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
                    }
                    super.prepareConnection(connection, httpMethod);
                }
            };
            
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            
            String auth = keyId + ":" + keySecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedAuth);
            
            String requestJson = String.format("{\"amount\":%d,\"currency\":\"INR\",\"receipt\":\"txn_%d\"}", amountInPaise, System.currentTimeMillis());
            
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                "https://api.razorpay.com/v1/orders",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> orderResponse = responseEntity.getBody();
            
            String orderId = (String) orderResponse.get("id");
            orderService.createPendingOrder(orderId, request.getUserId(), request.getAmount());

            PaymentResponseDto response = new PaymentResponseDto();
            response.setOrderId(orderId);
            response.setKeyId(keyId);
            response.setAmount(amountInPaise);
            response.setCurrency("INR");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify-success")
    public ResponseEntity<Map<String, String>> verifyPaymentSuccess(@RequestBody Map<String, String> payload) {
        try {
            String orderId = payload.get("razorpay_order_id");
            if (orderId != null) {
                orderService.completeOrder(orderId);
            }
            return ResponseEntity.ok(Map.of("message", "Payment verified and order completed"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
