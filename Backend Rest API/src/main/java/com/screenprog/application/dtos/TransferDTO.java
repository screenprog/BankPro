package com.screenprog.application.dtos;

public record TransferDTO(Long accountIdOfSender, Long accountIdOfReceiver, Double balance, String pin) {
}
