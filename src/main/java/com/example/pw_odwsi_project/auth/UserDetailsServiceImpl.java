//package com.example.pw_odwsi_project.auth;
//
//import com.example.pw_odwsi_project.domain.User;
//import com.example.pw_odwsi_project.repos.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//    private final UserDetailsMapper userDetailsMapper;
//
//    public UserDetailsServiceImpl(final UserRepository userRepository, UserDetailsMapper userDetailsMapper) {
//        this.userRepository = userRepository;
//        this.userDetailsMapper = userDetailsMapper;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(final String email) {
//        final User user = userRepository.findByEmailIgnoreCase(email);
//        if (user == null) {
//            throw new UsernameNotFoundException("User " + email + " not found");
//        }
//        return userDetailsMapper.toUserDetails(user);
//    }
//
//}
