package de.doubleslash.quiz.engine.security;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class JWTTokenService implements Clock, TokenService {

  private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

  private final String issuer;

  private final String secretKey;

  @Autowired
  public JWTTokenService(
      @Value("${quiz.jwt.secret-key}") String secret,
      @Value("${quiz.jwt.issuer}") String issuer) {

    super();
    this.secretKey = BASE64.encode(secret);
    this.issuer = issuer;
  }

  private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
    try {
      final var claims = toClaims.get();
      final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      for (final Map.Entry<String, Object> e : claims.entrySet()) {
        builder.put(e.getKey(), String.valueOf(e.getValue()));
      }
      return builder.build();
    } catch (final IllegalArgumentException | JwtException e) {
      return Map.of();
    }
  }

  public String newToken(final Map<String, String> attributes) {

    final var claims = Jwts.claims()
        .setIssuer(issuer)
        .setIssuedAt(now());

    claims.putAll(attributes);

    return Jwts.builder()
        .setClaims(claims)
        .signWith(HS256, secretKey)
        .compressWith(COMPRESSION_CODEC)
        .compact();
  }

  @Override
  public Map<String, String> verify(final String token) {

    final var parser = Jwts.parser()
        .requireIssuer(issuer)
        .setClock(this)
        .setSigningKey(secretKey);
    return parseClaims(() -> parser.parseClaimsJws(token)
        .getBody());
  }

  @Override
  public Date now() {
    return DateTime.now().toDate();
  }
}
