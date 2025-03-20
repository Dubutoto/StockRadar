// customerInquiryDetail.js
document.addEventListener('DOMContentLoaded', function() {
    // URL에서 inquiryId 추출
    const pathSegments = window.location.pathname.split('/');
    const inquiryId = pathSegments[pathSegments.length - 1]; // 마지막 세그먼트가 ID

    if (inquiryId && !isNaN(inquiryId)) {
        // 폼에 inquiryId 설정
        document.getElementById('inquiryId').value = inquiryId;

        // API에서 문의 상세 정보 가져오기
        fetch(`/customerInquiryprocessing/api/detail/${inquiryId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('네트워크 응답이 올바르지 않습니다');
                }
                return response.json();
            })
            .then(data => {
                // 폼 필드 채우기
                document.getElementById('email').value = data.memberEmail || '';

                // 읽기 전용 textarea에 문의 내용 설정
                const inquiryContentElem = document.getElementById('inquiryContent');
                if (inquiryContentElem) {
                    inquiryContentElem.value = data.inquiryContent || '';
                }

                // 답변 제목을 표준 응답 제목으로 미리 채우기
                document.getElementById('processingTitle').value = `RE: ${data.inquiryTitle || '문의에 대한 답변'}`;
            })
            .catch(error => {
                console.error('문의 상세 정보를 가져오는 중 오류 발생:', error);
                alert('문의 정보를 불러오는 중 오류가 발생했습니다.');
            });

        // 폼 제출 이벤트 처리
        document.getElementById('inquiryProcessingForm').addEventListener('submit', function(e) {
            // 유효성 검사
            const title = document.getElementById('processingTitle').value.trim();
            const content = document.getElementById('processingContent').value.trim();

            if (!title || !content) {
                e.preventDefault();
                alert('제목과 내용을 모두 입력해주세요.');
                return;
            }

            // 제출 버튼 비활성화 (중복 제출 방지)
            document.getElementById('submitButton').disabled = true;
        });
    }
});
