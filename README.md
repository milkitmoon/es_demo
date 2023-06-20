# 1. 개요
- 사전과제로 **<span style="color:blue">택시 배차앱 API</span>** 기능을 구현하였습니다.


# 2. 기술명세
- 언어 : Java 1.8
- 프레임워크 : spring boot 2.3.4
- 의존성 & 빌드 관리 : gradle
- Persistence : JPA
- Database : sqlite
- OAS : swagger

> sqlite database 에 대하여
- 현재 프로젝트에서는 sqlite memory db 형태로 사용하고 있습니다.
- 만약 로컬에서 DB 정보를 확인하고자 할 경우에
/resource/application.yml 에서 url 정보를 'url: jdbc:sqlite:sqlite.db' 와 같이 변경해 주세요

> swagger API명세 페이지 보기
- 어플리케이션 기동 후 아래와 같이 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 접속하여 API페이지를 조회할 수 있습니다.  
<img src="https://user-images.githubusercontent.com/61044774/103403547-e76eb500-4b93-11eb-8d4f-2bd48c3797a4.jpg" width="90%"></img>

# 3. 개발 요구사항
> 택시 배차 서비스가 잘 동작할지 검증하기 위하여 다음과 같은 Json API를 제공합니다.
- 회원가입 : 사용자는 사용자 정보를 입력하여 회원가입을 합니다.
  * 이메일, 비밀번호, 승객/기사 여부를 파라미터로 받습니다.
  * 이미 사용 중인 이메일로는 가입할 수 없어야 합니다.
- 로그인 : 가입한 회원정보로 로그인을 합니다.
  * 이메일과 비밀번호를 이용하여 로그인합니다.
- 목록조회 : 사용자들은 전체 배차정보를 조회할 수 있습니다.
  * 기사 배차가 완료된 요청과 대기 중인 요청 모두 목록에 포함됩니다.
  * 배차요청시간, 배차완료시간, 배차상태 등이 목록 포함됩니다.
- 택시 배차 요청 : 사용자는 이동을 위해 택시의 배차를 요청합니다.
  * 사용자의 현재 주소정보를 입력받아 배차 정보를 전달합니다.
- 기사 배차 : 기사는 특정 택시 배차를 수락하여 배차합니다.
  * 기사가 배정되지 않은 배차정보에 기사를 배치합니다.
- <span style="color:blue">배차 취소 (임의 추가) : 사용자 혹은 기사는 배차된 정보를 취소할 수 있습니다.
  * 사용자 및 기사는 자신의 배차된 정보를 취소할 수 있습니다.
  * 배채요청/배차완료 정보만 취소가 가능합니다. (기 취소된 건 및 승객탑승/도착완료 건은 취소 불가능)
  </span>

# 4. 택시배차 서비스 테이블 설계
택시배차 서비스를 구현하기 위한 기본 테이블 설계입니다.
<br>
## 4.1 USER (사용자정보)
- 사용자 정보가 담겨있는 테이블.  
<img src="https://user-images.githubusercontent.com/61044774/103405371-34558a00-4b9a-11eb-9911-2356d1ce6eb6.jpg" width="80%"></img>
  * ID : 사용자에 대한 고유정보 (PK)
  * USER_ID : 사용자계정 (Unique)
  * PASSWORD : 사용자 비밀번호
  * ROLE : 사용자구분
  * USE_YN : 사용여부 (Y,  N)
  * INST_TIME : 정보 등록시간
  * UPD_TIME : 정보 갱신시간
  * INST_USER : 정보 등록계정
  * UPD_USER : 정보 갱신계정

