document.addEventListener('DOMContentLoaded', function () {
    // RTX 4060Ti 제품 정보를 가져오는 함수
    function fetchRtx4060TiProducts() {
        fetch('/product/api/gpu/rtx4060Ti')
            .then(response => {
                // 응답 상태 코드 확인
                if (!response.ok) {
                    // 에러 응답을 JSON으로 파싱
                    return response.json().then(errorData => {
                        throw errorData; // 에러 데이터를 다음 catch 블록으로 전달
                    });
                }
                return response.json();
            })
            .then(responseData => {
                if (responseData && responseData.status === 200 && responseData.data) {
                    // 제품 데이터를 테이블에 표시
                    displayProducts(responseData.data);
                } else {
                    // 오류 메시지 표시
                    showErrorMessage(responseData.message || '제품 정보를 불러올 수 없습니다.');
                }
            })
            .catch(error => {
                    console.error('데이터 가져오기 오류:', error);

                    // ErrorResponse 객체 처리
                    if (error && error.message) {
                        // 에러 메시지를 alert로 표시
                        alert(error.message);

                        // 하드코딩된 URL로 리다이렉트
                        window.location.href = 'product/productCategory';
                    } else {
                        showErrorMessage('제품 정보를 불러오는 중 오류가 발생했습니다.');
                    }
                }
            )}


    // 제품 데이터를 테이블에 표시하는 함수
    function displayProducts(products) {
        const tableBody = document.querySelector('.product-list table tbody');
        if (!tableBody) {
            console.error('테이블 본문을 찾을 수 없습니다.');
            return;
        }

        // 테이블 내용 초기화
        tableBody.innerHTML = '';

        // 각 제품에 대한 행 추가
        products.forEach(product => {
            const row = document.createElement('tr');

            // 제품명 셀 - 클릭 가능한 링크로 변경
            const nameCell = document.createElement('td');
            if (product.productUrl) {
                const nameLink = document.createElement('a');
                nameLink.href = product.productUrl;
                nameLink.textContent = product.productName || '제품명 없음';
                nameLink.target = "_blank"; // 새 탭에서 열기
                nameLink.style.textDecoration = "none"; // 밑줄 제거
                nameLink.style.color = "#007bff"; // 링크 색상
                nameLink.style.cursor = "pointer"; // 커서 스타일
                nameCell.appendChild(nameLink);
            } else {
                nameCell.textContent = product.productName || '제품명 없음';
            }
            row.appendChild(nameCell);

            // 재고 상태 셀
            const statusCell = document.createElement('td');
            const isInStock = product.availability > 0;
            statusCell.textContent = isInStock ? 'In Stock' : 'Out of Stock';
            statusCell.style.color = isInStock ? 'green' : 'red';
            row.appendChild(statusCell);

            // 가격 셀
            const priceCell = document.createElement('td');
            const price = product.price;
            let formattedPrice;

            if (price < 10000) {
                formattedPrice = '$' + price;
            } else {
                formattedPrice = '₩' + price;
            }

            priceCell.innerHTML = `<strong>${formattedPrice}</strong>`;
            row.appendChild(priceCell);

            // 알림 버튼 셀
            const buttonCell = document.createElement('td');
            buttonCell.innerHTML = `
                <button type="button" class="btn btn-md btn-primary btn-ellipse" 
                        onclick="trackProduct('${product.productName}')">
                    <i class="icon-bell"></i> Track
                </button>
            `;
            row.appendChild(buttonCell);

            // 행을 테이블에 추가
            tableBody.appendChild(row);
        });

        // 새로고침 시간 업데이트
        updateRefreshTime();
    }

    // 오류 메시지를 표시하는 함수
    function showErrorMessage(message) {
        const tableBody = document.querySelector('.product-list table tbody');
        if (tableBody) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-danger">
                        <i class="icon-exclamation-triangle"></i> ${message}
                    </td>
                </tr>
            `;
        }
    }

    // 새로고침 시간을 업데이트하는 함수
    function updateRefreshTime() {
        const refreshInfo = document.querySelector('.refresh-info');
        if (refreshInfo) {
            const now = new Date();
            const timeString = now.toLocaleTimeString('ko-KR');
            refreshInfo.textContent = `마지막 업데이트: ${timeString}`;
        }
    }

    // 제품 추적 기능 (버튼 클릭 시 호출)
    window.trackProduct = function (productName) {
        console.log(`제품 추적 시작: ${productName}`);
        alert(`${productName} 제품 재고 알림을 설정했습니다.`);
        // 여기에 추적 기능 구현 (서버에 요청 등)
    };

    // 페이지 로드 시 제품 정보 가져오기
    fetchRtx4060TiProducts();

    // 새로고침 버튼 이벤트 리스너 추가 (필요한 경우)
    const refreshButton = document.querySelector('.refresh-info');
    if (refreshButton) {
        refreshButton.addEventListener('click', fetchRtx4060TiProducts);
    }
});
