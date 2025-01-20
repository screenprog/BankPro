package com.screenprog.application.service;

import com.screenprog.application.dtos.AccountDTO;
import com.screenprog.application.dtos.ChangePasswordDTO;
import com.screenprog.application.dtos.CustomerDTO;
import com.screenprog.application.dtos.TransferDTO;
import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.model.*;
import com.screenprog.application.repo.*;
import com.screenprog.application.security.BCryptEncryption;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static com.screenprog.application.security.BCryptEncryption.encoder;

/**
 * Service class responsible for managing user, customer, account, staff, and transaction-related operations.
 * This class leverages multiple repositories to interact with the
 * underlying database and uses EmailService for sending out notifications.
 *
 * <p>
 * The CenteralisedService handles operations related to users, customers, accounts, staff, and transactions using various repositories.
 * It integrates with EmailService for notifications. The service uses Spring's @Service annotation for dependency injection,
 * ensuring seamless interaction with the database while leveraging SLF4J for logging.
 * </p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>@Service: Indicates that this class is a Spring service component, which provides business logic for user management.</li>
 *     <li>@Transactional: Ensures that database operations within the methods are executed within a transaction context.</li>
 *     <li>@Autowired: Injects dependencies using constructor injection.</li>
 *     <li>@Deprecated: Indicates that the method is deprecated and should not be used.</li>
 * </ul>
 *
 * <p>Fields:</p>
 * <ul>
 *     <li>usersRepository: Repository for accessing and managing user data.</li>
 *     <li>customerRepository: Repository for accessing and managing customer data.</li>
 *     <li>accountRepository: Repository for accessing and managing account data.</li>
 *     <li>staffRepository: Repository for accessing and managing staff data.</li>
 *     <li>transactionRepository: Repository for accessing and managing transaction data.</li>
 *     <li>emailService: The service responsible for sending emails to users.</li>
 *     <li>LOGGER: Logger instance for logging messages.</li>
 *     <li>encoder: The encoder used for password encryption.</li>
 *     <li>authenticationManager: Authentication manager for user authentication.</li>
 *     <li>jwtService: The service for generating JWT tokens.</li>
 * </ul>
 *
 * <p>Constructor:</p>
 * <p>The constructor initializes the class with the required dependencies for
 * managing applications and customers. It sets up the email service and the
 * necessary repositories for data access and manipulation.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Register and save user credentials in the database.</li>
 *     <li>Save and retrieve staff information from the database.</li>
 *     <li>Save and retrieve customer information from the database.</li>
 *     <li>Save and retrieve account information from the database.</li>
 *     <li>Generate and retrieve transaction information from the database.</li>
 *     <li>Process account-related operations such as deposit, withdraw, and transfer.</li>
 *     <li>Save and retrieve transaction history from the database.</li>
 *     <li>Generate and return JWT tokens for user authentication, using the {@link JsonWebTokenService}.</li>
 *     <li>Send notification emails to customers informing them of their customer verification, using the {@link EmailService}.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <p>This service can be used by controllers or other services to handle user, customer,
 * account, staff, and transaction-related operations. It ensures that operations are processed
 * efficiently and users are notified appropriately regarding their operations.</p>
 *
 * @see UsersRepository
 * @see CustomerRepository
 * @see AccountRepository
 * @see StaffRepository
 * @see TransactionsRepository
 * @see EmailService
 * @see JsonWebTokenService
 * @see BCryptEncryption
 */
@Service
public class CenteralisedService {

    final private UsersRepository usersRepository;
    final private CustomerRepository customerRepository;
    final private AccountRepository accountRepository;
    final private StaffRepository staffRepository;
    final private TransactionsRepository transactionRepository;
    final private EmailService emailService;

    private final Logger LOGGER = LoggerFactory.getLogger(CenteralisedService.class);

    /**
     * The constructor for this class
     * @param usersRepository The repository for users
     * @param customerRepository The repository for customers
     * @param accountRepository The repository for accounts
     * @param staffRepository The repository for staff
     * @param transactionRepository The repository for transactions
     * @param emailService The service for sending emails
     * */
    public CenteralisedService(UsersRepository usersRepository, CustomerRepository customerRepository, AccountRepository accountRepository, StaffRepository staffRepository, TransactionsRepository transactionRepository, EmailService emailService) {
        this.usersRepository = usersRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    /**
     * This method is used to get all the customers
     * @return {@link List<Customer>} objects
     * */
    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }


    /**
     * This method ids used to add a new customer.
     * It converts the CustomerDTO to a Customer object,
     * saves it in the customer repository, calls register method and sends a verification email.
     * @param customerDTO {@link CustomerDTO} object containing customer details
     * @see Customer
     * @see CustomerDTO
     * @see CenteralisedService#register
     * @see EmailService#sendEmail
     * @return {@link Customer} after being saved in the repository
     */
    @Transactional
    public Customer addCustomer(CustomerDTO customerDTO) {
        Customer customer = null;
        customer = customerDTO.toCustomer();
        Customer saved = customerRepository.save(customer);
        String encodedPassword = register(saved.toUser()).getPassword();
        saved.setPassword(encodedPassword);
        emailService.sendEmail(saved.toCustomerEmail());
        return customerRepository.save(saved);
    }


