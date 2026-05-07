package es.codeurjc.ferrumgym.security;

import es.codeurjc.ferrumgym.model.User;
import es.codeurjc.ferrumgym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Find the user by their email in the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Read their roles (saved as "ROLE_USER" or "ROLE_ADMIN") and convert them to GrantedAuthority
        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role : user.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role));
        }

        // 3. Pass the official data to Spring Security so it can securely compare the password
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getEncodedPassword(), 
                roles);
    }
}