package edu.miu.horelo.dto.response;

import java.time.LocalDateTime;

public record StatusResponse(
        Long statusId,
        LocalDateTime received,
        LocalDateTime seen,
        LocalDateTime confirmed,
        LocalDateTime queued,
        LocalDateTime processing,
        LocalDateTime packed,
        LocalDateTime readyForPickup
) {
}
