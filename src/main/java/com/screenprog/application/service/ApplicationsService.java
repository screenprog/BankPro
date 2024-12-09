package com.screenprog.application.service;

import com.screenprog.application.email_service.EmailService;
import com.screenprog.application.model.*;
import com.screenprog.application.repo.ApplicationsRepository;
import com.screenprog.application.repo.CustomerRepository;
import com.screenprog.application.repo.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ApplicationsService {


    private final EmailService emailService;
    private final ApplicationsRepository applicationsRepository;
    private final CustomerRepository customerRepository;
    private final UsersRepository usersRepository;


    public ApplicationsService(EmailService emailService, ApplicationsRepository applicationsRepository, CustomerRepository customerRepository, UsersRepository usersRepository) {
        this.emailService = emailService;
        this.applicationsRepository = applicationsRepository;
        this.customerRepository = customerRepository;
        this.usersRepository = usersRepository;
    }

    public List<Application> getPendingApplications() {
        return applicationsRepository.findAllPendingApplications();
    }

    public List<Application> updateApplications(List<Application> applications) {
        List<Application> verifiedApplications = applications.stream()
                .filter(application -> application.getStatus().equals(ApplicationStatus.VERIFIED))
                .toList();

        //saving and sending emails for the verified applications
        for (Application value : verifiedApplications) {
            var customer = value.toCustomerDTO();
            Customer customer1 = registerCustomer(customer, value.getImage());
            emailService.sendEmail(customer1.toApplicationVerifiedEmail());
        }

        /*TODO: filter out the NON_VERIFIED applications and send them a non verified email :o DONE*/
        applications.stream()
                .filter(application -> application.getStatus().equals(ApplicationStatus.NON_VERIFIED))
                .map(Application::toApplicationNotVerifiedEmail)
                .forEach(emailService::sendEmail);

        return applicationsRepository.saveAll(applications);

    }

//    saving customer
    @Transactional
    public Customer registerCustomer(CustomerDTO customerDTO, byte[] image) {
        Customer customer = null;
        try { //this is just to handle the IOException so the working of the code is not affected
            customer = customerDTO.toCustomer(); // image is null
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        customer.setImage(image);
        Customer saved = customerRepository.save(customer);
        saveCredentials(saved.toUser());
        return saved;
    }

    private void saveCredentials(Users user) {
        usersRepository.save(user);
    }
}
