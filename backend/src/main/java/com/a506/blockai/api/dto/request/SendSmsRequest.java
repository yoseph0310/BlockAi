package com.a506.blockai.api.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendSmsRequest {
    private String phone;
    private String randomCode;
}