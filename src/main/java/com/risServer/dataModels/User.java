package com.risServer.dataModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @AllArgsConstructor
public class User {

    private  long id;
    private String name;
    private String username;
    private String password;
    private String role;


}

