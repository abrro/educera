package com.broc.educera.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

    private Long quiz_id;
    private LocalDateTime startTime;
    private LocalDateTime accessibleUntil;
    private Integer duration;
}
