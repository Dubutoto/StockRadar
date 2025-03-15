function readInterestProduct(){

    axios.get('/notification/read')
        .then(response =>{
            console.log("관심 상품 데이터:", response.data);
        })
        .catch(error => {
            console.error("관심 상품 조회 에러:", error);
        })
}

readInterestProduct();