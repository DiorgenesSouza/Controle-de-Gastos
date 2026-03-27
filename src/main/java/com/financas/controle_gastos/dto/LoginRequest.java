package com.financas.controle_gastos.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String senha;
}