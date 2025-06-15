package seoul.its.info.services.traffic.event.indexusage;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.web.client.RestTemplate;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;


@Service
public class IndexTrafficEventServiceImpl implements IndexTrafficEventService {

    @Value("${open.api.base.key}")
    private String openApiBaseKey;

    private final String eventAddressBase = "http://openapi.seoul.go.kr:8088/";
    private final String linkInfoBase = "http://openapi.seoul.go.kr:8088/";
    private final String jsonFilePath = "src/main/data/api/json/seoulTrafficEvent.json";

    @Override
    @Async
    public CompletableFuture<Void> processAndSaveTrafficEvents() {
        return CompletableFuture.supplyAsync(() -> {
            Path path = Paths.get(jsonFilePath);
            if (Files.exists(path)) {
                try {
                    LocalDateTime fileLastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), java.time.ZoneId.systemDefault());
                    LocalDateTime currentHourStart = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

                    if (fileLastModified.isAfter(currentHourStart)) {
                        System.out.println("최신 파일이므로 업데이트 필요 없음");
                        return null;
                    }
                } catch (IOException e) {
                    System.err.println("파일 수정 시간을 확인하는 중 오류 발생: " + e.getMessage());
                }
            } else {
                System.out.println("파일이 없습니다. 생성 하겠습니다.");
            }

            try {
                List<Map<String, Object>> trafficEvents = fetchAndProcessTrafficEvents();
                saveTrafficEventsToJson(trafficEvents);
                System.out.println("Traffic events updated successfully.");
            } catch (Exception e) {
                System.err.println("Error processing and saving traffic events: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
    }

    private List<Map<String, Object>> fetchAndProcessTrafficEvents() throws Exception {
        List<Map<String, Object>> processedEvents = new ArrayList<>();
        String eventApiUrl = eventAddressBase + openApiBaseKey + "/xml/AccInfo/1/10/";
        RestTemplate restTemplate = new RestTemplate();
        String xmlResponse = restTemplate.getForObject(eventApiUrl, String.class);
        System.out.println("Raw XML Response from Event API:\n" + xmlResponse);

        String validXmlResponse;
        int xmlStart = xmlResponse.indexOf("<?xml");
        int xmlEnd = xmlResponse.indexOf("</AccInfo>") + "</AccInfo>".length();

        if (xmlStart != -1 && xmlEnd != -1 && xmlEnd > xmlStart) {
            validXmlResponse = xmlResponse.substring(xmlStart, xmlEnd);
            System.out.println("Extracted Valid XML Response:\n" + validXmlResponse); // 유효한 XML 응답 출력
        } else {
            System.err.println("Could not extract valid XML from response: " + xmlResponse);
            throw new Exception("Invalid XML response format");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(validXmlResponse)));
        doc.getDocumentElement().normalize();

        NodeList rowList = doc.getElementsByTagName("row");
        int dataNoCounter = 1;

        CRSFactory crsFactory = new CRSFactory();
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateReferenceSystem grs80tm = crsFactory.createFromParameters("GRS80TM", "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs");
        CoordinateReferenceSystem wgs84 = crsFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
        CoordinateTransform transform = ctFactory.createTransform(grs80tm, wgs84);

        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();

        for (int i = 0; i < rowList.getLength(); i++) {
            Element row = (Element) rowList.item(i);
            String accId = getTagValue("acc_id", row);
            String occrDate = getTagValue("occr_date", row);
            String occrTime = getTagValue("occr_time", row);
            String expClrDate = getTagValue("exp_clr_date", row);
            String expClrTime = getTagValue("exp_clr_time", row);
            String linkId = getTagValue("link_id", row);
            String grs80tmX = getTagValue("grs80tm_x", row);
            String grs80tmY = getTagValue("grs80tm_y", row);
            String accInfo = getTagValue("acc_info", row);

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("data_no", dataNoCounter++);
            eventData.put("starttime", occrDate + occrTime);
            eventData.put("endtime", expClrDate + expClrTime);
            eventData.put("accinfo", accInfo);

            try {
                if (grs80tmX != null && grs80tmY != null) {
                    double x = Double.parseDouble(grs80tmX);
                    double y = Double.parseDouble(grs80tmY);
                    ProjCoordinate srcCoord = new ProjCoordinate(x, y);
                    ProjCoordinate dstCoord = new ProjCoordinate();
                    transform.transform(srcCoord, dstCoord);
    
                    eventData.put("xcoordinate", dstCoord.x);
                    eventData.put("ycoordinate", dstCoord.y);
                } else {
                    System.err.println("GRS80TM_X or GRS80TM_Y is null for ACC_ID: " + accId);
                    eventData.put("xcoordinate", null);
                    eventData.put("ycoordinate", null);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid coordinate format for ACC_ID: " + accId + " - " + e.getMessage());
                eventData.put("xcoordinate", null);
                eventData.put("ycoordinate", null);
            }

            if (linkId != null && !linkId.isEmpty()) {
                final String currentLinkId = linkId;
                futures.add(CompletableFuture.supplyAsync(() -> {
                    Map<String, Object> linkEventData = new HashMap<>();
                    linkEventData.putAll(eventData); // 기존 이벤트 데이터를 복사

                    try {
                        String linkApiUrl = linkInfoBase + openApiBaseKey.trim() + "/xml/LinkInfo/1/5/" + currentLinkId;
                        String linkXmlResponse = restTemplate.getForObject(linkApiUrl, String.class);
                        Document linkDoc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(linkXmlResponse)));
                        linkDoc.getDocumentElement().normalize();

                        NodeList linkRowList = linkDoc.getElementsByTagName("row");
                        if (linkRowList.getLength() > 0) {
                            Element linkRow = (Element) linkRowList.item(0);
                            linkEventData.put("roadname", getTagValue("road_name", linkRow));
                            linkEventData.put("startfrom", getTagValue("st_node_nm", linkRow));
                            linkEventData.put("endat", getTagValue("ed_node_nm", linkRow));
                            linkEventData.put("distmeter", getTagValue("map_dist", linkRow));
                        }
                    } catch (Exception e) {
                        System.err.println("Error fetching link data for LINK_ID: " + currentLinkId + " - " + e.getMessage());
                    }
                    return linkEventData;
                }));
            } else {
                processedEvents.add(eventData);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        processedEvents.addAll(futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));

        return processedEvents;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nl = element.getElementsByTagName(tag);
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return null;
    }

    private void saveTrafficEventsToJson(List<Map<String, Object>> events) throws IOException {
        Path filePath = Paths.get(jsonFilePath);
        try {
            Files.createDirectories(filePath.getParent());
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            try (FileWriter fileWriter = new FileWriter(filePath.toFile(), java.nio.charset.StandardCharsets.UTF_8)) {
                mapper.writeValue(fileWriter, events);
            }
        } catch (IOException e) {
            System.err.println("파일을 저장하는 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }
} 