# auth

## 📌 프로젝트 개요

**auth**는 시큐리티 JWT를 이용하여 회원가입,로그인,해당 유저 권한 변경 기능, 로그아웃을 구현한 과제입니다.

## 🛠️ 기술 스택

- **Backend**: Java, Spring Boot, JPA
- **Database**: MySQL ,Redis
- **Infra**: EC2 + Docker + ECR
- **API Management**: Postman, Swagger

### 1. 사용자(User)

- 회원가입, 로그인, 회원 조회, 해당 유저 권한 변경, 로그아웃 제공
- JWT 기반 인증 

### 2. 배포(IP)
- EC2 + Docker + ECR 배포
- http://43.200.178.33:8080/swagger
- http://43.200.178.33:8080/docs
