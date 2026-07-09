package br.com.jtech.tasklist.adapters.input.protocols;

import java.io.Serializable;

public class CreateUserRequest implements Serializable {

    public String email;
    public String password;
    public String name;

}
