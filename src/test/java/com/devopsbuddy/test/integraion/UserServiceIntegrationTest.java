package com.devopsbuddy.test.integraion;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateUser() throws Exception {
        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
    }

}
