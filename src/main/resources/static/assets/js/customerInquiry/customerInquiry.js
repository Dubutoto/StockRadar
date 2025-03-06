document.addEventListener('DOMContentLoaded', async function() {
    try {
        const response = await fetch('/customerInquiry/api/check', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include' // 쿠키 포함 (인증 정보)
        });

        // 응답 본문을 JSON으로 파싱
        const data = await response.json();
        console.log('서버 응답:', data); // 디버깅용 로그 추가

        // 응답 상태 코드에 따른 처리
        if (response.ok) {
            // 인증 성공 (200 OK)
            console.log('인증 성공:', data.message);
        } else {
            // 인증 실패 (4xx 오류)
            console.error('인증 실패:', data.message);

            // 힌트가 있으면 표시
            if (data.hint) {
                alert(data.hint);
            } else {
                alert(data.message || '인증에 실패했습니다.');
            }

            // 리다이렉트 URL이 있으면 해당 페이지로 이동
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                window.location.href = '/login'; // 기본 리다이렉트 URL
            }
        }
    } catch (error) {
        console.error('서버 연결 오류:', error);
        alert('서버 연결 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
        window.location.href = '/login';
    }
});

document.getElementById('submitButton').addEventListener('click', function(event) {
    event.preventDefault(); // 폼 기본 제출 동작 방지

    const email = document.getElementById('email').value;
    const title = document.getElementById('title').value;
    const category = document.getElementById('category').value;
    const content = document.getElementById('content').value;

    if (!email || !title || !category || !content) {
        alert('모든 필드를 입력해주세요.');
        return;
    }

    submitInquiry(email, title, category, content);
});

// 함수를 이벤트 핸들러 밖으로 이동
async function submitInquiry(email, title, category, content) {
    try {
        const response = await fetch('/customerInquiry/submit', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include', // 쿠키 포함 필수
            body: JSON.stringify({ email, title, category, content })
        });

        const data = await response.json();

        if (response.ok) {
            alert(data.message || '문의가 성공적으로 접수되었습니다.');
            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            }
        } else {
            // 서버에서 보내준 message와 hint를 모두 활용하여 명확히 표시
            let errorMsg = data.hint || data.message || '문의 접수 중 오류가 발생했습니다.';
            alert(hint);

            if (data.redirectUrl) {
                window.location.href = data.redirectUrl;
            } else {
                window.location.href = '/login';
            }
        }
    } catch (error) {
        console.error('Error:', error);
        alert('서버 연결 중 오류가 발생했습니다.');
        window.location.href = '/login';
    }
}

