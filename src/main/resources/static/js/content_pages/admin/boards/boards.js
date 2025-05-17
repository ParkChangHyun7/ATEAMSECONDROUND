import { createApp, ref, onMounted } from 'vue';

createApp({
    setup() {
        const boards = ref([]);
        const errorMessage = ref(null);

        const boardForm = ref({
            id: null,
            name: '',
            description: '',
            categoryCode: 0,
            isActive: 1,
            isAnonymous: 0,
            writeRole: 0,
            readRole: 1
        });

        const fetchBoards = async () => {
            try {
                console.log('Fetching boards...');
                const response = await fetch('/admin/boards/list', { method: 'GET' });

                if (!response.ok) {
                    const errorData = await response.json();
                    console.error('Failed to fetch boards:', response.status, errorData);
                    errorMessage.value = errorData.message || response.statusText || '게시판 목록을 불러오는데 실패했습니다.';
                    return;
                }
                const data = await response.json();
                boards.value = data;
                errorMessage.value = null;
                console.log('Boards fetched successfully:', data);
            } catch (error) {
                console.error('Error fetching boards:', error);
                errorMessage.value = `게시판 목록 불러오기 오류: ${error.message || '알 수 없는 오류'}`;
            }
        };

        const submitForm = async () => {
            try {
                console.log('Submitting form:', boardForm.value);
                let response;

                if (boardForm.value.id === null) {
                     console.log('Creating new board...');
                     const result = await apiService.postRequest('/admin/boards/create', boardForm.value);

                     if (!result.success) {
                         console.error('Failed to create board:', result);
                         errorMessage.value = result.message || '게시판 생성에 실패했습니다.';
                         return;
                     }
                      console.log('Board created successfully:', result.data);
                      errorMessage.value = null;
                     alert('게시판이 성공적으로 생성되었습니다!');
                } else {
                    console.log('Updating board:', boardForm.value.id);
                    const csrfHeaders = window.MyApp?.utils?.getCsrfHeadersAsObject();
                    if (!csrfHeaders) {
                         console.error('CSRF headers not available.');
                         alert('보안 정보가 없어 게시판을 수정할 수 없습니다.');
                         return;
                    }
                    response = await fetch(`/admin/boards/modify/${boardForm.value.id}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            ...csrfHeaders,
                        },
                        body: JSON.stringify(boardForm.value),
                    });

                     if (!response.ok) {
                        const errorData = await response.json();
                        console.error('Failed to update board:', response.status, errorData);
                        errorMessage.value = errorData.message || response.statusText || '게시판 수정에 실패했습니다.';
                        return;
                    }

                    const resultData = await response.json();
                    console.log('Board updated successfully:', resultData);
                    errorMessage.value = null;
                    alert('게시판이 성공적으로 수정되었습니다!');
                }
                await fetchBoards();
                resetForm();

            } catch (error) {
                console.error('Error submitting form:', error);
                errorMessage.value = `게시판 정보 저장 오류: ${error.message || '알 수 없는 오류'}`;
            }
        };

        const editBoard = (board) => {
            Object.assign(boardForm.value, board);
        };

        const resetForm = () => {
             boardForm.value = {
                id: null,
                name: '',
                description: '',
                categoryCode: 0,
                isActive: 1,
                isAnonymous: 0,
                writeRole: 0,
                readRole: 1
            };
        };

        const deleteBoard = async (boardId) => {
            if (confirm('정말로 이 게시판을 삭제하시겠습니까?')) {
                try {
                    console.log('Deleting board:', boardId);
                    const csrfHeaders = window.MyApp?.utils?.getCsrfHeadersAsObject();
                     if (!csrfHeaders) {
                         console.error('CSRF headers not available.');
                         alert('보안 정보가 없어 게시판을 삭제할 수 없습니다.');
                         return;
                    }
                    const response = await fetch(`/admin/boards/delete/${boardId}`, {
                        method: 'DELETE',
                         headers: {
                            'Content-Type': 'application/json',
                            ...csrfHeaders,
                        },
                    });

                     if (!response.ok) {
                        const errorData = await response.json();
                        console.error('Failed to delete board:', response.status, errorData);
                        errorMessage.value = errorData.message || response.statusText || '게시판 삭제에 실패했습니다.';
                        return;
                    }

                    console.log('Board deleted successfully.');
                     errorMessage.value = null;
                    alert('게시판이 성공적으로 삭제되었습니다!');
                    await fetchBoards();

                } catch (error) {
                    console.error('Error deleting board:', error);
                     errorMessage.value = `게시판 삭제 오류: ${error.message || '알 수 없는 오류'}`;
                }
            }
        };

        onMounted(() => {
            fetchBoards();
        });

        return {
            boards,
            boardForm,
            submitForm,
            editBoard,
            resetForm,
            deleteBoard,
            errorMessage
        };
    }
}).mount('#boards-app');