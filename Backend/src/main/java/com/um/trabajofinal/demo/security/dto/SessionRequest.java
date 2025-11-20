package com.um.trabajofinal.demo.security.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionRequest {
    private String username;
}
