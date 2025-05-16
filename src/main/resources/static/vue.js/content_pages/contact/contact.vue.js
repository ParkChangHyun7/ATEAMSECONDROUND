Vue.createApp({
  el: "#contactApp",
  data() {
    return {
      name: "",
      email: "",
      phone: "",
      subject: "",
      message: "",
      attachment: null,
      isLoading: false,
      messageText: "",
      messageType: "",
      allowedExtensions: [],
      uploadProgress: 0,
    };
  },
  mounted() {
    // Define allowed extensions based on application.properties
    this.allowedExtensions = [
      "jpg",
      "jpeg",
      "png",
      "gif",
      "bmp",
      "pdf",
      "doc",
      "docx",
      "xls",
      "xlsx",
      "ppt",
      "pptx",
      "hwp",
      "txt",
      "zip",
    ];
  },
  methods: {
    onFileChange(e) {
      const file = e.target.files[0];
      const maxFileSize = 10 * 1024 * 1024; // 10MB

      if (file) {
        // 1. 파일 크기 체크
        if (file.size > maxFileSize) {
          this.attachment = null;
          e.target.value = null; // Clear the file input
          this.messageText = "업로드 최대 용량은 10MB입니다.";
          this.messageType = "error";
          return; // 크기 초과 시 여기서 처리 종료
        }

        const fileName = file.name;
        const fileExtension = fileName.split(".").pop().toLowerCase();

        // 2. 파일 확장자 체크
        if (this.allowedExtensions.includes(fileExtension)) {
          this.attachment = file;
          this.messageText = ""; // Clear previous message
          this.messageType = "";
        } else {
          this.attachment = null;
          e.target.value = null; // Clear the file input
          this.messageText = `이미지, 문서, zip 파일만 업로드 가능합니다. <br> 다수 파일은 zip으로 압축해주세요 <br> 허용 확장자: ${this.allowedExtensions.join(
            ", "
          )}`;
          this.messageType = "error";
        }
      } else {
        this.attachment = null;
        this.messageText = "";
        this.messageType = "";
      }
    },
    submitContact() {
      this.isLoading = true;
      this.messageText = "";
      this.messageType = "";
      this.uploadProgress = 0;

      const csrfHeader = window.MyApp?.utils?.getCsrfHeader();
      const csrfToken = window.MyApp?.utils?.getCsrfToken();

      const formData = new FormData();
      formData.append("name", this.name);
      formData.append("email", this.email);
      formData.append("phone", this.phone);
      formData.append("subject", this.subject);
      formData.append("message", this.message);
      if (this.attachment) {
        formData.append("attachment", this.attachment);
        formData.append("fileIncluded", 1);
      }

      const xhr = new XMLHttpRequest();
      xhr.open("POST", "/contact", true);

      if (csrfHeader && csrfToken) {
        xhr.setRequestHeader(csrfHeader, csrfToken);
      }

      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          this.uploadProgress = Math.round((event.loaded / event.total) * 100);
        }
      };

      xhr.onload = () => {
        this.isLoading = false;
        this.uploadProgress = 0;
        let responseData = null;
        try {
          // Check if responseType is json, otherwise it might be a redirect (HTML)
          if (
            xhr.getResponseHeader("Content-Type") &&
            xhr.getResponseHeader("Content-Type").includes("application/json")
          ) {
            responseData = JSON.parse(xhr.responseText);
          } else if (xhr.responseURL && xhr.responseURL !== "/contact") {
            // Handle redirect case
            window.location.href = xhr.responseURL;
            return;
          }
        } catch (e) {
          console.error("응답 JSON 파싱 실패:", e);
        }

        if (xhr.status >= 200 && xhr.status < 300) {
          this.messageText =
            responseData?.message || "문의가 정상적으로 접수되었습니다.";
          this.messageType = "success";
          this.name = "";
          this.email = "";
          this.phone = "";
          this.subject = "";
          this.message = "";
          this.attachment = null;
          if (this.$refs.attachment) this.$refs.attachment.value = "";
        } else {
          this.messageText =
            responseData?.message ||
            "문의 접수에 실패했습니다. 다시 시도해주세요.";
          this.messageType = "error";
        }
      };

      xhr.onerror = () => {
        this.isLoading = false;
        this.uploadProgress = 0;
        this.messageText = "네트워크 오류가 발생했습니다.";
        this.messageType = "error";
      };

      xhr.send(formData);
    },
  },
}).mount("#contactApp");
