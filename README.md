## 🖥️ 프로젝트 소개

<div align="center">
<img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/9356f239-6e20-4835-b178-3e213eb750a1" width="20%"/>
</div>
<br>

- 스터디 플래너를 작성하고, 사람들과 공유하며 학습 의지를 높이는 웹 개발 프로젝트
- URL 🔗 : https://shadowmate.kro.kr/

<br>

## 🗓️ 프로젝트 기간

- **`Ver.1.0`: 2023.07.10 ~ 2023.12.13 (총 22주)**
- **`Ver.2.0`: 2024.01.02 ~ 2024.01.26 (총 4주)**

<br>

## 💁‍♂️ 팀원소개

<table>
<tr>
<td align="center"><a href="https://github.com/taboowiths"><img src="https://avatars.githubusercontent.com/u/85155789?v=4" width="127px;"/></br> <div>강정현</div><sub><b>Front-End</b></sub></a></br></td>
<td align="center"><a href="https://github.com/yeonsu-k"><img src="https://avatars.githubusercontent.com/u/83412032?v=4" width="127px;"/></br> <div>김연수</div><sub><b>Front-End</b></sub></a></br></td>
<td align="center"><a href="https://github.com/Kuuuna98"><img src="https://avatars.githubusercontent.com/u/26339069?v=4" width="127px;"/></br> <div>권유나</div><sub><b>Back-End</b></sub></a></br></td>
<td align="center"><a href="https://github.com/tgb02087"><img src="https://avatars.githubusercontent.com/u/63511273?v=4" width="127px;"/></br> <div>김강호</div><sub><b>Back-End</b></sub></a></br></td>
</tr>
</table>

<br>

## 📄 프로젝트 설계

