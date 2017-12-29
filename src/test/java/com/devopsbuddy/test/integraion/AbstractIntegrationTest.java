package com.devopsbuddy.test.integraion;

import java.util.HashSet;
import java.util.Set;

import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;

public abstract class AbstractIntegrationTest {

    @Autowired
    protected PlanRepository planRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserRepository userRepository;

    protected Plan createPlan(PlansEnum plansEnum) {
        // Plan plan = new Plan();
        // plan.setId(PlansEnum.BASIC.getId());
        // plan.setName("Basic");
        // return plan;
        return new Plan(plansEnum);
    }

    protected Role createRole(RolesEnum rolesEnum) {
        // Role role = new Role();
        // role.setId(rolesEnum.getId());
        // role.setName(rolesEnum.getRoleName());
        // return role;
        return new Role(rolesEnum);
    }

    protected User createUser(String username, String email) {
        Plan basicPlan = new Plan(PlansEnum.BASIC);
        planRepository.save(basicPlan);

        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(basicPlan);

        Role basicRole = new Role(RolesEnum.BASIC);
        roleRepository.save(basicRole);

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(basicUser, basicRole);
        userRoles.add(userRole);

        basicUser.getUserRoles().addAll(userRoles);
        basicUser = userRepository.save(basicUser);
        return basicUser;
    }

    protected User createUser(TestName testName) {
        return createUser(testName.getMethodName(), testName.getMethodName() + "@gmail.com");
    }
}
