<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="modify-app" class="topdiv-modify">
  <div class="container">
    <h1 class="h1">회원정보 수정</h1>
    <hr style="border: none; border-top: 1px solid #eee; margin-bottom: 35px" />
    <form action="#" method="post" @submit.prevent="isPasswordChecked ? submitModifyForm() : checkPassword()">
      <div v-if="isLoading">로딩 중...</div>
      <div v-else>
        <div v-if="!isPasswordChecked">
          <div class="form-section password-check">
            <label for="password-check" class="section-title">비밀번호 확인</label>
            <input type="password" id="password-check" v-model="passwordInput" placeholder="현재 비밀번호 입력" autocomplete="current-password" />
            <button type="button" class="submit-button" @click="checkPassword" :disabled="isLoading">확인</button>
            <div class="input-info password-error" v-if="passwordError" style="color:red;">{{ passwordError }}</div>
          </div>
        </div>
        <div v-else>
          <div class="form-section user_id">
            <label for="login-id" class="section-title">아이디</label>
            <input type="text" id="login-id" name="login_id" v-model="fields.loginId" readonly class="readonly-input" />
          </div>
          <div class="form-section name-nickname">
            <div class="name-section">
              <label for="name" class="section-title">이름</label>
              <input type="text" id="name" name="name" v-model="fields.name" readonly class="readonly-input" />
            </div>
            <div class="nickname-section">
              <label for="nickname" class="section-title">닉네임 <span style='color:#888;font-size:12px;'>{{ canChangeNickname ? '(변경 가능합니다)' : '(변경 불가합니다)' }}</span></label>
              <input type="text" id="nickname" name="nickname" v-model="fields.nickname" :readonly="!canChangeNickname" />
              <span v-if="!canChangeNickname" style="color:#f00; font-size:12px;">{{ daysLeft }}일 후 변경 가능</span>
            </div>
          </div>
          <div class="form-section email">
            <label for="email" class="section-title">이메일</label>
            <input type="email" id="email" name="email" v-model="fields.email" />
          </div>
          <div class="form-section birthdate-gender">
            <div class="birth-section">
              <label for="birthdate" class="section-title">생년월일</label>
              <input type="text" id="birthdate" name="birth" placeholder="8자리 생년월일 YYYYMMDD 입력" v-model="fields.birth" readonly class="readonly-input" />
            </div>
            <div class="gender-section">
              <label for="gender" class="section-title">성별</label>
              <select id="gender" name="gender" class="select-gender" v-model="fields.gender">
                <option value="N">선택 안함</option>
                <option value="M">남</option>
                <option value="F">여</option>
              </select>
            </div>
          </div>
          <div class="form-section address">
            <label class="section-title">주소</label>
            <div class="input-group postcode">
              <input type="text" id="address_postcode" name="address_postcode" placeholder="우편번호" readonly v-model="fields.address_postcode" class="readonly-input" />
              <button type="button" class="address-search-button" @click="openDaumPostcode">주소 찾기</button>
            </div>
            <div class="address-search-wrap" style="display: none; width: 100%; border: 1px solid; margin: 5px 0; box-sizing: border-box;"></div>
            <input type="text" id="address_base" name="address_base" placeholder="기본 주소" readonly v-model="fields.address_base" class="readonly-input" />
            <input type="text" id="address_detail" name="address_detail" placeholder="상세 주소" v-model="fields.address_detail" />
          </div>
          <div class="form-section phone_number">
            <label for="phone" class="section-title">휴대폰 번호</label>
            <div class="input-group">
              <input type="tel" class="phone-input readonly-input" name="phone_number" v-model="fields.phone" readonly />
              <button class="phone-verify-button" type="button" @click="openPhonePopup">변경</button>
            </div>
            <!-- 인레이어 팝업: 핸드폰 번호 변경용 -->
            <div v-if="showPhonePopup" class="phone-popup-overlay">
              <div class="phone-popup">
                <h3>휴대폰 번호 변경</h3>
                <input type="tel" placeholder="새 휴대폰 번호 입력" v-model="phonePopupFields.phone" @input="handlePhoneInput" :disabled="phoneAuth.isPhoneVerified" />
                <button type="button" @click="sendVerificationCode" :disabled="!phonePopupFields.valid || phoneAuth.isSendingCode || phoneAuth.isTimerActive">인증번호 전송</button>
                <div v-if="phoneAuth.showAuthCodeInputs" class="phone-authcode-inputs">
                  <input v-for="(code, idx) in phoneAuth.authCode" :key="idx" maxlength="1" pattern="[0-9]" inputmode="numeric" v-model="phoneAuth.authCode[idx]" @input="handleAuthCodeInput(idx, $event)" :disabled="phoneAuth.isPhoneVerified" />
                </div>
                <div class="phone-info" v-if="phonePopupFields.info">{{ phonePopupFields.info }}</div>
                <button type="button" @click="closePhonePopup">닫기</button>
              </div>
            </div>
          </div>
          <div class="form-section terms">
            <label class="section-title" style="font-size: 16px">약관 동의</label>
            <div class="agreement-box">
              <div class="aggrement-items-wrapper">
                <div class="agreement-item-required">
                  <div class="agreement-item">
                    <input type="checkbox" id="agree-age" name="agreement_age" v-model="fields.agreement_age" disabled />
                    <label for="agree-age">(필수) 만 15세 이상입니다.</label>
                  </div>
                  <div class="agreement-item">
                    <input type="checkbox" id="agree-service" name="agreement_service" v-model="fields.agreement_service" disabled />
                    <label for="agree-service">(필수) 서비스 이용약관동의</label>
                    <span class="arrow-icon"></span>
                  </div>
                  <div class="agreement-item">
                    <input type="checkbox" id="agree-privacy" name="agreement_privacy" v-model="fields.agreement_privacy" disabled />
                    <label for="agree-privacy">(필수) 개인정보 수집 및 이용 동의</label>
                    <span class="arrow-icon"></span>
                  </div>
                </div>
                <hr style="border: none; border-top: 1px solid #eee; margin: 15px 0;" />
                <div class="agreement-item-optional">
                  <div class="agreement-item">
                    <input type="checkbox" id="agree-alba" name="agreement_alba" v-model="fields.agreement_alba" />
                    <label for="agree-alba">(선택) 서울ITS 이용약관 동의</label>
                    <span class="arrow-icon"></span>
                  </div>
                  <div class="agreement-item">
                    <input type="checkbox" id="agree-marketing" name="agreement_marketing" v-model="fields.agreement_marketing" />
                    <label for="agree-marketing">(선택) 정보 이메일/SMS 수신 동의</label>
                    <span class="arrow-icon"></span>
                  </div>
                  <div class="agreement-item">
                    <input type="checkbox" id="agree-benefits" name="agreement_benefits" v-model="fields.agreement_benefits" />
                    <label for="agree-benefits">(선택) 개인정보수집 및 이용 동의(광고)</label>
                    <span class="arrow-icon"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <button type="submit" class="submit-button" :disabled="isSubmitting">수정하기</button>
          <div v-if="serverErrors.general" style="color:red; margin-top:10px;">{{ serverErrors.general }}</div>
        </div>
      </div>
    </form>
  </div>
</div>
