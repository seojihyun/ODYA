# Application introduction
위치기반 AR을 사용한 위치추적 어플인 'Findapple'은 단체여행시 여행객이 가이드를 놓쳐 무리에서 벗어난 경우 가이드의 위치, 다른 여행객들의 위치를 안내 해주기 위한 서비스 입니다.  GPS, AR 기능을 이용하여 현재 사용자 위치에 대한 상대방의 상대적 위치를 더 쉽게 파악할 수 있습니다.

#Prototype
가이드가 그룹을 생성하고 여행객들이 입장 했을때의 그룹메인 화면 입니다.
메인화면은 (Map, User List, Notice) 총 3가지의 탭으로 구성되어 있습니다.

## 1. Map tab
현재 그룹원들의 위치정보와 공지된 목적지의 위치 정보를 지도에 보여줍니다.
또한 해당 마커 클릭시 전화 연결, 위치안내(AR) 기능을 제공합니다.

![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/Map-1.png)
![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/Map-2.png)
![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/Map-3.png)

## 2. User tab
현재 그룹원들의 정보를 리스트로 보여주고, 일행 설정, map focus 기능을 제공합니다.

![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/User-2.png)
![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/User-3.png)

## 3. Notice tab
가이드의 공지사항 탭으로, 그룹의 가이드만 작성 가능합니다.
공지는 일반공지와 목적지 공지로 나뉘게 됩니다. 공지 작성시에는 모든 그룹원들에게 알림 메세지가 전송 됩니다.
* 일반공지 : 일반적인 공지사항을 작성하는 기능
* 목적지 공지 : 가이드와 여행객들간의 약속한 시간과 장소나 일정 장소등 위치 정보를 공지하는 기능


![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/Notice-1.png)
![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/Notice-2.png)
![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/Notice-3.png)

## 4. 위치 안내 (AR)
위치안내는 목적지, 가이드, 일행, 그룹원 모두 등 다양한 타입의 위치안내 서비스를 제공합니다.
단말기의 카메라가 바라보고있는 시야 안에 상대방이 존재하는 경우 마커가 보여집니다.
단말기의 gps센서를 통해 자신의 위치가 변경됨을 감지 하였을때마다 데이터를 update 합니다.
검색 반경 거리를 설정할수 있는 줌 버튼과 경보음 알림 버튼, 플래시 버튼이 오른쪽 아래 배치 되어 있습니다.

![ScreenShot](https://github.com/seojihyun/ODYA/blob/master/screenshot/AR-1.png)

# team introduction
* 한성대학교 컴퓨터공학과 1392019 서지현
* 한성대학교 컴퓨터공학과 1392003 권기훈
* 한성대학교 컴퓨터공학과 1392045 김누리
* 한성대학교 컴퓨터공학과 1392025 양해은

# Information
* Develop tools    : Android Studio, Mysql5.0, Apache Tomcat, Google Map API
* Develop language : JAVA, PHP
* Target Machine   : Android Smartphone (SDK Levels 19~)

# Sample Source Code
* Server Source Code :
* Android Source Code : https://github.com/seojihyun/ODYA.git