- [기능 명세서](https://docs.google.com/spreadsheets/d/1iCx9oub-ukbSvVHsLhCt_lLD5cjQjy0gRG1uDOlRtr0/edit#gid=0)
- [API 명세서](https://docs.google.com/spreadsheets/d/1iCx9oub-ukbSvVHsLhCt_lLD5cjQjy0gRG1uDOlRtr0/edit#gid=535765190)
- [ERD](https://www.erdcloud.com/d/2qXiqqWiaAi4Yz8Em)
- [와이어프레임](https://www.figma.com/file/wu8VCSqyZSIY6xliHf9AXm/ShadowMate?type=design&node-id=11-2&mode=design&t=BZldJ085DLgXp7qp-0)

<br>

## 🛠️ 기술스택

### Back-End

| JAVA | Sprin Boot | Gradle |  JWT  |  JPA   | Junit5 | JaCoCo | SonarQube |
| :--: | :--------: | :----: | :---: | :----: | :----: | :----: | :-------: |
|  8   |   2.7.14   | 7.5.1  | 4.3.0 | 2.7.14 | 5.8.2  | 0.8.5  |   3.4.0   |

### Front-End

|  React  | TypeScript | React-reudx | Redux-toolkit | React-router-dom | Axios | Firebase | Webpack |  Babel  | SASS (SCSS) |
| :-----: | :--------: | :---------: | :-----------: | :--------------: | :---: | :------: | :-----: | :-----: | :---------: |
| 18.2.20 |   5.1.6    |    4.2.1    |     1.9.6     |      6.14.2      | 1.5.0 |  10.5.2  | 5.88.2  | 7.22.10 |      -      |

### Database

| MySQL  | Redis  |
| :----: | :----: |
| 8.0.31 | 2.7.14 |

### CI/CD

|  Ubuntu   | Nginx  | Docker | Jenkins | SSL  |
| :-------: | :----: | :----: | :-----: | :--: |
| 20.04 LTS | 1.18.0 | 24.0.6 |  2.431  |  -   |

### TOOL

| GitHub | PostMan | Figma | Notion |
| ------ | ------- | ----- | ------ |

### IDE

| Visual Studio Code | IntelliJ |
| ------------------ | -------- |

<br>

## ⭐️ Git

<details>

<summary><strong> 🔍 브랜치 전략 및 컨벤션</strong></summary>

<div markdown=”1”>

#### **1. Git Workflow**

```
.
├── main: 배포 코드가 있는 브랜치
│    └── develop: 실제 개발 브랜치
│         ├── feature: 기능 구현 브랜치
│         ├── test: 테스트 코드 작성 브랜치
│         ├── fix: 버그 수정 브랜치
│         ├── refactor: 코드 스타일 수정 및 리팩토링을 위한 브랜치
│         └── docs: readme 등 문서를 작업하는 브랜치
└── hoxfix: main에서 버그를 수정할 브랜치
```

#### **2. Branch Naming**

```
⭐️ [해당하는 브랜치]/[front/back]-issue[이슈번호]
```

```
ex) develop
      ├── feature/front-issue25
      ├── fix/back-issue126
      └── ...
```

#### **3. Commit Convention**

```
💡 [Part] Tag: Subject
```

```
 ex) [FE] feat: 홈페이지 이미지추가
```

**[파트] 태그: 제목**의 형태로 ], **:** 뒤에는 **space**가 있다.

- `feat`: 기능 추가
- `test`: 테스트 코드 작성
- `modify`: 버그 아닌 코드 수정
- `fix`: 버그 수정
- `refactor`: 코드 리팩토링
- `style`: 코드 스타일(코드 컨벤션 추가) 수정
- `docs`: 문서 작업
- `design`: 프론트 CSS 수정
- `test`: 테스트 코드 작성
- `chore`: 프로젝트 설정 파일 수정
- `create`: 프로젝트 생성
- `rename`: 파일이나 폴더명을 수정하거나 옮기는 작업만 수행
- `remove`: 파일을 삭제하는 작업만 수행

</details>

</div>

<br>

## ⚡️ 업데이트 (2024.01.26)

1. **자동로그인**
   - 로그인 시 체크박스를 통해 자동로그인 설정이 가능합니다.
2. **할 일**
   - 할 일을 끝내지 못했을 때, 세모(△)를 사용해 표시할 수 있습니다.
   - 주별 및 일별 플래너에서 할 일의 순서를 변경할 수 있습니다.
3. **타임테이블**
   - 타임테이블 등록 시, 하나의 할 일에 여러 개의 시간을 입력할 수 있습니다.
4. **루틴**
   - 루틴을 통해 할 일을 일괄적으로 등록할 수 있습니다.
5. **소셜 검색 필터**
   - 소셜 검색 시 기간 필터를 통해 특정 기간의 게시물을 검색할 수 있습니다.
  
<br>

## 📺 화면 소개

#### 1. 랜딩

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/016f50e9-209e-45a6-a455-bf4f3a7e3873" width="70%"/></div>

<br>

#### 2. 회원가입/로그인

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/e2f29e61-ee2a-43e7-a60f-6f436ecd7eeb" width="70%"/></div>

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/fa8dfae6-1be7-4410-9687-0062b9ce86bf" width="70%"/></div>

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/c736b4bd-ce37-4fd2-8b96-554e235128e8" width="70%"/></div>

<br>

#### 3. 월별(메인)

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/3e869417-4db7-4c92-9bd6-93c329f9437f" width="70%"/></div>

<br>

#### 4. 주별 플래너

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/f61696a7-7274-405f-91e7-796d461c7994" width="70%"/></div>

<br>

#### 5. 일별 플래너

- 나의 플래너 
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/83412032/73c9d0ca-05b9-4d34-b540-80b2a04c4f88" width="70%"/></div>

- 다른 사용자 플래너
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/fc5f1063-f69a-44c1-b3c3-2827b803709d" width="70%"/></div>

<br>
      
#### 6. 소셜

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/2235a658-056f-4e4a-8f7e-fb095a6800f4" width="70%"/></div>

<br>


#### **7. 마이페이지**

- 회원정보 수정
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/2bfef5a4-66f2-4261-a85a-ad9fed30fb9d" width="70%"/></div>

- 플래너 공개범위 설정
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/e213b798-f420-4a3a-89f1-f16c6deff4ab" width="70%"/></div>

- 카테고리 설정
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/8ef4f224-1485-42ba-993b-42ededdda724" width="70%"/></div>

- 디데이 설정
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/4f94dbd7-1bf3-4c79-a3a8-1e28c8f8bfd9" width="70%"/></div>

- 친구 검색
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/e0af2003-a6f4-4540-a294-4a1f58b320ff" width="70%"/></div>

- 팔로우/팔로워 목록
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/2978e2f2-756f-4eab-8d03-4ef570103a41" width="70%"/></div>

- 루틴
<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/9a0965d9-1c18-42cb-92c9-9d1deb401954" width="70%"/></div>

<br>

#### 8. 다크모드 지원

<div align="center"><img src="https://github.com/NewSainTurtle/ShadowMate/assets/83412032/bb935308-8336-4fe8-a06c-390a3b6ce8b8" width="70%"/></div>


<br>
