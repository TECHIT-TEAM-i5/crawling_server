package com.example.crawling.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private LocalDateTime createdAt;

    private String loginId;
    private String password;
    private String nickname;
    private String email;
    private String gender;
    private String phone;
    private String profileImg;
    private String address;



}
