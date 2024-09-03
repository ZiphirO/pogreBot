package com.ziphiro.podBot.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {


    private Long userId;
    private String userName;
    private String userEmail;
    private String userPass;

}
