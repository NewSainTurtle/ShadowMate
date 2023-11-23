## 🖥️ 프로젝트 소개

<div align="center">
<img src="https://github.com/NewSainTurtle/ShadowMate/assets/26339069/9356f239-6e20-4835-b178-3e213eb750a1" width="20%"/>
</div>

- 스터디 플래너를 작성하고, 사람들과 공유하며 학습 의지를 높이는 웹 개발 프로젝트
- URL 🔗 : https://shadowmate.kro.kr/

<br>

## 🗓️ 프로젝트 기간

**2023.07.10 ~ 2023.11.23 (총 20주)**

<br>

## 💁‍♂️ 팀원소개

<table>
<tr>
<td align="center"><a href="https://github.com/taboowiths"><img src="https://avatars.githubusercontent.com/u/85155789?v=4" width="127px;"/></br> <div>강정현</div><sub><b>Front-End</b></sub></a></br></td>
<td align="center"><a href="https://github.com/yeonsu-k"><img src="https://avatars.githubusercontent.com/u/83412032?v=4" width="127px;"/></br> <div>김연수</div><sub><b>Front-End</b></sub></a></br></td>
<td align="center"><a href="https://github.com/Kuuuna98"><img src="https://avatars.githubusercontent.com/u/26339069?v=4" width="127px;"/></br> <div>권유나</div><sub><b>Back-End</b></sub></a></br></td>
<td align="center"><a href="https://github.com/tgb02087"><img src="https://avatars.githubusercontent.com/u/63511273?v=4" width="127px;"/></br> <div>김강호</div><sub><b>Back-End</b></sub></a></br></td>
</tr>
</table>

<br>

## 📄 프로젝트 설계

- [기능 명세서](https://docs.google.com/spreadsheets/d/1iCx9oub-ukbSvVHsLhCt_lLD5cjQjy0gRG1uDOlRtr0/edit#gid=0)
- [API 명세서](https://docs.google.com/spreadsheets/d/1iCx9oub-ukbSvVHsLhCt_lLD5cjQjy0gRG1uDOlRtr0/edit#gid=535765190)
- [ERD](https://www.erdcloud.com/d/2qXiqqWiaAi4Yz8Em)
- [와이어프레임](https://www.figma.com/file/wu8VCSqyZSIY6xliHf9AXm/ShadowMate?type=design&node-id=11-2&mode=design&t=BZldJ085DLgXp7qp-0)

<br>

## 🛠️ 기술스택

### Back-End

| JAVA | Sprin Boot | Gradle | JWT | JPA | Junit5 | JaCoCo | SonarQube |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| 8 | 2.7.14 | 7.5.1 | 4.3.0 | 2.7.14 | 5.8.2 | 0.8.5 | 3.4.0 |

### Front-End

| React | TypeScript | React-reudx | Redux-toolkit | React-router-dom | Axios | Firebase | Webpack | Babel | SASS (SCSS) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| 18.2.20 | 5.1.6 | 4.2.1 | 1.9.6 | 6.14.2 | 1.5.0 | 10.5.2 | 5.88.2 | 7.22.10 | - |

### Database

| MySQL | Redis |
| :---: | :---: |
| 8.0.31 | 2.7.14 |

### CI/CD

| Ubuntu | Nginx | Docker | Jenkins | SSL |
| :---: | :---: | :---: | :---: | :---: |
| 20.04 LTS | 1.18.0 | 24.0.6 | 2.431 | - |

### TOOL

| GitHub | PostMan | Figma | Notion |
| --- | --- | --- | --- |

### IDE

| Visual Studio Code | IntelliJ |
| --- | --- |

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
│         ├── feature: 기능 구현 브랜치
│         ├── test: 테스트 코드 작성 브랜치
│         ├── fix: 버그 수정 브랜치
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
💡 [Part] Tag: Subject
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
