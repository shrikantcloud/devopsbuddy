package com.devopsbuddy.backend.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {
        Plan plan = new Plan(plansEnum);
        if(!planRepository.exists(plansEnum.getId())) {
            planRepository.save(plan);
        }
        
        user.setPlan(plan);
        for(UserRole ur : userRoles) {
            roleRepository.save(ur.getRole());
        }
        user.getUserRoles().addAll(userRoles);
        userRepository.save(user);
        return user;
    }

}
