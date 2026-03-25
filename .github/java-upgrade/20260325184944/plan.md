# Upgrade Plan: edupulse-back (20260325184944)

- **Generated**: 2026-03-25 18:49:44
- **HEAD Branch**: main
- **HEAD Commit ID**: N/A (no commits yet)

## Available Tools

**JDKs**
- JDK 17.0.18: /usr/lib/jvm/java-17-openjdk-amd64/bin (current project JDK, used by step 2)
- JDK 21.0.8: /home/soporte/.jdk/jdk-21.0.8/bin (target JDK, used by steps 3–4)

**Build Tools**
- Maven Wrapper (mvnw): 3.9.14 — compatible with Java 21 (Maven 3.9+ required)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260325184944
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade Java from 17 to 21 (LTS)

### Technology Stack

| Technology/Dependency    | Current  | Min Compatible | Why Incompatible                                     |
| ------------------------ | -------- | -------------- | ---------------------------------------------------- |
| Java                     | 17       | 21             | User requested                                       |
| Spring Boot              | 4.0.4    | 4.0.4          | Already compatible with Java 21                      |
| Maven Wrapper            | 3.9.14   | 3.9.0          | Already compatible with Java 21                      |
| maven-compiler-plugin    | (BOM)    | 3.11.0         | Managed by Spring Boot 4.0.4 BOM; compatible         |
| Lombok                   | (BOM)    | 1.18.20        | Managed by Spring Boot BOM; Java 21 compatible       |
| postgresql               | (BOM)    | —              | Runtime dep; no Java version constraint              |

### Derived Upgrades

- No derived upgrades required. Spring Boot 4.0.4 already supports Java 21. Changing `java.version` from 17 to 21 in `pom.xml` is the only required change.

## Upgrade Steps

- **Step 1: Setup Environment**
  - **Rationale**: Verify JDK 21 is available; no installation needed (already present).
  - **Changes to Make**:
    - [ ] Confirm JDK 21 at `/home/soporte/.jdk/jdk-21.0.8/bin`
    - [ ] Confirm Maven Wrapper (`mvnw`) is executable
  - **Verification**:
    - Command: `#list_jdks` to confirm JDK 21 available
    - Expected: JDK 21.0.8 present

---

- **Step 2: Setup Baseline**
  - **Rationale**: Establish pre-upgrade compile and test results with Java 17.
  - **Changes to Make**:
    - [ ] Run baseline compilation with JDK 17
    - [ ] Run baseline tests with JDK 17
  - **Verification**:
    - Command: `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./mvnw clean test-compile -q && JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./mvnw clean test`
    - JDK: `/usr/lib/jvm/java-17-openjdk-amd64/bin`
    - Expected: Document SUCCESS/FAILURE and test pass rate as baseline

---

- **Step 3: Upgrade Java 17 → 21**
  - **Rationale**: Change `java.version` property in `pom.xml` from 17 to 21. Spring Boot 4.0.4 and Maven 3.9.14 are already Java 21 compatible; no other changes required.
  - **Changes to Make**:
    - [ ] Update `<java.version>17</java.version>` → `<java.version>21</java.version>` in `pom.xml`
  - **Verification**:
    - Command: `JAVA_HOME=/home/soporte/.jdk/jdk-21.0.8 ./mvnw clean test-compile -q`
    - JDK: `/home/soporte/.jdk/jdk-21.0.8/bin`
    - Expected: Compilation SUCCESS (main + test)

---

- **Step 4: Final Validation**
  - **Rationale**: Verify all upgrade goals met, full test suite passes with Java 21.
  - **Changes to Make**:
    - [ ] Verify `<java.version>21</java.version>` in `pom.xml`
    - [ ] Resolve any remaining TODOs
    - [ ] Run full test suite with Java 21
    - [ ] Fix any test failures (iterative fix loop)
  - **Verification**:
    - Command: `JAVA_HOME=/home/soporte/.jdk/jdk-21.0.8 ./mvnw clean test`
    - JDK: `/home/soporte/.jdk/jdk-21.0.8/bin`
    - Expected: Compilation SUCCESS + 100% tests pass

## Key Challenges

- **Minimal upgrade risk**
  - **Challenge**: Direct Java 17 → 21 LTS with Spring Boot 4.0.4 already supporting Java 21. No namespace migrations, no framework API breakages expected.
  - **Strategy**: Single property change in `pom.xml` suffices. If any test failures arise, they will be addressed in the Final Validation step with an iterative fix loop.
