# IS lab works backend

Spring boot приложение, выполняющее роль backend-а для лабораторных работ по курсу "Информационные системы"

Репозиторий будет включать в себя все 3 работы, ведь и 2-я, и 3-я работы модифицируют предыдущую.

---

## How to build?

1. `git clone https://github.com/DiedCloud/is-lab`
2.  Create `src/main/resources/application.properties` file like in
    [application-test.properties](src/main/resources/application-test.properties)

3. Build using Gradle `gradle bootRun`/`gradle bootJar`. The database schema should be built automatically via `hibernate`.