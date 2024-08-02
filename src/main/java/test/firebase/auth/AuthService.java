package test.firebase.auth;


import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.firebase.member.Member;
import test.firebase.member.MemberRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    public Member joinAndLogin(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        Optional<Member> memberOptional = memberRepository.findByUid(uid);
        if (memberOptional.isPresent()) {
            log.info("기존 회원입니다.");
            return memberOptional.get();
        }
        log.info("새로운 회원입니다.");
        Member member = Member.builder()
                .username(decodedToken.getName())
                .email(decodedToken.getEmail())
                .uid(uid).build();
        memberRepository.save(member);
        return member;
    }
}

