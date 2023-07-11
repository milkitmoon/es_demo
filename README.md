# 1. 개요
- 새로이 갱신된 spring boot 3.x 와 더불어 elasticsearch 와 연동하는 간단한 예제입니다.


# 2. 기술명세
- 언어 : Java 18
- 프레임워크 : spring boot 3.0.6
- 의존성 & 빌드 관리 : gradle
- Persistence API : JPA
- Database : h2db
- Document DB : Elasticsearch
- 인증/권한관리 : spring security 6.x (JWT 토큰 적용)
- OAS : springdoc-openapi v2.1.0 (OpenAPI 3)

> spring boot 3.x 적용에 대하여
- spring boot 3.x 의 경우 java17 이상을 필요로하며 기존 2.x 에서 deprecated 된 기능들이 있습니다.
- spring security 에서의 변경사항과 javax 패키지를 jakarta 패키지로 변경해야 하는 등 spring boot 2.x 를 사용할 경우 마이그레이션이 필요합니다. (https://covenant.tistory.com/279, https://www.baeldung.com/spring-boot-3-migration 과 같은 문서를 참조해 주세요)

> elasticsearch 실행에 대하여
- junit 테스트를 수행하거나 예제를 실행해 복고 싶다면 elasticsearch를 실행해야 합니다
- docker 를 이용해 elasticsearch 를 실행할 수 있습니다. (docker-compose 를 사용하면 좀 더 체계화된 구성으로 elasticsearch를 활용할 수 있습니다)
- 여기서는 테스트임으로 docker 명령어로 간단하게 실행 시키는 예제를 소개드립니다 (junit 테스트를 위해 elasticsearch Testcontainers로 실행시키는 방법이 있는데 별로 추천드리지는 않습니다. https://java.testcontainers.org/modules/elasticsearch/)
```
  $ docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" docker.elastic.co/elasticsearch/elasticsearch:8.8.0
```

> springdoc API명세 페이지 보기
- 어플리케이션 기동 후 아래와 같이 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 접속하여 API페이지를 조회할 수 있습니다.
- springdoc-openapi v2.x 기반으로 설정되었습니다.
- 기존 springfox, swagger2 기반으로 설정했을 경우 다음 문서를 참조하여 migration 을 진행해야 합니다 ->  https://springdoc.org/#migrating-from-springfox
<img src="https://github.com/milkitmoon/es_demo/assets/61044774/9b522710-abb6-4d17-af55-34dd13ef94fa" width="70%"></img>

# 3. 기본 기능 명세
> 사용자 정보를 등록하고 조회 시 elasticsearch 의 문서를 검색하도록 하는 API를 제공합니다.
- 회원가입 : 사용자는 사용자 정보를 입력하여 회원가입을 합니다.
  * 이메일, 비밀번호, 사용자/관리자 여부를 파라미터로 받습니다.
  * 이미 사용 중인 이메일로는 가입할 수 없어야 합니다.
- 로그인 : 가입한 회원정보로 로그인을 합니다.
    * 이메일과 비밀번호를 이용하여 로그인합니다.
- 목록조회 : 사용자들은 전체 사용자정보를 조회할 수 있습니다.
  * <span style="color:blue">사용자의 조건에(useId, role, description 등) 따라 elasticsearch 를 통해 검색을 합니다.</span>
  * 페이징 형태로 사용자 정보가 조회됩니다.

# 4. 테이블 설계
기본 테이블 설계입니다.
<br>
## 4.1 USER (사용자정보)
- 사용자 정보가 담겨있는 테이블.  
<img src="https://user-images.githubusercontent.com/61044774/103405371-34558a00-4b9a-11eb-9911-2356d1ce6eb6.jpg" width="80%"></img>
  * ID : 사용자에 대한 고유정보 (PK)
  * USER_ID : 사용자계정 (Unique)
  * PASSWORD : 사용자 비밀번호
  * ROLE : 사용자구분
  * USE_YN : 사용여부 (Y,  N)
  * DESCRIPTION : 사용자 정보
  * INST_TIME : 정보 등록시간
  * UPD_TIME : 정보 갱신시간
  * INST_USER : 정보 등록계정
  * UPD_USER : 정보 갱신계정

# 5. 실행

## 실행 하기

> main Application 실행하기
- gradle bootRun Task 를 통해 main Application 을 IDE 에서 바로 실행할 수 있습니다. (IntelliJ 기준)
 <img src="https://github.com/milkitmoon/es_demo/assets/61044774/15de0916-bf18-4dfd-8a13-41a51d4f5f33" width="60%"></img>


 # 6. 인증
 > 서버에서 제공되는 api를 호출하기 위해서는 먼저 인증을 수행해야 합니다.
 인증은 jwt 형식의 토큰방식으로 진행됩니다.

## 6. 1 회원가입 
- http://localhost:8080/api/user/signup URL로 POST로 인증정보를 전달합니다.
 <img src="https://github.com/milkitmoon/es_demo/assets/61044774/c64dac78-d0ca-49b0-8bae-3518d5109062" width="90%"></img>
  * 계정ID, 패스워드, 사용자 구분값을 넣습니다.
  * 사용자 계정 ID는 Email 형식이어야 합니다.
  * 사용자 구분값은 <span style="color:blue">ROLE_MEMBER</span> (사용자) 와 <span style="color:blue">ROLE_ADMIN</span> (관리자) 로 지정합니다.
  * 사용자 계정은 POST Body에 다음과 같은 형식의 json 값을 설정합니다.
  ```javascript
  {
	"userId" : "testNew@milkit.com",       /*  사용자 계정 ID */
	"password" : "test",                   /*  사용자 계정 비밀번호 */
	"role" : "ROLE_MEMBER"                 /*  사용자구분 (ROLE_MEMBER:사용자, ROLE_ADMIN:관리자) */
  }
  ```
## 6. 2 인증요청
- http://localhost:8080/login URL로 POST로 인증정보를 전달합니다.
<img src="https://github.com/milkitmoon/es_demo/assets/61044774/88cfd257-1121-4780-a286-1efbfd7bd4eb" width="90%"></img>
  * 사용자 계정은 가입한 사용자 정보로 지정할 수 있습니다.
  * 사용자 계정은 POST Body에 다음과 같은 형식의 json 값을 설정합니다.
  ```javascript
  {
    "userId" : "testNew@milkit.com",       /*  사용자 계정 ID */
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
- http://localhost:8080/api/user?role=ROLE_MEMBER 등과 같이 서버에서 제공하는 api를 호출하여 API 명세에 제공된 정보를 요청합니다.
<img src="https://github.com/milkitmoon/es_demo/assets/61044774/1a619c80-c0d8-4d2e-b4c9-cd2e104976d8" width="90%"></img>
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
      "role": ROLE_MEMBER,
      "useYn": "Y",
      "description": "사용자 입니다"
    }
  }
  ```

  - 실패 시 (*예제)
  ```javascript
  {
    "code": "302",
    "message": "인증오류가 발생하였습니다. 사용자 계정명과 비밀번호를 확인해 주세요.",
    "value": null
  }
  ```

  ## 7.1 사용자 조회 API
  - 사용자는 사용자 목록을 조회할 수 있습니다.

    * URL : GET http://localhost:8080/api/user?page=[페이지번호]
    * 요청 Body
    ```javascript
    N/A
    ```
    * 정상 응답 Body
    ```javascript
    {
      "code": "0",
      "message": "성공했습니다",
      "value": {
          "content": [
              {
                  "userId": "milkit9@aaa.bbb",
                  "password": "$2a$10$QvXEA4XPPXyLyAKYU2EUvuyWhxJkjU2KR2lvwSVu4KMN/0BA9uIPy",
                  "role": "ROLE_MEMBER",
                  "useYn": "Y",
                  "description": null
              }
          ],
          "pageable": {
              "sort": {
                  ...
              },
              "offset": 0,
              ...
          },
          "totalPages": 1,
          "totalElements": 1,
          "last": true,
          "size": 10
          ...
      }
    }
    ```

# 8. 기능 및 예외사항에 대한 테스트 전략
- 기능 및 예외사항 테스트에 대한 예제입니다

  ## 8.1 서비스 테스트 예제
  ### 8.1.1 기능테스트
  ```java
  @Test
  @DisplayName("6. 사용자문서 조건검색 테스트.")
  public void searchByCondition_test() throws Exception {
          .
          .
          .
    Page<User> foundUsers = userHandlerService.searchByCondition(targetUser, pageable);
    User matchedUser = foundUsers.stream().filter(u -> u.getUserId().equals(userId)).findFirst().get();

    assertThat(userId).isEqualTo(matchedUser.getUserId());
  }
  ```
  ### 8.1.2 예외테스트
  ```java
  @Test
  @DisplayName("5. 잘못된 회원정보형식으로 회원가입을 요청했을 시 예외를 테스트")
  public void signup_user_info_exception_test() {
          .
          .
          .
    assertThatThrownBy(() -> userHandlerService.signup(user) )
				.isInstanceOf(ServiceException.class)
				.hasMessageContaining("이메일 형식이 아닙니다. 입력정보를 확인해 주세요");
  }
  ```

  ## 8.2 API 테스트 예제
  ### 8.2.1 기능테스트
- mockito 를 통해 UserHandlerServiceImpl 의 실행결과를 mocking 하고 api의 연관된 부분만 테스트합니다 

  ```java
  @BeforeEach
  public void setup() {
      List<User> userList = new ArrayList<>();
      users = new PageImpl<>(userList);
          .
          .
          .
      userHandlerService = mock(UserHandlerServiceImpl.class);
      userController.setUserHandlerService(userHandlerService);
    }
  ```
  
  ```java
  @Test
  @DisplayName("1. 사용자 정보를 조회한다.")
  public void query_user_info_test() throws Exception {
          .
          .
          .
        when(userHandlerService.searchByCondition(userRequest, pageRequest)).thenReturn(users);
          .
          .
          .
        mvc.perform(MockMvcRequestBuilders.get("/api/user").params(requestParam)
            .header(AppCommon.JWT_HEADER_STRING, AppCommon.JWT_TOKEN_PREFIX+userJwtToken)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
    			.andDo(print())
    	        .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ErrorCodeEnum.ok.getCode()))
                .andExpect(jsonPath("message").value("성공했습니다"))
                .andExpect(jsonPath("value").isNotEmpty())
                .andExpect(jsonPath("$.value.content").isArray());
  }
  ```
  ### 8.2.2 예외테스트
- mockito 를 통해 UserHandlerServiceImpl 을 mocking 하여 ServiceException 을 raise 하게하고 오류과 발생한 api 의 결과값을 테스트합니다

```java
@Test
@DisplayName("2. 사용자가 잘못된 정보로 회원가입을 요청한다")
public void request_wrong_info_test() throws Exception {
        .
        .
        .
    userHandlerService = mock(UserHandlerServiceImpl.class);
    userController.setUserHandlerService(userHandlerService);
    when(userHandlerService.signup(newUser))
            .thenThrow(new ServiceException(ErrorCodeEnum.InvalidEmailFormException.getCode(), new String[]{newUser.getUserId()}));

    mvc.perform(MockMvcRequestBuilders.post("/api/user/signup")
                    .content(objectMapper.writeValueAsString(newUser))
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")).andDo(print())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("code").value(ErrorCodeEnum.InvalidEmailFormException.getCode()))
            .andExpect(jsonPath("message").value("이메일 형식이 아닙니다. 입력정보를 확인해 주세요. 계정ID:"+newUser.getUserId()))
            .andExpect(jsonPath("value").isEmpty())
}
```
