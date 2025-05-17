<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <div class="topdiv-boards">
        <div class="container" id="boards-app" v-cloak>
            <h1 class="h1">게시판 관리</h1>

            <%-- TODO: Add error message display area --%>
            <p v-if="errorMessage" class="board-error-message" style="color: red;">{{ errorMessage }}</p>

            <%-- 게시판 목록 섹션 --%>
            <div class="board-list-section form-section">
                <div class="section-title">게시판 목록</div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>이름</th>
                            <th>설명</th>
                            <th>활성 여부</th>
                            <th>익명 여부</th>
                            <th>액션</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- TODO: 여기에 Vue.js를 사용하여 게시판 목록 데이터를 렌더링 --%>
                        <%-- 임시 데이터 --%>
                        <!-- Vue.js를 사용하여 게시판 목록 데이터 렌더링 -->
                        <tr v-for="board in boards" :key="board.id">
                            <td>{{ board.id }}</td>
                            <td>{{ board.name }}</td>
                            <td>{{ board.description }}</td>
                            <td>{{ board.isActive === 1 ? '사용 중' : '사용 안 함' }}</td>
                            <td>{{ board.isAnonymous === 1 ? '맞음' : '아님' }}</td>
                            <td>
                                <button @click="editBoard(board)">수정</button>
                                <button @click="deleteBoard(board.id)">삭제</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <hr style="margin: 40px 0;">

            <%-- 게시판 생성/수정 폼 섹션 --%>
            <div class="board-form-section form-section">
                <div class="section-title">
                    <span v-if="boardForm.id === null">새 게시판 생성</span>
                    <span v-else>게시판 수정 (ID: {{ boardForm.id }})</span>
                </div>
                <form id="board-management-form" @submit.prevent="submitForm">
                    <input type="hidden" name="id" v-model="boardForm.id">
                    <div>
                        <label for="boardName">게시판 이름<span class="required">*</span>:</label>
                        <input type="text" id="boardName" name="name" required v-model="boardForm.name">
                    </div>
                    <div>
                        <label for="boardDescription">게시판 설명:</label>
                        <textarea id="boardDescription" name="description" v-model="boardForm.description"></textarea>
                    </div>
                     <div>
                        <label for="categoryCode">카테고리 코드:</label>
                        <input type="number" id="categoryCode" name="categoryCode" v-model.number="boardForm.categoryCode">
                    </div>
                    <div>
                        <label for="isActive">활성 여부:</label>
                        <select id="isActive" name="isActive" v-model.number="boardForm.isActive">
                            <option value="1">사용 중</option>
                            <option value="0">사용 안 함</option>
                        </select>
                    </div>
                     <div>
                        <label for="isAnonymous">익명 게시판 여부:</label>
                        <select id="isAnonymous" name="isAnonymous" v-model.number="boardForm.isAnonymous">
                            <option value="0">아님</option>
                            <option value="1">맞음</option>
                        </select>
                    </div>
                     <div>
                        <label for="writeRole">글쓰기 최소 레벨:</label>
                        <input type="number" id="writeRole" name="writeRole" v-model.number="boardForm.writeRole">
                    </div>
                     <div>
                        <label for="readRole">읽기 최소 레벨:</label>
                        <input type="number" id="readRole" name="readRole" v-model.number="boardForm.readRole">
                    </div>

                    <button type="submit" class="submit-button">
                        {{ boardForm.id === null ? '생성' : '수정' }}
                    </button>
                     <button type="button" class="submit-button" @click="resetForm">취소</button>

                    <button type="button" class="submit-button" v-if="boardForm.id !== null" @click="deleteBoard(boardForm.id)">삭제</button>
                </form>
            </div>

        </div>
    </div>