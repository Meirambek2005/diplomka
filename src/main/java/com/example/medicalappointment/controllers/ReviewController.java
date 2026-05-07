package com.example.medicalappointment.controllers;

import com.example.medicalappointment.dto.ReviewDto;
import com.example.medicalappointment.services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // Явный конструктор вместо @RequiredArgsConstructor
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<ReviewDto> leaveReview(
            @PathVariable Long doctorId,
            @RequestBody ReviewDto request,
            Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        ReviewDto response = reviewService.createReview(
                doctorId,
                auth.getName(),
                request.getRating(),
                request.getComment()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorReviews(doctorId));
    }
}