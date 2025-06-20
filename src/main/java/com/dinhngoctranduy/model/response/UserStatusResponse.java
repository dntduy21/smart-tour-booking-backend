package com.dinhngoctranduy.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatusResponse {
    private Long id;
    private boolean blocked;
    private boolean deleted;
}

