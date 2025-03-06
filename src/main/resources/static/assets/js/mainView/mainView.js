document.addEventListener('DOMContentLoaded', async function() {
    try {
        const response = await fetch('/main/check', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();
        console.log('서버 응답:', data);

        updateUIBasedOnRole(data.role);
    } catch (error) {
        console.error('Error:', error);
        updateUIBasedOnRole('GUEST');
    }
});

function updateUIBasedOnRole(role) {
    const customerInquiryLinks = document.querySelectorAll('.customer-inquiry-link');

    customerInquiryLinks.forEach(link => {
        if (role === 'ADMIN') {
            link.textContent = '고객문의 처리';
            link.href = '/customerInquiryprocessing/customerInquiryprocessing';
        } else {
            link.textContent = '고객문의';
            link.href = '/customerInquiry/customerInquiry';
        }
    });

    const adminMenuItems = document.querySelectorAll('.admin-only');
    const memberMenuItems = document.querySelectorAll('.member-only');
    const guestMenuItems = document.querySelectorAll('.guest-only');

    adminMenuItems.forEach(item => item.style.display = role === 'ADMIN' ? 'block' : 'none');
    memberMenuItems.forEach(item => item.style.display = role === 'MEMBER' ? 'block' : 'none');
    guestMenuItems.forEach(item => item.style.display = (role === 'GUEST' || !role) ? 'block' : 'none');
}

