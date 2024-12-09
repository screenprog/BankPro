package com.screenprog.application.service;

import com.screenprog.application.email_service.EmailDTO;
import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.model.*;
import com.screenprog.application.repo.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.screenprog.application.security.BCryptEncryption.encoder;

@Service
public class UserService {

    private final ApplicationsRepository applicationsRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final CustomerRepository customerRepository;
    private final DebitCardRepository debitCardRepository;
    private final TransactionsRepository transactionsRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(ApplicationsRepository applicationsRepository, AccountRepository accountRepository, EmailService emailService, CustomerRepository customerRepository, DebitCardRepository debitCardRepository, TransactionsRepository transactionsRepository) {
        this.applicationsRepository = applicationsRepository;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.customerRepository = customerRepository;
        this.debitCardRepository = debitCardRepository;
        this.transactionsRepository = transactionsRepository;
    }


    @Transactional
    public String applyCustomer(ApplicationDTO applicationDTO,
                                MultipartFile image,
                                MultipartFile verificationId,
                                MultipartFile signatureImage) {
        Application apply = applicationDTO.toApplication();
        try {
            apply.setImage(image.getBytes());
            LOGGER.info("image is converted");
            apply.setVerificationId(verificationId.getBytes());
            LOGGER.info("card is converted");
            apply.setSignatureImage(signatureImage.getBytes());
            LOGGER.info("signature is converted");
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert images into bytes");
        }
        applicationsRepository.save(apply);
        emailService.sendEmail(apply.toApplicationReceivedEmailDTO());
        return "Wait for your verification";

    }


    public Double getBalance(Long accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(Account::getBalance)
                .orElse(null);
    }

    /*TODO: Test this function and it's working*/
    @Transactional
    public Account openAccount(AccountDTO accountDto) {
        Optional<Customer> byId = customerRepository.findById(accountDto.customerId());
        if (byId.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");

        Customer customer = byId.get();
        if (customer.getAccount().size() == 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't open more than 2 accounts");
        if (customer.getAccount().size() == 1 && customer.getAccount().getFirst().getType().equals(accountDto.type()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't open more than one account of same type");
        var account = new Account();
        account.setCustomer(customer);
        account.setBalance(
                accountDto.balance() == null ?
                        accountDto.type().equals(AccountType.SAVING) ? 100.00 : 500.00 :
                        accountDto.balance());
        account.setStatus(Status.ACTIVE);
        account.setType(accountDto.type());
        DebitCard card = saveDebitCard(accountDto.pin(), customer.getFirstName() + " " + customer.getLastName());
        account.setCard(card);
        Account savedAccount = accountRepository.save(account);
        emailService.sendEmail(savedAccount.toAccountOpenEmailDTO(customer.getEmail()));
        return savedAccount;
    }

    /*TODO: Test this end point and it's new functionality of working with pin*/
    @Transactional
    public Transaction transferAmount(TransferDTO transferDTO) {
        Account accountOfReceiver = getAccount(transferDTO.accountIdOfReceiver());
        Account accountOfSender = getAccount(transferDTO.accountIdOfSender());

        if (accountOfReceiver == null)
            return Transaction.builder().description("Receiver account is incorrect").build();
        if (accountOfSender == null)
            return Transaction.builder().description("Sender account is incorrect").build();
        if (accountOfSender.getBalance() - transferDTO.balance() <= 100)
            return Transaction.builder().description("Insufficient balance").build();
        if(!encoder.matches(transferDTO.pin(), accountOfSender.getCard().getPin()))
            return Transaction.builder().description("Incorrect pin - Transaction failed!").build();

        accountOfReceiver.setBalance(accountOfReceiver.getBalance() + transferDTO.balance());
        accountOfSender.setBalance(accountOfSender.getBalance() - transferDTO.balance());

        List<Transaction> transactions = new ArrayList<>(List.of(
                Transaction.builder()
                        .accountId(accountOfReceiver)
                        .description("Deposited by " + transferDTO.accountIdOfSender())
                        .amount(transferDTO.balance())
                        .balanceLeft(accountOfReceiver.getBalance())
                        .build(),
                Transaction.builder()
                        .accountId(accountOfSender)
                        .description("Transferred to " + transferDTO.accountIdOfReceiver())
                        .amount(transferDTO.balance())
                        .balanceLeft(accountOfSender.getBalance())
                        .build()));
        transactionsRepository.saveAll(transactions);

        return transactions.get(1);
    }

    private Account getAccount(Long accountNumber) {
        return accountRepository.findById(accountNumber).orElse(null);
    }

    private final Random random = new Random();

    /*TODO: Test this function and it's working*/
    @Transactional
    public DebitCard saveDebitCard(Integer pin, String cardHolderName){
        var cvv = String.valueOf(random.nextInt(1000)); // Random 3 digit cvv
        var currentDate = LocalDate.now();
        var expiryDate = LocalDate.of(currentDate.getYear()+5, currentDate.getMonth(), 1);
        var debitCard = DebitCard.builder()
                .cardHolderName(cardHolderName)
                .cvv(cvv)
                .expirationDate(expiryDate)
                .pin(encoder.encode(pin.toString()))
                .build();
        return debitCardRepository.save(debitCard);
    }


    /*TODO: Test this function it requires pin*/
    @Transactional
    public Transaction withdraw(WithdrawDTO withdrawDTO) {
        Account account = getAccount(withdrawDTO.accountNumber());
        if(account == null)
            return null;
        if(!encoder.matches(withdrawDTO.pin(), account.getCard().getPin()))
            throw  new IllegalArgumentException("Incorrect pin - withdrawal failed");
        if(account.getBalance() - withdrawDTO.balance() < 100)
            throw new IllegalArgumentException("Insufficient balance");

        account.setBalance(account.getBalance() - withdrawDTO.balance());
        Transaction transaction = Transaction.builder()
                .accountId(account)
                .amount(withdrawDTO.balance())
                .balanceLeft(account.getBalance())
                .description("Withdrawn by user")
                .build();
        transactionsRepository.save(transaction);
        accountRepository.save(account);

        return transaction;
    }

    /*TODO: Test this function*/
    @Transactional
    public Transaction deposit(WithdrawDTO withdrawDTO) {
        Account account = getAccount(withdrawDTO.accountNumber());
        if(account == null)
            return null;

        if(withdrawDTO.balance() <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");
        if(!encoder.matches(withdrawDTO.pin(), account.getCard().getPin()))
            throw  new IllegalArgumentException("Incorrect pin - withdrawal failed");
        account.setBalance(account.getBalance() + withdrawDTO.balance());
        Transaction transaction = Transaction.builder()
                .accountId(account)
                .amount(withdrawDTO.balance())
                .balanceLeft(account.getBalance())
                .build();

        transactionsRepository.save(transaction);
        accountRepository.save(account);

        return transaction;
    }

    /*TODO: Test this function*/
    public List<Transaction> getTransactionHistory(Long accountNumber) {
//        return transactionsRepository.findAllByAccountId(accountNumber);
        return null;
    }

}
