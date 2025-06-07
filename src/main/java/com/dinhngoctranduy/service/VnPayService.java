package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.dinhngoctranduy.util.HMACUtil.hmacSHA512;

@Service
public class VnPayService {
    @Value("${vnpay.tmnCode}")
    private String tmnCode;
    @Value("${vnpay.hashSecret}")
    private String hashSecret;
    @Value("${vnpay.payUrl}")
    private String payUrl;
    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    public String createPaymentUrl(Booking booking, String baseUrl) {
        String identify = booking.getId().toString();  // duy nhất
        Map<String, String> vnpParams = new HashMap<>();
        // vnp_RequestId không bắt buộc, bạn có thể bỏ
        // vnpParams.put("vnp_RequestId", identify);
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", String.valueOf((long) (booking.getTotalPrice() * 100)));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", identify);
        vnpParams.put("vnp_OrderInfo", "Thanh toan tour: " + booking.getTour().getTitle());
        vnpParams.put("vnp_OrderType", "order-type");

        String locate = "vn";
        vnpParams.put("vnp_Locale", locate);
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");  // thử đổi IP này xem
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + secureHash;
        String paymentUrl = payUrl + "?" + queryUrl;
        return paymentUrl;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
