import { createApp, ref, onMounted } from 'vue';

const writeApp = {
    setup() {
        const postWriteAppElement = document.getElementById('post-write-app');
        
        // CSRF 토큰 가져오기
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        
        // 게시판 설정 및 사용자 정보 파싱
        const boardConfig = ref(JSON.parse(postWriteAppElement.dataset.boardConfig || '{}'));
        const currentUser = ref(JSON.parse(postWriteAppElement.dataset.currentUser || 'null'));
        const isAdmin = ref(currentUser.value?.role === 100); // 관리자 여부

        // 게시글 데이터
        const post = ref({
            title: '',
            content: '',
            isAnonymous: boardConfig.value.isAnonymous === 1 ? 1 : 0,
            isNotice: 0
        });

        let quill = null; // Quill 에디터 인스턴스를 저장할 변수

        // Quill에서 사용할 사용자 지정 폰트 목록을 정의합니다.
        const availableFonts = {
            '': 'Default', // 기본 폰트
            'noto-sans-kr': 'Noto Sans KR',
            'nanum-gothic': 'Nanum Gothic',
            'spoqa-han-sans-neo': 'Spoqa Han Sans Neo'
        };

        // Quill의 Font 모듈에 사용자 지정 폰트들을 등록합니다.
        const Font = Quill.import('formats/font');
        Font.whitelist = Object.keys(availableFonts);
        Quill.register(Font, true);

        // Quill 이미지 블롯 등록 (이미지 삽입 처리를 위해 필요)
        const ImageBlot = Quill.import('formats/image');
        Quill.register(ImageBlot, true);

        // Vue 컴포넌트가 마운트된 후 Quill 초기화
        const initializeQuill = () => {
             // Quill 에디터 초기화
            quill = new Quill('#editor', {
                theme: 'snow', // 'snow' 또는 'bubble' 테마 선택
                placeholder: '내용을 입력하세요...',
                modules: {
                    toolbar: {
                        container: [
                             [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                             [{ 'font': Object.keys(availableFonts) }],
                             ['bold', 'italic', 'underline', 'strike'],
                             [{ 'color': [] }, { 'background': [] }],
                             [{ 'script': 'sub' }, { 'script': 'super' }],
                             ['blockquote', 'code-block'],
                             [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                             [{ 'indent': '-1' }, { 'indent': '+1' }],
                             [{ 'direction': 'rtl' }],
                             [{ 'align': [] }],
                             ['link', 'image', 'video'],
                             ['clean']
                         ],
                         handlers: {
                             image: imageHandler // 커스텀 이미지 핸들러 연결
                         }
                    }
                }
            });

            // Quill 에디터 내용이 변경될 때 Vue 데이터 업데이트
            quill.on('text-change', () => {
                post.value.content = quill.root.innerHTML; // HTML 내용을 가져와 저장
            });
        };
        
        // 컴포넌트가 마운트된 후에 Quill 초기화
        onMounted(() => { 
            // 임시 게시글 생성 로직 제거
            initializeQuill();
        });

        // 이미지 업로드 핸들러 (Base64로 에디터에 삽입)
        const imageHandler = () => {
            const input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');
            input.click();

            input.onchange = () => {
                const file = input.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        const range = quill.getSelection(true);
                        // 사용자 현재 커서 위치에 이미지 삽입
                        quill.insertEmbed(range.index, 'image', e.target.result);
                        // 이미지 삽입 후 커서를 이미지 뒤로 이동
                        quill.setSelection(range.index + 1);
                    };
                    reader.readAsDataURL(file);
                }
            };
        };

        // Helper function to convert Base64 to File object
        const base64ToFile = (base64, filename) => {
            const arr = base64.split(',');
            if (arr.length < 2) { return null; }
            const mimeMatch = arr[0].match(/:(.*?);/);
            if (!mimeMatch || mimeMatch.length < 2) { return null; }
            const mime = mimeMatch[1];
            const bstr = atob(arr[1]);
            let n = bstr.length;
            const u8arr = new Uint8Array(n);
            while (n--) {
                u8arr[n] = bstr.charCodeAt(n);
            }
            return new File([u8arr], filename, { type: mime });
        };

        // Helper function to extract Base64 images and assign temporary IDs
        const extractBase64Images = (htmlContent) => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlContent, 'text/html');
            const images = doc.querySelectorAll('img[src^="data:image"]');
            const imageFiles = [];
            images.forEach((img, index) => {
                const originalSrc = img.src;
                const tempId = `temp-image-${Date.now()}-${index}`;
                const file = base64ToFile(originalSrc, `${tempId}.${originalSrc.split('/')[1].split(';')[0] || 'png'}`);
                if (file) {
                    img.setAttribute('data-temp-id', tempId); // Add temp ID to img tag for later replacement
                    imageFiles.push({ tempId, file, originalSrc });
                }
            });
            return { updatedHtml: doc.body.innerHTML, imageFiles };
        };

        // 폼 제출 처리
        const submitForm = async () => {
            // Quill 에디터의 내용을 post.content에 최종적으로 반영
            post.value.content = quill.root.innerHTML; 

            const { updatedHtml, imageFiles } = extractBase64Images(post.value.content);
            post.value.content = updatedHtml; // HTML에 data-temp-id가 추가된 내용으로 업데이트

            console.log('Extracted image files:', imageFiles);
            console.log('HTML with temp IDs:', post.value.content);

            // TODO: imageFiles를 서버로 업로드하고, 반환된 URL로 <img src>를 교체한 후 최종 저장해야 함.
            // 현재는 이미지 업로드 없이 바로 게시글 저장 로직으로 넘어감 (다음 단계에서 구현)
            if (imageFiles.length > 0) {
                const uploadPromises = imageFiles.map(imageData => {
                    const formData = new FormData();
                    formData.append('image', imageData.file);
                    formData.append('uploadFrom', 0); // 0: 게시글
                    formData.append('parentId', 0); // 최종 게시글 ID를 아직 모르므로 임시값 0 사용

                    return fetch('/api/upload/image', {
                        method: 'POST',
                        headers: {
                            [csrfHeader]: csrfToken
                        },
                        body: formData
                    })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(err => Promise.reject(err));
                        }
                        return response.json();
                    })
                    .then(result => {
                        // 업로드된 이미지 URL을 원래 imageFiles 객체에 추가
                        imageData.serverUrl = result.imageUrl; 
                        // imageData.fileId = result.fileId; // 필요하다면 파일 ID도 저장
                        return imageData; // 서버 URL이 추가된 imageData 반환
                    });
                });

                try {
                    const uploadedImagesData = await Promise.all(uploadPromises);
                    console.log('Uploaded images data with server URLs:', uploadedImagesData);

                    // HTML 콘텐츠에서 임시 ID를 가진 이미지들의 src를 서버 URL로 교체
                    let finalHtmlContent = post.value.content;
                    uploadedImagesData.forEach(imgData => {
                        if (imgData.serverUrl) {
                            const imgTagRegex = new RegExp(`<img[^>]*data-temp-id="${imgData.tempId}"[^>]*>`, 'i');
                            const imgTagMatch = finalHtmlContent.match(imgTagRegex);
                            if (imgTagMatch) {
                                const originalImgTag = imgTagMatch[0];
                                const newImgTag = originalImgTag.replace(/src="data:[^"]*"/, `src="${imgData.serverUrl}"`);
                                finalHtmlContent = finalHtmlContent.replace(originalImgTag, newImgTag);
                            }
                        }
                    });
                    post.value.content = finalHtmlContent; // 최종 URL로 업데이트된 HTML
                    console.log('Final HTML with server URLs:', post.value.content);

                } catch (uploadError) {
                    console.error('이미지 업로드 중 오류 발생:', uploadError);
                    alert('일부 이미지 업로드에 실패했습니다. ' + (uploadError.message || ''));
                    return; // 이미지 업로드 실패 시 게시글 저장 중단
                }
            }

            try {
                const response = await fetch(`/boards/${boardConfig.value.boardId}/posts`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify({
                        ...post.value,
                        boardId: boardConfig.value.boardId,
                        isAnonymous: post.value.isAnonymous ? 1 : 0,
                        isNotice: post.value.isNotice ? 1 : 0
                        // tempPostId 전송 코드 제거
                    })
                });
                
                if (response.ok) {
                    const result = await response.json();
                    window.location.href = `/boards/${boardConfig.value.boardId}/posts`;
                } else {
                    const error = await response.json();
                    alert('글 작성 중 오류가 발생했습니다: ' + (error.message || '알 수 없는 오류'));
                }
            } catch (error) {
                console.error('Error:', error);
                alert('글 작성 중 오류가 발생했습니다.');
            }
        };

        // 취소 처리
        const cancel = () => {
            if (confirm('작성 중인 내용이 사라집니다. 정말로 취소하시겠습니까?')) {
                window.history.back();
            }
        };
        
        return {
            post,
            boardConfig,
            isAdmin,
            submitForm,
            cancel
        };
    }
};

// Vue 앱 마운트
createApp(writeApp).mount('#post-write-app');