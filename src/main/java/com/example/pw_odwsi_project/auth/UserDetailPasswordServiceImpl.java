//package com.example.pw_odwsi_project.auth;
//
//import com.example.pw_odwsi_project.domain.User;
//import com.example.pw_odwsi_project.repos.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsPasswordService;
//
//public class UserDetailPasswordServiceImpl implements UserDetailsPasswordService {
//
//    private final UserRepository userRepository;
//    private final UserDetailsMapper userDetailsMapper;
//
//    public UserDetailPasswordServiceImpl(
//            UserRepository userRepository, UserDetailsMapper userDetailsMapper) {
//        this.userRepository = userRepository;
//        this.userDetailsMapper = userDetailsMapper;
//    }
//
//    @Override
//    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
//        User user = userRepository.findByUsername(userDetails.getUsername());
//        user.setPassword(newPassword);
//        return userDetailsMapper.toUserDetails(user);
//    }
//
//}
