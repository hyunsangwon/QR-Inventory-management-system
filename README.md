# inventory
# QR-service project :relaxed: :baby_chick:

씨앤테크 QR 서비스 프로젝트

# Summary

QR스티커로 자산을 편리하게 체크하기 위해 만드는 프로젝트 입니다.  
고가이거나 차량·건설기계처럼 이동성이 높은 동산, 도난 우려가 높은 것에 대해선 IoT 단말기를 활용한다.  
저가나 이동성이 낮은 자산, 원재료·반제품·완제품 등 재고자산에는 QR코드를 이용할 계획이다.  

# Development stack
##### Cloud stack
- AWS Beanstalk container  
- Lambda (Python 3.6v)  
- API gateway  
- S3  
- AWS Rekognition

##### Sofeware stack
- Spring Boot (2.2.6 v)  
- MariaDB (10.2.21 v)  
- Mybatis (2.0.1 v)  
- Thymeleaf  
- Gradle

# Required technology
- OCR  
OCR이란 Optical Character Reader(or Recognition), 즉 '광학 문자 인식'이라고 하는 기술  
빛을 이용해서 문자, 기호, 마크 등를 인식하는 기술 이라고 보면 된다.

- QR Code  
QR이란 Quick Response로 바코드 보다 수많은 정보를 입력할 수 있다.  
QR안에 특정 코드를 입력후 자산과 매칭 후 자산을 주기적으로 관리하고자 한다.  

# Organizer
 - 현상원 (master)
 - 김신애 (designer)
