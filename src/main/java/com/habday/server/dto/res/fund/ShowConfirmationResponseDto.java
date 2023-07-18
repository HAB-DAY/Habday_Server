package com.habday.server.dto.res.fund;

import com.habday.server.domain.confirmation.Confirmation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ShowConfirmationResponseDto {
    private String title;
    private String confirmationImg;
    private String message;
    private LocalDateTime createdDate;
    public ShowConfirmationResponseDto(Confirmation confirmation){
        this.title = confirmation.getTitle();
        this.confirmationImg = confirmation.getConfirmationImg();
        this.message = confirmation.getMessage();
        this.createdDate = confirmation.getCreatedDate();
    }
}