## 4.2 ASSIGNMENT (배차정보)
- 사용자 및 택시기사의 배차에 대한 정보가 들어가 있는 테이블.
<img src="https://user-images.githubusercontent.com/61044774/103405528-cf4e6400-4b9a-11eb-9d12-f5f33f495a18.jpg" width="100%"></img>
  * ID : 배차에 대한 고유정보
  * USER_ID : 배차요청 사용자 계정 (PK)
  * DRIVER_ID : 배차수락 기사 계정
  * ADDRESS : 배차 주소 정보
  * REQ_ASSIGN_TIME : 배차 요청 시간
  * ASSIGN_TIME : 배차 시간
  * BOARD_TIME : 탑승 시간 (현재 미사용)
  * ARRIVE_TIME : 도착 시간 (현재 미사용)
  * CANCEL_TIME : 배차 취소 시간
  * CANCEL_ID : 배차 취소 계정
  * STATUS : 배차 상태 (1:배차요청, 2:배차완료, 3:승객탑승, 4:도착완료, 9:배차취소)

# 5. 실행

## 실행 하기

> 소스 main Application 실행하기
- com.milkit.app.DemoApplication 을 IDE에서 run하여 바로 실행할 수 있습니다.
 <img src="https://user-images.githubusercontent.com/61044774/98205672-de8aaa00-1f7b-11eb-8a54-2ea4ad48cad6.jpg" width="90%"></img>


 # 6. 인증
 > 서버에서 제공되는 api를 호출하기 위해서는 먼저 인증을 수행해야 합니다.
 인증은 jwt 형식의 토큰방식으로 진행됩니다.

## 6. 1 회원가입
> 서비스 테스트를 위하여 서비스 초기화 시 마다 ApplicationRunner를 통해 다음과 같이 **<span style="color:blue">자동으로 일부 계정이 등록</span>** 되도록 구현 되었습니다. 테스트 시 유의해 주시기 바랍니다.  
test@milkit.com (사용자 계정1)  
test2@milkit.com (사용자 계정2)  
driver@milkit.com (드라이버 계정1)  
driver2@milkit.com (드라이버 계정2)  
- http://localhost:8080/api/user/signup URL로 POST로 인증정보를 전달합니다.
<img src="https://user-images.githubusercontent.com/61044774/103405883-1b4dd880-4b9c-11eb-9e07-68ab1742232f.jpg" width="90%"></img>
  * 계정ID, 패스워드, 사용자 구분값을 넣습니다.
  * 사용자 계정 ID는 Email 형식이어야 합니다.
  * 사용자 구분값은 <span style="color:blue">ROLE_MEMBER</span> (사용자) 와 <span style="color:blue">ROLE_DRIVER</span> (기사) 로 지정합니다.
  * 사용자 계정은 POST Body에 다음과 같은 형식의 json 값을 설정합니다.
  ```javascript
  {
	"userID" : "testNew@milkit.com",       /*  사용자 계정 ID */
	"password" : "test",                   /*  사용자 계정 비밀번호 */
	"role" : "ROLE_MEMBER"                 /*  사용자구분 (ROLE_MEMBER:사용자, ROLE_DRIVER:기사) */
  }
  ```
## 6. 2 인증요청
- http://localhost:8080/login URL로 POST로 인증정보를 전달합니다.
<img src="https://user-images.githubusercontent.com/61044774/103409238-2e1ada00-4ba9-11eb-8c45-5aaee3e55860.jpg" width="90%"></img>
  * 사용자 계정은 admin / test 혹은 test / test로 지정할 수 있습니다.
  * 사용자 계정은 POST Body에 다음과 같은 형식의 json 값을 설정합니다.
  ```javascript
  {
    "userID" : "test@milkit.com",       /*  사용자 계정 ID */
    "password" : "test"                 /*  사용자 계정 비밀번호 */
  }
  ```
  * 사용자가 인증되었다면 서버는 Response body에 JWT Token 정보를 전달합니다.
  ```javascript
  {
    "code": "0",                      /*  응답코드    */
    "message": "성공했습니다",          /*  메시지    */
    "value": {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTk0iOiLqtIDrpqzsnpAiLCJhdXRoUm9sZSI6IlJPTEVfQURNSU4iLCJuYW1lIjoiYWRtaW4iLCJleHAiOjE2MDAyMzQxMjgsImlhdCI6MTYwMDIzMjMyOH0.hYTzcG5nDhdVn4OVbrrH7ybSLwBxq1Fm2O9A60uk8Zw",  /*  액세스 토큰 (API 이용시 헤더에 등록)    */
      "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTk0iOiLqtIDrpqzsnpAiLCJhdXRoUm9sZSI6IlJPTEVfQURNSU4iLCJuYW1lIjoiYWRtaW4iLCJleHAiOjE2MDE0NDE5MzAsImlhdCI6MTYwMDIzMjMzMH0.MZLH17FUuUqYzlZDQ2AZDcRnSvxT2QJJeLHhiwtJFDo", /*  리프레시 토큰 (토큰을 리시프레시 할 시 헤더에 등록)    */
      "tokenType": "bearer"       /*  인증 토큰 타입    */
    }
  }
  ```