    /**
     * @deprecated use {@link CenteralisedService#register} instead
     * This method is used to register a customer
     * @param user {@link Users} object to register
     * @return {@link Users} object after being saved in the repository
     * @see BCryptEncryption
     */
    @Deprecated
    private Users registerCustomer(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    /**
     * @return {@link List<Account>} List of all the accounts*/
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * This method is used to add a new account.
     * It creates a new Account object using the AccountDTO,
     * saves it in the account repository and returns the saved account.
     * @param accountDto {@link AccountDTO} object containing account details
     * @return {@link Optional<Account>} object after being saved in the repository
     * @see Account
     * @see AccountDTO
     * @see AccountRepository
     * */
    @Transactional
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

    /**
     * This method is used to add a new staff member.
     * It registers the staff as a user by calling {@link CenteralisedService#register},
     * and saves the staff details in the staff repository.
     * @param staff {@link Staff} object containing the staff details
     * @return {@link Staff} object after being saved in the repository
     * @see CenteralisedService#register
     * @see Staff
     * @see StaffRepository
     */
    @Transactional
    public Staff addStaff(Staff staff) {
        String encodedPass = register(staff.toUser()).getPassword();
        staff.setPassword(encodedPass);
        return staffRepository.save(staff);
    }

    /**
     * This method is used to retrieve all the staff members details
     * @return {@link List<Staff>} List of Staff
     * @see Staff
     * @see StaffRepository
     * */
    public List<Staff> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        staffList.forEach(staff -> staff.setPassword("********"));
        return staffList;
    }


    /**
     * This method is used to add a new deposit transaction in the database.
     * It first retrieves the account with the given id,
     * then adds the given amount to the account balance,
     * creates a new Transaction object with the amount and balance,
     * and saves the transaction in the transaction repository.
     * @param accountId {@link Long} id to which the deposit is to be made
     * @param amount {@link Double} amount of money to be deposited
     * @return {@link Transaction} object after being saved in the repository
     * @throws IllegalArgumentException if the deposit amount is negative
     * @see Transaction
     * @see TransactionsRepository
     * @see Account
     * */
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

    /**
     * This method is used to withdraw a specified amount from an account.
     * It retrieves the account with the given accountId, checks if the balance
     * after withdrawal remains above the minimum required balance, and then
     * deducts the specified amount from the account. A transaction record is
     * created and saved in the transaction repository.
     *
     * @param accountId {@link Long} id of the account from which the withdrawal is to be made
     * @param amount {@link Double} amount of money to be withdrawn
     * @return {@link Transaction} object after being saved in the repository
     * @throws IllegalArgumentException if the withdrawal amount is greater than the available balance
     * @see Transaction
     * @see TransactionsRepository
     * @see Account
     * @see AccountRepository
     */
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

    /**
     * It retrieves an account by its id
     * @param accountId Long id of the account
     * @return {@link Account} object
     * @see Account
     * @see AccountRepository
     */
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    /**
     * It retrieves a customer by its id
     * @param id Long id of the customer
     * @return {@link Customer} object
     * @see Customer
     * @see CustomerRepository*/
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    /**
     * It retrieves a staff by its id
     * @param id Long id of the staff
     * @return {@link Staff} object
     * @see Staff
     * @see StaffRepository*/
    public Staff getStaff(Long id) {
        return staffRepository.findById(id).orElse(null);
    }


    /**
     * It transfers an amount of money from one account to another and updates the balance of both accounts.
     * Generates transaction records for both accounts.
     * @param transferDTO a TransferDTO object which contains the amount, id of sender's account and id of receiver's account.
     * @return {@link Transaction} object.
     * @see TransferDTO
     * @see Transaction
     * @see AccountRepository
     */
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

    /**
     * This method is used to register users for their credentials and it encodes the password
     * @param user Users object
     * @return {@link Users} object with id and encoded password
     * @see BCryptEncryption
     * */
    public Users register(Users user) {
        LOGGER.info("Inside register");
        if(user.getUsername() == null)
            throw new RuntimeException("Null Username");
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
    /**
     * This method is used to change the password of user, staff and admin.
     * It checks the current password if it matches then it encodes and changes the password
     * @param user {@link ChangePasswordDTO} object
     * @return {@link Users} object
     * @see ChangePasswordDTO
     * @see Users
     * */
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
    /**
     * This method is used to verify the user
     * @param user Users object
     * @return String - a jwt token
     * @see JsonWebTokenService
     * */
    public String verify(Users user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        return (authentication.isAuthenticated())?jwtService.generateToken(user.getUsername()) : null;
    }

    /**
     * This method is used to get the path of the user
     * @param user {@link Users} object
     * @return String of path */
    public String getPath(Users user) {
        Users byUsername = usersRepository.findByUsername(user.getUsername());
        if(byUsername.getRoles().contains("ADMIN"))
            return "Admin/staffCustomer.html";
        if(byUsername.getRoles().contains("STAFF"))
            return "Staff/Staffacces.html";
        List<Account> account = customerRepository.findById(Long.valueOf(byUsername.getUsername())).get().getAccount();
        if(account.isEmpty())
            return "User/Addaccount.html";
        return "User/Accountdetail.html";
    }

    /**
     * This method is used to get all the transactions
     * @return {@link List<Transaction>} of all the transactions*/
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /*TODO: Test this method*/
    public String verifyEmailForPassWord(String email) {
        if(customerRepository.existsByEmail(email) || staffRepository.existsByEmail(email)){
            return emailService.sendOTP(email);
        }
        return null;
    }

    /*TODO: Test this method*/
    public String changePassword(String email, String newPass) {
        Customer customer = customerRepository.findByEmail(email);
        customer.setPassword(encoder.encode(newPass));
        Users byUsername = usersRepository.findByUsername(customer.getCustomerID().toString());
        byUsername.setPassword(customer.getPassword());
        //TODO:
        // Send email of password changed.
        customerRepository.save(customer);
        usersRepository.save(byUsername);
        return "Password changed successfully";
    }

}
