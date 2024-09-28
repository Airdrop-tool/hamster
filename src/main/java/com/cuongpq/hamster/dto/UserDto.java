package com.cuongpq.hamster.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String first_name;
    private String last_name;
    private String username;
    private String language_code;
    private boolean allows_write_to_pm;
}
