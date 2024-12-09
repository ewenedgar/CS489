package edu.miu.horelo.dto.request;

import java.time.LocalDateTime;

public record StatusRequest(
        //Long statusId,
        LocalDateTime received,
        LocalDateTime seen,
        LocalDateTime confirmed,
        LocalDateTime queued,
        LocalDateTime processing,
        LocalDateTime packed,
        LocalDateTime readyForPickup

) {
}
