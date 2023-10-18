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
public class QuizParticipationRequest {
    //bolje student id dohvati iz principala
    //private Long student_id;
    private Long quiz_id;
    private LocalDateTime accessTime;
}
