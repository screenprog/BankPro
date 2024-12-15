package com.screenprog.application.model;

public record ChangePasswordDTO(String username, String currentPass, String newPass) {
}