## 6. 3 API 호출 예
- http://localhost:8080/api/taxi/request 등과 같이 서버에서 제공하는 api를 호출하여 API 명세에 제공된 정보를 요청합니다.
<img src="https://user-images.githubusercontent.com/61044774/103406262-76cc9600-4b9d-11eb-9943-7201fee73523.jpg" width="90%"></img>
  * <span style="color:red">사용자는 API 호출 시 상기 [인증요청] 에서 응답받은 JWT accessToken 값을 HTTP Header의 Authorization 항목에 입력하여 전송하여야 합니다.</span>  
    ex) Request HEADER의 Authorization 값 형식
    ```html
    Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9NRU1CRVIiLCJuYW1lIjoidGVzdEBtaWxraXQuY29tIiwiZXhwIjoxODAwMzYyNTg2LCJpYXQiOjE2MDk0MDI1ODZ9.oBn9jwoglE0w5lzh5arymMg2asd_6eHhFM7Nv3EHy3E
    ```
  * <span style="color:blue">서버는 API Request Header의 JWT Token을 확인하고 권한확인 및 접근제어를 수행할 수 있습니다.</span>

  # 7. API 명세
  아래의 정보는 어플리케이션 기동 후 swagger를 ([http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)) 통해서도 확인하실 수 있습니다.

  > Tips2 : API Response 형식

  | 구분    | 내용             | 비고                             |
  | :------ | :--------------- | :------------------------------- |
  | code    | 응답코드         | 0 : 성공, 그 외 코드 : 실패                 |
  | message | 메시지           | 성공 혹은 실패 시의 메시지 |
  | value    | 결과 값 |                                  |

  - 성공 시 (*예제)
  ```javascript
  {
    "code": "0",                /*  응답코드 */
    "message": "성공했습니다",    /*  메시지 */
    "value": {                   /*  결과값 */
      "id": 5,
      "userID": "test@milkit.com",
      "driverID": null,
      "address": "성남시 분당구 정자동 123",
      "cancelID": null,
      "status": "배차요청",
      "reqAssignTime": "2020-12-31 19:51:12",
      "assignTime": "",
      "cancelTime": ""
    }
  }
  ```

  - 실패 시 (*예제)
  ```javascript
  {
    "code": "504",
    "message": "사용자 정보가 일치하지 않아 배차 취소를 할 수 없습니다. 배차ID:5, 배차취소요청계정:test2@milkit.com",
    "value": null
  }
  ```

  ## 7.1 회원가입 API
  - 사용자/기사는 회원가입을 할 수 있습니다.

    * URL : POST http://localhost:8080/api/user/signup
    * 요청 Body
    ```javascript

    {
	  "userID" : "testNew@milkit.com", /*  계정 ID */
	  "password" : "test",             /*  비밀번호 */
	  "role" : "ROLE_MEMBER"           /*  사용자구분 (ROLE_MEMBER:사용자, ROLE_DRIVER:기사) */
    }

    ```
    * 정상 응답 Body
    ```javascript

    {
      "code": "0",                /*  응답코드 */
      "message": "성공했습니다",    /*  메시지 */
      "value": null
    }
    ```

  - 예외 1. 이미 같은 ID로 등록된 사용자가 있을 경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "308",
      "message": "사용자 계정이 이미 존재합니다. 다른 계정명으로 사용해 주세요. 계정ID:test@milkit.com",
      "value": null         
    }
    ```

  - 예외 2. 사용자 ID가 이메일 형식이 아닐경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "306",
      "message": "이메일 형식이 아닙니다. 입력정보를 확인해 주세요. 계정ID:testmilkit.com",
      "value": null         
    }
    ```

  ## 7.2 로그인 API
  - 사용자/기사는 서비스를 이용하기 위해 로그인을 할 수 있습니다.

    * URL : PUT http://localhost:8080/login
    * 요청 Body


    ```javascript
    {
	  "userID" : "testNew@milkit.com", /*  계정 ID */
	  "password" : "test"              /*  비밀번호 */
    }
    ```
    * 정상 응답 Body
    ```javascript
    {
      "code": "0",                      /*  응답코드    */
      "message": "성공했습니다",          /*  메시지    */
      "value": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTk0iOiLqtIDrpqzsnpAiLCJhdXRoUm9sZSI6IlJPTEVfQURNSU4iLCJuYW1lIjoiYWRtaW4iLCJleHAiOjE2MDAyMzQxMjgsImlhdCI6MTYwMDIzMjMyOH0.hYTzcG5nDhdVn4OVbrrH7ybSLwBxq1Fm2O9A60uk8Zw",  /*  액세스 토큰 (API 이용시 헤더에 등록)    */
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTk0iOiLqtIDrpqzsnpAiLCJhdXRoUm9sZSI6IlJPTEVfQURNSU4iLCJuYW1lIjoiYWRtaW4iLCJleHAiOjE2MDE0NDE5MzAsImlhdCI6MTYwMDIzMjMzMH0.MZLH17FUuUqYzlZDQ2AZDcRnSvxT2QJJeLHhiwtJFDo", /*  리프레시 토큰 (토큰을 리시프레시 할 시 헤더에 등록)    */
        "tokenType": "bearer"       /*  인증 토큰 타입    */
      }
    }
    ```

  - 예외 1. 인증정보가 올바르지 않을 경우
    * 응답 Body
    ```javascript
    {
      "code": "302",
      "message": "인증오류가 발생하였습니다. 사용자 계정명과 비밀번호를 확인해 주세요",
      "value": null         
    }
    ```

  ## 7.3 목록조회 API
  - 사용자/기사는 배차 전체 목록을 조회할 수 있습니다.

    * URL : GET http://localhost:8080/api/taxi/query?page=[페이지번호]
    * 요청 Body
    ```javascript
    N/A
    ```
    * 정상 응답 Body
    ```javascript
    {
      "code": "0",                      /*  응답코드    */
      "message": "성공했습니다",          /*  메시지    */
      "value": [
        {
          "id": 5,                                  /*  배차ID    */
          "userID": "test@milkit.com",              /*  배차요청 계정    */
          "driverID": "driver@milkit.com",          /*  배차수락 드라이버 계정    */
          "address": "성남시 분당구 정자동 123",     /*  배차 주소정보    */
          "cancelID": null,
          "status": "배차완료",                     /*  배차상태    */
          "reqAssignTime": "2020-12-31 19:51:12",   /*  배차요청시간    */
          "assignTime": "2020-12-31 19:51:19",      /*  배차완료시간    */
          "cancelTime": ""
        }
      ]
    }
    ```

  ## 7.4 택시배차 요청 API
  - 사용자는 택시 배차를 요청할 수 있습니다.

    * URL : GET http://localhost:8080/api/taxi/request
    * 요청 Body
    ```javascript
    {
	  "address" : "성남시 분당구 정자동 123"    /*  배차 주소  */
    }
    ```
    * 정상 응답 Body
    ```javascript
    {
      "code": "0",                                /*  응답코드    */
      "message": "성공했습니다",                    /*  메시지    */
      "value": {
        "id": 5,                                  /*  배차ID    */
        "userID": "test@milkit.com",              /*  배차요청 계정    */
        "driverID": null,          /*  배차수락 드라이버 계정    */
        "address": "성남시 분당구 정자동 123",     /*  배차 주소정보    */
        "cancelID": null,
        "status": "배차요청",                     /*  배차상태    */
        "reqAssignTime": "2020-12-31 19:51:12",   /*  배차요청시간    */
        "assignTime": "",                         /*  배차완료시간    */
        "cancelTime": ""
      }
    }
    ```

  - 예외 1. 사용자 권한이 아닌 계정이 배차를 요청했을 시 경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "304",
      "message": "사용자 구분에 맞지 않는 요청을 하고 있습니다. 사용자구분:ROLE_DRIVER, 요청:배차요청 API",
      "value": null         
    }
    ```

  - 예외 2. 사용자가 주소정보를 너무크게 입력했을 경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "311",
      "message": "주소정보의 길이가 너무 깁니다. 입력 주소정보 크기를 확인해 주세요",
      "value": null         
    }
    ```

  ## 7.5 기사 배차 API
  - 드라이버는 배차정보를 지정하여 배차수락을 할 수 있습니다.

    * URL : PUT http://localhost:8080/api/taxi/assign
    * 요청 Body
    ```javascript
    {
	  "id" : 5                                 /*  배차ID  */
    }
    ```
    * 정상 응답 Body
    ```javascript
    {
      "code": "0",                                /*  응답코드    */
      "message": "성공했습니다",                    /*  메시지    */
      "value": {
        "id": 5,                                  /*  배차ID    */
        "userID": "test@milkit.com",              /*  배차요청 계정    */
        "driverID": "driver@milkit.com",          /*  배차수락 드라이버 계정    */
        "address": "성남시 분당구 정자동 123",     /*  배차 주소정보    */
        "cancelID": null,
        "status": "배차완료",                     /*  배차상태    */
        "reqAssignTime": "2020-12-31 19:51:12",   /*  배차요청시간    */
        "assignTime": "2020-12-31 19:51:19",      /*  배차완료시간    */
        "cancelTime": ""
      }
    }
    ```

  - 예외 1. 택시 배차가 완료된 배차건을 수락 요청 시 예외
    * 응답 Body
    ```javascript
    {
      "code": "502",
      "message": "이미 다른 드라이버로 배차가 완료된 정보입니다. 배차ID:5",
      "value": null         
    }
    ```

  - 예외 2. 드라이버 권한이 아닌 계정이 배차를 수락했을 시 예외
    * 응답 Body
    ```javascript
    {
      "code": "304",
      "message": "사용자 구분에 맞지 않는 요청을 하고 있습니다. 사용자구분:ROLE_MEMBER, 요청:기사배차지정 API",
      "value": null         
    }
    ```

  ## 7.6 배차 취소 API
  - 사용자/드라이버는 배차정보를 취소 할 수 있습니다.

    * URL : PUT http://localhost:8080/api/taxi/cancel
    * 요청 Body
    ```javascript
    {
	  "id" : 5                                 /*  배차ID  */
    }
    ```
    * 정상 응답 Body
    ```javascript
    {
      "code": "0",                                /*  응답코드    */
      "message": "성공했습니다",                    /*  메시지    */
      "value": {
        "id": 5,                                  /*  배차ID    */
        "userID": "test@milkit.com",              /*  배차요청 계정    */
        "driverID": "driver@milkit.com",          /*  배차수락 드라이버 계정    */
        "address": "성남시 분당구 정자동 123",     /*  배차 주소정보    */
        "cancelID": "test@milkit.com",            /*  배차취소요청 계정    */
        "status": "배차취소",                     /*  배차상태    */
        "reqAssignTime": "2020-12-31 19:51:12",   /*  배차요청시간    */
        "assignTime": "2020-12-31 19:51:19",      /*  배차완료시간    */
        "cancelTime": "2020-12-31 20:36:57"       /*  배차완료시간    */
      }
    }
    ```

  - 예외 1. 택시 배차상태가 취소불가할 경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "503",
      "message": "배차 취소는 배차요청 및 배차완료 상태일때만 가능합니다. 배차상태:배차취소",
      "value": null         
    }
    ```

  - 예외 2. 사용자 자신이 요청하지 않은 배차건을 취소하고자 할 경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "504",
      "message": "사용자 정보가 일치하지 않아 배차 취소를 할 수 없습니다. 배차ID:5, 배차취소요청계정:test2@milkit.com",
      "value": null         
    }
    ```

  - 예외 3. 택시드라이버는 자신이 수락하지 않은 배차건을 취소하고자 할 경우 예외
    * 응답 Body
    ```javascript
    {
      "code": "505",
      "message": "드라이버 정보가 일치하지 않아 배차 취소를 할 수 없습니다. 배차ID:5, 배차취소요청계정:driver2@milkit.com",
      "value": null         
    }
    ```

