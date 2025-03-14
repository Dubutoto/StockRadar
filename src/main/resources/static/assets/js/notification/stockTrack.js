function trackProduct(productId) {
    axios.post('/notification/register', {
        productId: productId
    })
        .then(response => {
            console.log("관심 상품 등록 및 알림 전송 완료:", response.data);
        })
        .catch(error => {
            if (error.response && error.response.status === 401) {
                // 인증 실패인 경우, 에러 메시지(예: "로그인 해주세요.")를 alert로 보여주거나 로그인 페이지로 리다이렉트할 수 있습니다.
                alert(error.response.data);
            } else {
                console.error("관심 상품 등록 중 에러 발생:", error);
            }
        });
}
