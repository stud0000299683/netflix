package ru.utmn.chamortsev.netflix.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class JpaUserDetailsService implements UserDetailsService {

    PersonRepository personRepository;

    public JpaUserDetailsService(PersonRepository personRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Person person = personRepository.findByEmailIgnoreCase(username);
        if (person != null) {
            return User
                    .withUsername(person.getEmail())
                    .accountLocked(!person.isEnabled())
                    .password(person.getPassword())
                    .roles(person.getRole())
                    .build();
        }
        throw new UsernameNotFoundException(username);
    }
}