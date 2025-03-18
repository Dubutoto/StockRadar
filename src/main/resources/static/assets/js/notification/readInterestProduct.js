function readInterestProduct(page = 0) {
    axios.get('/notification/read', {
        params: { page: page }
    })
        .then(response => {
            console.log("관심 상품 데이터:", response.data);
            const pageData = response.data; // Page 객체 전체 (content, totalPages, number, first, last 등)
            const data = pageData.content;  // 실제 관심 상품 목록 배열

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
                // item.productUrl가 있으면 사용, 없으면 기본 링크 구성
                a.href = item.productUrl ? item.productUrl : `/product/productDetail?productId=${item.productId}`;
                a.textContent = item.productName;
                tdProductName.appendChild(a);
                tr.appendChild(tdProductName);

                // 재고 상태 td 생성
                const tdAvailability = document.createElement("td");
                tdAvailability.textContent = (item.availability > 0 ? "재고 있음" : "재고 없음");
                tr.appendChild(tdAvailability);

                // 삭제 버튼 td 생성 (추가 기능 구현 시 이벤트 핸들러 추가)
                const tdDelete = document.createElement("td");
                const btnDelete = document.createElement("button");
                btnDelete.textContent = "삭제";
                btnDelete.classList.add("btn", "btn-danger");
                // 클릭 시 deleteInterestProduct 함수를 호출하여 해당 상품 삭제
                btnDelete.addEventListener("click", function(event) {
                    event.preventDefault();
                    deleteInterestProduct(item.productId);
                });
                tdDelete.appendChild(btnDelete);
                tr.appendChild(tdDelete);

                // tbody에 tr 추가
                tbody.appendChild(tr);
            });

            // 페이지네이션 컨트롤 생성
            // HTML에 <div id="paginationContainer"></div> 가 존재해야 합니다.
            const paginationContainer = document.getElementById("paginationContainer");
            paginationContainer.innerHTML = ""; // 기존 페이지네이션 초기화

            const ul = document.createElement("ul");
            ul.className = "pagination justify-content-center";

            // 이전 버튼
            const liPrev = document.createElement("li");
            liPrev.className = "page-item" + (pageData.first ? " disabled" : "");
            const aPrev = document.createElement("a");
            aPrev.className = "page-link";
            aPrev.href = "#";
            aPrev.textContent = "이전";
            aPrev.addEventListener("click", function(event) {
                event.preventDefault();
                if (!pageData.first) {
                    readInterestProduct(pageData.number - 1);
                }
            });
            liPrev.appendChild(aPrev);
            ul.appendChild(liPrev);

            // 페이지 번호 버튼 생성
            for (let i = 0; i < pageData.totalPages; i++) {
                const liPage = document.createElement("li");
                liPage.className = "page-item" + (i === pageData.number ? " active" : "");
                const aPage = document.createElement("a");
                aPage.className = "page-link";
                aPage.href = "#";
                aPage.textContent = i + 1;
                aPage.addEventListener("click", function(event) {
                    event.preventDefault();
                    readInterestProduct(i);
                });
                liPage.appendChild(aPage);
                ul.appendChild(liPage);
            }

            // 다음 버튼
            const liNext = document.createElement("li");
            liNext.className = "page-item" + (pageData.last ? " disabled" : "");
            const aNext = document.createElement("a");
            aNext.className = "page-link";
            aNext.href = "#";
            aNext.textContent = "다음";
            aNext.addEventListener("click", function(event) {
                event.preventDefault();
                if (!pageData.last) {
                    readInterestProduct(pageData.number + 1);
                }
            });
            liNext.appendChild(aNext);
            ul.appendChild(liNext);

            paginationContainer.appendChild(ul);
        })
        .catch(error => {
            console.error("관심 상품 조회 에러:", error);
        });
}

function deleteInterestProduct(productId) {
    axios.post('/notification/delete', {
        productId: productId
    })
        .then(response => {
            console.log("관심 상품 삭제 및 알림 삭제 완료:", response.data);
            // 삭제 후, 목록을 다시 로드하여 화면 갱신
            readInterestProduct();
        })
        .catch(error => {
            console.error("관심 상품 삭제 에러:", error);
        });
}

function saveSettings(){
    const email = document.getElementById("emailNotification").checked;
    const sms = document.getElementById("smsNotification").checked;
    const discord = document.getElementById("discordNotification").checked;

    // Axios POST 요청으로 설정 전송
    axios.post('/notification/settings', {
        emailNotification: email,
        smsNotification: sms,
        discordNotification: discord
    })
        .then(response => {
            console.log("Settings saved successfully", response.data);
            // 성공 메시지 표시 등 추가 처리 가능
        })
        .catch(error => {
            console.error("Error saving settings", error);
        });
}

function readSettings(){

}

// 페이지 로딩 후 함수 호출
document.addEventListener('DOMContentLoaded', function() {
    readInterestProduct();
    readSettings();
});