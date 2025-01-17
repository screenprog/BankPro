package com.screenprog.application.service;

import com.screenprog.application.dtos.CustomerDTO;
import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.model.*;
import com.screenprog.application.repo.ApplicationsRepository;
import com.screenprog.application.repo.CustomerRepository;
import com.screenprog.application.repo.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service class responsible for managing and processing applications in the system.
 * This class provides functionalities to update the status of applications, register
 * customers based on verified applications, and save customer credentials.
 *
 * <p>The ApplicationsService class interacts with various repositories and services
 * to perform operations related to applications and customers. It handles the
 * processing of applications in parallel for efficiency and sends notification emails
 * to customers based on the status of their applications.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *   <li>@Service: Indicates that this class is a Spring service component, which
 *   provides business logic for application management.</li>
 *   <li>@Transactional: Ensures that database operations within the methods are
 *   executed within a transaction context.</li>
 * </ul>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>emailService: The service responsible for sending emails to customers.</li>
 *   <li>applicationsRepository: Repository for accessing and managing application
 *   data.</li>
 *   <li>customerRepository: Repository for accessing and managing customer data.</li>
 *   <li>usersRepository: Repository for accessing and managing user data.</li>
 * </ul>
 *
 * <p>Constructor:</p>
 * <p>The constructor initializes the class with the required dependencies for
 * managing applications and customers. It sets up the email service and the
 * necessary repositories for data access and manipulation.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Update the status of applications and perform specific actions based on
 *   the updated status.</li>
 *   <li>Register customers from verified applications and save their credentials
 *   in the database.</li>
 *   <li>Send notification emails to customers based on their application status.</li>
 *   <li>Retrieve pending applications from the repository for further processing.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <p>This service can be used by controllers or other services to handle application
 * and customer-related operations. It ensures that applications are processed
 * efficiently and customers are notified appropriately regarding their application
 * status.</p>
 *
 * @see EmailService
 * @see ApplicationsRepository
 * @see CustomerRepository
 * @see UsersRepository
 * @see Application
 * @see Customer
 */
@Service
public class ApplicationsService {


    private final EmailService emailService;
    private final ApplicationsRepository applicationsRepository;
    private final CustomerRepository customerRepository;
    private final UsersRepository usersRepository;


    /**
     * Constructor for ApplicationsService
     * @param emailService EmailService
     * @param applicationsRepository ApplicationsRepository
     * @param customerRepository CustomerRepository
     * @param usersRepository UsersRepository
     * @see EmailService
     * @see ApplicationsRepository
     * @see CustomerRepository
     * @see UsersRepository
     * */
    public ApplicationsService(EmailService emailService, ApplicationsRepository applicationsRepository, CustomerRepository customerRepository, UsersRepository usersRepository) {
        this.emailService = emailService;
        this.applicationsRepository = applicationsRepository;
        this.customerRepository = customerRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Retrieves a list of pending applications from the repository.
     * The pending applications can then be further processed or analyzed by other components of the
     * application.
     * @return a list of pending Application objects
     * @see ApplicationsRepository#findAllPendingApplications
     */
    public List<Application> getPendingApplications() {
        return applicationsRepository.findAllPendingApplications();
    }

    /**
     * This method updates the status of a list of applications and performs specific actions
     * based on the updated status. It filters out applications with a status of NON_VERIFIED
     * and sends a notification email to VERIFIED and NON_VERIFIED applications indicating their status.
     * The method processes the applications in parallel for efficiency.
     * @param applications List of Application objects to be updated
     * @return List of Application objects after being saved to the repository
     * @see Application
     * @see Application#toApplicationNotVerifiedEmail
     * @see Customer#toApplicationVerifiedEmail
     * @see Executors#newVirtualThreadPerTaskExecutor
     * @see ApplicationStatus
     * @see EmailService#sendEmail
     */
    public List<Application> updateApplications(List<Application> applications) {
        /*TODO: filter out the NON_VERIFIED applications and send them a non verified email :o DONE*/
        applications.parallelStream()
                .filter(application -> application.getStatus().equals(ApplicationStatus.NON_VERIFIED))
                .map(Application::toApplicationNotVerifiedEmail)
                .forEach(emailService::sendEmail);

        List<Application> savedApplications = applicationsRepository.saveAll(applications);
        List<Application> verifiedApplications = savedApplications.stream().filter(application -> application.getStatus().equals(ApplicationStatus.VERIFIED))
                .toList();
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        for (Application value : verifiedApplications) {
            executorService.submit(() -> {
                var customerDTO = value.toCustomerDTO();
                Customer customer = registerCustomer(customerDTO, value.getImage());
                emailService.sendEmail(customer.toApplicationVerifiedEmail());
            });
        }
        executorService.shutdown();
        return savedApplications;

    }

    /**
     * This method is used to save the customer credentials after verifying the application.
     * It takes the customerDTO and the image of the customer then it creates a customer object
     * and saves it in the database, then it creates the user credentials and saves them in the
     * database by calling the saveCredentials method.
     * @param customerDTO CustomerDTO object containing the customer details
     * @param image the image of the customer in bytes
     * @return the saved Customer object
     * @see CustomerDTO
     * @see Customer
     * @see Users
     */
    @Transactional
    public Customer registerCustomer(CustomerDTO customerDTO, byte[] image) {
        Customer customer = customerDTO.toCustomer();
        customer.setImage(image);
        Customer saved = customerRepository.save(customer);
        saveCredentials(saved.toUser());
        return saved;
    }

    /**
     * This method is used to save the credentials of the customer in the database.
     * It takes the user object and saves it in the database.
     * @param user the user object containing the credentials of the customer
     * @see Users
     */
    private void saveCredentials(Users user) {
        usersRepository.save(user);
    }
}
