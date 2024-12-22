package com.screenprog.application.dtos;

public record ChangePasswordDTO(String username, String currentPass, String newPass) {
}
