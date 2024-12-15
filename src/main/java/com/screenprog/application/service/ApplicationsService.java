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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

//    saving customer
    @Transactional
    public Customer registerCustomer(CustomerDTO customerDTO, byte[] image) {
        Customer customer = customerDTO.toCustomer();
        customer.setImage(image);
        Customer saved = customerRepository.save(customer);
        saveCredentials(saved.toUser());
        return saved;
    }

    private void saveCredentials(Users user) {
        usersRepository.save(user);
    }
}
