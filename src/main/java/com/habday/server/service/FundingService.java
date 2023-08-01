package com.habday.server.service;

import com.google.gson.Gson;
import com.habday.server.classes.Calculation;
import com.habday.server.classes.Common;
import com.habday.server.classes.UIDCreation;
import com.habday.server.classes.implemented.ParticipatedList;
import com.habday.server.config.S3Uploader;
import com.habday.server.config.email.EmailFormats;
import com.habday.server.constants.CmnConst;
import com.habday.server.constants.state.FundingConfirmState;
import com.habday.server.constants.state.FundingState;
import com.habday.server.constants.state.ScheduledPayState;
import com.habday.server.domain.confirmation.Confirmation;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.fundingItem.FundingItemRepository;
import com.habday.server.domain.fundingMember.FundingMember;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.domain.payment.Payment;
import com.habday.server.dto.req.fund.ConfirmationRequest;
import com.habday.server.dto.req.fund.ParticipateFundingRequest;
import com.habday.server.dto.req.iamport.NoneAuthPayScheduleRequestDto;
import com.habday.server.dto.req.iamport.NoneAuthPayUnscheduleRequestDto;
import com.habday.server.dto.res.fund.GetListResponseDto;
import com.habday.server.dto.res.fund.ParticipateFundingResponseDto;
import com.habday.server.dto.res.fund.ShowConfirmationResponseDto;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto.FundingParticipantList;
import com.habday.server.dto.res.iamport.UnscheduleResponseDto;
import com.habday.server.exception.CustomException;
import com.habday.server.exception.CustomExceptionWithMessage;
import com.habday.server.interfaces.ListInterface;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static com.habday.server.constants.code.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingService extends Common {
    private final UIDCreation uidCreation;
    private final Calculation calculation;
    private final IamportService iamportService;
    private final S3Uploader s3Uploader;
    private final EmailFormats emailFormats;
    private final PayService payService;


    @Transactional//예외 발생 시 롤백해줌
    public ParticipateFundingResponseDto participateFunding(ParticipateFundingRequest fundingRequestDto, Long memberId) {
        String merchantUid = uidCreation.createMerchantUid(fundingRequestDto.getFundingItemId(), memberId);
        Payment selectedPayment = paymentRepository.findById(fundingRequestDto.getPaymentId()).
                orElseThrow(() -> new CustomException(NO_PAYMENT_EXIST));
        FundingItem fundingItem = fundingItemRepository.findById(fundingRequestDto.getFundingItemId())
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        //이미 성공 처리된 펀딩일 경우
        if(fundingItem.getStatus().equals(FundingState.SUCCESS))
            return ParticipateFundingResponseDto.of(-7, "이미 성공한 펀딩에는 참여할 수 없습니다.");

        if(selectedPayment.getMember().getId() != memberId)
            throw new CustomException(PAYMENT_VALIDATION_FAIL);

        Date scheduleDate = calculation.calPayDate(fundingItem.getFinishDate());//30분 더하기
        log.info("schedule date: " + scheduleDate);
        //아이앰포트에 스케쥴 걸기
        IamportResponse<List<Schedule>> scheduleResult = iamportService.noneAuthPaySchedule(
                NoneAuthPayScheduleRequestDto.of(fundingRequestDto, selectedPayment.getBillingKey(), merchantUid, scheduleDate));
        log.info("FundingService.participateFunding(): " + new Gson().toJson(scheduleResult));

        if (scheduleResult.getCode() != 0) {
            throw new CustomExceptionWithMessage(PAY_SCHEDULING_FAIL, scheduleResult.getMessage());
        }

        //펀딩 참여 정보 저장
        fundingMemberRepository.save(FundingMember.of(fundingRequestDto, ScheduledPayState.ready, merchantUid, selectedPayment.getBillingKey()
                , fundingItem, member));

        BigDecimal totalPrice = calculation.calTotalPrice(fundingRequestDto.getAmount(), fundingItem.getTotalPrice());
        int percentage = calculation.calFundingPercentage(totalPrice, fundingItem.getGoalPrice());

        //펀딩 아이템 누적 금액 update
        fundingItem.updatePricePercentage(totalPrice, percentage);

        // 펀딩 금액 달성시, SUCCESS로 상태 변경
        if(fundingItem.getTotalPrice() == fundingItem.getGoalPrice()) {
            fundingItem.updateFundingSuccess();
        }
        return ParticipateFundingResponseDto.of(scheduleResult.getCode(), scheduleResult.getMessage());
    }

    public ShowFundingContentResponseDto showFundingContent(Long fundingItemId) {
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Member member = Optional.ofNullable(fundingItem.getMember()).orElseThrow(()-> new CustomException(NO_MEMBER_ID_SAVED));
        Optional<Confirmation> confirmation = Optional.ofNullable(confirmationRepository.findByFundingItem(fundingItem));
        return ShowFundingContentResponseDto.of(fundingItem, member, getParticipantList(fundingItem),
                confirmation.orElseGet(null).getId());
    }

    public List<FundingParticipantList> getParticipantList(FundingItem fundingItem) {
        return fundingMemberRepository.findByFundingItem(fundingItem);
    }

    /*
     * 참여/호스팅 목록 조회 status 2가지{
     *   완료(FINISHED): SUCCESS, FAIL
     *   진행중(PROGRESS): PROGRESS
     * }
     * */

    public <T> GetListResponseDto getList(ListInterface listInterface, Long memberId, String status, Long pointId) {
        List<T> hostingLists;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));

        if (status == "PROGRESS") {
            hostingLists = listInterface.getProgressList(member, pointId, PageRequest.of(0, 10));
        } else {
            hostingLists = listInterface.getFinishedList(member, pointId, PageRequest.of(0, 10));
        }

        Long lastIdOfList = hostingLists.isEmpty() ? null : listInterface.getId();
        return new GetListResponseDto(hostingLists, hasNext(lastIdOfList));
    }

    public GetListResponseDto getParticipateList(Long memberId, Long pointId) {
        List<ParticipatedList.ParticipatedListInterface> participatedList;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        if (pointId == null)
            participatedList = fundingMemberRepository.getPagingListFirst(member, PageRequest.of(0, 10));
        else
            participatedList = fundingMemberRepository.getPagingListAfter(pointId, member, PageRequest.of(0, 10));

        Long lastIdOfList = participatedList.isEmpty() ? null : participatedList.get(participatedList.size() - 1).getFundingMemberId();
        return new GetListResponseDto(participatedList, hasNext_p(lastIdOfList));
    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        log.info("hasNext: " + id + " " + fundingItemRepository.existsByIdLessThan(id));
        return fundingItemRepository.existsByIdLessThan(id);
    }

    private Boolean hasNext_p(Long id) {
        if (id == null) return false;
        log.info("hasNext: " + id + " " + fundingMemberRepository.existsByIdLessThan(id));
        return fundingItemRepository.existsByIdLessThan(id);
    }

    @Transactional
    public void confirm(MultipartFile img, ConfirmationRequest request, Long fundingItemId, Long memberId) {
        String fundingItemImgUrl;
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId).orElseThrow(
                () -> new CustomException(NO_FUNDING_ITEM_ID)
        );
        if (fundingItem.getIsConfirm().equals(FundingConfirmState.TRUE)){
            throw new CustomException(FUNDING_ALREADY_CONFIRMED);
        }

        if (!fundingItem.getStatus().equals(FundingState.SUCCESS)){
            log.info("confirm(): 아이템의 status가 success가 아님" + fundingItem.getStatus());
            throw new CustomException(FUNDING_CONFIRM_NOT_NEEDED);
        }

        if (fundingItem.getFinishDate().compareTo(LocalDate.now()) > 0){//now < finishDate
            log.info("confirm(): 아직 진행중인 펀딩임." + fundingItem.getFinishDate());
            throw new CustomException(FUNDING_CONFIRM_NOT_YET);
        }//fundingItemStatus는 SUCCESS이지만 아직 진행중인 경우

        if (calculation.isAfterTwoWeek(fundingItem)){//afterTwoWeek >= LocalDate.now()이면 인증 가능
            log.info("confirm(): 펀딩 인증 2주 지남");
            throw new CustomException(FUNDING_CONFIRM_EXCEEDED);
        }
        log.info("confirm(): 펀딩 인증 2주 이내 ");

        //S3 저장
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(NO_MEMBER_ID));
        log.info("confirm(): request: 2" + request.getMessage());
        try {
            fundingItemImgUrl = s3Uploader.upload(img, "images");
        } catch (IOException e) {
            throw new CustomException(FAIL_UPLOADING_IMG);
        }
        //db 저장
        confirmationRepository.save(Confirmation.builder()
                        .confirmationImg(fundingItemImgUrl)
                        .request(request)
                        .fundingItem(fundingItem)
                        .member(member)
                .build());
        //이메일 보내기
        emailFormats.sendFundingConfirmEmail(fundingItem);
        //펀딩 인증 여부 update
        fundingItem.updateIsConfirmTrue();
    }

    public ShowConfirmationResponseDto showConfirmation(Long fundingItemId){
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId).orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));
        Confirmation confirmation = Optional.ofNullable(confirmationRepository.findByFundingItem(fundingItem)).orElseThrow(() -> new CustomException(NO_CONFIRMATION_EXIST));
        return new ShowConfirmationResponseDto(confirmation, fundingItem.getTotalPrice());
    }

    @Transactional
    public void updateFundingItem(Long fundingItemId, MultipartFile fundingItemImg, String fundingItemName, String fundingItemDetail) throws IOException {
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));

        String updateFundingItemImgUrl = (fundingItemImg.isEmpty()) ? fundingItem.getFundingItemImg() : s3Uploader.upload(fundingItemImg, "images");
        String updateFundingName = (fundingItemName == null) ? fundingItem.getFundingName() : fundingItemName;
        String updateFundDetail = (fundingItemDetail == null) ? fundingItem.getFundDetail() : fundingItemDetail;

        fundingItem.update(updateFundingItemImgUrl, updateFundingName, updateFundDetail);
    }


    @Transactional
    public void deleteFundingItem(Long fundingItemId) {
        FundingItem fundingItem = fundingItemRepository.findById(fundingItemId)
                .orElseThrow(() -> new CustomException(NO_FUNDING_ITEM_ID));

        if(LocalDate.now().compareTo(fundingItem.getFinishDate())>=0){
            throw new CustomException(DELETE_FUNDING_UNAVAILABLE);
        }
        fundingItemRepository.delete(fundingItem);
        payService.unscheduleAll(fundingItem);
        emailFormats.sendFundingCanceledEmail(fundingItem);

        //연관된 컬럼 null처리
        List<FundingMember> fundingMembers = fundingMemberRepository.getFundingMemberMatchesFundingItem(fundingItem);
        if (!fundingMembers.isEmpty()){
            fundingMembers.forEach((fundingMember)->{//fundingItem을 삭제해도 관련된 데이터 남겨둘거임.
                fundingMember.updateFundingItemNull();
            });
        }

        Confirmation confirmation = confirmationRepository.findByFundingItem(fundingItem);
        if (confirmation != null)
            confirmation.updateFundingItemNull();
    }

    public UnscheduleResponseDto cancel(Long memberId, NoneAuthPayUnscheduleRequestDto request){
        FundingMember fundingMember = fundingMemberRepository.findById(request.getFundingMemberId()).orElseThrow(() -> new CustomException(NO_FUNDING_MEMBER_ID));
        if(memberId != fundingMember.getMember().getId())
            throw new CustomException(FUNDING_MEMBER_VALIDATION_FAIL);
        return payService.noneAuthPayUnschedule(request);

    }

    public Long getBirthdayLeft(Member member) {
        String[] birthList = member.getBirthday().split("-");

        Integer birthMonth = Integer.parseInt(birthList[1]);
        Integer birthDay = Integer.parseInt(birthList[2]);

        Calendar curCal = Calendar.getInstance();
        Calendar birthCal = Calendar.getInstance();
        birthCal.set(curCal.get(Calendar.YEAR), birthMonth-1, birthDay,0,0,0);

        Long birthTime = birthCal.getTime().getTime();
        Long currTime = curCal.getTime().getTime();
        long span = 0;

        if(birthTime < currTime) { // 생일이 이미 지난 경우
            birthCal.set(Calendar.YEAR, curCal.get(Calendar.YEAR)+1);
            birthTime = birthCal.getTime().getTime();
        }

        span = birthTime - currTime;
        return span/1000/60/60/24L;
    }
}
