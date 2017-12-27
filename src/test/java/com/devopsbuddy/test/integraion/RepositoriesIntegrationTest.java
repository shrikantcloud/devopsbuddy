package com.devopsbuddy.test.integraion;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoriesIntegrationTest {
    
    private static final Integer BASIC_PLAN_ID = 1;
    private static final Integer BASIC_ROLE_ID = 1;
    
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Before
    public void ini() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(roleRepository);
        Assert.assertNotNull(userRepository);
    }
    
    @Test
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findOne(BASIC_PLAN_ID);
        Assert.assertNotNull(retrievedPlan);
    }
    
    @Test
    public void testCreateNewRole() throws Exception {
        Role userRole = createBasicRole();
        roleRepository.save(userRole);
        Role retrievedRole = roleRepository.findOne(BASIC_ROLE_ID);
        Assert.assertNotNull(retrievedRole);
    }
    
    @Test
    public void createNewUser() throws Exception {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        
        User basicUser = createBasicUser();
        basicUser.setPlan(basicPlan);
        
        Role basicRole = createBasicRole();
        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setUser(basicUser);
        userRole.setRole(basicRole);
        userRoles.add(userRole);
        
        basicUser.getUserRoles().addAll(userRoles);
        for(UserRole uRole : userRoles) {
            roleRepository.save(uRole.getRole());
        }
        
        basicUser = userRepository.save(basicUser);
        User newlyCreatedUser = userRepository.findOne(basicUser.getId());
        
        Assert.assertNotNull(newlyCreatedUser);
        Assert.assertTrue(newlyCreatedUser.getId() != 0);
        Assert.assertNotNull(newlyCreatedUser.getPlan());
        Assert.assertNotNull(newlyCreatedUser.getPlan().getId());
        Set<UserRole> newlyCreatedUserUserRoles = newlyCreatedUser.getUserRoles();
        for(UserRole ur: newlyCreatedUserUserRoles) {
            Assert.assertNotNull(ur.getRole());
            Assert.assertNotNull(ur.getRole().getId());
        }
    }

    private User createBasicUser() {
        User user = new User();
        user.setUsername("basicUser");
        user.setPassword("secret");
        user.setEmail("s.a@g.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPhoneNumber("123456789123");
        user.setCountry("IN");
        user.setEnabled(true);
        user.setDescription("A basic User");
        user.setProfileImageUrl("/images/sa.jpg");
        return user;
    }

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("Basic");
        return plan;
    }
    
    private Role createBasicRole() {
        Role role = new Role();
        role.setId(BASIC_ROLE_ID);
        role.setName("ROLE_USER");
        return role;
    }

}
