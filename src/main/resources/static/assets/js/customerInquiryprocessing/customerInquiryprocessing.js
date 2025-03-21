document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로드 시 고객문의 목록 가져오기
    fetchCustomerInquiryList();

    function fetchCustomerInquiryList() {
        fetch('/customerInquiryprocessing/api/customerInquiryprocessing')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                displayCustomerInquiries(data);
            })
            .catch(error => {
                console.error('고객문의 데이터 가져오기 실패:', error);
                alert(error.message);
                window.location.href = '/main'; // 메인 페이지로 리다이렉트
            });
    }

    function displayCustomerInquiries(inquiries) {
        const tableBody = document.querySelector('.inquiry-list table tbody');
        tableBody.innerHTML = ''; // 기존 내용 초기화

        if (inquiries.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = '<td colspan="4" class="text-center">등록된 문의가 없습니다.</td>';
            tableBody.appendChild(emptyRow);
            return;
        }

        inquiries.forEach(inquiry => {
            const row = document.createElement('tr');

            // 카테고리 텍스트 변환
            let categoryText = '';
            switch(inquiry.inquiryCategory) {
                case 1: categoryText = '상품문의'; break;
                case 2: categoryText = '배송문의'; break;
                case 3: categoryText = '결제문의'; break;
                case 4: categoryText = '기타문의'; break;
                default: categoryText = '기타';
            }

            // 날짜 포맷팅
            const createdDate = new Date(inquiry.createdAt);
            const formattedDate = `${createdDate.getFullYear()}-${String(createdDate.getMonth() + 1).padStart(2, '0')}-${String(createdDate.getDate()).padStart(2, '0')} ${String(createdDate.getHours()).padStart(2, '0')}:${String(createdDate.getMinutes()).padStart(2, '0')}`;

            // 행 내용 설정
            row.innerHTML = `
                <td><a href="#" class="inquiry-title" data-id="${inquiry.inquiryId}">${inquiry.inquiryTitle}</a></td>
                <td>${categoryText}</td>
                <td>${inquiry.memberEmail}</td>
                <td>${formattedDate}</td>
            `;

            // 클릭 이벤트 추가
            const titleLink = row.querySelector('.inquiry-title');
            titleLink.addEventListener('click', function(e) {
                e.preventDefault();
                const inquiryId = this.getAttribute('data-id');
                window.location.href = `/customerInquiryprocessing/detail/${inquiryId}`;
            });

            tableBody.appendChild(row);
        });
    }
});
