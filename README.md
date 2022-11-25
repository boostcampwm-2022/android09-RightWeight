# Right Weight

**올바른 운동 습관 만들기**

## 프로젝트 개요

- 운동 루틴을 만들고, 기록하고, 공유할 수 있는 어플
- 사용자가 편하게 운동 루틴을 관리할 수 있고, 운동 기록을 통해 본인의 성장을 확인하고 지속적인 운동이 가능하도록 동기부여할 수 있는 서비스입니다.

## 프로젝트 목적

- 운동 기록을 통해 운동 시간과 운동 강도를 확인하고 조절할 수 있게 한다.
- 꾸준히 운동하였음을 달력을 통해 확인하고 성취감을 얻을 수 있게 한다.
- 루틴을 설정하여 날짜마다 수행할 운동 내용을 확인할 수 있게 한다.
- 루틴을 건너뛸 수 있게하여 운동을 못간 날이 있더라도 다음 운동을 바로 확인하여 혼동이 생기지 않도록 한다.
- 자신이 만든 루틴을 다른 유저들과 공유할 수 있고, 다른 유저들의 루틴을 받아 수행할 수 있다.

## 주요 기능

### 운동 기록
|홈화면|운동 시작 페이지|달력 화면|관리 화면|
|:----:|:----:|:----:|:----:|
|![홈 화면](https://user-images.githubusercontent.com/49135657/201580580-83296b77-341b-4aa5-9305-9d984519c05e.png)|![운동 시작 페이지](https://user-images.githubusercontent.com/49135657/201580612-524baaaa-3077-4246-a714-0081ba642bb0.png)|![달력 화면](https://user-images.githubusercontent.com/49135657/201580639-d9fced32-d63f-4073-86d8-00ec36373c79.png)|![관리 화면](https://user-images.githubusercontent.com/49135657/201580645-255d8d4e-e930-477b-b5d5-a88756c27528.png)|




- 오늘 수행할 운동 내용 확인
- 타이머를 통한 운동 기록
- 달력을 통한 운동 기록 확인과 루틴 건너뛰기

### 루틴 공유

|공유(커뮤니티)페이지|루틴 열람 페이지|루틴 생성/수정 페이지|
|:----:|:----:|:----:|
|![공유 화면](https://user-images.githubusercontent.com/49135657/201581154-be02271b-a5ea-4ddb-9968-493c6b1d07d0.png)|![루틴 열람 페이지 - type2](https://user-images.githubusercontent.com/49135657/201581174-604483ab-c113-41ae-986d-6270cecdda52.png)|![수정 페이지 - type2](https://user-images.githubusercontent.com/49135657/201581226-da549dce-74cd-4056-9c82-cfe724309c37.png)


- 나만의 루틴 생성과 공유
- 다른 사람이 공유한 루틴 복제

## 데모

<details>
<summary>2주차 데모</summary>
<div markdown="1">    
  
### 2주차 
  ![image](https://user-images.githubusercontent.com/75258748/202417931-3e4a332e-1975-478f-8b60-5c25ddccfecc.png)


### 데모 시나리오
- 로그인 화면에서 로그인
- 홈 화면에서 드로어 열어보기
- 하단 네비게이션으로 루틴 관리 페이지 이동
- 추가 버튼으로 루틴 생성/수정/저장 페이지로 이동
- ~~루틴 설정 후 저장 버튼 터치~~
- ~~Room에 루틴이 저장되었는지 확인~~


  [2차 데모.webm](https://user-images.githubusercontent.com/75258748/202416457-e470e729-91c6-40e2-908a-f9595e662901.webm)
  
  [루틴 생성.webm](https://user-images.githubusercontent.com/54586491/202475664-7dc28303-1afa-400d-b15d-31d09d1c428e.webm)


</div>
</details>

<details>
<summary>3주차 데모</summary>
<div markdown="1">    
  
## 3주차 
  ![image](https://user-images.githubusercontent.com/64251968/202945564-11649bc3-8a57-46a1-af23-dcabcba115b4.png)

### 로그인

1. 계정 선택 후 구글 로그인 성공 시 홈 화면으로 진입한다.
2. 로그아웃 시 로그인 화면으로 돌아가게 된다.
3. 다시 로그인 버튼 클릭 시 로그아웃을 했기 때문에 계정 선택 화면이 다시 나오게 된다.
4. 앱 종료 후 다시 실행해도 로그인 여부를 판단하여 자동으로 홈 화면으로 이동하게 된다.

https://user-images.githubusercontent.com/64251968/203969772-13c44996-92bf-48d9-aa77-9d5e3549dc5f.mov

### 루틴 선택

1. 홈화면에서 선택된 루틴이 없을 경우 루틴 버튼을 통해 루틴 관리 화면으로 이동 가능하다.
2. 루틴 관리 화면에서 자신의 루틴 목록을 확인 가능하며 루틴의 순서를 변경할 수 있다.
3. 루틴 하나를 클릭하면 루틴 상세 화면으로 이동한다.
4. 루틴의 상세 내용을 확인할 수 있고 루틴을 수행하려면 루틴 선택 버튼을 눌러 선택할 수 있다.
5. 홈화면에서 선택된 루틴의 이름을 확인하고 오늘 진행할 운동 목록을 볼 수 있다.

https://user-images.githubusercontent.com/64251968/203970405-3bb6a621-5087-4738-b3b1-0f0438d8f139.mov

### 루틴 열람/수정

1. 상세 화면에서 상단 메뉴의 수정 버튼을 통해 수정 화면으로 진입한다.
2. 3중첩 리사이클러뷰에서 Day와 운동을 분리하여 Day를 선택하면 하단에 해당 Day의 운동 목록이 나타난다.
3. 루틴의 내용을 수정하고 저장할 수 있다.
4. 루틴의 내용이 수정된 것을 볼 수 있고 운동을 클릭하여 세트 정보를 접고 펼 수 있다.

https://user-images.githubusercontent.com/64251968/203970449-94e95a23-5e6f-42c5-9e1a-5b90e31f0893.mov

### 운동 시작

1. 홈화면에서 운동 시작 버튼을 클릭 시 운동 시작 화면으로 진입한다.
2. 운동 기록을 위해 운동과 세트 정보를 수정할 수 있다.
3. 하단의 타이머를 조작해 운동을 시작하고 시간을 측정한다.
4. 앱이 백그라운드로 가도 foreground 서비스로 실행하여 상태바에서 타이머를 계속 확인할 수 있다.

https://user-images.githubusercontent.com/64251968/203970498-60bca1c3-95c3-4e7e-9a15-8de1db449b6c.mov

</div>
</details>
