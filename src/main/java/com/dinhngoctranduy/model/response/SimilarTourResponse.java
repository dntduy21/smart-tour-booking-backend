package com.dinhngoctranduy.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SimilarTourResponse {

    @JsonProperty("tour_ids")
    private List<Long> tourIds;
}