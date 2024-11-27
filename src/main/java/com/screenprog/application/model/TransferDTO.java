package com.screenprog.application.model;

public record TransferDTO(Long accountIdOfSender, Long accountIdOfReceiver, Double balance) {
}
