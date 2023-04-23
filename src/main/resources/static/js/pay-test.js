    const priceAmount = 5000
    const buyerMemberEmail = "yeonj630@gmail.com"
    const buyerMemberName = "testbuyername1"

    console.log(`priceAmount: ${priceAmount} buyerMemberName: ${buyerMemberName} buyerMemberEmail: ${buyerMemberEmail}`);

    const IMP = window.IMP;
    IMP.init('imp04005235');

//인증결제
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
                    url: `/verifyIamport/authpay/${rsp.imp_uid}`,
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

//정기결제나 비인증결제
//추후에는 customer_uid만 있으면 결제 가능
function nonConfirmPay(){
    const merchant_uid_made = 'fund_' + new Date().getTime()
    const name_made = "테스트 결제"
    const customer_uid_made = "testbuyername_0001_0001"
    const schedule_at_made = new Date(2023, 4, 23, 13, 46, 0)
    IMP.request_pay({ // param
        customer_uid: customer_uid_made,//고유한 값이어야 함(사용자 이메일_카드인지다른거인지_번호)
        pg: "kakaopay.TC0ONETIME", //상점ID
        //pay_method: "card",
        merchant_uid: merchant_uid_made, //주문번호(고유하게 채번해서 db에 저장하기
        name: name_made,  //주문이름
        amount: 1,
        /*buyer_email: buyerMemberEmail,
        buyer_name: buyerMemberName,
        buyer_tel:"010-1234-5678",
        buyer_addr: "서울특별시 강남구 신사동",
        buyer_postcode: "01111"*/
    }, rsp=> {
        if (rsp.success) {//빌링키 발급됨
            console.log(rsp);
            alert(`빌링키 발급 성공!`);
            axios({
                url: `/verifyIamport/noneauthpay/schedule`,
                method: "post",
                headers: { "Content-Type": "application/json" },
                data: {
                    customer_uid: customer_uid_made,
                    merchant_uid: merchant_uid_made, //주문번호(고유하게 채번해서 db에 저장하기)
                    schedule_at: schedule_at_made,
                    amount: priceAmount,
                    name: name_made,
                    buyer_name: buyerMemberName,
                    buyer_tel: "010-1234-5678",
                    buyer_email: buyerMemberEmail ,
                    //merchant_uid: rsp.merchant_uid
                }
            }).then((res) => {
                console.log(`아임포트 비인증결제 완료: ${JSON.stringify(res)}`);
            });
        }else{
            alert(`비인증결제 실패: respone => ${JSON.stringify(rsp)}`);
        }
    });
};
