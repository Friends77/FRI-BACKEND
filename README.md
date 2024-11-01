# FRI-BACKEND

이 프로젝트는 Docker Compose를 사용하여 백엔드 서버를 실행합니다.

## 요구 사항
- Docker: [설치 가이드](https://docs.docker.com/get-docker/)
- Docker Compose: Docker에 기본 포함되어 있습니다.

## 설정 파일 구성
`docker-compose.yml` 파일은 PostgreSQL, Redis, Spring Boot 서버를 포함한 멀티 컨테이너 애플리케이션을 정의합니다. PostgreSQL과 Redis는 Spring Boot 애플리케이션과 연결되며, Docker Compose를 통해 모든 서비스가 동시에 시작됩니다.

## 실행 방법

**백엔드 서버 실행**
다음 명령을 사용하여 `docker-compose.yml` 파일에 정의된 모든 서비스를 시작할 수 있습니다:

```bash
docker-compose up -d
```
