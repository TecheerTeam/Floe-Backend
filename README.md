# backend

# 계층구조
- Api
  - controller : 컨트롤러들이 들어있는 디렉토리
  - repository :  저장소들이 들어있는 디렉토리
  - service  : 비즈니스 서비스 로직들이 들어있는 디렉토리
      - util : 유틸로직들이 들어있는 디렉토리
        Domain
  - entity :  연결되는 객체들이 저장되는 디렉토리
  - dto : dto들이 저장되는 디렉토리
      - request : 요청관련 dto들이 들어있는 디렉토리
      - response : 응답관련 dto들이 들어있는 디렉토리
  - mapper : dto와 객체간 연결을 해주는 맵퍼들이 들어있는 디렉토리
- Global
  - aws : aws관련 로직들이 들어있는 디렉토리
  - exception: 예외 관련 로직들이 들어있는 디렉토리
  - docs : swagger같은 문서화와 관련된 로직들이 들어있는 디렉토리