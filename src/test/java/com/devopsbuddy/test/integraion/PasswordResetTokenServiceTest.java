package com.devopsbuddy.test.integraion;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.PasswordResetTokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordResetTokenServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Rule
    public TestName                          testName = new TestName();

    @Test
    public void testCreateNewTokenForUserEmail() throws Exception {
        User user = createUser(testName);

        PasswordResetToken passwordRestToken = passwordResetTokenService.createPasswordResetTokenForEmail(user.getEmail());
        Assert.assertNotNull(passwordRestToken);
        Assert.assertNotNull(passwordRestToken.getToken());
    }
    
    @Test
    public void testFindByToken() throws Exception{
        User user = createUser(testName);

        PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(user.getEmail());
        Assert.assertNotNull(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getToken());
        
        PasswordResetToken token = passwordResetTokenService.findByToken(passwordResetToken.getToken());
        Assert.assertNotNull(token);
    }

}