# 8. 기능 및 예외사항에 대한 테스트 전략
- 택시 배차 서비스의 기능 및 예외사항을 테스트 하기 위하여 서비스 및 API 테스트 항목을 작성하였습니다.

  ## 8.1 서비스 테스트 예제
  ### 8.1.1 기능테스트
  ```java
  @Test
  @DisplayName("1. 사용자가 요청한 택시 배차를 수락한다.")
  public void assign_test() throws Exception {
          .
          .
          .
    AssignRequest request = new AssignRequest(assignment.getId());
    Assignment result = assignmentHandlerService.process(driverHeaders, request);

    assertTrue(
        result != null
        && result.getDriverID().equals(request.getUserID())
    );
  }
  ```
  ### 8.1.2 예외테스트
  ```java
  @Test
  @DisplayName("2. 택시 배차가 완료된 배차건을 수락 요청 시 예외를 테스트 한다.")
  public void already_assign_exception_test() throws Exception {
          .
          .
          .
    ServiceException exception = assertThrows(ServiceException.class, () -> {
        Assignment result = assignmentHandlerService.process(driver2Headers, request);			// 2번째가 드라이버가 같은 배차정보 배차수락
    });

    assertTrue( exception.getCode().equals("502"));
  }
  ```

  ## 8.2 API 테스트 예제
  ### 8.2.1 기능테스트
  ```java
  @Test
  @DisplayName("1. 사용자가 요청한 택시 배차를 취소한다.")
  public void cancel_test() throws Exception {
          .
          .
          .
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/taxi/cancel")
          .header(AppCommon.JWT_HEADER_STRING, AppCommon.JWT_TOKEN_PREFIX+userJwtToken)
          .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("code").value("0"))
            .andExpect(jsonPath("message").value("성공했습니다"))
            .andExpect(jsonPath("value.status").value(StatusEnum.CANCEL.getDescription()))
            .andExpect(jsonPath("value.cancelID").value(assignment.getUserID()))
          ;
  }
  ```
  ### 8.2.2 예외테스트
  ```java
  @Test
  @DisplayName("2. 택시 배차가 완료된 배차건을 수락 요청 시 예외를 테스트 한다.")
  public void already_assign_exception_test() throws Exception {
          .
          .
          .
      ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.put("/api/taxi/assign")
          .header(AppCommon.JWT_HEADER_STRING, AppCommon.JWT_TOKEN_PREFIX+driver2JwtToken)
          .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("code").value("502"))
          .andExpect(jsonPath("value").isEmpty())
          ;
  }
  ```
