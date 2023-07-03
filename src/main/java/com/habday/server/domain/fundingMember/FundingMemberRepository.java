package com.habday.server.domain.fundingMember;

import com.habday.server.constants.FundingState;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import com.habday.server.classes.implemented.ParticipatedList.ParticipatedListInterface;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto.FundingParticipantList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FundingMemberRepository extends JpaRepository<FundingMember, Long> {
    Long countByFundingItemIdAndMemberId(Long id, Long memberId);
    FundingMember findByMerchantId(String merchant_id);
    List<FundingParticipantList> findByFundingItem(FundingItem fundingItem);
    @Query("select fm from FundingMember fm where fm.fundingItem = :fundingItem")
    List<FundingMember> good(FundingItem fundingItem);
    //List<FundingMember> findByFundingItem(FundingItem fundingItem);
    /*
    select * from funding_member m inner join funding_item i on m.funding_item_id = i.funding_item_id
    where m.member_id = 멤버id and m.funding_item_id = 펀딩아이템id
    */

    /*
    * <가져올 데이터>
    * //fundingItemId
    * //fundingMemberId(주문 기록이랑 매칭됨)
    * 펀딩이름
    * 펀딩 생성자 이름
    * 내가 펀딩한 금액
    * 펀딩 아이템 이미지
    * //전체 금액
    * //펀딩 끝
    * //펀딩 시작
    * //펀딩 참여 날짜(피그마에 없음)
    * //(merchantId)상품번호
    * //(payment_status)결제 상태
    * //(status) 펀딩 상태
    * */

    /*
    * 참여한 펀딩 리스트 가져오기
    * status = PROGRESS
    * 페이지네이션 = 1
    * */
    //@Query("select m.id as fundingMemberId, i.fundingItemImg as fundingItemImg, i.fundingName as fundingName, i.totalPrice as totalPrice, i.startDate as startDate, i.finishDate as finishDate, i.status as status, m.fundingDate as fundingDate, m.payment_status as payment_status, m.merchantId as merchantId from FundingMember m join m.fundingItem i where m.member = :member and i.status = :status order by m.id desc")
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.member = :member and i.status = :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListFirst_Progress(Member member, FundingState status, Pageable page);

    /*
     * 참여한 펀딩 리스트 가져오기
     * status = PROGRESS
     * 페이지네이션 > 1
     * */
    //@Query("select m.id as fundingMemberId, i.fundingItemImg as fundingItemImg, i.fundingName as fundingName, i.totalPrice as totalPrice, i.startDate as startDate, i.finishDate as finishDate, i.status as status, m.fundingDate as fundingDate, m.payment_status as payment_status, m.merchantId as merchantId from FundingMember m join m.fundingItem i where m.id < :id and m.member = :member and i.status = :status order by m.id desc")
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.id < :id and fm.member = :member and i.status = :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListAfter_Progress(Long id, Member member, FundingState status, Pageable page);

    /*
     * 참여한 펀딩 리스트 가져오기
     * status = SUCCESS || FAIL(이미 끝난 펀딩)
     * 페이지네이션 = 1
     * */
    //@Query("select m.id as fundingMemberId, i.fundingItemImg as fundingItemImg, i.fundingName as fundingName, i.totalPrice as totalPrice, i.startDate as startDate, i.finishDate as finishDate, i.status as status, m.fundingDate as fundingDate, m.payment_status as payment_status, m.merchantId as merchantId from FundingMember m join m.fundingItem i where m.member = :member and i.status <> :status order by m.id desc")
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.member = :member and i.status <> :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListFirst_Finished(Member member, FundingState status, Pageable page);

    /*
     * 참여한 펀딩 리스트 가져오기
     * status = SUCCESS || FAIL(이미 끝난 펀딩)
     * 페이지네이션 > 1
     * */
    //@Query("select m.id as fundingMemberId, i.fundingItemImg as fundingItemImg, i.fundingName as fundingName, i.totalPrice as totalPrice, i.startDate as startDate, i.finishDate as finishDate, i.status as status, m.fundingDate as fundingDate, m.payment_status as payment_status, m.merchantId as merchantId from FundingMember m join m.fundingItem i where m.id < :id and m.member = :member and i.status <> :status order by m.id desc")
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.id < :id and fm.member = :member and i.status <> :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListAfter_Finished(Long id, Member member, FundingState status, Pageable page);
}
