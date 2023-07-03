package com.habday.server.domain.fundingItem;

import com.habday.server.classes.implemented.HostedList.HostedListDto;
import com.habday.server.constants.FundingState;
import com.habday.server.domain.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FundingItemRepository extends JpaRepository<FundingItem, Long> {
    //status = PROGRESS
    List<HostedListDto> findByStatusAndMemberOrderByIdDesc(FundingState status, Member member, Pageable page);
    List<HostedListDto> findByIdLessThanAndStatusAndMemberOrderByIdDesc(Long id, FundingState status, Member member, Pageable page);

    //status = SUCCESS || FAIL
    List<HostedListDto> findByStatusNotAndMemberOrderByIdDesc(FundingState status, Member member, Pageable page);
    List<HostedListDto> findByIdLessThanAndStatusNotAndMemberOrderByIdDesc(Long id, FundingState status, Member member, Pageable page);
    Boolean existsByIdLessThan(Long id);

    /*
     * List<FundingItem> overdatedFundings = select * from FundingItem
     * where status == "PROGRESS"
     * and finishDate == "오늘"
     * */
    List<FundingItem> findByStatusAndFinishDate(FundingState status, LocalDate finishDate);

    /*
    * select * from funding_item
    * where id < 이전id
    * and status = "PROGRESS"
    * and member_id = memberId
    * order by id desc
    * limit 10;
    * */
}