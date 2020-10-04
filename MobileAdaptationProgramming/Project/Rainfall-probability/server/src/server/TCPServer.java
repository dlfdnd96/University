package server;

import java.net.*;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TCPServer {

    public static void main(String[] args) {
        ServerSocket serversocket = null;
        Socket socket = null;
        PythonScript ps = new PythonScript();

        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                ps.startPy();
            }
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(runnable, 0, 60, TimeUnit.SECONDS);

        try {
            serversocket = new ServerSocket(5001);
            socket = serversocket.accept();

            System.out.println("Java: 연결 성공");

            Thread thread = new receiverthread(socket);

            thread.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                serversocket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}

class receiverthread extends Thread {

    private Socket socket;
    private MariaDatabase my = new MariaDatabase();

    receiverthread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            String s = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream();

            while (true) {
                String str = in.readLine();

                System.out.println("Java: " + str);
                if (str.equals("weather")) {
                    String[] naver_result = null;
                    ArrayList<String> dht22_result = null;

                    naver_result = my.getNaverCrawlingData();
                    dht22_result = my.getDht22Data();

                    for (int i=0; i<naver_result.length; i++) {
                        System.out.println("Java: " + naver_result[i]);
                        out.write(naver_result[i].getBytes());
                    }

                    for (int i=0; i<dht22_result.size(); i++) {
                        System.out.println("Java: " + dht22_result.get(i));
                        out.write(dht22_result.get(i).getBytes());
                    }
                }

                if (str == null)
                    break;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}

class MariaDatabase {

    private ResultSet naverCrawling = null;
    private ResultSet dht22 = null;
    private String[] naver_data = new String[8];
    private ArrayList<String> dht22_data = new ArrayList<>();

    public String[] getNaverCrawlingData() {
        Connection conn = null;
        Statement stmt = null;
        int index = 0;

        try{
            // 1. 드라이버 로딩
            Class.forName("org.mariadb.jdbc.Driver");

            // 2. 연결하기
            String url = "jdbc:mariadb://127.0.0.1:3306/mobile_adaptation_programming";
            conn = DriverManager.getConnection(url, "root", "abcd1234");

            // 3. 쿼리 수행을 위한 Statement 객체 생성
            stmt = conn.createStatement();

            // 4. SQL 쿼리 작성
            // 주의사항
            // 1) JDBC에서 쿼리를 작성할 때는 세미콜론(;)을 빼고 작성한다.
            // 2) SELECT 할 때 * 으로 모든 칼럼을 가져오는 것보다
            //   가져와야 할 칼럼을 직접 명시해주는 것이 좋다.
            // 3) 원하는 결과는 쿼리로써 마무리 짓고, java 코드로 후작업 하는 것은 권하지 않음
            // 4) 쿼리를 한 줄로 쓰기 어려운 경우 들여쓰기를 사용해도 되지만 띄어쓰기에 유의 !!
            String naverCrawlingSQL = "SELECT hour, temperature, humidity FROM weather_info";

            // 5. 쿼리 수행
            // 레코드들은 ResultSet 객체에 추가된다.
            naverCrawling = stmt.executeQuery(naverCrawlingSQL);

            // 6. 실행결과 출력하기
            while(naverCrawling.next()){
                // 레코드의 칼럼은 배열과 달리 0부터 시작하지 않고 1부터 시작한다.
                // 데이터베이스에서 가져오는 데이터의 타입에 맞게 getString 또는 getInt 등을 호출한다.
                String hour = naverCrawling.getString(1);
                String temperature = naverCrawling.getString(2);
                String humidity = naverCrawling.getString(3);

                System.out.println("Java: " + hour + " " + temperature + " " + humidity);

                naver_data[index] = hour + "," + temperature + "," + humidity + ";";
                index++;
            }
        }
        catch( ClassNotFoundException e){
            System.out.println("Java: 드라이버 로딩 실패");
        }
        catch( SQLException e){
            System.out.println("Java: SQL 에러: " + e);
        }
        finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                    System.out.println("DB Connection 종료");
                }
                if( stmt != null && !stmt.isClosed()){
                    stmt.close();
                    System.out.println("DB statement 종료");
                }
                if( naverCrawling != null && !naverCrawling.isClosed()){
                    naverCrawling.close();
                    System.out.println("DB query 종료");
                }
            }
            catch( SQLException e){
                e.printStackTrace();
            }
        }

        return naver_data;
    }

    public ArrayList<String> getDht22Data() {
        Connection conn = null;
        Statement stmt = null;

        try{
            // 1. 드라이버 로딩
            Class.forName("org.mariadb.jdbc.Driver");

            // 2. 연결하기
            String url = "jdbc:mariadb://127.0.0.1:3306/mobile_adaptation_programming";
            conn = DriverManager.getConnection(url, "root", "abcd1234");

            // 3. 쿼리 수행을 위한 Statement 객체 생성
            stmt = conn.createStatement();

            // 4. SQL 쿼리 작성
            // 주의사항
            // 1) JDBC에서 쿼리를 작성할 때는 세미콜론(;)을 빼고 작성한다.
            // 2) SELECT 할 때 * 으로 모든 칼럼을 가져오는 것보다
            //   가져와야 할 칼럼을 직접 명시해주는 것이 좋다.
            // 3) 원하는 결과는 쿼리로써 마무리 짓고, java 코드로 후작업 하는 것은 권하지 않음
            // 4) 쿼리를 한 줄로 쓰기 어려운 경우 들여쓰기를 사용해도 되지만 띄어쓰기에 유의 !!
            String dht22SQL = "SELECT datetime, temperature, humidity FROM dht22_info";

            // 5. 쿼리 수행
            // 레코드들은 ResultSet 객체에 추가된다.
            dht22 = stmt.executeQuery(dht22SQL);

            // 6. 실행결과 출력하기
            while(dht22.next()) {
                // 레코드의 칼럼은 배열과 달리 0부터 시작하지 않고 1부터 시작한다.
                // 데이터베이스에서 가져오는 데이터의 타입에 맞게 getString 또는 getInt 등을 호출한다.
                String datetime = dht22.getString(1);
                String temperature = dht22.getString(2);
                String humidity = dht22.getString(3);

                System.out.println("Java: " + datetime + " " + temperature + " " + humidity + " " + humidity);

                dht22_data.add(datetime + "," + temperature + "," + humidity + ";");
            }
        }
        catch( ClassNotFoundException e){
            System.out.println("Java: 드라이버 로딩 실패");
        }
        catch( SQLException e){
            System.out.println("Java: SQL 에러: " + e);
        }
        finally{
            try{
                if( conn != null && !conn.isClosed()){
                    conn.close();
                    System.out.println("DB Connection 종료");
                }
                if( stmt != null && !stmt.isClosed()){
                    stmt.close();
                    System.out.println("DB statement 종료");
                }
                if( dht22 != null && !dht22.isClosed()){
                    dht22.close();
                    System.out.println("DB query 종료");
                }
            }
            catch( SQLException e){
                e.printStackTrace();
            }
        }

        return dht22_data;
    }

}

class PythonScript {

    public void startPy() {
        String command = "../../../crawling/venv/bin/python3 ../../../crawling/naver_weather.py";

        String s = null;

        try {
            // print a message
            System.out.println("Executing naver_crawling.py");
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println("error:" + s);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}