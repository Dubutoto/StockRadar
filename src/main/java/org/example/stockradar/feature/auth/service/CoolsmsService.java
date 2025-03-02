package org.example.stockradar.feature.auth.service;

import net.nurigo.sdk.message.model.*;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

@Service
public class CoolsmsService {

    private final DefaultMessageService messageService;

    public CoolsmsService() {
        // 실제 API Key / Secret 넣기
        String apiKey = "NCSF3I28IOPZI0RD";
        String apiSecret = "AD13YBB0TLTDWIZYV4WNTYEHVJ2CHUOV";
        this.messageService =  new DefaultMessageService(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendSms(String to, String text) {
        Message message = new Message();
        message.setFrom("01085339569"); // 인증된 발신번호
        message.setTo(to);
        message.setText(text);

        try {
            // 단일 발송
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println("CoolSMS Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
