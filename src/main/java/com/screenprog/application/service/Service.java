package com.screenprog.application.service;

import com.screenprog.application.model.*;
import com.screenprog.application.repo.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    final private UsersRepository usersRepository;
    final private CustomerRepository customerRepository;
    final private AccountRepository accountRepository;
    final private StaffRepository staffRepository;
    final private TransactionsRepository transactionRepository;
    final private ApplicationsRepository applicationsRepository;

    private Logger LOGGER = LoggerFactory.getLogger(Service.class);

    @Autowired
    public Service(UserService userService, UsersRepository usersRepository, CustomerRepository customerRepository, AccountRepository accountRepository, StaffRepository staffRepository, TransactionsRepository transactionRepository, ApplicationsRepository applicationsRepository) {
        this.usersRepository = usersRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
        this.transactionRepository = transactionRepository;
        this.applicationsRepository = applicationsRepository;
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer addCustomer(CustomerDTO customerDTO) {
        Customer customer = customerDTO.toCustomer();
        Customer saved = customerRepository.save(customer);
        String encodedPassword = registerCustomer(saved.toUser()).getPassword();
        saved.setPassword(encodedPassword);
        return customerRepository.save(saved);
    }

    private Users registerCustomer(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }


    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> addAccount(AccountDTO accountDto) {

        if(customerRepository.existsById(accountDto.customerId())){
            Customer customer = new Customer();
            customer.setCustomerID(accountDto.customerId());
            Account account = new Account();
            account.setCustomer(customer);
            account.setBalance(accountDto.balance());
            account.setStatus(accountDto.status());
            account.setType(accountDto.type());
            accountRepository.save(account);
            return Optional.of(account);
        }
        return Optional.empty();
    }

    @Transactional
    public Staff addStaff(Staff staff) {
        String encodedPass = register(staff.toUser()).getPassword();
        staff.setPassword(encodedPass);
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        staffList.forEach(staff -> staff.setPassword("********"));
        return staffList;
    }

    @Transactional
    public Transaction deposit(Long accountId, Double amount) {
        Account account = getAccount(accountId);
        if(account == null)
            return null;

        if(amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");

        account.setBalance(account.getBalance() + amount);
        Transaction transaction = Transaction.builder()
                .accountId(account)
                .amount(amount)
                .balanceLeft(account.getBalance())
                .build();

        transactionRepository.save(transaction);
        accountRepository.save(account);

        return transaction;
    }

    @Transactional
    public Transaction withdraw(Long accountId, Double amount) {
        Account account = getAccount(accountId);
        if(account == null)
            return null;

        if(account.getBalance() - amount < 100)
            throw new IllegalArgumentException("Insufficient balance");

        account.setBalance(account.getBalance() - amount);
        Transaction transaction = Transaction.builder()
                .accountId(account)
                .amount(amount)
                .balanceLeft(account.getBalance())
                .build();
        transactionRepository.save(transaction);
        accountRepository.save(account);

        return transaction;
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Staff getStaff(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    public List<Apply> getPendingApplications() {
         return applicationsRepository.findAllPendingApplications();
    }

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
        transactionRepository.saveAll(transactions);

        return transactions.get(1);
    }

    public List<Apply> updateApplications(List<Apply> applications) {
        return applicationsRepository.saveAll(applications);

    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(Users user) {
        LOGGER.info("Inside register");
        if(user.getUsername() == null)
            throw new RuntimeException("Null Username" + user.getPassword());
        if(usersRepository.existsByUsername(user.getUsername()))
            throw new RuntimeException("Already exist");
        user.setPassword(encoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public Users changePassword(Users user) {
        Users userInDB = usersRepository.findByUsername(user.getUsername());
        if (userInDB == null)
            return null;
        userInDB.setPassword(encoder.encode(user.getPassword()));
        return switch (user.getRoles().getFirst()) {
            case "ADMIN" -> usersRepository.save(userInDB);
            case "STAFF" -> {
                Staff staff = staffRepository.findById(Long.valueOf(userInDB.getUsername())).orElse(null);
                if(staff == null)
                    yield null;
                staff.setPassword(userInDB.getPassword());
                staffRepository.save(staff);
                yield usersRepository.save(userInDB);
            }
            case "USER" -> {
                Customer customer = customerRepository.findById(Long.valueOf(userInDB.getUsername())).orElse(null);
                if(customer == null)
                    yield null;
                customer.setPassword(userInDB.getPassword());
                customerRepository.save(customer);
                yield usersRepository.save(userInDB);
            }
            default -> null;
        };

    }
}
