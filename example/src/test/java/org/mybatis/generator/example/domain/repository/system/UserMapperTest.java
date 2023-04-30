package org.mybatis.generator.example.domain.repository.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.huxhorn.sulky.ulid.ULID;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.example.ServerConfiguration;
import org.mybatis.generator.example.domain.entity.system.gen.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = ServerConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
public class UserMapperTest {

  @Autowired //
  private UserMapper userMapper;

  private final ULID ulid = new ULID();

  @BeforeEach
  public void before() {
    // truncate table
    userMapper.truncate();

    // insert test data
    for (var i = 0; i < 10; i++) {
      var user =
          new User()
              .setUserId(ulid.nextULID())
              .setUserName("TestUser%03d".formatted(i))
              .setEmail("test-user-%03d@example.com".formatted(i))
              .setCreatedAt(LocalDateTime.now())
              .setCreatedBy("initial")
              .setVersion(0);
      userMapper.insert(user);
    }
  }

  @Test
  public void testSelectBy() {
    var users = userMapper.selectBy(new User().setUserName("TestUser002"));

    assertEquals(1, users.size());
    assertEquals(
        "test-user-002@example.com", users.stream().findFirst().map(User::getEmail).orElse(null));
  }

  @Test
  public void testDeleteBy() {
    var count = userMapper.deleteBy(new User().setUserName("TestUser008"));

    assertEquals(1, count);
  }

  @Test
  public void testUpdateByPrimaryKeyAndVersion() {
    var userId =
        userMapper.selectBy(new User().setUserName("TestUser001")).stream()
            .findFirst()
            .map(User::getUserId)
            .orElse(null);

    var count =
        userMapper.updateByPrimaryKeyAndVersion(
            new User().setUserId(userId).setVersion(0).setUserName("UpdatedUser001"));

    assertEquals(1, count);

    var updatedUser = userMapper.selectByPrimaryKey(userId);

    assertEquals("UpdatedUser001", updatedUser.getUserName());
    assertEquals(1, updatedUser.getVersion());
  }

  @Test
  public void testDeleteByPrimaryKeyAndVersion() {
    var userId =
        userMapper.selectBy(new User().setUserName("TestUser002")).stream()
            .findFirst()
            .map(User::getUserId)
            .orElse(null);

    var count =
        userMapper.deleteByPrimaryKeyAndVersion(
            new User().setUserId(userId).setVersion(0).setUserName("UpdatedUser002"));

    assertEquals(1, count);

    var deletedUser = userMapper.selectByPrimaryKey(userId);

    assertNull(deletedUser);
  }
}
