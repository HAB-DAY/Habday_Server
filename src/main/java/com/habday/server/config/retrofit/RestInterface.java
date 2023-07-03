package com.habday.server.config.retrofit;

import com.habday.server.dto.req.iamport.NoneAuthPayUnscheduleRequestDto;
import com.habday.server.dto.res.iamport.UnscheduleResponseDto;
import com.siot.IamportRestClient.request.BillingCustomerData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RestInterface {
    @POST("/verifyIamport/noneauthpay/unschedule")
    Call<UnscheduleResponseDto> unscheduleApi(
            @Body NoneAuthPayUnscheduleRequestDto requestDto
    );
}
