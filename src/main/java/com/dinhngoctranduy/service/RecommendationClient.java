package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.request.TourSearchRequest;
import com.dinhngoctranduy.model.response.SimilarTourResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class RecommendationClient {
    private final RestTemplate restTemplate;

    public RecommendationClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<Long> getSimilarToursFromFilters(TourSearchRequest request) {
        try {
            String url = "http://localhost:5000/api/similar-from-filters";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TourSearchRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<SimilarTourResponse> response = restTemplate.postForEntity(url, entity, SimilarTourResponse.class);

            return response.getBody() != null ? response.getBody().getTourIds() : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

