package org.clickenrent.authservice.service;

import lombok.RequiredArgsConstructor;
import org.clickenrent.authservice.entity.User;
import org.clickenrent.authservice.entity.UserCompany;
import org.clickenrent.authservice.entity.UserGlobalRole;
import org.clickenrent.authservice.exception.ResourceNotFoundException;
import org.clickenrent.authservice.repository.UserCompanyRepository;
import org.clickenrent.authservice.repository.UserGlobalRoleRepository;
import org.clickenrent.authservice.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * Loads user by username or email and populates authorities from global and company roles.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final UserGlobalRoleRepository userGlobalRoleRepository;
    private final UserCompanyRepository userCompanyRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with username or email: " + usernameOrEmail)));
        
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is not active");
        }
        
        if (user.getIsDeleted()) {
            throw new UsernameNotFoundException("User account has been deleted");
        }
        
        List<SimpleGrantedAuthority> authorities = loadUserAuthorities(user);
        
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                !user.getIsDeleted(), // accountNonLocked
                authorities
        );
    }
    
    /**
     * Load all authorities (roles) for a user from both global roles and company roles.
     */
    private List<SimpleGrantedAuthority> loadUserAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // Load global roles
        List<UserGlobalRole> globalRoles = userGlobalRoleRepository.findByUser(user);
        List<SimpleGrantedAuthority> globalAuthorities = globalRoles.stream()
                .map(ugr -> new SimpleGrantedAuthority("ROLE_" + ugr.getGlobalRole().getName().toUpperCase()))
                .collect(Collectors.toList());
        authorities.addAll(globalAuthorities);
        
        // Load company roles (prefixed with COMPANY_)
        List<UserCompany> userCompanies = userCompanyRepository.findByUser(user);
        List<SimpleGrantedAuthority> companyAuthorities = userCompanies.stream()
                .map(uc -> new SimpleGrantedAuthority(
                        "COMPANY_" + uc.getCompanyRole().getName().toUpperCase() + 
                        "_" + uc.getCompany().getId()))
                .collect(Collectors.toList());
        authorities.addAll(companyAuthorities);
        
        // If no roles found, assign default USER role
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
    
    @Transactional(readOnly = true)
    public User loadUserEntityByUsername(String usernameOrEmail) {
        return userRepository.findByUserName(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with username or email: " + usernameOrEmail)));
    }
}

