package com.isaacwallace.inventory_service.Presentation.Models;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookRequestModel {
    String authorid;

    String title;
    String genre;
    String publisher;

    LocalDateTime released;
}
