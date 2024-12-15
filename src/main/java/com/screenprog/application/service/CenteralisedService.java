package com.screenprog.application.service;

import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.model.*;
import com.screenprog.application.repo.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static com.screenprog.application.security.BCryptEncryption.encoder;

@org.springframework.stereotype.Service
public class CenteralisedService {

    final private UsersRepository usersRepository;
    final private CustomerRepository customerRepository;
    final private AccountRepository accountRepository;
    final private StaffRepository staffRepository;
    final private TransactionsRepository transactionRepository;
    final private EmailService emailService;

    private final Logger LOGGER = LoggerFactory.getLogger(CenteralisedService.class);

    public CenteralisedService(UserService userService, UsersRepository usersRepository, CustomerRepository customerRepository, AccountRepository accountRepository, StaffRepository staffRepository, TransactionsRepository transactionRepository, EmailService emailService) {
        this.usersRepository = usersRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer addCustomer(CustomerDTO customerDTO) {
        Customer customer = null;
        customer = customerDTO.toCustomer();
        Customer saved = customerRepository.save(customer);
        String encodedPassword = registerCustomer(saved.toUser()).getPassword();
        saved.setPassword(encodedPassword);
        emailService.sendEmail(saved.toCustomerEmail());
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
                .description("Deposited through bank")
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
                .description("Withdrawn through bank")
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
                        .description("Credited to " + transferDTO.accountIdOfReceiver())
                        .amount(transferDTO.balance())
                        .balanceLeft(accountOfSender.getBalance())
                        .build()));
        transactionRepository.saveAll(transactions);

        return transactions.get(1);
    }


    public Users register(Users user) {
        LOGGER.info("Inside register");
        if(user.getUsername() == null)
            throw new RuntimeException("Null Username" + user.getPassword());
        if(usersRepository.existsByUsername(user.getUsername()))
            throw new RuntimeException("Already exist");
        user.setPassword(encoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    /*TODO: Change the working of this function to receive
       the current password as well and compare it with the
       current password if password does not matched then don't
       change the password just throw an error message :o DONE*/
    /*TODO: Done completed and it's working very well*/
    public Users changePassword(ChangePasswordDTO user) {
        Users userInDB = usersRepository.findByUsername(user.username());
        if (userInDB == null)
            return null;
        if(!encoder.matches(user.currentPass(), userInDB.getPassword()))
            throw new RuntimeException("Incorrect pin");
        userInDB.setPassword(encoder.encode(user.newPass()));
        LOGGER.info(userInDB.getRoles().toString());
        return switch (userInDB.getRoles().getFirst()) {
            case "ADMIN" -> usersRepository.save(userInDB);
            case "STAFF" -> {
                Staff staff = staffRepository.findByEmail(userInDB.getUsername());
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


    /*TODO: Move the below code to the separate service class 'Authentication service' if required*/
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JsonWebTokenService jwtService;
    public String verify(Users user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        return (authentication.isAuthenticated())?jwtService.generateToken(user.getUsername()) : null;
    }

    public String getPath(Users user) {
        Users byUsername = usersRepository.findByUsername(user.getUsername());
        if(byUsername.getRoles().contains("ADMIN"))
            return "admin.html";
        if(byUsername.getRoles().contains("STAFF"))
            return "staff/dashboard";
        return "Addaccount.html";
    }
}
