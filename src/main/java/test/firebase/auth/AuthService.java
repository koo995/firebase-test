package test.firebase.auth;


import com.google.firebase.auth.FirebaseToken;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import test.firebase.member.Member;
import test.firebase.member.MemberRepository;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    private final ReentrantLock lock = new ReentrantLock();

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Member joinAndLogin(FirebaseToken decodedToken) throws InterruptedException {
        lock.lock();
        String uid = decodedToken.getUid();
        Thread.sleep(1000);
        Optional<Member> memberOptional = memberRepository.findByUid(uid);

        if (memberOptional.isPresent()) {
            log.info("이미 가입된 회원입니다.");
            Member member = memberOptional.get();
            lock.unlock();
            return member;
        } else {
            log.info("새로운 회원입니다.");
            Member newMember = Member.builder()
                    .username(decodedToken.getName())
                    .email(decodedToken.getEmail())
                    .uid(uid)
                    .build();
            Member member = memberRepository.save(newMember);
            lock.unlock();
            return member;
        }
    }


//        Member member = memberRepository.findByUid(uid)
//                .orElseGet(Member.builder()
//                        .username(decodedToken.getName())
//                        .email(decodedToken.getEmail())
//                        .uid(uid)::build);
//        memberRepository.save(member);
//        lock.unlock();
//        return member;

}
