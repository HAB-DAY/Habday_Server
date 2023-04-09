    const priceAmount = 5000
    const buyerMemberEmail = "yeonj630@gmail.com"
    const buyerMemberName = "testbuyername1"

    console.log(`priceAmount: ${priceAmount} buyerMemberName: ${buyerMemberName} buyerMemberEmail: ${buyerMemberEmail}`);

    const IMP = window.IMP;
    IMP.init('imp04005235');

    function requestPay() {
        // IMP.request_pay(param, callback) 결제창 호출
        IMP.request_pay({ // param
            pg: "kakaopay.TC0ONETIME", //상점ID
            pay_method: "card",
            merchant_uid: 'fund_' + new Date().getTime(), //주문번호(고유하게 채번해서 db에 저장하기
            name: "테스트 결제",  //주문이름
            amount: priceAmount,
            buyer_email: buyerMemberEmail,
            buyer_name: buyerMemberName,
            buyer_tel:"010-1234-5678",
            buyer_addr: "서울특별시 강남구 신사동",
            buyer_postcode: "01111"

        }, rsp => { // callback
            console.log(`rsp: ${JSON.stringify(rsp)}`);
             if (rsp.success) {
                  axios({
                    url: `/verifyIamport/${rsp.imp_uid}`,
                    method: "post",
                    headers: { "Content-Type": "application/json" },
                    data: {
                      imp_uid: rsp.imp_uid,
                      merchant_uid: rsp.merchant_uid
                    }
                  }).then((res) => {
                    console.log(`res: ${JSON.stringify(res)}`);
                    if(rsp.paid_amount === res.data.response.amount){
                        alert("결제가 완료되었습니다.");
                    } else{
                        alert(`금액이 일치하지 않습니다. 에러코드 : ${rsp.error_code} 에러 메시지 : ${rsp.error_message}`);
                    }
                  })
             } else {
                  alert(`결제에 실패하였습니다. 에러 내용: ${rsp.error_msg}`);
             }
        });
    };