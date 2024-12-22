package com.screenprog.application.service;

import com.screenprog.application.model.UserPrincipal;
import com.screenprog.application.model.Users;
import com.screenprog.application.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepo.findByUsername(username);

        if(user == null){
            System.out.println("User not found: " + username);
            throw new UsernameNotFoundException("User not found");
        }

        return new UserPrincipal(user);
    }
}
