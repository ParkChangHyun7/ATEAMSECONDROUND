import { createApp, ref, onMounted } from 'vue';

const contactApp = {
  setup() {
    const name = ref('');
    const email = ref('');
    const phone = ref('');
    const subject = ref('');
    const message = ref('');
    const attachment = ref(null);
    const isLoading = ref(false);
    const messageText = ref('');
    const messageType = ref('');
    const allowedExtensions = ref([]);
    const uploadProgress = ref(0);

    const attachmentInput = ref(null);

    onMounted(() => {
      allowedExtensions.value = [
        "jpg", "jpeg", "png", "gif", "bmp", "pdf", "doc", "docx",
        "xls", "xlsx", "ppt", "pptx", "hwp", "txt", "zip",
      ];
    });

    const onFileChange = (e) => {
      const file = e.target.files[0];
      const maxFileSize = 10 * 1024 * 1024;

      if (file) {
        if (file.size > maxFileSize) {
          attachment.value = null;
          e.target.value = null;
          messageText.value = "업로드 최대 용량은 10MB입니다.";
          messageType.value = "error";
          return;
        }

        const fileName = file.name;
        const fileExtension = fileName.split(".").pop().toLowerCase();

        if (allowedExtensions.value.includes(fileExtension)) {
          attachment.value = file;
          messageText.value = "";
          messageType.value = "";
        } else {
          attachment.value = null;
          e.target.value = null;
          messageText.value = `이미지, 문서, zip 파일만 업로드 가능합니다. <br> 다수 파일은 zip으로 압축해주세요 <br> 허용 확장자: ${allowedExtensions.value.join(", ")}`;
          messageType.value = "error";
        }
      } else {
        attachment.value = null;
        messageText.value = "";
        messageType.value = "";
      }
    };

    const submitContact = () => {
      isLoading.value = true;
      messageText.value = "";
      messageType.value = "";
      uploadProgress.value = 0;

      const csrfHeader = window.MyApp?.utils?.getCsrfHeader();
      const csrfToken = window.MyApp?.utils?.getCsrfToken();

      const formData = new FormData();
      formData.append("name", name.value);
      formData.append("email", email.value);
      formData.append("phone", phone.value);
      formData.append("subject", subject.value);
      formData.append("message", message.value);
      if (attachment.value) {
        formData.append("attachment", attachment.value);
        formData.append("fileIncluded", 1);
      }

      const xhr = new XMLHttpRequest();
      xhr.open("POST", "/contact", true);

      if (csrfHeader && csrfToken) {
        xhr.setRequestHeader(csrfHeader, csrfToken);
      }

      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          uploadProgress.value = Math.round((event.loaded / event.total) * 100);
        }
      };

      xhr.onload = () => {
        isLoading.value = false;
        uploadProgress.value = 0;
        let responseData = null;
        try {
          if (
            xhr.getResponseHeader("Content-Type") &&
            xhr.getResponseHeader("Content-Type").includes("application/json")
          ) {
            responseData = JSON.parse(xhr.responseText);
          } else if (xhr.responseURL && xhr.responseURL !== "/contact") {
            window.location.href = xhr.responseURL;
            return;
          }
        } catch (e) {
          console.error("응답 JSON 파싱 실패:", e);
        }

        if (xhr.status >= 200 && xhr.status < 300) {
          messageText.value = responseData?.message || "문의가 정상적으로 접수되었습니다.";
          messageType.value = "success";
          name.value = "";
          email.value = "";
          phone.value = "";
          subject.value = "";
          message.value = "";
          attachment.value = null;
          if (attachmentInput.value) attachmentInput.value.value = "";
        } else {
          messageText.value = responseData?.message || "문의 접수에 실패했습니다. 다시 시도해주세요.";
          messageType.value = "error";
        }
      };

      xhr.onerror = () => {
        isLoading.value = false;
        uploadProgress.value = 0;
        messageText.value = "네트워크 오류가 발생했습니다.";
        messageType.value = "error";
      };

      xhr.send(formData);
    };

    return {
      name,
      email,
      phone,
      subject,
      message,
      attachment,
      isLoading,
      messageText,
      messageType,
      allowedExtensions,
      uploadProgress,
      attachmentInput,
      onFileChange,
      submitContact,
    };
  }
};

createApp(contactApp).mount("#contactApp");
