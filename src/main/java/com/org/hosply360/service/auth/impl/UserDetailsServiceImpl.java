package com.org.hosply360.service.auth.impl;


import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Users;
import com.org.hosply360.exception.UserException;
import com.org.hosply360.helper.CustomUserDetails;
import com.org.hosply360.repository.authRepo.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UserException(ErrorConstant.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        logger.info("User found with username: {}", username);
        return new CustomUserDetails(user);

    }
}
