package com.devopsbuddy.backend.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;

@Service
@Transactional(readOnly = true)
public class UserService {
    private static final Logger   LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PlanRepository        planRepository;
    @Autowired
    private RoleRepository        roleRepository;
    @Autowired
    private UserRepository        userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        Plan plan = new Plan(plansEnum);
        if (!planRepository.exists(plansEnum.getId())) {
            planRepository.save(plan);
        }

        user.setPlan(plan);
        for (UserRole ur : userRoles) {
            roleRepository.save(ur.getRole());
        }
        user.getUserRoles().addAll(userRoles);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void updateUserPassword(long userId, String password) throws Exception {
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.updateUserPassword(userId, encodedPassword);
        LOG.debug("Password updated successfully for userID {}", userId);
    }

}
