package com.habday.server.domain.fundingMember;

import com.habday.server.constants.state.FundingState;
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
    @Query("select fm.id from FundingMember fm where fm.fundingItem = :fundingItem")
    List<Long> getFundingItemIdMatchesFundingItem(FundingItem fundingItem);

    @Query("select m.email from FundingMember fm join fm.member m where fm.fundingItem = :fundingItem")
    List<String> getMailList(FundingItem fundingItem);
    //List<FundingMember> findByFundingItem(FundingItem fundingItem);
    /*
    select * from funding_member m inner join funding_item i on m.funding_item_id = i.funding_item_id
    where m.member_id = 멤버id and m.funding_item_id = 펀딩아이템id
    */

    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.member = :member order by fm.id desc")
    List<ParticipatedListInterface> getPagingListFirst(Member member, Pageable page);

    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.id < :id and fm.member = :member order by fm.id desc")
    List<ParticipatedListInterface> getPagingListAfter(Long id, Member member, Pageable page);

    /*
    * 참여한 펀딩 리스트 가져오기
    * status = PROGRESS
    * 페이지네이션 = 1
    * */
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.member = :member and i.status = :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListFirst_Progress(Member member, FundingState status, Pageable page);

    /*
     * 참여한 펀딩 리스트 가져오기
     * status = PROGRESS
     * 페이지네이션 > 1
     * */
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.id < :id and fm.member = :member and i.status = :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListAfter_Progress(Long id, Member member, FundingState status, Pageable page);

    /*
     * 참여한 펀딩 리스트 가져오기
     * status = SUCCESS || FAIL(이미 끝난 펀딩)
     * 페이지네이션 = 1
     * */
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.member = :member and i.status <> :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListFirst_Finished(Member member, FundingState status, Pageable page);

    /*
     * 참여한 펀딩 리스트 가져오기
     * status = SUCCESS || FAIL(이미 끝난 펀딩)
     * 페이지네이션 > 1
     * */
    @Query("select fm.id as fundingMemberId, i.fundingName as fundingName, m.name as creatorName, fm.amount as fundingAmount, i.fundingItemImg as fundingItemImg,  i.status as fundingStatus, fm.fundingDate as fundingDate, fm.payment_status as payment_status, fm.merchantId as merchantId from FundingMember fm join fm.fundingItem i join fm.member m where fm.id < :id and fm.member = :member and i.status <> :status order by fm.id desc")
    List<ParticipatedListInterface> getPagingListAfter_Finished(Long id, Member member, FundingState status, Pageable page);
}
