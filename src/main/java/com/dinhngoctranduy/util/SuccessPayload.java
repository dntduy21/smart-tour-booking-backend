package com.dinhngoctranduy.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessPayload {
    private Boolean completed = true;

    public static SuccessPayload build() {
        return new SuccessPayload();
    }
}
