package es.codeurjc.ferrumgym.dto;

import java.time.LocalDateTime;

public record BookingDTO(
    Long id,
    LocalDateTime bookingDate,
    Long userId,
    String userName,
    Long activityId,
    String activityName
) {}