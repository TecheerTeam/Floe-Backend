package project.floe.global.auth.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.floe.domain.user.entity.User;
import project.floe.domain.user.entity.UserRole;
import project.floe.domain.user.repository.UserRepository;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.UserServiceException;

@Service
@Getter
@Slf4j
@Transactional
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String AUTH_CLAIM = "auth";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final Key hashedSecretKey;

    public JwtService(UserRepository userRepository, @Value("${jwt.secretKey}") String secretKey) {
        this.userRepository = userRepository;
        this.hashedSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String createAccessToken(String email){
        User accessUser = userRepository.findByEmail(email).orElseThrow(
                () -> new UserServiceException(ErrorCode.USER_NOT_FOUND_ERROR)
        );
        UserRole userRole = accessUser.getRole();
        // 클레임에 sub, email, auth(role) 삽입
        Claims claims = Jwts.claims().setSubject(ACCESS_TOKEN_SUBJECT);
        claims.put(EMAIL_CLAIM, String.valueOf(email));
        claims.put(AUTH_CLAIM, userRole.name());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .signWith(hashedSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        // 클레임에 sub만 삽입
        Claims claims = Jwts.claims().setSubject(REFRESH_TOKEN_SUBJECT);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .signWith(hashedSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * AccessToken 헤더에 실어서 보내기
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken + RefreshToken 헤더에 실어서 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractEmail(HttpServletRequest request) {
        return extractAccessToken(request)
                .flatMap(this::extractEmail);
    }

    public Optional<String> extractEmail(String accessToken) {
        try {
            // 토큰의 클레임 파싱
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(hashedSecretKey) // 시크릿 키 설정
                    .build()
                    .parseClaimsJws(accessToken) // 토큰 파싱
                    .getBody();

            // "email" 클레임 가져오기
            String email = claims.get(EMAIL_CLAIM, String.class);

            return Optional.ofNullable(email);
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(hashedSecretKey).build().parseClaimsJws(token).getBody()
                    .getSubject();
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
