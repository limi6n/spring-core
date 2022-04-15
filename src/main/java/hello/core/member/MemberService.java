package hello.core.member;

public interface MemberService {

    /**
     * 회원가입
     * @param member
     */
    void join(Member member);

    /**
     * 회원조회
     * @param memberId
     * @return
     */
    Member findMember(Long memberId);
}
