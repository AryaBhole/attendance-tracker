package com.arya.attendance.security;

import com.arya.attendance.model.User;
import com.arya.attendance.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String rollNumber) throws UsernameNotFoundException {
        User user = userRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new UsernameNotFoundException("No account for roll number: " + rollNumber));

        if (user.getPasswordHash() == null) {
            throw new UsernameNotFoundException("This account uses Google sign-in, not a password");
        }

        return new AppUserDetails(user);
    }
}