package test.firebase.auth;


import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.firebase.member.Member;
import test.firebase.member.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member JoinAndLogin(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        Member member = memberRepository.findByUid(uid)
                .orElseGet(Member.builder()
                        .username(decodedToken.getName())
                        .email(decodedToken.getEmail())
                        .uid(uid)::build);
        memberRepository.save(member);
        return member;
    }
}
