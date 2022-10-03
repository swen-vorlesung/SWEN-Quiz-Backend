package de.doubleslash.quiz.engine.security.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PasswordEncoder {

  @Value("{quiz.security.salt}")
  private String salt;

  public Optional<String> encode(String password) {

    final var spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(UTF_8), 65536, 128);
    final SecretKeyFactory factory;
    try {
      factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    } catch (NoSuchAlgorithmException e) {
      log.error("NoSuchAlgorithmException", e);
      return Optional.empty();
    }

    byte[] hash = new byte[0];
    try {
      hash = factory.generateSecret(spec).getEncoded();
    } catch (InvalidKeySpecException e) {
      log.error("InvalidKeySpecException", e);
      return Optional.empty();
    }

    return Optional.of(Arrays.toString(hash));
  }
}
