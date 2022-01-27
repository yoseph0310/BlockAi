# BlockAi
> 블록체인과 AI를 활용한 본인인증 플랫폼

공인인증서를 사용한 번거로운 본인인증, 개인정보 유출이 걱정되는 간편 본인인증에 대한 걱정.

블록체인과 AI를 활용한 솔루션, ```BlockAi```가 제공합니다.


## 주요기능

- DID(디지털 신분증) 발급
- DID로 신분 인증

## 세부기능
|구분|기능|설명|비고|
|:---|:---|:---|:---|
|1|DID(디지털 신분증) 발급|가입시 문자인증으로 1차 본인인증 -> 사용자의 안면과 음성 데이터를 입력 -> RSA 알고리즘으로 생체 데이터를 암호화 -> 암호화된 정보를 블록체인에 저장||
|2|DID로 신분 인증|전화번호로 1차 인증하여 DID를 조회 -> 인증자의 안면 및 음성 데이터를 입력받음 -> 조회한 DID의 생체 정보와 대조하여 유사도 판별||


## 아키텍처

![142981989-b7cb6a58-a91c-46b9-a276-4c9d52996622](/uploads/7aba029cdbcc69d4080b304b226a829a/142981989-b7cb6a58-a91c-46b9-a276-4c9d52996622.png)


## 설치

OS 별 설치 진행 확인 후 작성 예정.

## 사용 예시

**메인페이지**
>로그인/회원가입/신원증명발급을 할 수 있는 메인페이지입니다.

![image](https://user-images.githubusercontent.com/43156636/142370883-f5771d7a-8801-4270-a3d0-8861da7e51ac.png)



**회원가입** 
>사용자 정보를 입력받아 회원가입을 할 수 있습니다. 휴대폰 인증번호를 통해 본인인증이 필요합니다.

![image](https://user-images.githubusercontent.com/43156636/142382116-eff19394-7c57-44eb-b22a-afce9d9bb6ec.png)



**로그인**
>아이디와 비밀번호로 로그인이 가능합니다.

![image](https://user-images.githubusercontent.com/43156636/142381618-0f4dd11c-1ab3-4eb6-9858-736b147591a3.png)


**발급**
> 미발급 시, '신원증명발급' 버튼을 통해 신원증명발급이 가능합니다.
> 안면과 음성을 차례로 등록해 신원증명발급을 합니다.

- 얼굴 등록

![face1](https://user-images.githubusercontent.com/31243566/141932516-6ea8a33a-e4ad-415f-8ecc-c902c2bd19f7.PNG)
![face2](https://user-images.githubusercontent.com/31243566/141932518-696bff7a-d1d5-4012-bdf4-e4100dc186cf.PNG)
![face3](https://user-images.githubusercontent.com/31243566/141932523-e085e4f6-1a55-4b4a-b096-9d64bcd654c3.PNG)


- 음성 등록

![voice1](https://user-images.githubusercontent.com/31243566/141932528-9c7c75e7-be07-4690-9ed9-564c028842d4.PNG)
![voice3](https://user-images.githubusercontent.com/31243566/141932529-aef537a4-1d48-42ae-ab45-3e7cc5d4704a.PNG)



**DID 조회** 
> 로그인 후에 신원증명발급 클릭시 기존에 발급한 DID를 조회할 수 있으며 재발급도 가능합니다.
> '신원증명서'를 클릭하면 발급하면서 등록한 얼굴 데이터와 음성 데이터를 확인할 수 있습니다.

![image](https://user-images.githubusercontent.com/43156636/142382981-b4522f73-7661-41a0-bed6-cb1349cb3769.png)



**인증** 
> 로그인 없이 전화번호를 입력한 뒤, 등록했던 신원증명발급을 통해 안면과 음성을 비교해 본인인증을 할 수 있습니다.

- 키오스크 인증시
  > 블록아이가 적용된 키오스크에서 블록아이로 인증하여 결제를 합니다. 

![image](https://user-images.githubusercontent.com/43156636/142385435-72434550-de83-446e-9e1f-58ee98f21008.png)

- 전화번호로 1차 인증
  > 먼저 DID를 발급한 계정의 전화번호로 1차 인증을 합니다.

![image](https://user-images.githubusercontent.com/43156636/142388752-94feca35-b259-4f22-a22d-c5993d0268ea.png)

- 생체 인증
  > 생체 인증을 위해 얼굴을 촬영하고 음성을 녹음합니다.

![image](https://user-images.githubusercontent.com/43156636/142386894-6fd2adce-941d-4e25-94bf-e6d50a8d007e.png)
![image](https://user-images.githubusercontent.com/43156636/142389015-951e35a6-e682-416c-a172-15686ea1ee12.png)
![image](https://user-images.githubusercontent.com/43156636/142388576-3fd2da94-e70b-451a-9c26-eb3de3c4baa9.png)
![image](https://user-images.githubusercontent.com/43156636/142388314-0f4dd5c9-c70c-4c34-951a-7540d3381907.png)


**인증 내역 조회** 
> 본인인증을 한 내역을 통해 발급처와 발급날짜를 확인할 수 있습니다.

![image](https://user-images.githubusercontent.com/43156636/142384814-febafa68-0010-4ddf-af3f-51240a31264d.png)

## 개발 설정

OS 별 개발 설정 진행 확인 후 작성 예정.

## 릴리즈 히스토리

히스토리 작성이 필요한 경우, 영어로 작성하되, 괄호 안에 한글 설명을 첨부하셔도 됩니다.

* 0.0.1
    * Work in progress

## 추가정보

- 서요셉 – ssafy_coach_85@ssafy.com
- 김경원 – ssafy_coach_70@ssafy.com
- 최나현 – ssafy_coach_59@ssafy.com


## 기여

1. 해당 프로젝트를 Fork 하세요 (<https://lab.ssafy.com/ssafy_opensource/5/-/forks/new>)
2. feature 브랜치를 생성하세요 (`git checkout -b feature/fooBar`)
3. 변경사항을 commit 하세요 (`git commit -am 'Add some fooBar'`)
4. 브랜치에 Push 하세요 (`git push origin feature/fooBar`)
5. 새로운 Merge Request를 요청하세요

## 라이선스

Distributed under the SGPL license. See [License](LICENSE) for more information.

<!-- Markdown link & img dfn's -->
[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square
[npm-url]: https://npmjs.org/package/datadog-metrics
[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square
[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics
[wiki]: https://lab.ssafy.com/ssafy_coach_5th/open-source-template/wikis/home
