<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
   <div id="join-app" class="topdiv-join">
      <div class="container">
         <h1 class="h1">회원가입</h1>
         <hr style="border: none; border-top: 1px solid #eee; margin-bottom: 35px" />
         <transition name="slide-page" mode="out-in">
            <form action="#" method="post" @submit.prevent="submitJoinForm">
               <div v-if="!isPhoneVerified" class="first-page-wrap" key="first-page"
                  :style="{ pointerEvents: isPhoneVerified ? 'none' : 'auto' }">
                  <div class="form-section terms">
                     <label class="section-title" style="font-size: 16px">약관 동의<span class="required">*</span></label>
                     <div class="agreement-box">
                        <div class="agreement-item agreement-all">
                           <input type="checkbox" id="agree-all" name="agree_all" v-model="agreeAll"
                              @change="toggleAllAgreements" :disabled="isVerificationSent" />
                           <label for="agree-all">
                              전체동의
                              <span>선택항목 포함 모든 약관에 동의합니다.</span>
                           </label>
                           <span class="arrow-icon"></span>
                        </div>
                        <div class="aggrement-items-wrapper">
                           <hr style="
                    border: none;
                    border-top: 1px solid #eee;
                    margin: 15px 0;
                  " />
                           <div class="agreement-item-required">
                              <div class="agreement-item">
                                 <input type="checkbox" id="agree-age" name="agreement_age" required
                                    v-model="agreements.age" @change="checkIndividualAgreement"
                                    :disabled="isVerificationSent" />
                                 <label for="agree-age">(필수) 만 15세 이상입니다.</label>
                              </div>
                              <div class="agreement-item">
                                 <input type="checkbox" id="agree-service" name="agreement_service" required
                                    v-model="agreements.service" @change="checkIndividualAgreement"
                                    :disabled="isVerificationSent" />
                                 <label for="agree-service">(필수) 서비스 이용약관동의</label>
                                 <span class="arrow-icon"></span>
                              </div>
                              <div class="agreement-item">
                                 <input type="checkbox" id="agree-privacy" name="agreement_privacy" required
                                    v-model="agreements.privacy" @change="checkIndividualAgreement"
                                    :disabled="isVerificationSent" />
                                 <label for="agree-privacy">(필수) 개인정보 수집 및 이용 동의</label>
                                 <span class="arrow-icon"></span>
                              </div>
                           </div>
                           <hr style="
                    border: none;
                    border-top: 1px solid #eee;
                    margin: 15px 0;
                  " />
                           <div class="agreement-item-optional">
                              <div class="agreement-item">
                                 <input type="checkbox" id="agree-alba" name="agreement_alba" v-model="agreements.alba"
                                    @change="checkIndividualAgreement" />
                                 <label for="agree-alba">(선택) 서울ITS 이용약관 동의</label>
                                 <span class="arrow-icon"></span>
                              </div>
                              <div class="agreement-item">
                                 <input type="checkbox" id="agree-marketing" name="agreement_marketing"
                                    v-model="agreements.marketing" @change="checkIndividualAgreement" />
                                 <label for="agree-marketing">(선택) 정보 이메일/SMS 수신 동의</label>
                                 <span class="arrow-icon"></span>
                              </div>
                              <div class="agreement-item">
                                 <input type="checkbox" id="agree-benefits" name="agreement_benefits"
                                    v-model="agreements.benefits" @change="checkIndividualAgreement" />
                                 <label for="agree-benefits">(선택) 개인정보수집 및 이용 동의(광고)</label>
                                 <span class="arrow-icon"></span>
                              </div>
                           </div>
                        </div>
                     </div>
                     <div class="terms-info">
                        <span v-if="!allRequiredAgreed">필수 약관에 동의하지 않으면 가입하실 수 없습니다.</span>
                        <span v-else style="color: #333">휴대폰 인증을 진행해주세요.</span>
                     </div>
                  </div>
                  <div class="form-section phone_number">
                     <label for="phone" class="section-title">휴대폰 번호<span class="required">*</span></label>
                     <div class="input-group">
                        <input type="tel" placeholder="휴대폰 번호를 입력해주세요." class="phone-input"
                           :disabled="!allRequiredAgreed || isPhoneVerified"
                           :style="{ backgroundColor: (!allRequiredAgreed || isPhoneVerified) ? '#f0f0f0' : 'white' }"
                           name="phone_number" required v-model="fields.phone.value" @input="handlePhoneInput" />
                        <button class="phone-verify-button" type="button" id="phone-verify-button"
                           :disabled="!allRequiredAgreed || isPhoneVerified || isCertificationButtonDisabled" :style="{ backgroundColor: isPhoneVerified 
                                                ? '#f0f0f0' 
                                                : isTimerActive
                                                    ? '#f1b300' 
                                                    : isCertificationButtonDisabled
                                                        ? ''
                                                        : '#1d3b5b' }" @click="sendVerificationCode">
                           {{
                           isPhoneVerified
                           ? "인증 완료"
                           : isTimerActive
                           ? buttonTimerText
                           : "인증번호 전송"
                           }}
                        </button>
                     </div>
                     <div class="input-group" style="justify-content: center">
                        <div class="phone-authcode-inputs" v-show="showAuthCodeInputs">
                           <input class="phone-authcode" type="text" maxlength="1" pattern="[0-9]" inputmode="numeric"
                              required :disabled="isPhoneVerified"
                              :style="{ backgroundColor: isPhoneVerified ? '#f0f0f0' : '' }" v-model="authCode[0]"
                              @input="handleAuthCodeInput(0, $event)" @keydown="handleAuthCodeKeydown(0, $event)"
                              @paste.prevent="handleAuthCodePaste($event)" :ref="`authCodeInput0`" />
                           <input class="phone-authcode" type="text" maxlength="1" pattern="[0-9]" inputmode="numeric"
                              required :disabled="isPhoneVerified"
                              :style="{ backgroundColor: isPhoneVerified ? '#f0f0f0' : '' }" v-model="authCode[1]"
                              @input="handleAuthCodeInput(1, $event)" @keydown="handleAuthCodeKeydown(1, $event)"
                              :ref="`authCodeInput1`" />
                           <input class="phone-authcode" type="text" maxlength="1" pattern="[0-9]" inputmode="numeric"
                              required :disabled="isPhoneVerified"
                              :style="{ backgroundColor: isPhoneVerified ? '#f0f0f0' : '' }" v-model="authCode[2]"
                              @input="handleAuthCodeInput(2, $event)" @keydown="handleAuthCodeKeydown(2, $event)"
                              :ref="`authCodeInput2`" />
                           <input class="phone-authcode" type="text" maxlength="1" pattern="[0-9]" inputmode="numeric"
                              required :disabled="isPhoneVerified"
                              :style="{ backgroundColor: isPhoneVerified ? '#f0f0f0' : '' }" v-model="authCode[3]"
                              @input="handleAuthCodeInput(3, $event)" @keydown="handleAuthCodeKeydown(3, $event)"
                              :ref="`authCodeInput3`" />
                           <input class="phone-authcode" type="text" maxlength="1" pattern="[0-9]" inputmode="numeric"
                              required :disabled="isPhoneVerified"
                              :style="{ backgroundColor: isPhoneVerified ? '#f0f0f0' : '' }" v-model="authCode[4]"
                              @input="handleAuthCodeInput(4, $event)" @keydown="handleAuthCodeKeydown(4, $event)"
                              :ref="`authCodeInput4`" />
                        </div>
                     </div>
                     <div class="phone-info">
                        <span v-if="fields.phone.info" v-html="fields.phone.info"></span>
                     </div>
                  </div>
               </div>

               <div v-else class="second-page-wrap" key="second-page" v-cloak>
                  <div class="form-section user_id">
                     <label for="login-id" class="section-title">아이디(필수)<span class="required">*</span></label>
                     <input type="text" id="login-id" name="login_id" required v-model="fields.loginId.value"
                        @blur="handleLoginIdBlur" />
                  </div>
                  <div class="input-info user_id">
                     <span v-if="fields.loginId.info">{{ fields.loginId.info }}</span>
                  </div>

                  <div class="form-section password">
                     <div class="password-label-group">
                        <label for="password" class="section-title">비밀번호(필수)<span class="required">*</span></label>
                        <span class="password-help" style="margin-left: 10px">?</span>
                     </div>

                     <input type="password" id="password" name="password" required v-model="fields.password.value"
                        @blur="handlePasswordBlur" />
                  </div>
                  <div class="input-info password">
                     <span v-if="fields.password.info">{{ fields.password.info }}</span>
                  </div>

                  <div class="form-section name-nickname">
                     <div class="name-section">
                        <label for="name" class="section-title">이름(필수)<span class="required">*</span></label>
                        <input type="text" id="name" name="name" required v-model="fields.name.value"
                           @blur="handleNameBlur" />
                     </div>
                     <div class="nickname-section">
                        <label for="nickname" class="section-title">닉네임(선택)<span class="required">*</span><span
                              class="password-help" style="margin-left: 10px">?</span></label>
                        <input type="text" id="nickname" name="nickname" required v-model="fields.nickname.value"
                           @blur="handleNicknameBlur" />
                     </div>
                  </div>
                  <div class="input-info name-nickname">
                     <span v-if="fields.nickname.info">{{ fields.nickname.info }}</span>
                     <span v-if="fields.name.info">{{ fields.name.info }}</span>
                  </div>

                  <div class="form-section email">
                     <label for="email" class="section-title" class="required">이메일</label>
                     <input placeholder="abc@abc.com 형식의 이메일 주소 입력" type="email" id="email" name="email"
                        v-model="fields.email.value" @blur="handleEmailBlur" />
                  </div>
                  <div class="input-info email">
                     <span v-if="fields.email.info">{{ fields.email.info }}</span>
                  </div>
                  <div class="form-section birthdate-gender">
                     <div class="birth-section">
                        <label for="birthdate" class="section-title">생년월일</label>
                        <input type="text" id="birthdate" name="birth" placeholder="8자리 생년월일 MMMMYYDD 입력"
                           v-model="fields.birth.value" @blur="handleBirthBlur" />
                     </div>

                     <div class="gender-section">
                        <label for="gender" class="section-title">성별</label>
                        <select id="gender" name="gender" class="select-gender" v-model="gender">
                           <option class="option-gender" value="N">선택 안함</option>
                           <option class="option-gender" value="M">남</option>
                           <option class="option-gender" value="F">여</option>
                        </select>
                     </div>
                  </div>
                  <div class="input-info birth"><span v-html="fields.birth.info"></span></div>

                  <div class="form-section address">
                     <label class="section-title">주소</label>
                     <div class="input-group postcode">
                        <input type="text" id="address_postcode" name="address_postcode" placeholder="우편번호" readonly
                           v-model="fields.address_postcode.value" />
                        <button type="button" class="address-search-button" @click="openDaumPostcode">
                           주소 찾기
                        </button>
                     </div>
                     <div class="address-search-wrap" style="
                display: none;
                width: 100%;
                border: 1px solid;
                margin: 5px 0;
                box-sizing: border-box;
              ">
                        <%-- daum.Postcode embed 영역 --%>
                     </div>
                     <input type="text" id="address_base" name="address_base" placeholder="기본 주소" readonly
                        v-model="fields.address_base.value" />
                     <input type="text" id="address_detail" name="address_detail" placeholder="상세 주소"
                        v-model="fields.address_detail.value" />
                  </div>

                  <div class="input-info address"><span></span></div>
                  <div class="gapper" style="width: 100%; height: 25px"></div>
                  <button type="submit" class="submit-button" :disabled="!canSubmitForm" alt="하이요">가입하기</button>
                  <%-- 서버 오류 메시지 출력 영역 --%>
                     <div class="hasError" v-if="Object.keys(serverErrors).length > 0"
                        style="margin-top: 15px; color: red; border: 1px solid red; padding: 10px; border-radius: 5px;">
                        <p style="margin-bottom: 5px; font-weight: bold;">다음 오류를 확인해주세요:</p>
                        <ul>
                           <li v-for="(message, field) in serverErrors" :key="field">
                              {{ field === 'general' ? '' : field + ': ' }}{{ message }}
                           </li>
                        </ul>
                     </div>
               </div>

               <div style="height: 50px"></div>
            </form>
         </transition>
      </div>
   </div>