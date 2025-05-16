package seoul.its.info.services.contact;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface ContactPageService {

    void saveContactInquiry(ContactRequestDto contactDto, HttpServletRequest httpRequest)
            throws IOException;
}