// package seoul.its.info.services.metro;
// import org.springframework.stereotype.Service;
// import seoul.its.info.services.metro.dto.MetroAccidentDto;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.*;

// @Service

// public class MetroAccidentService {
//     private static final String CSV_PATH ="src/main/data/csv/metro/서울교통공사_최근 5년 지하철사고 현항_20250310.csv";

//     public List<MetroAccidentDto> getAccidentData() throws IOException {

//         List<MetroAccidentDto> data = new ArrayList<>();

//         try(BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))){
//             String line;
//             br.readLine();
//             while((line=br.readLine())!=null){
//             String[] tokens =line.split(",");    
//             if(tokens.length >=2){
//                     String year = tokens[0].trim();
//                     int count =Integer.parseInt((tokens[1].trim()));

//                     data.add(new MetroAccidentDto(year,count));
//                 }

//             }
//         }
//         return data;
//     }

// }
