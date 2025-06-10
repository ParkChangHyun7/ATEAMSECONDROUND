// package seoul.its.info.services.traffic.busstop;

// import org.springframework.boot.CommandLineRunner; // 스프링 부트 실행 시 자동 실행되는 인터페이스
// import org.springframework.stereotype.Component;  // 스프링 빈으로 등록하는 어노테이션
// import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입

// @Component // 이 클래스가 스프링이 관리하는 컴포넌트임을 명시 → run() 자동 실행 대상이 됨
// public class BusStopLoader implements CommandLineRunner {

//     @Autowired // BusStopService를 주입받음
//     private BusStopService busStopService;

//     @Override
//     public void run(String... args) {
//         try {
//             // 서버 시작 시 정류장 데이터를 불러오는 작업 수행
//             busStopService.loadBusStops();
//             System.out.println(" 정류장 데이터 로딩 완료");
//         } catch (Exception e) {
//             // 예외 발생 시 서버는 꺼지지 않고 로그만 출력됨
//             System.err.println(" 정류장 데이터 로딩 중 오류 발생: " + e.getMessage());
//             // e.printStackTrace(); // 자세한 에러 스택을 보고 싶으면 주석 해제
//         }
//     }
// }
