function readInterestProduct() {
    axios.get('/notification/read')
        .then(response => {
            console.log("관심 상품 데이터:", response.data);
            // 응답 데이터가 배열이라고 가정
            const data = response.data.content;
            const tbody = document.querySelector("table.table-wishlist tbody");

            // 기존 tbody 내용 초기화
            tbody.innerHTML = "";

            data.forEach(item => {
                // <tr class="product-row"> 생성
                const tr = document.createElement("tr");
                tr.classList.add("product-row");

                // 카테고리 td 생성
                const tdCategory = document.createElement("td");
                tdCategory.textContent = item.category;
                tr.appendChild(tdCategory);

                // 상품명 td 생성 (상품 상세 페이지 링크 포함)
                const tdProductName = document.createElement("td");
                const a = document.createElement("a");
                // 필요에 따라 item.productUrl 또는 productId 기반 링크 구성
                a.href = item.productUrl ? item.productUrl : `/product/productDetail?productId=${item.productId}`;
                a.textContent = item.productName;
                tdProductName.appendChild(a);
                tr.appendChild(tdProductName);

                // 재고 상태 td 생성
                const tdAvailability = document.createElement("td");
                // availability가 0 이상이면 "재고 있음", 아니면 "재고 없음" 등으로 처리
                tdAvailability.textContent = (item.availability > 0 ? "재고 있음" : "재고 없음");
                tr.appendChild(tdAvailability);

                // 삭제 버튼 td 생성 (추가 기능 구현 시 이벤트 핸들러 추가)
                const tdDelete = document.createElement("td");
                const btnDelete = document.createElement("button");
                btnDelete.textContent = "삭제";
                btnDelete.classList.add("btn", "btn-danger");
                // 예: btnDelete.addEventListener("click", () => { /* 삭제 로직 */ });
                tdDelete.appendChild(btnDelete);
                tr.appendChild(tdDelete);

                // tbody에 tr 추가
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error("관심 상품 조회 에러:", error);
        });
}

// 페이지 로딩 후 함수 호출
readInterestProduct();
